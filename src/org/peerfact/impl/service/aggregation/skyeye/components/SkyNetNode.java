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

package org.peerfact.impl.service.aggregation.skyeye.components;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.peerfact.api.common.ConnectivityEvent;
import org.peerfact.api.common.Operation;
import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.overlay.NeighborDeterminator;
import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.api.overlay.OverlayID;
import org.peerfact.api.overlay.OverlayNode;
import org.peerfact.api.service.aggr.AggregationMap;
import org.peerfact.api.service.aggr.AggregationResult;
import org.peerfact.api.service.aggr.AggregationService;
import org.peerfact.api.service.aggr.NoSuchValueException;
import org.peerfact.api.service.skyeye.SkyNetConstants;
import org.peerfact.api.service.skyeye.SkyNetEventType;
import org.peerfact.api.service.skyeye.SkyNetNodeInfo;
import org.peerfact.api.service.skyeye.overlay2SkyNet.MetricsCollectorDelegator;
import org.peerfact.api.service.skyeye.overlay2SkyNet.TreeHandlerDelegator;
import org.peerfact.api.transport.TransLayer;
import org.peerfact.impl.overlay.AbstractOverlayNode;
import org.peerfact.impl.overlay.AbstractOverlayNode.PeerStatus;
import org.peerfact.impl.service.aggregation.skyeye.AbstractSkyNetNode;
import org.peerfact.impl.service.aggregation.skyeye.SkyNetEventObject;
import org.peerfact.impl.service.aggregation.skyeye.SkyNetHostProperties;
import org.peerfact.impl.service.aggregation.skyeye.SkyNetPropertiesReader;
import org.peerfact.impl.service.aggregation.skyeye.SkyNetUtilities;
import org.peerfact.impl.service.aggregation.skyeye.SupportPeerInfo;
import org.peerfact.impl.service.aggregation.skyeye.analyzing.analyzers.QueryAnalyzer;
import org.peerfact.impl.service.aggregation.skyeye.attributes.AttributeEntry;
import org.peerfact.impl.service.aggregation.skyeye.attributes.operations.AttributeUpdateOperation;
import org.peerfact.impl.service.aggregation.skyeye.attributes.operations.ParentCoordinatorInformationOperation;
import org.peerfact.impl.service.aggregation.skyeye.metrics.MetricStorage;
import org.peerfact.impl.service.aggregation.skyeye.metrics.MetricsAggregate;
import org.peerfact.impl.service.aggregation.skyeye.metrics.MetricsEntry;
import org.peerfact.impl.service.aggregation.skyeye.metrics.MetricsInterpretation;
import org.peerfact.impl.service.aggregation.skyeye.metrics.messages.MetricUpdateACKMsg;
import org.peerfact.impl.service.aggregation.skyeye.metrics.operations.AggregationResultDummyOperation;
import org.peerfact.impl.service.aggregation.skyeye.metrics.operations.AggregationResultMapDummyOperation;
import org.peerfact.impl.service.aggregation.skyeye.metrics.operations.MetricUpdateOperation;
import org.peerfact.impl.service.aggregation.skyeye.queries.Query;
import org.peerfact.impl.service.aggregation.skyeye.queries.QueryCreator;
import org.peerfact.impl.service.aggregation.skyeye.queries.QueryResolver;
import org.peerfact.impl.simengine.SimulationEvent;
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
 * This class is the top-level class of a SkyNet-node, which comprises the
 * functionalities for the parts of a Coordinator and a Support Peer. It
 * triggers and organizes the several periodical routines, like
 * <i>attribute-update</i> or <i>metric-update</i> and is responsible for the
 * implementation of the interaction and communication with other SkyNet-nodes.
 * Moreover, it contains the methods for initializing or resetting all
 * components of a SkyNet-node, if the host goes on- or off-line.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 15.11.2008
 * 
 */
