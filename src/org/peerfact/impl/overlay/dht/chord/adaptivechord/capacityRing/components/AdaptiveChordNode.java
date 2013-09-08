/*
 * Copyright (c) 2012-2013 Open Source Community - <http://www.peerfact.org>
 * Copyright (c) 2011-2012 University of Paderborn - UPB
 * Copyright (c) 2005-2011 KOM - Multimedia Communications Lab
 *
 * This file is part of PeerfactSim.
 * 
 * PeerfactSim is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 * 
 * PeerfactSim is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with PeerfactSim.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.peerfact.impl.overlay.dht.chord.adaptivechord.capacityRing.components;

import java.util.List;
import java.util.Set;

import org.peerfact.api.common.ConnectivityEvent;
import org.peerfact.api.common.Host;
import org.peerfact.api.common.Message;
import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.overlay.JoinLeaveOverlayNode;
import org.peerfact.api.overlay.NeighborDeterminator;
import org.peerfact.api.overlay.OverlayNode;
import org.peerfact.api.overlay.dht.DHTListener;
import org.peerfact.api.overlay.dht.DHTObject;
import org.peerfact.api.overlay.kbr.KBRListener;
import org.peerfact.api.overlay.kbr.KBRLookupProvider;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.api.transport.TransLayer;
import org.peerfact.impl.overlay.dht.chord.adaptivechord.capacityRing.interfaces.AdaptiveRemoteControl;
import org.peerfact.impl.overlay.dht.chord.adaptivechord.capacityRing.operations.LoadbalancingOperation;
import org.peerfact.impl.overlay.dht.chord.adaptivechord.objectRing.components.ChordNodeType;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordContact;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordMessageHandler;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordNode;
import org.peerfact.impl.overlay.dht.chord.base.components.ChordBootstrapManager;
import org.peerfact.impl.overlay.dht.chord.base.components.ChordID;
import org.peerfact.impl.overlay.dht.chord.base.components.ChordKey;
import org.peerfact.impl.simengine.Simulator;


/**
 * 
 * This class represents a Peer/Host in Chord Overlay and the main
 * functionality.
 * 
 * @author Minh Hoang Nguyen
 */
public class AdaptiveChordNode extends AbstractChordNode {

	// node that is part of the p2p network that actually does file transfers
	org.peerfact.impl.overlay.dht.chord.adaptivechord.objectRing.components.ChordNode dataNetNode;

	// node that is used to participate in the network used to sort nodes
	// according to their capacity.
	AdaptiveRemoteControl loadBalancingNetNode;

	public AdaptiveChordNode(TransLayer transLayer, short portDataNetwork,
			short portLoadbalancingNetwork,
			ChordBootstrapManager bootstrapData,
			ChordBootstrapManager bootstrapLoadBalancing) {

		super(new ChordID(transLayer.getLocalTransInfo(portDataNetwork)),
				portDataNetwork);

		// participate in the networks.
		dataNetNode = new org.peerfact.impl.overlay.dht.chord.adaptivechord.objectRing.components.ChordNode(
				transLayer, portDataNetwork, bootstrapData);
		dataNetNode.setPeerStatus(PeerStatus.TO_JOIN);

		loadBalancingNetNode = new org.peerfact.impl.overlay.dht.chord.adaptivechord.objectRing.components.ChordNode(
				transLayer, portLoadbalancingNetwork, bootstrapLoadBalancing);
		loadBalancingNetNode.setPeerStatus(PeerStatus.TO_JOIN);

		dataNetNode.setNodeType(ChordNodeType.DATANETNODE);
		loadBalancingNetNode.setNodeType(ChordNodeType.LOADBALANCINGNETNODE);

		dataNetNode.zhNode = this;
		((org.peerfact.impl.overlay.dht.chord.adaptivechord.objectRing.components.ChordNode) loadBalancingNetNode).zhNode = this;

		dataNetNode.initReoccuringOperations();
		loadBalancingNetNode.initReoccuringOperations();

		// start load balancing.
		LoadbalancingOperation op = new LoadbalancingOperation(this);
		op.scheduleWithDelay(Simulator.MINUTE_UNIT
				+ (long) (Simulator.getRandom().nextDouble()
						* Simulator.MINUTE_UNIT * 30));
	}

