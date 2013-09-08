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

package org.peerfact.impl.overlay.dht.pastry.components;

import java.io.Serializable;
import java.math.BigInteger;

import org.peerfact.api.overlay.OverlayKey;
import org.peerfact.api.overlay.dht.DHTKey;

/**
 * This class represents the overlay Key used by pastry.
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class PastryKey implements DHTKey<BigInteger>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2030708457808556092L;

	/**
	 * The value of the Key
	 */
	private BigInteger key;

	public PastryKey(BigInteger value) {
		key = value.mod(PastryID.NUM_OF_DISTINCT_IDS);
	}

	public PastryKey(PastryID id) {
		key = id.getUniqueValue();
	}

	@Override
	public int compareTo(OverlayKey<BigInteger> otherKey) {
		return key.compareTo(((PastryKey) otherKey).getUniqueValue());
	}

	@Override
	public BigInteger getUniqueValue() {
		return key;
	}

	@Override
	public byte[] getBytes() {
		// FIXME: What to do here?
		return null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
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
		PastryKey other = (PastryKey) obj;
		if (key == null) {
			if (other.key != null) {
				return false;
			}
		} else if (!key.equals(other.key)) {
			return false;
		}
		return true;
	}

	/**
	 * @return the pastry ID corresponding to this pastry key
	 */
	public PastryID getCorrespondingId() {
		return new PastryID(this);
	}

	@Override
	public long getTransmissionSize() {
		return (int) Math.ceil(PastryConstants.ID_BIT_LENGTH / 8d);
	}
}
