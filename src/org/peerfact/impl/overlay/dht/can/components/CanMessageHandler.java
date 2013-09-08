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

package org.peerfact.impl.overlay.dht.can.components;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.peerfact.api.common.Message;
import org.peerfact.api.transport.TransMessageListener;
import org.peerfact.api.transport.TransProtocol;
import org.peerfact.impl.overlay.AbstractOverlayNode.PeerStatus;
import org.peerfact.impl.overlay.dht.can.messages.JoinMsg;
import org.peerfact.impl.overlay.dht.can.messages.JoinOverloadMsg;
import org.peerfact.impl.overlay.dht.can.messages.JoinReplyMsg;
import org.peerfact.impl.overlay.dht.can.messages.LeaveLeftMsg;
import org.peerfact.impl.overlay.dht.can.messages.LeaveMsg;
import org.peerfact.impl.overlay.dht.can.messages.LeaveReorganizeMsg;
import org.peerfact.impl.overlay.dht.can.messages.LeaveReorganizeReplyMsg;
import org.peerfact.impl.overlay.dht.can.messages.LookupMsg;
import org.peerfact.impl.overlay.dht.can.messages.LookupReplyMsg;
import org.peerfact.impl.overlay.dht.can.messages.NewNeighbourMsg;
import org.peerfact.impl.overlay.dht.can.messages.NewVIDNeighbourMsg;
import org.peerfact.impl.overlay.dht.can.messages.PingMsg;
import org.peerfact.impl.overlay.dht.can.messages.PongMsg;
import org.peerfact.impl.overlay.dht.can.messages.StartTakeoverMsg;
import org.peerfact.impl.overlay.dht.can.messages.StoreMsg;
import org.peerfact.impl.overlay.dht.can.messages.StoreReplyMsg;
import org.peerfact.impl.overlay.dht.can.messages.TakeoverMsg;
import org.peerfact.impl.overlay.dht.can.messages.TakeoverReorganizeMsg;
import org.peerfact.impl.overlay.dht.can.messages.TakeoverReorganizeReplyMsg;
import org.peerfact.impl.overlay.dht.can.operations.JoinHandler;
import org.peerfact.impl.overlay.dht.can.operations.LookupOperation;
import org.peerfact.impl.overlay.dht.can.operations.StoreOperation;
import org.peerfact.impl.overlay.dht.can.operations.TakeoverRebuildOperation;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.transport.TransMsgEvent;
import org.peerfact.impl.util.logging.SimLogger;

/**
 * The CanMessageHandler receives all messages of a peer. It decides what to do
 * after and how to handle the message. Implements TransMessageListener
 * 
 * @author Bjoern Dollak <peerfact@kom.tu-darmstadt.de>
 * @author Thim Strothmann <info@peerfact.org>
 * @version February 2010
 * 
 */
public class CanMessageHandler implements TransMessageListener {
	private static Logger log = SimLogger.getLogger(CanNode.class);

	private CanNode node;

	/**
	 * Starts the CanMessageHandler in a certain peer.
	 * 
	 * @param node
	 *            peer which wants to start its messageHandler
	 */
	public CanMessageHandler(CanNode node) {
		this.node = node;
		log.debug(Simulator.getSimulatedRealtime()
				+ " New Message Handler, NodeID "
				+ node.getCanOverlayID().toString());
	}

