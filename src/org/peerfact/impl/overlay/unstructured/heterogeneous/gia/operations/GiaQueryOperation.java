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

package org.peerfact.impl.overlay.unstructured.heterogeneous.gia.operations;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.peerfact.api.common.Message;
import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.simengine.SimulationEventHandler;
import org.peerfact.api.transport.TransLayer;
import org.peerfact.api.transport.TransMessageListener;
import org.peerfact.impl.common.AbstractOperation;
import org.peerfact.impl.common.Operations;
import org.peerfact.impl.overlay.unstructured.heterogeneous.api.GnutellaLikeOverlayContact;
import org.peerfact.impl.overlay.unstructured.heterogeneous.api.Query;
import org.peerfact.impl.overlay.unstructured.heterogeneous.api.QueryHit;
import org.peerfact.impl.overlay.unstructured.heterogeneous.common.messages.GnutellaQueryHit;
import org.peerfact.impl.overlay.unstructured.heterogeneous.gia.GiaNode;
import org.peerfact.impl.overlay.unstructured.heterogeneous.gia.GiaQueryManager;
import org.peerfact.impl.simengine.SimulationEvent;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.transport.TransMsgEvent;

/**
 * Initiates a query and then waits for QueryHits. If enough QueryHits have
 * returned, the operation is marked as successful and the QueryHits are
 * returned as result.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class GiaQueryOperation extends
		AbstractOperation<GiaNode, List<QueryHit<GnutellaLikeOverlayContact>>>
		implements TransMessageListener {

	private GiaQueryManager qMgr;

	private GiaNode node;

	int totalHits = 0;

	private int hitsWanted;

	public GiaQueryOperation(
			Query q,
			int hitsWanted,
			GiaQueryManager qMgr,
			GiaNode component,
			OperationCallback<List<QueryHit<GnutellaLikeOverlayContact>>> callback) {
		super(component, callback);
		this.qMgr = qMgr;
		this.node = component;
		this.hitsWanted = hitsWanted;
		this.query = q;
	}

	public Set<QueryHit<GnutellaLikeOverlayContact>> hitContacts = new LinkedHashSet<QueryHit<GnutellaLikeOverlayContact>>();

	private Query query;

	@Override
	public void execute() {
		debug("==Query started.");
		List<QueryHit<GnutellaLikeOverlayContact>> localQueryHits = qMgr
				.startQuery(
						query, hitsWanted);
		addQueryHits(localQueryHits);
		if (enoughHits()) {
			finishSuccessfully();
		} else {
			debug("Not enough local hits, wait for more hits from the query that will be relayed...");
			listen();
			this.new Timeout().scheduleWithDelay(getComponent().getConfig()
					.getQueryTimeout());
		}
	}

	public Query getQuery() {
		return query;
	}

	private static void debug(String msg) {
		log.debug(Simulator.getFormattedTime(Simulator.getCurrentTime()) + msg);
	}

	public boolean enoughHits() {
		return totalHits >= hitsWanted;
	}

	public void timeoutOccured() {
		debug("Timeout occured.");
		if (enoughHits()) {
			finishSuccessfully();
		} else {
			finishUnsuccessfully();
		}
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
	void stopListen() {
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
				if (enoughHits()) {
					finishSuccessfully();
				}
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

	/**
	 * Stopping of listening is encapsulated by an operation to avoid
	 * ConcurrentModificationExceptions.
	 * 
	 * @author Leo Nobach
	 * 
	 */
	class StopListeningOperation extends AbstractOperation<GiaNode, Object> {
		/**
		 * @param component
		 * @param callback
		 */
		protected StopListeningOperation(GiaNode component) {
			super(component, Operations.getEmptyCallback());
		}

		@Override
		protected void execute() {
			stopListen();
		}

		@Override
		public Object getResult() {
			return null;
		}

	}

	public void finishSuccessfully() {
		new StopListeningOperation(getComponent()).scheduleImmediately();
		Simulator.getMonitor().unstructuredQuerySucceeded(
				this.node.getOwnContact(), query,
				totalHits, QueryHit.getAverageHops(hitContacts));
		this.operationFinished(true);
		debug("==Query finished successfully.");
	}

	public void finishUnsuccessfully() {
		new StopListeningOperation(getComponent()).scheduleImmediately();
		Simulator.getMonitor().unstructuredQueryFailed(
				this.node.getOwnContact(), query,
				totalHits, QueryHit.getAverageHops(hitContacts));
		this.operationFinished(false);
		debug("==Query failed.");
	}

}
