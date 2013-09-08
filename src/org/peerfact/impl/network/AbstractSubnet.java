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

package org.peerfact.impl.network;

import java.util.LinkedHashMap;
import java.util.Map;

import org.peerfact.api.common.Message;
import org.peerfact.api.network.NetID;
import org.peerfact.api.network.NetLayer;
import org.peerfact.api.network.NetMessage;
import org.peerfact.impl.transport.AbstractTransMessage;

/**
 * The Subnet models the intrinsic complexity of the internet as a "big cloud"
 * which appears to be transparent for the end-systems (hosts). That is, when
 * sending a message using the tcp/udp protocol, the message is given from the
 * sending host to the the subnet and the subnet manages the calculation of
 * transmission times, establishes a tcp connection (if necessary) between the
 * sender and receiver, models the packet loss and jitter and schedules the
 * appropriate events at the simulation framework. It also triggers the arrival
 * of a message at the appropriate receiver by using the
 * org.peerfact.impl.network.AbstractNetLayer#receive(NetMessage) method.
 * 
 * @author Sebastian Kaune <peerfact@kom.tu-darmstadt.de>
 * @author Konstantin Pussep
 * @version 3.0, 11/29/2007
 */
public abstract class AbstractSubnet<Layer extends NetLayer> {

	private int lastCommId = 0;

	protected Map<NetID, Layer> layers;

	/**
	 * This method passes a given NetMessage from the sending host to the
	 * subnet.
	 * 
	 * @param msg
	 *            the message to be send
	 */
	public abstract void send(NetMessage msg);

	/**
	 * Registers a new NetLayer to the subnet.
	 * 
	 * @param net
	 *            the NetLayet to be registered
	 */
	public void registerNetLayer(Layer net) {
		this.layers.put(net.getNetID(), net);
	}

	/**
	 * Gets a net layer of this subnet.
	 * 
	 * @param id
	 *            the net id
	 * @return the net layer
	 */
	public Layer getNetLayer(NetID id) {
		return this.layers.get(id);
	}

	public AbstractSubnet() {
		this.layers = new LinkedHashMap<NetID, Layer>();
	}

	/**
	 * 
	 * @param message
	 * @return
	 */
	public int determineTransMsgNumber(Message message) {
		// Iterate over the nested messages until AbstractTransMessage is
		// reached
		Message tempMessage = message;
		while (!(tempMessage instanceof AbstractTransMessage)) {
			tempMessage = tempMessage.getPayload();
		}

		// Depending on the current commId of transMsg return a new id or the
		// old one
		AbstractTransMessage transMsg = (AbstractTransMessage) tempMessage;
		if (transMsg.getCommId() == -1) {
			return lastCommId++;
		} else {
			return transMsg.getCommId();
		}
	}

}
