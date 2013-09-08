/*
 * Copyright (c) 2012-2013 Open Source Community - <http://www.peerfact.org>
 * Copyright (c) 2011-2012 University of Paderborn - UPB
 * Copyright (c) 2005-2011 KOM - Multimedia Communications Lab
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */

package org.peerfact.impl.overlay.dht.centralizedstorage.components;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.peerfact.api.common.ConnectivityEvent;
import org.peerfact.api.common.Message;
import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.overlay.BootstrapManager;
import org.peerfact.api.overlay.NeighborDeterminator;
import org.peerfact.api.overlay.dht.DHTListener;
import org.peerfact.api.overlay.dht.DHTNode;
import org.peerfact.api.overlay.dht.DHTObject;
import org.peerfact.api.overlay.kbr.KBRForwardInformation;
import org.peerfact.api.overlay.kbr.KBRListener;
import org.peerfact.api.overlay.kbr.KBRLookupProvider;
import org.peerfact.api.overlay.kbr.KBRNode;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.api.transport.TransLayer;
import org.peerfact.api.transport.TransMessageListener;
import org.peerfact.api.transport.TransProtocol;
import org.peerfact.impl.application.AbstractApplication;
import org.peerfact.impl.application.filesharing.Torrent;
import org.peerfact.impl.common.Operations;
import org.peerfact.impl.overlay.AbstractOverlayNode;
import org.peerfact.impl.overlay.dht.centralizedstorage.messages.LookupRequestMsg;
import org.peerfact.impl.overlay.dht.centralizedstorage.messages.LookupResultMsg;
import org.peerfact.impl.overlay.dht.centralizedstorage.messages.StoreRequestMsg;
import org.peerfact.impl.overlay.dht.centralizedstorage.messages.StoreResultMsg;
import org.peerfact.impl.overlay.dht.centralizedstorage.operations.ServerJoinOperation;
import org.peerfact.impl.overlay.kbr.KBRMsgHandler;
import org.peerfact.impl.overlay.kbr.messages.KBRForwardInformationImpl;
import org.peerfact.impl.overlay.kbr.messages.KBRForwardMsg;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.transport.TransMsgEvent;
import org.peerfact.impl.util.logging.SimLogger;


/**
 * Server node of a napster-like pseudo-p2p overlay network. All mappings will
 * be stored here. The server should not have an application running on top of
 * it.
 * 
 * @author Konstantin Pussep
 * @author Sebastian Kaune
 * @version 3.0, 29.11.2007
 * 
 */
