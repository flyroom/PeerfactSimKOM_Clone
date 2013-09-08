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

import java.util.List;

import org.apache.log4j.Logger;
import org.peerfact.api.common.Message;
import org.peerfact.api.transport.TransMessageCallback;
import org.peerfact.api.transport.TransMessageListener;
import org.peerfact.impl.overlay.informationdissemination.cs.messages.ErrorMessage;
import org.peerfact.impl.overlay.informationdissemination.cs.messages.UpdatePositionClientMessage;
import org.peerfact.impl.simengine.Simulator;
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
 * This class is the Message Handler for a client. It handles the incoming
 * messages, which are not send with a {@link TransMessageCallback}.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 01/06/2011
 */
public class ClientMessageHandler implements TransMessageListener {

	/**
	 * The logger for this class
	 */
	final static Logger log = SimLogger.getLogger(ClientMessageHandler.class);

	/**
	 * The node, which adds this Message Handler.
	 */
	private ClientNode node;

	public ClientMessageHandler(ClientNode clientNode) {
		this.node = clientNode;
	}

	/**
	 * Handles the incoming messages. The message types
	 * {@link UpdatePositionClientMessage} and {@link ErrorMessage} will be
	 * handled.
	 */
	@Override
	public void messageArrived(TransMsgEvent receivingEvent) {
		Message message = receivingEvent.getPayload();

		if (message instanceof UpdatePositionClientMessage) {
			UpdatePositionClientMessage msg = (UpdatePositionClientMessage) message;
			List<ClientNodeInfo> nodeInfos = msg.getClientNodeInfos();

			node.getStorage().replaceNeighbors(nodeInfos);
			node.setLastUpdate(Simulator.getCurrentTime());

		} else if (message instanceof ErrorMessage) {
			ErrorMessage msg = (ErrorMessage) message;
			log.error("It arrived an ErrorMessage with ErrorType: "
					+ msg.getErrorType());
		}

	}
}
