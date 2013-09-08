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

package org.peerfact.impl.overlay.dht.centralizedstorage.messages;

import org.peerfact.api.common.Message;
import org.peerfact.api.overlay.OverlayID;
import org.peerfact.impl.overlay.AbstractOverlayMessage;

/**
 * Server's reply to a client's request.
 * 
 * @author Konstantin Pussep <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public abstract class AbstractReplyMsg<T extends OverlayID<?>> extends
		AbstractOverlayMessage<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1304300424799235699L;

	private Object result;

	private T sender;

	private T receiver;

	public AbstractReplyMsg(T sender, T receiver, Object result) {
		super(sender, receiver);
		this.sender = sender;
		this.receiver = receiver;
		this.result = result;
	}

	public AbstractReplyMsg(AbstractRequestMsg<T> requestMsg, Object content) {
		this(requestMsg.getReceiver(), requestMsg.getSender(), content);
	}

	public Object getResult() {
		return this.result;
	}

	@Override
	public String toString() {
		return "" + getClass().getSimpleName() + "(" + getReceiver() + " -> "
				+ getSender() + "; result= " + this.result + ")";
	}

	@Override
	public Message getPayload() {
		return null;
	}

	@Override
	public long getSize() {
		return sender.getTransmissionSize() + receiver.getTransmissionSize();
	}
}
