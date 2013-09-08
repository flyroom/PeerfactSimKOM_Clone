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

package org.peerfact.impl.overlay.dht.kademlia.kandy.operations;

import java.util.Set;

import org.peerfact.impl.overlay.dht.kademlia.base.components.AbstractKademliaNode;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayContact;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayKey;
import org.peerfact.impl.overlay.dht.kademlia.base.components.Node.VisibilityRestrictableNode;
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
 * A lookup coordinator that initially fills the set of the k closest known
 * nodes with a visibility restricted lookup to the local routing table.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public class VisibilityRestrictedLookupCoordinator
		extends StandardLookupCoordinator<HKademliaOverlayID> {

	/**
	 * The VisibilityRestrictableNode that has initiated this lookup.
	 */
	protected final VisibilityRestrictableNode<HKademliaOverlayID> myNode;

	/**
	 * Constructs a new lookup coordinator that carries out visibility
	 * restricted local lookups.
	 * 
	 * @param lookupKey
	 *            the KademliaOverlayKey that is to be looked up.
	 * @param node
	 *            the VisibilityRestrictableNode that initiates this lookup.
	 * @param conf
	 *            an OperationsConfig reference that permits to retrieve
	 *            configuration "constants".
	 */
	public VisibilityRestrictedLookupCoordinator(
			final KademliaOverlayKey lookupKey,
			final VisibilityRestrictableNode<HKademliaOverlayID> node,
			final OperationsConfig conf) {
		super(lookupKey, node, conf);
		myNode = node;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final Set<KademliaOverlayContact<HKademliaOverlayID>> localLookup(
			final KademliaOverlayKey key) {
		int localBucketSize = ((AbstractKademliaNode<HKademliaOverlayID>) getNode())
				.getLocalConfig().getBucketSize();

		return myNode.getKademliaRoutingTable()
				.visibilityRestrictedLocalLookup(key, localBucketSize);
	}
}
