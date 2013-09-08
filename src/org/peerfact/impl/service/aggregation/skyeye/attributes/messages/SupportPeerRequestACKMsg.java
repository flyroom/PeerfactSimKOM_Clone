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

package org.peerfact.impl.service.aggregation.skyeye.attributes.messages;

import org.peerfact.api.service.skyeye.SkyNetNodeInfo;
import org.peerfact.impl.service.aggregation.skyeye.AbstractSkyNetMessage;

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
 * This class defines a SkyNet-message, which is used as ACK to a
 * <code>SupportPeerRequestMsg</code>. In addition, it contains the answer of
 * the SkyNet-node, concerning the question of being a Support Peer for a
 * requesting Coordinator.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 15.11.2008
 * 
 */
public class SupportPeerRequestACKMsg extends AbstractSkyNetMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4805095199830775165L;

	private SkyNetNodeInfo nodeInfo;

	private boolean accept;

	public SupportPeerRequestACKMsg(SkyNetNodeInfo senderNodeInfo,
			SkyNetNodeInfo receiverNodeInfo, SkyNetNodeInfo nodeInfo,
			boolean accept, long skyNetMsgID, boolean senderSP) {
		super(senderNodeInfo, receiverNodeInfo, skyNetMsgID, true, false,
				senderSP);
		this.nodeInfo = nodeInfo;
		this.accept = accept;
	}

	/**
	 * This method returns the actual ID of a SkyNet-node, if it accepts to play
	 * the role of a Support Peer for the requesting Coordinator.
	 * 
	 * @return the ID of the answering SkyNet-node
	 */
	public SkyNetNodeInfo getNodeInfo() {
		return nodeInfo;
	}

	/**
	 * This method outlines, if a requested SkyNet-node accepts its role as
	 * Support Peer or not.
	 * 
	 * @return <code>true</code>, if the requested node accepts,
	 *         <code>false</code> otherwise.
	 */
	public boolean isAccept() {
		return accept;
	}

}
