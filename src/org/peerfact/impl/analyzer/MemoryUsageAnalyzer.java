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
import org.peerfact.impl.simengine.SimulationEvent;
import org.peerfact.impl.simengine.Simulator;

/**
 * Analyzer for the used memory of a simulation with PeerfactSim.KOM.
 * 
 * @author Sebastian Kaune <peerfact@kom.tu-darmstadt.de>
 * @author Konstantin Pussep
 * @author Matthias Feldotto <info@peerfact.org>
 * @version 1.0, 02/13/2013
 * 
 */

public class MemoryUsageAnalyzer extends AbstractFileMetricAnalyzer<Object> {

	/** One megabyte of memory units. */
	public static final double MEGABYTE = 1024 * 1024;

	/**
	 * Metric representing the free memory.
	 */
	private StatisticMetric<Object, Double> freeMemory = new StatisticMetric<Object, Double>(
			"Free Memory", "MB");

	/**
	 * Metric representing the used memory.
	 */
	private StatisticMetric<Object, Double> usedMemory = new StatisticMetric<Object, Double>(
			"Used Memory", "MB");

	/** Denotes if the memory analyzer is activated */
	private boolean isRunning;

	/** Interval of periodic memory status events */
	private long interval;

	public MemoryUsageAnalyzer() {
		setOutputFileName("SimulationMemory");
		setFlushEveryLine(true);
	}

	@Override
	protected void initializeMetrics() {
		addMetric(freeMemory);
		addMetric(usedMemory);
	}

	/**
	 * Collect new java virtual memory usage information and return current
	 * usage.
	 */
	public void collect() {
		Runtime run = Runtime.getRuntime();
		usedMemory.addValue(null, run.totalMemory()
				/ MemoryUsageAnalyzer.MEGABYTE);
		freeMemory.addValue(null, run.freeMemory()
				/ MemoryUsageAnalyzer.MEGABYTE);
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
