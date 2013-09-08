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

package org.peerfact.impl.overlay.dht.kademlia.base;

import java.math.BigInteger;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import org.apache.commons.math.random.RandomAdaptor;
import org.apache.log4j.Logger;
import org.peerfact.api.overlay.dht.DHTObject;
import org.peerfact.api.scenario.Configurable;
import org.peerfact.impl.overlay.dht.kademlia.base.components.AbstractNodeFactory;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayKey;
import org.peerfact.impl.overlay.dht.kademlia.base.components.Node;
import org.peerfact.impl.overlay.dht.kademlia.hkademlia.components.HKademliaOverlayID;
import org.peerfact.impl.simengine.Simulator;
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
 * Generates work that has to be carried out by the nodes in the overlay
 * network. For instance, this class generates data items and random lookup keys
 * for these items.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public class WorkloadGenerator implements Configurable {

	private final static Logger log = SimLogger
			.getLogger(WorkloadGenerator.class);

	/**
	 * A Collection that contains all data items that are available in the
	 * overlay network.
	 */
	private final Map<KademliaOverlayKey, DHTObject> dataItems;

	/**
	 * An array that contains the keys from <code>dataItems</code>.
	 */
	private KademliaOverlayKey[] dataKeys;

	/**
	 * The random generator that is used in this simulation - a copy of
	 * {@link Simulator#getRandom()}.
	 */
	private final Random rnd;

	/**
	 * Configuration values ("constants").
	 */
	private final Config config;

	/**
	 * Constructs a new WorkloadGenerator and gets the configuration information
	 * from {@link KademliaSetup}. The remainder of this constructor works as
	 * {@link #WorkloadGenerator(Config)}.
	 */
	public WorkloadGenerator() {
		this(KademliaSetup.getConfig());
	}

	/**
	 * Constructs a new WorkloadGenerator and builds
	 * {@link WorkloadConfig#NUMBER_OF_DATA_ITEMS} random data items.
	 * 
	 * @param conf
	 *            the Config that contains Kademlia-wide configuration
	 *            constants.
	 */
	public WorkloadGenerator(final Config conf) {
		config = conf;
		rnd = new RandomAdaptor(Simulator.getRandom());
		dataItems = new LinkedHashMap<KademliaOverlayKey, DHTObject>((int) Math
				.ceil(config.getNumberOfDataItems() * 1.002), 0.999f);
		buildDataItems();
	}

	/**
	 * "Callback" to notify this WorkloadGenerator that all Nodes have been
	 * constructed and the NodeFactory has been registered in
	 * {@link KademliaSetup}, to store the data items at the nodes.
	 * 
	 * @param nf
	 *            (not used)
	 */
	public final void setNodeFactory(final AbstractNodeFactory nf) {
		storeDataItemsAtNodes();
	}

	/**
	 * Determines with global knowledge whether the data lookup result as
	 * indicated by the parameters is accurate.
	 * 
	 * @param key
	 *            the KademliaOverlayKey that has been looked up.
	 * @param result
	 *            the DHTObject that has been found (or <code>null</code> if no
	 *            data item has been found).
	 * @return true if this class has <code>result</code> as data item for
	 *         <code>key</code> (or if <code>result==null</code> and there is no
	 *         data item for <code>key</code>).
	 */
	public final boolean isDataLookupResultCorrect(
			final KademliaOverlayKey key, final DHTObject result) {
		if (result == null && !dataItems.containsKey(key)) {
			return true;
		} else if (result != null && dataItems.containsKey(key)
				&& dataItems.get(key).equals(result)) {
			return true;
		}
		return false;
	}

	/**
	 * @return a random KademliaOverlayKey of an existing data item from the
	 *         overlay network.
	 */
	public final KademliaOverlayKey getRandomKeyForLookup() {
		return dataKeys[rnd.nextInt(dataKeys.length)];
	}

	/**
	 * Builds the data items to be stored in the overlay network (but does not
	 * actually store them on the peers).
	 */
	private final void buildDataItems() {
		KademliaOverlayKey key;
		DHTObject data;
		while (dataItems.size() < config.getNumberOfDataItems()) {
			key = constructRandomKademliaOverlayKey();
			data = new DHTObject() {
				@Override
				public long getTransmissionSize() {
					return 0;
				}
				// no implementation...
			};
			dataItems.put(key, data);
		}
		// store the keys in an extra array to allow (efficient) random access
		dataKeys = dataItems.keySet().toArray(new KademliaOverlayKey[] {});
		log.info("Built " + config.getNumberOfDataItems()
				+ " data items for the Kademlia overlay network.");
	}

	/**
	 * @return a new, random KademliaOverlayKey.
	 */
	private final KademliaOverlayKey constructRandomKademliaOverlayKey() {
		final int bits = config.getIDLength();
		final BigInteger id = new BigInteger(bits, rnd);
		return new KademliaOverlayKey(id, config);
	}

	/**
	 * Stores all data items at the nodes that are responsible to save them.
	 * This method should be executed if all Nodes have been constructed because
	 * it needs knowledge of all nodes in order to calculate which peer is
	 * responsible to store a given data item. That is, responsibility is
	 * calculated by taking into account the overall list of all existing peers
	 * (no matter their online status).
	 */
	private final void storeDataItemsAtNodes() {
		Collection<Node<HKademliaOverlayID>> kClosestNodes;
		log.info("Storing data items at responsible nodes...");

		for (final Map.Entry<KademliaOverlayKey, DHTObject> item : dataItems
				.entrySet()) {
			// fetch nodes that are responsible to save entry.getValue()
			kClosestNodes = KademliaSetup.getNodeFactory().getKClosestNodes(
					item.getKey());
			// store data on these nodes
			for (final Node<HKademliaOverlayID> currentNode : kClosestNodes) {
				currentNode.getLocalIndex().putInitialDataItem(item.getKey(),
						item.getValue());
			}
		}
		log.info("All data items have been stored at the responsible nodes.");
	}
}
