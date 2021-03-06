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

import java.util.Set;

import org.peerfact.api.common.Message;
import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.impl.overlay.AbstractOverlayNode.PeerStatus;
import org.peerfact.impl.overlay.dht.chord.base.callbacks.AbstractJoinOperation;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordContact;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordNode;
import org.peerfact.impl.overlay.dht.chord.base.components.ChordBootstrapManager;
import org.peerfact.impl.overlay.dht.chord.base.components.ChordConfiguration;
import org.peerfact.impl.overlay.dht.chord.base.messages.HandshakeMsg;
import org.peerfact.impl.overlay.dht.chord.base.messages.HandshakeReply;
import org.peerfact.impl.overlay.dht.chord.base.messages.JoinMessage;
import org.peerfact.impl.overlay.dht.chord.base.messages.JoinReply;
import org.peerfact.impl.overlay.dht.chord.chord.components.ChordNode;
import org.peerfact.impl.simengine.Simulator;


/**
 * This class represents a join event.
 * 
 * @author Minh Hoang Nguyen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class JoinOperation extends AbstractJoinOperation {

	private int joinMsgResendCount = 0, handshakeMsgResendCount = 0;

	private AbstractChordContact successorContact = null,
			predecessorContact = null;

	private Set<AbstractChordContact> succFingerTable = null;

	private AbstractChordContact joinContact, handShakeContact;

	private JoinMessage joinMsg;

	private HandshakeMsg handShakeMsg;

	public JoinOperation(AbstractChordNode component,
			OperationCallback<Object> callback) {
		super(component, callback);
		this.callback = callback;
	}

	/*
	 * join contains the following steps 1. pick a random node in
	 * ChordBootstrapManager as first contact 2. The first node look the
	 * successor for join node 3. join node send handshake message to its
	 * successor 4. successor inform join node about its predecessor,
	 * FingerTable 5. finish, join node receive successor, predecessor contact
	 * and FingerTable of successor
	 */

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
			predecessorContact = joinNode.getLocalOverlayContact();
			successorContact = joinNode.getLocalOverlayContact();
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
				ChordConfiguration.MESSAGE_TIMEOUT);
	}

	public void receiveSuccessorContact(AbstractChordContact successor) {
		successorContact = successor;

		// retrieve predecessor of successor
		if (successorContact.compareTo(joinNode.getLocalOverlayContact()) != 0) {

			handShakeContact = successor;
			handShakeMsg = new HandshakeMsg(joinNode.getLocalOverlayContact(),
					handShakeContact);
			sendHandShakeMsg();
		} else {
			abort();
		}
	}

	private void sendHandShakeMsg() {
		joinNode.getTransLayer().sendAndWait(handShakeMsg,
				handShakeContact.getTransInfo(), joinNode.getPort(),
				ChordConfiguration.TRANSPORT_PROTOCOL, this,
				ChordConfiguration.MESSAGE_TIMEOUT);
	}

	/**
	 * Inform JoinOperation that join process successfully ended
	 */
	@Override
	protected void finish() {

		if (predecessorContact.between(joinNode.getLocalOverlayContact(),
				successorContact)) {
			log.error("first successor and predecessor inconsitent node = "
					+ joinNode.getHost().getNetLayer().getNetID() + " succ = "
					+ successorContact + " pred = " + predecessorContact);

			// if (joinMsgResendCount < ChordConfiguration.OPERATION_MAX_REDOS)
			// {
			// joinMsgResendCount++;
			// // send Lookup message to predecessor contact
			// JoinMessage msg = new JoinMessage(joinNode
			// .getLocalChordContact(), predecessorContact);
			// joinNode.getTransLayer().sendAndWait(msg,
			// predecessorContact.getTransInfo(), joinNode.getPort(),
			// ChordConfiguration.TRANSPORT_PROTOCOL, this,
			// ChordConfiguration.LOOKUP_TIMEOUT);
			// if (joinNode.getLocalChordContact().equals(successorContact)) {
			// // critical fall
			// log.error("churn and rejoin so fast node = " + joinNode);
			// abort();
			// }
			// } else {
			abort();
			// }

		} else if (joinNode.isPresent()) {
			log.warn(Simulator.getSimulatedRealtime()
					+ " Peer "
					+ getComponent().getHost().getNetLayer().getNetID()
					+ " wants to set state to Present, although being already Present ");
			operationFinished(true);
			return;
		} else {
			joinNode.setPeerStatus(PeerStatus.PRESENT);
			log.debug("Join successful (node = " + joinNode + " succ = "
					+ successorContact + " pred = " + predecessorContact
					+ " time = " + Simulator.getCurrentTime()
					/ Simulator.SECOND_UNIT + ")");
			ChordBootstrapManager cbm = joinNode.getBootstrapManager();
			cbm.registerNode(joinNode);
			operationFinished(true);

			((ChordNode) joinNode).joinOperationFinished(successorContact,
					predecessorContact, succFingerTable);
		}

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
		log.debug("Message timeout during node join (node= "
				+ joinNode.getHost().getNetLayer().getNetID() + ")");

		if (successorContact == null) {
			if (joinMsgResendCount < ChordConfiguration.MESSAGE_RESEND) {
				joinMsgResendCount++;
				sendJoinMsg();
			} else {
				abort();
			}
		} else {
			if (handshakeMsgResendCount < ChordConfiguration.MESSAGE_RESEND) {
				handshakeMsgResendCount++;
				sendHandShakeMsg();
			} else {
				abort();
			}
		}
	}

	@Override
	public void receive(Message msg, TransInfo senderInfo, int commId) {
		if (isFinished()) {
			log.info("op is finished before receive reply msg node =  "
					+ joinNode);
			return;
		}
		if (joinNode.isPresent()) {
			log.warn(Simulator.getSimulatedRealtime()
					+ " Node "
					+ joinNode.getHost().getNetLayer().getNetID()
					+ " already present, but received a JoinReply or HandshakeReply message!");
			operationFinished(true);
			return;
		}
		joinMsgResendCount = 0;

		if (msg instanceof JoinReply) {
			JoinReply joinReply = (JoinReply) msg;
			AbstractChordContact successor = joinReply.getSuccessorContact();
			receiveSuccessorContact(successor);
		} else if (msg instanceof HandshakeReply) {
			HandshakeReply reply = (HandshakeReply) msg;
			predecessorContact = reply.getPredecessor();
			succFingerTable = reply.getAvailableContacts();
			finish();
		}
	}

	@Override
	public Object getResult() {
		return this.isSuccessful();
	}
}
