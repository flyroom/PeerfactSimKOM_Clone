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

/**
 * 
 */
package org.peerfact.impl.overlay.unstructured.heterogeneous.gia.operations;

import java.util.List;

import org.peerfact.api.common.OperationCallback;
import org.peerfact.impl.overlay.unstructured.heterogeneous.common.IManageableConnection;
import org.peerfact.impl.overlay.unstructured.heterogeneous.common.ConnectionManager.ConnectionState;
import org.peerfact.impl.overlay.unstructured.heterogeneous.common.messages.SeqMessage;
import org.peerfact.impl.overlay.unstructured.heterogeneous.common.operations.ReqRespOperation;
import org.peerfact.impl.overlay.unstructured.heterogeneous.gia.ConnectAcceptanceDecider;
import org.peerfact.impl.overlay.unstructured.heterogeneous.gia.GiaConnectionManager;
import org.peerfact.impl.overlay.unstructured.heterogeneous.gia.GiaConnectionMetadata;
import org.peerfact.impl.overlay.unstructured.heterogeneous.gia.GiaNode;
import org.peerfact.impl.overlay.unstructured.heterogeneous.gia.GiaOverlayContact;
import org.peerfact.impl.overlay.unstructured.heterogeneous.gia.ConnectAcceptanceDecider.Decision;
import org.peerfact.impl.overlay.unstructured.heterogeneous.gia.messages.GiaHandshake1;
import org.peerfact.impl.overlay.unstructured.heterogeneous.gia.messages.GiaHandshake2;
import org.peerfact.impl.overlay.unstructured.heterogeneous.gia.messages.GiaHandshake3;
import org.peerfact.impl.overlay.unstructured.heterogeneous.gia.messages.GiaMessage;


/**
 * Connection operation managing the three-way handshake on the requester's
 * (i.e. X) side.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class GiaConnectOperationX extends
		ReqRespOperation<GiaOverlayContact, GiaNode, Object> {

	private GiaConnectionManager mgr;

	private IManageableConnection<GiaOverlayContact, GiaConnectionMetadata> conn;

	private GiaOverlayContact to;

	private GiaNode component;

	public GiaConnectOperationX(
			GiaNode component,
			GiaOverlayContact to,
			GiaConnectionManager mgr,
			IManageableConnection<GiaOverlayContact, GiaConnectionMetadata> conn,
			OperationCallback<Object> callback) {
		super(component, to, callback);
		this.component = component;
		this.to = to;
		this.conn = conn;
		this.mgr = mgr;
	}

	@Override
	protected SeqMessage createReqMessage() {
		return new GiaHandshake1(component.getOwnContact(), getTo(),
				mgr.getDegree(), shallRequestTryPeers());
	}

	protected boolean shallRequestTryPeers() {
		return mgr.getNumberOfContactsInState(ConnectionState.Connected) < getComponent()
				.getConfig().getTryPeersAddLimit();
	}

	@Override
	protected long getTimeout() {
		return component.getConfig().getHandshakeTimeout();
	}

	@Override
	protected boolean gotResponse(SeqMessage response) {
		if (response instanceof GiaHandshake2) {
			GiaHandshake2 hs2 = (GiaHandshake2) response;
			if (to.equals(hs2.getY())) {
				processHandshake2(hs2);
			} else {
				log.debug("May not happen!!!!!!!!!!!!!!!!!!!!1");
				return false;
			}
			return true;
		}
		return false;
	}

	private void processHandshake2(GiaHandshake2 hs2) {
		if (hs2.isAccepted()) {
			ConnectAcceptanceDecider decider = new ConnectAcceptanceDecider(
					mgr, to, hs2.getDegreeOfY(), component.getConfig());
			if (decider.getDecision() == Decision.DropAndAccept) {
				mgr.closeConnection(decider.getContactToDrop(), to);
				finishConnection(to, hs2.getDegreeOfY(),
						hs2.getInitialTokenAllocationRate());
				conn.connectionSucceeded();
			} else if (decider.getDecision() == Decision.Accept) {
				finishConnection(to, hs2.getDegreeOfY(),
						hs2.getInitialTokenAllocationRate());
				conn.connectionSucceeded();
			} else {
				conn.connectionFailed();
			}

			GiaMessage response = new GiaHandshake3(component.getOwnContact(),
					decider.getDecision() != Decision.Reject,
					component.getTokenAllocationRateFor(to));
			response.setSeqNumber(hs2.getSeqNumber());
			sendMessage(response, to);

			this.operationFinished(decider.getDecision() != Decision.Reject);
		} else {
			conn.connectionFailed();
		}

		if (hs2.getTryPeers() != null) {
			List<GiaOverlayContact> contacts = hs2.getTryPeers();
			// AbstractGnutellaLikeNode.dumpStateOfAll();
			// log.debug("Adding contacts list " + hs2.getTryPeers() +
			// " from " + hs2.getY() + " to contact list of " +
			// getComponent().getOwnContact() + " with contacts " +
			// mgr.getConnectedContacts());
			mgr.seenContacts(contacts); // Adding received TryPeers to the host
										// cache.
		}
	}

	private void finishConnection(GiaOverlayContact toContact,
			int degreeOfContact,
			long initialTAR) {
		mgr.putMetadata(toContact,
				new GiaConnectionMetadata(component.getConfig(),
						component.getLocalClock(),
						initialTAR, degreeOfContact));
	}

	@Override
	protected void timeoutOccured() {
		conn.connectionTimeouted();
		this.operationFinished(false);
	}

	@Override
	public Object getResult() {
		return null;
	}

}
