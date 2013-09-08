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

/**
 * 
 */
package org.peerfact.impl.transport;

import java.util.EventObject;

import org.peerfact.api.common.Message;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.api.transport.TransLayer;
import org.peerfact.api.transport.TransProtocol;


/**
 * TransMsgEvent comprises data necessary to implement the virtual communication
 * between higher layers which are located above the TransLayer in the protocol
 * stack (such as overlays or applications). That is, the message decapsulation
 * process is done using TransMsgEvent as all necessary data is passed from the
 * TransLayer to the above registered layers which implement the
 * {@link org.peerfact.api.transport#TransMessageListener} interface.
 * 
 * @author Sebastian Kaune <peerfact@kom.tu-darmstadt.de>
 * @author Konstantin Pussep
 * @version 3.0, 11/29/2007
 * 
 */
public class TransMsgEvent extends EventObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6211928407401763190L;

	private TransInfo sender;

	private TransProtocol protocol;

	private int commId;

	private Message payload;

	/**
	 * Constructs a TransMsgEvent
	 * 
	 * @param msg
	 *            the received transport message
	 * @param sender
	 *            the TransInfo of the sender
	 * @param source
	 *            the source of this event
	 */
	public TransMsgEvent(AbstractTransMessage msg, TransInfo sender,
			TransLayer source) {
		super(source);
		this.protocol = msg.getProtocol();
		this.commId = msg.getCommId();
		this.sender = sender;
		this.payload = msg.getPayload();
	}

	/**
	 * Returns the TransInfo of the sender
	 * 
	 * @return the TransInfo of the sender
	 */
	public TransInfo getSenderTransInfo() {
		return sender;
	}

	/**
	 * Returns the used transport protocol
	 * 
	 * @return the used transport protocol
	 */
	public TransProtocol getProtocol() {
		return protocol;
	}

	/**
	 * Returns the unique communication identifier. This method should be
	 * invoked by TransMessageListener which send replies to specific request by
	 * using the
	 * {@link TransLayer#sendReply(Message, TransMsgEvent, short, TransProtocol)}
	 * method.
	 * 
	 * @return the unique communication identifier of this message
	 */
	public int getCommId() {
		return commId;
	}

	/**
	 * Returns the data which was encapsulated in the transport message
	 * 
	 * @return the data which was encapsulated in the transport message
	 */
	public Message getPayload() {
		return payload;
	}

}
