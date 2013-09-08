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

package org.peerfact.impl.service.dhtstorage.replication;

import java.util.List;

import org.peerfact.api.overlay.dht.DHTKey;
import org.peerfact.api.overlay.dht.DHTValue;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.impl.service.dhtstorage.SimpleDHTEntry;
import org.peerfact.impl.simengine.Simulator;


/**
 * This class encapsulates a DHTObject stored in ReplicationDHTService
 * 
 * @author Bjoern Richerzhagen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class ReplicationDHTObject<K extends DHTKey<?>> extends
		SimpleDHTEntry<K> {

	/**
	 * timestamp when this item was republished
	 */
	private long timeOfLastRepublication;

	/**
	 * List of all Replications
	 */
	private List<TransInfo> replications;

	/**
	 * Root TransInfo
	 */
	private TransInfo root;

	/**
	 * Create a new Entry in this DHT
	 * 
	 * @param key
	 *            The DHTKey
	 * @param object
	 *            The DHTObject
	 * @param root
	 *            Root TransInfo, null if own Node is root
	 * @param replications
	 *            List of TransInfos this Object is also stored on
	 */
	public ReplicationDHTObject(K key, DHTValue object, TransInfo root,
			List<TransInfo> replications) {
		super(key, object);
		this.root = root;
		this.replications = replications;
		this.timeOfLastRepublication = Simulator.getCurrentTime();
	}

	/**
	 * get a List of all Replications TransInfos
	 * 
	 * @return
	 */
	public List<TransInfo> getReplications() {
		return replications;
	}

	public TransInfo getRoot() {
		return root;
	}

	public void updateTimestamp() {
		timeOfLastRepublication = Simulator.getCurrentTime();
	}

	public long getTimestamp() {
		return timeOfLastRepublication;
	}

	@Override
	public String toString() {
		return getKey().toString() + getValue().toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ReplicationDHTObject) {

			return ((ReplicationDHTObject<K>) obj).getKey().equals(getKey())
					&& ((ReplicationDHTObject<K>) obj).getTimestamp() == (getTimestamp());
		}
		return false;
	}

}
