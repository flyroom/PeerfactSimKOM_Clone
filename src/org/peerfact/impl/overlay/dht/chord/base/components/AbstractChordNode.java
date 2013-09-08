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

package org.peerfact.impl.overlay.dht.chord.base.components;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.peerfact.api.common.ConnectivityEvent;
import org.peerfact.api.common.Message;
import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.overlay.NeighborDeterminator;
import org.peerfact.api.overlay.dht.DHTListener;
import org.peerfact.api.overlay.dht.DHTNode;
import org.peerfact.api.overlay.dht.DHTObject;
import org.peerfact.api.overlay.kbr.KBRForwardInformation;
import org.peerfact.api.overlay.kbr.KBRListener;
import org.peerfact.api.overlay.kbr.KBRLookupProvider;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.api.transport.TransLayer;
import org.peerfact.api.transport.TransProtocol;
import org.peerfact.impl.overlay.AbstractOverlayNode;
import org.peerfact.impl.overlay.dht.chord.base.callbacks.AbstractJoinOperation;
import org.peerfact.impl.overlay.dht.chord.base.callbacks.LeaveOperation;
import org.peerfact.impl.overlay.dht.chord.base.operations.AbstractChordOperation;
import org.peerfact.impl.overlay.dht.chord.base.operations.LookupOperation;
import org.peerfact.impl.overlay.dht.chord.base.operations.MonitorHostPropertiesOperation;
import org.peerfact.impl.overlay.dht.chord.base.operations.StoreOperation;
import org.peerfact.impl.overlay.dht.chord.base.operations.ValueLookupOperation;
import org.peerfact.impl.overlay.kbr.KBRMsgHandler;
import org.peerfact.impl.overlay.kbr.messages.KBRForwardInformationImpl;
import org.peerfact.impl.overlay.kbr.messages.KBRForwardMsg;
import org.peerfact.impl.service.dhtstorage.storage.SimpleDHTService;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.util.logging.SimLogger;

/**
 * 
 * This class represents a Peer/Host in Chord Overlay and the main
 * functionality.
 * 
 * @author Minh Hoang Nguyen <peerfact@kom.tu-darmstadt.de>
 * @author Thim Strothmann (adaptions)
 * 
 * @version 05/06/2011
 */
