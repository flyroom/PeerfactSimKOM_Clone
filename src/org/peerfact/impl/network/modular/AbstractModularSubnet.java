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

package org.peerfact.impl.network.modular;

import org.apache.log4j.Logger;
import org.peerfact.api.common.Monitor.Reason;
import org.peerfact.api.network.NetID;
import org.peerfact.api.network.NetLayer;
import org.peerfact.api.network.NetMessage;
import org.peerfact.api.simengine.SimulationEventHandler;
import org.peerfact.impl.network.AbstractSubnet;
import org.peerfact.impl.network.modular.db.NetMeasurementDB;
import org.peerfact.impl.network.modular.livemon.NetLayerLiveMonitoring;
import org.peerfact.impl.simengine.SimulationEvent;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.transport.AbstractTransMessage;
import org.peerfact.impl.util.BackToXMLWritable;
import org.peerfact.impl.util.logging.SimLogger;

/**
 * Base class for Subnets within the modular Net-Layer. This allows for
 * different subnet implementations if a subnet should not make the "big cloud"
 * assumption but instead rely on some kind of topology and routing.
 * 
 * @author Leo Nobach, modified by Bjoern Richerzhagen (v 1.1)
 *         <peerfact@kom.tu-darmstadt.de>
 * @version 1.1, 04/13/2011
 */
public abstract class AbstractModularSubnet extends
		AbstractSubnet<ModularNetLayer> implements
		BackToXMLWritable {

	private static Logger log = SimLogger
			.getLogger(AbstractModularSubnet.class);

	/**
	 * Used MeasurementDB for Latency, Jitter, etc.
	 */
	private NetMeasurementDB db;

	/**
	 * Used strategies
	 */
	private IStrategies strategies;

	/**
	 * Whether to deliver messages in the right order or not
	 */
	private boolean inOrderDelivery = false;

	/**
	 * Needed for creation of a subnet from config.xml, strategies should be
	 * provided via setStrategies
	 */
	public AbstractModularSubnet() {
		// nothing to do here
	}

	/**
	 * Create a new Subnet with the provided strategies
	 * 
	 * @param strategies
	 */
	public AbstractModularSubnet(IStrategies strategies) {
		this();
		setStrategies(strategies);
	}

	/**
	 * Returns true if a connection between Sender and Receiver is possible
	 * (this might not be the case in some adHoc-Routing-Situations)
	 * 
	 * @param nlSender
	 * @param nlReceiver
	 * @return
	 */
	protected abstract boolean isConnectionPossible(ModularNetLayer nlSender,
			ModularNetLayer nlReceiver);

	@Override
	public void send(NetMessage message) {
		final ModularNetMessage msg = (ModularNetMessage) message;

		// At this point, the network message may only contain a UDP transport
		// message,
		// this is asserted by the network layer calling this method.

		final ModularNetLayer nlSender = getNetLayer(msg.getSender());
		final ModularNetLayer nlReceiver = getNetLayer(msg.getReceiver());

		if (nlSender == null) {
			throw new AssertionError("Network layer of the sender ("
					+ msg.getSender()
					+ ") of the message is not in the modular subnet.");
		}
		if (nlReceiver == null) {
			throw new AssertionError("Network layer of the receiver ("
					+ msg.getReceiver()
					+ ") of the message is not in the modular sub net.");
		}

		/*
		 * In some routed subnets it may be possible that a message can not
		 * reach its destination
		 */
		boolean connectionIsPossible = isConnectionPossible(nlSender,
				nlReceiver);
		if (!connectionIsPossible) {
			int assignedMsgId = determineTransMsgNumber(msg);
			((AbstractTransMessage) msg.getPayload()).setCommId(assignedMsgId);
			log.debug("Dropping message "
					+ msg
					+ ", because due to subnet routing there is no connection possible.");
			NetLayerLiveMonitoring.getSubnetMsgDrop().droppedMessage();
			Simulator.getMonitor().netMsgEvent(msg, msg.getSender(),
					Reason.DROP);
			return;
		}

		boolean shallBeDropped = getStrategies().getPLossStrategy().shallDrop(
				msg, nlSender, nlReceiver, getDB());

		if (shallBeDropped) {
			int assignedMsgId = determineTransMsgNumber(msg);
			((AbstractTransMessage) msg.getPayload()).setCommId(assignedMsgId);
			log.debug("Dropping message " + msg
					+ ", because of the packet loss strategy that is used.");
			NetLayerLiveMonitoring.getSubnetMsgDrop().droppedMessage();
			Simulator.getMonitor().netMsgEvent(msg, msg.getSender(),
					Reason.DROP);
			return;
		} else {
			NetLayerLiveMonitoring.getSubnetMsgDrop().noDropMessage();
		}

		long delay = getStrategies().getLatencyStrategy()
				.getMessagePropagationDelay(msg, nlSender, nlReceiver, getDB());
		long jitter = getStrategies().getJitterStrategy().getJitter(delay, msg,
				nlSender, nlReceiver, getDB());

		long rcvTime;
		if (getUseInOrderDelivery()) {
			rcvTime = Math.max(nlReceiver.getLastSchRcvTime() + 1,
					Simulator.getCurrentTime() + delay + jitter);
			nlReceiver.setLastSchRcvTime(rcvTime);
		} else {
			rcvTime = Simulator.getCurrentTime() + delay + jitter;
		}

		log.debug("Delaying message " + message + ": delay=" + delay
				+ ", jitter=" + jitter);

		Simulator.scheduleEvent(null, rcvTime, new SimulationEventHandler() {

			@Override
			public void eventOccurred(SimulationEvent se) {
				nlReceiver.receive(msg, nlSender);
			}

		}, SimulationEvent.Type.MESSAGE_RECEIVED);
	}

	/**
	 * Load a set of strategies for this subnet
	 * 
	 * @param strategies
	 */
	public void setStrategies(IStrategies strategies) {
		this.strategies = strategies;
	}

	/**
	 * Called if a NetLayer goes online.
	 * 
	 * @param net
	 */
	protected abstract void netLayerWentOnline(NetLayer net);

	/**
	 * Called if a NetLayer goes offline.
	 * 
	 * @param net
	 */
	protected abstract void netLayerWentOffline(NetLayer net);

	/**
	 * Set Measurement-DB
	 * 
	 * @param db
	 */
	public void setDB(NetMeasurementDB db) {
		this.db = db;
	}

	/**
	 * Get Measurement-DB
	 * 
	 * @return
	 */
	public NetMeasurementDB getDB() {
		return db;
	}

	/**
	 * get the NetLayer of a given NetID
	 * 
	 * @param id
	 * @return
	 */
	public ModularNetLayer getNetLayer(NetID id) {
		return layers.get(id);
	}

	/**
	 * Get used Strategies
	 * 
	 * @return
	 */
	public IStrategies getStrategies() {
		return strategies;
	}

	/**
	 * Deliver messages in Order, if true
	 * 
	 * @param useInOrderDelivery
	 */
	public void setUseInOrderDelivery(boolean useInOrderDelivery) {
		this.inOrderDelivery = useInOrderDelivery;
	}

	/**
	 * Are messages delivered in order?
	 * 
	 * @return
	 */
	public boolean getUseInOrderDelivery() {
		return this.inOrderDelivery;
	}

}
