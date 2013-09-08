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
public class Position {

	private double xPos;

	public void setXPos(double pos) {
		xPos = pos;
	}

	public void setYPos(double pos) {
		yPos = pos;
	}

	private double yPos;

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

}
