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
import org.peerfact.impl.overlay.dht.chord.base.callbacks.OperationTimer;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordContact;
import org.peerfact.impl.overlay.dht.chord.base.components.ChordConfiguration;
import org.peerfact.impl.overlay.dht.chord.base.components.ChordID;
import org.peerfact.impl.overlay.dht.chord.maliciouschord.components.MaliciousChordNode;
import org.peerfact.impl.simengine.Simulator;


/**
 * @author Tobias Wybranietz
 * @author Thim Strothmann
 */
public class MaliciousLookupOperationNoForwarding extends
		AbstractMaliciousLookupOperation {

	/**
	 * Constructor
	 * 
	 * @param maliciousChordNode
	 * @param key
	 * @param callback
	 * @param lookupId
	 */
	public MaliciousLookupOperationNoForwarding(
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

			// AbstractChordRoutingTable routingTable =
			// masterNode.getChordRoutingTable();

			// if (routingTable.responsibleFor(target)) {
			// // itself is responsible for the key
			//
			// // I am responsible for key, but I just do nothing maliciously.
			//
			// } else {
			//
			// // I should forward the request. But I just do nothing
			// maliciously.
			//
			// }

		} else {
			// Notify Monitor
			Simulator.getMonitor().dhtLookupFailed(this.responsibleContact,
					this.target.getCorrespondingKey());
			masterNode.removeLookupOperation(getLookupId());
			operationFinished(false);
		}

	}

}
