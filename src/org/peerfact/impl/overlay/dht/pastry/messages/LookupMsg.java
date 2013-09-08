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
import org.peerfact.impl.overlay.dht.pastry.components.PastryContact;
import org.peerfact.impl.overlay.dht.pastry.components.PastryID;

/**
 * This message is used to find the responsible node for a given target id
 * 
 * @author Fabio Zöllner <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class LookupMsg extends PastryBaseMsg {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6978296728615500722L;

	/**
	 * The contact of the sender
	 */
	private PastryContact senderContact;

	/**
	 * The ID of the target
	 */
	private PastryID target;

	/**
	 * The operation id of the lookup operation
	 */
	private int lookupId;

	/**
	 * The number of hops this message has done
	 */
	private int hops;

	/**
	 * Creates a new lookup message to find the the responsible node for the
	 * given target id.
	 * 
	 * @param sender
	 *            The sender of this message
	 * @param receiver
	 *            The receiver of this message
	 * @param target
	 *            The target id that should be looked up
	 * @param lookupId
	 *            The operation id of the lookup operation
	 * @param hops
	 *            The number of hops this message has already done
	 */
	public LookupMsg(PastryContact sender, PastryContact receiver,
			PastryID target, int lookupId, int hops) {
		super(sender.getOverlayID(), receiver.getOverlayID());
		this.senderContact = sender;
		this.target = target;
		this.lookupId = lookupId;
		this.hops = hops;
	}

	/**
	 * returns the transmission size of this message
	 * 
	 * @return transmission size
	 */
	@Override
	public long getSize() {
		return super.getSize() + target.getTransmissionSize()
				+ senderContact.getTransmissionSize() + 2 * Constants.INT_SIZE;
	}

	/**
	 * @return The contact of the initial sender of this message
	 */
	public PastryContact getSenderContact() {
		return senderContact;
	}

	/**
	 * @return The target id for which the node is looked up
	 */
	public PastryID getTarget() {
		return target;
	}

	/**
	 * @return The operation id of the lookup operation
	 */
	public int getLookupId() {
		return lookupId;
	}

	/**
	 * @return The number of hops the message has done
	 */
	public int getHops() {
		return hops;
	}

	/**
	 * Increments the hop counter by 1
	 */
	public void incrementHopCount() {
		hops++;
	}
}
