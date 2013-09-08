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

import org.peerfact.impl.overlay.unstructured.heterogeneous.api.ConfID;
import org.peerfact.impl.overlay.unstructured.heterogeneous.common.IGnutellaConfig;

/**
 * GIA Configuration interface.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public interface IGiaConfig extends IGnutellaConfig {

	@ConfID("HANDSHAKE_TIMEOUT")
	public long getHandshakeTimeout();

	/**
	 * @return
	 */
	@ConfID("CONNECT_DECISION_HYSTERESIS")
	public int getConnectDecisionHysteresis();

	/**
	 * The min_alloc parameter. The paper says:
	 * "Finest level of granularity into which we are willing to split a node's capacity."
	 * 
	 * @return
	 */
	@ConfID("MIN_ALLOC")
	public int getMinAlloc();

	/**
	 * Minimum number of neighbors a peer should have.
	 * 
	 * @return
	 */
	@ConfID("MIN_NBRS")
	public int getMinNbrs();

	/**
	 * Maximum number of neighbors a peer may have.
	 * 
	 * @return
	 */
	@ConfID("MAX_NBRS")
	public int getMaxNbrs();

	/**
	 * The maximum interval between two adaptation attempts. If a peer is fully
	 * satisfied with its neighborhood, it uses this adaptation interval.
	 * 
	 * @return
	 */
	@ConfID("ADAPTATION_MAX_INTERVAL")
	public long getAdaptationMaxInterval();

	/**
	 * The daptation aggressiveness defines how fast the adaptation interval
	 * shrinks with lower satisfaction: adaptationInterval =
	 * adaptationMaxInterval * adaptationAggressiveness ^ -(1 -
	 * satisfactionLevel)
	 * 
	 * @return
	 */
	@ConfID("ADAPTATION_AGGRESSIVENESS")
	public double getAdaptationAggressiveness();

	/**
	 * After this time in the host cache, a contact is dropped.
	 * 
	 * @return
	 */
	@ConfID("HOST_CACHE_TIMEOUT")
	public long getHostCacheTimeout();

	/**
	 * The delay of the One-hop replication being initiated after a connection
	 * has succeeded.
	 * 
	 * @return
	 */
	@ConfID("REPLICATION_DELAY")
	public long getReplicationDelay();

	/**
	 * The time to live of random-walk queries.
	 * 
	 * @return
	 */
	@ConfID("QUERY_TTL")
	public long getQueryTTL();

	/**
	 * The size of the token buckets that regulate the flow of queries from
	 * every neighbor.
	 * 
	 * @return
	 */
	@ConfID("TOKEN_BUCKET_SIZE")
	public int getTokenBucketSize();

	/**
	 * The part of the bandwidth of the node that may be used solely for
	 * queries.
	 * 
	 * @return
	 */
	@ConfID("QUERY_BANDWIDTH_LIMIT_QUOTA")
	public double getQueryBandwidthLimitQuota();

	/**
	 * The part of the bandwidth of the node that may be used solely for
	 * queries, as it is assumed by the token allocation Should be slightly
	 * lower than QueryBandwidthLimitQuota
	 * 
	 * @return
	 */
	@ConfID("TOKEN_ALLOCATION_BANDWIDTH_QUOTA")
	public double getTokenAllocationBandwidthQuota();

	/**
	 * Returns the maximum query queue size. If a query queue gets larger than
	 * this value, packets will be dropped.
	 * 
	 * @return
	 */
	@ConfID("MAX_QUERY_QUEUE_SIZE")
	public int getMaxQueryQueueSize();

	/**
	 * Returns the maximum time a packet may stay in the query queue. A packet
	 * that stays in the queue for a longer time is dropped.
	 * 
	 * @return
	 */
	@ConfID("MAX_TIME_IN_QUERY_QUEUE")
	public long getMaxTimeInQueryQueue();

	/**
	 * In Gia, only active neighbors share the bandwidth of a peer for the query
	 * relaying. A peer becomes active if it is sending queries, and becomes
	 * inactive again after the period given here.
	 * 
	 * @return
	 */
	@ConfID("CONTACT_ACTIVITY_TIMEOUT")
	public long getContactActivityTimeout();

	/**
	 * If the query queue of a neighbor gets longer than the given queue size,
	 * its token allocation rate is lowered by TokenThrottle Quota.
	 * 
	 * @return
	 */
	@ConfID("THROTTLE_TOKEN_QUEUE_SIZE")
	public int getThrottleTokenQueueSize();

	/**
	 * If the query queue of a neighbor gets longer than ThrottleTokenQueueSize,
	 * its token allocation rate is lowered by the given value.
	 * 
	 * @return
	 */
	@ConfID("TOKEN_THROTTLE_QUOTA")
	public double getTokenThrottleQuota();

	/**
	 * If a query gets older than this value, the initiator will give up waiting
	 * for QueryHits.
	 * 
	 * @return
	 */
	@ConfID("QUERY_TIMEOUT")
	public long getQueryTimeout();

	/**
	 * Returns the size of the host cache
	 * 
	 * @return
	 */
	@ConfID("HOST_CACHE_SIZE")
	public int getHostCacheSize();

	/**
	 * Capacity Assignment: Returns the minimum capacity that may be assigned to
	 * a peer.
	 * 
	 * @return
	 */
	@ConfID("MIN_CAPACITY")
	public double getMinCapacity();

	/**
	 * For every UpBWPerCapPoint of its upstream bandwidth (in bytes/sec), the
	 * peer gets one capacity point. Then the minimum of upstream bandwidth and
	 * downstream bandwidth capacity is taken.
	 * 
	 * @return
	 */
	@ConfID("UP_BW_PER_CAP_POINT")
	public double getUpBWPerCapPoint();

	/**
	 * For every DownBWPerCapPoint a peer has got (in bytes/sec), the peer gets
	 * one capacity point. Then the minimum of upstream bandwidth and downstream
	 * bandwidth capacity is taken.
	 * 
	 * @return
	 */
	@ConfID("DOWN_BW_PER_CAP_POINT")
	public double getDownBWPerCapPoint();

}
