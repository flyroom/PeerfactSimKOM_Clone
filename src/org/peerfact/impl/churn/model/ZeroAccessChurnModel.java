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

package org.peerfact.impl.churn.model;

import java.util.List;

import org.apache.commons.math.distribution.WeibullDistributionImpl;
import org.peerfact.api.churn.ChurnModel;
import org.peerfact.api.common.Host;
import org.peerfact.impl.simengine.Simulator;

/**
 * 
 * @author flyroom <flyroomnuaa@gmail.com>
 * @version 1.0, 11.04.2013
 * 
 */
public class ZeroAccessChurnModel implements ChurnModel {

	public WeibullDistributionImpl sessionTime, interSessionTime;

	public ZeroAccessChurnModel() {
		sessionTime = new WeibullDistributionImpl(1.098592, 50.7444);
		interSessionTime = new WeibullDistributionImpl(5.8533, 181.004);
	}

	@Override
	public long getNextDowntime(Host host) {
		long time = Math.round(sessionTime
				.inverseCumulativeProbability(Simulator.getRandom()
						.nextDouble()))
				* Simulator.MINUTE_UNIT;
		return time;
	}

	@Override
	public long getNextUptime(Host host) {
		long time = Math.round(interSessionTime
				.inverseCumulativeProbability(Simulator.getRandom()
						.nextDouble()))
				* Simulator.MINUTE_UNIT;
		return time;
	}

	@Override
	public void prepare(List<Host> churnHosts) {
		// nothing to do
	}

}
