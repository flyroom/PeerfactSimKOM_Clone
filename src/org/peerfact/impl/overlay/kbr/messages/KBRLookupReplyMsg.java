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

package org.peerfact.impl.overlay.kbr.messages;

import org.peerfact.api.common.Message;
import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.api.overlay.OverlayID;
import org.peerfact.api.overlay.kbr.KBRLookupMessage;

/**
 * This message is used to reply to lookups for nodes.
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * 
 * @param <T>
 *            the overlay's implementation of the <code>OverlayID</code>
 * @param <S>
 *            the overlay's implementation of the <code>OvrlayContact</code>
 * @version 05/06/2011
 */
public class KBRLookupReplyMsg<T extends OverlayID<?>, S extends OverlayContact<T>>
implements KBRLookupMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3333657015029844117L;

	private final S senderContact;

	private final int operationID;

	/**
	 * @param senderContact
	 *            the contact to the sender of the message
	 * @param operationID
	 *            the ID of the operation that triggered the lookup
	 */
	public KBRLookupReplyMsg(S senderContact, int operationID) {
		this.senderContact = senderContact;
		this.operationID = operationID;
	}

	@Override
	public Message getPayload() {
		// There is no meaningful payload to return
		return null;
	}

	@Override
	public long getSize() {
		/*
		 * senderContact (type: OverlayContact) + operationID (type: int) =
		 * OverlayID (160 bit) + TransInfo (32 bit (IP-address) + 16 (Port) +
		 * int (32 bit) = 240 bit = 30 byte
		 */
		return 30;
	}

	/**
	 * @return the contact information of the sender
	 */
	public S getSenderContact() {
		return senderContact;
	}

	/**
	 * @return the ID of the operation that triggered the lookup
	 */
	public int getOperationID() {
		return operationID;
	}
}
