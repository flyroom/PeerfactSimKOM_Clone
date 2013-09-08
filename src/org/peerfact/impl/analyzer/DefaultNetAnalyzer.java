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

package org.peerfact.impl.analyzer;

import org.peerfact.api.analyzer.NetAnalyzer;
import org.peerfact.api.network.NetID;
import org.peerfact.api.network.NetMessage;
import org.peerfact.impl.analyzer.metric.CounterMetric;
import org.peerfact.impl.analyzer.metric.SumMetric;
import org.peerfact.impl.analyzer.metric.ThroughputMetric;

/**
 * Analyzer to generate statistics for net messages.
 * 
 * @author Matthias Feldotto <info@peerfact.org>
 * @version 1.0, 12/21/2011
 */
public class DefaultNetAnalyzer extends AbstractFileMetricAnalyzer<NetID>
		implements NetAnalyzer {

	private CounterMetric<NetID> outMsgCounter = new CounterMetric<NetID>(
			"Messages out", "number");

	private CounterMetric<NetID> inMsgCounter = new CounterMetric<NetID>(
			"Messages in", "number");

	private SumMetric<NetID, Long> bytesSentCounter = new SumMetric<NetID, Long>(
			"Bytes Sent", "bytes");

	private SumMetric<NetID, Long> bytesReceivedCounter = new SumMetric<NetID, Long>(
			"Bytes Received", "bytes");

	private ThroughputMetric<NetID, Long> outBandwidth = new ThroughputMetric<NetID, Long>(
			"Bandwidth out", "Bytes/sec");

	private ThroughputMetric<NetID, Long> inBandwidth = new ThroughputMetric<NetID, Long>(
			"Bandwidth in", "Bytes/sec");

	public DefaultNetAnalyzer() {
		setOutputFileName("Net");
		setFlushEveryLine(true);
	}

	@Override
	protected void initializeMetrics() {
		addMetric(outMsgCounter);
		addMetric(inMsgCounter);
		addMetric(bytesSentCounter);
		addMetric(bytesReceivedCounter);
		addMetric(outBandwidth);
		addMetric(inBandwidth);
		outBandwidth.setInterval(timeBetweenAnalyzeSteps, endOfAnalyzing
				- beginOfAnalyzing);
		inBandwidth.setInterval(timeBetweenAnalyzeSteps, endOfAnalyzing
				- beginOfAnalyzing);
	}

	@Override
	public void netMsgDrop(NetMessage msg, NetID id) {
		// Currently not needed
	}

	@Override
	public void netMsgReceive(NetMessage msg, NetID id) {
		addPeer(id);
		inMsgCounter.increment(id);
		bytesReceivedCounter.addValue(id, msg.getSize());
		inBandwidth.addValue(id, msg.getSize());
	}

	@Override
	public void netMsgSend(NetMessage msg, NetID id) {
		addPeer(id);
		outMsgCounter.increment(id);
		bytesSentCounter.addValue(id, msg.getSize());
		outBandwidth.addValue(id, msg.getSize());
	}
}
