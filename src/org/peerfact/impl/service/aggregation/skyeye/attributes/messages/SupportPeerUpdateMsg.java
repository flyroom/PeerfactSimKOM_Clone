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
 * A parent Coordinator uses this message-type to inform its Support Peer, if it
 * calculated a new Parent-Coordinator. For that reason, he sends this message,
 * which comprises the ID of the new Parent-Coordinator, to the Support Peer,
 * which is thereby informed about the new receiver for the
 * <i>attribute-updates</i>.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 15.11.2008
 * 
 */
public class SupportPeerUpdateMsg extends AbstractSkyNetMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3683866989305048160L;

	private SkyNetNodeInfo parentCoordinator;

	public SupportPeerUpdateMsg(SkyNetNodeInfo senderNodeInfo,
			SkyNetNodeInfo receiverNodeInfo, SkyNetNodeInfo parentCoordinator,
			long skyNetMsgID) {
		super(senderNodeInfo, receiverNodeInfo, skyNetMsgID, false, true, false);
		this.parentCoordinator = parentCoordinator;
	}

	@Override
	public long getSize() {
		return SkyNetConstants.SKY_NET_NODE_INFO_SIZE + super.getSize();
	}

	/**
	 * This method returns the ID of the new Parent-Coordinator, which the
	 * Support Peer must address.
	 * 
	 * @return the ID of a Coordinator's Parent-Coordinator
	 */
	public SkyNetNodeInfo getParentCoordinatorInfo() {
		return parentCoordinator;
	}

}
