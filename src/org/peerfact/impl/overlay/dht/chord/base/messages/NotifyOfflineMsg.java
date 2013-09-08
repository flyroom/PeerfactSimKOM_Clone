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

import org.peerfact.api.common.Message;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordContact;

/**
 * This message is used to notify of Peer offline event
 * 
 * @author Minh Hoang Nguyen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class NotifyOfflineMsg extends ChordMessage implements IStabilizeMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1025168635471989226L;

	private AbstractChordContact offlinePeer;

	public NotifyOfflineMsg(AbstractChordContact senderContact,
			AbstractChordContact receiverContact,
			AbstractChordContact offlinePeer) {
		super(senderContact, receiverContact);
		this.offlinePeer = offlinePeer;
	}

	@Override
	public Message getPayload() {
		return this;
	}

	@Override
	public long getSize() {
		return offlinePeer.getTransmissionSize() + super.getSize();
	}

	public AbstractChordContact getOfflineInfo() {
		return offlinePeer;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " offlinePeer " + offlinePeer;
	}
}
