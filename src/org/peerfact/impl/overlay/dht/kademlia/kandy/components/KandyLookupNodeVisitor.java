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
import java.util.Map;

import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayContact;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayKey;
import org.peerfact.impl.overlay.dht.kademlia.base.components.RoutingTableEntry;
import org.peerfact.impl.overlay.dht.kademlia.base.components.RoutingTableComparators.RoutingTableEntryXORMaxComparator;
import org.peerfact.impl.overlay.dht.kademlia.base.components.RoutingTablePredicates.MinimumClusterDepthRestrictor;
import org.peerfact.impl.overlay.dht.kademlia.base.routing.GenericLookupNodeVisitor;
import org.peerfact.impl.overlay.dht.kademlia.base.routing.LeafNode;
import org.peerfact.impl.overlay.dht.kademlia.base.routing.Node;
import org.peerfact.impl.overlay.dht.kademlia.hkademlia.components.HKademliaOverlayID;
import org.peerfact.impl.util.toolkits.Predicate;
import org.peerfact.impl.util.toolkits.Predicates;
import org.peerfact.impl.util.toolkits.Predicates.AndPredicate;


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
 * A lookup visitor similar to {@link GenericLookupNodeVisitor} except that
 * contacts that are forbidden by Kandy's routing table consistency rule are
 * hidden (not returned to the user). Specifically, a contact x with cluster
 * depth d is visible iff there is no contact c != ownID with cluster depth d'
 * &gt; d on the same level or a deeper level as x in the routing tree.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
final class KandyLookupNodeVisitor extends
		GenericLookupNodeVisitor<HKademliaOverlayID> {

	/**
	 * Single, final KandyLookupNodeVisitor instance (non-reentrant).
	 */
	private static final KandyLookupNodeVisitor singleton = new KandyLookupNodeVisitor();

	/**
	 * A Map with Nodes as keys and the permitted (exact) cluster depth for
	 * contacts that are visible from that LeafNode. Used to determine which
	 * contacts from the routing table are visible.
	 */
	private Map<Node<HKademliaOverlayID>, Integer> clusterDepths;

	/**
	 * Find the <code>numberOfResults</code> contacts from the tree that are
	 * closest to <code>goal</code>. Additionally, only contacts that are
	 * visible according to Kandy's routing table construction rule are
	 * returned.
	 * 
	 * @param goal
	 *            the KademliaOverlayKey that the routing tree is to be searched
	 *            for. This key is only used to select buckets (LeafNodes) from
	 *            the routing tree. The search inside buckets picks those
	 *            contacts that maximise <code>sortation</code>.
	 * @param numberOfResults
	 *            the overall number of contacts that are to be returned in
	 *            <code>result</code>.
	 * @param filter
	 *            a predicate on KademliaOverlayContacts (contained in
	 *            RoutingTableEntries) that determines which contacts from a
	 *            bucket may be considered in this lookup. If
	 *            <code>filter</code> is <code>null</code>, any contact may be
	 *            considered. In addition to the filter (or "no filter") given
	 *            here, the lookup automatically restricts contacts by their
	 *            visibility according to the Kandy routing table construction
	 *            rule.
	 * @param result
	 *            a Collection into which results will be inserted. Naturally,
	 *            this set may not be null.
	 * @param clusterDepths
	 *            a Map that contains the exact cluster depths permitted for
	 *            each bucket of the routing table. It is used to determine
	 *            which contacts are visible according to Kandy's routing table
	 *            construction rule.
	 * 
	 * @return an KandyLookupNodeVisitor instance. Note that this instance is
	 *         statically shared among all clients of this class. That is, at
	 *         runtime only one KandyLookupNodeVisitor instance exists. Thus, it
	 *         is non-reentrant and should not be saved by clients (should used
	 *         immediately).
	 */
	// protected KandyLookupNodeVisitor(final KademliaOverlayKey goal,
	// final int numberOfResults,
	// final Predicate<? super RoutingTableEntry<H>> filter,
	// final Collection<KademliaOverlayContact<H>> result,
	// final KandyVisibilityNodeVisitor<H> visibilityVisitor) {
	protected static final KandyLookupNodeVisitor getKandyLookupNodeVisitor(
			final KademliaOverlayKey goal,
			final int numberOfResults,
			final Predicate<RoutingTableEntry<HKademliaOverlayID>> filter,
			final Collection<KademliaOverlayContact<HKademliaOverlayID>> result,
			final Map<Node<HKademliaOverlayID>, Integer> clusterDepths) {

		singleton.clusterDepths = clusterDepths;

		// copied from GenericLookupNodeVisitor
		singleton.wantedKey = goal;
		singleton.numOfResults = numberOfResults;
		singleton.sortComp = new RoutingTableEntryXORMaxComparator<HKademliaOverlayID>(
				goal
						.getBigInt());
		singleton.result = result;
		singleton.filter = filter;

		return singleton;
	}

	private KandyLookupNodeVisitor() {
		// should not be called externally
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void visit(final LeafNode<HKademliaOverlayID> node) {
		if (result.size() >= numOfResults) {
			return;
		}
		final Predicate<RoutingTableEntry<HKademliaOverlayID>> visibilityFilter, appliedFilter;
		// determine which contacts are externally visible
		visibilityFilter = getVisibilityFilter(node);

		if (filter == null) {
			appliedFilter = visibilityFilter;
		} else {
			appliedFilter = new AndPredicate<RoutingTableEntry<HKademliaOverlayID>>(
					visibilityFilter, filter);
		}
		filterSortCopyAndUnwrap(node, appliedFilter);
	}

	/**
	 * Creates a filter that can be applied on the contacts from LeafNode
	 * <code>bucket</code> in order to determine which ones of them are visible
	 * according to Kandy's visibility rule. This method returns sensible
	 * results only if this.clusterDepths contains up-to-date information about
	 * the routing table.
	 * 
	 * @param bucket
	 *            the LeafNode from which the visible contacts are to be found.
	 * @return a Predicate that is true only for those entries from
	 *         <code>bucket</code> that are visible according to Kandy's
	 *         visibility rule. Note that this Predicate may only be applied to
	 *         the contacts contained in <code>bucket</code>. If this Predicate
	 *         is tested on a contact that is not contained in
	 *         <code>bucket</code>, its behaviour is undefined.
	 */
	public final Predicate<RoutingTableEntry<HKademliaOverlayID>> getVisibilityFilter(
			final LeafNode<HKademliaOverlayID> bucket) {
		if (clusterDepths.containsKey(bucket)) {
			/*
			 * Request only a minimum depth as the own ID has not been taken
			 * into account when determining the depth of visible contacts (the
			 * own ID is always visible, has the maximum possible depth, and
			 * does no harm when returned -> has already been queried, thus does
			 * not violate Kandy's requirements.)
			 */
			return new MinimumClusterDepthRestrictor<HKademliaOverlayID>(
					bucket.getOwnID(),
					clusterDepths.get(bucket));
		}
		// bucket unknown: return filter that blocks everything
		return Predicates.getFilterEverything();
	}
}
