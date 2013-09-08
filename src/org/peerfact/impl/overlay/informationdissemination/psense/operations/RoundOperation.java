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

package org.peerfact.impl.overlay.informationdissemination.psense.operations;

import java.awt.Point;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.peerfact.api.common.Message;
import org.peerfact.api.common.OperationCallback;
import org.peerfact.impl.common.AbstractOperation;
import org.peerfact.impl.overlay.informationdissemination.psense.IncomingMessageBean;
import org.peerfact.impl.overlay.informationdissemination.psense.OutgoingMessageBean;
import org.peerfact.impl.overlay.informationdissemination.psense.PSense;
import org.peerfact.impl.overlay.informationdissemination.psense.PSenseContact;
import org.peerfact.impl.overlay.informationdissemination.psense.PSenseID;
import org.peerfact.impl.overlay.informationdissemination.psense.PSenseNode;
import org.peerfact.impl.overlay.informationdissemination.psense.PSenseNodeInfo;
import org.peerfact.impl.overlay.informationdissemination.psense.messages.AbstractPSenseMsg;
import org.peerfact.impl.overlay.informationdissemination.psense.messages.AbstractPositionUpdateMsg;
import org.peerfact.impl.overlay.informationdissemination.psense.messages.ActionsMsg;
import org.peerfact.impl.overlay.informationdissemination.psense.messages.ForwardMsg;
import org.peerfact.impl.overlay.informationdissemination.psense.messages.PositionUpdateMsg;
import org.peerfact.impl.overlay.informationdissemination.psense.messages.SensorRequestMsg;
import org.peerfact.impl.overlay.informationdissemination.psense.messages.SensorResponseMsg;
import org.peerfact.impl.overlay.informationdissemination.psense.util.Configuration;
import org.peerfact.impl.overlay.informationdissemination.psense.util.IncomingMessageList;
import org.peerfact.impl.overlay.informationdissemination.psense.util.SequenceNumber;
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
 * This class is the operation for a round. First, it will be update the sensor
 * and neighbor list. After this, delete it old nodes. Then it creates the
 * messages. The messages are position updates, sensor responses, sensor
 * requests and position forwards. This message are stored in a outgoing message
 * queue for the next processing in this class. The outgoing message queue must
 * be fit to the round limit. For that, many messages of the type position
 * forwards and position updates will be deleted up to the round limit is
 * achieved. Then will be send the messages to the nodes.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 10/15/2010
 */
public class RoundOperation extends AbstractOperation<PSenseNode, Boolean> {

	private PSenseNode node;

	private Boolean connectedWithOverlay;

	public RoundOperation(PSenseNode node, OperationCallback<Boolean> callback) {
		super(node, callback);
		this.node = node;
		connectedWithOverlay = true;
	}

	@Override
	protected void execute() {
		if (log.isDebugEnabled()) {
			log.debug("========RoundOperation for Node: " + node.getOverlayID()
					+ "========");
		}
		// TODO: insert more DEBUG information
		IncomingMessageList incomingMsg = node.getIncomingMessageList();
		PSense localPSense = node.getLocalPSense();

		if (aloneInOverlay()) {
			if (Simulator.getCurrentTime()
					- node.getLastNotAloneInOverlayTime() > Configuration.TIMEOUT_ALONE_IN_OVERLAY) {
				connectedWithOverlay = false;
				operationFinished(false);
			}
		} else {
			node.setLastNotAloneInOverlayTime(Simulator.getCurrentTime());
		}

		if (log.isDebugEnabled()) {
			log.debug("IncomingMessage: " + incomingMsg);
		}

		node.incSeqNr();

		// The information of the incomingMessages are processed in the
		// messageHandler for this two tasks
		updateNearNodeList(localPSense);
		updateSensorNodeList(incomingMsg, localPSense);

		// Delete nodes, that are expired!
		localPSense.removeDeadNodes();

		List<OutgoingMessageBean> outgoingMessages = new Vector<OutgoingMessageBean>(
				100);

		createPositionUpdateMessages(outgoingMessages, localPSense);
		createSensorRequests(outgoingMessages, localPSense);
		createSensorResponses(outgoingMessages, localPSense, incomingMsg);
		createForwardMessages(outgoingMessages, localPSense, incomingMsg);

		createActionMessages(outgoingMessages, localPSense,
				node.getPlayerActions());

		removeDuplicateMessages(outgoingMessages);
		int roundLimit = getRoundLimit();

		fitOutgoingMessageForRoundLimit(outgoingMessages, roundLimit);
		sendMessages(outgoingMessages);

		outgoingMessages.clear();
		node.clearIncomingMessageList();
		node.clearPlayerActions();

		localPSense.removeUnusedNodes();

		operationFinished(true);
	}

