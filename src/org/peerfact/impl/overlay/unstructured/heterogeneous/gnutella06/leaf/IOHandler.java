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

package org.peerfact.impl.overlay.unstructured.heterogeneous.gnutella06.leaf;

import org.apache.log4j.Logger;
import org.peerfact.api.common.Message;
import org.peerfact.api.transport.TransMessageListener;
import org.peerfact.api.transport.TransProtocol;
import org.peerfact.impl.overlay.unstructured.heterogeneous.api.Gnutella06OverlayContact;
import org.peerfact.impl.overlay.unstructured.heterogeneous.common.IPongHandler;
import org.peerfact.impl.overlay.unstructured.heterogeneous.common.messages.GnutellaClose;
import org.peerfact.impl.overlay.unstructured.heterogeneous.common.messages.GnutellaConnect;
import org.peerfact.impl.overlay.unstructured.heterogeneous.common.messages.GnutellaConnectReply;
import org.peerfact.impl.overlay.unstructured.heterogeneous.common.messages.GnutellaPing;
import org.peerfact.impl.overlay.unstructured.heterogeneous.common.messages.GnutellaPong;
import org.peerfact.impl.overlay.unstructured.heterogeneous.gnutella06.Gnutella06ConnectionManager;
import org.peerfact.impl.transport.TransMsgEvent;
import org.peerfact.impl.util.logging.SimLogger;


/**
 * Receives the node's overlay messages, replies directly or forwards it to the
 * appropriate components. Only listens to asynchronous messages received, does
 * not receive RPC replies, this is done by the components itself (when they
 * have made a request).
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class IOHandler implements TransMessageListener {

	private final static Logger log = SimLogger.getLogger(IOHandler.class);

	private Leaf owner;

	private Gnutella06ConnectionManager<?> mgr;

	private IPongHandler<Gnutella06OverlayContact, GnutellaPong<Gnutella06OverlayContact>> pongHdlr;

	/**
	 * Creates the I/O handler.
	 * 
	 * @param mgr
	 *            : the connection manager of the node the I/O handler belongs
	 *            to.
	 * @param owner
	 *            : the owner of this I/O handler
	 * @param pongHdlr
	 *            : the pong handler of the node this I/O handler belongs to.
	 */
	public IOHandler(
			Gnutella06ConnectionManager<?> mgr,
			Leaf owner,
			IPongHandler<Gnutella06OverlayContact, GnutellaPong<Gnutella06OverlayContact>> pongHdlr) {
		this.mgr = mgr;
		this.owner = owner;
		this.pongHdlr = pongHdlr;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void messageArrived(TransMsgEvent receivingEvent) {
		Message receivedMessage = receivingEvent.getPayload();

		if (receivedMessage instanceof GnutellaConnect) {
			handleConnect(
					(GnutellaConnect<Gnutella06OverlayContact>) receivedMessage,
					receivingEvent);
			return;
		} else if (receivedMessage instanceof GnutellaPing) {
			handlePing(
					(GnutellaPing<Gnutella06OverlayContact>) receivedMessage,
					receivingEvent);
			return;
		} else if (receivedMessage instanceof GnutellaClose) {
			handleClose(
					(GnutellaClose<Gnutella06OverlayContact>) receivedMessage,
					receivingEvent);
			return;
		}

		// log.debug("Incompatible message received: " +
		// receivedMessage.getClass().getSimpleName());
	}

	/**
	 * Handles an attempt of a foreign ultrapeer to close the connection.
	 * 
	 * @param receivedMessage
	 * @param receivingEvent
	 */
	private void handleClose(
			GnutellaClose<Gnutella06OverlayContact> receivedMessage,
			TransMsgEvent receivingEvent) {
		mgr.foreignCloseAttempt(receivedMessage.getSndr());

		Gnutella06OverlayContact c = receivedMessage.getCausedContact();
		if (c != null) {
			mgr.seenContact(c);
		}
	}

	private void handleConnect(
			GnutellaConnect<Gnutella06OverlayContact> receivedMessage,
			TransMsgEvent receivingEvent) {

		Gnutella06OverlayContact requestingContact = receivedMessage
				.getSenderInfo();

		log
				.debug("Leaf received connection attempt. Leaves do not accept connections.");

		GnutellaConnectReply<Gnutella06OverlayContact> reply = new GnutellaConnectReply<Gnutella06OverlayContact>(
				mgr
						.getSomeConnectedPeers(owner.getConfig()
								.getTryPeersSize()), false);
		reply.setSeqNumber(receivedMessage.getSeqNumber());
		owner.getHost().getTransLayer().send(reply,
				requestingContact.getTransInfo(),
				requestingContact.getTransInfo().getPort(), TransProtocol.UDP);
	}

	/**
	 * Handles an incoming ping.
	 * 
	 * @param receivedMessage
	 * @param e
	 */
	private void handlePing(
			GnutellaPing<Gnutella06OverlayContact> receivedMessage,
			TransMsgEvent e) {

		Gnutella06OverlayContact requestingContact = receivedMessage
				.getSender();

		if (mgr.peerIsConnected(requestingContact)) {

			GnutellaPong<Gnutella06OverlayContact> reply = pongHdlr
					.generatePongMessage(requestingContact, owner
							.getOwnContact());
			reply.setSeqNumber(receivedMessage.getSeqNumber());
			owner.getHost().getTransLayer().send(reply,
					requestingContact.getTransInfo(),
					requestingContact.getTransInfo().getPort(),
					TransProtocol.UDP);
		}
	}

}
