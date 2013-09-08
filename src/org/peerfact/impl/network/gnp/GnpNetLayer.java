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

package org.peerfact.impl.network.gnp;

import java.util.LinkedHashMap;
import java.util.Map;

import org.peerfact.api.common.Message;
import org.peerfact.api.common.Monitor.Reason;
import org.peerfact.api.network.Bandwidth;
import org.peerfact.api.network.NetID;
import org.peerfact.api.network.NetMessage;
import org.peerfact.api.network.NetProtocol;
import org.peerfact.api.simengine.SimulationEventHandler;
import org.peerfact.api.transport.TransProtocol;
import org.peerfact.impl.network.AbstractNetLayer;
import org.peerfact.impl.network.IPv4Message;
import org.peerfact.impl.network.IPv4NetID;
import org.peerfact.impl.network.gnp.topology.GnpPosition;
import org.peerfact.impl.simengine.SimulationEvent;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.transport.AbstractTransMessage;

/**
 * 
 * @author geraldklunker <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class GnpNetLayer extends AbstractNetLayer implements
		SimulationEventHandler {

	private GeoLocation geoLocation;

	private long nextFreeSendingTime = 0;

	private long nextFreeReceiveTime = 0;

	private Map<GnpNetLayer, GnpNetBandwidthAllocation> connections = new LinkedHashMap<GnpNetLayer, GnpNetBandwidthAllocation>();

	public GnpNetLayer(GnpSubnet subNet, IPv4NetID netID,
			GnpPosition netPosition, GeoLocation geoLoc, Bandwidth maxBW) {
		super(maxBW, netPosition, subNet);
		this.myID = netID;
		this.online = true;
		this.geoLocation = geoLoc;
		subNet.registerNetLayer(this);
	}

	public GeoLocation getGeoLocation() {
		return geoLocation;
	}

	/**
	 * 
	 * @return 2-digit country code
	 */
	public String getCountryCode() {
		return geoLocation.getCountryCode();
	}

	/**
	 * 
	 * @return first time sending is possible (line is free)
	 */
	public long getNextFreeSendingTime() {
		return nextFreeSendingTime;
	}

	/**
	 * 
	 * @param time
	 *            first time sending is possible (line is free)
	 */
	public void setNextFreeSendingTime(long time) {
		nextFreeSendingTime = time;
	}

	/**
	 * 
	 * @param netLayer
	 * @return
	 */
	public boolean isConnected(GnpNetLayer netLayer) {
		return connections.containsKey(netLayer);
	}

	/**
	 * 
	 * @param netLayer
	 * @param allocation
	 */
	public void addConnection(GnpNetLayer netLayer,
			GnpNetBandwidthAllocation allocation) {
		connections.put(netLayer, allocation);
	}

	/**
	 * 
	 * @param netLayer
	 * @return
	 */
	public GnpNetBandwidthAllocation getConnection(GnpNetLayer netLayer) {
		return connections.get(netLayer);
	}

	/**
	 * 
	 * @param netLayer
	 */
	public void removeConnection(GnpNetLayer netLayer) {
		connections.remove(netLayer);
	}

	/**
	 * 
	 * @param msg
	 */
	public void addToReceiveQueue(IPv4Message msg) {
		((GnpSubnet) subnet).getLatencyModel();
		long receiveTime = GnpLatencyModel.getTransmissionDelay(
				msg.getSize(), getMaxBandwidth().getDownBW());
		long currenTime = Simulator.getCurrentTime();
		long arrivalTime = nextFreeReceiveTime + receiveTime;
		if (arrivalTime <= currenTime) {
			nextFreeReceiveTime = currenTime;
			receive(msg);
		} else {
			nextFreeReceiveTime = arrivalTime;
			Simulator.scheduleEvent(msg, arrivalTime, this,
					SimulationEvent.Type.MESSAGE_RECEIVED);
		}
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
				NetMessage netMsg = new IPv4Message(msg, receiver, this.myID);
				log.debug(Simulator.getSimulatedRealtime() + " Sending "
						+ netMsg);
				Simulator.getMonitor().netMsgEvent(netMsg, myID, Reason.SEND);
				this.subnet.send(netMsg);
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
			NetMessage netMsg = new IPv4Message(msg, receiver, this.myID);
			Simulator.getMonitor().netMsgEvent(netMsg, myID, Reason.DROP);
		}

	}

	@Override
	public String toString() {
		return this.getNetID().toString() + " ( "
				+ this.getHost().getProperties().getGroupID() + " )";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.peerfact.api.simengine.SimulationEventHandler#eventOccurred(
	 * org.peerfact.api.simengine.SimulationEvent)
	 */
	@Override
	public void eventOccurred(SimulationEvent se) {
		if (se.getType() == SimulationEvent.Type.MESSAGE_RECEIVED) {
			receive((NetMessage) se.getData());
		} else if (se.getType() == SimulationEvent.Type.TEST_EVENT) {
			Object[] msgInfo = (Object[]) se.getData();
			send((Message) msgInfo[0], (NetID) msgInfo[1],
					(NetProtocol) msgInfo[2]);
		}

		else if (se.getType() == SimulationEvent.Type.SCENARIO_ACTION
				&& se.getData() == null) {
			goOffline();
		} else if (se.getType() == SimulationEvent.Type.SCENARIO_ACTION) {
			log.error("ERROR" + se.getData());
			cancelTransmission((Integer) se.getData());
		}
	}

	@Override
	public void goOffline() {
		super.goOffline();
		((GnpSubnet) subnet).goOffline(this);
	}

	@Override
	public void cancelTransmission(int commId) {
		((GnpSubnet) subnet).cancelTransmission(commId);
	}

	// for JUnit Test

	public void goOffline(long time) {
		Simulator.scheduleEvent(null, time, this,
				SimulationEvent.Type.SCENARIO_ACTION);
	}

	public void cancelTransmission(int commId, long time) {
		Simulator.scheduleEvent(Integer.valueOf(commId), time, this,
				SimulationEvent.Type.SCENARIO_ACTION);
	}

	public void send(Message msg, NetID receiver, NetProtocol netProtocol,
			long sendTime) {
		Object[] msgInfo = new Object[3];
		msgInfo[0] = msg;
		msgInfo[1] = receiver;
		msgInfo[2] = netProtocol;
		Simulator.scheduleEvent(msgInfo, sendTime, this,
				SimulationEvent.Type.TEST_EVENT);
	}

}
