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

package org.peerfact.impl.overlay.dht.centralizedindex.components;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.peerfact.api.common.ConnectivityEvent;
import org.peerfact.api.common.Message;
import org.peerfact.api.common.Operation;
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
import org.peerfact.impl.common.Operations;
import org.peerfact.impl.network.IPv4NetID;
import org.peerfact.impl.overlay.AbstractOverlayNode;
import org.peerfact.impl.overlay.dht.centralizedindex.callbacks.ClientJoinOperationCallback;
import org.peerfact.impl.overlay.dht.centralizedindex.callbacks.ClientLeaveOperationCallback;
import org.peerfact.impl.overlay.dht.centralizedindex.operations.ClientJoinOperation;
import org.peerfact.impl.overlay.dht.centralizedindex.operations.ClientLeaveOperation;
import org.peerfact.impl.overlay.dht.centralizedindex.operations.GetPredecessorOperation;
import org.peerfact.impl.overlay.dht.centralizedindex.operations.NodeLookupOperation;
import org.peerfact.impl.overlay.dht.centralizedindex.operations.ResponsibleForKeyOperation;
import org.peerfact.impl.overlay.dht.centralizedindex.operations.ResponsibleForKeyResult;
import org.peerfact.impl.overlay.kbr.KBRMsgHandler;
import org.peerfact.impl.overlay.kbr.messages.KBRForwardInformationImpl;
import org.peerfact.impl.overlay.kbr.messages.KBRForwardMsg;
import org.peerfact.impl.service.aggregation.skyeye.components.SkyNetNode;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.util.logging.SimLogger;

/**
 * Implementing a centralized DHT overlay, whose organization of the centralized
 * index is similar to the distributed index of Chord
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 08.12.2008
 */
