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

import java.io.Serializable;

import org.peerfact.api.common.Transmitable;
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
 * This class is meant to encapsulates a VON ID and the TranInfo of a node.
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class VonContact implements OverlayContact<VonID>, Transmitable,
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3284709117693463662L;

	private final VonID id;

	private final TransInfo transInfo;

	public VonContact(VonID id, TransInfo transInfo) {
		this.id = id;
		this.transInfo = transInfo;
	}

	@Override
	public VonID getOverlayID() {
		return id;
	}

	@Override
	public TransInfo getTransInfo() {
		return transInfo;
	}

	@Override
	public long getTransmissionSize() {
		// size = sizeOfOlID + sizeOfTransInfo = sizeOfOlID + (4byte + 2byte) =
		// sizeOfOlID+ 6byte
		return id.getTransmissionSize() + 6;
	}

	@Override
	public String toString() {
		return "[olId=" + id + " transinfo=" + transInfo + "]";
	}

}
