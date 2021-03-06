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

package org.peerfact.impl.overlay.dht.kademlia.kandy.operations;

import java.util.List;

import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.api.overlay.OverlayID;
import org.peerfact.api.overlay.dht.DHTObject;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayContact;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayKey;
import org.peerfact.impl.overlay.dht.kademlia.base.components.Node.VisibilityRestrictableNode;
import org.peerfact.impl.overlay.dht.kademlia.base.operations.AbstractOperationFactory;
import org.peerfact.impl.overlay.dht.kademlia.base.operations.BucketRefreshOperation;
import org.peerfact.impl.overlay.dht.kademlia.base.operations.KademliaOperation;
import org.peerfact.impl.overlay.dht.kademlia.base.operations.OperationsConfig;
import org.peerfact.impl.overlay.dht.kademlia.base.operations.RepublishOperation;
import org.peerfact.impl.overlay.dht.kademlia.base.operations.RoutingTableBuildOperation;
import org.peerfact.impl.overlay.dht.kademlia.base.operations.StandardLookupCoordinator;
import org.peerfact.impl.overlay.dht.kademlia.base.operations.StoreOperation;
import org.peerfact.impl.overlay.dht.kademlia.base.operations.AbstractKademliaOperation.Reason;
import org.peerfact.impl.overlay.dht.kademlia.base.operations.LookupCoordinator.NonhierarchicalLookupCoordinator;
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
 * Operations as used in Kandy2. These are the same operations as in Kademlia,
 * except that they use a visibility restricted local lookup for data lookups.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public class KandyOperationFactory extends
		AbstractOperationFactory<HKademliaOverlayID> {

	/**
	 * The node that owns this operation factory
	 */
	private final VisibilityRestrictableNode<HKademliaOverlayID> myNode;

	/**
	 * Constructs a new Kandy2 operation factory for the given
	 * VisibilityRestrictableNode.
	 * 
	 * @param myNode
	 *            the VisibilityRestrictableNode that owns this operation
	 *            factory.
	 * @param conf
	 *            an OperationsConfig reference that permits to retrieve
	 *            configuration "constants".
	 */
	public KandyOperationFactory(
			final VisibilityRestrictableNode<HKademliaOverlayID> myNode,
			final OperationsConfig conf) {
		super(conf);
		this.myNode = myNode;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final KademliaOperation<List<KademliaOverlayContact<HKademliaOverlayID>>> getBucketLookupOperation(
			final KademliaOverlayKey key,
			final int bucketDepth,
			final OperationCallback<List<KademliaOverlayContact<HKademliaOverlayID>>> opCallback) {
		return getKClosestNodesLookupOperation(key, Reason.MAINTENANCE,
				opCallback);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final DataLookupOperation<DHTObject> getDataLookupOperation(
			final KademliaOverlayKey key,
			final OperationCallback<DHTObject> opCallback) {
		/*
		 * Although HKademliaOverlayIDs are used, no use is made of hierarchical
		 * lookup messages (with restriction to minimum common cluster depth)
		 * --> use NonhierarchicalLookupCoordinator
		 */
		final NonhierarchicalLookupCoordinator<HKademliaOverlayID> coord = new VisibilityRestrictedLookupCoordinator(
				key, myNode, config);
		final DataLookupOperation<DHTObject> op = new org.peerfact.impl.overlay.dht.kademlia.base.operations.DataLookupOperation<HKademliaOverlayID>(
				coord, myNode, opCallback, Reason.USER_INITIATED, config);
		return op;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final NodeLookupOperation<List<KademliaOverlayContact<HKademliaOverlayID>>, HKademliaOverlayID> getKClosestNodesLookupOperation(
			final KademliaOverlayKey key,
			final Reason why,
			final OperationCallback<List<KademliaOverlayContact<HKademliaOverlayID>>> opCallback) {
		/*
		 * Although HKademliaOverlayIDs are used, no use is made of hierarchical
		 * lookup messages (with restriction to minimum common cluster depth)
		 * --> use NonhierarchicalLookupCoordinator
		 */
		final NonhierarchicalLookupCoordinator<HKademliaOverlayID> coord = new StandardLookupCoordinator<HKademliaOverlayID>(
				key, myNode, config);
		final NodeLookupOperation<List<KademliaOverlayContact<HKademliaOverlayID>>, HKademliaOverlayID> op = new org.peerfact.impl.overlay.dht.kademlia.base.operations.KClosestNodesLookupOperation<HKademliaOverlayID>(
				coord, myNode, opCallback, why, config);
		return op;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final KademliaOperation<?> getRepublishOperation(
			final OperationCallback<?> opCallback) {
		final KademliaOperation<?> repubOp = new RepublishOperation<HKademliaOverlayID>(
				myNode,
				opCallback, config);
		return repubOp;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final KademliaOperation<OverlayContact<OverlayID<?>>> getStoreOperation(
			final DHTObject data,
			final KademliaOverlayKey key, final boolean storeForeignData,
			final Reason why, final OperationCallback<?> opCallback) {
		final KademliaOperation storeOp = new StoreOperation<HKademliaOverlayID>(
				key, data,
				storeForeignData, myNode, opCallback, why, config);
		return storeOp;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final KademliaOperation<List<KademliaOverlayContact<HKademliaOverlayID>>> getRefreshOperation(
			final boolean forceRefresh,
			final OperationCallback<List<KademliaOverlayContact<HKademliaOverlayID>>> opCallback) {
		return new BucketRefreshOperation<HKademliaOverlayID>(forceRefresh,
				myNode, opCallback,
				config);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final KademliaOperation<?> getBuildRoutingTableOperation(
			final OperationCallback<?> opCallback) {
		return new RoutingTableBuildOperation<HKademliaOverlayID>(myNode,
				opCallback, config);
	}

}
