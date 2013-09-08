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

package org.peerfact.impl.overlay.dht.chord.base.operations;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.impl.overlay.dht.chord.base.callbacks.MessageTimer;
import org.peerfact.impl.overlay.dht.chord.base.callbacks.OperationTimer;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordContact;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordNode;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordRoutingTable;
import org.peerfact.impl.overlay.dht.chord.base.components.ChordConfiguration;
import org.peerfact.impl.overlay.dht.chord.base.components.ChordID;
import org.peerfact.impl.overlay.dht.chord.base.messages.LookupMessage;
import org.peerfact.impl.simengine.Simulator;


/**
 * This operation tries to find the responsible node for a specified key
 * 
 * @author Minh Hoang Nguyen <peerfact@kom.tu-darmstadt.de>
 * @author Thim Strothmann (adaptions)
 * 
 * @version 05/06/2011
 */
public class LookupOperation extends
		AbstractChordOperation<List<AbstractChordContact>> {

	protected final AbstractChordNode masterNode;

	protected final ChordID target;

	protected AbstractChordContact responsibleContact = null;

	protected int lookupId;

	protected int lookupHopCount;

	protected int redoCounter = 0;

	private ArrayList<OverlayContact<?>> responsible = new ArrayList<OverlayContact<?>>();

	public LookupOperation(AbstractChordNode component, ChordID target,
			OperationCallback<List<AbstractChordContact>> callback) {

		super(component, callback);
		this.masterNode = component;
		this.target = target;
		lookupId = this.getOperationID();
	}

	public LookupOperation(AbstractChordNode component, ChordID target,
			OperationCallback<List<AbstractChordContact>> callback, int lookupId) {

		this(component, target, callback);
		this.lookupId = lookupId;
	}

	@Override
	protected void execute() {

		if (masterNode.isPresent()) {
			// scheduleOperationTimeout(ChordConfiguration.LOOKUP_TIMEOUT);

			// register in LookupStore
			log.debug("start lookup id = " + lookupId + " redo = "
					+ redoCounter);

			// Start Operation Timer
			OperationTimer operationTime = new OperationTimer(this);
			operationTime.schedule(ChordConfiguration.OPERATION_TIMEOUT);

			AbstractChordRoutingTable routingTable = masterNode
					.getChordRoutingTable();

			if (routingTable.responsibleFor(target)) {
				// itself is responsible for the key

				deliverResult(masterNode.getLocalOverlayContact(), target,
						getLookupId(), 0);

			} else {

				// Get maximum finger that precedes the id
				AbstractChordContact precedingFinger = routingTable
						.getClosestPrecedingFinger(target);

				// if no finger precedes the id
				if (precedingFinger.equals(masterNode.getLocalOverlayContact())) {
					// forward to direct successor

					AbstractChordContact succ = routingTable.getSuccessor();

					LookupMessage msg = new LookupMessage(
							masterNode.getLocalOverlayContact(),
							routingTable.getSuccessor(), target, lookupId, 0);

					MessageTimer msgTimer = new MessageTimer(masterNode, msg,
							succ);
					masterNode.getTransLayer().sendAndWait(msg,
							succ.getTransInfo(), masterNode.getPort(),
							ChordConfiguration.TRANSPORT_PROTOCOL, msgTimer,
							ChordConfiguration.MESSAGE_TIMEOUT);

				} else {
					// forward to the found preceding finger

					LookupMessage msg = new LookupMessage(
							masterNode.getLocalOverlayContact(),
							precedingFinger,
							target, lookupId, 0);

					MessageTimer msgTimer = new MessageTimer(masterNode, msg,
							precedingFinger);
					masterNode.getTransLayer().sendAndWait(msg,
							precedingFinger.getTransInfo(),
							masterNode.getPort(),
							ChordConfiguration.TRANSPORT_PROTOCOL, msgTimer,
							ChordConfiguration.MESSAGE_TIMEOUT);
				}
			}
		} else {
			masterNode.removeLookupOperation(getLookupId());
			// Notify Simulator
			Simulator.getMonitor().dhtLookupFailed(
					this.getComponent().getLocalOverlayContact(),
					target.getCorrespondingKey());
			operationFinished(false);
		}
	}

	public void timeoutOccurred() {

		if (!isFinished()) {
			if (redoCounter < ChordConfiguration.OPERATION_MAX_REDOS) {
				log.info("lookup redo id = " + this.getOperationID()
						+ " times = " + redoCounter);
				redoCounter++;
				this.execute();
			} else {
				log.debug("look up aborted id = " + lookupId + " redotime = "
						+ redoCounter);

				masterNode.removeLookupOperation(getLookupId());
				// Notify Simulator
				Simulator.getMonitor().dhtLookupFailed(
						this.getComponent().getLocalOverlayContact(),
						target.getCorrespondingKey());
				operationFinished(false);
			}
		}
	}

	public void deliverResult(AbstractChordContact responsibleContact1,
			ChordID targetKey, int lookupOperationID, int hopCount) {
		lookupHopCount = hopCount;

		log.debug("lookup finish id = " + getLookupId() + " redo = "
				+ redoCounter);

		this.responsibleContact = responsibleContact1;
		// Notify Simulator
		responsible.add(0, responsibleContact1);
		Simulator.getMonitor().dhtLookupFinished(
				this.getComponent().getLocalOverlayContact(),
				targetKey.getCorrespondingKey(),
				responsible, hopCount);
		if (!isFinished()) {
			masterNode.removeLookupOperation(getLookupId());
			operationFinished(true);
		}

	}

	@Override
	public List<AbstractChordContact> getResult() {
		LinkedList<AbstractChordContact> respNodes = new LinkedList<AbstractChordContact>();
		respNodes.add(responsibleContact);
		return respNodes;
	}

	public int getLookupId() {
		return lookupId;
	}

	public ChordID getTarget() {
		return target;
	}

	public int getLookupHopCount() {
		return lookupHopCount;
	}

}
