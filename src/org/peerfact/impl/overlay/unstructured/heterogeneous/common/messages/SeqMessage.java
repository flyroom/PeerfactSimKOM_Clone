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

/**
 * 
 */
package org.peerfact.impl.overlay.unstructured.heterogeneous.common.messages;

import org.peerfact.api.common.Message;

/**
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public abstract class SeqMessage implements Message {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2822282411787059138L;

	@Override
	public Message getPayload() {
		return this;
	}

	private int seqNo = -1;

	/**
	 * Returns the sequence number of this message.
	 * 
	 * @return
	 */
	public int getSeqNumber() {
		return seqNo;
	}

	/**
	 * Sets the sequence number of this message to the given value.
	 * 
	 * @param seqNo
	 */
	public void setSeqNumber(int seqNo) {
		this.seqNo = seqNo;
	}

}
