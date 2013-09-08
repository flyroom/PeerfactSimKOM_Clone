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
import org.peerfact.api.transport.TransInfo;
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
 * The Reply from the server of a {@link JoinMessage}. It contains the clientID
 * for the Client and the {@link TransInfo} for the server.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 01/06/2011
 * 
 */
public class JoinReplyMessage extends CSAbstractMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2258620160917711070L;

	/**
	 * The clientID for the client
	 */
	private ClientID clientID;

	/**
	 * The {@link TransInfo} of the server.
	 */
	private TransInfo serverTransInfo;

	/**
	 * Sets the ID, serverTransInfo and the messageType for the message.
	 * 
	 * @param id
	 *            The client ID for the client
	 * @param serverTransInfo
	 *            The {@link TransInfo} for a server, which the client should be
	 *            used.
	 */
	public JoinReplyMessage(ClientID id, TransInfo serverTransInfo) {
		super(MSG_TYPE.JOIN_RESPONSE);
		this.clientID = id;
		this.serverTransInfo = serverTransInfo;
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
	 * Gets the clientID for the client.
	 * 
	 * @return The clientID for the client
	 */
	public ClientID getClientId() {
		return clientID;
	}

	/**
	 * Gets the {@link TransInfo} of the server, which should be used from the
	 * client.
	 * 
	 * @return The {@link TransInfo} for the server, which should be used from
	 *         the client.
	 */
	public TransInfo getServerTransInfo() {
		return serverTransInfo;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof JoinReplyMessage) {
			JoinReplyMessage j = (JoinReplyMessage) o;
			return this.clientID.equals(j.clientID)
					&& this.serverTransInfo.equals(j.serverTransInfo)
					&& super.equals(o);
		}
		return false;
	}

	@Override
	public String toString() {
		StringBuffer temp = new StringBuffer();
		temp.append("[ MsgType: ");
		temp.append(getMsgType());
		temp.append(", clientID: ");
		temp.append(getClientId());
		temp.append(", serverTransInfo: ");
		temp.append(getServerTransInfo());
		temp.append(" ]");
		return temp.toString();
	}
}
