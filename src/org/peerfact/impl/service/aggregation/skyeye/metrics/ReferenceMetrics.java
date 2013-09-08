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

package org.peerfact.impl.service.aggregation.skyeye.metrics;

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
 * This class is used by the MetricsCollector class to generate values by using
 * periodic reference functions. these functions are depending on the given
 * period (in simulation time) and the current simulation time. The idea is that
 * all peers generate the same values if the functions are invoked at the same
 * time. The purpose of this is to evaluate the effect of the information age.
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class ReferenceMetrics {

	/**
	 * A simple zig-zag metric. The value is zero for the first half of the
	 * period and one for the second half.
	 * 
	 * @param period
	 * @return
	 */
	public static double simpleZigZag(long period) {
		long modPeriodeT = Simulator.getCurrentTime() % period;

		if (modPeriodeT < (period / 2)) {
			return 0;
		}
		return 1;
	}

	/**
	 * The sine function as everyone knows it from school. It is scaled to fit
	 * the given period.
	 * 
	 * @param period
	 * @return
	 */
	public static double sin(long period) {
		/*
		 * Compute the sine by using the simulation time and a period as given
		 */
		double value = Math.sin((Simulator.getCurrentTime() / (double) period)
				* 2 * Math.PI);

		return value;
	}
}
