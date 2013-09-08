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
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.apache.log4j.Logger;
import org.peerfact.api.common.Message;
import org.peerfact.api.service.skyeye.SkyNetEventType;
import org.peerfact.api.service.skyeye.SkyNetNodeInterface;
import org.peerfact.api.service.skyeye.SkyNetSimulationType;
import org.peerfact.api.service.skyeye.SupportPeer;
import org.peerfact.api.service.skyeye.SkyNetSimulationType.SimulationType;
import org.peerfact.api.transport.TransMessageListener;
import org.peerfact.api.transport.TransProtocol;
import org.peerfact.impl.overlay.AbstractOverlayNode;
import org.peerfact.impl.overlay.AbstractOverlayNode.PeerStatus;
import org.peerfact.impl.overlay.dht.centralizedindex.components.CIClientNode;
import org.peerfact.impl.overlay.dht.centralizedindex.components.CIOverlayContact;
import org.peerfact.impl.overlay.dht.kademlia.kademlia.components.KademliaNode;
import org.peerfact.impl.service.aggregation.skyeye.SkyNetEventObject;
import org.peerfact.impl.service.aggregation.skyeye.SkyNetHostProperties;
import org.peerfact.impl.service.aggregation.skyeye.SkyNetPropertiesReader;
import org.peerfact.impl.service.aggregation.skyeye.SkyNetUtilities;
import org.peerfact.impl.service.aggregation.skyeye.attributes.messages.AttributeUpdateACKMsg;
import org.peerfact.impl.service.aggregation.skyeye.attributes.messages.AttributeUpdateMsg;
import org.peerfact.impl.service.aggregation.skyeye.attributes.messages.ParentCoordinatorInformationACKMsg;
import org.peerfact.impl.service.aggregation.skyeye.attributes.messages.ParentCoordinatorInformationMsg;
import org.peerfact.impl.service.aggregation.skyeye.attributes.messages.SupportPeerRequestACKMsg;
import org.peerfact.impl.service.aggregation.skyeye.attributes.messages.SupportPeerRequestMsg;
import org.peerfact.impl.service.aggregation.skyeye.attributes.messages.SupportPeerUpdateACKMsg;
import org.peerfact.impl.service.aggregation.skyeye.attributes.messages.SupportPeerUpdateMsg;
import org.peerfact.impl.service.aggregation.skyeye.metrics.MetricsInterpretation;
import org.peerfact.impl.service.aggregation.skyeye.metrics.MetricsSubCoordinatorInfo;
import org.peerfact.impl.service.aggregation.skyeye.metrics.messages.MetricUpdateACKMsg;
import org.peerfact.impl.service.aggregation.skyeye.metrics.messages.MetricUpdateMsg;
import org.peerfact.impl.service.aggregation.skyeye.metrics.messages.MetricUpdateSyncMsg;
import org.peerfact.impl.service.aggregation.skyeye.queries.messages.QueryForwardACKMsg;
import org.peerfact.impl.service.aggregation.skyeye.queries.messages.QueryForwardMsg;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.transport.TransMsgEvent;
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
 * This class is responsible for the treatment of incoming SkyNet-messages. It
 * implements the {@link TransMessageListener}-interface, which explains, how
 * the messages are received and delivered to the appropriate component.<br>
 * For every message, which is received by <code>SkyNetMessageHandler</code>,
 * the MessageHandler responds with the corresponding ACK.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 04.12.2008
 * 
 */
public class SkyNetMessageHandler implements TransMessageListener {

	private static Logger log = SimLogger.getLogger(SkyNetMessageHandler.class);

	private final SkyNetNodeInterface skyNetNode;

	private final SupportPeer supportPeer;

	private long errorTimestamp;

	private boolean tryingJoin;

	private final LinkedHashMap<BigDecimal, QueryForwardACKMsg> queryMsgCounter;

	private final boolean alwaysPushSystemStatistics;

	public void setTryingJoin(boolean tryingJoin) {
		this.tryingJoin = tryingJoin;

	}

