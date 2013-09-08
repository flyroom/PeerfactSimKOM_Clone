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

import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.peerfact.api.common.Message;
import org.peerfact.api.overlay.dht.DHTObject;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.api.transport.TransLayer;
import org.peerfact.api.transport.TransMessageListener;
import org.peerfact.api.transport.TransProtocol;
import org.peerfact.impl.overlay.AbstractOverlayNode.PeerStatus;
import org.peerfact.impl.overlay.dht.kademlia.base.components.RoutingTable.ProximityListener;
import org.peerfact.impl.overlay.dht.kademlia.base.messages.DataLookupMsg;
import org.peerfact.impl.overlay.dht.kademlia.base.messages.DataMsg;
import org.peerfact.impl.overlay.dht.kademlia.base.messages.KClosestNodesLookupMsg;
import org.peerfact.impl.overlay.dht.kademlia.base.messages.KademliaMsg;
import org.peerfact.impl.overlay.dht.kademlia.base.messages.NodeListMsg;
import org.peerfact.impl.overlay.dht.kademlia.base.operations.AbstractKademliaOperation.Reason;
import org.peerfact.impl.transport.TransMsgEvent;
import org.peerfact.impl.util.logging.SimLogger;


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
 * A handler for incoming (unsolicited) messages/requests for a Kademlia node.
 * 
 * @author Sebastian Kaune <peerfact@kom.tu-darmstadt.de>
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public class RequestHandler<T extends KademliaOverlayID> implements
		TransMessageListener, ProximityListener<T> {

	/**
	 * A logger for this class.
	 */
	final static Logger log = SimLogger.getLogger(RequestHandler.class);

	/**
	 * The Node that owns this message handler.
	 */
	private final Node<T> myNode;

	/**
	 * Reference to the transport layer used to send messages.
	 */
	protected final TransLayer transLayer;

	/**
	 * Configuration values ("constants").
	 */
	protected final ComponentsConfig config;

	/**
	 * Constructs a new standard message handler (non-hierarchical). It has to
	 * be manually registered as ProximityListener of <code>myNode</code>'s
	 * routing table and as a TransMessageListener of <code>manager</code>.
	 * 
	 * @param manager
	 *            the TransLayer used to reply to messages.
	 * @param myNode
	 *            the node that owns this message handler.
	 * @param conf
	 *            an ComponentsConfig reference that permits to retrieve
	 *            configuration "constants".
	 */
	public RequestHandler(final TransLayer manager, final Node<T> myNode,
			final ComponentsConfig conf) {
		this.config = conf;
		this.transLayer = manager;
		this.myNode = myNode;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void messageArrived(final TransMsgEvent tme) {
		final Message msg = tme.getPayload();

		// ignore messages if peer is offline
		if (myNode.getPeerStatus() == PeerStatus.ABSENT) {
			log.debug("Ignoring msg=" + msg + " at node=" + myNode);
		} else {
			// log.debug("Received msg=" + msg + " at node=" + myNode);
			processMsg(tme);
		}
	}

	/**
	 * Processes the given TransMsgEvent. As a precondition, it can be assumed
	 * that this peer is not offline (that is, the event is actually to be
	 * processed).
	 * 
	 * @param tme
	 *            the TransMsgEvent that corresponds to the message that has
	 *            been received.
	 */
	@SuppressWarnings("unchecked")
	protected void processMsg(final TransMsgEvent tme) {
		final Message msg = tme.getPayload();
		final TransInfo senderAddr = tme.getSenderTransInfo();

		if (msg instanceof KademliaMsg) {
			myNode.addSenderToRoutingTable((KademliaMsg<T>) msg, senderAddr);

			if (msg instanceof KClosestNodesLookupMsg) {
				processNodeLookup((KClosestNodesLookupMsg<T>) msg, tme);
			} else if (msg instanceof DataLookupMsg) {
				processDataLookup((DataLookupMsg<T>) msg, tme);
			} else if (msg instanceof DataMsg) {
				processDataStore((DataMsg<T>) msg);
			} else {
				throw new IllegalStateException("["
						+ this.myNode.getTypedOverlayID()
						+ "] Unknown message received: " + msg);
			}
		}
	}

	/**
	 * Handles reception of a (non-hierarchical) node lookup message.
	 * 
	 * @param msg
	 *            the KClosestNodesLookupMsg that has been received. (This is a
	 *            copy of the message contained in <code>receiveEvent</code>.)
	 * @param receiveEvent
	 *            the TransMsgEvent that occurred at reception of the message
	 *            that is to be handled here.
	 */
	private final void processNodeLookup(final KClosestNodesLookupMsg<T> msg,
			final TransMsgEvent receiveEvent) {
		final KademliaOverlayKey key = msg.getNodeKey();
		final Set<KademliaOverlayContact<T>> neighbours = nodeLookupForNode(key);
		final NodeListMsg<T> reply = new NodeListMsg<T>(this.myNode
				.getTypedOverlayID(), msg.getSender(), neighbours, msg
				.getReason(), config);
		// log.debug("Replying to node lookup (key=" + key + "; result="
		// + neighbours + "; node=" + myNode + ")");
		transLayer.sendReply(reply, receiveEvent, myNode.getPort(),
				TransProtocol.UDP);
	}

	/**
	 * Looks up {@link ComponentsConfig#getBucketSize()} contacts to be returned
	 * if a node lookup message comes in.
	 * 
	 * @param key
	 *            the KademliaOverlayKey to be looked up.
	 * @return a Set with K contacts from the local routing table.
	 */
	protected Set<KademliaOverlayContact<T>> nodeLookupForNode(
			final KademliaOverlayKey key) {
		int maxReturnedResults = ((AbstractKademliaNode<T>) myNode)
				.getLocalConfig().getMaxReturnedResults();

		return myNode.getKademliaRoutingTable().localLookup(key,
				maxReturnedResults);
	}

	/**
	 * Handles reception of a (non-hierarchical) data lookup message.
	 * 
	 * @param msg
	 *            the DataLookupMsg that has been received. (This is a copy of
	 *            the message contained in <code>receiveEvent</code>.)
	 * @param receiveEvent
	 *            the TransMsgEvent that occurred at reception of the message
	 *            that is to be handled here.
	 */
	private final void processDataLookup(final DataLookupMsg<T> msg,
			final TransMsgEvent receiveEvent) {
		final KademliaOverlayKey key = msg.getDataKey();
		final KademliaMsg<T> reply;

		// Checks if TimestampedValue is located in local index or not
		final DHTObject value = myNode.getLocalIndex().get(key);
		if (value != null) {
			// log.debug("Replying to data lookup with data (key=" + key
			// + "; result=" + value + "; node=" + myNode + ")");
			reply = new DataMsg<T>(myNode.getTypedOverlayID(), msg.getSender(),
					key, value, msg.getReason(), config);
		} else {
			// Return message with list of k closest overlay contacts
			final Set<KademliaOverlayContact<T>> neighbours = nodeLookupForData(key);
			// log.debug("Replying to data lookup with node list (key=" + key
			// + "; result=" + neighbours + "; node=" + myNode + ")");
			reply = new NodeListMsg<T>(myNode.getTypedOverlayID(), msg
					.getSender(), neighbours, msg.getReason(), config);
		}
		transLayer.sendReply(reply, receiveEvent, myNode.getPort(),
				TransProtocol.UDP);
	}

	/**
	 * Looks up {@link ComponentsConfig#getBucketSize()} contacts to be returned
	 * if a data lookup message comes in.
	 * 
	 * @param key
	 *            the KademliaOverlayKey to be looked up.
	 * @return a Set with K contacts from the local routing table.
	 */
	protected Set<KademliaOverlayContact<T>> nodeLookupForData(
			final KademliaOverlayKey key) {
		int localBucketSize = ((AbstractKademliaNode<T>) myNode)
				.getLocalConfig().getBucketSize();

		return myNode.getKademliaRoutingTable().localLookup(key,
				localBucketSize);
	}

	/**
	 * Handles reception of an unsolicited DataMsg (used to store data on this
	 * node).
	 * 
	 * @param msg
	 *            the DataMsg that has been received.
	 */
	private final void processDataStore(final DataMsg<T> msg) {
		final KademliaOverlayKey key = msg.getKey();
		final DHTObject receivedValue = msg.getValue();

		// log.debug("Storing data (key=" + key + "; data=" + receivedValue
		// + "; node=" + myNode + ")");
		myNode.getLocalIndex().put(key, receivedValue);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void newCloseContactArrived(
			final KademliaOverlayContact<T> newContact) {
		final Map<KademliaOverlayKey, DHTObject> allItems;
		Set<KademliaOverlayContact<T>> closestAroundKey;
		DataMsg<T> message;

		/*
		 * A new close neighbour newContact has been seen - it has to be
		 * informed about the data items that it has to store. However, this
		 * method is only executed if this node (myNode) is PRESENT, that is, if
		 * its routing table is in a relatively stable state. (Else, in state
		 * TO_JOIN for instance, we cannot be sure that newContact is actually a
		 * close neighbour. There could as well be closer neighbours that just
		 * are not already known.)
		 */
		if (!myNode.getPeerStatus().equals(PeerStatus.PRESENT)) {
			return;
		}

		// get all entries from local database
		allItems = myNode.getLocalIndex().getEntries();

		/*
		 * Send all items that the new node has to store: entries that both this
		 * and the new node are responsible for. "Responsible for" is defined as
		 * being part of the K closest nodes around the key of the data item.
		 * This is determined with the knowledge of the local routing table. (We
		 * have to obtain an unfiltered view for the case of Kandy.)
		 */
		for (final Map.Entry<KademliaOverlayKey, DHTObject> item : allItems
				.entrySet()) {
			int localBucketSize = ((AbstractKademliaNode<T>) myNode)
					.getLocalConfig().getBucketSize();

			closestAroundKey = myNode.getKademliaRoutingTable().localLookup(
					item.getKey(), localBucketSize);

			// continue with next item if one of own/new node not responsible
			// for data. assumption: no visibility restriction
			if (!closestAroundKey.contains(myNode.getLocalContact())
					|| !closestAroundKey.contains(newContact)) {
				continue;
			}

			// else send data
			message = new DataMsg<T>(myNode.getTypedOverlayID(), newContact
					.getOverlayID(), item.getKey(), item.getValue(),
					Reason.MAINTENANCE, config);
			myNode.getMessageManager().send(message, newContact.getTransInfo(),
					myNode.getPort(), TransProtocol.UDP);
		}
	}

}
