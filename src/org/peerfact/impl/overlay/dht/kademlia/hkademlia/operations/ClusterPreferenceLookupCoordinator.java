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

package org.peerfact.impl.overlay.dht.kademlia.hkademlia.operations;

import java.util.Comparator;

import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayContact;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayKey;
import org.peerfact.impl.overlay.dht.kademlia.base.components.Node;
import org.peerfact.impl.overlay.dht.kademlia.base.components.RoutingTableComparators.KademliaOverlayContactHierarchyComparator;
import org.peerfact.impl.overlay.dht.kademlia.base.operations.OperationsConfig;
import org.peerfact.impl.overlay.dht.kademlia.base.operations.StandardLookupCoordinator;
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
 * A lookup coordinator that works similar to a standard lookup coordinator, but
 * queries contacts from a deeper common cluster depth preferably.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public class ClusterPreferenceLookupCoordinator
		extends StandardLookupCoordinator<HKademliaOverlayID> {

	/**
	 * Constructs a new lookup coordinator that queries contacts with a high
	 * common cluster depth with the initiator <code>node</code> preferably.
	 * 
	 * @param lookupKey
	 *            the HKademliaOverlayKey that is to be looked up.
	 * @param node
	 *            the Node that initiates this lookup.
	 * @param conf
	 *            an OperationsConfig reference that permits to retrieve
	 *            configuration "constants".
	 */
	public ClusterPreferenceLookupCoordinator(
			final KademliaOverlayKey lookupKey,
			final Node<HKademliaOverlayID> node,
			final OperationsConfig conf) {
		super(lookupKey, node, conf);
	}

	/**
	 * Returns the "best" contact from <code>kClosestNodes</code> that has not
	 * yet been queried (is in state TO_QUERY). Nodes that have a high common
	 * cluster depth with the initiator of this lookup are preferred. Distance
	 * is used to select a contact among those with the same cluster depth. That
	 * is, the first search criteria is cluster depth, distance comes second.
	 * 
	 * @return the deepest, closest known contact with state TO_QUERY, or null
	 *         if no such contact exists (then either all contacts are in
	 *         another state, or we do not know any contact at all).
	 */
	@Override
	public final KademliaOverlayContact<HKademliaOverlayID> getBestUnqueried() {
		final Comparator<KademliaOverlayContact<HKademliaOverlayID>> clusterDepth;
		clusterDepth = new KademliaOverlayContactHierarchyComparator<HKademliaOverlayID>(
				node
						.getTypedOverlayID());
		return getkClosestNodes().getMaxKey(clusterDepth, toQuery);
	}

}
