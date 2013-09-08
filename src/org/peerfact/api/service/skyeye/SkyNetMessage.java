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

package org.peerfact.api.service.skyeye;

import org.peerfact.api.common.Message;

/**
 * This interface defines the functionality, which a message of SkyNet must
 * have. All messages which are used by a SkyNet-node to transport information
 * must implement this interface.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 15.11.2008
 * 
 */
public interface SkyNetMessage extends Message {

	/**
	 * This method returns the point in time, when this message was initialized.
	 * 
	 * @return the point in time
	 */
	public long getTimestamp();

	/**
	 * This method returns the ID of the sender of this message.
	 * 
	 * @return the ID of the sender
	 */
	public SkyNetNodeInfo getSenderNodeInfo();

	/**
	 * This method returns the ID of the receiver of this message.
	 * 
	 * @return the ID of the receiver
	 */
	public SkyNetNodeInfo getReceiverNodeInfo();

	/**
	 * This method returns the ID of the message.
	 * 
	 * @return the ID of the message
	 */
	public long getSkyNetMsgID();

	/**
	 * This methods determines, if the current message is an ACK for a
	 * requesting-message, or if it is the requesting-message itself.
	 * 
	 * @return <code>true</code>, if this message is an ACK a
	 *         requesting-message.
	 */
	public boolean isACK();

	/**
	 * This method determines, if the receiver of this message is a Support
	 * Peer.
	 * 
	 * @return <code>true</code>, if the receiver of this message is a Support
	 *         Peer, <code>false</code> otherwise
	 */
	public boolean isReceiverSP();

	/**
	 * This method determines, if the sender of this message is a Support Peer.
	 * 
	 * @return <code>true</code>, if the sender of this message is a Support
	 *         Peer, <code>false</code> otherwise
	 */
	public boolean isSenderSP();
}
