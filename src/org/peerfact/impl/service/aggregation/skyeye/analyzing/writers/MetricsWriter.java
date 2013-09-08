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

package org.peerfact.impl.service.aggregation.skyeye.analyzing.writers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Vector;

import org.peerfact.api.network.NetID;
import org.peerfact.api.service.skyeye.ISkyNetMonitor;
import org.peerfact.api.service.skyeye.SkyNetConstants;
import org.peerfact.impl.network.IPv4NetID;
import org.peerfact.impl.service.aggregation.skyeye.SkyNetPropertiesReader;
import org.peerfact.impl.service.aggregation.skyeye.analyzing.AbstractSkyNetWriter;
import org.peerfact.impl.service.aggregation.skyeye.analyzing.analyzers.ChurnStatisticsAnalyzer;
import org.peerfact.impl.service.aggregation.skyeye.metrics.MetricsAggregate;
import org.peerfact.impl.service.aggregation.skyeye.visualization.SkyNetVisualization;
import org.peerfact.impl.service.aggregation.skyeye.visualization.util.DataSet;
import org.peerfact.impl.service.aggregation.skyeye.visualization.util.DeviationSet;
import org.peerfact.impl.service.aggregation.skyeye.visualization.util.VisualizationType;
import org.peerfact.impl.simengine.SimulationEvent;
import org.peerfact.impl.simengine.Simulator;


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
 * This class is responsible for the monitoring of the metrics within SkyNet.
 * Within this class, the metrics, that are calculated by the root and utilized
 * for the system-statistics, are compared to the metrics, which are calculated
 * by the simulator. Through this comparison, one can discover the differences
 * between the measured metrics of the root and the "real" metrics, which are
 * provided by the overall view of the simulator. Both types of metrics are
 * stored within <code>Metrics.dat</code> in the gnuScripts/data-directory.<br>
 * The <code>Array nameSpace</code> specifies, which metrics are compared and
 * written in the <code>Metrics.dat</code>-file.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 04.12.2008
 * 
 */
public class MetricsWriter extends AbstractSkyNetWriter {

	private static final String RESULTS_PATH = SkyNetConstants.COMMON_SIMULATIONS_PATH;

	private static final String METRICS_FILE = "skynet-Metrics.dat";

	private static MetricsWriter writer;

	private PrintWriter pw;

	private boolean writeHeader;

	private Vector<String> namespace;

	private LinkedHashMap<String, MetricsAggregate> skyNetMetrics;

	private int skyNetNodeCounter;

	private LinkedHashMap<String, MetricsAggregate> aggMetrics;

	private Vector<Long> ipTracer;

	// private double nodeCounter;

	private long metricUpdateTime;

	private ChurnStatisticsAnalyzer csAnalyzer;

	public static MetricsWriter getInstance() {
		if (writer == null) {
			writer = new MetricsWriter();
		}
		return writer;
	}

	public static boolean hasInstance() {
		if (writer == null) {
			return false;
		} else {
			return true;
		}
	}

