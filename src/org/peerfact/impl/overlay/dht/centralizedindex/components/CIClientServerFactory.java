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

import org.peerfact.api.common.ComponentFactory;
import org.peerfact.api.common.Host;
import org.peerfact.api.network.NetID;
import org.peerfact.api.overlay.OverlayNode;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.impl.service.aggregation.skyeye.analyzing.analyzers.NetLayerAnalyzer;

/**
 * Implementing a centralized DHT overlay, whose organization of the centralized
 * index is similar to the distributed index of Chord
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 08.12.2008
 */
public class CIClientServerFactory implements ComponentFactory {

	private short commonPort;

	private boolean isServer;

	private CIBootstrapManager bootstrap;

	private CIServerNode server;

	public CIClientServerFactory() {
		bootstrap = new CIBootstrapManager();
	}

	// the factory-method for a CIServerNode and a CIClientNode
	@Override
	public OverlayNode<?, ?> createComponent(Host host) {

		OverlayNode<?, ?> node;
		if (isServer) {
			node = createServer(host);
			server = (CIServerNode) node;
		} else {
			node = createClient(host);
		}
		return node;
	}

	// method for creating the server
	private CIServerNode createServer(Host host) {
		CIServerNode ciServer;
		NetID ip = host.getNetLayer().getNetID();
		// OPAnalyzer.setServerNetId(ip);
		NetLayerAnalyzer.setServerNetId(ip);
		ciServer = new CIServerNode(new CIOverlayID(ip), commonPort,
				host.getTransLayer(), bootstrap);
		return ciServer;
	}

	// method for creating the client
	private CIClientNode createClient(Host host) {
		CIClientNode client;
		CIOverlayID id = new CIOverlayID(host.getNetLayer()
				.getNetID());
		TransInfo t = host.getTransLayer().getLocalTransInfo(commonPort);
		CIOverlayContact c = new CIOverlayContact(id, t);
		client = new CIClientNode(commonPort, host.getTransLayer(),
				server, bootstrap, c);
		return client;
	}

	// method to set the port-parameter, defined in the xml-file
	public void setPort(long port) {
		this.commonPort = (short) port;
	}

	// method to set the isServer-parameter, defined in the xml-file
	public void setIsServer(boolean isServer) {
		this.isServer = isServer;
	}

}
