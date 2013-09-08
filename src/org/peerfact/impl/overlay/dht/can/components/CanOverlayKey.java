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

package org.peerfact.impl.overlay.dht.can.components;

import java.io.Serializable;
import java.math.BigInteger;

import org.peerfact.api.overlay.OverlayKey;
import org.peerfact.api.overlay.dht.DHTKey;
import org.peerfact.impl.util.toolkits.HashToolkit;

/**
 * Generates the hash values for the data files. So it is used for store and
 * lookup. Implements OverlayKey
 * 
 * @author Bjoern Dollak <peerfact@kom.tu-darmstadt.de>
 * @version Februar 2010
 * 
 */
public class CanOverlayKey implements DHTKey<BigInteger>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9027924631270280859L;

	private BigInteger id;

	/**
	 * Generates a hash from the String.
	 * 
	 * @param data
	 *            used to generate the hash.
	 */
	public CanOverlayKey(String data) {
		this.id = HashToolkit.getSHA1Hash(data);

	}

	/**
	 * sets a hash directly
	 * 
	 * @param data
	 *            long hash
	 */
	public CanOverlayKey(long data) {
		this.id = new BigInteger(String.valueOf(data));

	}

	/**
	 * sets a hash directly
	 * 
	 * @param data
	 *            BigIntger hash
	 */
	public CanOverlayKey(BigInteger data) {
		this.id = data;

	}

	public void setId(BigInteger id) {
		this.id = id;
	}

	public BigInteger getId() {
		return id;
	}

	@Override
	public String toString() {
		return id.toString();
	}

	/**
	 * Gives the x value of the hash. Therefore it takes the modular value of
	 * the first half of the hash and CanConfig.CanSize
	 * 
	 * @return
	 */
	public BigInteger getXValue() {
		BigInteger divider = new BigInteger("10").pow(
				id.toString().length() / 2).divide(
				new BigInteger(String.valueOf(CanConfig.CanSize)));
		if (id.mod(new BigInteger("2")).longValue() == 0) {
			return (new BigInteger(id.toString()
					.subSequence(0, id.toString().length() / 2).toString()))
					.divide(divider);
		}

		return (new BigInteger(id.toString()
				.subSequence(0, (id.toString().length() / 2) - 1).toString()))
				.mod(new BigInteger(String.valueOf(CanConfig.CanSize)));
	}

	/**
	 * Gives the x value of the hash. Therefore it takes the modular value of
	 * the first half of the hash and CanConfig.CanSize
	 * 
	 * @return
	 */
	public BigInteger getYValue() {
		return (new BigInteger(
				id.toString()
						.subSequence(id.toString().length() / 2,
								id.toString().length()).toString()))
				.mod(new BigInteger(String.valueOf(CanConfig.CanSize)));
	}

	public BigInteger[] getValue() {
		BigInteger[] output = { getXValue(), getYValue() };
		return output;
	}

	public int[] getIntValue() {
		int[] output = { getXValue().intValue(), getYValue().intValue() };
		return output;
	}

	/**
	 * Checks if the x value of the hash is in the same region as the x corner
	 * of the area
	 * 
	 * @param area
	 * @return
	 */
	public boolean sameXValue(CanArea area) {
		if (getXValue().intValue() >= area.getArea()[0]
				&& getXValue().intValue() <= area.getArea()[1]) {
			return true;
		}
		return false;
	}

	/**
	 * Checks if the y value of the hash is in the same region as the y corner
	 * of the area
	 * 
	 * @param area
	 * @return
	 */
	public boolean sameYValue(CanArea area) {
		if (getYValue().intValue() >= area.getArea()[2]
				&& getYValue().intValue() <= area.getArea()[3]) {
			return true;
		}
		return false;
	}

	/**
	 * Checks if the x value of the hash should be in this area
	 * 
	 * @param area
	 * @return true if it should be in the area
	 */
	public boolean includedInArea(CanArea area) {
		if (sameXValue(area) && sameYValue(area)) {
			return true;
		}

		return false;
	}

	@Override
	public byte[] getBytes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BigInteger getUniqueValue() {
		return id;
	}

	@Override
	public int compareTo(OverlayKey<BigInteger> arg0) {
		return id.compareTo(((CanOverlayKey) arg0).getUniqueValue());
	}

	@Override
	public long getTransmissionSize() {
		return 20;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		CanOverlayKey other = (CanOverlayKey) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}
}
