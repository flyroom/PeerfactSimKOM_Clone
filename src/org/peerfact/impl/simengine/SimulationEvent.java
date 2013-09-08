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

package org.peerfact.impl.simengine;

import java.util.EventObject;

/**
 * Each simulation event contains the time at which it should occur and a
 * reference to a SimulationEventHandler that will be informed about the
 * occurrence at that time.
 * 
 * @author Sebastian Kaune <peerfact@kom.tu-darmstadt.de>
 * @author Konstantin Pussep
 * @version 3.0, 11/29/2007
 * 
 */
public class SimulationEvent extends EventObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5513337629653875368L;

	/**
	 * The specific event type
	 */
	public enum Type {
		/**
		 * Event for operation processing.
		 */
		OPERATION_EXECUTE,
		/**
		 * Used to delivered messages to a remote NetLayer
		 */
		MESSAGE_RECEIVED,
		/**
		 * The message has left the send queue at the receiver and is in the
		 * subnet
		 */
		MESSAGE_AT_SUBNET,
		/**
		 * The message has arrived in the queue of the receiver's network layer
		 */
		MESSAGE_AT_RCV_Q,
		/**
		 * Simulation start token
		 */
		START_SIMULATION,
		/**
		 * Simulation stop token
		 */
		END_SIMULATION,
		/**
		 * Used to realize timeouts such as operation or message timeouts
		 */
		TIMEOUT_EXPIRED,
		/**
		 * Event is part of a scenario (Scenario is defined in config file)
		 */
		SCENARIO_ACTION,
		/**
		 * Used to print status information about the simulation state
		 */
		STATUS,
		/**
		 * Triggers online/offline behavior at the network layer
		 */
		CHURN_EVENT,
		/**
		 * Churn start token
		 */
		CHURN_START,
		/**
		 * Churn stop token
		 */
		CHURN_STOP,
		/**
		 * Triggers isolation behavior at the network layer
		 */
		ISOLATION_EVENT,
		/**
		 * Isolation start token
		 */
		ISOLATION_START,
		/**
		 * Isolation stop token
		 */
		ISOLATION_STOP,
		/**
		 * Monitor start token
		 */
		MONITOR_START,
		/**
		 * Monitor stop token
		 */
		MONITOR_STOP,
		/**
		 * Analyze event token
		 */
		ANALYZE_EVENT,
		/**
		 * For JUnit tests
		 */
		TEST_EVENT;
	}

	private final Type type;

	private final Object eventData;

	protected long simTime;

	/**
	 * Construct SimulationEvent
	 * 
	 * @param type
	 *            Type of Event
	 * @param eventData
	 *            The Event to schedule
	 * @param simTime
	 *            When should the event processed.
	 * @param simulator
	 *            The object on which the Event initially occurs
	 */
	public SimulationEvent(Type type, Object eventData, long simTime,
			Object simulator) {
		super(simulator);
		this.type = type;
		this.eventData = eventData;
		this.simTime = simTime;
	}

	/**
	 * Returns the event data
	 * 
	 * @return the event data
	 */
	public Object getData() {
		return eventData;
	}

	/**
	 * Returns the time when the even will be processed
	 * 
	 * @return time when the event will be processed
	 */
	public long getSimulationTime() {
		return simTime;
	}

	/**
	 * Returns the type of event
	 * 
	 * @return type of event
	 */
	public Type getType() {
		return type;
	}

}
