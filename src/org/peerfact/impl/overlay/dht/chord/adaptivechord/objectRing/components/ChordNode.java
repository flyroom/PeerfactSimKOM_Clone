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

package org.peerfact.impl.overlay.dht.chord.adaptivechord.objectRing.components;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;
import org.peerfact.api.common.ConnectivityEvent;
import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.overlay.OverlayID;
import org.peerfact.api.overlay.OverlayNode;
import org.peerfact.api.overlay.dht.DHTEntry;
import org.peerfact.api.overlay.dht.DHTObject;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.api.transport.TransLayer;
import org.peerfact.impl.common.Operations;
import org.peerfact.impl.overlay.dht.chord.adaptivechord.capacityRing.components.AdaptiveChordNode;
import org.peerfact.impl.overlay.dht.chord.adaptivechord.capacityRing.interfaces.AdaptiveRemoteControl;
import org.peerfact.impl.overlay.dht.chord.adaptivechord.capacityRing.interfaces.IChangableIdent;
import org.peerfact.impl.overlay.dht.chord.adaptivechord.objectRing.callbacks.JoinOperation;
import org.peerfact.impl.overlay.dht.chord.adaptivechord.objectRing.messages.RemovedMirrorMessage;
import org.peerfact.impl.overlay.dht.chord.adaptivechord.objectRing.operations.CheckMirrorOperation;
import org.peerfact.impl.overlay.dht.chord.adaptivechord.objectRing.operations.ChordOperationListener;
import org.peerfact.impl.overlay.dht.chord.adaptivechord.objectRing.operations.CommunicateMirrorRemovedOperation;
import org.peerfact.impl.overlay.dht.chord.adaptivechord.objectRing.operations.CreateMirrorOperation;
import org.peerfact.impl.overlay.dht.chord.adaptivechord.objectRing.operations.MirrorMaintanceOperation;
import org.peerfact.impl.overlay.dht.chord.adaptivechord.objectRing.operations.RejoinOperation;
import org.peerfact.impl.overlay.dht.chord.base.callbacks.LeaveOperation;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordContact;
import org.peerfact.impl.overlay.dht.chord.base.components.ChordBootstrapManager;
import org.peerfact.impl.overlay.dht.chord.base.components.ChordID;
import org.peerfact.impl.overlay.dht.chord.base.components.ChordKey;
import org.peerfact.impl.overlay.dht.chord.base.operations.AbstractChordOperation;
import org.peerfact.impl.overlay.dht.chord.base.operations.LookupOperation;
import org.peerfact.impl.overlay.dht.chord.base.operations.StoreOperation;
import org.peerfact.impl.overlay.dht.chord.base.operations.ValueLookupOperation;
import org.peerfact.impl.service.aggregation.skyeye.AbstractSkyNetNode;
import org.peerfact.impl.service.aggregation.skyeye.components.SkyNetNode;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.util.Tuple;
import org.peerfact.impl.util.logging.SimLogger;


/**
 * 
 * This class represents a Peer/Host in Chord Overlay and the main
 * functionality.
 * 
 * @author Minh Hoang Nguyen <peerfact@kom.tu-darmstadt.de>
 * @author Thim Strothmann (Adaptions)
 * 
 * @version 05/06/2011
 */

public class ChordNode extends AdaptiveRemoteControl implements IChangableIdent {

	private static Logger log = SimLogger.getLogger(ChordNode.class);

	private final ChordMessageHandler messageHandler;

	/**
	 * handle operation time out, deliver operation results
	 */
	private ChordOperationListener operationListener;

	/**
	 * Contain executing operations
	 */
	private final Map<Integer, AbstractChordOperation<?>> lookupOperationList = new LinkedHashMap<Integer, AbstractChordOperation<?>>();

	/**
	 * The Id of the next lookup
	 */
	private int nextLookupId = 0;

	/**
	 * a LinkedHashMap containing the current load of each document provided by
	 * this node.
	 */
	private LinkedHashMap<ChordKey, Double> loadMap = new LinkedHashMap<ChordKey, Double>();

	/**
	 * stores the last 10 timestamps of accesses for each document.
	 */
	private LinkedHashMap<ChordKey, LinkedList<Long>> accessMap = new LinkedHashMap<ChordKey, LinkedList<Long>>();

	/**
	 * stores mirrors for a document if this document has been outsourced.
	 */
	private LinkedHashMap<ChordKey, AbstractChordContact> mirrorMap = new LinkedHashMap<ChordKey, AbstractChordContact>();

