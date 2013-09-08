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

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.peerfact.api.common.Operation;
import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.overlay.dht.DHTObject;
import org.peerfact.impl.overlay.dht.kademlia.base.components.AbstractKademliaNode;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayContact;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayID;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayKey;
import org.peerfact.impl.overlay.dht.kademlia.base.components.Node;
import org.peerfact.impl.overlay.dht.kademlia.base.messages.DataMsg;


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
 * Determines all data items stored on this node that need to be republished and
 * sends these data items to the responsible neighbours. This Operation carries
 * out one KClosestNodesLookup for the Node's own ID to refresh that part of the
 * routing table and then uses local information only to determine who is
 * responsible for what data item.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public class RepublishOperation<T extends KademliaOverlayID> extends
		AbstractKademliaOperation<Object, T> {

	/**
	 * The identifier of the operation that is executed to look up the k closest
	 * neighbours before sending data messages.
	 */
	private int lookupOpId;

	/**
	 * A Map with all items that need to be republished.
	 */
	private Map<KademliaOverlayKey, DHTObject> itemsToRepublish;

	/**
	 * Constructs a new data republish operation. This operation sends all data
	 * items that have to be republished to the responsible nodes. This
	 * operation is for maintenance purposes.
	 * 
	 * @param node
	 *            the Node on which this operation will run.
	 * @param opCallback
	 *            a callback that is informed when this operation terminates.
	 * @param conf
	 *            an OperationsConfig reference that permits to retrieve
	 *            configuration "constants".
	 */
	public RepublishOperation(final Node<T> node,
			final OperationCallback<?> opCallback, final OperationsConfig conf) {
		super(node, opCallback, Reason.MAINTENANCE,
				conf);
		lookupOpId = -1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void execute() {
		final KademliaOperation<List<KademliaOverlayContact<T>>> lookupOp;
		final KademliaOverlayKey keyForOwnID = getComponent()
				.getTypedOverlayID().toKey();

		itemsToRepublish = getComponent().getLocalIndex()
				.getEntriesToRepublish();
		if (itemsToRepublish.size() == 0) {
			finishOperation(OperationState.SUCCESS);
			return;
		}

		scheduleOperationTimeout(config.getLookupOperationTimeout() + 1);
		// look up this node's own ID (need up to date routing table)
		lookupOp = getComponent()
				.getOperationFactory()
				.getKClosestNodesLookupOperation(keyForOwnID, getReason(), this);
		lookupOp.scheduleImmediately();
		lookupOpId = lookupOp.getOperationID();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void calledOperationDidFail(final Operation<?> op) {
		// check whether event was caused by the operation we are waiting for
		if (lookupOpId == op.getOperationID()) {
			// the previous lookup failed - we will not republish
			finishOperation(OperationState.ERROR);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void calledOperationDidSucceed(final Operation op) {
		Collection<KademliaOverlayContact<T>> closestAroundKey;

		final T myID = getComponent().getTypedOverlayID();
		DataMsg<T> message;

		// check whether event was caused by the operation we are waiting for
		if (lookupOpId != op.getOperationID()) {
			return;
		}

		/*
		 * For each data item, determine the k closest neighbours from the local
		 * routing table. (We need to obtain an unfiltered view for the case of
		 * Kandy.) These have to store that data item. However, only send data
		 * items for which this node is one of the k closest neighbours (that
		 * is, do not send data items that this node itself is not responsible
		 * for to another node).
		 */
		for (final Map.Entry<KademliaOverlayKey, DHTObject> item : itemsToRepublish
				.entrySet()) {
			int localBucketSize = ((AbstractKademliaNode<T>) getComponent())
					.getLocalConfig().getBucketSize();
			closestAroundKey = getComponent().getKademliaRoutingTable()
					.localLookup(item.getKey(), localBucketSize);
			// still among k closest nodes to data item?
			if (!closestAroundKey.contains(getComponent().getLocalContact())) {
				continue;
			}

			for (final KademliaOverlayContact<T> receiver : closestAroundKey) {
				if (receiver.getOverlayID().equals(myID)) {
					/*
					 * This is required to update the last republish time in the
					 * index so that entries that the node is responsible for do
					 * not expire!
					 */
					getComponent().getLocalIndex().put(item.getKey(),
							item.getValue());
				} else {
					message = new DataMsg<T>(myID, receiver.getOverlayID(),
							item.getKey(), item.getValue(), getReason(), config);
					send(message, receiver);
				}
			}
		}
		finishOperation(OperationState.SUCCESS);
	}

}
