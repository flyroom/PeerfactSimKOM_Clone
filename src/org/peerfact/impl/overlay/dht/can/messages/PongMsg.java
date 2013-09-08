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

import org.peerfact.Constants;
import org.peerfact.impl.overlay.dht.can.components.CanOverlayContact;
import org.peerfact.impl.overlay.dht.can.components.CanOverlayID;

/**
 * 
 * Answer message for ping request. Includes the own contact, neighbours and
 * VID-neighbours.
 * 
 * @author Bjoern Dollak <peerfact@kom.tu-darmstadt.de>
 * @version February 2010
 * 
 */
public class PongMsg extends CanMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5555120317952487585L;

	private List<CanOverlayContact> neighbours;

	private CanOverlayContact contact;

	private CanOverlayContact[] vidNeighbours;

	private int operationID = 0;

	/**
	 * Answer message for ping request. Includes the own contact, neighbours and
	 * VID-neighbours. Remember the operation ID should be set, too.
	 * 
	 * @param sender
	 * @param receiver
	 * @param contact
	 *            own contact
	 * @param neighbours
	 *            own list of neighbours
	 * @param vidNeighbours
	 *            own VID-neighbours
	 */
	public PongMsg(CanOverlayID sender, CanOverlayID receiver,
			CanOverlayContact contact,
			List<CanOverlayContact> neighbours,
			CanOverlayContact[] vidNeighbours) {
		super(sender, receiver);
		this.neighbours = neighbours;
		this.contact = contact;
		this.vidNeighbours = vidNeighbours;
	}

	/**
	 * Answer message for ping request. Includes the own contact, neighbours and
	 * VID-neighbours.
	 * 
	 * @param sender
	 * @param receiver
	 * @param contact
	 *            own contact
	 * @param neighbours
	 *            own list of neighbours
	 * @param vidNeighbours
	 *            own VID-neighbours
	 */
	public PongMsg(PingMsg ping, CanOverlayContact contact,
			List<CanOverlayContact> neighbours,
			CanOverlayContact[] vidNeighbours) {
		super(ping.getReceiver(), ping.getSender());

		this.neighbours = neighbours;
		this.contact = contact;
		this.vidNeighbours = vidNeighbours;
		this.operationID = ping.getOperationId();
	}

	@Override
	public long getSize() {
		return super.getSize()
				+ (neighbours.size() > 0 ? neighbours.size()
						* neighbours.get(0).getTransmissionSize() : 0)
				+ contact.getTransmissionSize()
				+ (vidNeighbours.length > 0 && vidNeighbours[0] != null ? vidNeighbours[0]
						.getTransmissionSize() : 0)
				+ (vidNeighbours.length > 0 && vidNeighbours[1] != null ? vidNeighbours[1]
						.getTransmissionSize() : 0)
				+ Constants.INT_SIZE;
	}

	public List<CanOverlayContact> getNeighbours() {
		return neighbours;
	}

	public CanOverlayContact getContact() {
		return contact;
	}

	public CanOverlayContact[] getVidNeighbours() {
		return vidNeighbours;
	}

	/**
	 * Sets the operation ID of the TakeoverReplyOperation
	 * 
	 * @param operationID
	 */
	public void setOperationID(int operationID) {
		this.operationID = operationID;
	}

	public int getOperationID() {
		return operationID;
	}
}
