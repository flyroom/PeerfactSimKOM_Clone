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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.peerfact.api.common.OperationCallback;
import org.peerfact.impl.overlay.dht.kademlia.base.components.AbstractKademliaNode;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayContact;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayID;
import org.peerfact.impl.overlay.dht.kademlia.base.components.Node;
import org.peerfact.impl.overlay.dht.kademlia.base.messages.KClosestNodesLookupMsg;
import org.peerfact.impl.overlay.dht.kademlia.base.messages.KademliaMsg;
import org.peerfact.impl.overlay.dht.kademlia.base.messages.NodeListMsg;
import org.peerfact.impl.overlay.dht.kademlia.base.operations.LookupCoordinator.HierarchicalLookupCoordinator;
import org.peerfact.impl.overlay.dht.kademlia.base.operations.LookupCoordinator.NonhierarchicalLookupCoordinator;
import org.peerfact.impl.overlay.dht.kademlia.hkademlia.components.HKademliaOverlayID;
import org.peerfact.impl.overlay.dht.kademlia.hkademlia.messages.HKClosestNodesLookupMsg;


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
 * An operation that looks up at least {@link OperationsConfig#getBucketSize()}
 * contacts that can be filled into the bucket denoted by a given key (the key
 * is held by the lookup coordinator) and the bucket depth. It has to be
 * customised with a LookupCoordinator that adapts the lookup process to a
 * certain flavour of Kademlia (standard, Kandy, or hierarchical). This
 * operation is hierarchy-enabled, that is, it restricts contacts returned as a
 * reply to lookup messages to a cluster depth as instructed by the lookup
 * coordinator.
 * 
 * This operation has reached its goal as soon as K valid contacts have been
 * found (valid means that they fit into the given bucket). However, it depends
 * on the lookup coordinator whether the lookup immediately halts or continues.
 * For instance, it may continue until the K closest nodes around the given key
 * have been found, or until a complete lookup has been performed for the
 * current cluster depth in a hierarchical lookup.
 * 
 * As all contacts seen during a lookup are automatically inserted into the
 * routing table, this operation does not offer a method to retrieve the K
 * contacts that have been looked up.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public abstract class BucketLookupOperation<T extends KademliaOverlayID>
		extends AbstractLookupOperation<List<KademliaOverlayContact<T>>, T> {

	/**
	 * Hierarchical bucket lookup operation (supports cluster restriction).
	 * 
	 * @see BucketLookupOperation
	 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
	 */
	public static class HierarchicalBucketLookupOperation
			extends BucketLookupOperation<HKademliaOverlayID> implements
			HierarchicalLookupCoordinatorClient<HKademliaOverlayID> {

		/**
		 * Constructs a new hierarchical bucket lookup operation that is enabled
		 * to send cluster-restricted lookup messages.
		 * 
		 * @param bucketDepth
		 *            the depth of the bucket for which contacts have to be
		 *            found. 0 is the depth of the root bucket. The bucket depth
		 *            determines the length of the prefix of the lookup key that
		 *            is relevant, or in other words, the length of the suffix
		 *            of the key that is arbitrary. It is used to find out which
		 *            of the seen contacts can be saved in that bucket. The
		 *            length of the relevant prefix is the bucket depth times
		 *            the split factor of the routing tree (
		 *            {@link OperationsConfig#getRoutingTreeOrder()}). The
		 *            length of the arbitrary suffix is the identifier length
		 *            {@link OperationsConfig#getIDLength()} minus the length of
		 *            the relevant prefix.
		 * 
		 * @param coordinator
		 *            the HierarchicalLookupCoordinator that implements the
		 *            details of the lookup process in a certains flavour of
		 *            Kademlia (for example standard Kademlia, Kandy, or
		 *            hierarchical Kademlia).
		 * @param node
		 *            the Node that initiates this lookup.
		 * 
		 * @param opCallback
		 *            a callback that is informed when this operation
		 *            terminates.
		 * @param reason
		 *            why this operation is to be executed - either
		 *            user-initiated or for maintenance.
		 * @param conf
		 *            an OperationsConfig reference that permits to retrieve
		 *            configuration "constants".
		 */
		public HierarchicalBucketLookupOperation(
				final int bucketDepth,
				final HierarchicalLookupCoordinator<HKademliaOverlayID> coordinator,
				final Node<HKademliaOverlayID> node,
				final OperationCallback<?> opCallback,
				final Reason reason, final OperationsConfig conf) {
			super(bucketDepth, coordinator, node, opCallback, reason, conf);
			coordinator.setClient(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final void sendLookupMessage(
				final KademliaOverlayContact<HKademliaOverlayID> destination,
				final int minClusterDepth) {
			final KademliaMsg<HKademliaOverlayID> message = new HKClosestNodesLookupMsg(
					getComponent().getTypedOverlayID(),
					destination.getOverlayID(), coordinator.getKey(),
					minClusterDepth, getReason(), config);
			sendAndWait(message, destination);
		}

	}

	/**
	 * Non-hierarchical bucket lookup operation.
	 * 
	 * @see BucketLookupOperation
	 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
	 */
	public static class NonhierarchicalBucketLookupOperation<T extends KademliaOverlayID>
			extends BucketLookupOperation<T> implements
			NonhierarchicalLookupCoordinatorClient<T> {

		/**
		 * Constructs a new non-hierarchical bucket lookup operation that sends
		 * standard lookup messages.
		 * 
		 * @param bucketDepth
		 *            the depth of the bucket for which contacts have to be
		 *            found. 0 is the depth of the root bucket. The bucket depth
		 *            determines the length of the prefix of the lookup key that
		 *            is relevant, or in other words, the length of the suffix
		 *            of the key that is arbitrary. It is used to find out which
		 *            of the seen contacts can be saved in that bucket. The
		 *            length of the relevant prefix is the bucket depth times
		 *            the split factor of the routing tree (
		 *            {@link OperationsConfig#getRoutingTreeOrder()}). The
		 *            length of the arbitrary suffix is the identifier length
		 *            {@link OperationsConfig#getIDLength()} minus the length of
		 *            the relevant prefix.
		 * 
		 * @param coordinator
		 *            the NonhierarchicalLookupCoordinator that implements the
		 *            details of the lookup process in a certains flavour of
		 *            Kademlia (for example standard Kademlia, Kandy, or
		 *            hierarchical Kademlia).
		 * @param node
		 *            the Node that initiates this lookup.
		 * 
		 * @param opCallback
		 *            a callback that is informed when this operation
		 *            terminates.
		 * @param reason
		 *            why this operation is to be executed - either
		 *            user-initiated or for maintenance.
		 * @param conf
		 *            an OperationsConfig reference that permits to retrieve
		 *            configuration "constants".
		 */
		public NonhierarchicalBucketLookupOperation(final int bucketDepth,
				final NonhierarchicalLookupCoordinator<T> coordinator,
				final Node<T> node, final OperationCallback<?> opCallback,
				final Reason reason, final OperationsConfig conf) {
			super(bucketDepth, coordinator, node, opCallback, reason, conf);
			coordinator.setClient(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final void sendLookupMessage(
				final KademliaOverlayContact<T> destination) {
			final KademliaMsg<T> message = new KClosestNodesLookupMsg<T>(
					getComponent().getTypedOverlayID(),
					destination.getOverlayID(), coordinator.getKey(),
					getReason(), config);
			sendAndWait(message, destination);
		}

	}

	/**
	 * The length of the suffix of the lookup key that is arbitrary.
	 */
	private final int suffixLength;

	/**
	 * The relevant prefix of the lookup key (the prefix of the bucket that has
	 * to be looked up).
	 */
	private final BigInteger keyPrefix;

	/**
	 * A set with valid KademliaOverlayIDs that have been seen. An ID is valid
	 * if it has the required prefix. This set keeps KademliaOverlayIDs in order
	 * to avoid to count an ID twice. After K IDs have been seen, the lookup
	 * goal has been reached and we do not need to continue remembering IDs (to
	 * save space).
	 */
	private final Set<T> seenValidIDs;

	/**
	 * Constructs a new bucket lookup operation that sends cluster-restricted
	 * lookup messages.
	 * 
	 * @param bucketDepth
	 *            the depth of the bucket for which contacts have to be found. 0
	 *            is the depth of the root bucket. The bucket depth determines
	 *            the length of the prefix of the lookup key that is relevant,
	 *            or in other words, the length of the suffix of the key that is
	 *            arbitrary. It is used to find out which of the seen contacts
	 *            can be saved in that bucket. The length of the relevant prefix
	 *            is the bucket depth times the split factor of the routing tree
	 *            ({@link OperationsConfig#getRoutingTreeOrder()}). The length
	 *            of the arbitrary suffix is the identifier length
	 *            {@link OperationsConfig#getIDLength()} minus the length of the
	 *            relevant prefix.
	 * 
	 * @param coordinator
	 *            the HierarchicalLookupCoordinator that implements the details
	 *            of the lookup process in a certains flavour of Kademlia (for
	 *            example standard Kademlia, Kandy, or hierarchical Kademlia).
	 * @param node
	 *            the Node that initiates this lookup.
	 * @param opCallback
	 *            a callback that is informed when this operation terminates.
	 * 
	 * @param reason
	 *            why this operation is to be executed - either user-initiated
	 *            or for maintenance.
	 * @param conf
	 *            an OperationsConfig reference that permits to retrieve
	 *            configuration "constants".
	 */
	public BucketLookupOperation(final int bucketDepth,
			final LookupCoordinator<T> coordinator, final Node<T> node,
			final OperationCallback<?> opCallback, final Reason reason,
			final OperationsConfig conf) {
		super(coordinator, node, opCallback, reason, conf);
		seenValidIDs = new LinkedHashSet<T>(config.getBucketSize(), 1.0f);
		suffixLength = config.getIDLength()
				- (config.getRoutingTreeOrder() * bucketDepth);
		keyPrefix = coordinator.getKey().getBigInt().shiftRight(suffixLength);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void messageReceived(final NodeListMsg<T> message) {
		int localBucketSize = ((AbstractKademliaNode<T>) getComponent())
				.getLocalConfig().getBucketSize();

		// remember valid contacts: (1) sender of message
		if (seenValidIDs.size() < localBucketSize
				&& isValidForBucket(message.getSender())) {
			seenValidIDs.add(message.getSender());
		}
		// remember valid contacts: (2) contacts returned in message
		for (final KademliaOverlayContact<T> contact : message.getNodes()) {
			if (seenValidIDs.size() < config.getBucketSize()
					&& isValidForBucket(contact.getOverlayID())) {
				seenValidIDs.add(contact.getOverlayID());
			}
		}

		// let superclass handle the message as usual
		super.messageReceived(message);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void coordinatorFinished() {
		int localBucketSize = ((AbstractKademliaNode<T>) getComponent())
				.getLocalConfig().getBucketSize();

		// success if we have seen at least K valid contacts
		if (seenValidIDs.size() < localBucketSize) {
			finishOperation(OperationState.ERROR);
		} else {
			finishOperation(OperationState.SUCCESS);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean isContinuationNecessary() {
		int localBucketSize = ((AbstractKademliaNode<T>) getComponent())
				.getLocalConfig().getBucketSize();

		// look up at least as long as we do not have seen K valid contacts
		if (seenValidIDs.size() < localBucketSize) {
			return true;
		}
		return false;
	}

	/**
	 * Determines whether the identifier <code>id</code> is relevant for this
	 * bucket lookup (whether it can be saved in the bucket that is looked up).
	 * 
	 * @param id
	 *            the KademliaOverlayID that is to be checked.
	 * @return true if <code>id</code> has the same prefix as the bucket.
	 */
	private final boolean isValidForBucket(final KademliaOverlayID id) {
		final BigInteger idPrefix = id.getBigInt().shiftRight(suffixLength);
		if (idPrefix.equals(keyPrefix)) {
			return true;
		}
		return false;
	}

}
