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

package org.peerfact.impl.overlay.dht.kademlia.hkademlia.components;

import java.util.Collection;
import java.util.Comparator;
import java.util.Set;

import org.peerfact.impl.overlay.dht.kademlia.base.components.AbstractKademliaNode;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayContact;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayKey;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaRoutingTable;
import org.peerfact.impl.overlay.dht.kademlia.base.components.RoutingTableConfig;
import org.peerfact.impl.overlay.dht.kademlia.base.components.RoutingTableEntry;
import org.peerfact.impl.overlay.dht.kademlia.base.components.RoutingTable.HierarchyRestrictableRoutingTable;
import org.peerfact.impl.overlay.dht.kademlia.base.components.RoutingTableComparators.RoutingTableEntryHierarchyComparator;
import org.peerfact.impl.overlay.dht.kademlia.base.components.RoutingTablePredicates.MinimumClusterDepthRestrictor;
import org.peerfact.impl.overlay.dht.kademlia.base.routing.AddNodeVisitor;
import org.peerfact.impl.overlay.dht.kademlia.base.routing.GenericLookupNodeVisitor;
import org.peerfact.impl.overlay.dht.kademlia.base.routing.MarkUnresponsiveNodeVisitor;


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
 * Hierarchy-aware routing table for Kademlia. Permits to store, lookup, and
 * mark contacts as unresponsive. Several strategies for optimisation and
 * hierarchy support can be configured.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public class HKademliaRoutingTable extends
		KademliaRoutingTable<HKademliaOverlayID> implements
		HierarchyRestrictableRoutingTable<HKademliaOverlayID> {

	/**
	 * Comparator defining priorities on the bucket contents used for the
	 * replacement of less prioritised contacts when adding.
	 */
	private final Comparator<RoutingTableEntry<HKademliaOverlayID>> addReplacementStrategy;

	/**
	 * Constructs a new hierarchy-aware routing table with the given contact
	 * information about the owning node (it will be inserted into the routing
	 * table).
	 * 
	 * @param ownContact
	 *            the KademliaOverlayContact of the node that owns this routing
	 *            table.
	 * @param conf
	 *            a RoutingTableConfig reference that permits to retrieve
	 *            configuration "constants".
	 */
	public HKademliaRoutingTable(
			final KademliaOverlayContact<HKademliaOverlayID> ownContact,
			final RoutingTableConfig conf,
			AbstractKademliaNode<HKademliaOverlayID> owningOverlayNode) {
		super(ownContact, conf, owningOverlayNode);
		this.addReplacementStrategy = new RoutingTableEntryHierarchyComparator<HKademliaOverlayID>(
				pseudoRoot.getOwnID());
	}

	public HKademliaRoutingTable(
			final KademliaOverlayContact<HKademliaOverlayID> ownContact,
			final RoutingTableConfig conf) {
		this(ownContact, conf, null);
	}

	/**
	 * Adds the given contact to the routing table. Adding a contact may replace
	 * another contact that belongs to a cluster that is more distant to the
	 * routing table owner's cluster than that of the new node.
	 * 
	 * @param contact
	 *            the HKademliaOverlayContact that is to be added.
	 */
	@Override
	public final void addContact(
			final KademliaOverlayContact<HKademliaOverlayID> contact) {
		final AddNodeVisitor<HKademliaOverlayID> addVis = AddNodeVisitor
				.getAddNodeVisitor(
						contact, addReplacementStrategy, proxHandler, config);
		pseudoRoot.accept(addVis);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void localLookup(final KademliaOverlayKey id, final int num,
			final int minDepth, final HKademliaOverlayID clusterRefID,
			final Collection<KademliaOverlayContact<HKademliaOverlayID>> result) {
		result.clear();
		final GenericLookupNodeVisitor<HKademliaOverlayID> lookupVis = GenericLookupNodeVisitor
				.getGenericLookupNodeVisitor(id, num,
						new MinimumClusterDepthRestrictor<HKademliaOverlayID>(
								clusterRefID,
								minDepth), result);
		pseudoRoot.accept(lookupVis);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Set<KademliaOverlayContact<HKademliaOverlayID>> localLookup(
			final KademliaOverlayKey id, final int num, final int minDepth,
			final HKademliaOverlayID clusterRefID) {
		localLookup(id, num, minDepth, clusterRefID, sharedResultSet);
		return sharedResultSet;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void markUnresponsiveContact(final HKademliaOverlayID id) {
		/*
		 * Use the same replacement strategy as when adding contacts to select a
		 * contact from the replacement cache
		 */
		final MarkUnresponsiveNodeVisitor<HKademliaOverlayID> unresVis = MarkUnresponsiveNodeVisitor
				.getMarkUnresponsiveNodeVisitor(id, addReplacementStrategy,
						proxHandler, config);
		pseudoRoot.accept(unresVis);
	}

}
