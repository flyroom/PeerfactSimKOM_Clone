/*
 * Copyright (c) 2012-2013 Open Source Community - <http://www.peerfact.org>
 * Copyright (c) 2011-2012 University of Paderborn - UPB
 * Copyright (c) 2005-2011 KOM - Multimedia Communications Lab
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */

package org.peerfact.impl.overlay.dht.kademlia2.base.routing;

import java.math.BigInteger;
import java.util.Set;

import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayContact;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayID;
import org.peerfact.impl.overlay.dht.kademlia.base.routing.Node;
import org.peerfact.impl.overlay.dht.kademlia.base.routing.NodeVisitor;
import org.peerfact.impl.overlay.dht.kademlia.base.routing.ParentNode;


/**
 * Test stub for ParentNode; exposes all instance variables as public.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 */
class ParentNodeStub<T extends KademliaOverlayID> implements ParentNode<T> {

	public int level;

	public T id;

	public Node<T> child = null;

	public BigInteger prefix;

	/**
	 * Creates a new ParentNode stub. Instance variables are public accessible.
	 * All parameters to this method (at least the non-primitive ones) may be
	 * set to null if desired.
	 * 
	 * @param level
	 *            the level of this node.
	 * @param id
	 *            the ID of this node.
	 * @param child
	 *            a preset child of this node.
	 * @param prefix
	 *            the prefix of this node.
	 */
	public ParentNodeStub(int level, T id, Node<T> child,
			BigInteger prefix) {
		this.level = level;
		this.id = id;
		this.child = child;
		this.prefix = prefix;
	}

	@Override
	public int getLevel() {
		return level;
	}

	@Override
	public T getOwnID() {
		return id;
	}

	@Override
	public void registerNewChild(Node<T> child1) {
		this.child = child1;
	}

	@Override
	public void accept(NodeVisitor<T> visitor) {
		visitor.visit(this);
	}

	@Override
	public BigInteger getPrefix() {
		return prefix;
	}

	@Override
	public Set<KademliaOverlayContact<T>> getAllSubContacts() {
		// TODO Auto-generated method stub
		return null;
	}
}