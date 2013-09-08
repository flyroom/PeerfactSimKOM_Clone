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

import java.util.Map;

import org.peerfact.api.common.Operation;
import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.api.overlay.OverlayID;
import org.peerfact.api.overlay.dht.DHTObject;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayID;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayKey;
import org.peerfact.impl.overlay.dht.kademlia.base.components.Node;


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
 * sends these data items to the responsible neighbours by issuing a
 * StoreOperation for each data item to be republished. Only data that this node
 * is responsible for is republished (that is, only data for which this node is
 * one of the K closest nodes).
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public class StoreRepublishOperation<T extends KademliaOverlayID> extends
		AbstractKademliaOperation<Object, T> {

	/**
	 * The identifier of the currently running operation that is executed to
	 * store one data item.
	 */
	private int currentStoreOpId;

	/**
	 * The data items that need to be republished.
	 */
	private Map<KademliaOverlayKey, DHTObject> itemsToRepublish;

	/**
	 * Constructs a new data republish operation. This operation sends all data
	 * items that have to be republished to the responsible nodes by using a
	 * store operation for each data item. This operation is for maintenance
	 * purposes.
	 * 
	 * @param node
	 *            the Node on which this operation will run.
	 * @param opCallback
	 *            a callback that is informed when this operation terminates.
	 * @param conf
	 *            an OperationsConfig reference that permits to retrieve
	 *            configuration "constants".
	 */
	public StoreRepublishOperation(final Node<T> node,
			final OperationCallback<?> opCallback, final OperationsConfig conf) {
		super(node, opCallback, Reason.MAINTENANCE,
				conf);
		currentStoreOpId = -1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void execute() {
		itemsToRepublish = getComponent().getLocalIndex()
				.getEntriesToRepublish();
		// all store operations will be executed one after another
		// -> later executions can benefit from earlier lookup results
		scheduleOperationTimeout(config.getLookupOperationTimeout()
				* itemsToRepublish.size() + 1);
		issueStoreOperation();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void calledOperationDidFail(final Operation<?> op) {
		// check whether event was caused by the operation we are waiting for
		if (currentStoreOpId == op.getOperationID()) {
			// the previous store failed - we carry on with the next one
			// (failing may mean that this node is not responsible for one item)
			issueStoreOperation();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void calledOperationDidSucceed(final Operation op) {
		// check whether event was caused by the operation we are waiting for
		if (currentStoreOpId == op.getOperationID()) {
			issueStoreOperation();
		}
	}

	/**
	 * Issues a store operation for a data item from
	 * <code>itemsToRepublish</code>. The data item for which the store has been
	 * scheduled will be removed from <code>itemsToRepublish</code> and the
	 * operation identifier of the store operation will be saved in
	 * <code>currentStoreOpId</code>. If there is no data item left to
	 * republish, this republish operation will have finished successfully.
	 */
	private final void issueStoreOperation() {
		// key of entry is data key, value is data item
		final Map.Entry<KademliaOverlayKey, DHTObject> currentDataItem;
		final KademliaOperation<OverlayContact<OverlayID<?>>> storeOperation;

		if (itemsToRepublish.size() > 0) {
			currentDataItem = itemsToRepublish.entrySet().iterator().next();
			// forbid to store data if this Node is not part of K closest nodes
			storeOperation = getComponent().getOperationFactory()
					.getStoreOperation(currentDataItem.getValue(),
							currentDataItem.getKey(), false, getReason(), this);
			currentStoreOpId = storeOperation.getOperationID();
			storeOperation.scheduleImmediately();
			itemsToRepublish.remove(currentDataItem.getKey());
		} else {
			// no more buckets to refresh (this operation cannot fail)
			finishOperation(OperationState.SUCCESS);
		}
	}

}
