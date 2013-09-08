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

import org.peerfact.Constants;
import org.peerfact.api.overlay.OverlayKey;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordContact;

/**
 * This message is used to initialize a download.
 * 
 * @author Philip Wette <info@peerfact.org>
 * 
 * @version 21/06/2011
 */
public class StartDownloadMessage extends AbstractRequestMessage {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1836302722964050742L;

	OverlayKey<?> documentId;

	long totalFilesize;

	long sizeOfNextChunk;

	public StartDownloadMessage(AbstractChordContact senderContact,
			AbstractChordContact receiverContact, OverlayKey<?> documentId,
			long totalFileSize, long sizeOfNextChunk) {
		super(senderContact, receiverContact);
		this.documentId = documentId;
		this.totalFilesize = totalFileSize;
		this.sizeOfNextChunk = sizeOfNextChunk;
	}

	public OverlayKey<?> getDocumentId() {
		return documentId;
	}

	public long getTotalFilesize() {
		return totalFilesize;
	}

	public long getSizeOfNextChunk() {
		return sizeOfNextChunk;
	}

	@Override
	public long getSize() {
		return documentId.getTransmissionSize() + 2 * Constants.LONG_SIZE
				+ super.getSize();
	}
}
