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

package org.peerfact.impl.network.mobile;

/**
 * 
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public class Neighbor extends Position {

	private double distanceToReceiver;

	private int delay;

	public Neighbor(double x, double y, double sqrt) {
		super();
		super.setXPos(x);
		super.setYPos(y);
		distanceToReceiver = sqrt;
	}

	public double getDistanceToReceiver() {
		return distanceToReceiver;
	}

	public void setSteps(int steps) {
		this.delay = steps;

	}

	public int getSteps() {
		return delay;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + delay;
		long temp;
		temp = Double.doubleToLongBits(distanceToReceiver);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Neighbor other = (Neighbor) obj;
		if (delay != other.delay) {
			return false;
		}
		if (Double.doubleToLongBits(distanceToReceiver) != Double
				.doubleToLongBits(other.distanceToReceiver)) {
			return false;
		}
		return true;
	}

}
