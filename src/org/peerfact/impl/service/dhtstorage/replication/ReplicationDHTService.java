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

package org.peerfact.impl.service.dhtstorage.replication;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.jfree.util.Log;
import org.peerfact.api.common.ConnectivityEvent;
import org.peerfact.api.common.ConnectivityListener;
import org.peerfact.api.common.Host;
import org.peerfact.api.common.Message;
import org.peerfact.api.common.Operation;
import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.overlay.NeighborDeterminator;
import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.api.overlay.OverlayID;
import org.peerfact.api.overlay.dht.DHTEntry;
import org.peerfact.api.overlay.dht.DHTKey;
import org.peerfact.api.overlay.dht.DHTListenerSupported;
import org.peerfact.api.overlay.dht.DHTValue;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.api.transport.TransMessageListener;
import org.peerfact.impl.common.Operations;
import org.peerfact.impl.service.dhtstorage.AbstractDHTService;
import org.peerfact.impl.service.dhtstorage.replication.messages.AckMessage;
import org.peerfact.impl.service.dhtstorage.replication.messages.DeleteReplicationMessage;
import org.peerfact.impl.service.dhtstorage.replication.messages.KeepReplicationMessage;
import org.peerfact.impl.service.dhtstorage.replication.messages.NewRootMessage;
import org.peerfact.impl.service.dhtstorage.replication.messages.PingMessage;
import org.peerfact.impl.service.dhtstorage.replication.messages.ReplicationDHTMessage;
import org.peerfact.impl.service.dhtstorage.replication.messages.StoreReplicationMessage;
import org.peerfact.impl.service.dhtstorage.replication.operations.CheckRootOperation;
import org.peerfact.impl.service.dhtstorage.replication.operations.NewRootOperation;
import org.peerfact.impl.service.dhtstorage.replication.operations.PeriodicRepublishOperation;
import org.peerfact.impl.service.dhtstorage.replication.operations.ReplicationOperation;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.transport.DefaultTransInfo;
import org.peerfact.impl.transport.TransMsgEvent;


