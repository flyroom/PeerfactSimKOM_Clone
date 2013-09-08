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

import org.apache.log4j.Logger;
import org.peerfact.api.service.aggr.AggregationResult;
import org.peerfact.impl.service.aggregation.DefaultAggregationResult;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.util.logging.SimLogger;


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
 * A floating-point value stored locally, along with all gossiping aggregation
 * metadata needed by the protocol to let the value converge to the aggregation
 * result.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class GossipingAggregationValue {

	static final Logger log = SimLogger
			.getLogger(GossipingAggregationValue.class);

	double localVal;

	// values rendered in the current gossiping step;
	double curAvg = Double.NaN;

	double curMin = Double.NaN;

	double curMax = Double.NaN;

	double curVar = Double.NaN;

	long curMinTime = Long.MAX_VALUE;

	long curMaxTime = Long.MIN_VALUE;

	long curAvgTime = 0;

	// ...

	// values rendered in the last gossiping process step
	double lastAvg;

	double lastMin;

	double lastMax;

	double lastVar;

	private long lastMinTime;

	private long lastMaxTime;

	private long lastAvgTime;

	// ...

	private long globalAggregateTimestamp;

	private GossipingAggregationService parent;

	@Override
	public String toString() {
		return "(localVal=" + localVal + ",curAvg=" + curAvg + ",curMin="
				+ curMin + ",curMax=" + curMax + ",curVar=" + curVar
				+ ",curMinTime=" + curMinTime + ",curMaxTime=" + curMaxTime
				+ ",curAvgTime=" + curAvgTime + ",lastAvg=" + lastAvg
				+ ",lastMin=" + lastMin + ",lastMax=" + lastMax + ",lastVar="
				+ lastVar + ",lastMinTime=" + lastMinTime + ",lastMaxTime="
				+ lastMaxTime + ",lastAvgTime=" + lastAvgTime
				+ ",globalAggregateTime=" + globalAggregateTimestamp + ")";
	}

	public GossipingAggregationValue(double localVal,
			GossipingAggregationService parent) {
		this.parent = parent;
		this.localVal = localVal;
		restart();
		log.debug("Created " + this + " from local value " + localVal);
	}

	private GossipingAggregationValue(UpdateInfo info,
			GossipingAggregationService parent) {
		this.parent = parent;
		this.localVal = Double.NaN;
		this.curAvg = info.getAvg();
		this.curMin = info.getMin();
		this.curMax = info.getMax();
		this.curVar = info.getVar();
		this.curMinTime = info.getMinTime();
		this.curMaxTime = info.getMaxTime();
		this.curAvgTime = info.getAvgTime();
		log.debug("Created " + this + " from foreign info " + info);
	}

	public void restart() {

		// "output" current values
		lastAvg = curAvg;
		lastMin = curMin;
		lastMax = curMax;
		lastVar = curVar;
		lastMinTime = curMinTime;
		lastMaxTime = curMaxTime;
		lastAvgTime = curAvgTime;
		globalAggregateTimestamp = Simulator.getCurrentTime();
		// ...

		// reinitialize current values
		if (!Double.isNaN(localVal)) {
			curAvg = localVal;
			curMin = localVal;
			curMax = localVal;
			double curVarSqrt = localVal
					- (Double.isNaN(lastAvg) ? curAvg : lastAvg);
			curVar = curVarSqrt * curVarSqrt;
			curMinTime = Simulator.getCurrentTime();
			curMaxTime = Simulator.getCurrentTime();
			curAvgTime = Simulator.getCurrentTime();
			// ...
		}
	}

	long getEpoch() {
		return parent.getSync().getEpoch();
	}

	public void merge(UpdateInfo info2merge, String dbgNote) {

		// log.debug("Merging " + this + " with " + info2merge);
		curAvg = (curAvg + info2merge.getAvg()) / 2d;
		curMax = Math.max(curMax, info2merge.getMax());
		curMin = Math.min(curMin, info2merge.getMin());
		curVar = (curVar + info2merge.getVar()) / 2d;
		curMinTime = Math.min(curMinTime, info2merge.getMinTime());
		curMaxTime = Math.max(curMaxTime, info2merge.getMaxTime());
		// (counterL * avgL + counteU * avgU) / (counterL + counterU)
		curAvgTime = curAvgTime + info2merge.getAvgTime() / 2;

	}

	public UpdateInfo extractInfo() {
		return new UpdateInfo(curAvg, curMin, curMax, curVar, curMinTime,
				curMaxTime, curAvgTime);
	}

	public static GossipingAggregationValue fromInfo(UpdateInfo info,
			GossipingAggregationService parent) {
		return new GossipingAggregationValue(info, parent);
	}

	public void updateValue(double value) {
		this.localVal = value;
	}

	public double getValue() {
		return this.localVal;
	}

	/*
	 * private int getAverageNC() { double countAvgAcc = 0d; for (Double val :
	 * ncVals.values()) { countAvgAcc += val; //log.debug(1/val); //debug } int
	 * c = ncVals.size(); int result = (int)Math.round(c==0?-1:(c/countAvgAcc));
	 * return result; }
	 */

	public AggregationResult getAggregationResult() {
		if (Double.isNaN(lastAvg)) {
			return new DefaultAggregationResult(curMin, curMax, curAvg, curVar,
					parent.getGossipingNodeCountValue().getNC(), curMinTime,
					curMaxTime, curAvgTime);
		}
		return new DefaultAggregationResult(lastMin, lastMax, lastAvg, lastVar,
				parent.getGossipingNodeCountValue().getLastNC(), lastMinTime,
				lastMaxTime, lastAvgTime);
	}

	public long getGlobalAggregationTimestamp() {
		if (Double.isNaN(lastAvg)) {
			return Simulator.getCurrentTime(); // actually value as
												// globalAggregate
		}
		return globalAggregateTimestamp;
	}

	public void setGlobalAggregationTimestamp(long time) {
		globalAggregateTimestamp = time;
	}

}
