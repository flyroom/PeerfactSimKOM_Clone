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
import org.peerfact.impl.overlay.unstructured.heterogeneous.api.ConfID;
import org.peerfact.impl.overlay.unstructured.heterogeneous.common.IGnutellaConfig;

/**
 * 
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public interface IGnutella06Config extends IGnutellaConfig {

	/**
	 * Returns the maximum number of connections an ultrapeer may keep to other
	 * ultrapeers
	 * 
	 * @return
	 */
	@ConfID("MAX_SP_TO_SP_CONNECTIONS")
	public int getMaxSPToSPConnections();

	/**
	 * Returns the maximum number of connections a leaf may keep to ultrapeers
	 * 
	 * @return
	 */
	@ConfID("MAX_LEAF_TO_SP_CONNECTIONS")
	public int getMaxLeafToSPConnections();

	/**
	 * Returns the maximum number of connections an ultrapeer may keep to leaves
	 */
	@ConfID("MAX_SP_TO_LEAF_CONNECTIONS")
	public int getMaxSPToLeafConnections();

	/**
	 * Returns, whether it is allowed to kick peers randomly in order to have
	 * room for poorly connected peers.
	 * 
	 * @return
	 */
	@ConfID("RANDOMLY_KICK_PEER")
	public boolean randomlyKickPeer();

	/**
	 * Returns the probability to kick a random peer if a poorly connected one
	 * attempts to connect and the routing table is full.
	 * 
	 * @return
	 */
	@ConfID("RANDOMLY_KICK_PEER_PROB")
	public int randomlyKickPeerProb();

	/**
	 * Returns the bandwidth below which a node has to become a leaf.
	 * 
	 * @return
	 */
	@ConfID("MAY_BE_ULTRAPEER_BANDWIDTH")
	public Bandwidth getMayBeUltrapeerBandwidth();

	/**
	 * Returns the bandwidth above which a node has to become an ultrapeer.
	 * 
	 * @return
	 */
	@ConfID("MUST_BE_ULTRAPEER_BANDWIDTH")
	public Bandwidth getMustBeUltrapeerBandwidth();

	/**
	 * Returns the ratio in percent(!) of ultrapeers between the
	 * MayBeUltrapeerBandwidth and MustBeUltrapeerBandwidth
	 * 
	 * @return
	 */
	@ConfID("ULTRAPEER_RATIO")
	public int getUltrapeerRatio();

	/**
	 * Returns the duration of the probe query phase
	 * 
	 * @return
	 */
	@ConfID("PROBE_QUERY_DURATION")
	public long getProbeQueryDuration();

	/**
	 * Returns the duration of the controlled broadcast step in the Dynamic
	 * Query operation.
	 * 
	 * @return
	 */
	@ConfID("CONTROLLED_BCAST_STEP_DURATION")
	public long getControlledBcastStepDuration();

	/**
	 * Returns the number of TTLs a query can be relayed until it is dropped.
	 * 
	 * @return
	 */
	@ConfID("QUERY_DEPTH")
	public int getQueryDepth();

	/**
	 * Returns the duration a leaf waits for a response when it has requested an
	 * ultrapeer to do its query.
	 * 
	 * @return
	 */
	@ConfID("LEAF_QUERY_DURATION")
	public long getLeafQueryDuration();

	/**
	 * Returns the probability that a peer is kicked randomly in order to have
	 * room for other peers with low connectivity.
	 * 
	 * @return
	 */
	@ConfID("RANDOMLY_KICK_PEER_PROB")
	public int randomlyKickPeerLeafProb();

}
