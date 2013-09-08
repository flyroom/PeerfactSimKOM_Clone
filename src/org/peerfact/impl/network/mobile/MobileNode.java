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

import org.peerfact.api.network.NetPosition;
import org.peerfact.impl.network.mobile.movementmodel.MovementInstance;

/**
 * Implementation of the <code>NetPosition</code> interface representing a two
 * dimensional point in euclidian space.
 * 
 * @author Carsten Snider <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 12/01/2008
 * 
 */
public class MobileNode extends Position implements NetPosition {

	private MovementInstance MM;

	// Parameters for the movement of a network node

	public Object getMovementModel() {
		return MM;
	}

	public void setMovementModel(MovementInstance MovementModel) {
		MM = MovementModel;
	}

	public void updateMovement()
	{
		MM.updateMovement(this);
	}

	/**
	 * Constructs a two dimensional euclidian point
	 * 
	 * @param xPos
	 * @param yPos
	 */
	public MobileNode(double xPos, double yPos) {
		super.setXPos(xPos);
		super.setYPos(yPos);
	}

	/**
	 * Constructs a two dimensional euclidian point in the middle of the X/Y
	 * coordination system
	 */
	@Override
	public double getDistance(NetPosition ep) {

		double xDiff = 0;
		double yDiff = 0;
		MobileNode point = (MobileNode) ep;
		if (Math.abs(super.getXPos() - point.getXPos()) > MobileSubnet.SUBNET_WIDTH / 2) {
			if (super.getXPos() < point.getXPos()) {
				xDiff = super.getXPos() + MobileSubnet.SUBNET_WIDTH
						- point.getXPos();
			} else {
				xDiff = point.getXPos() + MobileSubnet.SUBNET_WIDTH - getXPos();
			}
		} else {
			xDiff = Math.abs(super.getXPos() - point.getXPos());
		}
		if (Math.abs(this.getYPos() - point.getYPos()) > MobileSubnet.SUBNET_HEIGHT / 2) {
			if (this.getYPos() < point.getYPos()) {
				yDiff = getYPos() + MobileSubnet.SUBNET_HEIGHT
						- point.getYPos();
			} else {
				yDiff = point.getYPos() + MobileSubnet.SUBNET_HEIGHT
						- getYPos();
			}
		} else {
			yDiff = Math.abs(getYPos() - point.getYPos());
		}

		return Math.pow(Math.pow(xDiff, 2) + Math.pow(yDiff, 2), 0.5);

	}

	@Override
	public int hashCode() {
		int result = 17;
		long yHash = Double.doubleToLongBits(getYPos());
		long xHash = Double.doubleToLongBits(getXPos());
		result *= 37 + (int) (yHash ^ (yHash >>> 32));
		result *= 37 + (int) (xHash ^ (xHash >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof MobileNode)) {
			return false;
		}
		MobileNode point2 = (MobileNode) o;
		return point2.getXPos() == this.getXPos()
				&& point2.getYPos() == this.getYPos();
	}

}
