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

package org.peerfact.impl.overlay.dht.pastry.components;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.peerfact.api.common.Message;
import org.peerfact.api.overlay.dht.DHTObject;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.api.transport.TransLayer;
import org.peerfact.api.transport.TransMessageCallback;
import org.peerfact.api.transport.TransMessageListener;
import org.peerfact.api.transport.TransProtocol;
import org.peerfact.impl.overlay.dht.pastry.components.TransmissionCallback.Failed;
import org.peerfact.impl.overlay.dht.pastry.components.TransmissionCallback.Succeeded;
import org.peerfact.impl.overlay.dht.pastry.messages.AckMsg;
import org.peerfact.impl.overlay.dht.pastry.messages.ConfirmStoreMsg;
import org.peerfact.impl.overlay.dht.pastry.messages.FinalJoinAnswerMsg;
import org.peerfact.impl.overlay.dht.pastry.messages.JoinMsg;
import org.peerfact.impl.overlay.dht.pastry.messages.LookupMsg;
import org.peerfact.impl.overlay.dht.pastry.messages.LookupReplyMsg;
import org.peerfact.impl.overlay.dht.pastry.messages.MsgTransInfo;
import org.peerfact.impl.overlay.dht.pastry.messages.PastryBaseMsg;
import org.peerfact.impl.overlay.dht.pastry.messages.RequestLeafSetMsg;
import org.peerfact.impl.overlay.dht.pastry.messages.RequestNeighborhooodSetMsg;
import org.peerfact.impl.overlay.dht.pastry.messages.RequestRouteSetMsg;
import org.peerfact.impl.overlay.dht.pastry.messages.StateUpdateMsg;
import org.peerfact.impl.overlay.dht.pastry.messages.StoreMsg;
import org.peerfact.impl.overlay.dht.pastry.messages.ValueLookupMsg;
import org.peerfact.impl.overlay.dht.pastry.messages.ValueLookupReplyMsg;
import org.peerfact.impl.overlay.dht.pastry.nodestate.PastryRoutingTable;
import org.peerfact.impl.overlay.dht.pastry.nodestate.RouteSet;
import org.peerfact.impl.overlay.dht.pastry.operations.AbstractPastryOperation;
import org.peerfact.impl.overlay.dht.pastry.operations.JoinOperation;
import org.peerfact.impl.overlay.dht.pastry.operations.LookupOperation;
import org.peerfact.impl.overlay.dht.pastry.operations.StoreOperation;
import org.peerfact.impl.overlay.dht.pastry.operations.ValueLookupOperation;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.transport.TransMsgEvent;
import org.peerfact.impl.util.logging.SimLogger;


