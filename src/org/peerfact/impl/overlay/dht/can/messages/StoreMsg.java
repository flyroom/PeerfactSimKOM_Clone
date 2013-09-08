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
import org.peerfact.api.overlay.dht.DHTObject;
import org.peerfact.impl.overlay.dht.can.components.CanOverlayContact;
import org.peerfact.impl.overlay.dht.can.components.CanOverlayID;
import org.peerfact.impl.overlay.dht.can.components.CanOverlayKey;

/**
 * 
 * Tries to find the right peer to send a hash value and the connected contact.
 * 
 * @author Bjoern Dollak <peerfact@kom.tu-darmstadt.de>
 * @version February 2010
 * 
 */
public class StoreMsg extends CanMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7663536639600079325L;

	private CanOverlayContact sender;

	private CanOverlayKey id;

	private DHTObject object;

	private int operationID;

	/**
	 * Tries to find the right peer to send a hash value and the connected
	 * contact.
	 * 
	 * @param sender
	 * @param receiver
	 * @param contact
	 *            CanOverlayContact of the peer which want to save the hash
	 * @param id
	 *            hash value
	 * @param operationID
	 *            operation id of the storeOperation
	 */
	public StoreMsg(CanOverlayID sender, CanOverlayID receiver,
			CanOverlayContact contact, CanOverlayKey id, DHTObject object,
			int operationID) {
		super(sender, receiver);
		this.sender = contact;
		this.id = id;
		this.object = object;
		this.operationID = operationID;

		setHop(1);
	}

	@Override
	public long getSize() {
		return super.getSize() + sender.getTransmissionSize()
				+ id.getTransmissionSize() + object.getTransmissionSize()
				+ Constants.INT_SIZE;
	}

	public CanOverlayContact getContact() {
		return this.sender;
	}

	public CanOverlayKey getId() {
		return id;
	}

	public DHTObject getObject() {
		return this.object;
	}

	public int getOperationID() {
		return this.operationID;
	}
}
