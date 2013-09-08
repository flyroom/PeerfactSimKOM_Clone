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
 * This churn model is adapted from the technical report of Moritz Steiner et
 * al.: "Analyzing Peer Behavior in KAD". The authors define the session as the
 * time a host was present in the system without an interruption whereas the
 * inter-session time is defined as the time a host is continuously absent from
 * the system.
 * 
 * The weibull distributions used in this churn model result from a distribution
 * fit of the measured date of about six month in KAD:
 * 
 * Session time distribution (Weibull) - First crawl :
 * mean=670.6789,std=1741.3677,scale=357.7152,shape=0.54512
 * 
 * - Up from 2nd crawl: mean=266.5358,std=671.5063,scale=169.5385,shape=0.61511
 * 
 * Inter-session time distribution (Weibull) - First crawl :
 * mean=1110.2091,std=4308.0877,scale=413.6765,shape=0.47648
 * 
 * @author Sebastian Kaune <peerfact@kom.tu-darmstadt.de>
 * @version 3.0, 18.03.2008
 * 
 */
public class KadChurnModel implements ChurnModel {

	public WeibullDistributionImpl sessionTime, interSessionTime;

	public KadChurnModel() {
		sessionTime = new WeibullDistributionImpl(0.61511, 169.5385);
		interSessionTime = new WeibullDistributionImpl(0.47648, 413.6765);
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
