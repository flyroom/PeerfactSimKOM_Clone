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

package org.peerfact.impl.overlay.dht.kademlia.hkademlia.messages;

import org.peerfact.impl.overlay.dht.kademlia.base.TypesConfig;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayKey;
import org.peerfact.impl.overlay.dht.kademlia.base.messages.KClosestNodesLookupMsg;
import org.peerfact.impl.overlay.dht.kademlia.base.operations.AbstractKademliaOperation.Reason;
import org.peerfact.impl.overlay.dht.kademlia.hkademlia.components.HKademliaOverlayID;

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
 * key and restricts the contacts that may be returned to have a given minimum
 * common cluster depth with the sender of this message.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public final class HKClosestNodesLookupMsg
		extends KClosestNodesLookupMsg<HKademliaOverlayID> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6329136850248490590L;

	/**
	 * The minimum depth of the deepest common cluster between each returned
	 * contact and the sender's ID.
	 */
	private final int minClusterDepth;

	/**
	 * Constructs a new message used to look up the k closest known neighbours
	 * of <code>nodeKey</code> that have at least the cluster depth
	 * <code>minClusterDepth</code> with respect to the sender's ID.
	 * 
	 * @param sender
	 *            the KademliaOverlayID of the sender of this message.
	 * @param destination
	 *            the KademliaOverlayID of the destination of this message.
	 * @param dataKey
	 *            the KademliaOverlayKey of the node of which the k closest
	 *            known neighbours are to be returned.
	 * @param minClusterDepth
	 *            the required minimum depth of the deepest common cluster
	 *            between each returned contact and the sender's ID.
	 * @param why
	 *            the reason why this message will be sent.
	 * @param conf
	 *            a TypesConfig reference that permits to retrieve configuration
	 *            "constants".
	 */
	public HKClosestNodesLookupMsg(final HKademliaOverlayID sender,
			final HKademliaOverlayID destination,
			final KademliaOverlayKey nodeKey, final int minClusterDepth,
			final Reason why, final TypesConfig conf) {
		super(sender, destination, nodeKey, why, conf);
		this.minClusterDepth = minClusterDepth;
	}

	/**
	 * @return the minimum required depth of the deepest common cluster between
	 *         each returned contact and the ID of the sender of this message.
	 */
	public final int getMinClusterDepth() {
		return minClusterDepth;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final long getOtherFieldSize() {
		return super.getOtherFieldSize() + 4;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String toString() {
		return "[HKClosestNodesLookupMsg|from:" + getSender() + "; to:"
				+ getDestination() + "; reason:" + getReason() + "; lookup:"
				+ getNodeKey() + "; minClusterDepth=" + minClusterDepth + "]";
	}

}
