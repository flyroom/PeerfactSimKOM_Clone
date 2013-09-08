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

package org.peerfact.impl.network.modular;

import org.peerfact.api.common.Message;
import org.peerfact.api.network.NetID;
import org.peerfact.api.network.NetProtocol;
import org.peerfact.impl.network.AbstractNetMessage;

/**
 * Implementation of a NetMessage for the Modular Network layer
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class ModularNetMessage extends AbstractNetMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7540317292096395324L;

	private long size;

	private int noOfFragments;

	ModularNetMessage(Message payload, NetID receiver, NetID sender,
			IStrategies strategies, NetProtocol netProtocol) {
		super(payload, receiver, sender, netProtocol);
		this.noOfFragments = strategies.getFragmentingStrategy()
				.getNoOfFragments(payload, receiver, sender, netProtocol);
		this.size = strategies.getPacketSizingStrategy().getPacketSize(payload,
				receiver, sender, netProtocol, noOfFragments);
		assert noOfFragments >= 1 : "The number of fragments per message may never be less than 1. ";
	}

	/**
	 * Returns the sum of all fragments of this net message.
	 */
	@Override
	public long getSize() {
		return size;
	}

	/**
	 * Returns the number of fragments the message is split into.
	 * 
	 * @return
	 */
	public int getNoOfFragments() {
		return noOfFragments;
	}

	@Override
	public String toString() {
		return "[ IP " + super.getSender() + " -> " + super.getReceiver()
				+ " | size: " + getSize() + " ( " + getSize()
				+ " ) bytes | payload: " + getPayload() + " ]";
	}

}
