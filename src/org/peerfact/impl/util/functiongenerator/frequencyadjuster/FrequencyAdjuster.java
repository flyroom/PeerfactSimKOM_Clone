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

package org.peerfact.impl.util.functiongenerator.frequencyadjuster;

import org.apache.log4j.Logger;
import org.peerfact.api.simengine.SimulationEventHandler;
import org.peerfact.impl.simengine.SimulationEvent;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.util.functiongenerator.functions.Function;
import org.peerfact.impl.util.logging.SimLogger;


/**
 * The abstract FrequencyAdjuster provides a class to implement simulation
 * events that modify the frequency of a specific function.
 * 
 * @author Fabio Zöllner <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public abstract class FrequencyAdjuster implements SimulationEventHandler {
	private static final Logger log = SimLogger
			.getLogger(FrequencyAdjuster.class);

	private long start = 0;

	private Function function = null;

	private long interval = 0;

	private long executionCounter = 1;

	private long maxExecutions = 0;

	private long stop = -1; // -1 to allow a stop at 0 ms sim time

	public FrequencyAdjuster() {
		/* Required for instantiation by a configurator */
	}

	/**
	 * Instantiates a new frequency adjuster
	 * 
	 * @param simTime
	 *            Simulation time at which the frequency adjuster shall be
	 *            executed
	 * @param function
	 *            Function to be changed
	 * @param interval
	 *            Interval in which the frequency shall be adjusted (0 =
	 *            non-repeating)
	 */
	public FrequencyAdjuster(long simTime, Function function, long interval) {
		this.start = simTime;
		this.function = function;
		this.interval = interval;
	}

	/**
	 * Schedules the frequency adjuster with the current start time
	 */
	public void schedule() {
		scheduleAdjustment(this.start);
	}

	/**
	 * Schedules the frequency adjuster with the given start time
	 * 
	 * @param simTime
	 *            Simulation time at which the frequency shall be adjusted
	 */
	private void scheduleAdjustment(long simTime) {
		Simulator.scheduleEvent(null, simTime, this, null);
	}

	@Override
	public void eventOccurred(SimulationEvent se) {
		Simulator.getInstance();
		if (interval != 0
				&& !(maxExecutions > 0 && executionCounter >= maxExecutions)
				&& !(stop >= 0 && Simulator.getCurrentTime() >= stop)) {
			Simulator.getInstance();
			scheduleAdjustment(Simulator.getCurrentTime()
					+ interval);
		}

		if (this.function != null) {
			adjustFrequency();
			this.executionCounter++;
		} else {
			log.warn(new StringBuilder(
					"Missing function, couldn't adjust frequency at ")
			.append(this.start)
			.append(" with ")
			.append(this.getClass().getSimpleName())
			.toString());
		}
	}

	/**
	 * This method is called when the event occurred and the frequency needs to
	 * be changed.
	 */
	protected abstract void adjustFrequency();

	public void setStart(long simTime) {
		this.start = simTime;
	}

	public long getStart() {
		return this.start;
	}

	public void setInterval(long interval) {
		this.interval = interval;
	}

	public long getInterval() {
		return this.interval;
	}

	public void setFunction(Function function) {
		this.function = function;
	}

	public Function getFunction() {
		return this.function;
	}

	@Override
	public String toString() {
		StringBuilder strBuilder = new StringBuilder(this.getClass()
				.getSimpleName())
		.append(" [")
		.append("start=")
		.append(this.start)
		.append(", interval=")
		.append(this.interval)
		.append(", function=")
		.append(this.function != null ? this.function.getClass()
				.getSimpleName() : "null")
				.append("]");

		return strBuilder.toString();
	}

	public long getMaxExecutions() {
		return maxExecutions;
	}

	public void setMaxExecutions(long maxExecutions) {
		this.maxExecutions = maxExecutions;
	}

	public long getStop() {
		return stop;
	}

	public void setStop(long stop) {
		this.stop = stop;
	}

	protected double getFrequency() {
		return this.function.getFrequency();
	}

	protected void setFrequency(double frequency) {
		this.function.setFrequencyDouble(frequency);
	}
}
