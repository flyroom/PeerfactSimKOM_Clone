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

package org.peerfact.impl.overlay.informationdissemination.cs.operations;

import java.util.List;

import org.peerfact.api.common.Message;
import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.api.transport.TransMessageCallback;
import org.peerfact.impl.common.AbstractOperation;
import org.peerfact.impl.overlay.AbstractOverlayNode.PeerStatus;
import org.peerfact.impl.overlay.informationdissemination.cs.ClientID;
import org.peerfact.impl.overlay.informationdissemination.cs.ClientNode;
import org.peerfact.impl.overlay.informationdissemination.cs.messages.ErrorMessage;
import org.peerfact.impl.overlay.informationdissemination.cs.messages.JoinMessage;
import org.peerfact.impl.overlay.informationdissemination.cs.messages.JoinReplyMessage;
import org.peerfact.impl.overlay.informationdissemination.cs.util.CSConfiguration;


/**
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * This part of the Simulator is not maintained in the current version of
 * PeerfactSim.KOM. There is no intention of the authors to fix this
 * circumstances, since the changes needed are huge compared to overall benefit.
 * 
 * If you want it to work correctly, you are free to make the specific changes
 * and provide it to the community.
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! !!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * 
 * The join operation of a client. It sends a join message to the server and
 * receive in this class the response from the server.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version
 */
public class JoinOperation extends AbstractOperation<ClientNode, Object>
		implements TransMessageCallback {
	/**
	 * The logger for this class
	 */

	private ClientNode node;

	private int queryCommID;

	public JoinOperation(ClientNode component,
			OperationCallback<Object> callback) {
		super(component, callback);
		this.node = component;
	}

	/**
	 * Sends to the first entry in the BootstrapManager a join Message.
	 */
	@Override
	protected void execute() {
		List<TransInfo> transInfos = node.getBootstrapManager()
				.getBootstrapInfo();
		if (transInfos == null || transInfos.size() == 0) {
			log.warn("No bootstrap contact information");
		} else {
			int firstServer = 0;
			TransInfo serverTransInfo = transInfos.get(firstServer);

			JoinMessage msg = new JoinMessage(node.getPosition(), node.getAOI());

			// send the msg and wait. Callback is in this class.
			queryCommID = node.getTransLayer().sendAndWait(msg,
					serverTransInfo, node.getPort(),
					CSConfiguration.TRANSPORT_PROTOCOL, this,
					CSConfiguration.JOIN_TIME_OUT);
		}
	}

	@Override
	public Object getResult() {
		return null;
	}

	/**
	 * Receive the response from the server to the joinMessage. If the message
	 * the right and contains the needed information, the operation is
	 * successful finished. Otherwise is the operation failed.
	 */
	@Override
	public void receive(Message msg, TransInfo senderInfo, int commId) {
		if (node.getPeerStatus() == PeerStatus.TO_JOIN) {
			if (msg instanceof JoinReplyMessage) {
				if (this.queryCommID == commId) {
					JoinReplyMessage joinRe = (JoinReplyMessage) msg;
					ClientID id = joinRe.getClientId();

					// sets the needed Information
					node.setOverlayID(id);
					node.setServerTransInfo(joinRe.getServerTransInfo());
					operationFinished(true);
				} else {
					log.error(node.getOverlayID()
							+ " has get a JoinResponse with the wrong commId.");
					operationFinished(false);
				}
			} else if (msg instanceof ErrorMessage) {
				ErrorMessage errMsg = (ErrorMessage) msg;
				log.error(node.getOverlayID()
						+ " cannot connect with Server. The Server send the ErrorMessage with "
						+ errMsg.getErrorType());
				operationFinished(false);
			} else {
				log.warn(node.getOverlayID() + " has get a wrong Msg.");
				operationFinished(false);
			}
		}
		operationFinished(false);
	}

	@Override
	public void messageTimeoutOccured(int commId) {
		if (log.isInfoEnabled()) {
			log.info(node.getOverlayID() + " msg timeout in JoinOperation.");
		}

		if (this.queryCommID == commId) {
			operationFinished(false);
		}
	}

	/**
	 * Stops the operation.
	 */
	public void stop() {
		if (log.isInfoEnabled()) {
			log.info(node.getOverlayID() + " could not complete join.");
		}
		operationFinished(false);
	}

}
