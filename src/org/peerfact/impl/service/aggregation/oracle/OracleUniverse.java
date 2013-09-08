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

package org.peerfact.impl.service.aggregation.oracle;

import java.util.LinkedHashSet;
import java.util.Set;

import org.peerfact.api.service.aggr.AggregationResult;
import org.peerfact.api.service.aggr.AggregationService;
import org.peerfact.api.service.aggr.NoSuchValueException;
import org.peerfact.impl.service.aggregation.DefaultAggregationResult;
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
 * 
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public class OracleUniverse {

	Set<AggregationService<Object>> oracleNodes = new LinkedHashSet<AggregationService<Object>>();

	public void add(AggregationService<Object> aggregationServiceOracle) {
		oracleNodes.add(aggregationServiceOracle);
	}

	public AggregationResult getAggregationResult(Object identifier) {

		double minimum = Double.MAX_VALUE;
		double maximum = Double.MIN_VALUE;
		double sum = 0;
		int nodeCount = 0;
		int nodeCountVal = 0;
		long minTime = Simulator.getCurrentTime();
		long maxTime = Simulator.getCurrentTime();
		long avgTime = Simulator.getCurrentTime();

		for (AggregationService<Object> nd : oracleNodes) {
			try {
				double val = nd.getLocalValue(identifier);
				if (val != Double.MAX_VALUE) {
					if (val < minimum) {
						minimum = val;
					}
					if (val > maximum) {
						maximum = val;
					}
					sum += val;
					nodeCountVal++;
				}
			} catch (NoSuchValueException e) {
				// Value not given, ignoring.
			}
			nodeCount++;
		}

		double avg = sum / nodeCountVal;

		double varAcc = 0d;

		for (AggregationService<Object> nd : oracleNodes) {
			try {
				double val = nd.getLocalValue(identifier);
				if (val != Double.MAX_VALUE) {
					varAcc += (avg - val) * (avg - val);
				}
			} catch (NoSuchValueException e) {
				// Value not given, ignoring.
			}

		}

		double var = nodeCountVal <= 1 ? 0 : varAcc
				/ (nodeCountVal - 1);

		return new DefaultAggregationResult(minimum, maximum, avg, var,
				nodeCount, minTime, maxTime, avgTime);
	}

}