	public SkyNetMessageHandler(SkyNetNodeInterface skyNetNode,
			SupportPeer supportPeer) {
		alwaysPushSystemStatistics = SkyNetPropertiesReader.getInstance()
				.getBooleanProperty("AlwaysPushSystemStatistics");
		this.skyNetNode = skyNetNode;
		this.supportPeer = supportPeer;
		tryingJoin = false;
		queryMsgCounter = new LinkedHashMap<BigDecimal, QueryForwardACKMsg>();
	}

	@Override
	public void messageArrived(TransMsgEvent receivingEvent) {
		if (((AbstractOverlayNode<?, ?>) skyNetNode.getOverlayNode())
				.getPeerStatus()
				.equals(PeerStatus.PRESENT)
				|| (skyNetNode.getOverlayNode() instanceof KademliaNode && ((KademliaNode<?>) skyNetNode
						.getOverlayNode()).getPeerStatus().equals(
						PeerStatus.PRESENT))) {
			long timestamp = Simulator.getCurrentTime();
			Message msg = receivingEvent.getPayload();
			if (msg instanceof MetricUpdateMsg) {
				MetricUpdateMsg request = (MetricUpdateMsg) msg;
				processMetricUpdate(request, receivingEvent, timestamp);
			} else if (msg instanceof ParentCoordinatorInformationMsg) {
				ParentCoordinatorInformationMsg request = (ParentCoordinatorInformationMsg) msg;
				processParentCoordinatorInfo(request, receivingEvent, timestamp);
			} else if (msg instanceof AttributeUpdateMsg) {
				AttributeUpdateMsg request = (AttributeUpdateMsg) msg;
				processAttributeUpdate(request, receivingEvent, timestamp,
						request.isReceiverSP());
			} else if (msg instanceof SupportPeerRequestMsg) {
				SupportPeerRequestMsg request = (SupportPeerRequestMsg) msg;
				processSupportPeerRequest(request, receivingEvent, timestamp);
			} else if (msg instanceof SupportPeerUpdateMsg) {
				SupportPeerUpdateMsg request = (SupportPeerUpdateMsg) msg;
				processSupportPeerUpdate(request, receivingEvent);
			} else if (msg instanceof MetricUpdateSyncMsg) {
				MetricUpdateSyncMsg request = (MetricUpdateSyncMsg) msg;
				processMetricUpdateSyncMsg(request, receivingEvent);
			} else if (msg instanceof QueryForwardMsg) {
				QueryForwardMsg request = (QueryForwardMsg) msg;
				BigDecimal senderID = request.getSenderNodeInfo().getSkyNetID()
						.getID();

				// This if-block is used to test if (a) a new query-message is
				// received, which must be normally processed or if (b) an old
				// query-message is received, that was already processed. If
				// case (b) is encountered, an ack is directly retransmitted to
				// satisfy the originator of the query-message
				if (queryMsgCounter.containsKey(senderID)) {
					if (queryMsgCounter.get(senderID).getSkyNetMsgID() < request
							.getSkyNetMsgID()) {
						processQueryForwardMsg(request, receivingEvent);
					} else {
						log.warn(SkyNetUtilities.getTimeAndNetID(skyNetNode)
								+ "Recreate ACK-message with ID "
								+ request.getSkyNetMsgID()
								+ " instead of ID "
								+ queryMsgCounter.get(senderID)
										.getSkyNetMsgID()
								+ " for "
								+ SkyNetUtilities.getNetID(queryMsgCounter.get(
										senderID).getReceiverNodeInfo()));
						Message reply = new QueryForwardACKMsg(request
								.getReceiverNodeInfo(), request
								.getSenderNodeInfo(), request.getSkyNetMsgID(),
								request.isSenderSP(), request.isReceiverSP());
						supportPeer.getTransLayer().sendReply(reply,
								receivingEvent, skyNetNode.getPort(),
								TransProtocol.UDP);
					}
				} else {
					processQueryForwardMsg(request, receivingEvent);
				}

			} else {
				log.warn("Received unknown message type");
			}

		} else {
			if (((AbstractOverlayNode<?, ?>) skyNetNode.getOverlayNode())
					.getPeerStatus().equals(PeerStatus.ABSENT)
					|| (skyNetNode.getOverlayNode() instanceof KademliaNode && ((KademliaNode<?>) skyNetNode
							.getOverlayNode()).getPeerStatus().equals(
							PeerStatus.ABSENT))) {
				if (skyNetNode.getHost().getNetLayer().isOnline()) {
					if (SkyNetSimulationType.getSimulationType().equals(
							SimulationType.NAPSTER_SIMULATION)) {
						manualJoin();
					} else if (SkyNetSimulationType.getSimulationType().equals(
							SimulationType.CHORD_SIMULATION)) {
						log.fatal("A manualJoin-method for Chord is needed");
					} else if (SkyNetSimulationType.getSimulationType().equals(
							SimulationType.KADEMLIA_SIMULATION)) {
						/*
						 * if (((KBRKademliaNode) skyNetNode.getOverlayNode())
						 * .getPeerStatus().equals(PeerStatus.ABSENT)) { log
						 * .fatal("A manualJoin-method for Kademlia is needed");
						 * }
						 */
					} else {
						log.error("Unknown SimulationType");
					}
				} else {
					log.warn(SkyNetUtilities.getTimeAndNetID(skyNetNode)
							+ " is absent and offline");
				}
			}
		}
	}

