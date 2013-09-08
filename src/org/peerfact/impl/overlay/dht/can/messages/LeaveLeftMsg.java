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

import java.util.LinkedList;
import java.util.List;

import org.peerfact.impl.overlay.dht.can.components.CanArea;
import org.peerfact.impl.overlay.dht.can.components.CanOverlayContact;
import org.peerfact.impl.overlay.dht.can.components.CanOverlayID;
import org.peerfact.impl.overlay.dht.can.components.CanVID;

/**
 * 
 * Answer for leave or takeover, tells the node the new data.
 * 
 * @author Bjoern Dollak <peerfact@kom.tu-darmstadt.de>
 * @version February 2010
 * 
 */
public class LeaveLeftMsg extends CanMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1355134506575431845L;

	private CanArea area;

	private List<CanOverlayContact> neighbours;

	private CanOverlayContact[] vidNeighbours;

	private CanVID vid;

	private List<Object[]> newHashs;

	/**
	 * Answer for leave or takeover, tells the node the new data.
	 * 
	 * @param sender
	 * @param receiver
	 * @param c
	 *            new CanOverlayContact data
	 * @param neighbours
	 *            new list of neighbours
	 * @param vidNeighbours
	 *            new array of VID-neighbours
	 * @param vid
	 *            new own VID
	 * @param newHashs
	 *            new hashs which should be saved in the area of the peer
	 */
	public LeaveLeftMsg(CanOverlayID sender, CanOverlayID receiver,
			CanOverlayContact c, List<CanOverlayContact> neighbours,
			CanOverlayContact[] vidNeighbours, CanVID vid,
			List<Object[]> newHashs) {
		super(sender, receiver);
		this.area = c.getArea();
		this.vid = vid;
		this.neighbours = neighbours;
		this.vidNeighbours = vidNeighbours;
		this.newHashs = newHashs;
		if (newHashs == null) {
			this.newHashs = new LinkedList<Object[]>();
		}
		if (neighbours == null) {
			this.neighbours = new LinkedList<CanOverlayContact>();
		}
	}

	@Override
	public long getSize() {
		return super.getSize()
				+ area.getTransmissionSize()
				+ neighbours.size()
				* neighbours.get(0).getTransmissionSize()
				+ vidNeighbours.length
				* vidNeighbours[0].getTransmissionSize()
				+ vid.getTransmissionSize()
				+ newHashs.size() * neighbours.get(0).getTransmissionSize();
	}

	public CanArea getArea() {
		return area;
	}

	public List<CanOverlayContact> getNeighbours() {
		return neighbours;
	}

	public CanOverlayContact[] getVidNeighbours() {
		return this.vidNeighbours;
	}

	public CanVID getVid() {
		return this.vid;
	}

	public List<Object[]> getNewHashs() {
		return this.newHashs;
	}

}
