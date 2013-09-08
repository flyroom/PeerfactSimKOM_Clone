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

import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.api.transport.TransInfo;

/**
 * Contact information of a peer participating in a Gnutella overlay.
 * 
 * @author <info@peerfact.org>
 * 
 * @version 05/06/2011
 */
public class GnutellaLikeOverlayContact implements
		OverlayContact<GnutellaOverlayID> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7678943948263724796L;

	TransInfo transInfo;

	transient GnutellaOverlayID id;

	public GnutellaLikeOverlayContact(GnutellaOverlayID id,
			TransInfo transInfo) {
		this.id = id;
		this.transInfo = transInfo;
	}

	/**
	 * Returns the overlay ID of the given Gnutella06 node
	 */
	@Override
	public GnutellaOverlayID getOverlayID() {
		return id;
	}

	@Override
	public TransInfo getTransInfo() {
		return transInfo;
	}

	@Override
	public String toString() {
		return id.toString();
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof GnutellaLikeOverlayContact)) {
			return false;
		}
		return id.equals(((GnutellaLikeOverlayContact) o).id);
	}

	/**
	 * Returns the size of this contact information.
	 * 
	 * @return
	 */
	public static int getSize() {
		return 8; // TransInfo: 4, ID: 4
	}

	@Override
	public long getTransmissionSize() {
		return transInfo.getTransmissionSize() + id.getTransmissionSize();
	}

}
