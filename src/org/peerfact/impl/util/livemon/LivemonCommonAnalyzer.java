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

package org.peerfact.impl.util.livemon;

import java.io.Writer;

import org.peerfact.api.analyzer.NetAnalyzer;
import org.peerfact.api.analyzer.OperationAnalyzer;
import org.peerfact.api.common.Operation;
import org.peerfact.api.network.NetID;
import org.peerfact.api.network.NetMessage;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.util.LiveMonitoring;
import org.peerfact.impl.util.LiveMonitoring.ProgressValue;


/**
 * 
 * @author <info@peerfact.org>
 * @author Kalman Graffi <info@peerfact.org>
 * @version 08/18/2011
 * 
 */
public class LivemonCommonAnalyzer implements NetAnalyzer, OperationAnalyzer {

	int messagesIn = 0;

	int messagesOut = 0;

	int operationsInitiated = 0;

	int operationsFinished = 0;

	int netMsgsDropped = 0;

	int netMsgsReceived = 0;

	int netMsgsSent = 0;

	public LivemonCommonAnalyzer() {

		LiveMonitoring.addProgressValue(new SimulationTime());
		// view.addProgressValue(new MessagesIn());
		// view.addProgressValue(new MessagesOut());
		LiveMonitoring.addProgressValue(new OperationsInitiated());
		LiveMonitoring.addProgressValue(new OperationsFinished());
		LiveMonitoring.addProgressValue(new NetMsgsDropped());
		LiveMonitoring.addProgressValue(new NetMsgsReceived());
		LiveMonitoring.addProgressValue(new NetMsgsSent());
		LiveMonitoring.addProgressValue(new MemoryConsumption());

	}

	@Override
	public void operationFinished(Operation<?> op) {
		operationsFinished++;
	}

	@Override
	public void operationInitiated(Operation<?> op) {
		operationsInitiated++;
	}

	@Override
	public void netMsgDrop(NetMessage msg, NetID id) {
		netMsgsDropped++;
	}

	@Override
	public void netMsgReceive(NetMessage msg, NetID id) {
		netMsgsReceived++;
	}

	@Override
	public void netMsgSend(NetMessage msg, NetID id) {
		netMsgsSent++;
	}

	public static class SimulationTime implements ProgressValue {

		@Override
		public String getName() {
			return "Simulation time";
		}

		@Override
		public String getValue() {
			long time = Simulator.getCurrentTime();
			return String.valueOf(Simulator.getFormattedTime(time));
		}

	}

	public class MessagesIn implements ProgressValue {

		@Override
		public String getName() {
			return "Received messages";
		}

		@Override
		public String getValue() {
			return String.valueOf(messagesIn);
		}

	}

	public class MessagesOut implements ProgressValue {

		@Override
		public String getName() {
			return "Sent messages";
		}

		@Override
		public String getValue() {
			return String.valueOf(messagesOut);
		}

	}

	public class OperationsInitiated implements ProgressValue {

		@Override
		public String getName() {
			return "Started operations";
		}

		@Override
		public String getValue() {
			return String.valueOf(operationsInitiated);
		}

	}

	public class OperationsFinished implements ProgressValue {

		@Override
		public String getName() {
			return "Finished operations";
		}

		@Override
		public String getValue() {
			return String.valueOf(operationsFinished);
		}

	}

	public class NetMsgsDropped implements ProgressValue {

		@Override
		public String getName() {
			return "Lost packets";
		}

		@Override
		public String getValue() {
			return String.valueOf(netMsgsDropped);
		}

	}

	public class NetMsgsReceived implements ProgressValue {

		@Override
		public String getName() {
			return "Received packets";
		}

		@Override
		public String getValue() {
			return String.valueOf(netMsgsReceived);
		}

	}

	public class NetMsgsSent implements ProgressValue {

		@Override
		public String getName() {
			return "Sent packets";
		}

		@Override
		public String getValue() {
			return String.valueOf(netMsgsSent);
		}

	}

	public static class MemoryConsumption implements ProgressValue {

		@Override
		public String getName() {
			return "Memory consumption";
		}

		@Override
		public String getValue() {

			long total = Runtime.getRuntime().totalMemory();

			return getMemStr(total - Runtime.getRuntime().freeMemory())
					+ " of " + getMemStr(total)
					+ ", max. " + getMemStr(Runtime.getRuntime().maxMemory());
		}

		public static String getMemStr(long mem) {
			return Math.floor(mem / 1048576) + " MB";
		}

	}

	@Override
	public void start() {
		// Nothing to do
	}

	@Override
	public void stop(Writer output) {
		// Nothing to do
	}

}