public abstract class AbstractChordNode extends
		AbstractOverlayNode<ChordID, AbstractChordContact>
		implements DHTNode<ChordID, AbstractChordContact, ChordKey> {

	private static Logger log = SimLogger.getLogger(AbstractChordNode.class);

	private final TransLayer transLayer;

	/**
	 * Flag to prevent automatical rejoins after churn related on-line events,
	 * if the initial node join was not initiated yet (using the join method) or
	 * the node has intentionally left the overlay (using the leave method).
	 */
	protected boolean rejoinOnOnlineEvent = false;

	/**
	 * Contain executing operations
	 */
	private final Map<Integer, AbstractChordOperation<?>> lookupOperationList = new LinkedHashMap<Integer, AbstractChordOperation<?>>();

	/**
	 * The Id of the next lookup
	 */
	private int nextLookupId = 0;

	/**
	 * A map containing all stored DHTObjects of this node
	 */
	private DHTListener<ChordKey> dht = new SimpleDHTService<ChordKey>();

	private ChordBootstrapManager bootstrap;

	private AbstractJoinOperation joinOperation = null;

	/**
	 * Standard Constructor for nearly all Nodes.
	 * 
	 * @param transLayer
	 * @param port
	 * @param bootstrap
	 */
	public AbstractChordNode(TransLayer transLayer, short port,
			ChordBootstrapManager bootstrap) {

		super(new ChordID(transLayer.getLocalTransInfo(port)), port);
		this.transLayer = transLayer;

		this.bootstrap = bootstrap;

		// begin hostpropertyMonitoring - for statistics
		new MonitorHostPropertiesOperation(this).scheduleImmediately();

	}

	/**
	 * ALternative Constructor for the ZHRemoteControl
	 * 
	 * @param peerId
	 * @param port
	 */
	public AbstractChordNode(ChordID peerId, short port) {
		super(peerId, port);
		this.transLayer = null;

	}

	/**
	 * Join the overlay with a delay
	 * 
	 * @param callback
	 * @return the Id of the JoinOperation
	 */
	public abstract int joinWithDelay(OperationCallback<Object> callback,
			long delay);

	/**
	 * Immediately join the overlay
	 * 
	 * @param callback
	 * @return the Id of the JoinOperation
	 */
	@Override
	public int join(OperationCallback<Object> callback) {
		setPeerStatus(PeerStatus.TO_JOIN);
		return joinWithDelay(callback, 0l);
	}

	/**
	 * Leave the overlay
	 * 
	 * @param callback
	 * @return the Id of the LeaveOperation
	 */
	@Override
	public int leave(OperationCallback<Object> callback) {
		// Node intentionally left --> Do not rejoin after churn on-line events
		this.rejoinOnOnlineEvent = false;
		bootstrap.unregisterNode(this);

		// Check if the node is present in the overlay
		if (getPeerStatus() != PeerStatus.PRESENT) {
			log.debug("Node initiated leave but was not present " + this
					+ " at Time[s] " + Simulator.getCurrentTime()
					/ Simulator.SECOND_UNIT);
			return -1;
		} else {
			log.debug("Node initiated leave " + this + " at Time[s] "
					+ Simulator.getCurrentTime() / Simulator.SECOND_UNIT);
		}

		LeaveOperation leave = new LeaveOperation(this, callback);
		leave.scheduleImmediately();
		return leave.getOperationID();
	}

	@Override
	public abstract void connectivityChanged(ConnectivityEvent ce);

	/**
	 * Find node that is responsible for the given key
	 * 
	 * @param key
	 *            the key to look up
	 * @param callback
	 * @return the Id of the LookupOperation, -1 if the node is not present in
	 *         the overlay
	 */
	public int overlayNodeLookup(ChordID key,
			OperationCallback<List<AbstractChordContact>> callback) {
		if (!isPresent()) {
			return -1;
		}

		log.debug("Start look up from node = " + this + " key = " + key);

		int lookupId = getNextLookupId();
		LookupOperation op = new LookupOperation(this, key, callback, lookupId);
		registerLookupOperation(lookupId, op);
		op.scheduleImmediately();

		return lookupId;
	}

	/**
	 * This method is called when leave operation is finished
	 */
	public abstract void leaveOperationFinished();

	// Getters and Setters

	@Override
	public boolean isPresent() {
		return getPeerStatus() == PeerStatus.PRESENT;
	}

	public AbstractChordRoutingTable getChordRoutingTable() {
		return (AbstractChordRoutingTable) routingTable;
	}

	@Override
	public TransLayer getTransLayer() {
		return transLayer;
	}

	public TransInfo getTransInfo() {
		return getTransLayer().getLocalTransInfo(getPort());
	}

	public AbstractJoinOperation getJoinOperation() {
		return joinOperation;
	}

	protected void registerLookupOperation(int lookupId,
			AbstractChordOperation<?> op) {
		lookupOperationList.put(lookupId, op);
	}

	public AbstractChordOperation<?> removeLookupOperation(int lookupId) {
		return lookupOperationList.remove(lookupId);
	}

	public AbstractChordOperation<?> getLookupOperation(int lookupId) {
		return lookupOperationList.get(lookupId);
	}

	public abstract AbstractChordMessageHandler getMessageHandler();

	@Override
	public String toString() {
		return "Node " + getOverlayID() + " " + getPeerStatus();
	}

	protected int getNextLookupId() {
		nextLookupId++;
		return nextLookupId;
	}

	public boolean isOnline() {
		return getHost().getNetLayer().isOnline();
	}

	public boolean absentCausedByChurn() {
		return (getPeerStatus() == PeerStatus.ABSENT) && rejoinOnOnlineEvent;
	}

	/*
	 * DHTNode methods
	 */

	@Override
	public int store(ChordKey key, DHTObject obj,
			OperationCallback<Set<AbstractChordContact>> callback) {
		if (!(key != null)) {
			return -1;
		}

		// Notify the analyzer
		Simulator.getMonitor().dhtStoreInitiated(this.getLocalOverlayContact(),
				key, obj);

		StoreOperation op = new StoreOperation(this,
				key.getCorrespondingID(), obj, callback);
		op.scheduleImmediately();
		return op.getOperationID();
	}

	@Override
	public int valueLookup(ChordKey key,
			OperationCallback<DHTObject> callback) {
		if (!(key != null)) {
			return -1;
		}

		// Inform the monitors about an initiated query
		Simulator.getMonitor()
				.dhtLookupInitiated(getLocalOverlayContact(), key);

		ValueLookupOperation op = new ValueLookupOperation(this,
				key.getCorrespondingID(), callback);
		op.scheduleImmediately();
		return op.getOperationID();
	}

	@Override
	public int nodeLookup(ChordKey key,
			OperationCallback<List<AbstractChordContact>> callback,
			boolean returnSingleNode) {
		if (!(key != null)) {
			return -1;
		}
		// Inform the monitors about an initiated query
		Simulator.getMonitor()
				.dhtLookupInitiated(getLocalOverlayContact(), key);

		return overlayNodeLookup(key.getCorrespondingID(),
				callback);
	}

	public DHTListener<ChordKey> getDHT() {
		return this.dht;
	}

	/*
	 * KBR methods
	 */

	protected KBRListener<ChordID, AbstractChordContact, ChordKey> kbrListener;

	private KBRLookupProvider<ChordID, AbstractChordContact, ChordKey> kbrLookupProvider;

	@Override
	public void route(ChordKey key, Message msg, AbstractChordContact hint) {
		if (getChordRoutingTable() == null) {
			return;
		}

		AbstractChordContact nextHop = null;
		if (hint != null) {
			nextHop = hint;
		} else if (key != null) {
			List<AbstractChordContact> hops = local_lookup(key, 1);
			if (hops.size() > 0) {
				nextHop = hops.get(0);
				// Inform the monitors about an initiated query
				Simulator.getMonitor().kbrQueryStarted(
						getLocalOverlayContact(),
						msg);
			} else {
				log.error("KBR route problem: local_lookup returns no next hops");
			}
		} else {
			log.error("KBR route problem: Both key and hint are null! No idea where to route the message.");
			return;
		}

		KBRForwardInformation<ChordID, AbstractChordContact, ChordKey> info = new KBRForwardInformationImpl<ChordID, AbstractChordContact, ChordKey>(
				key, msg,
				nextHop);
		kbrListener.forward(info);
		ChordKey nextKey = info.getKey();
		Message nextMsg = info.getMessage();
		nextHop = info.getNextHopAgent();

		if (nextHop != null) { // see kbrListener-Interface, stop Message if
			// nextHop = null
			KBRForwardMsg<ChordID, ChordKey> fm = new KBRForwardMsg<ChordID, ChordKey>(
					getOverlayID(),
					nextHop.getOverlayID(), nextKey, nextMsg);
			getTransLayer().send(fm, nextHop.getTransInfo(), getPort(),
					TransProtocol.UDP);
		}
	}

	@Override
	public List<AbstractChordContact> local_lookup(ChordKey key, int num) {
		if (getChordRoutingTable() == null) {
			return new LinkedList<AbstractChordContact>();
		}

		List<AbstractChordContact> nodes = getChordRoutingTable()
				.getClosestPrecedingFingers(
						key.getCorrespondingID(), num);

		// copied over from rechord - shouldn't do any damage to chord
		while (nodes.contains(null)) {
			nodes.remove(null);
		}

		if (nodes.isEmpty()) {
			nodes.add(getChordRoutingTable().getSuccessor());
		} else if (nodes.get(0).equals(getLocalOverlayContact())) {
			nodes.set(0, getChordRoutingTable().getSuccessor());
		}

		return nodes;
	}

	@Override
	public List<AbstractChordContact> replicaSet(ChordKey key, int maxRank) {
		List<AbstractChordContact> succs = getChordRoutingTable()
				.getSuccessors();

		if (maxRank < succs.size()) {
			return succs.subList(0, maxRank);
		}
		return succs;
	}

	@Override
	public List<AbstractChordContact> neighborSet(int num) {
		List<AbstractChordContact> neighbors = new LinkedList<AbstractChordContact>();
		List<AbstractChordContact> preds = getChordRoutingTable()
				.getPredecessors();
		List<AbstractChordContact> succs = getChordRoutingTable()
				.getSuccessors();

		for (int i = 0; i < num; i++) {
			if (i < preds.size()) {
				neighbors.add(preds.get(i));
			}
			if (i < succs.size() && neighbors.size() < num) {
				neighbors.add(succs.get(i));
			}
			if (neighbors.size() >= num
					|| i >= Math.max(preds.size(), succs.size())) {
				break;
			}
		}
		return neighbors;
	}

	@Override
	public ChordID[] range(AbstractChordContact contact, int rank) {
		/*
		 * FIXME: Look up concrete meaning of rank in KBR-Paper!
		 */

		ChordID[] range = new ChordID[2];

		ChordIDFactory.getInstance();
		range[0] = ChordIDFactory.getChordID(
				getChordRoutingTable().getPredecessor().getOverlayID()
						.getValue().add(BigInteger.ONE));

		range[1] = getOverlayID();

		return range;
	}

	@Override
	public boolean isRootOf(ChordKey key) {
		if (getChordRoutingTable() == null || (key == null)) {
			return false;
		}

		return getChordRoutingTable().responsibleFor(
				key.getCorrespondingID());
	}

	@Override
	public void setKBRListener(
			KBRListener<ChordID, AbstractChordContact, ChordKey> listener) {
		this.kbrListener = listener;
		KBRMsgHandler<ChordID, AbstractChordContact, ChordKey> msgHandler = new KBRMsgHandler<ChordID, AbstractChordContact, ChordKey>(
				this, this, kbrListener);

		kbrLookupProvider = msgHandler.getLookupProvider();
	}

	@Override
	public ChordKey getNewOverlayKey(int rank) {
		ChordIDFactory.getInstance();
		return ChordIDFactory.getChordID(String.valueOf(rank))
				.getCorrespondingKey();
	}

	@Override
	public ChordKey getRandomOverlayKey() {
		ChordIDFactory.getInstance();
		return ChordIDFactory.createRandomChordID()
				.getCorrespondingKey();
	}

	@Override
	public void hadContactTo(AbstractChordContact contact) {
		// Nothing to do here.
	}

	@Override
	public KBRLookupProvider<ChordID, AbstractChordContact, ChordKey> getKbrLookupProvider() {
		return kbrLookupProvider;
	}

	@Override
	public NeighborDeterminator<AbstractChordContact> getNeighbors() {
		return new NeighborDeterminator<AbstractChordContact>() {

			@Override
			public Collection<AbstractChordContact> getNeighbors() {
				if (getChordRoutingTable() == null) {
					return Collections.emptySet();
				}
				return getChordRoutingTable().getNeighbors();
			}
		};
	}

	public ChordBootstrapManager getBootstrapManager() {
		return bootstrap;
	}

	@Override
	public void registerDHTListener(DHTListener<ChordKey> listener) {
		dht = listener;
	}

}
