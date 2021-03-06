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

package org.peerfact.impl.overlay.dht.chord.base.messages;

import org.peerfact.Constants;
import org.peerfact.api.common.Message;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordContact;

/**
 * Base class for all request messages
 * 
 * @author Minh Hoang Nguyen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public abstract class AbstractRequestMessage extends ChordMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5924586075734254495L;

	private int hopCount = 0;

	public AbstractRequestMessage(AbstractChordContact senderContact,
			AbstractChordContact receiverContact) {
		super(senderContact, receiverContact);
	}

	@Override
	public Message getPayload() {
		return this;
	}

	public int getHopCount() {
		return this.hopCount;
	}

	public void incHop() {
		this.hopCount++;
	}

	public void setHop(int hop) {
		this.hopCount = hop;
	}

	@Override
	public long getSize() {
		return Constants.INT_SIZE + super.getSize();
	}

	@Override
	public String toString() {

		return this.getClass().getSimpleName() + " sender " + getSender()
				+ " receiver " + getReceiver();
	}
}
