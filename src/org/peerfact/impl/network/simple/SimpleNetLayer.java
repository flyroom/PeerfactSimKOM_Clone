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

package org.peerfact.impl.network.simple;

import org.peerfact.api.common.Message;
import org.peerfact.api.common.Monitor.Reason;
import org.peerfact.api.network.Bandwidth;
import org.peerfact.api.network.NetID;
import org.peerfact.api.network.NetMessage;
import org.peerfact.api.network.NetPosition;
import org.peerfact.api.network.NetProtocol;
import org.peerfact.api.transport.TransProtocol;
import org.peerfact.impl.network.AbstractNetLayer;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.transport.AbstractTransMessage;

/**
 * 
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public class SimpleNetLayer extends AbstractNetLayer {

	private double currentDownBandwidth;

	private double currentUpBandwidth;

	public SimpleNetLayer(SimpleSubnet subNet, SimpleNetID netID,
			NetPosition netPosition, Bandwidth bandwidth) {
		super(bandwidth, netPosition, subNet);
		this.myID = netID;
		this.online = true;
		subNet.registerNetLayer(this);
	}

	@Override
	public boolean isSupported(TransProtocol transProtocol) {
		return (transProtocol.equals(TransProtocol.UDP) || transProtocol
				.equals(TransProtocol.TCP));
	}

	@Override
	public void send(Message msg, NetID receiver, NetProtocol netProtocol) {
		// outer if-else-block is used to avoid sending although the host is
		// offline
		if (this.isOnline()) {
			TransProtocol usedTransProtocol = ((AbstractTransMessage) msg)
					.getProtocol();
			if (this.isSupported(usedTransProtocol)) {
				NetMessage netMsg = new SimpleNetMessage(msg, receiver, myID,
						netProtocol);
				Simulator.getMonitor().netMsgEvent(netMsg, myID, Reason.SEND);
				subnet.send(netMsg);
			} else {
				throw new IllegalArgumentException("Transport protocol "
						+ usedTransProtocol
						+ " not supported by this NetLayer implementation.");
			}
		} else {
			int assignedMsgId = subnet.determineTransMsgNumber(msg);
			log.debug("During send: Assigning MsgId " + assignedMsgId
					+ " to dropped message");
			((AbstractTransMessage) msg).setCommId(assignedMsgId);
			NetMessage netMsg = new SimpleNetMessage(msg, receiver, myID,
					netProtocol);
			Simulator.getMonitor().netMsgEvent(netMsg, myID, Reason.DROP);
		}

	}

	@Override
	public String toString() {
		return "NetLayer(netID=" + myID + ", "
				+ (online ? "online" : "offline") + ")";
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(currentDownBandwidth);
		result = PRIME * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(currentUpBandwidth);
		result = PRIME * result + (int) (temp ^ (temp >>> 32));
		result = PRIME * result + ((subnet == null) ? 0 : subnet.hashCode());
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
		final SimpleNetLayer other = (SimpleNetLayer) obj;
		if (Double.doubleToLongBits(currentDownBandwidth) != Double
				.doubleToLongBits(other.currentDownBandwidth)) {
			return false;
		}
		if (Double.doubleToLongBits(currentUpBandwidth) != Double
				.doubleToLongBits(other.currentUpBandwidth)) {
			return false;
		}
		if (subnet == null) {
			if (other.subnet != null) {
				return false;
			}
		} else if (!subnet.equals(other.subnet)) {
			return false;
		}
		return true;
	}

	@Override
	public void cancelTransmission(int commId) {
		throw new UnsupportedOperationException();
	}

}
