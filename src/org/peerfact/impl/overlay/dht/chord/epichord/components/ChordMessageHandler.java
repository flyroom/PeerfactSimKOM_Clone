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

package org.peerfact.impl.overlay.dht.chord.epichord.components;

import java.util.Set;

import org.apache.log4j.Logger;
import org.peerfact.api.common.Message;
import org.peerfact.api.overlay.dht.DHTObject;
import org.peerfact.impl.overlay.dht.chord.base.callbacks.HandshakeCallback;
import org.peerfact.impl.overlay.dht.chord.base.callbacks.MessageTimer;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordContact;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordMessageHandler;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordNode;
import org.peerfact.impl.overlay.dht.chord.base.components.ChordConfiguration;
import org.peerfact.impl.overlay.dht.chord.base.components.ChordID;
import org.peerfact.impl.overlay.dht.chord.base.messages.AbstractRequestMessage;
import org.peerfact.impl.overlay.dht.chord.base.messages.AckMessage;
import org.peerfact.impl.overlay.dht.chord.base.messages.ChordMessage;
import org.peerfact.impl.overlay.dht.chord.base.messages.HandshakeMsg;
import org.peerfact.impl.overlay.dht.chord.base.messages.InitializeDownloadMessage;
import org.peerfact.impl.overlay.dht.chord.base.messages.InitializeDownloadReplyMessage;
import org.peerfact.impl.overlay.dht.chord.base.messages.JoinMessage;
import org.peerfact.impl.overlay.dht.chord.base.messages.LeaveMessage;
import org.peerfact.impl.overlay.dht.chord.base.messages.LookupMessage;
import org.peerfact.impl.overlay.dht.chord.base.messages.LookupReply;
import org.peerfact.impl.overlay.dht.chord.base.messages.NotifyOfflineMsg;
import org.peerfact.impl.overlay.dht.chord.base.messages.NotifyPredecessorMsg;
import org.peerfact.impl.overlay.dht.chord.base.messages.NotifySuccessorMsg;
import org.peerfact.impl.overlay.dht.chord.base.messages.RetrievePredecessorMsg;
import org.peerfact.impl.overlay.dht.chord.base.messages.RetrievePredecessorReply;
import org.peerfact.impl.overlay.dht.chord.base.messages.RetrieveSuccessorMsg;
import org.peerfact.impl.overlay.dht.chord.base.messages.RetrieveSuccessorReply;
import org.peerfact.impl.overlay.dht.chord.base.messages.StoreMessage;
import org.peerfact.impl.overlay.dht.chord.base.messages.StoreReplyMessage;
import org.peerfact.impl.overlay.dht.chord.base.messages.ValueLookupMessage;
import org.peerfact.impl.overlay.dht.chord.base.messages.ValueLookupReplyMessage;
import org.peerfact.impl.overlay.dht.chord.base.operations.AbstractChordOperation;
import org.peerfact.impl.overlay.dht.chord.base.util.RoutingTableContructor;
import org.peerfact.impl.overlay.dht.chord.epichord.messages.HandshakeReply;
import org.peerfact.impl.overlay.dht.chord.epichord.messages.LookupInfoReply;
import org.peerfact.impl.overlay.dht.chord.epichord.operations.EpiLookupOperation;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.transport.TransMsgEvent;
import org.peerfact.impl.util.logging.SimLogger;


/**
 * ChordMessageHandler handle incoming Overlay Messages.
 * 
 * @author Minh Hoang Nguyen <peerfact@kom.tu-darmstadt.de>
 * @author Philip Wette <info@peerfact.org>
 * @author Thim Strothmann (adaptions)
 * 
 * @version 18/08/2011
 */
public class ChordMessageHandler extends AbstractChordMessageHandler {

	private static Logger log = SimLogger.getLogger(AbstractChordNode.class);

	private final AbstractChordNode node;

	// count number of lookup was dropped cause of exceedance MAX_HOP_COUNT
	private static int dropLookupcount;

	public ChordMessageHandler(AbstractChordNode node) {
		this.node = node;
	}

	@Override
	public void messageArrived(TransMsgEvent receivingEvent) {

		if (!node.isPresent()) {
			return;
		}
		Message msg = receivingEvent.getPayload();
		log.debug("node " + node + " receive msg " + msg + " at "
				+ Simulator.getCurrentTime() / Simulator.SECOND_UNIT);
		ChordRoutingTable routingTable = (ChordRoutingTable) node
				.getChordRoutingTable();

		// EpiChord: update cache with sender information for all incoming
		// messages
		// only JoinMessage senders are not saved because they are not yet in
		// the
		// network and should not be used at the moment (these contacts were
		// added
		// later by other message types)
		if (msg instanceof ChordMessage) {
			if (!(msg instanceof JoinMessage)) {
				routingTable.getChordCache().update(
						((ChordMessage) msg).getSenderContact());
			}
		}

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
						log.debug("drop msg = " + chordMsg);
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
			node.overlayNodeLookup(((JoinMessage) msg).getSender(),
					new HandshakeCallback(joinMessage.getSenderContact(), node,
							receivingEvent));
		}

