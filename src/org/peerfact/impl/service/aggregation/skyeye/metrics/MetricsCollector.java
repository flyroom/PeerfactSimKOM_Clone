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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.peerfact.api.common.Message;
import org.peerfact.api.network.NetID;
import org.peerfact.api.network.NetMessage;
import org.peerfact.api.overlay.OverlayID;
import org.peerfact.api.overlay.OverlayNode;
import org.peerfact.api.overlay.kbr.KBRNode;
import org.peerfact.api.service.skyeye.ISkyNetMonitor;
import org.peerfact.api.service.skyeye.SkyNetConstants;
import org.peerfact.api.service.skyeye.SkyNetMessage;
import org.peerfact.api.service.skyeye.SkyNetNodeInterface;
import org.peerfact.api.service.skyeye.overlay2SkyNet.MetricsCollectorDelegator;
import org.peerfact.impl.service.aggregation.skyeye.SkyNetUtilities;
import org.peerfact.impl.service.aggregation.skyeye.analyzing.analyzers.KBROlAnalyzer;
import org.peerfact.impl.service.aggregation.skyeye.analyzing.analyzers.NetLayerAnalyzer;
import org.peerfact.impl.service.aggregation.skyeye.analyzing.analyzers.OPAnalyzer;
import org.peerfact.impl.service.aggregation.skyeye.analyzing.analyzers.OPAnalyzerEntry;
import org.peerfact.impl.service.aggregation.skyeye.analyzing.analyzers.KBROlAnalyzer.MessageHopTupel;
import org.peerfact.impl.service.aggregation.skyeye.analyzing.analyzers.KBROlAnalyzer.QueryStat;
import org.peerfact.impl.service.aggregation.skyeye.analyzing.writers.MetricsWriter;
import org.peerfact.impl.service.aggregation.skyeye.attributes.messages.AttributeUpdateMsg;
import org.peerfact.impl.service.aggregation.skyeye.components.SkyNetNode;
import org.peerfact.impl.service.aggregation.skyeye.metrics.messages.MetricUpdateACKMsg;
import org.peerfact.impl.service.aggregation.skyeye.metrics.messages.MetricUpdateMsg;
import org.peerfact.impl.service.aggregation.skyeye.overlay2skynet.util.AbstractMetricsCollectorDelegator;
import org.peerfact.impl.service.aggregation.skyeye.queries.messages.QueryForwardACKMsg;
import org.peerfact.impl.service.aggregation.skyeye.queries.messages.QueryForwardMsg;
import org.peerfact.impl.service.aggregation.skyeye.queries.operations.QueryTransmissionOperation;
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
 * This class is responsible for measuring the predefined metrics from all the
 * different layers of a host and for providing the measured metrics to the
 * SkyNet-node for further processing. To obtain the different metrics from the
 * host, <code>MetricsCollector</code> employs the analyzers, which are defined
 * in the <code>analyzers</code>-package and supply this class with the needed
 * information.
 * 
 * @author Dominik Stingl, Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 06.12.2008
 * 
 */
public class MetricsCollector extends AbstractMetricsCollectorDelegator {

	private static Logger log = SimLogger.getLogger(MetricsCollector.class);

	private final NetLayerAnalyzer netAnalyzer;

	private final OPAnalyzer opAnalyzer;

	private final KBROlAnalyzer kbrOlAnalyzer;

	private final MetricsCollectorDelegator metricsCollectorDelegator;

	public MetricsCollector(SkyNetNodeInterface skyNetNode,
			MetricsCollectorDelegator metricsCollectorDelegator) {
		this.skyNetNode = skyNetNode;
		this.metricsCollectorDelegator = metricsCollectorDelegator;
		this.metricsCollectorDelegator.setSkyNetNode(skyNetNode);
		ISkyNetMonitor monitor = (ISkyNetMonitor) Simulator.getMonitor();

		netAnalyzer = (NetLayerAnalyzer) monitor
				.getNetAnalyzer(NetLayerAnalyzer.class);
		opAnalyzer = (OPAnalyzer) monitor
				.getOperationAnalyzer(OPAnalyzer.class);
		kbrOlAnalyzer = (KBROlAnalyzer) monitor
				.getKBROverlayAnalyzer(KBROlAnalyzer.class);
	}

