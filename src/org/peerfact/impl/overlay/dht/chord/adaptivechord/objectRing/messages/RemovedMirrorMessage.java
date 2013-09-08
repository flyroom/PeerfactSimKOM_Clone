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

package org.peerfact.impl.overlay.dht.chord.adaptivechord.objectRing.messages;

import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordContact;
import org.peerfact.impl.overlay.dht.chord.base.components.ChordKey;
import org.peerfact.impl.overlay.dht.chord.base.messages.AbstractRequestMessage;

/**
 * Used to tell the oridin of a mirror that the mirror does not exist no more.
 * 
 * @author Philip Wette
 * 
 * @version 21/06/2011
 */
public class RemovedMirrorMessage extends AbstractRequestMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6693542804310825596L;

	private ChordKey key;

	public RemovedMirrorMessage(AbstractChordContact senderContact,
			AbstractChordContact receiverContact, ChordKey objectKey) {
		super(senderContact, receiverContact);

		this.key = objectKey;

	}

	@Override
	public long getSize() {
		return key.getTransmissionSize() + super.getSize();
	}

	public ChordKey getKey() {
		return key;
	}

}
