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
import java.util.Map;

import org.apache.log4j.Logger;
import org.peerfact.api.common.Operation;
import org.peerfact.api.common.OperationCallback;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayContact;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayID;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayKey;
import org.peerfact.impl.overlay.dht.kademlia.base.components.Node;
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
 * An operation that issues bucket lookups for those buckets that need to be
 * refreshed.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @author Sebastian Kaune
 * @version 05/06/2011
 */
public class BucketRefreshOperation<T extends KademliaOverlayID> extends
		AbstractKademliaOperation<List<KademliaOverlayContact<T>>, T> {

	final static Logger log = SimLogger.getLogger(BucketRefreshOperation.class);

	/**
	 * Whether a refresh is forced for all buckets.
	 */
	private final boolean forceRefresh;

	/**
	 * The operation identifier of the currently executing bucket lookup
	 * operation.
	 */
	private int currentBucketLookupOperationID;

	/**
	 * A Map that contains those buckets that still need to be refreshed.
	 */
	private Map<KademliaOverlayKey, Integer> refreshBuckets;

	/**
	 * Constructs a new bucket refresh operation. This operation is always for
	 * maintenance purposes.
	 * 
	 * @param forceRefresh
	 *            if true, all buckets have to be refreshed, no matter their
	 *            last lookup time.
	 * @param node
	 *            the AbstractKademliaNode that initiates this lookup.
	 * @param opCallback
	 *            a callback that is informed when this operation terminates.
	 * @param conf
	 *            a OperationsConfig reference that permits to retrieve
	 *            configuration "constants".
	 */
	public BucketRefreshOperation(final boolean forceRefresh,
			final Node<T> node, final OperationCallback<?> opCallback,
			final OperationsConfig conf) {
		super(
				node,
				opCallback,
				Reason.MAINTENANCE, conf);
		this.forceRefresh = forceRefresh;
		currentBucketLookupOperationID = -1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void execute() {
		final long notLookedUpAfter;
		// log.debug("Started bucket refresh (node=" + getComponent()); // TODO
		// uncomment

		if (forceRefresh) {
			notLookedUpAfter = Simulator.getCurrentTime();
		} else {
			notLookedUpAfter = Simulator.getCurrentTime()
					- config.getRefreshInterval();
		}

		refreshBuckets = getComponent().getKademliaRoutingTable()
				.getRefreshBuckets(notLookedUpAfter);
		// all bucket lookups will be executed one after another
		scheduleOperationTimeout(config.getLookupOperationTimeout()
				* refreshBuckets.size() + 1);
		issueBucketLookup();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void calledOperationDidFail(final Operation<?> op) {
		// check whether event was caused by the operation we are waiting for
		if (currentBucketLookupOperationID == op.getOperationID()) {
			// the previous lookup failed - we carry on with the next one
			issueBucketLookup();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void calledOperationDidSucceed(final Operation op) {
		// check whether event was caused by the operation we are waiting for
		if (currentBucketLookupOperationID == op.getOperationID()) {
			issueBucketLookup();
		}
	}

	/**
	 * Issues a bucket lookup for a bucket from <code>refreshBuckets</code>. The
	 * bucket for which the lookup has been scheduled will be removed from
	 * <code>refreshBuckets</code> and the operation identifier of the lookup
	 * operation will be saved in <code>currentBucketLookupOperationID</code>.
	 * If there is no bucket left to look up, this refresh operation will be
	 * finished successfully.
	 */
	private final void issueBucketLookup() {
		// key of entry is bucket key, value is bucket depth
		final Map.Entry<KademliaOverlayKey, Integer> currentBucket;
		final KademliaOperation<List<KademliaOverlayContact<T>>> bucketLookup;

		if (refreshBuckets.size() > 0) {
			currentBucket = refreshBuckets.entrySet().iterator().next();
			bucketLookup = getComponent().getOperationFactory()
					.getBucketLookupOperation(currentBucket.getKey(),
							currentBucket.getValue(), this);
			currentBucketLookupOperationID = bucketLookup.getOperationID();
			bucketLookup.scheduleImmediately();
			refreshBuckets.remove(currentBucket.getKey());
		} else {
			// no more buckets to refresh (this operation cannot fail)
			// log.debug("Finished bucket refresh (node=" + getComponent()); //
			// TODO uncomment
			finishOperation(OperationState.SUCCESS);
		}
	}

}
