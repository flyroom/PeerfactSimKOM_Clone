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

package org.peerfact.impl.overlay.unstructured.homogeneous.gnutella04;

import java.math.BigInteger;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.api.overlay.OverlayID;
import org.peerfact.api.overlay.OverlayRoutingTable;
import org.peerfact.impl.simengine.Simulator;


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
 * 
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public class GnutellaOverlayRoutingTable implements
		OverlayRoutingTable<GnutellaOverlayID, GnutellaAccessOverlayContact> {

	private OverlayID<BigInteger> overlayID;

	private Map<OverlayID<BigInteger>, GnutellaAccessOverlayContact> overlayIDs = new LinkedHashMap<OverlayID<BigInteger>, GnutellaAccessOverlayContact>();

	private Set<GnutellaAccessOverlayContact> activeContacts = new LinkedHashSet<GnutellaAccessOverlayContact>();

	private Set<GnutellaAccessOverlayContact> inactiveContacts = new LinkedHashSet<GnutellaAccessOverlayContact>();

	// number of connections
	private int numConn;

	private long refresh;

	private long contactTimeout;

	private long descriptorTimeout;

	// TODO low priority: create descriptors and add timeout
	private Map<BigInteger, Set<OverlayContact<GnutellaOverlayID>>> acceptPong = new LinkedHashMap<BigInteger, Set<OverlayContact<GnutellaOverlayID>>>();

	private Map<BigInteger, OverlayContact<GnutellaOverlayID>> routePong = new LinkedHashMap<BigInteger, OverlayContact<GnutellaOverlayID>>();

	private Map<BigInteger, Set<OverlayContact<GnutellaOverlayID>>> acceptQueryHit = new LinkedHashMap<BigInteger, Set<OverlayContact<GnutellaOverlayID>>>();

	private Map<BigInteger, OverlayContact<GnutellaOverlayID>> routeQueryHit = new LinkedHashMap<BigInteger, OverlayContact<GnutellaOverlayID>>();

	private Map<BigInteger, Map<OverlayContact<GnutellaOverlayID>, OverlayContact<GnutellaOverlayID>>> routePush = new LinkedHashMap<BigInteger, Map<OverlayContact<GnutellaOverlayID>, OverlayContact<GnutellaOverlayID>>>();

	private Map<BigInteger, BigInteger> descriptorRefreshTime = new LinkedHashMap<BigInteger, BigInteger>();

	public GnutellaOverlayRoutingTable(OverlayID<BigInteger> overlayID) {
		this.overlayID = overlayID;
	}

	public void initiatedPing(GnutellaOverlayID overlayID2,
			BigInteger descriptor) {
		routePong.put(descriptor, null);
	}

	/**
	 * @param contact
	 * @param descriptor
	 * @return if new descriptor
	 */
	public boolean incomingPing(GnutellaOverlayID id, BigInteger descriptor) {
		refreshDesriptor(descriptor);
		OverlayContact<GnutellaOverlayID> contact = getContact(id);
		if (routePong.containsKey(descriptor) || contact == null) {
			return false;
		}
		routePong.put(descriptor, contact);
		return true;
	}

	public void outgoingPing(OverlayContact<GnutellaOverlayID> contact,
			BigInteger descriptor) {
		refreshDesriptor(descriptor);
		if (!acceptPong.containsKey(descriptor)) {
			acceptPong.put(descriptor,
					new LinkedHashSet<OverlayContact<GnutellaOverlayID>>());
		}
		acceptPong.get(descriptor).add(contact);
	}

	public boolean incomingPong(GnutellaOverlayID idSender,
			BigInteger descriptor) {
		refreshDesriptor(descriptor);
		OverlayContact<GnutellaOverlayID> contactSender = getContact(idSender);
		// return contact to forward message to
		if (!acceptPong.containsKey(descriptor)
				|| !routePong.containsKey(descriptor)) {
			// do not route messages with wrong descriptor
			return false;
		}
		if (!acceptPong.get(descriptor).contains(contactSender)) {
			// do not reply messages without having sent a request
			return false;
		}
		return true;
	}

	public OverlayContact<GnutellaOverlayID> outgoingPong(BigInteger descriptor) {
		refreshDesriptor(descriptor);
		// return contact to forward message to
		return routePong.get(descriptor);
	}

	public void initiatedQuery(GnutellaOverlayID overlayID2,
			BigInteger descriptor) {
		routeQueryHit.put(descriptor, null);
	}

	public boolean incomingQuery(GnutellaOverlayID id, BigInteger descriptor) {
		refreshDesriptor(descriptor);
		OverlayContact<GnutellaOverlayID> contact = getContact(id);
		if (routeQueryHit.containsKey(descriptor) || contact == null) {
			return false;
		}
		routeQueryHit.put(descriptor, contact);
		return true;
	}

	public void outgoingQuery(OverlayContact<GnutellaOverlayID> contact,
			BigInteger descriptor) {
		refreshDesriptor(descriptor);
		if (!acceptQueryHit.containsKey(descriptor)) {
			acceptQueryHit.put(descriptor,
					new LinkedHashSet<OverlayContact<GnutellaOverlayID>>());
		}
		acceptQueryHit.get(descriptor).add(contact);
	}

	public boolean incomingQueryHit(GnutellaOverlayID idSender,
			BigInteger descriptor,
			OverlayContact<GnutellaOverlayID> contactInitiator) {
		refreshDesriptor(descriptor);
		OverlayContact<GnutellaOverlayID> contactSender = this
				.getContact(idSender);
		if (!acceptQueryHit.containsKey(descriptor)
				|| !routeQueryHit.containsKey(descriptor)) {
			// do not route messages with wrong descriptor
			return false;
		}
		if (!acceptQueryHit.get(descriptor).contains(contactSender)) {
			// do not reply messages without having sent a request
			return false;
		}
		if (!routePush.containsKey(descriptor)) {
			routePush
					.put(
							descriptor,
							new LinkedHashMap<OverlayContact<GnutellaOverlayID>, OverlayContact<GnutellaOverlayID>>());
		}
		if (routePush.get(descriptor).containsKey(contactInitiator)) {
			// do not accept if a push route already exists
			return false;
		}
		routePush.get(descriptor).put(contactInitiator, contactSender);
		return true;
	}

	public OverlayContact<GnutellaOverlayID> outgoingQueryHit(
			BigInteger descriptor) {
		refreshDesriptor(descriptor);
		// return contact to forward message to
		return routeQueryHit.get(descriptor);
	}

	public OverlayContact<GnutellaOverlayID> outgoingPush(
			OverlayContact<GnutellaOverlayID> contactQueryHitInitiator,
			BigInteger descriptor) {
		refreshDesriptor(descriptor);
		if (!routePush.containsKey(descriptor)) {
			return null;
		}
		if (!routePush.get(descriptor).containsKey(contactQueryHitInitiator)) {
			return null;
		}
		return routePush.get(descriptor).get(contactQueryHitInitiator);
	}

	public void addInactiveContact(GnutellaAccessOverlayContact c) {
		GnutellaAccessOverlayContact contact = new GnutellaAccessOverlayContact(c);
		if (!c.getOverlayID().equals(this.overlayID)
				&& !this.overlayIDs.containsKey(c.getOverlayID())) {
			this.overlayIDs.put(contact.getOverlayID(), contact);
			this.inactiveContacts.add(contact);
		}
	}

	@Override
	public void addContact(GnutellaAccessOverlayContact c) {
		GnutellaAccessOverlayContact contact = new GnutellaAccessOverlayContact(c);
		if (!this.activeContacts.contains(contact)) {
			inactiveContacts.remove(contact);
			if (this.activeContacts.size() == this.numConn) {
				// TODO sort active Contacts and disconnect worst contact
				List<GnutellaAccessOverlayContact> sortedActiveContacts = new LinkedList<GnutellaAccessOverlayContact>(
						activeContacts);
				Collections.sort(sortedActiveContacts,
						new GnutellaOverlayContactRankComparator());
				GnutellaAccessOverlayContact inactiveContact = sortedActiveContacts
						.remove(0);
				activeContacts.remove(inactiveContact);
				inactiveContacts.add(inactiveContact);
			}
			if (!overlayIDs.containsKey(contact.getOverlayID())) {
				this.overlayIDs.put(contact.getOverlayID(), contact);
			}
			contact.reset();
			contact.refresh();
			this.activeContacts.add(contact);
		}
		overlayIDs.get(contact.getOverlayID()).refresh();
	}

	@Override
	public List<GnutellaAccessOverlayContact> allContacts() {
		return new LinkedList<GnutellaAccessOverlayContact>(activeContacts);
	}

	public int numberOfActiveContacts() {
		return activeContacts.size();
	}

	public List<GnutellaAccessOverlayContact> inactiveContacts() {
		return new LinkedList<GnutellaAccessOverlayContact>(inactiveContacts);
	}

	public OverlayContact<GnutellaOverlayID> removeInactiveContact() {
		Iterator<GnutellaAccessOverlayContact> iterator = inactiveContacts.iterator();
		if (iterator.hasNext()) {
			GnutellaAccessOverlayContact contact = iterator.next();
			this.inactiveContacts.remove(contact);
			this.overlayIDs.remove(contact.getOverlayID());
			return contact;
		}
		return null;
	}

	@Override
	public GnutellaAccessOverlayContact getContact(GnutellaOverlayID oid) {
		return this.overlayIDs.get(oid);
	}

	public boolean isActive(GnutellaOverlayID oid) {
		GnutellaAccessOverlayContact contact = this.overlayIDs.get(oid);
		if (contact == null || !activeContacts.contains(contact)) {
			return false;
		}
		return true;
	}

	@Override
	public void removeContact(GnutellaOverlayID oid) {
		throw new RuntimeException("The method removeContact() within "
				+ this.getClass().getSimpleName() + "is not implemented.");
	}

	@Override
	public void clearContacts() {
		this.overlayIDs.clear();
		this.activeContacts.clear();
		this.inactiveContacts.clear();
	}

	public void setNumConn(int numConn) {
		this.numConn = numConn;
	}

	public int getNumConn() {
		return numConn;
	}

	public void setRefresh(long refresh) {
		this.refresh = refresh;
	}

	public void setContactTimeout(long contactTimeout) {
		this.contactTimeout = contactTimeout;
	}

	public void setDescriptorTimeout(long descriptorTimeout) {
		this.descriptorTimeout = descriptorTimeout;
	}

	public void refreshDesriptor(BigInteger descriptor) {
		if (descriptorRefreshTime.containsKey(descriptor)) {
			descriptorRefreshTime.remove(descriptor);
		}
		descriptorRefreshTime.put(descriptor, BigInteger.valueOf(Simulator
				.getCurrentTime()));
	}

	public List<BigInteger> getDeadContacts() {
		List<BigInteger> deadDescriptors = new LinkedList<BigInteger>();
		Iterator<BigInteger> iterator = descriptorRefreshTime.keySet()
				.iterator();
		while (iterator.hasNext()) {
			BigInteger descriptor = iterator.next();
			if (descriptorRefreshTime.get(descriptor).longValue()
					+ this.descriptorTimeout < Simulator.getCurrentTime()) {
				deadDescriptors.add(descriptor);
			}
		}
		for (BigInteger descriptor : deadDescriptors) {
			acceptPong.remove(descriptor);
			routePong.remove(descriptor);
			acceptQueryHit.remove(descriptor);
			routeQueryHit.remove(descriptor);
			routePush.remove(descriptor);
			descriptorRefreshTime.remove(descriptor);
		}
		return deadDescriptors;
	}

	public List<GnutellaAccessOverlayContact> getRefreshContacts() {
		List<GnutellaAccessOverlayContact> refreshContacts = new LinkedList<GnutellaAccessOverlayContact>();
		List<GnutellaAccessOverlayContact> timeoutContacts = new LinkedList<GnutellaAccessOverlayContact>();
		Iterator<GnutellaAccessOverlayContact> iterator = activeContacts.iterator();
		while (iterator.hasNext()) {
			GnutellaAccessOverlayContact contact = iterator.next();
			// remove dead contacts
			if (contact.getLastRefresh() + this.contactTimeout < Simulator
					.getCurrentTime()) {
				timeoutContacts.add(contact);
			}
			// return contacts to be refreshed
			if (contact.getLastRefresh() + this.refresh < Simulator
					.getCurrentTime()) {
				refreshContacts.add(contact);
			}
		}
		// remove dead contacts
		for (GnutellaAccessOverlayContact timeoutContact : timeoutContacts) {
			activeContacts.remove(timeoutContact);
		}
		return refreshContacts;
	}
}
