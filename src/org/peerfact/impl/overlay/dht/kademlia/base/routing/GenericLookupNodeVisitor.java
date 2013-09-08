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

package org.peerfact.impl.overlay.dht.kademlia.base.routing;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayContact;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayID;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayKey;
import org.peerfact.impl.overlay.dht.kademlia.base.components.RoutingTableComparators;
import org.peerfact.impl.overlay.dht.kademlia.base.components.RoutingTableEntry;
import org.peerfact.impl.overlay.dht.kademlia.base.components.RoutingTableComparators.RoutingTableEntryXORMaxComparator;
import org.peerfact.impl.util.toolkits.CollectionHelpers;
import org.peerfact.impl.util.toolkits.Predicate;


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
 * A generic contact lookup operation that allows to specify a metric according
 * to which contacts are chosen from the routing tree.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public class GenericLookupNodeVisitor<T extends KademliaOverlayID> extends
		AbstractNodeVisitor<T> {

	/**
	 * Single, final GenericLookupNodeVisitor instance (non-reentrant).
	 */
	private static final GenericLookupNodeVisitor singleton = new GenericLookupNodeVisitor();

	/**
	 * The KademliaOverlayID that the routing tree is to be searched for. Used
	 * to navigate through the tree to find the bucket that is responsible for
	 * that key. ({@link #sortComp} is used to select contacts inside that
	 * bucket.) If the lookup is not complete after having considered one
	 * bucket, its siblings are traversed in the order of their XOR-distance to
	 * <code>wantedKey</code>.
	 */
	protected KademliaOverlayKey wantedKey;

	/**
	 * The number of expected results.
	 */
	protected int numOfResults;

	/**
	 * A comparator used as a distance metric to select the nearest contacts
	 * <i>inside</i> a bucket. May be preset to choose contacts that have
	 * minimal distance to <code>wantedKey</code>. (The bucket is chosen by
	 * traversing the routing tree on the path corresponding to
	 * {@link #wantedKey}.)
	 */
	protected Comparator<? super RoutingTableEntry<T>> sortComp;

	/**
	 * A filter that permits to hide contacts from a bucket so that they will
	 * not be returned by this search.
	 */
	// protected final Predicate<? super RoutingTableEntry<T>> filter;
	protected Predicate<RoutingTableEntry<T>> filter;

	/**
	 * A Collection that will gradually be filled with the search results.
	 */
	protected Collection<KademliaOverlayContact<T>> result;

	/**
	 * Find the <code>numberOfResults</code> contacts from the tree that are
	 * closest to <code>goal</code>.
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
	 *            considered.
	 * @param result
	 *            a Collection into which results will be inserted. Naturally,
	 *            this Collection may not be null.
	 * 
	 * @return a GenericLookupNodeVisitor instance. Note that this instance is
	 *         statically shared among all clients of this class. That is, at
	 *         runtime only one GenericLookupNodeVisitor instance exists. Thus,
	 *         it is non-reentrant and should not be saved by clients (should
	 *         used immediately).
	 */
	// protected GenericLookupNodeVisitor(final KademliaOverlayKey goal,
	// final int numberOfResults,
	// final Predicate<? super RoutingTableEntry<T>> filter,
	// final Collection<KademliaOverlayContact<T>> result) {
	public static final <T extends KademliaOverlayID> GenericLookupNodeVisitor<T> getGenericLookupNodeVisitor(
			final KademliaOverlayKey goal, final int numberOfResults,
			final Predicate<RoutingTableEntry<T>> filter,
			final Collection<KademliaOverlayContact<T>> result) {
		// if modified, need to modify KandyLookupNodeVisitor as well!
		singleton.wantedKey = goal;
		singleton.numOfResults = numberOfResults;
		singleton.sortComp = new RoutingTableEntryXORMaxComparator<T>(goal
				.getBigInt());
		singleton.result = result;
		singleton.filter = filter;
		return singleton;
	}

	public GenericLookupNodeVisitor() {
		// should not be called externally
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void visit(final BranchNode<T> node) {
		final BigInteger perfectIndex = node.getDiscriminantIDBits(wantedKey
				.getBigInt());

		/*
		 * for performance reasons, first check whether it is enough to lookup
		 * in the "perfect" branch
		 */
		node.children.get(perfectIndex).accept(this);
		if (result.size() >= numOfResults) {
			return;
		}

		/*
		 * else try remaining branches in the order imposed by the XOR metric
		 */
		final List<BigInteger> remainingIndices = new ArrayList<BigInteger>(
				node.children.keySet());
		remainingIndices.remove(perfectIndex); // already tried this one
		// sort by ascending (increasing) distance
		Collections.sort(remainingIndices,
				new RoutingTableComparators.BigIntegerXORMaxComparator(
						perfectIndex));
		for (final BigInteger index : remainingIndices) {
			if (result.size() >= numOfResults) {
				return;
			}
			node.children.get(index).accept(this);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(final LeafNode<T> node) {
		if (result.size() >= numOfResults) {
			return;
		}

		filterSortCopyAndUnwrap(node, filter);
	}

	/**
	 * Filters the entries contained in <code>node</code>'s bucket according to
	 * <code>appliedFilter</code>, sorts these entries using this class'
	 * <code>sortComp</code>, extracts the KademliaOverlayContacts contained in
	 * the RoutingTableEntries and copies them in this order into this class'
	 * <code>result</code> Collection until its size has reached
	 * <code>numOfResults</code>.
	 * 
	 * @param node
	 *            the LeafNode with the bucket from which the entries will be
	 *            used.
	 * @param appliedFilter
	 *            the filter that has to be applied to the bucket entries. If
	 *            set to <code>null</code>, all contacts are considered (no
	 *            filtering). (Note: this class' <code>filter</code> is not used
	 *            in this method and has to be passed explicitly if desired.)
	 */
	// protected final void filterSortCopyAndUnwrap(final LeafNode<T> node,
	// final Predicate<? super RoutingTableEntry<T>> appliedFilter) {
	protected final void filterSortCopyAndUnwrap(final LeafNode<T> node,
			final Predicate<RoutingTableEntry<T>> appliedFilter) {
		final SortedSet<RoutingTableEntry<T>> sortedAndFiltered = new TreeSet<RoutingTableEntry<T>>(
				sortComp);
		if (appliedFilter == null) {
			sortedAndFiltered.addAll(node.kBucket.values());
		} else {
			CollectionHelpers.filter(node.kBucket.values(), sortedAndFiltered,
					appliedFilter);
		}
		Iterator<RoutingTableEntry<T>> it = sortedAndFiltered.iterator();
		while (it.hasNext() && result.size() < numOfResults) {
			result.add(it.next().getContact());
		}
	}
}
