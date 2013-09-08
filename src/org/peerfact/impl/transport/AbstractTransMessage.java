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

package org.peerfact.impl.transport;

import org.peerfact.api.common.Message;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.api.transport.TransProtocol;

/**
 * The base class of all transport layer messages.
 * 
 * @author <info@peerfact.org>
 * @version 05/06/2011
 */
public abstract class AbstractTransMessage implements Message {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8824675492568393592L;

	protected TransProtocol protocol;

	protected int commId;

	protected Message payload;

	protected short srcPort;

	protected short dstPort;

	protected TransInfo senderTransInfo;

	protected TransInfo receiverTransInfo;

	protected boolean isReply;

	public int getCommId() {
		return this.commId;
	}

	public void setCommId(int commId) {
		this.commId = commId;
	}

	public short getSenderPort() {
		return this.srcPort;
	}

	public short getReceiverPort() {
		return this.dstPort;
	}

	public TransProtocol getProtocol() {
		return protocol;
	}

	public TransInfo getSenderTransInfo() {
		return senderTransInfo;
	}

	public TransInfo getReceiverTransInfo() {
		return receiverTransInfo;
	}

	public boolean isReply() {
		return this.isReply;
	}

}
