/*
 * Copyright (c) 2005-2010 KOM – Multimedia Communications Lab
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

package org.peerfact.impl.overlay.dht.chord.carechord.components;

import java.util.LinkedHashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.peerfact.api.common.Message;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.api.transport.TransMessageCallback;
import org.peerfact.impl.overlay.dht.chord.base.callbacks.MessageTimer;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordContact;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordNode;
import org.peerfact.impl.overlay.dht.chord.base.components.ChordConfiguration;
import org.peerfact.impl.overlay.dht.chord.base.components.ChordID;
import org.peerfact.impl.overlay.dht.chord.base.messages.LookupMessage;
import org.peerfact.impl.overlay.dht.chord.base.messages.LookupReply;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.util.logging.SimLogger;


/**
 * This class is the message handler for LookupMessage messages If a node is
 * offline (recognized by timeout), the message is forwarded to the successor of
 * the successor etc.
 * 
 * @author Markus Benter (original author)
 * 
 * @author Thim Strothmann (Adaptions)
 */
public class ChordMessageHandlerLookup implements TransMessageCallback {

	private static Logger log = SimLogger
			.getLogger(ChordMessageHandlerLookup.class);

	private final AbstractChordNode node;

	// stores the messages which are still not delivered
	private LinkedHashMap<Integer, LookupMessage> waitForReply;

	// the number of retries for each LookupMessage which is still not delivered
	private LinkedHashMap<Integer, Integer> retryCount = new LinkedHashMap<Integer, Integer>();

	// number of packets which have tried all possible successors/fingers (for
	// debug and evaluation purpose)
	private static int retryExceeded = 0;

	/**
	 * 
	 * @param node
	 */
	public ChordMessageHandlerLookup(AbstractChordNode node) {
		this.node = node;
		waitForReply = new LinkedHashMap<Integer, LookupMessage>();
	}

	public void handleLookupMsg(LookupMessage lookupMsg) {
		handleLookupMsg(lookupMsg, false);
	}

	/**
	 * Method handle LookupMessage only
	 * 
	 * @param lookupMsg
	 */
	private void handleLookupMsg(LookupMessage lookupMsg, boolean ignoreFirst) {
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

			// notify Analyzer about this:

			Simulator.getMonitor().dhtLookupForwarded(
					lookupMsg.getSenderContact(),
					lookupMsg.getTarget().getCorrespondingKey(),
					node.getLocalOverlayContact(), lookupMsg.getHopCount());

			// Get maximum finger that precedes the id
			AbstractChordContact precedingFinger;

			if (retryCount.containsKey(lookupMsg.getLookupID())) {
				// special case: message timeout before, try to use the
				// successor of the successor
				// (or the next preceding finger if all k successors are
				// offline)
				List<AbstractChordContact> precedingFingers = routingTable
						.getClosestPrecedingFingers(target, Integer.MAX_VALUE,
								true);
				if (precedingFingers.size() > retryCount.get(lookupMsg
						.getLookupID())) {
					// this is the next hop
					precedingFinger = precedingFingers.get(retryCount
							.get(lookupMsg.getLookupID()));
				}
				else {
					// all possible next hops already tried and offline, message
					// lost
					retryExceeded++;
					log.debug("Retrys EXCEEDED " + lookupMsg + "("
							+ retryExceeded + ")");
					return;
				}
			}
			else {
				// normal case: find the preceding finger as usual
				precedingFinger = routingTable.getClosestPrecedingFingers(
						target, 1, true).get(0);
			}

			log.debug("forward lookup receiver = " + precedingFinger);

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
			}
			else {
				// forward to the found preceding finger
				LookupMessage forwardMsg = new LookupMessage(
						lookupMsg.getSenderContact(), precedingFinger, target,
						lookupMsg.getLookupID(), lookupMsg.getHopCount());

				if (node.getOverlayID().compareTo(
						precedingFinger.getOverlayID()) != 0) {

					int replyId = node.getTransLayer().sendAndWait(forwardMsg,
							precedingFinger.getTransInfo(), node.getPort(),
							ChordConfiguration.TRANSPORT_PROTOCOL, this,
							ChordConfiguration.MESSAGE_TIMEOUT);

					waitForReply.put(replyId, lookupMsg);
				}
			}
		}
	}

	@Override
	/**
	 * LookupMessage send successful
	 */
	public void receive(Message msg, TransInfo senderInfo, int commId) {
		// release memory
		waitForReply.remove(commId);

		if (msg instanceof LookupMessage) {
			retryCount.remove(((LookupMessage) msg).getLookupID());
		}
	}

	@Override
	/**
	 * LookupMessage timeout, assume that node is offline
	 */
	public void messageTimeoutOccured(int commId) {
		LookupMessage msg = waitForReply.get(commId);
		log.debug("Message TIMEOUT " + msg);

		// store retry count for the current message
		if (retryCount.containsKey(msg.getLookupID())) {
			int newRetryCount = retryCount.remove(msg.getLookupID());
			newRetryCount++;
			retryCount.put(msg.getLookupID(), newRetryCount);
		}
		else {
			retryCount.put(msg.getLookupID(), 1);
		}

		// retry to send the message
		handleLookupMsg(msg, true);
	}

}
