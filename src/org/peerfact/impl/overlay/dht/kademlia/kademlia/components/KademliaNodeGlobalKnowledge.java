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
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.peerfact.api.common.Host;
import org.peerfact.api.common.Message;
import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.api.overlay.OverlayKey;
import org.peerfact.api.overlay.kbr.KBRForwardInformation;
import org.peerfact.api.overlay.kbr.KBRListener;
import org.peerfact.api.overlay.kbr.KBRLookupProvider;
import org.peerfact.api.overlay.kbr.KBRNode;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.api.transport.TransLayer;
import org.peerfact.api.transport.TransProtocol;
import org.peerfact.impl.overlay.dht.kademlia.base.KademliaSetup;
import org.peerfact.impl.overlay.dht.kademlia.base.StaticConfig;
import org.peerfact.impl.overlay.dht.kademlia.base.components.ComponentsConfig;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayContact;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayID;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayKey;
import org.peerfact.impl.overlay.kbr.KBRMsgHandler;
import org.peerfact.impl.overlay.kbr.messages.KBRForwardInformationImpl;
import org.peerfact.impl.overlay.kbr.messages.KBRForwardMsg;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.util.oracle.GlobalOracle;
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
 * This class provides access to the <code>KademliaNode</code> via the
 * <code>KBR</code> interface.
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * 
 * @param <T>
 *            the concrete implementation of overlay id used by the scenario
 * @version 05/06/2011
 */
public class KademliaNodeGlobalKnowledge<T extends KademliaOverlayID>
		extends KademliaNode<T> {

	private KBRListener<T, KademliaOverlayContact<T>, KademliaOverlayKey> kbrListener;

	private KBRLookupProvider<T, KademliaOverlayContact<T>, KademliaOverlayKey> kbrLookupProvider;

	public KademliaNodeGlobalKnowledge(KademliaOverlayContact<T> myContact,
			TransLayer msgMgr, ComponentsConfig conf) {
		super(myContact, msgMgr, conf);

	}

	@Override
	public boolean isRootOf(KademliaOverlayKey key) {

		List<KademliaOverlayContact<T>> closestAroundKey = local_lookup(key,
				getLocalConfig().getBucketSize());

		/*
		 * This node is possibly the root if no other node in his routing table
		 * is "nearer"
		 */
		return isNodeCloserThanTheOthers(key, closestAroundKey);
	}

	private boolean isNodeCloserThanTheOthers(OverlayKey<?> key,
			Collection<KademliaOverlayContact<T>> closestAroundKey) {
		BigInteger kademliaKeyAsBigInteger = ((KademliaOverlayKey) key)
				.getBigInt();

		// Compute the distance of the this node to the key
		BigInteger myDistance = getLocalOverlayContact().getOverlayID().toKey()
				.getBigInt().xor(kademliaKeyAsBigInteger);

		// Check if there is a node that is "nearer" than this node itself
		for (KademliaOverlayContact<T> contact : closestAroundKey) {
			BigInteger distance = contact.getOverlayID().toKey().getBigInt()
					.xor(kademliaKeyAsBigInteger);
			if (myDistance.compareTo(distance) > 0) {
				return false;
			}
		}

		/*
		 * There was no node that is "nearer", so this node thinks it is the
		 * root
		 */
		return true;
	}

	@Override
	public List<KademliaOverlayContact<T>> local_lookup(KademliaOverlayKey key,
			int num) {

		LinkedList<KademliaOverlayContact<T>> contactList = new LinkedList<KademliaOverlayContact<T>>();

		Set<KademliaOverlayContact<T>> contactSet = getKademliaRoutingTable()
				.localLookup(key, num);

		/*
		 * FIXME: This is just a workaround!
		 * 
		 * The following lines are a hack to be able to route deterministic to
		 * the globally closest peers in the scenario. It seems as if Kademlia
		 * does not guarantee the routing to the global nearest peer in the
		 * overlay.
		 * 
		 * With this lines a node that does not know any closer node (and
		 * therefore would think that it is the root of a key) uses global
		 * knowledge to find the real root.
		 */
		if (isNodeCloserThanTheOthers(key, contactSet)) {
			List<Host> allHosts = GlobalOracle.getHosts();

			BigInteger kademliaKeyAsBigInteger = key
					.getBigInt();

			// Compute the distance of the this node to the key
			BigInteger myDistance = getLocalOverlayContact().getOverlayID()
					.toKey().getBigInt().xor(kademliaKeyAsBigInteger);

			/*
			 * Find the really nearest node to the key with global knowledge
			 */
			BigInteger nearestDistance = myDistance;
			KademliaOverlayContact<T> nearestContact = null;

			for (Host host : allHosts) {
				KBRNode<T, KademliaOverlayContact<T>, KademliaOverlayKey> node = (KBRNode<T, KademliaOverlayContact<T>, KademliaOverlayKey>) host
						.getOverlay(KBRNode.class);

				BigInteger hisDistance = node.getLocalOverlayContact()
						.getOverlayID().toKey().getBigInt().xor(
								kademliaKeyAsBigInteger);

				if (hisDistance.compareTo(nearestDistance) < 0) {
					nearestDistance = hisDistance;
					nearestContact = node
							.getLocalOverlayContact();
				}
			}

			if (nearestDistance.compareTo(myDistance) != 0) {
				contactList.add(nearestContact);
				if (isInRoute) {
					log
							.debug("Use global knowledge to find the nearest peer for a key.");
				}
			} else {
				contactList.addAll(contactSet);
				if (isInRoute) {
					log.debug("Global knowledge was not necessary.");
				}
			}

		} else {
			contactList.addAll(contactSet);
		}

		return contactList;
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
		// inside the interval and the two exclusive borders of the interval
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
	public List<KademliaOverlayContact<T>> replicaSet(KademliaOverlayKey key,
			int maxRank) {
		return local_lookup(key, maxRank);
	}

	private boolean isInRoute = false;

	@Override
	public void route(KademliaOverlayKey key, Message msg,
			KademliaOverlayContact<T> hint) {
		OverlayContact<T> nextHop = null;
		if (hint != null) {
			nextHop = hint;
		} else if (key != null) {
			isInRoute = true;
			nextHop = local_lookup(key, 1).get(0);
			isInRoute = false;
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
	public void hadContactTo(KademliaOverlayContact<T> contact) {
		getKademliaRoutingTable().addContact(
				contact);
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
	public KademliaOverlayContact getOverlayContact(KademliaOverlayID id,
			TransInfo transinfo) {
		return new KademliaOverlayContact<KademliaOverlayID>(id, transinfo);
	}

	@Override
	public KBRLookupProvider<T, KademliaOverlayContact<T>, KademliaOverlayKey> getKbrLookupProvider() {
		return kbrLookupProvider;
	}

}
