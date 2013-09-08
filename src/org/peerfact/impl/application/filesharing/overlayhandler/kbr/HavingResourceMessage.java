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
import org.peerfact.api.overlay.OverlayKey;

/**
 * Returned to the query initiator, if the node that was requested for a
 * specified key holds the resource assigned to the key.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class HavingResourceMessage implements Message {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7312779625954225273L;

	long queryUID;

	OverlayKey<?> keyLookedUp;

	private OverlayContact<?> requestor;

	private OverlayContact<?> holder;

	/**
	 * Default constructor
	 * 
	 * @param queryUID
	 *            : the UID that uniquely identifies the query being made.
	 * @param keyLookedUp
	 *            : the key that is looked up with this message
	 * @param requestor
	 *            : the requester of the query, i.e. the receiver of this
	 *            message
	 * @param holder
	 *            : the holder of the resource, i.e. the sender of this message
	 */
	public HavingResourceMessage(long queryUID, OverlayKey<?> keyLookedUp,
			OverlayContact<?> requestor, OverlayContact<?> holder) {
		super();
		this.queryUID = queryUID;
		this.keyLookedUp = keyLookedUp;
		this.requestor = requestor;
		this.holder = holder;
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
	 * Returns the UID that uniquely identifies the query being made.
	 * 
	 * @return
	 */
	public long getQueryUID() {
		return queryUID;
	}

	/**
	 * Returns the key that is looked up with this message
	 * 
	 * @return
	 */
	public OverlayKey<?> getKeyLookedUp() {
		return keyLookedUp;
	}

	/**
	 * Returns the requester of the query, i.e. the receiver of this message
	 * 
	 * @return
	 */
	public OverlayContact<?> getRequestor() {
		return requestor;
	}

	/**
	 * Returns the holder of the resource, i.e. the sender of this message
	 * 
	 * @return
	 */
	public OverlayContact<?> getHolder() {
		return holder;
	}

	@Override
	public String toString() {
		return "(Having: " + keyLookedUp + ")";
	}

}
