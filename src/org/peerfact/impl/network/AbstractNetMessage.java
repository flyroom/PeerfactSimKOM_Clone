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

package org.peerfact.impl.network;

import org.peerfact.api.common.Message;
import org.peerfact.api.network.NetID;
import org.peerfact.api.network.NetMessage;
import org.peerfact.api.network.NetProtocol;

/**
 * This abstract class provides a skeletal implementation of the
 * <code>NetMessage<code> interface to lighten the effort for implementing this interface.
 * 
 * @author Sebastian Kaune <peerfact@kom.tu-darmstadt.de>
 * @author Konstantin Pussep
 * @version 3.0, 11/29/2007
 */
public abstract class AbstractNetMessage implements NetMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8583662213374293967L;

	/**
	 * The payload of the NetMessage.
	 */
	private Message payload;

	/**
	 * The NetID of the receiver.
	 */
	private NetID receiver;

	/**
	 * The NetID of the sender.
	 */
	private NetID sender;

	/**
	 * The NetProtocol of the NetMessage.
	 */
	private NetProtocol netProtocol;

	/**
	 * Constructor called by subclasses of this
	 * 
	 * @param payload
	 *            The payload of the ComplexNetworkMessage.
	 * @param receiver
	 *            The NetID of the receiver.
	 * @param sender
	 *            The NetID of the sender.
	 * @param netProtocol
	 *            The ServiceCategory of the ComplexNetworkMessage.
	 */
	public AbstractNetMessage(Message payload, NetID receiver, NetID sender,
			NetProtocol netProtocol) {
		this.payload = payload;
		this.receiver = receiver;
		this.sender = sender;
		this.netProtocol = netProtocol;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.peerfact.api.network.NetMessage#getReceiver()
	 */
	@Override
	public NetID getReceiver() {
		return this.receiver;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.peerfact.api.network.NetMessage#getSender()
	 */
	@Override
	public NetID getSender() {
		return this.sender;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.peerfact.api.network.NetMessage#getNetProtocol()
	 */
	@Override
	public NetProtocol getNetProtocol() {
		return this.netProtocol;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.peerfact.api.common.Message#getPayload()
	 */
	@Override
	public Message getPayload() {
		return this.payload;
	}

}
