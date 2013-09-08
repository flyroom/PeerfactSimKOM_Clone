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

package org.peerfact.impl.overlay.informationdissemination.cs;

import org.peerfact.api.common.Component;
import org.peerfact.api.common.ComponentFactory;
import org.peerfact.api.common.Host;

/**
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * This part of the Simulator is not maintained in the current version of
 * PeerfactSim.KOM. There is no intention of the authors to fix this
 * circumstances, since the changes needed are huge compared to overall benefit.
 * 
 * If you want it to work correctly, you are free to make the specific changes
 * and provide it to the community.
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! !!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * 
 * The {@link ComponentFactory} for the Server. It register the server in the
 * {@link CSBootstrapManager}, that are used by clients.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 01/06/2011
 * 
 */
public class ServerNodeFactory implements ComponentFactory {

	/**
	 * The listing port of the server.
	 */
	private short port;

	/**
	 * The maximal clients for the server
	 */
	private int maxClients;

	@Override
	public Component createComponent(Host host) {
		ServerNode<?, ?> node = new ServerNode(host.getTransLayer(), port,
				maxClients);
		CSBootstrapManager.getInstance().registerNode(node);
		return node;
	}

	public void setMaxClients(int maxClients) {
		this.maxClients = maxClients;
	}

	public void setPort(short port) {
		this.port = port;
	}

}
