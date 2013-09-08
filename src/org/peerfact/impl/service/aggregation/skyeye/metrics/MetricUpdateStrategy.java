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
import org.peerfact.api.common.Operation;
import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.service.skyeye.SkyNetEventType;
import org.peerfact.api.service.skyeye.SkyNetNodeInfo;
import org.peerfact.api.service.skyeye.SkyNetNodeInterface;
import org.peerfact.api.transport.TransProtocol;
import org.peerfact.impl.overlay.AbstractOverlayNode;
import org.peerfact.impl.overlay.AbstractOverlayNode.PeerStatus;
import org.peerfact.impl.service.aggregation.skyeye.AbstractUpdateStrategy;
import org.peerfact.impl.service.aggregation.skyeye.SkyNetEventObject;
import org.peerfact.impl.service.aggregation.skyeye.SkyNetPropertiesReader;
import org.peerfact.impl.service.aggregation.skyeye.SkyNetUtilities;
import org.peerfact.impl.service.aggregation.skyeye.analyzing.writers.MetricsWriter;
import org.peerfact.impl.service.aggregation.skyeye.analyzing.writers.StatisticWriter;
import org.peerfact.impl.service.aggregation.skyeye.components.SkyNetNode;
import org.peerfact.impl.service.aggregation.skyeye.metrics.messages.MetricUpdateACKMsg;
import org.peerfact.impl.service.aggregation.skyeye.metrics.messages.MetricUpdateSyncMsg;
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
 * This class handles the outgoing <i>Metric-Updates</i> from a Coordinator to
 * its Sub-Coordinator. The sent <code>MetricEntry</code> is aggregated from the
 * stored <code>MetricEntry</code>s in <code>MetricStorage</code>.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 15.11.2008
 * 
 */
public class MetricUpdateStrategy extends AbstractUpdateStrategy<MetricStorage> {

	private static Logger log = SimLogger.getLogger(MetricUpdateStrategy.class);

	private final SkyNetNodeInterface skyNetNode;

	private final MetricStorage metricStorage;

	private final MetricsSentinel sentinel;

	private long sendMsgID;

	private MetricsEntry metrics;

	private int systemStatisticsCounter;

	private final int systemStatisticsTreshold;

	private final float removalPeriode;

	private long updatePeriod;

	// Variables for configuring the root-based triggering of metrics-updates

	private long lastMetricSync;

	/**
	 * Determines the time for the next synchronization of the metric-update,
	 * triggered by the root
	 */
	private long metricSyncInterval;

	/**
	 * Determines the maximum time which must be append to the current
	 * update-period in order to synchronize the metric updates
	 */
	private long maxUpdateIntervalOffset;

	private long metricIntervalDecrease;

	private int metricsUpdateCounter;

	public MetricUpdateStrategy(SkyNetNodeInterface skyNetNode) {
		super();

		// setting the needed values from the properties file
		SkyNetPropertiesReader propReader = SkyNetPropertiesReader
				.getInstance();
		this.updatePeriod = propReader.getTimeProperty("MetricUpdateTime");
		this.numberOfRetransmissions = propReader
				.getIntProperty("MetricNumberOfRetransmissions");
		this.timeForAck = propReader.getTimeProperty("MetricTimeForAck");
		this.systemStatisticsTreshold = propReader
				.getIntProperty("TimeForGeneratingSystemStatistics");
		this.removalPeriode = propReader
				.getFloatProperty("MetricRemoveStaleSubCo");

		this.skyNetNode = skyNetNode;
		this.metricStorage = new MetricStorage(skyNetNode);
		this.sentinel = new MetricsSentinel(skyNetNode);
		sendMsgID = 0;
		systemStatisticsCounter = 0;

		metricSyncInterval = 0;
		maxUpdateIntervalOffset = 0;
		metricIntervalDecrease = 0;
		if (!"Normal".equals(propReader
				.getStringProperty("MetricUpdateStrategy"))) {
			metricSyncInterval = propReader
					.getTimeProperty("MetricSynchronizationInterval");
			maxUpdateIntervalOffset = propReader
					.getTimeProperty("MaximumUpdateIntervalOffset");
			metricIntervalDecrease = propReader
					.getTimeProperty("MetricIntervalDecrease");
		}

		lastMetricSync = 0;
		// metricsSyncCounter = 0;
		metricsUpdateCounter = 0;
	}

