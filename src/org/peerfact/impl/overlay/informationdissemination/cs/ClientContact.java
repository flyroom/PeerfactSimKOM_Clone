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

import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.api.transport.TransInfo;

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
 * Provides a Container for id, ip and port for a Client.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 01/06/2011
 */
public class ClientContact implements OverlayContact<ClientID> {

	/**
	 * The identifier of a node.
	 */
	private final ClientID clientID;

	/**
	 * Contains the information to contact the overlay node.
	 */
	private final TransInfo transInfo;

	/**
	 * Constructor of this class. Sets the ClientID and transInfo.
	 * 
	 * @param id
	 *            The ID of a node in the overlay.
	 * @param transInfo
	 *            The contact information of a node in the overlay.
	 */
	public ClientContact(ClientID id, TransInfo transInfo) {
		this.clientID = id;
		this.transInfo = transInfo;
	}

	@Override
	public ClientID getOverlayID() {
		return clientID;
	}

	@Override
	public TransInfo getTransInfo() {
		return transInfo;
	}

	@Override
	public String toString() {
		return "[clientID=" + clientID + " transinfo=" + transInfo + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((clientID == null) ? 0 : clientID.hashCode());
		result = prime * result
				+ ((transInfo == null) ? 0 : transInfo.hashCode());
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
		ClientContact other = (ClientContact) obj;
		if (clientID == null) {
			if (other.clientID != null) {
				return false;
			}
		} else if (!clientID.equals(other.clientID)) {
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

	@Override
	public long getTransmissionSize() {
		return clientID.getTransmissionSize() + transInfo.getTransmissionSize();
	}
}