		else if (msg instanceof HandshakeMsg) {
			Set<AbstractChordContact> routingContacts = RoutingTableContructor
					.getDistinctContactList(routingTable.copyFingerTable());
			HandshakeReply reply = new HandshakeReply(
					((HandshakeMsg) msg).getReceiverContact(),
					((HandshakeMsg) msg).getSenderContact(),
					routingTable.getPredecessor(), routingContacts,
					routingTable.getChordCache());
			sendReply(reply, receivingEvent);
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

		else if (msg instanceof NotifyPredecessorMsg) {
			NotifyPredecessorMsg notifyPredecessorMsg = (NotifyPredecessorMsg) msg;
			routingTable.updatePredecessor(notifyPredecessorMsg
					.getPredecessor());
			sendAck(msg, receivingEvent, false);
		}

		else if (msg instanceof NotifySuccessorMsg) {
			NotifySuccessorMsg notifySuccessorMsg = (NotifySuccessorMsg) msg;
			routingTable.updateSuccessor(notifySuccessorMsg.getSuccessor());
			sendAck(msg, receivingEvent, false);
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

		else if (msg instanceof LookupInfoReply) {
			LookupInfoReply reply = (LookupInfoReply) msg;

			// EpiChord: update cache with all received contacts
			for (AbstractChordContact contact : reply.getBetterContacts()) {
				routingTable.getChordCache().update(contact);
			}

			// forward lookup message
			int lookupId = reply.getRequest().getLookupID();
			AbstractChordOperation<?> op = node.getLookupOperation(lookupId);
			if (op != null && op instanceof EpiLookupOperation) {
				EpiLookupOperation lookupOp = (EpiLookupOperation) op;
				if (!lookupOp.isFinished()) {

					// get best p contacts from cache
					AbstractChordContact[] nextContacts = routingTable
							.getChordCache().lookup(
									reply.getRequest().getTarget(),
									EpiChordConfiguration.P);

					for (AbstractChordContact nextHop : nextContacts) {
						// send parallel to p best contacts which have not yet
						// been used for this lookup operation
						if (lookupOp.addContact(nextHop)) {
							if (node.getOverlayID().compareTo(
									nextHop.getOverlayID()) != 0) {

								LookupMessage forwardMsg = new LookupMessage(
										reply.getRequest().getSenderContact(),
										nextHop,
										reply.getRequest().getTarget(),
										reply.getRequest().getLookupID(), reply
												.getRequest().getHopCount());
								MessageTimer msgTimer = new MessageTimer(node,
										forwardMsg,
										nextHop);
								node.getTransLayer().sendAndWait(forwardMsg,
										nextHop.getTransInfo(), node.getPort(),
										ChordConfiguration.TRANSPORT_PROTOCOL,
										msgTimer,
										ChordConfiguration.MESSAGE_TIMEOUT);
							}
						}
					}
				}
			} else {
				log.debug("lookup operation not found -> duplicated answer to lookup - receiver = "
						+ node.getOverlayID() + " lookupId = " + lookupId);
			}

			sendAck(msg, receivingEvent, false);

		}

		else if (msg instanceof LookupReply) {

			LookupReply reply = (LookupReply) msg;
			int lookupId = reply.getRequest().getLookupID();
			AbstractChordOperation<?> op = node.getLookupOperation(lookupId);
			if (op != null && op instanceof EpiLookupOperation) {
				EpiLookupOperation lookupOp = (EpiLookupOperation) op;
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

			ValueLookupReplyMessage reply = new ValueLookupReplyMessage(
					((ValueLookupMessage) msg).getReceiverContact(),
					((ValueLookupMessage) msg).getSenderContact(), object);
			sendReply(reply, receivingEvent);
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

	}

	/**
	 * Method handle LookupMessage only
	 * 
	 * @param lookupMsg
	 */
	@Override
	public void handleLookupMsg(LookupMessage lookupMsg) {

		ChordRoutingTable routingTable = (ChordRoutingTable) node
				.getChordRoutingTable();
		ChordID target = lookupMsg.getTarget();

		if (routingTable.responsibleFor(target)
				|| node.getDHT().getDHTEntry(target.getCorrespondingKey()) != null) {
			// the node itself is responsible for the key. Additionally, if it
			// stores information on the key (due to content replication) it
			// will answer

			LookupReply reply = new LookupReply(lookupMsg.getReceiverContact(),
					lookupMsg.getSenderContact(),
					node.getLocalOverlayContact(),
					lookupMsg);
			log.debug("send lookup reply " + reply + "receiver "
					+ lookupMsg.getSenderContact());

			MessageTimer msgTimer = new MessageTimer(node, reply,
					lookupMsg.getSenderContact());
			node.getTransLayer().sendAndWait(reply,
					lookupMsg.getSenderContact().getTransInfo(),
					node.getPort(), ChordConfiguration.TRANSPORT_PROTOCOL,
					msgTimer, ChordConfiguration.MESSAGE_TIMEOUT);

		} else {
			// forward message
			log.debug("forward lookup");
			// notify Analyzer about this:

			Simulator.getMonitor().dhtLookupForwarded(
					lookupMsg.getSenderContact(),
					lookupMsg.getTarget().getCorrespondingKey(),
					node.getLocalOverlayContact(), lookupMsg.getHopCount());

			// get best l contacts from cache and send to lookup sender
			AbstractChordContact[] betterContacts = routingTable
					.getChordCache().lookup(target, EpiChordConfiguration.L);

			LookupInfoReply reply = new LookupInfoReply(
					lookupMsg.getReceiverContact(),
					lookupMsg.getSenderContact(), betterContacts,
					lookupMsg);

			MessageTimer msgTimer = new MessageTimer(node, reply,
					lookupMsg.getSenderContact());
			node.getTransLayer().sendAndWait(reply,
					lookupMsg.getSenderContact().getTransInfo(),
					node.getPort(), ChordConfiguration.TRANSPORT_PROTOCOL,
					msgTimer, ChordConfiguration.MESSAGE_TIMEOUT);

		}
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
