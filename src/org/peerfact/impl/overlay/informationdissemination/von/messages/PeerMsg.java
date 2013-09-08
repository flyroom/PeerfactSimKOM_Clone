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

package org.peerfact.impl.overlay.informationdissemination.von.messages;

import org.peerfact.api.common.Message;
import org.peerfact.impl.overlay.informationdissemination.von.VonID;
import org.peerfact.impl.overlay.informationdissemination.von.VonNodeInfo;

/**
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * This part of the Simulator is not maintained in the current version of
 * PeerfactSim.KOM. There is no intention of the authors to fix this
 * circumstances, since the changes needed are huge compared to overall benefit.
 * 
 * If you want it to work correctly, you are free to make the specific changes
 * and provide it to the community.
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! !!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * 
 * This message is used to inform nodes about new neighbors.
 * 
 * It includes a list of new nodes and timestamps when they were last in contact
 * with the sender.
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 */
public class PeerMsg extends AbstractVonMsg {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8519852605849319459L;

	VonNodeInfo[] nodes;

	Long[] timestamps;

	public PeerMsg(VonID sender, VonID receiver, VonNodeInfo[] nodes,
			Long[] timestamps) {
		super(sender, receiver);
		this.nodes = nodes;
		this.timestamps = timestamps;
	}

	@Override
	public Message getPayload() {
		// This message does not contain a payload
		return null;
	}

	@Override
	public long getSize() {
		// size = sizeOfAbstractMsg + nodes.length*sizeOfNodeInfo +
		// timestamps.length*sizeOfTimestamp
		// sizeOfTimestamp = 4byte (according to the VON specifications)
		long sizeOfNodeInfo = (nodes.length > 0) ? nodes[0]
				.getTransmissionSize() : 0;
		return getSizeOfAbstractMessage() + nodes.length * sizeOfNodeInfo
				+ timestamps.length * 4;
	}

	public VonNodeInfo[] getNodes() {
		return nodes;
	}

	public Long[] getTimestamps() {
		return timestamps;
	}

}
