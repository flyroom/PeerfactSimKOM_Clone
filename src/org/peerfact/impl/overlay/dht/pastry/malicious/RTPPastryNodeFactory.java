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

package org.peerfact.impl.overlay.dht.pastry.malicious;

import java.util.HashMap;

import org.peerfact.api.common.Component;
import org.peerfact.api.common.Host;
import org.peerfact.impl.overlay.dht.pastry.components.PastryID;
import org.peerfact.impl.simengine.Simulator;


public class RTPPastryNodeFactory extends
		org.peerfact.impl.overlay.dht.pastry.components.PastryNodeFactory {

	private double ratioOfMaliciousNodes = 0.2; // default, but is set in xml

	private String rtpmethod = "default";

	public static HashMap<PastryID, RTPPastryNode> allMaliciousNodes = new HashMap<PastryID, RTPPastryNode>();

	// private static boolean first = true;

	@Override
	public Component createComponent(Host host) {

		if (Simulator.getRandom().nextDouble() < ratioOfMaliciousNodes) {
			// first = false;
			RTPPastryNode node = new RTPPastryNode(host.getTransLayer(),
					this.getRTPMethod());
			allMaliciousNodes.put(node.getOverlayID(), node);

			return node;

		} else {
			return super.createComponent(host);
		}
	}

	public double getRatio() {
		return ratioOfMaliciousNodes;
	}

	public void setRatio(double ratioOfMaliciousNodes) {
		this.ratioOfMaliciousNodes = ratioOfMaliciousNodes;
	}

	public void setRTPMethod(String method) {
		this.rtpmethod = method;
	}

	public String getRTPMethod() {
		return this.rtpmethod;
	}

}
