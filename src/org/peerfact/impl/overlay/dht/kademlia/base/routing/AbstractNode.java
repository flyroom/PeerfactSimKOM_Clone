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

import org.peerfact.impl.overlay.dht.kademlia.base.components.AbstractKademliaNode;
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
 * An abstract node in the routing tree that handles common behaviour for
 * subclasses that may be either a leaf node, that is a node without children,
 * or a branching node with exactly 2^
 * {@link RoutingTableConfig#getRoutingTreeOrder()} children. Common behaviour
 * includes calculation of the level of a node, finding out the node's prefix,
 * its own identifier and its parent node, and determining whether this node is
 * responsible for a given identifier.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public abstract class AbstractNode<T extends KademliaOverlayID> implements
		Node<T> {

	/**
	 * The prefix that all nodes saved in this subtree have in common. This
	 * subtree is responsible for identifiers in the range: [prefix *
	 * 2^(ID_LENGTH-level); (prefix+1) * 2^(ID_LENGTH-level) - 1]
	 * 
	 * The true bit length of this prefix is
	 * {@link RoutingTableConfig#getRoutingTreeOrder()} level, although
	 * BigInteger might return another length as BigInteger cuts off leading
	 * zeros.
	 */
	private final BigInteger prefix;

	/**
	 * The parent of this node.
	 */
	private final ParentNode<T> parent;

	/**
	 * Configuration values ("constants").
	 */
	protected final RoutingTableConfig config;

	private AbstractKademliaNode<T> owningOverlayNode;

	/**
	 * Constructor used by concrete subclasses to set the prefix of this
	 * abstract routing tree node. This constructor also registers this node
	 * with the parent node (as a child).
	 * 
	 * @param prefix
	 *            the prefix that all nodes saved in this subtree have in
	 *            common.
	 * @param parent
	 *            the ParentNode of this new node - may not be null.
	 * @param conf
	 *            a RoutingTableConfig reference that permits to retrieve
	 *            configuration "constants".
	 */
	protected AbstractNode(final BigInteger prefix, final ParentNode<T> parent,
			final RoutingTableConfig conf,
			final AbstractKademliaNode<T> owningOverlayNode) {
		this.prefix = prefix;
		this.parent = parent;
		this.config = conf;
		this.owningOverlayNode = owningOverlayNode;
		parent.registerNewChild(this);
	}

	protected AbstractNode(final BigInteger prefix, final ParentNode<T> parent,
			final RoutingTableConfig conf) {
		this(prefix, parent, conf, null);
	}

	/**
	 * @return the level of this node. The root node has level 0, and each child
	 *         node has the level of its parent plus one.
	 */
	@Override
	public final int getLevel() {
		return this.parent.getLevel() + 1;
	}

	/**
	 * @return the KademliaOverlayID of the peer that owns this routing table.
	 */
	@Override
	public final T getOwnID() {
		return this.parent.getOwnID();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final BigInteger getPrefix() {
		return this.prefix;
	}

	/**
	 * @return the parent of this node.
	 */
	public final ParentNode<T> getParent() {
		return this.parent;
	}

	/**
	 * Tests whether this node is responsible for the given identifier
	 * <code>test</code>. Responsibility is transitive, that is the current node
	 * might not be directly responsible for a given identifier although this
	 * method returns true. In that case, it is guaranteed that one of this
	 * node's children is responsible. Similarly, the root node is responsible
	 * for all identifiers.
	 * 
	 * A node is responsible for identifiers in the range: <code>[prefix *
	 * 2^(ID_LENGTH-level); (prefix+1) * 2^(ID_LENGTH-level) - 1]</code>
	 * 
	 * @param test
	 *            the contact ID that is to be checked.
	 * @return true, if this node (or any of its children) is responsible for
	 *         the given identifier.
	 */
	public final boolean isResponsibleFor(final T test) {
		// cannot use prefix.bitLength() because that ignores leading zeros
		return prefix.equals(test.getBigInt().shiftRight(
				config.getIDLength() - getLevel()
						* config.getRoutingTreeOrder()));
	}

	public AbstractKademliaNode<T> getOwningOverlayNode() {
		return owningOverlayNode;
	}

}
