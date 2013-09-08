/*
 * Copyright (c) 2005-2010 KOM – Multimedia Communications Lab
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

package org.peerfact.impl.overlay.dht.chord.carechord.messages;

import org.peerfact.Constants;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordContact;
import org.peerfact.impl.overlay.dht.chord.base.components.ChordID;
import org.peerfact.impl.overlay.dht.chord.base.messages.AbstractRequestMessage;

/**
 * Reply of LookupSuccessorsMessage
 */
public class SuccessorReplyMessage extends AbstractRequestMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3435918383994627027L;

	// the replying node is the i-th successor of the sending node
	private int successorNumber;

	// identifies the virtual node of the real node which is receiving this
	// message
	private ChordID virtualNodeIdentifier;

	public SuccessorReplyMessage(AbstractChordContact senderContact,
			ChordID virtualNodeIdentifier,
			AbstractChordContact receiverContact, int successorNumber) {
		super(senderContact, receiverContact);

		this.successorNumber = successorNumber;
		this.virtualNodeIdentifier = virtualNodeIdentifier;
	}

	public int getSuccessorNumber() {
		return successorNumber;
	}

	public ChordID getVirtualNodeIdentifier() {
		return virtualNodeIdentifier;
	}

	@Override
	public long getSize() {
		return super.getSize() + virtualNodeIdentifier.getTransmissionSize()
				+ Constants.INT_SIZE;
	}

	@Override
	public String toString() {
		return super.toString() + " number: " + successorNumber;
	}
}