	/**
	 * stores documents that have been mirrored to this node.
	 */
	private LinkedHashMap<ChordKey, DHTObject> mirroredObjects = new LinkedHashMap<ChordKey, DHTObject>();

	private LinkedHashMap<ChordKey, AbstractChordContact> mirroredObjectsOrigin = new LinkedHashMap<ChordKey, AbstractChordContact>();

	/**
	 * stores for each document that has been mirrored to this node when the
	 * last CheckMirrorMessage from the original node was received.
	 */
	private LinkedHashMap<ChordKey, Long> mirroredObjectsLastContactToOrigin = new LinkedHashMap<ChordKey, Long>();

	/**
	 * type of the node
	 */
	private ChordNodeType nodeType;

	/**
	 * id that this node will use at the next rejoin
	 */
	public ChordID iDOnNextRejoin = null;

	/**
	 * owning ZHNode
	 */
	public AdaptiveChordNode zhNode = null;

	/**
	 * minimum number of served queries a node has to serve for a document
	 * before thinking about giving responsibility for the document away.
	 */
	private final int MIN_HIT_COUNT = 1;

	private boolean firstJoin = true;

	public double minimalMirrorBandwidth = ChordConfiguration.MINIMAL_MIRROR_BANDWIDTH_DEFAULT;

	public int failedMirrorOperationCount = 0;

	/**
	 * INTERVALS:
	 */
	public long UPDATE_SUCCESSOR_INTERVAL;

	public long UPDATE_FINGERTABLE_INTERVAL;

	public long UPDATE_NEIGHBOURS_INTERVAL;

	public int STORED_NEIGHBOURS;

	public void initLoadMap(ChordKey k) {
		if (!this.loadMap.containsKey(k)) {
			this.loadMap.put(k, 0.0);
		}

		if (!accessMap.containsKey(k)) {
			accessMap.put(k, new LinkedList<Long>());
		}
	}

	public ChordNodeType getNodeType() {
		return this.nodeType;
	}

	@Override
	public void setNodeType(ChordNodeType t) {
		this.nodeType = t;

		if (this.nodeType == ChordNodeType.DATANETNODE) {
			this.UPDATE_SUCCESSOR_INTERVAL = ChordConfiguration.UPDATE_SUCCESSOR_INTERVAL_DATANET;
			this.UPDATE_FINGERTABLE_INTERVAL = ChordConfiguration.UPDATE_FINGERTABLE_INTERVAL_DATANET;
			this.UPDATE_NEIGHBOURS_INTERVAL = ChordConfiguration.UPDATE_NEIGHBOURS_INTERVAL_DATANET;
			this.STORED_NEIGHBOURS = ChordConfiguration.STORED_NEIGHBOURS_DATANET;
		}
		if (this.nodeType == ChordNodeType.LOADBALANCINGNETNODE) {
			this.UPDATE_SUCCESSOR_INTERVAL = ChordConfiguration.UPDATE_SUCCESSOR_INTERVAL_LOADNET;
			this.UPDATE_FINGERTABLE_INTERVAL = ChordConfiguration.UPDATE_FINGERTABLE_INTERVAL_LOADNET;
			this.UPDATE_NEIGHBOURS_INTERVAL = ChordConfiguration.UPDATE_NEIGHBOURS_INTERVAL_LOADNET;
			this.STORED_NEIGHBOURS = ChordConfiguration.STORED_NEIGHBOURS_LOADNET;
		}

	}

	public void incrementMinimalMirrorBandwidth() {
		double newminimalMirrorBandwidth = minimalMirrorBandwidth
				+ minimalMirrorBandwidth
				* ChordConfiguration.MIRROR_BANDWIDTH_INCREMENT_FACTOR;

		if (newminimalMirrorBandwidth < AdaptiveRemoteControl.MAXIMUM_UPLOAD_BANDWIDTH) {
			minimalMirrorBandwidth = newminimalMirrorBandwidth;
		}

	}

	public void decrementMinimalMirrorBandwidth() {
		minimalMirrorBandwidth -= minimalMirrorBandwidth
				* ChordConfiguration.MIRROR_BANDWIDTH_INCREMENT_FACTOR;

		if (minimalMirrorBandwidth <= ChordConfiguration.MINIMAL_USABLE_BANDWIDTH) {
			minimalMirrorBandwidth = ChordConfiguration.MINIMAL_MIRROR_BANDWIDTH_DEFAULT;
		}
	}

