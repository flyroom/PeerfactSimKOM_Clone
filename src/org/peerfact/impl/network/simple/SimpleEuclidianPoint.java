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

package org.peerfact.impl.network.simple;

import org.peerfact.api.network.NetPosition;

/**
 * Implementation of the <code>NetPosition</code> interface representing a two
 * dimensional point in euclidian space.
 * 
 * @author Sebastian Kaune <peerfact@kom.tu-darmstadt.de>
 * @author Konstantin Pussep
 * @version 3.0, 11/29/2007
 * 
 */
public class SimpleEuclidianPoint implements NetPosition {

	private double xPos;

	private double yPos;

	/**
	 * Constructs a two dimensional euclidian point
	 * 
	 * @param xPos
	 * @param yPos
	 */
	public SimpleEuclidianPoint(double xPos, double yPos) {
		this.xPos = xPos;
		this.yPos = yPos;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.peerfact.api.network.NetPosition#getDistance(org.peerfact
	 * .api.network.NetPosition)
	 */
	@Override
	public double getDistance(NetPosition ep) {
		double xDiff = 0;
		double yDiff = 0;
		SimpleEuclidianPoint point = (SimpleEuclidianPoint) ep;
		if (Math.abs(xPos - point.getXPos()) > SimpleSubnet.SUBNET_WIDTH / 2) {
			if (xPos < point.getXPos()) {
				xDiff = xPos + SimpleSubnet.SUBNET_WIDTH - point.getXPos();
			} else {
				xDiff = point.getXPos() + SimpleSubnet.SUBNET_WIDTH - xPos;
			}
		} else {
			xDiff = Math.abs(xPos - point.getXPos());
		}
		if (Math.abs(this.yPos - point.getYPos()) > SimpleSubnet.SUBNET_HEIGHT / 2) {
			if (this.yPos < point.getYPos()) {
				yDiff = yPos + SimpleSubnet.SUBNET_HEIGHT - point.getYPos();
			} else {
				yDiff = point.getYPos() + SimpleSubnet.SUBNET_HEIGHT - yPos;
			}
		} else {
			yDiff = Math.abs(yPos - point.getYPos());
		}
		return Math.pow(Math.pow(xDiff, 2) + Math.pow(yDiff, 2), 0.5);
	}

	/**
	 * Get the position of the point in the X axis.
	 * 
	 * @return the X position.
	 */
	public double getXPos() {
		return this.xPos;
	}

	/**
	 * Get the position of the point in the Y axis.
	 * 
	 * @return the Y position.
	 */
	public double getYPos() {
		return this.yPos;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof SimpleEuclidianPoint)) {
			return false;
		}
		SimpleEuclidianPoint point2 = (SimpleEuclidianPoint) o;
		return point2.xPos == this.xPos && point2.yPos == this.yPos;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int result = 17;
		long yHash = Double.doubleToLongBits(yPos);
		long xHash = Double.doubleToLongBits(xPos);
		result *= 37 + (int) (yHash ^ (yHash >>> 32));
		result *= 37 + (int) (xHash ^ (xHash >>> 32));
		return result;
	}
}
