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

package org.peerfact.impl.overlay.dht.centralizedindex.operations;

import org.peerfact.api.common.Message;
import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.api.transport.TransMessageCallback;
import org.peerfact.api.transport.TransProtocol;
import org.peerfact.impl.overlay.dht.centralizedindex.components.CIClientNode;
import org.peerfact.impl.overlay.dht.centralizedindex.components.CIOverlayContact;
import org.peerfact.impl.overlay.dht.centralizedindex.components.CIOverlayID;
import org.peerfact.impl.overlay.dht.centralizedindex.messages.PredecessorReplyMsg;
import org.peerfact.impl.overlay.dht.centralizedindex.messages.PredecessorRequestMsg;
import org.peerfact.impl.simengine.Simulator;

/**
 * Implementing a centralized DHT overlay, whose organization of the centralized
 * index is similar to the distributed index of Chord
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 08.12.2008
 */
public class GetPredecessorOperation extends
		AbstractCIClientOperation<CIOverlayContact> implements
		TransMessageCallback {

	private PredecessorRequestMsg request;

	private PredecessorReplyMsg reply;

	private int msgID = -2;

	private int retry;

	public GetPredecessorOperation(CIClientNode component,
			OperationCallback<CIOverlayContact> callback) {
		super(component, callback);
		CIOverlayID ownOID = getComponent().getOwnOverlayID();
		CIOverlayID serverOID = getComponent().getServerOverlayContact()
				.getOverlayID();
		request = new PredecessorRequestMsg(ownOID, serverOID, ownOID,
				getOperationID());
		retry = 0;
	}

	@Override
	protected void execute() {
		TransInfo serverTransInfo = getComponent().getServerOverlayContact()
				.getTransInfo();
		msgID = getComponent().getTransLayer().sendAndWait(request,
				serverTransInfo, getComponent().getPort(), TransProtocol.UDP,
				this, 5 * Simulator.SECOND_UNIT);
		log.info("[Client] Initiating transMessage with id " + msgID
				+ "-->opID " + getOperationID());

	}

	@Override
	public CIOverlayContact getResult() {
		return reply.getResult();
	}

	@Override
	public void messageTimeoutOccured(int commId) {
		log
				.info(retry
						+ ". GetPredecessorOperation failed @ "
						+ getComponent().getOwnOverlayContact().getTransInfo()
								.getNetId()
						+ " due to Message-timeout of transMessage with ID = "
						+ commId);
		if (retry < 3) {
			retry = retry + 1;
			execute();
		} else {
			retry = 0;
			operationFinished(false);
		}
	}

	@Override
	public void receive(Message msg, TransInfo senderInfo, int commId) {
		retry = 0;
		reply = (PredecessorReplyMsg) msg;
		if (request.getOpID() == reply.getOpID()) {
			log.info("[Client] transMessage with ID = " + commId
					+ " is received");
			operationFinished(true);
		} else {
			log
					.error("[Client] The opID send does not equal the opID received");
		}

	}

}
