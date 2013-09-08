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
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.peerfact.api.analyzer.AggregationAnalyzer;
import org.peerfact.api.common.Host;
import org.peerfact.api.service.aggr.AggregationResult;
import org.peerfact.api.simengine.SimulationEventHandler;
import org.peerfact.impl.simengine.SimulationEvent;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.util.logging.SimLogger;
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
public class AggregationQueryAnalyzer implements AggregationAnalyzer,
		SimulationEventHandler, IOutputWriterDelegator {
	private final static Logger log = SimLogger
			.getLogger(AggregationQueryAnalyzer.class);

	private String AGGREGATION_QUERY_STATISTICS_TABLE = "aggregationQueryStatistics";

	private String NUMBER_OF_SUCCESS_QUERIES = "succeededQueries";

	private String NUMBER_OF_FAILED_QUERIES = "failedQueries";

	private String NUMBER_OF_QUERIES = "queries";

	private String AVG_RESPONSE_TIME = "avgResponseTime";

	private String STD_RESPONSE_TIME = "stdResponseTime";

	private String QUANTIL_5_RESPONSE_TIME = "quantil5ResponseTime";

	private String QUANTIL_95_RESPONSE_TIME = "quantil95ResponseTime";

	private String MEDIAN_RESPONSE_TIME = "medianResponseTime";

	private String MAX_RESPONSE_TIME = "maxResponseTime";

	private String MIN_RESPONSE_TIME = "minResponseTime";

	/**
	 * The needed time for queries.
	 */
	private List<Long> responseTimes;

	private Map<Object, Long> queryStartTime;

	private int succeededQueries;

	private int failedQueries;

	boolean active = false;

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
		this.outputWriter = analyzerOutputWriter;
	}

	// ************************************
	// Grabbing the data of the simulation
	// ************************************
	@Override
	public void aggregationQueryStarted(Host host, Object identifier, Object UID) {
		if (isActive()) {
			queryStartTime.put(UID, Simulator.getCurrentTime());
		}
	}

	@Override
	public void aggregationQuerySucceeded(Host host, Object identifier,
			Object UID, AggregationResult result) {
		if (isActive()) {
			Long startTime = queryStartTime.remove(UID);
			succeededQueries++;
			if (startTime != null) {
				responseTimes.add(Simulator.getCurrentTime() - startTime);
			} else {
				log.error("AggregationQuerySucceeded without starting ...");
			}
		}

	}

	@Override
	public void aggregationQueryFailed(Host host, Object identifier, Object UID) {
		queryStartTime.remove(UID);
		failedQueries++;
		log.error("AggregationQuery failed!");

	}

	// ************************************
	// Handling for this analyzer
	// ************************************
	@Override
	public void start() {
		active = true;
		this.queryStartTime = new LinkedHashMap<Object, Long>();
		resetIntervalMetrics();

		outputWriter.initialize(AGGREGATION_QUERY_STATISTICS_TABLE);

		Simulator.scheduleEvent(null, Simulator.getCurrentTime(), this, null);
	}

	@Override
	public void stop(Writer output) {
		active = false;
		// outputWriter.flush();
	}

	private boolean isActive() {
		return active;
	}

	private void resetIntervalMetrics() {
		this.responseTimes = new LinkedList<Long>();
		this.succeededQueries = 0;
		this.failedQueries = 0;
	}

	@Override
	public void eventOccurred(SimulationEvent se) {
		if (isActive()) {
			persistenceMetrics();
			resetIntervalMetrics();
			Simulator.scheduleEvent(null, Simulator.getCurrentTime()
					+ measurementInterval, this, null);
		}
	}

	private void persistenceMetrics() {
		long curTime = Simulator.getCurrentTime();
		List<Double> responseTimeList = lLongTolDouble(this.responseTimes);
		outputWriter.persist(AGGREGATION_QUERY_STATISTICS_TABLE,
				new AnalyzerOutputEntry(curTime, NUMBER_OF_SUCCESS_QUERIES,
						succeededQueries));
		outputWriter.persist(AGGREGATION_QUERY_STATISTICS_TABLE,
				new AnalyzerOutputEntry(curTime, NUMBER_OF_FAILED_QUERIES,
						failedQueries));
		outputWriter.persist(AGGREGATION_QUERY_STATISTICS_TABLE,
				new AnalyzerOutputEntry(curTime, NUMBER_OF_QUERIES,
						succeededQueries + failedQueries));

		outputWriter.persist(AGGREGATION_QUERY_STATISTICS_TABLE,
				new AnalyzerOutputEntry(curTime, AVG_RESPONSE_TIME,
						stats.arithmeticMean(responseTimeList)));

		outputWriter.persist(AGGREGATION_QUERY_STATISTICS_TABLE,
				new AnalyzerOutputEntry(curTime, STD_RESPONSE_TIME,
						stats.standardDeviation(responseTimeList)));

		outputWriter.persist(AGGREGATION_QUERY_STATISTICS_TABLE,
				new AnalyzerOutputEntry(curTime, QUANTIL_5_RESPONSE_TIME,
						stats.quantile(responseTimeList, 0.05)));

		outputWriter.persist(AGGREGATION_QUERY_STATISTICS_TABLE,
				new AnalyzerOutputEntry(curTime, QUANTIL_95_RESPONSE_TIME,
						stats.quantile(responseTimeList, 0.95)));

		outputWriter.persist(AGGREGATION_QUERY_STATISTICS_TABLE,
				new AnalyzerOutputEntry(curTime, MEDIAN_RESPONSE_TIME,
						stats.median(responseTimeList)));

		outputWriter.persist(AGGREGATION_QUERY_STATISTICS_TABLE,
				new AnalyzerOutputEntry(curTime, MAX_RESPONSE_TIME,
						stats.max(responseTimeList)));

		outputWriter.persist(AGGREGATION_QUERY_STATISTICS_TABLE,
				new AnalyzerOutputEntry(curTime, MIN_RESPONSE_TIME,
						stats.min(responseTimeList)));

	}

	private static List<Double> lLongTolDouble(List<Long> lLong) {
		List<Double> lDouble = new Vector<Double>();
		for (Long l : lLong) {
			lDouble.add(new Double(l));
		}
		return lDouble;
	}
}
