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

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.apache.log4j.Logger;
import org.peerfact.api.common.Message;
import org.peerfact.api.service.skyeye.InputStrategy;
import org.peerfact.api.service.skyeye.SkyNetNodeInfo;
import org.peerfact.api.service.skyeye.SkyNetNodeInterface;
import org.peerfact.api.service.skyeye.SubCoordinatorInfo;
import org.peerfact.api.service.skyeye.overlay2SkyNet.MetricsCollectorDelegator;
import org.peerfact.impl.service.aggregation.skyeye.metrics.messages.MetricUpdateMsg;
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
 * This class handles the incoming <i>Metric-Updates</i> from all
 * Sub-Coordinators. The received <code>MetricEntry</code>s are delivered to the
 * <code>MetricStorage</code>, where they can be accessed by other classes.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 15.11.2008
 * 
 */
public class MetricInputStrategy implements InputStrategy {

	private static Logger log = SimLogger.getLogger(MetricInputStrategy.class);

	private MetricStorage metricStorage;

	private SkyNetNodeInterface skyNetNode;

	private MetricsCollector collector;

	public MetricInputStrategy(SkyNetNodeInterface skyNetNode,
			MetricsCollectorDelegator metricsCollectorDelegator,
			MetricStorage metricStorage) {
		collector = new MetricsCollector(skyNetNode, metricsCollectorDelegator);
		this.metricStorage = metricStorage;
		this.skyNetNode = skyNetNode;
	}

	@Override
	public void processUpdateMessage(Message msg, long timestamp) {
		MetricUpdateMsg message = (MetricUpdateMsg) msg;
		SkyNetNodeInfo skyNetNodeInfo = message.getSenderNodeInfo();
		MetricsSubCoordinatorInfo subCoInfo = new MetricsSubCoordinatorInfo(
				skyNetNodeInfo, timestamp, skyNetNode.getMetricUpdateStrategy()
						.getUpdateInterval(), message.getContent());
		addSubCoordinator(subCoInfo);
	}

	@Override
	public void addSubCoordinator(SubCoordinatorInfo subCo) {
		MetricsSubCoordinatorInfo subCoordinator = (MetricsSubCoordinatorInfo) subCo;
		LinkedHashMap<BigDecimal, MetricsSubCoordinatorInfo> list = getMetricStorage()
				.getListOfSubCoordinators();
		BigDecimal id = subCoordinator.getNodeInfo().getSkyNetID().getID();

		if (list.containsKey(id)) {
			refreshContentOfSubCoordinator(id, subCoordinator.getData());
			refreshTimestampOfSubCoordinator(id, subCoordinator
					.getTimestampOfUpdate());
			refreshTresholdOfSubCoordinator(id, subCoordinator
					.getUpdateThreshold());
		} else {
			list.put(id, subCoordinator);
			log.info(skyNetNode.getSkyNetNodeInfo().toString()
					+ " Size of list after adding " + id + " = " + list.size());
		}

	}

	private void refreshTimestampOfSubCoordinator(BigDecimal id, long timestamp) {
		LinkedHashMap<BigDecimal, MetricsSubCoordinatorInfo> list = getMetricStorage()
				.getListOfSubCoordinators();
		list.get(id).setTimestampOfUpdate(timestamp);
	}

	private void refreshContentOfSubCoordinator(BigDecimal id,
			MetricsEntry content) {
		LinkedHashMap<BigDecimal, MetricsSubCoordinatorInfo> list = getMetricStorage()
				.getListOfSubCoordinators();
		list.get(id).setData(content);
	}

	private void refreshTresholdOfSubCoordinator(BigDecimal id, long treshold) {
		LinkedHashMap<BigDecimal, MetricsSubCoordinatorInfo> list = getMetricStorage()
				.getListOfSubCoordinators();
		list.get(id).setUpdateThreshold(treshold);
	}

	public MetricStorage getMetricStorage() {
		return metricStorage;
	}

	@Override
	public void writeOwnDataInStorage() {
		metricStorage.setOwnMetrics(collector.collectOwnData());
	}

	public void refreshNeedsUpdateOfAllSubCos() {
		LinkedHashMap<BigDecimal, MetricsSubCoordinatorInfo> list = getMetricStorage()
				.getListOfSubCoordinators();
		Iterator<BigDecimal> idIter = list.keySet().iterator();
		while (idIter.hasNext()) {
			list.get(idIter.next()).setNeedsUpdate(true);
		}
		// getMetricStorage().setListOfSubCoordinators(list);
	}

}
