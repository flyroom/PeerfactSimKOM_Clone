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

package org.peerfact.impl.overlay.dht.kademlia.base.components;

import java.util.Collection;

import org.peerfact.api.overlay.OverlayNode;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.api.transport.TransLayer;
import org.peerfact.impl.overlay.AbstractOverlayNode.PeerStatus;
import org.peerfact.impl.overlay.dht.kademlia.base.components.RoutingTable.HierarchyRestrictableRoutingTable;
import org.peerfact.impl.overlay.dht.kademlia.base.components.RoutingTable.VisibilityRestrictableRoutingTable;
import org.peerfact.impl.overlay.dht.kademlia.base.messages.KademliaMsg;
import org.peerfact.impl.overlay.dht.kademlia.base.operations.OperationFactory;
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
 * Interface for Kademlia overlay nodes.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public interface Node<T extends KademliaOverlayID> extends
		OverlayNode<T, KademliaOverlayContact<T>> {

	/**
	 * Interface for (explicitly) hierarchy-aware Kademlia overlay nodes.
	 * 
	 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
	 */
	public interface HierarchyRestrictableNode<H extends HKademliaOverlayID>
			extends Node<H> {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public HierarchyRestrictableRoutingTable<H> getKademliaRoutingTable();
	}

	/**
	 * Interface for Kademlia overlay nodes that permit to carry out visibility
	 * restricted routing table lookups.
	 * 
	 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
	 */
	public interface VisibilityRestrictableNode<H extends HKademliaOverlayID>
			extends Node<H> {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public VisibilityRestrictableRoutingTable<H> getKademliaRoutingTable();
	}

	/**
	 * @return the local database of this node.
	 */
	public KademliaIndexer<T> getLocalIndex();

	/**
	 * @return the KademliaOverlayContact with contact information (address,
	 *         port, id) about this node.
	 */
	public KademliaOverlayContact<T> getLocalContact();

	/**
	 * @return the routing table of this node.
	 */
	public RoutingTable<T> getKademliaRoutingTable();

	/**
	 * @return a factory that permits to create operations that match the
	 *         currently chosen flavour of Kademlia (standard or Kandy or with
	 *         virtual hierarchy...)
	 */
	public OperationFactory<T> getOperationFactory();

	/**
	 * Extracts contact information about the sender of <code>msg</code> and
	 * inserts it into this node's routing table.
	 * 
	 * @param msg
	 *            the received message from which the sender is to be extracted
	 * @param senderAddr
	 *            the TransInfo of the sender
	 */
	public void addSenderToRoutingTable(KademliaMsg<T> msg, TransInfo senderAddr);

	/**
	 * Adds the contacts given in the Collection to the routing table.
	 * 
	 * @param contacts
	 *            a Collection with KademliaOverlayContacts to be added to the
	 *            routing table.
	 */
	public void addContactsToRoutingTable(
			Collection<KademliaOverlayContact<T>> contacts);

	/**
	 * {@inheritDoc}
	 */
	// TODO @Override
	public T getTypedOverlayID();

	/**
	 * @return the status ({@see BaseOverlayNode#PeerStatus}) of this node.
	 */
	public PeerStatus getPeerStatus();

	/**
	 * @return the message manager of this node.
	 */
	public TransLayer getMessageManager();

	/**
	 * This message is called to inform the Node that the routing table has been
	 * initially built (that is, the Node has joined the overlay network).
	 */
	public void routingTableBuilt();

	@Override
	public short getPort();

}
