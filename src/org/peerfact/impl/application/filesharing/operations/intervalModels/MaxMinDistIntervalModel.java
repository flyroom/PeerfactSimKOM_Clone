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

package org.peerfact.impl.application.filesharing.operations.intervalModels;

import org.apache.commons.math.random.RandomGenerator;
import org.peerfact.impl.application.filesharing.operations.AbstractPeriodicFilesharingOperation.IntervalModel;
import org.peerfact.impl.simengine.Simulator;


/**
 * Interval model that produces equally distributed intervals between a maximum
 * and a minimum value
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class MaxMinDistIntervalModel implements IntervalModel {

	private long maxInterval;

	private long minInterval;

	static RandomGenerator rand = Simulator.getRandom();

	/**
	 * Creates a new MaxMinDistIntervalModel with the given parameters.
	 * 
	 * @param minInterval
	 *            : the minimum value the model may return as an interval.
	 * @param maxInterval
	 *            : the maximum value the model may return as an interval.
	 */
	public MaxMinDistIntervalModel(long minInterval, long maxInterval) {
		this.minInterval = minInterval;
		this.maxInterval = maxInterval;
	}

	@Override
	public long getNewDelay() {
		long difference = maxInterval + minInterval;
		return minInterval + (long) (rand.nextDouble() * difference);

	}

}
