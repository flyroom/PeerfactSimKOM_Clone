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

import org.peerfact.api.common.ComponentFactory;
import org.peerfact.api.common.Host;
import org.peerfact.api.overlay.BootstrapManager;
import org.peerfact.api.overlay.OverlayNode;

/**
 * Factory for client and server nodes of the server-based pseudo-dht.
 * 
 * @author Konstantin Pussep
 * @author Sebastian Kaune
 * @version 3.0, 29.11.2007
 * 
 */
public class CSClientServerFactory implements ComponentFactory {

	private short clientPort;

	private boolean isServer;

	BootstrapManager<CSServerNode> bootstrap = new CSBootstrapManager();

	protected CSClientNode createClient(Host host) {
		CSClientNode cSClientNode = new CSClientNode(host.getTransLayer(), clientPort);
		cSClientNode.setBootstrap(bootstrap);
		return cSClientNode;
	}

	private CSServerNode createServer(Host host) {
		CSServerNode cSServerNode = new CSServerNode(host.getTransLayer());
		cSServerNode.setBootstrap(bootstrap);
		return cSServerNode;
	}

	/**
	 * The port which will be used by clients
	 * 
	 * @param port
	 */
	public void setPort(long port) {
		this.clientPort = (short) port;
	}

	/**
	 * Whether the next node will be ServerNode or ClientNode
	 * 
	 * @param isServer
	 */
	public void setIsServer(boolean isServer) {
		this.isServer = isServer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.peerfact.api.common.ComponentFactory#createComponent(de.tud.
	 * kom.p2psim.api.scenario.Host)
	 */
	@Override
	public OverlayNode<CSOverlayID, CSContact> createComponent(
			Host host) {
		// TransLayer transLayer = host.getTransport();
		OverlayNode<CSOverlayID, CSContact> node;
		if (isServer) {
			node = createServer(host);
		} else {
			node = createClient(host);
		}

		return node;
	}

	@Override
	public String toString() {
		return "Centralized Client/Server Factory";
	}

}
