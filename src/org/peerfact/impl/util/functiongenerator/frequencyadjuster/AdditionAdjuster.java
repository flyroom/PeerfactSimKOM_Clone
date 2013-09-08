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

import org.peerfact.impl.util.functiongenerator.functions.Function;

/**
 * This frequency adjuster increases the current frequency of a function by a
 * given amount.
 * 
 * @author Fabio Zöllner <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class AdditionAdjuster extends FrequencyAdjuster {
	private double value = 0;

	public AdditionAdjuster() {
		/* Required for instantiation by a configurator */
	}

	/**
	 * Instantiates a new addition adjuster
	 * 
	 * @param simTime
	 *            Simulation time at which the frequency adjuster shall be
	 *            executed
	 * @param function
	 *            Function to be changed
	 * @param value
	 *            Value by which the frequency is increased
	 * @param interval
	 *            Interval in which the frequency shall be adjusted (0 =
	 *            non-repeating)
	 */
	public AdditionAdjuster(long simTime, Function function, double value,
			long interval) {
		super(simTime, function, interval);

		this.value = value;
	}

	/**
	 * Instantiates a new non-repeating addition adjuster
	 * 
	 * @param simTime
	 *            Simulation time at which the frequency adjuster shall be
	 *            executed
	 * @param function
	 *            Function to be changed
	 * @param value
	 *            Value by which the frequency is increased
	 */
	public AdditionAdjuster(long simTime, Function function, double value) {
		super(simTime, function, 0);

		this.value = value;
	}

	@Override
	protected void adjustFrequency() {
		setFrequency(getFrequency() + this.value);
	}

	/**
	 * Sets the value by which the frequency is increased
	 * 
	 * @param value
	 *            Value by which the frequency is increased
	 */
	public void setValue(double value) {
		this.value = value;
	}

	/**
	 * Returns the value by which the frequency is increased
	 * 
	 * @return Value by which the frequency is increased
	 */
	public double getValue() {
		return this.value;
	}

	@Override
	public String toString() {
		StringBuilder strBuilder = new StringBuilder(this.getClass()
				.getSimpleName())
				.append(" [")
				.append("start=")
				.append(super.getStart())
				.append(", interval=")
				.append(super.getInterval())
				.append(", value=")
				.append(this.value)
				.append(", function=")
				.append(super.getFunction() != null ? super.getFunction()
						.getClass().getSimpleName() : "null")
				.append("]");

		return strBuilder.toString();
	}
}
