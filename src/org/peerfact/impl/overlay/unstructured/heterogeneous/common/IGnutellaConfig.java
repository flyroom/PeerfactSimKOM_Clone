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

package org.peerfact.impl.overlay.unstructured.heterogeneous.common;

import org.peerfact.impl.overlay.unstructured.heterogeneous.api.ConfID;

/**
 * 
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public interface IGnutellaConfig {

	/**
	 * Returns the size of the pong cache
	 * 
	 * @return
	 */
	@ConfID("PONG_CACHE_SIZE")
	public int getPongCacheSize();

	/**
	 * Returns the number of connection attempts before a contact is marked as
	 * stale.
	 * 
	 * @return
	 */
	@ConfID("STALE_CONN_ATTEMPTS")
	public int getStaleConnAttempts();

	/**
	 * Returns the timeout for connection attempts
	 * 
	 * @return
	 */
	@ConfID("CONNECT_TIMEOUT")
	public long getConnectTimeout();

	/**
	 * Returns the timeout of a ping request.
	 * 
	 * @return
	 */
	@ConfID("PING_TIMEOUT")
	public long getPingTimeout();

	/**
	 * Returns the interval of ping messages
	 * 
	 * @return
	 */
	@ConfID("PING_INTERVAL")
	public long getPingInterval();

	/**
	 * Response timeout for various message types.
	 * 
	 * @return
	 */
	@ConfID("RESPONSE_TIMEOUT")
	public long getResponseTimeout();

	/**
	 * Returns the required hit count that is necessary to mark a query as
	 * succeeded.
	 * 
	 * @return
	 */
	@ConfID("REQUIRED_HIT_COUNT")
	public int getRequiredHitCount();

	/**
	 * Returns if only the last entry of a received pong cache shall be taken
	 * into account as a new discovered peer.
	 * 
	 * @return
	 */
	@ConfID("CONSIDER_ONLY_LAST_ENTRY")
	public boolean getConsiderOnlyLastEntry();

	/**
	 * Returns the time a query UID is hold in the query cache to avoid
	 * duplicate relays of the same query.
	 * 
	 * @return
	 */
	@ConfID("QUERY_CACHE_TIMEOUT")
	public long getQueryCacheTimeout();

	/**
	 * Returns the bootstrap interval between two bootstrap attempts of the same
	 * node.
	 * 
	 * @return
	 */
	@ConfID("BOOTSTRAP_INTERVAL")
	public long getBootstrapIntvl();

	/**
	 * Returns a routing table size limit when peers received via the
	 * "TryUltrapeers" shall not be added to the routing table anymore.
	 * 
	 * @return
	 */
	@ConfID("TRY_PEERS_ADD_LIMIT")
	public int getTryPeersAddLimit();

	/**
	 * Returns the size of the "TryUltrapeers" list that is returned when a
	 * connection attempt is unsuccessful.
	 * 
	 * @return
	 */
	@ConfID("TRY_PEERS_SIZE")
	public int getTryPeersSize();

}