public class CSServerNode extends
		AbstractOverlayNode<CSOverlayID, CSContact>
		implements
		TransMessageListener,
		DHTNode<CSOverlayID, CSContact, CSOverlayKey> {

	final static short PORT = 100;

	private BootstrapManager<CSServerNode> bootstrap;

	private TransLayer transLayer;

	private static final Logger log = SimLogger
			.getLogger(CSServerNode.class);

	private Map<CSOverlayKey, DHTObject> index = new LinkedHashMap<CSOverlayKey, DHTObject>();

	private KBRListener<CSOverlayID, CSContact, CSOverlayKey> kbrListener;

	private KBRLookupProvider<CSOverlayID, CSContact, CSOverlayKey> kbrLookupProvider;

	protected CSServerNode(TransLayer transLayer) {
		super(new CSOverlayID(true), PORT);
		this.transLayer = transLayer;
		this.transLayer.addTransMsgListener(this, PORT);
	}

	/**
	 * 
	 * @return data stored on the server (the psedo-distributed hash table)
	 */
	public Map<CSOverlayKey, DHTObject> listIndex() {
		return Collections.unmodifiableMap(index);
	}

	public DHTObject getDHTObject(CSOverlayKey key) {
		return this.index.get(key);
	}

	/**
	 * 
	 * @return overlay id of the server
	 */
	public CSOverlayID getServerID() {
		return getOverlayID();
	}

	/**
	 * Switch the server on.
	 * 
	 * @param callback
	 * @return operation id
	 */
	@Override
	public int join(OperationCallback<Object> callback) {
		ServerJoinOperation serverJoinOperation = new ServerJoinOperation(this,
				callback);
		serverJoinOperation.scheduleImmediately();
		return serverJoinOperation.getOperationID();
	}

	/**
	 * Switch the server off.
	 * 
	 * @param callback
	 * @return operation id
	 */
	@Override
	public int leave(OperationCallback<Object> callback) {
		// This information is related to execution of the napster application
		AbstractApplication.log
				.info("[" + this.getHost() + "] Server disconnected @ "
						+ Simulator.getSimulatedRealtime());
		return Operations.scheduleEmptyOperation(this, callback);
	}

	@Override
	public void messageArrived(TransMsgEvent receivingEvent) {
		Message msg = receivingEvent.getPayload();
		// TransInfo senderAddr = receivingEvent.getSenderTransInfo();
		// int commId = receivingEvent.getCommId();
		log.info("Received: " + msg);
		if (msg instanceof StoreRequestMsg) {
			StoreRequestMsg sr = (StoreRequestMsg) msg;
			processStore(sr, receivingEvent);
		} else if (msg instanceof LookupRequestMsg) {
			LookupRequestMsg req = (LookupRequestMsg) msg;
			processLookup(req, receivingEvent);
		}

	}

	/**
	 * Reply with the list of clients storing the requested document.
	 * 
	 * @param requestMsg
	 * @param receivingEvent
	 *            TODO
	 */
	private void processLookup(LookupRequestMsg requestMsg,
			TransMsgEvent receivingEvent) {
		CSOverlayKey key = requestMsg.getKey();
		DHTObject result = this.index.get(key);
		log.debug("Query result" + " for " + key + " is " + result);

		CSOverlayID sender = requestMsg.getSender();
		Assert.assertNotNull(sender);
		Message reply = new LookupResultMsg(requestMsg, result);
		// TODO upcall to the application here?
		this.transLayer.sendReply(reply, receivingEvent, this.getPort(),
				TransProtocol.UDP);
	}

	/**
	 * Update index table
	 * 
	 * @param req
	 * @param receivingEvent
	 *            TODO
	 */
	private void processStore(StoreRequestMsg req, TransMsgEvent receivingEvent) {
		CSOverlayKey key = req.getKey();
		updateIndex(key, req.getValue());

		// reply ok
		Message replyMsg = new StoreResultMsg(req);
		this.transLayer.sendReply(replyMsg, receivingEvent, this.getPort(),
				TransProtocol.UDP);
	}

	/**
	 * Update the local index by inserting a new key - value pair.
	 * 
	 * @param key
	 *            - key
	 * @param obj
	 *            - value
	 */
	public void updateIndex(CSOverlayKey key, DHTObject obj) {
		index.put(key, obj);
		log.debug("Updated index table to " + index);
	}

	public boolean isIndexEmpty() {
		return index.isEmpty();
	}

	public boolean containsIndexKey(CSOverlayKey key) {
		return index.containsKey(key);
	}

	void setBootstrap(BootstrapManager<CSServerNode> bootstrap) {
		this.bootstrap = bootstrap;
	}

	public BootstrapManager<CSServerNode> getBootstrap() {
		return this.bootstrap;
	}

	@Override
	public void connectivityChanged(ConnectivityEvent ce) {
		// FIXME Auto-generated method stub
	}

	@Override
	public String toString() {
		return "Server Node (" + this.transLayer.getLocalTransInfo(getPort())
				+ ")";
	}

	@Override
	public TransLayer getTransLayer() {
		return this.transLayer;
	}

	@Override
	public NeighborDeterminator<CSContact> getNeighbors() {
		return null;
	}

	@Override
	public List<CSContact> local_lookup(
			CSOverlayKey key,
			int num) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CSContact> neighborSet(int num) {
		List<CSContact> neighbors = new LinkedList<CSContact>();

		// Extract all clients from the servers index
		Map<CSOverlayKey, DHTObject> list = listIndex();
		for (Map.Entry<CSOverlayKey, DHTObject> entry : list
				.entrySet()) {
			// Return if the maximal number of neighbors is reached
			if (neighbors.size() >= num) {
				return neighbors;
			}

			DHTObject data = entry.getValue();
			if (data instanceof Torrent) {
				Torrent torrent = (Torrent) data;
				TransInfo trans = torrent.getOwnerInfo();
				CSOverlayID overlayID = (CSOverlayID) torrent
						.getOwnerID();

				neighbors.add(new CSContact(overlayID,
						trans));
			} else {
				// TODO: Fehler - sollte nicht passieren. Schauen, ob wirklich
				// der Fall
			}
		}
		return neighbors;
	}

	@Override
	public CSOverlayID[] range(CSContact contact, int rank) {
		CSOverlayID[] bounds = null;

		// Is the contact the server itself?
		if (contact.getTransInfo().getNetId() == getHost().getNetLayer()
				.getNetID()
				& contact.getOverlayID().isServer) {
			bounds = new CSOverlayID[2];

			// The server is responsible for himself and all clients
			bounds[0] = new CSOverlayID(false);
			bounds[1] = new CSOverlayID(true);
		}

		return bounds;
	}

	@Override
	public List<CSContact> replicaSet(
			CSOverlayKey key,
			int maxRank) {

		// The server is the only participant that holds objects in a DHT.
		// So the list is empty.
		return new LinkedList<CSContact>();
	}

	@Override
	public void route(CSOverlayKey key, Message msg,
			CSContact hint) {
		CSContact nextHop = null;

		if (hint != null) {
			nextHop = hint;
		} else {
			// This should only happen if the server is also a client
			nextHop = this.getLocalOverlayContact();
		}

		KBRForwardInformation<CSOverlayID, CSContact, CSOverlayKey> info = new KBRForwardInformationImpl<CSOverlayID, CSContact, CSOverlayKey>(
				key, msg,
				nextHop);
		kbrListener.forward(info);
		CSOverlayKey nextKey = info.getKey();
		Message nextMsg = info.getMessage();
		nextHop = info.getNextHopAgent();

		KBRForwardMsg<CSOverlayID, CSOverlayKey> fm = new KBRForwardMsg<CSOverlayID, CSOverlayKey>(
				getOverlayID(),
				nextHop.getOverlayID(),
				nextKey, nextMsg);
		getTransLayer().send(fm, nextHop.getTransInfo(), getPort(),
				TransProtocol.UDP);

	}

	@Override
	public boolean isRootOf(CSOverlayKey key) {
		// the server is root of all files
		return true;
	}

	@Override
	public CSOverlayKey getNewOverlayKey(int rank) {

		return new CSOverlayKey(Integer.toString(rank));
	}

	@Override
	public void setKBRListener(
			KBRListener<CSOverlayID, CSContact, CSOverlayKey> listener) {
		this.kbrListener = listener;
		KBRMsgHandler<CSOverlayID, CSContact, CSOverlayKey> msgHandler = new KBRMsgHandler<CSOverlayID, CSContact, CSOverlayKey>(
				this, this, kbrListener);

		kbrLookupProvider = msgHandler.getLookupProvider();
	}

	@Override
	public CSContact getLocalOverlayContact() {
		CSOverlayID overlayID = (CSOverlayID) getHost()
				.getOverlay(
						KBRNode.class).getOverlayID();
		short port = getHost().getOverlay(KBRNode.class).getPort();
		TransInfo transInfo = getHost().getTransLayer().getLocalTransInfo(port);

		return new CSContact(overlayID, transInfo);
	}

	@Override
	public CSContact getOverlayContact(CSOverlayID id,
			TransInfo transinfo) {
		return new CSContact(id, transinfo);
	}

	@Override
	public void hadContactTo(CSContact contact) {
		// TODO Auto-generated method stub
	}

	@Override
	public CSOverlayKey getRandomOverlayKey() {

		return getNewOverlayKey(Simulator.getRandom()
				.nextInt());
	}

	@Override
	public KBRLookupProvider<CSOverlayID, CSContact, CSOverlayKey> getKbrLookupProvider() {
		return kbrLookupProvider;
	}

	@Override
	public void registerDHTListener(DHTListener<CSOverlayKey> listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public int store(CSOverlayKey key, DHTObject obj,
			OperationCallback<Set<CSContact>> callback) {
		throw new UnsupportedOperationException(
				"server node cannot interact directly");
	}

	@Override
	public int valueLookup(CSOverlayKey key,
			OperationCallback<DHTObject> callback) {
		throw new UnsupportedOperationException(
				"server node cannot interact directly");
	}

	@Override
	public int nodeLookup(
			CSOverlayKey key,
			OperationCallback<List<CSContact>> callback,
			boolean returnSingleNode) {
		throw new UnsupportedOperationException(
				"server node cannot interact directly");
	}
}
