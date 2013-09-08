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

package org.peerfact.impl.overlay.dht.kademlia.base.components;

import java.math.BigInteger;

import org.peerfact.api.overlay.OverlayID;
import org.peerfact.impl.overlay.dht.kademlia.base.TypesConfig;

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
 * 
 * A KademliaOverlayID consists of at most {@link #ID_LENGTH} bit and it is
 * always positive. Trying to construct a KademliaOverlayID outside these
 * boundaries will throw an IndexOutOfBoundsException.
 * 
 * @author Sebastian Kaune <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 08/10/07
 * 
 */
public class KademliaOverlayID implements OverlayID<BigInteger> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3037055438869769470L;

	private final BigInteger bigInt;

	/**
	 * Configuration values ("constants").
	 */
	protected final TypesConfig config;

	/**
	 * Constructs a KademliaOverlayID from the specified Integer.
	 * 
	 * @param id
	 *            the ID of this object
	 * @param conf
	 *            a TypesConfig reference that permits to retrieve configuration
	 *            "constants".
	 */
	public KademliaOverlayID(final Integer id, final TypesConfig conf) {
		this.config = conf;
		this.bigInt = new BigInteger(id.toString());
		this.checkBounds();
	}

	/**
	 * Constructs a KademliaOverlayID from the specified binary representation.
	 * 
	 * @param id
	 *            a String that contains the binary representation of the
	 *            KademliaOverlayID. Alphabet is {0, 1}.
	 * @param conf
	 *            a TypesConfig reference that permits to retrieve configuration
	 *            "constants".
	 */
	public KademliaOverlayID(final String id, final TypesConfig conf) {
		this.config = conf;
		this.bigInt = new BigInteger(id, 2);
		this.checkBounds();
	}

	/**
	 * Constructs a KademliaOverlayID from the specified byte array which is
	 * interpreted as a positive integer in big-endian order (most significant
	 * byte is in the zeroth element)
	 * 
	 * @param val
	 *            the specified byte array
	 * @param conf
	 *            a TypesConfig reference that permits to retrieve configuration
	 *            "constants".
	 */
	// public KademliaOverlayID(final byte[] val, final TypesConfig conf) {
	// this.config = conf;
	// this.bigInt = new BigInteger(1, val);
	// this.checkBounds();
	// }
	/**
	 * Constructs a KademliaOverlayID from a BigInteger.
	 * 
	 * @param id
	 *            the identifier of this KademliaOverlayID.
	 * @param conf
	 *            a TypesConfig reference that permits to retrieve configuration
	 *            "constants".
	 */
	public KademliaOverlayID(final BigInteger id, final TypesConfig conf) {
		this.config = conf;
		this.bigInt = id;
		this.checkBounds();
	}

	// /** Was never used locally
	// * Returns the binary representation of a BigInteger as string
	// *
	// * @return the binary representation of a BigInteger as string
	// */
	// private final String toBinary() {
	// return toBinary(this.bigInt);
	// }

	// /** Was never used locally
	// * Returns a binary string representation of the specified BigInteger with
	// * <code>ID_LENGTH</code> bits.
	// *
	// * @param id
	// * the specified BigInteger
	// * @return binary string representation of the specified BigInteger
	// */
	// private final String toBinary(final BigInteger id) {
	// String s = id.toString(2);
	// StringBuffer sb = new StringBuffer(config.getIDLength());
	// for (int i = 0; i < (config.getIDLength() - s.length()); i++) {
	// sb.append('0');
	// }
	// sb.append(s);
	// return sb.toString();
	// }

	/**
	 * Safeguard to help detect errors.
	 */
	private final void checkBounds() {
		if ((bigInt.signum() < 0)
				|| (bigInt.bitLength() > config.getIDLength())) {
			throw new IndexOutOfBoundsException("Value out of bounds: "
					+ bigInt.toString() + ", bitLength=" + bigInt.bitLength());
		}
	}

	/**
	 * Returns the ID (BigInteger)
	 * 
	 * @return the BigInteger ID
	 */
	public final BigInteger getBigInt() {
		return bigInt;
	}

	/**
	 * Returns the value of the KademliaOverlayID in a byte array of size
	 * <code>IDSIZE/8</code> in big-endian order (most significant byte is in
	 * the zeroth element).
	 * 
	 * @throws IndexOutOfBoundsException
	 *             if the identifier is to large to fit in the array
	 */
	@Override
	public final byte[] getBytes() {
		int len = config.getIDLength() / 8;
		byte[] a = bigInt.toByteArray();
		byte[] b = new byte[len];
		if (a.length == len + 1) {
			// strip sign bit which is in the first byte
			System.arraycopy(a, 1, b, len - a.length + 1, a.length - 1);
		} else if (a.length <= len) {
			System.arraycopy(a, 0, b, len - a.length, a.length);
		} else {
			throw new IndexOutOfBoundsException("length=" + (a.length - 1)
					+ ", required=" + len);
		}
		return b;
	}

	@Override
	public final int compareTo(final OverlayID<BigInteger> arg0) {
		return bigInt.compareTo(((KademliaOverlayID) arg0).bigInt);
	}

	// TODO: Check J. Bloch
	@Override
	public final boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		KademliaOverlayID that = (KademliaOverlayID) o;

		return !(bigInt != null ? !bigInt.equals(that.bigInt)
				: that.bigInt != null);

	}

	@Override
	public final int hashCode() {
		return (bigInt != null ? bigInt.hashCode() : 0);
	}

	@Override
	public final String toString() {
		// return "id={" + bigInt + "/" + toBinary() + "}";
		return bigInt.toString();
	}

	@Override
	public BigInteger getUniqueValue() {
		return bigInt;
	}

	/**
	 * @return a new KademliaOverlayKey that represents the same element of the
	 *         identifier space (that is, has the same bit vector / BigInteger)
	 *         as this KademliaOverlayID.
	 */
	public final KademliaOverlayKey toKey() {
		return new KademliaOverlayKey(bigInt, config);
	}

	@Override
	public long getTransmissionSize() {
		return getBytes().length;
	}

}
