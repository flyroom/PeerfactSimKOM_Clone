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

import java.util.LinkedHashMap;
import java.util.Map;

import org.peerfact.api.analyzer.KBROverlayAnalyzer;
import org.peerfact.api.common.Message;
import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.impl.analyzer.metric.CounterMetric;
import org.peerfact.impl.analyzer.metric.StatisticMetric;
import org.peerfact.impl.simengine.Simulator;


/**
 * Analyzer to collect data about the hop count of a simulation using the KBR
 * interface for routing.
 * 
 * @author Matthias Feldotto <info@peerfact.org>
 */
public class DefaultKBROverlayAnalyzer extends
		AbstractFileMetricAnalyzer<OverlayContact<?>>
		implements KBROverlayAnalyzer {

	CounterMetric<OverlayContact<?>> numberOfInitiatedQueries = new CounterMetric<OverlayContact<?>>(
			"Initiated Queries", "number");

	CounterMetric<OverlayContact<?>> numberOfKeyRoutedQueries = new CounterMetric<OverlayContact<?>>(
			"Routed Queries", "number");

	CounterMetric<OverlayContact<?>> numberOfFailedQueries = new CounterMetric<OverlayContact<?>>(
			"Failed Queries", "number");

	CounterMetric<OverlayContact<?>> numberOfForwardedMessages = new CounterMetric<OverlayContact<?>>(
			"Forwarded Messages", "number");

	StatisticMetric<OverlayContact<?>, Integer> hops = new StatisticMetric<OverlayContact<?>, Integer>(
			"Median Hop Count", "number");

	StatisticMetric<OverlayContact<?>, Long> durations = new StatisticMetric<OverlayContact<?>, Long>(
			"Median Duration", "seconds");

	CounterMetric<OverlayContact<?>> additionalRoutedQueries = new CounterMetric<OverlayContact<?>>(
			"Additional Routed Queries", "number");

	CounterMetric<OverlayContact<?>> additionalFailedQueries = new CounterMetric<OverlayContact<?>>(
			"Additional Failed Queries", "number");

	Map<Message, Long> queryMsgs = new LinkedHashMap<Message, Long>();

	public DefaultKBROverlayAnalyzer() {
		setOutputFileName("KBROverlay");
		setFlushEveryLine(true);
	}

	@Override
	protected void initializeMetrics() {
		addMetric(numberOfInitiatedQueries);
		addMetric(numberOfKeyRoutedQueries);
		addMetric(numberOfFailedQueries);
		addMetric(numberOfForwardedMessages);
		addMetric(hops);
		addMetric(durations);
		addMetric(additionalRoutedQueries);
		addMetric(additionalFailedQueries);
	}

	@Override
	public void overlayMessageDelivered(OverlayContact<?> contact, Message msg,
			int madeHops) {
		addPeer(contact);
		if (queryMsgs.containsKey(msg.getPayload())) {
			long starttime = queryMsgs.remove(msg.getPayload());
			long duration = (Simulator.getCurrentTime() - starttime)
					/ Simulator.SECOND_UNIT;
			this.durations.addValue(contact, duration);
			this.hops.addValue(contact, madeHops);
			numberOfKeyRoutedQueries.increment(contact);
		} else {
			additionalRoutedQueries.increment(contact);
		}
	}

	@Override
	public void overlayMessageForwarded(OverlayContact<?> sender,
			OverlayContact<?> receiver, Message msg, int madeHops) {
		addPeer(sender);
		numberOfForwardedMessages.increment(sender);
	}

	@Override
	public void queryFailed(OverlayContact<?> failedHop, Message appMsg) {
		addPeer(failedHop);
		if (queryMsgs.containsKey(appMsg)) {
			queryMsgs.remove(appMsg);
			numberOfFailedQueries.increment(failedHop);
		} else {
			additionalFailedQueries.increment(failedHop);
		}
	}

	@Override
	public void queryStarted(OverlayContact<?> contact, Message appMsg) {
		queryMsgs.put(appMsg, Simulator.getCurrentTime());
		addPeer(contact);
		numberOfInitiatedQueries.increment(contact);
	}

}