	/**
	 * This method puts <code>MetricUpdateStrategy</code> back in its initial
	 * state, if the host got off-line or if the peer left the underlying
	 * overlay.
	 */
	public void reset() {
		lastMetricSync = 0;
		metricStorage.reset();
		systemStatisticsCounter = 0;
	}

	// ----------------------------------------------------------------------
	// Getter- and Setter(Calculate)-Methods for the five variables
	// metricStorage, nextUpdateTime, numberOfRetransmissions, timeForAck and
	// receiverOfNextUpdate
	// ----------------------------------------------------------------------

	@Override
	public MetricStorage getStorage() {
		return metricStorage;
	}

	@Override
	public void calculateUpdateInterval() {
		updateInterval = updatePeriod;
	}

	public void scheduleNextUpdateEventAt(long offset) {
		metricsUpdateCounter++;
		Simulator.scheduleEvent(new SkyNetEventObject(
				SkyNetEventType.METRICS_UPDATE, Simulator.getCurrentTime(),
				metricsUpdateCounter), Simulator.getCurrentTime() + offset,
				skyNetNode, null);
	}

	@Override
	public void scheduleNextUpdateEvent() {
		long updateTime = 0;
		long actualTime = Simulator.getCurrentTime();
		long diff = actualTime - sendingTime;

		if (diff >= updateInterval) {
			if (skyNetNode.getTreeHandler().isRoot()) {
				log.warn(Simulator.getFormattedTime(actualTime)
						+ " MetricUpdate longer than one interval");
			}
			diff = diff % updateInterval;
		}
		long nextUpdateTime = updateInterval - diff;
		updateTime = actualTime + nextUpdateTime;

		// if (diff < getUpdateInterval()) {
		// long nextUpdateTime = getUpdateInterval() - diff;
		// // long time = Simulator.getCurrentTime();
		// updateTime = actualTime + nextUpdateTime;
		// } else {
		// if (skyNetNode.getTreeHandler().isRoot()) {
		// log.warn(Simulator.getFormattedTime(actualTime)
		// + " MetricUpdate longer than one interval");
		// }
		// long rest = diff % getUpdateInterval();
		// long nextUpdateTime = getUpdateInterval() - rest;
		// long time = Simulator.getCurrentTime();
		// updateTime = time + nextUpdateTime;
		//
		// }
		metricsUpdateCounter++;
		Simulator.scheduleEvent(new SkyNetEventObject(
				SkyNetEventType.METRICS_UPDATE, actualTime,
				metricsUpdateCounter), updateTime, skyNetNode, null);
	}

	@Override
	public void calculateNumberOfRetransmissions() {
		// TODO other implementation possible
	}

	@Override
	public void calculateReceiverForNextUpdate() {
		if (skyNetNode.getTreeHandler().isRoot()) {
			log.info("This the root of the SkyNet-Tree."
					+ " No need to send metrics");
		} else {
			this.receiverOfNextUpdate = skyNetNode.getTreeHandler()
					.getParentCoordinator();
		}
	}

	@Override
	public void calculateTimeForACK(long time) {
		// TODO other implementation possible
	}

	// ----------------------------------------------------------------------
	//
	// ----------------------------------------------------------------------

