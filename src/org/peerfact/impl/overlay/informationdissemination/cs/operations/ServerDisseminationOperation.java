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

import org.peerfact.api.common.OperationCallback;
import org.peerfact.impl.common.AbstractOperation;
import org.peerfact.impl.overlay.informationdissemination.cs.ClientContact;
import org.peerfact.impl.overlay.informationdissemination.cs.ClientID;
import org.peerfact.impl.overlay.informationdissemination.cs.ClientNodeInfo;
import org.peerfact.impl.overlay.informationdissemination.cs.ServerNode;
import org.peerfact.impl.overlay.informationdissemination.cs.messages.UpdatePositionClientMessage;
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
 * The operation of the server, to disseminate the positions of the clients to
 * one client.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 06/01/2011
 * 
 */
public class ServerDisseminationOperation extends
		AbstractOperation<ServerNode<?, ?>, ClientID> {

	/**
	 * The id of the receiver of this dissemination.
	 */
	private ClientID id;

	private ServerNode<?, ?> node;

	public ServerDisseminationOperation(ServerNode<?, ?> node, ClientID id,
			OperationCallback<ClientID> callback) {
		super(node, callback);
		this.node = node;
		this.id = id;
	}

	/**
	 * Derive all neighbors for the stored clientID, and send this list to the
	 * client.
	 */
	@Override
	protected void execute() {
		if (!isFinished()) {
			ClientContact contact = node.getStorage().getClientContact(id);
			List<ClientNodeInfo> nodeInfos = node.getStorage()
					.findNeighbors(id);
			UpdatePositionClientMessage msg = new UpdatePositionClientMessage(
					nodeInfos);

			node.getTransLayer().send(msg, contact.getTransInfo(),
					node.getPort(), CSConfiguration.TRANSPORT_PROTOCOL);

			this.operationFinished(true);
		}
		this.operationFinished(false);
	}

	@Override
	public ClientID getResult() {
		return this.id;
	}

	/**
	 * Stops this Operation.
	 */
	public void stop() {
		this.operationFinished(false);
	}

}
