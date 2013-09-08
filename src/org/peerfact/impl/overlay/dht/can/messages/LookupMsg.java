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

package org.peerfact.impl.overlay.dht.can.messages;

import org.peerfact.Constants;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.impl.overlay.dht.can.components.CanOverlayContact;
import org.peerfact.impl.overlay.dht.can.components.CanOverlayID;
import org.peerfact.impl.overlay.dht.can.components.CanOverlayKey;

/**
 * 
 * Sends a lookup request
 * 
 * @author Bjoern Dollak <peerfact@kom.tu-darmstadt.de>
 * @version February 2010
 * 
 */
public class LookupMsg extends CanMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3054325005287333796L;

	private CanOverlayKey id;

	private TransInfo senderTransInfo;

	private int operationID;

	private CanOverlayContact sender;

	/**
	 * Sends a lookup request
	 * 
	 * @param sender
	 * @param receiver
	 * @param contact
	 *            CanOverlayContact of requesting peer
	 * @param id
	 *            requested lookup hash
	 * @param operationID
	 *            operation ID of the request Operation
	 */
	public LookupMsg(CanOverlayID sender, CanOverlayID receiver,
			CanOverlayContact contact, CanOverlayKey id,
			int operationID) {
		super(sender, receiver);
		this.id = id;
		this.senderTransInfo = contact.getTransInfo();
		this.sender = contact;
		this.operationID = operationID;
		setHop(1);
	}

	@Override
	public long getSize() {
		return super.getSize() + id.getTransmissionSize() + Constants.INT_SIZE;
	}

	/* getters & setters */

	public CanOverlayKey getId() {
		return id;
	}

	public int getOperationID() {
		return operationID;
	}

	public CanOverlayContact getOriginalSender() {
		return sender;
	}
}
