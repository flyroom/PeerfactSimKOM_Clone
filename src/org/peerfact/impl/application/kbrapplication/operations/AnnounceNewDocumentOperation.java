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
import org.peerfact.api.overlay.cd.ContentDistribution;
import org.peerfact.api.overlay.kbr.KBRNode;
import org.peerfact.impl.application.kbrapplication.KBRDocument;
import org.peerfact.impl.application.kbrapplication.KBRDummyApplication;
import org.peerfact.impl.application.kbrapplication.messages.AnnounceNewDocumentMessage;
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
 * This operation is used to announce new documents.
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class AnnounceNewDocumentOperation extends
		AbstractOperation<KBRDummyApplication, Object> {

	private final KBRDocument doc;

	private final OverlayContact<?> ownerContact;

	private final KBRNode<OverlayID<?>, OverlayContact<OverlayID<?>>, OverlayKey<?>> node;

	/**
	 * @param app
	 *            the application that created the operation
	 * @param doc
	 *            the document to announce
	 * @param callback
	 */
	public AnnounceNewDocumentOperation(KBRDummyApplication app,
			KBRDocument doc, OperationCallback<Object> callback) {
		super(app, callback);

		this.doc = doc;
		this.ownerContact = app.getNode().getLocalOverlayContact();
		this.node = app.getNode();
	}

	@Override
	protected void execute() {
		((ContentDistribution<OverlayKey<?>>) getComponent().getHost()
				.getOverlay(ContentDistribution.class))
				.storeDocument(doc);

		Message announceMsg = new AnnounceNewDocumentMessage(ownerContact, doc
				.getKey());
		node.route(doc.getKey(), announceMsg, null);

		operationFinished(true);
	}

	@Override
	public Object getResult() {
		// There is no meaningful result to return
		return null;
	}

}