	/**
	 * what to do if a message arrives.
	 */
	@Override
	public void messageArrived(TransMsgEvent receivingEvent) {
		Message msg = receivingEvent.getPayload();

		/**
		 * If the peer isn't present it just throws a warning.
		 */
		if (!node.getPeerStatus().equals(PeerStatus.PRESENT)) {
			log.debug(Simulator.getSimulatedRealtime()
					+ "Message arrived, but peer isn't present! PeerID "
					+ node.getLocalOverlayContact().getOverlayID().toString()
					+ " " + msg.getClass().getName());
			return;
		}

		/**
		 * A PingMsg arrives, so the messageHandler sends a pong back
		 */
		if (msg instanceof PingMsg) { // handle ping
			PingMsg ping = (PingMsg) msg;

			PongMsg pongMsg = new PongMsg(ping, node.getLocalOverlayContact()
					.clone(),
					node.getNeighbours(), node.getVIDNeighbours());
			pongMsg.setOperationID(ping.getOperationId());
			node.getTransLayer().send(pongMsg, ping.getSenderNode(),
					node.getPort(), TransProtocol.UDP);

		}

		/**
		 * A Pong arrives: The payload from the pong messages is saved. That
		 * means the neighbours and the VID neighbours of the sender.
		 */
		else if (msg instanceof PongMsg) {
			PongMsg pong = (PongMsg) msg;

			node.getTakeoverOperation().found(pong);
			node.updateNeighboursOfNeighbours(pong.getContact().clone(), pong
					.getNeighbours());
			node.updateVidNeighboursOfNeighbours(pong.getContact().clone(),
					pong.getVidNeighbours());

		}

		/**
		 * A JoinMsg arrives: A peer wants to join the CAN and the picked peer
		 * is the actual peer. So it starts the JoinHandler Operation.
		 * 
		 * If the CanConfig.distribution value is two, the actual node checks if
		 * one of its neighbours has a bigger a bigger area and sends the join
		 * message to this node.
		 */
		else if (msg instanceof JoinMsg) { // joining network
			JoinMsg join = (JoinMsg) msg;
			log.debug("New JoinMsg"
					+ node.getLocalOverlayContact().getOverlayID().toString());

			if (CanConfig.distribution == 2 && node.getNeighbours() != null) {
				CanOverlayContact biggestArea = node.getLocalOverlayContact();
				for (CanOverlayContact neighbour : node.getNeighbours()) {
					CanArea neighbourArea = neighbour.getArea();
					if (((neighbourArea.getArea()[1] - neighbourArea.getArea()[0]) * (neighbourArea
							.getArea()[3] - neighbourArea.getArea()[2])) > ((biggestArea
							.getArea().getArea()[1] - biggestArea.getArea()
							.getArea()[0]) * (biggestArea.getArea().getArea()[3] - biggestArea
							.getArea().getArea()[2]))) {
						biggestArea = neighbour;
					}
					log
							.debug("biggestAreaTemp: "
									+ biggestArea.getOverlayID().toString()
									+ " "
									+ ((neighbourArea.getArea()[1] - neighbourArea
											.getArea()[0]) * (neighbourArea
											.getArea()[3] - neighbourArea
											.getArea()[2]))
									+ " "
									+ ((biggestArea.getArea().getArea()[1] - biggestArea
											.getArea().getArea()[0]) * (biggestArea
											.getArea().getArea()[3] - biggestArea
											.getArea().getArea()[2])));
				}
				JoinMsg sendFurther;
				log.debug("biggestArea: "
						+ biggestArea.getOverlayID().toString());
				if (!biggestArea
						.getOverlayID()
						.toString()
						.equals(
								node.getLocalOverlayContact().getOverlayID()
										.toString())) {
					sendFurther = new JoinMsg(join.getSender(), biggestArea
							.getOverlayID(), join.getJoiningNode());
					node.getTransLayer().send(sendFurther,
							biggestArea.getTransInfo(), node.getPort(),
							TransProtocol.TCP);
				} else {
					new JoinHandler(node, join, receivingEvent);
				}
			} else {
				new JoinHandler(node, join, receivingEvent);
			}

		}

		/**
		 * a JoinReplyMsg arrives: Thats prepared for the overload extension,
		 * but not in use and not well implemented right now.
		 */
		else if (msg instanceof JoinReplyMsg) {
			JoinReplyMsg joinReply = (JoinReplyMsg) msg;
			if (((JoinReplyMsg) msg).getReceiver() == node.getCanOverlayID()) {
				node.setMainNode(joinReply.getMainNode());
			}
		}

		/**
		 * A JoinOverloadMsg arrives: A OverloadJoin Operation is done and the
		 * peer receives the results. Here it saves the new data. (area, VID,
		 * neighbours and VID neighbours)
		 */
		else if (msg instanceof JoinOverloadMsg) {
			JoinOverloadMsg joinOverload = (JoinOverloadMsg) msg;

			log.debug("JoinOverloadMsg "
					+ (joinOverload.getReceiver()).toString()
					+ " VID "
					+ joinOverload.getArea().getVid().toString()
					+ " "
					+ joinOverload.getVidNeighbours()[0].getArea().getVid()
							.toString()
					+ " "
					+ joinOverload.getVidNeighbours()[1].getArea().getVid()
							.toString());
			if (((JoinOverloadMsg) msg).getReceiver().toString().equals(
					node.getCanOverlayID().toString())) {

				node.getLocalOverlayContact().getArea().setArea(
						joinOverload.getArea().getArea());
				node.getLocalOverlayContact().getArea().setVid(
						joinOverload.getArea().getVid());
				List<CanOverlayContact> toSave = new LinkedList<CanOverlayContact>();

				for (int i = 0; i < joinOverload.getNeighbours().size(); i++) {
					if (node.getLocalOverlayContact().getArea().commonCorner(
							joinOverload.getNeighbours()
									.get(i).getArea())) {
						toSave.add(joinOverload
								.getNeighbours().get(i));
					}
				}

				node.setNeighbours(toSave);
				node.setMainNode(null);
				node.getLocalOverlayContact().getArea().setArea(
						joinOverload.getArea().getArea());
				node.getLocalOverlayContact().getArea().setVid(
						joinOverload.getArea().getVid());
				CanOverlayContact[] toSaveVIDNeighbours = {
						joinOverload.getVidNeighbours()[0],
						joinOverload.getVidNeighbours()[1] };
				log.debug("JoinOverloadMsg "
						+ (joinOverload.getReceiver()).toString() + " VID "
						+ joinOverload.getArea().getVid().toString() + " "
						+ toSaveVIDNeighbours[0].getArea().getVid().toString()
						+ " "
						+ toSaveVIDNeighbours[1].getArea().getVid().toString());
				node.setVIDNeigbours(toSaveVIDNeighbours);
				log.debug("Number of Neighbours: "
						+ node.getNeighbours().size());
				log.debug(Simulator.getSimulatedRealtime()
						+ "area: "
						+ node.getLocalOverlayContact().getArea().toString()
						+ " VID: "
						+ node.getLocalOverlayContact().getArea().getVid()
								.toString()
						+ " id: "
						+ node.getLocalOverlayContact().getOverlayID()
								.toString()
						+ " vid neighbours "
						+ node.getVIDNeighbours()[0].getArea().getVid()
								.toString()
						+ " "
						+ node.getVIDNeighbours()[0].getArea().getVid()
								.toString()
						+ " received VID Neighbours "
						+ joinOverload.getVidNeighbours()[0].getArea().getVid()
								.toString()
						+ " "
						+ joinOverload.getVidNeighbours()[1].getArea().getVid()
								.toString());

				node.setPeerStatus(PeerStatus.PRESENT);
				node.getBootstrap().registerNode(node);
			}
		}

		/**
		 * A LeaveMsg arrives: Another peer wants to leave. If just two peers
		 * are in the CAN it sends a LeaveReorganizeMsg directly back, else it
		 * send the LeaveReorganizeMsg to the next VID neighbour. Therefore the
		 * peer has to decide if the message arrived from the first or the
		 * second VID neighbour and if the other VID neighbour has the same
		 * parents. At the end the peer sends a LeaveReorganizeReplyMsg back to
		 * the leaving peer.
		 */
		else if (msg instanceof LeaveMsg) { // handle node departure
			LeaveMsg leave = (LeaveMsg) msg;
			log.debug(Simulator.getSimulatedRealtime()
					+ " new leaveMsg "
					+ " own ID "
					+ node.getLocalOverlayContact().getOverlayID().toString()
					+ " VID "
					+ node.getLocalOverlayContact().getArea().getVid()
							.toString()
					+ " from node: " + leave.getSender());
			if (((LeaveMsg) msg).getReceiver().toString().equals(
					node.getCanOverlayID().toString())) {
				log.debug(node.getVIDNeighbours()[0].getArea().getVid()
						.toString()
						+ " "
						+ node.getLocalOverlayContact().getArea().getVid()
								.toString()
						+ " "
						+ node.getVIDNeighbours()[1].getArea().getVid()
								.toString()
						+ " "
						+ leave.getLeavingNode().getArea().getVid().toString()
						+ " "
						+ node.getVIDNeighbours()[0].getArea().getVid()
								.toString().equals(
										leave.getLeavingNode().getArea()
												.getVid().toString())
						+ " "
						+ node.getVIDNeighbours()[0].getOverlayID().toString()
								.equals(
										leave.getLeavingNode().getOverlayID()
												.toString())
						+ " "
						+ node.getLocalOverlayContact().getArea().getVid()
								.numberCommon(leave.getArea().getVid())
						+ " "
						+ node.getVIDNeighbours()[0].getArea().getVid()
								.numberCommon(leave.getArea().getVid())
						+ " "
						+ node.getVIDNeighbours()[1].getArea().getVid()
								.numberCommon(leave.getArea().getVid())
						+ " "
						+ node.getVIDNeighbours()[0].getOverlayID().toString()
						+ " "
						+ leave.getLeavingNode().getOverlayID().toString());
				if (node.getVIDNeighbours()[0].getOverlayID().toString()
						.equals(
								node.getVIDNeighbours()[1].getOverlayID()
										.toString())) {
					LeaveReorganizeMsg nextLeaveReorganize = new LeaveReorganizeMsg(
							leave.getSender(), node.getVIDNeighbours()[1]
									.getOverlayID(), node
									.getLocalOverlayContact()
									.clone(), leave.getArea().getVid());
					node.getTransLayer().send(nextLeaveReorganize,
							node.getVIDNeighbours()[1].getTransInfo(),
							node.getPort(), TransProtocol.TCP);
				} else if (node.getVIDNeighbours()[0].getOverlayID().toString()
						.equals(
								leave.getLeavingNode().getOverlayID()
										.toString())
						&& node.getVIDNeighbours()[1].getArea().getVid()
								.numberCommon(leave.getArea().getVid()) == node
								.getLocalOverlayContact().getArea().getVid()
								.numberCommon(leave.getArea().getVid())) {
					LeaveReorganizeMsg nextLeaveReorganize = new LeaveReorganizeMsg(
							node.getLocalOverlayContact().getOverlayID(), node
									.getVIDNeighbours()[1].getOverlayID(),
							leave.getLeavingNode(), leave.getArea().getVid());
					node.getTransLayer().send(nextLeaveReorganize,
							node.getVIDNeighbours()[1].getTransInfo(),
							node.getPort(), TransProtocol.TCP);
				} else if (node.getVIDNeighbours()[1].getOverlayID().toString()
						.equals(
								leave.getLeavingNode().getOverlayID()
										.toString())
						&& node.getVIDNeighbours()[0].getArea().getVid()
								.numberCommon(leave.getArea().getVid()) == node
								.getLocalOverlayContact().getArea().getVid()
								.numberCommon(leave.getArea().getVid())) {
					LeaveReorganizeMsg nextLeaveReorganize = new LeaveReorganizeMsg(
							node.getLocalOverlayContact().getOverlayID(), node
									.getVIDNeighbours()[0].getOverlayID(),
							leave.getLeavingNode(), leave.getArea().getVid());
					node.getTransLayer().send(nextLeaveReorganize,
							node.getVIDNeighbours()[0].getTransInfo(),
							node.getPort(), TransProtocol.TCP);
				}

				LeaveReorganizeReplyMsg leaveReorganizeReply = new LeaveReorganizeReplyMsg(
						node.getLocalOverlayContact().getOverlayID(), leave
								.getSender(), node.getLocalOverlayContact()
								.clone(),
						node.getLocalOverlayContact().getArea(),
						node.getNeighbours(),
						node.getVIDNeighbours(), node.getStoredHashs());
				node.getTransLayer().send(leaveReorganizeReply,
						leave.getLeavingNode().getTransInfo(), node.getPort(),
						TransProtocol.TCP);

			}

		}

		/**
		 * A LeaveReorganizeMsg arrives: If this message arrives the peer sends
		 * a LeaveReorganizeReplyMsg with all its data back to the leaving peer.
		 * This message arrives from one VID neighbour, if the other has the
		 * same parents the LeaveReorganizeMsg is forwarded to it.
		 */
		else if (msg instanceof LeaveReorganizeMsg) {
			LeaveReorganizeMsg leaveReorganize = (LeaveReorganizeMsg) msg;
			log.debug(Simulator.getSimulatedRealtime()
					+ " New LeaveReorganizeMsg arrived in ID "
					+ node.getLocalOverlayContact().getOverlayID().toString()
					+ " from node: " + leaveReorganize.getSender().toString());

			// forward
			LeaveReorganizeReplyMsg leaveReorganizeReply = new LeaveReorganizeReplyMsg(
					node.getLocalOverlayContact().getOverlayID(),
					leaveReorganize
							.getSender(),
					node.getLocalOverlayContact().clone(), node
							.getLocalOverlayContact().getArea(),
					node.getNeighbours(),
					node.getVIDNeighbours(), node.getStoredHashs());
			node.getTransLayer().send(leaveReorganizeReply,
					leaveReorganize.getMaster().getTransInfo(), node.getPort(),
					TransProtocol.TCP);

			if (node.getVIDNeighbours()[0].getOverlayID().toString().equals(
					node.getVIDNeighbours()[1].getOverlayID().toString())) {
				log.debug("leaveReorganize VID0=VID1");
			}// do nothing
			else if ((node.getVIDNeighbours()[0].getOverlayID().toString()
					.equals(leaveReorganize.getSender().toString()) && node
					.getVIDNeighbours()[1].getArea().getVid().numberCommon(
					leaveReorganize.getVid()) == node.getLocalOverlayContact()
					.getArea().getVid().numberCommon(leaveReorganize.getVid()))) {
				LeaveReorganizeMsg nextLeaveReorganize = new LeaveReorganizeMsg(
						node.getLocalOverlayContact().getOverlayID(), node
								.getVIDNeighbours()[1].getOverlayID(),
						leaveReorganize.getMaster(), leaveReorganize.getVid());
				node.getTransLayer().send(nextLeaveReorganize,
						node.getVIDNeighbours()[1].getTransInfo(),
						node.getPort(), TransProtocol.TCP);
			} else if ((node.getVIDNeighbours()[1].getOverlayID().toString()
					.equals(leaveReorganize.getSender().toString()) && node
					.getVIDNeighbours()[0].getArea().getVid().numberCommon(
					leaveReorganize.getVid()) == node.getLocalOverlayContact()
					.getArea().getVid().numberCommon(leaveReorganize.getVid()))) {
				LeaveReorganizeMsg nextLeaveReorganize = new LeaveReorganizeMsg(
						node.getLocalOverlayContact().getOverlayID(), node
								.getVIDNeighbours()[0].getOverlayID(),
						leaveReorganize.getMaster(), leaveReorganize.getVid());
				node.getTransLayer().send(nextLeaveReorganize,
						node.getVIDNeighbours()[0].getTransInfo(),
						node.getPort(), TransProtocol.TCP);
			}
		}

		/**
		 * A Leave ReorganizeReplyMsg arrives: All the data from the sender peer
		 * are saved.
		 */
		else if (msg instanceof LeaveReorganizeReplyMsg) {
			LeaveReorganizeReplyMsg leaveReorganizeReply = (LeaveReorganizeReplyMsg) msg;
			log.debug(Simulator.getSimulatedRealtime()
					+ " New LeaveReorganizeReplyMsg from ID "
					+ leaveReorganizeReply.getSender().toString()
					+ " area "
					+ leaveReorganizeReply.getArea().toString()
					+ " received VID "
					+ leaveReorganizeReply.getMaster().getArea().getVid()
							.toString() + " receiveced Neighbours "
					+ leaveReorganizeReply.getNeighbours().size());
			for (int x = 0; x < leaveReorganizeReply.getNeighbours().size(); x++) {
				log.debug(leaveReorganizeReply.getNeighbours().get(x));
			}

			node.addLeavingArea(leaveReorganizeReply.getArea());
			node.addLeavingNeighbours(leaveReorganizeReply.getNeighbours());
			node.addLeavingReplyContacts(leaveReorganizeReply.getMaster());
			node.addLeavingReplyVIDNeighbours(leaveReorganizeReply
					.getVidNeighbours());
			node.addLeavingHash(leaveReorganizeReply.getStoredHashs());

		}

		/**
		 * A LeaveLeftMsg arrives: The Leave or TakeoverOperation is done and
		 * the actual peer gets its new data. So it stores the new area, VID,
		 * neighbours, VID neighbours and hashs.
		 */
		else if (msg instanceof LeaveLeftMsg) {
			LeaveLeftMsg leaveLeft = (LeaveLeftMsg) msg;
			if (!((LeaveLeftMsg) msg).getReceiver().toString().equals(
					node.getLocalOverlayContact().getOverlayID().toString())) {
				// forward
			} else {
				node.setArea(leaveLeft.getArea());
				if (node.getLocalOverlayContact().getArea().getVid().toString()
						.equals("0")) {
					node.setNeighbours(new LinkedList<CanOverlayContact>());
					node.emptyVIDNeigbours();
					log.debug("new LeaveLeftMsg: "
							+ node.getLocalOverlayContact().getOverlayID()
									.toString()
							+ " new VID "
							+ node.getLocalOverlayContact().getArea().getVid()
									.toString());
				} else {
					List<CanOverlayContact> neighboursToSave = new LinkedList<CanOverlayContact>();

					for (int i = 0; i < leaveLeft.getNeighbours().size(); i++) {
						if (leaveLeft.getNeighbours().get(
								i).getArea().commonCorner(
								node.getLocalOverlayContact().getArea())
								&& !neighboursToSave.contains(leaveLeft
										.getNeighbours().get(i))) {
							neighboursToSave.add(leaveLeft
									.getNeighbours().get(i));
						}
					}
					node.setNeighbours(neighboursToSave);
					node.setVIDNeigbours(leaveLeft.getVidNeighbours());
					log.debug("new LeaveLeftMsg: "
							+ node.getLocalOverlayContact().getOverlayID()
									.toString()
							+ " new VID "
							+ node.getLocalOverlayContact().getArea().getVid()
									.toString()
							+ " vid neighbours "
							+ node.getVIDNeighbours()[0].getArea().getVid()
									.toString()
							+ " "
							+ node.getVIDNeighbours()[1].getArea().getVid()
									.toString()
							+ " "
							+ node.getLocalOverlayContact().getArea()
									.toString()
							+ " vid neighbours "
							+ node.getVIDNeighbours()[0].getOverlayID()
									.toString()
							+ " "
							+ node.getVIDNeighbours()[1].getOverlayID()
									.toString());
					for (int x = 0; x < node.getNeighbours().size(); x++) {
						log.debug("new Neighbours: "
								+ node.getNeighbours().get(x).getOverlayID()
										.toString());
					}

					CanOverlayContact[] sendVID1 = { null,
							node.getLocalOverlayContact().clone() };
					CanOverlayContact[] sendVID2 = {
							node.getLocalOverlayContact().clone(), null };
					NewVIDNeighbourMsg newVIDNeighboursMsg1 = new NewVIDNeighbourMsg(
							node.getLocalOverlayContact().getOverlayID(), node
									.getVIDNeighbours()[0].getOverlayID(),
							sendVID1);

					node.getTransLayer().send(newVIDNeighboursMsg1,
							node.getVIDNeighbours()[0].getTransInfo(),
							node.getPort(), TransProtocol.TCP);

					NewVIDNeighbourMsg newVIDNeighboursMsg2 = new NewVIDNeighbourMsg(
							node.getLocalOverlayContact().getOverlayID(), node
									.getVIDNeighbours()[1].getOverlayID(),
							sendVID2);
					node.getTransLayer().send(newVIDNeighboursMsg2,
							node.getVIDNeighbours()[1].getTransInfo(),
							node.getPort(), TransProtocol.TCP);
				}
				node.setStoredHashs(leaveLeft.getNewHashs());
			}
		}

		/**
		 * A NewNeighbourMsg arrives: The new neighbours will be saved and the
		 * old neighbours will be removed.
		 */
		else if (msg instanceof NewNeighbourMsg) {
			NewNeighbourMsg newNeighbourMsg = (NewNeighbourMsg) msg;
			log.debug("List Neighbours (before), number of neighbours "
					+ node.getNeighbours().size() + " own ID "
					+ node.getLocalOverlayContact().getOverlayID());
			for (int i = 0; i < node.getNeighbours().size(); i++) {
				log
						.debug(node.getNeighbours().get(i).getOverlayID()
								.toString());
			}

			node.removeNeighbour(newNeighbourMsg.getContact());

			for (int i = 0; i < newNeighbourMsg.getOldNeighbours().size(); i++) {
				node.removeNeighbour(newNeighbourMsg
						.getOldNeighbours().get(i));

			}
			for (int i = 0; i < newNeighbourMsg.getNewNeighbours().size(); i++) {
				if (node.getLocalOverlayContact().getArea().commonCorner(
						newNeighbourMsg.getNewNeighbours()
								.get(i).getArea())
						&& !(node
								.neighboursContain(newNeighbourMsg
										.getNewNeighbours().get(i)))
						&& !node.getLocalOverlayContact().getOverlayID()
								.toString()
								.equals(
										newNeighbourMsg
												.getNewNeighbours().get(i)
												.getOverlayID().toString())) {
					List<CanOverlayContact> addNeighbours = node
							.getNeighbours();
					addNeighbours.add(newNeighbourMsg
							.getNewNeighbours().get(i));
					node.setNeighbours(addNeighbours);
				}
			}
			node.getBootstrap().update(node);

			log.debug("List Neighbours (after), number of neighbours "
					+ node.getNeighbours().size() + " own ID "
					+ node.getLocalOverlayContact().getOverlayID());
			for (int i = 0; i < node.getNeighbours().size(); i++) {
				log.debug(node.getNeighbours().get(i).getOverlayID().toString()
						+ " "
						+ node.getNeighbours().get(i).getArea().toString());
			}
		}

		/**
		 * A NewVIDNeighbourMsg arrives: The new VID neighbour replaces the old
		 * one. The message includes an array[2] one value of the array is null.
		 * So the peer knows which neighbour should be replaced.
		 */
		else if (msg instanceof NewVIDNeighbourMsg) {
			NewVIDNeighbourMsg newVIDNeighbourMsg = (NewVIDNeighbourMsg) msg;

			if (node.getLocalOverlayContact().getArea().getVid().toString()
					.equals("0")) {
				CanOverlayContact[] vidNeighboursToSave = {
						node.getLocalOverlayContact().clone(),
						node.getLocalOverlayContact().clone() };
				node.setVIDNeigbours(vidNeighboursToSave);
			} else if (newVIDNeighbourMsg.getContact()[1] == null) {
				log.debug("NewVIDNeighbourMsg arrived: "
						+ node.getLocalOverlayContact().getOverlayID()
								.toString()
						+ " new VID Neighbours "
						+ newVIDNeighbourMsg.getContact()[0].getArea().getVid()
								.toString()
						+ " old VID Neighbours "
						+ node.getVIDNeighbours()[0].getArea().getVid()
								.toString()
						+ " "
						+ node.getVIDNeighbours()[1].getArea().getVid()
								.toString()
						+ " "
						+ node.getVIDNeighbours()[0].getArea().getVid()
								.numberCommon(
										newVIDNeighbourMsg.getContact()[0]
												.getArea().getVid())
						+ " "
						+ node.getVIDNeighbours()[1].getArea().getVid()
								.numberCommon(
										newVIDNeighbourMsg.getContact()[0]
												.getArea().getVid())
						+ " from node "
						+ newVIDNeighbourMsg.getSender().toString());
				node.getVIDNeighbours()[0] = newVIDNeighbourMsg.getContact()[0];
			}

			else {
				log.debug("NewVIDNeighbourMsg arrived: "
						+ node.getLocalOverlayContact().getOverlayID()
								.toString()
						+ " new VID Neighbours "
						+ newVIDNeighbourMsg.getContact()[1].getArea().getVid()
								.toString()
						+ " old VID Neighbours "
						+ node.getVIDNeighbours()[0].getArea().getVid()
								.toString()
						+ " "
						+ node.getVIDNeighbours()[1].getArea().getVid()
								.toString()
						+ " "
						+ node.getVIDNeighbours()[0].getArea().getVid()
								.numberCommon(
										newVIDNeighbourMsg.getContact()[1]
												.getArea().getVid())
						+ " "
						+ node.getVIDNeighbours()[1].getArea().getVid()
								.numberCommon(
										newVIDNeighbourMsg.getContact()[1]
												.getArea().getVid())
						+ " from node "
						+ newVIDNeighbourMsg.getSender().toString());
				node.getVIDNeighbours()[1] = newVIDNeighbourMsg.getContact()[1];
			}
			node.getBootstrap().update(node);
		}

		/**
		 * A TakeoverMsg arrives: This message is sent to the peer which is
		 * closest to the the missed peer. If the actual peer isn't the closest
		 * the TakeoverMsg is forwarded to either the first or the second VID
		 * neighbour. If the actual peer is the closest it saves the missed
		 * neighbour and sends a TakeoverReorganizeMsg to either the first or
		 * the second VID neighbour. Afterwards it starts the
		 * TakeoverRebuildOperation.
		 */
		else if (msg instanceof TakeoverMsg) {
			TakeoverMsg takeoverMsg = (TakeoverMsg) msg;
			log
					.debug(Simulator.getSimulatedRealtime()
							+ " TakeoverMsg arrived from: "
							+ takeoverMsg.getSender().toString()
							+ " lost "
							+ takeoverMsg.getMissingNode().getOverlayID()
									.toString()
							+ " in "
							+ node.getLocalOverlayContact().getOverlayID()
									.toString());

			CanOverlayContact receiver = null;
			CanOverlayContact neighbour = takeoverMsg.getMissingNode();
			if (!neighbour.getOverlayID().toString().equals(
					node.getVIDNeighbours()[0].getOverlayID().toString())
					&& !neighbour.getOverlayID().toString().equals(
							node.getVIDNeighbours()[1].getOverlayID()
									.toString())) {
				if (node.getLocalOverlayContact().getArea().getVid().higher(
						neighbour.getArea().getVid())) {
					receiver = node.getVIDNeighbours()[0];
				} else {
					receiver = node.getVIDNeighbours()[1];
				}
				TakeoverMsg sendFurther = new TakeoverMsg(node
						.getLocalOverlayContact().getOverlayID(), receiver
						.getOverlayID(), neighbour, takeoverMsg
						.getNeighboursOfMissing(), takeoverMsg
						.getVidNeighboursOfMissing());
				node.getTransLayer().send(sendFurther, receiver.getTransInfo(),
						node.getPort(), TransProtocol.TCP);
			} else if (neighbour.getOverlayID().toString().equals(
					node.getVIDNeighbours()[0].getOverlayID().toString())
					&& neighbour.getArea().getVid().getVIDList()
							.get(
									neighbour.getArea().getVid().getVIDList()
											.size() - 1).toString().equals("1")
					&& node.getLocalOverlayContact()
							.getArea()
							.getVid()
							.getVIDList()
							.get(
									node.getLocalOverlayContact().getArea()
											.getVid()
											.getVIDList().size() - 1)
							.toString().equals("0")) {
				receiver = takeoverMsg.getVidNeighboursOfMissing()[0];
				TakeoverMsg sendFurther = new TakeoverMsg(node
						.getLocalOverlayContact().getOverlayID(), receiver
						.getOverlayID(), neighbour, takeoverMsg
						.getNeighboursOfMissing(), takeoverMsg
						.getVidNeighboursOfMissing());
				node.getTransLayer().send(sendFurther, receiver.getTransInfo(),
						node.getPort(), TransProtocol.TCP);
			} else if (neighbour.getOverlayID().toString().equals(
					node.getVIDNeighbours()[1].getOverlayID().toString())
					&& neighbour.getArea().getVid().getVIDList()
							.get(
									neighbour.getArea().getVid().getVIDList()
											.size() - 1).toString().equals("0")
					&& node.getLocalOverlayContact()
							.getArea()
							.getVid()
							.getVIDList()
							.get(
									node.getLocalOverlayContact().getArea()
											.getVid()
											.getVIDList().size() - 1)
							.toString().equals("1")) {
				receiver = takeoverMsg.getVidNeighboursOfMissing()[1];
				TakeoverMsg sendFurther = new TakeoverMsg(node
						.getLocalOverlayContact().getOverlayID(), receiver
						.getOverlayID(), neighbour, takeoverMsg
						.getNeighboursOfMissing(), takeoverMsg
						.getVidNeighboursOfMissing());
				node.getTransLayer().send(sendFurther, receiver.getTransInfo(),
						node.getPort(), TransProtocol.TCP);
			} else {
				if (node.getMissingNode() == null) {
					log.debug("vid neighbour "
							+ node.getLocalOverlayContact().getOverlayID()
									.toString());
					node.setMissingNode(takeoverMsg.getMissingNode());
					try {
						for (int x = 0; x < takeoverMsg
								.getNeighboursOfMissing().size(); x++) {
							log.debug(takeoverMsg.getNeighboursOfMissing().get(
									x));
						}
					} catch (Exception e) {
						log.debug("neighbourlist is empty");
					}

					node.updateNeighboursOfNeighbours(takeoverMsg
							.getMissingNode(), takeoverMsg
							.getNeighboursOfMissing());
					node.updateVidNeighboursOfNeighbours(takeoverMsg
							.getMissingNode(), takeoverMsg
							.getVidNeighboursOfMissing());
					if (!node.getLocalOverlayContact().getArea().getVid()
							.closestNeighbour(
									node.getMissingNode().getArea().getVid())) {
						CanOverlayContact n = null;
						if (node.getVIDNeighbours()[0]
								.getArea()
								.getVid()
								.toString()
								.equals(neighbour.getArea().getVid().toString())) {
							n = node.getVIDNeighbours()[1];
						} else {
							n = node.getVIDNeighbours()[0];
						}
						TakeoverReorganizeMsg leave = new TakeoverReorganizeMsg(
								node.getOverlayID(), n
										.getOverlayID(), node
										.getLocalOverlayContact()
										.clone(), neighbour);
						node.getTransLayer().send(leave, n.getTransInfo(),
								node.getPort(), TransProtocol.TCP);
					}

					TakeoverRebuildOperation takeoverRebuildOperation = new TakeoverRebuildOperation(
							node);
					takeoverRebuildOperation
							.scheduleWithDelay(CanConfig.waitForTakeover);
				} else {
					log.debug("mssing node is allready set");
				}
			}
		}

		/**
		 * A TakeoverReorganizeMsg arrives: If this messages arrives from the
		 * first VID neighbour it is send to the second an vice versa.
		 * Afterwards a TakeoverReorganizeReplyMsg is send to the closest
		 * neighbour.
		 */
		else if (msg instanceof TakeoverReorganizeMsg) {
			TakeoverReorganizeMsg takeoverReorganizeMsg = (TakeoverReorganizeMsg) msg;
			log.debug(Simulator.getSimulatedRealtime()
					+ " New TakoverReorganizeMsg arrived in ID "
					+ node.getLocalOverlayContact().getOverlayID().toString()
					+ " from node: "
					+ takeoverReorganizeMsg.getSender().toString());
			log.debug(node.getLocalOverlayContact().clone().getOverlayID()
					.toString());
			log.debug(node.getLocalOverlayContact().getArea().toString());

			// weiterleiten

			TakeoverReorganizeReplyMsg takeoverReorganizeReply = new TakeoverReorganizeReplyMsg(
					node.getLocalOverlayContact().getOverlayID(),
					takeoverReorganizeMsg.getSender(), node
							.getLocalOverlayContact()
							.clone(), node.getLocalOverlayContact().getArea(),
					node
							.getNeighbours(), node.getVIDNeighbours(), node
							.getStoredHashs());
			node.getTransLayer().send(takeoverReorganizeReply,
					takeoverReorganizeMsg.getSenderNode().getTransInfo(),
					node.getPort(), TransProtocol.TCP);

			log.debug(node.getVIDNeighbours()[0].getOverlayID().toString()
					+ " "
					+ node.getVIDNeighbours()[1].getOverlayID().toString()
					+ " "
					+ takeoverReorganizeMsg.getSender().toString()
					+ " "
					+ node.getVIDNeighbours()[1].getArea().getVid()
							.numberCommon(takeoverReorganizeMsg.getVid())
					+ " "
					+ node.getLocalOverlayContact().getArea().getVid()
							.numberCommon(
									takeoverReorganizeMsg.getVid()));
			if (node.getVIDNeighbours()[0].getOverlayID().toString().equals(
					node.getVIDNeighbours()[1].getOverlayID().toString())) {
				log.debug("leaveReorganize VID0=VID1");
			}// do nothing
			else if ((node.getVIDNeighbours()[0].getOverlayID().toString()
					.equals(takeoverReorganizeMsg.getSender().toString()) && node
					.getVIDNeighbours()[1].getArea().getVid().numberCommon(
					takeoverReorganizeMsg.getVid()) == node
					.getLocalOverlayContact()
					.getArea().getVid().numberCommon(
							takeoverReorganizeMsg.getVid()))) {
				TakeoverReorganizeMsg nextLeaveReorganize = new TakeoverReorganizeMsg(
						node.getLocalOverlayContact().getOverlayID(), node
								.getVIDNeighbours()[1].getOverlayID(),
						takeoverReorganizeMsg.getSenderNode(),
						takeoverReorganizeMsg.getMissing());
				node.getTransLayer().send(nextLeaveReorganize,
						node.getVIDNeighbours()[1].getTransInfo(),
						node.getPort(), TransProtocol.TCP);
			} else if ((node.getVIDNeighbours()[1].getOverlayID().toString()
					.equals(takeoverReorganizeMsg.getSender().toString()) && node
					.getVIDNeighbours()[0].getArea().getVid().numberCommon(
					takeoverReorganizeMsg.getVid()) == node
					.getLocalOverlayContact()
					.getArea().getVid().numberCommon(
							takeoverReorganizeMsg.getVid()))) {
				TakeoverReorganizeMsg nextLeaveReorganize = new TakeoverReorganizeMsg(
						node.getLocalOverlayContact().getOverlayID(), node
								.getVIDNeighbours()[0].getOverlayID(),
						takeoverReorganizeMsg.getSenderNode(),
						takeoverReorganizeMsg.getMissing());
				node.getTransLayer().send(nextLeaveReorganize,
						node.getVIDNeighbours()[0].getTransInfo(),
						node.getPort(), TransProtocol.TCP);
			}
			// node.cancelTakeoverOperation();
			node.stopTakeoverOperation();
		}

		/**
		 * A TakeoverReorganizeReplyMsg arrives: The data from the message is
		 * just saved.
		 */
		else if (msg instanceof TakeoverReorganizeReplyMsg) {
			TakeoverReorganizeReplyMsg takeoverReorganizeReplyMsg = (TakeoverReorganizeReplyMsg) msg;
			log.debug(Simulator.getSimulatedRealtime()
					+ " New TakeoverReorganizeReplyMsg from ID "
					+ takeoverReorganizeReplyMsg.getSender().toString()
					+ " area "
					+ takeoverReorganizeReplyMsg.getArea().toString()
					+ " received VID "
					+ takeoverReorganizeReplyMsg.getMaster().getArea().getVid()
							.toString() + " receiveced Neighbours "
					+ takeoverReorganizeReplyMsg.getNeighbours().size());
			log.debug("vidNeighbours "
					+ takeoverReorganizeReplyMsg.getVidNeighbours()[0]
							.getArea().getVid().toString()
					+ " "
					+ takeoverReorganizeReplyMsg.getVidNeighbours()[1]
							.getArea().getVid().toString());
			for (int x = 0; x < takeoverReorganizeReplyMsg.getNeighbours()
					.size(); x++) {
				log.debug(takeoverReorganizeReplyMsg.getNeighbours().get(x));
			}

			node.addLeavingArea(takeoverReorganizeReplyMsg.getArea());
			node.addLeavingNeighbours(takeoverReorganizeReplyMsg
					.getNeighbours());
			node
					.addLeavingReplyContacts(takeoverReorganizeReplyMsg
							.getMaster());
			node.addLeavingReplyVIDNeighbours(takeoverReorganizeReplyMsg
					.getVidNeighbours());
			node
					.updateVidNeighboursOfNeighbours(takeoverReorganizeReplyMsg
							.getMaster(), takeoverReorganizeReplyMsg
							.getVidNeighbours());
			node.addLeavingHash(takeoverReorganizeReplyMsg.getStoredHashs());

		}

		/**
		 * A StoreMsg arrives: Eighter the peer saves the hash and the
		 * CanOverlayContact and sends the reply or the message is forwarded to
		 * the next neighbour.
		 */
		else if (msg instanceof StoreMsg) {
			StoreMsg storeMsg = (StoreMsg) msg;
			CanOverlayKey id = storeMsg.getId();
			if (!id.includedInArea(node.getLocalOverlayContact().getArea())
					&& (storeMsg.getHopCount() < CanConfig.lookupMaxHop)) {
				CanOverlayContact next = node.routingNext(id);

				StoreMsg sendFurther = new StoreMsg(node
						.getLocalOverlayContact()
						.getOverlayID(), next.getOverlayID(), storeMsg
						.getContact(), id, storeMsg.getObject(),
						storeMsg.getOperationID());
				sendFurther.setHop(storeMsg.getHopCount() + 1);
				node.getTransLayer().send(sendFurther, next.getTransInfo(),
						node.getPort(), TransProtocol.UDP);

			} else {
				node.addStoredHashs(storeMsg.getId(),
						node.getLocalOverlayContact());
				StoreReplyMsg storeReplyMsg = new StoreReplyMsg(storeMsg
						.getReceiver(), storeMsg.getSender(),
						node.getLocalOverlayContact(), storeMsg
								.getOperationID());
				storeReplyMsg.setHop(storeMsg.getHopCount());
				node.getTransLayer().send(storeReplyMsg,
						storeMsg.getContact().getTransInfo(), node.getPort(),
						TransProtocol.UDP);
			}
		}

		/**
		 * A StoreReplyMsg arrives: The StoreOperation with the id from the
		 * message is called and the message is handed to it.
		 */
		else if (msg instanceof StoreReplyMsg) {
			StoreReplyMsg storeRpl = (StoreReplyMsg) msg;
			StoreOperation op = (StoreOperation) node.getLookupStore().get(
					storeRpl.getOperationID());
			if (op != null) {
				op.found(storeRpl.getContact());
			}
		}

		/**
		 * A LookupMsg arrives: Either the peer send the reply or it forwards it
		 * to the next neighbour.
		 */
		else if (msg instanceof LookupMsg) {
			LookupMsg lookupMsg = (LookupMsg) msg;
			CanOverlayKey id = lookupMsg.getId();
			if (!id.includedInArea(node.getLocalOverlayContact().getArea())
					&& (lookupMsg.getHopCount() < CanConfig.lookupMaxHop)) {
				CanOverlayContact next = node.routingNext(id);
				if (next != null) {
					LookupMsg sendFurther = new LookupMsg(node
							.getLocalOverlayContact().getOverlayID(), next
							.getOverlayID(), lookupMsg.getOriginalSender(), id,
							lookupMsg.getOperationID());

					sendFurther.setHop(lookupMsg.getHopCount() + 1);
					// Inform Analyzer
					Simulator.getMonitor().dhtLookupForwarded(
							lookupMsg.getOriginalSender(),
							lookupMsg.getId(),
							node.getLocalOverlayContact(),
							lookupMsg.getHopCount());
					node.getTransLayer().send(sendFurther, next.getTransInfo(),
							node.getPort(), TransProtocol.UDP);
				}
			} else {
				LookupReplyMsg lookupReplyMsg = new LookupReplyMsg(lookupMsg
						.getReceiver(), lookupMsg.getSender(),
						node.getLocalOverlayContact(), lookupMsg
								.getOperationID());
				lookupReplyMsg.setHop(lookupMsg.getHopCount());

				node.getTransLayer().send(lookupReplyMsg,
						lookupMsg.getOriginalSender().getTransInfo(),
						node.getPort(),
						TransProtocol.UDP);
			}
		}

		/**
		 * A LookupReplyMsg arrives: The Lookup Operation with the ID from the
		 * message is called and the message is handed to it.
		 */
		else if (msg instanceof LookupReplyMsg) { // handle lookup reply
			LookupReplyMsg lookupRpl = (LookupReplyMsg) msg;
			LookupOperation op = (LookupOperation) node.getLookupStore().get(
					lookupRpl.getOperationID());
			if (op != null) {
				op.found(lookupRpl);
			}
		} else if (msg instanceof StartTakeoverMsg) {
			node.resumeDirectTakeoverOperation();
		}
	}

}
