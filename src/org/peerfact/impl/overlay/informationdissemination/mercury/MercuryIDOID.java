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

package org.peerfact.impl.overlay.informationdissemination.mercury;

import java.math.BigInteger;

import org.peerfact.api.overlay.OverlayID;

/**
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * This part of the Simulator is not maintained in the current version of
 * PeerfactSim.KOM. There is no intention of the authors to fix this
 * circumstances, since the changes needed are huge compared to overall benefit.
 * 
 * If you want it to work correctly, you are free to make the specific changes
 * and provide it to the community.
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! !!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * 
 * This class provides an identifier for the mercury overlay. Every peer has an
 * identifier. So it is possible to associate a received information with a
 * peer.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 01/20/2011
 */
public class MercuryIDOID implements OverlayID<BigInteger> {

	/**
	 * An empty identifier
	 */
	public final static MercuryIDOID EMPTY_ID = new MercuryIDOID(
			new BigInteger("-1"));

	/**
	 * The identifier as integer
	 */
	private final BigInteger overlayID;

	/**
	 * Create a Mercury IDO id with the given id.
	 * 
	 * @param id
	 *            The identifier for this instance
	 */
	public MercuryIDOID(BigInteger id) {
		overlayID = id;
	}

	@Override
	public BigInteger getUniqueValue() {
		return overlayID;
	}

	@Override
	public byte[] getBytes() {
		/*
		 * Convert the integer to an byte array
		 */
		int convertID = overlayID.intValue();
		byte[] buffer = new byte[4];
		buffer[0] = (byte) (convertID >> 24);
		buffer[1] = (byte) ((convertID << 8) >> 24);
		buffer[2] = (byte) ((convertID << 16) >> 24);
		buffer[3] = (byte) ((convertID << 24) >> 24);

		return buffer;
	}

	@Override
	public String toString() {
		return overlayID.toString();

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + overlayID.intValue();
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
		MercuryIDOID other = (MercuryIDOID) obj;
		if (overlayID != other.overlayID) {
			return false;
		}
		return true;
	}

	@Override
	public int compareTo(OverlayID<BigInteger> id) {
		return overlayID.compareTo(((MercuryIDOID) id)
				.getUniqueValue());
	}

	@Override
	public long getTransmissionSize() {
		return getBytes().length;
	}

}
