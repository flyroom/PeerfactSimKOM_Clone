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
import org.peerfact.impl.overlay.informationdissemination.cs.ClientID;
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
 * The leave message from the client to the server. It contains only the ID of
 * the client, which will be leave.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 01/06/2011
 * 
 */
public class LeaveMessage extends CSAbstractMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6439026773255582093L;

	/**
	 * The id of the leaving client.
	 */
	private ClientID clientID;

	/**
	 * Sets the id in the message, of the leaving client.
	 * 
	 * @param id
	 *            The ID of the leaving client.
	 */
	public LeaveMessage(ClientID id) {
		super(MSG_TYPE.LEAVE_MESSAGE);
		this.clientID = id;
	}

	@Override
	public long getSize() {
		return super.getSize() + clientID.getTransmissionSize();
	}

	@Override
	public Message getPayload() {
		return this;
	}

	/**
	 * Gets the ID of the leaving client.
	 * 
	 * @return The clientID.
	 */
	public ClientID getClientID() {
		return clientID;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof LeaveMessage) {
			LeaveMessage l = (LeaveMessage) o;
			return this.clientID.equals(l.clientID) && super.equals(o);
		}
		return false;
	}

	@Override
	public String toString() {
		StringBuffer temp = new StringBuffer();
		temp.append("[ MsgType: ");
		temp.append(getMsgType());
		temp.append(", clientID: ");
		temp.append(getClientID());
		temp.append(" ]");
		return temp.toString();
	}

}
