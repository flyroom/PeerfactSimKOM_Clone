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

import org.peerfact.api.analyzer.OperationAnalyzer;
import org.peerfact.api.common.Host;
import org.peerfact.api.common.Operation;
import org.peerfact.impl.analyzer.metric.CounterMetric;
import org.peerfact.impl.analyzer.metric.StatisticMetric;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.util.stats.StatHelper;

/**
 * Analyzer to generate statistics for all operations.
 * 
 * @author Matthias Feldotto <info@peerfact.org>
 * @version 1.0, 12/21/2011
 */
public class DefaultOperationAnalyzer extends
		AbstractFileMetricAnalyzer<Host>
		implements OperationAnalyzer {

	private CounterMetric<Host> initiatedOperations = new CounterMetric<Host>(
			"Initiated Operations", "number");

	private CounterMetric<Host> finishedOperations = new CounterMetric<Host>(
			"Finished Operations", "number");

	private CounterMetric<Host> successfulOperations = new CounterMetric<Host>(
			"Successful Operations", "number");

	private CounterMetric<Host> failedOperations = new CounterMetric<Host>(
			"Failed Operations", "number");

	private StatisticMetric<Host, Long> durations = new StatisticMetric<Host, Long>(
			"Duration", "seconds");

	StatHelper<Long> stats = new StatHelper<Long>();

	public DefaultOperationAnalyzer() {
		setOutputFileName("Operation");
		setFlushEveryLine(true);
	}

	@Override
	protected void initializeMetrics() {
		addMetric(initiatedOperations);
		addMetric(finishedOperations);
		addMetric(successfulOperations);
		addMetric(failedOperations);
		addMetric(durations);
	}

	@Override
	public void operationInitiated(Operation<?> op) {
		addPeer(op.getComponent().getHost());
		initiatedOperations.increment(op.getComponent().getHost());
	}

	@Override
	public void operationFinished(Operation<?> op) {
		addPeer(op.getComponent().getHost());
		finishedOperations.increment(op.getComponent().getHost());
		durations.addValue(op.getComponent().getHost(), op.getDuration()
				/ Simulator.SECOND_UNIT);
		if (op.isSuccessful()) {
			successfulOperations.increment(op.getComponent().getHost());
		} else {
			failedOperations.increment(op.getComponent().getHost());
		}
	}
}
