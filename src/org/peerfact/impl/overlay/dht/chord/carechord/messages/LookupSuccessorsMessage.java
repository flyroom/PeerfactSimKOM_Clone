/*
 * Copyright (c) 2005-2010 KOM – Multimedia Communications Lab
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

package org.peerfact.impl.overlay.dht.chord.carechord.messages;

import org.peerfact.Constants;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordContact;
import org.peerfact.impl.overlay.dht.chord.base.components.ChordID;
import org.peerfact.impl.overlay.dht.chord.base.messages.AbstractRequestMessage;

public class LookupSuccessorsMessage extends AbstractRequestMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2342617051526655070L;

	// the time to live of this message
	// decrease by 1 each hop, 0 means the message is no longer forwarded
	private int timeToLive;

	// the time to live this message was started with
	private int initialTTL;

	// the ChordID of the (possibly virtual) node which sends this message
	// (senderContact contains the ID of the corresponding real node, necessary
	// for identification of the virtual node)
	private ChordID virtualNodeIdentifier;

	/**
	 * 
	 * @param senderContact
	 *            sender
	 * @param virtualNodeIdentifier
	 *            identifier of the virtual node which is sending this message
	 * @param receiverContact
	 *            receiver
	 * @param timeToLive
	 *            find up i-th this number of successors
	 */
	public LookupSuccessorsMessage(AbstractChordContact senderContact,
			ChordID virtualNodeIdentifier,
			AbstractChordContact receiverContact, int timeToLive, int initialTTL) {
		super(senderContact, receiverContact);

		this.initialTTL = initialTTL;
		this.timeToLive = timeToLive;
		this.virtualNodeIdentifier = virtualNodeIdentifier;
	}

	public int getInitialTTL() {
		return initialTTL;
	}

	public int getTimeToLive() {
		return timeToLive;
	}

	public void setTimeToLive(int timeToLive) {
		this.timeToLive = timeToLive;
	}

	public ChordID getVirtualNodeIdentifier() {
		return virtualNodeIdentifier;
	}

	@Override
	public long getSize() {
		return super.getSize() + virtualNodeIdentifier.getTransmissionSize()
				+ Constants.INT_SIZE + Constants.INT_SIZE;
	}

	@Override
	public String toString() {
		return super.toString() + " TTL " + timeToLive;
	}
}
