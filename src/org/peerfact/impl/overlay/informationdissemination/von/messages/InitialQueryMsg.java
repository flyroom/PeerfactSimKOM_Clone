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

import java.awt.Point;

import org.peerfact.api.common.Message;
import org.peerfact.impl.overlay.informationdissemination.von.VonContact;
import org.peerfact.impl.overlay.informationdissemination.von.VonID;
import org.peerfact.impl.overlay.informationdissemination.von.VonNodeInfo;


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
 * This message is used to query for the initial neighbors of a node.
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class InitialQueryMsg extends AbstractVonMsg {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1328503643896600273L;

	private final VonNodeInfo senderInfo;

	public InitialQueryMsg(VonContact senderContact, Point position,
			int aoiRadius) {
		/*
		 * The receiver is not specified as it is unknown -> other nodes will
		 * forward the msg to the right receiver
		 */
		super(senderContact.getOverlayID(), VonID.EMPTY_ID);

		this.senderInfo = new VonNodeInfo(senderContact, position, aoiRadius);
	}

	@Override
	public Message getPayload() {
		// There is no payload message in this case
		return null;
	}

	@Override
	public long getSize() {
		// size = sizeOfAbstractMsg + sizeOfNodeInfo
		return getSizeOfAbstractMessage() + senderInfo.getTransmissionSize();
	}

	public VonNodeInfo getSenderInfo() {
		return senderInfo;
	}
}
