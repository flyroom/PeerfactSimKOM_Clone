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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.math.random.RandomAdaptor;
import org.apache.log4j.Logger;
import org.peerfact.api.common.Component;
import org.peerfact.api.common.ComponentFactory;
import org.peerfact.api.common.Host;
import org.peerfact.impl.overlay.AbstractOverlayNode.PeerStatus;
import org.peerfact.impl.overlay.dht.kademlia.base.Config;
import org.peerfact.impl.overlay.dht.kademlia.base.KademliaSetup;
import org.peerfact.impl.overlay.dht.kademlia.hkademlia.components.HKademliaOverlayID;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.util.logging.SimLogger;
import org.peerfact.impl.util.toolkits.KSmallestMap;
import org.peerfact.impl.util.toolkits.KSortedLookupList;
import org.peerfact.impl.util.toolkits.Predicate;
import org.peerfact.impl.util.toolkits.Comparators.KademliaOverlayIDXORMaxComparator;


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
 * Superclass for Kademlia node factories. Subclasses have to implement
 * construction of concrete Nodes.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public abstract class AbstractNodeFactory implements ComponentFactory {

	private final static Logger log = SimLogger
			.getLogger(AbstractNodeFactory.class);

	/**
	 * The local port on which Kademlia nodes listen for incoming messages.
	 */
	private static final short LOCAL_KADEMLIA_PORT = 1605;

	/**
	 * A Predicate that holds for all Nodes that are not ABSENT.
	 */
	private static final Predicate<Node<?>> notAbsent = new Predicate<Node<?>>() {

		@Override
		public final boolean isTrue(final Node<?> peer) {
			return peer.getPeerStatus() != PeerStatus.ABSENT;
		}

	};

	/**
	 * The random generator that is used in this simulation - a copy of
	 * {@link Simulator#getRandom()}.
	 */
	protected final Random rnd;

	/**
	 * Configuration values ("constants").
	 */
	protected final Config config;

	/**
	 * Map of all Nodes that have been created so far (mapped by their ID).
	 */
	private final Map<HKademliaOverlayID, Node<HKademliaOverlayID>> constructedNodes;

	/**
	 * A List that contains the constructed Nodes for efficient random access.
	 */
	private final List<Node<HKademliaOverlayID>> constructedNodeValues;

	/**
	 * Constructs a new AbstractNodeFactory, the necessary constants are read
	 * from {@link KademliaSetup}.
	 */
	public AbstractNodeFactory() {
		config = KademliaSetup.getConfig();
		this.rnd = new RandomAdaptor(Simulator.getRandom());

		final int approxSize = (int) Math
				.ceil(config.getNumberOfPeers() * 1.02);
		constructedNodes = new LinkedHashMap<HKademliaOverlayID, Node<HKademliaOverlayID>>(
				approxSize, 0.99f);
		constructedNodeValues = new ArrayList<Node<HKademliaOverlayID>>(
				approxSize);
	}

	/*
	 * Methods to construct Nodes
	 */

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Component createComponent(final Host host) {
		final Node<HKademliaOverlayID> newNode;
		HKademliaOverlayID id;

		// make sure that no two peers use the same ID (although quite unlikely)
		do {
			id = getRandomHKademliaOverlayID(host);
		} while (constructedNodes.containsKey(id));
		newNode = buildNode(id, LOCAL_KADEMLIA_PORT, host);
		constructedNodes.put(newNode.getTypedOverlayID(), newNode);
		constructedNodeValues.add(newNode);

		log.debug("Built node " + newNode);

		return newNode;
	}

	/**
	 * Constructs a new random ID.
	 * 
	 * @param host
	 *            the Host on which the Node will run - can be used to retrieve
	 *            scenario information.
	 * @return a new random HKademliaOverlayID.
	 */
	protected HKademliaOverlayID getRandomHKademliaOverlayID(final Host host) {
		final int bits = config.getIDLength();
		final BigInteger id = new BigInteger(bits, rnd);
		return new HKademliaOverlayID(id, config);
	}

	/**
	 * Constructs a new Node with the given initialisation data.
	 * 
	 * @param id
	 *            the HKademliaOverlayID of the new Node.
	 * @param port
	 *            the port on which the new Node will listen for incoming
	 *            messages.
	 * @param host
	 *            the Host on which the new Node will run.
	 * @return a new Node with the given HKademliaOverlayID, port, and transport
	 *         layer.
	 */
	protected abstract Node<HKademliaOverlayID> buildNode(
			HKademliaOverlayID id, short port, Host host);

	/*
	 * Methods to store initial routing table contents
	 */

	/**
	 * "Callback" to inform this factory that all nodes have been constructed.
	 * Further steps that rely on that fact will be executed: all Nodes get
	 * their initial routing table contents.
	 * <p>
	 * <i>This method should be called only once and only if all Nodes have been
	 * constructed!</i>
	 * 
	 * @param unused
	 *            dummy parameter to enable a use of the method in the config
	 *            file
	 */
	public final void setTriggerInitialRTBuild(String unused) {
		storeInitialRoutingTableContents();
	}

	private void storeInitialRoutingTableContents() {
		log.info("Storing initial routing table contents at nodes...");

		Collection<KademliaOverlayContact<HKademliaOverlayID>> initRTContents;
		for (final Node<HKademliaOverlayID> currentNode : constructedNodeValues) {
			initRTContents = getRandomInitialRoutingTableContents(currentNode);
			currentNode.addContactsToRoutingTable(initRTContents);
		}

		log.info("Initial routing table contents has been set at all nodes.");
	}

	/**
	 * Determines the initial routing table contents for Node
	 * <code>target</code>.
	 * 
	 * @param target
	 *            the Node that will receive the routing table contents.
	 * @return a Collection that contains random KademliaOverlayContacts to be
	 *         used as initial routing table contents of a Node.
	 */
	/*
	 * This base implementation determines the routing table contents randomly &
	 * independent of the target Node.
	 */
	protected Collection<KademliaOverlayContact<HKademliaOverlayID>> getRandomInitialRoutingTableContents(
			final Node<HKademliaOverlayID> target) {
		return getRandomInitialRoutingTableContentsFromList(constructedNodeValues);
	}

	/**
	 * Determines the initial routing table contents for a Node according to the
	 * List of candidate entries <code>candidates</code>.
	 * 
	 * @param candidates
	 *            a List from which the routing table contents will be chosen at
	 *            random.
	 * @return a Collection that contains random KademliaOverlayContacts to be
	 *         used as initial routing table contents of a Node.
	 */
	protected final Collection<KademliaOverlayContact<HKademliaOverlayID>> getRandomInitialRoutingTableContentsFromList(
			final List<Node<HKademliaOverlayID>> candidates) {
		final int numOfContacts = Math.min(config
				.getNumberOfInitialRoutingTableContacts(), candidates.size());
		final Set<KademliaOverlayContact<HKademliaOverlayID>> result = new LinkedHashSet<KademliaOverlayContact<HKademliaOverlayID>>(
				(int) Math.ceil(numOfContacts * 1.2), 0.9f);
		Node<HKademliaOverlayID> randomNode;

		while (result.size() < numOfContacts) {
			randomNode = candidates.get(rnd.nextInt(candidates.size()));
			result.add(randomNode.getLocalContact());
		}

		return result;
	}

	/*
	 * Methods for evaluation/measurement purposes
	 */

	/**
	 * Determines whether the given Node is currently ABSENT.
	 * 
	 * @param peer
	 *            the KademliaOverlayID of the Node.
	 * @return whether the Node is ABSENT.
	 */
	public final boolean isOffline(final KademliaOverlayID peer) {
		return constructedNodes.get(peer).getPeerStatus() == PeerStatus.ABSENT;
	}

	/**
	 * Determines whether the given peer has a data item with the given key in
	 * its local database.
	 * 
	 * @param peer
	 *            the KademliaOverlayID of the peer.
	 * @param key
	 *            the KademliaOverlayKey of the data item.
	 * @return true if the peer has the data item.
	 */
	public final boolean hasDataItem(final KademliaOverlayID peer,
			final KademliaOverlayKey key) {
		return constructedNodes.get(peer).getLocalIndex().get(key) != null;
	}

	/**
	 * Determines the {@link KademliaConfig#K} Node-IDs that are closest to
	 * <code>key</code> and are online/going online (not PeerStatus.ABSENT) at
	 * the point in time of invocation of this method.
	 * 
	 * @param key
	 *            the KademliaOverlayKey which is to be looked up.
	 * @return a Collection with the K HKademliaOverlayIDs of nodes that are
	 *         closest to <code>key</code>. These are guaranteed to be
	 *         online/going online.
	 */
	public final Set<HKademliaOverlayID> getKClosestOnlineIDs(
			final KademliaOverlayKey key) {
		final Comparator<HKademliaOverlayID> xorToKey;
		final KSortedLookupList<HKademliaOverlayID, Node<HKademliaOverlayID>> kClosestFilteredNodes;

		xorToKey = new KademliaOverlayIDXORMaxComparator<HKademliaOverlayID>(
				key.getBigInt());
		kClosestFilteredNodes = new KSmallestMap<HKademliaOverlayID, Node<HKademliaOverlayID>>(
				config.getBucketSize(), xorToKey);
		kClosestFilteredNodes.putAll(constructedNodes, null, notAbsent);
		return kClosestFilteredNodes.keySet();
	}

	/**
	 * Returns all IDs that are closer to key than ref.
	 * 
	 * @param ref
	 *            a reference HKademliaOverlayID.
	 * @param key
	 *            the KademliaOverlayKey according to which the distance is
	 *            calculated.
	 * @return a Collection with all HKademliaOverlayIDs of peers that are
	 *         closer to key than ref.
	 */
	public final Collection<HKademliaOverlayID> getCloserIDs(
			final HKademliaOverlayID ref, final KademliaOverlayKey key) {
		final List<HKademliaOverlayID> closer;
		final Comparator<HKademliaOverlayID> xorToKey;

		closer = new ArrayList<HKademliaOverlayID>(config.getBucketSize() * 2);
		xorToKey = new KademliaOverlayIDXORMaxComparator<HKademliaOverlayID>(
				key.getBigInt());

		for (final HKademliaOverlayID candidate : constructedNodes.keySet()) {
			if (xorToKey.compare(candidate, ref) < 0) {
				closer.add(candidate);
			}
		}

		return closer;
	}

	/**
	 * Determines how many peers have the data item associated with key.
	 * 
	 * @param key
	 *            the KademliaOverlayKey of the data item.
	 * @return an array with the total number of peers that have the data as
	 *         first entry and the number of online peers that have it as second
	 *         entry.
	 */
	public final int[] numberOfPeersWithData(final KademliaOverlayKey key) {
		int peersWithData = 0, onlinePeersWithData = 0;

		for (final Node<HKademliaOverlayID> peer : constructedNodeValues) {
			if (peer.getLocalIndex().get(key) != null) {
				peersWithData++;
				if (peer.getPeerStatus() != PeerStatus.ABSENT) {
					onlinePeersWithData++;
				}
			}
		}

		return new int[] { peersWithData, onlinePeersWithData };
	}

	/*
	 * Method for WorkloadGenerator
	 */

	/**
	 * Determines the {@link KademliaConfig#K} Nodes that are closest to
	 * <code>key</code>.
	 * 
	 * @param key
	 *            the KademliaOverlayKey which is to be looked up.
	 * @return a Collection with the K Nodes that are closest to
	 *         <code>key</code>. These need not all be online.
	 */
	/*
	 * Note that the underlying KSmallestMap in getKClosestFilteredNodes can be
	 * garbage collected only when the result returned here is no longer in use!
	 */
	public final Collection<Node<HKademliaOverlayID>> getKClosestNodes(
			final KademliaOverlayKey key) {
		final Comparator<HKademliaOverlayID> xorToKey;
		final KSortedLookupList<HKademliaOverlayID, Node<HKademliaOverlayID>> kClosestFilteredNodes;

		xorToKey = new KademliaOverlayIDXORMaxComparator<HKademliaOverlayID>(
				key.getBigInt());
		kClosestFilteredNodes = new KSmallestMap<HKademliaOverlayID, Node<HKademliaOverlayID>>(
				config.getBucketSize(), xorToKey);
		kClosestFilteredNodes.putAll(constructedNodes);
		return kClosestFilteredNodes.values();
	}

}
