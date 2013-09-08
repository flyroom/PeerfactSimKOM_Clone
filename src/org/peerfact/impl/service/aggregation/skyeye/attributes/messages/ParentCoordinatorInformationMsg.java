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

import org.peerfact.Constants;
import org.peerfact.api.service.skyeye.SkyNetNodeInfo;
import org.peerfact.impl.service.aggregation.skyeye.AbstractSkyNetMessage;
import org.peerfact.impl.service.aggregation.skyeye.SupportPeerInfo;

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
 * This message contains the information, which a Coordinator sends to its
 * Sub-Coordinators concerning the <i>attribute-updates</i>. The contents of
 * this messages is utilized to tell every child, if a new Support Peer for
 * load-distribution was chosen, and to submit the maximum amount of allowed
 * entries to every Sub-Coordinator.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 15.11.2008
 * 
 */
public class ParentCoordinatorInformationMsg extends AbstractSkyNetMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 500322734911632154L;

	private SupportPeerInfo spInfo;

	private int maxEntriesForCo;

	public ParentCoordinatorInformationMsg(SkyNetNodeInfo senderNodeInfo,
			SkyNetNodeInfo receiverNodeInfo, SupportPeerInfo spInfo,
			int maxEntriesForCo, long skyNetMsgID, boolean receiverSP) {
		super(senderNodeInfo, receiverNodeInfo, skyNetMsgID, false, receiverSP,
				false);
		this.spInfo = spInfo;
		this.maxEntriesForCo = maxEntriesForCo;
	}

	/**
	 * If the Coordinator uses a new Support Peer for load-distribution, this
	 * method returns the <code>SupportPeerInfo</code>-object, which contains
	 * the required information for a Sub-Coordinator to address that Support
	 * Peer.
	 * 
	 * @return the <code>SupportPeerInfo</code>-object
	 */
	public SupportPeerInfo getSupportPeerInfo() {
		return spInfo;
	}

	@Override
	public long getSize() {
		if (spInfo == null) {
			return 2 * Constants.INT_SIZE + super.getSize();
		} else {
			return 2 * Constants.INT_SIZE + SupportPeerInfo.getSize()
					+ super.getSize();
		}
	}

	/**
	 * Within the process of negotiating the amount of
	 * <code>AttributeEntry</code>s between a Coordinator and its
	 * Sub-Coordinators, this method returns the maximum amount of entries,
	 * which a Coordinator assigns to its children.
	 * 
	 * @return the maximum amount of entries, which a Sub-Coordinator may send.
	 */
	public int getMaxEntriesForCo() {
		return maxEntriesForCo;
	}

}
