/*
 * Copyright (c) 2012-2013 Open Source Community - <http://www.peerfact.org>
 * Copyright (c) 2011-2012 University of Paderborn - UPB
 * Copyright (c) 2005-2011 KOM - Multimedia Communications Lab
 *
 * This file is part of PeerfactSim.KOM.
 * 
 * PeerfactSim.KOM is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 * 
 * PeerfactSim.KOM is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with PeerfactSim.KOM.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

/**
 * 
 */
package org.peerfact.impl.overlay.unstructured.heterogeneous.gia;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;
import org.peerfact.api.common.LocalClock;
import org.peerfact.api.common.Message;
import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.network.NetLayer;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.api.transport.TransMessageListener;
import org.peerfact.api.transport.TransProtocol;
import org.peerfact.impl.common.AbstractOperation;
import org.peerfact.impl.common.Operations;
import org.peerfact.impl.overlay.unstructured.heterogeneous.api.GnutellaLikeOverlayContact;
import org.peerfact.impl.overlay.unstructured.heterogeneous.api.IQueryInfo;
import org.peerfact.impl.overlay.unstructured.heterogeneous.api.Query;
import org.peerfact.impl.overlay.unstructured.heterogeneous.api.QueryHit;
import org.peerfact.impl.overlay.unstructured.heterogeneous.common.ConnectionManager.ConnectionManagerListener;
import org.peerfact.impl.overlay.unstructured.heterogeneous.common.ConnectionManager.ConnectionState;
import org.peerfact.impl.overlay.unstructured.heterogeneous.common.IConnection;
import org.peerfact.impl.overlay.unstructured.heterogeneous.common.messages.GnutellaQueryHit;
import org.peerfact.impl.overlay.unstructured.heterogeneous.gia.messages.GiaQueryMessage;
import org.peerfact.impl.overlay.unstructured.heterogeneous.gia.operations.GiaQueryOperation;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.transport.TransMsgEvent;
import org.peerfact.impl.util.logging.SimLogger;
import org.peerfact.impl.util.scheduling.ISchedQueue;
import org.peerfact.impl.util.scheduling.ISchedulableResource;
import org.peerfact.impl.util.scheduling.IScheduler;
import org.peerfact.impl.util.scheduling.StartTimeFairQueueingScheduler;
import org.peerfact.impl.util.timeoutcollections.TimeoutSet;