public class SkyNetNode extends AbstractSkyNetNode implements
		AggregationService<String> {

	private static Logger log = SimLogger.getLogger(SkyNetNode.class);

	private final int simulationSize;

	private long presentTime;

	private long metricEventArrived;

	private final LinkedList<Long> onlineTimes;

	private final QueryResolver qResolver;

	private final long queryRemainderTime;

	private final int queryStartingProbability;

	private int queryID;

	public SkyNetNode(SkyNetNodeInfo nodeInfo, short port,
			TransLayer transLayer, OverlayNode<?, ?> overlayNode,
			int simulationSize,
			TreeHandlerDelegator treeHandlerDelegator,
			MetricsCollectorDelegator metricsCollectorDelegator) {
		super(nodeInfo, port, transLayer, overlayNode, treeHandlerDelegator,
				metricsCollectorDelegator);
		this.simulationSize = simulationSize;
		metricEventArrived = Simulator.getCurrentTime();
		onlineTimes = new LinkedList<Long>();
		qResolver = new QueryResolver(this);
		queryRemainderTime = SkyNetPropertiesReader.getInstance()
				.getTimeProperty("QueryRemainderTime");
		queryStartingProbability = SkyNetPropertiesReader.getInstance()
				.getIntProperty("QueryStartingProbability");
		queryID = 1;
	}

	@Override
	public void eventOccurred(SimulationEvent se) {
		if (((SkyNetEventObject) se.getData()).getInitTime() >= presentTime) {
			// Receiving a METRICS_UPDATE-Event
			if (((SkyNetEventObject) se.getData()).getType().equals(
					SkyNetEventType.METRICS_UPDATE)) {
				if (((AbstractOverlayNode<?, ?>) getOverlayNode())
						.getPeerStatus()
						.equals(PeerStatus.PRESENT)) {
					if (((SkyNetEventObject) se.getData())
							.getMetricsUpdateCounter() == getMetricUpdateStrategy()
							.getMetricsUpdateCounter()) {
						long temp = Simulator.getCurrentTime();
						if (temp == metricEventArrived) {
							log.warn(SkyNetUtilities.getTimeAndNetID(this)
									+ "received more than one metric-update-event"
									+ " in one period, which will not be processed");
						} else {
							metricEventArrived = temp;
							processMetricsUpdate();
						}
					} else {
						log.warn(SkyNetUtilities.getTimeAndNetID(this)
								+ " received old metricEvent "
								+ ((SkyNetEventObject) se.getData())
										.getMetricsUpdateCounter());
					}

				} else {
					log.info("SkyNetNode cannot process MetricUpdate"
							+ ", because he is not PRESENT");
				}
				// Receiving a ATTRIBUTE_UPDATE-Event
			} else if (((SkyNetEventObject) se.getData()).getType().equals(
					SkyNetEventType.ATTRIBUTE_UPDATE)) {
				if (((AbstractOverlayNode<?, ?>) getOverlayNode())
						.getPeerStatus()
						.equals(PeerStatus.PRESENT)) {
					processAttributeUpdate();
				} else {
					log.info("SkyNetNode cannot process AttributeUpdate"
							+ ", because he is not PRESENT");
				}
				// Receiving a SUPPORT_PEER_UPDATE-Event
			} else if (((SkyNetEventObject) se.getData()).getType().equals(
					SkyNetEventType.SUPPORT_PEER_UPDATE)) {
				if (((AbstractOverlayNode<?, ?>) getOverlayNode())
						.getPeerStatus()
						.equals(PeerStatus.PRESENT)) {
					processSupportPeerUpdate();
				} else {
					log.info("SupportPeer cannot process SupportPeerUpdate"
							+ ", because he is not PRESENT");
				}
				// Receiving a PARENT_COORDINATOR_INFORMATION_UPDATE-Event
			} else if (((SkyNetEventObject) se.getData()).getType().equals(
					SkyNetEventType.PARENT_COORDINATOR_INFORMATION_UPDATE)) {
				if (((AbstractOverlayNode<?, ?>) getOverlayNode())
						.getPeerStatus()
						.equals(PeerStatus.PRESENT)) {
					processParentCoordinatorInformationUpdate();
				} else {
					log.info("SkyNetNode cannot process"
							+ " ParentCoordinatorInformationUpdate"
							+ ", because he is not PRESENT");
				}
				// Receiving a QUERY_REMAINDER-Event
			} else if (((SkyNetEventObject) se.getData()).getType().equals(
					SkyNetEventType.QUERY_REMAINDER)) {
				if (((AbstractOverlayNode<?, ?>) getOverlayNode())
						.getPeerStatus()
						.equals(PeerStatus.PRESENT)) {
					processQueryRemainderEvent();
				} else {
					log.info("SkyNetNode cannot process"
							+ " QUERY_REMAINDER-Event"
							+ ", because he is not PRESENT");
				}
			} else {
				log.error("Received an unknown SimulationEvent");
			}
		} else {
			log.info(Simulator.getFormattedTime(Simulator.getCurrentTime())
					+ "Event is to old");
		}
	}

	/**
	 * If <code>eventOccurred()</code> is called due to a fired
	 * <code>SkyNetEventType</code>, this method executes a <i>metric-update</i>
	 * if a <code>METRICS_UPDATE</code>-event occurred.
	 */
	private void processMetricsUpdate() {
		getMetricUpdateStrategy().removeStaleSubCoordinators();
		getMetricInputStrategy().writeOwnDataInStorage();
		getMetricUpdateStrategy().setDataToSend();
		getMetricUpdateStrategy().setSendingTime(Simulator.getCurrentTime());
		getTreeHandler().calculateResponsibilityInterval(
				getSkyNetNodeInfo().getSkyNetID());
	}

	/**
	 * If <code>eventOccurred()</code> is called due to a fired
	 * <code>SkyNetEventType</code>, this method executes a
	 * <i>attribute-update</i> for a Coordinator, if a
	 * <code>ATTRIBUTE_UPDATE</code>-event occurred.
	 */
	private void processAttributeUpdate() {
		getAttributeUpdateStrategy().removeStaleSubCoordinators();
		getAttributeInputStrategy().writeOwnDataInStorage();
		getAttributeUpdateStrategy().setDataToSend();
		getAttributeUpdateStrategy().setSendingTime(Simulator.getCurrentTime());
		getAttributeUpdateStrategy().sendNextDataUpdate();
	}

	/**
	 * If <code>eventOccurred()</code> is called due to a fired
	 * <code>SkyNetEventType</code>, this method executes a
	 * <i>attribute-update</i> for a Support Peer, if a
	 * <code>SUPPORT_PEER_UPDATE</code>-event occurred.
	 */
	private void processSupportPeerUpdate() {
		if (getSPAttributeUpdateStrategy().isProcessSupportPeerEvents()) {
			getSPAttributeUpdateStrategy().removeStaleSubCoordinators();
			getSPAttributeUpdateStrategy().setDataToSend();
			getSPAttributeUpdateStrategy().setSendingTime(
					Simulator.getCurrentTime());
			getSPAttributeUpdateStrategy().sendNextDataUpdate();
		} else {
			log.warn(SkyNetUtilities.getTimeAndNetID(this)
					+ " blocks SUPPORTPEER-Update"
					+ ", because node is not longer needed as SupportPeer");
		}
	}

	/**
	 * If <code>eventOccurred()</code> is called due to a fired
	 * <code>SkyNetEventType</code>, this method prompts the Coordinator to
	 * process the advertised amount of attribute-entries from its
	 * Sub-Coordinators and to communicate the outcome of the processing to its
	 * children.
	 */
	private void processParentCoordinatorInformationUpdate() {
		// check if more space for entries is requested and if
		// actually no search for a SupportPeer is running
		if (!getAttributeInputStrategy().isBlockSupportPeerSearch()) {
			getAttributeInputStrategy().compareTempWithActualEntryRequest();
			if (getAttributeInputStrategy().isMoreEntriesRequested()) {
				getAttributeInputStrategy().checkForSupportPeer();
			} else {
				log.debug(Simulator.getFormattedTime(Simulator.getCurrentTime())
						+ getSkyNetNodeInfo().getTransInfo().getNetId()
								.toString()
						+ " needs not to assign new amount of entries to his SubCos"
						+ ", because there are not more requested");
			}
		} else {
			log.warn(Simulator.getFormattedTime(Simulator.getCurrentTime())
					+ getSkyNetNodeInfo().getTransInfo().getNetId().toString()
					+ " cannot process PARENT_COORDINATOR_INFORMATION_UPDATE"
					+ ", because it is searching SupportPeer");
		}
	}

	/**
	 * If <code>eventOccurred()</code> is called due to a fired
	 * <code>SkyNetEventType</code>, this method is used to automatically
	 * originate a query.
	 */
	private void processQueryRemainderEvent() {
		if (Simulator.getRandom().nextInt(100) < queryStartingProbability) {

			startQuery(QueryCreator.getInstance(simulationSize)
					.createQueryString());
		}
		// Schedule next query-remainder
		long time = Simulator.getCurrentTime();
		long queryRemainderStartTime = time + queryRemainderTime;
		Simulator.scheduleEvent(new SkyNetEventObject(
				SkyNetEventType.QUERY_REMAINDER, time),
				queryRemainderStartTime, this, null);
	}

	/**
	 * This method starts a <code>MetricUpdateOperation</code>, which executes a
	 * <i>metric-update</i>, and provides the operation with the needed values.
	 * 
	 * @param senderInfo
	 *            contains the ID of the sender
	 * @param receiverInfo
	 *            contains the ID of the receiver
	 * @param content
	 *            contains the metrics for the update
	 * @param skyNetMsgID
	 *            specifies the ID of the message
	 * @param callback
	 *            contains the object, which receives the corresponding ACK
	 * @return the ID of the executed operation
	 */
	public int metricUpdate(SkyNetNodeInfo senderInfo,
			SkyNetNodeInfo receiverInfo, MetricsEntry content,
			long skyNetMsgID, OperationCallback<MetricUpdateACKMsg> callback) {
		MetricUpdateOperation op = new MetricUpdateOperation(this, senderInfo,
				receiverInfo, content, skyNetMsgID, callback);
		op.scheduleImmediately();
		return op.getOperationID();
	}

	/**
	 * This method starts an <code>AttributeUpdateOperation</code>, which
	 * executes an <i>attribute-update</i>, and provides the operation with the
	 * needed values.
	 * 
	 * @param senderInfo
	 *            contains the ID of the sender
	 * @param receiverInfo
	 *            contains the ID of the receiver
	 * @param attributeEntries
	 *            contains the attribute-entries for the update
	 * @param numberOfUpdates
	 *            if this <i>attribute-update</i> is addressed to a Support
	 *            Peer, this variable contains the amount of updates, which this
	 *            node will still send to the Support Peer
	 * @param maxEntries
	 *            contains the maximum amount of entries, which a Coordinator
	 *            wishes to send
	 * @param downSupportPeer
	 *            specifies, if an addressed Support Peer is down
	 * @param skyNetMsgID
	 *            specifies the ID of the message
	 * @param receiverSP
	 *            specifies, if the receiver of the message is a Support Peer
	 * @param senderSP
	 *            specifies, if the sender of the message is a Support Peer
	 * @param callback
	 *            contains the object, which receives the corresponding ACK
	 * @return the ID of the executed operation
	 */
	public int attributeUpdate(SkyNetNodeInfo senderInfo,
			SkyNetNodeInfo receiverInfo,
			TreeMap<BigDecimal, AttributeEntry> attributeEntries,
			int numberOfUpdates, int maxEntries, boolean downSupportPeer,
			long skyNetMsgID, boolean receiverSP, boolean senderSP,
			OperationCallback<Object> callback) {
		AttributeUpdateOperation op = new AttributeUpdateOperation(this,
				senderInfo, receiverInfo, attributeEntries, numberOfUpdates,
				maxEntries, downSupportPeer, skyNetMsgID, receiverSP, senderSP,
				callback);
		op.scheduleImmediately();
		return op.getOperationID();
	}

	/**
	 * This method starts an <code>ParentCoordinatorInformationOperation</code>
	 * and provides the operation with the needed values. The transmitted values
	 * are meant for the Sub-Coordinators of a Coordinator and contain the
	 * information of a potential Support Peer, as well as the maximum amount of
	 * attribute-entries, which a Coordinator can handle from its addressed
	 * Sub-Coordinator.
	 * 
	 * @param senderInfo
	 *            contains the ID of the sender
	 * @param receiverInfo
	 *            contains the ID of the receiver
	 * @param spInfo
	 *            contains the <code>SupportPeerInfo</code>-object, if a Support
	 *            Peer is provided to the Sub-Coordinators
	 * @param maxEntriesForCo
	 *            contains the maximum amount of attribute-entries, which a
	 *            Coordinator can receive
	 * @param skyNetMsgID
	 *            specifies the ID of the message
	 * @param receiverSP
	 *            specifies, if the receiver of the message is a Support Peer
	 * @param callback
	 *            contains the object, which receives the corresponding ACK
	 * @return the ID of the executed operation
	 */
	public int parentCoordinatorInfoUpdate(SkyNetNodeInfo senderInfo,
			SkyNetNodeInfo receiverInfo, SupportPeerInfo spInfo,
			int maxEntriesForCo, long skyNetMsgID, boolean receiverSP,
			OperationCallback<Object> callback) {

		ParentCoordinatorInformationOperation op = new ParentCoordinatorInformationOperation(
				this, senderInfo, receiverInfo, spInfo, maxEntriesForCo,
				skyNetMsgID, receiverSP, callback);
		op.scheduleImmediately();
		return op.getOperationID();
	}

	/**
	 * This method obtains a query as <code>String</code> and converts it to an
	 * <code>Query</code>-object. Before sending the query to the over-overlay,
	 * it is tried to solve the query locally by accessing the data of
	 * <code>AttributeStorage</code>. If the query cannot be solved locally,
	 * this method calls <code>QueryHandler</code>, which is responsible for
	 * transmitting the query to the next node in the over-overlay.
	 * 
	 * @param queryString
	 *            the query, represented as <code>String</code>
	 */
	public void startQuery(String queryString) {
		QueryAnalyzer.getInstance().queryStarted(
				qResolver.createQuery(queryString));
		Query actualQuery = getQueryHandler().localLookupAtAttributeStorage(
				qResolver.createQuery(queryString));
		actualQuery.setQueryID(queryID);
		queryID++;
		// check if the query can already be solved with the local knowledge.
		// Otherwise transmit the query
		if (actualQuery.getIndexOfSolvedAddend() != -1) {
			log.warn(SkyNetUtilities.getTimeAndNetID(this)
					+ "could solve query " + actualQuery.getQueryID()
					+ " locally");
			QueryAnalyzer.getInstance().solvedQueryReceived(actualQuery);
		} else {
			// check first if a SupportPeer is in use, which could probably
			// resolve the query. If no SupportPeer is in use, send the query to
			// the ParentCoordinator
			if (getAttributeInputStrategy().isSupportPeerInUse()) {
				SkyNetNodeInfo supportPeer = getAttributeInputStrategy()
						.getSpHandler().getActiveSupportPeer().getNodeInfo();
				getQueryHandler().startQuery(getSkyNetNodeInfo(), supportPeer,
						actualQuery, true);
			} else if (!getTreeHandler().isRoot()) {
				SkyNetNodeInfo parentCo = getTreeHandler()
						.getParentCoordinator();
				if (parentCo != null) {
					getQueryHandler().startQuery(getSkyNetNodeInfo(), parentCo,
							actualQuery, false);
				} else {
					log.error(SkyNetUtilities.getTimeAndNetID(this)
							+ "Cannot send query"
							+ ", since no parentCoordinator is known");
				}
			} else {
				log.warn(SkyNetUtilities.getTimeAndNetID(this)
						+ "cannot forward query, since it is the root");
			}
		}
	}

	@Override
	public void connectivityChanged(ConnectivityEvent ce) {
		// Not needed, using the connectivity-status of the underlying overlay
	}

	/**
	 * This method puts the whole SkyNet-node, including all components, in its
	 * initial state, if the host leaves the overlay or goes off-line. The
	 * passed time is used to mark the point in time, when this method was
	 * called. Additionally, <code>SkyNetNode</code> utilizes this time-stamp to
	 * calculate the duration of the last interval of being present or online.
	 * 
	 * @param currentTime
	 *            the point in time, when this method is called
	 */
	public void resetSkyNetNode(long currentTime) {
		addOnlineTime(currentTime - presentTime);
		if (getTreeHandler().isRoot()) {
			log.warn(SkyNetUtilities.getTimeAndNetID(this)
					+ "lost the root-position");
		}
		getTreeHandler().reset();
		getMetricsInterpretation().reset();
		getMetricUpdateStrategy().reset();
		getAttributeInputStrategy().reset();
		getAttributeUpdateStrategy().reset();
		getSPAttributeUpdateStrategy().reset();
	}

	public long getPresentTime() {
		return presentTime;
	}

	public void setPresentTime(long presentTime) {
		this.presentTime = presentTime;
	}

	private void addOnlineTime(long time) {
		onlineTimes.add(time);
		if (onlineTimes.size() > 10) {
			onlineTimes.pollFirst();
		}
		if (onlineTimes.size() > 10) {
			log.error("Size of online-Times is bigger than 10");
		}
	}

	/**
	 * This method is used to calculate the average online-time of a host during
	 * a simulation.
	 * 
	 * @return the average online-time of a host
	 */
	public double getAverageOnlineTimeInSec() {
		double sum = 0;
		for (int i = 0; i < onlineTimes.size(); i++) {
			sum = sum + onlineTimes.get(i).doubleValue();
		}
		double result = (sum / onlineTimes.size())
				/ SkyNetConstants.DIVISOR_FOR_SECOND;
		if (new Double(result).isNaN()) {
			return (Simulator.getCurrentTime() - presentTime)
					/ SkyNetConstants.DIVISOR_FOR_SECOND;
		} else {
			return result;
		}
	}

	public double getAverageOnlineTime() {
		double sum = 0;
		long currentOnlineTime = Simulator.getCurrentTime() - presentTime;
		for (int i = 0; i < onlineTimes.size(); i++) {
			sum = sum + onlineTimes.get(i).doubleValue();
		}
		return (sum + currentOnlineTime) / (onlineTimes.size() + 1);
	}

	/**
	 * This method is called when a host joins the overlay. Since it is a
	 * participant of that overlay, SkyNet is started to monitor the behavior of
	 * the peer and of the overlay. To perform all specified tasks, this method
	 * initializes the complete SkyNet-node and starts all required periodical
	 * routines.
	 * 
	 * @param time
	 *            contains the point in time, when this method is called
	 */
	public void startSkyNetNode(long time) {
		// set the calculated SkyNetID and start the timers
		getSkyNetMessageHandler().setTryingJoin(false);
		log.info(getSkyNetNodeInfo().toString());

		setPresentTime(time);

		// Schedule next metric-update
		getMetricUpdateStrategy().setSendingTime(time);
		long metricsTime = time + getMetricUpdateStrategy().getUpdateInterval();
		Simulator.scheduleEvent(new SkyNetEventObject(
				SkyNetEventType.METRICS_UPDATE, time, getMetricUpdateStrategy()
						.getMetricsUpdateCounter()), metricsTime, this, null);

		// Schedule next attribute-update
		getAttributeUpdateStrategy().setSendingTime(time);
		long attributeTime = time
				+ getAttributeUpdateStrategy().getUpdateInterval();
		Simulator.scheduleEvent(new SkyNetEventObject(
				SkyNetEventType.ATTRIBUTE_UPDATE, time), attributeTime, this,
				null);

		// Schedule next query-remainder
		long queryRemainderStartTime = time + 2 * queryRemainderTime
				+ Simulator.getRandom().nextInt((int) queryRemainderTime);
		Simulator.scheduleEvent(new SkyNetEventObject(
				SkyNetEventType.QUERY_REMAINDER, time),
				queryRemainderStartTime, this, null);
		// other inits
		((SkyNetHostProperties) getHost().getProperties()).init();
	}

	public long getQueryRemainderTime() {
		return queryRemainderTime;
	}

	@Override
	public NeighborDeterminator<OverlayContact<OverlayID<?>>> getNeighbors() {
		return null;
	}

	// ---------------------------------------------
	// Implementing the methods of the IAggregationService Interface
	// ---------------------------------------------

	@Override
	public double setLocalValue(Object identifier, double value)
			throws NoSuchValueException {
		// get the previously stored aggregate, which should just contain one
		// element
		MetricsAggregate aggregate = getMetricInputStrategy()
				.getMetricStorage().getOwnMetrics().getMetrics()
				.remove(identifier);
		// set the provided local value
		getMetricInputStrategy()
				.getMetricStorage()
				.getOwnMetrics()
				.getMetrics()
				.put((String) identifier,
						new MetricsAggregate((String) identifier, value, value,
								value, Math.pow(value, 2), 1, Simulator
										.getCurrentTime(), Simulator
										.getCurrentTime(), Simulator
										.getCurrentTime()));
		// return the previously stored aggregate if available
		if (aggregate != null) {
			return aggregate.getSumOfAggregates();
		}
		// otherwise return -1
		return -1;
	}

	@Override
	public double getLocalValue(Object identifier) throws NoSuchValueException {
		// returns the local value for the given identifier
		AggregationResult val = null;
		if (getMetricInputStrategy() != null
				&& getMetricInputStrategy().getMetricStorage() != null
				&& getMetricInputStrategy().getMetricStorage().getOwnMetrics() != null) {
			val = getMetricInputStrategy().getMetricStorage().getOwnMetrics()
					.getAggregationResult((String) identifier);
		} else {
			return Double.MAX_VALUE;
		}
		if (val == null) {
			throw new NoSuchValueException(identifier);
		}
		return val.getAverage();
	}

	@Override
	public void join(OperationCallback<Object> cb) {
		// Starts the Skynet-Node out of the interface
		startSkyNetNode(Simulator.getCurrentTime());
	}

	@Override
	public void leave(OperationCallback<Object> cb) {
		// Stops the Skynet-Node out of the interface
		resetSkyNetNode(Simulator.getCurrentTime());
	}

	@Override
	public int getAggregationResultMap(
			OperationCallback<AggregationMap<String>> callback) {
		if (((AbstractOverlayNode<?, ?>) getOverlayNode()).getPeerStatus()
				.equals(
						PeerStatus.PRESENT)) {
			AggregationMap<String> aggMap = getMetricsInterpretation()
					.getActualSystemStatistics();
			AggregationResultMapDummyOperation op = new AggregationResultMapDummyOperation(
					this, callback, aggMap);
			op.scheduleImmediately();
			return op.getOperationID();
		} else {
			return -1;
		}
	}

	@Override
	public int getAggregationResult(final Object identifier,
			final OperationCallback<AggregationResult> callback)
			throws NoSuchValueException {
		if (((AbstractOverlayNode<?, ?>) getOverlayNode()).getPeerStatus()
				.equals(
						PeerStatus.PRESENT)) {
			MetricsEntry entry = getMetricsInterpretation()
					.getActualSystemStatistics();
			if (entry == null) {
				return -2;
			}
			AggregationResult val = entry.getMetrics().get(identifier);
			if (val == null) {
				throw new NoSuchValueException(identifier);
			}
			AggregationResultDummyOperation op = new AggregationResultDummyOperation(
					this, new OperationCallback<AggregationResult>() {

						@Override
						public void calledOperationFailed(
								Operation<AggregationResult> operation) {
							Simulator.getMonitor().aggregationQueryFailed(
									SkyNetNode.this.getHost(), identifier,
									operation.getOperationID());
							callback.calledOperationFailed(operation);

						}

						@Override
						public void calledOperationSucceeded(
								Operation<AggregationResult> operation) {
							Simulator.getMonitor().aggregationQuerySucceeded(
									SkyNetNode.this.getHost(), identifier,
									operation.getOperationID(),
									operation.getResult());
							callback.calledOperationSucceeded(operation);
						}
					}, val);
			op.scheduleImmediately();
			Simulator.getMonitor().aggregationQueryStarted(this.getHost(),
					identifier, op.getOperationID());
			return op.getOperationID();
		} else {
			return -1;
		}
	}

	public void getAggregationResultAction(String identifier) {

		int state = 0;
		try {
			state = getAggregationResult(identifier,
					new OperationCallback<AggregationResult>() {

						@Override
						public void calledOperationFailed(
								Operation<AggregationResult> op) {
							// not required
						}

						@Override
						public void calledOperationSucceeded(
								Operation<AggregationResult> op) {
							// TODO implement call for a respective analyzer
						}

					});
		} catch (NoSuchValueException e) {
			e.printStackTrace();
		}
		switch (state) {
		case -1:
			log.warn(SkyNetUtilities.getTimeAndNetID(getSkyNetNodeInfo())
					+ "is not present in the overlay to determine its global view");
			break;
		case -2:
			log.warn(SkyNetUtilities.getTimeAndNetID(getSkyNetNodeInfo())
					+ "has currently no global view available");
			break;
		default:
			break;
		}
	}

	@Override
	public List<Object> getIdentifiers() {
		List<Object> result = new Vector<Object>();
		if (getMetricsInterpretation() != null
				&& getMetricsInterpretation().getActualSystemStatistics() != null) {
			result.addAll(getMetricsInterpretation()
					.getActualSystemStatistics().getKeySet());
		}
		return result;
	}

	@Override
	public AggregationResult getStoredAggregationResult(Object identifier) {
		MetricsEntry entry = getMetricsInterpretation()
				.getActualSystemStatistics();
		if (entry == null) {
			return null;
		}
		return entry.getMetrics().get(identifier);
	}

	@Override
	public long getGlobalAggregationReceivingTime(Object identifier) {
		MetricsInterpretation metricInterpretation = getMetricsInterpretation();
		if (metricInterpretation == null) {
			return 0;
		}
		return metricInterpretation.getReceivingTimestamp();
	}

	@Override
	public int getNumberOfMonitoredAttributes() {
		MetricsInterpretation metricInterpretation = getMetricsInterpretation();
		if (metricInterpretation == null) {
			return 0;
		}
		MetricStorage metricStorage = metricInterpretation.getStorage();
		if (metricStorage == null) {
			return 0;
		}
		MetricsEntry metricEntry = metricStorage.getOwnMetrics();
		if (metricEntry == null) {
			return 0;
		}
		return metricEntry.getMapSize();
	}
}
