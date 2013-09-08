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
 * This message is routed to the contact responsible for the given key in order
 * to ask it to answer with a response whether this key exists, and if so, along
 * with the result.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class LookupResourceMessage extends FilesharingMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7123754326040746729L;

	long queryUID;

	OverlayKey<?> keyLookedUp;

	OverlayContact<?> initiator;

	/**
	 * Default constructor
	 * 
	 * @param keyLookedUp
	 *            : the key that shall be looked up.
	 * @param initiator
	 *            : the initiator of this query, i.e. the sender of this
	 *            message.
	 */
	public LookupResourceMessage(OverlayKey<?> keyLookedUp,
			OverlayContact<?> initiator) {
		super();
		this.queryUID = generateQueryUID();
		this.keyLookedUp = keyLookedUp;
		this.initiator = initiator;
	}

	@Override
	public Message getPayload() {
		return this;
	}

	@Override
	public long getSize() {
		return 6;
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
	 * Returns the initiator of the query, i.e. the contact that sent this
	 * message.
	 * 
	 * @return
	 */
	public OverlayContact<?> getInitiator() {
		return initiator;
	}

	@Override
	public String toString() {
		return "(Lookup : " + keyLookedUp + " init: "
				+ initiator.getTransInfo().getNetId() + ")";
	}

}