	@Override
	public void sendNextDataUpdate() {
		if (skyNetNode.getTreeHandler().isRoot()) {
			interpretUpdate(metrics, true);
			if (metricSyncInterval != 0
					&& (Simulator.getCurrentTime() - lastMetricSync >= metricSyncInterval)) {
				lastMetricSync = Simulator.getCurrentTime();
				log.warn(SkyNetUtilities.getTimeAndNetID(skyNetNode)
						+ "syncs MetricUpdate");
				LinkedHashMap<BigDecimal, MetricsSubCoordinatorInfo> subCoMap = getStorage()
						.getListOfSubCoordinators();
				Iterator<BigDecimal> iter = subCoMap.keySet().iterator();
				MetricsSubCoordinatorInfo subCoInfo = null;
				while (iter.hasNext()) {
					sendMsgID = skyNetNode.getMessageCounter()
							.assignmentOfMessageNumber();
					subCoInfo = subCoMap.get(iter.next());
					Message msg = new MetricUpdateSyncMsg(
							skyNetNode.getSkyNetNodeInfo(),
							subCoInfo.getNodeInfo(), maxUpdateIntervalOffset,
							Simulator.getCurrentTime(), sendMsgID);
					skyNetNode
							.getHost()
							.getTransLayer()
							.send(msg, subCoInfo.getNodeInfo().getTransInfo(),
									skyNetNode.getPort(), TransProtocol.UDP);
				}
			}
		} else {
			calculateReceiverForNextUpdate();
			sendMsgID = skyNetNode.getMessageCounter()
					.assignmentOfMessageNumber();
			if (isTreeSentinel()) {
				interpretUpdate(metrics, false);
			}
			sendUpdate();
		}
	}

	private boolean isTreeSentinel() {
		if (skyNetNode.getSkyNetNodeInfo().getLevel() > -1) {
			return skyNetNode.getSkyNetNodeInfo().getLevel() % 1 == 0;
		} else {
			return false;
		}
	}

	@Override
	public void setDataToSend() {
		// TEST the argument of the method aggregateMetrics is only for
		// debugging
		this.metrics = metricStorage.aggregateMetrics(false);
	}

	private void sendUpdate() {
		if (((AbstractOverlayNode<?, ?>) skyNetNode.getOverlayNode())
				.getPeerStatus()
				.equals(PeerStatus.PRESENT)) {
			if (receiverOfNextUpdate != null
					&& !receiverOfNextUpdate.getSkyNetID().equals(
							skyNetNode.getSkyNetNodeInfo().getSkyNetID())) {
				log.debug(SkyNetUtilities.getTimeAndNetID(skyNetNode)
						+ "sends next update to "
						+ SkyNetUtilities.getNetID(getReceiverOfNextUpdate()));
				SkyNetNodeInfo receiverInfo = receiverOfNextUpdate;
				SkyNetNodeInfo senderInfo = skyNetNode.getSkyNetNodeInfo();
				((SkyNetNode) skyNetNode).metricUpdate(senderInfo,
						receiverInfo, metrics, sendMsgID,
						new OperationCallback<MetricUpdateACKMsg>() {

							@Override
							public void calledOperationFailed(
									Operation<MetricUpdateACKMsg> op) {
								metricUpdateFailed(op);
							}

							@Override
							public void calledOperationSucceeded(
									Operation<MetricUpdateACKMsg> op) {
								metricUpdateSucceeded(op);
							}
						});
			} else {
				log.warn(SkyNetUtilities.getTimeAndNetID(skyNetNode)
						+ ((receiverOfNextUpdate != null) ? "Sender and receiver of metricUpdates are the same!"
								: "No receiver for metricUpdates!"));
				scheduleNextUpdateEvent();
			}
		} else {
			log.warn("SkyNetNode cannot send MetricUpdates"
					+ ", because he is not PRESENT");
		}
	}

	/**
	 * This method is only uses by the root of the tree, to write the actual
	 * data in files for later analysis. Additionally, the root uses the metrics
	 * to generate the <i>system-statistics</i> of the whole P2P-system and to
	 * specify the behavioral rules for the SkyNet-nodes to adapt themselves to
	 * the current state of the P2P-system.
	 * 
	 * @param m
	 *            contains the actual metrics of the whole SkyNet-tree
	 */
	private void interpretUpdate(MetricsEntry m, boolean isRoot) {
		LinkedHashMap<String, MetricsAggregate> vec = m.getMetrics();
		long time = Simulator.getCurrentTime();
		if (((AbstractOverlayNode<?, ?>) skyNetNode.getOverlayNode())
				.getPeerStatus()
				.equals(PeerStatus.PRESENT)) {
			if (isRoot) {
				// print the collected data of the tree in some files for later
				// visualization and data-processing
				StatisticWriter sw = StatisticWriter.getInstance();
				MetricsWriter.getInstance().addAggregatedMap(
						skyNetNode.getSkyNetNodeInfo().getTransInfo()
								.getNetId(), vec, true);
				sw.writeArffLine(vec, time);
				sw.writeAggregateEntry(vec, time);

				// push the new information of the tree down the tree
				systemStatisticsCounter++;
				if (systemStatisticsCounter == systemStatisticsTreshold) {
					systemStatisticsCounter = 0;
					skyNetNode.getMetricsInterpretation()
							.setActualSystemStatistics(m, time);
				}

				// End of an iteration of the MetricsEvent. Calculating now when
				// to start it again
				scheduleNextUpdateEvent();
			} else {
				sentinel.interpolateKnowledge(m);
			}
		} else {
			log.warn("SkyNetNode cannot interprete MetricUpdates"
					+ ", because he is not PRESENT");
		}
	}