/**
 * DHT-Service that replicates each DHTObject on k Neighboring Nodes to increase
 * reliability. Each file has a "root" node that is responsible for this file.
 * The root gets polled periodically by nodes it distributed files to, if it
 * does not answer an election between this nodes is started to determine which
 * one will become the new root. Therefore contact information of all nodes that
 * hold a replication of a file are sent with the replication. The order of this
 * contact information determines the rank of a node, higher ranked nodes will
 * become the new root.
 * 
 * 
 * @author Bjoern Richerzhagen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class ReplicationDHTService<K extends DHTKey<?>> extends
		AbstractDHTService<K> implements
		TransMessageListener {

	/**
	 * All known Contacts, stores centrally when they had their last
	 * interaction. Prevents duplicate Operations to find out if a contact still
	 * exists
	 */
	private Map<TransInfo, ReplicationDHTContact> knownContacts;

	private ReplicationDHTConfig config;

	private short port;

	protected boolean isOnline;

	private Map<K, NewRootOperation<K>> waitingNewRootOperations = new LinkedHashMap<K, NewRootOperation<K>>();

	/**
	 * Start a new Replication-Service
	 * 
	 * @param node
	 *            The node to register this Service as a Listener
	 */
	public ReplicationDHTService(
			Host host,
			short port,
			DHTListenerSupported<OverlayID<?>, OverlayContact<OverlayID<?>>, K> node,
			ReplicationDHTConfig config) {
		super(node);
		setHost(host);
		isOnline = false;
		host.getProperties().addConnectivityListener(
				new ConnectivityListener() {
					@Override
					public void connectivityChanged(ConnectivityEvent ce) {
						if (ce.isOnline()) {
							start();
						}
						if (ce.isOffline()) {
							stop();
						}
					}
				});
		host.getTransLayer().addTransMsgListener(this, port);

		this.port = port;
		this.config = config;
		knownContacts = new LinkedHashMap<TransInfo, ReplicationDHTContact>();
	}

	/**
	 * Starts this service and all periodically executed routines
	 */
	protected void start() {
		isOnline = true;
		checkAllRootsOperation();
		checkOldReplicatesOperation();
	}

	/**
	 * Pauses this service
	 */
	protected void stop() {
		isOnline = false;
	}

	/**
	 * Add object locally and replicate among k neighboring nodes
	 * 
	 * @param key
	 *            The key of the object to store
	 * @param obj
	 *            The DHTObject to store
	 */
	@Override
	public void addDHTEntry(K key, DHTValue value) {
		List<TransInfo> replicateTargets = getNeighborsContacts();

		// Create Object and store locally (this node is root)
		ReplicationDHTObject<K> object = new ReplicationDHTObject<K>(key,
				value,
				null,
				replicateTargets);

		addDHTEntry(object);

		// Distribute Object to all Targets
		replicateObject(key, value, replicateTargets);
	}

	/**
	 * Start a ReplicateOperation
	 * 
	 * @param key
	 * @param value
	 * @param contacts
	 */
	protected void replicateObject(K key, DHTValue value,
			List<TransInfo> contacts) {
		ReplicationOperation<K> op = new ReplicationOperation<K>(this,
				Operations.getEmptyCallback(), getConfig(),
				new ReplicationDHTObject<K>(key, value, null, null),
				contacts);
		op.scheduleImmediately();
	}

	/**
	 * periodically iterate over all DHTObjects and contact roots. If a root
	 * does not reply this node will try to become the new root for all files
	 * hosted by the old root. It therefore contacts all other replicators of a
	 * file with a RootReplace-Message, which basically tells them to replace
	 * root with this contact
	 * 
	 */
	protected void checkAllRootsOperation() {
		if (!isOnline()) {
			return;
		}
		CheckRootOperation<K> op = new CheckRootOperation<K>(this,
				new OperationCallback<Object>() {
					@Override
					public void calledOperationFailed(
							Operation<Object> operation) {
						// this does not happen...
						Log.error("checkRootsOperation failed??");
					}

					@Override
					public void calledOperationSucceeded(
							Operation<Object> operation) {
						// Restart
						checkAllRootsOperation();
					}
				}, getConfig());
		op.scheduleWithDelay(getConfig().getTimeBetweenRootPings());
	}

	/**
	 * periodically iterate over all DHTObjects and republish the file, if a
	 * certain threshold of Contacts had no interaction within a defined period
	 * of time. If they are still alive they should have contacted this node via
	 * their CheckRootOperation which will update their LastSeen-Timestamp
	 */
	protected void checkOldReplicatesOperation() {
		if (!isOnline()) {
			return;
		}

		PeriodicRepublishOperation<K> op = new PeriodicRepublishOperation<K>(
				this,
				new OperationCallback<Object>() {
					@Override
					public void calledOperationFailed(
							Operation<Object> operation) {
						// this does not happen...
						Log.error("periodicRepublishOperation failed??");
					}

					@Override
					public void calledOperationSucceeded(
							Operation<Object> operation) {
						// Restart
						checkOldReplicatesOperation();
					}
				}, getConfig());
		op.scheduleWithDelay(getConfig().getTimeBetweenReplicationChecks());
	}

	/**
	 * Needs to inform all holders of replications about the deletion
	 */
	@Override
	public void removeDHTEntry(K key) {
		ReplicationDHTObject<K> object = (ReplicationDHTObject<K>) getDHTValue(key);
		ReplicationOperation<K> op = new ReplicationOperation<K>(this,
				Operations.getEmptyCallback(), getConfig(), object, null);
		op.scheduleImmediately();
		removeDHTEntryLocally(key);
	}

	/**
	 * Used in this service to remove an entry withour triggering removal on all
	 * replications.
	 * 
	 * @param key
	 */
	protected void removeDHTEntryLocally(K key) {
		super.removeDHTEntry(key);
	}

	/**
	 * Get a List of Neighbors TransInfo. It is important to note that the port
	 * will be changed to reflect the port of the DHTService, so all Instances
	 * of DHTService should use the same port number!
	 * 
	 * @return List of TransInfo, sorted as provided by INeighborDeterminator
	 */
	public List<TransInfo> getNeighborsContacts() {
		NeighborDeterminator<OverlayContact<OverlayID<?>>> neighbors = getNode()
				.getNeighbors();
		List<TransInfo> ret = new Vector<TransInfo>();

		if (neighbors == null) {
			return ret;
		}

		// Add NumberOfReplicates Contacts
		int i = getConfig().getNumberOfReplicates();
		for (OverlayContact<?> contact : neighbors.getNeighbors()) {
			TransInfo cTransInfo = contact.getTransInfo();
			// change port, as Service does not operate on same port as
			// Overlay!!
			TransInfo newTransInfo = DefaultTransInfo.getTransInfo(
					cTransInfo.getNetId(), getPort());
			ret.add(newTransInfo);
			addContact(newTransInfo);
			if (i == 0) {
				return ret;
			}
			i--;
		}
		return ret;
	}

	public short getPort() {
		return port;
	}

	protected ReplicationDHTConfig getConfig() {
		return config;
	}

	/**
	 * Return Contact-Instance of a Node
	 * 
	 * @param transInfo
	 * @return
	 */
	public ReplicationDHTContact getContact(TransInfo transInfo) {
		if (knownContacts.get(transInfo) == null) {
			knownContacts.put(transInfo, new ReplicationDHTContact(transInfo,
					getConfig().getTimeToLiveForContacts()));
		}
		return knownContacts.get(transInfo);
	}

	/**
	 * Add a new Contact to local storage
	 * 
	 * @param transInfo
	 */
	protected void addContact(TransInfo transInfo) {
		knownContacts.put(transInfo, new ReplicationDHTContact(transInfo,
				getConfig().getTimeToLiveForContacts()));
	}

	/**
	 * Inform Service of an offline contact
	 */
	public void contactDidNotRespond(TransInfo transInfo) {
		getContact(transInfo).markAsOffline();
		for (DHTEntry<K> entry : getDHTEntries()) {
			ReplicationDHTObject<K> obj = (ReplicationDHTObject<K>) entry;
			if (obj.getRoot() == null || !obj.getRoot().equals(transInfo)) {
				continue;
			}

			if (!waitingNewRootOperations.containsKey(obj.getKey())
					|| waitingNewRootOperations.get(obj.getKey()).isFinished()) {
				int i = obj.getReplications().indexOf(getOwnTransInfo());
				if (i >= 0) {
					NewRootOperation<K> op = new NewRootOperation<K>(this,
							Operations.getEmptyCallback(), getConfig(), obj);
					op.scheduleWithDelay((i + 1) * Simulator.MINUTE_UNIT);
					waitingNewRootOperations.put(obj.getKey(), op);
				}
			}
		}
	}

	private void cancelNewRootOperation(K key) {
		NewRootOperation<K> op = waitingNewRootOperations.get(key);
		if (op != null) {
			op.cancel();
		}
	}

	/**
	 * A Contact had interaction with the service
	 * 
	 * @param transInfo
	 */
	public void contactDidRespond(TransInfo transInfo) {
		getContact(transInfo).updateLastAction();
	}

	/*
	 * TRANS-MESSAGE Listener
	 */

	public TransInfo getOwnTransInfo() {
		return getHost().getTransLayer().getLocalTransInfo(getPort());
	}

	/**
	 * Service acts as TransMessageListener for direct Messages
	 */
	@Override
	public void messageArrived(TransMsgEvent receivingEvent) {
		// Only responsible for Service-Messages
		Message msg = receivingEvent.getPayload();
		if (msg instanceof ReplicationDHTMessage) {
			if (msg instanceof StoreReplicationMessage) {
				handleStoreReplicationMessage(
						receivingEvent.getSenderTransInfo(),
						(StoreReplicationMessage) msg);
				sendReply(receivingEvent);
			}

			if (msg instanceof KeepReplicationMessage) {
				handleKeepReplicationMessage(
						receivingEvent.getSenderTransInfo(),
						(KeepReplicationMessage) msg);
				sendReply(receivingEvent);
			}

			if (msg instanceof DeleteReplicationMessage) {
				handleDeleteReplicationMessage(
						receivingEvent.getSenderTransInfo(),
						(DeleteReplicationMessage<K>) msg);
				sendReply(receivingEvent);
			}

			if (msg instanceof NewRootMessage<?>) {
				handleNewRootMessage((NewRootMessage<?>) msg);
				sendReply(receivingEvent);
			}

			// Just a Ping, reply with Ack
			if (msg instanceof PingMessage) {
				contactDidRespond(receivingEvent.getSenderTransInfo());
				sendReply(receivingEvent);
			}
		}
	}

	/**
	 * Message contains an object that is a replication and needs to be stored
	 * on this instance
	 * 
	 * @param msg
	 */
	private void handleStoreReplicationMessage(TransInfo root,
			StoreReplicationMessage msg) {
		ReplicationDHTObject<K> object = new ReplicationDHTObject<K>(
				(K) msg.getKey(),
				msg.getObject(),
				root, msg.getDuplicatesContacts());
		// Add contact Information of root
		addContact(root);
		cancelNewRootOperation(object.getKey());
		addDHTEntry(object);
	}

	/**
	 * Signals the node to keep the entry. Possible future use might be a
	 * timeout for objects
	 * 
	 * @param root
	 * @param msg
	 */
	private void handleKeepReplicationMessage(TransInfo root,
			KeepReplicationMessage msg) {
		getContact(root).updateLastAction();
		cancelNewRootOperation((K) msg.getKey());
		// TODO max StorageLifetime for Objects?
	}

	/**
	 * Notifies this node, that the entry is not valid anymore
	 * 
	 * @param root
	 * @param msg
	 */
	private void handleDeleteReplicationMessage(TransInfo root,
			DeleteReplicationMessage<K> msg) {
		getContact(root).updateLastAction();
		removeDHTEntryLocally(msg.getKey());
		cancelNewRootOperation(msg.getKey());
	}

	/**
	 * Receiving this message will trigger a new ReplicationOperation that will
	 * announce to all receivers that this node is the new root
	 * 
	 * @param msg
	 */
	private void handleNewRootMessage(NewRootMessage<?> msg) {
		ReplicationDHTObject<?> entry = (ReplicationDHTObject<?>) getDHTEntry((K) msg
				.getKey());
		if (entry != null && entry.getRoot() != null) {
			List<TransInfo> replicateTargets = getNeighborsContacts();
			entry.getReplications().remove(
					getHost().getTransLayer().getLocalTransInfo(getPort()));

			// Create Object and store locally (this node is root)
			ReplicationDHTObject<?> object = new ReplicationDHTObject(
					entry.getKey(), entry.getValue(), null,
					replicateTargets);

			ReplicationOperation<?> op = new ReplicationOperation(this,
					Operations.getEmptyCallback(), getConfig(), object,
					entry.getReplications());
			op.scheduleImmediately();

			// removeDHTEntry(entry.getKey());
			addDHTEntry((DHTEntry<K>) object);
			cancelNewRootOperation((K) object.getKey());
			System.err.println("NEW ROOT OPERATION ARRIVED AT... NEW ROOT!");
		}
	}

	/**
	 * Send an ACK-Message
	 * 
	 * @param receivingEvent
	 */
	protected void sendReply(TransMsgEvent receivingEvent) {
		getHost().getTransLayer().sendReply(new AckMessage(), receivingEvent,
				getPort(), receivingEvent.getProtocol());
	}

	/**
	 * is this service instance online?
	 * 
	 * @return
	 */
	protected boolean isOnline() {
		return isOnline;
	}

}
