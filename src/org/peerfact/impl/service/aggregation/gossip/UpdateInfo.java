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
 * Information about every gossiping aggregation value that is carried on
 * messages between nodes.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class UpdateInfo {

	double avg;

	double min;

	double max;

	double var;

	long minTime;

	long maxTime;

	long avgTime;

	/**
	 * @return the current estimation of the minimum
	 */
	public double getMin() {
		return min;
	}

	/**
	 * @return the current estimation of the maximum
	 */
	public double getMax() {
		return max;
	}

	/**
	 * @return the current estimation of the average
	 */
	public double getAvg() {
		return avg;
	}

	/**
	 * @return the current estimation of the variance
	 */
	public double getVar() {
		return var;
	}

	/**
	 * @return the oldest time of this aggregate
	 */
	public long getMinTime() {
		return minTime;
	}

	/**
	 * @return the newest time of this aggregate
	 */
	public long getMaxTime() {
		return maxTime;
	}

	/**
	 * @return the average time of this aggregate
	 */
	public long getAvgTime() {
		return avgTime;
	}

	public UpdateInfo(double avg, double min, double max, double var,
			long minTime, long maxTime, long avgTime) {
		this.avg = avg;
		this.min = min;
		this.max = max;
		this.var = var;
		this.minTime = minTime;
		this.maxTime = maxTime;
		this.avgTime = avgTime;
	}

	/**
	 * @return the size in bytes of this UpdateInfo
	 */
	public static long getSize() {
		return 56;
	}

	@Override
	public String toString() {
		return "(avg=" + avg + ",min=" + min + ",max=" + max + ",var=" + var
				+ ",minTime=" + minTime + ",maxTime=" + maxTime + ",avgTime="
				+ avgTime + ")";
	}

}