	/**
	 * @param transLayer
	 * @param port
	 * @param bootstrap
	 */
	public ChordNode(TransLayer transLayer, short port,
			ChordBootstrapManager bootstrap) {

		super(transLayer, port, bootstrap);

		operationListener = new ChordOperationListener(this);

		// create message handler
		messageHandler = new ChordMessageHandler(this);
		// add message handler to the listeners list
		this.getTransLayer().addTransMsgListener(this.messageHandler,
				this.getPort());

		// set intervals - assume default is datanet
		this.UPDATE_SUCCESSOR_INTERVAL = ChordConfiguration.UPDATE_SUCCESSOR_INTERVAL_DATANET;
		this.UPDATE_FINGERTABLE_INTERVAL = ChordConfiguration.UPDATE_FINGERTABLE_INTERVAL_DATANET;
		this.UPDATE_NEIGHBOURS_INTERVAL = ChordConfiguration.UPDATE_NEIGHBOURS_INTERVAL_DATANET;
		this.STORED_NEIGHBOURS = ChordConfiguration.STORED_NEIGHBOURS_DATANET;

	}

	public void DEBUG_showLoads() {
		for (Tuple<ChordKey, Double> l : this.getOfferedObjects()) {
			log.debug(l.getA() + " creates load " + l.getB());
		}
	}

	public double DEBUG_getFreeBandwidth() {
		return this.getHost().getProperties().getMaxUploadBandwidth()
				- getLoad();
	}

	public int DEBUG_getServedCounter(ChordKey key) {
		if (this.accessMap.containsKey(key)) {
			return this.accessMap.get(key).size();
		} else {
			return 0;
		}
	}

	@Override
	public void initReoccuringOperations() {
		if (this.nodeType == ChordNodeType.DATANETNODE) {

			// use rand that not everything is parallel...
			long randPart = (long) (Simulator.getRandom().nextDouble() * (Simulator.MINUTE_UNIT * 10));

			MirrorMaintanceOperation newop = new MirrorMaintanceOperation(this);
			newop.scheduleWithDelay(ChordConfiguration.CHECK_MIRROR_INTERVAL
					+ randPart);
		}

	}

	public void clearRoutingtable() {
		if (this.routingTable != null) {
			this.routingTable = new ChordRoutingTable(this,
					this.getLocalOverlayContact(),
					((ChordRoutingTable) this.routingTable).getSuccessor(),
					null);
		}
	}

	private JoinOperation joinOperation = null;

	public ChordID getChordIdCorrespondingToRemainingLoad() {
		double load = getPerformanceIndex();
		if (load < 0) {
			load = 0;
		}

		// calculate the id in the load network:
		BigDecimal bl = new BigDecimal(load);
		BigDecimal temp;
		temp = new BigDecimal(bl.toPlainString());
		temp = temp.multiply(new BigDecimal(
				"1461501637330902918203684832716283019655932542975"));

		Integer rand = Integer.valueOf(Simulator.getRandom().nextInt(
				ChordConfiguration.RANDOM_TIE_BREAKER_SIZE));
		temp = temp.add(new BigDecimal(rand.toString()));

		BigInteger newid = temp.toBigInteger();
		return new ChordID(newid);
	}

