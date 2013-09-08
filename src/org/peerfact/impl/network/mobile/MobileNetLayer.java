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

package org.peerfact.impl.network.mobile;

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
public class MobileNetLayer extends AbstractNetLayer {

	private double currentDownBandwidth;

	private double currentUpBandwidth;

	private final MobileMovementManager mv;

	public MobileNetLayer(MobileSubnet subNet, MobileNetID netID,
			MobileMovementManager mv, NetPosition netPosition, Bandwidth bw) {
		super(bw, netPosition, subNet);
		this.mv = mv;
		this.myID = netID;
		this.online = true;
		subNet.registerNetLayer(this);
	}

	@Override
	public boolean isSupported(TransProtocol transProtocol) {
		return TransProtocol.UDP.equals(transProtocol);
	}

	@Override
	public void send(Message msg, NetID receiver, NetProtocol netProtocol) {
		TransProtocol usedTransProtocol = ((AbstractTransMessage) msg)
				.getProtocol();
		if (this.isSupported(usedTransProtocol)) {
			NetMessage netMsg = new MobileNetMessage(msg, receiver, myID,
					netProtocol);
			Simulator.getMonitor().netMsgEvent(netMsg, myID, Reason.SEND);
			subnet.send(netMsg);
		} else {
			throw new IllegalArgumentException("Transport protocol "
					+ usedTransProtocol
					+ " not supported by this NetLayer implementation.");
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
		final MobileNetLayer other = (MobileNetLayer) obj;
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

	public MobileMovementManager getMv() {
		return mv;
	}

}
