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

import org.peerfact.Constants;
import org.peerfact.api.overlay.OverlayID;

/**
 * Unique identification number of a Gnutella node. Not necessary for routing,
 * unlike in structured overlays.
 * 
 * @author <info@peerfact.org>
 * 
 * @version 05/06/2011
 */
public class GnutellaOverlayID implements OverlayID<Integer> {

	int overlayID;

	/**
	 * Creates a new overlay ID from the given integer value.
	 */
	public GnutellaOverlayID(int id) {
		this.overlayID = id;
	}

	@Override
	public byte[] getBytes() {
		// TODO: Encoding into bytes not implemented yet.
		return null;
	}

	@Override
	public Integer getUniqueValue() {
		return overlayID;
	}

	@Override
	public int compareTo(OverlayID<Integer> o) {
		return this.overlayID - ((GnutellaOverlayID) o).overlayID;
	}

	@Override
	public String toString() {
		return "#" + overlayID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + overlayID;
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
		GnutellaOverlayID other = (GnutellaOverlayID) obj;
		if (overlayID != other.overlayID) {
			return false;
		}
		return true;
	}

	@Override
	public long getTransmissionSize() {
		return Constants.INT_SIZE;
	}

}
