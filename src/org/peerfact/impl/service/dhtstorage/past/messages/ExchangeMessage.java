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
import java.util.Map;

import org.peerfact.api.overlay.dht.DHTKey;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.impl.simengine.Simulator;


/**
 * Used to exchange information about all stored values.
 */
public class ExchangeMessage extends ReplicationDHTAbstractMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3420447332717366213L;

	private Map<DHTKey<?>, Map<TransInfo, BigInteger>> keyList;

	private long time;

	boolean reply;

	public ExchangeMessage(
			Map<DHTKey<?>, Map<TransInfo, BigInteger>> exchangeMap,
			boolean reply) {
		this.keyList = exchangeMap;
		time = Simulator.getCurrentTime();
		this.reply = reply;
	}

	@Override
	public long getSize() {
		int size = 0;
		for (DHTKey<?> key : keyList.keySet()) {
			size += key.getTransmissionSize();
		}
		for (Map<TransInfo, BigInteger> v : keyList.values()) {
			size += v.size() * 14;
		}
		return size;
	}

	public Map<DHTKey<?>, Map<TransInfo, BigInteger>> getKeySet() {
		return keyList;
	}

	public long getTime() {
		return time;
	}

	public boolean isReply() {
		return reply;
	}

}