	private void processMetricUpdateSyncMsg(MetricUpdateSyncMsg request,
			TransMsgEvent receivingEvent) {
		if (request.getLastMetricSync()
				- skyNetNode.getMetricUpdateStrategy().getLastMetricSync() >= skyNetNode
				.getMetricUpdateStrategy().getMetricSyncInterval()) {

			long lastMetricSync = request.getLastMetricSync();

			long updateIntervalOffset = 0;
			if ((request.getUpdateIntervalOffset() - (0.5 * skyNetNode
					.getMetricUpdateStrategy().getMetricIntervalDecrease())) >= 0) {
				updateIntervalOffset = request.getUpdateIntervalOffset()
						- skyNetNode.getMetricUpdateStrategy()
								.getMetricIntervalDecrease();
			} else {
				updateIntervalOffset = request.getUpdateIntervalOffset();
			}

			skyNetNode.getMetricUpdateStrategy().setLastMetricSync(
					lastMetricSync);
			skyNetNode.getMetricUpdateStrategy().scheduleNextUpdateEventAt(
					updateIntervalOffset);
			log.warn(SkyNetUtilities.getTimeAndNetID(skyNetNode)
					+ "rescheduled new metricUpdate with offset = "
					+ updateIntervalOffset);
			LinkedHashMap<BigDecimal, MetricsSubCoordinatorInfo> subCoMap = skyNetNode
					.getMetricUpdateStrategy().getStorage()
					.getListOfSubCoordinators();
			Iterator<BigDecimal> iter = subCoMap.keySet().iterator();
			MetricsSubCoordinatorInfo subCoInfo = null;
			while (iter.hasNext()) {
				subCoInfo = subCoMap.get(iter.next());
				Message msg = new MetricUpdateSyncMsg(skyNetNode
						.getSkyNetNodeInfo(), subCoInfo.getNodeInfo(),
						updateIntervalOffset, lastMetricSync, skyNetNode
								.getMessageCounter()
								.assignmentOfMessageNumber());
				skyNetNode.getHost().getTransLayer().send(msg,
						subCoInfo.getNodeInfo().getTransInfo(),
						skyNetNode.getPort(), TransProtocol.UDP);
			}
		} else {
			log.warn(SkyNetUtilities.getTimeAndNetID(skyNetNode)
					+ "received old sync-msg");
		}
	}

