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
import java.util.Comparator;
import java.util.Set;

import org.apache.log4j.Logger;
import org.peerfact.impl.overlay.dht.kademlia.base.components.AbstractKademliaNode;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayContact;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayID;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayKey;
import org.peerfact.impl.overlay.dht.kademlia.base.components.Node;
import org.peerfact.impl.overlay.dht.kademlia.base.components.RoutingTable;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.util.logging.SimLogger;
import org.peerfact.impl.util.toolkits.KExtendedLookupMap;
import org.peerfact.impl.util.toolkits.KSortedLookupList;
import org.peerfact.impl.util.toolkits.Predicate;
import org.peerfact.impl.util.toolkits.Comparators.KademliaOverlayContactXORMaxComparator;


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
 * Abstract lookup coordinator that implements a control flow framework for
 * lookup algorithms. Some methods that are likely to be reusable in subclasses
 * are implemented here, whereas other methods need to be implemented by
 * subclasses.
 * <p>
 * The two methods that have to be implemented in subclasses are {@link #init()}
 * and {@link #proceed()}. Init() is called by the control flow implemented in
 * this class to initialise the lookup process, and proceed() is called after
 * initialisation or when a message has been received to proceed in the lookup
 * process (by sending further lookup messages, for instance).
 * <p>
 * This lookup coordinator keeps a list with the
 * {@link OperationsConfig#getBucketSize()} currently known closest nodes to
 * {@link LookupCoordinatorClient#getA()}. This list is automatically maintained
 * and {@link #getBestUnqueried()} can be used to determine the next contact
 * from that list that should be queried. {@link #isFinished()} implements a
 * generic termination check that makes use of {@link #isFinishedAppSpecific()}
 * to determine whether the lookup has finished. IsFinished() should be used by
 * subclasses and should not be overwritten, whereas isFinishedAppSpecific()
 * should not be used directly, but is likely to have to be modified to fit into
 * the concrete subclass.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public abstract class AbstractLookupCoordinator<T extends KademliaOverlayID>
		implements LookupCoordinator<T> {

	final static Logger log = SimLogger
			.getLogger(AbstractLookupCoordinator.class);

	/**
	 * The state of a contact: either it has already been queried, or it hasn't.
	 * 
	 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
	 */
	public enum ContactState {
		/**
		 * The contact has not yet been queried.
		 */
		TO_QUERY,

		/**
		 * The contact has been queried. (No statement about query result.)
		 */
		QUERIED;

	}

	/**
	 * A Predicate that holds for ContactState.TO_QUERY
	 */
	protected static final Predicate<ContactState> toQuery = new Predicate<ContactState>() {

		@Override
		public final boolean isTrue(final ContactState state) {
			return state == ContactState.TO_QUERY;
		}

	};

	/**
	 * A KSmallestMap that maps the k currently known contacts that are closest
	 * to lookupKey to their state (TO_QUERY or QUERIED).
	 * 
	 * This map is sorted by <i>decreasing</i> XOR closeness (in other words,
	 * increasing XOR distance) to <code>lookupKey</code>.
	 */
	protected final KSortedLookupList<KademliaOverlayContact<T>, ContactState> kClosestNodes;

	/**
	 * The KademliaOverlayKey that is to be looked up.
	 */
	private final KademliaOverlayKey lookupKey;

	/**
	 * Whether this lookup has completed.
	 */
	private boolean finished;

	/**
	 * Configuration values ("constants").
	 */
	protected final OperationsConfig config;

	/**
	 * Constructs a new standard lookup coordinator.
	 * 
	 * @param lookupKey
	 *            the KademliaOverlayKey that is to be looked up.
	 * @param conf
	 *            an OperationsConfig reference that permits to retrieve
	 *            configuration "constants".
	 */
	public AbstractLookupCoordinator(final KademliaOverlayKey lookupKey,
			final OperationsConfig conf) {
		this.config = conf;
		this.lookupKey = lookupKey;
		this.finished = false;
		// sort by decreasing XOR closeness/increasing XOR distance to lookupKey
		final Comparator<KademliaOverlayContact<T>> xorDist;
		xorDist = new KademliaOverlayContactXORMaxComparator<T>(lookupKey
				.getBigInt());
		this.kClosestNodes = new KExtendedLookupMap<KademliaOverlayContact<T>, ContactState>(
				config.getBucketSize(), xorDist);
	}

	@Override
	public String toString() {
		return "AbstractLookupCoordinator: " + getkClosestNodes();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final KademliaOverlayKey getKey() {
		return lookupKey;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Set<KademliaOverlayContact<T>> getCurrentlyKnownKClosestNodes() {

		Set<KademliaOverlayContact<T>> result = new java.util.LinkedHashSet<KademliaOverlayContact<T>>();
		int i = 0;
		for (KademliaOverlayContact<T> k : getkClosestNodes().keySet()) {
			if (getkClosestNodes().get(k) == ContactState.QUERIED) {
				result.add(k);
			}
			i++;
			if (i >= config.getBucketSize()) {
				break;
			}
		}
		return result;
	}

	/**
	 * @returns the client that uses this LookupCoordinator (used for
	 *          callbacks).
	 */
	protected abstract LookupCoordinatorClient<T> getClient();

	/**
	 * @return the routing table of the node that owns this lookup coordinator.
	 */
	protected abstract RoutingTable<T> getRoutingTable();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void start() {
		// fetch initial contacts from routing table
		init();
		// set last lookup time of lookupKey
		getRoutingTable().setLastLookupTime(getKey(),
				Simulator.getCurrentTime());
		if (!this.isFinished()) {
			// send lookup messages
			proceed(getNumOfMessages());
		}
	}

	/**
	 * Continue the lookup process whenever a message is received (either with
	 * lookup results or with a timeout, in the latter case <code>results</code>
	 * is empty). If the lookup process has not already completed, the lookup
	 * results are inserted and new lookup messages are triggered.
	 * 
	 * @param contacts
	 *            a collection containing the lookup results. May be empty, but
	 *            not null.
	 */
	@Override
	public final void contactListReceived(
			final Collection<KademliaOverlayContact<T>> contacts) {
		if (this.isFinished()) {
			// lookup already complete, new results are irrelevant, so
			// don't alter the data structure and stop here.
			return;
		}

		// insert contacts as not yet finished
		insertBestContacts(contacts);
		if (!this.isFinished()) {
			// only proceed if still not finished (new contacts might have
			// been enough)
			proceed(getNumOfMessages());
		}
	}

	/*
	 * Subclass methods (lookup coordination)
	 */

	/**
	 * Initialise the lookup process. Should get the k closest nodes from the
	 * routing table, insert them into <code>kClosestNodes</code> (where it has
	 * to be made sure that <code>kClosestNodes</code> does not contain more
	 * than k nodes), and mark all new entries as TO_QUERY.
	 */
	protected abstract void init();

	/**
	 * Send up to <code>numOfMessages</code> lookup messages to proceed in the
	 * lookup process. Is called after the initialisation is complete or
	 * whenever a lookup result has been received and the new contacts have been
	 * inserted into <code>kClosestNodes</code>. When this method is called, it
	 * can be assumed that <code>{@link #isFinished()}==false</code> holds.
	 * 
	 * @param numOfMessages
	 *            the number of messages that may be sent. The method is free to
	 *            send less, but not more than <code>numOfMessages</code>
	 *            messages.
	 */
	protected abstract void proceed(int numOfMessages);

	/**
	 * Checks whether various termination conditions hold and returns the
	 * result. Additionally, if one termination condition has just been reached,
	 * the finished status is set.
	 * <p>
	 * These termination conditions are:
	 * <ul>
	 * <li>This coordinator has been marked as finished previously (that is, one
	 * of the following conditions has been met before).</li>
	 * <li>The application-specific termination check
	 * {@link #isFinishedAppSpecific()} has determined that the lookup is
	 * complete.
	 * </ul>
	 * 
	 * @return true, if at least one termination condition holds.
	 */
	public final boolean isFinished() {
		if (finished) {
			// previously set as finished
			return true;
		} else if (isFinishedAppSpecific()) {
			finished = true;
			getClient().coordinatorFinished();
			return true;
		} else {
			// no finish condition is true, consequently we are not finished.
			return false;
		}
	}

	/**
	 * Application specific check whether the lookup has already completed.
	 * 
	 * The lookup is complete if
	 * <ul>
	 * <li>(a) the lookup coordinator client has determined that it is not
	 * necessary to continue the lookup, or
	 * <li>(b) there are no outstanding queries, and all known k closest
	 * contacts have been queried.</li>
	 * </ul>
	 * 
	 * This method should not be directly invoked by subclasses, but may be
	 * customised (overridden).
	 * 
	 * @return whether the operation is finished (true) or still in progress
	 *         (false).
	 */
	public boolean isFinishedAppSpecific() {
		if (!getClient().isContinuationNecessary()) {
			// client does not need lookup to continue
			return true;
		} else if (getClient().getTransitCount() == 0
				&& containsKQueriedNodes()) {
			// no more nodes to query: closest neighbours found.
			return true;
		} else {
			return false;
		}
	}

	protected boolean containsKQueriedNodes() {
		int i = 0;
		for (ContactState v : getkClosestNodes().values()) {
			if (v == ContactState.QUERIED) {
				i++;
			}
			if (i >= 5) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Inserts the closest new contacts from <code>results</code> into
	 * <code>kClosestNodes</code>, replacing more distant entries, so that it
	 * contains at most {@link OperationsConfig#getBucketSize()} entries. The
	 * state of newly inserted contacts is set to TO_QUERY, but the state of a
	 * contact that has already been contained is never overwritten.
	 * 
	 * @param newContacts
	 *            a collection with contacts that are to be inserted (lookup
	 *            results, for instance).
	 */
	public final void insertBestContacts(
			final Collection<KademliaOverlayContact<T>> newContacts) {
		getkClosestNodes().putAll(newContacts, ContactState.TO_QUERY, false);
	}

	/**
	 * Returns the "best" contact from <code>kClosestNodes</code> (that is, one
	 * with minimal distance to <code>lookupKey</code>) that has not yet been
	 * queried (is in state TO_QUERY).
	 * 
	 * @return the closest known contact with state TO_QUERY, or null if no such
	 *         contact exists (then either all contacts are in another state, or
	 *         we do not know any contact at all).
	 */
	public KademliaOverlayContact<T> getBestUnqueried() {
		return getkClosestNodes().getMinKey(toQuery);
	}

	/**
	 * @return the number of messages that may be sent right now (
	 *         {@link OperationsConfig#getMaxConcurrentLookups()} minus
	 *         {@link LookupCoordinatorClient#getTransitCount()}).
	 */
	private final int getNumOfMessages() {
		int maxConcurrentLookups = ((AbstractKademliaNode<T>) getNode())
				.getLocalConfig().getMaxConcurrentLookups();
		return maxConcurrentLookups - getClient().getTransitCount();
	}

	abstract public Node<T> getNode();

	public KSortedLookupList<KademliaOverlayContact<T>, ContactState> getkClosestNodes() {
		return kClosestNodes;
	}
}
