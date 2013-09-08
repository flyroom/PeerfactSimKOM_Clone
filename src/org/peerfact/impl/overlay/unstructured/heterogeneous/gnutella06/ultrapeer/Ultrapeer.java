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

package org.peerfact.impl.overlay.unstructured.heterogeneous.gnutella06.ultrapeer;

import java.util.Collection;
import java.util.List;

import org.peerfact.api.common.Host;
import org.peerfact.api.common.OperationCallback;
import org.peerfact.impl.common.Operations;
import org.peerfact.impl.overlay.unstructured.heterogeneous.api.Gnutella06OverlayContact;
import org.peerfact.impl.overlay.unstructured.heterogeneous.api.GnutellaLikeOverlayContact;
import org.peerfact.impl.overlay.unstructured.heterogeneous.api.GnutellaOverlayID;
import org.peerfact.impl.overlay.unstructured.heterogeneous.api.IQueryInfo;
import org.peerfact.impl.overlay.unstructured.heterogeneous.api.QueryHit;
import org.peerfact.impl.overlay.unstructured.heterogeneous.common.ConnectionManager.ConnectionManagerListener;
import org.peerfact.impl.overlay.unstructured.heterogeneous.common.ConnectionManager.ConnectionState;
import org.peerfact.impl.overlay.unstructured.heterogeneous.common.GnutellaBootstrap;
import org.peerfact.impl.overlay.unstructured.heterogeneous.gnutella06.AbstractGnutella06Node;
import org.peerfact.impl.overlay.unstructured.heterogeneous.gnutella06.Gnutella06ConnectionManager;
import org.peerfact.impl.overlay.unstructured.heterogeneous.gnutella06.IGnutella06Config;
import org.peerfact.impl.simengine.Simulator;

/**
 * An ultrapeer, also called hub, is the component doing the whole Gnutella
 * traffic. It allows connections from other ultrapeers as well as leaves, which
 * connect to them like to a server.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class Ultrapeer extends AbstractGnutella06Node implements
		ConnectionManagerListener<Gnutella06OverlayContact, LeafInfo> {

	Gnutella06ConnectionManager<Object> upMgr;

	private Gnutella06ConnectionManager<LeafInfo> leafMgr;

	IOHandler io;

	private PongHandler pongHdlr;

	QueryHandler qHndlr;

	/**
	 * Creates a new ultrapeer
	 * 
	 * @param host
	 *            : the host this ultrapeer is part of
	 * @param id
	 *            : the Overlay ID to use
	 * @param config
	 *            : the configuration to use
	 * @param bootstrap
	 *            : the bootstrap source to use
	 * @param port
	 *            : the port to listen on
	 */
	public Ultrapeer(Host host, GnutellaOverlayID id, IGnutella06Config config,
			GnutellaBootstrap<Gnutella06OverlayContact> bootstrap, short port) {
		super(host, id, config, bootstrap, port);

		upMgr = new Gnutella06ConnectionManager<Object>(this, this.getConfig(),
				this.getConfig().getMaxSPToSPConnections(),
				config.randomlyKickPeerProb());
		leafMgr = new Gnutella06ConnectionManager<LeafInfo>(this,
				this.getConfig(), this.getConfig().getMaxSPToLeafConnections(),
				0);
		pongHdlr = new PongHandler(this, upMgr);
		upMgr.setPongHandler(pongHdlr);
		leafMgr.addListener(this);
		qHndlr = new QueryHandler(this, leafMgr, upMgr);
		io = new IOHandler(upMgr, leafMgr, qHndlr, this, pongHdlr);
		host.getTransLayer().addTransMsgListener(io, this.getPort());
	}

	@Override
	protected boolean isUltrapeer() {
		return true;
	}

	@Override
	protected void initConnection(Gnutella06OverlayContact contact) {
		upMgr.seenContact(contact);
	}

	@Override
	public String toString() {
		return "UlPr " + (this.isPresent() ? "on " : "off")
				+ this.getOwnContact() + upMgr.toString() + ", "
				+ leafMgr.toString() + ", " + pongHdlr.getLocalPongCache();
	}

	@Override
	protected void updateResources() {
		// Superpeers have nothing to do here.
	}

	@Override
	public void connectionEnded(Gnutella06OverlayContact c, LeafInfo info) {
		// TODO Auto-generated method stub

	}

	@Override
	public void newConnectionEstablished(Gnutella06OverlayContact c,
			LeafInfo info) {
		leafMgr.putMetadata(c, new LeafInfo());
	}

	@Override
	protected void startQuery(
			IQueryInfo info,
			int hitsWanted,
			OperationCallback<List<QueryHit<GnutellaLikeOverlayContact>>> callback) {
		qHndlr.startQuery(info, hitsWanted, callback);
	}

	@Override
	protected void handleOfflineStatus() {
		upMgr.flushRaw();
		leafMgr.flushRaw();
	}

	@Override
	protected void handleOnlineStatus() {
		// Nothing to do
	}

	@Override
	protected boolean hasConnection() {
		return upMgr.getNumberOfContactsInState(ConnectionState.Connected) > 0;
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
	public boolean hasLowConnectivity() {
		return upMgr.getNumberOfContactsInState(ConnectionState.Connected) < 5;
	}

	@Override
	public Collection<Gnutella06OverlayContact> getConnectedContacts() {
		return upMgr.getConnectedContacts();
	}

}
