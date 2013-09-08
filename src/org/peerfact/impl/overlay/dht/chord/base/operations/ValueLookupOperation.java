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

import java.util.List;

import org.peerfact.api.common.Message;
import org.peerfact.api.common.Operation;
import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.overlay.dht.DHTObject;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.api.transport.TransMessageCallback;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordContact;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordNode;
import org.peerfact.impl.overlay.dht.chord.base.components.ChordConfiguration;
import org.peerfact.impl.overlay.dht.chord.base.components.ChordID;
import org.peerfact.impl.overlay.dht.chord.base.messages.ValueLookupMessage;
import org.peerfact.impl.simengine.Simulator;


/**
 * This operation is used to realize the value lookup functionality of the
 * DHTNode interface. It retrieves a stored instance of DHTObject if the
 * responsible peer holds it in its store. Therefore it initializes a lookup for
 * the key and then sends a ValueLookupMessage to the responsible peer, returned
 * by the lookup.
 * 
 * The operation expects message losses and does retransmissions when needed.
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * @author Philip Wette <info@peerfact.org>
 * @author Thim Strothmann (adaptions)
 * 
 * @version 05/06/2011
 */
public class ValueLookupOperation extends AbstractChordOperation<DHTObject>
		implements OperationCallback<List<AbstractChordContact>>,
		TransMessageCallback {

	/*
	 * The key of the object to be looked up
	 */
	private final ChordID targetKey;

	/*
	 * The number of unsuccessful retries to lookup the responsible peer using a
	 * LookupOperation
	 */
	private int lookupRetryCount = 0;

	/*
	 * The value lookup message to be directly sent to the responsible peer
	 */
	private ValueLookupMessage valLookupMsg;

	/*
	 * The number of unsuccessful retries to send the ValueLookupMessage to the
	 * responsible peer
	 */
	private int valLookupMsgSentRetryCount = 0;

	/*
	 * The result of the lookup. The field is Null until a result was retrieved.
	 */
	private DHTObject lookupResult;

	/*
	 * The number of hops the lookup for the responsible peer took.
	 */
	private int lookupHopCount;

	/**
	 * to measure how long the query took:
	 */
	long duration;

	public ValueLookupOperation(AbstractChordNode component, ChordID targetKey,
			OperationCallback<DHTObject> callback) {
		super(component, callback);

		this.targetKey = targetKey;
	}

	/*
	 * AbstractChordOperation methods
	 */

	@Override
	protected void execute() {
		if (!getComponent().isPresent()) {
			return;
		}

		// check if we are responsible for that key:
		if (super.getComponent().getDHT()
				.getDHTEntry(this.targetKey.getCorrespondingKey()) != null) {
			lookupResult = (DHTObject) super.getComponent().getDHT()
					.getDHTEntry(this.targetKey.getCorrespondingKey())
					.getValue();
		}

		if (lookupResult != null) {
			Simulator.getMonitor().dhtLookupFinished(
					this.getComponent().getLocalOverlayContact(),
					targetKey.getCorrespondingKey(), lookupResult,
					lookupHopCount);
			operationFinished(true);

			return;
		}
		// scheduleOperationTimeout(ChordConfiguration.OPERATION_TIMEOUT);

		duration = Simulator.getCurrentTime();
		getComponent().overlayNodeLookup(targetKey, this);
	}

	@Override
	public DHTObject getResult() {
		if (!isError()) {
			return lookupResult;
		}
		return null;
	}

	/*
	 * OperationCallback methods
	 */

	@Override
	public void calledOperationFailed(Operation<List<AbstractChordContact>> op) {

		if (lookupRetryCount <= ChordConfiguration.OPERATION_MAX_REDOS) {
			lookupRetryCount++;
			getComponent().overlayNodeLookup(targetKey, this);
		} else {
			// Notify Analyzer
			Simulator.getMonitor().dhtLookupFailed(
					this.getComponent().getLocalOverlayContact(),
					targetKey.getCorrespondingKey());
			operationFinished(false);
		}

	}

	@Override
	public void calledOperationSucceeded(
			Operation<List<AbstractChordContact>> op) {
		List<AbstractChordContact> targets = op.getResult();

		if (!targets.isEmpty()) {

			lookupHopCount = ((LookupOperation) op).getLookupHopCount();

			lookupResult = (DHTObject) targets.get(0);

			operationFinished(true);
		}

	}

	private void sendLookupMsg(ValueLookupMessage msg) {
		getComponent().getTransLayer().sendAndWait(msg,
				msg.getReceiverContact().getTransInfo(),
				getComponent().getPort(),
				ChordConfiguration.TRANSPORT_PROTOCOL, this,
				ChordConfiguration.MESSAGE_TIMEOUT);
	}

	/*
	 * TransMessageCallback methods
	 */

	@Override
	public void receive(Message msg, TransInfo senderInfo, int commId) {
		// if (msg instanceof ValueLookupReplyMessage) {
		// lookupResult = ((ValueLookupReplyMessage) msg).getObject();
		//
		// // init file download:
		// TransferDocumentOperation transOp = new TransferDocumentOperation(
		// this, this.getComponent(), this.getComponent()
		// .getLocalOverlayContact(),
		// ((ValueLookupReplyMessage) msg).getSenderContact(),
		// targetKey.getCorrespondingKey());
		//
		// transOp.scheduleImmediately();
		//
		// stage = 1;
		//
		// } else {
		operationFinished(false);
		Simulator.getMonitor().dhtLookupFailed(
				this.getComponent().getLocalOverlayContact(),
				targetKey.getCorrespondingKey());
		// }
	}

	@Override
	public void messageTimeoutOccured(int commId) {
		if (valLookupMsgSentRetryCount <= ChordConfiguration.MESSAGE_RESEND) {
			valLookupMsgSentRetryCount++;
			sendLookupMsg(valLookupMsg);
		} else {
			operationFinished(false);
			Simulator.getMonitor().dhtLookupFailed(
					this.getComponent().getLocalOverlayContact(),
					targetKey.getCorrespondingKey());
		}
	}

	public int getLookupHopCount() {
		return lookupHopCount;
	}

}
