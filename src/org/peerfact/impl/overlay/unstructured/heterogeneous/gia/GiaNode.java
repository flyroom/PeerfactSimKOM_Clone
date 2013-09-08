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

/**
 * 
 */
package org.peerfact.impl.overlay.unstructured.heterogeneous.gia;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.peerfact.api.common.Host;
import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.overlay.NeighborDeterminator;
import org.peerfact.impl.common.Operations;
import org.peerfact.impl.overlay.unstructured.heterogeneous.api.GnutellaLikeOverlayContact;
import org.peerfact.impl.overlay.unstructured.heterogeneous.api.GnutellaOverlayID;
import org.peerfact.impl.overlay.unstructured.heterogeneous.api.IQueryInfo;
import org.peerfact.impl.overlay.unstructured.heterogeneous.api.QueryHit;
import org.peerfact.impl.overlay.unstructured.heterogeneous.common.AbstractGnutellaLikeNode;
import org.peerfact.impl.overlay.unstructured.heterogeneous.common.ConnectionManager.ConnectionManagerListener;
import org.peerfact.impl.overlay.unstructured.heterogeneous.common.ConnectionManager.ConnectionState;
import org.peerfact.impl.overlay.unstructured.heterogeneous.common.GnutellaBootstrap;
import org.peerfact.impl.simengine.Simulator;

/**
 * Gia is a Gnutella-like overlay which especially exploits heterogeneity of its
 * peers. Allows smooth heterogeneity transitions, unlike Gnutella 0.6 which
 * just separates its peers into two categories of ultrapeers and leaves. For
 * more information about Gia, please consult
 * "Making Gnutella-like P2P Systems Scalable" from Chawathe et al.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class GiaNode extends
		AbstractGnutellaLikeNode<GiaOverlayContact, IGiaConfig> implements
		ConnectionManagerListener<GiaOverlayContact, GiaConnectionMetadata> {

	private int capacity;

	private IGiaConfig config;

	GiaConnectionManager mgr;

	GiaPongHandler pongHdlr;

	OneHopReplicator replicator;

	GiaQueryManager qMgr;

	/**
	 * @param host
	 * @param id
	 * @param config
	 * @param bootstrap
	 * @param port
	 */
	public GiaNode(Host host, int capacity, GnutellaOverlayID id,
			IGiaConfig config,
			GnutellaBootstrap<GiaOverlayContact> bootstrap, short port) {
		super(host, id, config, bootstrap, port);

		this.capacity = capacity;
		this.config = config;

		mgr = new GiaConnectionManager(this, config, capacity);
		mgr.addListener(this);
		pongHdlr = new GiaPongHandler(this, mgr);
		mgr.setPongHandler(pongHdlr);
		IOHandler hdlr = new IOHandler(this, mgr, pongHdlr);
		host.getTransLayer().addTransMsgListener(hdlr, this.getPort());
		replicator = new OneHopReplicator(this, mgr);
		mgr.addListener(replicator);
		qMgr = new GiaQueryManager(mgr, this, this.getLocalClock());
		host.getTransLayer().addTransMsgListener(qMgr, this.getPort());
		mgr.addListener(qMgr);

	}

	/**
	 * Returns the degree of this node, i.e. the number of successfully
	 * connected contacts.
	 * 
	 * @return
	 */
	public int getDegree() {
		return mgr.getDegree();
	}

	@Override
	public IGiaConfig getConfig() {
		return config;
	}

	@Override
	public GiaOverlayContact getOwnContact() {
		return new GiaOverlayContact(id, host.getTransLayer()
				.getLocalTransInfo(port), capacity);
	}

	@Override
	protected void handleOfflineStatus() {
		mgr.flushRaw();
		mgr.stopTrigger();
	}

	@Override
	protected void handleOnlineStatus() {
		mgr.startTrigger();
	}

	@Override
	protected boolean hasConnection() {
		return mgr.getNumberOfContactsInState(ConnectionState.Connected) > 0;
	}

	@Override
	protected void initConnection(GiaOverlayContact contact) {
		mgr.seenContact(contact);
		mgr.startTrigger();
	}

	@Override
	protected void startQuery(
			IQueryInfo info,
			int hitsWanted,
			OperationCallback<List<QueryHit<GnutellaLikeOverlayContact>>> callback) {
		qMgr.query(info, hitsWanted, callback);

	}

	@Override
	protected void updateResources() {
		replicator.documentsChanged();
	}

	@Override
	protected boolean canBeUsedForBootstrapping() {
		return true;
	}

	@Override
	public String toString() {
		return this.getOwnContact().toString();
		// return this.getOwnContact() + ", " + (this.isOnline()?"on ":"off") +
		// "Cap=" + capacity + "    	Max=" + mgr.getMaxNbrs() + "	Min=" +
		// mgr.getMinNbrs() +
		// " Sat:" +
		// NumberFormatToolkit.floorToDecimalsString(mgr.getSatisfactionLevel(),
		// 2) + ", " + mgr.toString();
	}

	@Override
	public boolean hasLowConnectivity() {
		return mgr.getNumberOfContactsInState(ConnectionState.Connected) < getConfig()
				.getMinNbrs();
	}

	/**
	 * conn can be null
	 * 
	 * @param c
	 * @param conn
	 * @return
	 */
	public long getTokenAllocationRateFor(GiaOverlayContact c) {
		return qMgr.getTokenAllocationRateFor(c);
	}

	/**
	 * Returns the satisfaction level of this peer. This is a number between 0
	 * and 1 that shows how satisfied a peer is with its connectivity.
	 * 
	 * @return
	 */
	public double getSatisfactionLevel() {
		return mgr.getSatisfactionLevel();
	}

	@Override
	public void connectionEnded(GiaOverlayContact c,
			GiaConnectionMetadata metadata) {
		// Nothing to do
	}

	@Override
	public void lostConnectivity() {
		if (this.isPresent() && !this.isBootstrapping()) {
			Simulator.getMonitor().unstructuredReBootstrapped(getOwnContact());
			this.new BootstrapOperation(Operations.getEmptyCallback())
					.scheduleImmediately();
		}
	}

	@Override
	public void newConnectionEstablished(GiaOverlayContact c,
			GiaConnectionMetadata metadata) {
		// Nothing to do
	}

	@Override
	public NeighborDeterminator<GiaOverlayContact> getNeighbors() {
		return new NeighborDeterminator<GiaOverlayContact>() {

			@Override
			public Collection<GiaOverlayContact> getNeighbors() {
				Collection<GiaOverlayContact> coll = mgr
						.getConnectedContacts();
				return Collections.unmodifiableCollection(coll);
			}
		};
	}

	@Override
	public Collection<GiaOverlayContact> getConnectedContacts() {
		return mgr.getConnectedContacts();
	}

}
