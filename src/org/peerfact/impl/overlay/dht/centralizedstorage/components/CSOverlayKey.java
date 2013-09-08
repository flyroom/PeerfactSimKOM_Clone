/*
 * Copyright (c) 2012-2013 Open Source Community - <http://www.peerfact.org>
 * Copyright (c) 2011-2012 University of Paderborn - UPB
 * Copyright (c) 2005-2011 KOM - Multimedia Communications Lab
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */

package org.peerfact.impl.overlay.dht.centralizedstorage.components;

import java.io.Serializable;

import org.peerfact.api.overlay.OverlayKey;
import org.peerfact.api.overlay.dht.DHTKey;

/**
 * Simple implementation of an OverlayKey. The content of the key is an
 * arbitrary string.
 * 
 * @author Konstantin Pussep
 * @author Sebastian Kaune
 * @version 3.0, 10.12.2007
 * 
 */
public class CSOverlayKey implements DHTKey<String>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6363895242690140210L;

	private String value;

	/**
	 * Create new overlay key from the given string value.
	 * 
	 * @param value
	 *            - value of the key.
	 */
	public CSOverlayKey(String value) {
		this.value = value;
	}

	@Override
	public byte[] getBytes() {
		return value.getBytes();
	}

	@Override
	public String getUniqueValue() {
		return value;
	}

	@Override
	public int compareTo(OverlayKey<String> o) {
		CSOverlayKey otherKey = (CSOverlayKey) o;
		return this.value.compareTo(otherKey.value);
	}

	@Override
	public String toString() {
		return "OverlayKey(" + value + ")";
	}

	@Override
	public long getTransmissionSize() {
		return getBytes().length;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		CSOverlayKey other = (CSOverlayKey) obj;
		if (value == null) {
			if (other.value != null) {
				return false;
			}
		} else if (!value.equals(other.value)) {
			return false;
		}
		return true;
	}

}