	@Override
	public LinkedHashMap<String, MetricsAggregate> getStatisticsOfOperations(
			Vector<OPAnalyzerEntry> opVector, double interval) {
		LinkedHashMap<String, MetricsAggregate> map = new LinkedHashMap<String, MetricsAggregate>();
		/*
		 * int completeOPCounter = 0; int succeededOPCounter = 0; int
		 * failedOPCounter = 0; double averageLookupTime = 0;
		 */

		int queryOpCounter = 0;
		double averageQueryTime = 0;

		if (opVector != null) {
			// completeOPCounter = opVector.size();
			// double lookupCounter = 0;
			OPAnalyzerEntry entry = null;
			for (int i = 0; i < opVector.size(); i++) {
				entry = opVector.get(i);
				if (entry.isSucccess()) {
					// succeededOPCounter = succeededOPCounter + 1;
					/*
					 * if (entry.getOp() instanceof GetPredecessorOperation ||
					 * entry.getOp() instanceof NodeLookupOperation ||
					 * entry.getOp() instanceof ResponsibleForKeyOperation ||
					 * entry.getOp() instanceof LookupOperation) {
					 * lookupCounter++; averageLookupTime = averageLookupTime +
					 * entry.getDuration(); } else
					 */if (entry.getOp() instanceof QueryTransmissionOperation) {
						queryOpCounter++;
						averageQueryTime = averageQueryTime
								+ entry.getDuration();
					}
				} /*
				 * else { failedOPCounter = failedOPCounter + 1; }
				 */
			}
			/*
			 * if (lookupCounter != 0) { averageLookupTime = averageLookupTime /
			 * (lookupCounter SkyNetConstants.DIVISOR_FOR_SECOND); }
			 */

			if (queryOpCounter != 0) {
				averageQueryTime = averageQueryTime
						/ (queryOpCounter * SkyNetConstants.DIVISOR_FOR_SECOND);
			}
		}

		// number of completed OPs
		/*
		 * MetricsAggregate ag = createAggregate("CompleteOPs",
		 * completeOPCounter, interval); map.put(ag.getAggregateName(), ag);
		 * 
		 * // number of succeeded OPs ag = createAggregate("SucceededOPs",
		 * succeededOPCounter, interval); map.put(ag.getAggregateName(), ag);
		 * 
		 * // number of failed OPs ag = createAggregate("FailedOPs",
		 * failedOPCounter, interval); map.put(ag.getAggregateName(), ag);
		 * 
		 * // average lookup-time of lookup-Operations in sec ag =
		 * createAggregate("AverageLookupTimeInSec", averageLookupTime, 1);
		 * map.put(ag.getAggregateName(), ag);
		 */

		// average time of a successful query in sec
		MetricsAggregate ag = createAggregate("AverageQueryTimeInSec",
				averageQueryTime, 1);
		map.put(ag.getAggregateName(), ag);

		return map;
	}

