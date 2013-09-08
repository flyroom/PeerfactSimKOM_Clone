/*
 * Copyright (c) 2012-2013 Open Source Community - <http://www.peerfact.org>
 * Copyright (c) 2011-2012 University of Paderborn - UPB
 * Copyright (c) 2005-2011 KOM - Multimedia Communications Lab
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */

package org.peerfact.impl.overlay.dht.centralizedstorage.operations;

import org.peerfact.api.common.Message;
import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.api.transport.TransMessageCallback;
import org.peerfact.impl.common.AbstractOperation;
import org.peerfact.impl.overlay.dht.centralizedstorage.components.CSServerNode;
import org.peerfact.impl.simengine.Simulator;

public class ServerJoinOperation extends
		AbstractOperation<CSServerNode, Object>
		implements TransMessageCallback {

	CSServerNode node;

	public ServerJoinOperation(CSServerNode node,
			OperationCallback<Object> callback) {
		super(node, callback);
		this.node = node;
	}

	@Override
	public void execute() {
		node.getBootstrap().registerNode(node);
		operationFinished(true);

		// This information is related to execution of the napster application
		log.info("[" + this.getComponent().getHost() + "] Server connected @ "
				+ Simulator.getSimulatedRealtime());
	}

	@Override
	protected void operationTimeoutOccured() {
		operationFinished(false);
	}

	@Override
	public void messageTimeoutOccured(int commId) {
		operationFinished(false);
	}

	@Override
	public Object getResult() {
		return null;
	}

	@Override
	public void receive(Message msg, TransInfo senderAddr, int commId) {
		log.warn("Received unexpected message: " + msg);
	}

}
