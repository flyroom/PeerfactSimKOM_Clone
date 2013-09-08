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
import org.peerfact.impl.overlay.dht.can.components.CanOverlayContact;
import org.peerfact.impl.overlay.dht.can.components.CanOverlayID;

/**
 * 
 * Reply for storeMsg. Tells that the hash value is saved.
 * 
 * @author Bjoern Dollak <peerfact@kom.tu-darmstadt.de>
 * @version February 2010
 * 
 */
public class StoreReplyMsg extends CanMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 898598007442267042L;

	private CanOverlayContact contact;

	private int operationID;

	/**
	 * Reply for storeMsg. Tells that the hash value is saved.
	 * 
	 * @param sender
	 * @param receiver
	 * @param saved
	 *            true if saved
	 * @param operationID
	 *            operation if of the storeOperation
	 */
	public StoreReplyMsg(CanOverlayID sender, CanOverlayID receiver,
			CanOverlayContact contact, int operationID) {
		super(sender, receiver);
		this.contact = contact;
		this.operationID = operationID;
	}

	@Override
	public long getSize() {
		return super.getSize() + contact.getTransmissionSize()
				+ Constants.INT_SIZE;
	}

	public CanOverlayContact getContact() {
		return contact;
	}

	public int getOperationID() {
		return operationID;
	}
}