public class CIClientNode extends
		AbstractOverlayNode<CIOverlayID, CIOverlayContact> implements
		DHTNode<CIOverlayID, CIOverlayContact, CIOverlayKey> {

	private static final Logger log = SimLogger
			.getLogger(CIClientNode.class);

	private final TransLayer transLayer;

	private final CIBootstrapManager bootstrap;

	private CIOverlayContact ownOverlayContact;

	private CIOverlayContact serverOverlayContact;

	private final CIClientMessageHandler messageHandler;

	// private DHTListener<CIOverlayKey> dht = new
	// SimpleDHTService<CIOverlayKey>();

	private KBRListener<CIOverlayID, CIOverlayContact, CIOverlayKey> kbrListener;

	private KBRLookupProvider<CIOverlayID, CIOverlayContact, CIOverlayKey> kbrLookupProvider;

	// FIXME Just for workaround. Just used to delete the client at the server
	// if the client goes down. Later, this will be realized, by a normal DHT
	@Deprecated
	private final CIServerNode server;

	@Deprecated
	public CIServerNode getServer() {
		return server;
	}

	public CIClientNode(short port, TransLayer transLayer,
			CIServerNode server, CIBootstrapManager bootstrap,
			CIOverlayContact contact) {
		super(contact.getOverlayID(), port);
		this.server = server;
		this.transLayer = transLayer;
		this.bootstrap = bootstrap;
		this.messageHandler = new CIClientMessageHandler(this);
		this.transLayer.addTransMsgListener(messageHandler, getPort());
		transLayer.getLocalTransInfo(getPort());
		// only needed for visualization
		this.ownOverlayContact = contact;

		// this.ownOverlayContact = new CIOverlayContact(null, t);
		// REMOVIX
		// setOverlayID(ownOverlayContact.getOverlayID());
	}

	@Override
	public TransLayer getTransLayer() {
		return transLayer;
	}

	@Override
	public int join(OperationCallback<Object> callback) {
		if (callback.equals(Operations.EMPTY_CALLBACK)) {
			return tryJoin();
		} else {
			TransInfo transInfo = transLayer.getLocalTransInfo(getPort());
			ClientJoinOperation op = new ClientJoinOperation(this, transInfo,
					callback);
			op.scheduleImmediately();
			return op.getOperationID();
		}
	}

	@Override
	public int leave(OperationCallback<Object> callback) {
		if (callback.equals(Operations.EMPTY_CALLBACK)) {
			return tryLeave();
		} else {
			ClientLeaveOperation op = new ClientLeaveOperation(this, callback);
			op.scheduleImmediately();
			return op.getOperationID();
		}
	}

	public int responsibleForKey(CIOverlayKey key,
			OperationCallback<ResponsibleForKeyResult> callback) {
		ResponsibleForKeyOperation op = new ResponsibleForKeyOperation(this,
				key, callback);
		op.scheduleImmediately();
		return op.getOperationID();
	}

	public int getPredecessor(OperationCallback<CIOverlayContact> callback) {
		GetPredecessorOperation op = new GetPredecessorOperation(this, callback);
		op.scheduleImmediately();
		return op.getOperationID();
	}

	@Override
	public int store(CIOverlayKey key, DHTObject obj,
			OperationCallback<Set<CIOverlayContact>> callback) {
		throw new UnsupportedOperationException("not yet implemented");
	}

	@Override
	public int valueLookup(CIOverlayKey key,
			OperationCallback<DHTObject> callback) {
		throw new UnsupportedOperationException("not yet implemented");
	}

	@Override
	public void connectivityChanged(ConnectivityEvent ce) {
		if (ce.isOnline()) {
			if (getPeerStatus().equals(PeerStatus.ABSENT)) {
				join(new ClientJoinOperationCallback(this, 0));
			} else {
				log.fatal(Simulator.getFormattedTime(Simulator.getCurrentTime())
						+ " NapsterClient "
						+ getHost().getNetLayer().getNetID()
						+ " was never set to ABSENT");
			}
		} else if (ce.isOffline()) {
			if (getPeerStatus().equals(PeerStatus.PRESENT)) {
				setPeerStatus(PeerStatus.ABSENT);
				server.getDHT().removeContact(ownOverlayContact.getOverlayID());
				((SkyNetNode) getHost().getOverlay(SkyNetNode.class))
						.resetSkyNetNode(Simulator.getCurrentTime());
			} else {
				if (getServer().getDHT().containsOverlayID(getOwnOverlayID())) {
					log.warn(Simulator.getFormattedTime(Simulator
							.getCurrentTime())
							+ " Scruffy Access to the DHT to remove "
							+ getOwnOverlayContact().toString()
							+ ". This is needed, since the DHT does not refresh its entries");
					getServer().getDHT().removeContact(getOwnOverlayID());
					setPeerStatus(PeerStatus.ABSENT);
				}
				log.fatal(Simulator.getFormattedTime(Simulator.getCurrentTime())
						+ " NapsterClient "
						+ getOwnOverlayContact().toString()
						+ " was never set to PRESENT");
			}
		}
	}

	public TransInfo getServerTransInfo() {
		List<TransInfo> bootstrapInfo = bootstrap.getBootstrapInfo();
		return bootstrapInfo.isEmpty() ? null : bootstrapInfo.get(0);
	}

	public CIOverlayID getOwnOverlayID() {
		return getOverlayID();
	}

	public CIOverlayContact getOwnOverlayContact() {
		return ownOverlayContact;
	}

	public void setOwnOverlayContact(CIOverlayContact ownOverlayContact) {
		this.ownOverlayContact = ownOverlayContact;
	}

	public CIOverlayContact getServerOverlayContact() {
		return serverOverlayContact;
	}

	public void setServerOverlayContact(
			CIOverlayContact serverOverlayContact) {
		if (this.serverOverlayContact == null) {
			log.info("Created serverOverlayContact "
					+ serverOverlayContact.toString());
			this.serverOverlayContact = serverOverlayContact;
		}
	}

	// flag-methods, checks if the host is actually online or present to execute
	// one of the
	// methods, defined in this class

	@Override
	public boolean isPresent() {
		return getPeerStatus().equals(PeerStatus.PRESENT);
	}

	// ---------------------------------------------------------------------
	// aux-methods for other operations
	// ---------------------------------------------------------------------

	public void resetNapsterClient() {
		// TODO reset the NapsterClient, if it goes offline or leaves the
		// overlay(=becomining absent)
	}

	// ---------------------------------------------------------------------
	// methods for triggering the operations from the action-file, just for
	// testing
	// ---------------------------------------------------------------------

	public int tryJoin() {
		if (getPeerStatus().equals(PeerStatus.ABSENT)) {
			return join(new ClientJoinOperationCallback(this, 0));
		} else {
			log.warn("Client " + ownOverlayContact.toString()
					+ " cannot join, he is already PRESENT");
			return -1;
		}

	}

	public int tryLeave() {
		if (isPresent()) {
			return leave(new ClientLeaveOperationCallback(this, 0));
		} else {
			log.warn("Client " + ownOverlayContact.toString()
					+ " cannot leave, he is actually not PRESENT");
			return -1;
		}
	}

	public void tryNodeLookup(String key) {
		if (isPresent()) {
			final CIOverlayKey keyID = new CIOverlayKey(
					new IPv4NetID(
							key));
			nodeLookup(keyID,
					new OperationCallback<List<CIOverlayContact>>() {

						@Override
						public void calledOperationFailed(
								Operation<List<CIOverlayContact>> op) {
							log.info("NodeLookupOperation with id "
									+ op.getOperationID() + " failed");
						}

						@Override
						public void calledOperationSucceeded(
								Operation<List<CIOverlayContact>> op) {
							CIOverlayContact contact = (CIOverlayContact) op
									.getResult();
							log.info("NodeLookupOperation with id "
									+ op.getOperationID()
									+ " succeeded. The client "
									+ getOwnOverlayContact().toString()
									+ " received the overlayContact "
									+ contact.toString()
									+ " for the OverlayKey "
									+ keyID.getID());
						}
					}, true);
		}
	}

	public void tryGetPredecessor() {
		if (isPresent()) {
			getPredecessor(new OperationCallback<CIOverlayContact>() {

				@Override
				public void calledOperationFailed(
						Operation<CIOverlayContact> op) {
					log.info("GetPredecessorOperation with id "
							+ op.getOperationID() + " failed");
				}

				@Override
				public void calledOperationSucceeded(
						Operation<CIOverlayContact> op) {
					CIOverlayContact contact = op.getResult();
					log.info("GetPredecessorOperation with id "
							+ op.getOperationID() + " succeeded. The client "
							+ getOwnOverlayContact().toString()
							+ " has the Predecessor " + contact.toString());
				}
			});
		}
	}

	public void tryResponsibiltyForKey(String key) {
		if (isPresent()) {
			final CIOverlayKey keyID = new CIOverlayKey(
					new IPv4NetID(
							key));
			responsibleForKey(keyID,
					new OperationCallback<ResponsibleForKeyResult>() {

						@Override
						public void calledOperationFailed(
								Operation<ResponsibleForKeyResult> op) {
							log.info("NodeLookupOperation with id "
									+ op.getOperationID() + " failed");
						}

						@Override
						public void calledOperationSucceeded(
								Operation<ResponsibleForKeyResult> op) {
							Boolean flag = op.getResult()
									.getResponsibiltyFlag();
							if (flag) {
								log.info("ResponsibiltyForKeyOperation with id "
										+ op.getOperationID()
										+ " succeeded. The client "
										+ getOwnOverlayContact().toString()
										+ " is responsible for the OverlayKey "
										+ keyID.getID());
							} else {
								log.info("ResponsibiltyForKeyOperation with id "
										+ op.getOperationID()
										+ " succeeded. The client "
										+ getOwnOverlayContact().toString()
										+ " is not responsible for the OverlayKey "
										+ keyID.getID());
							}
						}
					});
		}
	}

	public int nodeLookup(CIOverlayID id,
			OperationCallback<List<CIOverlayContact>> callback) {
		return nodeLookup(new CIOverlayKey(id.getID()), callback, true);
	}

	@Override
	public int nodeLookup(CIOverlayKey key,
			OperationCallback<List<CIOverlayContact>> callback,
			boolean returnSingleNode) {
		NodeLookupOperation op = new NodeLookupOperation(this,
				key, callback);
		op.scheduleImmediately();
		// Inform the monitors about an initiated query
		Simulator.getMonitor()
				.dhtLookupInitiated(getLocalOverlayContact(), key);

		return op.getOperationID();
	}

	@Override
	public NeighborDeterminator<CIOverlayContact> getNeighbors() {
		return null;
	}

	@Override
	public void registerDHTListener(DHTListener<CIOverlayKey> listener) {
		// doNothing
	}

	@Override
	public CIOverlayKey getRandomOverlayKey() {
		return new CIOverlayKey(new BigInteger(Integer.toString(Simulator
				.getRandom().nextInt())));
	}

	@Override
	public void route(CIOverlayKey key, Message msg,
			CIOverlayContact hint) {
		CIOverlayContact nextHop = null;
		if (hint != null) {
			nextHop = hint;
		} else if (key != null) {
			nextHop = local_lookup(key, 1).get(0);
			// Inform the monitors about an initiated query
			Simulator.getMonitor().kbrQueryStarted(getLocalOverlayContact(),
					msg);
		} else {
			System.err.println("Both key and hint are null!!");
			return;
		}
		KBRForwardInformation<CIOverlayID, CIOverlayContact, CIOverlayKey> info = new KBRForwardInformationImpl<CIOverlayID, CIOverlayContact, CIOverlayKey>(
				key, msg,
				nextHop);
		kbrListener.forward(info);
		CIOverlayKey nextKey = info.getKey();
		Message nextMsg = info.getMessage();
		nextHop = info.getNextHopAgent();

		if (nextHop != null) { // see kbrListener-Interface, stop Message if
								// nextHop = null
			KBRForwardMsg<CIOverlayID, CIOverlayKey> fm = new KBRForwardMsg<CIOverlayID, CIOverlayKey>(
					getOverlayID(),
					nextHop.getOverlayID(),
					nextKey, nextMsg);
			getTransLayer().send(fm, nextHop.getTransInfo(), getPort(),
					TransProtocol.UDP);
		}
	}

	@Override
	public List<CIOverlayContact> local_lookup(CIOverlayKey key,
			int num) {
		List<CIOverlayContact> nexthops = new LinkedList<CIOverlayContact>();

		// Add the only next hop: the server
		nexthops.add(getServerOverlayContact());
		return nexthops;
	}

	@Override
	public List<CIOverlayContact> replicaSet(CIOverlayKey key,
			int maxRank) {
		List<CIOverlayContact> replicas = new LinkedList<CIOverlayContact>();

		// Add the only possible replica: the server
		replicas.add(getServerOverlayContact());
		return replicas;
	}

	@Override
	public List<CIOverlayContact> neighborSet(int num) {
		List<CIOverlayContact> neighbors = new LinkedList<CIOverlayContact>();

		// Add the only possible replica: the server
		neighbors.add(getServerOverlayContact());
		return neighbors;
	}

	@Override
	public CIOverlayID[] range(CIOverlayContact contact, int rank) {
		CIOverlayID[] range = new CIOverlayID[2];

		// The client is responsible for only himself (range: [ownID, ownID])
		range[0] = getOverlayID();
		range[1] = range[0];

		return range;
	}

	@Override
	public boolean isRootOf(CIOverlayKey key) {
		// Clients can not be roots of documents
		return false;
	}

	@Override
	public void setKBRListener(
			KBRListener<CIOverlayID, CIOverlayContact, CIOverlayKey> listener) {
		this.kbrListener = listener;
		KBRMsgHandler<CIOverlayID, CIOverlayContact, CIOverlayKey> msgHandler = new KBRMsgHandler<CIOverlayID, CIOverlayContact, CIOverlayKey>(
				this, this, kbrListener);

		kbrLookupProvider = msgHandler.getLookupProvider();
	}

	@Override
	public CIOverlayKey getNewOverlayKey(int rank) {
		return new CIOverlayKey(new BigInteger(Integer.toString(rank)));
	}

	@Override
	public CIOverlayContact getLocalOverlayContact() {
		return ownOverlayContact;
	}

	@Override
	public CIOverlayContact getOverlayContact(CIOverlayID id,
			TransInfo transinfo) {

		return new CIOverlayContact(id, transinfo);
	}

	@Override
	public void hadContactTo(CIOverlayContact contact) {
		// TODO Auto-generated method stub

	}

	@Override
	public KBRLookupProvider<CIOverlayID, CIOverlayContact, CIOverlayKey> getKbrLookupProvider() {
		return kbrLookupProvider;
	}

}
