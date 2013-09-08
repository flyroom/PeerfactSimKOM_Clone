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

package org.peerfact.impl.overlay.dht.pastry.operations;

import org.peerfact.api.common.Message;
import org.peerfact.impl.overlay.dht.pastry.components.PastryConstants;
import org.peerfact.impl.overlay.dht.pastry.components.PastryContact;
import org.peerfact.impl.overlay.dht.pastry.components.PastryNode;
import org.peerfact.impl.overlay.dht.pastry.components.TransmissionCallback.Failed;
import org.peerfact.impl.overlay.dht.pastry.components.TransmissionCallback.Succeeded;
import org.peerfact.impl.overlay.dht.pastry.messages.MsgTransInfo;
import org.peerfact.impl.overlay.dht.pastry.messages.RequestNeighborhooodSetMsg;

/**
 * @author Julius Rückert <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class RequestNeighborhoodSetOperation extends
		AbstractPastryOperation<Object> implements Failed, Succeeded {

	PastryContact targetNode;

	PastryNode node;

	public RequestNeighborhoodSetOperation(PastryNode node,
			PastryContact targetNode) {
		super(node);
		this.node = node;
		this.targetNode = targetNode;
	}

	@Override
	public void execute() {
		if (!node.isPresent()) {
			operationFinished(false);
			return;
		}

		scheduleOperationTimeout(PastryConstants.OP_JOIN_TIMEOUT);

		RequestNeighborhooodSetMsg msg = new RequestNeighborhooodSetMsg(
				node.getOverlayID(), targetNode.getOverlayID());

		node.getMsgHandler().sendMsg(
				new MsgTransInfo<PastryContact>(msg, targetNode), this);

		/*
		 * FIXME: Is there a better solution? Do we need this operation? Maybe
		 * just move code to LeafSet.
		 */
	}

	@Override
	public Object getResult() {
		// There is no result
		return null;
	}

	@Override
	public void transmissionSucceeded(Message msg, Message reply) {
		operationFinished(true);
	}

	@Override
	public void transmissionFailed(Message msg) {
		operationFinished(false);
	}

}
