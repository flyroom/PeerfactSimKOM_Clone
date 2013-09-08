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

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.peerfact.api.network.NetID;
import org.peerfact.api.network.NetLatencyModel;
import org.peerfact.api.network.NetLayer;
import org.peerfact.api.network.NetMessage;
import org.peerfact.api.simengine.SimulationEventHandler;
import org.peerfact.impl.network.AbstractSubnet;
import org.peerfact.impl.simengine.SimulationEvent;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.transport.AbstractTransMessage;
import org.peerfact.impl.util.logging.SimLogger;

/**
 * The default implementation of the SubNet interface.
 * 
 * @author Sebastian Kaune <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 */
public class SimpleSubnet extends AbstractSubnet<SimpleNetLayer> implements
		SimulationEventHandler {
	private static Logger log = SimLogger.getLogger(SimpleSubnet.class);

	NetLatencyModel netLatencyModel;

	public static final double SUBNET_WIDTH = 2d;

	public static final double SUBNET_HEIGHT = 2d;

	public static final long inOrderOffset = 1;

	Map<SimpleSubnet.LinkID, Long> links = new LinkedHashMap<SimpleSubnet.LinkID, Long>();

	public SimpleSubnet() {
	}

	@Override
	public void send(NetMessage msg) {
		// In order to enable TransMessageCallbacks at the transport layer on
		// every implemented network layer and subnet, AbstractSubnet contains
		// a method, which determines the next number for a message at the
		// transport layer.
		AbstractTransMessage transMsg = (AbstractTransMessage) msg.getPayload();
		transMsg.setCommId(determineTransMsgNumber(msg));
		NetLayer sender = layers.get(msg.getSender());
		NetLayer receiver = layers.get(msg.getReceiver());
		long latency = netLatencyModel.getLatency(sender, receiver);
		log.debug("Send from " + sender + " to " + receiver + " with delay "
				+ latency);
		scheduleReceiveEvent(msg, receiver, latency);
	}

	void scheduleReceiveEvent(NetMessage msg, NetLayer receiver, long latency) {
		LinkID link = new LinkID(msg.getSender(), msg.getReceiver());
		long lastArrivalTime = getLastArrivalTime(link);

		long newArrivalTime = Simulator.getCurrentTime() + latency;
		log.debug("new arrival time = " + newArrivalTime + " lastArrivalTime="
				+ lastArrivalTime);
		if (lastArrivalTime > newArrivalTime) { // assure ordered delivery
			newArrivalTime = lastArrivalTime + inOrderOffset;
			log.debug("arrival time adjusted to " + newArrivalTime);
		}
		links.put(link, newArrivalTime);

		Simulator.scheduleEvent(msg, newArrivalTime, this,
				SimulationEvent.Type.MESSAGE_RECEIVED);
	}

	long getLastArrivalTime(LinkID link) {
		long lastArrivalTime = (links.containsKey(link)) ? links.get(link) : -1;
		return lastArrivalTime;
	}

	static class LinkID {
		private NetID srcId;

		private NetID dstId;

		public LinkID(NetID srcId, NetID dstId) {
			super();
			this.srcId = srcId;
			this.dstId = dstId;
		}

		@Override
		public boolean equals(Object obj) {
			if (!LinkID.class.isInstance(obj)) {
				return false;
			}
			LinkID id2 = (LinkID) obj;
			return srcId.equals(id2.srcId) && dstId.equals(id2.dstId);
		}

		@Override
		public int hashCode() {
			int hCode = 17;
			hCode += (37 * srcId.hashCode());
			hCode += (37 * dstId.hashCode());
			return hCode;
		}

	}

	public void clear() {
		layers.clear();
		// NetworkLink.clearPool();
		links.clear();
		// singleton = new SimpleSubnet();
	}

	public void setLatencyModel(NetLatencyModel model) {
		this.netLatencyModel = model;
	}

	/**
	 * Dispatch the arriving message to its destination.
	 * 
	 * @param se
	 *            event containing the network message
	 */
	@Override
	public void eventOccurred(SimulationEvent se) {
		SimpleNetMessage msg = (SimpleNetMessage) se.getData();
		NetID senderID = msg.getSender();
		NetID receiverID = msg.getReceiver();
		NetLayer receiver = layers.get(receiverID);
		LinkID linkID = new LinkID(senderID, receiverID);
		long lastArrivalTime = getLastArrivalTime(linkID);
		if (lastArrivalTime == se.getSimulationTime()) {
			links.remove(linkID);
			log.debug("Remove obsolete link " + linkID);
			assert !links.containsKey(linkID);
		}
		((SimpleNetLayer) receiver).receive(msg);
	}

}
