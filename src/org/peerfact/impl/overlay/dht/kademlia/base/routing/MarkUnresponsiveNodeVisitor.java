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

import java.util.Collections;
import java.util.Comparator;

import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayID;
import org.peerfact.impl.overlay.dht.kademlia.base.components.ProximityHandler;
import org.peerfact.impl.overlay.dht.kademlia.base.components.RoutingTableComparators;
import org.peerfact.impl.overlay.dht.kademlia.base.components.RoutingTableConfig;
import org.peerfact.impl.overlay.dht.kademlia.base.components.RoutingTableEntry;


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
 * Visitor to mark unresponsive contacts.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public final class MarkUnresponsiveNodeVisitor<T extends KademliaOverlayID>
		extends
		AbstractNodeVisitor<T> {

	/**
	 * Single, final GenericLookupNodeVisitor instance (non-reentrant).
	 */
	private static final MarkUnresponsiveNodeVisitor singleton = new MarkUnresponsiveNodeVisitor();

	/**
	 * The unresponsive contact's ID that is to be marked.
	 */
	private T unresponsive;

	/**
	 * A comparator that selects the node from the cache that is to replace an
	 * evicted one (the maximum with respect to the comparator will be
	 * selected).
	 */
	private Comparator<? super RoutingTableEntry<T>> replacementComparator;

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
	 * Marks a contact as unresponsive and removes it from the bucket or
	 * replacement cache if the threshold of STALE_COUNTER=
	 * {@link RoutingTableConfig#getStaleCounter()} marks has been reached.
	 * <p>
	 * A contact from the bucket with at least <code>STALE_COUNTER</code> marks
	 * will be replaced with a contact from the replacement cache (the maximum
	 * with respect to <code>replacementComparator</code> will be chosen).
	 * However, if the replacement cache is empty, the contact will not be
	 * removed from the bucket.
	 * <p>
	 * A contact from the replacement cache is immediately dropped if the
	 * replacement cache is full. Else, it is removed after having received
	 * <code>STALE_COUNTER</code> marks.
	 * 
	 * @param unresponsive
	 *            the <code>KademliaOverlayID</code> of the node that is to be
	 *            marked as unresponsive.
	 * @param replacementComparator
	 *            a comparator that selects the node from the cache that is to
	 *            replace an evicted node from the bucket (the <i>maximum</i>
	 *            with respect to the comparator will be selected). If this
	 *            argument is <code>null</code>, the most recently seen contact
	 *            from the cache will be chosen.
	 * @param proxHandler
	 *            a ProximityHandler that will be notified if a contact is added
	 *            to or removed from a LeafNode's kBucket (not replacement
	 *            cache). If <code>proxHandler</code> is <code>null</code>, it
	 *            will be ignored.
	 * @param conf
	 *            a RoutingTableConfig reference that permits to retrieve
	 *            configuration "constants".
	 * @return a MarkUnresponsiveNodeVisitor instance. Note that this instance
	 *         is statically shared among all clients of this class. That is, at
	 *         runtime only one MarkUnresponsiveNodeVisitor instance exists.
	 *         Thus, it is non-reentrant and should not be saved by clients
	 *         (should used immediately).
	 */
	public static final <T extends KademliaOverlayID> MarkUnresponsiveNodeVisitor<T> getMarkUnresponsiveNodeVisitor(
			final T unresponsive,
			final Comparator<? super RoutingTableEntry<T>> replacementComparator,
			final ProximityHandler<T> proxHandler, final RoutingTableConfig conf) {
		singleton.config = conf;
		singleton.unresponsive = unresponsive;
		if (replacementComparator == null) {
			singleton.replacementComparator = RoutingTableComparators
					.getLastSeenComp();
		} else {
			singleton.replacementComparator = replacementComparator;
		}
		singleton.proximityHandler = proxHandler;
		return singleton;
	}

	private MarkUnresponsiveNodeVisitor() {
		// should not be called externally
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void visit(final BranchNode<T> node) {
		node.getResponsibleChild(unresponsive.getBigInt()).accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void visit(final LeafNode<T> node) {
		RoutingTableEntry<T> contact, replacement;

		if ((contact = node.kBucket.get(unresponsive)) != null
				&& contact.increaseStaleCounter() >= config.getStaleCounter()
				&& node.getReplacementCache().size() > 0) {
			node.kBucket.remove(unresponsive);
			replacement = Collections.max(node.getReplacementCache().values(),
					this.replacementComparator);
			node.kBucket.put(replacement.getContact().getOverlayID(),
					replacement);
			node.getReplacementCache().remove(replacement.getContact()
					.getOverlayID());
			if (proximityHandler != null) {
				proximityHandler.contactRemoved(unresponsive);
				proximityHandler.contactAdded(replacement.getContact());
			}
		} else if ((contact = node.getReplacementCache().get(unresponsive)) != null
				&& (contact.increaseStaleCounter() >= config.getStaleCounter() || node
						.getReplacementCache()
						.size() == config.getReplacementCacheSize())) {
			node.getReplacementCache().remove(unresponsive);
		}
	}

}
