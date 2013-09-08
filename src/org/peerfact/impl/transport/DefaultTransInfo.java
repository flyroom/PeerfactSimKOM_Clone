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

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import org.peerfact.Constants;
import org.peerfact.api.network.NetID;
import org.peerfact.api.transport.TransInfo;

/**
 * Container class which includes information needed to communicate at the
 * transport layer. This includes the <code>NetID</code> and the port of a host.
 * 
 * @author <info@peerfact.org>
 * @version 05/06/2011
 */
public class DefaultTransInfo implements TransInfo, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3215599418915298861L;

	private final NetID netId;

	private final short port;

	static final Map<DefaultTransInfo, DefaultTransInfo> transInfos = new LinkedHashMap<DefaultTransInfo, DefaultTransInfo>();

	/**
	 * Set to true if you want single instances for the DefaultTransInfo.
	 */
	private static final boolean SINGLE_INSTANCE_MODE = true;

	/**
	 * Returns an instance of <code>DefaultTransInfo</code> to work with
	 * 
	 * Comment: We use the singleton pattern here to decrease memory
	 * consumption, since one instance per trans info is created only.
	 * 
	 * @param netID
	 * @param port
	 * @return the unique instance of <code>DefaultTransInfo</code> for the
	 *         given <code>NetID</code> and port
	 */
	public static DefaultTransInfo getTransInfo(NetID netID, short port) {
		DefaultTransInfo newInfo = new DefaultTransInfo(netID, port);
		if (SINGLE_INSTANCE_MODE) {
			DefaultTransInfo info = transInfos.get(newInfo);
			if (info != null) {
				return info;
			}
			transInfos.put(newInfo, newInfo);
		}
		return newInfo;
	}

	private DefaultTransInfo(NetID netId, short port) {
		this.netId = netId;
		this.port = port;
	}

	@Override
	public NetID getNetId() {
		return this.netId;
	}

	@Override
	public short getPort() {
		return this.port;
	}

	@Override
	public String toString() {
		return "{netId=" + netId + ", port= " + port + "}";
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((netId == null) ? 0 : netId.hashCode());
		result = PRIME * result + port;
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
		final DefaultTransInfo other = (DefaultTransInfo) obj;
		if (netId == null) {
			if (other.netId != null) {
				return false;
			}
		} else if (!netId.equals(other.netId)) {
			return false;
		}
		if (port != other.port) {
			return false;
		}
		return true;
	}

	@Override
	public long getTransmissionSize() {
		return this.netId.getTransmissionSize() + Constants.SHORT_SIZE;
	}

}
