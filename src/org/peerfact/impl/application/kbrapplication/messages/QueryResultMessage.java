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
 * This message is used to answer to a query for a document.
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class QueryResultMessage implements Message {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5629823094716405461L;

	private final OverlayContact<?> senderContact;

	private final OverlayKey<?> key;

	private final OverlayContact<?> documentProvider;

	/**
	 * @param senderContact
	 *            the contact of the node that initiated the answer to the query
	 * @param key
	 *            the key that was queried for
	 * @param documentProvider
	 *            the contact of the node that provides the document queried for
	 */
	public QueryResultMessage(OverlayContact<?> senderContact,
			OverlayKey<?> key,
			OverlayContact<?> documentProvider) {
		this.senderContact = senderContact;
		this.key = key;
		this.documentProvider = documentProvider;
	}

	/**
	 * @return the contact of the node that initiated the answer to the query
	 */
	public OverlayContact<?> getSenderContact() {
		return senderContact;
	}

	/**
	 * @return the key that was queried for
	 */
	public OverlayKey<?> getKey() {
		return key;
	}

	/**
	 * @return the contact of the node that provides the file queried for
	 */
	public OverlayContact<?> getDocumentProvider() {
		return documentProvider;
	}

	@Override
	public Message getPayload() {
		// There is no meaningful payload to return
		return null;
	}

	@Override
	public long getSize() {
		return MessageConfig.Sizes.QueryResultMessage;
	}

}
