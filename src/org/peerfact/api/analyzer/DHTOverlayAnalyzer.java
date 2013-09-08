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

package org.peerfact.api.analyzer;

import java.util.List;

import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.api.overlay.dht.DHTKey;
import org.peerfact.api.overlay.dht.DHTObject;


/**
 * DHTOverlayAnalyzers receive notifications about events on the DHT layer. This
 * way it is possible to collect data independent of the used overlay. To use
 * this kind of analyzer in a meaningful way you have to use an application that
 * uses the DHT layer.
 * 
 * @author Matthias Feldotto <info@peerfact.org>
 * 
 */
public interface DHTOverlayAnalyzer extends Analyzer {

	/**
	 * Informs about the start of a store of an object.
	 * 
	 * @param contact
	 *            the contact of the peer who has initiated the store
	 * @param key
	 *            the key to store
	 * @param object
	 *            the object to store
	 */
	public void storeInitiated(OverlayContact<?> contact, DHTKey<?> key,
			DHTObject object);

	/**
	 * Informs about the fail of a store of an object.
	 * 
	 * @param contact
	 *            the contact of the peer who has initiated the store
	 * @param key
	 *            the key to store
	 * @param object
	 *            the object to store
	 * @param failedHop
	 *            the contact of the peer that let the store failed
	 */
	public void storeFailed(OverlayContact<?> contact, DHTKey<?> key,
			DHTObject object);

	/**
	 * Informs about the finish of a store of an object.
	 * 
	 * @param contact
	 *            the contact of the peer who has initiated the store
	 * @param key
	 *            the key to store
	 * @param object
	 *            the object to store
	 * @param responsibleContacts
	 *            the contacts of the peers who are responsible for the object
	 * @param hops
	 *            the number of hops needed for the store
	 */
	public void storeFinished(OverlayContact<?> contact, DHTKey<?> key,
			DHTObject object, List<OverlayContact<?>> responsibleContacts);

	/**
	 * Informs about the start of a lookup for a specific key.
	 * 
	 * @param contact
	 *            the contact of the peer who has initiated the lookup
	 * @param key
	 *            the key to find
	 */
	public void lookupInitiated(OverlayContact<?> contact, DHTKey<?> key);

	/**
	 * Informs about the fail of a lookup.
	 * 
	 * @param contact
	 *            the contact of the peer who has initiated the lookup
	 * @param key
	 *            the key to lookup
	 * @param currentHop
	 *            the current contact
	 * @param hops
	 *            the number of hops until now
	 */
	public void lookupForwarded(OverlayContact<?> contact, DHTKey<?> key,
			OverlayContact<?> currentHop, int hops);

	/**
	 * Informs about the fail of a lookup.
	 * 
	 * @param contact
	 *            the contact of the peer who has initiated the lookup
	 * @param key
	 *            the key to lookup
	 * @param failedHop
	 *            the contact of the peer that let the lookup failed
	 */
	public void lookupFailed(OverlayContact<?> contact, DHTKey<?> key);

	/**
	 * Informs about the finish of a lookup with the responsible contact.
	 * 
	 * @param contact
	 *            the contact of the peer who has initiated the lookup
	 * @param key
	 *            the key to lookup
	 * @param responsibleContacts
	 *            the contacts of the peers who are responsible for the key
	 * @param hops
	 *            the number of hops needed for the lookup
	 */
	public void lookupFinished(OverlayContact<?> contact,
			DHTKey<?> key, List<OverlayContact<?>> responsibleContacts,
			int hops);

	/**
	 * Informs about the finish of a lookup with the result.
	 * 
	 * @param contact
	 *            the contact of the peer who has initiated the lookup
	 * @param key
	 *            the key to lookup
	 * @param hops
	 *            the number of hops needed for the lookup
	 */
	public void lookupFinished(OverlayContact<?> contact,
			DHTKey<?> key, DHTObject object, int hops);
}