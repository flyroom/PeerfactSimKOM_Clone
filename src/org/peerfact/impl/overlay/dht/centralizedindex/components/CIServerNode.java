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
import org.peerfact.api.overlay.kbr.KBRNode;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.api.transport.TransLayer;
import org.peerfact.api.transport.TransProtocol;
import org.peerfact.impl.overlay.AbstractOverlayNode;
import org.peerfact.impl.overlay.dht.centralizedindex.operations.ServerJoinOperation;
import org.peerfact.impl.overlay.kbr.KBRMsgHandler;
import org.peerfact.impl.overlay.kbr.messages.KBRForwardInformationImpl;
import org.peerfact.impl.overlay.kbr.messages.KBRForwardMsg;
import org.peerfact.impl.simengine.Simulator;


/**
 * Implementing a centralized DHT overlay, whose organization of the centralized
 * index is similar to the distributed index of Chord
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 08.12.2008
 */
public class CIServerNode extends
		AbstractOverlayNode<CIOverlayID, CIOverlayContact> implements
		DHTNode<CIOverlayID, CIOverlayContact, CIOverlayKey> {

	private TransLayer transLayer;

	private CIServerMessageHandler messageHandler;

	private CIDHT dht;

	private KBRListener<CIOverlayID, CIOverlayContact, CIOverlayKey> kbrListener;

	private KBRLookupProvider<CIOverlayID, CIOverlayContact, CIOverlayKey> kbrLookupProvider;

	public CIServerNode(CIOverlayID peerId, short port,
			TransLayer transLayer, CIBootstrapManager bootstrap) {
		super(peerId, port);
		this.transLayer = transLayer;
		bootstrap.registerNode(this);
		messageHandler = new CIServerMessageHandler(this);
		this.transLayer.addTransMsgListener(messageHandler, getPort());
		dht = new CIDHT();
	}

	@Override
	public TransLayer getTransLayer() {
		return transLayer;
	}

	@Override
	public void connectivityChanged(ConnectivityEvent ce) {
		// Server is intented not to go down, because this would break the whole
		// system and stop the simulation
	}

	/*
	 * public CIOverlayID generateNapsterOverlayID(IPv4NetID ip) { return new
	 * CIOverlayID(Math.abs(ip.toString().hashCode())); // return new
	 * CIOverlayID(ip.getID().longValue()); }
	 */

	public CIDHT getDHT() {
		return dht;
	}

	@Override
	public int join(OperationCallback<Object> callback) {
		ServerJoinOperation serverJoinOperation = new ServerJoinOperation(this,
				callback);
		serverJoinOperation.scheduleImmediately();
		return serverJoinOperation.getOperationID();
	}

	@Override
	public int leave(OperationCallback<Object> callback) {
		throw new IllegalStateException("server cannot leave network");
	}

	@Override
	public NeighborDeterminator<CIOverlayContact> getNeighbors() {
		return null;
	}

	@Override
	public List<CIOverlayContact> local_lookup(
			CIOverlayKey key,
			int num) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CIOverlayContact> neighborSet(int num) {
		// not yet implemented
		return null;
	}

	@Override
	public CIOverlayID[] range(CIOverlayContact contact, int rank) {
		// not yet implemented
		return null;
	}

	@Override
	public List<CIOverlayContact> replicaSet(
			CIOverlayKey key,
			int maxRank) {

		// The server is the only participant that holds objects in a DHT.
		// So the list is empty.
		return new LinkedList<CIOverlayContact>();
	}

	@Override
	public void route(CIOverlayKey key, Message msg,
			CIOverlayContact hint) {
		CIOverlayContact nextHop = null;

		if (hint != null) {
			nextHop = hint;
		} else {
			// This should only happen if the server is also a client
			nextHop = this.getLocalOverlayContact();
		}

		KBRForwardInformation<CIOverlayID, CIOverlayContact, CIOverlayKey> info = new KBRForwardInformationImpl<CIOverlayID, CIOverlayContact, CIOverlayKey>(
				key, msg,
				nextHop);
		kbrListener.forward(info);
		CIOverlayKey nextKey = info.getKey();
		Message nextMsg = info.getMessage();
		nextHop = info.getNextHopAgent();

		KBRForwardMsg<CIOverlayID, CIOverlayKey> fm = new KBRForwardMsg<CIOverlayID, CIOverlayKey>(
				getOverlayID(),
				nextHop.getOverlayID(),
				nextKey, nextMsg);
		getTransLayer().send(fm, nextHop.getTransInfo(), getPort(),
				TransProtocol.UDP);

	}

	@Override
	public boolean isRootOf(CIOverlayKey key) {
		// the server is root of all files
		return true;
	}

	@Override
	public CIOverlayKey getNewOverlayKey(int rank) {

		return new CIOverlayKey(new BigInteger(Integer.toString(rank)));
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
	public CIOverlayContact getLocalOverlayContact() {
		CIOverlayID overlayID = (CIOverlayID) getHost()
				.getOverlay(
						KBRNode.class).getOverlayID();
		short port = getHost().getOverlay(KBRNode.class).getPort();
		TransInfo transInfo = getHost().getTransLayer().getLocalTransInfo(port);

		return new CIOverlayContact(overlayID, transInfo);
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
	public CIOverlayKey getRandomOverlayKey() {

		return getNewOverlayKey(Simulator.getRandom()
				.nextInt());
	}

	@Override
	public KBRLookupProvider<CIOverlayID, CIOverlayContact, CIOverlayKey> getKbrLookupProvider() {
		return kbrLookupProvider;
	}

	@Override
	public void registerDHTListener(DHTListener<CIOverlayKey> listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public int store(CIOverlayKey key, DHTObject obj,
			OperationCallback<Set<CIOverlayContact>> callback) {
		throw new UnsupportedOperationException(
				"server node cannot interact directly");
	}

	@Override
	public int valueLookup(CIOverlayKey key,
			OperationCallback<DHTObject> callback) {
		throw new UnsupportedOperationException(
				"server node cannot interact directly");
	}

	@Override
	public int nodeLookup(
			CIOverlayKey key,
			OperationCallback<List<CIOverlayContact>> callback,
			boolean returnSingleNode) {
		throw new UnsupportedOperationException(
				"server node cannot interact directly");
	}
}
