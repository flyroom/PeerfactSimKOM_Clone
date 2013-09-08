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
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayContact;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayID;
import org.peerfact.impl.overlay.dht.kademlia.base.components.ProximityHandler;
import org.peerfact.impl.overlay.dht.kademlia.base.components.RoutingTableComparators;
import org.peerfact.impl.overlay.dht.kademlia.base.components.RoutingTableConfig;
import org.peerfact.impl.overlay.dht.kademlia.base.components.RoutingTableEntry;
import org.peerfact.impl.util.toolkits.BigIntegerHelpers;


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
 * Operation that adds a contact to the routing tree according to the standard
 * Kademlia rules for splitting and the replacement cache.
 * 
 * Optionally, contacts from a more prioritised cluster may replace contacts
 * from less prioritised clusters (for example, from clusters that are farther
 * away from the routing table owner's cluster than the new contact's cluster).
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public final class AddNodeVisitor<T extends KademliaOverlayID> extends
		AbstractNodeVisitor<T> {

	/**
	 * Single, final AddNodeVisitor instance (non-reentrant).
	 */
	private static final AddNodeVisitor singleton = new AddNodeVisitor();

	/**
	 * The ID of the contact to be added.
	 */
	private T newID;

	/**
	 * The contact to be added.
	 */
	private KademliaOverlayContact<T> newContact;

	/**
	 * A comparator that defines priorities on contacts (used for priority
	 * replacement).
	 */
	private Comparator<? super RoutingTableEntry<T>> replacementStrategy;

	/**
	 * The proximity handler has to be notified whenever a contact is added to
	 * or deleted from a kBucket (not replacement cache).
	 */
	private ProximityHandler<T> proximityHandler;

	/**
	 * Configuration values ("constants").
	 */
	private RoutingTableConfig config;

	/**
	 * Attempts to add the given contact to the routing table. If the
	 * responsible bucket is full, it is attempted to split this bucket (if
	 * permitted).
	 * <p>
	 * If the responsible bucket is full, cannot be split, and the new contact
	 * has a higher priority than at least one of the contacts in the bucket,
	 * the new contact can replace the least prioritised contact in the bucket.
	 * The old contact in turn is moved to the replacement cache. If the
	 * replacement cache is already full, it replaces the least prioritised
	 * contact from the cache. (Note that following this construction principle,
	 * the cache contains no contacts that have a higher priority than any of
	 * the contacts in the bucket. However, for this assumption to hold, it is
	 * necessary that the same way of computing priorities is used to replace an
	 * unresponsive node from the bucket with the highest prioritised contact
	 * from the cache.)
	 * <p>
	 * The replacement strategy, that is the way priorities are computed, is
	 * passed to this visitor using a Comparator. If that Comparator is null,
	 * priority replacement is disabled.
	 * <p>
	 * Finally, if the bucket is full, cannot be split, and priority replacement
	 * is not available, the new contact is added to the replacement cache if
	 * the cache is not full. Else, the contact is dropped.
	 * <p>
	 * The contact will be saved under its <code>KademliaOverlayID</code> which
	 * is derived from <code>contactInfo</code>. If two distinct contacts have
	 * the same <code>KademliaOverlayID</code>, a newly inserted contact will
	 * replace an existing one.
	 * 
	 * @param newEntry
	 *            the <code>KademliaOverlayContact</code> contact to be
	 *            inserted.
	 * @param replacementStrategy
	 *            a Comparator that defines priorities on
	 *            RoutingTableEntries/KademliaOverlayContacts (or a subtype,
	 *            depending on the type parameter of this class). Elements that
	 *            are <i>smaller</i> with respect to this comparator have
	 *            <i>lower</i> priority. If <code>replacementStrategy</code> is
	 *            <code>null</code>, priority-based contact replacement is
	 *            disabled. This replacement strategy should be the same as used
	 *            in the {@link MarkUnresponsiveNodeVisitor}.
	 * @param proxHandler
	 *            a ProximityHandler that will be notified if a contact is added
	 *            to or removed from a LeafNode's kBucket (not replacement
	 *            cache). If <code>proxHandler</code> is <code>null</code>, it
	 *            will be ignored.
	 * @param conf
	 *            a RoutingTableConfig reference that permits to retrieve
	 *            configuration "constants".
	 * @return an AddNodeVisitor instance. Note that this instance is statically
	 *         shared among all clients of this class. That is, at runtime only
	 *         one AddNodeVisitor instance exists. Thus, it is non-reentrant and
	 *         should not be saved by clients (should used immediately).
	 */
	public static final <T extends KademliaOverlayID> AddNodeVisitor<T> getAddNodeVisitor(
			final KademliaOverlayContact<T> contact,
			final Comparator<? super RoutingTableEntry<T>> replacementStrategy,
			final ProximityHandler<T> proxHandler, final RoutingTableConfig conf) {
		// construct new entry with reset stale counter
		singleton.newContact = contact;
		singleton.newID = contact.getOverlayID();
		singleton.replacementStrategy = replacementStrategy;
		singleton.proximityHandler = proxHandler;
		singleton.config = conf;
		return singleton;
	}

	private AddNodeVisitor() {
		// should not be called externally
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void visit(final BranchNode<T> node) {
		node.getResponsibleChild(newID.getBigInt()).accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void visit(final LeafNode<T> node) {
		final Node<T> newMe;
		final RoutingTableEntry<T> existingEntryB, existingEntryC, staleEntry, bucketMin, newEntry;
		int localBucketSizeOfNode = node.getOwningOverlayNode()
				.getLocalConfig().getBucketSize();

		if ((existingEntryB = node.kBucket.get(newID)) != null) {
			// update last seen time, reset stale counter
			existingEntryB.seen();
		} else if (node.kBucket.size() < localBucketSizeOfNode) {
			// still places left
			node.kBucket.put(newID, new RoutingTableEntry<T>(newContact));
			if (proximityHandler != null) {
				proximityHandler.contactAdded(newContact);
			}
		} else if ((existingEntryC = node.getReplacementCache().get(newID)) != null) {
			// performance: if contact in cache, no reason to check if it can
			// be added to the bucket.
			existingEntryC.seen();
		} else if ((staleEntry = getStaleEntry(node)) != null) {
			// replace stale entry (cache empty, else MUNVisitor had done it)
			node.kBucket.remove(staleEntry.getContact().getOverlayID());
			node.kBucket.put(newID, new RoutingTableEntry<T>(newContact));
			if (proximityHandler != null) {
				proximityHandler.contactRemoved(staleEntry.getContact()
						.getOverlayID());
				proximityHandler.contactAdded(newContact);
			}
		} else if (isSplittable(node)) {
			newMe = split(node);
			newMe.accept(this); // insert node into split bucket
		} else {
			newEntry = new RoutingTableEntry<T>(newContact);
			if ((bucketMin = getPriorityReplacementCandidate(node, newEntry)) != null) {
				doPriorityReplacement(node, bucketMin, newEntry);
			} else if (node.getReplacementCache().size() < config
					.getReplacementCacheSize()) {
				node.getReplacementCache().put(newID, newEntry);
			} else {
				// drop contact
			}
		}
	}

	/**
	 * Determines whether the given node can be split into two nodes. Currently,
	 * this is possible if that bucket is responsible for the owner node's ID,
	 * and if the maximum height of the routing tree has not yet been reached.
	 * 
	 * @param node
	 *            the node to be tested for splitability.
	 * @return true if it is allowed to split the bucket.
	 */
	public final boolean isSplittable(final LeafNode<T> node) {
		return (node.getLevel() < (config.getIDLength() / config
				.getRoutingTreeOrder()) && node.isResponsibleFor(node
				.getOwnID()));
	}

	/**
	 * Creates and returns a new branch node by splitting and reusing the state
	 * of the template node <code>splitNode</code> in
	 * {@link RoutingTableConfig#getRoutingTreeOrder()} new LeafNodes.
	 * <p>
	 * The newly created nodes will be readily wired, that is the new BranchNode
	 * registers as a child at <code>splitNode</code>'s parent (which means it
	 * will replace the reference to <code>splitNode</code> and reuse its
	 * prefix), and the LeafNodes are registered as children in the new
	 * BranchNode.
	 * <p>
	 * <code>splitNode</code>'s bucket contents is distributed over the
	 * children's buckets. All child nodes will inherit <code>splitNode</code>'s
	 * <code>lastLookup</code> time, and the contacts' properties such as the
	 * stale counter are preserved as well.
	 * <p>
	 * (As the replacement cache of <code>splitNode</code> is always empty with
	 * the current splitting strategy, its contents is not distributed over the
	 * new LeafNodes.)
	 * 
	 * @param splitNode
	 *            the LeafNode that is to be split and replaced by a new branch
	 *            node.
	 * @return the new BranchNode, registered as a child of the parent of
	 *         <code>splitNode</code>. Has as children the newly created
	 *         nodes/buckets over which <code>splitNode</code>'s contents has
	 *         been distributed.
	 */
	protected final AbstractNode<T> split(final LeafNode<T> splitNode) {
		final BranchNode<T> branch;
		branch = new BranchNode<T>(splitNode.getPrefix(),
				splitNode.getParent(), config);
		for (int i = 0; i < Math.pow(2, config.getRoutingTreeOrder()); i++) {
			buildLeafNode(splitNode, i, branch);
		}
		return branch;
	}

	/**
	 * Builds a new bucket (LeafNode) by copying the state from
	 * <code>template</code>. All entries from <code>template</code>'s bucket
	 * that can be stored in the new leaf node will be copied, that is all
	 * entries that have the new LeafNode's prefix.
	 * <p>
	 * The new prefix is computed by appending at the right of
	 * <code>template</code>'s prefix the binary representation of
	 * <code>suffix</code> (length is
	 * {@link RoutingTableConfig#getRoutingTreeOrder()}).
	 * <p>
	 * The bucket is filled with eligible entries from the old bucket. (As the
	 * cache is always empty with the current splitting strategy, there is no
	 * code to distribute its contents currently available.)
	 * <p>
	 * The newly created leaf node will furthermore register itself as a child
	 * with <code>parent</code>.
	 * 
	 * @param template
	 *            the template LeafNode from which the state will be copied.
	 * @param suffix
	 *            the suffix that is to be appended to the parent's prefix to
	 *            form the new node's prefix. The suffix is interpreted in its
	 *            binary representation of length
	 *            {@link RoutingTableConfig#getRoutingTreeOrder()}.
	 * @param parent
	 *            the parent node of the new leaf.
	 */
	protected final void buildLeafNode(final LeafNode<T> template,
			final int suffix, final ParentNode<T> parent) {
		/*
		 * Build the child's prefix: the parent's prefix plus the suffix in
		 * binary representation shifted in from the right (binary length of
		 * suffix is config.getRoutingTableOrder()).
		 */
		final BigInteger prefix = BigIntegerHelpers.shiftLeft(
				template.getPrefix(), BigInteger.valueOf(suffix),
				config.getRoutingTreeOrder());
		final LeafNode<T> leaf = new LeafNode<T>(prefix, parent, config,
				template.getOwningOverlayNode());

		leaf.setLastLookup(template.getLastLookup());

		// copy all valid entries from old bucket into new bucket
		// these are guaranteed to be <= K entries
		for (final Map.Entry<T, RoutingTableEntry<T>> candidate : template.kBucket
				.entrySet()) {
			if (leaf.isResponsibleFor(candidate.getKey())) {
				leaf.kBucket.put(candidate.getKey(), candidate.getValue());
			}
		}

		// assumption: cache always empty with current splitting strategy
	}

	/**
	 * Calculates whether the given LeafNode's bucket contains an entry that is
	 * stale, that is, that has at least
	 * {@link RoutingTableConfig#getStaleCounter()} marks. If so, the entry with
	 * most marks is returned. Else <code>null</code>.
	 * 
	 * @param node
	 *            the LeafNode containing the bucket that is to be checked for
	 *            stale contacts.
	 * @return the entry with the most marks, or null if no entry exists/ has at
	 *         least STALE_COUNTER marks.
	 */
	protected final RoutingTableEntry<T> getStaleEntry(final LeafNode<T> node) {
		final Comparator<RoutingTableEntry<?>> staleComp = RoutingTableComparators
				.getStaleComparator();
		final RoutingTableEntry<T> mostMarks = Collections.min(node.kBucket
				.values(), staleComp);
		if (mostMarks == null
				|| mostMarks.getStaleCounter() < config.getStaleCounter()) {
			return null;
		}
		return mostMarks;
	}

	/**
	 * Calculates whether the given LeafNode's bucket contains an entry that has
	 * strictly less priority than the <code>newContact</code> of this Visitor
	 * and, if available, returns the least prioritised of those contacts from
	 * the bucket (priorities are calculated according to the
	 * <code>replacementStrategy</code> Comparator). If no such contact exists,
	 * or if no replacementStrategy is given, this method returns null.
	 * 
	 * @param node
	 *            the LeafNode containing the bucket that is to be checked for
	 *            less prioritised contacts.
	 * @param newEntry
	 *            the new Entry to be added and that is to be checked if it can
	 *            replace another entry.
	 * @return the smallest of the less prioritised entries, or null if no such
	 *         entry exists or no replacement strategy is given.
	 */
	protected final RoutingTableEntry<T> getPriorityReplacementCandidate(
			final LeafNode<T> node, final RoutingTableEntry<T> newEntry) {
		final RoutingTableEntry<T> bucketMin;

		if (replacementStrategy != null
				&& replacementStrategy.compare(
						newEntry,
						(bucketMin = Collections.min(node.kBucket.values(),
								replacementStrategy))) > 0) {
			return bucketMin;
		}
		return null;
	}

	/**
	 * Does a priority replacement of the given <code>bucketMin</code> with this
	 * Visitor's <code>newEntry</code> , that is this method inserts this
	 * Visitor's <code>newEntry</code> into <code>node</code>'s bucket, moving
	 * <code>bucketMin</code> from the bucket to the replacement cache. If the
	 * replacement cache is full, its least prioritised entry is evicted and
	 * replaced by <code>bucketMin</code>.
	 * 
	 * @param node
	 *            the LeafNode on which these operations take place.
	 * @param bucketMin
	 *            the least prioritised entry from <code>node</code>'s bucket
	 *            that is to be replaced by this Visitor's
	 *            <code>newContact</code> and moved to the replacement cache.
	 * @param newEntry
	 *            the new Entry to be added to the bucket as a replacement
	 *            (should contain this.newContact).
	 */
	protected final void doPriorityReplacement(final LeafNode<T> node,
			final RoutingTableEntry<T> bucketMin,
			final RoutingTableEntry<T> newEntry) {
		// assumes replacementStrategy != null
		final RoutingTableEntry<T> cacheMin;

		node.kBucket.remove(bucketMin.getContact().getOverlayID());
		node.kBucket.put(newID, newEntry);
		if (proximityHandler != null) {
			proximityHandler.contactRemoved(bucketMin.getContact()
					.getOverlayID());
			proximityHandler.contactAdded(newContact);
		}
		if (node.getReplacementCache().size() >= config
				.getReplacementCacheSize()) {
			cacheMin = Collections.min(node.getReplacementCache().values(),
					replacementStrategy);
			node.getReplacementCache().remove(
					cacheMin.getContact().getOverlayID());
		}
		node.getReplacementCache().put(bucketMin.getContact().getOverlayID(),
				bucketMin);
	}

}
