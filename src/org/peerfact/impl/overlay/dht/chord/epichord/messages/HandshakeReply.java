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

import java.util.Set;

import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordContact;
import org.peerfact.impl.overlay.dht.chord.base.messages.AbstractReplyMsg;
import org.peerfact.impl.overlay.dht.chord.base.messages.IStabilizeMessage;
import org.peerfact.impl.overlay.dht.chord.epichord.components.ChordCache;

/**
 * Reply message for <code>HandshakeMsg</code>
 * 
 * @author Minh Hoang Nguyen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class HandshakeReply extends AbstractReplyMsg implements
		IStabilizeMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1327789493001056500L;

	private final AbstractChordContact predecessor;

	private final Set<AbstractChordContact> availableContacts;

	private final ChordCache chordCache;

	public HandshakeReply(AbstractChordContact senderContact,
			AbstractChordContact receiverContact,
			AbstractChordContact predecessor,
			Set<AbstractChordContact> availableContacts, ChordCache chordCache) {
		super(senderContact, receiverContact);
		this.predecessor = predecessor;
		this.availableContacts = availableContacts;
		this.chordCache = chordCache;
	}

	public AbstractChordContact getPredecessor() {
		return predecessor;
	}

	public Set<AbstractChordContact> getAvailableContacts() {
		return availableContacts;
	}

	public ChordCache getChordCache() {
		return chordCache;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " cache " + chordCache;
	}

	@Override
	public long getSize() {
		return (availableContacts.size() + 1)
				* predecessor.getTransmissionSize() + chordCache.size()
				+ super.getSize();
	}
}
