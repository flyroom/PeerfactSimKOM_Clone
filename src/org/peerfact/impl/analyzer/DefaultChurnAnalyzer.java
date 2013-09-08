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

import org.peerfact.api.analyzer.ChurnAnalyzer;
import org.peerfact.impl.analyzer.metric.StatisticMetric;
import org.peerfact.impl.simengine.Simulator;

/**
 * Analyzer creates statistics for churn times.
 * 
 * @author Matthias Feldotto <info@peerfact.org>
 * @version 1.0, 12/21/2011
 */
public class DefaultChurnAnalyzer extends AbstractFileMetricAnalyzer<Object>
		implements ChurnAnalyzer {

	private StatisticMetric<Object, Long> sessionTimesAverage = new StatisticMetric<Object, Long>(
			"Session-Time", "minutes");

	private StatisticMetric<Object, Long> interSessionTimesAverage = new StatisticMetric<Object, Long>(
			"Inter-Session-Time", "minutes");

	public DefaultChurnAnalyzer() {
		setOutputFileName("Churn");
		setFlushEveryLine(true);
	}

	@Override
	protected void initializeMetrics() {
		addMetric(sessionTimesAverage);
		addMetric(interSessionTimesAverage);
	}

	@Override
	public void nextInterSessionTime(long time) {
		interSessionTimesAverage.addValue(null, time / Simulator.MINUTE_UNIT);
	}

	@Override
	public void nextSessionTime(long time) {
		sessionTimesAverage.addValue(null, time / Simulator.MINUTE_UNIT);
	}

}