	/**
	 * Join the overlay with a delay
	 * 
	 * @param callback
	 * @return the Id of the JoinOperation
	 */
	@Override
	public int joinWithDelay(OperationCallback<Object> callback, long delay) {
		setPeerStatus(PeerStatus.TO_JOIN);
		// Node intentionally joined --> Do rejoins after churn on-line events
		this.rejoinOnOnlineEvent = true;

		if (firstJoin) {
			firstJoin = false;
			if (this.nodeType == ChordNodeType.LOADBALANCINGNETNODE) {
				this.setOverlayID(getChordIdCorrespondingToRemainingLoad());
			}
		}

		log.debug("Node initiated join " + this + " at Time[s] "
				+ Simulator.getCurrentTime() / Simulator.SECOND_UNIT);
		joinOperation = new JoinOperation(this, callback);

		if (delay > 0) {
			joinOperation.scheduleWithDelay(delay);
		} else {
			joinOperation.scheduleImmediately();
		}

		return joinOperation.getOperationID();
	}

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
		super.getBootstrapManager().unregisterNode(this);

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
	public void connectivityChanged(ConnectivityEvent ce) {

		log.debug("Connectivity changed " + this + " to online="
				+ ce.isOnline());
		if (ce.isOnline()) {
			if (getPeerStatus().equals(PeerStatus.ABSENT)
					&& this.rejoinOnOnlineEvent) {

				log.debug(Simulator.getSimulatedRealtime() + " Peer "
						+ getHost().getNetLayer().getNetID()
						+ " received online event ");
				setPeerStatus(PeerStatus.TO_JOIN);
				join(Operations.EMPTY_CALLBACK);
			}
		} else if (ce.isOffline()) {
			if (getPeerStatus().equals(PeerStatus.PRESENT)
					|| getPeerStatus().equals(PeerStatus.TO_JOIN)) {

				log.debug(Simulator.getSimulatedRealtime() + " Peer "
						+ getHost().getNetLayer().getNetID()
						+ " is affected by churn ");

				setPeerStatus(PeerStatus.ABSENT);

				// Mark the old listener as inactive and create an new one
				operationListener.setInactive();
				operationListener = new ChordOperationListener(this);

				// Mark the old routing table as inactive
				if (routingTable != null) {
					((ChordRoutingTable) routingTable).setInactive();
				}
				routingTable = null;

				// Unregister node at bootstrap manager
				this.getBootstrapManager().unregisterNode(this);

				// delete the stored data items
				// TODO check this remove operation as it throws a
				// ConcurrentModificationException
				// Set<DHTEntry> entries = dht.getDHTEntries();
				// Iterator<DHTEntry> it = entries.iterator();
				// while (it.hasNext()) {
				// DHTEntry dhtEntry = (DHTEntry) it.next();
				// it.remove();
				// dht.removeDHTEntry(dhtEntry.getKey());
				// }

				// Reset the SkyNet node
				OverlayNode<?, ?> olNode = getHost().getOverlay(
						SkyNetNode.class);
				if (olNode != null && olNode instanceof SkyNetNode) {
					((SkyNetNode) olNode).resetSkyNetNode(Simulator
							.getCurrentTime());
				}
			}
		}
	}

	/**
	 * Find node that is responsible for the given key
	 * 
	 * @param key
	 *            the key to look up
	 * @param callback
	 * @return the Id of the LookupOperation, -1 if the node is not present in
	 *         the overlay
	 */
	@Override
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
	 * 
	 * This method is called when join operation is finished. As results of join
	 * operation are
	 * 
	 * @param successor
	 *            : next direct successor in ring form
	 * @param predecessor
	 *            : : next direct predecessor in ring form
	 * @param succFingerTable
	 *            : FingerTable of successor. The FingerTable size is so big to
	 *            put in a message, thus a list of different ChordContact
	 *            represent the FingerTable instance. The FingerTable can be
	 *            reconstructed by using utility class RoutingTableContructor
	 * 
	 */
	public void joinOperationFinished(AbstractChordContact successor,
			AbstractChordContact predecessor,
			Set<AbstractChordContact> succFingerTable) {

		log.debug(Simulator.getSimulatedRealtime() + " Peer "
				+ getHost().getNetLayer().getNetID() + " joined ");

		// create Routing table
		routingTable = new ChordRoutingTable(this, predecessor, successor,
				succFingerTable);

		if (((ChordRoutingTable) routingTable).isNodeItsOwnSuccessor()
				&& ((ChordRoutingTable) routingTable)
						.isNodeItsOwnSPredecessor()
				&& this.getBootstrapManager().getNumOfAvailableNodes() > 1) {

			joinWithDelay(
					Operations.EMPTY_CALLBACK,
					Simulator
							.getRandom()
							.nextInt(
									(int) ChordConfiguration.MAX_WAIT_BEFORE_JOIN_RETRY));

			log.error(getOverlayID()
					+ " joined but has itself as successor although there are other alive peers in the system. Will do a rejoin again.");

		} else {

			// Start SkyNet after the joining was successful
			SkyNetNode node = ((SkyNetNode) getHost().getOverlay(
					AbstractSkyNetNode.class));
			if (node != null) {
				node.startSkyNetNode(Simulator.getCurrentTime());
			}
		}
	}

