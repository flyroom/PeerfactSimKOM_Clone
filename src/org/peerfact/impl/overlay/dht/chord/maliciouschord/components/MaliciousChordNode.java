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

import java.util.List;

import org.apache.log4j.Logger;
import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.transport.TransLayer;
import org.peerfact.impl.overlay.dht.chord.base.callbacks.HandshakeCallback;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordContact;
import org.peerfact.impl.overlay.dht.chord.base.components.ChordBootstrapManager;
import org.peerfact.impl.overlay.dht.chord.base.components.ChordID;
import org.peerfact.impl.overlay.dht.chord.base.operations.LookupOperation;
import org.peerfact.impl.overlay.dht.chord.chord.components.ChordNode;
import org.peerfact.impl.overlay.dht.chord.maliciouschord.operations.EMaliciousLookupOperations;
import org.peerfact.impl.overlay.dht.chord.maliciouschord.operations.MaliciousLookupOperationNoForwarding;
import org.peerfact.impl.overlay.dht.chord.maliciouschord.operations.MaliciousLookupOperationWrongForwardingToClosestFingertableSuccessor;
import org.peerfact.impl.overlay.dht.chord.maliciouschord.operations.MaliciousLookupOperationWrongForwardingToDirectPredecessor;
import org.peerfact.impl.overlay.dht.chord.maliciouschord.operations.MaliciousLookupOperationWrongForwardingToDirectSuccessor;
import org.peerfact.impl.util.logging.SimLogger;


/**
 * @author Tobias Wybranietz
 * @author Thim Strothmann
 */
public class MaliciousChordNode extends ChordNode {

	/**
	 * Defines which malicious lookup operation should be used by all malicious
	 * nodes.
	 */
	private EMaliciousLookupOperations typeOfMaliciousLookupOperations;

	private static Logger log = SimLogger.getLogger(MaliciousChordNode.class);

	/**
	 * Constructor
	 * 
	 * @param transLayer
	 * @param port
	 * @param bootstrap
	 * @param typeOfMaliciousLookupOperations
	 */
	public MaliciousChordNode(TransLayer transLayer, short port,
			ChordBootstrapManager bootstrap,
			EMaliciousLookupOperations typeOfMaliciousLookupOperations,
			int leadTime) {

		super(transLayer, port, bootstrap, true);
		this.typeOfMaliciousLookupOperations = typeOfMaliciousLookupOperations;

		// Create malicious message handler
		if (typeOfMaliciousLookupOperations == EMaliciousLookupOperations.MaliciousLookupOperationNoForwarding) {
			messageHandler = new MaliciousChordMessageHandlerNoForwarding(this,
					leadTime);
		} else if (typeOfMaliciousLookupOperations == EMaliciousLookupOperations.MaliciousLookupOperationWrongForwardingToClosestFingertableSuccessor) {
			messageHandler = new MaliciousChordMessageHandlerWrongForwardingToClosestFingertableSuccessor(
					this, leadTime);
		} else if (typeOfMaliciousLookupOperations == EMaliciousLookupOperations.MaliciousLookupOperationWrongForwardingToDirectSuccessor) {
			messageHandler = new MaliciousChordMessageHandlerWrongForwardingToDirectSuccessor(
					this, leadTime);
		} else if (typeOfMaliciousLookupOperations == EMaliciousLookupOperations.MaliciousLookupOperationWrongForwardingToDirectPredecessor) {
			messageHandler = new MaliciousChordMessageHandlerWrongForwardingToDirectPredecessor(
					this, leadTime);
		}
		this.getTransLayer().addTransMsgListener(this.messageHandler,
				this.getPort());

		// Debug my ID
		log.debug("A new malicious node has spawned (id=" + this.getOverlayID()
				+ ")");

	}

	/**
	 * Find node that is responsible for the given key
	 * 
	 * @param key
	 *            the key to look up
	 * @param callback
	 * @return the Id of the LookupOperation, -1 if the node is not present in
	 *         the overlay
	 */
	@Override
	public int overlayNodeLookup(ChordID key,
			OperationCallback<List<AbstractChordContact>> callback) {
		if (!isPresent()) {
			return -1;
		}

		log.debug("Start look up from node = " + this + " key = " + key);

		int lookupId = getNextLookupId();

		// Differentiate between different requests to this malicious node.
		// If the request is a Joinoperation, the malicious should work
		// correctly like a healthy Chord Node.
		LookupOperation op;
		if (callback instanceof HandshakeCallback) {
			// This request is a JoinOperation, so use "normal"/healthy lookup
			op = new LookupOperation(this, key, callback, lookupId);

		} else {
			// Use malicious lookup. Use defined malicious lookup operation.
			if (typeOfMaliciousLookupOperations == EMaliciousLookupOperations.MaliciousLookupOperationNoForwarding) {
				op = new MaliciousLookupOperationNoForwarding(this, key,
						callback, lookupId);
			} else if (typeOfMaliciousLookupOperations == EMaliciousLookupOperations.MaliciousLookupOperationWrongForwardingToDirectSuccessor) {
				op = new MaliciousLookupOperationWrongForwardingToDirectSuccessor(
						this, key, callback, lookupId);
			} else if (typeOfMaliciousLookupOperations == EMaliciousLookupOperations.MaliciousLookupOperationWrongForwardingToDirectPredecessor) {
				op = new MaliciousLookupOperationWrongForwardingToDirectPredecessor(
						this, key, callback, lookupId);
			} else if (typeOfMaliciousLookupOperations == EMaliciousLookupOperations.MaliciousLookupOperationWrongForwardingToClosestFingertableSuccessor) {
				op = new MaliciousLookupOperationWrongForwardingToClosestFingertableSuccessor(
						this, key, callback, lookupId);
			} else {
				log.error("Wrong type of malicious lookup operation: "
						+ typeOfMaliciousLookupOperations);
				op = null;
			}

		}

		if (op != null) {
			registerLookupOperation(lookupId, op);
			op.scheduleImmediately();
		}

		return lookupId;

	}

}
