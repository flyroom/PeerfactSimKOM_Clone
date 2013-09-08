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

package org.peerfact.impl.overlay.unstructured.heterogeneous.gnutella06.ultrapeer;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.peerfact.api.common.Message;
import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.simengine.SimulationEventHandler;
import org.peerfact.api.transport.TransLayer;
import org.peerfact.api.transport.TransMessageListener;
import org.peerfact.api.transport.TransProtocol;
import org.peerfact.impl.common.AbstractOperation;
import org.peerfact.impl.overlay.unstructured.heterogeneous.api.Gnutella06OverlayContact;
import org.peerfact.impl.overlay.unstructured.heterogeneous.api.GnutellaLikeOverlayContact;
import org.peerfact.impl.overlay.unstructured.heterogeneous.api.Query;
import org.peerfact.impl.overlay.unstructured.heterogeneous.api.QueryHit;
import org.peerfact.impl.overlay.unstructured.heterogeneous.common.ConnectionManager;
import org.peerfact.impl.overlay.unstructured.heterogeneous.common.messages.AbstractGnutellaMessage;
import org.peerfact.impl.overlay.unstructured.heterogeneous.common.messages.GnutellaPong;
import org.peerfact.impl.overlay.unstructured.heterogeneous.common.messages.GnutellaQueryHit;
import org.peerfact.impl.overlay.unstructured.heterogeneous.gnutella06.AbstractGnutella06Node;
import org.peerfact.impl.overlay.unstructured.heterogeneous.gnutella06.Gnutella06ConnectionManager;
import org.peerfact.impl.overlay.unstructured.heterogeneous.gnutella06.IGnutella06Config;
import org.peerfact.impl.overlay.unstructured.heterogeneous.gnutella06.messages.Gnutella06Query;
import org.peerfact.impl.simengine.SimulationEvent;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.transport.TransMsgEvent;


