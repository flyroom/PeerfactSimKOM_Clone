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

package org.peerfact.impl.overlay.informationdissemination.von.messages;

import org.peerfact.api.common.Message;
import org.peerfact.impl.overlay.informationdissemination.von.VonID;

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
 * This message is used by a joining peer to obtain a unique id from the gateway
 * server. As this peer does not have a valid ID at beginning, it uses
 * <code>VonOverlayID.EMPTY_ID</code> instead. The gateway server answers with a
 * message of the same type containing a valid ID.
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class ObtainIDMsg extends AbstractVonMsg {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3034580261859115881L;

	public ObtainIDMsg(VonID sender, VonID receiver) {
		super(sender, receiver);
	}

	@Override
	public Message getPayload() {
		// This message does not contain a payload
		return null;
	}

	@Override
	public long getSize() {
		// size = sizeOfAbstractMsg + 2*sizeOfVonOverlayID = sizeOfAbstractMsg +
		// 2*sizeOfInt = sizeOfAbstractMsg + 2*4byte = sizeOfAbstractMsg + 8byte
		return getSizeOfAbstractMessage() + 8;
	}

}
