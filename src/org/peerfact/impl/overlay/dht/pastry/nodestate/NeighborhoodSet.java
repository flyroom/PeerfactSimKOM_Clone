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
import java.util.LinkedList;
import java.util.List;

import org.peerfact.impl.overlay.dht.pastry.components.PastryConstants;
import org.peerfact.impl.overlay.dht.pastry.components.PastryContact;
import org.peerfact.impl.overlay.dht.pastry.components.PastryID;
import org.peerfact.impl.overlay.dht.pastry.components.PastryNode;
import org.peerfact.impl.overlay.dht.pastry.operations.RequestNeighborhoodSetOperation;
import org.peerfact.impl.overlay.dht.pastry.proximity.ProximityMetricProvider;
import org.peerfact.impl.simengine.Simulator;


/**
 * This class represents a pastry node's neighborhood set. It contains contacts
 * of other nodes that are considered to be near, according to the used
 * proximity metric. The proximity metric as well as the maximum size of this
 * set is configured via the constants PROXIMITY_PROVIDER and
 * MAX_SIZE_OF_NEIGHBORHOODSET.
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class NeighborhoodSet implements Iterable<PastryContact> {

	private LinkedHashSet<PastryContact> neighbors;

	private static ProximityMetricProvider proxProvider = PastryConstants.PROXIMITY_PROVIDER;

	private static int maxSize = PastryConstants.MAX_SIZE_OF_NEIGHBORHOODSET;

	/**
	 * Tells when the set was changed the last time.
	 */
	private long lastChanged;

	/**
	 * The contact of the node owning this neighborhood set.
	 */
	private PastryNode owningNode;

	/**
	 * @param owningContact
	 *            the contact of the owning node.
	 */
	public NeighborhoodSet(PastryNode owningNode) {
		this.owningNode = owningNode;
		reset();
	}

	/**
	 * Adds the given contact to the set. If its insertion results in a set size
	 * exceeding the maximum size, nodes with the highest distance (according to
	 * the proximity metric) are removed to meet the max size again.
	 * 
	 * @param c
	 *            the contact to add
	 * @return tells whether the given node really was added to the set
	 */
	public boolean put(PastryContact c) {
		boolean added = neighbors.add(c);
		if (added) {
			lastChanged = Simulator.getCurrentTime();
			cleanSet();
		}
		return neighbors.contains(c);
	}

	/**
	 * Adds the given collection of contacts to the set. If the insertion
	 * results in a set size exceeding the maximum size, nodes with the highest
	 * distance (according to the proximity metric) are removed to meet the max
	 * size again.
	 * 
	 * @param nodes
	 *            the nodes to be added
	 */
	public void putAll(Collection<PastryContact> nodes) {
		boolean added = neighbors.addAll(nodes);
		if (added) {
			lastChanged = Simulator.getCurrentTime();
			cleanSet();
		}
	}

	private void cleanSet() {
		/*
		 * Check for the maximum size and remove the most distant nodes if
		 * necessary.
		 */

		while (neighbors.size() > maxSize) {
			int farestDistance = Integer.MIN_VALUE;
			PastryContact farest = null;

			for (PastryContact n : neighbors) {
				int d = proxProvider.calculateDistance(
						owningNode.getOverlayContact(), n);

				if (d > farestDistance) {
					farestDistance = d;
					farest = n;
				}
			}
			neighbors.remove(farest);
			lastChanged = Simulator.getCurrentTime();
		}
	}

	/**
	 * Removes the given contact from the set.
	 * 
	 * @param c
	 *            the contact to be removed
	 * @return tells whether the given contact was removed
	 */
	public boolean remove(PastryContact c) {
		boolean removed = neighbors.remove(c);
		if (removed) {
			lastChanged = Simulator.getCurrentTime();
		}
		return removed;
	}

	public void removeAndSubstitute(PastryContact c) {
		boolean removed = neighbors.remove(c);
		if (removed) {
			lastChanged = Simulator.getCurrentTime();
			cleanSet();

			if (neighbors.size() > 0 && neighbors.size() < maxSize) {
				PastryContact toBeAsked = neighbors.iterator().next();
				RequestNeighborhoodSetOperation op = new RequestNeighborhoodSetOperation(
						owningNode, toBeAsked);
				op.scheduleImmediately();
			}
		}
	}

	/**
	 * Returns the contact with the minimum distance to the owning node,
	 * according to the proximity metric.
	 * 
	 * @return the nearest node contact
	 */
	public PastryContact getNearestContact() {
		int nearestDistance = Integer.MAX_VALUE;
		PastryContact nearest = null;

		for (PastryContact n : neighbors) {
			int d = proxProvider.calculateDistance(
					owningNode.getOverlayContact(), n);

			if (d < nearestDistance) {
				nearestDistance = d;
				nearest = n;
			}
		}
		return nearest;
	}

	public PastryContact getNumericallyClosestContact(PastryID id) {
		return getNumericallyClosestContact(id, new LinkedList<PastryContact>());
	}

	public PastryContact getNumericallyClosestContact(PastryID id,
			List<PastryContact> exclude) {
		return StateHelpers.getClosestContact(id, exclude, neighbors);
	}

	@Override
	public Iterator<PastryContact> iterator() {
		return neighbors.iterator();
	}

	/**
	 * Resets the set to its initial state
	 */
	public void reset() {
		neighbors = new LinkedHashSet<PastryContact>();
		lastChanged = Simulator.getCurrentTime();
	}

	/**
	 * @return Tells when the set was changed the last time.
	 */
	public long getLastChanged() {
		return lastChanged;
	}

	public int getSize() {
		return neighbors.size();
	}

}
