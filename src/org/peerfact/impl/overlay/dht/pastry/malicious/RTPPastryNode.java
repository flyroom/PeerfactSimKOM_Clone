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

package org.peerfact.impl.overlay.dht.pastry.malicious;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.peerfact.api.transport.TransLayer;
import org.peerfact.impl.overlay.dht.pastry.components.PastryContact;
import org.peerfact.impl.overlay.dht.pastry.nodestate.PastryRoutingTable;
import org.peerfact.impl.util.logging.SimLogger;


public class RTPPastryNode extends
		org.peerfact.impl.overlay.dht.pastry.components.PastryNode {

	private static Logger log = SimLogger.getLogger(RTPPastryNode.class);

	public RTPPastryNode(TransLayer transLayer, String rtpmethod) {

		super(transLayer);

		// Register the message handler at the transport layer
		getTransLayer().removeTransMsgListener(msgHandler, getPort());

		if (rtpmethod.equals("default")) {
			msgHandler = new DefaultRTPPastryMessageHandler(this);
		}

		getTransLayer().addTransMsgListener(msgHandler, getPort());

		log.debug("A new malicious node has spawned (id=" + this.getOverlayID()
				+ ")");

		leafSet.putAll(RTPPastryUtil.getEclipseNeighbors());
		neighborhoodSet.putAll(RTPPastryUtil.getEclipseNeighbors());
		((PastryRoutingTable) routingTable).insertAll(RTPPastryUtil
				.getEclipseNeighbors());
	}

	protected RTPPastryNode(TransLayer translayer) {
		super(translayer);

		leafSet.putAll(RTPPastryUtil.getEclipseNeighbors());
		neighborhoodSet.putAll(RTPPastryUtil.getEclipseNeighbors());
		((PastryRoutingTable) routingTable).insertAll(RTPPastryUtil
				.getEclipseNeighbors());
	}

	@Override
	public Collection<PastryContact> getAllNeighbors() {
		return RTPPastryUtil.getEclipseNeighbors();
	}

	@Override
	public Collection<PastryContact> getLeafSetNodes() {
		return RTPPastryUtil.getEclipseNeighbors();
	}

	@Override
	public Collection<PastryContact> getRoutingTableNodes() {
		return RTPPastryUtil.getEclipseNeighbors();
	}

	@Override
	public Collection<PastryContact> getNeighborSetNodes() {
		return RTPPastryUtil.getEclipseNeighbors();
	}

}