	/**
	 * This method is called when leave operation is finished
	 */
	@Override
	public void leaveOperationFinished() {
		// Stop SkyNet
		SkyNetNode node = ((SkyNetNode) getHost().getOverlay(
				AbstractSkyNetNode.class));
		if (node != null) {
			node.resetSkyNetNode(Simulator.getCurrentTime());
		}

		log.info(" node leave " + this + " at Time[s] "
				+ Simulator.getCurrentTime() / Simulator.SECOND_UNIT);
		setPeerStatus(PeerStatus.ABSENT);

		// Mark the old listener as inactive and create an new one
		operationListener.setInactive();
		operationListener = new ChordOperationListener(this);

		// Mark the old routing table as inactive
		if (routingTable != null) {
			((ChordRoutingTable) routingTable).setInactive();
		}

	}

	// Getters and Setters

	@Override
	public boolean isPresent() {
		return getPeerStatus() == PeerStatus.PRESENT;
	}

	@Override
	public ChordRoutingTable getChordRoutingTable() {
		return (ChordRoutingTable) routingTable;
	}

	@Override
	public TransInfo getTransInfo() {
		return getTransLayer().getLocalTransInfo(getPort());
	}

	@Override
	public JoinOperation getJoinOperation() {
		return joinOperation;
	}

	public ChordOperationListener getOperationListener() {
		return operationListener;
	}

	@Override
	protected void registerLookupOperation(int lookupId,
			AbstractChordOperation<?> op) {
		lookupOperationList.put(lookupId, op);
	}

	@Override
	public AbstractChordOperation<?> removeLookupOperation(int lookupId) {
		return lookupOperationList.remove(lookupId);
	}

	@Override
	public AbstractChordOperation<?> getLookupOperation(int lookupId) {
		return lookupOperationList.get(lookupId);
	}

	@Override
	public ChordMessageHandler getMessageHandler() {
		return messageHandler;
	}

	@Override
	public String toString() {
		if (nodeType == ChordNodeType.LOADBALANCINGNETNODE) {
			return "Node (loadbalancingNet) " + getOverlayID() + " "
					+ getPeerStatus();
		} else {
			return "Node (dataNetNet) " + getOverlayID() + " "
					+ getPeerStatus();
		}
	}

	@Override
	protected int getNextLookupId() {
		nextLookupId++;
		return nextLookupId;
	}

	// test methods

	/**
	 * This method is called to periodically start dummy lookup request
	 */

	@Override
	public boolean isOnline() {
		return getHost().getNetLayer().isOnline();
	}

	@Override
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

		// check if this node has this node already:
		if (this.getDHT().getDHTEntry(key) != null) {
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
		Simulator.getMonitor().kbrQueryStarted(getLocalOverlayContact(), null);
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
		// Inform Analyzers
		Simulator.getMonitor()
				.dhtLookupInitiated(getLocalOverlayContact(), key);
		return overlayNodeLookup(key.getCorrespondingID(),
				callback);
	}

	@Override
	public ChordContact getLocalOverlayContact() {
		return new ChordContact(getOverlayID(),
				super.getTransLayer().getLocalTransInfo(this.getPort()));
	}

	@Override
	public ChordContact getOverlayContact(ChordID id, TransInfo transInfo) {
		if (!(id != null)) {
			return null;
		}

		return new ChordContact(id, transInfo);
	}

	// METHODS TO SUPPORT ZH-CHORD!

	public DHTObject getMirroredObject(ChordKey k) {
		return this.mirroredObjects.get(k);
	}

	public void documentAccessed(ChordKey documentId) {
		long now = Simulator.getCurrentTime();
		LinkedList<Long> ll = null;

		if (this.accessMap.containsKey(documentId)) {
			ll = this.accessMap.get(documentId);
		} else {
			ll = new LinkedList<Long>();
			this.accessMap.put(documentId, ll);
		}

		ll.addLast(Long.valueOf(now));

		// check if list is too long
		if (ll.size() > 10) {
			ll.removeFirst();
		}

		// now update the load value this node generates:
		if (ll.size() > MIN_HIT_COUNT) {
			long firstAccess = ll.get(0);

			long timespan = now - firstAccess;
			long secondsBetween2Hits = (timespan / ll.size())
					* Simulator.SECOND_UNIT; // time in seconds between two
			// accesses

			// get the filesize:
			long fileSizeInByte = 0;

			if (this.getDHT().getDHTEntry(documentId) != null) {
				fileSizeInByte = this.getDHT().getDHTEntry(documentId)
						.getTransmissionSize();
			} else if (this.mirroredObjects.containsKey(documentId)) {
				fileSizeInByte = this.mirroredObjects.get(documentId)
						.getTransmissionSize();
			}

			int consumedBandwidthPerSecond = (int) (fileSizeInByte / secondsBetween2Hits);

			double load = consumedBandwidthPerSecond;

			this.loadMap.put(documentId, load);
		}

	}