	public OverlayNode<ChordID, AbstractChordContact> getDataNetNode() {
		return dataNetNode;
	}

	public OverlayNode<ChordID, AbstractChordContact> getLoadbalancingNetNode() {
		return loadBalancingNetNode;
	}

	@Override
	public final void setHost(final Host host) {
		super.setHost(host);
		dataNetNode.setHost(host);
		loadBalancingNetNode.setHost(host);

	}

	@Override
	public int join(OperationCallback<Object> callback) {
		((JoinLeaveOverlayNode<ChordID, AbstractChordContact>) loadBalancingNetNode)
				.join(null); // - do not
		// join
		// automatically!
		return dataNetNode.join(callback);
	}

	@Override
	public int leave(OperationCallback<Object> callback) {
		((JoinLeaveOverlayNode<ChordID, AbstractChordContact>) loadBalancingNetNode)
				.leave(null);
		return dataNetNode.leave(callback);
	}

	@Override
	public NeighborDeterminator getNeighbors() {
		return dataNetNode.getNeighbors();
	}

	@Override
	public void registerDHTListener(DHTListener<ChordKey> listener) {
		dataNetNode.registerDHTListener(listener);

	}

	@Override
	public void connectivityChanged(ConnectivityEvent ce) {
		dataNetNode.connectivityChanged(ce);
		loadBalancingNetNode.connectivityChanged(ce);
	}

	@Override
	public void route(ChordKey key, Message msg, AbstractChordContact hint) {
		dataNetNode.route(key, msg, hint);
	}

	@Override
	public List<AbstractChordContact> local_lookup(ChordKey key, int num) {
		return dataNetNode.local_lookup(key, num);
	}

	@Override
	public List<AbstractChordContact> replicaSet(ChordKey key, int maxRank) {
		return dataNetNode.replicaSet(key, maxRank);
	}

	@Override
	public List<AbstractChordContact> neighborSet(int num) {
		return dataNetNode.neighborSet(num);
	}

	@Override
	public ChordID[] range(AbstractChordContact contact, int rank) {
		return dataNetNode.range(contact, rank);
	}

	@Override
	public boolean isRootOf(ChordKey key) {
		return dataNetNode.isRootOf(key);
	}

	@Override
	public void setKBRListener(
			KBRListener<ChordID, AbstractChordContact, ChordKey> listener) {
		dataNetNode.setKBRListener(listener);
	}

	@Override
	public ChordKey getNewOverlayKey(int rank) {
		return dataNetNode.getNewOverlayKey(rank);
	}

	@Override
	public ChordKey getRandomOverlayKey() {
		return dataNetNode.getRandomOverlayKey();
	}

	@Override
	public AbstractChordContact getLocalOverlayContact() {
		return dataNetNode.getLocalOverlayContact();
	}

	@Override
	public AbstractChordContact getOverlayContact(ChordID id,
			TransInfo transinfo) {
		return dataNetNode.getOverlayContact(id, transinfo);
	}

	@Override
	public void hadContactTo(AbstractChordContact contact) {
		dataNetNode.hadContactTo(contact);

	}

	@Override
	public KBRLookupProvider<ChordID, AbstractChordContact, ChordKey> getKbrLookupProvider() {
		return dataNetNode.getKbrLookupProvider();
	}

	@Override
	public int store(ChordKey key, DHTObject obj,
			OperationCallback<Set<AbstractChordContact>> callback) {
		return dataNetNode.store(key, obj, callback);
	}

	@Override
	public int valueLookup(ChordKey key,
			OperationCallback<DHTObject> callback) {
		return dataNetNode.valueLookup(key, callback);
	}

	@Override
	public int nodeLookup(ChordKey key,
			OperationCallback<List<AbstractChordContact>> callback,
			boolean returnSingleNode) {
		return dataNetNode.nodeLookup(key, callback, returnSingleNode);
	}

	@Override
	public TransLayer getTransLayer() {
		return dataNetNode.getTransLayer();
	}

	@Override
	public int joinWithDelay(OperationCallback<Object> callback, long delay) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void leaveOperationFinished() {
		// TODO Auto-generated method stub

	}

	@Override
	public AbstractChordMessageHandler getMessageHandler() {
		// TODO Auto-generated method stub
		return null;
	}

} // class
