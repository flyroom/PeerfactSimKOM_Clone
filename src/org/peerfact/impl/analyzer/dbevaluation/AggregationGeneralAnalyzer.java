/*
 * Copyright (c) 2012-2013 Open Source Community - <http://www.peerfact.org>
 * Copyright (c) 2011-2012 University of Paderborn - UPB
 * Copyright (c) 2005-2011 KOM - Multimedia Communications Lab
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.peerfact.impl.analyzer.dbevaluation;

import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.peerfact.api.analyzer.Analyzer;
import org.peerfact.api.common.Host;
import org.peerfact.api.overlay.OverlayNode;
import org.peerfact.api.service.aggr.AggregationService;
import org.peerfact.api.simengine.SimulationEventHandler;
import org.peerfact.impl.simengine.SimulationEvent;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.util.oracle.GlobalOracle;
import org.peerfact.impl.util.stats.StatHelper;


/**
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * This part of the Simulator is not maintained in the current version of
 * PeerfactSim.KOM. There is no intention of the authors to fix this
 * circumstances, since the changes needed are huge compared to overall benefit.
 * 
 * If you want it to work correctly, you are free to make the specific changes
 * and provide it to the community.
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! !!!!!!!!!!!!!!!!!!!!!!!!!!!!
 */
public class AggregationGeneralAnalyzer implements SimulationEventHandler,
		Analyzer, IOutputWriterDelegator {

	private static final String GENERAL_ENTITY = "general_aggregation_information";

	private static final String NUMBER_OF_AGGREGATION_PREFIX = "number_of_aggregation_";

	private static final String AVG = "AVG";

	private static final String STDDEV = "STDDEV";

	private static final String MIN = "MIN";

	private static final String MAX = "MAX";

	private static final String QUANTILE_5 = "QUANTILE_5";

	private static final String QUANTILE_95 = "QUANTILE_95";

	private static final String COUNT = "COUNT";

	private boolean active = false;

	private long measurementInterval;

	private IAnalyzerOutputWriter outputWriter;

	StatHelper<Double> stats = new StatHelper<Double>();

	// ************************************
	// Setting and preparing the analyzer
	// ************************************

	public void setMeasurementInterval(long timeInterval) {
		this.measurementInterval = timeInterval;
	}

	@Override
	public void setAnalyzerOutputWriter(
			IAnalyzerOutputWriter analyzerOutputWriter) {
		outputWriter = analyzerOutputWriter;
	}

	// ************************************
	// Control analyzer methods
	// ************************************
	@Override
	public void start() {
		this.active = true;
		outputWriter.initialize(GENERAL_ENTITY);
		Simulator.scheduleEvent(null, Simulator.getCurrentTime(), this, null);
	}

	@Override
	public void stop(Writer output) {
		this.active = false;

		// outputWriter.flush();
	}

	@Override
	public void eventOccurred(SimulationEvent se) {
		if (this.active) {
			List<AggregationService<Object>> onlineServices = getOnlineAggregationServices();

			List<Double> numberOfAggregationsPerPeer = getNumberOfAggregationsPerPeer(onlineServices);

			writeDataList(GENERAL_ENTITY, NUMBER_OF_AGGREGATION_PREFIX,
					numberOfAggregationsPerPeer);

			Simulator.scheduleEvent(null, Simulator.getCurrentTime()
					+ measurementInterval, this, null);
			outputWriter.flush();
		}

	}

	private void writeDataList(String entityName, String metricPrefix,
			List<Double> data) {
		long time = Simulator.getCurrentTime();
		AnalyzerOutputEntry avg = new AnalyzerOutputEntry(time, metricPrefix
				+ AVG, stats.arithmeticMean(data));
		AnalyzerOutputEntry stdDev = new AnalyzerOutputEntry(time, metricPrefix
				+ STDDEV, stats.standardDeviation(data));
		AnalyzerOutputEntry min = new AnalyzerOutputEntry(time, metricPrefix
				+ MIN, stats.min(data));
		AnalyzerOutputEntry max = new AnalyzerOutputEntry(time, metricPrefix
				+ MAX, stats.max(data));
		AnalyzerOutputEntry quantile_5 = new AnalyzerOutputEntry(time,
				metricPrefix + QUANTILE_5, stats.quantile(data,
						0.05));
		AnalyzerOutputEntry quantile_95 = new AnalyzerOutputEntry(time,
				metricPrefix + QUANTILE_95, stats.quantile(data,
						0.95));
		AnalyzerOutputEntry count = new AnalyzerOutputEntry(time, metricPrefix
				+ COUNT, data.size());

		outputWriter.persist(entityName, avg);
		outputWriter.persist(entityName, stdDev);
		outputWriter.persist(entityName, min);
		outputWriter.persist(entityName, max);
		outputWriter.persist(entityName, quantile_5);
		outputWriter.persist(entityName, quantile_95);
		outputWriter.persist(entityName, count);

	}

	private static List<Double> getNumberOfAggregationsPerPeer(
			List<AggregationService<Object>> onlineServices) {
		List<Double> result = new Vector<Double>();
		for (AggregationService<?> service : onlineServices) {
			result.add((double) service.getNumberOfMonitoredAttributes());
		}
		return result;
	}

	// ************************************
	// Static methods for own and other classes
	// ************************************
	/**
	 * Gets a list of {@link AggregationService}, who the host is present in the
	 * Overlay. If no {@link AggregationService} exists for the host, then will
	 * be ignored the host.
	 * 
	 * @return A list of {@link AggregationService}s, which are online.
	 */
	public static List<AggregationService<Object>> getOnlineAggregationServices() {
		List<Host> onlineHosts = getOnlineHosts();
		List<AggregationService<Object>> result = new Vector<AggregationService<Object>>();
		for (Host host : onlineHosts) {
			AggregationService<Object> aggrService = host
					.getComponent(AggregationService.class);
			if (aggrService != null) {
				result.add(aggrService);
			}
		}
		return result;
	}

	/**
	 * Gets a list of hosts, which are connected with the Overlay.
	 * 
	 * @return A list of online hosts.
	 */
	private static List<Host> getOnlineHosts() {
		List<Host> allHosts = GlobalOracle.getHosts();
		List<Host> onlineHosts = new Vector<Host>();
		for (Host host : allHosts) {
			// is one overlay online
			Iterator<OverlayNode<?, ?>> ovNos = host.getOverlays();
			while (ovNos.hasNext()) {
				OverlayNode<?, ?> ovNo = ovNos.next();
				if (ovNo.isPresent()) {
					onlineHosts.add(host);
					break;
				}
			}
		}
		return onlineHosts;
	}

}
