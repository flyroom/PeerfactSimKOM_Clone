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

import org.peerfact.api.overlay.dht.DHTKey;

/**
 * 
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public class DeleteReplicationMessage<K extends DHTKey<?>> extends ReplicationDHTAbstractMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2731606499419728136L;

	private K key;

	/**
	 * Recipient must delete the Object with provided key
	 * 
	 * @param key
	 */
	public DeleteReplicationMessage(K key) {
		this.key = key;
	}

	/**
	 * Key of the object to delete
	 * 
	 * @return
	 */
	public K getKey() {
		return key;
	}

	@Override
	public String toString() {
		return "DELETE Replication";
	}

	@Override
	public long getSize() {
		return super.getSize() + key.getTransmissionSize();
	}

}
