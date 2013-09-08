/*
 * Copyright (c) 2012-2013 Open Source Community - <http://www.peerfact.org>
 * Copyright (c) 2011-2012 University of Paderborn - UPB
 * Copyright (c) 2005-2011 KOM - Multimedia Communications Lab
 *
 * This file is part of PeerfactSim.
 * 
 * PeerfactSim is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 * 
 * PeerfactSim is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with PeerfactSim.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.peerfact.impl.overlay.dht.chord.carechord.components;

import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.peerfact.api.common.Message;
import org.peerfact.api.overlay.dht.DHTObject;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordContact;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordMessageHandler;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordNode;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordRoutingTable;
import org.peerfact.impl.overlay.dht.chord.base.components.ChordConfiguration;
import org.peerfact.impl.overlay.dht.chord.base.messages.AbstractRequestMessage;
import org.peerfact.impl.overlay.dht.chord.base.messages.AckMessage;
import org.peerfact.impl.overlay.dht.chord.base.messages.ChordMessage;
import org.peerfact.impl.overlay.dht.chord.base.messages.InitializeDownloadMessage;
import org.peerfact.impl.overlay.dht.chord.base.messages.InitializeDownloadReplyMessage;
import org.peerfact.impl.overlay.dht.chord.base.messages.JoinMessage;
import org.peerfact.impl.overlay.dht.chord.base.messages.JoinReply;
import org.peerfact.impl.overlay.dht.chord.base.messages.LeaveMessage;
import org.peerfact.impl.overlay.dht.chord.base.messages.LookupMessage;
import org.peerfact.impl.overlay.dht.chord.base.messages.LookupReply;
import org.peerfact.impl.overlay.dht.chord.base.messages.NotifyOfflineMsg;
import org.peerfact.impl.overlay.dht.chord.base.messages.RetrievePredecessorMsg;
import org.peerfact.impl.overlay.dht.chord.base.messages.RetrievePredecessorReply;
import org.peerfact.impl.overlay.dht.chord.base.messages.RetrieveSuccessorMsg;
import org.peerfact.impl.overlay.dht.chord.base.messages.RetrieveSuccessorReply;
import org.peerfact.impl.overlay.dht.chord.base.messages.StoreMessage;
import org.peerfact.impl.overlay.dht.chord.base.messages.StoreReplyMessage;
import org.peerfact.impl.overlay.dht.chord.base.messages.ValueLookupMessage;
import org.peerfact.impl.overlay.dht.chord.base.messages.ValueLookupReplyMessage;
import org.peerfact.impl.overlay.dht.chord.base.operations.AbstractChordOperation;
import org.peerfact.impl.overlay.dht.chord.base.operations.LookupOperation;
import org.peerfact.impl.overlay.dht.chord.carechord.messages.CreateLinkMessage;
import org.peerfact.impl.overlay.dht.chord.carechord.messages.CreateLinkReplyMessage;
import org.peerfact.impl.overlay.dht.chord.carechord.messages.LookupSuccessorsMessage;
import org.peerfact.impl.overlay.dht.chord.carechord.messages.PingMessage;
import org.peerfact.impl.overlay.dht.chord.carechord.messages.PongMessage;
import org.peerfact.impl.overlay.dht.chord.carechord.messages.SuccessorReplyMessage;
import org.peerfact.impl.overlay.dht.chord.carechord.messages.VNodeDownMessage;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.transport.TransMsgEvent;
import org.peerfact.impl.util.logging.SimLogger;


/**
 * ChordMessageHandler handle incoming Overlay Messages.
 * 
 * @author Markus Benter (original author)
 * @author Thim Strothmann (Adaptions)
 * 
 */
public class ChordMessageHandler extends AbstractChordMessageHandler {

	private static Logger log = SimLogger.getLogger(ChordMessageHandler.class);

	private final AbstractChordNode node;

	private final ChordMessageHandlerLookup chordMessageHandlerLookup;

	// count number of lookup was dropped cause of exceedance MAX_HOP_COUNT
	private static int dropLookupcount;

	public ChordMessageHandler(AbstractChordNode node) {
		this.node = node;

		chordMessageHandlerLookup = new ChordMessageHandlerLookup(node);
	}

