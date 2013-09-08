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

import org.peerfact.impl.application.filesharing.operations.AbstractPeriodicFilesharingOperation.IntervalModel;
import org.peerfact.impl.simengine.Simulator;

/**
 * 
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public class LinearIntervalModel implements IntervalModel {

	private long timeOffset;

	private long startOffset;

	private double b;

	private double m;

	private long interval;

	public LinearIntervalModel(double startRequestsPerHour,
			double endRequestsPerHour, long interval) {
		this.timeOffset = Simulator.getCurrentTime();

		this.interval = interval;
		double y1 = Simulator.HOUR_UNIT / startRequestsPerHour;
		double y2 = Simulator.HOUR_UNIT / endRequestsPerHour;
		this.b = y1;
		this.startOffset = (Math.abs(Simulator.getRandom().nextLong()) % (long) Math
				.abs(y1));
		this.m = (y2 - y1) / interval;
	}

	@Override
	public long getNewDelay() {
		long delay = (long) (m * (Simulator.getCurrentTime() - timeOffset))
				- startOffset + (long) b;
		// only once
		this.startOffset = 0;
		if ((Simulator.getCurrentTime() - timeOffset) > interval) {
			delay = (long) (m * interval) + (long) b;
		}
		return delay;
	}
}
