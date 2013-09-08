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
 * Lookup reply, tells the contact data which has the data. Just one result is
 * sent. If more than one contact has the data, it would be another way to send
 * the list of contacts.
 * 
 * @author Bjoern Dollak <peerfact@kom.tu-darmstadt.de>
 * @version February 2010
 * 
 */
public class LookupReplyMsg extends CanMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8086772668946656840L;

	private CanOverlayContact result;

	private int operationID;

	/**
	 * Lookup reply, tells the contact data which has the data. Just one result
	 * is sent. If more than one contact has the data, it would be another way
	 * to send the list of contacts.
	 * 
	 * @param sender
	 * @param receiver
	 * @param result
	 *            CanOverlayContact contains the result
	 * @param operationID
	 *            OperatinoID of the store or lookup operation
	 */
	public LookupReplyMsg(CanOverlayID sender, CanOverlayID receiver,
			CanOverlayContact result, int operationID) {
		super(sender, receiver);
		this.result = result;
		this.operationID = operationID;
	}

	public CanOverlayContact getResult() {
		return result;
	}

	@Override
	public long getSize() {
		return super.getSize()
				+ (result != null ? result.getTransmissionSize() : 0)
				+ Constants.INT_SIZE;
	}

	public int getOperationID() {
		return operationID;
	}
}
