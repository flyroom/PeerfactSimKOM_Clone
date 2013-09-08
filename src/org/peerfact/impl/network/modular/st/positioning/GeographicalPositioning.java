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

package org.peerfact.impl.network.modular.st.positioning;

import org.peerfact.api.common.Host;
import org.peerfact.api.network.NetPosition;
import org.peerfact.impl.network.modular.common.GeoToolkit;
import org.peerfact.impl.network.modular.db.NetMeasurementDB;
import org.peerfact.impl.network.modular.st.PositioningStrategy;

/**
 * Applies a geographical position as defined by the GeoIP project.
 * 
 * Based on code from the GeoLocationOracle (unknown author, Gerald Klunker?) in
 * the GNP net layer
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class GeographicalPositioning implements PositioningStrategy {

	@Override
	public GeographicalPosition getPosition(
			Host host,
			NetMeasurementDB db,
			org.peerfact.impl.network.modular.db.NetMeasurementDB.Host hostMeta) {

		if (hostMeta == null) {
			throw new IllegalArgumentException(
					"The geographical positioner can not access the required measurement database, which is not set.");
		}

		return new GeographicalPosition(hostMeta);

	}

	static class GeographicalPosition implements NetPosition {

		private double latitude;

		private double longitude;

		public GeographicalPosition(NetMeasurementDB.Host hostMeta) {
			latitude = hostMeta.getLatitude();
			longitude = hostMeta.getLongitude();
		}

		/**
		 * Calculates the distance in meters (m) from one host to another, using
		 * the Haversine formula. The squashed shape of the earth into account
		 * (approximately)
		 * 
		 */
		@Override
		public double getDistance(NetPosition netPosition) {
			if (!(netPosition instanceof GeographicalPosition)) {
				throw new AssertionError(
						"Can not calculate the distance between two different position classes: "
								+ this.getClass() + " and "
								+ netPosition.getClass());
			}
			GeographicalPosition other = (GeographicalPosition) netPosition;

			return GeoToolkit.getDistance(this.latitude, this.longitude,
					other.latitude, other.longitude);
		}

	}

	@Override
	public void writeBackToXML(BackWriter bw) {
		// No simple/complex types to write back
	}

}
