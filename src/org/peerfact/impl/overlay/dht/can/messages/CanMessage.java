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

import org.peerfact.api.common.Message;
import org.peerfact.impl.overlay.AbstractOverlayMessage;
import org.peerfact.impl.overlay.dht.can.components.CanOverlayID;

/**
 * 
 * Abstract Method for Messages.
 * 
 * @author Bjoern Dollak <peerfact@kom.tu-darmstadt.de>
 * @version February 2010
 * 
 */
public abstract class CanMessage extends AbstractOverlayMessage<CanOverlayID> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6904728222703446709L;

	private int hopCount = 0;

	/**
	 * creates a new AbstractOverlayMessage
	 * 
	 * @param sender
	 * @param receiver
	 */
	public CanMessage(CanOverlayID sender, CanOverlayID receiver) {
		super(sender, receiver);
	}

	@Override
	public Message getPayload() {
		return this;
	}

	@Override
	public long getSize() {
		return getSender().getTransmissionSize()
				+ getReceiver().getTransmissionSize();
	}

	public int getHopCount() {
		return this.hopCount;
	}

	public void setHop(int hop) {
		this.hopCount = hop;
	}

}
