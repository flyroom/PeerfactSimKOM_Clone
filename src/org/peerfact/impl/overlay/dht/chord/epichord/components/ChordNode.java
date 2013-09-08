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

package org.peerfact.impl.overlay.dht.chord.epichord.components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.peerfact.api.common.ConnectivityEvent;
import org.peerfact.api.common.Message;
import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.overlay.OverlayNode;
import org.peerfact.api.overlay.kbr.KBRForwardInformation;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.api.transport.TransLayer;
import org.peerfact.api.transport.TransProtocol;
import org.peerfact.impl.common.Operations;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordContact;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordNode;
import org.peerfact.impl.overlay.dht.chord.base.components.ChordBootstrapManager;
import org.peerfact.impl.overlay.dht.chord.base.components.ChordConfiguration;
import org.peerfact.impl.overlay.dht.chord.base.components.ChordID;
import org.peerfact.impl.overlay.dht.chord.base.components.ChordKey;
import org.peerfact.impl.overlay.dht.chord.base.operations.AbstractChordOperationListener;
import org.peerfact.impl.overlay.dht.chord.base.operations.LookupOperation;
import org.peerfact.impl.overlay.dht.chord.epichord.callbacks.JoinOperation;
import org.peerfact.impl.overlay.dht.chord.epichord.operations.ChordOperationListener;
import org.peerfact.impl.overlay.dht.chord.epichord.operations.EpiLookupOperation;
import org.peerfact.impl.overlay.kbr.messages.KBRForwardInformationImpl;
import org.peerfact.impl.overlay.kbr.messages.KBRForwardMsg;
import org.peerfact.impl.service.aggregation.skyeye.AbstractSkyNetNode;
import org.peerfact.impl.service.aggregation.skyeye.components.SkyNetNode;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.util.logging.SimLogger;


/**
 * 
 * This class represents a Peer/Host in Chord Overlay and the main
 * functionality.
 * 
 * @author Minh Hoang Nguyen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class ChordNode extends AbstractChordNode {

	private static Logger log = SimLogger.getLogger(ChordNode.class);

	private final ChordMessageHandler messageHandler;

	/**
	 * handle operation time out, deliver operation results
	 */
	private AbstractChordOperationListener operationListener;

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
	}

	private JoinOperation joinOperation = null;

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
				// Set<DHTEntry> entries = super.getDHT().getDHTEntries();
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
			Set<AbstractChordContact> succFingerTable, ChordCache chordCache) {

		log.debug(Simulator.getSimulatedRealtime() + " Peer "
				+ getHost().getNetLayer().getNetID() + " joined ");

		// create Routing table
		routingTable = new ChordRoutingTable(this, predecessor, successor,
				succFingerTable, chordCache);

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
	public ChordRoutingTable getChordRoutingTable() {
		return (ChordRoutingTable) routingTable;
	}

	@Override
	public ChordContact getLocalOverlayContact() {
		return new ChordContact(getOverlayID(),
				super.getTransLayer().getLocalTransInfo(this.getPort()));
	}

	@Override
	public JoinOperation getJoinOperation() {
		return joinOperation;
	}

	public AbstractChordOperationListener getOperationListener() {
		return operationListener;
	}

	@Override
	public ChordMessageHandler getMessageHandler() {
		return messageHandler;
	}

	@Override
	public String toString() {
		return "Node " + getOverlayID() + " " + getPeerStatus();
	}

	@Override
	public boolean isOnline() {
		return getHost().getNetLayer().isOnline();
	}

	@Override
	public boolean absentCausedByChurn() {
		return (getPeerStatus() == PeerStatus.ABSENT) && rejoinOnOnlineEvent;
	}

	/*
	 * KBR methods
	 */

	@Override
	public ChordContact getOverlayContact(ChordID id, TransInfo transInfo) {
		if (!(id != null)) {
			return null;
		}

		return new ChordContact(id, transInfo);
	}

	@Override
	public void route(ChordKey key, Message msg, AbstractChordContact hint) {
		if (getChordRoutingTable() == null) {
			return;
		}

		AbstractChordContact nextHop = null;
		List<AbstractChordContact> hops = null;
		if (hint != null) {
			nextHop = hint;
		} else if (key != null) {
			hops = local_lookup(key, EpiChordConfiguration.P);
			if (hops.size() > 0) {
				// Inform the monitors about an initiated query
				Simulator.getMonitor().kbrQueryStarted(getLocalOverlayContact(),
						msg);
			} else {
				log.error("KBR route problem: local_lookup returns no next hops");
			}
		} else {
			log.error("KBR route problem: Both key and hint are null! No idea where to route the message.");
			return;
		}

		if (nextHop != null) {
			KBRForwardInformation<ChordID, AbstractChordContact, ChordKey> info = new KBRForwardInformationImpl<ChordID, AbstractChordContact, ChordKey>(
					key, msg,
					nextHop);
			kbrListener.forward(info);

			if (nextHop != null) { // see kbrListener-Interface, stop Message if
				// nextHop = null
				KBRForwardMsg<ChordID, ChordKey> fm = new KBRForwardMsg<ChordID, ChordKey>(
						getOverlayID(),
						nextHop.getOverlayID(), key, msg);
				getTransLayer().send(fm, nextHop.getTransInfo(), getPort(),
						TransProtocol.UDP);
			}
		} else if (hops != null) {
			for (AbstractChordContact contact : hops) {

				if (getOverlayID().compareTo(
						contact.getOverlayID()) != 0) {

					KBRForwardInformation<ChordID, AbstractChordContact, ChordKey> info = new KBRForwardInformationImpl<ChordID, AbstractChordContact, ChordKey>(
							key, msg,
							contact);
					kbrListener.forward(info);

					if (contact != null) { // see kbrListener-Interface, stop
											// Message if
						// nextHop = null
						KBRForwardMsg<ChordID, ChordKey> fm = new KBRForwardMsg<ChordID, ChordKey>(
								getOverlayID(),
								contact.getOverlayID(), key, msg);
						getTransLayer().send(fm, contact.getTransInfo(),
								getPort(),
								TransProtocol.UDP);
					}
				}
			}
		}
	}

	@Override
	public List<AbstractChordContact> local_lookup(ChordKey key, int num) {
		if (getChordRoutingTable() == null) {
			return new LinkedList<AbstractChordContact>();
		}

		// get best p contacts from cache
		AbstractChordContact[] contacts = ((ChordRoutingTable) routingTable)
				.getChordCache()
				.lookup(new ChordID(key.getKey()), num);

		List<AbstractChordContact> nodes = new ArrayList<AbstractChordContact>();
		Collections.addAll(nodes, contacts);

		if (nodes.isEmpty()) {
			nodes.add(getChordRoutingTable().getSuccessor());
		} else if (nodes.get(0).equals(getLocalOverlayContact())) {
			nodes.set(0, getChordRoutingTable().getSuccessor());
		}

		return nodes;
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
		LookupOperation op = new EpiLookupOperation(this, key, callback,
				lookupId);
		registerLookupOperation(lookupId, op);
		op.scheduleImmediately();

		return lookupId;
	}

}
