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

package org.peerfact.impl.overlay.dht.pastry.nodestate;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

import org.peerfact.impl.overlay.dht.pastry.components.PastryContact;
import org.peerfact.impl.overlay.dht.pastry.components.PastryID;


/**
 * This class defines some static methods, used by several other node state
 * classes.
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class StateHelpers {

	/**
	 * @param id
	 *            the ID that the distance is computed to
	 * @param toCheck
	 *            the set of peer contacts to compute the closest contact in
	 * @return null if the set was empty, the closest peer's contact otherwise
	 */
	public static PastryContact getClosestContact(PastryID id,
			Iterable<PastryContact> toCheck) {
		return getClosestContact(id, new LinkedList<PastryContact>(), toCheck);
	}

	/**
	 * @param id
	 *            the ID that the distance is computed to
	 * @param toExclude
	 *            a set of peer contacts that should not be considered
	 * @param toCheck
	 *            the set of peer contacts to compute the closest contact in
	 * @return null if the set was empty or all contacts were excluded, the
	 *         closest peer's contact otherwise
	 */
	public static PastryContact getClosestContact(PastryID id,
			List<PastryContact> toExclude, Iterable<PastryContact> toCheck) {
		if (toCheck == null) {
			return null;
		}
		BigInteger minDistance = null;
		PastryContact minContact = null;

		for (PastryContact c : toCheck) {
			if (c == null || toExclude.contains(c)) {
				continue;
			}

			PastryID nId = c.getOverlayID();
			BigInteger d = id.getMinAbsDistance(nId);

			if (minDistance == null || d.compareTo(minDistance) < 0) {
				minDistance = d;
				minContact = c;
			}
		}
		return minContact;
	}

}
