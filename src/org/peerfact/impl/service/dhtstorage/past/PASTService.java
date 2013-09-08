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

package org.peerfact.impl.service.dhtstorage.past;

import java.math.BigInteger;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.peerfact.api.common.ConnectivityEvent;
import org.peerfact.api.common.ConnectivityListener;
import org.peerfact.api.common.Host;
import org.peerfact.api.common.Message;
import org.peerfact.api.common.Operation;
import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.api.overlay.OverlayID;
import org.peerfact.api.overlay.dht.DHTEntry;
import org.peerfact.api.overlay.dht.DHTKey;
import org.peerfact.api.overlay.dht.DHTListenerSupported;
import org.peerfact.api.overlay.dht.DHTValue;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.api.transport.TransMessageListener;
import org.peerfact.impl.common.Operations;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordContact;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordNode;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordRoutingTable;
import org.peerfact.impl.overlay.dht.chord.base.components.ChordID;
import org.peerfact.impl.overlay.dht.chord.base.components.ChordKey;
import org.peerfact.impl.overlay.dht.chord.base.messages.NotifyOfflineMsg;
import org.peerfact.impl.overlay.dht.chord.base.messages.NotifyPredecessorMsg;
import org.peerfact.impl.overlay.dht.chord.base.messages.NotifySuccessorMsg;
import org.peerfact.impl.service.dhtstorage.AbstractDHTService;
import org.peerfact.impl.service.dhtstorage.past.messages.AckMessage;
import org.peerfact.impl.service.dhtstorage.past.messages.ExchangeMessage;
import org.peerfact.impl.service.dhtstorage.past.messages.IssueReplicationMessage;
import org.peerfact.impl.service.dhtstorage.past.messages.PASTMessage;
import org.peerfact.impl.service.dhtstorage.past.messages.PingMessage;
import org.peerfact.impl.service.dhtstorage.past.messages.ReplicationRemovedMessage;
import org.peerfact.impl.service.dhtstorage.past.messages.StoreReplicationMessage;
import org.peerfact.impl.service.dhtstorage.past.operations.ExchangeOperation;
import org.peerfact.impl.service.dhtstorage.past.operations.IssueReplicationOperation;
import org.peerfact.impl.service.dhtstorage.past.operations.PingOperation;
import org.peerfact.impl.service.dhtstorage.past.operations.ReplicationOperation;
import org.peerfact.impl.service.dhtstorage.past.operations.ReplicationRemovedOperation;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.transport.DefaultTransInfo;
import org.peerfact.impl.transport.TransMsgEvent;


/**
 * The PASTService implements simplified version of the PAST replication
 * approach. For more information take a look at the corresponding paper.
 */
