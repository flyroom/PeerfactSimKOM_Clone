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

package org.peerfact.impl.overlay.informationdissemination.psense;

import java.util.List;

import org.peerfact.impl.overlay.informationdissemination.psense.messages.AbstractPSenseMsg;
import org.peerfact.impl.overlay.informationdissemination.psense.messages.ForwardMsg;
import org.peerfact.impl.overlay.informationdissemination.psense.messages.PositionUpdateMsg;
import org.peerfact.impl.overlay.informationdissemination.psense.messages.SensorRequestMsg;
import org.peerfact.impl.overlay.informationdissemination.psense.messages.SensorResponseMsg;


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
 * Combine the message, the receiverList and the contact information to the
 * receiver of the message. <br>
 * The given receiverList should have the same Pointer as the List in message.
 * For {@link SensorRequestMsg} and {@link SensorResponseMsg}, should this
 * <code>null</code> or an empty list. This has the advantage, that a message
 * must be delete, that the receiver stay up to date.
 * 
 * @author Christoph Münker <peerfact@kom.tu-darmstadt.de>
 * @version 09/15/2010
 */
public class OutgoingMessageBean {

	/**
	 * The contact information to the receiver of the content of this stored
	 * message.
	 */
	private final PSenseContact contact;

	/**
	 * List of receivers to this message
	 */
	private final List<PSenseID> receivers;

	/**
	 * The message that is to store.
	 */
	private final AbstractPSenseMsg msg;

	/**
	 * Constructor of this class, it sets the attributes of this class with the
	 * given parameters.
	 * 
	 * @param contact
	 *            The contact information from the receiver of the content of
	 *            this message.
	 * @param receivers
	 *            A list of the receivers of this message. (only used for
	 *            {@link PositionUpdateMsg}s and {@link ForwardMsg}s. For other
	 *            message should be used <code>null</code> or an empty list.
	 * @param msg
	 *            The message that is to store.
	 */
	public OutgoingMessageBean(PSenseContact contact, List<PSenseID> receivers,
			AbstractPSenseMsg msg) {
		this.contact = contact;
		this.msg = msg;
		this.receivers = receivers;
	}

	/**
	 * Gets the contact from the receiver of the content of the message
	 * 
	 * @return The contact to the receiver of the content of the message
	 */
	public PSenseContact getContact() {
		return contact;
	}

	/**
	 * Gets a list of receivers of this message
	 * 
	 * @return list of receivers of this message
	 */
	public List<PSenseID> getReceivers() {
		return receivers;
	}

	/**
	 * Gets the message, that is stored in this bean
	 * 
	 * @return The message, that is stored in this bean
	 */
	public AbstractPSenseMsg getMessage() {
		return msg;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((contact == null) ? 0 : contact.hashCode());
		result = prime * result + ((msg == null) ? 0 : msg.hashCode());
		result = prime * result
				+ ((receivers == null) ? 0 : receivers.hashCode());
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
		OutgoingMessageBean other = (OutgoingMessageBean) obj;
		if (contact == null) {
			if (other.contact != null) {
				return false;
			}
		} else if (!contact.equals(other.contact)) {
			return false;
		}
		if (msg == null) {
			if (other.msg != null) {
				return false;
			}
		} else if (!msg.equals(other.msg)) {
			return false;
		}
		if (receivers == null) {
			if (other.receivers != null) {
				return false;
			}
		} else if (!receivers.equals(other.receivers)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuffer temp = new StringBuffer();
		temp.append("[ contact: ");
		temp.append(getContact());
		temp.append(", msg: ");
		temp.append(getMessage());
		temp.append(", receivers: ");
		temp.append(getReceivers());
		temp.append(" ]");
		return temp.toString();
	}
}
