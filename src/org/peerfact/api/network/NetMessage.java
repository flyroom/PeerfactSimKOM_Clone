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

package org.peerfact.api.network;

import org.peerfact.api.common.Message;

/**
 * NetMessages are used to realize the communication between two NetLayers and
 * encapsulate the necessary information such as the used network protocol and
 * the NetID of the sender and receiver of a given message.
 * 
 * @author Sebastian Kaune <peerfact@kom.tu-darmstadt.de>
 * @author Konstantin Pussep
 * @version 3.0, 11/29/2007
 * 
 */
public interface NetMessage extends Message {

	/**
	 * Returns the NetID of the sender of a NetMessage
	 * 
	 * @return The NetID of the sender.
	 */
	public NetID getSender();

	/**
	 * Returns the NetID of the receiver of a NetMessage
	 * 
	 * @return The NetID of the receiver.
	 */
	public NetID getReceiver();

	/**
	 * Returns the network protocol used to send this message
	 * 
	 * @return The network protocol used to send this message
	 */
	public NetProtocol getNetProtocol();
}