	@Override
	public LinkedHashMap<String, MetricsAggregate> getStatisticsOfMsgs(
			Vector<NetMessage> msgVector, double interval, boolean sent) {
		LinkedHashMap<String, MetricsAggregate> map = new LinkedHashMap<String, MetricsAggregate>();
		String prefix;
		if (sent) {
			prefix = "Sent";
		} else {
			prefix = "Rec";
		}

		// count the different messages of skynet
		int skyNetMsgCounter = 0;
		double skyNetTraffic = 0;
		int metricUpdateMsgCounter = 0;
		long metricUpdateTraffic = 0;
		int metricUpdateACKMsgCounter = 0;
		long metricUpdateACKTraffic = 0;
		int attributeUpdateMsgCounter = 0;
		long attributeUpdateTraffic = 0;
		int queryMsgCounter = 0;
		long queryTraffic = 0;

		if (msgVector != null) {
			Message msg = null;
			for (int i = 0; i < msgVector.size(); i++) {
				msg = msgVector.get(i).getPayload().getPayload();
				if (msg instanceof SkyNetMessage) {
					skyNetMsgCounter = skyNetMsgCounter + 1;
					skyNetTraffic = skyNetTraffic + msgVector.get(i).getSize();
					if (msg instanceof QueryForwardMsg
							|| msg instanceof QueryForwardACKMsg) {
						queryMsgCounter++;
						queryTraffic = queryTraffic
								+ msgVector.get(i).getSize();
					} else if (msg instanceof MetricUpdateMsg) {
						metricUpdateMsgCounter++;
						metricUpdateTraffic = metricUpdateTraffic
								+ msgVector.get(i).getSize();
					} else if (msg instanceof MetricUpdateACKMsg) {
						metricUpdateACKMsgCounter++;
						metricUpdateACKTraffic = metricUpdateACKTraffic
								+ msgVector.get(i).getSize();
					} else if (msg instanceof AttributeUpdateMsg) {
						attributeUpdateMsgCounter++;
						attributeUpdateTraffic = attributeUpdateTraffic
								+ msgVector.get(i).getSize();
						if (skyNetNode.getTreeHandler().isRoot() && !(sent)) {
							log
									.debug(SkyNetUtilities
											.getTimeAndNetID(skyNetNode)
											+ "received "
											+ ((AttributeUpdateMsg) msg)
													.getContent().size()
											+ " attributeEntries from "
											+ SkyNetUtilities
													.getNetID(((AttributeUpdateMsg) msg)
															.getSenderNodeInfo()));
						}
					}
				} else {
					log.debug(msg.toString() + "is not a SkyNet-Message");
				}
			}// for()

		}

		// amount of SkyNet-messages
		MetricsAggregate ag = createAggregate(prefix + "SkyNetMessages",
				skyNetMsgCounter, interval);
		map.put(ag.getAggregateName(), ag);

		// amount of metricUpdate-messages
		ag = createAggregate(prefix + "MetricUpdateMessages",
				metricUpdateMsgCounter, interval);
		map.put(ag.getAggregateName(), ag);

		// amount of metricUpdateACK-messages
		ag = createAggregate(prefix + "MetricUpdateACKMessages",
				metricUpdateACKMsgCounter, interval);
		map.put(ag.getAggregateName(), ag);

		// amount of attributeUpdate-messages
		ag = createAggregate(prefix + "AttributeUpdateMessages",
				attributeUpdateMsgCounter, interval);
		map.put(ag.getAggregateName(), ag);

		// amount of Query-messages
		ag = createAggregate(prefix + "QueryMessages", queryMsgCounter,
				interval);
		map.put(ag.getAggregateName(), ag);

		// size of SkyNet-messages
		ag = createAggregate(prefix + "SizeSkyNetMessages", skyNetTraffic,
				interval);
		map.put(ag.getAggregateName(), ag);

		// size of metricUpdate-messages
		ag = createAggregate(prefix + "SizeMetricUpdateMessages",
				metricUpdateTraffic, interval);
		map.put(ag.getAggregateName(), ag);

		// size of metricUpdateACK-messages
		ag = createAggregate(prefix + "SizeMetricUpdateACKMessages",
				metricUpdateACKTraffic, interval);
		map.put(ag.getAggregateName(), ag);

		// size of AttributeUpdate-messages
		ag = createAggregate(prefix + "SizeAttributeUpdateMessages",
				attributeUpdateTraffic, interval);
		map.put(ag.getAggregateName(), ag);

		// size of Query-messages
		ag = createAggregate(prefix + "QueryTraffic", queryTraffic, interval);
		map.put(ag.getAggregateName(), ag);

		return map;
	}

