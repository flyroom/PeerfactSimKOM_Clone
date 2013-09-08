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

package org.peerfact.impl.util.oracle;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import org.peerfact.api.common.Host;
import org.peerfact.api.network.NetID;
import org.peerfact.api.overlay.OverlayNode;
import org.peerfact.api.scenario.Composable;
import org.peerfact.api.scenario.Configurator;
import org.peerfact.api.scenario.HostBuilder;


/**
 * This class gives access to the hosts of the scenario. To work, it has to be
 * referenced in the configuration file after the host builder.
 * 
 * The purpose of this class is to enable a global knowledge for analyzing. It
 * is not meant to be used within any functional parts of simulated systems.
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class GlobalOracle implements Composable {

	private HostBuilder hostBuilder;

	private static LinkedHashMap<NetID, Host> netIDtoHosts = new LinkedHashMap<NetID, Host>();

	private static List<Host> hosts = new LinkedList<Host>();

	@Override
	public void compose(Configurator config) {
		hostBuilder = (HostBuilder) config
				.getConfigurable(Configurator.HOST_BUILDER);

		hosts = hostBuilder.getAllHosts();
		for (Host host : hosts) {
			netIDtoHosts.put(host.getNetLayer().getNetID(), host);
		}
	}

	/**
	 * @param id
	 * @return the host with the given <code>NetID</code>
	 */
	public static Host getHostForNetID(NetID id) {
		return netIDtoHosts.get(id);
	}

	/**
	 * @return the list with all hosts of the scenario
	 */
	public static List<Host> getHosts() {
		return new LinkedList<Host>(hosts);
	}

	/**
	 * @return the number of online hosts in the scenario
	 */
	public static int getOnlineHostsNumber() {
		int count = 0;
		for (Host host : hosts) {
			Iterator<OverlayNode<?, ?>> iter = host.getOverlays();
			boolean present = false;
			while (!present && iter.hasNext()) {
				OverlayNode<?, ?> overlay = iter.next();
				if (overlay != null && overlay.isPresent()) {
					count++;
					present = true;
				}
			}
		}
		return count;
	}
}
