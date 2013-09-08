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

import java.util.Set;

import org.peerfact.api.transport.TransLayer;
import org.peerfact.impl.overlay.dht.kademlia.base.components.AbstractKademliaNode;
import org.peerfact.impl.overlay.dht.kademlia.base.components.ComponentsConfig;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayContact;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayKey;
import org.peerfact.impl.overlay.dht.kademlia.base.components.RequestHandler;
import org.peerfact.impl.overlay.dht.kademlia.base.components.Node.VisibilityRestrictableNode;
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
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public class GenerallyRestrictedRequestHandler
		extends RequestHandler<HKademliaOverlayID> {

	/**
	 * The node that owns this request handler.
	 */
	private final VisibilityRestrictableNode<HKademliaOverlayID> myNode;

	/**
	 * Constructs a new message handler that restricts results for both data and
	 * node lookups. It has to be manually registered as ProximityListener of
	 * <code>myNode</code>'s routing table and as a TransMessageListener of
	 * <code>manager</code>.
	 * 
	 * @param manager
	 *            the TransLayer used to reply to messages.
	 * @param myNode
	 *            the VisibilityRestrictableNode that owns this message handler.
	 * @param conf
	 *            an ComponentsConfig reference that permits to retrieve
	 *            configuration "constants".
	 */
	public GenerallyRestrictedRequestHandler(final TransLayer manager,
			final VisibilityRestrictableNode<HKademliaOverlayID> myNode,
			ComponentsConfig conf) {
		super(manager, myNode, conf);
		this.myNode = myNode;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final Set<KademliaOverlayContact<HKademliaOverlayID>> nodeLookupForNode(
			final KademliaOverlayKey key) {
		int localBucketSize = ((AbstractKademliaNode<HKademliaOverlayID>) myNode)
				.getLocalConfig().getBucketSize();

		return myNode.getKademliaRoutingTable()
				.visibilityRestrictedLocalLookup(key, localBucketSize);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final Set<KademliaOverlayContact<HKademliaOverlayID>> nodeLookupForData(
			final KademliaOverlayKey key) {
		int localBucketSize = ((AbstractKademliaNode<HKademliaOverlayID>) myNode)
				.getLocalConfig().getBucketSize();

		return myNode.getKademliaRoutingTable()
				.visibilityRestrictedLocalLookup(key, localBucketSize);
	}
}
