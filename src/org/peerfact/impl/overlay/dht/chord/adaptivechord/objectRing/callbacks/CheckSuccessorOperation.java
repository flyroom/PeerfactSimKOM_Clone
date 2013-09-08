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
import org.peerfact.api.transport.TransInfo;
import org.peerfact.api.transport.TransMessageCallback;
import org.peerfact.impl.common.AbstractOperation;
import org.peerfact.impl.overlay.dht.chord.adaptivechord.objectRing.components.ChordConfiguration;
import org.peerfact.impl.overlay.dht.chord.adaptivechord.objectRing.components.ChordRoutingTable;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordContact;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordNode;
import org.peerfact.impl.overlay.dht.chord.base.messages.RetrieveSuccessorMsg;
import org.peerfact.impl.overlay.dht.chord.base.messages.RetrieveSuccessorReply;
import org.peerfact.impl.simengine.Simulator;

/**
 * This class is used to check a successor of <code>ChordNode</code> The
 * successor is required to deliver its next direct successor. If the successor
 * was offline, the operation returns null as result.
 * 
 * @author Minh Hoang Nguyen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class CheckSuccessorOperation extends
		AbstractOperation<AbstractChordNode, AbstractChordContact> implements
		TransMessageCallback {

	private final AbstractChordNode masterNode;

	private AbstractChordContact updateSuccessor, succOfSuccessor;

	private RetrieveSuccessorMsg msg;

	private int resendCounter = 0;

	public CheckSuccessorOperation(AbstractChordNode component) {
		super(component);
		masterNode = component;

	}

	@Override
	protected void execute() {

		if (masterNode.isPresent()) {
			ChordRoutingTable routingTable = (ChordRoutingTable) masterNode
					.getChordRoutingTable();
			updateSuccessor = routingTable.getNextUpdateSuccessor();

			log.debug("check successor node " + masterNode + " update pred = "
					+ updateSuccessor + " simTime = "
					+ Simulator.getCurrentTime() / Simulator.SECOND_UNIT);

			msg = new RetrieveSuccessorMsg(masterNode.getLocalOverlayContact(),
					updateSuccessor);
			if (updateSuccessor.equals(masterNode.getLocalOverlayContact())) {

				// catch exception that the first host has itself as successor
				succOfSuccessor = routingTable.getSuccessor();
				operationFinished(true);
				((ChordRoutingTable) masterNode.getChordRoutingTable())
						.updateDistantSuccessor(
								updateSuccessor, succOfSuccessor);

			} else {
				sendRetrieveSuccessorMsg();
			}
		} else {
			operationFinished(false);
		}

	}

	@Override
	public AbstractChordContact getResult() {
		return succOfSuccessor;
	}

	@Override
	public void messageTimeoutOccured(int commId) {

		if (!masterNode.isPresent()) {
			return;
		}

		if (resendCounter < ChordConfiguration.MESSAGE_RESEND) {
			resendCounter++;
			sendRetrieveSuccessorMsg();
		} else {
			operationFinished(false);
			log.warn("inform offline node = " + masterNode);
			((ChordRoutingTable) masterNode.getChordRoutingTable())
					.updateDistantSuccessor(
							updateSuccessor, null);

		}
	}

	@Override
	public void receive(Message message, TransInfo senderInfo, int commId) {

		if (!masterNode.isPresent()) {
			return;
		}

		RetrieveSuccessorReply reply = (RetrieveSuccessorReply) message;
		succOfSuccessor = reply.getSuccessor();
		operationFinished(true);
		((ChordRoutingTable) masterNode.getChordRoutingTable())
				.updateDistantSuccessor(
						updateSuccessor, succOfSuccessor);
	}

	private int sendRetrieveSuccessorMsg() {

		if (masterNode.getOverlayID().compareTo(updateSuccessor.getOverlayID()) == 0) {
			return -1;
		}

		log.debug("sendRetrieveSuccessor " + msg + " times = " + resendCounter);

		return masterNode.getTransLayer().sendAndWait(msg,
				updateSuccessor.getTransInfo(), masterNode.getPort(),
				ChordConfiguration.TRANSPORT_PROTOCOL, this,
				ChordConfiguration.MESSAGE_TIMEOUT);
	}
}
