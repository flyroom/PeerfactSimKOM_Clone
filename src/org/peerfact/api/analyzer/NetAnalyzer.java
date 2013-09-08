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

package org.peerfact.api.analyzer;

import org.peerfact.api.network.NetID;
import org.peerfact.api.network.NetMessage;

/**
 * NetAnalyzers receive notifications when a network message is send,
 * received or dropped at the network layer.
 * 
 */
public interface NetAnalyzer extends Analyzer {
	/**
	 * Invoking this method denotes that the given network message is sent
	 * at the network layer with the given NetID
	 * 
	 * @param msg
	 *            the message which is send out
	 * @param id
	 *            the NetID of the sender of the given message
	 */
	public void netMsgSend(NetMessage msg, NetID id);

	/**
	 * Invoking this method denotes that the given network message is
	 * received at the network layer with the given NetID
	 * 
	 * @param msg
	 *            the received message
	 * @param id
	 *            the NetID of the receiver of the given message
	 */
	public void netMsgReceive(NetMessage msg, NetID id);

	/**
	 * Invoking this method denotes that the given network message is
	 * dropped at the network layer with the given NetID (due to packet loss
	 * or the receiving network layer has no physical connectivity).
	 * 
	 * @param msg
	 *            the dropped message
	 * @param id
	 *            the NetID of the receiver at which the message is droped
	 */
	public void netMsgDrop(NetMessage msg, NetID id);
}