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

import org.peerfact.api.service.skyeye.SkyNetConstants;
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
 * This message contains the request of a Coordinator for a Support Peer. The
 * Coordinator sends the message to another node, which becomes a candidate for
 * supporting the requesting Coordinator. In addition, this message comprises
 * the required information for the receiving node, if it accepts its new role
 * as Support Peer.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 15.11.2008
 * 
 */
public class SupportPeerRequestMsg extends AbstractSkyNetMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3730418154545196794L;

	private SkyNetNodeInfo parentCoordinator;

	public SupportPeerRequestMsg(SkyNetNodeInfo senderNodeInfo,
			SkyNetNodeInfo receiverNodeInfo, SkyNetNodeInfo parentCoordinator,
			long skyNetMsgID) {
		super(senderNodeInfo, receiverNodeInfo, skyNetMsgID, false, false,
				false);
		this.parentCoordinator = parentCoordinator;
	}

	/**
	 * This method returns the ID of the Parent-Coordinator from the sending
	 * Coordinator. If the candidate for the Support Peer accepts its new role,
	 * it uses this information to send its received updates to the provided
	 * Parent-Coordinator.
	 * 
	 * @return the ID of a Coordinator's Parent-Coordinator
	 */
	public SkyNetNodeInfo getParentCoordinator() {
		return parentCoordinator;
	}

	@Override
	public long getSize() {
		return SkyNetConstants.SKY_NET_NODE_INFO_SIZE + super.getSize();
	}
}
