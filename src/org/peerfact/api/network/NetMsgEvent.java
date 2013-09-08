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

package org.peerfact.api.network;

import java.util.EventObject;

import org.peerfact.api.common.Message;


/**
 * NetMsgEvents comprises data necessary to implement the virtual communication
 * between higher layers which are located above the NetLayer in the protocol
 * stack (such as the TransLayer). That is, the message decapsulation process is
 * done using NetMsgEvents as all necessary data is passed from the NetLayer to
 * the above registered layers which implement the NetMessageListener interface.
 * (@see org.peerfact.api.network#NetMessageListener)
 * 
 * @author Sebastian Kaune <peerfact@kom.tu-darmstadt.de>
 * @author Konstantin Pussep
 * @version 3.0, 11/29/2007
 * 
 */
public class NetMsgEvent extends EventObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8495581951571783332L;

	private Message payload;

	private NetID sender;

	private NetProtocol netProtocol;

	/**
	 * Constructs NetMsgEvent
	 * 
	 * @param netMsg
	 *            the NetMessage received by the NetLayer
	 * @param source
	 *            the source of this event
	 */
	public NetMsgEvent(NetMessage netMsg, NetLayer source) {
		super(source);
		this.netProtocol = netMsg.getNetProtocol();
		this.payload = netMsg.getPayload();
		this.sender = netMsg.getSender();
	}

	/**
	 * Returns the data which was encapsulated in the network message
	 * 
	 * @return the data which was encapsulated in the network message
	 */
	public Message getPayload() {
		return payload;
	}

	/**
	 * Returns the NetID of the sender of the received network message
	 * 
	 * @return the NetID of sender of the received network message
	 */
	public NetID getSender() {
		return sender;
	}

	/**
	 * Returns the used network protocol
	 * 
	 * @return the used network protocol
	 * 
	 */
	public NetProtocol getNetProtocol() {
		return netProtocol;
	}
}
