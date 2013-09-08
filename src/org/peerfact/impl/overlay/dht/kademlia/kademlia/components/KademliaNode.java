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

package org.peerfact.impl.overlay.dht.kademlia.kademlia.components;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.peerfact.api.common.Message;
import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.api.overlay.dht.DHTNode;
import org.peerfact.api.overlay.kbr.KBRForwardInformation;
import org.peerfact.api.overlay.kbr.KBRListener;
import org.peerfact.api.overlay.kbr.KBRLookupProvider;
import org.peerfact.api.overlay.kbr.KBRNode;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.api.transport.TransLayer;
import org.peerfact.api.transport.TransProtocol;
import org.peerfact.impl.overlay.dht.kademlia.base.KademliaSetup;
import org.peerfact.impl.overlay.dht.kademlia.base.StaticConfig;
import org.peerfact.impl.overlay.dht.kademlia.base.components.AbstractKademliaNode;
import org.peerfact.impl.overlay.dht.kademlia.base.components.ComponentsConfig;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayContact;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayID;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayKey;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaRoutingTable;
import org.peerfact.impl.overlay.dht.kademlia.base.components.RequestHandler;
import org.peerfact.impl.overlay.dht.kademlia.base.components.RoutingTable;
import org.peerfact.impl.overlay.dht.kademlia.base.operations.AbstractKademliaOperation.Reason;
import org.peerfact.impl.overlay.dht.kademlia.base.operations.OperationFactory.NodeLookupOperation;
import org.peerfact.impl.overlay.dht.kademlia.kademlia.operations.KademliaOperationFactory;
import org.peerfact.impl.overlay.kbr.KBRMsgHandler;
import org.peerfact.impl.overlay.kbr.messages.KBRForwardInformationImpl;
import org.peerfact.impl.overlay.kbr.messages.KBRForwardMsg;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.util.toolkits.HashToolkit;


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
 * A standard Kademlia node. For details about nodes, see
 * {@link AbstractKademliaNode}.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public class KademliaNode<T extends KademliaOverlayID> extends
		AbstractKademliaNode<T> implements
		DHTNode<T, KademliaOverlayContact<T>, KademliaOverlayKey> {

	/**
	 * This node's operation factory (used to construct lookup, store etc.
	 * operations)
	 */
	private final KademliaOperationFactory<T> operationFactory;

	private KBRListener<T, KademliaOverlayContact<T>, KademliaOverlayKey> kbrListener;

	private KBRLookupProvider<T, KademliaOverlayContact<T>, KademliaOverlayKey> kbrLookupProvider;

	/**
	 * Constructs a new standard Kademlia node.
	 * 
	 * @param myContact
	 *            the KademliaOverlayContact of the new node.
	 * @param messageManager
	 *            the TransLayer of the new node.
	 * @param conf
	 *            a ComponentsConfig reference that permits to retrieve
	 *            configuration "constants".
	 */
	public KademliaNode(final KademliaOverlayContact<T> myContact,
			final TransLayer msgMgr, final ComponentsConfig conf) {
		super(myContact, msgMgr, conf);
		routingTable = new KademliaRoutingTable<T>(myContact, conf, this);
		operationFactory = new KademliaOperationFactory<T>(this, conf);

		// construct handler for incoming requests & new neighbours
		final RequestHandler<T> requestHandler = new RequestHandler<T>(
				getMessageManager(), this, conf);
		getMessageManager().addTransMsgListener(requestHandler, getPort());
		((KademliaRoutingTable<T>) routingTable)
				.registerProximityListener(requestHandler);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final KademliaOperationFactory<T> getOperationFactory() {
		return this.operationFactory;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final RoutingTable<T> getKademliaRoutingTable() {
		return (RoutingTable<T>) routingTable;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String toString() {
		return "[KademliaNode|" + getLocalContact().getOverlayID()
				+ "; status=" + getPeerStatus() + "]";
	}

	@Override
	public int nodeLookup(KademliaOverlayKey key,
			OperationCallback<List<KademliaOverlayContact<T>>> callback,
			boolean returnSingleNode) {

		if (!(key != null)) {
			return -1;
		}

		NodeLookupOperation<List<KademliaOverlayContact<T>>, T> op = getOperationFactory()
				.getKClosestNodesLookupOperation(key,
						Reason.USER_INITIATED, callback);

		return op.getOperationID();
	}

	@Override
	public KademliaOverlayKey getRandomOverlayKey() {

		BigInteger idValue = HashToolkit.getSHA1Hash(
				((Integer) Simulator.getRandom().nextInt()).toString(),
				KademliaSetup.getConfig().getIDLength());

		return new KademliaOverlayID(idValue, KademliaSetup.getConfig())
				.toKey();
	}

	@Override
	public void route(KademliaOverlayKey key, Message msg,
			KademliaOverlayContact<T> hint) {
		OverlayContact<T> nextHop = null;
		if (hint != null) {
			nextHop = hint;
		} else if (key != null) {
			nextHop = local_lookup(key, 1).get(0);

			// Inform the monitors about an initiated query
			Simulator.getMonitor().kbrQueryStarted(getLocalOverlayContact(),
					msg);
		} else {
			log.error("Both key and hint are null!!");
			return;
		}
		if (kbrListener != null) {
			KBRForwardInformation<T, KademliaOverlayContact<T>, KademliaOverlayKey> info = new KBRForwardInformationImpl<T, KademliaOverlayContact<T>, KademliaOverlayKey>(
					key,
					msg, (KademliaOverlayContact<T>) nextHop);
			kbrListener.forward(info);
			key = info.getKey();
			msg = info.getMessage();
			nextHop = info.getNextHopAgent();
		} else {
			log
					.error("There is no KBRListener to notify. Please register your Application (use method: setKBRListener).");
		}
		KBRForwardMsg<T, KademliaOverlayKey> fm = new KBRForwardMsg<T, KademliaOverlayKey>(
				getOverlayID(),
				nextHop.getOverlayID(),
				key, msg);
		getTransLayer().send(fm, nextHop.getTransInfo(), getPort(),
				TransProtocol.UDP);

	}

	@Override
	public List<KademliaOverlayContact<T>> local_lookup(KademliaOverlayKey key,
			int num) {

		LinkedList<KademliaOverlayContact<T>> contactList = new LinkedList<KademliaOverlayContact<T>>();

		Set<KademliaOverlayContact<T>> contactSet = getKademliaRoutingTable()
				.localLookup(key, num);

		contactList.addAll(contactSet);
		return contactList;
	}

	@Override
	public List<KademliaOverlayContact<T>> replicaSet(KademliaOverlayKey key,
			int maxRank) {
		return local_lookup(key, maxRank);
	}

	@Override
	public List<KademliaOverlayContact<T>> neighborSet(int num) {
		LinkedList<KademliaOverlayContact<T>> neighbors = new LinkedList<KademliaOverlayContact<T>>();

		// Extract neighbors from routing table by performing a local_lockup
		// for the key generated from the OverlayID of this node.
		Set<KademliaOverlayContact<T>> neighborSet = getKademliaRoutingTable()
				.localLookup(getTypedOverlayID().toKey(), num);

		neighbors.addAll(neighborSet);
		return neighbors;
	}

	@Override
	public T[] range(KademliaOverlayContact<T> contact, int rank) {

		// For the following computation we need the node itself, rank nodes
		// inside the
		// interval and the two exclusive borders of the interval
		int numberOfNeededNodes = rank + 3;

		Set<KademliaOverlayContact<T>> neighborSet = getKademliaRoutingTable()
				.localLookup(getTypedOverlayID().toKey(), numberOfNeededNodes);

		BigInteger xorDistanceToUpper = BigInteger.ZERO;
		BigInteger xorDistanceToLower = BigInteger.ZERO;

		BigInteger localKeyAsBigInteger = getTypedOverlayID().toKey()
				.getBigInt();

		// Find the distances to the nodes at the border of the interval

		for (KademliaOverlayContact<T> contactInSet : neighborSet) {
			BigInteger contactKeyAsBigInteger = contactInSet.getOverlayID()
					.toKey().getBigInt();

			BigInteger distance = localKeyAsBigInteger
					.xor(contactKeyAsBigInteger);

			if (contactKeyAsBigInteger.compareTo(localKeyAsBigInteger) == -1) {
				// The contacts key is smaller than the local key

				if (distance.compareTo(xorDistanceToLower) == 1) {
					// Current distance is the new greatest distance to a lower
					// key
					xorDistanceToLower = distance;
				}
			} else if (contactKeyAsBigInteger.compareTo(localKeyAsBigInteger) == 1) {
				// The contacts key is greater than the local key

				if (distance.compareTo(xorDistanceToUpper) == 1) {
					// Current distance is the new greatest distance to a upper
					// key
					xorDistanceToUpper = distance;
				}
			}
		}

		/*
		 * Compute the range for return
		 * 
		 * Lower bound of the range = (key of local node) - [(distance to lowest
		 * node in set of neighbors)/2]
		 * 
		 * Upper bound of the range = (key of local node) + [(distance to
		 * greatest node in set of neighbors)/2]
		 */

		KademliaOverlayID[] range = new KademliaOverlayID[2];

		BigInteger lowerBound = localKeyAsBigInteger
				.subtract(xorDistanceToLower.divide(BigInteger
						.valueOf(2)));

		BigInteger upperBound = localKeyAsBigInteger.add(xorDistanceToUpper
				.divide(BigInteger.valueOf(2)));

		range[0] = new KademliaOverlayID(lowerBound, KademliaSetup.getConfig());
		range[1] = new KademliaOverlayID(upperBound, KademliaSetup.getConfig());

		return (T[]) range;
	}

	@Override
	public boolean isRootOf(KademliaOverlayKey key) {

		Set<KademliaOverlayContact<T>> closestAroundKey = this
				.getKademliaRoutingTable().localLookup(
						key,
						getLocalConfig().getBucketSize());

		// This node is possibly the root if no other node in his routing table
		// is "nearer"

		BigInteger kademliaKeyAsBigInteger = key
				.getBigInt();

		// Compute the distance of the this node to the key
		BigInteger myDistance = getLocalOverlayContact().getOverlayID().toKey()
				.getBigInt().xor(kademliaKeyAsBigInteger);

		// Check if there is a node that is "nearer" than this node itself
		for (KademliaOverlayContact<T> contact : closestAroundKey) {
			BigInteger distance = contact.getOverlayID().toKey().getBigInt()
					.xor(kademliaKeyAsBigInteger);
			if (myDistance.compareTo(distance) == 1) {
				return false;
			}
		}

		// There was no node that is "nearer", so this node thinks it is the
		// root
		return true;
	}

	@Override
	public void setKBRListener(
			KBRListener<T, KademliaOverlayContact<T>, KademliaOverlayKey> listener) {
		this.kbrListener = listener;
		KBRMsgHandler<T, KademliaOverlayContact<T>, KademliaOverlayKey> msgHandler = new KBRMsgHandler<T, KademliaOverlayContact<T>, KademliaOverlayKey>(
				this, this, kbrListener);

		kbrLookupProvider = msgHandler.getLookupProvider();
	}

	@Override
	public KademliaOverlayKey getNewOverlayKey(int rank) {
		return new KademliaOverlayKey(rank, new StaticConfig());
	}

	@Override
	public KademliaOverlayContact<T> getLocalOverlayContact() {
		short port = getHost().getOverlay(KBRNode.class).getPort();
		TransInfo transInfo = getHost().getTransLayer().getLocalTransInfo(port);

		return new KademliaOverlayContact<T>(getTypedOverlayID(), transInfo);
	}

	@Override
	public KademliaOverlayContact<T> getOverlayContact(
			KademliaOverlayID id,
			TransInfo transinfo) {
		return new KademliaOverlayContact<T>((T) id, transinfo);
	}

	@Override
	public void hadContactTo(KademliaOverlayContact<T> contact) {
		getKademliaRoutingTable().addContact(
				contact);
	}

	@Override
	public KBRLookupProvider<T, KademliaOverlayContact<T>, KademliaOverlayKey> getKbrLookupProvider() {
		return kbrLookupProvider;
	}

}
