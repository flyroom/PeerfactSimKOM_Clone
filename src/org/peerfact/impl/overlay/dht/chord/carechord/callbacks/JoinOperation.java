/*
 * Copyright (c) 2012-2013 Open Source Community - <http://www.peerfact.org>
 * Copyright (c) 2011-2012 University of Paderborn - UPB
 * Copyright (c) 2005-2011 KOM - Multimedia Communications Lab
 *
 * This file is part of PeerfactSim.
 * 
 * PeerfactSim is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 * 
 * PeerfactSim is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with PeerfactSim.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.peerfact.impl.overlay.dht.chord.carechord.callbacks;

import org.peerfact.api.common.Message;
import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.impl.overlay.AbstractOverlayNode.PeerStatus;
import org.peerfact.impl.overlay.dht.chord.base.callbacks.AbstractJoinOperation;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordContact;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordNode;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordRoutingTable;
import org.peerfact.impl.overlay.dht.chord.base.components.ChordBootstrapManager;
import org.peerfact.impl.overlay.dht.chord.base.components.ChordConfiguration;
import org.peerfact.impl.overlay.dht.chord.base.messages.ChordMessage;
import org.peerfact.impl.overlay.dht.chord.base.messages.JoinMessage;
import org.peerfact.impl.overlay.dht.chord.base.messages.JoinReply;
import org.peerfact.impl.overlay.dht.chord.carechord.components.ChordNode;
import org.peerfact.impl.overlay.dht.chord.carechord.components.ChordRoutingTable;
import org.peerfact.impl.simengine.Simulator;

/**
 * This class represents a join event.
 * 
 * @author Markus Benter (original author)
 * @author Thim Strothmann (Adaptions)
 * 
 */
public class JoinOperation extends AbstractJoinOperation {

	private int joinMsgResendCount = 0;

	private AbstractChordContact joinContact;

	private JoinMessage joinMsg;

	public JoinOperation(AbstractChordNode component,
			OperationCallback<Object> callback) {
		super(component, callback);
		this.callback = callback;
	}

	@Override
	protected void execute() {
		if (getComponent().isPresent()) {
			log.warn(Simulator.getSimulatedRealtime() + " Peer "
					+ getComponent().getHost().getNetLayer().getNetID()
					+ " wants to join, although being already present ");
			operationFinished(true);
			return;
		}
		ChordBootstrapManager cbm = joinNode.getBootstrapManager();

		if (cbm.isEmpty()) {
			log.info("Create initial ring structure");
			finish();
		} else {
			// use firstContactNode to find successor for new joining node
			joinContact = cbm.getRandomAvailableNode();
			joinMsg = new JoinMessage(joinNode.getLocalOverlayContact(),
					joinContact);
			sendJoinMsg();
		}
	}

	private void sendJoinMsg() {
		joinNode.getTransLayer().sendAndWait(joinMsg,
				joinContact.getTransInfo(), joinNode.getPort(),
				ChordConfiguration.TRANSPORT_PROTOCOL, this,
				ChordConfiguration.JOIN_MESSAGE_TIMEOUT);
	}

	/**
	 * Inform JoinOperation that join process successfully ended
	 */
	@Override
	protected void finish() {

		joinNode.setPeerStatus(PeerStatus.PRESENT);
		log.debug("Join successful (node = " + joinNode + " time = "
				+ Simulator.getCurrentTime() / Simulator.SECOND_UNIT + ")");
		ChordBootstrapManager cbm = joinNode.getBootstrapManager();
		cbm.registerNode(joinNode);
		operationFinished(true);

		((ChordNode) joinNode).joinOperationFinished(joinContact);
	}

	private void abort() {
		if (joinNode.isPresent()) {
			log.warn(Simulator.getSimulatedRealtime() + " Node "
					+ joinNode.getHost().getNetLayer().getNetID()
					+ " already present, but abort was called!");
			operationFinished(true);
			return;
		}

		log.error(Simulator.getSimulatedRealtime() + " Node "
				+ joinNode.getHost().getNetLayer().getNetID()
				+ " - Join operation failed. Try to do a rejoin. "
				+ joinNode.getBootstrapManager().getNumOfAvailableNodes()
				+ " Available nodes");
		joinNode.setPeerStatus(PeerStatus.ABSENT);
		joinNode.joinWithDelay(
				callback,
				Simulator.getRandom().nextInt(
						(int) ChordConfiguration.MAX_WAIT_BEFORE_JOIN_RETRY));
	}

	@Override
	public void messageTimeoutOccured(int commId) {
		log.error(Simulator.getSimulatedRealtime()
				+ " Message timeout during node join (node= "
				+ joinNode.getHost().getNetLayer().getNetID() + ")");

		if (joinMsgResendCount < ChordConfiguration.MESSAGE_RESEND) {
			joinMsgResendCount++;
			sendJoinMsg();
		} else {
			abort();
		}
	}

	@Override
	public void receive(Message msg, TransInfo senderInfo, int commId) {
		if (isFinished()) {
			log.error("op is finished before receive reply msg node =  "
					+ joinNode);
			return;
		}
		if (joinNode.isPresent()) {
			log.error(Simulator.getSimulatedRealtime()
					+ " Node "
					+ joinNode.getHost().getNetLayer().getNetID()
					+ " already present, but received a JoinReply or HandshakeReply message!");
			operationFinished(true);
			return;
		}
		joinMsgResendCount = 0;

		if (msg instanceof JoinReply) {
			AbstractChordRoutingTable rt = joinNode
					.getChordRoutingTable();
			((ChordRoutingTable) rt).addUnmarkedContact(joinContact);
			operationFinished(true);
			finish();
		} else {
			log.error(Simulator.getSimulatedRealtime()
					+ " Node "
					+ ((ChordMessage) msg).getReceiverContact().getTransInfo()
							.getNetId()
					+ " receives WRONG MESSAGE from "
					+ ((ChordMessage) msg).getSenderContact().getTransInfo()
							.getNetId());
		}
	}

	@Override
	public Object getResult() {
		return this.isSuccessful();
	}
}
