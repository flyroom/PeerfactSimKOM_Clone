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

import java.util.List;
import java.util.Vector;

import org.peerfact.api.common.Message;
import org.peerfact.impl.overlay.informationdissemination.cs.ClientNodeInfo;
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
 * The update Messages from the Server to the clients. It contains a list of
 * {@link ClientNodeInfo}s, which are in the area of interest radius of the
 * client.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 01/06/2011
 * 
 */
public class UpdatePositionClientMessage extends CSAbstractMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5687700295818653443L;

	/**
	 * A list of {@link ClientNodeInfo}s.
	 */
	List<ClientNodeInfo> nodeInfos;

	/**
	 * Sets the list of ClientNodeInfos and the message type.
	 * 
	 * @param nodeInfos
	 *            A list of ClientNodeInfos.
	 */
	public UpdatePositionClientMessage(List<ClientNodeInfo> nodeInfos) {
		super(MSG_TYPE.UPDATE_POSITION_CLIENT_MESSAGE);
		if (nodeInfos != null) {
			this.nodeInfos = nodeInfos;
		} else {
			this.nodeInfos = new Vector<ClientNodeInfo>();
		}
	}

	@Override
	public long getSize() {
		long size = 0;
		if (nodeInfos.size() > 0) {
			long temp = nodeInfos.get(0).getTransmissionSize();
			size = nodeInfos.size() * temp;
		}
		return super.getSize() + size;
	}

	/**
	 * Gets a list of ClientNodeInfos, that are stored in the message.
	 * 
	 * @return A list of ClientNodeInfos.
	 */
	public List<ClientNodeInfo> getClientNodeInfos() {
		List<ClientNodeInfo> result = new Vector<ClientNodeInfo>(nodeInfos);
		return result;
	}

	@Override
	public Message getPayload() {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((nodeInfos == null) ? 0 : nodeInfos.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof UpdatePositionClientMessage)) {
			return false;
		}
		UpdatePositionClientMessage other = (UpdatePositionClientMessage) obj;
		if (nodeInfos == null) {
			if (other.nodeInfos != null) {
				return false;
			}
		} else if (!nodeInfos.equals(other.nodeInfos)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuffer temp = new StringBuffer();
		temp.append("[ MsgType: ");
		temp.append(getMsgType());
		temp.append(", clientNodeInfos: ");
		temp.append(getClientNodeInfos());
		temp.append(" ]");
		return temp.toString();
	}

}