	/**
	 * This private method is called if a message of the type
	 * {@link QueryForwardMsg} was received.
	 * 
	 * @param request
	 *            contains the received message.
	 * @param receivingEvent
	 *            contains the event, which encapsulates the message and other
	 *            information concerning that receiving-event
	 */
	private void processQueryForwardMsg(QueryForwardMsg request,
			TransMsgEvent receivingEvent) {
		// create reply- is done before processing to reply just in time
		Message reply = new QueryForwardACKMsg(request.getReceiverNodeInfo(),
				request.getSenderNodeInfo(), request.getSkyNetMsgID(), request
						.isSenderSP(), request.isReceiverSP());
		supportPeer.getTransLayer().sendReply(reply, receivingEvent,
				skyNetNode.getPort(), TransProtocol.UDP);
		BigDecimal senderID = request.getSenderNodeInfo().getSkyNetID().getID();
		queryMsgCounter.put(senderID, (QueryForwardACKMsg) reply);
		// start real processing of the message
		if (request.isSolved()) {
			log.debug(SkyNetUtilities.getTimeAndNetID(skyNetNode)
					+ "received queryAnswer");
			skyNetNode.getQueryHandler().processQueryResult(request);
		} else {
			if (request.isReceiverSP()) {
				log.debug(SkyNetUtilities.getTimeAndNetID(supportPeer)
						+ "as SP received queryForwardMsg");
				supportPeer.getSPQueryHandler().processForeignQuery(request);
			} else {
				log.debug(SkyNetUtilities.getTimeAndNetID(skyNetNode)
						+ "as Co received queryForwardMsg");
				skyNetNode.getQueryHandler().processForeignQuery(request);
			}
		}
	}

	/**
	 * This private method is called if a message of the type
	 * {@link SupportPeerUpdateMsg} was received.
	 * 
	 * @param request
	 *            contains the received message.
	 * @param receivingEvent
	 *            contains the event, which encapsulates the message and other
	 *            information concerning that receiving-event
	 */
	private void processSupportPeerUpdate(SupportPeerUpdateMsg request,
			TransMsgEvent receivingEvent) {
		if (supportPeer.isSupportPeer()) {
			supportPeer.getSPAttributeUpdateStrategy().setBrotherCoordinator(
					request.getSenderNodeInfo());
			supportPeer.getSPAttributeUpdateStrategy().setParentCoordinator(
					request.getParentCoordinatorInfo());
			log.debug(SkyNetUtilities.getTimeAndNetID(supportPeer)
					+ "processed SupportPeerUpdate from "
					+ SkyNetUtilities.getNetID(request.getSenderNodeInfo()));
		} else {
			log.warn(SkyNetUtilities.getTimeAndNetID(skyNetNode)
					+ "received SupportPeerUpdate from "
					+ SkyNetUtilities.getNetID(request.getSenderNodeInfo())
					+ ", but does not process the message"
					+ ", since it is no SupportPeer");

		}
		// create reply
		Message reply = new SupportPeerUpdateACKMsg(request
				.getReceiverNodeInfo(), request.getSenderNodeInfo(), request
				.getSkyNetMsgID(), supportPeer.isSupportPeer());
		supportPeer.getTransLayer().sendReply(reply, receivingEvent,
				skyNetNode.getPort(), TransProtocol.UDP);
	}

