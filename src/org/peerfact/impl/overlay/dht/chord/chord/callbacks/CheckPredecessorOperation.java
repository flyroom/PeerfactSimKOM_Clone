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

package org.peerfact.impl.overlay.dht.chord.chord.callbacks;

import org.peerfact.api.common.Message;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.api.transport.TransMessageCallback;
import org.peerfact.impl.common.AbstractOperation;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordContact;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordNode;
import org.peerfact.impl.overlay.dht.chord.base.components.ChordConfiguration;
import org.peerfact.impl.overlay.dht.chord.base.messages.RetrievePredecessorMsg;
import org.peerfact.impl.overlay.dht.chord.base.messages.RetrievePredecessorReply;
import org.peerfact.impl.overlay.dht.chord.chord.components.ChordRoutingTable;
import org.peerfact.impl.simengine.Simulator;

/**
 * This class is used to check a predecessor of <code>ChordNode</code> The
 * predecessor is required to deliver its next direct predecessor. If the
 * predecessor was offline, the operation returns null as result.
 * 
 * @author Minh Hoang Nguyen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class CheckPredecessorOperation extends
		AbstractOperation<AbstractChordNode, AbstractChordContact> implements
		TransMessageCallback {

	private final AbstractChordNode masterNode;

	private AbstractChordContact updatePredecessor, predOfPredecessor;

	private RetrievePredecessorMsg msg;

	private int resendCounter = 0;

	public CheckPredecessorOperation(AbstractChordNode component) {
		super(component);
		masterNode = component;

	}

	@Override
	protected void execute() {

		if (masterNode.isPresent()) {
			ChordRoutingTable routingTable = (ChordRoutingTable) masterNode
					.getChordRoutingTable();
			updatePredecessor = routingTable.getNextUpdatePredecessor();

			log.debug("check predecessor node " + masterNode
					+ " update pred = " + updatePredecessor + " simTime = "
					+ Simulator.getCurrentTime() / Simulator.SECOND_UNIT);

			msg = new RetrievePredecessorMsg(
					masterNode.getLocalOverlayContact(),
					updatePredecessor);

			if (updatePredecessor.equals(masterNode.getLocalOverlayContact())) {
				// catch exception that the first host has itself as predecessor
				predOfPredecessor = routingTable.getPredecessor();
				operationFinished(true);
				((ChordRoutingTable) masterNode.getChordRoutingTable())
						.updateDistantPredecessor(
								updatePredecessor, predOfPredecessor);
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
			log.debug("inform offline node = " + masterNode);
			operationFinished(false);
			((ChordRoutingTable) masterNode.getChordRoutingTable())
					.updateDistantPredecessor(
							updatePredecessor, null);
		}
	}

	@Override
	public void receive(Message message, TransInfo senderInfo, int commId) {
		if (!masterNode.isPresent()) {
			return;
		}
		if (message instanceof RetrievePredecessorReply) {
			RetrievePredecessorReply reply = (RetrievePredecessorReply) message;
			predOfPredecessor = reply.getPredecessor();
			operationFinished(true);
			((ChordRoutingTable) masterNode.getChordRoutingTable())
					.updateDistantPredecessor(
							updatePredecessor, predOfPredecessor);
		}
	}

	private int sendRetrievePredecessorMsg() {

		if (masterNode.getOverlayID().compareTo(
				updatePredecessor.getOverlayID()) == 0) {
			return -1;
		}

		log.debug("sendRetrievePredecessorMsg " + msg + " times = "
				+ resendCounter);
		return masterNode.getTransLayer().sendAndWait(msg,
				updatePredecessor.getTransInfo(), masterNode.getPort(),
				ChordConfiguration.TRANSPORT_PROTOCOL, this,
				ChordConfiguration.MESSAGE_TIMEOUT);
	}

	@Override
	public AbstractChordContact getResult() {

		return predOfPredecessor;
	}

}
