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

package org.peerfact.impl.overlay.dht.can.messages;

import org.peerfact.Constants;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.impl.overlay.dht.can.components.CanOverlayContact;
import org.peerfact.impl.overlay.dht.can.components.CanOverlayID;

/**
 * 
 * Is neighbour is alive?
 * 
 * @author Bjoern Dollak <peerfact@kom.tu-darmstadt.de>
 * @version February 2010
 * 
 */
public class PingMsg extends CanMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4418016559814356985L;

	private TransInfo senderNode;

	private int operationId = 0;

	/**
	 * Is neighbour is alive? Remember: The operationID should be set, too.
	 * 
	 * @param sender
	 * @param receiver
	 * @param senderNode
	 *            CanOverlayContact of the sender node.
	 */
	public PingMsg(CanOverlayID sender, CanOverlayID receiver,
			CanOverlayContact senderNode) {
		super(sender, receiver);
		this.senderNode = senderNode.getTransInfo();
	}

	@Override
	public long getSize() {
		return super.getSize() + senderNode.getTransmissionSize()
				+ Constants.INT_SIZE;
	}

	public TransInfo getSenderNode() {
		return this.senderNode;
	}

	/**
	 * sets the operation ID of the TakeoverReplyOperation
	 * 
	 * @param operationId
	 */
	public void setOperationId(int operationId) {
		this.operationId = operationId;
	}

	public int getOperationId() {
		return operationId;
	}
}
