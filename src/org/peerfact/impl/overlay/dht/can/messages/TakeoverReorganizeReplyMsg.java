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

/**
 * 
 * Answer to TakeoverRebuild Operation. Tells the responsible peer the own data:
 * own OverlayContact, area, neighburs, VId neighbours and stored hashs
 * 
 * @author Bjoern Dollak <peerfact@kom.tu-darmstadt.de>
 * @version February 2010
 * 
 */
public class TakeoverReorganizeReplyMsg extends CanMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5226043207945708596L;

	private CanOverlayContact master;

	private CanArea area;

	private List<CanOverlayContact> neighbours;

	private CanOverlayContact[] vidNeighbours;

	private List<Object[]> storedHashs;

	/**
	 * Answer to TakeoverRebuild Operation. Tells the responsible peer the own
	 * data: own OverlayContact, area, neighburs, VId neighbours and stored
	 * hashs.
	 * 
	 * @param sender
	 * @param receiver
	 * @param c
	 *            own CanOverlayContact
	 * @param area
	 *            own area
	 * @param neighbours
	 *            List of own neighbours
	 * @param vidNeighbours
	 *            array of own VID neighbours
	 * @param storedHashs
	 *            List of own hashs with connected contact
	 */
	public TakeoverReorganizeReplyMsg(CanOverlayID sender,
			CanOverlayID receiver, CanOverlayContact c, CanArea area,
			List<CanOverlayContact> neighbours,
			CanOverlayContact[] vidNeighbours, List<Object[]> storedHashs) {
		super(sender, receiver);
		this.master = c;
		this.area = area;
		this.neighbours = neighbours;
		this.vidNeighbours = vidNeighbours;
		this.storedHashs = storedHashs;
		if (storedHashs == null) {
			this.storedHashs = new LinkedList<Object[]>();
		}
		if (neighbours == null) {
			this.neighbours = new LinkedList<CanOverlayContact>();
		}
	}

	@Override
	public long getSize() {
		return super.getSize() + master.getTransmissionSize()
				+ area.getTransmissionSize() + neighbours.size()
				* neighbours.get(0).getTransmissionSize()
				+ vidNeighbours.length * vidNeighbours[0].getTransmissionSize()
				+ storedHashs.size() * neighbours.get(0).getTransmissionSize();
	}

	public CanOverlayContact getMaster() {
		return master;
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

	public List<Object[]> getStoredHashs() {
		return storedHashs;
	}

}
