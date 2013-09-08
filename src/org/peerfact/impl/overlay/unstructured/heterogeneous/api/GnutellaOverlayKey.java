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

package org.peerfact.impl.overlay.unstructured.heterogeneous.api;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import org.peerfact.api.overlay.OverlayKey;
import org.peerfact.api.overlay.dht.DHTKey;

/**
 * As Gnutella uses Integers as "rank", this class is needed to support
 * DHTKey-Generation and operations like equals on this key
 * 
 * @author Bjoern Richerzhagen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class GnutellaOverlayKey<T> implements DHTKey<T> {

	private T identifier;

	/**
	 * Create a key for given Rank
	 * 
	 * @param rank
	 */
	public GnutellaOverlayKey(T identifier) {
		this.identifier = identifier;
	}

	public T getIdentifier() {
		return identifier;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((identifier == null) ? 0 : identifier.hashCode());
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
		GnutellaOverlayKey<?> other = (GnutellaOverlayKey<?>) obj;
		if (identifier == null) {
			if (other.identifier != null) {
				return false;
			}
		} else if (!identifier.equals(other.identifier)) {
			return false;
		}
		return true;
	}

	@Override
	public long getTransmissionSize() {
		return 4;
	}

	@Override
	public int compareTo(OverlayKey<T> arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public T getUniqueValue() {
		return identifier;
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
			throw new RuntimeException(e);
		}
		return buf;
	}

}
