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

import org.peerfact.api.overlay.dht.DHTObject;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordContact;
import org.peerfact.impl.overlay.dht.chord.base.components.ChordID;

/**
 * This message is used in the context of the realization of the store
 * functionality, defined by the DHTNode interface. It is send to a peer
 * responsible for a key, which then stores the transmitted DHTObject.
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class StoreMessage extends AbstractRequestMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6461772188002998474L;

	ChordID key;

	transient DHTObject object;

	public StoreMessage(AbstractChordContact senderContact,
			AbstractChordContact receiverContact, ChordID objectKey,
			DHTObject object) {
		super(senderContact, receiverContact);

		this.key = objectKey;
		this.object = object;

	}

	@Override
	public long getSize() {
		return key.getTransmissionSize() + super.getSize();
	}

	public ChordID getKey() {
		return key;
	}

	public DHTObject getObject() {
		return object;
	}

}