	/**
	 * This private method is called if a message of the type
	 * {@link SupportPeerUpdateMsg} was received.
	 * 
	 * @param request
	 *            contains the received message.
	 * @param receivingEvent
	 *            contains the event, which encapsulates the message and other
	 *            information concerning that receiving-event
	 */
	private void processSupportPeerRequest(SupportPeerRequestMsg request,
			TransMsgEvent receivingEvent, long timestamp) {
		if (skyNetNode.isSupportPeer()) {
			log.warn(SkyNetUtilities.getTimeAndNetID(supportPeer)
					+ " Peer already SupportPeer");
			// create reply
			Message msg = new SupportPeerRequestACKMsg(request
					.getReceiverNodeInfo(), request.getSenderNodeInfo(), null,
					false, request.getSkyNetMsgID(), true);
			skyNetNode.getTransLayer().sendReply(msg, receivingEvent,
					skyNetNode.getPort(), TransProtocol.UDP);
		} else {
			log.debug(SkyNetUtilities.getTimeAndNetID(supportPeer)
					+ " SupportPeer-Creation");
			supportPeer.setSupportPeer(true);
			supportPeer.getSPAttributeUpdateStrategy()
					.setProcessSupportPeerEvents(true);
			supportPeer.getSPAttributeUpdateStrategy().setBrotherCoordinator(
					request.getSenderNodeInfo());
			supportPeer.getSPAttributeUpdateStrategy().setParentCoordinator(
					request.getParentCoordinator());

			// Schedule next attribute-update
			long time = Simulator.getCurrentTime();
			supportPeer.getSPAttributeUpdateStrategy().setSendingTime(time);
			long attributeTime = time
					+ (supportPeer.getSPAttributeUpdateStrategy()
							.getUpdateInterval());
			Simulator.scheduleEvent(new SkyNetEventObject(
					SkyNetEventType.SUPPORT_PEER_UPDATE, time), attributeTime,
					supportPeer, null);

			// create reply
			Message msg = new SupportPeerRequestACKMsg(request
					.getReceiverNodeInfo(), request.getSenderNodeInfo(),
					skyNetNode.getSkyNetNodeInfo(), true, request
							.getSkyNetMsgID(), true);
			supportPeer.getTransLayer().sendReply(msg, receivingEvent,
					supportPeer.getPort(), TransProtocol.UDP);
		}
	}

	/**
	 * This private method is called if a message of the type
	 * {@link ParentCoordinatorInformationMsg} was received.
	 * 
	 * @param request
	 *            contains the received message.
	 * @param receivingEvent
	 *            contains the event, which encapsulates the message and other
	 *            information concerning that receiving-event
	 */
	private void processParentCoordinatorInfo(
			ParentCoordinatorInformationMsg request,
			TransMsgEvent receivingEvent, long timestamp) {

		if (request.isReceiverSP()) {
			// receiver is SupportPeer
			if (supportPeer.getSPAttributeUpdateStrategy()
					.getParentCoordinator() != null) {
				if (request.getSenderNodeInfo().getSkyNetID().getID()
						.compareTo(
								supportPeer.getSPAttributeUpdateStrategy()
										.getParentCoordinator().getSkyNetID()
										.getID()) == 0) {
					log.debug(supportPeer.getSkyNetNodeInfo().getTransInfo()
							.getNetId().toString()
							+ " Received as SupportPeer "
							+ request.getClass().getSimpleName());
					supportPeer.getSPAttributeUpdateStrategy()
							.processParentCoordinatorInfo(request);
				} else {
					log.debug(Simulator.getFormattedTime(Simulator
							.getCurrentTime())
							+ " "
							+ skyNetNode.getSkyNetNodeInfo().getTransInfo()
									.getNetId().toString()
							+ " Received ParentCoordinatorInfo of unknown "
							+ request.getSenderNodeInfo().getTransInfo()
									.getNetId().toString());
				}
			} else {
				supportPeer.getSPAttributeUpdateStrategy()
						.processParentCoordinatorInfo(request);
			}
		} else {
			if (skyNetNode.getAttributeUpdateStrategy()
					.getReceiverOfNextUpdate() != null) {
				if (request.getSenderNodeInfo().getSkyNetID().getID()
						.compareTo(
								skyNetNode.getAttributeUpdateStrategy()
										.getReceiverOfNextUpdate()
										.getSkyNetID().getID()) == 0) {
					// receiver is SubCoordinator
					log.debug(SkyNetUtilities.getTimeAndNetID(skyNetNode)
							+ " Received as SubCoordinator "
							+ request.getClass().getSimpleName());
					skyNetNode.getAttributeUpdateStrategy()
							.processParentCoordinatorInfo(request);
				} else {
					log.debug(SkyNetUtilities.getTimeAndNetID(skyNetNode)
							+ "Received ParentCoordinatorInfo of unknown "
							+ request.getSenderNodeInfo().getTransInfo()
									.getNetId().toString());
				}
			} else {
				skyNetNode.getAttributeUpdateStrategy()
						.processParentCoordinatorInfo(request);
			}
		}

		// create reply
		Message reply = new ParentCoordinatorInformationACKMsg(request
				.getReceiverNodeInfo(), request.getSenderNodeInfo(), request
				.getSkyNetMsgID(), request.isReceiverSP());
		skyNetNode.getTransLayer().sendReply(reply, receivingEvent,
				skyNetNode.getPort(), TransProtocol.UDP);
	}

