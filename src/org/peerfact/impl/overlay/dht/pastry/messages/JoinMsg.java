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

import org.peerfact.impl.overlay.dht.pastry.components.PastryContact;
import org.peerfact.impl.overlay.dht.pastry.components.PastryKey;

/**
 * This is the message send to join an overlay.
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class JoinMsg extends PastryBaseMsg {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7372693081066186219L;

	private PastryKey key;

	private PastryContact senderContact;

	/**
	 * @param sender
	 *            the initial sender of the message
	 */
	public JoinMsg(PastryContact sender) {
		super(null, null);
		this.senderContact = sender;
		this.key = sender.getOverlayID().getCorrespondingKey();
	}

	@Override
	public long getSize() {
		return super.getSize() + key.getTransmissionSize()
				+ senderContact.getTransmissionSize();
	}

	/**
	 * @return the key this message is routed towards
	 */
	public PastryKey getKey() {
		return key;
	}

	/**
	 * @return the contact of the initial sender of this message
	 */
	public PastryContact getSenderContact() {
		return senderContact;
	}

}
