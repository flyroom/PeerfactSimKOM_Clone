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

package org.peerfact.impl.overlay.unstructured.heterogeneous.gia;

import org.apache.log4j.Logger;
import org.peerfact.api.common.Component;
import org.peerfact.api.common.ComponentFactory;
import org.peerfact.api.common.Host;
import org.peerfact.api.network.Bandwidth;
import org.peerfact.impl.overlay.unstructured.heterogeneous.api.GnutellaOverlayID;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.util.logging.SimLogger;


/**
 * Factory component for Gia overlay peers.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class GiaFactory implements ComponentFactory {

	int idCounter = 0;

	private static Logger log = SimLogger.getLogger(GiaFactory.class);

	protected int sumCapacityAssigned = 0;

	protected int numCapacitiesAssigned = 0;

	IGiaConfig config = new GiaDefaultConfig();

	@Override
	public Component createComponent(Host host) {
		return new GiaNode(host, getCapacityForNode(host), getNewID(), config,
				GiaBootstrap.getInstance(), (short) 2345);
	}

	int getCapacityForNode(Host host) {
		return calculateCapacity(host);
	}

	/**
	 * Generates a new unique Overlay ID.
	 * 
	 * @return
	 */
	GnutellaOverlayID getNewID() {
		GnutellaOverlayID result = new GnutellaOverlayID(idCounter);
		idCounter++;
		return result;
	}

	/**
	 * @return capacity of the peer in operations per minute
	 */
	public int calculateCapacity(Host host) {

		int cap = calculateCapacityAdvanced(host);

		numCapacitiesAssigned++;
		sumCapacityAssigned += cap;

		log.debug("Assigning capacity ( " + cap + " to host " + host
				+ ". Mean capacity assigned: " + sumCapacityAssigned
				/ numCapacitiesAssigned);
		return cap;
	}

	/**
	 * Simple calculation of capacity for Gia peers, according to the paper. No
	 * smooth transitions, only 1,10,100,1k and 10k possible for capacity
	 * values.
	 * 
	 * @param host
	 * @return
	 */
	protected static int calculateCapacitySimple(Host host) {
		double bw = host.getNetLayer().getMaxBandwidth().getUpBW();
		int rand = Simulator.getRandom().nextInt(100);

		if (bw < 7500) {
			return 1;
		}
		if (bw < 50000) {
			return rand < 31.25 ? 1 : 10;
		}
		if (bw < 88000) {
			return rand < 80.952 ? 10 : 100;
		}
		if (bw < 200000) {
			return 100;
		}

		if (rand < 17) {
			return 100;
		}
		if (rand < 67) {
			return 1000;
		}
		return 10000;

	}

	/**
	 * Advanced capacity calculation. Accepts smooth transitions.
	 * 
	 * @param host
	 * @return
	 */
	protected int calculateCapacityAdvanced(Host host) {

		Bandwidth bw = host.getNetLayer().getMaxBandwidth();

		double downloadBW = bw.getDownBW();
		double uploadBW = bw.getUpBW();

		double capFromDown = downloadBW / config.getDownBWPerCapPoint();
		double capFromUp = uploadBW / config.getUpBWPerCapPoint();

		log.debug("Capacity from Downstream: " + capFromDown
				+ " Capacity from Upstream: " + capFromUp);

		return (int) Math.max(config.getMinCapacity(), Math.min(
				capFromDown, capFromUp));
	}

}
