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

import java.util.Collection;

import org.peerfact.Constants;
import org.peerfact.api.common.Message;
import org.peerfact.impl.overlay.dht.pastry.components.PastryContact;
import org.peerfact.impl.overlay.dht.pastry.components.PastryID;

/**
 * This class represents a message that is send as a reply to JoinMsgs. It
 * contains a flag to tell whether this reply was sent by the peer numerically
 * closest to the new peer's ID or not, as well as a list of peer contacts to be
 * inserted in the joining peer's state tables.
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class StateUpdateMsg extends PastryBaseMsg {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1589225033291177906L;

	private Collection<PastryContact> contacts;

	private long leafSetTimestamp = -1;

	/**
	 * @param sender
	 *            the sender of the message
	 * @param receiver
	 *            the receiver of the message
	 * @param contacts
	 *            the list of peer contacts to be inserted into the receiving
	 *            peer's state tables
	 */
	public StateUpdateMsg(PastryID sender, PastryID receiver,
			Collection<PastryContact> contacts) {
		super(sender, receiver);
		this.contacts = contacts;
	}

	public StateUpdateMsg(PastryID sender, PastryID receiver,
			Collection<PastryContact> contacts, long leafSetTimestamp) {
		super(sender, receiver);
		this.contacts = contacts;
		this.leafSetTimestamp = leafSetTimestamp;
	}

	/**
	 * @return the list of contacts to be used by the joining peer to insert
	 *         into its state tables
	 */
	public Collection<PastryContact> getContacts() {
		return contacts;
	}

	@Override
	public Message getPayload() {
		// There is no payload message
		return null;
	}

	/**
	 * @return get the timestamp of the message creation
	 */
	public long getLeafSetTimestamp() {
		return leafSetTimestamp;
	}

	@Override
	public long getSize() {
		long size = super.getSize();
		if (contacts.size() > 0) {
			size += contacts.iterator().next().getTransmissionSize()
					* contacts.size();
		}

		return size + Constants.LONG_SIZE;
	}
}
