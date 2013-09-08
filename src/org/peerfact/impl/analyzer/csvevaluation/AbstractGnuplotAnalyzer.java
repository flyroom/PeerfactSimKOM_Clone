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

package org.peerfact.impl.analyzer.csvevaluation;

import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.peerfact.api.analyzer.Analyzer;
import org.peerfact.api.scenario.ConfigurationException;
import org.peerfact.impl.analyzer.csvevaluation.ResultsWriter.FileAction;
import org.peerfact.impl.analyzer.csvevaluation.metrics.Metric;
import org.peerfact.impl.analyzer.csvevaluation.metrics.Time;
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
 * 
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public abstract class AbstractGnuplotAnalyzer implements Analyzer {

	final public Logger log = Logger
			.getLogger(AbstractGnuplotAnalyzer.class);

	private long interval = -1;

	private long end = -1;

	private long start = 0;

	FileAction fileAction = FileAction.increment;

	private long intervalCounter = 0;

	private List<Metric> metrics = new ArrayList<Metric>();

	private ResultsWriter writer;

	private String outputFile = "output";

	Metric time = new Time();

	@Override
	public void start() {
		log.debug("Starting gnuplot analyzer...");
		start = Simulator.getCurrentTime();
		if (end == -1)
		{
			end = Simulator.getEndTime(); // Default time: End time
		}
		if (interval <= 0) {
			throw new ConfigurationException(
					"Interval has to be set properly for analyzing");
		}
		addMetric(time);
		declareMetrics();
		writer = new ResultsWriter(outputFile, fileAction);
		writer.writeHeader(getAllMetricsNames());

	}

	/**
	 * Here you should declare your metrics and add them with using
	 * addMetrics(Metric m)
	 */
	protected abstract void declareMetrics();

	protected void addMetric(Metric m) {
		metrics.add(m);
	}

	/**
	 * Has to be called BEFORE any event is being processed to check if the next
	 * interval has begun.
	 */
	public void checkTimeProgress() {
		if (!isActive()) {
			return;
		}
		// logger.debug("CheckTimeProgress");
		progressIntervals(Simulator.getCurrentTime());
	}

	public void progressIntervals(long progressTime) {
		int i = 0;

		while (timeFromIntervalCount(intervalCounter + i) <= progressTime) {
			writer.writeDataSet(getMeasurementsFor(timeFromIntervalCount(intervalCounter
					+ i)));
			i++;
		}
		intervalCounter = intervalCounter + i;
	}

	public long timeFromIntervalCount(long intervalCount) {
		return start + intervalCount * interval;
	}

	/**
	 * Returns if the analyzer is supposed to do measurement.
	 */
	protected boolean isActive() {
		long actualSimTime = Simulator.getCurrentTime();
		// logger.debug("Time Sim: " + actualSimTime + ", TimeEnd = " + end);
		return (actualSimTime >= start && actualSimTime <= end);
	}

	private List<String> getMeasurementsFor(long timepoint) {
		List<String> result = new ArrayList<String>();
		for (Metric m : metrics) {
			result.add(m.getMeasurementFor(timepoint));
		}
		return result;
	}

	private List<String> getAllMetricsNames() {
		List<String> result = new ArrayList<String>();
		for (Metric m : metrics) {
			result.add(m.getName());
		}
		return result;
	}

	@Override
	public void stop(Writer output) {
		progressIntervals(end);
		writer.finish();
	}

	/**
	 * Sets the interval in milliseconds
	 * 
	 * @param ms
	 */
	public void setInterval(long interval) {
		this.interval = interval; // Milliseconds to simulation time
		log.debug("Setting interval to " + interval);
	}

	public void setStart(long start) {
		this.start = start;
	}

	public void setEnd(long end) {
		this.end = end;
	}

	public void setOutputFile(String fileName) {
		this.outputFile = fileName;
	}

	public void setAction(String action) {
		fileAction = FileAction.valueOf(action);
	}

}
