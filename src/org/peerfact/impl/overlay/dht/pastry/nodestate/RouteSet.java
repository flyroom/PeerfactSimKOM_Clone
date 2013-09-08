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

package org.peerfact.impl.overlay.dht.pastry.nodestate;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;

import org.peerfact.impl.overlay.dht.pastry.components.PastryContact;
import org.peerfact.impl.overlay.dht.pastry.operations.RequestRouteSetOperation;


/**
 * A set of node contacts typically stored in the routing table, for redundancy
 * reasons.
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class RouteSet implements Iterable<PastryContact> {

	/**
	 * A dummy RouteSet to mark entries, the owning node itself is the best
	 * choice to use.
	 */
	public static final RouteSet OWNER_SET = new RouteSet();

	/**
	 * If this Set is to be updated, the corresponding Operation is stored here
	 * to ensure that there is only one for each set.
	 */
	private RequestRouteSetOperation reqOp = null;

	private LinkedHashSet<PastryContact> entries = new LinkedHashSet<PastryContact>();

	public RouteSet() {
		// Nothing to do here
	}

	private RouteSet(Collection<PastryContact> entries) {
		this.entries = new LinkedHashSet<PastryContact>(entries);
	}

	public boolean removeEntry(PastryContact e) {
		return entries.remove(e);
	}

	public int getSize() {
		return entries.size();
	}

	/**
	 * Insert a new entry into the RouteSet. This method does not check whether
	 * this RouteSet is the right one to add the contact. This should be checked
	 * prior to the insertion by the routing table.
	 * 
	 * @param c
	 *            the contact to add
	 * @return true if this set did not already contain the specified element
	 */
	public boolean insertEntry(PastryContact c) {
		return entries.add(c);
	}

	/**
	 * Returns a new copy of the RouteSet
	 * 
	 * @return the copy
	 */
	public RouteSet getNewInstance() {
		return new RouteSet(entries);
	}

	/**
	 * @param c
	 *            the contact to check
	 * @return true if the given contact is part of the set, false otherwise
	 */
	public boolean contains(PastryContact c) {
		return entries.contains(c);
	}

	@Override
	public Iterator<PastryContact> iterator() {
		return entries.iterator();
	}

	/**
	 * true, if this set is currently performing an update to retrieve new
	 * entries
	 * 
	 * @return
	 */
	public boolean isUpdating() {
		return reqOp != null && !reqOp.isFinished();
	}

	public void startUpdateOperation(RequestRouteSetOperation op) {
		reqOp = op;
	}

	@Override
	public String toString() {
		return entries.toString();
	}
}
