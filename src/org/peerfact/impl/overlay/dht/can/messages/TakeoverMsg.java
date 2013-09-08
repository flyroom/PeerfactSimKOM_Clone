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

import org.peerfact.impl.overlay.dht.can.components.CanOverlayContact;
import org.peerfact.impl.overlay.dht.can.components.CanOverlayID;

/**
 * 
 * A missing peer is detected and the responsible peer is informed.
 * 
 * @author Bjoern Dollak <peerfact@kom.tu-darmstadt.de>
 * @version February 2010
 * 
 */
public class TakeoverMsg extends CanMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5709631404963777459L;

	private CanOverlayContact missingNode;

	private List<CanOverlayContact> neighboursOfMissing;

	private CanOverlayContact[] vidNeighbourOfMissing;

	/**
	 * A missing peer is detected and the responsible peer is informed.
	 * 
	 * @param sender
	 * @param receiver
	 * @param missingNode
	 *            lost node
	 * @param neighboursOfMissing
	 *            neighbourList of the lost peer
	 * @param vidNeighbourOfMissing
	 *            VID neighbours of the lost peer
	 */
	public TakeoverMsg(CanOverlayID sender, CanOverlayID receiver,
			CanOverlayContact missingNode,
			List<CanOverlayContact> neighboursOfMissing,
			CanOverlayContact[] vidNeighbourOfMissing) {
		super(sender, receiver);

		this.missingNode = missingNode;
		this.neighboursOfMissing = neighboursOfMissing;
		this.vidNeighbourOfMissing = vidNeighbourOfMissing;
		if (neighboursOfMissing == null) {
			this.neighboursOfMissing = new LinkedList<CanOverlayContact>();
		}
		if (vidNeighbourOfMissing == null) {
			this.vidNeighbourOfMissing = new CanOverlayContact[2];
		}
	}

	@Override
	public long getSize() {
		return missingNode.getTransmissionSize() + neighboursOfMissing.size()
				* neighboursOfMissing.get(0).getTransmissionSize()
				+ vidNeighbourOfMissing.length
				* vidNeighbourOfMissing[0].getTransmissionSize();
	}

	public CanOverlayContact getMissingNode() {
		return missingNode;
	}

	public List<CanOverlayContact> getNeighboursOfMissing() {
		return this.neighboursOfMissing;
	}

	public CanOverlayContact[] getVidNeighboursOfMissing() {
		return this.vidNeighbourOfMissing;
	}
}
