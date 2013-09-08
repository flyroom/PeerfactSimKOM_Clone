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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.math.BigInteger;

import org.apache.log4j.Logger;
import org.peerfact.api.overlay.OverlayID;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.util.logging.SimLogger;
import org.peerfact.impl.util.toolkits.HashToolkit;

/**
 * This class represents the overlay ID used by pastry.
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class PastryID implements OverlayID<BigInteger> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8945204214066915121L;

	private static final BigInteger TWO = new BigInteger("2");

	private static Logger log = SimLogger.getLogger(PastryID.class);

	/**
	 * The number of possible, distinct ID values
	 */
	public static final BigInteger NUM_OF_DISTINCT_IDS = TWO
			.pow(PastryConstants.ID_BIT_LENGTH);

	/**
	 * The maximum possible ID value
	 */
	public static final BigInteger MAX_ID_VALUE = NUM_OF_DISTINCT_IDS
			.subtract(BigInteger.ONE);

	/**
	 * The number of digits the ID has, assuming one digit has b bits
	 */
	public static final int NUM_OF_DIGITS = PastryConstants.ID_BIT_LENGTH
			/ PastryConstants.ID_BASE_BIT_LENGTH;

	/**
	 * The value of the ID
	 */
	private BigInteger id;

	/**
	 * @param localTransInfo
	 *            the transport layer information the pastry ID is generated
	 *            from
	 */
	public PastryID(TransInfo localTransInfo) {

		/*
		 * The paper proposes to hash the IP address as one possibility to
		 * generate an Id.
		 */
		id = HashToolkit.getSHA1Hash(localTransInfo.getNetId()
				.toString(), PastryConstants.ID_BIT_LENGTH);
	}

	public PastryID(BigInteger value) {
		id = value.mod(NUM_OF_DISTINCT_IDS);
	}

	public PastryID(PastryKey key) {
		id = key.getUniqueValue();
	}

	@Override
	public int compareTo(OverlayID<BigInteger> otherID) {
		return id.compareTo(((PastryID) otherID).getUniqueValue());
	}

	@Override
	public BigInteger getUniqueValue() {
		return id;
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

	/**
	 * @return the pastry key corresponding to this pastry ID
	 */
	public PastryKey getCorrespondingKey() {
		return new PastryKey(this);
	}

	@Override
	public long getTransmissionSize() {
		return (long) Math.ceil(PastryConstants.ID_BIT_LENGTH / 8d);
	}

	/**
	 * Returns the ith digit of the ID (in base 2^b). i=0 returns the least
	 * significant digit.
	 * 
	 * @param i
	 * @return the ith digit of the ID (in base 2^b)
	 */
	public int getDigit(int i) {
		BigInteger rId;

		// Shift the ID by i*b bits
		int shift = i * PastryConstants.ID_BASE_BIT_LENGTH;
		rId = id.shiftRight(shift);

		// Remove the leading digits by doing a mod 2^b
		rId = rId.mod(TWO.pow(PastryConstants.ID_BASE_BIT_LENGTH));

		// Check whether we can return the result as an integer or not
		if (rId.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) <= 0) {
			return rId.intValue();
		} else {
			return -1;
		}
	}

	/**
	 * Returns the index of the most significant different digit (MSDD) in a
	 * given base.
	 * 
	 * @param otherId
	 *            another node id to compare with.
	 * @return the index of the MSDD (0 is the least significant) / will return
	 *         negative if they do not differ.
	 */
	public int indexOfMSDD(PastryID otherId) {
		// Compare the digits, starting with the most significant bit
		for (int i = NUM_OF_DIGITS - 1; i >= 0; i--) {
			if (getDigit(i) != otherId.getDigit(i)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Returns the minimum absolute distance to the given Id (it may be computed
	 * clockwise or counter clockwise).
	 * 
	 * @param otherId
	 *            the Id to compute the distance to
	 * @return the absolute distance
	 */
	public BigInteger getMinAbsDistance(PastryID otherId) {
		if (otherId == null) {
			return null;
		}

		return getCwDistance(otherId).min(getCcwDistance(otherId));
	}

	public BigInteger getCwDistance(PastryID otherId) {
		if (otherId == null) {
			return null;
		}

		BigInteger oId = otherId.getUniqueValue();
		BigInteger d;

		if (id.compareTo(oId) <= 0) {
			// oId -ID
			d = oId.subtract(id);
		} else {
			// 2^ID_LENGTH - ID + oId
			d = NUM_OF_DISTINCT_IDS.subtract(id).add(oId);
		}

		return d.abs();
	}

	public BigInteger getCcwDistance(PastryID otherId) {
		if (otherId == null) {
			return null;
		}

		BigInteger oId = otherId.getUniqueValue();
		BigInteger d;

		if (id.compareTo(oId) >= 0) {
			// ID -oId
			d = id.subtract(oId);
		} else {
			// ID + 2^ID_LENGTH - oId
			d = id.add(NUM_OF_DISTINCT_IDS).subtract(oId);
		}

		return d.abs();
	}

	/**
	 * Tells whether the ID lies between the two given IDs. Be aware of the fact
	 * that the ID space is cyclic. This means the order of the two given IDs is
	 * crucial.
	 * 
	 * @param id1
	 * @param id2
	 * @return true if this id lies between the two given ids, false otherwise
	 */
	public boolean isBetween(PastryID id1, PastryID id2) {
		/*
		 * The ID lies between id1 and id2 iff one of the following statements
		 * is true:
		 * 
		 * 1) (id1 < id2) && (id1 < ID < id2)
		 * 
		 * 2) (id1 > id2) && (ID < id1 || ID > id2)
		 */
		if ((id1.compareTo(id2) < 0 && id1.compareTo(this) < 0 && this
				.compareTo(id2) < 0)
				|| (id2.compareTo(id1) < 0 && (this.compareTo(id1) < 0 || id2
						.compareTo(this) < 0))) {
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		String idString = id.toString(TWO.pow(
				PastryConstants.ID_BASE_BIT_LENGTH).intValue());
		while (idString.length() < PastryConstants.ID_BIT_LENGTH
				/ PastryConstants.ID_BASE_BIT_LENGTH) {
			idString = "0" + idString;
		}
		if (idString.length() > 6) {
			return idString.substring(0, 5) + ".."
					+ idString.substring(idString.length() - 5);
		} else {
			return idString;
		}
	}

	// Implementation of equals and hashCode

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if ((obj == null) || (obj.getClass() != this.getClass())) {
			return false;
		}

		PastryID oId = (PastryID) obj;
		return id.equals(oId.getUniqueValue());
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 31 * hash + (null == id ? 0 : id.hashCode());
		return hash;
	}

	public static PastryID createRandomId() {
		BigInteger id = HashToolkit.getSHA1Hash(
				Integer.valueOf(Simulator
						.getRandom().nextInt()).toString(),
				PastryConstants.ID_BIT_LENGTH);
		return new PastryID(id);
	}
}