	private boolean aloneInOverlay() {
		if (node.getIncomingMessageList().size() == 0) {
			if (node.getLocalPSense().getNearNodes().size() == 0) {

				// check for an empty sensor node list
				boolean emptySensorNodesList = true;
				PSenseID[] sensorNodes = node.getLocalPSense().getSensorNodes();
				for (int i = 0; i < sensorNodes.length; i++) {
					if (sensorNodes[i] != null) {
						emptySensorNodesList = false;
					}
				}

				if (emptySensorNodesList) {
					return true;
				}
			}
		}
		return false;
	}

	private static void fitOutgoingMessageForRoundLimit(
			List<OutgoingMessageBean> outgoingMessages, int roundLimit) {
		int notToDeleteMsgSize = getNotToDeleteMsgSize(outgoingMessages);

		if (notToDeleteMsgSize > roundLimit) {
			deleteAllPosMsgs(outgoingMessages);

			if (log.isDebugEnabled()) {
				log.debug("Remove all position message, because Round Limit ("
						+ roundLimit + "Byte) is to small for "
						+ notToDeleteMsgSize + "Byte of messages");
			}

			deleteArbitraryMsgs(outgoingMessages, roundLimit);

			if (log.isDebugEnabled()) {
				log.debug("Remove arbitrary messages, to fit the Round Limit of"
						+ roundLimit + "Bytes");
			}
			if (log.isInfoEnabled()) {
				log.info("Only sensor and action message will be send!");
			}
		} else {
			deletePositionUpdatesToFitRoundLimit(outgoingMessages, roundLimit);
		}
	}

	private static void deletePositionUpdatesToFitRoundLimit(
			List<OutgoingMessageBean> outgoingMessages, int roundLimit) {

		// fetch all position updates from the outgoingMessage list.
		List<OutgoingMessageBean> posUpdateOutgoingMsgs = new Vector<OutgoingMessageBean>(
				100);
		for (OutgoingMessageBean outMsgBean : outgoingMessages) {
			Message msg = outMsgBean.getMessage();
			if (msg instanceof AbstractPositionUpdateMsg) {
				posUpdateOutgoingMsgs.add(outMsgBean);
			}
		}

		int outgoingSize = getAllMsgSize(outgoingMessages);
		while (outgoingSize > roundLimit) {
			int maxIndex = posUpdateOutgoingMsgs.size();
			int offsetIndex = 0;
			int deleteIndex = 0;
			if (maxIndex > 0) {
				deleteIndex = Simulator.getRandom().nextInt(maxIndex);
				offsetIndex = Simulator.getRandom().nextInt(maxIndex);
			}

			// Determine the number of msgs, that should be deleted.
			double averageMsgSize = (double) outgoingSize
					/ outgoingMessages.size();
			double toMuch = outgoingSize - roundLimit;
			// Delete only the half of the estimated numberToDelete.
			int numberToDelete = (int) Math
					.ceil(((toMuch / averageMsgSize) * 0.5));

			for (int i = 0; i < numberToDelete
					&& posUpdateOutgoingMsgs.size() != 0; i++) {
				OutgoingMessageBean outMsgBean = posUpdateOutgoingMsgs
						.get(deleteIndex);
				deleteOutMsgBean(outgoingMessages, outMsgBean);
				posUpdateOutgoingMsgs.remove(outMsgBean);

				maxIndex = posUpdateOutgoingMsgs.size();
				if (maxIndex > 0) {
					deleteIndex = (deleteIndex + offsetIndex) % maxIndex;
				} else {
					deleteIndex = 0;
				}
			}
			outgoingSize = getAllMsgSize(outgoingMessages);
		}
	}

