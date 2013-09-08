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

package org.peerfact.impl.overlay.informationdissemination.psense;

import java.io.Serializable;

import org.peerfact.api.common.Transmitable;
import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.impl.overlay.informationdissemination.psense.util.Constants;

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
 * This class encapsulates a pSenseID and the TransInfo of a node. It contains
 * the needed information about a node in pSense for the contact.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 09/15/2010
 */
public class PSenseContact implements OverlayContact<PSenseID>, Transmitable,
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4032518970131705947L;

	/**
	 * The identifier of a node.
	 */
	private final PSenseID pSenseID;

	/**
	 * Contains the information to contact the overlay node.
	 */
	private final TransInfo transInfo;

	/**
	 * Constructor of this class. Sets the pSenseID and transInfo.
	 * 
	 * @param id
	 *            The ID of a node in the overlay.
	 * @param transInfo
	 *            The contact information of a node in the overlay.
	 */
	public PSenseContact(PSenseID id, TransInfo transInfo) {
		this.pSenseID = id;
		this.transInfo = transInfo;
	}

	@Override
	public PSenseID getOverlayID() {
		return pSenseID;
	}

	@Override
	public TransInfo getTransInfo() {
		return transInfo;
	}

	@Override
	public long getTransmissionSize() {
		// size = sizeOfpSenseID + sizeOfTransInfo = sizeOfpSenseID + (sizeOfIP
		// + sizeOfPort)
		return pSenseID.getTransmissionSize() + Constants.BYTE_SIZE_OF_IP
				+ Constants.BYTE_SIZE_OF_PORT;
	}

	@Override
	public String toString() {
		return "[pSenseID=" + pSenseID + " transinfo=" + transInfo + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((pSenseID == null) ? 0 : pSenseID.hashCode());
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
		PSenseContact other = (PSenseContact) obj;
		if (pSenseID == null) {
			if (other.pSenseID != null) {
				return false;
			}
		} else if (!pSenseID.equals(other.pSenseID)) {
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

}