	/**
	 * called to update the loadMap.
	 */
	public void updateLoads() {
		LinkedList<Long> ll = null;
		long now = Simulator.getCurrentTime();

		for (DHTEntry<?> document : this.getDHT().getDHTEntries()) {

			ChordKey documentId = (ChordKey) document.getKey();

			if (this.accessMap.containsKey(documentId)) {
				ll = this.accessMap.get(documentId);
			} else {
				continue;
			}

			// now update the load value this node generates:
			if (ll.size() > MIN_HIT_COUNT) {
				long firstAccess = ll.get(0);

				long timespan = now - firstAccess;
				long secondsBetween2Hits = (timespan / ll.size())
						/ Simulator.SECOND_UNIT; // time in seconds between two
				// accesses
				if (secondsBetween2Hits == 0) {
					secondsBetween2Hits = 1;
				}

				// get the filesize:
				long fileSizeInByte = this.getDHT().getDHTEntry(documentId)
						.getTransmissionSize();

				int consumedBandwidthPerSecond = (int) (fileSizeInByte / secondsBetween2Hits);

				double load = consumedBandwidthPerSecond;

				this.loadMap.put(documentId, load);
			}
		}

		for (Entry<ChordKey, DHTObject> document : this.mirroredObjects
				.entrySet()) {

			ChordKey documentId = document.getKey();

			if (this.accessMap.containsKey(documentId)) {
				ll = this.accessMap.get(documentId);
			} else {
				continue;
			}

			// now update the load value this node generates:
			if (ll.size() > MIN_HIT_COUNT) {
				long firstAccess = ll.get(0);

				long timespan = now - firstAccess;
				long secondsBetween2Hits = (timespan / ll.size())
						/ Simulator.SECOND_UNIT; // time in seconds between two
				// accesses
				if (secondsBetween2Hits == 0) {
					secondsBetween2Hits = 1;
				}

				// get the filesize:
				long fileSizeInByte = this.mirroredObjects.get(documentId)
						.getTransmissionSize();

				int consumedBandwidthPerSecond = (int) (fileSizeInByte / secondsBetween2Hits);

				double load = consumedBandwidthPerSecond;

				this.loadMap.put(documentId, load);
			}
		}

	}

	@Override
	public void changeIdentTo(OverlayID<BigInteger> newIdent) {
		// TODO: first check if the new id is free -
		// else increment newIdent as long as a free position is found.
		// but we will do so by adding a random integer between 0 and 10.000.000
		BigInteger id = newIdent.getUniqueValue();

		Integer rand = Integer.valueOf(Simulator.getRandom().nextInt(
				ChordConfiguration.RANDOM_TIE_BREAKER_SIZE));

		id = id.add(new BigInteger(rand.toString()));
		ChordID newid = new ChordID(id);
		log.debug(this + ": changing OverlayID to " + newid);

		RejoinOperation op = new RejoinOperation(this, newid);
		op.scheduleImmediately();

	}

	@Override
	public double getLoad() {
		double load = 0.0;
		LinkedList<Tuple<ChordKey, Double>> myObjects = getOfferedObjects();
		for (Tuple<ChordKey, Double> t : myObjects) {
			if (!this.mirrorMap.containsKey(t.getA())) {
				load += t.getB();
			}
		}

		// add load of all i have to mirror
		for (ChordKey k : this.mirroredObjects.keySet()) {
			if (this.loadMap.containsKey(k)) {
				load += this.loadMap.get(k);
			}

		}

		return load;
	}

	@Override
	public double getPerformanceIndex() {
		double uploadBand = this.getHost().getProperties()
				.getMaxUploadBandwidth();

		/**
		 * find out about the load this node has to provide due to its
		 * documents.
		 */
		double load = getLoad();

		double performanceIndex = (uploadBand - load)
				/ MAXIMUM_UPLOAD_BANDWIDTH;
		if (performanceIndex > 1.0) {
			performanceIndex = 1.0;
		}

		return performanceIndex;
	}

