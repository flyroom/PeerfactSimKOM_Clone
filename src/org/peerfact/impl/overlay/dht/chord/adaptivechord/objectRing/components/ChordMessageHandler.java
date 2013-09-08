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

package org.peerfact.impl.overlay.dht.chord.adaptivechord.objectRing.components;

import java.util.Set;

import org.apache.log4j.Logger;
import org.peerfact.api.common.Message;
import org.peerfact.api.overlay.dht.DHTObject;
import org.peerfact.impl.overlay.dht.chord.adaptivechord.objectRing.messages.CheckMirrorMessage;
import org.peerfact.impl.overlay.dht.chord.adaptivechord.objectRing.messages.CheckMirrorReplyMessage;
import org.peerfact.impl.overlay.dht.chord.adaptivechord.objectRing.messages.CreateMirrorMessage;
import org.peerfact.impl.overlay.dht.chord.adaptivechord.objectRing.messages.CreateMirrorReplyMessage;
import org.peerfact.impl.overlay.dht.chord.adaptivechord.objectRing.messages.RemovedMirrorMessage;
import org.peerfact.impl.overlay.dht.chord.base.callbacks.HandshakeCallback;
import org.peerfact.impl.overlay.dht.chord.base.callbacks.MessageTimer;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordContact;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordMessageHandler;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordRoutingTable;
import org.peerfact.impl.overlay.dht.chord.base.components.ChordID;
import org.peerfact.impl.overlay.dht.chord.base.components.ChordKey;
import org.peerfact.impl.overlay.dht.chord.base.messages.AbstractRequestMessage;
import org.peerfact.impl.overlay.dht.chord.base.messages.AckMessage;
import org.peerfact.impl.overlay.dht.chord.base.messages.ChordMessage;
import org.peerfact.impl.overlay.dht.chord.base.messages.HandshakeMsg;
import org.peerfact.impl.overlay.dht.chord.base.messages.HandshakeReply;
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
import org.peerfact.impl.overlay.dht.chord.base.operations.LookupOperation;
import org.peerfact.impl.overlay.dht.chord.base.operations.TransferDocumentOperation;
import org.peerfact.impl.overlay.dht.chord.base.util.RoutingTableContructor;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.transport.TransMsgEvent;
import org.peerfact.impl.util.logging.SimLogger;


/**
 * ChordMessageHandler handle incoming Overlay Messages.
 * 
 * @author Minh Hoang Nguyen <peerfact@kom.tu-darmstadt.de>
 * @author Thim Strothmann (adaptions)
 * 
 * @version 05/06/2011
 */
public class ChordMessageHandler extends AbstractChordMessageHandler {

	private static Logger log = SimLogger.getLogger(ChordNode.class);

	private final ChordNode node;

	// count number of lookup was dropped cause of exceedance MAX_HOP_COUNT
	private static int dropLookupcount;

	public ChordMessageHandler(ChordNode node) {
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
		AbstractChordRoutingTable routingTable = node.getChordRoutingTable();

		// increase hop count
		if (msg instanceof AbstractRequestMessage) {

			AbstractRequestMessage chordMsg = (AbstractRequestMessage) msg;

			// drop message if not directly addressed to us:
			if (!chordMsg.getReceiverContact().getOverlayID()
					.equals(this.node.getOverlayID())) {
				return;
			}

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
					routingTable.getPredecessor(), routingContacts);
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
			((ChordRoutingTable) routingTable)
					.updatePredecessor(notifyPredecessorMsg
							.getPredecessor());
			sendAck(msg, receivingEvent, false);
		}

