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

package org.peerfact.impl.overlay.informationdissemination.von;

import java.util.LinkedList;
import java.util.List;

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
 * This class implements the bootstrapping of VON. Normally the first joining
 * node is registered as bootstrap node.
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class VonBootstrapManager implements BootstrapManager<VonNode> {

	final static Logger log = SimLogger.getLogger(VonBootstrapManager.class);

	private static VonBootstrapManager singeltonInstance;

	private final LinkedList<TransInfo> bootstrapNodes = new LinkedList<TransInfo>();

	public static VonBootstrapManager getInstance() {
		if (singeltonInstance == null) {
			singeltonInstance = new VonBootstrapManager();
		}
		return singeltonInstance;
	}

	@Override
	public List<TransInfo> getBootstrapInfo() {
		return bootstrapNodes;
	}

	@Override
	public void registerNode(VonNode node) {
		bootstrapNodes.add(node.getTransInfo());

		log.error(node.getOverlayID() + " is new bootstrap manager ("
				+ node.getHost().getNetLayer().getNetID() + ")");
	}

	@Override
	public void unregisterNode(VonNode node) {
		bootstrapNodes.remove(node.getTransInfo());
	}

	public boolean anyNodeAvailable() {
		return !bootstrapNodes.isEmpty();
	}
}
