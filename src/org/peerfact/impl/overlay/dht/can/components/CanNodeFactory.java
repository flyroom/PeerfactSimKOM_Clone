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

package org.peerfact.impl.overlay.dht.can.components;

import org.apache.log4j.Logger;
import org.peerfact.api.common.Component;
import org.peerfact.api.common.ComponentFactory;
import org.peerfact.api.common.Host;
import org.peerfact.impl.overlay.AbstractOverlayNode.PeerStatus;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.util.logging.SimLogger;

/**
 * 
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 * 
 */
public class CanNodeFactory implements ComponentFactory {

	private static Logger log = SimLogger.getLogger(CanNode.class);

	private short port = 123;

	CanBootstrapManager bootstrap = new CanBootstrapManager();

	@Override
	public Component createComponent(Host host) {
		CanNode node = new CanNode(host.getTransLayer(), port);
		node.setBootstrap(bootstrap);
		node.setPeerStatus(PeerStatus.TO_JOIN);

		log.debug(Simulator.getSimulatedRealtime() + ": Create NodeFactory");
		return node;
	}

	public void setPort(int port) {
		this.port = (short) port;
	}

}