		else if (msg instanceof NotifySuccessorMsg) {
			NotifySuccessorMsg notifySuccessorMsg = (NotifySuccessorMsg) msg;
			((ChordRoutingTable) routingTable)
					.updateSuccessor(notifySuccessorMsg.getSuccessor());
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
					storeMsg.getKey().getCorrespondingKey()) == null
					&&
					((ChordNode) node.zhNode.getDataNetNode())
							.getMirroredObject(storeMsg.getKey()
									.getCorrespondingKey()) == null) {

				node.getDHT().addDHTEntry(
						storeMsg.getKey().getCorrespondingKey(),
						storeMsg.getObject());

				node.initLoadMap(storeMsg.getKey().getCorrespondingKey());

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

			if (object == null) {
				// check if the document is in the mirrored object list:
				object = node.getMirroredObject(lookupMsg.getTargetKey()
						.getCorrespondingKey());
				log.debug(node + ": answering a query for document "
						+ lookupMsg.getTargetKey().getCorrespondingKey()
						+ " for that i own a mirror!!!!!!");

				// notify Analyzer about this:
				if (object != null) {
					Simulator.getMonitor().dhtMirroredDocumentServed(
							node.getLocalOverlayContact(), object, true);
				}

			} else {
				// log.debug(node + ": answering a query for document "
				// + lookupMsg.getTargetKey().getCorespondingKey() +
				// "that belongs to me. size: " + object.getTransmissionSize() +
				// " my free: " + node.DEBUG_getFreeBandwidth() + " counter: " +
				// node.DEBUG_getServedCounter(lookupMsg.getTargetKey().getCorespondingKey()
				// ));
				log.debug(node + ": answering a query for document "
						+ lookupMsg.getTargetKey().getCorrespondingKey()
						+ "that belongs to me......");

				// notify Analyzer about this:
				Simulator.getMonitor().dhtOwnDocumentServed(
						node.getLocalOverlayContact(), object, true);
			}

			if (object != null)
			{
				node.documentAccessed(lookupMsg.getTargetKey()
						.getCorrespondingKey()); // ZH Chord Call
			}

			ValueLookupReplyMessage reply = new ValueLookupReplyMessage(
					((ValueLookupMessage) msg).getReceiverContact(),
					((ValueLookupMessage) msg).getSenderContact(), object);

			node.getTransLayer().sendReply(reply, receivingEvent,
					node.getPort(),
					ChordConfiguration.TRANSPORT_PROTOCOL);

		}

		// mirroring messages:
		if (msg instanceof CreateMirrorMessage) {
			CreateMirrorMessage ccm = (CreateMirrorMessage) msg;
			// check if we have positive load available:
			boolean success = false;
			try {
				success = !(((ChordNode) node.zhNode.getDataNetNode())
						.isOverloaded());
			} catch (Exception e) {
				log.debug(e.getMessage());
			}
			if (success) {
				// check if not too much elements on this node:
				if (((ChordNode) node.zhNode.getDataNetNode())
						.getNumMirroredObjects() >= ChordConfiguration.MAX_MIRROR_COUNT) {
					success = false;
				}
			}

			if (success) {
				((ChordNode) node.zhNode.getDataNetNode())
						.addDocumentToThisMirror(ccm.getKey(),
								ccm.getObject(), ccm.getSenderContact());

				// start Download of the object.
				TransferDocumentOperation tr = new TransferDocumentOperation(
						null, this.node, this.node.getLocalOverlayContact(),
						ccm.getSenderContact(), ccm.getKey());
				tr.scheduleImmediately();

				log.debug(node.zhNode.getDataNetNode()
						+ ": mirroring a document from " + ccm.getSender());
			} else {
				log.debug(node.zhNode.getDataNetNode()
						+ ": not mirroring a document from "
						+ ccm.getSender()
						+ " cause of overloaded. free bandwidth: "
						+ ((ChordNode) (this.node.zhNode.getDataNetNode()))
								.DEBUG_getFreeBandwidth());

				// //as i am overloaded is should renew my loadbalancingnet id:
				// //but only if a have in id that is high enough.
				// BigInteger i = ((ChordNode)
				// node.zhNode.getLoadbalancingNetNode()).getOverlayID().getUniqueValue();
				// if( i.compareTo( new BigInteger(
				// ChordConfiguration.RANDOM_TIE_BREAKER_SIZE.toString() ) ) <
				// 0) {
				// ChordID newId = ((ChordNode)
				// node.zhNode.getLoadbalancingNetNode()).getChordIdCorrespondingToRemainingLoad();
				// ((ChordNode)
				// node.zhNode.getLoadbalancingNetNode()).ChangeIdentTo( newId
				// );
				// }
			}

			// reply in the name of the dataNetNode:
			CreateMirrorReplyMessage reply = new CreateMirrorReplyMessage(
					((ChordNode) node.zhNode.getDataNetNode()).getLocalOverlayContact(),
					ccm.getSenderContact(),
					success);
			sendReply(reply, receivingEvent);
		}

		if (msg instanceof CheckMirrorMessage) {
			CheckMirrorMessage checkMsg = (CheckMirrorMessage) msg;

			boolean success = node
					.checkIfMirrorIsPresentAndUpdateLastContact((ChordKey) checkMsg
							.getDocumentId());

			// reply:
			CheckMirrorReplyMessage reply = new CheckMirrorReplyMessage(
					checkMsg.getReceiverContact(),
					checkMsg.getSenderContact(),
					checkMsg.getDocumentId(),
					success);
			sendReply(reply, receivingEvent);

		}
		if (msg instanceof RemovedMirrorMessage) {
			RemovedMirrorMessage m = (RemovedMirrorMessage) msg;
			ChordNode data = (ChordNode) node.zhNode.getDataNetNode();
			data.removeRedirection(m.getKey());

			RemovedMirrorMessage reply = new RemovedMirrorMessage(
					m.getSenderContact(), m.getReceiverContact(), null);
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

			// search mirrored object store for that document.
			if (object == null) {
				object = node
						.getMirroredObject(init.getDocumentId());
				if (object != null) {
					log.debug(node
							+ ": serving a download for the mirrored object "
							+ object);
				}
			}

			if (object != null) {
				if (objectSize == 0) {
					objectSize = object.getTransmissionSize();
				}
				// reply:
				InitializeDownloadReplyMessage rep =
						new InitializeDownloadReplyMessage(
								init.getReceiverContact(),
								init.getSenderContact(),
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

		AbstractChordRoutingTable routingTable = node.getChordRoutingTable();
		ChordID target = lookupMsg.getTarget();

		if (routingTable.responsibleFor(target)
		// || node.getDHT().getDHTEntry(target.getCorespondingKey()) != null
		) {
			// the node itself is responsible for the key. Additionally, if it
			// stores information on the key (due to content replication) it
			// will answer

			// Adaptive-Chord: check if he have a mirror for this document ->
			// redirect!
			ChordContact responsibleForDocument = node.getLocalOverlayContact();
			if (node.getMirrorForObject(target.getCorrespondingKey()) != null) {
				responsibleForDocument = (ChordContact) node
						.getMirrorForObject(target.getCorrespondingKey());
				node.documentAccessed(target.getCorrespondingKey()); // for
				// statictics
				log.debug(node + ": redirecting query for document "
						+ target + " to mirror " + responsibleForDocument);
			}

			LookupReply reply = new LookupReply(lookupMsg.getReceiverContact(),
					lookupMsg.getSenderContact(), responsibleForDocument,
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

			// Get maximum finger that precedes the id
			AbstractChordContact precedingFinger = routingTable
					.getClosestPrecedingFinger(target);

			// if no finger precedes the id
			if (precedingFinger.equals(node.getLocalOverlayContact())) {
				/*
				 * This is the case if the key lies between this node's id and
				 * its direct successor. That means the message is delivered to
				 * the direct successor, otherwise it is forwarded to the
				 * closest preceding node.
				 */

				log.trace("next successor is responder succ = "
						+ routingTable.getSuccessor());

				AbstractChordContact nextHop = routingTable.getSuccessor();

				LookupMessage forwardMsg = new LookupMessage(
						lookupMsg.getSenderContact(), nextHop, target,
						lookupMsg.getLookupID(), lookupMsg.getHopCount());

				if (node.getOverlayID().compareTo(nextHop.getOverlayID()) != 0) {

					MessageTimer msgTimer = new MessageTimer(node, forwardMsg,
							nextHop);

					node.getTransLayer().sendAndWait(forwardMsg,
							nextHop.getTransInfo(), node.getPort(),
							ChordConfiguration.TRANSPORT_PROTOCOL, msgTimer,
							ChordConfiguration.MESSAGE_TIMEOUT);
				}
			} else {
				// forward to the found preceding finger

				LookupMessage forwardMsg = new LookupMessage(
						lookupMsg.getSenderContact(), precedingFinger, target,
						lookupMsg.getLookupID(), lookupMsg.getHopCount());

				if (node.getOverlayID().compareTo(
						precedingFinger.getOverlayID()) != 0) {

					MessageTimer msgTimer = new MessageTimer(node, forwardMsg,
							precedingFinger);

					node.getTransLayer().sendAndWait(forwardMsg,
							precedingFinger.getTransInfo(), node.getPort(),
							ChordConfiguration.TRANSPORT_PROTOCOL, msgTimer,
							ChordConfiguration.MESSAGE_TIMEOUT);
				}
			}
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
