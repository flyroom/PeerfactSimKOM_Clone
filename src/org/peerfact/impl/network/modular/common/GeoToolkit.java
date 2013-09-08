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

package org.peerfact.impl.network.modular.common;

import org.peerfact.impl.network.gnp.HaversineHelpers;

/**
 * 
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public class GeoToolkit {

	/**
	 * Returns the distance between two points in meters.
	 * 
	 * @param latitudeA
	 * @param longitudeA
	 * @param latitudeB
	 * @param longitudeB
	 * @return the distance between two points in meters.
	 */
	public static double getDistance(double latitudeA, double longitudeA,
			double latitudeB, double longitudeB) {
		double lat1 = HaversineHelpers.radians(latitudeA);
		double lat2 = HaversineHelpers.radians(latitudeB);
		double dlat = lat2 - lat1;
		double dlong = HaversineHelpers.radians(longitudeB)
				- HaversineHelpers.radians(longitudeA);

		double a = HaversineHelpers.square(Math.sin(dlat / 2)) + Math.cos(lat1)
				* Math.cos(lat2) * HaversineHelpers.square(Math.sin(dlong / 2));

		// angle in radians formed by start point, earth's center, & end point
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		// radius of earth at midpoint of route
		double r = HaversineHelpers.globeRadiusOfCurvature((lat1 + lat2) / 2);
		return r * c;
	}

}
