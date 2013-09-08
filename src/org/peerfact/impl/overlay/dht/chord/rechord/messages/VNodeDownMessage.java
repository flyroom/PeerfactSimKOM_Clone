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

package org.peerfact.impl.overlay.dht.chord.rechord.messages;

import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordContact;
import org.peerfact.impl.overlay.dht.chord.base.components.ChordID;
import org.peerfact.impl.overlay.dht.chord.base.messages.AbstractRequestMessage;
import org.peerfact.impl.overlay.dht.chord.base.messages.IStabilizeMessage;

public class VNodeDownMessage extends AbstractRequestMessage implements
		IStabilizeMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1692897282862983247L;

	private ChordID vNodeThatIsDown;

	private AbstractChordContact receiverL;

	private AbstractChordContact senderL;

	public VNodeDownMessage(AbstractChordContact senderContact,
			AbstractChordContact receiverContact) {
		super(senderContact, receiverContact);
		this.vNodeThatIsDown = null;
		receiverL = receiverContact;
		senderL = senderContact;
	}

	public ChordID getReceiverID() {
		return receiverL.getOverlayID();
	}

	public ChordID getSenderID() {
		return senderL.getOverlayID();
	}

	public void setvNode(ChordID id) {
		this.vNodeThatIsDown = id;
	}

	public ChordID getVNode() {
		return this.vNodeThatIsDown;
	}

	@Override
	public long getSize() {
		return super.getSize() + vNodeThatIsDown.getTransmissionSize();
	}

}
