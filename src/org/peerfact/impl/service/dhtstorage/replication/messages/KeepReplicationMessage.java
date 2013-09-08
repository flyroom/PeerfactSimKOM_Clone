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

import org.peerfact.api.overlay.dht.DHTKey;
import org.peerfact.api.transport.TransInfo;


/**
 * Tells a replicator to keep the entry of a file which is beeing republished
 * 
 * @author Bjoern Richerzhagen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class KeepReplicationMessage extends ReplicationDHTAbstractMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1361677796259727172L;

	private DHTKey<?> key;

	private List<TransInfo> replications;

	/**
	 * Tell recipient to keep the Object with the provided Key, but update List
	 * of replications
	 * 
	 * @param key
	 * @param replications
	 *            List of Nodes replicating the content
	 */
	public KeepReplicationMessage(DHTKey<?> key, List<TransInfo> replications) {
		this.key = key;
		this.replications = replications;
	}

	/**
	 * Get the key of the object that should be kept
	 * 
	 * @return
	 */
	public DHTKey<?> getKey() {
		return key;
	}

	/**
	 * Get the List of Nodes storing this Object
	 * 
	 * @return
	 */
	public List<TransInfo> getReplications() {
		return replications;
	}

	@Override
	public String toString() {
		return "KEEP Replication";
	}

	@Override
	public long getSize() {
		return super.getSize() + key.getTransmissionSize()
				+ (replications.size() * 6);
	}

}
