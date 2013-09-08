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

import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.overlay.dht.DHTObject;
import org.peerfact.impl.overlay.dht.pastry.components.PastryConstants;
import org.peerfact.impl.overlay.dht.pastry.components.PastryContact;
import org.peerfact.impl.overlay.dht.pastry.components.PastryID;
import org.peerfact.impl.overlay.dht.pastry.components.PastryNode;
import org.peerfact.impl.overlay.dht.pastry.messages.MsgTransInfo;
import org.peerfact.impl.overlay.dht.pastry.messages.ValueLookupMsg;
import org.peerfact.impl.overlay.dht.pastry.messages.ValueLookupReplyMsg;
import org.peerfact.impl.simengine.SimulationEvent;
import org.peerfact.impl.simengine.Simulator;

/**
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 02/05/2011
 * 
 */
public class ValueLookupOperation extends AbstractPastryOperation<DHTObject> {

	private PastryID lookupKey;

	private DHTObject retrievedObject;

	private int redoCounter = 0;

	public ValueLookupOperation(PastryNode component, PastryID lookupKey,
			OperationCallback<DHTObject> callback) {
		super(component, callback);
		this.lookupKey = lookupKey;
	}

	@Override
	protected void execute() {
		if (!getComponent().isPresent()) {
			operationFinished(false);
			return;
		}

		if (getComponent().isResponsibleFor(lookupKey.getCorrespondingKey())) {
			log.debug("PastryNode "
					+ getComponent().getHost().getNetLayer().getNetID()
					+ " is responsible for the requested object with key "
					+ lookupKey);
			// object is retrieved from the requesting node
			retrievedObject = (DHTObject) getComponent().getDHT().getDHTValue(
					lookupKey.getCorrespondingKey());
			if (retrievedObject == null) {
				log.warn("PastryNode "
						+ getComponent().getHost().getNetLayer().getNetID()
						+ " is responsible for the requested object with key "
						+ lookupKey + " but does not maintain it. Start lookup");
				startLookup();
			} else {
				operationFinished(true);
				Simulator.getMonitor().dhtLookupFinished(
						this.getComponent().getLocalOverlayContact(),
						lookupKey.getCorrespondingKey(), retrievedObject,
						0);
			}

		} else {
			startLookup();
		}
	}

	private void startLookup() {
		// Schedule a timeout to redo the lookup after some time
		scheduleOperationTimeout(PastryConstants.OP_TIMEOUT);

		// Do the lookup
		getComponent().registerOperation(this);
		PastryContact receiver = getComponent().getNextHop(lookupKey);
		ValueLookupMsg msg = new ValueLookupMsg(getComponent()
				.getOverlayContact(), receiver.getOverlayID(), lookupKey,
				getOperationID());
		getComponent().getMsgHandler().sendMsg(
				new MsgTransInfo<PastryContact>(msg, receiver));
	}

	public void deliverResult(ValueLookupReplyMsg vlrMsg) {
		if (!isFinished()) {
			retrievedObject = vlrMsg.getValue();
			if (retrievedObject == null) {
				log.warn(Simulator.getSimulatedRealtime() + ": IPAddress= "
						+ getComponent().getHost().getNetLayer().getNetID()
						+ ": Received empty object for key " + lookupKey);
				operationFinished(false);

				// Notify Simulator
				Simulator.getMonitor().dhtLookupFailed(
						this.getComponent().getLocalOverlayContact(),
						lookupKey.getCorrespondingKey());
			} else {
				log.info(Simulator.getSimulatedRealtime() + ": IPAddress= "
						+ getComponent().getHost().getNetLayer().getNetID()
						+ ": Received object for key " + lookupKey);
				operationFinished(true);

				Simulator.getMonitor().dhtLookupFinished(
						this.getComponent().getLocalOverlayContact(),
						lookupKey.getCorrespondingKey(), retrievedObject,
						vlrMsg.getHopCount());
			}
		}
	}

	@Override
	public void eventOccurred(SimulationEvent se) {
		if (!isFinished()
				&& se.getType() == SimulationEvent.Type.TIMEOUT_EXPIRED) {
			if (redoCounter < PastryConstants.OP_MAX_RETRIES) {
				log.debug(Simulator.getSimulatedRealtime() + ": IPAddress= "
						+ getComponent().getHost().getNetLayer().getNetID()
						+ ": Retry of ValueLookupOperation with ID "
						+ getOperationID() + " for the " + (redoCounter + 1)
						+ ". time");
				// unregister this operation at the PastryNode
				if (getComponent().unregisterOperation(getOperationID()) == null) {
					log.error(Simulator.getSimulatedRealtime()
							+ ": IPAddress= "
							+ getComponent().getHost().getNetLayer().getNetID()
							+ ": Trying to unregister ValueLookupOperation"
							+ getOperationID() + ", which was not successfull");
				}
				redoCounter++;
				execute();
			} else {
				log.warn(Simulator.getSimulatedRealtime() + ": IPAddress= "
						+ getComponent().getHost().getNetLayer().getNetID()
						+ ": ValueLookupOperation with ID " + getOperationID()
						+ " failed!");
				operationTimeoutOccured();
			}
		} else if (se.getType() == SimulationEvent.Type.OPERATION_EXECUTE
				&& se.getData() == this) {
			Simulator.getMonitor().operationInitiated(this);
			execute();
		}
	}

	@Override
	public DHTObject getResult() {
		return retrievedObject;
	}

}
