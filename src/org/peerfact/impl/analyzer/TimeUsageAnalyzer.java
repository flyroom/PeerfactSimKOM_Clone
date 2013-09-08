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

import java.io.Writer;

import org.peerfact.impl.analyzer.metric.StatisticMetric;
import org.peerfact.impl.analyzer.metric.ValueMetric;
import org.peerfact.impl.simengine.SimulationEvent;
import org.peerfact.impl.simengine.Simulator;

/**
 * Analyzer for the used time of a simulation with PeerfactSim.KOM.
 * 
 * @author Matthias Feldotto <info@peerfact.org>
 * @version 1.0, 02/13/2013
 */
public class TimeUsageAnalyzer extends AbstractFileMetricAnalyzer<Object> {

	/** One second of time units. */
	public static final double MINUTES = 1000 * 60;

	/**
	 * Metric representing the total time.
	 */
	private ValueMetric<Object, Double> totalTime = new ValueMetric<Object, Double>(
			"Total Time", "minutes");

	/**
	 * Metric representing the used time.
	 */
	private StatisticMetric<Object, Double> usedTime = new StatisticMetric<Object, Double>(
			"Used Time", "minutes");

	/** Denotes if the time analyzer is activated */
	private boolean isRunning;

	/** Interval of periodic time status events */
	private long interval;

	/** The time at last collect */
	private long oldTime;

	public TimeUsageAnalyzer() {
		setOutputFileName("SimulationTime");
		setFlushEveryLine(true);
	}

	@Override
	protected void initializeMetrics() {
		addMetric(totalTime);
		addMetric(usedTime);
	}

	/**
	 * Collect new java virtual memory usage information and return current
	 * usage.
	 */
	public void collect() {
		long newTime = System.currentTimeMillis();
		usedTime.addValue(null, (newTime - oldTime)
				/ TimeUsageAnalyzer.MINUTES);
		totalTime.updateValue(null,
				(newTime - Simulator.getStartTime())
						/ TimeUsageAnalyzer.MINUTES);
		oldTime = newTime;
	}

	@Override
	public void eventOccurred(SimulationEvent se) {
		if (se.getType().equals(SimulationEvent.Type.STATUS)) {
			if (this.isRunning) {
				this.collect();
				Simulator.scheduleEvent(null, Simulator.getCurrentTime()
						+ this.interval, this, SimulationEvent.Type.STATUS);
			}
		} else {
			super.eventOccurred(se);
		}
	}

	/**
	 * Sets the interval of periodic memory status events.
	 * 
	 * @param time
	 *            the interval of periodic memory status events
	 * 
	 */
	public void setInterval(long time) {
		this.interval = time;
	}

	@Override
	public void start() {
		this.isRunning = true;
		this.oldTime = System.currentTimeMillis();
		this.collect();
		Simulator.scheduleEvent(null, Simulator.getCurrentTime()
				+ this.interval, this, SimulationEvent.Type.STATUS);
		super.start();
	}

	@Override
	public void stop(Writer SysOutput) {
		this.isRunning = false;
		this.collect();
		super.stop(output);
	}

}
