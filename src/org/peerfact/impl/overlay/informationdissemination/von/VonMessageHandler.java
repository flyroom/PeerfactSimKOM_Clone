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

package org.peerfact.impl.overlay.informationdissemination.von;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.peerfact.api.common.Message;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.api.transport.TransMessageCallback;
import org.peerfact.api.transport.TransMessageListener;
import org.peerfact.impl.common.Operations;
import org.peerfact.impl.overlay.informationdissemination.von.messages.HelloMsg;
import org.peerfact.impl.overlay.informationdissemination.von.messages.InitialQueryMsg;
import org.peerfact.impl.overlay.informationdissemination.von.messages.MoveMsg;
import org.peerfact.impl.overlay.informationdissemination.von.messages.ObtainIDMsg;
import org.peerfact.impl.overlay.informationdissemination.von.messages.PeerMsg;
import org.peerfact.impl.overlay.informationdissemination.von.messages.SimpleAckMsg;
import org.peerfact.impl.overlay.informationdissemination.von.operations.HelloOperation;
import org.peerfact.impl.overlay.informationdissemination.von.voronoi.Voronoi;
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
 * This class includes all logic concerned with incoming messages to not
 * overload the node class.
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class VonMessageHandler implements TransMessageCallback,
		TransMessageListener {

	final static Logger log = SimLogger.getLogger(VonMessageHandler.class);

	private final VonNode node;

	private final LinkedHashMap<Integer, UnAckedMsg<InitialQueryMsg>> notAckedInitalQueries = new LinkedHashMap<Integer, UnAckedMsg<InitialQueryMsg>>();

	/**
	 * @param node
	 *            the VON node, this message hanlder belongs to
	 */
	public VonMessageHandler(VonNode node) {
		this.node = node;
	}

	@Override
	public void messageTimeoutOccured(int commId) {

		if (notAckedInitalQueries.containsKey(Integer.valueOf(commId))) {
			/*
			 * Do retransmission until configured limit, then remove node from
			 * voronoi and retrieve new candidate for a new try.
			 */
			UnAckedMsg<InitialQueryMsg> mInfo = notAckedInitalQueries
					.remove(Integer.valueOf(commId));

			if (mInfo.numOfRetries <= VonConfiguration.GENERAL_MSG_RETRANSMISSIONS) {

				// Do retransmission
				forwardInitialQueryMsg(mInfo.msg, mInfo.recInfo,
						mInfo.numOfRetries);

			} else {

				VonID recOlID = mInfo.recInfo.getContact().getOverlayID();
				if (recOlID != null) {
					/*
					 * Remove receiver from the voronoi and try another one.
					 */
					node.getLocalVoronoi().remove(recOlID);
					VonNodeInfo closestNodeInfo = node.getLocalVoronoi()
							.getClosestToNodeInfo(
									mInfo.msg.getSenderInfo().getPosition());

					forwardInitialQueryMsg(mInfo.msg, closestNodeInfo);

				} else {
					// Could not transmit to bootstrap node
					log.error(node.getTransInfo().getNetId()
							+ " Could not deliver InitialQueryMsg to bootstrap node ("
							+ mInfo.recInfo.getContact().getTransInfo()
									.getNetId() + ").");
				}
			}

		}
	}

	@Override
	public void receive(Message msg, TransInfo senderInfo, int commId) {
		// Not used as we do not use SendAndWait here
		notAckedInitalQueries.remove(commId);
	}

	@Override
	public void messageArrived(TransMsgEvent receivingEvent) {
		Message msg = receivingEvent.getPayload();

		Voronoi localVoronoi = node.getLocalVoronoi();

		if (msg instanceof ObtainIDMsg) {
			ObtainIDMsg om = (ObtainIDMsg) msg;

			log.debug(node.getVonID() + " received ObtainIDMsg from "
					+ receivingEvent.getSenderTransInfo().getNetId());

			if (node.isMaster && om.getSender().compareTo(VonID.EMPTY_ID) == 0) {

				VonID newID = new VonID(
						VonNode.lastAssignedID.getUniqueValue() + 1);
				VonNode.setLastAssignedID(newID);

				om = new ObtainIDMsg(node.getVonID(), newID);

				node.getTransLayer().sendReply(om, receivingEvent,
						node.getPort(), VonConfiguration.TRANSPORT_PROTOCOL);
			}
		} else if (msg instanceof HelloMsg) {
			/*
			 * Insert the new node into local voronoi and check if all enclosing
			 * neighbors are known. Otherwise inform sender about them with
			 * PeerMsg.
			 */

			HelloMsg hMsg = (HelloMsg) msg;

			log.debug(node.getVonID() + " received new HelloMsg from "
					+ hMsg.getSender());
			/*
			 * Insert new node into local voronoi
			 */
			if (!localVoronoi.isPresentInVoronoi(hMsg.getSender())) {
				localVoronoi.insert(hMsg.getSenderNodeInfo(),
						Simulator.getCurrentTime());
			} else {
				localVoronoi.updateTimestamp(hMsg.getSender(),
						Simulator.getCurrentTime());
			}

			/*
			 * Find out which enclosings the sender is missing
			 */
			LinkedList<VonID> realEnclosings = new LinkedList<VonID>(
					Arrays.asList(localVoronoi.getEnclosingNeighbors(node
							.getVonID())));

			realEnclosings.removeAll(Arrays.asList(hMsg
					.getReceiversEnclosings()));

			// Remove me as the node knows me already
			if (realEnclosings.remove(node.getVonID())) {
				log.debug(node.getVonID()
						+ " removed own ID from real enclosings.");
			}

			/*
			 * If there are unknown enclosing neighbors, inform the sender about
			 * them with a PeerMsg.
			 */
			if (realEnclosings.size() > 0) {
				log.debug(node.getVonID() + " found " + realEnclosings.size()
						+ " enclosings the sender (" + hMsg.getSender()
						+ ") of the HelloMsg does not know: "
						+ realEnclosings.getFirst().getUniqueValue()
						+ ((realEnclosings.size() > 1) ? ",.." : ""));

				VonNodeInfo[] unkownToSenders = localVoronoi
						.getAllNodeInfo(realEnclosings);

				Long[] unkownToSenderTimestamps = localVoronoi
						.getAllNodeTimestamps(realEnclosings);

				PeerMsg pMsg = new PeerMsg(node.getVonID(), hMsg
						.getSenderNodeInfo().getContact().getOverlayID(),
						unkownToSenders, unkownToSenderTimestamps);

				node.getTransLayer().send(pMsg,
						hMsg.getSenderNodeInfo().getContact().getTransInfo(),
						node.getPort(), VonConfiguration.TRANSPORT_PROTOCOL);
			}

			// Remove contacts that are not needed anymore
			node.removeUnneededContactsFromVoronoi();

		} else if (msg instanceof InitialQueryMsg) {
			InitialQueryMsg qm = (InitialQueryMsg) msg;

			log.debug(node.getVonID() + " received InitialQueryMsg from "
					+ qm.getSender());

			// Acknowledge the receiving of the message
			node.getTransLayer().sendReply(
					new SimpleAckMsg(node.getVonID(), qm.getSender()),
					receivingEvent, node.getPort(),
					VonConfiguration.TRANSPORT_PROTOCOL);

			VonNodeInfo closestNode = localVoronoi.getClosestToNodeInfo(qm
					.getSenderInfo().getPosition());

			// FIXME: Is a dirty solution!
			// it is a null pointer exception thrown, by the Scenario 500 Nodes,
			// 250 goes offline, 200 AOI and 1200x1200 map.
			if (closestNode == null || closestNode.getContact() == null) {
				return;
			}
			VonID closestNodeID = closestNode.getContact().getOverlayID();

			if (qm.getSenderInfo().getContact().getOverlayID()
					.compareTo(closestNodeID) == 0) {
				/*
				 * Avoid that the message is delivered to the node itself. This
				 * can happen when a node rejoins after churn into it's old
				 * region and the neighbors did not realize that it was
				 * off-line.
				 */
				// node.getLocalVoronoi().remove(closestNodeID);
				closestNode = localVoronoi.getClosestToNodeInfo(qm
						.getSenderInfo().getPosition());
				closestNodeID = closestNode.getContact().getOverlayID();
			}

			boolean isInsideSite = localVoronoi.insideRegion(node.getVonID(),
					qm.getSenderInfo().getPosition());

			if (isInsideSite) {
				log.debug(node.getVonID() + " got InitialQueryMsg("
						+ qm.getSender() + ") from peer in his region.");
				/*
				 * Answer with contacts to neighbors
				 */
				doInitialPeering(qm);
			} else {
				/*
				 * Greedy forward to nearer peer
				 */

				VonNodeInfo recInfo = closestNode;

				forwardInitialQueryMsg(qm, recInfo);

			}
		} else if (msg instanceof PeerMsg) {
			PeerMsg pm = (PeerMsg) msg;

			log.debug(node.getOverlayID()
					+ " received PeerMsg ("
					+ pm.getNodes().length
					+ " contacts ("
					+ (pm.getNodes().length > 0 ? pm.getNodes()[0].getContact()
							.getOverlayID()
							+ (pm.getNodes().length > 1 ? ","
									+ pm.getNodes()[1].getContact()
											.getOverlayID()
									+ (pm.getNodes().length > 2 ? ",.." : "")
									: "") : "-") + ")) from " + pm.getSender());

			VonNodeInfo[] newNeighbors = pm.getNodes();
			Long[] timestamps = pm.getTimestamps();

			if (newNeighbors.length == timestamps.length) {
				/*
				 * Insert all new nodes into the local voronoi
				 */
				LinkedList<VonNodeInfo> toHello = localVoronoi.insertAll(
						newNeighbors, timestamps);

				/*
				 * Tell the node that the PeerMsg was received. Used to end
				 * potential waiting JoinOperations successful.
				 */
				node.peerMsgReceived();

				// Remove contacts that are not needed anymore
				LinkedList<VonNodeInfo> notNeeded = node
						.removeUnneededContactsFromVoronoi();
				toHello.removeAll(notNeeded);

				log.debug(node.getVonID()
						+ " not needed contacts of PeerMsg: "
						+ (notNeeded.size() > 0 ? notNeeded.get(0)
								+ ""
								+ (notNeeded.size() > 1 ? notNeeded.get(1) + ""
										+ (notNeeded.size() > 2 ? ",.." : "")
										: "") : "-"));

				if (toHello.size() > 0) {
					HelloOperation hOp = new HelloOperation(node, toHello,
							Operations.EMPTY_CALLBACK);
					hOp.scheduleImmediately();
				}

			} else {
				log.error("Could not add new contacts. The number of contacts and timestamps was not equal.");
			}

		} else if (msg instanceof MoveMsg) {
			MoveMsg mMsg = (MoveMsg) msg;

			log.debug(node.getVonID() + " received MoveMsg (boundary:"
					+ mMsg.isToBoundary() + ") from " + mMsg.getSender()
					+ " (new position: " + mMsg.getPosition() + ")");

			// If sender is unknow, just add him to the voronoi
			if (!localVoronoi.isPresentInVoronoi(mMsg.getSender())) {
				localVoronoi.insert(
						new VonNodeInfo(new VonContact(mMsg.getSender(),
								receivingEvent.getSenderTransInfo()), mMsg
								.getPosition(), mMsg.getAoiRadius()), Simulator
								.getCurrentTime());

				log.debug(node.getVonID() + " sender of MoveMsg ("
						+ mMsg.getSender()
						+ ") is not present in voronoi. -> I inserted him.");
			} else {

				if (mMsg.isToBoundary()) {
					/*
					 * Check for new neighbors after a position update and send
					 * PeerMsg to inform about them.
					 */

					List<VonNodeInfo> oldNeighbors = new LinkedList<VonNodeInfo>(
							Arrays.asList(localVoronoi.getBoundingOrEnclosing(
									mMsg.getSender(), mMsg.getAoiRadius())));

					// Here we use the current time not the transmitted one as
					// told in the paper
					localVoronoi.update(mMsg.getSender(), mMsg.getPosition(),
							mMsg.getAoiRadius(), Simulator.getCurrentTime());

					List<VonNodeInfo> newNeighbors = new LinkedList<VonNodeInfo>(
							Arrays.asList(localVoronoi.getBoundingOrEnclosing(
									mMsg.getSender(), mMsg.getAoiRadius())));

					newNeighbors.removeAll(oldNeighbors);

					if (newNeighbors.size() > 0) {

						/*
						 * Retrieve all needed information and send PeerMsg
						 */

						VonNodeInfo[] toInform = newNeighbors
								.toArray(new VonNodeInfo[newNeighbors.size()]);

						Long[] timestamps = localVoronoi
								.getAllNodeTimestampsByInfo(newNeighbors);

						PeerMsg pMsg = new PeerMsg(node.getVonID(),
								mMsg.getSender(), toInform, timestamps);

						node.getTransLayer().send(pMsg,
								receivingEvent.getSenderTransInfo(),
								node.getPort(),
								VonConfiguration.TRANSPORT_PROTOCOL);

						log.debug(node.getVonID() + " send PeerMsg ("
								+ newNeighbors.size()
								+ " contacts) to inform about new Neighbors");

					}
				} else {
					// Simple update the position
					localVoronoi.update(mMsg.getSender(), mMsg.getPosition(),
							mMsg.getAoiRadius(), Simulator.getCurrentTime());
				}

			}

			// Remove contacts that are not needed anymore
			node.removeUnneededContactsFromVoronoi();

		}

	}

	private void doInitialPeering(InitialQueryMsg qm) {

		Voronoi localVoronoi = node.getLocalVoronoi();

		// Insert the querying peer itself
		localVoronoi.insert(qm.getSenderInfo(), Simulator.getCurrentTime());

		/*
		 * retrieve nodeinfos and timestamps of peer to be notified about
		 */
		VonNodeInfo[] toBeNotifiedAbout = localVoronoi.getVonNeighbors(
				qm.getSender(), qm.getSenderInfo().getAoiRadius());

		Long[] timestamps = localVoronoi.getAllNodeTimestampsByInfo(Arrays
				.asList(toBeNotifiedAbout));

		/*
		 * Build the PeerMsg and send it
		 */
		PeerMsg pm = new PeerMsg(node.getVonID(), qm.getSenderInfo()
				.getContact().getOverlayID(), toBeNotifiedAbout, timestamps);

		node.getTransLayer().send(pm,
				qm.getSenderInfo().getContact().getTransInfo(), node.getPort(),
				VonConfiguration.TRANSPORT_PROTOCOL);

		log.debug(node.getVonID() + " sent PeerMsg to " + qm.getSender() + " ("
				+ toBeNotifiedAbout.length + " contacts)");
	}

	public void sendInitialQueryMsgToBootstrap(InitialQueryMsg qm) {
		VonBootstrapManager bsm = node.getBootstrapManager();

		if (bsm.anyNodeAvailable()) {
			TransInfo recInfo = bsm.getBootstrapInfo().get(0);

			forwardInitialQueryMsg(qm, new VonNodeInfo(new VonContact(null,
					recInfo), null, 0));
		} else {
			// Should not happen - just to be sure
			log.error(node.getTransInfo().getNetId()
					+ " tried to send inital query msg but there is no gateway server.");
		}
	}

	private void forwardInitialQueryMsg(InitialQueryMsg qm, VonNodeInfo recInfo) {
		forwardInitialQueryMsg(qm, recInfo, 0);
	}

	private void forwardInitialQueryMsg(InitialQueryMsg qm,
			VonNodeInfo recInfo, int numOfRetransmissions) {
		if (recInfo != null && recInfo.getContact() != null) {
			int commID = node.getTransLayer().sendAndWait(qm,
					recInfo.getContact().getTransInfo(), node.getPort(),
					VonConfiguration.TRANSPORT_PROTOCOL, this,
					VonConfiguration.GENERAL_MSG_TIMEOUT);

			notAckedInitalQueries.put(commID, new UnAckedMsg<InitialQueryMsg>(
					qm, recInfo, numOfRetransmissions + 1));
		}

	}

	private static class UnAckedMsg<T extends Message> {
		public T msg;

		public VonNodeInfo recInfo;

		public int numOfRetries = 0;

		public UnAckedMsg(T msg, VonNodeInfo recInfo, int numOfRetries) {
			this.numOfRetries = numOfRetries;
			this.msg = msg;
			this.recInfo = recInfo;
		}
	}

}