	private static void deleteArbitraryMsgs(
			List<OutgoingMessageBean> outgoingMessages, int roundLimit) {
		while (getAllMsgSize(outgoingMessages) > roundLimit) {
			int maxIndex = outgoingMessages.size();
			int deleteIndex = Simulator.getRandom().nextInt(maxIndex);

			OutgoingMessageBean outMsgBean = outgoingMessages.get(deleteIndex);
			// delete the choices message from the outgoing messages.
			deleteOutMsgBean(outgoingMessages, outMsgBean);
		}
	}

	private static void deleteAllPosMsgs(
			List<OutgoingMessageBean> outgoingMessages) {
		List<OutgoingMessageBean> toRemove = new Vector<OutgoingMessageBean>();

		for (OutgoingMessageBean outMsgBean : outgoingMessages) {
			Message msg = outMsgBean.getMessage();
			if (msg instanceof PositionUpdateMsg || msg instanceof ForwardMsg) {
				toRemove.add(outMsgBean);
			}
		}

		for (OutgoingMessageBean outMsgBean : toRemove) {
			deleteOutMsgBean(outgoingMessages, outMsgBean);
		}
	}

	private static void deleteOutMsgBean(
			List<OutgoingMessageBean> outgoingMessages,
			OutgoingMessageBean outMsgBean) {

		outgoingMessages.remove(outMsgBean);

		List<PSenseID> receiversList = outMsgBean.getReceivers();
		if (log.isDebugEnabled()) {
			log.debug("Before deletion of a receiver: " + receiversList);
		}

		// delete the receiver from the receiversList in all other Messages,
		// where the same receiversList is used! This is a nice side effect of
		// the outgoingMessages.
		if (receiversList != null) {
			receiversList.remove(outMsgBean.getContact().getOverlayID());
		}

		if (log.isDebugEnabled()) {
			log.debug("After deletion of a receiver: " + receiversList);
		}
	}

	private static int getAllMsgSize(List<OutgoingMessageBean> outgoingMessages) {
		int sum = 0;
		for (OutgoingMessageBean outMsgBean : outgoingMessages) {
			Message msg = outMsgBean.getMessage();
			sum += msg.getSize() + Configuration.NETWORT_PROTOCOL_OVERHEAD
					+ Configuration.TRANSPORT_PROTOCOL_OVERHEAD;
		}
		return sum;

	}

	private static int getNotToDeleteMsgSize(
			List<OutgoingMessageBean> outgoingMessages) {
		int sum = 0;
		for (OutgoingMessageBean outMsgBean : outgoingMessages) {
			Message msg = outMsgBean.getMessage();
			if (msg instanceof SensorRequestMsg
					|| msg instanceof SensorResponseMsg
					|| msg instanceof ActionsMsg) {
				sum += msg.getSize() + Configuration.NETWORT_PROTOCOL_OVERHEAD
						+ Configuration.TRANSPORT_PROTOCOL_OVERHEAD;
			}
		}
		return sum;
	}

	private static int getRoundLimit() {
		int roundLimitByte = Configuration.ROUND_BYTE_LIMIT;

		if (log.isDebugEnabled()) {
			log.debug("RoundLimit for this round is: " + roundLimitByte
					+ "Byte.");
		}

		return roundLimitByte;
	}