public class PASTService extends AbstractDHTService<DHTKey<?>> implements
		TransMessageListener {

	/**
	 * All known Contacts, stores centrally when they had their last
	 * interaction. Prevents duplicate Operations to find out if a contact still
	 * exists
	 */
	private Map<TransInfo, PASTContact> knownContacts;

	protected PASTConfig config;

	private short port;

	protected boolean isOnline;

	private Set<TransInfo> contactsToPing = new LinkedHashSet<TransInfo>();

	protected Map<TransInfo, PingOperation> pingOperations = new LinkedHashMap<TransInfo, PingOperation>();

	protected Map<TransInfo, Set<TransInfo>> failureMap = new LinkedHashMap<TransInfo, Set<TransInfo>>();

	protected Map<DHTKey<?>, ReplicationOperation> replicationsInProgress = new LinkedHashMap<DHTKey<?>, ReplicationOperation>();

	private ReplicationCallback repCallback = new ReplicationCallback();

	private Set<PASTObject> closestObject = new LinkedHashSet<PASTObject>();

	/**
	 * Start a new Replication-Service
	 * 
	 * @param node
	 *            The node to register this Service as a Listener
	 */
	public PASTService(
			Host host,
			short port,
			DHTListenerSupported<OverlayID<?>, OverlayContact<OverlayID<?>>, DHTKey<?>> node,
			PASTConfig config) {
		super(node);
		setHost(host);
		isOnline = false;
		host.getProperties().addConnectivityListener(
				new ConnectivityListener() {
					@Override
					public void connectivityChanged(ConnectivityEvent ce) {
						if (ce.isOnline()) {
							start();
						}
						if (ce.isOffline()) {
							stop();
						}
					}
				});
		host.getTransLayer().addTransMsgListener(this, port);
		host.getTransLayer().addTransMsgListener(this, (short) 123);

		this.port = port;
		this.config = config;
		knownContacts = new LinkedHashMap<TransInfo, PASTContact>();
		if (host.getNetLayer().isOnline()) {
			start();
		}
	}

	/**
	 * Starts this service and all periodically executed routines
	 */
	protected void start() {
		isOnline = true;
		if (config.isDropFiles()) {
			getDHTEntries().clear();
		}
	}

	/**
	 * Pauses this service
	 */
	protected void stop() {
		isOnline = false;
	}

	/**
	 * Add object locally and replicate among k neighboring nodes
	 * 
	 * @param key
	 *            The key of the object to store
	 * @param obj
	 *            The DHTObject to store
	 */
	@Override
	public void addDHTEntry(DHTKey<?> key, DHTValue value) {
		log.debug("DHTService: add!");

		if (getDHTEntry(key) == null) {
			Map<TransInfo, BigInteger> replicateTargets = new LinkedHashMap<TransInfo, BigInteger>();
			replicateTargets.put(getOwnTransInfo(),
					getDistance(getIDForKey(key)));

			// Create Object and store locally (this node is root)
			PASTObject object = new PASTObject(key, value, replicateTargets);

			addDHTEntry(object);

			// Distribute Object to all Targets
			replicateObject(0, object, true, null);
		}
	}

	/**
	 * Starts a ReplicateOperation
	 * 
	 * @param key
	 * @param value
	 * @param contacts
	 */
	protected void replicateObject(long delay, PASTObject obj,
			boolean allowDelegation, Set<TransInfo> ignore) {
		if (!replicationsInProgress.containsKey(obj.getKey())) {
			ReplicationOperation op = new ReplicationOperation(this,
					repCallback, config, obj, null, ignore);
			replicationsInProgress.put(obj.getKey(), op);
			op.scheduleWithDelay(delay);
		} else if (allowDelegation) {
			createIssueReplicationOperation(obj, ignore);
		}
	}

	public short getPort() {
		return port;
	}

	protected PASTConfig getConfig() {
		return config;
	}

	/**
	 * Return Contact-Instance of a Node
	 * 
	 * @param transInfo
	 * @return
	 */
	public PASTContact getContact(TransInfo transInfo) {
		if (knownContacts.get(transInfo) == null) {
			knownContacts.put(transInfo,
					new PASTContact(transInfo, Simulator.getCurrentTime()));
		}
		return knownContacts.get(transInfo);
	}

	/**
	 * Add a new Contact to local storage
	 * 
	 * @param transInfo
	 */
	protected void addContact(TransInfo transInfo) {
		knownContacts.put(transInfo,
				new PASTContact(transInfo, Simulator.getCurrentTime()));
	}

	/**
	 * Inform Service of an offline contact
	 */
	public void contactDidNotRespond(TransInfo transInfo) {
		getContact(transInfo).markAsOffline();
		removeFailedNode(transInfo);
	}

	/**
	 * A Contact had interaction with the service
	 * 
	 * @param transInfo
	 */
	public void contactDidRespond(TransInfo transInfo) {
		getContact(transInfo).updateLastAction();
	}

	/*
	 * TRANS-MESSAGE Listener
	 */

	public TransInfo getOwnTransInfo() {
		return getHost().getTransLayer().getLocalTransInfo(getPort());
	}

	/**
	 * Service acts as TransMessageListener for direct Messages
	 */
	@Override
	public void messageArrived(TransMsgEvent receivingEvent) {
		// Only responsible for Service-Messages
		Message msg = receivingEvent.getPayload();
		if (msg instanceof NotifyOfflineMsg) {
			TransInfo failedNode = convertTransInfo(((NotifyOfflineMsg) msg)
					.getOfflineInfo().getTransInfo());
			removeFailedNode(failedNode);
		} else if (msg instanceof NotifyPredecessorMsg) {
			processNewNeighbor(((NotifyPredecessorMsg) msg).getPredecessor());
		} else if (msg instanceof NotifySuccessorMsg) {
			processNewNeighbor(((NotifySuccessorMsg) msg).getSuccessor());
		} else if (msg instanceof PASTMessage) {
			if (msg instanceof StoreReplicationMessage) {
				handleStoreReplicationMessage(
						receivingEvent.getSenderTransInfo(),
						(StoreReplicationMessage) msg);
				sendReply(receivingEvent);
			} else if (msg instanceof PingMessage) {
				contactDidRespond(receivingEvent.getSenderTransInfo());
				handlePingMessage(receivingEvent.getSenderTransInfo(),
						(PingMessage) msg);
				sendReply(receivingEvent);
			} else if (msg instanceof ReplicationRemovedMessage) {
				contactDidRespond(receivingEvent.getSenderTransInfo());
				handleReplicationRemovedMessage(
						receivingEvent.getSenderTransInfo(),
						(ReplicationRemovedMessage) msg);
				sendReply(receivingEvent);
			} else if (msg instanceof IssueReplicationMessage) {
				contactDidRespond(receivingEvent.getSenderTransInfo());
				handleIssueReplicationMessage(
						receivingEvent.getSenderTransInfo(),
						(IssueReplicationMessage) msg);
				sendReply(receivingEvent);
			} else if (msg instanceof ExchangeMessage) {
				handleExchangeMessage(receivingEvent.getSenderTransInfo(),
						(ExchangeMessage) msg);
				sendReply(receivingEvent);
			}
		}
	}

	private void handleExchangeMessage(TransInfo sender, ExchangeMessage msg) {
		Map<DHTKey<?>, Map<TransInfo, BigInteger>> info = msg.getKeySet();
		Set<PASTObject> objsToCheck = new LinkedHashSet<PASTObject>();
		Map<DHTKey<?>, Map<TransInfo, BigInteger>> exchangeMap = new LinkedHashMap<DHTKey<?>, Map<TransInfo, BigInteger>>();
		for (Map.Entry<DHTKey<?>, Map<TransInfo, BigInteger>> entry : info
				.entrySet()) {
			PASTObject obj = (PASTObject) getDHTEntry(entry.getKey());
			if (obj != null) {
				int size = obj.getReplications().size();
				obj.getReplications().putAll(entry.getValue());
				if (size != obj.getReplications().size()) {
					objsToCheck.add(obj);
				}
				final Map<TransInfo, BigInteger> replicaHolders = new LinkedHashMap<TransInfo, BigInteger>(
						obj.getReplications());
				replicaHolders.remove(getOwnTransInfo());
				replicaHolders.remove(sender);
				for (TransInfo key : entry.getValue().keySet()) {
					replicaHolders.remove(key);
				}
				if (!replicaHolders.isEmpty()) {
					exchangeMap.put(obj.getKey(), replicaHolders);
				}
			}
		}
		if (!exchangeMap.isEmpty() && !msg.isReply()) {
			createExchangeOperation(sender, exchangeMap, true);
		}
		checkObjs(objsToCheck);
		if (!objsToCheck.isEmpty()) {
			updatePingOperations();
		}
	}

	private void createExchangeOperation(final TransInfo recipient,
			Map<DHTKey<?>, Map<TransInfo, BigInteger>> exchangeMap,
			boolean reply) {
		ExchangeOperation op = new ExchangeOperation(this,
				new OperationCallback<Object>() {

					@Override
					public void calledOperationFailed(
							Operation<Object> operation) {
						removeFailedNode(recipient);
					}

					@Override
					public void calledOperationSucceeded(
							Operation<Object> operation) {
						// happy :)
					}
				}, config, exchangeMap, recipient, reply);
		op.scheduleImmediately();
	}

	private void processNewNeighbor(AbstractChordContact contact) {
		BigInteger distance;
		int i = 0;
		TransInfo info = convertTransInfo(contact.getTransInfo());
		for (PASTObject obj : closestObject) {
			if (!obj.getReplications().containsKey(info)) {
				distance = contact.getOverlayID().getMinDistance(
						getIDForKey(obj.getKey()));
				for (BigInteger v : obj.getReplications().values()) {
					if (distance.compareTo(v) == -1) {
						// replicateObject(i++ * Simulator.SECOND_UNIT * 5, obj,
						// false, null);
						if (replicationsInProgress.containsKey(obj.getKey())) {
							ReplicationOperation op = new ReplicationOperation(
									this, repCallback, config, obj, contact,
									null);
							replicationsInProgress.put(obj.getKey(), op);
							op.scheduleWithDelay(i++ * Simulator.SECOND_UNIT
									* 5);
						}
						break;
					}
				}
			}
		}
	}

	private void handleIssueReplicationMessage(TransInfo sender,
			IssueReplicationMessage msg) {
		PASTObject obj = (PASTObject) getDHTEntry(msg.getKey());
		if (obj == null) {
			createReplicationRemovedOperation(Simulator.SECOND_UNIT * 5,
					msg.getKey(), sender);
		} else {
			replicateObject(0, obj, true, msg.getIgnore());
		}
	}

	private void handleReplicationRemovedMessage(TransInfo sender,
			ReplicationRemovedMessage msg) {
		DHTKey<?> key = msg.getKey();
		DHTEntry<?> entry = getDHTEntry(key);
		if (entry != null && entry instanceof PASTObject) {
			PASTObject obj = (PASTObject) entry;
			obj.getReplications().remove(convertTransInfo(sender));
			checkReplicaCount(0, obj, null, false);
		}
	}

	/**
	 * Message contains an object that is a replication and needs to be stored
	 * on this instance
	 * 
	 * @param msg
	 */
	private void handleStoreReplicationMessage(TransInfo root,
			StoreReplicationMessage msg) {
		// Add contact Information of root
		addContact(root);
		PASTObject o = msg.getReplicationObject();
		if (!o.getReplicationHolders().contains(getOwnTransInfo())) {
			o.getReplications().put(getOwnTransInfo(),
					getDistance(getIDForKey(msg.getKey())));
		}
		addDHTEntry(o);
		checkReplicaCount(0, o, null, false);
		updatePingOperations();
		Simulator.getMonitor().dhtMirrorAssigned(
				super.getHost()
						.<AbstractChordNode> getComponent(
								AbstractChordNode.class)
						.getLocalOverlayContact(), msg.getObject());
	}

	private void handlePingMessage(TransInfo senderOrig, PingMessage msg) {
		TransInfo sender = DefaultTransInfo.getTransInfo(senderOrig.getNetId(),
				getPort());
		Set<DHTKey<?>> keys = msg.getKeySet(); // keys of object that the sender
												// holds
		Set<PASTObject> objsToCheck = new LinkedHashSet<PASTObject>();
		Map<DHTKey<?>, Map<TransInfo, BigInteger>> exchangeMap = new LinkedHashMap<DHTKey<?>, Map<TransInfo, BigInteger>>();
		for (DHTEntry<?> entry : getDHTEntries()) {
			if (entry instanceof PASTObject) {
				PASTObject obj = (PASTObject) entry;
				if (obj.getReplications().containsKey(sender)) {
					if (!keys.contains(obj.getKey())) { // delete sender because
														// he does not hold the
														// object anymore
						obj.getReplications().remove(sender);
						objsToCheck.add(obj);
					}
				} else if (keys.contains(obj.getKey())) {
					obj.getReplications().put(sender,
							msg.getDistance(obj.getKey()));
					Map<TransInfo, BigInteger> replicaHolders = new LinkedHashMap<TransInfo, BigInteger>(
							obj.getReplications());
					replicaHolders.remove(getOwnTransInfo());
					replicaHolders.remove(sender);
					if (!replicaHolders.isEmpty()) {
						exchangeMap.put(obj.getKey(), replicaHolders);
					}
					objsToCheck.add(obj);
				}
			}
		}
		if (!exchangeMap.isEmpty()) {
			createExchangeOperation(sender, exchangeMap, false);
		}
		int i = 0;
		for (DHTKey<?> key : keys) {
			if (getDHTEntry(key) == null) {
				createReplicationRemovedOperation(i++ * Simulator.SECOND_UNIT
						* 5, key, sender);
			}
		}
		checkObjs(objsToCheck);
		if (msg.getFailedNodes() != null) {
			for (TransInfo failedNode : msg.getFailedNodes()) {
				// never remove myself and never remove a node that has answered
				// after failure
				if (!failedNode.equals(getOwnTransInfo())
						&& getContact(failedNode).getLastAction() > msg
								.getTime()) {
					removeFailedNode(failedNode);
				}
			}
		}
		updatePingOperations();
	}

	public void createReplicationRemovedOperation(long delay, DHTKey<?> key,
			TransInfo sender) {
		Map<TransInfo, BigInteger> replications = new LinkedHashMap<TransInfo, BigInteger>();
		replications.put(sender, BigInteger.ZERO);
		PASTObject obj = new PASTObject(key, null, replications);
		ReplicationRemovedOperation op = new ReplicationRemovedOperation(this,
				Operations.getEmptyCallback(), config, obj);
		op.scheduleWithDelay(delay);
	}

	public void removeFailedNode(TransInfo failedNode) {
		if (failedNode.equals(getOwnTransInfo())) {
			return;
		}
		Set<TransInfo> informContacts = new LinkedHashSet<TransInfo>();
		Set<PASTObject> objsToCheck = new LinkedHashSet<PASTObject>();
		for (DHTEntry<?> entry : getDHTEntries()) {
			if (entry instanceof PASTObject) {
				PASTObject obj = (PASTObject) entry;
				if (obj.getReplicationHolders().contains(failedNode)) {
					obj.getReplications().remove(failedNode);
					informContacts.addAll(obj.getReplicationHolders());
					objsToCheck.add(obj);
				}
			}
		}
		checkObjs(objsToCheck);
		for (TransInfo contact : informContacts) {
			if (!failureMap.containsKey(contact)) {
				failureMap.put(contact, new LinkedHashSet<TransInfo>());
			}
			failureMap.get(contact).add(failedNode);
		}
	}

	public void checkObjs(Collection<PASTObject> objs) {
		for (PASTObject obj : objs) {
			checkReplicaCount(0, obj, null, false);
		}
	}

	/**
	 * Send an ACK-Message
	 * 
	 * @param receivingEvent
	 */
	protected void sendReply(TransMsgEvent receivingEvent) {
		getHost().getTransLayer().sendReply(new AckMessage(), receivingEvent,
				getPort(), receivingEvent.getProtocol());
	}

	/**
	 * is this service instance online?
	 * 
	 * @return
	 */
	public boolean isOnline() {
		return isOnline;
	}

	public Map<TransInfo, BigInteger> getTargets(ChordID id,
			Collection<TransInfo> ignore) {
		AbstractChordNode node = getHost()
				.getComponent(AbstractChordNode.class);
		AbstractChordRoutingTable tbl = node.getChordRoutingTable();
		Map<TransInfo, BigInteger> targets = new LinkedHashMap<TransInfo, BigInteger>();
		if (tbl != null) {
			for (AbstractChordContact contact : tbl.getPredecessors()) {
				TransInfo info = convertTransInfo(contact.getTransInfo());
				if (!ignore.contains(info)) {
					targets.put(info, contact.getOverlayID().getMinDistance(id));
				}
			}
			for (AbstractChordContact contact : tbl.getSuccessors()) {
				TransInfo info = DefaultTransInfo.getTransInfo(contact
						.getTransInfo().getNetId(), getPort());
				if (!ignore.contains(info)) {
					targets.put(info, contact.getOverlayID().getMinDistance(id));
				}
			}
			// finger table has a bad influence
			for (AbstractChordContact contact : tbl.copyFingerTable()) {
				TransInfo info = DefaultTransInfo.getTransInfo(contact
						.getTransInfo().getNetId(), getPort());
				if (!ignore.contains(info)) {
					targets.put(info, contact.getOverlayID().getMinDistance(id));
				}
			}
		}
		return targets;
	}

	public TransInfo convertTransInfo(TransInfo info) {
		return DefaultTransInfo.getTransInfo(info.getNetId(), getPort());
	}

	public BigInteger getDistance(ChordID id) {
		AbstractChordNode node = getHost()
				.getComponent(AbstractChordNode.class);
		return node.getLocalOverlayContact().getOverlayID().getMinDistance(id);
	}

	public TransInfo getMinimum(Map<TransInfo, BigInteger> targets) {
		return getMinimum(targets, null);
	}

	public TransInfo getMinimum(Map<TransInfo, BigInteger> targets,
			TransInfo ignore) {
		BigInteger minDistance = null;
		TransInfo min = null;
		for (TransInfo key : targets.keySet()) {
			BigInteger value = targets.get(key);
			if ((ignore == null || !ignore.equals(key))
					&& (minDistance == null
							|| minDistance.compareTo(value) == 1 || min
							.getNetId().toString()
							.compareTo(getOwnTransInfo().getNetId().toString()) == 1
							&& minDistance.equals(value))) {
				min = key;
				minDistance = value;
			}
		}
		return min;
	}

	public TransInfo getMaximum(Map<TransInfo, BigInteger> targets) {
		BigInteger maxDistance = null;
		TransInfo max = null;
		for (TransInfo key : targets.keySet()) {
			BigInteger value = targets.get(key);
			if (maxDistance == null
					|| maxDistance.compareTo(value) == -1
					|| maxDistance.equals(value)
					&& max.getNetId().toString()
							.compareTo(getOwnTransInfo().getNetId().toString()) == 1) {
				max = key;
				maxDistance = value;
			}
		}
		return max;
	}

	public BigInteger getMaximumValue(Map<TransInfo, BigInteger> targets) {
		BigInteger maxDistance = null;
		TransInfo max = null;
		for (TransInfo key : targets.keySet()) {
			BigInteger value = targets.get(key);
			if (maxDistance == null
					|| maxDistance.compareTo(value) == -1
					|| maxDistance.equals(value)
					&& max.getNetId().toString()
							.compareTo(getOwnTransInfo().getNetId().toString()) == 1) {
				max = key;
				maxDistance = value;
			}
		}
		return maxDistance;
	}

	public static ChordID getIDForKey(DHTKey<?> key) {
		ChordKey chordKey = (ChordKey) key;
		return chordKey.getCorrespondingID();
	}

	protected void updatePingOperations() {
		contactsToPing.clear();
		for (DHTEntry<?> entry : getDHTEntries()) {
			PASTObject obj = (PASTObject) entry;
			for (TransInfo contact : obj.getReplicationHolders()) {
				if (!contact.equals(getOwnTransInfo())
						&& !contactsToPing.contains(contact)) {
					contactsToPing.add(contact);
				}
			}
		}
		int i = 0;
		for (TransInfo contact : contactsToPing) {
			if (!pingOperations.containsKey(contact)) {
				createPingOperation(i * 5 * Simulator.SECOND_UNIT, contact);
			}
		}
	}

	public void createPingOperation(long delay, TransInfo contact) {
		PingOperation op = new PingOperation(this, new PingCallback(), config,
				contact, failureMap.get(contact));
		pingOperations.put(contact, op);
		op.scheduleWithDelay(delay);
	}

	public static Set<TransInfo> getSet(TransInfo info) {
		Set<TransInfo> set = new LinkedHashSet<TransInfo>();
		set.add(info);
		return set;
	}

	public void createIssueReplicationOperation(PASTObject obj,
			Set<TransInfo> ignore) {
		if (obj != null) {
			IssueReplicationOperation op = new IssueReplicationOperation(this,
					Operations.getEmptyCallback(), config, obj, ignore);
			op.scheduleImmediately();
		}
	}

	public int getRank(PASTObject obj) {
		if (obj.getReplications().containsKey(getOwnTransInfo())) {
			final BigInteger distance = obj.getReplications().get(
					getOwnTransInfo());
			int rank = 0;
			for (TransInfo key : obj.getReplications().keySet()) {
				BigInteger value = obj.getReplications().get(key);
				if (distance.compareTo(value) == -1
						|| distance.equals(value)
						&& key.getNetId()
								.toString()
								.compareTo(
										getOwnTransInfo().getNetId().toString()) == -1) {
					rank++;
				}
			}
			return rank;
		}
		return Integer.MAX_VALUE;
	}

	public void checkReplicaCount(long delay, PASTObject obj,
			Set<TransInfo> ignore, boolean checkNeighbor) {
		int rank = getRank(obj);
		if (rank == 0) {
			closestObject.add(obj);
		} else {
			closestObject.remove(obj);
		}
		int diff = config.getNumberOfReplicates()
				- obj.getReplicationHolders().size();
		if (diff > 0) {
			if (closestObject.contains(obj) || rank < diff) {
				replicateObject(delay, obj, true, ignore);
			} else {
				// should be handle by ping message.
			}

		} else if (obj.getReplicationHolders().size() > config
				.getMaxNumberOfReplicates()) {
			if (getMaximum(obj.getReplications()).equals(getOwnTransInfo())) {
				removeDHTEntry(obj.getKey());
				obj.getReplications().remove(getOwnTransInfo());
				ReplicationRemovedOperation op = new ReplicationRemovedOperation(
						this, Operations.getEmptyCallback(), config, obj);
				op.scheduleWithDelay(delay);
			}
		} else if (checkNeighbor && obj != null && rank == 0) {
			Map<TransInfo, BigInteger> targets = getTargets(
					getIDForKey(obj.getKey()), obj.getReplicationHolders());
			TransInfo min = getMinimum(targets);
			if (min != null) {
				BigInteger max = getMaximumValue(obj.getReplications());
				if (max != null && targets.get(min).compareTo(max) == -1) {
					replicateObject(delay, obj, false, null);
				}
			}
		}
	}

	public class PingCallback implements OperationCallback<Object> {

		@Override
		public void calledOperationFailed(Operation<Object> op) {
			// handled by PingOperation#sendMessageFailed() -->
			// contactDidNotRespond

		}

		@Override
		public void calledOperationSucceeded(Operation<Object> op) {
			PingOperation o = (PingOperation) op;
			pingOperations.remove(o.getTarget());
			createPingOperation(config.getTimeBetweenRootPings(), o.getTarget());
		}

	}

	public class ReplicationCallback implements OperationCallback<Object> {

		@Override
		public void calledOperationFailed(Operation<Object> o) {
			ReplicationOperation op = (ReplicationOperation) o;
			replicationsInProgress.remove(op.getKey());
			if (op.getRecipient() == null) {
				createIssueReplicationOperation((PASTObject) op.getResult(),
						null);
			} else {
				checkReplicaCount(Simulator.SECOND_UNIT * 10,
						(PASTObject) op.getResult(), getSet(op.getRecipient()),
						true);
			}
		}

		@Override
		public void calledOperationSucceeded(Operation<Object> o) {
			ReplicationOperation op = (ReplicationOperation) o;
			replicationsInProgress.remove(op.getKey());
			checkReplicaCount(Simulator.SECOND_UNIT * 10,
					(PASTObject) op.getResult(), getSet(op.getRecipient()),
					true);
		}

	}
}
