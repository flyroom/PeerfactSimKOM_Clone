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

import org.peerfact.impl.overlay.dht.can.components.CanArea;
import org.peerfact.impl.overlay.dht.can.components.CanOverlayContact;
import org.peerfact.impl.overlay.dht.can.components.CanOverlayID;

/**
 * 
 * Tells the vid neighbour with a common parent node, that it wants to leave.
 * 
 * @author Bjoern Dollak <peerfact@kom.tu-darmstadt.de>
 * @version February 2010
 * 
 */
public class LeaveMsg extends CanMessage {

	/** 
	 * 
	 */
	private static final long serialVersionUID = 2417082589973599972L;

	private CanOverlayContact leavingNode;

	private List<CanOverlayContact> neighbours;

	private CanOverlayContact[] vidNeighbours;

	private CanArea area;

	/**
	 * 
	 * @param sender
	 * @param receiver
	 * @param c
	 *            leaving contact
	 * @param neighbours
	 *            neighbours of leaving contact
	 * @param area
	 *            area of leaving node
	 * @param vidNeighbours
	 *            VID neighbours of leaving node
	 */
	public LeaveMsg(CanOverlayID sender, CanOverlayID receiver,
			CanOverlayContact c, List<CanOverlayContact> neighbours,
			CanArea area, CanOverlayContact[] vidNeighbours) {
		super(sender, receiver);
		this.leavingNode = c;
		this.neighbours = neighbours;
		this.area = area;
		this.vidNeighbours = vidNeighbours;
		this.area = area;
	}

	public CanOverlayContact[] getVidNeighbours() {
		return vidNeighbours;
	}

	public CanOverlayContact getLeavingNode() {
		return leavingNode;
	}

	public List<CanOverlayContact> getNeighbours() {
		return this.neighbours;
	}

	public CanArea getArea() {
		return this.area;
	}

	@Override
	public long getSize() {
		return super.getSize() + leavingNode.getTransmissionSize()
				+ neighbours.size() * neighbours.get(0).getTransmissionSize()
				+ vidNeighbours.length * vidNeighbours[0].getTransmissionSize()
				+ area.getTransmissionSize();
	}

}