	@Override
	public void messageArrived(TransMsgEvent receivingEvent) {

		if (!node.isPresent()) {
			return;
		}
		Message msg = receivingEvent.getPayload();
		log.debug("node " + node + " receive msg " + msg + " at "
				+ Simulator.getCurrentTime() / Simulator.SECOND_UNIT);
		AbstractChordRoutingTable routingTable = node.getChordRoutingTable();

		// increase hop count
		if (msg instanceof AbstractRequestMessage) {

			AbstractRequestMessage chordMsg = (AbstractRequestMessage) msg;
			if (!receivingEvent.getSenderTransInfo()
					.equals(node.getTransInfo())) {
				chordMsg.incHop();
				if (chordMsg.getHopCount() > ChordConfiguration.MAX_HOP_COUNT) {
					log.debug("invalid route hop = " + chordMsg.getHopCount()
							+ " msg = " + chordMsg + " last sender "
							+ receivingEvent.getSenderTransInfo());
					log.debug(" last sender "
							+ node.getBootstrapManager().getOverlayNode(
									receivingEvent.getSenderTransInfo()));
					if (chordMsg.getHopCount() > ChordConfiguration.MAX_HOP_COUNT * 3 / 2) {
						log.error("drop msg = " + chordMsg);
						if (chordMsg instanceof LookupMessage) {
							dropLookupcount++;
							log.debug("sum of dropped lookup = "
									+ dropLookupcount);
						}
						return;
					}
				}
			}
		}

		// request messages

		if (msg instanceof LookupMessage) {
			LookupMessage lookupMsg = (LookupMessage) msg;
			handleLookupMsg(lookupMsg);
			sendAck(msg, receivingEvent, true);
		}

		else if (msg instanceof JoinMessage) {
			JoinMessage joinMessage = (JoinMessage) msg;
			JoinReply reply = new JoinReply(joinMessage.getReceiverContact(),
					joinMessage.getSenderContact());
			sendReply(reply, receivingEvent);

			// add joining node to our routingtable.
			((ChordRoutingTable) node.getChordRoutingTable())
					.addUnmarkedContact(
					joinMessage.getSenderContact());
		}

		else if (msg instanceof RetrievePredecessorMsg) {
			RetrievePredecessorReply reply = new RetrievePredecessorReply(
					((RetrievePredecessorMsg) msg).getReceiverContact(),
					((RetrievePredecessorMsg) msg).getSenderContact(),
					routingTable.getPredecessor());
			sendReply(reply, receivingEvent);
		}

		else if (msg instanceof RetrieveSuccessorMsg) {
			RetrieveSuccessorReply reply = new RetrieveSuccessorReply(
					((RetrieveSuccessorMsg) msg).getReceiverContact(),
					((RetrieveSuccessorMsg) msg).getSenderContact(),
					routingTable.getSuccessor());
			sendReply(reply, receivingEvent);
		}

		else if (msg instanceof NotifyOfflineMsg) {
			NotifyOfflineMsg notifyOfflineMsg = (NotifyOfflineMsg) msg;
			log.info("Inform offline receive NotifyOfflineMsg");
			routingTable.receiveOfflineEvent(notifyOfflineMsg.getOfflineInfo());
			sendAck(msg, receivingEvent, false);
		}

		else if (msg instanceof LeaveMessage) {
			LeaveMessage leaveMessage = (LeaveMessage) msg;
			log.debug("Inform offline receive LeaveMessage");
			routingTable.receiveOfflineEvent(leaveMessage.getSenderContact());
			sendAck(msg, receivingEvent, false);
		}

		// reply messages

		else if (msg instanceof LookupReply) {

			LookupReply reply = (LookupReply) msg;
			int lookupId = reply.getRequest().getLookupID();
			AbstractChordOperation<?> op = node.getLookupOperation(lookupId);
			if (op != null && op instanceof LookupOperation) {
				LookupOperation lookupOp = (LookupOperation) op;
				lookupOp.deliverResult(reply.getResponsibleContact(), reply
						.getRequest().getTarget(), reply.getRequest()
						.getLookupID(), reply.getRequest().getHopCount());
			} else {
				log.debug("lookup operation not found -> duplicated answer to lookup - receiver = "
						+ node.getOverlayID() + " lookupId = " + lookupId);
			}
			sendAck(msg, receivingEvent, false);
		}

		// messages used to confirm to the DHTNode interface

		else if (msg instanceof StoreMessage) {
			StoreMessage storeMsg = (StoreMessage) msg;

			// first check if we already stored this particular object:
			if (node.getDHT().getDHTValue(
					storeMsg.getKey().getCorrespondingKey()) == null) {

				node.getDHT().addDHTEntry(
						storeMsg.getKey().getCorrespondingKey(),
						storeMsg.getObject());

				// notify Analyzer about this:

			}

			// node.getStoredObjects()
			// .put(storeMsg.getKey(), storeMsg.getObject());

			StoreReplyMessage reply = new StoreReplyMessage(
					((StoreMessage) msg).getReceiverContact(),
					((StoreMessage) msg).getSenderContact(),
					node.getLocalOverlayContact());
			sendReply(reply, receivingEvent);

		} else if (msg instanceof ValueLookupMessage) {
			ValueLookupMessage lookupMsg = (ValueLookupMessage) msg;
			DHTObject object = (DHTObject) node.getDHT().getDHTValue(
					lookupMsg.getTargetKey().getCorrespondingKey());
			// node.getStoredObjects().get(
			// lookupMsg.getTargetKey());

			if (object != null) {
				// notify Analyzer about this:
				Simulator.getMonitor().dhtOwnDocumentServed(
						node.getLocalOverlayContact(), object, true);
			}

			ValueLookupReplyMessage reply = new ValueLookupReplyMessage(
					((ValueLookupMessage) msg).getReceiverContact(),
					((ValueLookupMessage) msg).getSenderContact(), object);
			sendReply(reply, receivingEvent);
		}

		// stabilization Messages:
		if (msg instanceof CreateLinkMessage) {
			CreateLinkMessage m = (CreateLinkMessage) msg;

			if (!m.getTypeOfEdge().equals("unmarked")
					&& !m.getTypeOfEdge().equals("ring")
					&& !m.getTypeOfEdge().equals("connection")) {
				System.out
						.println("ERROR! received CreateLinkMessage with unknown Edgetype "
								+ m.getTypeOfEdge());
			}

			if (m.getReceiverID().equals(this.node.getOverlayID())) {
				for (AbstractChordContact cc : m.getTargetOfNewLink()) {
					if (m.getTypeOfEdge().equals("unmarked")) {
						((ChordRoutingTable) node.getChordRoutingTable()).unmarkedEdgeListToAddNextRound
								.add(cc);
					}
					if (m.getTypeOfEdge().equals("ring")) {
						((ChordRoutingTable) node.getChordRoutingTable()).ringEdgeListToAddNextRound
								.add(cc);
					}
					if (m.getTypeOfEdge().equals("connection")) {
						((ChordRoutingTable) node.getChordRoutingTable()).connectionEdgeListToAddNextRound
								.add(cc);
					}
				}
			} else {
				// the message is for one of my virtual peers - find out who:
				boolean vNodeFound = false;
				if (this.node.isPresent()) {
					for (AbstractChordRoutingTable v : ((ChordNode) node)
							.getVirtualNodes()) {
						if (((ChordRoutingTable) v).id
								.equals(m.getReceiverID())) {
							vNodeFound = true;
							for (AbstractChordContact cc : m
									.getTargetOfNewLink()) {
								if (m.getTypeOfEdge().equals("unmarked")) {
									((ChordRoutingTable) v).unmarkedEdgeListToAddNextRound
											.add(cc);
								}
								if (m.getTypeOfEdge().equals("ring")) {
									((ChordRoutingTable) v).ringEdgeListToAddNextRound
											.add(cc);
								}
								if (m.getTypeOfEdge().equals("connection")) {
									((ChordRoutingTable) v).connectionEdgeListToAddNextRound
											.add(cc);
								}
							}
							break;
						}
					}
				}
				if (!vNodeFound) {
					// the message is for a vNode that does not exist no more.

					// send message that this vNode is dead.
					VNodeDownMessage vmsg = new VNodeDownMessage(
							this.node.getLocalOverlayContact(),
							m.getSenderContact());
					vmsg.setvNode(m.getReceiverID());

					this.node.getTransLayer().send(vmsg,
							m.getSenderContact().getTransInfo(),
							this.node.getTransInfo().getPort(),
							ChordConfiguration.TRANSPORT_PROTOCOL);
				}
			}
			// reply:
			CreateLinkReplyMessage reply = new CreateLinkReplyMessage(
					m.getReceiverContact(), m.getSenderContact());
			sendReply(reply, receivingEvent);
		}
		if (msg instanceof VNodeDownMessage) {
			// we received a vDownMessage, so remove that peer from all
			// routingtables.
			VNodeDownMessage vmsg = (VNodeDownMessage) msg;

			((ChordNode) this.node).FoundDeadHost(vmsg.getVNode());

		}

		if (msg instanceof PingMessage) {
			// respond with pong message.
			PongMessage pong = new PongMessage(
					((PingMessage) msg).getReceiverContact(),
					((PingMessage) msg).getSenderContact());
			sendReply(pong, receivingEvent);
		}

		// download messages:
		if (msg instanceof InitializeDownloadMessage) {
			InitializeDownloadMessage init = (InitializeDownloadMessage) msg;
			// find document and send reply back:
			DHTObject object = null;
			long objectSize = 0;

			// search local storage for that document.
			if (node.getDHT().getDHTEntry(init.getDocumentId()) != null) {
				object = (DHTObject) node.getDHT()
						.getDHTEntry(init.getDocumentId()).getValue();
				objectSize = node.getDHT().getDHTEntry(init.getDocumentId())
						.getTransmissionSize();
			}

			if (object != null) {
				if (objectSize == 0) {
					objectSize = object.getTransmissionSize();
				}
				// reply:
				InitializeDownloadReplyMessage rep = new InitializeDownloadReplyMessage(
						init.getReceiverContact(), init.getSenderContact(),
						init.getDocumentId(), objectSize,
						ChordConfiguration.INIT_DOWNLOAD_CHUNK_SIZE);

				sendReply(rep, receivingEvent);
			}
		}

		// looks up the next successor of successor (up to k successors, as
		// defined in CaReChordOptions)
		if (msg instanceof LookupSuccessorsMessage) {
			LookupSuccessorsMessage lsMsg = (LookupSuccessorsMessage) msg;
			int ttl = lsMsg.getTimeToLive();
			ttl = ttl - 1;

			// forward message if time to live > 0
			if (ttl > 0) {
				AbstractChordContact receiverContact = routingTable
						.getSuccessor();

				if (receiverContact != null) {
					LookupSuccessorsMessage forwardMsg = new LookupSuccessorsMessage(
							lsMsg.getSenderContact(),
							lsMsg.getVirtualNodeIdentifier(), receiverContact,
							ttl, lsMsg.getInitialTTL());

					node.getTransLayer().send(forwardMsg,
							forwardMsg.getReceiverContact().getTransInfo(),
							node.getTransInfo().getPort(),
							ChordConfiguration.TRANSPORT_PROTOCOL);
				}
			}

			// reply if message was forwarded at least 2 hops (successor of
			// successor of the sender)
			if (ttl <= lsMsg.getInitialTTL() - 2) {
				// this node is the "successorNumber"-th successor of the
				// sending (asking) node
				int successorNumber = lsMsg.getInitialTTL() - ttl;

				SuccessorReplyMessage reply = new SuccessorReplyMessage(
						lsMsg.getReceiverContact(),
						lsMsg.getVirtualNodeIdentifier(),
						lsMsg.getSenderContact(), successorNumber);

				node.getTransLayer().send(reply,
						reply.getReceiverContact().getTransInfo(),
						node.getTransInfo().getPort(),
						ChordConfiguration.TRANSPORT_PROTOCOL);
			}
		}

		// this message is received from the i-th successor (i >= 2)
		if (msg instanceof SuccessorReplyMessage) {
			SuccessorReplyMessage srMsg = (SuccessorReplyMessage) msg;

			// add to routing table
			if (srMsg.getVirtualNodeIdentifier() == node.getOverlayID()) {
				// add to real node
				((ChordRoutingTable) node.getChordRoutingTable())
						.updateSucSuccessor(srMsg.getSuccessorNumber(),
								srMsg.getSenderContact());
			}
			else {
				// add to virtual node
				LinkedList<ChordRoutingTable> vRoutingTables = (((ChordNode) node)
						.getVirtualNodes());
				for (ChordRoutingTable rt : vRoutingTables) {
					if (rt.getChordId() == srMsg.getVirtualNodeIdentifier()) {
						rt.updateSucSuccessor(srMsg.getSuccessorNumber(),
								srMsg.getSenderContact());
					}
				}
			}
		}
	}

	/**
	 * Method handle LookupMessage only
	 * 
	 * @param lookupMsg
	 */
	@Override
	public void handleLookupMsg(LookupMessage lookupMsg) {
		chordMessageHandlerLookup.handleLookupMsg(lookupMsg);
	}

	@Override
	protected void sendReply(Message reply, TransMsgEvent receivingEvent) {

		node.getTransLayer().sendReply(reply, receivingEvent, node.getPort(),
				ChordConfiguration.TRANSPORT_PROTOCOL);
	}

	/**
	 * send acknowledge message
	 * 
	 * @param receivingEvent
	 */
	@Override
	protected void sendAck(Message msg, TransMsgEvent receivingEvent,
			boolean look) {
		ChordMessage chordMsg = (ChordMessage) msg; // We wouldn't send an ack
													// for a non-chord message

		AckMessage ack = new AckMessage(chordMsg.getReceiverContact(),
				chordMsg.getSenderContact(), receivingEvent.getCommId(), look);
		node.getTransLayer().sendReply(ack, receivingEvent, node.getPort(),
				ChordConfiguration.TRANSPORT_PROTOCOL);
	}
}
