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

import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

import org.peerfact.impl.overlay.dht.pastry.components.PastryConstants;
import org.peerfact.impl.overlay.dht.pastry.components.PastryContact;
import org.peerfact.impl.overlay.dht.pastry.components.PastryID;
import org.peerfact.impl.overlay.dht.pastry.components.PastryNode;
import org.peerfact.impl.overlay.dht.pastry.operations.RequestLeafSetOperation;
import org.peerfact.impl.simengine.Simulator;

/**
 * This class represents a party node's leaf set (named "L" within the original
 * paper). Its maximum size is determined by the constant MAX_SIZE_OF_LEAFSET.
 * 
 * @author Julius Ruckert <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class LeafSet implements Iterable<PastryContact> {

	private LinkedList<PastryContact> ccwNodes;

	private LinkedList<PastryContact> cwNodes;

	private PastryNode owningNode;

	private int maxSize = PastryConstants.MAX_SIZE_OF_LEAFSET;

	/**
	 * Tells when the set was changed the last time.
	 */
	private long lastChanged;

	public LeafSet(PastryNode owningNode) {
		this.owningNode = owningNode;
		reset();
	}

	public boolean isComplete() {
		if (PastryConstants.MAX_SIZE_OF_LEAFSET == getSize() || overlaps()) {
			return true;
		}
		return false;
	}

	public boolean overlaps() {
		if (cwNodes.size() == 0 || ccwNodes.size() == 0) {
			return false;
		}
		PastryContact farestCW = cwNodes.getLast();
		PastryContact farestCCW = ccwNodes.getLast();

		if (getSize() > 0
				&& (cwNodes.contains(farestCCW) || ccwNodes.contains(farestCW))) {
			return true;
		}
		return false;
	}

	/**
	 * This method tries to add a given contact to the leaf set. Due to the
	 * limited size of the set, it is possible that the contact is considered
	 * useless and is not added to the set. It is also possible that during this
	 * process another contact is considered as useless and therefore removed
	 * from the set.
	 * 
	 * @param c
	 *            the contact to add
	 * @return true if the given contact is part of the set after the method
	 *         call
	 */
	public boolean put(PastryContact c) {
		boolean added = false;
		if (!cwNodes.contains(c)) {
			added = cwNodes.add(c);
		}
		if (!ccwNodes.contains(c)) {
			added = ccwNodes.add(c) || added;
		}
		if (added) {
			lastChanged = Simulator.getCurrentTime();
			cleanSet();
		}

		if (cwNodes.contains(c) || ccwNodes.contains(c)) {
			return true;
		}
		return false;
	}

	/**
	 * This method tries to add a given collection of contacts to the leaf set.
	 * Due to the limited size of the set, it is possible that some or all of
	 * the contacts are considered useless and are not added to the set. It is
	 * also possible that during this process another contact is considered as
	 * useless and therefore removed from the set.
	 * 
	 * @param nodes
	 *            the nodes to be added
	 */
	public void putAll(Collection<PastryContact> nodes) {
		boolean added = false;

		for (PastryContact c : nodes) {
			if (!cwNodes.contains(c)) {
				added = cwNodes.add(c) || added;
			}
			if (!ccwNodes.contains(c)) {
				added = ccwNodes.add(c) || added;
			}
		}

		if (added) {
			lastChanged = Simulator.getCurrentTime();
			cleanSet();
		}
	}

	/**
	 * This method removes a given contact from the leaf set.
	 * 
	 * @param c
	 *            the contact to remove
	 * @return true, if the contact really was part of the leaf set
	 */
	public boolean remove(PastryContact c) {
		boolean removed = false;

		if (cwNodes.contains(c)) {
			removed = cwNodes.remove(c);
		}
		if (ccwNodes.contains(c)) {
			removed = ccwNodes.remove(c) || removed;
		}

		if (removed) {
			lastChanged = Simulator.getCurrentTime();
		}
		return removed;
	}

	/**
	 * Removes a contact from the leaf set and requests substitutes if the set
	 * becomes too small due to the removal.
	 * 
	 * @param c
	 *            the contact to be removed
	 */
	public void removeAndSubstitute(PastryContact c) {

		if (overlaps()) {
			remove(c);
		} else {
			boolean wasInCw = cwNodes.remove(c);
			boolean wasInCcw = cwNodes.remove(c);
			int maxSizeOfList = maxSize / 2;

			if (wasInCw && cwNodes.size() > 0
					&& (cwNodes.size() < maxSizeOfList || getSize() < maxSize)) {
				RequestLeafSetOperation op = new RequestLeafSetOperation(
						owningNode, cwNodes.getLast());
				op.scheduleImmediately();
			}

			if (wasInCcw && ccwNodes.size() > 0
					&& (ccwNodes.size() < maxSizeOfList || getSize() < maxSize)) {
				RequestLeafSetOperation op = new RequestLeafSetOperation(
						owningNode, ccwNodes.getLast());
				op.scheduleImmediately();
			}

			if (wasInCw || wasInCcw) {
				lastChanged = Simulator.getCurrentTime();
			}
		}
	}

	/**
	 * This method does the following:
	 * 
	 * 1) Sort the lists ccwNodes and cwNodes according their absolute distance
	 * to the base ID in a ascending order.
	 * 
	 * 2) If the size of one of the two lists exceeds the maximum leaf set size,
	 * it removes the most distant entries until to match the maximum size.
	 */
	private void cleanSet() {

		sortCw(cwNodes);
		sortCcw(ccwNodes);

		if (getSize() <= maxSize) {
			return;
		}

		/*
		 * Remove most distant nodes if the number of nodes exceeds the defined
		 * maximum number.
		 */

		LinkedHashSet<PastryContact> removedCcw = new LinkedHashSet<PastryContact>();
		LinkedHashSet<PastryContact> removedCw = new LinkedHashSet<PastryContact>();

		int maxSizeOfList = maxSize / 2;
		boolean changedSet = false;

		while (getSize() > maxSize && ccwNodes.size() > maxSizeOfList) {
			removedCcw.add(ccwNodes.removeLast());
			changedSet = true;
		}

		while (getSize() > maxSize && cwNodes.size() > maxSizeOfList) {
			removedCw.add(cwNodes.removeLast());
			changedSet = true;
		}

		if (changedSet) {
			lastChanged = Simulator.getCurrentTime();
		}

	}

	private void sortCcw(List<PastryContact> l) {
		Comparator<PastryContact> compCcw = new Comparator<PastryContact>() {
			@Override
			public int compare(PastryContact c1, PastryContact c2) {
				/*
				 * This compares the absolute distance of the two contacts to
				 * the base ID.
				 */
				BigInteger d1 = owningNode.getOverlayID().getCcwDistance(
						c1.getOverlayID());
				BigInteger d2 = owningNode.getOverlayID().getCcwDistance(
						c2.getOverlayID());

				return d1.compareTo(d2);
			}
		};

		// Sort the list according to counter clockwise distance
		Collections.sort(l, compCcw);
	}

	private void sortCw(List<PastryContact> l) {
		Comparator<PastryContact> compCw = new Comparator<PastryContact>() {
			@Override
			public int compare(PastryContact c1, PastryContact c2) {
				/*
				 * This compares the absolute distance of the two contacts to
				 * the base ID.
				 */
				BigInteger d1 = owningNode.getOverlayID().getCwDistance(
						c1.getOverlayID());
				BigInteger d2 = owningNode.getOverlayID().getCwDistance(
						c2.getOverlayID());

				return d1.compareTo(d2);
			}
		};

		// Sort the list according to clockwise distance
		Collections.sort(l, compCw);
	}

	public void setMaxSize(int size) {
		maxSize = size;
	}

	/**
	 * Calculates the number of distinct contacts in the set.
	 * 
	 * @return the number of distinct contacts
	 */
	public int getSize() {
		LinkedHashSet<PastryContact> l = new LinkedHashSet<PastryContact>();
		l.addAll(ccwNodes);
		l.addAll(cwNodes);

		return l.size();
	}

	/**
	 * Resets the set to its initial state
	 */
	public void reset() {
		ccwNodes = new LinkedList<PastryContact>();
		cwNodes = new LinkedList<PastryContact>();
		lastChanged = Simulator.getCurrentTime();
	}

	/**
	 * Tells whether a given key falls within the range of the set.
	 * 
	 * @param key
	 *            the key to check
	 * @return true if it falls in the range, false otherwise
	 */
	public boolean isInRange(PastryID id) {
		if (ccwNodes.isEmpty() || cwNodes.isEmpty()) {
			return false;
		}

		LinkedList<PastryContact> ccwList = new LinkedList<PastryContact>(
				ccwNodes);
		ccwList.add(owningNode.getOverlayContact());
		sortCcw(ccwList);

		LinkedList<PastryContact> cwList = new LinkedList<PastryContact>(
				cwNodes);
		cwList.add(owningNode.getOverlayContact());
		sortCw(cwList);

		BigInteger ccwDistanceToSmallest = owningNode.getOverlayID()
				.getCcwDistance(ccwList.getLast().getOverlayID());
		BigInteger cwDistanceToLarges = owningNode.getOverlayID()
				.getCwDistance(cwList.getLast().getOverlayID());

		BigInteger ccwDistanceToId = owningNode.getOverlayID().getCcwDistance(
				id);
		BigInteger cwDistanceToId = owningNode.getOverlayID().getCwDistance(id);

		if (ccwDistanceToSmallest.compareTo(ccwDistanceToId) >= 0
				|| cwDistanceToLarges.compareTo(cwDistanceToId) >= 0) {
			return true;
		}
		return false;
	}

	public PastryContact getNumericallyClosestContact(PastryID id) {
		return getNumericallyClosestContact(id, new LinkedList<PastryContact>());
	}

	public PastryContact getNumericallyClosestContact(PastryID id,
			List<PastryContact> exclude) {
		LinkedList<PastryContact> l = new LinkedList<PastryContact>(ccwNodes);
		l.addAll(cwNodes);
		l.add(owningNode.getOverlayContact()); // The node itself can be also
												// the closest

		return StateHelpers.getClosestContact(id, exclude, l);
	}

	/**
	 * @return Tells when the set was changed the last time.
	 */
	public long getLastChanged() {
		return lastChanged;
	}

	@Override
	public Iterator<PastryContact> iterator() {
		LinkedHashSet<PastryContact> l = new LinkedHashSet<PastryContact>(
				ccwNodes);
		l.addAll(cwNodes);
		return l.iterator();
	}
}
