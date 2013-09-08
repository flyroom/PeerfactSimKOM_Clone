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

import org.peerfact.api.analyzer.AggregationAnalyzer;
import org.peerfact.api.common.Host;
import org.peerfact.api.service.aggr.AggregationResult;
import org.peerfact.impl.analyzer.metric.CounterMetric;

/**
 * Analyzer to generate statistics for all aggregation queries.
 * 
 * @author Matthias Feldotto <info@peerfact.org>
 * @version 1.0, 12/21/2011
 */
public class DefaultAggregationAnalyzer extends
		AbstractFileMetricAnalyzer<Host>
		implements AggregationAnalyzer {

	private CounterMetric<Host> startedQueries = new CounterMetric<Host>(
			"Started Queries", "number");

	private CounterMetric<Host> succeededQueries = new CounterMetric<Host>(
			"Succeeded Queries", "number");

	private CounterMetric<Host> failedQueries = new CounterMetric<Host>(
			"Failed Queries", "number");

	private CounterMetric<Host> finishedQueries = new CounterMetric<Host>(
			"Finished Queries", "number");

	public DefaultAggregationAnalyzer() {
		setOutputFileName("Aggregation");
		setFlushEveryLine(true);
	}

	@Override
	protected void initializeMetrics() {
		addMetric(startedQueries);
		addMetric(succeededQueries);
		addMetric(failedQueries);
		addMetric(finishedQueries);
	}

	@Override
	public void aggregationQueryStarted(Host host, Object identifier, Object UID) {
		addPeer(host);
		startedQueries.increment(host);
	}

	@Override
	public void aggregationQuerySucceeded(Host host, Object identifier,
			Object UID, AggregationResult result) {
		addPeer(host);
		succeededQueries.increment(host);
		finishedQueries.increment(host);
	}

	@Override
	public void aggregationQueryFailed(Host host, Object identifier, Object UID) {
		addPeer(host);
		failedQueries.increment(host);
		finishedQueries.increment(host);
	}

}
