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

package org.peerfact.impl.overlay.dht.chord.base.components;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.math.BigInteger;

import org.apache.log4j.Logger;
import org.peerfact.api.overlay.OverlayID;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.impl.util.logging.SimLogger;

/**
 * Each ChordNode has a unique ChordID value.
 * 
 * @author Minh Hoang Nguyen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */

public class ChordID implements OverlayID<BigInteger> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3519893806569768318L;

	private static Logger log = SimLogger.getLogger(ChordID.class);

	/**
	 * bit length of ChordId value
	 */
	public static final int KEY_BIT_LENGTH = 160;

	private static final BigInteger maxPossibleKeyValue = new BigInteger("2")
			.pow(KEY_BIT_LENGTH);

	private static final BigInteger halfDistance = new BigInteger("2")
			.pow(KEY_BIT_LENGTH - 1);

	private final BigInteger id;

	public int intId;

	public ChordID(BigInteger id) {
		this.id = id;
		this.intId = ChordIDFactory.getInstance().getIntForBigInt(id);
	}

	public ChordID(TransInfo localTransInfo) {
		ChordIDFactory.getInstance();
		this.id = ChordIDFactory.createNewID(localTransInfo);
		this.intId = ChordIDFactory.getInstance().getIntForBigInt(id);
	}

	@Override
	public byte[] getBytes() {

		byte[] buf = null;
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutput out = new ObjectOutputStream(bos);
			out.writeObject(this);
			out.close();

			buf = bos.toByteArray();
		} catch (IOException e) {
			log.error(e.getMessage());
		}
		return buf;
	}

	@Override
	public BigInteger getUniqueValue() {
		return this.id;
	}

	public BigInteger getValue() {
		return this.id;
	}

	public static BigInteger getMaxValue() {
		return maxPossibleKeyValue;
	}

	public BigInteger getDistance(ChordID identifier) {
		BigInteger oid = identifier.getValue();
		return this.id.subtract(oid).abs();
	}

	public BigInteger getMinDistance(ChordID identifier) {
		BigInteger a = getDistanceMod(identifier);
		BigInteger b = identifier.getDistanceMod(this);
		if (a.compareTo(b) == -1) {
			return a;
		}
		return b;
	}

	/**
	 * like getDistance but in one direction - meaning distance from 3 to 5 is 2
	 * but from 5 to 3 is MAX_VALUE - 2
	 * 
	 * @param identifier
	 * @return
	 */
	public BigInteger getDistanceMod(ChordID identifier) {
		BigInteger oid = identifier.getValue();

		if (oid.compareTo(this.id) > 0) {
			return oid.subtract(this.id);
		} else {
			BigInteger ret = maxPossibleKeyValue;
			return ret.subtract(this.id).add(oid);
		}

	}

	public boolean isPredecessorOf(ChordID identifier) {
		BigInteger otherSide = identifier.id.subtract(halfDistance);
		if (this.compareTo(identifier) == -1
				&& this.id.compareTo(otherSide) == 1) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isSuccessorOf(ChordID identifier) {
		BigInteger otherSide = identifier.id.subtract(halfDistance);
		if (this.compareTo(identifier) == 1) {
			return true;
		} else if (this.compareTo(identifier) == -1
				&& this.id.compareTo(otherSide) == -1) {
			return true;
		}
		return false;
	}

	public boolean equals(ChordID o) {
		return intId == o.intId;
		// return this.id.compareTo(o.getValue()) == 0;
	}

	/**
	 * @return whether this ChordID locates in the interval (a,b) on the Chord
	 *         ring
	 */
	public boolean between(ChordID a, ChordID b) {

		if (a.getValue().compareTo(maxPossibleKeyValue) > 0
				|| b.getValue().compareTo(maxPossibleKeyValue) > 0) {
			log.error("chord id has illegal value");
			return false;
		}

		// if a < b
		if (a.compareTo(b) < 0) {
			// if a < id < b
			if (this.compareTo(a) > 0 && this.compareTo(b) < 0) {
				return true;
			}
		} else { // if b < a
			// if a < id || id < b --> if ring wraps around between a and b
			if (this.compareTo(a) > 0 || this.compareTo(b) < 0) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int compareTo(OverlayID<BigInteger> arg0) {
		ChordID m = (ChordID) arg0;
		return this.id.compareTo(m.getValue());
	}

	@Override
	public String toString() {
		String idString = id.toString(16);
		while (idString.length() < 160 / 4) {
			idString = "0" + idString;
		}
		// return "[id = " + idString +" ]";
		return "[id = " + idString.substring(0, 5) + ".."
				+ idString.substring(idString.length() - 5) + "]";
	}

	public ChordKey getCorrespondingKey() {
		return new ChordKey(id);
	}

	@Override
	public final int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public final boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final ChordID other = (ChordID) obj;
		if (id == null) {
			if (other.getValue() != null) {
				return false;
			}
		} else if (!id.equals(other.getValue())) {
			return false;
		}
		return true;
	}

	@Override
	public long getTransmissionSize() {
		return KEY_BIT_LENGTH;
	}
}