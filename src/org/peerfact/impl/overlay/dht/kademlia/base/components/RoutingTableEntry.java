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

package org.peerfact.impl.overlay.dht.kademlia.base.components;

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
 * A routing table entry is a combination of a KademliaOverlayContact with
 * information about the time the contact has last been seen, time first seen
 * and how many times a foreign contact did not respond to a query.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @author Sebastian Kaune
 * @version 05/06/2011
 */
public final class RoutingTableEntry<T extends KademliaOverlayID> {

	/**
	 * The contact about which the additional information in this routing table
	 * entry is provided.
	 */
	private final KademliaOverlayContact<T> contact;

	/**
	 * The most recent (simulation) time that the contact has been seen.
	 */
	private long lastSeen;

	/**
	 * The first (simulation) time that the contact has been seen.
	 */
	// private final long firstSeen;
	/**
	 * The number of marks that the contact has received so far for not being
	 * responsive.
	 */
	private int staleCounter;

	/**
	 * Constructs a new RoutingTableEntry with the given contact. Sets its
	 * first-seen time to the current simulation time and starts with a stale
	 * counter of zero.
	 * 
	 * @param contact
	 *            the KademliaOverlayContact that is contained in this routing
	 *            table entry.
	 */
	public RoutingTableEntry(final KademliaOverlayContact<T> contact) {
		this.contact = contact;
		// this.firstSeen = Simulator.getCurrentTime();
		seen();
	}

	/**
	 * @return the KademliaOverlayContact contained in this RoutingTableEntry.
	 */
	public final KademliaOverlayContact<T> getContact() {
		return contact;
	}

	/**
	 * Updates time last seen for this contact and resets the stale counter.
	 */
	public final void seen() {
		this.lastSeen = Simulator.getCurrentTime();
		staleCounter = 0;
	}

	/**
	 * @return the time this contact was first seen.
	 */
	// public long getFirstSeen() {
	// return firstSeen;
	// }
	/**
	 * @return the time this contact was last seen.
	 */
	public final long getLastSeen() {
		return this.lastSeen;
	}

	/**
	 * Increments the failure counter and returns the new value.
	 * 
	 * @return the new value of the stale counter.
	 */
	public final int increaseStaleCounter() {
		return ++this.staleCounter;
	}

	/**
	 * @return the current value of the stale counter.
	 */
	public final int getStaleCounter() {
		return this.staleCounter;
	}

	/**
	 * Returns <code>true</code> if <code>o</code> is a
	 * <code>RoutingTableEntry</code> and has the same KademliaOverlayContact as
	 * this. Specifics of this routing table entry such as stale counter are
	 * ignored.
	 */
	@Override
	public final boolean equals(Object o) {
		if (o instanceof RoutingTableEntry) {
			return this.contact.getOverlayID().equals(
					((RoutingTableEntry<?>) o).getContact().getOverlayID());
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int hashCode() {
		return contact.hashCode();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String toString() {
		return "RTEntry[Contact=" + contact /* + ", firstseen@" + this.firstSeen */
				+ ", lastseen@" + this.lastSeen + "]";
	}

}
