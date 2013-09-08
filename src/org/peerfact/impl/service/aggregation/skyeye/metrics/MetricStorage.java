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

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.peerfact.api.service.skyeye.SkyNetNodeInterface;
import org.peerfact.api.service.skyeye.Storage;
import org.peerfact.impl.service.aggregation.skyeye.SkyNetPropertiesReader;
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
 * This class is responsible for storing the own measured metrics of a
 * Coordinator and for storing the received metrics of its Sub-Coordinators.
 * Besides the storage, <code>MetricStorage</code> is responsible of aggregating
 * the inclosed metrics. The class stores the Sub-Coordinators as well as their
 * provided information within <code>listOfSubCoordinators</code>.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 06.12.2008
 * 
 */
public class MetricStorage implements Storage {

	private static Logger log = SimLogger.getLogger(MetricStorage.class);

	// a vector for collecting some metric-aggregations
	private LinkedList<LinkedHashMap<String, MetricsAggregate>> metricsHistory;

	// a LinkedHashMap for collecting the sending SubCoordinators including
	// their
	// metric-aggregations
	private LinkedHashMap<BigDecimal, MetricsSubCoordinatorInfo> listOfSubCoordinators;

	// a LinkedHashMap for the metrics, collected at the own host. The own
	// metrics are
	// aggregated by the method aggregateMetrics() with the metrics-aggregates
	// stored in the list of SubCoordinators
	private MetricsEntry ownMetrics;

	private final String smoothingType;

	private int historySize;

	private float exponentialSmoothing;

	private final SkyNetNodeInterface skynetNode;

	public MetricStorage(SkyNetNodeInterface skynetNode) {
		this.skynetNode = skynetNode;
		metricsHistory = new LinkedList<LinkedHashMap<String, MetricsAggregate>>();
		listOfSubCoordinators = new LinkedHashMap<BigDecimal, MetricsSubCoordinatorInfo>();
		ownMetrics = null;
		SkyNetPropertiesReader reader = SkyNetPropertiesReader.getInstance();
		historySize = reader.getIntProperty("SizeOfHistory");
		if (historySize < 1) {
			historySize = 1;
		}
		smoothingType = reader.getStringProperty("SmoothingType");
		if (smoothingType.equals("ExponentialSmoothing")) {
			exponentialSmoothing = reader
					.getFloatProperty("ExponentialSmoothingFactor");
		}

	}

	public void reset() {
		listOfSubCoordinators.clear();
		ownMetrics = null;
	}

	/**
	 * This method returns the list of Sub-Coordinators, which is maintained by
	 * <code>MetricStorage</code>.
	 * 
	 * @return the list of Sub-Coordinators
	 */
	public LinkedHashMap<BigDecimal, MetricsSubCoordinatorInfo> getListOfSubCoordinators() {
		return listOfSubCoordinators;
	}

	/**
	 * This method stores the list of Sub-Coordinators, which is provided by the
	 * parameter of this method.
	 * 
	 * @param listOfSubCoordinators
	 *            contains the new list of Sub-Coordinators
	 */
	public void setListOfSubCoordinators(
			LinkedHashMap<BigDecimal, MetricsSubCoordinatorInfo> listOfSubCoordinators) {
		this.listOfSubCoordinators = listOfSubCoordinators;
	}

	/**
	 * This method returns the history of measured or aggregated metrics of the
	 * last ten metric-updates.
	 * 
	 * @return a list, which contains the information of the last metric-updates
	 */
	public LinkedList<LinkedHashMap<String, MetricsAggregate>> getMetricsHistory() {
		return metricsHistory;
	}

	/**
	 * This method stores the list of the last metric-updates, which is provided
	 * by the parameter of the method.
	 * 
	 * @param data
	 *            represents the list, which contains the information of the
	 *            last metric-updates
	 */
	public void setMetricsHistory(
			LinkedList<LinkedHashMap<String, MetricsAggregate>> data) {
		this.metricsHistory = data;
	}

