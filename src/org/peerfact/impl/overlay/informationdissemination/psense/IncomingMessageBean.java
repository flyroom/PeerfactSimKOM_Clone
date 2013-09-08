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

import org.peerfact.impl.overlay.informationdissemination.psense.messages.AbstractPSenseMsg;
import org.peerfact.impl.overlay.informationdissemination.psense.util.SequenceNumber;

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
 * Combine the message and the contact information from the originator of the
 * content of the message.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 09/15/2010
 */
public class IncomingMessageBean {

	/**
	 * The contact information from the originator of the content of this stored
	 * message.
	 */
	private final PSenseContact contact;

	/**
	 * The message that is to store.
	 */
	private final AbstractPSenseMsg msg;

	/**
	 * Constructor of this class, it sets the attributes of this class with the
	 * given parameters.
	 * 
	 * @param contact
	 *            The contact information from the originator of the content of
	 *            this message.
	 * @param msg
	 *            The message that is to store.
	 */
	public IncomingMessageBean(PSenseContact contact, AbstractPSenseMsg msg) {
		this.contact = contact;
		this.msg = msg;
	}

	/**
	 * Gets the contact from the originator of the content of the message
	 * 
	 * @return The contact to the originator of the content of the message
	 */
	public PSenseContact getContact() {
		return contact;
	}

	/**
	 * Gets the message, that is stored in this bean
	 * 
	 * @return The message, that is stored in this bean
	 */
	public AbstractPSenseMsg getMessage() {
		return msg;
	}

	/**
	 * Gets the sequence number of the stored message ({@link #msg})
	 * 
	 * @return The sequence number of the stored message
	 */
	public SequenceNumber getSeqNr() {
		return msg.getSequenceNr();
	}

	@Override
	public String toString() {
		return "[ " + contact.toString() + ", " + msg.toString() + " ]";
	}
}
