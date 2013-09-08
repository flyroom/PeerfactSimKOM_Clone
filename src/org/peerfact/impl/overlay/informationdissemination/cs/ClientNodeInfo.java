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

import java.awt.Point;
import java.io.Serializable;

import org.peerfact.api.common.Transmitable;
import org.peerfact.api.overlay.OverlayID;
import org.peerfact.api.overlay.ido.IDONodeInfo;

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
 * This class is a container of information of a client. It contains the
 * position, area of interest radius and the id of the client.<br>
 * This class is used to transmit/disseminate information from client to server
 * and server to clients.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 06/01/2011
 */
public class ClientNodeInfo implements IDONodeInfo, Transmitable, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7007399533225946390L;

	/**
	 * The position of the client
	 */
	private Point position;

	/**
	 * The area of interest of the client
	 */
	private int aoi;

	/**
	 * The ID of the client
	 */
	private ClientID clientID;

	public ClientNodeInfo(Point position, int aoi, ClientID clientID) {
		this.position = position;
		this.aoi = aoi;
		this.clientID = clientID;
	}

	@Override
	public Point getPosition() {
		return position;
	}

	@Override
	public int getAoiRadius() {
		return aoi;
	}

	@Override
	public OverlayID<Integer> getID() {
		return clientID;
	}

	@Override
	public long getTransmissionSize() {
		// position + aoi + clientID = 2*4Bytes + 4 Bytes + clientID
		return 8 + 4 + clientID.getTransmissionSize();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + aoi;
		result = prime * result
				+ ((clientID == null) ? 0 : clientID.hashCode());
		result = prime * result
				+ ((position == null) ? 0 : position.hashCode());
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
		ClientNodeInfo other = (ClientNodeInfo) obj;
		if (aoi != other.aoi) {
			return false;
		}
		if (clientID == null) {
			if (other.clientID != null) {
				return false;
			}
		} else if (!clientID.equals(other.clientID)) {
			return false;
		}
		if (position == null) {
			if (other.position != null) {
				return false;
			}
		} else if (!position.equals(other.position)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuffer temp = new StringBuffer();
		temp.append("[ AOI: ");
		temp.append(getAoiRadius());
		temp.append(", position: ");
		temp.append(getPosition());
		temp.append(", clientID: ");
		temp.append(getID());
		temp.append(" ]");
		return temp.toString();
	}

}
