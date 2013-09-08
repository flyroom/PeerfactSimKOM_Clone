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

import java.util.List;

import org.apache.log4j.Logger;
import org.peerfact.impl.overlay.dht.can.components.CanNode;
import org.peerfact.impl.overlay.dht.can.components.CanOverlayContact;
import org.peerfact.impl.overlay.dht.can.components.CanOverlayID;
import org.peerfact.impl.util.logging.SimLogger;

/**
 * 
 * Tells a peer its new neighbours.
 * 
 * @author Bjoern Dollak <peerfact@kom.tu-darmstadt.de>
 * @version February 2010
 * 
 */
public class NewNeighbourMsg extends CanMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3132810480288054819L;

	private static Logger log = SimLogger.getLogger(CanNode.class);

	private List<CanOverlayContact> oldNeighbours;

	private List<CanOverlayContact> newNeighbours;

	private CanOverlayContact c;

	/**
	 * Tells a peer its new neighbours.
	 * 
	 * @param sender
	 * @param receiver
	 * @param c
	 *            sender peer is as well removed
	 * @param oldNeighbours
	 *            these neighbours will be deleted
	 * @param newNeighbours
	 *            these neighbours are added.
	 */
	public NewNeighbourMsg(CanOverlayID sender, CanOverlayID receiver,
			CanOverlayContact c, List<CanOverlayContact> oldNeighbours,
			List<CanOverlayContact> newNeighbours) {
		super(sender, receiver);
		this.newNeighbours = newNeighbours;
		this.oldNeighbours = oldNeighbours;
		this.c = c;
		log.debug("NewNeighbourMsg sent. sent from node " + sender.toString()
				+ " to: " + receiver.toString());
	}

	@Override
	public long getSize() {
		return super.getSize() + oldNeighbours.size()
				* oldNeighbours.get(0).getTransmissionSize()
				+ newNeighbours.size()
				* newNeighbours.get(0).getTransmissionSize()
				+ c.getTransmissionSize();
	}

	public List<CanOverlayContact> getNewNeighbours() {
		return newNeighbours;
	}

	public List<CanOverlayContact> getOldNeighbours() {
		return oldNeighbours;
	}

	public CanOverlayContact getContact() {
		return this.c;
	}
}
