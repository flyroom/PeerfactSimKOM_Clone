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

package org.peerfact.impl.service.dhtstorage.replication.messages;

import java.util.List;

import org.peerfact.api.common.Message;
import org.peerfact.api.overlay.dht.DHTKey;
import org.peerfact.api.overlay.dht.DHTValue;
import org.peerfact.api.transport.TransInfo;


/**
 * Message used to replicate a DHTObject.
 * 
 * @author Bjoern Richerzhagen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class StoreReplicationMessage extends ReplicationDHTAbstractMessage
		implements Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2717296786570564471L;

	private DHTKey<?> key;

	private transient DHTValue object;

	private List<TransInfo> duplicates;

	/**
	 * Create a new Store-Republication Message
	 * 
	 * @param key
	 *            Key of the object to store
	 * @param object
	 *            The Object to store
	 * @param duplicates
	 *            List of Contacts this Object is replicated on
	 */
	public StoreReplicationMessage(DHTKey<?> key, DHTValue object,
			List<TransInfo> duplicates) {
		this.key = key;
		this.object = object;
		this.duplicates = duplicates;
	}

	/**
	 * Use clone to create multiple instances of this message to send to all
	 * receivers
	 */
	@Override
	public StoreReplicationMessage clone() {
		// FIXME: clone() object? Maybe needed for update() functionality?
		return new StoreReplicationMessage(key, object, duplicates);
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

	/**
	 * List of Contacts this Message was sent to, first entry is the root-Node
	 * of the object
	 * 
	 * @return
	 */
	public List<TransInfo> getDuplicatesContacts() {
		return duplicates;
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
				+ object.getTransmissionSize() + (duplicates.size() * 6);
	}

}
