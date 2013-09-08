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
import java.util.List;

import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.transport.TransProtocol;
import org.peerfact.impl.overlay.unstructured.heterogeneous.api.Gnutella06OverlayContact;
import org.peerfact.impl.overlay.unstructured.heterogeneous.api.GnutellaLikeOverlayContact;
import org.peerfact.impl.overlay.unstructured.heterogeneous.api.IQueryInfo;
import org.peerfact.impl.overlay.unstructured.heterogeneous.api.Query;
import org.peerfact.impl.overlay.unstructured.heterogeneous.api.QueryHit;
import org.peerfact.impl.overlay.unstructured.heterogeneous.common.messages.AbstractGnutellaMessage;
import org.peerfact.impl.overlay.unstructured.heterogeneous.common.messages.GnutellaQueryHit;
import org.peerfact.impl.overlay.unstructured.heterogeneous.gnutella06.Gnutella06ConnectionManager;
import org.peerfact.impl.overlay.unstructured.heterogeneous.gnutella06.messages.Gnutella06Query;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.util.timeoutcollections.TimeoutSet;


/**
 * Manages incoming queries, decides if to answer them with a QueryHit or to
 * relay them
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class QueryHandler {

	Ultrapeer owner;

	private Gnutella06ConnectionManager<Object> upMgr;

	private Gnutella06ConnectionManager<LeafInfo> leafMgr;

	TimeoutSet<Integer> queryUIDsReceived;

	TimeoutSet<Integer> queryUIDsRelayed;

	/**
	 * Creates a new query handler
	 * 
	 * @param owner
	 *            : the ulrapeer that owns this query handler
	 * @param leafMgr
	 *            : the connection manager for leaves connected to this node
	 * @param upMgr
	 *            : the connection manager for ultrapeers connected to this node
	 */
	public QueryHandler(Ultrapeer owner,
			Gnutella06ConnectionManager<LeafInfo> leafMgr,
			Gnutella06ConnectionManager<Object> upMgr) {
		this.owner = owner;
		this.upMgr = upMgr;
		this.leafMgr = leafMgr;
		long cacheTimeout = owner.getConfig().getQueryCacheTimeout();
		queryUIDsReceived = new TimeoutSet<Integer>(cacheTimeout);
		queryUIDsRelayed = new TimeoutSet<Integer>(cacheTimeout);
	}

	/**
	 * Begins a <b>new</b> query with the given query info (not for relaying
	 * them). Does it by calling the Dynamic Query operation.
	 * 
	 * @param info
	 */
	public void startQuery(
			IQueryInfo info,
			int hitsWanted,
			OperationCallback<List<QueryHit<GnutellaLikeOverlayContact>>> callback) {
		Query q = new Query(info);
		Simulator.getMonitor().unstructuredQueryStarted(owner.getOwnContact(),
				q);
		new DynamicQueryOperation(q, hitsWanted, this, owner, leafMgr, upMgr,
				callback).scheduleImmediately();
	}

	/**
	 * Processes a query received from a neighbor.
	 * 
	 * @param queryMsg
	 */
	public void foreignUPQueryAttempt(Gnutella06Query queryMsg) {
		Query query = queryMsg.getQueryInfo();
		List<QueryHit<GnutellaLikeOverlayContact>> localHits = browseLocallyAndLeaves(
				query.getInfo(), queryMsg.getHops());

		int numLocalHits = QueryHit.getTotalHits(localHits);
		int remainingHits = queryMsg.getMaximumHitsWanted() - numLocalHits;
		int queryUID = query.getQueryUID();

		if (numLocalHits > 0 && !queryUIDsReceived.contains(queryUID)) {
			sendMessage(new GnutellaQueryHit<GnutellaLikeOverlayContact>(
					queryUID, localHits), queryMsg
					.getUPIntiator());
		}
		markQueryAsReceived(queryUID);

		Simulator.getMonitor().unstructuredQueryMadeHop(queryUID,
				owner.getOwnContact());

		if (remainingHits > 0 && queryMsg.getTTL() > 1
				&& !queryUIDsRelayed.contains(queryUID)) {
			queryMsg.decreaseTTL();
			queryMsg.setMaximumHitsWanted(remainingHits);
			for (Gnutella06OverlayContact relayContact : upMgr
					.getConnectedContacts()) {
				sendMessage(queryMsg.clone(), relayContact);
			}
			markQueryAsRelayed(queryUID);
		}
	}

	/**
	 * Marks the given query UID as relayed in the query cache.
	 * 
	 * @param queryUID
	 */
	public void markQueryAsRelayed(int queryUID) {
		queryUIDsRelayed.addNow(queryUID);
	}

	/**
	 * Marks the given query UID as received in the query cache.
	 * 
	 * @param queryUID
	 */
	public void markQueryAsReceived(int queryUID) {
		queryUIDsReceived.addNow(queryUID);
	}

	/**
	 * Sends an asynchronous message to the given contact
	 * 
	 * @param msg
	 * @param to
	 */
	private void sendMessage(AbstractGnutellaMessage msg,
			Gnutella06OverlayContact to) {
		owner.getHost().getTransLayer().send(msg, to.getTransInfo(),
				to.getTransInfo().getPort(), TransProtocol.UDP);
	}

	/**
	 * Browses locally for resources. The replication info received from the
	 * leaves as well as the own shared resources are considered.
	 * 
	 * @param query
	 * @param hops
	 * @return
	 */
	public List<QueryHit<GnutellaLikeOverlayContact>> browseLocallyAndLeaves(
			IQueryInfo query, int hops) {
		List<QueryHit<GnutellaLikeOverlayContact>> hits = browseLeaves(query,
				hops);
		QueryHit<GnutellaLikeOverlayContact> localHit = browseLocally(query,
				hops);
		if (localHit != null) {
			hits.add(localHit);
		}
		return hits;

	}

	/**
	 * Browses for hits in the local resource set. Returns null if there is no
	 * hit locally
	 * 
	 * @param query
	 * @param hops
	 * @return
	 */
	public QueryHit<GnutellaLikeOverlayContact> browseLocally(IQueryInfo query,
			int hops) {
		int hitCount = query.getNumberOfMatchesIn(owner.getResources());

		if (hitCount > 0) {
			return new QueryHit<GnutellaLikeOverlayContact>(
					owner.getOwnContact(), hitCount, hops);
		} else {
			return null;
		}
	}

	/**
	 * Browses for hits in the replication data received from connected leaves.
	 * 
	 * @param query
	 * @param hops
	 * @return
	 */
	public List<QueryHit<GnutellaLikeOverlayContact>> browseLeaves(
			IQueryInfo query, int hops) {

		List<QueryHit<GnutellaLikeOverlayContact>> hits = new ArrayList<QueryHit<GnutellaLikeOverlayContact>>();

		for (Gnutella06OverlayContact leaf : leafMgr.getConnectedContacts()) {
			LeafInfo info = leafMgr.getMetadata(leaf);
			int hitCount = query.getNumberOfMatchesIn(info.getLeafResources());

			if (hitCount > 0) {
				hits.add(new QueryHit<GnutellaLikeOverlayContact>(leaf,
						hitCount, hops));
			}
		}
		return hits;
	}

}
