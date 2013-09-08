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

import java.util.Collections;
import java.util.Set;

import org.peerfact.api.common.LocalClock;
import org.peerfact.impl.overlay.unstructured.heterogeneous.api.IResource;
import org.peerfact.impl.overlay.unstructured.heterogeneous.gia.GiaQueryManager.EnqueuedQuery;
import org.peerfact.impl.util.scheduling.ISchedQueue;
import org.peerfact.impl.util.timeoutcollections.TimeoutSet;


/**
 * Information that is kept by a peer about one of its connected neighbors.
 * Includes the token bucket, the last degree of the neighbor that was observed,
 * the queries that already were relayed to this neighbor, the queue of queries
 * that still need to be relayed to it etc.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class GiaConnectionMetadata {

	private int lastDegreeObserved;

	private Set<IResource> resources = Collections.emptySet();

	private TokenBucket queryTokens;

	TimeoutSet<Integer> queryUIDsRelayed;

	ISchedQueue<EnqueuedQuery> relayQueue;

	public GiaConnectionMetadata(IGiaConfig config, LocalClock clock,
			long initialTAR, int lastDegreeObserved) {
		this.lastDegreeObserved = lastDegreeObserved;
		queryTokens = new TokenBucket(clock, 0, config.getTokenBucketSize(),
				initialTAR);
		queryUIDsRelayed = new TimeoutSet<Integer>(
				config.getQueryCacheTimeout());
	}

	/**
	 * Returns the last degree of the neighbor that was observed
	 * 
	 * @param lastDegreeObserved
	 */
	public void setLastDegreeObserved(int lastDegreeObserved) {
		this.lastDegreeObserved = lastDegreeObserved;
	}

	/**
	 * Returns the token bucket that regulates the rate of queries that may be
	 * sent to the neighbor.
	 * 
	 * @return
	 */
	public TokenBucket getQueryTokenBucket() {
		return queryTokens;
	}

	/**
	 * Returns the last number of neighbors that was observed at the neighbor.
	 * 
	 * @return
	 */
	public int getLastDegreeObserved() {
		return lastDegreeObserved;
	}

	/**
	 * Sets the replicated resources of the neighbor to the given set (One-hop
	 * replication)
	 * 
	 * @param resources
	 */
	public void setReplicatedResources(Set<IResource> resources) {
		this.resources = resources;
	}

	/**
	 * Returns the set of resources that the neighbor replicated here
	 * 
	 * @return
	 */
	public Set<IResource> getResources() {
		return Collections.unmodifiableSet(resources);
	}

	@Override
	public String toString() {
		return "Metadata(lastDegreeObserved=" + lastDegreeObserved
				+ ", resources=" + resources + getQueryTokenBucket() + ", "
				+ "RelayedQIDs=" + queryUIDsRelayed + ")";
	}

	/**
	 * Marks the query with the given query UID as relayed, so that it is not
	 * relayed to this node again.
	 * 
	 * @param queryUID
	 */
	public void markQueryAsRelayed(int queryUID) {
		queryUIDsRelayed.addNow(queryUID);
	}

	/**
	 * Returns whether the query with the given query UID already was relayed.
	 * 
	 * @param queryUID
	 * @return
	 */
	public boolean hasRelayedQuery(int queryUID) {
		return queryUIDsRelayed.contains(queryUID);
	}

	/**
	 * Removes the query with the given queryUID from the query cache.
	 * 
	 * @param queryUID
	 * @return
	 */
	public boolean removeQueryFromCache(int queryUID) {
		return queryUIDsRelayed.remove(queryUID);
	}

	/**
	 * Returns the relay queue for this neighbor.
	 * 
	 * @return
	 */
	public ISchedQueue<EnqueuedQuery> getRelayQueue() {
		return relayQueue;
	}

	/**
	 * Sets the relay queue of this neighbor to the given value.
	 * 
	 * @param relayQueue
	 */
	public void setRelayQueue(ISchedQueue<EnqueuedQuery> relayQueue) {
		this.relayQueue = relayQueue;
	}

}
