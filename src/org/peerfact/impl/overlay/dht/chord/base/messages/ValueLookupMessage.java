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

package org.peerfact.impl.overlay.dht.chord.base.messages;

import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordContact;
import org.peerfact.impl.overlay.dht.chord.base.components.ChordID;

/**
 * This message is used in the context of the realization of the value lookup
 * functionality, defined by the DHTNode interface. It is send to a peer
 * responsible for a key, which then checks if it has a DHTObject for a given
 * key in its store.
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class ValueLookupMessage extends AbstractRequestMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8551315411713947697L;

	private final ChordID targetKey;

	public ValueLookupMessage(AbstractChordContact senderContact,
			AbstractChordContact receiverContact, ChordID targetKey) {
		super(senderContact, receiverContact);

		this.targetKey = targetKey;
	}

	public ChordID getTargetKey() {
		return targetKey;
	}

	@Override
	public long getSize() {
		return this.targetKey.getTransmissionSize() + super.getSize();
	}

}
