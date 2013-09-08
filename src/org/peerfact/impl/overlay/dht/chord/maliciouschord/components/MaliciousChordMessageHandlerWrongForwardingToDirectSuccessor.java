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

/**
 * 
 */
package org.peerfact.impl.overlay.dht.chord.maliciouschord.components;

import org.peerfact.impl.overlay.dht.chord.base.callbacks.MessageTimer;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordContact;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordNode;
import org.peerfact.impl.overlay.dht.chord.base.components.ChordConfiguration;
import org.peerfact.impl.overlay.dht.chord.base.components.ChordID;
import org.peerfact.impl.overlay.dht.chord.base.messages.LookupMessage;
import org.peerfact.impl.overlay.dht.chord.base.messages.LookupReply;
import org.peerfact.impl.overlay.dht.chord.chord.components.ChordRoutingTable;
import org.peerfact.impl.simengine.Simulator;

/**
 * @author Tobias Wybranietz
 * @author Thim Strothmann
 */
public class MaliciousChordMessageHandlerWrongForwardingToDirectSuccessor
		extends AbstractMaliciousChordMessageHandler {

	/**
	 * @param node
	 */
	public MaliciousChordMessageHandlerWrongForwardingToDirectSuccessor(
			AbstractChordNode node, int leadtime) {
		super(node, leadtime);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Method handle LookupMessage only
	 * 
	 * @param lookupMsg
	 */
	@Override
	public void doMaliciousHandleLookupMsg(LookupMessage lookupMsg) {

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

			// Get minimum finger that succeeds the id
			AbstractChordContact succeedingFinger = routingTable.getSuccessor();

			// if no finger succeedes the id
			if (succeedingFinger == null) {

				log.trace("next successor is responder succ = "
						+ routingTable.getSuccessor());

				AbstractChordContact nextHop = routingTable.getPredecessor();

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
				// forward to the found succeeding finger

				LookupMessage forwardMsg = new LookupMessage(
						lookupMsg.getSenderContact(), succeedingFinger, target,
						lookupMsg.getLookupID(), lookupMsg.getHopCount());

				if (node.getOverlayID().compareTo(
						succeedingFinger.getOverlayID()) != 0) {

					MessageTimer msgTimer = new MessageTimer(node, forwardMsg,
							succeedingFinger);

					node.getTransLayer().sendAndWait(forwardMsg,
							succeedingFinger.getTransInfo(), node.getPort(),
							ChordConfiguration.TRANSPORT_PROTOCOL, msgTimer,
							ChordConfiguration.MESSAGE_TIMEOUT);
				}
			}
		}

	}

}
