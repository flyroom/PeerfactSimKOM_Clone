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

package org.peerfact.impl.overlay.dht.centralizedindex.components;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.peerfact.api.network.NetID;
import org.peerfact.api.overlay.OverlayKey;
import org.peerfact.api.overlay.dht.DHTKey;

/**
 * Implementing a centralized DHT overlay, whose organization of the centralized
 * index is similar to the distributed index of Chord
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 08.12.2008
 */
public class CIOverlayKey implements DHTKey<BigInteger> {

	private BigInteger id;

	public CIOverlayKey(NetID id) {
		this.id = getID(id.toString());
	}

	public CIOverlayKey(BigInteger id) {
		this.id = id;
	}

	public BigInteger getID() {
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
			System.err.println("IOException!");
		}
		return buf;
	}

	@Override
	public BigInteger getUniqueValue() {
		// not needed
		return id;
	}

	@Override
	public int compareTo(OverlayKey<BigInteger> arg0) {
		if (arg0 == null) {
			return 1;
		} else {
			BigInteger fid = ((CIOverlayKey) arg0).getID();
			return id.compareTo(fid);
		}
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
		CIOverlayKey other = (CIOverlayKey) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

	private static BigInteger getID(String s) {
		MessageDigest md;
		byte[] sha1hash = new byte[20];
		try {
			md = MessageDigest.getInstance("SHA-1");
			md.update(s.getBytes("iso-8859-1"), 0, s.length());
			sha1hash = md.digest();
		} catch (NoSuchAlgorithmException e) {
			System.err.println("NoSuchAlgorithmException");
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			System.err.println("UnsupportedEncodingException");
			e.printStackTrace();
		}
		return new BigInteger(1, sha1hash);
	}

	@Override
	public long getTransmissionSize() {
		return getBytes().length;
	}

}