	/**
	 * This private method is called if a message of the type
	 * {@link AttributeUpdateMsg} was received.
	 * 
	 * @param request
	 *            contains the received message.
	 * @param receivingEvent
	 *            contains the event, which encapsulates the message and other
	 *            information concerning that receiving-event
	 */
	private void processAttributeUpdate(AttributeUpdateMsg request,
			TransMsgEvent receivingEvent, long timestamp, boolean toSupportPeer) {
		// give message to Coordinator or SupportPeer
		if (toSupportPeer) {
			log.debug(supportPeer.getSkyNetNodeInfo().getTransInfo().getNetId()
					.toString()
					+ " Received as SupportPeer "
					+ request.getClass().getSimpleName());
			supportPeer.getSPAttributeInputStrategy().processUpdateMessage(
					request, timestamp);
		} else {
			log.debug("Received as ParentCoordinator "
					+ request.getClass().getSimpleName());
			skyNetNode.getAttributeInputStrategy().processUpdateMessage(
					request, timestamp);
		}

		// create reply
		Message reply;
		reply = new AttributeUpdateACKMsg(request.getReceiverNodeInfo(),
				request.getSenderNodeInfo(), request.getSkyNetMsgID(), request
						.isSenderSP(), toSupportPeer);
		skyNetNode.getTransLayer().sendReply(reply, receivingEvent,
				skyNetNode.getPort(), TransProtocol.UDP);

	}

	/**
	 * This private method is called if a message of the type
	 * {@link MetricUpdateMsg} was received.
	 * 
	 * @param request
	 *            contains the received message.
	 * @param receivingEvent
	 *            contains the event, which encapsulates the message and other
	 *            information concerning that receiving-event
	 */
	private void processMetricUpdate(MetricUpdateMsg request,
			TransMsgEvent receivingEvent, long timestamp) {
		log.debug("Received " + request.getClass().getSimpleName() + ": "
				+ request.toString());
		skyNetNode.getMetricInputStrategy().processUpdateMessage(request,
				timestamp);
		Message reply = null;
		MetricsInterpretation temp = skyNetNode.getMetricsInterpretation();

		// create reply depending on the definition for pushing the
		// systemStatistics and the DHTParamManipulator
		if (alwaysPushSystemStatistics) {
			reply = new MetricUpdateACKMsg(request.getReceiverNodeInfo(),
					request.getSenderNodeInfo(), temp
							.getActualSystemStatistics(), temp
							.getParaManipulator(), temp
							.getStatisticsTimestamp(), temp
							.getManipulatorTimestamp(), request
							.getSkyNetMsgID(), skyNetNode.getSkyNetNodeInfo()
							.getObservedLevelFromRoot());
		} else {
			LinkedHashMap<BigDecimal, MetricsSubCoordinatorInfo> list = skyNetNode
					.getMetricInputStrategy().getMetricStorage()
					.getListOfSubCoordinators();
			MetricsSubCoordinatorInfo subCo = list.get(request
					.getSenderNodeInfo().getSkyNetID().getID());
			if (subCo.isNeedsUpdate()) {
				// In this case the selected SubCoordinator has no actual
				// systemStatistics nor DHTParaManipulator. So the new
				// information
				// is piggybacked with the MetricUpdateACKMsg
				reply = new MetricUpdateACKMsg(request.getReceiverNodeInfo(),
						request.getSenderNodeInfo(), temp
								.getActualSystemStatistics(), temp
								.getParaManipulator(), temp
								.getStatisticsTimestamp(), temp
								.getManipulatorTimestamp(), request
								.getSkyNetMsgID(), skyNetNode
								.getSkyNetNodeInfo().getObservedLevelFromRoot());
				subCo.setNeedsUpdate(false);
				list.put(request.getSenderNodeInfo().getSkyNetID().getID(),
						subCo);
				skyNetNode.getMetricInputStrategy().getMetricStorage()
						.setListOfSubCoordinators(list);

			} else {
				// No need to send systemStatistics nor DHTParaManipulator
				reply = new MetricUpdateACKMsg(request.getReceiverNodeInfo(),
						request.getSenderNodeInfo(), null, null, -1, -1,
						request.getSkyNetMsgID(), skyNetNode
								.getSkyNetNodeInfo().getObservedLevelFromRoot());
			}
		}

		skyNetNode.getTransLayer().sendReply(reply, receivingEvent,
				skyNetNode.getPort(), TransProtocol.UDP);
	}

