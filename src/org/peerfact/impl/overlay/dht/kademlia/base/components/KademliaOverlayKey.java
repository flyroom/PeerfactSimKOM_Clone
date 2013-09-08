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

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Random;

import org.peerfact.api.overlay.OverlayKey;
import org.peerfact.api.overlay.dht.DHTKey;
import org.peerfact.impl.overlay.dht.kademlia.base.TypesConfig;
import org.peerfact.impl.simengine.Simulator;

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
 * In Kademlia, participating OverlayNodes are assigned uniform random
 * <code>KademliaOverlayID</code>s from a large identifier space that consists
 * of at most {@link #ID_LENGTH} bit. Application-specific objects (documents
 * etc.) are assigned unique identifiers called <code>KademliaOverlayKey</code>
 * s, selected from the same identifier space.
 * 
 * @author Sebastian Kaune <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 08/10/07
 * 
 */
public final class KademliaOverlayKey implements DHTKey<BigInteger>,
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8869482328143402957L;

	private final BigInteger bigInt;

	/**
	 * A seed to produce random Kademlia overlay keys from ranks.
	 */
	private static final long seed = Simulator.getRandom().nextLong();

	/**
	 * Configuration values ("constants").
	 */
	private final TypesConfig config;

	/**
	 * Constructs a KademliaOverlayKey from its binary representation.
	 * 
	 * @param value
	 *            a String that contains the binary representation of the
	 *            KademliaOverlayKey. Alphabet is {0, 1}.
	 * @param conf
	 *            a TypesConfig reference that permits to retrieve configuration
	 *            "constants".
	 */
	public KademliaOverlayKey(final String value, final TypesConfig conf) {
		this.config = conf;
		this.bigInt = new BigInteger(value, 2);
		this.checkBounds();
	}

	/**
	 * Constructs a KademliaOverlayKey from the specified BigInteger.
	 * 
	 * @param value
	 *            the specified BigInteger
	 * @param conf
	 *            a TypesConfig reference that permits to retrieve configuration
	 *            "constants".
	 */
	public KademliaOverlayKey(final BigInteger value, final TypesConfig conf) {
		this.config = conf;
		this.bigInt = value;
		this.checkBounds();
	}

	/**
	 * Creates a random Kademlia overlay key from the specified rank. This means
	 * that if you create two keys with the same rank value, this method will
	 * produce the same key. But this ensures each key is equally distributed
	 * through the key space. Needed for application layers that work with ranks
	 * instead of keys.
	 * 
	 * Added by Leo Nobach
	 * 
	 * @param rank
	 * @param conf
	 */
	public static KademliaOverlayKey fromRank(final int rank,
			final TypesConfig conf) {
		TypesConfig newConfig = conf;
		BigInteger newBigInt = new BigInteger(conf.getIDLength(), new Random(
				seed ^ rank));
		return new KademliaOverlayKey(newBigInt, newConfig);
	}

	/**
	 * Constructs a KademliaOverlayKey from the specified integer.
	 * 
	 * @param value
	 *            the specified integer
	 * @param conf
	 *            a TypesConfig reference that permits to retrieve configuration
	 *            "constants".
	 */
	public KademliaOverlayKey(final int value, final TypesConfig conf) {
		this.config = conf;
		this.bigInt = BigInteger.valueOf(value);
		this.checkBounds();
	}

	/**
	 * Returns the value of the KademliaOverlayKey in a byte array of size
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

	@Override
	public final String toString() {
		// return "key=" + this.bigInt;
		return bigInt.toString();
	}

	@Override
	public final int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((bigInt == null) ? 0 : bigInt.hashCode());
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
		final KademliaOverlayKey other = (KademliaOverlayKey) obj;
		if (bigInt == null) {
			if (other.bigInt != null) {
				return false;
			}
		} else if (!bigInt.equals(other.bigInt)) {
			return false;
		}
		return true;
	}

	@Override
	public final int compareTo(final OverlayKey<BigInteger> arg0) {
		return bigInt.compareTo(((KademliaOverlayKey) arg0).bigInt);
	}

	@Override
	public final BigInteger getUniqueValue() {
		return this.bigInt;
	}

	public final BigInteger getBigInt() {
		return this.bigInt;
	}

	@Override
	public long getTransmissionSize() {
		return getBytes().length;
	}

}
