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

package org.peerfact.api.overlay.dht;

import java.util.Set;

/**
 * Common API for upcalls by DHT-Overlays, used to inform a service or
 * application of new Objects that are stored on this Node. A possible use is a
 * generic replication mechanism for DHT-Overlays and the Implementation of the
 * "pure" DHT outside of the Node for consistent behaviour.
 * 
 * 
 * @author Bjoern Richerzhagen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public interface DHTListener<K extends DHTKey<?>> {

	/**
	 * Add a new Object to this nodes' part of the DHT
	 * 
	 * @param key
	 *            the key of the object to store
	 * @param value
	 *            the object to store
	 */
	public void addDHTEntry(K key, DHTValue value);

	/**
	 * Remove an Object from this nodes' part of the DHT
	 * 
	 * @param key
	 *            the key of the object
	 */
	public void removeDHTEntry(K key);

	/**
	 * get a stored DHTEntry
	 * 
	 * @param key
	 * @return null, if Object does not exist
	 */
	public DHTEntry<K> getDHTEntry(K key);

	/**
	 * Get a stored DHTValue
	 * 
	 * @param key
	 * @return null, if object does not exist
	 */
	public DHTValue getDHTValue(K key);

	/**
	 * get all stored Entries
	 * 
	 * @return
	 */
	public Set<DHTEntry<K>> getDHTEntries();

	/**
	 * Number of stored Objects
	 * 
	 * @return
	 */
	public int getNumberOfDHTEntries();

}
