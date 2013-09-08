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

package org.peerfact.impl.application.kbrapplication.operations;

import java.util.Collection;

import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.api.overlay.OverlayID;
import org.peerfact.api.overlay.OverlayKey;
import org.peerfact.api.overlay.cd.ContentDistribution;
import org.peerfact.api.overlay.cd.Document;
import org.peerfact.impl.application.kbrapplication.KBRDummyApplication;
import org.peerfact.impl.application.kbrapplication.messages.TransferDocumentMessage;
import org.peerfact.impl.common.AbstractOperation;


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
 * This operation transfers a document direct to a contact
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class TransferDocumentOperation extends
		AbstractOperation<KBRDummyApplication, Object> {

	KBRDummyApplication app;

	OverlayKey<?> keyOfDocument;

	OverlayContact<OverlayID<?>> receiverContact;

	/**
	 * @param app
	 *            the application that created the operation
	 * @param keyOfDocument
	 *            the key of the document to be sent
	 * @param receiverContact
	 *            the contact of the receiver of the message
	 * @param callback
	 */
	public TransferDocumentOperation(KBRDummyApplication app,
			OverlayKey<?> keyOfDocument,
			OverlayContact<OverlayID<?>> receiverContact,
			OperationCallback<Object> callback) {
		super(app, callback);

		this.app = app;
		this.keyOfDocument = keyOfDocument;
		this.receiverContact = receiverContact;
	}

	@Override
	protected void execute() {
		OverlayContact<OverlayID<?>> senderContact = app.getNode()
				.getLocalOverlayContact();

		// Load document from local storage
		Document<OverlayKey<?>> doc = getDocumnetFromStorage(keyOfDocument);

		// Send file direct to the receiver without routing for the key
		TransferDocumentMessage msg = new TransferDocumentMessage(
				keyOfDocument, senderContact, doc);
		app.getNode().route(null, msg, receiverContact);

		operationFinished(true);
	}

	@Override
	public Object getResult() {
		// There is no meaningful result to return
		return null;
	}

	private Document<OverlayKey<?>> getDocumnetFromStorage(OverlayKey<?> key) {
		Collection<OverlayKey<?>> keys = ((ContentDistribution<OverlayKey<?>>) (app
				.getHost()
				.getOverlay(ContentDistribution.class)))
				.listDocumentKeys();

		for (OverlayKey<?> currentKey : keys) {
			if (currentKey.compareTo((OverlayKey) key) == 0) {
				return ((ContentDistribution<OverlayKey<?>>) (app.getHost()
						.getOverlay(ContentDistribution.class)))
						.loadDocument(currentKey);
			}
		}
		return null;
	}
}
