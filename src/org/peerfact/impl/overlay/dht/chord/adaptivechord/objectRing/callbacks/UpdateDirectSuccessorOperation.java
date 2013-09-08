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

package org.peerfact.impl.overlay.dht.chord.adaptivechord.objectRing.callbacks;

import org.peerfact.api.common.Message;
import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.api.transport.TransMessageCallback;
import org.peerfact.impl.overlay.dht.chord.adaptivechord.objectRing.components.ChordConfiguration;
import org.peerfact.impl.overlay.dht.chord.adaptivechord.objectRing.components.ChordNode;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordContact;
import org.peerfact.impl.overlay.dht.chord.base.messages.RetrievePredecessorMsg;
import org.peerfact.impl.overlay.dht.chord.base.messages.RetrievePredecessorReply;
import org.peerfact.impl.overlay.dht.chord.base.operations.AbstractChordOperation;
import org.peerfact.impl.simengine.Simulator;

/**
 * This class is used to check a next direct successor. The successor is
 * required to deliver its predecessor. If the successor was offline, the
 * operation returns null as result.
 * 
 * @author Minh Hoang Nguyen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class UpdateDirectSuccessorOperation extends
		AbstractChordOperation<AbstractChordContact> implements
		TransMessageCallback {

	private final ChordNode masterNode;

	private int resendCounter = 0;

	private RetrievePredecessorMsg msg;

	private AbstractChordContact successor;

	private AbstractChordContact predecessorContact;

	protected long beginTime;

	public UpdateDirectSuccessorOperation(ChordNode component,
			OperationCallback<AbstractChordContact> callback) {
		super(component, callback);
		masterNode = component;
	}

	@Override
	protected void execute() {
		beginTime = Simulator.getCurrentTime();

		if (masterNode.isPresent()) {
			// send request to retrieve predecessor from successor
			successor = masterNode.getChordRoutingTable().getSuccessor();
			msg = new RetrievePredecessorMsg(
					masterNode.getLocalOverlayContact(),
					successor);

			if (successor.equals(masterNode.getLocalOverlayContact())) {
				// catch exception that the first host has itself as predecessor
				predecessorContact = masterNode.getChordRoutingTable()
						.getPredecessor();
				operationFinished(true);
				masterNode.getChordRoutingTable().updatePredecessorOfSuccessor(
						successor, predecessorContact);
			} else {
				sendRetrievePredecessorMsg();
			}

		} else {
			operationFinished(false);
		}
	}

	@Override
	public void messageTimeoutOccured(int commId) {
		if (!masterNode.isPresent()) {
			return;
		}

		if (resendCounter < ChordConfiguration.MESSAGE_RESEND) {
			resendCounter++;
			sendRetrievePredecessorMsg();
		} else {
			operationFinished(false);
			log.debug("Update Direct Successor failed");
			// masterNode.getChordRoutingTable().receiveOfflineEvent(successor);
			masterNode.getChordRoutingTable().updatePredecessorOfSuccessor(
					successor, null);
		}
	}

	@Override
	public void receive(Message message, TransInfo senderInfo, int commId) {
		if (!masterNode.isPresent()) {
			return;
		}

		operationFinished(true);
		RetrievePredecessorReply reply = (RetrievePredecessorReply) message;
		predecessorContact = reply.getPredecessor();
		masterNode.getChordRoutingTable().updatePredecessorOfSuccessor(
				successor, predecessorContact);
	}

	@Override
	public AbstractChordContact getResult() {

		return predecessorContact;
	}

	private int sendRetrievePredecessorMsg() {
		log.debug("sendRetrievePredecessor " + msg + " times = "
				+ resendCounter);
		return masterNode.getTransLayer().sendAndWait(msg,
				successor.getTransInfo(), masterNode.getPort(),
				ChordConfiguration.TRANSPORT_PROTOCOL, this,
				ChordConfiguration.MESSAGE_TIMEOUT);
	}

	public long getBeginTime() {
		return beginTime;
	}
}