/**
 * Gia uses random walks for the routing of its queries. Additionally, it uses a
 * flow control mechanism that ensures there is no congestion of queries in the
 * network. This component implements the common Gia behavior.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class GiaQueryManager implements
		ISchedulableResource<GiaQueryManager.EnqueuedQuery>,
		ConnectionManagerListener<GiaOverlayContact, GiaConnectionMetadata>,
		TransMessageListener {

	private final static Logger log = SimLogger
			.getLogger(GiaQueryManager.class);

	private GiaConnectionManager mgr;

	private static final long MAX_TOKEN_WAITING_TIME = 50 * Simulator.MILLISECOND_UNIT;

	GiaNode component;

	IGiaConfig config;

	TimeoutSet<GiaOverlayContact> activeContacts;

	IScheduler<EnqueuedQuery> scheduler;

	/**
	 * If a node treats its own queries like the ones relayed from others, it
	 * has to include them in the scheduling process. They are enqueued in this
	 * relay queue.
	 */
	ISchedQueue<EnqueuedQuery> ownRelayQueue;

	boolean waitsForTokens = false;

	private LocalClock clock;

	public GiaQueryManager(GiaConnectionManager mgr, GiaNode component,
			LocalClock clock) {
		this.mgr = mgr;
		this.component = component;
		config = component.getConfig();
		scheduler = new StartTimeFairQueueingScheduler<EnqueuedQuery>(
				component, this);
		ownRelayQueue = scheduler.createNewSchedQueue(component.getOwnContact()
				.getCapacity());
		this.clock = clock;
		activeContacts = new TimeoutSet<GiaOverlayContact>(
				config.getContactActivityTimeout());
	}

	/**
	 * Called when a query message has been received by the owner of this
	 * component.
	 * 
	 * @param msg
	 */
	public void queryMessageReceived(GiaQueryMessage msg) {

		Simulator.getMonitor().unstructuredQueryMadeHop(msg.getQueryUID(),
				component.getOwnContact());

		List<QueryHit<GnutellaLikeOverlayContact>> localHits = browseLocally(msg);
		int numberOfHits = QueryHit.getTotalHits(localHits);
		if (!localHits.isEmpty()) {
			sendQueryHitMessage(msg.getQuery(), msg.getInitiator(), localHits,
					numberOfHits);
		}
		msg.decreaseWantedResponsesBy(numberOfHits);

		if (msg.getWantedResponses() > 0
				&& msg.getHopCounter() < config.getQueryTTL())
		{
			relayQuery(msg); // Still too less hits, so relay the query.
		} else {
			log.debug("Not relaying the query");
		}
	}

	/**
	 * Returns the number of hits that were made locally for a query message,
	 * including One-hop replicated content.
	 * 
	 * @param msg
	 */
	private List<QueryHit<GnutellaLikeOverlayContact>> browseLocally(
			GiaQueryMessage msg) {
		Query q = msg.getQuery();
		// int totalHits;
		List<QueryHit<GnutellaLikeOverlayContact>> queryHitsMade = new ArrayList<QueryHit<GnutellaLikeOverlayContact>>();

		int localHits = q.getInfo().getNumberOfMatchesIn(
				component.getResources());
		// totalHits += localHits
		if (localHits > 0) {
			queryHitsMade.add(new QueryHit<GnutellaLikeOverlayContact>(
					component
							.getOwnContact(), localHits, msg.getHopCounter()));
		}

		for (IConnection<GiaOverlayContact, GiaConnectionMetadata> conn : mgr
				.getEstablishedConnections()) {
			int replicatedHits = q.getInfo().getNumberOfMatchesIn(
					conn.getMetadata().getResources());
			if (replicatedHits > 0) {
				// totalHits += replicatedHits;
				queryHitsMade.add(new QueryHit<GnutellaLikeOverlayContact>(conn
						.getContact(), replicatedHits, msg.getHopCounter()));
			}
		}

		// sendQueryHitMessage(q, msg.getInitiator(), queryHitsMade, totalHits);

		return queryHitsMade;
	}

	/**
	 * Sends a query hit message.
	 * 
	 * @param q
	 *            : the query that was made by the initiator
	 * @param initiator
	 *            : the initiator of the query
	 * @param queryHitsMade
	 *            : the list of query hits that were made locally and One-hop
	 *            replicated
	 * @param totalHits
	 *            : the total number of hits that were made.
	 */
	private void sendQueryHitMessage(Query q, GiaOverlayContact initiator,
			List<QueryHit<GnutellaLikeOverlayContact>> queryHitsMade,
			int totalHits) {
		sendMessage(
				new GnutellaQueryHit<GnutellaLikeOverlayContact>(
						q.getQueryUID(),
						queryHitsMade), initiator);
	}

	/**
	 * Relays the query message msg to the appropriate neighbor. Enqueues it in
	 * the relay queue, if necessary.
	 * 
	 * @param msg
	 */
	void relayQuery(GiaQueryMessage msg) {
		GiaOverlayContact receivedFrom = msg.getLastHop();
		GiaConnectionMetadata meta = mgr.getMetadata(receivedFrom);
		relayQuery(msg, receivedFrom, meta.getRelayQueue());
	}

	/**
	 * Relays the given query message msg, received from receivedFrom to the
	 * appropriate neighbor Enqueues it in the relay queue queueOfRelayer, if
	 * necessary.
	 * 
	 * @param msg
	 * @param receivedFrom
	 * @param queueOfRelayer
	 */
	void relayQuery(GiaQueryMessage msg, GiaOverlayContact receivedFrom,
			ISchedQueue<EnqueuedQuery> queueOfRelayer) {
		// Marks the contact which sent this message as active.
		activeContacts.addNow(receivedFrom);
		// Check whether this contact has enqueued too many messages for
		// relaying.
		if (queueOfRelayer.getSize() >= component.getConfig()
				.getMaxQueryQueueSize()) {
			log.warn(component.getOwnContact()
					+ ": Too much in relay queue for host " + receivedFrom
					+ " to relay its message. Will be dropped. Should happen " +
					"rarely.");
			return; // Drop message.
		}
		queueOfRelayer.arrive(
				new EnqueuedQuery(msg, clock.getCurrentLocalTime()),
				getServiceTimeNeededFor(msg));
	}

	@Override
	public boolean service(EnqueuedQuery obj) {

		// Too old queries are simply dropped and the queue is continued.

		if (obj.getTimeEnqueued() + config.getMaxTimeInQueryQueue() < clock
				.getCurrentLocalTime()) {
			return true;
		}

		GiaQueryMessage msg = obj.getMsg();

		if (mgr.getNumberOfContactsInState(ConnectionState.Connected) <= 1)
		{
			return true;
			// No candidate to relay messages to.
		}

		int queryUID = msg.getQueryUID();

		IConnection<GiaOverlayContact, GiaConnectionMetadata> c = getForwardingCandidate(queryUID);
		if (c == null) {
			// No candidate is there with tokens available that has not relayed
			// the query.
			if (flushForwardingCache(queryUID)) {
				// There were contacts that have already relayed the query
				c = getForwardingCandidate(queryUID); // Look again, now without
														// cache (cache was
														// flushed.
				if (c == null) {
					// No candidate is there to relay the message with tokens
					// available at all.
					log.debug(component
							+ ": No candidate is there to relay the message with tokens available at all, although query caches were flushed. Suspending relaying");
					if (!waitsForTokens)
					{
						waitForNextTokenAllocated(); // We have to wait
					}
					return false;
				}
			} else {
				// No contact is there that already has relayed the query and
				// has tokens.
				log.debug(component
						+ ": No contact is there that has already relayed the query, and no contact at all has tokens. Suspending query.");
				if (!waitsForTokens) {
					waitForNextTokenAllocated();
				}
				return false;
			}
		}

		relayQueryDirectly(c, msg);

		return true;
	}

	/**
	 * Flushes the entry queryUID of the forwarding cache of all nodes connected
	 * 
	 * @param queryUID
	 */
	private boolean flushForwardingCache(int queryUID) {
		boolean contactHasQueryInCache = false;
		for (IConnection<GiaOverlayContact, GiaConnectionMetadata> c : mgr
				.getAllConnections()) {
			GiaConnectionMetadata meta = c.getMetadata();
			if (meta != null) {
				if (meta.removeQueryFromCache(queryUID)) {
					contactHasQueryInCache = true;
				}
			}
		}
		return contactHasQueryInCache;
	}

	/**
	 * Relays the query directly to the node c.
	 * 
	 * @param c
	 * @param obj
	 */
	private void relayQueryDirectly(
			IConnection<GiaOverlayContact, GiaConnectionMetadata> conn,
			GiaQueryMessage msg) {
		GiaConnectionMetadata meta = conn.getMetadata();
		meta.getQueryTokenBucket().takeToken();
		meta.markQueryAsRelayed(msg.getQueryUID());
		msg.setLastHop(component.getOwnContact());
		msg.increaseHopCounter();
		sendMessage(msg, conn.getContact());
	}

	/**
	 * Returns the interval after which the next token will be allocated by one
	 * of the connected nodes.
	 */
	private void waitForNextTokenAllocated() {

		IConnection<GiaOverlayContact, GiaConnectionMetadata> contactWithEarliestTokenAllocation = Collections
				.min(mgr.getEstablishedConnections(),
						new Comparator<IConnection<GiaOverlayContact, GiaConnectionMetadata>>() {

							@Override
							public int compare(
									IConnection<GiaOverlayContact, GiaConnectionMetadata> arg0,
									IConnection<GiaOverlayContact, GiaConnectionMetadata> arg1) {
								long a = arg0.getMetadata()
										.getQueryTokenBucket()
										.getTimeToNextTokenAllocation();
								long b = arg1.getMetadata()
										.getQueryTokenBucket()
										.getTimeToNextTokenAllocation();
								if (a > b) {
									return 1;
								}
								if (a < b) {
									return -1;
								}
								return 0;
							}
						});

		long waitingTime = Math.min(contactWithEarliestTokenAllocation
				.getMetadata().getQueryTokenBucket()
				.getTimeToNextTokenAllocation(),
				MAX_TOKEN_WAITING_TIME);

		log.debug(component + ": Waiting for tokens of "
				+ contactWithEarliestTokenAllocation + " for "
				+ waitingTime);

		new WaitForNextTokenAllocation().scheduleWithDelay(waitingTime);
		waitsForTokens = true;
	}

	/**
	 * Begins a query. Returns the hits that were made locally.
	 * 
	 * @param q
	 * @param responsesWanted
	 */
	public List<QueryHit<GnutellaLikeOverlayContact>> startQuery(Query q,
			int responsesWanted) {
		GiaQueryMessage msg = new GiaQueryMessage(component.getOwnContact(),
				component.getOwnContact(), q, responsesWanted);
		List<QueryHit<GnutellaLikeOverlayContact>> localHits = browseLocally(msg);
		int numLocalHits = QueryHit.getTotalHits(localHits);
		if (numLocalHits >= responsesWanted) {
			return localHits;
		}

		msg.decreaseWantedResponsesBy(numLocalHits);
		relayQuery(msg, component.getOwnContact(), ownRelayQueue); // For
																	// relaying,
																	// the query
																	// is
																	// inserted
																	// into the
																	// own relay
																	// queue.
		return localHits;
	}

	public IConnection<GiaOverlayContact, GiaConnectionMetadata> getForwardingCandidate(
			int queryUID) {
		int highestCapacity = 0;
		IConnection<GiaOverlayContact, GiaConnectionMetadata> highestCapacityNode = null;

		for (IConnection<GiaOverlayContact, GiaConnectionMetadata> c : mgr
				.getEstablishedConnections()) {
			if (c.getContact().getCapacity() > highestCapacity) {
				if (c.getMetadata().getQueryTokenBucket().hasTokens()
						&& !c.getMetadata().hasRelayedQuery(queryUID)) {
					highestCapacity = c.getContact().getCapacity();
					highestCapacityNode = c;
				}
			}
		}
		return highestCapacityNode;
	}

	/**
	 * Returns the service time needed for the given query message.
	 * 
	 * @param msg
	 * @return
	 */
	long getServiceTimeNeededFor(GiaQueryMessage msg) {
		long result = (long) (msg.getSize()
				/ component.getHost().getNetLayer().getMaxBandwidth().getUpBW()
				* Simulator.SECOND_UNIT * config.getQueryBandwidthLimitQuota());
		// log.debug("Service time for " + msg + ": " + result);
		return result;
	}

	/**
	 * @param c
	 * @return
	 */
	public long getTokenAllocationRateFor(GiaOverlayContact c) {
		int capSumOfActiveContacts = 0;
		for (GiaOverlayContact con : activeContacts.getUnmodifiableSet()) {
			if (!con.equals(c)) {
				capSumOfActiveContacts += con.getCapacity();
			}
		}
		capSumOfActiveContacts += c.getCapacity();

		double partOfTotalBandwidth = config.getTokenAllocationBandwidthQuota()
				* c.getCapacity() / capSumOfActiveContacts;
		NetLayer net = component.getHost().getNetLayer();
		double minBW = Math.min(net.getMaxBandwidth().getUpBW(), net
				.getMaxBandwidth().getDownBW());

		double throttle;
		GiaConnectionMetadata meta = mgr.getMetadata(c);
		if (meta != null
				&& meta.getRelayQueue().getSize() < config
						.getThrottleTokenQueueSize()) {
			throttle = config.getTokenThrottleQuota();
		} else {
			throttle = 1d;
		}

		long result = (long) (GiaQueryMessage.ESTIMATED_SIZE
				/ (minBW * partOfTotalBandwidth * throttle) * Simulator.SECOND_UNIT);

		// log.debug("ASSIGN TR: " + result + " µs, " + c + " ");
		return result;
	}

	@Override
	public void connectionEnded(GiaOverlayContact c,
			GiaConnectionMetadata metadata) {
		metadata.getRelayQueue().disconnect();
		// Disconnects the relay queue from the scheduler to avoid orphaned
		// queues in the data structure.
	}

	static double getSchedulerWeightFor(GiaOverlayContact c) {
		return c.getCapacity() / 100d;
	}

	@Override
	public void lostConnectivity() {
		// Nothing to do
	}

	@Override
	public void newConnectionEstablished(GiaOverlayContact c,
			GiaConnectionMetadata metadata) {
		metadata.setRelayQueue(scheduler
				.createNewSchedQueue(getSchedulerWeightFor(c)));
		// Adds a new relay queue to the relay bandwidth scheduler.
	}

	class WaitForNextTokenAllocation extends AbstractOperation<GiaNode, Object> {

		/**
		 * @param component
		 * @param callback
		 */
		protected WaitForNextTokenAllocation() {
			super(component, Operations.getEmptyCallback());
		}

		@Override
		protected void execute() {
			waitsForTokens = false;
			log.debug(component
					+ ": Finished waiting for tokens. Poking scheduler.");
			scheduler.poke();
		}

		@Override
		public Object getResult() {
			return null;
		}

	}

	/**
	 * Simply sends a message.
	 * 
	 * @param msg
	 * @param to
	 */
	protected void sendMessage(Message msg, GiaOverlayContact to) {
		TransInfo info = to.getTransInfo();
		component.getHost().getTransLayer()
				.send(msg, info, component.getPort(), TransProtocol.UDP);
	}

	public static class EnqueuedQuery {
		public EnqueuedQuery(GiaQueryMessage msg, long timeEnqueued) {
			super();
			this.msg = msg;
			this.timeEnqueued = timeEnqueued;
		}

		public GiaQueryMessage getMsg() {
			return msg;
		}

		public long getTimeEnqueued() {
			return timeEnqueued;
		}

		GiaQueryMessage msg;

		long timeEnqueued;

		@Override
		public String toString() {
			return "Q:" + msg.getQueryUID();
		}

	}

	/**
	 * Begins a <b>new</b> query with the given query info (not for relaying
	 * them). Does it by calling the Dynamic Query operation.
	 * 
	 * @param info
	 */
	public void query(
			IQueryInfo info,
			int hitsWanted,
			OperationCallback<List<QueryHit<GnutellaLikeOverlayContact>>> callback) {
		Query q = new Query(info);
		Simulator.getMonitor().unstructuredQueryStarted(
				component.getOwnContact(), q);
		new GiaQueryOperation(q, hitsWanted, this, component,
				callback).scheduleImmediately();
	}

	@Override
	public void messageArrived(TransMsgEvent receivingEvent) {

		final Message msg = receivingEvent.getPayload();
		if (msg instanceof GiaQueryMessage) {
			if (!mgr.peerIsConnected(((GiaQueryMessage) msg).getLastHop())) {
				// AbstractGnutellaLikeNode.dumpStateOfAll();
				// throw new IllegalStateException("Not connected: " +
				// ((GiaQueryMessage) msg).getLastHop() + " at " +
				// component.getOwnContact());
				log.warn("Not connected: "
						+ ((GiaQueryMessage) msg).getLastHop()
						+ " at "
						+ component.getOwnContact()
						+ ". Query "
						+ ((GiaQueryMessage) msg).getQuery()
						+ " will not be relayed. (DDoS and fairness protection, or illegal asymmetric connection state)");
				return;
			}
			new RelayQueryOperation(component, (GiaQueryMessage) msg)
					.scheduleImmediately();
		}

	}

	class RelayQueryOperation extends AbstractOperation<GiaNode, Object> {

		private GiaQueryMessage msg;

		/**
		 * @param component
		 * @param callback
		 */
		protected RelayQueryOperation(GiaNode component,
				GiaQueryMessage msgReceived) {
			super(component, Operations.getEmptyCallback());
			this.msg = msgReceived;
		}

		@Override
		protected void execute() {
			queryMessageReceived(msg);
		}

		@Override
		public Object getResult() {
			return null;
		}

	}

}
