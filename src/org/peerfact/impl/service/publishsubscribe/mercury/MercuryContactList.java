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

package org.peerfact.impl.service.publishsubscribe.mercury;

import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.Vector;

import org.peerfact.api.transport.TransProtocol;
import org.peerfact.impl.service.publishsubscribe.mercury.messages.MercurySendRange;


/**
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * This part of the Simulator is not maintained in the current version of
 * PeerfactSim.KOM. There is no intention of the authors to fix this
 * circumstances, since the changes needed are huge compared to overall benefit.
 * 
 * If you want it to work correctly, you are free to make the specific changes
 * and provide it to the community.
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! !!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * 
 * All known Contacts for one attribute. This List utilizes a tree-based
 * structure in oder to speed up the lookup-process.
 * 
 * @author Bjoern Richerzhagen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class MercuryContactList {

	private TreeSet<MercuryContactListEntry> sortedContacts = null;

	private MercuryService service;

	/**
	 * Create a Contact List, Contacts are sorted based on the lower bound of
	 * their attribute range
	 * 
	 * @param service
	 */
	public MercuryContactList(MercuryService service) {
		sortedContacts = new TreeSet<MercuryContactListEntry>(
				new Comparator<MercuryContactListEntry>() {
					@Override
					public int compare(MercuryContactListEntry o1,
							MercuryContactListEntry o2) {
						// Compare two Contact-Infos
						return o1.getContact().getRange()[0].compareTo(o2
								.getContact().getRange()[0]);
					}
				});
		this.service = service;
	}

	/**
	 * Get neighbor of provided node (ascending), used to forward subscriptions
	 * to next Hop possibly a lot faster than chord, as we use the DHT only once
	 * to get interval-borders
	 * 
	 * @return
	 */
	public MercuryContact getNeighbor(MercuryContact contact) {
		MercuryContactListEntry neighbor = sortedContacts
				.higher(new MercuryContactListEntry(contact));
		if (neighbor != null) {
			return neighbor.getContact();
		} else {
			return null;
		}
	}

	/**
	 * Get all recipients within a given range. This is used to speed up
	 * Subscription delivery: if a node has a small ammount of subscribers, it
	 * may publish a subscription directly to all Contacts in its range
	 * 
	 * @param minValue
	 * @param maxValue
	 * @return
	 */
	public List<MercuryContact> getAllRecipients(Comparable<Object> minValue,
			Comparable<Object> maxValue) {
		List<MercuryContact> ret = new Vector<MercuryContact>();
		for (MercuryContactListEntry entry : sortedContacts) {
			if (entry.shouldUpdate()) {
				// log.debug(Simulator.getFormattedTime(Simulator
				// .getCurrentTime())
				// + " Started RangeUpdate for Contact "
				// + actEntry.toString());
				entry.updateStarted();
				updateRangeInfo(entry);
			}
			if (entry.getContact().getRange()[0].compareTo(minValue) >= 0
					&& entry.getContact().getRange()[1].compareTo(maxValue) <= 0) {
				ret.add(entry.getContact());
			}
		}
		return ret;
	}

	/**
	 * Get a contact for one attribute (hub)
	 * 
	 * @param attribute
	 *            attribute to search a contact for
	 * @param value
	 *            value the contact should be responsible for
	 * @return
	 */
	public MercuryContact getContact(Comparable<Object> value) {
		if (sortedContacts.isEmpty()) {
			return null;
		}
		// sorted List allows for fast searching
		for (MercuryContactListEntry entry : sortedContacts) {
			if (entry.shouldUpdate()) {
				// log.debug(Simulator.getFormattedTime(Simulator
				// .getCurrentTime())
				// + " Started RangeUpdate for Contact "
				// + actEntry.toString());
				entry.updateStarted();
				updateRangeInfo(entry);
			}
			if (entry.getContact().getRange()[0].compareTo(value) <= 0
					&& entry.getContact().getRange()[1].compareTo(value) >= 0) {
				return entry.getContact();
			}
		}
		// wrap-around occured?
		MercuryContactListEntry last = sortedContacts.last();
		if (last.getContact().getRange()[0].compareTo(last.getContact()
				.getRange()[1]) > 0
				&& (last.getContact().getRange()[0].compareTo(value) <= 0 || last
						.getContact().getRange()[1].compareTo(value) >= 0)) {
			return last.getContact();
		}
		return null;
	}

	/**
	 * Same as getContact(), but if no contact matches exact range, nearest
	 * contact is returned. this ensures that the hub can still be reached
	 * 
	 * @param attributeName
	 * @param value
	 * @return
	 */
	public MercuryContact getNearestContact(Comparable<Object> value) {

		// Try to find perfect match:
		MercuryContact contact = getContact(value);
		if (contact != null) {
			return contact;
		}

		for (MercuryContactListEntry entry : sortedContacts) {
			if (entry.getContact().getRange()[0].compareTo(value) <= 0) {
				return entry.getContact();
			}
		}
		if (!sortedContacts.isEmpty()) {
			return sortedContacts.first().getContact();
		}
		return null;
	}

	/**
	 * Add a contact to the contact list.
	 * 
	 * @param contact
	 */
	public void addContact(MercuryContact contact) {
		// update stored Information, range
		if (contact.getTransInfo().equals(service.getOwnTransInfo())) {
			return;
		}

		MercuryContactListEntry newContact = null;
		for (MercuryContactListEntry entry : sortedContacts) {
			if (entry.getContact().equals(contact)) {
				entry.getContact().setRange(contact.getRange()[0],
						contact.getRange()[1]);
				newContact = entry;
				break;
			}
		}

		if (newContact == null) {
			newContact = new MercuryContactListEntry(contact);
			sortedContacts.add(newContact);
		}
		// TODO: wrap around is still a Problem. For now Bootstrapping prevents
		// this problem from occuring

		// update lower Bound of next Contact
		// null-pointer
		// MercuryContactListEntry higher = sortedContacts.higher(newContact);
		// if (higher != null) {
		// higher.getContact()
		// .setRange(newContact.getContact().getRange()[1], null);
		// } else {
		// // wrap-arond!
		// int i = 0;
		// i++;
		// }

		// update higher Bound of previous Contact
		// MercuryContactListEntry lower = sortedContacts.lower(newContact);
		// if (lower != null) {
		// lower.getContact()
		// .setRange(null, newContact.getContact().getRange()[0]);
		// } else {
		// int j = 0;
		// j++;
		// }

	}

	/**
	 * Request updated Range-Info for given Contact
	 * 
	 * @param contact
	 */
	private void updateRangeInfo(MercuryContactListEntry contact) {
		MercurySendRange msg = new MercurySendRange(service.getOwnContact(),
				true);
		service.getHost()
				.getTransLayer()
				.send(msg, contact.getContact().getTransInfo(),
						service.getPort(), TransProtocol.UDP);
	}

	@Override
	public String toString() {
		// return this.contacts.toString();
		return this.sortedContacts.toString();
	}

}
