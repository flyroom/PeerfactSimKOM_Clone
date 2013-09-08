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

import org.peerfact.api.overlay.OverlayKey;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordContact;
import org.peerfact.impl.overlay.dht.chord.base.messages.AbstractRequestMessage;

/**
 * This message is used to periodically check weather a mirror is still online
 * or not.
 * 
 * @author Philip Wette
 * 
 * @version 21/06/2011
 */
public class CheckMirrorMessage extends AbstractRequestMessage {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7123499990988174487L;

	OverlayKey<?> documentId;

	public CheckMirrorMessage(AbstractChordContact senderContact,
			AbstractChordContact receiverContact, OverlayKey<?> documentId) {
		super(senderContact, receiverContact);
		this.documentId = documentId;
	}

	public OverlayKey<?> getDocumentId() {
		return documentId;
	}

	@Override
	public long getSize() {
		return this.documentId.getTransmissionSize() + super.getSize();
	}
}