	private LinkedHashMap<String, MetricsAggregate> getStatisticsOfKBROverlay() {

		/**
		 * create Aggregates for KBROverlay statistics if there is a KBROverlay
		 */
		LinkedHashMap<String, MetricsAggregate> map = new LinkedHashMap<String, MetricsAggregate>();
		MetricsAggregate ag;

		OverlayNode<?, ?> kbrNode = skyNetNode.getHost().getOverlay(
				KBRNode.class);
		if (kbrNode != null && kbrOlAnalyzer != null) {
			OverlayID<?> id = kbrNode.getOverlayID();

			Vector<MessageHopTupel> deliveredVec = kbrOlAnalyzer
					.getDeliveredMessages(id);

			double averageHops;
			int sumOfHops;

			/**
			 * Generate aggregate for average number of hops at message delivery
			 */
			if (deliveredVec.size() > 0) {
				sumOfHops = 0;
				for (MessageHopTupel messageHopTupel : deliveredVec) {
					sumOfHops += messageHopTupel.getHops();
				}
				averageHops = (sumOfHops == 0) ? 0 : ((double) sumOfHops)
						/ deliveredVec.size();
				ag = createAggregate(
						"KBR_AverageNumberOfHopsAtMessageDelivery",
						averageHops, 1);
				map.put(ag.getAggregateName(), ag);
			}

			/**
			 * Generate aggregate for total number of delivered messages
			 */
			ag = createAggregate("KBR_NumberOfDeliveredMsgs", deliveredVec
					.size(), 1);
			map.put(ag.getAggregateName(), ag);

			Vector<MessageHopTupel> forwardedVec = kbrOlAnalyzer
					.getForwardedMessages(id);

			/**
			 * Generate aggregate for average number of hops at message forward
			 */
			if (forwardedVec.size() > 0) {
				sumOfHops = 0;
				for (MessageHopTupel messageHopTupel : forwardedVec) {
					sumOfHops += messageHopTupel.getHops();
				}
				averageHops = (sumOfHops == 0) ? 0 : ((double) sumOfHops)
						/ forwardedVec.size();
				ag = createAggregate("KBR_AverageNumberOfHopsAtMessageForward",
						averageHops, 1);
				map.put(ag.getAggregateName(), ag);
			} else {
				ag = createStubAggregate("KBR_AverageNumberOfHopsAtMessageForward");
				map.put(ag.getAggregateName(), ag);
			}

			/**
			 * Generate aggregate for total number of forwarded messages
			 */
			ag = createAggregate("KBR_NumberOfForwardedMsgs", forwardedVec
					.size(), 1);
			map.put(ag.getAggregateName(), ag);

			Vector<Message> startedQueriesVec = kbrOlAnalyzer
					.getQueriesStarted(id);

			/**
			 * Generate aggregate for total number of started queries
			 */
			ag = createAggregate("KBR_NumberOfStartedQueries",
					startedQueriesVec.size(), 1);
			map.put(ag.getAggregateName(), ag);

			Vector<QueryStat> deliveredQueriesVec = kbrOlAnalyzer
					.getQueriesDelivered(id);

			/**
			 * Generate aggregate for average number of hops and duration at
			 * query delivery
			 */
			if (deliveredQueriesVec.size() > 0) {
				sumOfHops = 0;
				long sumOfDurations = 0;
				for (QueryStat stat : deliveredQueriesVec) {
					sumOfHops += stat.getHops();
					sumOfDurations += stat.getDuration();
				}

				averageHops = (sumOfHops == 0) ? 0 : ((double) sumOfHops)
						/ deliveredQueriesVec.size();
				ag = createAggregate("KBR_AverageNumberOfHopsAtQueryDelivery",
						averageHops, 1);
				map.put(ag.getAggregateName(), ag);

				double averageDuration = (sumOfDurations == 0) ? 0
						: ((double) sumOfDurations)
								/ deliveredQueriesVec.size();
				ag = createAggregate("KBR_AverageDurationAtQueryDelivery",
						averageDuration, 1);
				map.put(ag.getAggregateName(), ag);
			} else {
				ag = createStubAggregate("KBR_AverageNumberOfHopsAtQueryDelivery");
				map.put(ag.getAggregateName(), ag);

				ag = createStubAggregate("KBR_AverageDurationAtQueryDelivery");
				map.put(ag.getAggregateName(), ag);
			}

			/**
			 * Generate aggregate for total number of delivered queries
			 */
			ag = createAggregate("KBR_NumberOfDeliveredQueries",
					deliveredQueriesVec.size(), 1);
			map.put(ag.getAggregateName(), ag);
		} else {
			/*
			 * Generate Aggregate stubs
			 */
			ag = createStubAggregate("KBR_AverageNumberOfHopsAtMessageDelivery");
			map.put(ag.getAggregateName(), ag);

			ag = createStubAggregate("KBR_NumberOfDeliveredMsgs");
			map.put(ag.getAggregateName(), ag);

			ag = createStubAggregate("KBR_AverageNumberOfHopsAtMessageForward");
			map.put(ag.getAggregateName(), ag);

			ag = createStubAggregate("KBR_NumberOfForwardedMsgs");
			map.put(ag.getAggregateName(), ag);

			ag = createStubAggregate("KBR_NumberOfStartedQueries");
			map.put(ag.getAggregateName(), ag);

			ag = createStubAggregate("KBR_AverageNumberOfHopsAtQueryDelivery");
			map.put(ag.getAggregateName(), ag);

			ag = createStubAggregate("KBR_AverageDurationAtQueryDelivery");
			map.put(ag.getAggregateName(), ag);

		}
		return map;
	}

