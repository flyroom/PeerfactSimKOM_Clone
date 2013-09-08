/*
 * Copyright (c) UPB - University of Paderborn
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

import org.peerfact.api.overlay.OverlayKey;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordContact;

/**
 * This message is used to transfer a chunk of data
 * 
 * @author Philip Wette <info@peerfact.org>
 * 
 * @version 21/06/2011
 */
public class TransferDownloadMessage extends AbstractRequestMessage {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4687695484422052047L;

	OverlayKey<?> documentId;

	long payloadSize;

	public long getPayloadSize() {
		return payloadSize;
	}

	public void setPayloadSize(long payloadSize) {
		this.payloadSize = payloadSize;
	}

	public TransferDownloadMessage(AbstractChordContact senderContact,
			AbstractChordContact receiverContact, OverlayKey<?> documentId,
			long payloadSize) {
		super(senderContact, receiverContact);
		this.documentId = documentId;
		this.payloadSize = payloadSize;
	}

	public OverlayKey<?> getDocumentId() {
		return documentId;
	}

	@Override
	public long getSize() {
		return documentId.getTransmissionSize() + this.payloadSize
				+ super.getSize();
	}
}
