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

import java.util.Set;

import org.peerfact.api.common.Message;
import org.peerfact.api.overlay.dht.DHTKey;
import org.peerfact.api.transport.TransInfo;


/**
 * Used to inform another peer that he should issue a replication even though he
 * is not the closest node. Practically delegates the replication process to
 * another peer storing a replica. A set of nodes that should be ignored during
 * the replication process is past along as well.
 */
public class IssueReplicationMessage extends ReplicationDHTAbstractMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4749168363575441560L;

	private DHTKey<?> key;

	private Set<TransInfo> ignore;

	public IssueReplicationMessage(DHTKey<?> key, Set<TransInfo> ignore) {
		this.key = key;
		this.ignore = ignore;
	}

	/**
	 * Key of the transmitted DHTObject
	 * 
	 * @return
	 */
	public DHTKey<?> getKey() {
		return key;
	}

	@Override
	public Message getPayload() {
		return super.getPayload();
	}

	public Set<TransInfo> getIgnore() {
		return ignore;
	}

	@Override
	public long getSize() {
		// TODO add distance
		return super.getSize() + key.getTransmissionSize() + ignore.size() * 6;
	}

}
