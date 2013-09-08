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

package org.peerfact.impl.overlay.informationdissemination.cs.messages;

import org.peerfact.api.common.Message;
import org.peerfact.impl.overlay.informationdissemination.cs.util.CSConstants.MSG_TYPE;

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
 * An abstract Message for the Client/Server IDO-System. It provides the
 * transmit of the {@link MSG_TYPE}.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 01/06/2011
 * 
 */
public abstract class CSAbstractMessage implements Message {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8630740906950346373L;

	/**
	 * The message type of the message
	 */
	private MSG_TYPE msgType;

	/**
	 * The constructor of the Message. It sets the given msgType.
	 * 
	 * @param msgType
	 */
	public CSAbstractMessage(MSG_TYPE msgType) {
		this.msgType = msgType;
	}

	/**
	 * Gets the type of the message back.
	 * 
	 * @return The type of the message.
	 */
	public MSG_TYPE getMsgType() {
		return this.msgType;
	}

	@Override
	public long getSize() {
		// ein Byte fpr MSG_TYPE
		return 1;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((msgType == null) ? 0 : msgType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		CSAbstractMessage other = (CSAbstractMessage) obj;
		if (msgType != other.msgType) {
			return false;
		}
		return true;
	}

}
