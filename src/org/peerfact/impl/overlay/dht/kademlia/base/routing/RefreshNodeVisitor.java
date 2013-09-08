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

import java.util.Map;

import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayID;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayKey;
import org.peerfact.impl.overlay.dht.kademlia.base.components.RoutingTableConfig;


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
 * Routing tree visitor that collects a list of IDs from buckets that need a
 * refresh.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public final class RefreshNodeVisitor<T extends KademliaOverlayID> extends
		AbstractNodeVisitor<T> {

	/**
	 * Single, final RefreshNodeVisitor instance (non-reentrant).
	 */
	private static final RefreshNodeVisitor singleton = new RefreshNodeVisitor();

	/**
	 * A point in time, stating that all buckets that had their last lookup
	 * before or at that point in time need to be refreshed.
	 */
	private long notLookedUpAfter;

	/**
	 * A Map in which KademliaOverlayKeys from buckets that are to be refreshed
	 * are accumulated as keys and the corresponding bucket depth as values.
	 */
	private Map<KademliaOverlayKey, Integer> refreshBuckets;

	/**
	 * Configuration values ("constants").
	 */
	private RoutingTableConfig config;

	/**
	 * Fills a Map with IDs from buckets that need to be refreshed because the
	 * last lookup over the net took place before or at the point in time
	 * <code>notLookedUpSince</code> or has never taken place at all (for
	 * instance, after routing table creation). (After the lookup process, the
	 * last lookup time for the bucket from which an ID originated has to be
	 * updated separately.)
	 * 
	 * @param notLookedUpAfter
	 *            a point in time, given in simulation time units. If a bucket's
	 *            last lookup time is earlier than or equal to
	 *            <code>notLookedUpAfter</code>, or a lookup in that bucket has
	 *            never taken place at all, it has to be refreshed. For
	 *            instance, if
	 *            <code>notLookedUpAfter=getCurrentSimulationTime()</code>, then
	 *            all buckets have to be refreshed.
	 * @param refreshBuckets
	 *            a Map into which KademliaOverlayKeys from buckets that have to
	 *            be refreshed will be inserted as keys and the corresponding
	 *            bucket depth (or routing tree node level) as values (0 being
	 *            the root bucket).
	 * @param conf
	 *            a RoutingTableConfig reference that permits to retrieve
	 *            configuration "constants".
	 * @return a RefreshNodeVisitor instance. Note that this instance is
	 *         statically shared among all clients of this class. That is, at
	 *         runtime only one RefreshNodeVisitor instance exists. Thus, it is
	 *         non-reentrant and should not be saved by clients (should used
	 *         immediately).
	 */
	public static final <T extends KademliaOverlayID> RefreshNodeVisitor<T> getRefreshNodeVisitor(
			final long notLookedUpAfter,
			final Map<KademliaOverlayKey, Integer> refreshIDs,
			final RoutingTableConfig conf) {
		singleton.notLookedUpAfter = notLookedUpAfter;
		singleton.config = conf;
		singleton.refreshBuckets = refreshIDs;
		return singleton;
	}

	private RefreshNodeVisitor() {
		// should not be called externally.
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void visit(final BranchNode<T> node) {
		for (final Node<T> child : node.children.values()) {
			child.accept(this);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	/*
	 * TODO: create random keys
	 */
	@Override
	public final void visit(final LeafNode<T> node) {
		if (node.getLastLookup() < 0 // "< 0" means "never set"
				|| node.getLastLookup() <= notLookedUpAfter) {
			refreshBuckets.put(new KademliaOverlayKey(node.getPrefix()
					.shiftLeft(
							config.getIDLength() - node.getLevel()
									* config.getRoutingTreeOrder()), config),
					node.getLevel());
		}
	}

}
