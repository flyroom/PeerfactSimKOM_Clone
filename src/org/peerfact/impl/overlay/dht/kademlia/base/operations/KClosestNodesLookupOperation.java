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

import java.math.BigInteger;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.peerfact.api.common.OperationCallback;
import org.peerfact.impl.overlay.dht.kademlia.base.KademliaSetup;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayContact;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayID;
import org.peerfact.impl.overlay.dht.kademlia.base.components.Node;
import org.peerfact.impl.overlay.dht.kademlia.base.messages.KClosestNodesLookupMsg;
import org.peerfact.impl.overlay.dht.kademlia.base.messages.KademliaMsg;
import org.peerfact.impl.overlay.dht.kademlia.base.operations.LookupCoordinator.NonhierarchicalLookupCoordinator;
import org.peerfact.impl.overlay.dht.kademlia.base.operations.LookupCoordinatorClient.NonhierarchicalLookupCoordinatorClient;
import org.peerfact.impl.overlay.dht.kademlia.base.operations.OperationFactory.NodeLookupOperation;


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
 * An operation that looks up the {@link OperationsConfig#getBucketSize()}
 * closest nodes around a given key (the key is held by the lookup coordinator).
 * It has to be customised with a LookupCoordinator that adapts the lookup
 * process to a certain flavour of Kademlia (standard, Kandy, or hierarchical).
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public class KClosestNodesLookupOperation<T extends KademliaOverlayID> extends
		AbstractLookupOperation<List<KademliaOverlayContact<T>>, T> implements
		NonhierarchicalLookupCoordinatorClient<T>,
		NodeLookupOperation<List<KademliaOverlayContact<T>>, T> {

	/**
	 * Constructs a new node lookup operation.
	 * 
	 * @param coordinator
	 *            the NonhierarchicalLookupCoordinator that implements the
	 *            details of the lookup process in a certains flavour of
	 *            Kademlia (for example standard Kademlia, Kandy, or
	 *            hierarchical Kademlia).
	 * @param node
	 *            the Node that initiates this lookup.
	 * @param opCallback
	 *            a callback that is informed when this operation terminates.
	 * @param reason
	 *            why this operation is to be executed - either user-initiated
	 *            or for maintenance.
	 * @param conf
	 *            an OperationsConfig reference that permits to retrieve
	 *            configuration "constants".
	 */
	public KClosestNodesLookupOperation(
			final NonhierarchicalLookupCoordinator<T> coordinator,
			final Node<T> node, final OperationCallback<?> opCallback,
			final Reason reason, final OperationsConfig conf) {
		super(coordinator, node, opCallback, reason, conf);
		coordinator.setClient(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Set<KademliaOverlayContact<T>> getNodes() {
		return coordinator.getCurrentlyKnownKClosestNodes();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean isContinuationNecessary() {
		if (isFinished()) {
			return false; // operation has been stopped
		}
		return true; // continue lookup as long as possible
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void coordinatorFinished() {

		finishOperation(OperationState.SUCCESS); // node lookup "cannot" fail
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void operationJustFinished() {
		// log.debug("K closest nodes lookup complete (key="
		// + coordinator.getKey() + "; |result|=" + getNodes().size()
		// + "; node=" + getComponent() + "; how=" + getState()); // TODO
		// uncomment
		if (KademliaSetup.getMonitor() != null) {
			KademliaSetup.getMonitor().kClosestNodesLookupCompleted(
					coordinator.getKey(), getNodes(), this);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void sendLookupMessage(
			final KademliaOverlayContact<T> destination) {
		final KademliaMsg<T> message = new KClosestNodesLookupMsg<T>(
				getComponent().getTypedOverlayID(), destination.getOverlayID(),
				coordinator.getKey(), getReason(), config);
		sendAndWait(message, destination);
	}

	@Override
	public List<KademliaOverlayContact<T>> getResult() {

		LinkedList<KademliaOverlayContact<T>> nodes = new LinkedList<KademliaOverlayContact<T>>(
				getNodes());
		final BigInteger keyLookedUp = coordinator.getKey().getBigInt();

		/*
		 * Do a sorting of the contacts according to their distance to the key
		 */
		Comparator<KademliaOverlayContact<T>> comp = new Comparator<KademliaOverlayContact<T>>() {
			@Override
			public int compare(KademliaOverlayContact<T> o1,
					KademliaOverlayContact<T> o2) {

				BigInteger dist1 = keyLookedUp.xor(o1.getOverlayID()
						.getBigInt());
				BigInteger dist2 = keyLookedUp.xor(o2.getOverlayID()
						.getBigInt());

				return dist1.compareTo(dist2);
			}
		};
		Collections.sort(nodes, comp);

		return nodes;
	}
}
