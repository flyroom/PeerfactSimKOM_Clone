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

package org.peerfact.impl.service.aggregation.gossip;

import java.util.Set;

import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.util.LiveMonitoring;
import org.peerfact.impl.util.LiveMonitoring.ProgressValue;
import org.peerfact.impl.util.livemon.AvgAccumulatorDouble;
import org.peerfact.impl.util.timeoutcollections.TimeoutSet;
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
 * 
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public class Monitoring {

	static AvgAccumulatorDouble nbrC = new AvgAccumulatorDouble(
			"Gossip: Mean neighbor count", 200);

	static TimeoutSet<Integer> lastSeenNCInitiatorsCounts = new TimeoutSet<Integer>(
			10 * Simulator.SECOND_UNIT);

	static volatile Object lock = new Object();

	public static void register() {
		LiveMonitoring.addProgressValueIfNotThere(nbrC);
		LiveMonitoring.addProgressValue(new ProgressValue() {

			@Override
			public String getValue() {
				return String.valueOf(getAverage(lastSeenNodeCounts));
			}

			@Override
			public String getName() {
				return "Gossip: Average seen node count";
			}
		});

		LiveMonitoring.addProgressValue(new ProgressValue() {

			@Override
			public String getValue() {
				return String.valueOf(getAverage(lastSeenNCInitiatorsCounts));
			}

			@Override
			public String getName() {
				return "Gossip: Average seen NC initiators count";
			}
		});

		LiveMonitoring.addProgressValue(new ProgressValue() {

			@Override
			public String getValue() {
				long totalRPCs = successfulRPCs + unsuccessfulRPCs;
				return "Succ.: "
						+ successfulRPCs
						+ ", Unsucc.: "
						+ unsuccessfulRPCs
						+ ", Succ. Quota: "
						+ (totalRPCs == 0 ? "n/a" : NumberFormatToolkit
								.formatPercentage(successfulRPCs
										/ (double) totalRPCs, 2));
			}

			@Override
			public String getName() {
				return "Gossip: RPC Success";
			}
		});
	}

	public static void onNeighborCountSeen(int neighbors) {
		nbrC.newVal(neighbors);
	}

	public static String getAverage(TimeoutSet<Integer> timeoutset) {
		synchronized (lock) {
			Set<Integer> set = timeoutset.getUnmodifiableSet();
			if (set.size() <= 0) {
				return "n/a";
			}
			int all = 0;
			for (int val : set) {
				all += val;
			}
			return NumberFormatToolkit.floorToDecimalsString(
					all / (double) set.size(), 2);
		}
	}

	public static void addNCInitiatorCount(int count) {
		synchronized (lock) {
			lastSeenNCInitiatorsCounts.addNow(count);
		}
	}

	public static void addNodeCount(int count) {
		synchronized (lock) {
			lastSeenNodeCounts.addNow(count);
		}
	}

	static TimeoutSet<Integer> lastSeenNodeCounts = new TimeoutSet<Integer>(
			10 * Simulator.SECOND_UNIT);

	static long successfulRPCs = 0;

	static long unsuccessfulRPCs = 0;

	public static void addSuccessfulRPC() {
		successfulRPCs++;
	}

	public static void addUnsuccessfulRPC() {
		unsuccessfulRPCs++;
	}

}