	/**
	 * This method is responsible for aggregating all metrics of the Coordinator
	 * and the Sub-Coordinators, in order to return a <code>MetricEntry</code>
	 * -object, that contains all actual aggregated metrics.
	 * 
	 * @return a <code>MetricEntry</code>-object, which comprises all actual
	 *         aggregated metrics.
	 */
	public MetricsEntry aggregateMetrics(boolean printRoot) {
		// reference will later contain the new aggregated metrics
		LinkedHashMap<String, MetricsAggregate> referenceMap = new LinkedHashMap<String, MetricsAggregate>();
		Iterator<String> metric;
		boolean leaf = false;
		if (listOfSubCoordinators.size() > 0) {
			// iterator over the subCoordinators
			Iterator<BigDecimal> mapIter = listOfSubCoordinators.keySet()
					.iterator();

			// in this block the received metrics of the SubCoordinators will be
			// aggregated
			MetricsSubCoordinatorInfo sci = null;
			LinkedHashMap<String, MetricsAggregate> elem = null;

			String metricName;
			MetricsAggregate ag = null;
			MetricsAggregate newAg = null;
			while (mapIter.hasNext()) {
				// pick the next map of aggregates and merge it with the
				// aggregates of the reference-map
				sci = listOfSubCoordinators.get(mapIter.next());
				elem = sci.getData().getMetrics();
				metric = elem.keySet().iterator();
				while (metric.hasNext()) {
					metricName = metric.next();
					ag = elem.get(metricName);
					newAg = mergeTwoAggregates(ag,
							referenceMap.remove(metricName));
					referenceMap.put(metricName, newAg);
				}
			}
		} else {
			leaf = true;
		}
		// now, the own metrics- collected by the MetricsCollector-class are
		// aggregated with the already aggregated map
		LinkedHashMap<String, MetricsAggregate> own = ownMetrics.getMetrics();
		metric = own.keySet().iterator();

		String metricName;
		MetricsAggregate ag = null;
		MetricsAggregate newAg = null;
		while (metric.hasNext()) {
			metricName = metric.next();
			ag = own.get(metricName);
			newAg = mergeTwoAggregates(ag, referenceMap.remove(metricName));

			referenceMap.put(metricName, newAg);
		}
		// store aggregated data at
		addMetricsMapToMetricsHistory(referenceMap);

		// Get the median value of every metric and store it in the new
		// metrics-map, which will be sent to the Parent-Coordinator. The median
		// of the n last elements will only be collected if this Coordinator is
		// not a leaf
		boolean isRoot = skynetNode.getTreeHandler().isRoot();
		String smoothingPoint = SkyNetPropertiesReader.getInstance()
				.getStringProperty("SmootingPoint");
		boolean smoothAtRoot = smoothingPoint.compareTo("Root") == 0
				|| smoothingPoint.compareTo("Both") == 0;
		boolean smoothAtNonRoots = smoothingPoint.compareTo("NonRoots") == 0
				|| smoothingPoint.compareTo("Both") == 0;

		MetricsEntry metricsEntry = null;
		if (!leaf && historySize > 1
				&& ((isRoot && smoothAtRoot) || (!isRoot && smoothAtNonRoots))) {
			if (smoothingType.equals("ExponentialSmoothing")) {
				metricsEntry = MetricsSmoother.exponentialSmoothing(
						metricsHistory, historySize, ownMetrics,
						exponentialSmoothing, printRoot);
			} else {
				metricsEntry = MetricsSmoother.medianSmoothingOfMetrics(
						metricsHistory, historySize, ownMetrics, printRoot);
			}
		} else {
			metricsEntry = new MetricsEntry(ownMetrics.getNodeInfo(),
					referenceMap);
		}

		// return the new aggregated metrics, containing the metrics of the list
		// of SubCoordinators, as well as the own ones
		return metricsEntry;
	}

	private static MetricsAggregate mergeTwoAggregates(MetricsAggregate first,
			MetricsAggregate second) {
		if (second == null) {
			return new MetricsAggregate(first.getAggregateName(),
					first.getMinimum(), first.getMaximum(), first
							.getSumOfAggregates().doubleValue(), first
							.getSumOfSquares().doubleValue(),
					first.getNodeCount(), first.getMinTime(),
					first.getMaxTime(), first.getAvgTime());

		} else {
			long minTime = Math.min(first.getMinTime(), second.getMinTime());
			long maxTime = Math.max(first.getMaxTime(), second.getMaxTime());
			long avgTime = (first.getAvgTime() * first.getNodeCount() + second
					.getAvgTime() * second.getNodeCount())
					/ (first.getNodeCount() + second.getNodeCount());

			return new MetricsAggregate(first.getAggregateName(), Math.min(
					first.getMinimum(), second.getMinimum()), Math.max(
					first.getMaximum(), second.getMaximum()),
					first.getSumOfAggregates() + second.getSumOfAggregates(),
					first.getSumOfSquares() + second.getSumOfSquares(),
					first.getNodeCount() + second.getNodeCount(), minTime,
					maxTime, avgTime);
		}
	}

	/**
	 * This method stores the own measured metrics of a SkyNet-node within the
	 * storage.
	 * 
	 * @param ownMetrics
	 *            contains the measured metrics
	 */
	public void setOwnMetrics(MetricsEntry ownMetrics) {
		this.ownMetrics = ownMetrics;
	}

	/**
	 * This method adds the provided metrics to the list, that stores the latest
	 * aggregated metrics.
	 * 
	 * @param metricsMap
	 *            contains the freshly aggregated metrics
	 */
	private void addMetricsMapToMetricsHistory(
			LinkedHashMap<String, MetricsAggregate> metricsMap) {
		metricsHistory.push(metricsMap);
		if (metricsHistory.size() > 10) {
			metricsHistory.pollLast();
		}
		if (metricsHistory.size() > 10) {
			log.error("Size of data-list is bigger than 10");
		}
	}

	public MetricsEntry getOwnMetrics() {
		return ownMetrics;
	}

}
