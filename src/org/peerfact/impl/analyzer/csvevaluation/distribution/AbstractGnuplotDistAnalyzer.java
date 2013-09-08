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

package org.peerfact.impl.analyzer.csvevaluation.distribution;

import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.peerfact.api.analyzer.Analyzer;
import org.peerfact.api.network.NetID;
import org.peerfact.api.scenario.ConfigurationException;
import org.peerfact.impl.analyzer.csvevaluation.AbstractGnuplotAnalyzer;
import org.peerfact.impl.analyzer.csvevaluation.ResultsWriter;
import org.peerfact.impl.analyzer.csvevaluation.ResultsWriter.FileAction;
import org.peerfact.impl.analyzer.csvevaluation.distribution.IDistribution.IDistResultStream;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.util.toolkits.NumberFormatToolkit;


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
 * Creates a three-dimensional distribution plot of a value at each host over
 * time
 * 
 * @author <info@peerfact.org>
 * 
 * @version 05/06/2011
 */
public abstract class AbstractGnuplotDistAnalyzer implements Analyzer {

	static final Logger logger = Logger
			.getLogger(AbstractGnuplotAnalyzer.class);

	protected final long TIME_UNIT_OUTPUT = 1000000; // (1 sec);

	private long interval = -1;

	private long end = -1;

	private long start = 0;

	private long intervalCounter = 0;

	ResultsWriter writer;

	private String outputFile = "output";

	FileAction fileAction = FileAction.increment;

	private List<IDistribution<NetID>> dists = new ArrayList<IDistribution<NetID>>();

	private List<String> distNames = new ArrayList<String>();

	@Override
	public void start() {
		logger.debug("Starting up gnuplot analyzer...");
		if (end == -1)
		{
			end = Simulator.getEndTime(); // Default time: End time
		}
		if (interval <= 0) {
			throw new ConfigurationException(
					"Interval has to be set properly for analyzing");
		}
		writer = new ResultsWriter(outputFile, fileAction);
		declareDistributions();
		writer.writeHeader(getHeader());

	}

	protected abstract void declareDistributions();

	protected void addDistribution(String distName) {
		distNames.add(distName);
		dists.add(new Distribution<NetID>());
	}

	public List<String> getHeader() {
		List<String> hdr = new ArrayList<String>();
		hdr.add("Time");
		hdr.add("Distribution");
		hdr.add("Value");
		for (String distName : distNames) {
			hdr.add(distName);
		}

		return hdr;
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

	private void progressIntervals(long progressTime) {
		int i = 0;

		while (timeFromIntervalCount(intervalCounter + i + 1) <= progressTime) {
			createDataSet(timeFromIntervalCount(intervalCounter + i));
			resetDistributions();
			i++;
		}
		intervalCounter = intervalCounter + i;
	}

	public long timeFromIntervalCount(long intervalCount) {
		return start + intervalCount * interval;
	}

	protected void createDataSet(long time) {

		List<IDistResultStream> streams = new ArrayList<IDistResultStream>();

		for (IDistribution<?> dist : dists) {
			streams.add(dist.getResultStream());
		}

		assert (streams.size() > 0) : "There was no distribution declared for the analyzer.";

		int size = streams.get(0).getDistSize();
		for (IDistResultStream stream : streams) {
			if (stream.getDistSize() != size) {
				throw new AssertionError(
						"Illegal state: Inequal distribution size :" + size
								+ " and " + stream.getDistSize());
			}
		}

		for (int i = 0; i < size; i++) {
			List<String> set = new ArrayList<String>();

			set.add(String.valueOf(time / TIME_UNIT_OUTPUT));
			set.add(NumberFormatToolkit.floorToDecimalsString(
					(double) i / size, 4));
			for (IDistResultStream s : streams) {
				set.add(modifyResultValue(s.getNextValue()));
			}

			writer.writeDataSet(set);
		}

	}

	protected void resetDistributions() {
		dists = new ArrayList<IDistribution<NetID>>();
		for (int i = 0; i < distNames.size(); i++) {
			dists.add(new Distribution<NetID>());
		}
	}

	/**
	 * Returns if the analyzer is supposed to do measurement.
	 */
	protected boolean isActive() {
		long actualSimTime = Simulator.getCurrentTime();
		// logger.debug("Time Sim: " + actualSimTime + ", TimeEnd = " + end);
		return (actualSimTime >= start && actualSimTime <= end);
	}

	@Override
	public void stop(Writer output) {
		progressIntervals(end);
		writer.finish();
	}

	/**
	 * Sets the interval in simulation units
	 * 
	 * @param ms
	 */
	public void setInterval(long interval) {
		this.interval = interval;
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

	protected void addHostOrUpdateAll(NetID host, long[] initialValues) {
		if (!(initialValues.length == dists.size())) {
			throw new AssertionError(
					"The selected initial values are not equal to the declared distributions: "
							+ initialValues.length + " != " + dists.size());
		}
		int i = 0;
		for (IDistribution<NetID> dist : dists) {
			dist.setValue(host, initialValues[i]);
			i++;
		}
	}

	protected void updateHost(NetID host, int field, long value) {
		dists.get(field).setValue(host, value);
	}

	protected void removeHost(NetID host) {
		for (IDistribution<NetID> dist : dists) {
			dist.remove(host);
		}
	}

	/**
	 * Modifies the displayed result e.g. to divide it with the time interval.
	 * 
	 * @param result
	 * @return
	 */
	protected abstract String modifyResultValue(long result);

	public long getInterval() {
		return interval;
	}

}