	@Override
	public void removeStaleSubCoordinators() {
		long actualTime = Simulator.getCurrentTime();
		LinkedHashMap<BigDecimal, MetricsSubCoordinatorInfo> list = metricStorage
				.getListOfSubCoordinators();
		log.debug("Size of metricSubCo-list before = " + list.size());

		Iterator<BigDecimal> iter = list.keySet().iterator();
		BigDecimal key = null;
		MetricsSubCoordinatorInfo subCoInfo = null;
		long periode = -1;
		long sub = -1;
		long treshold = -1;

		while (iter.hasNext()) {
			key = iter.next();
			subCoInfo = list.get(key);
			periode = subCoInfo.getUpdatePeriode();
			sub = actualTime - subCoInfo.getTimestampOfUpdate();
			treshold = (long) (removalPeriode * periode);
			if (treshold < sub) {
				log.debug(Simulator.getFormattedTime(treshold) + "--"
						+ Simulator.getFormattedTime(sub));
				iter.remove();
				log.info(skyNetNode.getSkyNetNodeInfo().getSkyNetID()
						.getPlainSkyNetID()
						+ " Size of metricSubCo-list after deleting "
						+ key
						+ " = " + list.size());
			}
		}
	}

	void metricUpdateFailed(Operation<MetricUpdateACKMsg> op) {
		log.error(Simulator.getFormattedTime(Simulator.getCurrentTime())
				+ " "
				+ skyNetNode.getSkyNetNodeInfo().getTransInfo().getNetId()
						.toString() + " ----NO CHANCE TO UPDATE METRICS TO "
				+ receiverOfNextUpdate.getTransInfo().getNetId().toString()
				+ "----");
		// End of an iteration of the MetricsEvent. Calculating now when to
		// start it again
		scheduleNextUpdateEvent();

	}

	void metricUpdateSucceeded(Operation<MetricUpdateACKMsg> op) {
		log.debug("MetricUpdateOperation with id " + op.getOperationID()
				+ " succeeded");
		// End of an iteration of the MetricsEvent. Calculating now when to
		// start it again
		MetricUpdateACKMsg msg = op.getResult();
		if (msg.getActualSystemStatistics() != null) {
			skyNetNode.getMetricsInterpretation().setActualSystemStatistics(
					msg.getActualSystemStatistics(),
					msg.getStatisticsTimestamp());
		}
		if (msg.getParaManipulator() != null) {
			skyNetNode.getMetricsInterpretation().setParaManipulator(
					msg.getParaManipulator(), msg.getManipulatorTimestamp());
		}
		scheduleNextUpdateEvent();
	}

	public MetricsEntry getOwnMetrics() {
		return metricStorage.getOwnMetrics();
	}

	public int getMetricsUpdateCounter() {
		return metricsUpdateCounter;
	}

	public long getMaxUpdateIntervalOffset() {
		return maxUpdateIntervalOffset;
	}

	public long getLastMetricSync() {
		return lastMetricSync;
	}

	public void setLastMetricSync(long lastMetricSync) {
		this.lastMetricSync = lastMetricSync;
	}

	public long getMetricSyncInterval() {
		return metricSyncInterval;
	}

	public long getMetricIntervalDecrease() {
		return metricIntervalDecrease;
	}

}