	@Override
	public LinkedList<Tuple<ChordKey, Double>> getOfferedObjects() {

		// FIXME: check if this update is necessary - maybe schedule event every
		// minute or so..
		// updateLoads();

		LinkedList<Tuple<ChordKey, Double>> ret = new LinkedList<Tuple<ChordKey, Double>>();

		// HashSet<DHTKey> seen = new LinkedHashSet<DHTKey>();

		for (DHTEntry<ChordKey> e : this.getDHT().getDHTEntries()) {
			// if(!seen.contains(e.getKey())) {
			// seen.add(e.getKey());
			Double load = 0.0;
			if (this.loadMap.containsKey(e.getKey())) {
				load = this.loadMap.get(e.getKey());
			}

			ret.add(new Tuple<ChordKey, Double>(e.getKey(), load));
			// }
		}

		return ret;
	}

	@Override
	public AbstractChordContact getMirrorForObject(ChordKey documentId) {
		return this.mirrorMap.get(documentId);
	}

	@Override
	public boolean isOverloaded() {

		if (!ChordConfiguration.USE_LOADBALANCING) {
			return false;
		}

		double uploadBand = this.getHost().getProperties()
				.getMaxUploadBandwidth();

		double load = getLoad();

		if (load > uploadBand - ChordConfiguration.MINIMAL_USABLE_BANDWIDTH
				|| uploadBand < ChordConfiguration.MINIMAL_USABLE_BANDWIDTH) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void removeRedirection(ChordKey documentId) {
		this.mirrorMap.remove(documentId);
	}

	@Override
	public void addRedirection(ChordKey documentId,
			AbstractChordContact mirrorNode) {
		// TODO Auto-generated method stub
		DHTObject object = (DHTObject) this.getDHT().getDHTValue(
				documentId);

		double usedBandwidth = 0;
		if (loadMap != null && loadMap.get(documentId) != null) {
			usedBandwidth = loadMap.get(documentId);
		}

		CreateMirrorOperation op = new CreateMirrorOperation(this,
				this.getLocalOverlayContact(),
				(ChordContact) mirrorNode,
				object,
				documentId,
				usedBandwidth);
		op.scheduleImmediately();

	}

	/**
	 * called if the addredirectionoperation was successful. fills mirrorMap
	 */
	public void addMirrorForObject(ChordKey documentId,
			AbstractChordContact mirror) {
		this.mirrorMap.put(documentId, mirror);
	}

	/**
	 * called to insert a document to mirroredObjects
	 */
	public void addDocumentToThisMirror(ChordKey documentId,
			DHTObject document, AbstractChordContact ownerOfTheDocument) {
		if (mirroredObjects.get(documentId) == null) {
			((ChordNode) this.zhNode.getDataNetNode()).mirroredObjects.put(
					documentId, document);
			((ChordNode) this.zhNode.getDataNetNode()).mirroredObjectsLastContactToOrigin
					.put(documentId, Simulator.getCurrentTime());
			((ChordNode) this.zhNode.getDataNetNode()).mirroredObjectsOrigin
					.put(documentId, ownerOfTheDocument);

			((ChordNode) this.zhNode.getDataNetNode()).initLoadMap(documentId);

			// notify Analyzer about this:
			Simulator.getMonitor().dhtMirrorAssigned(
					this.getLocalOverlayContact(), document);
		}
	}

	/**
	 * this method checks weather this node holds the mirrored document
	 * documentId method invoked on receive of a CheckMirrorMessage
	 * 
	 * @param documentId
	 * @return
	 */
	public boolean checkIfMirrorIsPresentAndUpdateLastContact(
			ChordKey documentId) {
		boolean ret = false;

		if (this.mirroredObjects.containsKey(documentId)) {
			ret = true;
			this.mirroredObjectsLastContactToOrigin.put(documentId,
					Simulator.getCurrentTime());
		}

		return ret;
	}

	/**
	 * checks for each document that has been mirrored to this node when it had
	 * last contact to the node that is responsible for this document. If this
	 * was long ago the mirror will be deleted.
	 */
	public void removeMirroredObjectsIfHostDown() {

		LinkedList<ChordKey> toDelete = new LinkedList<ChordKey>();

		long now = Simulator.getCurrentTime();
		for (ChordKey key : this.mirroredObjects.keySet()) {
			if (!this.mirroredObjectsLastContactToOrigin.containsKey(key)) {
				this.mirroredObjectsLastContactToOrigin.put(key, now);
			} else {
				// check last contact.
				long lastContact = this.mirroredObjectsLastContactToOrigin
						.get(key);

				if (lastContact < now - 3
						* ChordConfiguration.CHECK_MIRROR_INTERVAL) {
					// delete the document.
					toDelete.add(key);
				}
			}
		}

		// commit deletes!
		for (ChordKey key : toDelete) {
			log.debug(this + ": removing mirror for document " + key
					+ " because did not heared from its origin.");

			// notify Analyzer about this:
			Simulator.getMonitor().dhtMirrorDeleted(getLocalOverlayContact(),
					this.mirroredObjects.get(key));

			this.mirroredObjects.remove(key);
			this.mirroredObjectsOrigin.remove(key);
			this.mirroredObjectsLastContactToOrigin.remove(key);
		}

	}

	/**
	 * update all nodes that hold mirrors of documents belonging to this node.
	 */
	public void contactAllMirrorHolders() {

		long delay = (ChordConfiguration.CHECK_MIRROR_INTERVAL / (this.mirrorMap
				.size() + 1));

		int i = 0;
		for (ChordKey key : this.mirrorMap.keySet()) {
			CheckMirrorOperation op = new CheckMirrorOperation(this,
					this.getLocalOverlayContact(),
					mirrorMap.get(key), key);
			op.scheduleWithDelay(i * delay);
			i++;
		}
	}

	public int getNumMirroredObjects() {
		return this.mirroredObjects.size();
	}

	/**
	 * remove the mirror with highest load.
	 * 
	 * @return true if a mirror has been deleted.
	 */
	public boolean removeHighestLoadMirroredObject() {
		ChordKey k = null;
		double max = 0;

		for (ChordKey key : this.mirroredObjects.keySet()) {
			if (k == null || loadMap.containsKey(key) && loadMap.get(key) > max) {
				k = key;
				if (loadMap.containsKey(key)) {
					max = loadMap.get(key);
				}
			}
		}
		if (max > 0.0) {

			// notify Analyzer about this:
			Simulator.getMonitor().dhtMirrorDeleted(getLocalOverlayContact(),
					this.mirroredObjects.get(k));

			mirroredObjects.remove(k);
			mirroredObjectsLastContactToOrigin.remove(k);

			// tell the origin of the mirror from this!
			CommunicateMirrorRemovedOperation op = new CommunicateMirrorRemovedOperation(
					this, this.getLocalOverlayContact(),
					mirroredObjectsOrigin.get(k), k);
			op.scheduleImmediately();
			mirroredObjectsOrigin.remove(k);

			return true;
		}
		return false;
	}

	// this method checks weather it is possible to serve the load of documents
	// that have been sourced out
	// to a mirror. If there is enough load to do so --> destroy the mirror and
	// take responsibility for document back
	public void getMirroredObjectsBack() {
		if (isOverloaded()) {
			return;
		}
		double fullUploadBandwidth = this.getHost().getProperties()
				.getMaxUploadBandwidth();

		double freeload = fullUploadBandwidth * OVERLOAD_FACTOR;

		double aktLoad = getLoad();
		freeload -= aktLoad;

		ChordKey takeBack = null;
		double maxLoad = 0;

		for (ChordKey k : this.mirrorMap.keySet()) {
			double load = 0.0;
			if (loadMap.get(k) != null) {
				load = loadMap.get(k);
			}
			double loadForThisDocument = load * (1.0 + OVERLOAD_FACTOR);
			if (loadMap.containsKey(k) && freeload > loadForThisDocument) {
				if (loadForThisDocument > maxLoad) {
					takeBack = k;
					maxLoad = loadMap.get(k);
				}
			}
		}

		if (takeBack != null && freeload > maxLoad) {
			log.debug(this + ": taking back control for document "
					+ takeBack + " because " + freeload + " > " + maxLoad);
			this.removeRedirection(takeBack);
		}
	}

	// called on leave operation: tell everyone i am going offline - do not wait
	// for acks.
	public void removeAllMirroredObjectsOnLeave() {

		for (ChordKey document : mirroredObjects.keySet()) {
			AbstractChordContact c = mirroredObjectsOrigin.get(document);

			RemovedMirrorMessage msg = new RemovedMirrorMessage(
					this.getLocalOverlayContact(), c, document);

			this.getTransLayer().send(msg,
					c.getTransInfo(),
					this.getTransInfo().getPort(),
					ChordConfiguration.TRANSPORT_PROTOCOL);
		}

	}

	// END METHODES TO SUPPORT ZH CHORD.

}