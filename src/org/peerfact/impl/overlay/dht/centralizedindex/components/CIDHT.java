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

package org.peerfact.impl.overlay.dht.centralizedindex.components;

import java.math.BigInteger;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.peerfact.api.network.NetID;
import org.peerfact.api.overlay.OverlayRoutingTable;
import org.peerfact.impl.network.IPv4NetID;
import org.peerfact.impl.util.logging.SimLogger;


/**
 * Implementing a centralized DHT overlay, whose organization of the centralized
 * index is similar to the distributed index of Chord
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 08.12.2008
 */
public class CIDHT implements
		OverlayRoutingTable<CIOverlayID, CIOverlayContact> {

	private static Logger log = SimLogger.getLogger(CIDHT.class);

	private LinkedHashMap<BigInteger, CIOverlayContact> dhtTable;

	public CIDHT() {
		dhtTable = new LinkedHashMap<BigInteger, CIOverlayContact>();
	}

	@Override
	public void addContact(CIOverlayContact contact) {
		if (dhtTable.containsKey(contact.getOverlayID().getID())) {
			log.debug("Cannot add contact " + contact.toString()
					+ ", because Entry with overlayID "
					+ contact.getOverlayID().getID()
					+ " as key, already exitsts");
		} else {
			BigInteger key = contact.getOverlayID().getID();
			dhtTable.put(key, contact);
			log.debug("Size of the DHT after adding " + key + " is "
					+ dhtTable.size());
		}
	}

	@Override
	public List<CIOverlayContact> allContacts() {
		// Not needed
		return null;
	}

	@Override
	public void clearContacts() {
		dhtTable.clear();
		log.debug("Cleared the DHT");
	}

	public boolean containsOverlayID(CIOverlayID oid) {
		if (dhtTable.containsKey(oid.getID())) {
			return true;
		} else {
			return false;
		}
	}

	public CIOverlayContact getPredecessor(CIOverlayID oid) {
		if (dhtTable.size() > 0) {
			if (dhtTable.containsKey(oid.getID())) {
				// Creating a sorted list out of a LinkedHashMap
				Set<BigInteger> keySet = dhtTable.keySet();
				List<BigInteger> list = Collections.list(Collections
						.enumeration(keySet));
				Collections.sort(list);

				// Search the Predecessor of the node with the CIOverlayID
				// oid
				int iter = 0;
				boolean flag = true;
				while (flag) {
					if (oid.getID().compareTo((list.get(iter))) == 0) {
						flag = false;
					} else {
						iter++;
					}
				}
				if (iter == 0) {
					iter = list.size();
				}
				CIOverlayContact contact = dhtTable
						.get(list.get(iter - 1));
				log.debug("Looked up node " + contact.toString() + " for key "
						+ oid.getID());
				return contact;

			} else {
				log.warn("No NapsterClient with OverlayID " + oid.getID()
						+ " is registered");
				return null;
			}

		} else {
			log
					.error("all nodes left the napster-overlay. This request is an orphan");
			return null;
		}
	}

	@Override
	public CIOverlayContact getContact(CIOverlayID oid) {
		if (dhtTable.containsKey(oid.getID())) {
			CIOverlayContact contact = dhtTable.get(oid.getID());
			log.debug("Returned OverlayContact " + contact.toString()
					+ " from the DHT");
			return contact;
		} else {
			log.debug("No entry with OverlayID " + oid.getID() + " was found");
			return null;
		}
	}

	// only for an workaround
	public void removeContact(NetID id) {
		IPv4NetID ip = (IPv4NetID) id;
		removeContact(new CIOverlayID(ip));
	}

	@Override
	public void removeContact(CIOverlayID oid) {
		CIOverlayContact contact = dhtTable.remove(oid.getID());
		if (contact != null) {
			log.debug("Size of the DHT after deleting is " + dhtTable.size());
		} else {
			log.warn("No entry with OverlayID " + oid.getID()
					+ " could be removed");
		}
	}

	public CIOverlayContact nodeLookup(CIOverlayKey key) {
		// Creating a sorted list out of a LinkedHashMap
		Set<BigInteger> keySet = dhtTable.keySet();
		List<BigInteger> list = Collections.list(Collections
				.enumeration(keySet));
		Collections.sort(list);

		// Search the CIOverlayContact of the node, which is responsible
		// for the given key
		if (dhtTable.size() > 0) {
			int iter = 0;
			boolean flag = true;
			while (flag) {
				if (key.getID().compareTo(list.get(iter)) < 1) {
					flag = false;
				} else {
					iter++;
					if (iter == list.size()) {
						flag = false;
						iter = 0;
					}
				}
			}
			CIOverlayContact contact = dhtTable.get(list.get(iter));
			log.debug("Looked up node " + contact.toString() + " for key "
					+ key.getID());
			return contact;
		} else {
			log
					.error("all nodes left the napster-overlay. This request is an orphant");
			return null;
		}
	}

}