	private static Map<String, MetricsAggregate> getReferenceMetrics() {

		LinkedHashMap<String, MetricsAggregate> map = new LinkedHashMap<String, MetricsAggregate>();

		/*
		 * Simple ZigZag
		 * 
		 * Swapping from 0 to 1 and back with a period of periodT (in simulation
		 * time)
		 */
		long periodT = 1 * Simulator.MINUTE_UNIT;
		double value = ReferenceMetrics.simpleZigZag(periodT);
		MetricsAggregate ag = createAggregate("SimpleZigZagValue_1m", value, 1);
		map.put(ag.getAggregateName(), ag);

		periodT = 3 * Simulator.MINUTE_UNIT;
		value = ReferenceMetrics.simpleZigZag(periodT);
		ag = createAggregate("SimpleZigZagValue_3m", value, 1);
		map.put(ag.getAggregateName(), ag);

		periodT = 10 * Simulator.MINUTE_UNIT;
		value = ReferenceMetrics.simpleZigZag(periodT);
		ag = createAggregate("SimpleZigZagValue_10m", value, 1);
		map.put(ag.getAggregateName(), ag);

		periodT = 30 * Simulator.MINUTE_UNIT;
		value = ReferenceMetrics.simpleZigZag(periodT);
		ag = createAggregate("SimpleZigZagValue_30m", value, 1);
		map.put(ag.getAggregateName(), ag);

		/*
		 * Sin-function
		 * 
		 * Sine function with a period of periodT (in simulation time)
		 */
		periodT = 1 * Simulator.MINUTE_UNIT;
		value = ReferenceMetrics.sin(periodT);
		ag = createAggregate("Sine_1m", value, 1);
		map.put(ag.getAggregateName(), ag);

		periodT = 3 * Simulator.MINUTE_UNIT;
		value = ReferenceMetrics.sin(periodT);
		ag = createAggregate("Sine_3m", value, 1);
		map.put(ag.getAggregateName(), ag);

		periodT = 10 * Simulator.MINUTE_UNIT;
		value = ReferenceMetrics.sin(periodT);
		ag = createAggregate("Sine_10m", value, 1);
		map.put(ag.getAggregateName(), ag);

		periodT = 30 * Simulator.MINUTE_UNIT;
		value = ReferenceMetrics.sin(periodT);
		ag = createAggregate("Sine_30m", value, 1);
		map.put(ag.getAggregateName(), ag);

		return map;
	}

