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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.peerfact.api.common.Host;
import org.peerfact.api.common.Message;
import org.peerfact.api.network.Bandwidth;
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
public class BandwidthConsumption {

	static final int DECIMALS = 3;

	static final int MULTIPLICATOR = 100; // value in percent.

	Map<NetID, Bandwidth> bwOfHosts = new LinkedHashMap<NetID, Bandwidth>();

	Map<NetID, BWAccumulator> accumulators = new LinkedHashMap<NetID, BWAccumulator>();

	AvgDn avgDn = new AvgDn();

	PeakDn peakDn = new PeakDn();

	AvgUp avgUp = new AvgUp();

	PeakUp peakUp = new PeakUp();

	private long lastResultGenerationTime = -1;

	private boolean resultGenerated;

	double resultPeakUp;

	double resultPeakDown;

	double resultAvgUp;

	double resultAvgDn;

	public void init() {
		Map<String, List<Host>> hosts = Simulator.getInstance().getScenario()
				.getHosts();
		for (List<Host> group : hosts.values()) {
			for (Host host : group) {
				double upBW = host.getNetLayer().getMaxBandwidth().getUpBW();
				double downBW = host.getNetLayer().getMaxBandwidth()
						.getDownBW();
				NetID netID = host.getNetLayer().getNetID();
				bwOfHosts.put(netID, new Bandwidth(downBW, upBW));
				accumulators.put(netID, new BWAccumulator());
			}
		}
	}

	public void messageSent(NetID id, Message msg) {
		continueMeasurement();
		accumulators.get(id).incConsumedUp(msg.getSize());
	}

	public void messageReceived(NetID id, Message msg) {
		continueMeasurement();
		accumulators.get(id).incConsumedDown(msg.getSize());
	}

	public Metric getAvgDn() {
		return avgDn;
	}

	public Metric getAvgUp() {
		return avgUp;
	}

	public Metric getPeakDn() {
		return peakDn;
	}

	public Metric getPeakUp() {
		return peakUp;
	}

	public void continueMeasurement() {
		resultGenerated = false;
	}

	public void generateResultCond() {

		if (resultGenerated == true) {
			return;
		}

		double tempPeakUp = 0;
		double tempPeakDn = 0;
		double sumUp = 0;
		double sumDn = 0;

		for (Entry<NetID, BWAccumulator> e : accumulators.entrySet()) {
			Bandwidth bw = bwOfHosts.get(e.getKey());
			BWAccumulator a = e.getValue();
			double up = a.getConsumedUp() / bw.getUpBW();
			double down = a.getConsumedDown() / bw.getDownBW();
			if (up > tempPeakUp) {
				tempPeakUp = up;
			}
			if (down > tempPeakDn) {
				tempPeakDn = down;
			}
			sumUp += up;
			sumDn += down;
			a.reset();
		}

		long currentTime = Simulator.getCurrentTime();

		double timeInterval = (currentTime - lastResultGenerationTime)
				/ (double) Simulator.SECOND_UNIT;

		resultPeakUp = tempPeakUp / timeInterval;
		resultPeakDown = tempPeakDn / timeInterval;
		resultAvgUp = sumUp / timeInterval / accumulators.size();
		resultAvgDn = sumDn / timeInterval / accumulators.size();

		resultGenerated = true;
		this.lastResultGenerationTime = currentTime;
	}

	public class AvgUp implements Metric {

		@Override
		public String getMeasurementFor(long time) {
			generateResultCond();
			return formatNumber(resultAvgUp);
		}

		@Override
		public String getName() {
			return "AvgUpBWCons(%)";
		}

	}

	public class AvgDn implements Metric {

		@Override
		public String getMeasurementFor(long time) {
			generateResultCond();
			return formatNumber(resultAvgDn);
		}

		@Override
		public String getName() {
			return "AvgDnBWCons(%)";
		}

	}

	public class PeakUp implements Metric {

		@Override
		public String getMeasurementFor(long time) {
			generateResultCond();
			return formatNumber(resultPeakUp);
		}

		@Override
		public String getName() {
			return "PeakUpBWCons(%)";
		}

	}

	public class PeakDn implements Metric {

		@Override
		public String getMeasurementFor(long time) {
			generateResultCond();
			return formatNumber(resultPeakDown);
		}

		@Override
		public String getName() {
			return "PeakDnBWCons(%)";
		}

	}

	static class BWAccumulator {

		public long getConsumedUp() {
			return consumedUp;
		}

		public void incConsumedUp(long inc) {
			this.consumedUp += inc;
		}

		public long getConsumedDown() {
			return consumedDown;
		}

		public void incConsumedDown(long inc) {
			this.consumedDown += inc;
		}

		public void reset() {
			consumedUp = 0;
			consumedDown = 0;
		}

		long consumedUp = 0;

		long consumedDown = 0;

	}

	static String formatNumber(double number) {
		return NumberFormatToolkit.floorToDecimalsString(
				number * MULTIPLICATOR, DECIMALS);
		// return String.valueOf(number*MULTIPLICATOR);
	}

}
