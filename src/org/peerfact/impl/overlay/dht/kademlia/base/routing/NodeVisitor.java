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
 * Visitor for Nodes according to the GoF design pattern "Visitor". Can be used
 * to add functionality to the routing table, or implement different variations
 * of methods without having to change the routing table itself.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public interface NodeVisitor<T extends KademliaOverlayID> {

	/**
	 * The callback function if a Node of an "unknown" subtype is visited (for
	 * example in test cases when stubs are used).
	 * 
	 * @param node
	 *            the currently visited Node.
	 */
	public void visit(Node<T> node);

	/**
	 * The callback function if a PseudoRootNode is visited.
	 * 
	 * @param node
	 *            the currently visited PseudoRootNode.
	 */
	public void visit(PseudoRootNode<T> node);

	/**
	 * The callback function if a BranchNode is visited.
	 * 
	 * @param node
	 *            the currently visited BranchNode.
	 */
	public void visit(BranchNode<T> node);

	/**
	 * The callback function if a LeafNode is visited.
	 * 
	 * @param node
	 *            the currently visited LeafNode.
	 */
	public void visit(LeafNode<T> node);

}
