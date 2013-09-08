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
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayKey;
import org.peerfact.impl.overlay.dht.kademlia.base.components.Node;
import org.peerfact.impl.overlay.dht.kademlia.base.components.Node.HierarchyRestrictableNode;
import org.peerfact.impl.overlay.dht.kademlia.base.components.RoutingTable.HierarchyRestrictableRoutingTable;
import org.peerfact.impl.overlay.dht.kademlia.base.operations.LookupCoordinator.HierarchicalLookupCoordinator;
import org.peerfact.impl.overlay.dht.kademlia.base.operations.LookupCoordinatorClient.HierarchicalLookupCoordinatorClient;
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
 * A round-based, hierarchy-enabled lookup coordinator. It proceeds in rounds,
 * that is, in each round, the lookup results are restricted to a common cluster
 * depth with the initiator. If one round does not return a sufficient result,
 * the next round relaxes the cluster depth restriction by one. Moving from one
 * round to another means that some contacts that have been queried in the first
 * round might be queried again; this is necessary as they might now return
 * additional contacts.
 * <p>
 * A second important point with this coordinator is that a lookup always
 * terminates the round that has currently been begun, even if the client does
 * not require the lookup to continue. This is because this coordinator is
 * mainly intended for bucket lookups, where the client only needs k contacts
 * with a certain prefix, but we want to finish the round so that the overlay
 * network gets more stable (by other peers seeing this contact).
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public class RoundbasedLookupCoordinator<H extends HKademliaOverlayID> extends
		AbstractLookupCoordinator<H> implements
		HierarchicalLookupCoordinator<H> {

	/**
	 * The client that uses this LookupCoordinator (used for callbacks).
	 */
	private HierarchicalLookupCoordinatorClient<H> client;

	/**
	 * The HKademliaNode that has initiated this lookup.
	 */
	protected final HierarchyRestrictableNode<H> node;

	/**
	 * The depth of the currently running round. 0 is the depth of the cluster
	 * that contains all nodes (that is, the last possible depth).
	 */
	private int currentDepth;

	/**
	 * Constructs a new round-based, hierarchy-enabled lookup coordinator.
	 * <p>
	 * This coordinator proceeds in rounds, where each round restricts the
	 * lookup results to contacts that have at least some common cluster depth
	 * in common with the node's own ID. If no satisfying result has been found
	 * in one round, this restriction is relaxed by one, that is in the next
	 * round, contacts from the next higher level in the cluster hierarchy may
	 * also be considered in this lookup. The last round has a depth of zero,
	 * that is all contacts are allowed. This way of proceeding guarantees that
	 * if a result can be found in a lower (more specialised) cluster, it will
	 * be returned by this lookup operation. It furthermore guarantees that this
	 * lookup returns no result if and only if no result can be found in the
	 * whole identifier space (that is, without any cluster restriction).
	 * <i>Please note that this guarantee only refers to the impact of cluster
	 * restriction. In general, there is always some probability that a key
	 * cannot be found with a lookup from certain nodes although it is present
	 * in the network.</i>
	 * 
	 * @param lookupKey
	 *            the KademliaOverlayKey that is to be looked up.
	 * @param node
	 *            the HierarchicalNode that initiates this lookup.
	 * 
	 * @param initialDepth
	 *            the depth of the first round of this lookup. The depth of a
	 *            round restricts the contacts that are considered to those that
	 *            have at least the given common cluster depth with the
	 *            initiator. It should be a positive number bigger than or equal
	 *            to zero. Zero is the depth of the cluster that contains all
	 *            nodes, hence setting initialDepth to zero means that only one
	 *            round is carried out (hierarchy disabled).
	 * @param conf
	 *            an OperationsConfig reference that permits to retrieve
	 *            configuration "constants".
	 */
	public RoundbasedLookupCoordinator(final KademliaOverlayKey lookupKey,
			final HierarchyRestrictableNode<H> node, final int initialDepth,
			final OperationsConfig conf) {
		super(lookupKey, conf);
		this.node = node;
		this.currentDepth = initialDepth;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void setClient(
			final HierarchicalLookupCoordinatorClient<H> client) {
		this.client = client;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final HierarchicalLookupCoordinatorClient<H> getClient() {
		return client;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final HierarchyRestrictableRoutingTable<H> getRoutingTable() {
		return node.getKademliaRoutingTable();
	}

	/**
	 * Initialises a lookup round: get the k closest nodes from the routing
	 * table that match <code>currentDepth</code>, insert them into
	 * <code>kClosestNodes</code> (maybe replacing more distant contacts), and
	 * mark all its entries as TO_QUERY.
	 */
	@Override
	protected final void init() {
		final Set<KademliaOverlayContact<H>> contactsFromRT;

		int localBucketSize = ((AbstractKademliaNode<H>) getNode())
				.getLocalConfig().getBucketSize();

		// find new contacts from routing table (new cluster)
		contactsFromRT = getRoutingTable().localLookup(getKey(),
				localBucketSize, currentDepth, node.getTypedOverlayID());
		insertBestContacts(contactsFromRT);

		// mark all (existing) contacts as TO_QUERY
		getkClosestNodes().setAllValues(ContactState.TO_QUERY);
		/*
		 * Insert own contact as QUERIED. This makes sure that the own contact
		 * is never queried via the network. (If the own contact is too distant
		 * from the target to be of interest, it will be dropped.)
		 */
		getkClosestNodes().put(node.getLocalContact(), ContactState.QUERIED);
	}

	/**
	 * Send up to <code>numOfMessages</code> lookup messages to proceed in the
	 * lookup process. If no more contacts are available to be sent queries at
	 * the current depth, the next round is started.
	 * 
	 * @param numOfMessages
	 *            the number of messages that may be sent. The method is free to
	 *            send less, but not more than <code>numOfMessages</code>
	 *            messages.
	 */
	@Override
	protected final void proceed(final int numOfMessages) {
		KademliaOverlayContact<H> bestUnqueried;

		// send up to numOfMessages messages
		for (int i = 1; i <= numOfMessages; i++) {
			bestUnqueried = getBestUnqueried();
			if (bestUnqueried != null) {
				/*
				 * Send a message to the currently "best" available contact.
				 */
				getkClosestNodes().put(bestUnqueried, ContactState.QUERIED);
				client.sendLookupMessage(bestUnqueried, currentDepth);
			} else if (currentDepth > 0 && client.getTransitCount() == 0) {
				/*
				 * There are no more contacts available at the current depth, so
				 * continue at the next higher level. But make sure that all
				 * queries have returned (else new contacts for this depth might
				 * still arrive).
				 */
				currentDepth--;
				init();
				i--; // cheat to do one more loop (no message sent this time)
			} else {
				/*
				 * Currently no contact to query and no possibility to switch to
				 * a higher level (either already at level 0 or we need to wait
				 * for remaining queries to return), so just wait.
				 */
				return;
			}
		}
	}

	/**
	 * Application specific check whether the lookup has already completed.
	 * 
	 * The lookup is complete if the current round is complete (all nodes have
	 * been queried and there are no outstanding queries) <b>and</b> if
	 * <ul>
	 * <li>(a) the lookup coordinator client has determined that it is not
	 * necessary to continue the lookup, or
	 * <li>(b) we have just completed the last round at depth zero (thus there
	 * are no contacts available to continue the query at a higher level).</li>
	 * </ul>
	 * 
	 * @return whether the operation is finished (true) or still in progress
	 *         (false).
	 */
	@Override
	public final boolean isFinishedAppSpecific() {
		final boolean roundFinished = getClient().getTransitCount() == 0
				&& !this.getkClosestNodes()
						.containsValue(ContactState.TO_QUERY);
		final boolean clientSatisfied = !getClient().isContinuationNecessary();
		final boolean inLastRound = currentDepth == 0;

		/*
		 * Can only finish if one round has been completed. Then we are finished
		 * if either the client is satisfied, or if we have just finished the
		 * last round (thus cannot continue anyway).
		 */
		if (roundFinished && (clientSatisfied || inLastRound)) {
			return true;
		}
		return false;
	}

	@Override
	public Node<H> getNode() {
		return node;
	}

	/*
	 * TODO: override insertBestContacts to remove all contacts that do not
	 * fulfil the cluster depth restriction??
	 */

}