	private void manualJoin() {
		long currentTime = Simulator.getCurrentTime();
		if (!tryingJoin) {
			log.warn(SkyNetUtilities.getTimeAndNetID(skyNetNode)
					+ "is absent but online, "
					+ "so setting timer for possible manual join");
			tryingJoin = true;
			errorTimestamp = currentTime;
		} else if (currentTime - errorTimestamp > (80 * Simulator.SECOND_UNIT)) {
			CIClientNode napsterNode = ((CIClientNode) skyNetNode
					.getOverlayNode());
			napsterNode.setPeerStatus(PeerStatus.PRESENT);

			// Create ServerOverlayContact, since this was never
			// done before
			if (napsterNode.getServerOverlayContact() == null) {
				CIOverlayContact serverOverlayContact = new CIOverlayContact(
						napsterNode.getServer()
								.getOverlayID(), napsterNode
								.getServerTransInfo());
				napsterNode.setServerOverlayContact(serverOverlayContact);
			} else if (napsterNode.getServerOverlayContact().getOverlayID() == null) {
				CIOverlayContact serverOverlayContact = new CIOverlayContact(
						napsterNode.getServer()
								.getOverlayID(), napsterNode
								.getServerTransInfo());
				napsterNode.setServerOverlayContact(serverOverlayContact);
			}

			((SkyNetNode) skyNetNode).setPresentTime(currentTime);
			// Schedule next metric-update
			skyNetNode.getMetricUpdateStrategy().setSendingTime(currentTime);
			long delta = currentTime
					% skyNetNode.getMetricUpdateStrategy().getUpdateInterval();
			delta = skyNetNode.getMetricUpdateStrategy().getUpdateInterval()
					- delta;
			long metricsTime = currentTime + delta;
			Simulator.scheduleEvent(new SkyNetEventObject(
					SkyNetEventType.METRICS_UPDATE, currentTime), metricsTime,
					skyNetNode, null);

			// Schedule next attribute-update
			skyNetNode.getAttributeUpdateStrategy().setSendingTime(currentTime);
			delta = currentTime
					% skyNetNode.getAttributeUpdateStrategy()
							.getUpdateInterval();
			delta = skyNetNode.getAttributeUpdateStrategy().getUpdateInterval()
					- delta;
			long attributeTime = currentTime + delta;
			Simulator.scheduleEvent(new SkyNetEventObject(
					SkyNetEventType.ATTRIBUTE_UPDATE, currentTime),
					attributeTime, skyNetNode, null);

			// Schedule next query-remainder
			delta = currentTime
					% ((SkyNetNode) skyNetNode).getQueryRemainderTime();
			delta = ((SkyNetNode) skyNetNode).getQueryRemainderTime() - delta;
			long queryRemainderStartTime = currentTime + delta;
			Simulator.scheduleEvent(new SkyNetEventObject(
					SkyNetEventType.QUERY_REMAINDER, currentTime),
					queryRemainderStartTime, skyNetNode, null);

			// other inits
			((SkyNetHostProperties) skyNetNode.getHost().getProperties())
					.init();

			log.warn(SkyNetUtilities.getTimeAndNetID(skyNetNode)
					+ "tries to go PRESENT manually");
			tryingJoin = false;
		}
	}

}
