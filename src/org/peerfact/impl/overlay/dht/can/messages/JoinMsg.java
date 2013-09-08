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

package org.peerfact.impl.overlay.dht.can.messages;

import org.peerfact.impl.overlay.dht.can.components.CanOverlayContact;
import org.peerfact.impl.overlay.dht.can.components.CanOverlayID;

/**
 * 
 * Join Messages is sent, when a peer wants to join the overlay. Is sent from
 * the new peer to the selected area.
 * 
 * @author Bjoern Dollak <peerfact@kom.tu-darmstadt.de>
 * @version February 2010
 * 
 */
public class JoinMsg extends CanMessage {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5402786958241731438L;

	private CanOverlayContact joiningNode;

	/**
	 * Join Messages is sent, when a peer wants to join the overlay. Is sent
	 * from the new peer to the selected area.
	 * 
	 * @param sender
	 * @param receiver
	 * @param c
	 *            CanOverlayContact of the joining node
	 */
	public JoinMsg(CanOverlayID sender, CanOverlayID receiver,
			CanOverlayContact c) {
		super(sender, receiver);
		this.joiningNode = c.clone();
	}

	@Override
	public long getSize() {
		return super.getSize() + joiningNode.getTransmissionSize();
	}

	public CanOverlayContact getJoiningNode() {
		return joiningNode;
	}

}
