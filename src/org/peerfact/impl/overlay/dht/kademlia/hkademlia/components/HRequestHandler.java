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

import java.util.Set;

import org.peerfact.api.common.Message;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.api.transport.TransLayer;
import org.peerfact.api.transport.TransProtocol;
import org.peerfact.impl.overlay.dht.kademlia.base.components.AbstractKademliaNode;
import org.peerfact.impl.overlay.dht.kademlia.base.components.ComponentsConfig;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayContact;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayKey;
import org.peerfact.impl.overlay.dht.kademlia.base.components.RequestHandler;
import org.peerfact.impl.overlay.dht.kademlia.base.components.Node.HierarchyRestrictableNode;
import org.peerfact.impl.overlay.dht.kademlia.base.messages.KademliaMsg;
import org.peerfact.impl.overlay.dht.kademlia.base.messages.NodeListMsg;
import org.peerfact.impl.overlay.dht.kademlia.hkademlia.messages.HKClosestNodesLookupMsg;
import org.peerfact.impl.transport.TransMsgEvent;


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
 * A handler for incoming (unsolicited) messages/requests that is able to answer
 * cluster-restricted lookup requests.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
class HRequestHandler extends RequestHandler<HKademliaOverlayID> {

	/**
	 * The HierarchicalNode that owns this message handler.
	 */
	private final HierarchyRestrictableNode<HKademliaOverlayID> myNode;

	/**
	 * Constructs a new hierarchical message/request handler that is able to
	 * answer cluster-restricted lookup requests. It has to be manually
	 * registered as ProximityListener of <code>myNode</code>'s routing table
	 * and as a TransMessageListener of <code>manager</code>.
	 * 
	 * @param manager
	 *            the TransLayer used to reply to messages.
	 * @param myNode
	 *            the HierarchicalNode that owns this message handler.
	 * @param conf
	 *            an ComponentsConfig reference that permits to retrieve
	 *            configuration "constants".
	 */
	public HRequestHandler(final TransLayer manager,
			final HierarchyRestrictableNode<HKademliaOverlayID> myNode,
			final ComponentsConfig conf) {
		super(manager, myNode, conf);
		this.myNode = myNode;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected final void processMsg(final TransMsgEvent tme) {
		final Message msg = tme.getPayload();
		final TransInfo senderAddr;

		if (msg instanceof HKClosestNodesLookupMsg) {
			senderAddr = tme.getSenderTransInfo();
			myNode.addSenderToRoutingTable(
					(KademliaMsg<HKademliaOverlayID>) msg, senderAddr);
			processClusterRestrictedNodeLookup(
					(HKClosestNodesLookupMsg) msg, tme);
		} else {
			// other kinds of messages handled in superclass
			super.processMsg(tme);
		}
	}

	/**
	 * Handles reception of a cluster-restricted node lookup message.
	 * 
	 * @param msg
	 *            the HKClosestNodesLookupMsg that has been received. (This
	 *            message is a copy of the message contained in
	 *            <code>receiveEvent</code>.)
	 * @param receiveEvent
	 *            the TransMsgEvent that occurred at reception of the message
	 *            that is to be handled here.
	 */
	private final void processClusterRestrictedNodeLookup(
			final HKClosestNodesLookupMsg msg,
			final TransMsgEvent receiveEvent) {
		final KademliaOverlayKey key = msg.getNodeKey();
		final int minClusterDepth = msg.getMinClusterDepth();
		final HKademliaOverlayID clusterRef = msg.getSender();
		int localBucketSize = ((AbstractKademliaNode<HKademliaOverlayID>) myNode)
				.getLocalConfig().getBucketSize();
		final Set<KademliaOverlayContact<HKademliaOverlayID>> neighbours = myNode
				.getKademliaRoutingTable().localLookup(key, localBucketSize,
						minClusterDepth, clusterRef);
		final NodeListMsg<HKademliaOverlayID> reply = new NodeListMsg<HKademliaOverlayID>(
				myNode.getTypedOverlayID(), msg.getSender(), neighbours,
				msg.getReason(), config);
		transLayer.sendReply(reply, receiveEvent, myNode.getPort(),
				TransProtocol.UDP);
	}

}
