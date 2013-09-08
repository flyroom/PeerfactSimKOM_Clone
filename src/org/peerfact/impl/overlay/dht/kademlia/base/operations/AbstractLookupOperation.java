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
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayContact;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayID;
import org.peerfact.impl.overlay.dht.kademlia.base.components.Node;
import org.peerfact.impl.overlay.dht.kademlia.base.messages.DataMsg;
import org.peerfact.impl.overlay.dht.kademlia.base.messages.KademliaMsg;
import org.peerfact.impl.overlay.dht.kademlia.base.messages.NodeListMsg;
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
 * Abstract lookup operation that is used as a base class for node and data
 * lookup operations.
 * 
 * Internally, it uses a lookup coordinator that is responsible for coordinating
 * the flow of the lookup (who will be queried when etc.). The lookup
 * coordinator distinguishes between standard Kademlia, Kandy, and hierarchical
 * Kademlia).
 * 
 * This class and subclasses are responsible for handling message reception and
 * sending as well as the general issues related to operations (interaction with
 * the simulation environment and the client that uses the lookup operation).
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
abstract class AbstractLookupOperation<S, T extends KademliaOverlayID> extends
		AbstractKademliaOperation<S, T> implements LookupCoordinatorClient<T> {

	final static Logger log = SimLogger
			.getLogger(AbstractLookupOperation.class);

	/**
	 * A map that keeps track of queries that are currently in transit and
	 * awaiting a reply. No more than <code>KademliaConfig.ALPHA</code> messages
	 * may be in transit at the same time. Communication IDs as Integers are
	 * keys and HKademliaOverlayIDs are the values.
	 */
	private final Map<Integer, T> transit;

	/**
	 * A lookup coordinator that coordinates the flow of the lookup process. It
	 * distinguishes between standard Kademlia, Kandy, and hierarchical
	 * Kademlia.
	 */
	protected final LookupCoordinator<T> coordinator;

	/**
	 * Counts the hops of this lookup operation. Added by Leo Nobach
	 */
	private int hopCounter = 0;

	/**
	 * Constructs a new abstract lookup operation. This class does <i>not</i>
	 * register itself as a client of <code>coordinator</code>, that is up to
	 * the subclasses.
	 * 
	 * @param coordinator
	 *            the LookupCoordinator used for the lookup (distinguishes
	 *            between standard Kademlia, Kandy, and hierarchical Kademlia).
	 * @param node
	 *            the AbstractKademliaNode that initiates this lookup.
	 * 
	 * @param opCallback
	 *            a callback that is informed when this operation terminates.
	 * @param reason
	 *            why this operation is to be executed - either user-initiated
	 *            or for maintenance.
	 * @param conf
	 *            an OperationsConfig reference that permits to retrieve
	 *            configuration "constants".
	 */
	public AbstractLookupOperation(final LookupCoordinator<T> coordinator,
			final Node<T> node, final OperationCallback<?> opCallback,
			final Reason reason, final OperationsConfig conf) {
		super(node, opCallback, reason, conf);
		this.transit = new LinkedHashMap<Integer, T>(
				config.getMaxConcurrentLookups(), 1.0f);
		this.coordinator = coordinator;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int getTransitCount() {
		return transit.size();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * Called by the scheduler when this operation is initially started.
	 */
	@Override
	protected void execute() {
		scheduleOperationTimeout(config.getLookupOperationTimeout());
		coordinator.start();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void messageTimedOut(final int commId) {
		// mark contact as unresponsive
		final T unresponsive = transit.remove(commId);
		getComponent().getKademliaRoutingTable().markUnresponsiveContact(
				unresponsive);
		// log.debug("Msg timeout (commId=" + commId + "; destination="
		// + unresponsive + "; node=" + getComponent()); // TODO uncomment
		// notify coordinator so that it can send further messages
		final List<KademliaOverlayContact<T>> noResult = Collections
				.emptyList();
		coordinator.contactListReceived(noResult);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void messageReceived(final KademliaMsg<T> msg,
			final TransInfo senderAddr, final int commId) {
		// remove message from transit
		transit.remove(commId);
		// log.debug("Received reply (msg=" + msg + "; node=" + getComponent());
		// // TODO uncomment

		/*
		 * Although the sender of the message has already been seen (if not, how
		 * could we get a reply?), add it to the routing table to allow, for
		 * example, the stale counter to decrease.
		 */
		getComponent().addSenderToRoutingTable(msg, senderAddr);

		// if message handled externally, coordinator has to be notified there!
		if (msg instanceof NodeListMsg) {
			messageReceived((NodeListMsg<T>) msg);
		} else if (msg instanceof DataMsg) {
			messageReceived((DataMsg<T>) msg);
		} else {
			// unknown message type received - only notify coordinator
			final List<KademliaOverlayContact<T>> noResult = Collections
					.emptyList();
			coordinator.contactListReceived(noResult);
		}
	}

	/**
	 * A NodeListMsg has been received. This method should decide on what to do
	 * with it and notify the lookup coordinator to continue (
	 * {@link LookupCoordinator#contactListReceived(List)}.
	 * 
	 * @param message
	 *            the NodeListMsg that has been received.
	 */
	protected void messageReceived(final NodeListMsg<T> message) {
		final Collection<KademliaOverlayContact<T>> result = message.getNodes();
		// add contacts returned in message to routing table
		getComponent().addContactsToRoutingTable(result);
		// notify coordinator
		log.debug("Contact list received: " + result);
		coordinator.contactListReceived(result);
	}

	/**
	 * A DataMsg has been received. This method should decide on what to do with
	 * it and notify the lookup coordinator to continue (
	 * {@link LookupCoordinator#contactListReceived(List)}.
	 * 
	 * @param message
	 *            the DataMsg that has been received.
	 */
	protected void messageReceived(final DataMsg<T> message) {
		// this type of messages is ignored by default - only notify coordinator
		final List<KademliaOverlayContact<T>> noResult = Collections
				.emptyList();
		coordinator.contactListReceived(noResult);
	}

	/**
	 * Returns the hops that were made by this lookup operation.
	 */
	public int getHopCount() {
		return hopCounter;
	}

	/**
	 * {@inheritDoc}
	 */
	/*
	 * A message has been sent. The number of open RPCs (queries) will be
	 * increased. This method is automatically called and should not be called
	 * by subclasses.
	 */
	@Override
	protected final void messageSent(final int commId, final T destination) {
		transit.put(commId, destination);
		hopCounter++;
	}

}
