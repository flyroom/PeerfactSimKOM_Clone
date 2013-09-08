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

package org.peerfact.impl.service.aggregation.skyeye;

import org.peerfact.Constants;
import org.peerfact.api.common.Message;
import org.peerfact.api.service.skyeye.SkyNetConstants;
import org.peerfact.api.service.skyeye.SkyNetMessage;
import org.peerfact.api.service.skyeye.SkyNetNodeInfo;
import org.peerfact.impl.simengine.Simulator;

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
 * This abstract class is the base-class for all types of message, which are
 * used within SkyNet. The class implements all methods, which are introduced by
 * the <code>SkyNetMessage</code>-interface to relieve every extending message
 * of implementing the methods again.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 15.11.2008
 * 
 */
public abstract class AbstractSkyNetMessage implements SkyNetMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4564855165189859403L;

	private long timestamp;

	private SkyNetNodeInfo senderNodeInfo;

	private SkyNetNodeInfo receiverNodeInfo;

	private long skyNetMsgID;

	private boolean ack;

	private boolean receiverSP;

	private boolean senderSP;

	private long size;

	public AbstractSkyNetMessage(SkyNetNodeInfo senderNodeInfo,
			SkyNetNodeInfo receiverNodeInfo, long skyNetMsgID, boolean ack,
			boolean receiverSP, boolean senderSP) {
		timestamp = Simulator.getCurrentTime();
		this.senderNodeInfo = senderNodeInfo;
		this.receiverNodeInfo = receiverNodeInfo;
		this.skyNetMsgID = skyNetMsgID;
		this.ack = ack;
		this.receiverSP = receiverSP;
		this.senderSP = senderSP;
		this.size = Constants.LONG_SIZE
				+ (SkyNetConstants.SKY_NET_NODE_INFO_SIZE) * 2
				+ Constants.LONG_SIZE + 3 * Constants.BOOLEAN_SIZE;
	}

	@Override
	public long getTimestamp() {
		return timestamp;
	}

	@Override
	public SkyNetNodeInfo getSenderNodeInfo() {
		return senderNodeInfo;
	}

	@Override
	public SkyNetNodeInfo getReceiverNodeInfo() {
		return receiverNodeInfo;
	}

	@Override
	public long getSkyNetMsgID() {
		return skyNetMsgID;
	}

	@Override
	public boolean isACK() {
		return ack;
	}

	@Override
	public boolean isReceiverSP() {
		return receiverSP;
	}

	@Override
	public boolean isSenderSP() {
		return senderSP;
	}

	@Override
	public String toString() {
		return "[ SkyNetMsg "
				+ getSenderNodeInfo().getSkyNetID().getPlainSkyNetID() + " -> "
				+ getReceiverNodeInfo().getSkyNetID().getPlainSkyNetID()
				+ " | size: " + getSize() + " | MsgNo: " + getSkyNetMsgID()
				+ " | reply: " + isACK() + " ]";
	}

	@Override
	public long getSize() {
		return size;
	}

	@Override
	public Message getPayload() {
		// not needed
		return null;
	}

}
