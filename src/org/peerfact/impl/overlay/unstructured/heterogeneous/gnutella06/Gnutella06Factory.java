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

package org.peerfact.impl.overlay.unstructured.heterogeneous.gnutella06;

import org.peerfact.api.common.Component;
import org.peerfact.api.common.ComponentFactory;
import org.peerfact.api.common.Host;
import org.peerfact.impl.overlay.unstructured.heterogeneous.api.GnutellaOverlayID;
import org.peerfact.impl.overlay.unstructured.heterogeneous.gnutella06.leaf.Leaf;
import org.peerfact.impl.overlay.unstructured.heterogeneous.gnutella06.ultrapeer.Ultrapeer;
import org.peerfact.impl.simengine.Simulator;

/**
 * Node factory for Gnutella 0.6. Decides whether a node shall be an ultrapeer
 * or not and constructs it according to the default configuration.
 * 
 * @author <info@peerfact.org>
 * 
 * @version 05/06/2011
 */
public class Gnutella06Factory implements ComponentFactory {

	int idCounter = 0;

	IGnutella06Config config = new Gnutella06DefaultConfig();

	@Override
	public Component createComponent(Host host) {
		if (shallBeUltrapeer(host)) {
			return new Ultrapeer(host, getNewID(), config, Gnutella06Bootstrap
					.getInstance(), (short) 2345);
		} else {
			return new Leaf(host, getNewID(), config, Gnutella06Bootstrap
					.getInstance(), (short) 2345);
		}
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
	 * Returns whether the given host shall use an Ultrapeer overlay or not.
	 * 
	 * @param host
	 * @return
	 */
	public boolean shallBeUltrapeer(Host host) {

		// Bandwidth bw = host.getNetLayer().getMaxBandwidth();
		//
		// double downBandwidth = bw.getDownBW();
		// double upBandwidth = bw.getUpBW();
		//
		// Bandwidth mayBeUltrapeerBandwidth =
		// config.getMayBeUltrapeerBandwidth();
		// Bandwidth mustBeUltrapeerBandwidth = config
		// .getMustBeUltrapeerBandwidth();
		//
		// if (downBandwidth < mayBeUltrapeerBandwidth.getDownBW()
		// || upBandwidth < mayBeUltrapeerBandwidth.getUpBW()) {
		// return false;
		// }
		// if (downBandwidth > mustBeUltrapeerBandwidth.getDownBW()
		// || upBandwidth > mustBeUltrapeerBandwidth.getUpBW()) {
		// return true;
		// }

		return (Simulator.getRandom().nextInt(100) < config.getUltrapeerRatio());
	}

}
