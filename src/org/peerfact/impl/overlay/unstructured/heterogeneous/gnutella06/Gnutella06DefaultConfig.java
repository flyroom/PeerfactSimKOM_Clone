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

package org.peerfact.impl.overlay.unstructured.heterogeneous.gnutella06;

import org.peerfact.api.network.Bandwidth;
import org.peerfact.impl.simengine.Simulator;

/**
 * Default constant-based implementation of a configurator.
 * 
 * @author <info@peerfact.org>
 * 
 * @version 05/06/2011
 */
public class Gnutella06DefaultConfig implements IGnutella06Config {

	private static final long CONNECT_TIMEOUT = 1 * Simulator.SECOND_UNIT;

	private static final int MAX_LEAF_TO_SP_CONNS = 3;

	private static final int MAX_SP_TO_LEAF_CONNS = 30;

	private static final int MAX_SP_TO_SP_CONNS = 32;

	private static final int PONG_CACHE_SIZE = 5;

	private static final int STALE_CONN_ATTEMPTS = 2;

	private static final int TRY_ULTRAPEERS_SIZE = 10;

	private static final long PING_INTERVAL = 15 * Simulator.SECOND_UNIT;

	private static final long PING_TIMEOUT = 1 * Simulator.SECOND_UNIT;

	private static final boolean RANDOMLY_KICK_PEER = true;

	private static final int RANDOMLY_KICK_PEER_PROB = 20; // probability in %

	private static final int RANDOMLY_KICK_PEER_LEAF_PROB = 2; // probability in
																// %

	private static final int TRY_ULTRAPEERS_ADD_LIMIT = 12;

	private static final int ULTRAPEER_RATIO = 90;

	private static final long BOOTSTRAP_INTVL = 5 * Simulator.SECOND_UNIT;

	private static final long RESPONSE_TIMEOUT = 1 * Simulator.SECOND_UNIT;

	private static final int QUERY_DEPTH = 2;

	private static final long CONTROLLED_BCAST_STEP_DURATION = 1 * Simulator.SECOND_UNIT;

	private static final long PROBE_QUERY_DURATION = 1 * Simulator.SECOND_UNIT;

	private static final int REQUIRED_HIT_COUNT = 1;

	private static final long LEAF_QUERY_DURATION = 40 * Simulator.SECOND_UNIT;

	private static final long QUERY_CACHE_TIMEOUT = 30 * Simulator.SECOND_UNIT;

	private static final Bandwidth MAY_BE_ULTRAPEER_BANDWIDTH = new Bandwidth(
			100000d, 100000d); // 50KByte/s up&down

	private static final Bandwidth MUST_BE_ULTRAPEER_BANDWIDTH = new Bandwidth(
			2000000d, 2000000d); // 2MByte/s up&down

	private boolean PONGCACHE_CONSIDER_ONLY_LAST_ENTRY = false;

	@Override
	public long getConnectTimeout() {
		return CONNECT_TIMEOUT;
	}

	@Override
	public int getMaxLeafToSPConnections() {
		return MAX_LEAF_TO_SP_CONNS;
	}

	@Override
	public int getMaxSPToLeafConnections() {
		return MAX_SP_TO_LEAF_CONNS;
	}

	@Override
	public int getMaxSPToSPConnections() {
		return MAX_SP_TO_SP_CONNS;
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
	public int getTryPeersSize() {
		return TRY_ULTRAPEERS_SIZE;
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
	public boolean randomlyKickPeer() {
		return RANDOMLY_KICK_PEER;
	}

	@Override
	public int randomlyKickPeerProb() {
		return RANDOMLY_KICK_PEER_PROB;
	}

	@Override
	public int randomlyKickPeerLeafProb() {
		return RANDOMLY_KICK_PEER_LEAF_PROB;
	}

	@Override
	public int getTryPeersAddLimit() {
		return TRY_ULTRAPEERS_ADD_LIMIT;
	}

	@Override
	public int getUltrapeerRatio() {
		return ULTRAPEER_RATIO;
	}

	@Override
	public Bandwidth getMayBeUltrapeerBandwidth() {
		return MAY_BE_ULTRAPEER_BANDWIDTH;
	}

	@Override
	public Bandwidth getMustBeUltrapeerBandwidth() {
		return MUST_BE_ULTRAPEER_BANDWIDTH;
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
	public long getControlledBcastStepDuration() {
		return CONTROLLED_BCAST_STEP_DURATION;
	}

	@Override
	public long getProbeQueryDuration() {
		return PROBE_QUERY_DURATION;
	}

	@Override
	public int getQueryDepth() {
		return QUERY_DEPTH;
	}

	@Override
	public int getRequiredHitCount() {
		return REQUIRED_HIT_COUNT;
	}

	@Override
	public long getLeafQueryDuration() {
		return LEAF_QUERY_DURATION;
	}

	@Override
	public long getQueryCacheTimeout() {
		return QUERY_CACHE_TIMEOUT;
	}

	@Override
	public boolean getConsiderOnlyLastEntry() {
		return PONGCACHE_CONSIDER_ONLY_LAST_ENTRY;
	}

}
