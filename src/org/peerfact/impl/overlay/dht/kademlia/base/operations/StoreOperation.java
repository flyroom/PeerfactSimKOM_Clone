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

import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.peerfact.api.common.Operation;
import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.overlay.dht.DHTObject;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayContact;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayID;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayKey;
import org.peerfact.impl.overlay.dht.kademlia.base.components.Node;
import org.peerfact.impl.overlay.dht.kademlia.base.messages.DataMsg;
import org.peerfact.impl.overlay.dht.kademlia.base.operations.OperationFactory.NodeLookupOperation;
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
 * An operation that stores a given data item in the overlay network.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public class StoreOperation<T extends KademliaOverlayID> extends
		AbstractKademliaOperation<Set<KademliaOverlayContact<T>>, T> {

	static Logger log = SimLogger.getLogger(StoreOperation.class);

	/**
	 * The key of the data item.
	 */
	private final KademliaOverlayKey dataKey;

	/**
	 * The data item to be published.
	 */
	private final DHTObject dataValue;

	/**
	 * Whether the data should be stored on the K closest nodes around key if
	 * this node is not part of them.
	 */
	private final boolean storeForeignData;

	/**
	 * The nodes that stored the data item
	 */
	Set<KademliaOverlayContact<T>> receivers;

	/**
	 * The operation used to determine the k closest nodes to dataKey.
	 */
	private NodeLookupOperation<List<KademliaOverlayContact<T>>, T> lookupOperation;

	/**
	 * Constructs an operation that looks up the {@KademliaConfig#K
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * } closest nodes to <code>key</code> (over the network)
	 * and stores the data item <code>value</code> on them.
	 * 
	 * @param key
	 *            the KademliaOverlayKey of the data item to be published.
	 * @param value
	 *            the data item that is to be published in the overlay network.
	 * @param storeForeignData
	 *            whether <code>value</code> should be stored on the K closest
	 *            neighbours of <code>key</code> if <code>node</code> is
	 *            <i>not</i> one of them. This is determined having carried out
	 *            the lookup for <code>key</code>'s neighbours. (If set to
	 *            <code>false</code> and if <code>node</code> is not part of the
	 *            K closest nodes of <code>key</code>, this StoreOperation will
	 *            finish in state ERROR.)
	 * @param node
	 *            the Node that runs this operation.
	 * @param opCallback
	 *            a callback that is informed when this operation terminates.
	 * @param reason
	 *            the reason why this operation is being run.
	 * @param conf
	 *            an OperationsConfig reference that permits to retrieve
	 *            configuration "constants".
	 */
	public StoreOperation(final KademliaOverlayKey key,
			final DHTObject value, final boolean storeForeignData,
			final Node<T> node, final OperationCallback<?> opCallback,
			final Reason reason, final OperationsConfig conf) {
		super(node, opCallback, reason, conf);
		dataKey = key;
		dataValue = value;
		this.storeForeignData = storeForeignData;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void execute() {
		scheduleOperationTimeout(config.getLookupOperationTimeout() + 1);
		lookupOperation = getComponent().getOperationFactory()
				.getKClosestNodesLookupOperation(dataKey, getReason(), this);
		lookupOperation.scheduleImmediately();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void calledOperationDidFail(final Operation<?> op) {
		// check whether event was caused by the operation we are waiting for
		if (lookupOperation != null
				&& lookupOperation.getOperationID() == op.getOperationID()) {
			// the previous lookup failed - we will not store
			log.debug("Store: Node Lookup failed.");

			finishOperation(OperationState.ERROR);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void calledOperationDidSucceed(final Operation op) {
		// check whether event was caused by the operation we are waiting for
		if (lookupOperation == null
				|| lookupOperation.getOperationID() != op.getOperationID()) {
			return;
		}
		receivers = lookupOperation.getNodes();
		sendDataItem(receivers);
		finishOperation(OperationState.SUCCESS);
	}

	/**
	 * Delivers the data item to the given list of receivers. If the local node
	 * is one of the receivers, the data item will directly be put into its
	 * local index. Data messages are sent to distant nodes. If the local
	 * contact is not part of the receivers and
	 * <code>storeForeignData==false</code>, then this Operation is aborted with
	 * an error and without storing the data anywhere.
	 * 
	 * @param receiverSet
	 *            a Set of KademliaOverlayContacts that will receive data
	 *            messages with the data item. (A local contact will directly
	 *            receive the data item into its local index.)
	 */
	private void sendDataItem(final Set<KademliaOverlayContact<T>> receiverSet) {
		final T myID = getComponent().getTypedOverlayID();
		final KademliaOverlayContact<T> myContact = getComponent()
				.getLocalContact();
		DataMsg<T> message;

		if (!storeForeignData && !receiverSet.contains(myContact)) {
			// we may store data only if we are part of k closest nodes
			finishOperation(OperationState.ERROR);
			return;
		}

		for (final KademliaOverlayContact<T> receiver : receiverSet) {
			if (receiver.equals(myContact)) {
				/*
				 * This is required to update the last republish time in the
				 * index so that entries that the node is responsible for do not
				 * expire!
				 */
				getComponent().getLocalIndex().put(dataKey, dataValue);
			} else {
				message = new DataMsg<T>(myID, receiver.getOverlayID(),
						dataKey, dataValue, getReason(), config);
				send(message, receiver);
			}
		}
	}

	@Override
	public Set<KademliaOverlayContact<T>> getResult() {
		return receivers;
	}

}
