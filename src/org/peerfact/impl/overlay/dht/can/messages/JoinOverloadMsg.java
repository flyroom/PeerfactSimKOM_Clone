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
import org.peerfact.impl.overlay.dht.can.components.CanArea;
import org.peerfact.impl.overlay.dht.can.components.CanNode;
import org.peerfact.impl.overlay.dht.can.components.CanOverlayContact;
import org.peerfact.impl.overlay.dht.can.components.CanOverlayID;
import org.peerfact.impl.util.logging.SimLogger;

/**
 * 
 * Answer in the join Operation. Tells the node the new area, neighbours and vid
 * neighbours.
 * 
 * @author Bjoern Dollak <peerfact@kom.tu-darmstadt.de>
 * @version February 2010
 * 
 */
public class JoinOverloadMsg extends CanMessage {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6186588258033691965L;

	private static Logger log = SimLogger.getLogger(CanNode.class);

	private List<CanOverlayContact> neighbours;

	CanArea area;

	CanOverlayContact[] vidNeighbours;

	/**
	 * Answer in the join Operation. Tells the node the new area, neighbours and
	 * vid neighbours.
	 * 
	 * @param sender
	 * @param receiver
	 * @param neighbours
	 *            new Neighbours
	 * @param area
	 *            new area
	 * @param vidNeighbours
	 *            new vidNeighbours
	 */
	public JoinOverloadMsg(CanOverlayID sender, CanOverlayID receiver,
			List<CanOverlayContact> neighbours, CanArea area,
			CanOverlayContact[] vidNeighbours) {
		super(sender, receiver);
		log.debug("JoinOverloadMsg sent.");
		this.neighbours = neighbours;
		this.area = area;
		this.vidNeighbours = vidNeighbours;
	}

	@Override
	public long getSize() {
		return super.getSize() + neighbours.size()
				* neighbours.get(0).getTransmissionSize()
				+ area.getTransmissionSize() + vidNeighbours.length
				* vidNeighbours[0].getTransmissionSize();
	}

	public List<CanOverlayContact> getNeighbours() {
		return neighbours;
	}

	public CanArea getArea() {
		return area;
	}

	public CanOverlayContact[] getVidNeighbours() {
		return vidNeighbours;
	}

}
