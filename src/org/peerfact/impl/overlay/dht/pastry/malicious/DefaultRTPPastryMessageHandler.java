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

package org.peerfact.impl.overlay.dht.pastry.malicious;

import java.util.Collection;
import java.util.LinkedHashSet;

import org.peerfact.api.common.Message;
import org.peerfact.impl.overlay.dht.pastry.components.PastryContact;
import org.peerfact.impl.overlay.dht.pastry.components.PastryKey;
import org.peerfact.impl.overlay.dht.pastry.components.PastryMessageHandler;
import org.peerfact.impl.overlay.dht.pastry.components.PastryNode;
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
import org.peerfact.impl.transport.TransMsgEvent;


/**
 * This class handles incoming messages and dispatches/processes them.
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class DefaultRTPPastryMessageHandler extends PastryMessageHandler {

	// private static int involvedInRouting = 0;

	/**
	 * @param node
	 *            the owning pastry node
	 */
	public DefaultRTPPastryMessageHandler(PastryNode node) {
		super(node);
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
			/*
			 * node.addContact(new PastryContact(pMsg.getSender(),
			 * receivingEvent .getSenderTransInfo()));
			 */
		}

		// TODO check if acknowledgment can be combined with possible answer
		/*
		 * Instead of a plain AckMsg each Block may decide to overwrite ackMsg
		 * to piggyback the ACK. It is important to note that a Message sent via
		 * reply will not trigger messageArrived() at the receiver but instead
		 * fire the provided callback.
		 */
		Message ackMsg = new AckMsg();

		// involvedInRouting++;
		// System.out.println("Malicious: " + involvedInRouting);

		if (pMsg instanceof LookupMsg
				|| pMsg instanceof StoreMsg
				|| pMsg instanceof ValueLookupMsg
				|| pMsg instanceof LookupReplyMsg
				|| pMsg instanceof ConfirmStoreMsg
				|| pMsg instanceof ValueLookupReplyMsg) {

			if (RTPPastryUtil.isMalicious(pMsg.getReceiver())) {
				// System.out.println("sending lookup ack to malicious");
			} else {
				// System.out.println("sending lookup ack to normal");
			}
			sendReply(receivingEvent, ackMsg);
			return;
		}

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
		}
		/*
		 * ACK for the message. This Message may be overwritten by a Messages
		 * ElseIf-Block
		 */
		sendReply(receivingEvent, ackMsg);
	}
}
