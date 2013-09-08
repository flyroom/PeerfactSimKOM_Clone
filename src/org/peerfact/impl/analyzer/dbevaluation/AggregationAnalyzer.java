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
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.peerfact.api.analyzer.Analyzer;
import org.peerfact.api.common.Host;
import org.peerfact.api.service.aggr.AggregationResult;
import org.peerfact.api.service.aggr.AggregationService;
import org.peerfact.api.simengine.SimulationEventHandler;
import org.peerfact.impl.network.IPv4NetID;
import org.peerfact.impl.service.aggregation.DefaultAggregationResult;
import org.peerfact.impl.service.aggregation.centralizedmonitoring.AttributeIdentifier;
import org.peerfact.impl.service.aggregation.oracle.OracleUniverse;
import org.peerfact.impl.simengine.SimulationEvent;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.util.functiongenerator.FunctionGenerator;
import org.peerfact.impl.util.functiongenerator.functions.Function;
import org.peerfact.impl.util.oracle.GlobalOracle;


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
public class AggregationAnalyzer implements SimulationEventHandler, Analyzer,
		IOutputWriterDelegator {

	private static final String AVERAGE = "AVG";

	private static final String AVERAGE_TIME = "avg_time";

	private static final String MAXIMUM = "MAX";

	private static final String MAXIMUM_TIME = "max_time";

	private static final String MINIMUM = "MIN";

	private static final String MINIMUM_TIME = "min_time";

	private static final String NODE_COUNT = "node_count";

	private static final String VARIANCE = "SUMSQ";

	private static final String RECEIVING_TIMESTAMP = "receiving_global_aggregation";

	private static String DUMMY_AGGREGATION_PREFIX = "DummyMetric";

	private List<String> aggregationEntityNames = new Vector<String>();

	private long measurementInterval;

	private IAnalyzerOutputWriter outputWriter;

	private boolean active = false;

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
	// Control analyzer methods
	// ************************************
	@Override
	public void start() {
		this.active = true;

		Simulator.scheduleEvent(null, Simulator.getCurrentTime(), this, null);
	}

	@Override
	public void stop(Writer output) {
		this.active = false;
		// outputWriter.flush();
	}

	@Override
	public void eventOccurred(SimulationEvent se) {
		if (active) {
			List<AggregationService<Object>> onlineServices = AggregationGeneralAnalyzer
					.getOnlineAggregationServices();
			OracleUniverse oracle = createOracle(onlineServices);
			Set<Object> identifieres = new LinkedHashSet<Object>();

			// write global Oracle aggregation out.

			// Pick dummy online Service to retrieve List of aggregates
			Map<Object, AggregationResult> aggregations = new LinkedHashMap<Object, AggregationResult>();
			for (int i = 0; i < onlineServices.size(); i++) {
				aggregations = getAggregations(onlineServices.get(i));
				if (aggregations.size() > 0) {
					break;
				}
			}

			for (Object o : aggregations.keySet()) {
				AggregationResult aggregationResult = null;

				String aggName = null;
				if (o instanceof String) {
					aggName = (String) o;
				} else if (o instanceof AttributeIdentifier) {
					aggName = ((AttributeIdentifier) o).name();
				}

				// Handle aggregates of function differently
				if (aggName != null && aggName.startsWith("Function")) {

					String[] split = aggName.split("_");
					long simTime = Simulator.getCurrentTime();
					FunctionGenerator generator = FunctionGenerator
							.getInstance();
					Map<Class<? extends Function>, Double> functionResults = generator
							.getValues();
					if (functionResults != null && functionResults.size() > 0) {
						Iterator<Class<? extends Function>> iter = functionResults
								.keySet().iterator();
						Class<? extends Function> clazz = null;
						while (iter.hasNext()) {
							clazz = iter.next();
							String name = clazz.getSimpleName();
							if (name.equals(split[1])) {
								double result = functionResults.get(clazz)
										.doubleValue();
								aggregationResult = new DefaultAggregationResult(
										result, result, result, result,
										getNumberOfOnlineNodes(), simTime,
										simTime, simTime);
								break;
							}
						}
					}
				} else {
					aggregationResult = oracle.getAggregationResult(aggName);
				}
				String entityName = initializeEntity(aggName);
				writeAggregationOut(entityName, null, aggregationResult);
				identifieres.add(aggName);
			}

			// write for every peer the aggregation
			for (AggregationService<?> service : onlineServices) {
				aggregations = getAggregations(service);
				Long hostID = ((IPv4NetID) service.getHost().getNetLayer()
						.getNetID()).getID();
				for (Object o : aggregations.keySet()) {
					AggregationResult aggregationResult = aggregations.get(o);
					String entityName = initializeEntity(o);
					writeAggregationOut(entityName, hostID, aggregationResult);

					// Write out the receiving time of the aggregate
					AnalyzerOutputEntry receivingAggr = new AnalyzerOutputEntry(
							hostID,
							Simulator.getCurrentTime(),
							RECEIVING_TIMESTAMP,
							service.getGlobalAggregationReceivingTime(o) == 0 ? null
									: service
											.getGlobalAggregationReceivingTime(o));
					outputWriter.persist(entityName, receivingAggr);

					identifieres.add(o);
				}
			}

			// write out the Derivation for every function, which is loaded
			// writeFunctionDerivationOut();

			Simulator.scheduleEvent(null, Simulator.getCurrentTime()
					+ measurementInterval, this, null);
			outputWriter.flush();
		}
	}

	private void writeAggregationOut(String entityName, Long givenHostId,
			AggregationResult aggrResult) {
		Long hostId = givenHostId;
		if (hostId == null) {
			hostId = AnalyzerOutputEntry.GENERAL_METRIC_ID;
		}
		long time = Simulator.getCurrentTime();

		AnalyzerOutputEntry average = new AnalyzerOutputEntry(hostId, time,
				AVERAGE, aggrResult.getAverage());
		AnalyzerOutputEntry avgTime = new AnalyzerOutputEntry(hostId, time,
				AVERAGE_TIME, aggrResult.getAvgTime());
		AnalyzerOutputEntry maximum = new AnalyzerOutputEntry(hostId, time,
				MAXIMUM, aggrResult.getMaximum());
		AnalyzerOutputEntry maxTime = new AnalyzerOutputEntry(hostId, time,
				MAXIMUM_TIME, aggrResult.getMaxTime());
		AnalyzerOutputEntry minimum = new AnalyzerOutputEntry(hostId, time,
				MINIMUM, aggrResult.getMinimum());
		AnalyzerOutputEntry minTime = new AnalyzerOutputEntry(hostId, time,
				MINIMUM_TIME, aggrResult.getMinTime());
		AnalyzerOutputEntry nodeCount = new AnalyzerOutputEntry(hostId, time,
				NODE_COUNT, aggrResult.getNodeCount());
		AnalyzerOutputEntry variance = new AnalyzerOutputEntry(hostId, time,
				VARIANCE, aggrResult.getVariance());

		outputWriter.persist(entityName, average);
		outputWriter.persist(entityName, minTime);
		outputWriter.persist(entityName, maxTime);
		outputWriter.persist(entityName, nodeCount);

		if (!entityName.startsWith("Function")) {
			outputWriter.persist(entityName, avgTime);
			outputWriter.persist(entityName, minimum);
			outputWriter.persist(entityName, maximum);
			outputWriter.persist(entityName, variance);
		}
	}

	private String initializeEntity(Object o) {
		String result = o.toString();
		if (!aggregationEntityNames.contains(result)) {
			outputWriter.initialize(result);
			aggregationEntityNames.add(result);
		}
		return result;
	}

	private static Map<Object, AggregationResult> getAggregations(
			AggregationService<?> service) {
		Map<Object, AggregationResult> result = new LinkedHashMap<Object, AggregationResult>();
		for (Object o : service.getIdentifiers()) {
			if (!o.toString().startsWith(DUMMY_AGGREGATION_PREFIX)) {
				result.put(o, service.getStoredAggregationResult(o));
			}
		}
		return result;
	}

	private static int getNumberOfOnlineNodes() {
		List<Host> hosts = GlobalOracle.getHosts();
		int count = 0;
		for (Host host : hosts) {
			if (host.getNetLayer().isOnline()) {
				count++;
			}
		}
		return count;
	}

	private static OracleUniverse createOracle(
			List<AggregationService<Object>> onlineServices) {
		OracleUniverse oracle = new OracleUniverse();
		for (AggregationService<Object> aggrService : onlineServices) {
			oracle.add(aggrService);
		}
		return oracle;
	}

}
