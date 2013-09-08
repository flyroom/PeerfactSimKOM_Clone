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

import java.math.BigInteger;

import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.api.transport.TransInfo;

/**
 * ChordContact encapsulates ChordId and Transport Address. This information is
 * used to contact between overlay nodes.
 * 
 * @author Minh Hoang Nguyen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public abstract class AbstractChordContact implements OverlayContact<ChordID>,
		Comparable<AbstractChordContact>, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8387309277752374303L;

	private final ChordID id;

	private final TransInfo transInfo;

	private boolean isAlive;

	public AbstractChordContact(ChordID id, TransInfo transInfo) {
		this.id = id;
		this.transInfo = transInfo;
	}

	@Override
	public ChordID getOverlayID() {
		return id;
	}

	@Override
	public TransInfo getTransInfo() {
		return transInfo;
	}

	public boolean isAlive() {
		return isAlive;
	}

	public void setAlive(boolean isAlive) {
		this.isAlive = isAlive;
	}

	public BigInteger getDistance(AbstractChordContact contact) {
		return this.id.getDistance(contact.getOverlayID());
	}

	@Override
	public int compareTo(AbstractChordContact o) {
		return this.id.compareTo(o.getOverlayID());
	}

	public boolean equals(AbstractChordContact o) {
		return (o == null) ? false : id.equals(o.getOverlayID());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + (isAlive ? 1231 : 1237);
		result = prime * result
				+ ((transInfo == null) ? 0 : transInfo.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof AbstractChordContact)) {
			return false;
		}
		AbstractChordContact other = (AbstractChordContact) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		if (isAlive != other.isAlive) {
			return false;
		}
		if (transInfo == null) {
			if (other.transInfo != null) {
				return false;
			}
		} else if (!transInfo.equals(other.transInfo)) {
			return false;
		}
		return true;
	}

	/**
	 * @param a
	 * @param b
	 * @return if ChordContact stands in the interval (a,b) in the ring form.
	 * 
	 */
	public boolean between(AbstractChordContact a, AbstractChordContact b) {
		if (a == null || b == null) {
			return false;
		}
		return this.getOverlayID().between(a.getOverlayID(), b.getOverlayID());
	}

	@Override
	public String toString() {
		return "" + id;
	}

	@Override
	public long getTransmissionSize() {
		return this.id.getTransmissionSize()
				+ this.transInfo.getTransmissionSize();

	}
}
