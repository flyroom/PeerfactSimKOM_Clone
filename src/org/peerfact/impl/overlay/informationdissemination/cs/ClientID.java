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

package org.peerfact.impl.overlay.informationdissemination.cs;

import java.io.Serializable;

import org.peerfact.api.common.Transmitable;
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
 * This class provides an ID for a Client. The ID is an integer.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 01/06/2011
 */
public class ClientID implements OverlayID<Integer>, Transmitable, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5872123652570655941L;

	/**
	 * An empty ID for a client
	 */
	public final static ClientID EMPTY_ID = new ClientID(-1);

	/**
	 * The overlay id for the client
	 */
	private final Integer overlayID;

	public ClientID(int id) {
		overlayID = id;
	}

	@Override
	public int compareTo(OverlayID<Integer> id) {
		return Integer.valueOf(overlayID).compareTo(((ClientID) id)
				.getUniqueValue());
	}

	@Override
	public Integer getUniqueValue() {
		return overlayID;
	}

	@Override
	public byte[] getBytes() {
		/*
		 * Convert the integer to an byte array
		 */
		byte[] buffer = new byte[4];
		buffer[0] = (byte) (overlayID >> 24);
		buffer[1] = (byte) ((overlayID << 8) >> 24);
		buffer[2] = (byte) ((overlayID << 16) >> 24);
		buffer[3] = (byte) ((overlayID << 24) >> 24);

		return buffer;
	}

	@Override
	public long getTransmissionSize() {
		// size = siezOfInt = 4byte
		return 4;
	}

	@Override
	public String toString() {
		return Integer.valueOf(overlayID).toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ClientID) {
			ClientID o = (ClientID) obj;

			return this.overlayID.equals(o.overlayID);
		}
		return false;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result
				+ ((overlayID == null) ? 0 : overlayID.hashCode());
		return result;
	}
}
