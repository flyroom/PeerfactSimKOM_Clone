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
package org.peerfact.impl.overlay.dht.chord.maliciouschord.operations;

import java.util.List;

import org.peerfact.api.common.OperationCallback;
import org.peerfact.impl.overlay.dht.chord.base.callbacks.MessageTimer;
import org.peerfact.impl.overlay.dht.chord.base.callbacks.OperationTimer;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordContact;
import org.peerfact.impl.overlay.dht.chord.base.components.ChordConfiguration;
import org.peerfact.impl.overlay.dht.chord.base.components.ChordID;
import org.peerfact.impl.overlay.dht.chord.base.messages.LookupMessage;
import org.peerfact.impl.overlay.dht.chord.chord.components.ChordRoutingTable;
import org.peerfact.impl.overlay.dht.chord.maliciouschord.components.MaliciousChordNode;
import org.peerfact.impl.simengine.Simulator;


/**
 * @author Tobias Wybranietz
 * @author Thim Strothmann
 */
public class MaliciousLookupOperationWrongForwardingToMaliciousOnly
		extends AbstractMaliciousLookupOperation {

	/**
	 * Constructor
	 * 
	 * @param maliciousChordNode
	 * @param key
	 * @param callback
	 * @param lookupId
	 */
	public MaliciousLookupOperationWrongForwardingToMaliciousOnly(
			MaliciousChordNode maliciousChordNode, ChordID key,
			OperationCallback<List<AbstractChordContact>> callback, int lookupId) {
		super(maliciousChordNode, key, callback, lookupId);
	}

	@Override
	protected void execute() {

		if (super.masterNode.isPresent()) {
			// scheduleOperationTimeout(ChordConfiguration.LOOKUP_TIMEOUT);

			// register in LookupStore
			log.debug("start lookup id = " + lookupId + " redo = "
					+ redoCounter);

			// Start Operation Timer
			OperationTimer operationTime = new OperationTimer(this);
			operationTime.schedule(ChordConfiguration.OPERATION_TIMEOUT);

			ChordRoutingTable routingTable = (ChordRoutingTable) masterNode
					.getChordRoutingTable();

			// Get minimum finger that succeeds the id
			AbstractChordContact succeedingFinger = getClosestSucceedingFinger(
					routingTable, target);

			// // DEBUGGING LOGGING
			// log.warn("Node " +
			// masterNode.getLocalChordContact().getOverlayID()
			// +" has to find succeeding finger for " + target + ":");
			// if (succeedingFinger == null) {
			// log.warn("   No result");
			// }
			// Set<AbstractChordContact> hist = new
			// HashSet<AbstractChordContact>();
			// for (int n = 0 ; n < routingTable.getFingerCount(); n++) {
			// if (!hist.contains(routingTable.getFingerEntry(n))) {
			// String T = "";
			// if (routingTable.getFingerEntry(n).equals(succeedingFinger)) {
			// T = "  <-------";
			// }
			// log.warn("   * " + routingTable.getFingerEntry(n).getOverlayID()
			// + T);
			// hist.add(routingTable.getFingerEntry(n));
			// }
			// }

			// if no finger succeeds the id
			if (succeedingFinger == null) {
				// forward to direct predecessor

				AbstractChordContact pre = routingTable.getPredecessor();

				LookupMessage msg = new LookupMessage(
						masterNode.getLocalOverlayContact(),
						routingTable.getPredecessor(), target, lookupId, 0);

				MessageTimer msgTimer = new MessageTimer(masterNode, msg,
						pre);
				masterNode.getTransLayer().sendAndWait(msg,
						pre.getTransInfo(), masterNode.getPort(),
						ChordConfiguration.TRANSPORT_PROTOCOL, msgTimer,
						ChordConfiguration.MESSAGE_TIMEOUT);

			} else {
				// forward to the found succeeding finger

				LookupMessage msg = new LookupMessage(
						masterNode.getLocalOverlayContact(), succeedingFinger,
						target, lookupId, 0);

				MessageTimer msgTimer = new MessageTimer(masterNode, msg,
						succeedingFinger);
				masterNode.getTransLayer().sendAndWait(msg,
						succeedingFinger.getTransInfo(),
						masterNode.getPort(),
						ChordConfiguration.TRANSPORT_PROTOCOL, msgTimer,
						ChordConfiguration.MESSAGE_TIMEOUT);
			}

		} else {
			// Notify Monitor
			Simulator.getMonitor().dhtLookupFailed(this.responsibleContact,
					this.target.getCorrespondingKey());
			masterNode.removeLookupOperation(getLookupId());
			operationFinished(false);
		}

	}

	public static AbstractChordContact getClosestSucceedingFinger(
			ChordRoutingTable routingTable, ChordID target) {

		// for (int index = 0; index <= routingTable.bitLength - 1; index++) {
		// if
		// (target.between(masterNode.getOverlayID(),routingTable.finger[index].getOverlayID())
		// && (!target.equals(routingTable.finger[index].getOverlayID()))) {
		// return routingTable.finger[index];
		// }
		// }
		AbstractChordContact currentMinimalSuccessor = null;
		for (int index = 0; index <= routingTable.getBitLength() - 1; index++) {
			if (target
					.compareTo(routingTable.getFinger()[index].getOverlayID()) < 0
					&& (currentMinimalSuccessor == null || currentMinimalSuccessor
							.getOverlayID().compareTo(
									routingTable.getFinger()[index]
											.getOverlayID()) > 0)) {
				currentMinimalSuccessor = routingTable.getFinger()[index];
			}
		}
		return currentMinimalSuccessor;

	}

}
