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

package org.peerfact.impl.overlay.informationdissemination.von;

import java.awt.Point;
import java.io.Serializable;

import org.peerfact.api.common.Transmitable;
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
 * This class is used to encapsulate the important node data which has to be
 * send within various messages.
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class VonNodeInfo implements IDONodeInfo, Transmitable, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6333865292278092870L;

	private final VonContact contact;

	private final Point position;

	private final int aoiRadius;

	public VonNodeInfo(VonContact contact, Point position, int aoiRadius) {
		this.contact = contact;
		this.position = position;
		this.aoiRadius = aoiRadius;
	}

	public VonContact getContact() {
		return contact;
	}

	@Override
	public int getAoiRadius() {
		return aoiRadius;
	}

	@Override
	public long getTransmissionSize() {
		// size = sizeOfVonContact + sizeOfPoint + sizeOfAOI = sizeOfVonContact
		// + (2*sizeOfInt) + sizeOfInt = sizeOfVonContact + 8byte + 4byte =
		// sizeOfVonContact + 12byte
		return contact.getTransmissionSize() + 12;
	}

	@Override
	public String toString() {
		return "[contact=" + contact + " position=" + position + " aoiRadius="
				+ aoiRadius + "]";
	}

	@Override
	public Point getPosition() {
		return position;
	}

	@Override
	public VonID getID() {
		return contact.getOverlayID();
	}
}
