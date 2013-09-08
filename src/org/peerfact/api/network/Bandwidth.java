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

package org.peerfact.api.network;

/**
 * 
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public class Bandwidth implements Cloneable {

	private double downBW;

	private double upBW;

	public Bandwidth(double downBW, double upBW) {
		super();
		this.downBW = downBW;
		this.upBW = upBW;
	}

	/**
	 * The downstream bandwidth in byte/s
	 * 
	 * @return
	 */
	public double getDownBW() {
		return downBW;
	}

	/**
	 * The upstream bandwidth in byte/s
	 * 
	 * @return
	 */
	public double getUpBW() {
		return upBW;
	}

	/**
	 * Sets the downstream bandwidth in byte/s
	 * 
	 * @param downBW
	 */
	public void setDownBW(double downBW) {
		this.downBW = downBW;
	}

	/**
	 * Sets the upstream bandwidth in byte/s
	 * 
	 * @param upBW
	 */
	public void setUpBW(double upBW) {
		this.upBW = upBW;
	}

	@Override
	public String toString() {
		return "(Down: " + downBW + " byte/s, Up: " + upBW + " byte/s)";
	}

	@Override
	public Bandwidth clone() {
		return new Bandwidth(downBW, upBW);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(downBW);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(upBW);
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
		Bandwidth other = (Bandwidth) obj;
		if (Double.doubleToLongBits(downBW) != Double
				.doubleToLongBits(other.downBW)) {
			return false;
		}
		if (Double.doubleToLongBits(upBW) != Double
				.doubleToLongBits(other.upBW)) {
			return false;
		}
		return true;
	}

}
