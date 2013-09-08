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
import java.util.LinkedHashMap;
import java.util.Map;

import org.peerfact.api.common.Message;
import org.peerfact.api.overlay.dht.DHTKey;
import org.peerfact.api.overlay.dht.DHTValue;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.impl.service.dhtstorage.past.PASTObject;


/**
 * Contains a file that the receiver should store and a list of nodes that store
 * the file as well.
 */
public class StoreReplicationMessage extends ReplicationDHTAbstractMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5678361065845376577L;

	private DHTKey<?> key;

	private DHTValue object;

	private Map<TransInfo, BigInteger> duplicates;

	/**
	 * Create a new Store-Republication Message
	 * 
	 * @param key
	 *            Key of the object to store
	 * @param object
	 *            The Object to store
	 * @param map
	 *            List of Contacts this Object is replicated on
	 */
	public StoreReplicationMessage(DHTKey<?> key, DHTValue object,
			Map<TransInfo, BigInteger> map) {
		this.key = key;
		this.object = object;
		this.duplicates = new LinkedHashMap<TransInfo, BigInteger>(map);
	}

	/**
	 * Transmitted DHTObject
	 * 
	 * @return
	 */
	public DHTValue getObject() {
		return object;
	}

	/**
	 * Key of the transmitted DHTObject
	 * 
	 * @return
	 */
	public DHTKey<?> getKey() {
		return key;
	}

	public Map<TransInfo, BigInteger> getDuplicatesContacts() {
		return duplicates;
	}

	public PASTObject getReplicationObject() {
		return new PASTObject(key, object, duplicates);
	}

	@Override
	public String toString() {
		return "STORE Replication";
	}

	@Override
	public Message getPayload() {
		return super.getPayload();
	}

	@Override
	public long getSize() {
		return super.getSize() + key.getTransmissionSize()
				+ object.getTransmissionSize() + (duplicates.size() * 7);
	}

}
