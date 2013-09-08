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

import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayContact;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayID;


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
 * Represents a node in the routing tree.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public interface Node<T extends KademliaOverlayID> {

	/**
	 * Accepts and calls back the given visitor (according to the GoF design
	 * pattern "Visitor").
	 * 
	 * @param visitor
	 *            the visitor that is to be called back.
	 */
	public void accept(NodeVisitor<T> visitor);

	/**
	 * @return the KademliaOverlayID of the peer that owns this routing table.
	 */
	public T getOwnID();

	/**
	 * @return the level of this node. The root node has level 0, and each child
	 *         node has the level of its parent plus one. (If there are pseudo
	 *         nodes on top of the root node, these might have negative level
	 *         values.)
	 */
	public int getLevel();

	/**
	 * Returns the prefix that all nodes saved in this subtree have in common.
	 * 
	 * @return the prefix that all nodes saved in this subtree have in common.
	 */
	public BigInteger getPrefix();

	public Set<KademliaOverlayContact<T>> getAllSubContacts();

}
