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

package org.peerfact.impl.overlay.dht.pastry.messages;

import org.peerfact.api.common.Message;
import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.impl.overlay.dht.pastry.components.PastryID;

/**
 * This class is used as a container to hold information about messages that are
 * not yet acknowledged and might be retransmitted.
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class MsgTransInfo<T extends OverlayContact<PastryID>> {

	private Message msg;

	private TransInfo receiverTranInfo;

	private T olContact;

	private int retransmissions = 0;

	/**
	 * @param msg
	 *            the message send
	 * @param receiverContact
	 *            the contact the message is send to
	 */
	public MsgTransInfo(Message msg, T receiverContact) {
		this.msg = msg;
		this.receiverTranInfo = receiverContact.getTransInfo();
		this.olContact = receiverContact;
	}

	/**
	 * @return the message send
	 */
	public Message getMsg() {
		return msg;
	}

	/**
	 * @return the transport info the message is send to
	 */
	public TransInfo getReceiverTranInfo() {
		return receiverTranInfo;
	}

	/**
	 * @return the overlay contact of the receiver of the message
	 */
	public T getOlContact() {
		return olContact;
	}

	/**
	 * @return the number of retransmissions
	 */
	public int getRetransmissions() {
		return retransmissions;
	}

	/**
	 * Increment the counter of retransmissions by one
	 */
	public void incRetransmissions() {
		retransmissions++;
	}
}
