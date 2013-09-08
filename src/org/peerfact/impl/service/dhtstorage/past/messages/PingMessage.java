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

package org.peerfact.impl.service.dhtstorage.past.messages;

import java.math.BigInteger;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.peerfact.api.overlay.dht.DHTKey;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.impl.overlay.dht.chord.base.components.ChordID;
import org.peerfact.impl.simengine.Simulator;


/**
 * Simple PING-Message, to check if a contact is still alive. Contains
 * information about all value stored by the sender.
 */
public class PingMessage extends ReplicationDHTAbstractMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3684080643555192819L;

	private Map<DHTKey<?>, BigInteger> keyList;

	private Set<TransInfo> failedNodes;

	private long time;

	public PingMessage(Map<DHTKey<?>, BigInteger> key,
			Set<TransInfo> failedNodes) {
		this.keyList = key;
		if (failedNodes != null) {
			this.failedNodes = new LinkedHashSet<TransInfo>(failedNodes);
		}
		time = Simulator.getCurrentTime();
	}

	@Override
	public long getSize() {
		int size = 0;
		// for(DHTKey key : keyList.keySet())
		// size+= key.getTransmissionSize();
		// for(int v : keyList.values()) {
		// size += Math.ceil(Math.log10(v)/Math.log10(2));
		// }
		size += keyList.size() * (ChordID.KEY_BIT_LENGTH / 8 + 1);
		if (failedNodes != null) {
			size += failedNodes.size() * 6;
		}
		return size;
	}

	public Set<DHTKey<?>> getKeySet() {
		return keyList.keySet();
	}

	public BigInteger getDistance(DHTKey<?> key) {
		return keyList.get(key);
	}

	public Set<TransInfo> getFailedNodes() {
		return failedNodes;
	}

	public long getTime() {
		return time;
	}

}
