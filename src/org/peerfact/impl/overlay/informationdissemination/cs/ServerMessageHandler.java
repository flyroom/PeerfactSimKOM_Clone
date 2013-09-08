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

package org.peerfact.impl.overlay.informationdissemination.cs;

import java.awt.Point;

import org.apache.log4j.Logger;
import org.peerfact.api.common.Message;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.api.transport.TransMessageListener;
import org.peerfact.impl.overlay.informationdissemination.cs.exceptions.FullServerException;
import org.peerfact.impl.overlay.informationdissemination.cs.messages.ErrorMessage;
import org.peerfact.impl.overlay.informationdissemination.cs.messages.JoinMessage;
import org.peerfact.impl.overlay.informationdissemination.cs.messages.JoinReplyMessage;
import org.peerfact.impl.overlay.informationdissemination.cs.messages.LeaveMessage;
import org.peerfact.impl.overlay.informationdissemination.cs.messages.UpdatePositionServerMessage;
import org.peerfact.impl.overlay.informationdissemination.cs.util.CSConstants.ERROR_TYPES;
import org.peerfact.impl.transport.TransMsgEvent;
import org.peerfact.impl.util.logging.SimLogger;


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
 * The incoming message handler for the server.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 01/06/2011
 * 
 */
public class ServerMessageHandler implements TransMessageListener {

	/**
	 * The logger for this class
	 */
	final static Logger log = SimLogger.getLogger(ServerMessageHandler.class);

	/**
	 * The {@link ServerNode}, which is associated with this message handler
	 */
	private ServerNode<?, ?> serverNode;

	/**
	 * Creates this class and set the server node.
	 * 
	 * @param serverNode
	 *            server node, which register the message handler
	 */
	public ServerMessageHandler(ServerNode<?, ?> serverNode) {
		this.serverNode = serverNode;
	}

	@Override
	public void messageArrived(TransMsgEvent receivingEvent) {
		Message message = receivingEvent.getPayload();
		if (message instanceof JoinMessage) {
			JoinMessage msg = (JoinMessage) message;

			handleJoinMessage(msg, receivingEvent);

		} else if (message instanceof LeaveMessage) {
			LeaveMessage msg = (LeaveMessage) message;

			handleLeaveMessage(msg);

		} else if (message instanceof UpdatePositionServerMessage) {
			UpdatePositionServerMessage msg = (UpdatePositionServerMessage) message;

			handlePositionUpdate(msg);
		}

	}

	private void handlePositionUpdate(UpdatePositionServerMessage msg) {
		ClientNodeInfo nodeInfo = msg.getClientNodeInfo();
		ClientID id = (ClientID) nodeInfo.getID();

		serverNode.getStorage().updateClientInfo(id, nodeInfo);
	}

	private void handleLeaveMessage(LeaveMessage msg) {
		ClientID id = msg.getClientID();
		serverNode.getStorage().removeClient(id);

		serverNode.stopDissemination(id);
	}

	private void handleJoinMessage(JoinMessage msg, TransMsgEvent receivingEvent) {
		ClientID id = serverNode.getUniqueClientID();
		Point position = msg.getPosition();
		int aoi = msg.getAoi();
		TransInfo transInfo = receivingEvent.getSenderTransInfo();

		// create ClientNodeInfo
		ClientNodeInfo nodeInfo = new ClientNodeInfo(position, aoi, id);
		// create ClientContact
		ClientContact contact = new ClientContact(id, transInfo);
		try {
			serverNode.getStorage().addClient(id, contact, nodeInfo);
			serverNode.startDissemination(id);

			JoinReplyMessage replyMsg = new JoinReplyMessage(id,
					serverNode.getTransInfo());

			serverNode.getTransLayer().sendReply(replyMsg, receivingEvent,
					serverNode.getPort(), receivingEvent.getProtocol());

		} catch (FullServerException e) {
			ErrorMessage replyMsg = new ErrorMessage(ERROR_TYPES.FULL_SERVER);

			serverNode.getTransLayer().sendReply(replyMsg, receivingEvent,
					serverNode.getPort(), receivingEvent.getProtocol());
		}
	}
}
