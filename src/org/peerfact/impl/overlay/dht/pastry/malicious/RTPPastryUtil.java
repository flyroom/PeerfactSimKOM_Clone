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

import java.util.Collection;
import java.util.LinkedHashSet;

import org.peerfact.impl.overlay.dht.pastry.components.PastryContact;
import org.peerfact.impl.overlay.dht.pastry.components.PastryID;
import org.peerfact.impl.overlay.dht.pastry.components.PastryKey;


public class RTPPastryUtil {

	public static boolean isMalicious(PastryID pid) {
		return (RTPPastryNodeFactory.allMaliciousNodes.containsKey(pid));
	}

	public static RTPPastryNode getNearestMaliciousNode(PastryKey key) {
		RTPPastryNode nn = null;
		for (PastryID pid : RTPPastryNodeFactory.allMaliciousNodes.keySet()) {
			if (nn == null
					|| ((key.getUniqueValue().subtract(
							pid.getUniqueValue())).abs().compareTo((key
							.getUniqueValue().subtract(nn.getOverlayID()
							.getUniqueValue())).abs())) == -1) {
				nn = RTPPastryNodeFactory.allMaliciousNodes.get(pid);
			}
		}
		return nn;
	}

	public static Collection<PastryContact> getEclipseNeighbors() {
		LinkedHashSet<PastryContact> neighbors = new
				LinkedHashSet<PastryContact>();

		for (RTPPastryNode n : RTPPastryNodeFactory.allMaliciousNodes.values()) {
			neighbors.add(n.getOverlayContact());
		}

		return neighbors;
	}

	/*
	 * public static Collection<PastryContact> getEclipseNeighbors() {
	 * LinkedHashSet<PastryContact> neighbors = new
	 * LinkedHashSet<PastryContact>();
	 * 
	 * PastryNode[] an = RTPPastryNodeFactory.allMaliciousNodes.values()
	 * .toArray(new PastryNode[1]);
	 * 
	 * Collections.shuffle(Arrays.asList(an));
	 * 
	 * for (PastryNode n : an) { if (null == n) { System.out.println("ERROR"); }
	 * else { neighbors.add(n.getOverlayContact()); } }
	 * 
	 * return neighbors; }
	 */

}
