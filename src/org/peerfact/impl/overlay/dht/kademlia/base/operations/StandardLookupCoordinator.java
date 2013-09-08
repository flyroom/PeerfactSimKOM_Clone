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

package org.peerfact.impl.overlay.dht.kademlia.base.operations;

import java.util.Set;

import org.peerfact.impl.overlay.dht.kademlia.base.components.AbstractKademliaNode;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayContact;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayID;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayKey;
import org.peerfact.impl.overlay.dht.kademlia.base.components.Node;
import org.peerfact.impl.overlay.dht.kademlia.base.components.RoutingTable;
import org.peerfact.impl.overlay.dht.kademlia.base.operations.LookupCoordinator.NonhierarchicalLookupCoordinator;
import org.peerfact.impl.overlay.dht.kademlia.base.operations.LookupCoordinatorClient.NonhierarchicalLookupCoordinatorClient;


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
 * Lookup coordinator for standard Kademlia lookups. The lookup coordinator
 * keeps a list with the K={@link OperationsConfig#getBucketSize()} currently
 * known closest nodes to {@link LookupCoordinatorClient#getA()} and
 * concurrently queries the {@link OperationsConfig#getMaxConcurrentLookups()}
 * closest ones of them that have not yet been queried. As lookup replies are
 * received, the new contacts are inserted into the list and more distant
 * contacts are removed so that at most <code>K</code> contacts are saved.
 * Further lookup messages are sent with the constraint that no more than
 * <code>ALPHA</code> messages are in transit at the same time.
 * <p>
 * The lookup stops if
 * <ul>
 * <li>(a) the lookup coordinator client returns
 * <code>{@link LookupCoordinatorClient#isContinuationNecessary()}==false</code>
 * or
 * <li>(b) all currently known <code>K</code> closest nodes to the
 * <code>key</code> have been queried and all replies (or timeouts) have been
 * received (that is, there is no possibility to continue the lookup).
 * </ul>
 * 
 * This lookup coordinator does not consider clusters (it works as in standard
 * Kademlia).
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public class StandardLookupCoordinator<T extends KademliaOverlayID> extends
		AbstractLookupCoordinator<T> implements
		NonhierarchicalLookupCoordinator<T> {

	/**
	 * The client that uses this LookupCoordinator (used for callbacks).
	 */
	private NonhierarchicalLookupCoordinatorClient<T> client;

	/**
	 * The Node that has initiated this lookup.
	 */
	protected final Node<T> node;

	/**
	 * Constructs a new standard lookup coordinator.
	 * 
	 * @param lookupKey
	 *            the KademliaOverlayKey that is to be looked up.
	 * @param node
	 *            the Node that initiates this lookup.
	 * @param conf
	 *            an OperationsConfig reference that permits to retrieve
	 *            configuration "constants".
	 */
	public StandardLookupCoordinator(final KademliaOverlayKey lookupKey,
			final Node<T> node, final OperationsConfig conf) {
		super(lookupKey, conf);
		this.node = node;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void setClient(
			final NonhierarchicalLookupCoordinatorClient<T> client) {
		this.client = client;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final NonhierarchicalLookupCoordinatorClient<T> getClient() {
		return client;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final RoutingTable<T> getRoutingTable() {
		return node.getKademliaRoutingTable();
	}

	/*
	 * Internal methods (lookup coordination)
	 */

	/**
	 * Initialises the lookup: get the k closest nodes from the routing table,
	 * insert them into <code>kClosestNodes</code>, and mark all its entries as
	 * TO_QUERY.
	 */
	@Override
	protected final void init() {
		final Set<KademliaOverlayContact<T>> contactsFromRT;

		// find new contacts from routing table
		contactsFromRT = localLookup(getKey());

		// insert all contacts as TO_QUERY
		getkClosestNodes().putAll(contactsFromRT, ContactState.TO_QUERY, true);
		/*
		 * Insert own contact as QUERIED. This makes sure that the own contact
		 * is never queried via the network. (If the own contact is too distant
		 * from the target to be of interest, it will be dropped.)
		 */
		getkClosestNodes().put(node.getLocalContact(), ContactState.QUERIED);
	}

	/**
	 * Looks up {@link OperationsConfig#getBucketSize()} contacts from the local
	 * routing table.
	 * 
	 * @param key
	 *            the KademliaOverlayKey to be looked up.
	 * @return a Set containing K contacts.
	 */
	protected Set<KademliaOverlayContact<T>> localLookup(
			final KademliaOverlayKey key) {
		int localBucketSize = ((AbstractKademliaNode<T>) getNode())
				.getLocalConfig().getBucketSize();

		return node.getKademliaRoutingTable().localLookup(key, localBucketSize);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void proceed(final int numOfMessages) {
		KademliaOverlayContact<T> bestUnqueried;

		// send up to numOfMessages messages
		for (int i = 1; i <= numOfMessages; i++) {
			/*
			 * Select "best" contact that has not already been queried, and if
			 * such a contact exists, send a message. Else stop.
			 */
			bestUnqueried = getBestUnqueried();
			if (bestUnqueried != null) {
				getkClosestNodes().put(bestUnqueried, ContactState.QUERIED);
				client.sendLookupMessage(bestUnqueried);
			} else {
				return;
			}
		}
	}

	@Override
	public Node<T> getNode() {
		return node;
	}

}
