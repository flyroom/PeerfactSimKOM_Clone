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

package org.peerfact.impl.overlay.dht.kademlia.base.components;

import java.util.Collection;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jfree.util.Log;
import org.peerfact.api.common.ConnectivityEvent;
import org.peerfact.api.common.Operation;
import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.overlay.JoinLeaveOverlayNode;
import org.peerfact.api.overlay.NeighborDeterminator;
import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.api.overlay.OverlayRoutingTable;
import org.peerfact.api.overlay.dht.DHTListener;
import org.peerfact.api.overlay.dht.DHTObject;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.api.transport.TransLayer;
import org.peerfact.impl.common.Operations;
import org.peerfact.impl.overlay.AbstractOverlayNode;
import org.peerfact.impl.overlay.dht.kademlia.base.messages.KademliaMsg;
import org.peerfact.impl.overlay.dht.kademlia.base.operations.AbstractKademliaOperation.Reason;
import org.peerfact.impl.overlay.dht.kademlia.base.operations.KademliaOperation;
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
 * Abstract Kademlia overlay node that implements behaviour common to standard
 * Kademlia, Kandy (from the Chord paper) and hierarchical Kademlia. Some
 * methods have been copied from AbstractOverlayNode which has been the
 * superclass in a previous version.
 * 
 * An overlay node acts as a facade to the outside to represent a participant in
 * the overlay. To the inside, it is a container for a routing table, a local
 * database, a message handler to handle incoming, unrequested messages
 * (messages in response to an operation are handled directly in the requesting
 * operation) and an operation factory that permits to create operations
 * according to the flavour of Kademlia that is currently being used.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @author Sebastian Kaune
 * @version 05/06/2011
 */
