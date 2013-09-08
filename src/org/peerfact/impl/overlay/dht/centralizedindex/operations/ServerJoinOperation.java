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
import org.peerfact.impl.common.AbstractOperation;
import org.peerfact.impl.overlay.dht.centralizedindex.components.CIServerNode;
import org.peerfact.impl.simengine.Simulator;

/**
 * Implementing a centralized DHT overlay, whose organization of the centralized
 * index is similar to the distributed index of Chord
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 08.12.2008
 */
public class ServerJoinOperation extends
		AbstractOperation<CIServerNode, Object> implements
		TransMessageCallback {

	CIServerNode node;

	public ServerJoinOperation(CIServerNode node,
			OperationCallback<Object> callback) {
		super(node, callback);
		this.node = node;
	}

	@Override
	public void execute() {
		operationFinished(true);

		// This information is related to execution of the napster application
		log.warn("[" + this.getComponent().getHost() + "] Server connected @ "
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
