/*
 * Copyright (c) 2012-2013 Open Source Community - <http://www.peerfact.org>
 * Copyright (c) 2011-2012 University of Paderborn - UPB
 * Copyright (c) 2005-2011 KOM - Multimedia Communications Lab
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.peerfact.impl.network.modular.st.positioning;

import java.awt.Point;

import org.peerfact.api.common.Host;
import org.peerfact.api.network.NetPosition;
import org.peerfact.impl.network.modular.db.NetMeasurementDB;
import org.peerfact.impl.network.modular.st.PositioningStrategy;
import org.peerfact.impl.util.positioning.PositionDistribution;
import org.peerfact.impl.util.positioning.PositionVector;


/**
 * Positioning on a 2D-Plane, used for all Movement-Enabled Routing-Scenarios
 * 
 * @author Bjoern Richerzhagen
 * @version 1.0, 04/25/2011
 */
public class PlanePositioning implements PositioningStrategy {

	private PositionDistribution distribution;

	@Override
	public void writeBackToXML(BackWriter bw) {
		// nothing to configure here
	}

	@Override
	public NetPosition getPosition(
			Host host,
			NetMeasurementDB db,
			org.peerfact.impl.network.modular.db.NetMeasurementDB.Host hostMeta) {
		if (distribution == null) {
			throw new AssertionError(
					"You have to specify an IPositionDistribution when using DeviceDependentPositioning!");
		}
		PositionVector pos = distribution.getNextPosition();
		return new PlanePosition(pos);
	}

	/**
	 * Specify a Distribution for Positions of Hosts
	 * 
	 * @param distribution
	 */
	public void setPositionDistribution(PositionDistribution distribution) {
		this.distribution = distribution;
		this.distribution.setDimensions(2);
	}

	/**
	 * Position on a 2D-Plane
	 * 
	 * @author Bjoern Richerzhagen
	 * @version 1.0, 04/25/2011
	 */
	public static class PlanePosition extends PositionVector implements
			NetPosition {

		public PlanePosition(PositionVector vec) {
			super(vec);
		}

		public PlanePosition(double x, double y) {
			super(2);
			setEntry(0, x);
			setEntry(1, y);
		}

		@Override
		public double getDistance(NetPosition netPosition) {
			if (netPosition instanceof PositionVector) {
				PositionVector pVec = (PositionVector) netPosition;
				return calculateDistance(pVec);
			} else {
				throw new AssertionError("Cannot calculate Distance!");
			}
		}

		public Point getPoint() {
			return new Point((int) getEntry(0), (int) getEntry(1));
		}

	}

}
