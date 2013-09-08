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
import org.peerfact.impl.overlay.dht.chord.base.components.ChordKey;

/**
 * This message is used to initialize a download.
 * 
 * @author Philip Wette <info@peerfact.org>
 * 
 * @version 21/06/2011
 */
public class InitializeDownloadMessage extends AbstractRequestMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 36915623784019895L;

	ChordKey documentId;

	public InitializeDownloadMessage(AbstractChordContact senderContact,
			AbstractChordContact receiverContact, ChordKey documentId) {
		super(senderContact, receiverContact);
		this.documentId = documentId;
	}

	public ChordKey getDocumentId() {
		return documentId;
	}

	@Override
	public long getSize() {
		return documentId.getTransmissionSize() + super.getSize();
	}
}
