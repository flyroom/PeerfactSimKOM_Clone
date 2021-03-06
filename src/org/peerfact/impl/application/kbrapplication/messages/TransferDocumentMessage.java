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

package org.peerfact.impl.application.kbrapplication.messages;

import org.peerfact.api.common.Message;
import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.api.overlay.OverlayKey;
import org.peerfact.api.overlay.cd.Document;

/**
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * This part of the Simulator is not maintained in the current version of
 * PeerfactSim.KOM. There is no intention of the authors to fix this
 * circumstances, since the changes needed are huge compared to overall benefit.
 * 
 * If you want it to work correctly, you are free to make the specific changes
 * and provide it to the community.
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! !!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * 
 * This message is used to send a document.
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class TransferDocumentMessage implements Message {

	/**
	 * 
	 */
	private static final long serialVersionUID = 380915064420003758L;

	private final OverlayContact<?> senderContact;

	private final OverlayKey<?> keyOfDocument;

	private final Document<?> doc;

	/**
	 * @param keyOfDocument
	 *            the key of the document transfered within this message
	 * @param senderContact
	 *            the contact oft the node that sends the document
	 * @param doc
	 *            the document that is sent
	 */
	public TransferDocumentMessage(OverlayKey<?> keyOfDocument,
			OverlayContact<?> senderContact, Document<?> doc) {

		this.senderContact = senderContact;
		this.keyOfDocument = keyOfDocument;
		this.doc = doc;
	}

	@Override
	public Message getPayload() {
		// There is no meaningful payload to return
		return null;
	}

	@Override
	public long getSize() {
		return doc.getSize() + MessageConfig.Sizes.TransferDocumentMessage;
	}

	/**
	 * @return the contact oft the node that sends the document
	 */
	public OverlayContact<?> getSenderContact() {
		return senderContact;
	}

	/**
	 * @return the key of the document transfered within this message
	 */
	public OverlayKey<?> getKeyOfDocument() {
		return keyOfDocument;
	}

}
