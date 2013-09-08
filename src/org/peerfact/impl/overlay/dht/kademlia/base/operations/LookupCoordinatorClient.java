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

import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayContact;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayID;
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
 * Interface with callback methods to be implemented by classes that use a
 * {@link LookupCoordinator}.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public abstract interface LookupCoordinatorClient<T extends KademliaOverlayID> {

	/**
	 * Non-hierarchical version of LookupCoordinatorClient.
	 * 
	 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
	 */
	public interface NonhierarchicalLookupCoordinatorClient<S extends KademliaOverlayID>
			extends LookupCoordinatorClient<S> {

		/**
		 * Sends a lookup message with key {@link getKey()} to
		 * <code>destination</code>. The concrete type of the message sent
		 * depends on the type of the LookupCoordinatorClient (whether the
		 * method is invoked in the context of a node lookup or a data lookup).
		 * 
		 * @param destination
		 *            the KademliaOverlayContact to which the message will be
		 *            sent.
		 */
		public void sendLookupMessage(KademliaOverlayContact<S> destination);

	}

	/**
	 * Hierarchical version of LookupCoordinatorClient.
	 * 
	 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
	 */
	public interface HierarchicalLookupCoordinatorClient<H extends HKademliaOverlayID>
			extends LookupCoordinatorClient<H> {

		/**
		 * Sends a lookup message with key {@link getKey()} to
		 * <code>destination</code>. The concrete type of the message sent
		 * depends on the type of the LookupCoordinatorClient (whether the
		 * method is invoked in the context of a node lookup or a data lookup).
		 * All results that may be sent as a response to this message need to
		 * have a common cluster depth of at least <code>minClusterDepth</code>
		 * with the sender of this message.
		 * 
		 * @param destination
		 *            the KademliaOverlayContact to which the message will be
		 *            sent.
		 * @param minClusterDepth
		 *            the minimal common cluster depth that all contacts
		 *            returned as a reply need to have in common with the sender
		 *            of the lookup message.
		 */
		public void sendLookupMessage(KademliaOverlayContact<H> destination,
				int minClusterDepth);

	}

	/**
	 * @return the number of messages that are currently in transit (messages
	 *         that have been sent, but for which no reply has been received and
	 *         no timeout has occurred).
	 */
	public int getTransitCount();

	/**
	 * Determines whether the client needs the lookup to continue. For instance,
	 * if the desired data item has been found, the lookup can stop. In
	 * contrast, if the goal is to find the k closest neighbours of the key, the
	 * lookup must continue until no more nodes are available to query.
	 * 
	 * In general, the lookup process can not be expected to halt as soon as
	 * this method returns <code>true</code> as some work to clean up and finish
	 * the lookup process might be necessary.
	 * 
	 * @return true iff the lookup has to continue from the lookup coordinator
	 *         client's point of view.
	 */
	public boolean isContinuationNecessary();

	/**
	 * Called to notify the client that the coordinator has finished the lookup
	 * process. It is up to the client to decide whether the lookup was
	 * successful.
	 */
	public void coordinatorFinished();
}
