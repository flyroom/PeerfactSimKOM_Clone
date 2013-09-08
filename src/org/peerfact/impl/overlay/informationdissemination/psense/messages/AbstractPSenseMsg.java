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
 * This class combined all information, which are contained in all messages.<br>
 * That are the hopCount, a message type identifier, a sequence number, the
 * position and the vision range for the information of this message.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 09/15/2010
 */
public abstract class AbstractPSenseMsg implements Message {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5734877762337690101L;

	/**
	 * To distinguish the type of the message.
	 */
	private final MSG_TYPE msgType;

	/**
	 * The number of allowed hops
	 */
	private final byte hopCount;

	/**
	 * The radius for the message
	 */
	private final int radius;

	/**
	 * The position
	 */
	private final Point position;

	/**
	 * The sequence number for this message, to distinguish old and new
	 * messages.
	 */
	private final SequenceNumber sequenceNr;

	/**
	 * Constructor of the abstract message. It sets the attributes of this
	 * class.
	 * 
	 * @param hopCount
	 *            The number of allowing hops in the overlay for this message.
	 * @param sequenceNr
	 *            A consecutively number to distinguish old and new messages.
	 * @param radius
	 *            The radius for the message.
	 * @param position
	 *            The position.
	 * @param msgType
	 *            To distinguish the type of the message. The values are in the
	 *            {@link Constants.MSG_TYPE} class.
	 */
	public AbstractPSenseMsg(byte hopCount, SequenceNumber sequenceNr,
			int radius, Point position, MSG_TYPE msgType) {

		this.hopCount = hopCount;
		this.sequenceNr = sequenceNr;
		this.radius = radius;
		this.position = position;
		this.msgType = msgType;
	}

	/**
	 * Gets the size in bytes of the abstract message. It counts all bytes of
	 * the stored attributes in this class.
	 * 
	 * @return the size in bytes of the abstract message
	 */
	public int getSizeOfAbstractMessage() {

		int size = 0;
		size += Constants.BYTE_SIZE_OF_MSG_TYPE;
		size += Constants.BYTE_SIZE_OF_HOP_COUNT;
		size += Constants.BYTE_SIZE_OF_RADIUS;
		size += Constants.BYTE_SIZE_OF_POINT;
		size += Constants.BYTE_SIZE_OF_SEQ_NR;

		return size;
	}

	/**
	 * Gets the message type back. The defined value for the message types is to
	 * find in {@link Constants.MSG_TYPE}
	 * 
	 * @return the message type
	 */
	public MSG_TYPE getMsgType() {
		return msgType;
	}

	/**
	 * Gets the number of allowing hops back. If the number smaller then 0, then
	 * return 0.
	 * 
	 * @return The number of allowing hops.
	 */
	public byte getHopCount() {
		if (hopCount < 0) {
			return 0;
		}
		return hopCount;
	}

	/**
	 * Gets the radius for this message back
	 * 
	 * @return the radius of this message
	 */
	public int getRadius() {
		return radius;
	}

	/**
	 * Gets the position back
	 * 
	 * @return the position as {@link Point}
	 */
	public Point getPosition() {
		return position;
	}

	/**
	 * Gets the sequence number back
	 * 
	 * @return the sequence number
	 */
	public SequenceNumber getSequenceNr() {
		return sequenceNr;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + hopCount;
		result = prime * result + ((msgType == null) ? 0 : msgType.hashCode());
		result = prime * result
				+ ((position == null) ? 0 : position.hashCode());
		result = prime * result + radius;
		result = prime * result
				+ ((sequenceNr == null) ? 0 : sequenceNr.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		AbstractPSenseMsg other = (AbstractPSenseMsg) obj;
		if (hopCount != other.hopCount) {
			return false;
		}
		if (msgType != other.msgType) {
			return false;
		}
		if (position == null) {
			if (other.position != null) {
				return false;
			}
		} else if (!position.equals(other.position)) {
			return false;
		}
		if (radius != other.radius) {
			return false;
		}
		if (sequenceNr == null) {
			if (other.sequenceNr != null) {
				return false;
			}
		} else if (!sequenceNr.equals(other.sequenceNr)) {
			return false;
		}
		return true;
	}
}
