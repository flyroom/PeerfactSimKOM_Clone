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

package org.peerfact.impl.overlay.informationdissemination.cs.messages;

import org.peerfact.api.common.Message;
import org.peerfact.impl.overlay.informationdissemination.cs.ClientNodeInfo;
import org.peerfact.impl.overlay.informationdissemination.cs.util.CSConstants.MSG_TYPE;

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
 * The Update message from Client to the Server. It contains a
 * {@link ClientNodeInfo}.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 06/01/2011
 * 
 */
public class UpdatePositionServerMessage extends CSAbstractMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5739198097674555699L;

	/**
	 * The clientNodeInfo from the client.
	 */
	ClientNodeInfo clientNodeInfo;

	/**
	 * Sets the given nodeInfo and the message type.
	 * 
	 * @param nodeInfo
	 *            The actually nodeInfo of the client.
	 */
	public UpdatePositionServerMessage(ClientNodeInfo nodeInfo) {
		super(MSG_TYPE.UPDATE_POSITION_SERVER_MESSAGE);
		this.clientNodeInfo = nodeInfo;
	}

	@Override
	public long getSize() {
		return super.getSize() + clientNodeInfo.getTransmissionSize();
	}

	@Override
	public Message getPayload() {
		return this;
	}

	/**
	 * Gets the clientNodeInfo from the client.
	 * 
	 * @return The clientNodeInfo from the client.
	 */
	public ClientNodeInfo getClientNodeInfo() {
		return clientNodeInfo;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof UpdatePositionServerMessage) {
			UpdatePositionServerMessage u = (UpdatePositionServerMessage) o;
			return this.clientNodeInfo.equals(u.clientNodeInfo)
					&& super.equals(o);
		}
		return false;
	}

	@Override
	public String toString() {
		StringBuffer temp = new StringBuffer();
		temp.append("[ MsgType: ");
		temp.append(getMsgType());
		temp.append(", clientNodeInfo: ");
		temp.append(getClientNodeInfo());
		temp.append(" ]");
		return temp.toString();
	}

}
