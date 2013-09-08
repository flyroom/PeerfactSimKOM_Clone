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

package org.peerfact.impl.overlay.dht.centralizedindex.messages;

import org.peerfact.Constants;
import org.peerfact.api.common.Message;
import org.peerfact.impl.overlay.AbstractOverlayMessage;
import org.peerfact.impl.overlay.dht.centralizedindex.components.CIOverlayID;

/**
 * Implementing a centralized DHT overlay, whose organization of the centralized
 * index is similar to the distributed index of Chord
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 08.12.2008
 */
public abstract class AbstractCIRequestMsg<S extends Object> extends
		AbstractOverlayMessage<CIOverlayID> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9111665688840823453L;

	private int opID;

	private long size;

	private S content;

	public AbstractCIRequestMsg(CIOverlayID sender,
			CIOverlayID receiver, S content, int opID) {
		super(sender, receiver);
		this.content = content;
		this.opID = opID;
		this.size = sender.getTransmissionSize()
				+ receiver.getTransmissionSize() + Constants.INT_SIZE;
	}

	public int getOpID() {
		return opID;
	}

	public S getContent() {
		return this.content;
	}

	@Override
	public Message getPayload() {
		return null;
	}

	@Override
	public long getSize() {
		return size;
	}

}