	private static void removeDuplicateMessages(
			List<OutgoingMessageBean> outgoingMessages) {
		for (int i = 0; i < outgoingMessages.size(); i++) {
			for (int j = i + 1; j < outgoingMessages.size(); j++) {

				if (outgoingMessages.get(i).equals(outgoingMessages.get(j))) {
					// remove
					OutgoingMessageBean rmMsgBean = outgoingMessages.remove(j);

					if (log.isDebugEnabled()) {
						log.debug("This Msg:          "
								+ outgoingMessages.get(i)
								+ "\nis a duplicate of: " + rmMsgBean);
					}
					// counter will be decrement, but the actually [j] is
					// removed
					j--;
				}
			}
		}
	}

	private void sendMessages(List<OutgoingMessageBean> outgoingMessages) {
		for (OutgoingMessageBean outMsgBean : outgoingMessages) {
			PSenseContact receiverContact = outMsgBean.getContact();
			node.getTransLayer().send(outMsgBean.getMessage(),
					receiverContact.getTransInfo(), node.getPort(),
					Configuration.TRANSPORT_PROTOCOL);
		}
	}

	private void createActionMessages(
			List<OutgoingMessageBean> outgoingMessages, PSense localPSense,
			List<Integer> playerActions) {
		if (playerActions.size() > 0) {
			List<PSenseID> ignoreNodes = new Vector<PSenseID>();
			ignoreNodes.add(localPSense.getLocalNode());

			List<PSenseID> interestedNodes = localPSense.getAllNodesInArea(
					node.getPosition(), Configuration.ACTION_RANGE_RADIUS,
					ignoreNodes);

			for (PSenseID id : interestedNodes) {
				int SumActionSize = 0;
				for (Integer actionSize : playerActions) {
					SumActionSize += actionSize;
				}
				PSenseNodeInfo nodeInfo = localPSense.getNodeInfo(id);
				PSenseContact contact = nodeInfo.getContact();
				ActionsMsg msg = new ActionsMsg(Configuration.MAXIMAL_HOP,
						node.getSeqNr(), Configuration.ACTION_RANGE_RADIUS,
						node.getPosition(), SumActionSize);

				OutgoingMessageBean msgBean = new OutgoingMessageBean(contact,
						null, msg);
				outgoingMessages.add(msgBean);
			}
		}
	}

	private static void createForwardMessages(
			List<OutgoingMessageBean> outgoingMessages, PSense localPSense,
			IncomingMessageList incomingMsg) {
		LinkedList<IncomingMessageBean> positionUpdateMsgs = incomingMsg
				.getPositionUpdateMsgs();
		for (IncomingMessageBean inMsgBean : positionUpdateMsgs) {
			AbstractPositionUpdateMsg updateMsg = (AbstractPositionUpdateMsg) inMsgBean
					.getMessage();
			if (updateMsg.getHopCount() <= 0)
			{
				continue; // abort this msg, and check the next
			}

			List<PSenseID> toForwardNodes;
			// TODO: Receiverslist of nodeInfo could be used, to send less
			// Forward msgs. (check if seqNr is equals)

			// if it is a pos update from a sensor node and it exits no near
			// node, then should forwarded to the closest node.
			boolean nodeInArea = localPSense.existsNodeInArea(updateMsg
					.getPosition(), updateMsg.getRadius(), inMsgBean
					.getContact().getOverlayID());
			if (!nodeInArea
					&& localPSense.isSensorNode(inMsgBean.getContact()
							.getOverlayID())) {
				toForwardNodes = new Vector<PSenseID>();
				PSenseID closestNode = localPSense.getClosestNode(
						updateMsg.getPosition(), updateMsg.getReceiversList());
				if (closestNode != null) {
					toForwardNodes.add(closestNode);
				}

			} else {
				toForwardNodes = localPSense.getAllNodesInArea(
						updateMsg.getPosition(), updateMsg.getRadius(),
						updateMsg.getReceiversList());
			}

			// getReceiversList() get a copy of the list back!
			List<PSenseID> receivers = updateMsg.getReceiversList();
			// add the new receivers
			receivers.addAll(toForwardNodes);

			// create the msg
			byte hops = (byte) (updateMsg.getHopCount() - (byte) 1);
			SequenceNumber seqNr = updateMsg.getSequenceNr();
			int visionRangeRadius = updateMsg.getRadius();
			Point position = updateMsg.getPosition();
			PSenseContact contact = inMsgBean.getContact();

			ForwardMsg msg = new ForwardMsg(hops, seqNr, receivers,
					visionRangeRadius, position, contact);

			// add the msgs to the outgoingMessages list
			for (PSenseID forwardID : toForwardNodes) {
				// get the contact information of the receiver
				PSenseContact receiverContact = localPSense.getNodeInfo(
						forwardID).getContact();
				OutgoingMessageBean outMsgBean = new OutgoingMessageBean(
						receiverContact, receivers, msg);
				outgoingMessages.add(outMsgBean);
			}

		}

	}

