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
import org.peerfact.impl.application.kbrapplication.messages.QueryForDocumentMessage;
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
 * This operation is used to query for documents.
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class QueryForDocumentOperation extends
		AbstractOperation<KBRDummyApplication, Object> {

	private final OverlayKey<?> key;

	private final KBRDummyApplication app;

	private final OverlayContact<OverlayID<?>> senderContact;

	/**
	 * @param app
	 *            the application that created the operation
	 * @param key
	 *            the key of the document to query for
	 * @param callback
	 */
	public QueryForDocumentOperation(KBRDummyApplication app,
			OverlayKey<?> key,
			OperationCallback<Object> callback) {
		super(app, callback);

		this.key = key;
		this.app = app;
		this.senderContact = app.getNode().getLocalOverlayContact();
	}

	@Override
	protected void execute() {

		Message msg = new QueryForDocumentMessage(senderContact, key);
		app.getNode().route(key, msg, null);

		operationFinished(true);
	}

	@Override
	public Object getResult() {
		// There is no meaningful result to return
		return null;
	}

	public OverlayKey<?> getKeyQueriedFor() {
		return key;
	}

}
