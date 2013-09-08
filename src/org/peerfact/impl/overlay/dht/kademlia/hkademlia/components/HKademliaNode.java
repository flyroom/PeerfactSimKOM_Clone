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

package org.peerfact.impl.overlay.dht.kademlia.hkademlia.components;

import org.peerfact.api.transport.TransLayer;
import org.peerfact.impl.overlay.dht.kademlia.base.components.AbstractKademliaNode;
import org.peerfact.impl.overlay.dht.kademlia.base.components.ComponentsConfig;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayContact;
import org.peerfact.impl.overlay.dht.kademlia.base.components.RequestHandler;
import org.peerfact.impl.overlay.dht.kademlia.base.components.Node.HierarchyRestrictableNode;
import org.peerfact.impl.overlay.dht.kademlia.base.components.RoutingTable.HierarchyRestrictableRoutingTable;
import org.peerfact.impl.overlay.dht.kademlia.hkademlia.operations.HKademliaOperationFactory;

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
 * A node in an overlay network that supports routing in virtual hierarchies
 * ("hierarchical Kademlia"), version 2. For details about nodes, see
 * {@link AbstractKademliaNode}.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public class HKademliaNode extends
		AbstractKademliaNode<HKademliaOverlayID> implements
		HierarchyRestrictableNode<HKademliaOverlayID> {

	/**
	 * This node's operation factory (used to construct lookup, store etc.
	 * operations)
	 */
	private final HKademliaOperationFactory operationFactory;

	/**
	 * Constructs a new hierarchical Kademlia node (version 2).
	 * 
	 * @param myContact
	 *            the KademliaOverlayContact of the new node.
	 * @param messageManager
	 *            the TransLayer of the new node.
	 * @param conf
	 *            a ComponentsConfig reference that permits to retrieve
	 *            configuration "constants".
	 */
	public HKademliaNode(
			final KademliaOverlayContact<HKademliaOverlayID> myContact,
			final TransLayer messageManager, final ComponentsConfig conf) {
		super(myContact, messageManager, conf);
		routingTable = new HKademliaRoutingTable(myContact,
				conf, this);
		operationFactory = new HKademliaOperationFactory(this, conf);

		// construct handler for incoming requests & new neighbours
		final RequestHandler<HKademliaOverlayID> requestHandler = new HRequestHandler(
				getMessageManager(), this, conf);
		getMessageManager().addTransMsgListener(requestHandler, getPort());
		((HierarchyRestrictableRoutingTable<HKademliaOverlayID>) routingTable)
				.registerProximityListener(requestHandler);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final HKademliaOperationFactory getOperationFactory() {
		return this.operationFactory;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final HierarchyRestrictableRoutingTable<HKademliaOverlayID> getKademliaRoutingTable() {
		return (HierarchyRestrictableRoutingTable<HKademliaOverlayID>) routingTable;
	}

}
