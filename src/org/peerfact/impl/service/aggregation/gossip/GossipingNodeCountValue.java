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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.util.Tuple;
import org.peerfact.impl.util.logging.SimLogger;
import org.peerfact.impl.util.toolkits.CollectionHelpers;


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
public class GossipingNodeCountValue {

	static final Logger log = SimLogger
			.getLogger(GossipingNodeCountValue.class);

	private int lastNC;

	/**
	 * whether or not to use the node count debugger, outputting additional
	 * information useful for debugging.
	 */
	static final boolean NC_DEBUG = true;

	Map<Integer, Double> ncVals = new LinkedHashMap<Integer, Double>();

	private GossipingAggregationService parent;

	@Override
	public String toString() {
		return "(lastNC=" + lastNC + ")";
	}

	public GossipingNodeCountValue(GossipingAggregationService parent) {
		this.parent = parent;
		restart();
		log.debug("Created " + this + " from local value ");
		if (NC_DEBUG) {
			NCDebugger.register(this);
		}
	}

	private GossipingNodeCountValue(UpdateInfo info,
			GossipingAggregationService parent) {
		this.parent = parent;
		log.debug("Created " + this + " from foreign info " + info);
		if (NC_DEBUG) {
			NCDebugger.register(this);
		}
	}

	public static GossipingNodeCountValue fromInfo(UpdateInfo info,
			GossipingAggregationService parent) {
		return new GossipingNodeCountValue(info, parent);
	}

	public void restart() {
		lastNC = getNC();
		if (NC_DEBUG) {
			NCDebugger.onNCCalculated(this.getEpoch(), lastNC);
		}
		ncVals.clear();
	}

	public UpdateInfoNodeCount extractInfo() {
		return new UpdateInfoNodeCount(Tuple.tupleListFromMap(ncVals));
	}

	public void merge(UpdateInfoNodeCount info2merge, String dbgNote) {

		Set<Integer> idsOfMergeInfo = new LinkedHashSet<Integer>();
		for (Tuple<Integer, Double> tpl : info2merge.getNCList()) {
			int id = tpl.getA();
			Double oldVal = ncVals.get(id);
			if (oldVal == null) {
				oldVal = 0d;
			}
			double newVal = (tpl.getB() + oldVal) / 2d;
			// log.debug("Merging " + val + " with " + tpl.getB() +
			// " at " + parent.getUID() + ", key " + id);
			ncVals.put(id, newVal);
			if (NC_DEBUG) {
				NCDebugger.valueUpdate(this, id, oldVal, newVal, tpl.getB(),
						dbgNote, getEpoch());
			}
			idsOfMergeInfo.add(id);
		}
		for (Entry<Integer, Double> valE : ncVals.entrySet()) {
			int key = valE.getKey();
			if (!idsOfMergeInfo.contains(key)) {
				double val = valE.getValue();
				double newVal = val / 2d;
				ncVals.put(key, newVal);
				if (NC_DEBUG) {
					NCDebugger.valueUpdate(this, key, val, newVal, null,
							dbgNote, getEpoch());
				}
			}
		}
		if (parent.getSync().getCycle() == parent.getConf()
				.getNodeCountStartCycle()) {
			startNCProcedure();
		}
	}

	public void mergeOnOutdatedNeighbor() {
		for (Entry<Integer, Double> valE : ncVals.entrySet()) {
			int key = valE.getKey();
			double val = valE.getValue();
			double newVal = val / 2d;
			ncVals.put(key, newVal);
			if (NC_DEBUG) {
				NCDebugger.valueUpdate(this, key, val, newVal, null,
						"outdated", getEpoch());
			}
		}
	}

	public int getLastNC() {
		return lastNC;
	}

	public void createNCInstance() {
		// log.debug("Creating NC instance with id " + parent.getUID() +
		// " at node with NetID " + parent.getHost().getNetLayer().getNetID());
		ncVals.put(parent.getUID(), 1d);
		if (NC_DEBUG) {
			NCDebugger.valueUpdate(this, parent.getUID(), null, 1d, null,
					"init", getEpoch());
		}
	}

	private void startNCProcedure() {
		double prob2NCLeader = parent.getConf().getConcurrentNCLeaders()
				/ (double) (lastNC <= 0 ? parent.getConf()
						.getInitiallyAssumedNodeCount() : lastNC);
		// double prob2NCLeader =
		// parent.getConf().getConcurrentNCLeaders()/(double)INIT_NC;
		// log.debug("PROB:" + prob2NCLeader);
		if (NC_DEBUG) {
			NCDebugger.newEpoch(parent.getSync().getEpoch());
		}
		if (Simulator.getRandom().nextDouble() < prob2NCLeader) {
			createNCInstance();
		}
	}

	public int getNC() {
		// int result = getAverageNC();
		int result = getMedianNC();

		Monitoring.addNCInitiatorCount(ncVals.size());
		Monitoring.addNodeCount(result);
		return result;
	}

	private int getMedianNC() {
		int result = (int) Math.min(Math.round(1d / CollectionHelpers
				.getQuantile(ncVals.values(), 0.5d)), Integer.MAX_VALUE);
		if (NC_DEBUG) {
			List<Double> l = new ArrayList<Double>(ncVals.values());
			for (int i = 0; i < l.size(); i++) {
				l.set(i, Math.rint(1d / l.get(i)));
			}
			Collections.sort(l);
			log.debug("Returning median of list values " + l);
		}
		return result;
	}

	long getEpoch() {
		return parent.getSync().getEpoch();
	}

	Map<Integer, Double> getNCVals() {
		return ncVals;
	}
}
