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

import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.peerfact.api.overlay.BootstrapManager;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.impl.util.logging.SimLogger;


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
 * The BootstrapManager for clients in the Client Server IDO Overlay. It is a
 * singleton. It contains the server, which is to use to disseminate the
 * positions.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 01/06/2011
 * 
 */
public class CSBootstrapManager implements BootstrapManager<ServerNode<?, ?>> {
	/**
	 * The logger for this class
	 */
	final static Logger log = SimLogger.getLogger(CSBootstrapManager.class);

	/**
	 * A list of {@link ServerNode}.
	 */
	private List<ServerNode<?, ?>> serverNodes = new Vector<ServerNode<?, ?>>();

	/**
	 * The only instance of this class
	 */
	private static CSBootstrapManager instance;

	/**
	 * Private Constructor, to create this bootstrapManager
	 */
	private CSBootstrapManager() {
		// to overwrite the standard constructor, because should be
		// private
	}

	/**
	 * Returns the only instance of this class.
	 * 
	 * @return An instance of this class.
	 */
	public static CSBootstrapManager getInstance() {
		if (instance == null) {
			instance = new CSBootstrapManager();
		}
		return instance;
	}

	@Override
	public void registerNode(ServerNode<?, ?> node) {
		log.info("The ServerNode " + node + " will be registered");
		if (serverNodes.contains(node)) {
			log.warn("The same ServerNode is registered");
		} else {
			serverNodes.add(node);
		}
	}

	@Override
	public void unregisterNode(ServerNode<?, ?> node) {
		log.info("The ServerNode " + node + " will be unregistered");
		if (serverNodes.contains(node)) {
			serverNodes.add(node);
		} else {
			log.warn("The serverNode cannot unregistered. It is not registered!");
		}
	}

	@Override
	public List<TransInfo> getBootstrapInfo() {
		List<TransInfo> result = new Vector<TransInfo>();
		for (ServerNode<?, ?> node : serverNodes) {
			result.add(node.getTransLayer().getLocalTransInfo(node.getPort()));
		}
		return result;
	}

}
