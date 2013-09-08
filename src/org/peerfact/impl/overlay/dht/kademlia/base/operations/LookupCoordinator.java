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
import java.util.Set;

import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayContact;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayID;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayKey;
import org.peerfact.impl.overlay.dht.kademlia.base.operations.LookupCoordinatorClient.HierarchicalLookupCoordinatorClient;
import org.peerfact.impl.overlay.dht.kademlia.base.operations.LookupCoordinatorClient.NonhierarchicalLookupCoordinatorClient;
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
 * A lookup coordinator is responsible for coordinating the flow of the lookup
 * (who will be queried when etc.). The lookup coordinator distinguishes between
 * standard Kademlia, Kandy, and hierarchical Kademlia). It abstracts from the
 * details of the simulation environment (event handling, message sending and
 * reception).
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public abstract interface LookupCoordinator<T extends KademliaOverlayID> {

	/**
	 * Non-hierarchical version of LookupCoordinator.
	 * 
	 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
	 */
	public interface NonhierarchicalLookupCoordinator<S extends KademliaOverlayID>
			extends LookupCoordinator<S> {

		/**
		 * Sets the client operation that uses this LookupCoordinator (the
		 * object on which the callback methods will be called).
		 * 
		 * @param client
		 *            the NonhierarchicalLookupCoordinatorClient that uses this
		 *            NonhierarchicalLookupCoordinator.
		 */
		public void setClient(NonhierarchicalLookupCoordinatorClient<S> client);

	}

	/**
	 * Hierarchical version of LookupCoordinator.
	 * 
	 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
	 */
	public interface HierarchicalLookupCoordinator<H extends HKademliaOverlayID>
			extends LookupCoordinator<H> {

		/**
		 * Sets the client operation that uses this LookupCoordinator (the
		 * object on which the callback methods will be called).
		 * 
		 * @param client
		 *            the HierarchicalLookupCoordinatorClient that uses this
		 *            HierarchicalLookupCoordinator.
		 */
		public void setClient(HierarchicalLookupCoordinatorClient<H> client);

	}

	/**
	 * Starts the lookup. {@link #setClient(LookupCoordinatorClient)} must have
	 * been called before the lookup can be started.
	 */
	public void start();

	/**
	 * A lookup reply with a list of contacts has been received from a queried
	 * node. Continues the lookup process. Should also be called if a timeout
	 * has occurred (in that case with an empty contact list), so that this
	 * LookupCoordinator can continue sending further messages. The lookup must
	 * have been started ({@link #start()}) before this method can be called.
	 * 
	 * @param contacts
	 *            a collection with the received KademliaOverlayContacts (lookup
	 *            result).
	 */
	public void contactListReceived(
			Collection<KademliaOverlayContact<T>> contacts);

	/**
	 * @return the {@link OperationsConfig#getBucketSize()} closest nodes to the
	 *         lookup key that are currently known.
	 */
	public Set<KademliaOverlayContact<T>> getCurrentlyKnownKClosestNodes();

	/**
	 * @return the KademliaOverlayKey that is to be looked up.
	 */
	public KademliaOverlayKey getKey();

	/**
	 * @return true iff the lookup has completed (if there are no more nodes
	 *         that can be queried, for instance).
	 */
	// public boolean isFinished();
}