/**
 * The Dynamic querying operation is a mechanism to save bandwidth by stopping
 * the query if enough hits have been made.
 * 
 * The operation consists of the steps:
 * <ul>
 * <li>Leaf Browsing
 * <li>Probe Querying
 * <li>Controlled Broadcasting
 * </ul>
 * 
 * At each step the next one is only triggered if still not enough documents
 * have been found.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class DynamicQueryOperation
		extends
		AbstractOperation<AbstractGnutella06Node, List<QueryHit<GnutellaLikeOverlayContact>>>
		implements
		TransMessageListener {

	AbstractGnutella06Node node;

	private ConnectionManager<?, Gnutella06OverlayContact, IGnutella06Config, GnutellaPong<Gnutella06OverlayContact>> upMgr;

	private Query query;

	public Set<QueryHit<GnutellaLikeOverlayContact>> hitContacts = new LinkedHashSet<QueryHit<GnutellaLikeOverlayContact>>();

	private int totalHits = 0;

	Queue<GnutellaLikeOverlayContact> remainingBcastNodes;

	private State state;

	private QueryHandler handler;

	private int hitsWanted;

	/**
	 * Starts a new Dynamic Query Operation.
	 * 
	 * @param q
	 *            : the query that has been made.
	 * @param handler
	 *            : the query handler of the node
	 * @param component
	 *            : the main component of the node that triggers the query
	 * @param leafMgr
	 *            : the leaf connection manager of the node that triggers the
	 *            query
	 * @param upMgr
	 *            : the ultrapeer connection manager of the node that triggers
	 *            the query
	 * @param callback
	 */
	public DynamicQueryOperation(
			Query q,
			int hitsWanted,
			QueryHandler handler,
			AbstractGnutella06Node component,
			Gnutella06ConnectionManager<LeafInfo> leafMgr,
			Gnutella06ConnectionManager<Object> upMgr,
			OperationCallback<List<QueryHit<GnutellaLikeOverlayContact>>> callback) {
		super(component, callback);
		this.node = component;
		this.upMgr = upMgr;
		this.query = q;
		this.handler = handler;
		this.hitsWanted = hitsWanted;
	}

	@Override
	public void execute() {
		debug("==Query started.");
		state = State.browseLeaves;
		debug("Browsing leaves...");
		browseLeaves();
		if (enoughHits()) {
			finishSuccessfully();
		} else {
			debug("Not enough hits, do probe query...");
			state = State.probeQuery;
			doProbeQuery();
			listen();
			this.new Timeout().scheduleWithDelay(getComponent().getConfig()
					.getProbeQueryDuration());
		}
	}

	public Query getQuery() {
		return query;
	}

	private static void debug(String msg) {
		log.debug(Simulator.getFormattedTime(Simulator.getCurrentTime()) + msg);
	}

	/**
	 * First step, browses leaves first
	 */
	public void browseLeaves() {
		addQueryHits(handler.browseLocallyAndLeaves(query.getInfo(), 0));
	}

	public boolean enoughHits() {
		return totalHits >= hitsWanted;
	}

	public void timeoutOccured() {
		debug("Timeout occured.");
		if (state == State.probeQuery) {
			if (enoughHits()) {
				state = State.Finished;
				finishSuccessfully();
			} else {
				state = State.ControlledBcast;
				// query.newUID(); //Regenerate uid
				remainingBcastNodes = new LinkedList<GnutellaLikeOverlayContact>();
				remainingBcastNodes.addAll(upMgr.getConnectedContacts());
				handler.markQueryAsRelayed(query.getQueryUID());
				timeoutOccured();
			}
		} else if (state == State.ControlledBcast) {
			if (enoughHits()) {
				state = State.Finished;
				finishSuccessfully();
			} else if (!doControlledBcastStep()) {
				state = State.Finished;
				finishUnsuccessfully();
			} else {
				this.new Timeout().scheduleWithDelay(getConfig()
						.getControlledBcastStepDuration());
			}
		}
	}

	IGnutella06Config getConfig() {
		return getComponent().getConfig();
	}

	/**
	 * Starts a controlled broadcast step
	 * 
	 * Returns true if there was a node remaining for the controlled broadcast
	 * procedure.
	 * 
	 * @return
	 */
	public boolean doControlledBcastStep() {
		if (remainingBcastNodes.isEmpty()) {
			return false;
		}

		GnutellaLikeOverlayContact contact2query = remainingBcastNodes.remove();
		sendMessageAsync(new Gnutella06Query(getComponent().getOwnContact(),
				query, getConfig().getQueryDepth(), getRemainingHitCount()),
				contact2query);
		return true;
	}

	/**
	 * Returns the hits that remain until the query is satisfied
	 * 
	 * @return
	 */
	private int getRemainingHitCount() {
		return hitsWanted - totalHits;
	}

	/**
	 * Starts the probe query step
	 */
	private void doProbeQuery() {
		for (GnutellaLikeOverlayContact c : upMgr.getConnectedContacts()) {
			sendMessageAsync(new Gnutella06Query(
					getComponent().getOwnContact(), query, 1,
					getRemainingHitCount()), c);
		}
	}

	/**
	 * Sends an asynchronous message to the given peer
	 * 
	 * @param msg
	 * @param receiver
	 */
	private void sendMessageAsync(AbstractGnutellaMessage msg,
			GnutellaLikeOverlayContact receiver) {
		getTransLayer().send(msg, receiver.getTransInfo(),
				receiver.getTransInfo().getPort(), TransProtocol.UDP);
	}

	/**
	 * Starts the listening for incoming QueryHits
	 */
	private void listen() {
		getTransLayer().addTransMsgListener(this, getComponent().getPort());
	}

	/**
	 * Stops the listening for incoming QueryHits
	 */
	private void stopListen() {
		getTransLayer().removeTransMsgListener(this, getComponent().getPort());
	}

	TransLayer getTransLayer() {
		return getComponent().getHost().getTransLayer();
	}

	/**
	 * Adds the query hits to the result of this operation
	 * 
	 * @param newHits
	 */
	private void addQueryHits(List<QueryHit<GnutellaLikeOverlayContact>> newHits) {
		hitContacts.addAll(newHits);
		totalHits += QueryHit.getTotalHits(newHits);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void messageArrived(TransMsgEvent receivingEvent) {
		Message msg = receivingEvent.getPayload();
		if (msg instanceof GnutellaQueryHit) {
			debug("Hit message arrived.");
			GnutellaQueryHit<GnutellaLikeOverlayContact> hitMsg = ((GnutellaQueryHit<GnutellaLikeOverlayContact>) msg);
			if (hitMsg.getQueryUID() == query.getQueryUID()) {
				addQueryHits(hitMsg.getQueryHits());
			}
		}
	}

	@Override
	public List<QueryHit<GnutellaLikeOverlayContact>> getResult() {
		return new ArrayList<QueryHit<GnutellaLikeOverlayContact>>(hitContacts);
	}

	public class Timeout implements SimulationEventHandler {

		boolean listeningStopped = false;

		public void scheduleWithDelay(long delay) {
			long time = Simulator.getCurrentTime() + delay;
			scheduleAtTime(time);
		}

		public void scheduleAtTime(long time) {
			long newtime = Math.max(time, Simulator.getCurrentTime());
			Simulator.scheduleEvent(this, newtime, this,
					SimulationEvent.Type.TIMEOUT_EXPIRED);
		}

		@Override
		public void eventOccurred(SimulationEvent se) {
			timeoutOccured();
		}

	}

	public void finishSuccessfully() {
		stopListen();
		this.operationFinished(true);
		debug("==Query finished successfully.");
		Simulator.getMonitor().unstructuredQuerySucceeded(
				this.node.getOwnContact(), query,
				totalHits, QueryHit.getAverageHops(hitContacts));
	}

	public void finishUnsuccessfully() {
		stopListen();
		this.operationFinished(false);
		debug("==Query failed.");
		Simulator.getMonitor().unstructuredQueryFailed(
				this.node.getOwnContact(), query, totalHits,
				QueryHit.getAverageHops(hitContacts));
	}

	enum State {
		browseLeaves, probeQuery, ControlledBcast, Finished;
	}

}
