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

package org.peerfact.impl.overlay.informationdissemination.psense.messages;

import java.awt.Point;

import org.peerfact.api.common.Message;
import org.peerfact.impl.overlay.informationdissemination.psense.PSenseID;
import org.peerfact.impl.overlay.informationdissemination.psense.util.Constants;
import org.peerfact.impl.overlay.informationdissemination.psense.util.SequenceNumber;
import org.peerfact.impl.overlay.informationdissemination.psense.util.Constants.MSG_TYPE;


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
 * This class represent the sensor request message in the pSense. It contains:
 * <ul>
 * <li>maximal hops for this message in the overlay</li>
 * <li>a sequence number to distinguish old and new information</li>
 * <li>the vision range radius of the sender</li>
 * <li>the position of the sender</li>
 * <li>an identifier for the message type</li>
 * <li>a sectorID, for the requested sector</li>
 * <li>the {@link PSenseID} of the originator of this content</li>
 * </ul>
 * This message will be send to all <b>sensor nodes</b> of the local node.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 09/15/2010
 */
public class SensorRequestMsg extends AbstractPSenseMsg {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7291913791649723509L;

	/**
	 * The sector identifier for this request.
	 */
	private final byte sectorID;

	/**
	 * The {@link PSenseID} of the originator of this content
	 */
	private final PSenseID senderID;

	/**
	 * Constructor of this class. It sets the attributes with the given
	 * parameters.
	 * 
	 * @param sender
	 *            The pSenseID of the sender.
	 * @param hopCount
	 *            The maximal hops for this information.
	 * @param sequenceNr
	 *            The actual sequence number for this round of the sender.
	 * @param visionRangeRadius
	 *            The vision range radius of the sender
	 * @param position
	 *            The position of the sender
	 * @param sectorID
	 *            The requested sector id
	 */
	public SensorRequestMsg(PSenseID sender, byte hopCount,
			SequenceNumber sequenceNr, int visionRangeRadius, Point position,
			byte sectorID) {
		super(hopCount, sequenceNr, visionRangeRadius, position,
				MSG_TYPE.SENSOR_REQUEST);
		this.sectorID = sectorID;
		senderID = sender;
	}

	@Override
	public long getSize() {
		// size = sizeOfAbstractMessage + sizeOfSectorID + sizeOfSenderID
		return getSizeOfAbstractMessage() + Constants.BYTE_SIZE_OF_SECTOR_ID
				+ senderID.getTransmissionSize();
	}

	@Override
	public Message getPayload() {
		return this;
	}

	/**
	 * Gets the sector identifier for this request.
	 * 
	 * @return the sectorID for that is requested
	 */
	public byte getSectorID() {
		return sectorID;
	}

	/**
	 * Gets the {@link PSenseID} of the originator from the content.
	 * 
	 * @return Gets the pSenseID of the sender.
	 */
	public PSenseID getSenderID() {
		return senderID;
	}

	@Override
	public String toString() {
		StringBuffer temp = new StringBuffer();
		temp.append("[ MsgType: ");
		temp.append(getMsgType());
		temp.append(", hopCount: ");
		temp.append(getHopCount());
		temp.append(", sequenceNumber: ");
		temp.append(getSequenceNr());
		temp.append(", Position: ");
		temp.append(getPosition());
		temp.append(", VisionRange: ");
		temp.append(getRadius());
		temp.append(", senderID: ");
		temp.append(getSenderID());
		temp.append(", sectorID: ");
		temp.append(getSectorID());
		temp.append(" ]");
		return temp.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SensorRequestMsg) {
			SensorRequestMsg o = (SensorRequestMsg) obj;

			return super.equals(o)
					&& this.sectorID == o.sectorID
					&& (this.senderID == o.senderID || (this.senderID != null && this.senderID
							.equals(o.senderID)));
		}
		return false;
	}
}