public abstract class AbstractKademliaNode<T extends KademliaOverlayID> extends
		AbstractOverlayNode<T, KademliaOverlayContact<T>> implements Node<T>,
		JoinLeaveOverlayNode<T, KademliaOverlayContact<T>> {

	protected final static Logger log = SimLogger
			.getLogger(AbstractKademliaNode.class);

	/**
	 * The contact information for this node (port, network address etc.)
	 */
	private final KademliaOverlayContact<T> ownContact;

	/**
	 * A reference to the transport layer used to send messages.
	 */
	private final TransLayer transLayer;

	/**
	 * The local database (own "shared files").
	 */
	private final KademliaIndexer<T> localIndex;

	/**
	 * The handler of this node's online/offline state.
	 */
	private final StateHandler<T> stateHandler;

	/**
	 * Configuration values ("constants").
	 */
	protected final ComponentsConfig config;

	/**
	 * The local configuration store used for self optimization
	 */
	protected final LocalConfig localConfig;

	/**
	 * Constructs an abstract Kademlia node. Initialises commonly used
	 * components such as the local database. <b>The routing table, operation
	 * factory, and request handler have to be initialised (and possibly
	 * registered as listeners) in the subclasses.</b>
	 * 
	 * @param myContact
	 *            the KademliaOverlayContact of the new node.
	 * @param messageManager
	 *            the TransLayer of the new node.
	 * @param conf
	 *            a ComponentsConfig reference that permits to retrieve
	 *            configuration "constants".
	 */
	public AbstractKademliaNode(final KademliaOverlayContact<T> myContact,
			final TransLayer msgMgr, final ComponentsConfig conf) {

		super(myContact.getOverlayID(), myContact.getTransInfo().getPort());
		config = conf;
		ownContact = myContact;
		transLayer = msgMgr;
		stateHandler = new StateHandler<T>(this, conf);
		localIndex = new KademliaIndexer<T>(conf);

		/**
		 * Setup the local configuration store
		 */
		localConfig = new LocalConfig(conf);

		/*
		 * IMPORTANT: The handler for incoming (unsolicited) messages/requests
		 * and new close neighbours has to be constructed in each subclass after
		 * the routing table has been constructed!! That handler also needs to
		 * be registered with the routing table (as proximity listener) and with
		 * the transport layer (as transport message listener).
		 */

		// connect triggered externally
		// initial routing table contacts inserted externally
	}

	/*
	 * "Own" methods
	 */

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final KademliaIndexer<T> getLocalIndex() {
		return this.localIndex;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final KademliaOverlayContact<T> getLocalContact() {
		return this.ownContact;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final TransLayer getMessageManager() {
		return transLayer;
	}

	@Override
	public TransLayer getTransLayer() {
		return getMessageManager();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void addSenderToRoutingTable(final KademliaMsg<T> msg,
			final TransInfo senderAddr) {
		final KademliaOverlayContact<T> newCon = new KademliaOverlayContact<T>(
				msg.getSender(), senderAddr);
		getKademliaRoutingTable().addContact(newCon);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void addContactsToRoutingTable(
			final Collection<KademliaOverlayContact<T>> contacts) {
		for (final KademliaOverlayContact<T> contact : contacts) {
			getKademliaRoutingTable().addContact(contact);
		}
	}

	/*
	 * Methods from OverlayNode interface
	 */

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final T getTypedOverlayID() {
		return this.ownContact.getOverlayID();
	}

	@Override
	public T getOverlayID() {
		return this.ownContact.getOverlayID();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final short getPort() {
		return this.ownContact.getTransInfo().getPort();
	}

	/**
	 * Connects this Node to the Kademlia overlay network.
	 */
	public final void connect() {
		stateHandler.doConnect();
	}

	/**
	 * Disconnects this Node from the Kademlia overlay network.
	 */
	public final void disconnect() {
		stateHandler.doDisconnect();
	}

	/**
	 * Begins periodic lookups for random keys. (The lookups are executed only
	 * if this Node is already PRESENT.)
	 */
	public final void beginLookups() {
		stateHandler.enablePeriodicLookup();
	}

	/**
	 * Starts a single lookup for a rank along with an operation callback. Added
	 * by Leo Nobach
	 * 
	 * @param a
	 * @param callback
	 */
	public final void lookupRank(int rank, OperationCallback<DHTObject> callback) {
		stateHandler.doSingleLookup(KademliaOverlayKey.fromRank(rank, config),
				callback);
	}

	/**
	 * Starts a single lookup, only with a defined rank. Added by Leo Nobach
	 * 
	 * @param a
	 */
	public final void lookupRank(int rank) {
		lookupRank(rank, Operations.EMPTY_CALLBACK);
	}

	/**
	 * Stores the given data in the Kademlia overlay network.
	 * 
	 * @param key
	 *            a String that will be converted into a KademliaOverlayKey.
	 * @param data
	 *            a String that represents the data item (arbitrary).
	 */
	public final void store(final String key, final String data) {
		store(key, data, Operations.EMPTY_CALLBACK);
	}

	public final void store(final String key, final String data,
			OperationCallback<DHTObject> callback) {
		store(new KademliaOverlayKey(key, config), data, callback);
	}

	/**
	 * Stores a kademlia key and its data with the specified rank.
	 * 
	 * @param rank
	 * @param data
	 * @param callback
	 */
	public final void storeRank(final int rank, final String data,
			OperationCallback<DHTObject> callback) {
		store(KademliaOverlayKey.fromRank(rank, config), data, callback);
	}

	/**
	 * Stores the given data in the Kademlia overlay network and provides a
	 * callback
	 * 
	 * @param key
	 *            a String that will be converted into a KademliaOverlayKey.
	 * @param data
	 *            a String that represents the data item (arbitrary).
	 * @param callback
	 */
	public final void store(KademliaOverlayKey key, final String data,
			OperationCallback<DHTObject> callback) {
		getOperationFactory().getStoreOperation(new DHTObject() {
			@Override
			public String toString() {
				return data;
			}

			@Override
			public long getTransmissionSize() {
				return data.length();
			}
		}, key, true, Reason.USER_INITIATED, callback).scheduleImmediately();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void routingTableBuilt() {
		stateHandler.routingTableBuilt();
	}

	/*
	 * Methods transfered from BaseOverlayNode
	 */

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final PeerStatus getPeerStatus() {
		return stateHandler.getPeerStatus();
	}

	@Override
	public void connectivityChanged(ConnectivityEvent ce) {
		if (ce.isOffline()) {
			setPeerStatus(PeerStatus.ABSENT);
		} else if (ce.isOnline()) {
			setPeerStatus(PeerStatus.PRESENT);
		}
	}

	@Override
	public OverlayRoutingTable<T, ? extends OverlayContact<T>> getRoutingTable() {
		// TODO: Write wrapper for RoutingTable<T>
		throw new UnsupportedOperationException(
				"This overlay does not support this call yet ");
	}

	/**
	 * @return the local configuration store of a node.
	 */
	public LocalConfig getLocalConfig() {
		return localConfig;
	}

	@Override
	public int join(OperationCallback<Object> callback) {
		connect();
		return -1;
	}

	@Override
	public int leave(OperationCallback<Object> callback) {
		disconnect();
		return -1;
	}

	public int store(KademliaOverlayKey key, DHTObject obj,
			OperationCallback<Set<KademliaOverlayContact<T>>> callback) {

		if (!(key != null)) {
			return -1;
		}

		Operation<?> op = getOperationFactory()
				.getStoreOperation(obj, key, true,
						Reason.USER_INITIATED, callback);
		op.scheduleImmediately();
		return op.getOperationID();
	}

	public int valueLookup(KademliaOverlayKey key,
			OperationCallback<DHTObject> callback) {

		if (key == null) {
			return -1;
		}
		// Inform the monitors about an initiated query
		Simulator.getMonitor().dhtLookupInitiated(getLocalContact(), key);

		KademliaOperation<DHTObject> op = getOperationFactory()
				.getDataLookupOperation(key, callback);
		op.scheduleImmediately();

		return op.getOperationID();
	}

	@Override
	public NeighborDeterminator<KademliaOverlayContact<T>> getNeighbors() {
		return this.getKademliaRoutingTable();
	}

	public void registerDHTListener(
			DHTListener<KademliaOverlayKey> listener) {
		// do nothing. Kademlia has own Indexer.
		Log.warn("Kademlia does not support a DHTListener.");
	}

}