	private MetricsWriter() {
		initWriteDirectory(RESULTS_PATH, false);

		// setting the needed values from the properties file
		SkyNetPropertiesReader propReader = SkyNetPropertiesReader
				.getInstance();
		this.metricUpdateTime = propReader.getTimeProperty("MetricUpdateTime");
		long time = Simulator.getCurrentTime();
		long delta = time % metricUpdateTime;
		Simulator.scheduleEvent("Update", time + metricUpdateTime - delta,
				this, SimulationEvent.Type.STATUS);

		writeHeader = true;
		namespace = new Vector<String>();
		ISkyNetMonitor monitor = (ISkyNetMonitor) Simulator.getMonitor();
		csAnalyzer = (ChurnStatisticsAnalyzer) monitor
				.getConnectivityAnalyzer(ChurnStatisticsAnalyzer.class);
		aggMetrics = new LinkedHashMap<String, MetricsAggregate>();
		ipTracer = new Vector<Long>();
		skyNetMetrics = new LinkedHashMap<String, MetricsAggregate>();
		skyNetNodeCounter = 0;
		// nodeCounter = 0;
		try {
			pw = new PrintWriter(new BufferedWriter(new FileWriter(RESULTS_PATH
					+ File.separator + METRICS_FILE)), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method either obtains the metrics, which are calculated by the root,
	 * or collects the metrics of every node and averages them for the
	 * comparison.
	 * 
	 * @param map
	 *            contains the metrics
	 * @param skyNet
	 *            specifies if the provided metrics are submitted by the root or
	 *            not.
	 */
	public void addAggregatedMap(NetID id,
			LinkedHashMap<String, MetricsAggregate> map, boolean skyNet) {
		if (writeHeader) {
			writeHeader = writeHeaderFromUnprocessedMap(map);
		}
		if (skyNet) {
			skyNetNodeCounter = map.get(namespace.get(0)).getNodeCount();
			String name = null;
			for (int i = 0; i < namespace.size(); i++) {
				name = namespace.get(i);
				skyNetMetrics.put(name, map.get(name));
			}
			// collect the metrics from all SkyNet-Peers
		} else {
			// Double temp;
			if (!ipTracer.contains(((IPv4NetID) id).getID())) {
				ipTracer.add(((IPv4NetID) id).getID());
				for (int i = 0; i < namespace.size(); i++) {
					String metricsName = namespace.get(i);
					MetricsAggregate mergedAggregate = mergeTwoAggregates(
							map.get(metricsName), aggMetrics.get(metricsName));
					aggMetrics.put(metricsName, mergedAggregate);
				}
			}
		}
	}

	private boolean writeHeaderFromUnprocessedMap(
			LinkedHashMap<String, MetricsAggregate> aggregatedMetrics) {
		if (aggregatedMetrics.size() > 0) {
			pw.println("# Time in sec");
			pw.println("# Online-Peers");
			pw.println("# Offline-Peers");
			Iterator<String> nameIter = aggregatedMetrics.keySet().iterator();
			writeNamespace(nameIter);
			return false;
		} else {
			return true;
		}
	}

	private boolean writeHeaderFromProcessedMap(
			LinkedHashMap<String, MetricsAggregate> aggregatedMetrics) {
		if (aggregatedMetrics.size() > 0) {
			pw.println("# Time in sec");
			pw.println("# Online-Peers");
			pw.println("# Offline-Peers");
			Iterator<String> nameIter = aggregatedMetrics.keySet().iterator();
			writeNamespace(nameIter);
			return false;
		} else {
			return true;
		}
	}

	private void writeNamespace(Iterator<String> nameIter) {
		String name = null;
		while (nameIter.hasNext()) {
			name = nameIter.next();
			namespace.add(name);
		}
		if (SkyNetVisualization.hasInstance()) {
			SkyNetVisualization.getInstance().setAvailableMetrics(namespace);
		}
		Collections.sort(namespace);
		for (int i = 0; i < namespace.size(); i++) {
			pw.println("# " + namespace.get(i));
		}

		pw.println("# SKYNET-Online-Peers");
		for (int i = 0; i < namespace.size(); i++) {
			pw.println("# SKYNET-" + namespace.get(i));
		}
		pw.println("# Free Memory");
		pw.println("# Total Memory");
		pw.println("# Max Memory");
		pw.println();
	}

	public void closeWriter() {
		long time = Simulator.getCurrentTime();
		pw.print((time / SkyNetConstants.DIVISOR_FOR_SECOND) + "\t");
		pw.print(csAnalyzer.getNoOfPresentPeers() + "\t");
		pw.print(csAnalyzer.getNoOfAbsentPeers() + "\t");
		if (aggMetrics.size() > 0) {
			for (int i = 0; i < namespace.size(); i++) {
				pw.print((aggMetrics.get(namespace.get(i)).getAverage()) + "\t");
			}
		} else {
			for (int i = 0; i < namespace.size(); i++) {
				pw.print("0.0" + "\t");
			}
		}

		if (skyNetMetrics.size() > 0) {
			pw.print(skyNetNodeCounter + "\t");
			for (int i = 0; i < namespace.size(); i++) {
				pw.print(skyNetMetrics.get(namespace.get(i)).getAverage());
				if (i < (namespace.size() - 1)) {
					pw.print("\t");
				}
			}
			// skyNetMetrics.clear();
		} else {
			// this entry represents the missing node count
			pw.print("0\t");
			for (int i = 0; i < namespace.size(); i++) {
				pw.print("0.0");
				if (i < (namespace.size() - 1)) {
					pw.print("\t");
				}
			}
		}
		pw.println();
		skyNetMetrics.clear();
		aggMetrics.clear();
		ipTracer.clear();
		skyNetNodeCounter = 0;
		// nodeCounter = 0;

		pw.flush();
		pw.close();
	}

	@Override
	public void eventOccurred(SimulationEvent se) {
		if (se.getType().equals(SimulationEvent.Type.STATUS)) {
			if (((String) se.getData()).equals("Update")) {
				if (writeHeader) {
					writeHeader = writeHeaderFromProcessedMap(aggMetrics);
				}
				long time = Simulator.getCurrentTime();
				pw.print((time / SkyNetConstants.DIVISOR_FOR_SECOND) + "\t");
				pw.print(csAnalyzer.getNoOfPresentPeers() + "\t");
				pw.print(csAnalyzer.getNoOfAbsentPeers() + "\t");
				if (aggMetrics.size() > 0) {
					for (int i = 0; i < namespace.size(); i++) {
						pw.print((aggMetrics.get(namespace.get(i)).getAverage())
								+ "\t");
					}
				} else {
					for (int i = 0; i < namespace.size(); i++) {
						pw.print("0.0" + "\t");
					}
				}

				if (skyNetMetrics.size() > 0) {
					pw.print(skyNetNodeCounter + "\t");
					for (int i = 0; i < namespace.size(); i++) {
						pw.print(skyNetMetrics.get(namespace.get(i))
								.getAverage());
						if (i < (namespace.size() - 1)) {
							pw.print("\t");
						}
					}
					// skyNetMetrics.clear();
				} else {
					// this entry represents the missing node count
					pw.print("0\t");
					for (int i = 0; i < namespace.size(); i++) {
						pw.print("0.0");
						if (i < (namespace.size() - 1)) {
							pw.print("\t");
						}
					}
				}
				long freeMemory = Runtime.getRuntime().freeMemory() / 1024;
				long totalMemory = Runtime.getRuntime().totalMemory() / 1024;
				long maxMemory = Runtime.getRuntime().maxMemory() / 1024;
				long consumedMemory = totalMemory - freeMemory;

				pw.print("\t");
				pw.print(freeMemory);
				pw.print("\t");
				pw.print(totalMemory);
				pw.print("\t");
				pw.print(maxMemory);
				pw.println();
				pw.flush();

				// add the collected metrics by the simulator as well as the
				// measured metrics by the root to the SkyNetVisualization
				if (SkyNetVisualization.hasInstance()) {
					SkyNetVisualization.getInstance()
							.updateDisplayedMetrics(time, aggMetrics,
									skyNetMetrics/* , nodeCounter */);
				}
				DeviationSet[] values = {
						new DeviationSet(csAnalyzer.getNoOfPresentPeers()),
						new DeviationSet(csAnalyzer.getNoOfAbsentPeers()),
						new DeviationSet(skyNetNodeCounter) };
				String[] names = { "Real Online Peers", "Offline Peers",
						"Counted Online Peers" };
				if (SkyNetVisualization.hasInstance()) {
					SkyNetVisualization.getInstance().updateDisplayedMetric(
							"Online Peers",
							new DataSet(VisualizationType.State, time / 1000,
									values, names));
				}
				DeviationSet[] values2 = { new DeviationSet(freeMemory),
						new DeviationSet(totalMemory),
						new DeviationSet(maxMemory),
						new DeviationSet(consumedMemory) };
				String[] names2 = { "Free Memory", "Total Memory",
						"Max Memory", "Consumed Memory" };
				if (SkyNetVisualization.hasInstance()) {
					SkyNetVisualization.getInstance().updateDisplayedMetric(
							"Memory Usage",
							new DataSet(VisualizationType.State, time / 1000,
									values2, names2));
				}
				skyNetMetrics.clear();
				aggMetrics.clear();
				ipTracer.clear();
				skyNetNodeCounter = 0;
				// nodeCounter = 0;
				Simulator.scheduleEvent("Update", time + metricUpdateTime,
						this, SimulationEvent.Type.STATUS);
			}
		}
	}

	private MetricsAggregate mergeTwoAggregates(MetricsAggregate first,
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
}
