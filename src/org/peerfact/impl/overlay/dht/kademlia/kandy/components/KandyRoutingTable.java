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

package org.peerfact.impl.overlay.dht.kademlia.kandy.components;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.peerfact.impl.overlay.dht.kademlia.base.components.AbstractKademliaNode;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayContact;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayKey;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaRoutingTable;
import org.peerfact.impl.overlay.dht.kademlia.base.components.RoutingTableConfig;
import org.peerfact.impl.overlay.dht.kademlia.base.components.RoutingTableEntry;
import org.peerfact.impl.overlay.dht.kademlia.base.components.RoutingTable.VisibilityRestrictableRoutingTable;
import org.peerfact.impl.overlay.dht.kademlia.base.components.RoutingTableComparators.RoutingTableEntryHierarchyComparator;
import org.peerfact.impl.overlay.dht.kademlia.base.routing.AddNodeVisitor;
import org.peerfact.impl.overlay.dht.kademlia.base.routing.MarkUnresponsiveNodeVisitor;
import org.peerfact.impl.overlay.dht.kademlia.base.routing.Node;
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
 * A routing table for Kandy, the Kademlia version of Canon. Kandy supports
 * somewhat simplified routing in a virtual hierarchy only by modifying the way
 * the routing table is filled: contacts with a higher cluster depth replace
 * contacts with a lower cluster depth (if the bucket is full), and a contact x
 * with cluster depth d is (externally) <i>visible</i> iff there is no contact c
 * != ownID with cluster depth d' &gt; d on the same level or a deeper level as
 * x in the routing tree.
 * <p>
 * The visibility rule affects only the
 * <code>visibilityRestrictedLocalLookup</code>. It does not affect the
 * proximity listener or regular lookup. That is, close contacts are always
 * announced to the listener even if they are not visible to (restricted)
 * lookups.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public class KandyRoutingTable extends
		KademliaRoutingTable<HKademliaOverlayID> implements
		VisibilityRestrictableRoutingTable<HKademliaOverlayID> {

	/**
	 * A Map with Nodes as keys and the permitted (exact) cluster depth for
	 * contacts that are visible from that LeafNode. Used to determine which
	 * contacts from the routing table are visible.
	 */
	private final Map<Node<HKademliaOverlayID>, Integer> clusterDepths;

	/**
	 * Comparator defining priorities on the bucket contents used for the
	 * replacement of less prioritised contacts when adding.
	 */
	private final Comparator<RoutingTableEntry<HKademliaOverlayID>> addReplacementStrategy;

	/**
	 * Constructs a new routing table that will be filled according to the rules
	 * of Kandy to support routing in a virtual hierarchy without modifying
	 * routing algorithms.
	 * 
	 * @param ownContact
	 *            the KademliaOverlayContact of the node that owns this routing
	 *            table. Will be inserted into the routing table.
	 * @param conf
	 *            a RoutingTableConfig reference that permits to retrieve
	 *            configuration "constants".
	 */
	public KandyRoutingTable(
			final KademliaOverlayContact<HKademliaOverlayID> ownContact,
			final RoutingTableConfig conf,
			AbstractKademliaNode<HKademliaOverlayID> owningOverlayNode) {
		super(ownContact, conf, owningOverlayNode);
		this.addReplacementStrategy = new RoutingTableEntryHierarchyComparator<HKademliaOverlayID>(
				pseudoRoot.getOwnID());
		/*
		 * Approximate number of nodes, including inner nodes, in a routing
		 * tree: one root plus 2^BTREE at each level = 1+height2^BTREE. The
		 * number of contacts in a complete (non-routing) tree would be
		 * #contacts = K (2^BTREE)^height. With #contacts given, it follows that
		 * height = log_{2^BTREE}(#contacts/K). Thus, one can expect
		 * approximately 1 + log_{2^BTREE}(#contacts/K)2^BTREE nodes.
		 */
		final double branches = Math.pow(2, config.getRoutingTreeOrder());
		final double totalBuckets = config.getNumberOfPeers()
				/ (double) config.getBucketSize();
		final double expectedNodes = 1 + branches * Math.log(totalBuckets)
				/ Math.log(branches);
		clusterDepths = new LinkedHashMap<Node<HKademliaOverlayID>, Integer>(
				(int) (1.3 * expectedNodes), 0.99f);
	}

	public KandyRoutingTable(
			final KademliaOverlayContact<HKademliaOverlayID> ownContact,
			final RoutingTableConfig conf) {
		this(ownContact, conf, null);
	}

	/**
	 * Adds the given contact to the routing table. Adding a contact may replace
	 * another contact that belongs to a cluster that is more distant to the
	 * routing table owner's cluster than that of the new node. A contact that
	 * has been added is not necessarily <i>visible</i> afterwards - it may have
	 * been dropped if the routing table is full, or it might not be visible due
	 * to Kandy's routing table construction rule.
	 * 
	 * @param contact
	 *            the HKademliaOverlayContact that is to be added.
	 */
	@Override
	public final void addContact(
			final KademliaOverlayContact<HKademliaOverlayID> contact) {
		if (clusterDepths != null) {
			clusterDepths.clear();
		}
		final AddNodeVisitor<HKademliaOverlayID> addVis = AddNodeVisitor
				.getAddNodeVisitor(
						contact, addReplacementStrategy, proxHandler, config);
		pseudoRoot.accept(addVis);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void visibilityRestrictedLocalLookup(
			final KademliaOverlayKey id, final int num,
			final Collection<KademliaOverlayContact<HKademliaOverlayID>> result) {
		result.clear();
		/*
		 * If the routing table has (potentially) been modified since the last
		 * run of the visibility visitor, it has been set to null and has to be
		 * re-run here.
		 */
		if (clusterDepths.size() == 0) {
			final KandyClusterDepthNodeVisitor visibilityVisitor = KandyClusterDepthNodeVisitor
					.getKandyClusterDepthNodeVisitor(clusterDepths);
			pseudoRoot.accept(visibilityVisitor);
		}
		final KandyLookupNodeVisitor lookupVis = KandyLookupNodeVisitor
				.getKandyLookupNodeVisitor(id, num, null, result, clusterDepths);
		pseudoRoot.accept(lookupVis);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Set<KademliaOverlayContact<HKademliaOverlayID>> visibilityRestrictedLocalLookup(
			final KademliaOverlayKey id, final int num) {
		visibilityRestrictedLocalLookup(id, num, sharedResultSet);
		return sharedResultSet;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void markUnresponsiveContact(final HKademliaOverlayID id) {
		clusterDepths.clear();
		final MarkUnresponsiveNodeVisitor<HKademliaOverlayID> unresVis = MarkUnresponsiveNodeVisitor
				.getMarkUnresponsiveNodeVisitor(id, addReplacementStrategy,
						proxHandler, config);
		pseudoRoot.accept(unresVis);
	}

}
