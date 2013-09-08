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
import org.peerfact.impl.overlay.informationdissemination.psense.PSenseContact;
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
 * This class represent the sensor response message in the pSense. It contains:
 * <ul>
 * <li>maximal hops of this information in the overlay</li>
 * <li>a sequence number to distinguish old and new information</li>
 * <li>the vision range radius of the new sensor node</li>
 * <li>the position of the new sensor node</li>
 * <li>an identifier for the message type</li>
 * <li>a sectorID, for the sector which was requested</li>
 * <li>contact information, because it may be a new sensor node</li>
 * </ul>
 * This message will be send to the node, which has send a
 * {@link SensorRequestMsg}.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 09/15/2010
 */
public class SensorResponseMsg extends AbstractPSenseMsg {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5093716631088353654L;

	/**
	 * The sector identifier for this response.
	 */
	private final byte sectorID;

	/**
	 * Contact information, because it may be a new sensor node
	 */
	private final PSenseContact contact;

	/**
	 * The sequence number of the request message, to distinguish old and new
	 * response messages for the same sector.
	 */
	private final SequenceNumber sequenceNrRequest;

	/**
	 * Constructor of this class. It sets the attributes with the given
	 * parameters.
	 * 
	 * @param hopCount
	 *            The maximal hops for this information
	 * @param sequenceNr
	 *            The sequence number of this information
	 * @param visionRangeRadius
	 *            The vision range radius of the next sensor node for this
	 *            sector
	 * @param position
	 *            The position of the next sensor node for this sector
	 * @param sectorID
	 *            The identifier for which sector is response
	 * @param contact
	 *            The contact information for the next sensor node
	 * @param sequenceNrRequest
	 *            The sequence number of the corresponding
	 *            {@link SensorRequestMsg}
	 */
	public SensorResponseMsg(byte hopCount, SequenceNumber sequenceNr,
			int visionRangeRadius, Point position, byte sectorID,
			PSenseContact contact, SequenceNumber sequenceNrRequest) {
		super(hopCount, sequenceNr, visionRangeRadius, position,
				MSG_TYPE.SENSOR_RESPONSE);
		this.sectorID = sectorID;
		this.contact = contact;
		this.sequenceNrRequest = sequenceNrRequest;
	}

	@Override
	public long getSize() {
		// size = sizeOfAbstractMessage + sizeOfSectorID + sizeOfPSenseContact +
		// sequenceNrRequest
		return getSizeOfAbstractMessage() + Constants.BYTE_SIZE_OF_SECTOR_ID
				+ contact.getTransmissionSize() + Constants.BYTE_SIZE_OF_SEQ_NR;
	}

	@Override
	public Message getPayload() {
		return this;
	}

	/**
	 * Gets the sector identifier for this response.
	 * 
	 * @return the sectorID for which is this response
	 */
	public byte getSectorID() {
		return sectorID;
	}

	/**
	 * Gets the contact information to the given content.
	 * 
	 * @return the contact information
	 */
	public PSenseContact getContact() {
		return contact;
	}

	/**
	 * Gets the sequence number of the corresponding request
	 * 
	 * @return sequence number of the corresponding request
	 */
	public SequenceNumber getSequenceNrRequest() {
		return sequenceNrRequest;
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
		temp.append(", ContactofOriginator: ");
		temp.append(getContact());
		temp.append(", sectorID: ");
		temp.append(getSectorID());
		temp.append(", sequenceNumberRequest: ");
		temp.append(getSequenceNrRequest());
		temp.append(" ]");
		return temp.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((contact == null) ? 0 : contact.hashCode());
		result = prime * result + sectorID;
		result = prime
				* result
				+ ((sequenceNrRequest == null) ? 0 : sequenceNrRequest
						.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof SensorResponseMsg)) {
			return false;
		}
		SensorResponseMsg other = (SensorResponseMsg) obj;
		if (contact == null) {
			if (other.contact != null) {
				return false;
			}
		} else if (!contact.equals(other.contact)) {
			return false;
		}
		if (sectorID != other.sectorID) {
			return false;
		}
		if (sequenceNrRequest == null) {
			if (other.sequenceNrRequest != null) {
				return false;
			}
		} else if (!sequenceNrRequest.equals(other.sequenceNrRequest)) {
			return false;
		}
		return true;
	}

}