	private void createSensorResponses(
			List<OutgoingMessageBean> outgoingMessages, PSense localPSense,
			IncomingMessageList incomingMsg) {
		LinkedList<IncomingMessageBean> incomingSensorRequests = incomingMsg
				.getSensorRequestsMsgs();
		for (IncomingMessageBean inMsgBean : incomingSensorRequests) {
			AbstractPSenseMsg inMsg = inMsgBean.getMessage();

			SensorRequestMsg reqMsg = (SensorRequestMsg) inMsg;

			List<PSenseID> ignoreNodes = new Vector<PSenseID>();
			ignoreNodes.add(reqMsg.getSenderID());

			PSenseNodeInfo sensorNodeInfo = null;
			PSenseID sensorNode = null;
			byte tempHop = 0;

			// search a node, with enough hopCount
			do {
				sensorNode = localPSense.findSensorNode(reqMsg.getPosition(),
						reqMsg.getRadius(), reqMsg.getSectorID(), ignoreNodes);
				if (sensorNode == null) {
					sensorNode = localPSense.getLocalNode();
					if (log.isInfoEnabled()) {
						log.info("FindSensorNode give null back! Replaced with localNode for SensorRequest!");
					}
				}

				sensorNodeInfo = localPSense.getNodeInfo(sensorNode);

				if (sensorNodeInfo != null) {
					tempHop = sensorNodeInfo.getHops();
				} else {
					break;
				}
				ignoreNodes.add(sensorNode);
			} while (tempHop <= 0);

			if (sensorNodeInfo != null) {

				// set sequence number.
				SequenceNumber seqNr;
				if (sensorNode == localPSense.getLocalNode()) {
					// in sensorNodeInfo of the localNode is an old sequence
					// Number, because that, take new node sequence number
					seqNr = node.getSeqNr();
				} else {
					seqNr = sensorNodeInfo.getSequenceNr();
					tempHop--;
				}

				SensorResponseMsg msg = new SensorResponseMsg(tempHop, seqNr,
						sensorNodeInfo.getVisionRangeRadius(),
						sensorNodeInfo.getPosition(), reqMsg.getSectorID(),
						sensorNodeInfo.getContact(), reqMsg.getSequenceNr());

				PSenseContact contact = inMsgBean.getContact();
				OutgoingMessageBean outMsgBean = new OutgoingMessageBean(
						contact, null, msg);
				outgoingMessages.add(outMsgBean);
			}
		}
	}

	private void createSensorRequests(
			List<OutgoingMessageBean> outgoingMessages, PSense localPSense) {
		PSenseID[] sensorNodes = localPSense.getSensorNodes();

		for (int i = 0; i < sensorNodes.length; i++) {
			PSenseID id = sensorNodes[i];
			if (id != null) {
				PSenseNodeInfo nodeInfo = localPSense.getNodeInfo(id);
				if (nodeInfo != null) {
					PSenseContact contact = nodeInfo.getContact();
					SensorRequestMsg msg = new SensorRequestMsg(
							node.getOverlayID(), Configuration.MAXIMAL_HOP,
							node.getSeqNr(), Configuration.VISION_RANGE_RADIUS,
							node.getPosition(), (byte) i);

					OutgoingMessageBean outMsgBean = new OutgoingMessageBean(
							contact, null, msg);
					outgoingMessages.add(outMsgBean);
				} else {
					log.error("Inconsistent's in PSense! SensorNodes contains the id "
							+ id + " that are not stored in nodeStorage!");
				}
			}
		}

	}

