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

package org.peerfact.impl.overlay.dht.pastry.proximity;

import org.peerfact.impl.overlay.dht.pastry.components.PastryContact;

/**
 * This interface is meant to allow the definition of different proximity
 * metrics.
 * 
 * Assumption: The defined proximity metric is assumed to be Euclidiean. This
 * means the triangulation inequality holds for distances among nodes.
 * 
 * @author Julius Rückert <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public interface ProximityMetricProvider {

	/**
	 * Calculates the distance between two peers according to a proximity
	 * metric. The absolute value is not important. It is only important that
	 * higher distances are represented by higher value than lower distances.
	 * Peers that have a lower distance to another node are preferred when there
	 * is a choice between alternatives for e.g. routing.
	 * 
	 * @param c1
	 * @param c2
	 * @return
	 */
	public int calculateDistance(PastryContact c1, PastryContact c2);

}
