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

package org.peerfact.impl.overlay.unstructured.heterogeneous.gia;

import org.peerfact.impl.simengine.Simulator;

/**
 * Configuration class of Gia, working with constants.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class GiaDefaultConfig implements IGiaConfig {

	private static final long CONNECT_TIMEOUT = 1 * Simulator.SECOND_UNIT;

	private static final int PONG_CACHE_SIZE = 5;

	private static final int STALE_CONN_ATTEMPTS = 2;

	private static final long PING_INTERVAL = 15 * Simulator.SECOND_UNIT;

	private static final long PING_TIMEOUT = 1 * Simulator.SECOND_UNIT;

	private static final long BOOTSTRAP_INTVL = 5 * Simulator.SECOND_UNIT;

	private static final long RESPONSE_TIMEOUT = 1 * Simulator.SECOND_UNIT;

	private static final int REQUIRED_HIT_COUNT = 1;

	private static final long QUERY_CACHE_TIMEOUT = 30 * Simulator.SECOND_UNIT;

	private static final int CONNECT_DECISION_HYSTERESIS = 5;

	private static final long HANDSHAKE_TIMEOUT = 1 * Simulator.SECOND_UNIT;

	private static final int MAX_NBRS = 128;

	private static final int MIN_ALLOC = 4;

	private static final int MIN_NBRS = 3;

	private static final long ADAPTATION_MAX_INTERVAL = 10 * Simulator.SECOND_UNIT;

	private static final int ADAPTATION_AGGRESSIVENESS = 64;

	private static final long HOST_CACHE_TIMEOUT = 50 * Simulator.SECOND_UNIT;

	private static final int TRY_PEERS_ADD_LIMIT = 5;

	private static final int TRY_PEERS_SIZE = 10;

	/**
	 * Delay for transmission of one-hop replicated content after a successful
	 * connect operation.
	 */
	private static final long REPLICATION_DELAY = 50 * Simulator.MILLISECOND_UNIT;

	private static final double QUERY_BANDWIDTH_LIMIT_QUOTA = 0.2d;

	private static final double TOKEN_ALLOCATION_BANDWIDTH_QUOTA = 0.1d;

	private static final int TOKEN_BUCKET_SIZE = 5;

	private static final int MAX_QUERY_QUEUE_SIZE = 10;

	private static final long QUERY_TTL = 512;

	private static final long MAX_TIME_IN_QUERY_QUEUE = 10 * Simulator.SECOND_UNIT;

	private static final long CONTACT_ACTIVITY_TIMEOUT = 15 * Simulator.SECOND_UNIT;

	private static final double TOKEN_THROTTLE_QUOTA = 0.2;

	private static final int THROTTLE_TOKEN_QUEUE_SIZE = 7;

	private static final long QUERY_TIMEOUT = 20 * Simulator.SECOND_UNIT;

	private static final int HOST_CACHE_SIZE = 15;

	/*
	 * Capacity assignment
	 */

	private static final double DOWN_BW_PER_CAP_POINT = 2000; // (bytes/sec)

	private static final double MIN_CAPACITY = 1;

	private static final double UP_BW_PER_CAP_POINT = 2000; // (bytes/sec)

	private boolean PONGCACHE_CONSIDER_ONLY_LAST_ENTRY = false;

	@Override
	public long getConnectTimeout() {
		return CONNECT_TIMEOUT;
	}

	@Override
	public int getPongCacheSize() {
		return PONG_CACHE_SIZE;
	}

	@Override
	public int getStaleConnAttempts() {
		return STALE_CONN_ATTEMPTS;
	}

	@Override
	public long getPingInterval() {
		return PING_INTERVAL;
	}

	@Override
	public long getPingTimeout() {
		return PING_TIMEOUT;
	}

	@Override
	public long getBootstrapIntvl() {
		return BOOTSTRAP_INTVL;
	}

	@Override
	public long getResponseTimeout() {
		return RESPONSE_TIMEOUT;
	}

	@Override
	public int getRequiredHitCount() {
		return REQUIRED_HIT_COUNT;
	}

	@Override
	public long getQueryCacheTimeout() {
		return QUERY_CACHE_TIMEOUT;
	}

	@Override
	public boolean getConsiderOnlyLastEntry() {
		return PONGCACHE_CONSIDER_ONLY_LAST_ENTRY;
	}

	@Override
	public int getConnectDecisionHysteresis() {
		return CONNECT_DECISION_HYSTERESIS;
	}

	@Override
	public long getHandshakeTimeout() {
		return HANDSHAKE_TIMEOUT;
	}

	@Override
	public int getMaxNbrs() {
		return MAX_NBRS;
	}

	@Override
	public int getMinAlloc() {
		return MIN_ALLOC;
	}

	@Override
	public int getMinNbrs() {
		return MIN_NBRS;
	}

	@Override
	public long getAdaptationMaxInterval() {
		return ADAPTATION_MAX_INTERVAL;
	}

	@Override
	public double getAdaptationAggressiveness() {
		return ADAPTATION_AGGRESSIVENESS;
	}

	@Override
	public long getHostCacheTimeout() {
		return HOST_CACHE_TIMEOUT;
	}

	@Override
	public int getTryPeersAddLimit() {
		return TRY_PEERS_ADD_LIMIT;
	}

	@Override
	public int getTryPeersSize() {
		return TRY_PEERS_SIZE;
	}

	@Override
	public long getReplicationDelay() {
		return REPLICATION_DELAY;
	}

	@Override
	public double getQueryBandwidthLimitQuota() {
		return QUERY_BANDWIDTH_LIMIT_QUOTA;
	}

	@Override
	public double getTokenAllocationBandwidthQuota() {
		return TOKEN_ALLOCATION_BANDWIDTH_QUOTA;
	}

	@Override
	public int getTokenBucketSize() {
		return TOKEN_BUCKET_SIZE;
	}

	@Override
	public int getMaxQueryQueueSize() {
		return MAX_QUERY_QUEUE_SIZE;
	}

	@Override
	public long getQueryTTL() {
		return QUERY_TTL;
	}

	@Override
	public long getMaxTimeInQueryQueue() {
		return MAX_TIME_IN_QUERY_QUEUE;
	}

	@Override
	public long getContactActivityTimeout() {
		return CONTACT_ACTIVITY_TIMEOUT;
	}

	@Override
	public int getThrottleTokenQueueSize() {
		return THROTTLE_TOKEN_QUEUE_SIZE;
	}

	@Override
	public double getTokenThrottleQuota() {
		return TOKEN_THROTTLE_QUOTA;
	}

	@Override
	public long getQueryTimeout() {
		return QUERY_TIMEOUT;
	}

	@Override
	public int getHostCacheSize() {
		return HOST_CACHE_SIZE;
	}

	@Override
	public double getDownBWPerCapPoint() {
		return DOWN_BW_PER_CAP_POINT;
	}

	@Override
	public double getMinCapacity() {
		return MIN_CAPACITY;
	}

	@Override
	public double getUpBWPerCapPoint() {
		return UP_BW_PER_CAP_POINT;
	}

}
