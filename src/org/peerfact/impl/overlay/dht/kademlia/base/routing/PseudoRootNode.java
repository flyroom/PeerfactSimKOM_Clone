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
import java.util.Set;

import org.peerfact.impl.overlay.dht.kademlia.base.components.AbstractKademliaNode;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayContact;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayID;
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
 * This class represents a pseudo routing tree root. Pseudo because it is not
 * the actual root of the tree, but "a node above the root". It internally keeps
 * a reference to the current root node. This class is necessary to allow the
 * actual root to replace itself with another, new root (for example when
 * splitting a bucket) in a polymorphic manner.
 * 
 * Furthermore, this class keeps information that is common to all nodes in the
 * tree, such as the owner's identifier.
 * 
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public class PseudoRootNode<T extends KademliaOverlayID> implements
		ParentNode<T> {

	/**
	 * A reference to the root node of the routing tree. May be replaced by
	 * child nodes, for example when a bucket is split and has to be replaced
	 * with a branching.
	 */
	protected Node<T> root;

	/**
	 * The identifier of this routing tree's owner.
	 */
	private final T ownID;

	/**
	 * Constructs a new PseudoRootNode (and the underlying routing tree with
	 * initially one bucket).
	 * 
	 * @param ownID
	 *            the identifier of the owner of this routing table.
	 * @param conf
	 *            a RoutingTableConfig reference that permits to retrieve
	 *            configuration "constants".
	 */
	public PseudoRootNode(final T ownID, final RoutingTableConfig conf,
			final AbstractKademliaNode<T> owningOverlayNode) {
		this.ownID = ownID;
		this.root = new LeafNode<T>(this, conf, owningOverlayNode);
	}

	public PseudoRootNode(final T ownID, final RoutingTableConfig conf) {
		this(ownID, conf, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final T getOwnID() {
		return this.ownID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void registerNewChild(final Node<T> child) {
		this.root = child;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int getLevel() {
		// we are one level atop of the true root
		return -1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void accept(final NodeVisitor<T> visitor) {
		visitor.visit(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final BigInteger getPrefix() {
		return BigInteger.ZERO;
	}

	public Node<T> getRoot() {
		return root;
	}

	@Override
	public Set<KademliaOverlayContact<T>> getAllSubContacts() {
		return root.getAllSubContacts();
	}

}
