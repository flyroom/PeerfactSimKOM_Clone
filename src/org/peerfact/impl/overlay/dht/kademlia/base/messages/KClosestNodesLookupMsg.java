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

package org.peerfact.impl.overlay.dht.kademlia.base.messages;

import org.peerfact.impl.overlay.dht.kademlia.base.TypesConfig;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayID;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayKey;
import org.peerfact.impl.overlay.dht.kademlia.base.operations.AbstractKademliaOperation.Reason;

/**
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * This part of the Simulator is not maintained in the current version of
 * PeerfactSim.KOM. There is no intention of the authors to fix this
 * circumstances, since the changes needed are huge compared to overall benefit.
 * 
 * If you want it to work correctly, you are free to make the specific changes
 * and provide it to the community.
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! !!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * 
 * Format for a message that requests the k closest known nodes around a given
 * key.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public class KClosestNodesLookupMsg<T extends KademliaOverlayID> extends
		KademliaMsg<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -316726795979312378L;

	/**
	 * The KademliaOverlayKey of the node of which the k closest known
	 * neighbours are to be looked up.
	 */
	private final KademliaOverlayKey key;

	/**
	 * Constructs a new message used to look up the k closest known neighbours
	 * of <code>nodeKey</code>.
	 * 
	 * @param sender
	 *            the KademliaOverlayID of the sender of this message.
	 * @param destination
	 *            the KademliaOverlayID of the destination of this message.
	 * @param dataKey
	 *            the KademliaOverlayKey of the node of which the k closest
	 *            known neighbours are to be returned.
	 * @param why
	 *            the reason why this message will be sent.
	 * @param conf
	 *            a TypesConfig reference that permits to retrieve configuration
	 *            "constants".
	 */
	public KClosestNodesLookupMsg(final T sender, final T destination,
			final KademliaOverlayKey nodeKey, final Reason why,
			final TypesConfig conf) {
		super(sender, destination, why, conf);
		this.key = nodeKey;
	}

	/**
	 * @return the KademliaOverlayKey of the node of which the k closest known
	 *         neighbours are to be looked up.
	 */
	public final KademliaOverlayKey getNodeKey() {
		return this.key;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected long getOtherFieldSize() {
		return (config.getIDLength() / 8);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "[KClosestNodesLookupMsg|from:" + getSender() + "; to:"
				+ getDestination() + "; reason:" + getReason() + "; lookup:"
				+ key + "]";
	}

}
