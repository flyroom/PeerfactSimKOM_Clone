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

package org.peerfact.impl.analyzer.csvevaluation.metrics;

import java.util.LinkedHashMap;
import java.util.Map;

import org.peerfact.api.common.Message;
import org.peerfact.api.network.NetID;
import org.peerfact.impl.simengine.Simulator;
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
public class Bandwidth {

	Map<NetID, BWInfo> info = new LinkedHashMap<NetID, BWInfo>();

	long totalBW;

	String type;

	AvgBW avg = new AvgBW();

	PeakBW peak = new PeakBW();

	public Bandwidth(String type) {
		this.type = type;
	}

	public AvgBW getAvgBW() {
		return avg;
	}

	public PeakBW getPeakBW() {
		return peak;
	}

	public void addMsg(NetID affectedNode, Message msg) {
		addAndGet(affectedNode).add(msg.getSize());
		totalBW += msg.getSize();
	}

	protected BWInfo addAndGet(NetID contact) {
		BWInfo element = info.get(contact);
		if (element == null) {
			element = new BWInfo();
			info.put(contact, element);
		}
		return element;
	}

	public static class BWInfo {

		public BWInfo() {
			reset();
		}

		public long getBW() {
			return bw;
		}

		long bw;

		public void reset() {
			bw = 0;
		}

		public void add(long bytes) {
			bw += bytes;
		}

	}

	protected static double toPerSeconds(long measurement,
			long measurementBeginTime) {
		long time = Simulator.getCurrentTime();
		long diff = time - measurementBeginTime;

		return measurement / (double) diff * Simulator.SECOND_UNIT;
	}

	public class AvgBW implements Metric {

		long measurementBeginTime = 0;

		@Override
		public String getMeasurementFor(long time) {
			String result;
			if (info.size() <= 0) {
				result = "NaN";
			} else {
				result = NumberFormatToolkit.floorToDecimalsString(
						toPerSeconds(totalBW / info.size(),
								measurementBeginTime), 1);
			}

			totalBW = 0;
			measurementBeginTime = Simulator.getCurrentTime();

			return result;

		}

		@Override
		public String getName() {
			return "avg" + type + "BW";
		}

	}

	public class PeakBW implements Metric {

		long measurementBeginTime = 0;

		@Override
		public String getMeasurementFor(long time) {
			long maximum = 0;

			for (BWInfo bwElem : info.values()) {
				long bw = bwElem.getBW();
				if (bw > maximum) {
					maximum = bw;
				}
			}
			String result = NumberFormatToolkit.floorToDecimalsString(
					toPerSeconds(maximum, measurementBeginTime), 1);

			for (BWInfo bw : info.values()) {
				bw.reset();
			}

			measurementBeginTime = Simulator.getCurrentTime();

			return result;
		}

		@Override
		public String getName() {
			return "peak" + type + "BW";
		}

	}

}
