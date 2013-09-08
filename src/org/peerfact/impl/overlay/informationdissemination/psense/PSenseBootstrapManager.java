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

package org.peerfact.impl.overlay.informationdissemination.psense;

import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.peerfact.api.overlay.BootstrapManager;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.impl.overlay.informationdissemination.psense.util.Configuration;
import org.peerfact.impl.overlay.informationdissemination.psense.util.Constants;
import org.peerfact.impl.simengine.Simulator;
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
 * The bootstrapManager is one instance, that know all online nodes. It is used
 * as "server" in pSense, that give a random number of Nodes, that are online in
 * the overlay.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 09/15/2010
 */
public class PSenseBootstrapManager implements BootstrapManager<PSenseNode> {

	/**
	 * The logger for this class
	 */
	final static Logger log = SimLogger.getLogger(PSenseBootstrapManager.class);

	private final List<TransInfo> bootstrapNodes = new Vector<TransInfo>();

	private static PSenseBootstrapManager singeltonInstance;

	private PSenseID lastAssigned = Constants.EMPTY_PSENSE_ID;

	public static PSenseBootstrapManager getInstance() {
		if (singeltonInstance == null) {
			singeltonInstance = new PSenseBootstrapManager();
		}
		return singeltonInstance;
	}

	@Override
	public void registerNode(PSenseNode node) {
		log.info("Register node: " + node.getOverlayID());
		if (bootstrapNodes.contains(node.getTransInfo())) {
			log.warn("Duplicate register of a node: "
					+ node.getOverlayID().getUniqueValue());
		}
		bootstrapNodes.add(node.getTransInfo());
	}

	@Override
	public void unregisterNode(PSenseNode node) {
		log.info("Unregister node: " + node.getOverlayID());
		if (!bootstrapNodes.contains(node.getTransInfo())) {
			log.warn("Unregister node, that not exists: "
					+ node.getOverlayID().getUniqueValue());
		}
		bootstrapNodes.remove(node.getTransInfo());
	}

	@Override
	public List<TransInfo> getBootstrapInfo() {
		return getBootstrapInfo(Configuration.NUMBER_SECTORS);
	}

	/**
	 * Gets a list of random contacts back. The list has the length of number.
	 * 
	 * @param number
	 *            The number of contacts
	 * @return A list of {@link TransInfo}.
	 */
	public List<TransInfo> getBootstrapInfo(int number) {
		List<TransInfo> randomTransInfos = new Vector<TransInfo>();
		for (int i = 0; i < number; i++) {
			int randInt = Simulator.getRandom().nextInt(bootstrapNodes.size());
			randomTransInfos.add(bootstrapNodes.get(randInt));
		}
		return randomTransInfos;
	}

	/**
	 * Create and get the next unique PSenseID return. The returned value is the
	 * new {@link #lastAssigned}.
	 * 
	 * @return A new unique PSenseID
	 */
	synchronized public PSenseID getNextUniquePSenseID() {
		if (lastAssigned == Constants.EMPTY_PSENSE_ID) {
			lastAssigned = new PSenseID(0);
		} else {
			lastAssigned = new PSenseID(lastAssigned.getUniqueValue() + 1);
		}
		if (log.isDebugEnabled()) {
			log.debug("New unique PSense ID created: "
					+ lastAssigned.getUniqueValue());
		}
		return lastAssigned;
	}

	public boolean anyNodeAvailable() {
		return !bootstrapNodes.isEmpty();
	}

}
