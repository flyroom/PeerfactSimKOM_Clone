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

import java.util.Map;

import org.peerfact.impl.overlay.dht.kademlia.base.routing.AbstractNodeVisitor;
import org.peerfact.impl.overlay.dht.kademlia.base.routing.BranchNode;
import org.peerfact.impl.overlay.dht.kademlia.base.routing.LeafNode;
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
 * Visitor that determines for each LeafNode which contacts are visible
 * according to Kandy's visibility rule (a contact x with cluster depth d is
 * visible iff there is no contact c != ownID with cluster depth d' &gt; d on
 * the same level or a deeper level as x in the routing tree).
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
class KandyClusterDepthNodeVisitor extends
		AbstractNodeVisitor<HKademliaOverlayID> {

	/**
	 * Single, final KandyClusterDepthNodeVisitor instance (non-reentrant).
	 */
	private static final KandyClusterDepthNodeVisitor singleton = new KandyClusterDepthNodeVisitor();

	/**
	 * A Map with Nodes as keys and the permitted (exact) cluster depth for
	 * contacts that are visible from that LeafNode.
	 */
	private Map<Node<HKademliaOverlayID>, Integer> clusterDepths;

	/**
	 * Determine for each bucket which contacts are visible in a Kandy routing
	 * table.
	 * 
	 * @param clusterDepths
	 *            a Map in which the cluster depths will be saved. It will be
	 *            cleared.
	 * @return an KandyClusterDepthNodeVisitor instance. Note that this instance
	 *         is statically shared among all clients of this class. That is, at
	 *         runtime only one KandyClusterDepthNodeVisitor instance exists.
	 *         Thus, it is non-reentrant and should not be saved by clients
	 *         (should used immediately).
	 */
	protected static final KandyClusterDepthNodeVisitor getKandyClusterDepthNodeVisitor(
			final Map<Node<HKademliaOverlayID>, Integer> clusterDepths) {
		singleton.clusterDepths = clusterDepths;
		singleton.clusterDepths.clear();
		return singleton;
	}

	private KandyClusterDepthNodeVisitor() {
		// should not be called externally
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void visit(final BranchNode<HKademliaOverlayID> node) {
		int currentDepth = 0, maxDepth = 0;

		// calculate depth for each child
		for (final Node<HKademliaOverlayID> child : node.children.values()) {
			child.accept(this);
		}

		// calculate maximum depth of node's children
		for (final Node<HKademliaOverlayID> child : node.children.values()) {
			if ((currentDepth = clusterDepths.get(child)) > maxDepth) {
				maxDepth = currentDepth;
			}
		}

		// set depth of node and its direct children to max. depth
		for (final Node<HKademliaOverlayID> child : node.children.values()) {
			clusterDepths.put(child, maxDepth);
		}
		clusterDepths.put(node, maxDepth);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void visit(final LeafNode<HKademliaOverlayID> node) {
		// calculate maximum depth of all contacts in bucket except own ID
		int maxDepth = 0, currentDepth = 0;
		for (final HKademliaOverlayID contactID : node.kBucket.keySet()) {
			if (!contactID.equals(node.getOwnID())
					&& (currentDepth = contactID.getCommonClusterDepth(node
							.getOwnID())) > maxDepth) {
				maxDepth = currentDepth;
			}
		}
		clusterDepths.put(node, maxDepth);
	}

}
