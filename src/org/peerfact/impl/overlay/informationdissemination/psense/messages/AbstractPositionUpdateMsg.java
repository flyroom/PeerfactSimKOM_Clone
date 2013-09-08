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
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.peerfact.impl.overlay.informationdissemination.psense.PSenseID;
import org.peerfact.impl.overlay.informationdissemination.psense.util.Constants;
import org.peerfact.impl.overlay.informationdissemination.psense.util.SequenceNumber;
import org.peerfact.impl.overlay.informationdissemination.psense.util.Constants.MSG_TYPE;
import org.peerfact.impl.util.logging.SimLogger;


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
 * That class abstracts the message of the a position update. It adds only one
 * attribute {@link #receiversList} to the {@link AbstractPSenseMsg}. This
 * attribute is used for a normal position update message and a forwarded
 * position update.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 09/15/2010
 */
public abstract class AbstractPositionUpdateMsg extends AbstractPSenseMsg {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1160649491859943426L;

	/**
	 * Logger for this class
	 */
	final static Logger log = SimLogger.getLogger(AbstractPSenseMsg.class);

	/**
	 * A list of receivers, which has get the information of this message
	 */
	private List<PSenseID> receiversList;

	/**
	 * Constructor of the abstract message. It sets the attributes of this
	 * class.
	 * 
	 * @param hopCount
	 *            The number of allowing hops in the overlay for this message.
	 * @param sequenceNr
	 *            A consecutively number to distinguish old and new messages.
	 * @param receiversList
	 *            A list of receivers, which gets the message.
	 * @param visionRangeRadius
	 *            The vision range radius.
	 * @param position
	 *            The position.
	 * @param msgType
	 *            To distinguish the type of the message. The values are in the
	 *            {@link Constants} class and start with <code>MSG_*</code>.
	 */
	public AbstractPositionUpdateMsg(byte hopCount, SequenceNumber sequenceNr,
			List<PSenseID> receiversList, int visionRangeRadius,
			Point position, MSG_TYPE msgType) {
		super(hopCount, sequenceNr, visionRangeRadius, position, msgType);

		this.receiversList = receiversList;
	}

	@Override
	public int getSizeOfAbstractMessage() {
		int size = 0;
		size += super.getSizeOfAbstractMessage();
		if (receiversList != null && receiversList.size() > 0) {
			// the size of the list represents the number of receivers
			// all receivers has the same TransmissionSize.
			size += receiversList.size()
					* receiversList.get(0).getTransmissionSize();
		}
		return size;
	}

	/**
	 * Gets a copy of list of receivers back.
	 * 
	 * @return A list of receivers. If {@link #receiversList} <code>null</code>,
	 *         then return a empty List.
	 */
	public List<PSenseID> getReceiversList() {
		List<PSenseID> copyReceiversList = new Vector<PSenseID>();
		if (receiversList != null) {
			copyReceiversList.addAll(receiversList);
		}
		return copyReceiversList;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AbstractPositionUpdateMsg) {
			AbstractPositionUpdateMsg o = (AbstractPositionUpdateMsg) obj;

			return super.equals(o)
					&& (this.receiversList == o.receiversList || (this.receiversList != null && this.receiversList
							.equals(o.receiversList)));
		}
		return false;
	}
}
