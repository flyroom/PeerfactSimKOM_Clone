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

package org.peerfact.impl.application.filesharing.overlayhandler.kbr;

import org.peerfact.api.common.Message;
import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.api.overlay.OverlayID;
import org.peerfact.api.overlay.OverlayKey;

/**
 * Sent to the node that requested a publish in order to confirm it as
 * successful.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class PublishSucceededMessage extends FilesharingMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8855024929677933055L;

	long queryUID;

	OverlayKey<?> keyPublished;

	private OverlayContact<OverlayID<?>> requestor;

	private OverlayContact<OverlayID<?>> publisher;

	/**
	 * Default Constructor
	 * 
	 * @param queryUID
	 *            : the UID that uniquely identifies the publish being made.
	 * @param keyPublished
	 *            : the key of the resource that was stored in the overlay
	 * @param requestor
	 *            : the requestor, i.e. the node that receives this message.
	 * @param publisher
	 *            : the responsible node for the publish, i.e. the node that
	 *            sends this message.
	 */
	public PublishSucceededMessage(long queryUID, OverlayKey<?> keyPublished,
			OverlayContact<OverlayID<?>> requestor,
			OverlayContact<OverlayID<?>> publisher) {
		super();
		this.queryUID = queryUID;
		this.keyPublished = keyPublished;
		this.requestor = requestor;
		this.publisher = publisher;
	}

	@Override
	public Message getPayload() {
		return this;
	}

	@Override
	public long getSize() {
		return 12;
	}

	/**
	 * Returns the UID that uniquely identifies the publish being made.
	 * 
	 * @return
	 */
	public long getQueryUID() {
		return queryUID;
	}

	/**
	 * Returns the key of the resource that was stored in the overlay
	 * 
	 * @return
	 */
	public OverlayKey<?> getKeyPublished() {
		return keyPublished;
	}

	/**
	 * Returns the requestor, i.e. the node that receives this message.
	 * 
	 * @return
	 */
	public OverlayContact<OverlayID<?>> getRequestor() {
		return requestor;
	}

	/**
	 * Returns the responsible node for the publish, i.e. the node that sends
	 * this message.
	 * 
	 * @return
	 */
	public OverlayContact<OverlayID<?>> getPublishResponsibleNode() {
		return publisher;
	}

	@Override
	public String toString() {
		return "(Published: " + keyPublished + ")";
	}

}
