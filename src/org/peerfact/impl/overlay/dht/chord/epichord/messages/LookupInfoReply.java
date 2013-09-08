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

package org.peerfact.impl.overlay.dht.chord.epichord.messages;

import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordContact;
import org.peerfact.impl.overlay.dht.chord.base.messages.AbstractReplyMsg;
import org.peerfact.impl.overlay.dht.chord.base.messages.ISetupMessage;
import org.peerfact.impl.overlay.dht.chord.base.messages.LookupMessage;

/**
 * This message is used as reply for the <code>LookupMessage</code> to send
 * better contacts for EpiChord.
 * 
 * @author Matthias Feldotto <info@peerfact.org>
 * 
 */
public class LookupInfoReply extends AbstractReplyMsg implements ISetupMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 577483698459414099L;

	/**
	 * The better contacts.
	 */
	private final AbstractChordContact[] betterContacts;

	/**
	 * The initial lookup message.
	 */
	private final LookupMessage request;

	public LookupInfoReply(AbstractChordContact senderContact,
			AbstractChordContact receiverContact,
			AbstractChordContact[] betterContacts,
			LookupMessage request) {
		super(senderContact, receiverContact);
		this.betterContacts = betterContacts;
		this.request = request;
	}

	public AbstractChordContact[] getBetterContacts() {
		return betterContacts;
	}

	public LookupMessage getRequest() {
		return request;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " contacts "
				+ betterContacts + " target " + request.getTarget();
	}

	@Override
	public long getSize() {
		return request.getSize() + betterContacts.length
				* betterContacts[0].getTransmissionSize()
				+ super.getSize();
	}

}
