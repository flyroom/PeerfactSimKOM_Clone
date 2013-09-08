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

import org.peerfact.api.common.Message;
import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.api.overlay.OverlayID;
import org.peerfact.api.overlay.OverlayKey;
import org.peerfact.impl.application.kbrapplication.KBRDummyApplication;
import org.peerfact.impl.application.kbrapplication.messages.QueryResultMessage;
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
 * This operation sends a QueryResultMessage direct to a contact
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class QueryResultOperation extends
		AbstractOperation<KBRDummyApplication, Object> {

	private final KBRDummyApplication app;

	private final OverlayKey<?> key;

	private final OverlayContact<OverlayID<?>> senderContact;

	private final OverlayContact<OverlayID<?>> documentProvider;

	private final OverlayContact<OverlayID<?>> receiverContact;

	/**
	 * @param app
	 *            the application that created the operation
	 * @param key
	 *            the key of the documents that was queried
	 * @param documentProvider
	 *            the provider of the document
	 * @param receiverContact
	 *            the contact of the receiver of the message
	 * @param callback
	 */
	public QueryResultOperation(KBRDummyApplication app, OverlayKey<?> key,
			OverlayContact<OverlayID<?>> documentProvider,
			OverlayContact<OverlayID<?>> receiverContact,
			OperationCallback<Object> callback) {
		super(app, callback);

		this.key = key;
		this.app = app;
		this.senderContact = app.getNode().getLocalOverlayContact();
		this.documentProvider = documentProvider;
		this.receiverContact = receiverContact;
	}

	@Override
	protected void execute() {

		Message msg = new QueryResultMessage(senderContact, key,
				documentProvider);

		// Route the message direct to the receiver (direct means the key is
		// null and
		// the receiver is given as hint)
		app.getNode().route(null, msg, receiverContact);

		operationFinished(true);
	}

	@Override
	public Object getResult() {
		// There is no meaningful result to return
		return null;
	}

}
