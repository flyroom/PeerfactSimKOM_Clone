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

package org.peerfact.impl.overlay.dht.pastry.operations;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.impl.overlay.dht.pastry.components.PastryConstants;
import org.peerfact.impl.overlay.dht.pastry.components.PastryContact;
import org.peerfact.impl.overlay.dht.pastry.components.PastryID;
import org.peerfact.impl.overlay.dht.pastry.components.PastryMessageHandler;
import org.peerfact.impl.overlay.dht.pastry.components.PastryNode;
import org.peerfact.impl.overlay.dht.pastry.messages.LookupMsg;
import org.peerfact.impl.overlay.dht.pastry.messages.MsgTransInfo;
import org.peerfact.impl.simengine.SimulationEvent;
import org.peerfact.impl.simengine.Simulator;


/**
 * @author Fabio Zöllner, improved by Julius Rueckert
 *         <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class LookupOperation extends
		AbstractPastryOperation<List<PastryContact>> {

	private PastryNode node;

	private PastryID targetId;

	private Integer lookupId;

	private Integer redoCounter = 0;

	private PastryContact responsibleContact = null;

	private PastryMessageHandler msgHandler;

	private int lookupHopCount;

	private ArrayList<OverlayContact<?>> responsible = new ArrayList<OverlayContact<?>>();

	// TODO check, if this can be improved
	public LookupOperation(PastryNode component, PastryID target,
			OperationCallback<List<PastryContact>> callback) {

		super(component, callback);
		this.targetId = target;
		lookupId = this.getOperationID();
		this.node = component;
		msgHandler = node.getMsgHandler();
	}

	public LookupOperation(PastryNode component, PastryID target,
			OperationCallback<List<PastryContact>> callback, int lookupId) {

		this(component, target, callback);
		this.lookupId = lookupId;
	}

	@Override
	protected void execute() {
		if (!node.isPresent()) {
			operationFinished(false);
			return;
		}

		if (node.isResponsibleFor(targetId.getCorrespondingKey())) {
			this.responsibleContact = node.getOverlayContact();
			operationFinished(true);
		} else {

			// Schedule a timeout to redo the lookup after some time
			Simulator.scheduleEvent("retryLookup" + lookupId,
					Simulator.getCurrentTime() + PastryConstants.OP_TIMEOUT,
					this, SimulationEvent.Type.OPERATION_EXECUTE);

			// Do the lookup
			node.registerOperation(this);
			PastryContact receiver = node.getNextHop(targetId);
			LookupMsg msg = new LookupMsg(node.getOverlayContact(), receiver,
					targetId, lookupId, 1);
			msgHandler.sendMsg(new MsgTransInfo<PastryContact>(msg, receiver));
		}
	}

	@Override
	public void eventOccurred(SimulationEvent se) {
		if (se.getData().equals("retryLookup" + lookupId)) {
			if (!isFinished()) {
				if (redoCounter < PastryConstants.OP_MAX_RETRIES) {
					log.info("lookup redo id = " + this.getOperationID()
							+ " times = " + redoCounter);
					redoCounter++;
					this.execute();
				} else {
					log.debug("look up aborted id = " + lookupId
							+ " redotime = " + redoCounter);

					node.unregisterOperation(lookupId);
					operationFinished(false);
					// Notify Simulator
					Simulator.getMonitor().dhtLookupFailed(
							this.getComponent().getLocalOverlayContact(),
							targetId.getCorrespondingKey());
				}
			}
		} else {
			super.eventOccurred(se);
		}
	}

	public void deliverResult(PastryContact responsibleNode,
			PastryID target, int lookupID, int hops) {
		this.lookupHopCount = hops;
		this.responsibleContact = responsibleNode;

		if (!isFinished()) {
			node.unregisterOperation(this.lookupId);
			operationFinished(true);
		}
		// Notify Monitor
		responsible.add(0, responsibleNode);
		Simulator.getMonitor().dhtLookupFinished(
				this.getComponent().getLocalOverlayContact(),
				target.getCorrespondingKey(),
				responsible, hops);
	}

	public int getLookupId() {
		return this.lookupId;
	}

	public PastryID getTarget() {
		return this.targetId;
	}

	@Override
	public List<PastryContact> getResult() {
		LinkedList<PastryContact> l = new LinkedList<PastryContact>();
		if (responsibleContact != null) {
			l.add(responsibleContact);
		}
		return l;
	}

	public int getLookupHopCount() {
		return lookupHopCount;
	}
}