/**
 * This class handles incoming messages and dispatches/processes them.
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class PastryMessageHandler implements TransMessageListener,
		TransMessageCallback {

	private static Logger log = SimLogger.getLogger(PastryMessageHandler.class);

	/**
	 * The node owning this message handler
	 */
	protected PastryNode node;

	private TransLayer translayer;

	private LinkedHashMap<Integer, MsgTransInfo<PastryContact>> notYetAcked = new LinkedHashMap<Integer, MsgTransInfo<PastryContact>>();

	private LinkedHashMap<Integer, Failed> notYetAckedCallbacksFail = new LinkedHashMap<Integer, Failed>();

	private LinkedHashMap<Integer, Succeeded> notYetAckedCallbacksSucceed = new LinkedHashMap<Integer, Succeeded>();

	protected JoinOperation unfinishedJoinOp = null;

	/**
	 * @param node
	 *            the owning pastry node
	 */
	public PastryMessageHandler(PastryNode node) {
		this.node = node;
		this.translayer = node.getTransLayer();
	}

	@Override
	public void messageArrived(TransMsgEvent receivingEvent) {
		Message msg = receivingEvent.getPayload();

		if (!(msg instanceof PastryBaseMsg)) {
			return;
		}

		PastryBaseMsg pMsg = (PastryBaseMsg) msg;
		PastryContact senderContact = new PastryContact(pMsg.getSender(),
				receivingEvent.getSenderTransInfo());

		if (pMsg.getSender() != null) {
			node.addContact(new PastryContact(pMsg.getSender(), receivingEvent
					.getSenderTransInfo()));
		}

		// TODO check if acknowledgment can be combined with possible answer
		/*
		 * Instead of a plain AckMsg each Block may decide to overwrite ackMsg
		 * to piggyback the ACK. It is important to note that a Message sent via
		 * reply will not trigger messageArrived() at the receiver but instead
		 * fire the provided callback.
		 */
		Message ackMsg = new AckMsg();

		if (msg instanceof JoinMsg) {
			JoinMsg jMsg = (JoinMsg) msg;
			PastryKey key = jMsg.getKey();

			StateUpdateMsg reply;

			if (node.isResponsibleFor(key)) {
				// Mark the reply as final reply
				reply = new FinalJoinAnswerMsg(node.getOverlayID(),
						jMsg.getSender(), node.getAllNeighbors());
			} else {
				reply = new StateUpdateMsg(node.getOverlayID(),
						jMsg.getSender(), node.getAllNeighbors());

				// Forward the JoinMessage
				forwardMsg(msg, key);
			}
			MsgTransInfo<PastryContact> replyInfo = new MsgTransInfo<PastryContact>(
					reply, jMsg.getSenderContact());
			sendMsg(replyInfo);

		} else if (msg instanceof StateUpdateMsg) {
			StateUpdateMsg rMsg = (StateUpdateMsg) msg;
			Collection<PastryContact> newContacts = rMsg.getContacts();

			long leafSetLastChanged = rMsg.getLeafSetTimestamp();
			if (leafSetLastChanged != -1
					&& leafSetLastChanged < node.getLeafSet().getLastChanged()) {
				/*
				 * Send back a more recent leaf set
				 */

				StateUpdateMsg informMsg = new StateUpdateMsg(
						node.getOverlayID(), pMsg.getSender(),
						node.getLeafSetNodes());

				MsgTransInfo<PastryContact> replyInfo = new MsgTransInfo<PastryContact>(
						informMsg, senderContact);
				sendMsg(replyInfo);
			}

			// Insert new contacts into state tables
			node.addContacts(newContacts);

			// If this was the final answer, finish join operation
			if (rMsg instanceof FinalJoinAnswerMsg && unfinishedJoinOp != null) {

				Collection<PastryContact> allNeighbors = node.getAllNeighbors();
				StateUpdateMsg informMsg;

				for (PastryContact n : allNeighbors) {
					informMsg = new StateUpdateMsg(node.getOverlayID(),
							n.getOverlayID(), allNeighbors, node.getLeafSet()
									.getLastChanged());

					MsgTransInfo<PastryContact> replyInfo = new MsgTransInfo<PastryContact>(
							informMsg, n);
					sendMsg(replyInfo);
				}

				// Inform about finished join
				unfinishedJoinOp.joinOperationFinished();
				unfinishedJoinOp = null;
			}
		} else if (msg instanceof RequestLeafSetMsg) {
			/*
			 * Answer the request with this node's leafset entries
			 */
			RequestLeafSetMsg rMsg = (RequestLeafSetMsg) msg;
			Collection<PastryContact> leafSetNodes = node.getLeafSetNodes();
			StateUpdateMsg reply = new StateUpdateMsg(node.getOverlayID(),
					rMsg.getSender(), leafSetNodes, node.getLeafSet()
							.getLastChanged());

			sendMsg(new MsgTransInfo<PastryContact>(reply, senderContact));

		} else if (msg instanceof RequestNeighborhooodSetMsg) {
			/*
			 * Answer the request with this node's neighborhoodset entries
			 */
			RequestNeighborhooodSetMsg rMsg = (RequestNeighborhooodSetMsg) msg;
			Collection<PastryContact> neighbors = node.getNeighborSetNodes();
			StateUpdateMsg reply = new StateUpdateMsg(node.getOverlayID(),
					rMsg.getSender(), neighbors);

			sendMsg(new MsgTransInfo<PastryContact>(reply, senderContact));

		} else if (msg instanceof RequestRouteSetMsg) {
			/*
			 * Answer the request with this node's entries for given
			 * row/column-pair in Routing Table
			 */
			RequestRouteSetMsg rMsg = (RequestRouteSetMsg) msg;
			PastryRoutingTable routingTable = node.getPastryRoutingTable();
			Collection<PastryContact> neighbors = new LinkedHashSet<PastryContact>();
			RouteSet entries = routingTable.getEntrySet(rMsg.getRow(),
					rMsg.getCol());
			if (entries != null) {
				for (PastryContact c : entries) {
					neighbors.add(c);
				}
			}
			// overwrite ACK to include the StateUpdate
			ackMsg = new StateUpdateMsg(rMsg.getReceiver(), rMsg.getSender(),
					neighbors);
		} else if (msg instanceof LookupMsg) {
			LookupMsg lMsg = (LookupMsg) msg;

			if (node.isResponsibleFor(lMsg.getTarget().getCorrespondingKey())) {
				// Send a reply
				LookupReplyMsg reply = new LookupReplyMsg(
						node.getOverlayContact(), lMsg.getSenderContact(), lMsg);

				MsgTransInfo<PastryContact> replyInfo = new MsgTransInfo<PastryContact>(
						reply, lMsg.getSenderContact());

				sendMsg(replyInfo);
			} else {
				// only forward messages with less than MSG_MAX_HOPS hops
				// (hack to avoid too high hop counts)
				if (lMsg.getHops() < PastryConstants.MSG_MAX_HOPS) {
					// Forward the request
					lMsg.incrementHopCount();
					// Notify Monitor
					Simulator.getMonitor().dhtLookupForwarded(
							lMsg.getSenderContact(),
							lMsg.getTarget().getCorrespondingKey(),
							node.getOverlayContact(), lMsg.getHops());
					forwardMsg(lMsg, lMsg.getTarget().getCorrespondingKey());
				}

			}
		} else if (msg instanceof LookupReplyMsg) {
			LookupReplyMsg lMsg = (LookupReplyMsg) msg;

			// Get the operation that requested the lookup
			int lookupId = lMsg.getRequest().getLookupId();
			AbstractPastryOperation<?> op = node.unregisterOperation(lookupId);

			// Deliver the result if the operation still exists
			if (op != null && op instanceof LookupOperation) {
				LookupOperation lookupOp = (LookupOperation) op;
				lookupOp.deliverResult(lMsg.getResponsibleContact(), lMsg
						.getRequest().getTarget(), lMsg.getRequest()
						.getLookupId(), lMsg.getRequest().getHops());
			} else {
				log.debug("lookup operation not found -> duplicated answer to lookup - receiver = "
						+ node.getOverlayID() + " lookupId = " + lookupId);
			}

		} else if (msg instanceof StoreMsg) {
			StoreMsg storeMsg = (StoreMsg) msg;
			if (node.isResponsibleFor(storeMsg.getKey().getCorrespondingKey())) {
				// node is responsible to store the DHTObject with the
				// respective key
				log.debug("PastryNode " + node.getOverlayID()
						+ " is maintainer of the DHTObject with key "
						+ storeMsg.getKey() + " from node "
						+ storeMsg.getSender());
				node.getDHT().addDHTEntry(
						storeMsg.getKey().getCorrespondingKey(),
						storeMsg.getValue());
				// create answer for the storing peer
				Set<PastryContact> storingPeers = new LinkedHashSet<PastryContact>();
				storingPeers.add(node.getOverlayContact());
				ConfirmStoreMsg confirmMsg = new ConfirmStoreMsg(
						node.getOverlayContact(), storeMsg.getSenderContact(),
						storingPeers, storeMsg.getOperationID());
				sendMsg(new MsgTransInfo<PastryContact>(confirmMsg,
						storeMsg.getSenderContact()));
			} else {
				// node is not responsible for the DHTObject and has to forward
				// it
				storeMsg.incrementHops();
				forwardMsg(storeMsg, storeMsg.getKey().getCorrespondingKey());
			}
		} else if (msg instanceof ConfirmStoreMsg) {
			ConfirmStoreMsg csMsg = (ConfirmStoreMsg) msg;
			AbstractPastryOperation<?> op = node
					.unregisterOperation(Integer.valueOf(csMsg.getOperationID()));
			if (op != null) {
				((StoreOperation) op).deliverResult(csMsg);
			}
		}

		// handle the value-lookup-related messages
		else if (msg instanceof ValueLookupMsg) {
			ValueLookupMsg lookupMsg = (ValueLookupMsg) msg;
			if (node.isResponsibleFor(lookupMsg.getKey().getCorrespondingKey())) {
				// node is responsible to answer with the corresponding
				// DHTObject for the respective key

				DHTObject value = (DHTObject) node.getDHT().getDHTValue(
						lookupMsg.getKey().getCorrespondingKey());
				if (value == null) {
					log.warn("PastryNode "
							+ node.getOverlayID()
							+ " is maintainer but does not have the DHTObject with key "
							+ lookupMsg.getKey() + " for requesting node "
							+ lookupMsg.getSender());
				} else {
					log.info("PastryNode " + node.getOverlayID()
							+ " is maintainer of the DHTObject with key "
							+ lookupMsg.getKey() + " for requesting node "
							+ lookupMsg.getSender());
				}
				// create answer for the requesting peer
				ValueLookupReplyMsg replyMsg = new ValueLookupReplyMsg(
						node.getOverlayContact(), lookupMsg.getSender(), value,
						lookupMsg.getOperationID(), lookupMsg.getHops());
				sendMsg(new MsgTransInfo<PastryContact>(replyMsg,
						lookupMsg.getSenderContact()));
			} else {
				// node is not responsible for the DHTObject and has to forward
				// it
				lookupMsg.incrementHops();
				forwardMsg(lookupMsg, lookupMsg.getKey().getCorrespondingKey());
			}
		} else if (msg instanceof ValueLookupReplyMsg) {
			ValueLookupReplyMsg replyMsg = (ValueLookupReplyMsg) msg;
			AbstractPastryOperation<?> op = node
					.unregisterOperation(Integer.valueOf(replyMsg
							.getOperationID()));
			if (op != null) {
				((ValueLookupOperation) op).deliverResult(replyMsg);
			}
		}

		// TODO Handle other message types if needed

		/*
		 * ACK for the message. This Message may be overwritten by a Messages
		 * ElseIf-Block
		 */
		sendReply(receivingEvent, ackMsg);
	}

	protected void sendReply(TransMsgEvent receivingEvent, Message reply) {
		translayer.sendReply(reply, receivingEvent, node.getPort(),
				TransProtocol.UDP);
	}

	/**
	 * @param msg
	 *            the message to be forwarded
	 * @param key
	 *            the key to be forwarded towards
	 * @return the communication id of the message transfer
	 */
	protected int forwardMsg(Message msg, final PastryKey key) {
		/*
		 * Forward message
		 */
		final PastryContact nextHop = node.getNextHop(key.getCorrespondingId());
		MsgTransInfo<PastryContact> toSend = new MsgTransInfo<PastryContact>(
				msg, nextHop);

		Failed transFailedCallback = new Failed() {

			private int retries = 0;

			@Override
			public void transmissionFailed(Message message) {

				// Remove failed hop
				node.removeNeighbor(nextHop);

				if (retries > PastryConstants.MSG_MAX_ALTERNATIVE_HOPS) {
					return;
				}

				// Get an alternative
				PastryContact nextHop1 = node.getNextHop(key
						.getCorrespondingId());

				if (nextHop1 != null && nextHop1 != node.getOverlayContact()) {
					MsgTransInfo<PastryContact> toSend1 = new MsgTransInfo<PastryContact>(
							message, nextHop1);
					retries++;

					sendMsg(toSend1, this);
				}
			}
		};

		return sendMsg(toSend, transFailedCallback);
	}

	/**
	 * This method is called by a join operation to inform the message handler
	 * about an ongoing join. The message handler then will inform the join
	 * operation when the final reply to its join request was received.
	 * 
	 * @param unfinishedJoinOp
	 *            the join operation to be informed
	 */
	public void setUnfinishedJoinOp(JoinOperation unfinishedJoinOp) {
		this.unfinishedJoinOp = unfinishedJoinOp;
	}

	/**
	 * Sends a message to the receiver specified in the message info.
	 * 
	 * @param info
	 *            the info about the message to be send
	 * @param cb
	 *            a callback to be able to be notified of a failed or succeeded
	 *            transmission (failing means the receiver did not answer even
	 *            after retries to send the message to him.
	 * @return the communication ID of the sent message
	 */
	public int sendMsg(MsgTransInfo<PastryContact> info, TransmissionCallback cb) {
		int commId = translayer.sendAndWait(info.getMsg(),
				info.getReceiverTranInfo(), node.getPort(), TransProtocol.UDP,
				this, PastryConstants.MSG_TIMEOUT);
		notYetAcked.put(commId, info);

		if (cb != null) {
			if (cb instanceof Failed) {
				notYetAckedCallbacksFail.put(commId, (Failed) cb);
			}
			if (cb instanceof Succeeded) {
				notYetAckedCallbacksSucceed.put(commId, (Succeeded) cb);
			}
		}

		return commId;
	}

	/**
	 * Sends a message to the receiver specified in the message info.
	 * 
	 * @param info
	 *            the info about the message to be send
	 * @return the communication ID of the sent message
	 */
	public int sendMsg(MsgTransInfo<PastryContact> info) {
		return sendMsg(info, null);
	}

	@Override
	public void receive(Message msg, TransInfo senderInfo, int commId) {
		if (commId == -1) {
			return;
		}

		// check if PastryBaseMsg is sufficient, maybe introduce new marker
		// Interface
		if (msg instanceof PastryBaseMsg) {
			Message originalMsg = notYetAcked.remove(commId).getMsg();

			Succeeded cb = notYetAckedCallbacksSucceed.remove(commId);
			if (cb != null) {
				cb.transmissionSucceeded(originalMsg, msg);
			}
		}
	}

	@Override
	public void messageTimeoutOccured(int commId) {
		if (commId == -1) {
			return;
		}

		MsgTransInfo<PastryContact> info = notYetAcked.remove(commId);

		if (info != null) {
			if (info.getRetransmissions() < PastryConstants.MSG_MAX_RETRANSMISSIONS) {
				info.incRetransmissions();

				Failed cb = notYetAckedCallbacksFail.remove(commId);
				sendMsg(info, cb);
			} else {
				Failed cb = notYetAckedCallbacksFail.remove(commId);
				if (cb != null) {
					cb.transmissionFailed(info.getMsg());

					/*
					 * Contacted node is considered as failed. We remove it from
					 * the state tables. This may trigger the state tables to
					 * replace the entry.
					 */
					node.removeNeighbor(info.getOlContact());
				}
			}
		}
	}
}