	private void createPositionUpdateMessages(
			List<OutgoingMessageBean> outgoingMessages, PSense localPSense) {
		// create list with nearNodes and SensorNodes
		List<PSenseID> nodes = new Vector<PSenseID>();
		nodes.addAll(localPSense.getNearNodes());
		for (PSenseID id : localPSense.getSensorNodes()) {
			if (id != null && !nodes.contains(id)) {
				nodes.add(id);
			}
		}

		// create receivers list, so that all msgs have the same list
		List<PSenseID> receivers = new Vector<PSenseID>();
		// add self to the receiverList
		receivers.add(node.getOverlayID());

		for (PSenseID id : nodes) {
			PSenseNodeInfo nodeInfo = localPSense.getNodeInfo(id);
			if (nodeInfo != null) {
				receivers.add(id);
				PSenseContact contact = nodeInfo.getContact();
				PositionUpdateMsg msg = new PositionUpdateMsg(
						node.getOverlayID(), Configuration.MAXIMAL_HOP,
						node.getSeqNr(), receivers,
						Configuration.VISION_RANGE_RADIUS, node.getPosition());
				OutgoingMessageBean outMsgBean = new OutgoingMessageBean(
						contact, receivers, msg);
				outgoingMessages.add(outMsgBean);
			} else {
				log.error("Inconsistent's in PSense! NearNodes or SensorNodes contains the id "
						+ id + " that are not stored in nodeStorage!");
			}
		}
	}

	private static void updateNearNodeList(PSense localPSense) {
		localPSense.updateNearNodeList();
	}

	private static void updateSensorNodeList(IncomingMessageList incomingMsg,
			PSense localPSense) {
		LinkedList<IncomingMessageBean> sensorResponesMsgs = incomingMsg
				.getSensorResponseMsgs();
		PSenseID[] newSensorNodes = computeNewSensorNodesFromResponses(sensorResponesMsgs);
		localPSense.updateSensorNodeList(newSensorNodes);
	}

	private static PSenseID[] computeNewSensorNodesFromResponses(
			LinkedList<IncomingMessageBean> sensorResponesMsgs) {

		// filter the newest sensor node msg for every sector
		IncomingMessageBean[] temp = new IncomingMessageBean[Configuration.NUMBER_SECTORS];
		for (IncomingMessageBean msgBean : sensorResponesMsgs) {
			SensorResponseMsg msg = (SensorResponseMsg) msgBean.getMessage();
			int sector = msg.getSectorID();
			// if empty then add
			if (temp[sector] == null) {
				temp[sector] = msgBean;
			} else {
				// otherwise, look, which msg is newer
				SequenceNumber tempSeqNrReq = ((SensorResponseMsg) temp[sector]
						.getMessage()).getSequenceNrRequest();
				SequenceNumber msgSeqNrReq = msg.getSequenceNrRequest();

				if (msgSeqNrReq.isNewerAs(tempSeqNrReq)) {
					temp[sector] = msgBean;
				}
			}
		}

		// store the PSenseID to every sector
		PSenseID[] newSensorNodes = new PSenseID[temp.length];
		for (int i = 0; i < temp.length; i++) {
			if (temp[i] != null) {
				newSensorNodes[i] = temp[i].getContact().getOverlayID();
			}
		}
		return newSensorNodes;
	}

	@Override
	public Boolean getResult() {
		return connectedWithOverlay;
	}

	public void stopOperation() {
		if (log.isDebugEnabled()) {
			log.debug("The Round Operation is stopped for node: "
					+ node.getOverlayID());
		}
		operationFinished(false);
	}
}
