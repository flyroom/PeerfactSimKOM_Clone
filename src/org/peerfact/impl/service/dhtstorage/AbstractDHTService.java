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

package org.peerfact.impl.service.dhtstorage;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.peerfact.api.common.Host;
import org.peerfact.api.common.SupportOperations;
import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.api.overlay.OverlayID;
import org.peerfact.api.overlay.dht.DHTEntry;
import org.peerfact.api.overlay.dht.DHTKey;
import org.peerfact.api.overlay.dht.DHTListener;
import org.peerfact.api.overlay.dht.DHTListenerSupported;
import org.peerfact.api.overlay.dht.DHTValue;
import org.peerfact.impl.util.logging.SimLogger;


/**
 * 
 * @author Bjoern Richerzhagen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
abstract public class AbstractDHTService<K extends DHTKey<?>> implements
		SupportOperations, DHTListener<K> {

	private Host host;

	private DHTListenerSupported<OverlayID<?>, OverlayContact<OverlayID<?>>, K> node;

	private Set<DHTEntry<K>> storedEntries;

	protected static Logger log = SimLogger.getLogger(AbstractDHTService.class);

	/**
	 * Create a service and register as Listener at the DHTNode
	 * 
	 * @param node
	 */
	public AbstractDHTService(
			DHTListenerSupported<OverlayID<?>, OverlayContact<OverlayID<?>>, K> node) {
		storedEntries = new LinkedHashSet<DHTEntry<K>>();
		if (node != null) {
			this.node = node;
			node.registerDHTListener(this);
		}
	}

	@Override
	public void addDHTEntry(K key, DHTValue value) {
		addDHTEntry(new SimpleDHTEntry<K>(key, value));
	}

	protected void addDHTEntry(DHTEntry<K> entry) {
		if (!storedEntries.add(entry)) {
			// update an entry, as the value might have changed
			// add will not be executed if Set already contained the object
			removeDHTEntry(entry.getKey());
			storedEntries.add(entry);
		}
	}

	@Override
	public void removeDHTEntry(K key) {
		Iterator<DHTEntry<K>> it = storedEntries.iterator();
		while (it.hasNext()) {
			DHTEntry<?> entry = it.next();
			if (entry.getKey().equals(key)) {
				it.remove();
			}
		}
	}

	@Override
	public DHTEntry<K> getDHTEntry(K key) {
		Iterator<DHTEntry<K>> it = storedEntries.iterator();
		while (it.hasNext()) {
			DHTEntry<K> entry = it.next();
			if (entry.getKey().equals(key)) {
				return entry;
			}
		}
		return null;
	}

	@Override
	public DHTValue getDHTValue(K key) {
		DHTEntry<K> entry = getDHTEntry(key);
		if (entry == null) {
			return null;
		}
		return entry.getValue();
	}

	@Override
	public Set<DHTEntry<K>> getDHTEntries() {
		return storedEntries;
	}

	@Override
	public int getNumberOfDHTEntries() {
		return storedEntries.size();
	}

	@Override
	public void setHost(Host host) {
		this.host = host;
	}

	@Override
	public Host getHost() {
		return host;
	}

	protected DHTListenerSupported<OverlayID<?>, OverlayContact<OverlayID<?>>, K> getNode() {
		return node;
	}

}
