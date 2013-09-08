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

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.peerfact.api.transport.TransProtocol;
import org.peerfact.impl.common.Operations;
import org.peerfact.impl.overlay.AbstractOverlayNode;
import org.peerfact.impl.overlay.dht.centralizedstorage.operations.LookupOperation;
import org.peerfact.impl.overlay.dht.centralizedstorage.operations.StoreOperation;
import org.peerfact.impl.overlay.kbr.KBRMsgHandler;
import org.peerfact.impl.overlay.kbr.messages.KBRForwardInformationImpl;
import org.peerfact.impl.overlay.kbr.messages.KBRForwardMsg;
import org.peerfact.impl.simengine.Simulator;


/**
 * Client implementation for the server-based pseudo-dht. All store and lookup
 * requests will be forwarded to the server, which has the global view of the
 * system.
 * 
 * @author Konstantin Pussep
 * @version 0.1, 09.07.2007
 * 
 */
public class CSClientNode extends
		AbstractOverlayNode<CSOverlayID, CSContact>
		implements
		DHTNode<CSOverlayID, CSContact, CSOverlayKey> {

	private TransInfo serverAddress;

	static short port = 200;

	Map<CSOverlayKey, List<CSOverlayID>> contacts = new LinkedHashMap<CSOverlayKey, List<CSOverlayID>>();// TODO

	private KBRListener<CSOverlayID, CSContact, CSOverlayKey> kbrListener;

	private KBRLookupProvider<CSOverlayID, CSContact, CSOverlayKey> kbrLookupProvider;

	// many
	private BootstrapManager<CSServerNode> bootstrap;

	// private DHTListener<CSOverlayKey> dht = new
	// SimpleDHTService<CSOverlayKey>();

	private TransLayer transLayer;

	protected CSClientNode(TransLayer transLayer, short port) {
		super(new CSOverlayID(false), port);
		CSClientNode.port = port;
		this.transLayer = transLayer;
	}

	public CSOverlayID getClientID() {
		return getOverlayID();
	}

	@Override
	public int join(OperationCallback<Object> callback) {
		return Operations.scheduleEmptyOperation(this, callback);
	}

	@Override
	public int leave(OperationCallback<Object> callback) {
		return Operations.scheduleEmptyOperation(this, callback);
	}

	public TransInfo getServerAddress() {
		if (serverAddress == null) {
			List<TransInfo> bootstrapInfo = bootstrap.getBootstrapInfo();
			serverAddress = bootstrapInfo.isEmpty() ? null : bootstrapInfo
					.get(0);
		}
		return serverAddress;
	}

	@Override
	public int store(CSOverlayKey key, DHTObject obj,
			OperationCallback<Set<CSContact>> callback) {
		StoreOperation op = new StoreOperation(this, null, key, obj, callback);
		// Notify the analyzer
		Simulator.getMonitor().dhtStoreInitiated(this.getLocalOverlayContact(),
				key, obj);
		op.scheduleImmediately();
		return op.getOperationID();
	}

	@Override
	public void registerDHTListener(DHTListener<CSOverlayKey> listener) {
		// Do Nothing
	}

	@Override
	public int valueLookup(CSOverlayKey key,
			OperationCallback<DHTObject> callback) {

		LookupOperation op = new LookupOperation(this, null, key, callback);
		op.scheduleImmediately();
		return op.getOperationID();
	}

	void setBootstrap(BootstrapManager<CSServerNode> bootstrap) {
		this.bootstrap = bootstrap;
	}

	@Override
	public void connectivityChanged(ConnectivityEvent ce) {
		// FIXME Auto-generated method stub
	}

	@Override
	public int nodeLookup(
			CSOverlayKey key,
			OperationCallback<List<CSContact>> callback,
			boolean returnSingleNode) {
		// Inform the monitors about an initiated query
		Simulator.getMonitor()
				.dhtLookupInitiated(getLocalOverlayContact(), key);
		LookupOperation op = new LookupOperation(this, null, key, null);
		op.scheduleImmediately();
		return op.getOperationID();
	}

	@Override
	public String toString() {
		return "DHT Client Node ("
				+ this.transLayer.getLocalTransInfo(getPort()) + ")";
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
	public CSOverlayKey getRandomOverlayKey() {
		return new CSOverlayKey(String.valueOf(Simulator.getRandom()
				.nextInt()));
	}

	@Override
	public void route(CSOverlayKey key, Message msg,
			CSContact hint) {
		CSContact nextHop = null;
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
	public List<CSContact> local_lookup(
			CSOverlayKey key,
			int num) {
		List<CSContact> nexthops = new LinkedList<CSContact>();

		// Add the only next hop: the server
		nexthops.add(new CSContact(new CSOverlayID(
				true), getServerAddress()));
		return nexthops;
	}

	@Override
	public List<CSContact> replicaSet(
			CSOverlayKey key,
			int maxRank) {
		List<CSContact> replicas = new LinkedList<CSContact>();

		// Add the only possible replica: the server
		replicas.add(new CSContact(new CSOverlayID(
				true), getServerAddress()));
		return replicas;
	}

	@Override
	public List<CSContact> neighborSet(int num) {
		List<CSContact> neighbors = new LinkedList<CSContact>();

		// Add the only neighbor: the server
		neighbors.add(new CSContact(new CSOverlayID(
				true), getServerAddress()));
		return neighbors;
	}

	@Override
	public CSOverlayID[] range(CSContact contact, int rank) {
		CSOverlayID[] range = new CSOverlayID[2];

		// The client is responsible for only himself (range: [ownID, ownID])
		range[0] = getOverlayID();
		range[1] = range[0];

		return range;
	}

	@Override
	public boolean isRootOf(CSOverlayKey key) {
		// Clients can not be roots of documents
		return false;
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
	public CSOverlayKey getNewOverlayKey(int rank) {
		return new CSOverlayKey(Integer.toString(rank));
	}

	@Override
	public CSContact getLocalOverlayContact() {
		CSOverlayID overlayID = (CSOverlayID) getHost()
				.getOverlay(
						KBRNode.class).getOverlayID();
		short tempPort = getHost().getOverlay(KBRNode.class).getPort();
		TransInfo transInfo = getHost().getTransLayer().getLocalTransInfo(
				tempPort);

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
	public KBRLookupProvider<CSOverlayID, CSContact, CSOverlayKey> getKbrLookupProvider() {
		return kbrLookupProvider;
	}
}
