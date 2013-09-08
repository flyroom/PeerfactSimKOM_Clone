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

package org.peerfact.impl.service.dhtstorage.past;

import org.apache.log4j.Logger;
import org.peerfact.api.common.Component;
import org.peerfact.api.common.ComponentFactory;
import org.peerfact.api.common.Host;
import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.api.overlay.OverlayID;
import org.peerfact.api.overlay.dht.DHTKey;
import org.peerfact.api.overlay.dht.DHTListenerSupported;


/**
 * Create a new ServiceInstance for a ReplicatingDHT. All Config-Parameters are
 * prepopulated with default values.
 * 
 * Usage: add the Service to your Host, right after the specification of the
 * overlayNode-Factory. It will register itself as a DHTListener at the node.
 * 
 * @author Bjoern Richerzhagen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class PASTServiceFactory implements ComponentFactory {

	static final Logger log = Logger
			.getLogger(PASTServiceFactory.class);

	PASTConfig config = new PASTConfig();

	@Override
	public Component createComponent(Host host) {

		short port = 609;
		DHTListenerSupported<OverlayID<?>, OverlayContact<OverlayID<?>>, DHTKey<?>> node = host
				.getComponent(DHTListenerSupported.class);
		if (node == null) {
			log.error("DHTService could not be started: there is no DHTListenerSupported Node!");
			return null;
		}
		PASTService service = new PASTService(host, port,
				node, config);
		node.registerDHTListener(service);

		return service;

	}

	/**
	 * Number of replicates to create
	 * 
	 * @param number
	 */
	public void setNumberOfReplicates(int number) {
		config.setNumberOfReplicates(number);
	}

	/**
	 * Each Ping will be tried <code>number</code> times before the contact is
	 * declared offline
	 * 
	 * @param number
	 */
	public void setNumberOfPingTries(int number) {
		config.setNumberOfPingTries(number);
	}

	/**
	 * Time between PING-Operations
	 * 
	 * @param time
	 */
	public void setTimeBetweenRootPings(long time) {
		config.setTimeBetweenRootPings(time);
	}

	public void setNumberOfReplicationTries(int numberOfReplicationTries) {
		config.setNumberOfReplicationTries(numberOfReplicationTries);
	}

	public void setMaxNumberOfReplicates(int maxNumberOfReplicates) {
		config.setMaxNumberOfReplicates(maxNumberOfReplicates);
	}

	public void setDropFiles(boolean dropFiles) {
		config.setDropFiles(dropFiles);
	}

}
