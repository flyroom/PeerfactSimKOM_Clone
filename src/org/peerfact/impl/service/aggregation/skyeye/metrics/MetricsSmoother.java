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

package org.peerfact.impl.service.aggregation.skyeye.metrics;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.peerfact.impl.service.aggregation.skyeye.SkyNetUtilities;
import org.peerfact.impl.util.logging.SimLogger;


/**
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * This part of the Simulator is not maintained in the current version of
 * PeerfactSim.KOM. There is no intention of the authors to fix this
 * circumstances, since the changes needed are huge compared to overall benefit.
 * 
 * If you want it to work correctly, you are free to make the specific changes
 * and provide it to the community.
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! !!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * 
 * 
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public class MetricsSmoother {

	private static Logger log = SimLogger.getLogger(MetricStorage.class);

	public static MetricsEntry medianSmoothingOfMetrics(
			LinkedList<LinkedHashMap<String, MetricsAggregate>> metricsHistory,
			int medianSize, MetricsEntry ownMetrics, boolean printRoot) {
		LinkedHashMap<String, MetricsAggregate> medianMetrics = new LinkedHashMap<String, MetricsAggregate>();
		// Get a metric of the latest metricsEntry as a reference for the
		// different amount of nodes
		final String metricsName = "OnlineTime";
		int historyElement = 0;

		// Collect the available amount of metricsEntries out of the history
		Vector<LinkedHashMap<String, MetricsAggregate>> entryHistory = new Vector<LinkedHashMap<String, MetricsAggregate>>();
		while (historyElement < medianSize
				&& historyElement < metricsHistory.size()) {
			entryHistory.add(metricsHistory.get(historyElement));
			historyElement++;
		}

		// Sort the retrieved metricsEntries and take the median entry
		Collections.sort(entryHistory,
				new Comparator<LinkedHashMap<String, MetricsAggregate>>() {
					@Override
					public int compare(
							LinkedHashMap<String, MetricsAggregate> o1,
							LinkedHashMap<String, MetricsAggregate> o2) {
						if (o1.get(metricsName).getNodeCount() < o2.get(
								metricsName).getNodeCount()) {
							return -1;
						} else if (o1.get(metricsName).getNodeCount() > o2
								.get(metricsName).getNodeCount()) {
							return 1;
						} else {
							return 0;
						}
					}
				});
		int numberOfElements = Math.min(historyElement, metricsHistory.size());
		medianMetrics.putAll(entryHistory.get((numberOfElements / 2)));

		// Only for debugging
		if (printRoot) {
			StringBuffer buf = new StringBuffer();
			for (int i = 0; i < historyElement; i++) {
				buf.append(entryHistory.get(i).get(metricsName)
						.getNodeCount() + ",");
			}
			log.warn(SkyNetUtilities.getTimeAndNetID(ownMetrics.getNodeInfo())
					+ " chose the metrics from the entry with "
					+ medianMetrics.get(metricsName).getNodeCount()
					+ " nodes, out of the values " + buf.toString()
					+ " for the calculated value "
					+ medianMetrics.get(metricsName).getNodeCount());
		}

		// return the chosen metricsEntry
		MetricsEntry medianEntry = new MetricsEntry(ownMetrics.getNodeInfo(),
				medianMetrics);
		return medianEntry;

	}

	public static MetricsEntry exponentialSmoothing(
			LinkedList<LinkedHashMap<String, MetricsAggregate>> metricsHistory,
			int historySize, MetricsEntry ownMetrics, float expFactor,
			boolean printRoot) {
		float counterFactor = 1 - expFactor;
		double result = 0;

		// Get a name of a metric from a set of metrics to access the amount of
		// counted nodes, which is used for the calculation of the exponential
		// smoothing
		String metricsName = "OnlineTime";

		// calculate the exponential smoothing out of the n elements
		int historyElement = Math.min(historySize, metricsHistory.size());
		historyElement--;
		result = metricsHistory.get(historyElement).get(metricsName)
				.getNodeCount();
		while (historyElement > 0) {
			result = (expFactor * metricsHistory.get(historyElement - 1).get(
					metricsName).getNodeCount())
					+ (counterFactor * result);
			historyElement--;
		}

		// Check, which of the sets of metrics is the closest to the calculated
		// value and return this set
		LinkedHashMap<String, MetricsAggregate> currentMetrics = null;
		historyElement = Math.min(historySize, metricsHistory.size());
		double compare = Double.MAX_VALUE;
		double difference = 0;
		for (int i = 0; i < historyElement; i++) {
			difference = Math.abs(metricsHistory.get(i).get(metricsName)
					.getNodeCount()
					- result);
			if (difference < compare) {
				compare = difference;
				currentMetrics = metricsHistory.get(i);
			}
		}

		// Only for debugging
		if (printRoot) {
			StringBuffer buf = new StringBuffer();

			for (int i = 0; i < historyElement; i++) {
				buf.append(metricsHistory.get(i).get(metricsName)
						.getNodeCount() + ",");
			}
			log.warn(SkyNetUtilities.getTimeAndNetID(ownMetrics.getNodeInfo())
					+ " chose the metrics from the entry with "
					+ currentMetrics.get(metricsName).getNodeCount()
					+ " nodes, out of the values " + buf.toString()
					+ " for the calculated value " + result);
		}

		MetricsEntry expEntry = new MetricsEntry(ownMetrics.getNodeInfo(),
				currentMetrics);
		return expEntry;
	}
}
