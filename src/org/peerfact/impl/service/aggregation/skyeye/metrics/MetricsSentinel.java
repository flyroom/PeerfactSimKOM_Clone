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

import java.util.Iterator;
import java.util.LinkedHashMap;

import org.apache.log4j.Logger;
import org.peerfact.api.service.skyeye.SkyNetNodeInterface;
import org.peerfact.impl.service.aggregation.skyeye.SkyNetUtilities;
import org.peerfact.impl.service.aggregation.skyeye.analyzing.writers.Coor2RootEntry;
import org.peerfact.impl.service.aggregation.skyeye.analyzing.writers.Coor2RootWriter;
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
 * 
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public class MetricsSentinel {

	private static Logger log = SimLogger.getLogger(MetricsSentinel.class);

	private SkyNetNodeInterface skyNetNode;

	public MetricsSentinel(SkyNetNodeInterface skyNetNode) {
		this.skyNetNode = skyNetNode;
	}

	public void interpolateKnowledge(MetricsEntry entryToSend) {
		Coor2RootWriter ctrWriter = Coor2RootWriter.getInstance();
		if (skyNetNode.getMetricsInterpretation().getActualSystemStatistics() != null) {
			long timestamp = skyNetNode.getMetricsInterpretation()
					.getStatisticsTimestamp();
			if (ctrWriter.checkForNewData(skyNetNode.getSkyNetNodeInfo(),
					timestamp)) {
				log.info(SkyNetUtilities.getTimeAndNetID(skyNetNode)
						+ " which is responsible for the Coordinator-Key "
						+ skyNetNode.getSkyNetNodeInfo().getCoordinatorKey()
								.getPlainSkyNetID() + " at level "
						+ skyNetNode.getSkyNetNodeInfo().getLevel()
						+ ", is going to interpolate its"
						+ " metrics with the ones of the root,"
						+ " which were created at "
						+ Simulator.getFormattedTime(timestamp));
				LinkedHashMap<String, MetricsAggregate> rootMetrics = skyNetNode
						.getMetricsInterpretation().getActualSystemStatistics()
						.getMetrics();
				LinkedHashMap<String, MetricsAggregate> coordinatorMetrics = entryToSend
						.getMetrics();
				LinkedHashMap<String, Coor2RootEntry> dataMap = new LinkedHashMap<String, Coor2RootEntry>();
				boolean first = true;
				Iterator<String> nameIter = rootMetrics.keySet().iterator();
				String name = null;
				MetricsAggregate rootAg = null;
				MetricsAggregate coordinatorAg = null;
				double factor = -1;
				while (nameIter.hasNext()) {
					name = nameIter.next();
					rootAg = rootMetrics.get(name);
					coordinatorAg = coordinatorMetrics.get(name);
					if (first) {
						first = false;
						factor = (coordinatorAg.getNodeCount() / (double) rootAg
								.getNodeCount());
						dataMap.put("NodeCount", new Coor2RootEntry(
								"NodeCount", coordinatorAg
										.getNodeCount(), coordinatorAg
										.getNodeCount()
										/ factor, rootAg.getNodeCount(),
								factor));
					}
					double interpolatedSum = coordinatorAg.getSumOfAggregates()
							/ factor;
					double interpolatedCount = coordinatorAg
							.getNodeCount()
							/ factor;

					dataMap.put(name, new Coor2RootEntry(name, coordinatorAg
							.getAverage(), interpolatedSum / interpolatedCount,
							rootAg.getAverage(), -1));
				}
				ctrWriter.writeData(skyNetNode.getSkyNetNodeInfo(), dataMap,
						timestamp);
			}
		}
	}
}
