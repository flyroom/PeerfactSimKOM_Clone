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

package org.peerfact.impl.service.aggregation;

import org.peerfact.api.common.Host;
import org.peerfact.api.service.aggr.AggregationResult;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.util.toolkits.TimeToolkit;

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
public class AggregationToolkit {

	static final String separator = "\t";

	static final TimeToolkit tk;

	static {
		tk = new TimeToolkit(Simulator.MILLISECOND_UNIT);
		tk.setSeparateTimeUnitsWhitespace(false);
	}

	public static String printResultCSV(Host host, long time, long duration,
			AggregationResult res) {
		return host.getNetLayer().getNetID() + separator
				+ tk.timeStringFromLong(time) + separator
				+ tk.timeStringFromLong(duration) + separator
				+ "succ" + separator
				+ res.getAverage() + separator
				+ res.getMaximum() + separator
				+ res.getMinimum() + separator
				+ res.getVariance() + separator
				+ res.getNodeCount();
	}

	public static String printDescLineCSV() {
		return "#host" + separator
				+ "time" + separator
				+ "duration" + separator
				+ "status" + separator
				+ "mean" + separator
				+ "max" + separator
				+ "min" + separator
				+ "variance" + separator
				+ "count";
	}

	public static String printResultFailedCSV(Host host, long time,
			long duration) {
		return host.getNetLayer().getNetID() + separator
				+ tk.timeStringFromLong(time) + separator
				+ tk.timeStringFromLong(duration) + separator
				+ "fail" + separator
				+ "NaN" + separator
				+ "NaN" + separator
				+ "NaN" + separator
				+ "NaN" + separator
				+ "NaN";
	}

}