	/*
	 * private MetricsAggregate createAggregate(String name, double value,
	 * double intervalLength) { double mean = value / intervalLength; return new
	 * MetricsAggregate(name, mean, mean, mean, mean mean, 1); }
	 */

	/**
	 * This method is responsible for initializing a {@link MetricsEntry}
	 * -object, which contains the required metrics of a SkyNet-node. The
	 * metrics are delivered by different analyzers and stored within the
	 * <code>MetricsEntry</code>-object, which is afterwards returned by this
	 * method.
	 * 
	 * @return the <code>MetricsEntry</code>-object with the measured metrics of
	 *         a SkyNet-node
	 */
	public MetricsEntry collectOwnData() {
		NetID netID = skyNetNode.getHost().getNetLayer().getNetID();
		LinkedHashMap<String, MetricsAggregate> map;
		Vector<NetMessage> v1;
		Vector<OPAnalyzerEntry> v2;
		double currentTime = Simulator.getCurrentTime();
		double interval = (currentTime - skyNetNode.getMetricUpdateStrategy()
				.getSendingTime())
				/ SkyNetConstants.DIVISOR_FOR_SECOND;
		if (interval == 0) {
			log.fatal(SkyNetUtilities.getTimeAndNetID(skyNetNode)
					+ "INTERVAL == 0, THIS CANNOT BE POSSIBLE");
		}

		// /////////////////////////////////////////////
		// create Aggregates of amount of sent NetMsgs
		// /////////////////////////////////////////////

		// for SkyNet
		v1 = netAnalyzer.getSentMsgs(netID);
		map = getStatisticsOfMsgs(v1, interval, true);

		// for the underlying overlay
		map.putAll(metricsCollectorDelegator.getStatisticsOfMsgs(v1, interval,
				true));

		// /////////////////////////////////////////////
		// create Aggregates of amount of received NetMsgs
		// /////////////////////////////////////////////

		// for SkyNet
		v1 = netAnalyzer.getReceivedMsgs(netID);
		map.putAll(getStatisticsOfMsgs(v1, interval, false));

		// for the underlying overlay
		map.putAll(metricsCollectorDelegator.getStatisticsOfMsgs(v1, interval,
				false));

		// /////////////////////////////////////////////
		// create Aggregate of Operations
		// /////////////////////////////////////////////

		// for SkyNet
		v2 = opAnalyzer.getCompletedOperations(netID);
		map.putAll(getStatisticsOfOperations(v2, interval));

		// for the underlying overlay
		map.putAll(metricsCollectorDelegator.getStatisticsOfOperations(v2,
				interval));

		// create actual online time
		double onlineTime = (currentTime - ((SkyNetNode) skyNetNode)
				.getPresentTime())
				/ SkyNetConstants.DIVISOR_FOR_SECOND;
		MetricsAggregate aggregate = createAggregate("OnlineTime", onlineTime,
				1);
		map.put(aggregate.getAggregateName(), aggregate);

		aggregate = createAggregate("aggregateFreshness", Simulator
				.getCurrentTime(), 1);
		map.put(aggregate.getAggregateName(), aggregate);

		// /////////////////////////////////////////////
		// create Aggregate of KBROverlay statistics
		// /////////////////////////////////////////////
		map.putAll(getStatisticsOfKBROverlay());

		// /////////////////////////////////////////////
		// create Aggregate of Reference ZigZag metric
		// /////////////////////////////////////////////
		map.putAll(getReferenceMetrics());

		MetricsWriter.getInstance().addAggregatedMap(
				skyNetNode.getSkyNetNodeInfo().getTransInfo().getNetId(), map,
				false);

		return new MetricsEntry(skyNetNode.getSkyNetNodeInfo(), map);
	}

}
