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

package org.peerfact.impl.overlay.dht.pastry.messages;

import org.peerfact.Constants;
import org.peerfact.api.overlay.dht.DHTObject;
import org.peerfact.impl.overlay.dht.pastry.components.PastryContact;
import org.peerfact.impl.overlay.dht.pastry.components.PastryID;

/**
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 02/05/2011
 * 
 */
public class StoreMsg extends PastryBaseMsg {

	/**
	 * 
	 */
	private static final long serialVersionUID = -728195493856634765L;

	private PastryContact senderContact;

	private PastryID key;

	private DHTObject value;

	private int operationID;

	private int hops;

	public StoreMsg(PastryContact sender, PastryContact receiver, PastryID key,
			DHTObject value, int operationID) {
		super(sender.getOverlayID(), receiver.getOverlayID());
		this.senderContact = sender;
		this.key = key;
		this.value = value;
		this.operationID = operationID;
		this.hops = 0;
	}

	@Override
	public long getSize() {
		return super.getSize() + key.getTransmissionSize()
				+ senderContact.getTransmissionSize() + 2 * Constants.INT_SIZE
				+ value.getTransmissionSize();
	}

	public PastryID getKey() {
		return key;
	}

	public DHTObject getValue() {
		return value;
	}

	public int getOperationID() {
		return operationID;
	}

	public PastryContact getSenderContact() {
		return senderContact;
	}

	public int getHops() {
		return hops;
	}

	public void incrementHops() {
		hops++;
	}

}
