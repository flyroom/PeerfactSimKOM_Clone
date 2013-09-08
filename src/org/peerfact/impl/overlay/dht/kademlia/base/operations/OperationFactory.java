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

import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.api.overlay.OverlayID;
import org.peerfact.api.overlay.dht.DHTObject;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayContact;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayID;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayKey;
import org.peerfact.impl.overlay.dht.kademlia.base.operations.AbstractKademliaOperation.Reason;


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
 * An operation factory is used to construct operations. Internally, these
 * operations correspond to a certain flavour of Kademlia (that is, for
 * instance, either standard Kademlia, Kandy, Kademlia with virtual
 * hierarchies...). This interface permits to decouple the general functioning
 * of Kademlia from these variable details.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public interface OperationFactory<T extends KademliaOverlayID> {

	/**
	 * Operation interface for data lookup operations that permits clients to
	 * access the data that has been looked up.
	 * 
	 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
	 */
	public interface DataLookupOperation<T> extends KademliaOperation<T> {

		/**
		 * @return the data item that has been looked up. Returns
		 *         <code>null</code> if no data has been found or if the lookup
		 *         process has not yet completed.
		 */
		public DHTObject getData();
	}

	/**
	 * Operation interfaces for operations that look up the k closest nodes
	 * around a given key. Permits clients to access the k closest nodes that
	 * have been found.
	 * 
	 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
	 */
	public interface NodeLookupOperation<T, S extends KademliaOverlayID>
			extends KademliaOperation<T> {

		/**
		 * @return a Set of the closest nodes that have been found in the lookup
		 *         operation. No assumption can be made about the ordering of
		 *         the nodes. If the lookup process has not yet completed, the
		 *         result returned by this method is undefined.
		 */
		public Set<KademliaOverlayContact<S>> getNodes();
	}

	/**
	 * @return an operation that connects the node that owns this operation
	 *         factory to the overlay network. The operation has only been
	 *         constructed, but not yet scheduled.
	 */
	// public KademliaOperation getConnectOperation();
	/**
	 * Constructs an operation that initially builds the routing table.
	 * 
	 * @param opCallback
	 *            a callback that is informed when this operation terminates.
	 * @return an operation that initially fills the routing table of the node
	 *         that owns this operation factory. The operation has only been
	 *         constructed, but not yet scheduled.
	 */
	public KademliaOperation<?> getBuildRoutingTableOperation(
			OperationCallback<?> opCallback);

	/**
	 * Creates an operation that stores <code>data</code> on the
	 * {@link OperationsConfig#getBucketSize()} closest nodes of
	 * <code>key</code>. A look up over the network is carried out to determine
	 * these K closest neighbours.
	 * 
	 * @param data
	 *            the DHTObject (data item) that is to be stored in the DHT.
	 * @param key
	 *            the KademliaOverlayKey of the data item.
	 * @param storeForeignData
	 *            whether <code>data</code> should be stored on the k closest
	 *            neighbours of <code>key</code> if this Node is not one of
	 *            them. That is, whether this StoreOperation should be allowed
	 *            to store data that this Node is not responsible for on other
	 *            nodes.
	 * @param why
	 *            the reason why this store is carried out - user-initiated or
	 *            for maintenance purposes.
	 * @param opCallback
	 *            a callback that is informed when this operation terminates.
	 * @return an operation that stores <code>data</code> in the DHT. The
	 *         operation has only been constructed, but not yet scheduled.
	 */
	public KademliaOperation<OverlayContact<OverlayID<?>>> getStoreOperation(
			DHTObject data,
			KademliaOverlayKey key, boolean storeForeignData, Reason why,
			OperationCallback<?> opCallback);

	/**
	 * Creates an operation that looks up the data item associated with
	 * <code>key</code> in the DHT. It will <i>not</i> look it up in the local
	 * database.
	 * 
	 * @param key
	 *            the KademliaOverlayKey associated with the data item to be
	 *            looked up.
	 * @param opCallback
	 *            a callback that is informed when this operation terminates.
	 * @return an operation that looks up the data for <code>key</code> in the
	 *         DHT. The operation has only been constructed, but not yet
	 *         scheduled.
	 */
	public DataLookupOperation<DHTObject> getDataLookupOperation(
			KademliaOverlayKey key, OperationCallback<DHTObject> opCallback);

	/**
	 * Creates an operation that finds the K=
	 * {@link OperationsConfig#getBucketSize()} closest nodes of
	 * <code>key</code>.
	 * 
	 * @param key
	 *            the KademliaOverlayKey of which the <code>K</code> closest
	 *            neighbours should be determined.
	 * @param why
	 *            the reason why this lookup is carried out - user-initiated or
	 *            for maintenance purposes.
	 * @param opCallback
	 *            a callback that is informed when this operation terminates.
	 * @return an operation that determines the <code>K</code> closest
	 *         neighbours of <code>key</code> in the DHT. The operation has only
	 *         been constructed, but not yet scheduled.
	 */
	public NodeLookupOperation<List<KademliaOverlayContact<T>>, T> getKClosestNodesLookupOperation(
			KademliaOverlayKey key, Reason why,
			OperationCallback<List<KademliaOverlayContact<T>>> opCallback);

	/**
	 * Creates an operation that finds {@link OperationsConfig#getBucketSize()}
	 * nodes to fill the bucket given by its prefix <code>key</code> and its
	 * depth in the routing tree <code>bucketDepth</code>.
	 * 
	 * @param key
	 *            the prefix of the bucket to be filled. Has to be of length
	 *            {@link OperationsConfig#getIDLength()}, that is it may not be
	 *            only the prefix of the bucket shifted to the right such as
	 *            used in the routing table implementation. May be a random key
	 *            from the range of identifiers that the bucket is responsible
	 *            for.
	 * @param bucketDepth
	 *            the depth of the bucket in the routing tree. The root bucket
	 *            has depth 0. Is used to determine the actual length of the
	 *            prefix in <code>key</code> (in conjunction with the
	 *            {@link OperationsConfig#getRoutingTreeOrder()} parameter).
	 * @param opCallback
	 *            a callback that is informed when this operation terminates.
	 * @return an operation that finds <code>K</code> contacts to fill the given
	 *         bucket. The operation has only been constructed, but not yet
	 *         scheduled.
	 */
	public KademliaOperation<List<KademliaOverlayContact<T>>> getBucketLookupOperation(
			KademliaOverlayKey key, int bucketDepth,
			OperationCallback<List<KademliaOverlayContact<T>>> opCallback);

	/**
	 * @param opCallback
	 *            a callback that is informed when this operation terminates.
	 * @return an operation that determines the data items from the local
	 *         database which have to be republished and stores them on the
	 *         responsible nodes in the DHT. The operation has only been
	 *         constructed, but not yet scheduled.
	 */
	public KademliaOperation<?> getRepublishOperation(
			OperationCallback<?> opCallback);

/**
	 * @param forceRefresh
	 *                if true, all buckets need to be refreshed (instead of
	 *                those that have not been looked up for the time given by
	 *                {@link OperationsConfig#getRefreshInterval()).
	 * @param opCallback
	 *                a callback that is informed when this operation
	 *                terminates.
	 * @return an operation that refreshes those buckets from the local routing
	 *         table for which no lookup (over the network) has been carried out
	 *         in the last hour. The operation has only been constructed, but
	 *         not yet scheduled.
	 */
	public KademliaOperation<List<KademliaOverlayContact<T>>> getRefreshOperation(
			boolean forceRefresh,
			OperationCallback<List<KademliaOverlayContact<T>>> opCallback);

	/**
	 * The given Operation has been constructed. It will be added to the set of
	 * the running Operations of this Node.
	 * 
	 * @param newOperation
	 *            the new Operation.
	 */
	public void operationConstructed(KademliaOperation<?> newOperation);

	/**
	 * The given Operation has finished. It will be removed from the set of the
	 * running Operations of this Node.
	 * 
	 * @param finishedOperation
	 *            the finished Operation.
	 */
	public void operationFinished(KademliaOperation<?> finishedOperation);

	/**
	 * Aborts all Operations of this Node.
	 */
	public void abortAllOperations();

}
