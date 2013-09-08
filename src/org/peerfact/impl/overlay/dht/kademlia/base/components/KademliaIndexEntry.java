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

import org.peerfact.api.overlay.dht.DHTObject;
import org.peerfact.impl.overlay.dht.kademlia.base.operations.OperationsConfig;
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
 * A KademliaIndexEntry groups a DHTObject together with the point in time at
 * which it has last been republished (or received) at the node that owns the
 * KademliaIndexer in which this entry is saved.
 * 
 * @author Sebastian Kaune <peerfact@kom.tu-darmstadt.de>
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public final class KademliaIndexEntry {

	/** The DHTObject (data item) to be saved. */
	private final DHTObject value;

	/**
	 * The point in time at which the item associated with this
	 * KademliaIndexEntry has last been republished (or received) at the node
	 * that owns the index. A negative value (smaller than zero) means that it
	 * has never been published.
	 */
	private long lastRepublish = -1;

	/**
	 * Configuration values ("constants").
	 */
	protected final ComponentsConfig config;

	/**
	 * Constructs a new KademliaIndexEntry that groups a DHTObject together with
	 * the point in time at which it has last been republished (or received) at
	 * the node that owns the KademliaIndexer in which this entry is saved. The
	 * last republish time is initially set to "not republished (-1)" and has to
	 * be set manually.
	 * 
	 * @param value
	 *            the DHTObject (data item) to be saved.
	 * @param conf
	 *            a ComponentsConfig reference that permits to retrieve
	 *            configuration "constants".
	 */
	public KademliaIndexEntry(final DHTObject value, final ComponentsConfig conf) {
		this.config = conf;
		this.value = value;
	}

	/**
	 * Returns the value of this index entry.
	 * 
	 * @return the DHTObject that is saved in this entry.
	 */
	public final DHTObject getValue() {
		return this.value;
	}

	/**
	 * Returns the last republish time of this entry. This method is for test
	 * purposes only!
	 * 
	 * @return the point in time at which the item associated with this
	 *         KademliaIndexEntry has last been republished (or received) at the
	 *         node that owns the index (in simulation time units). A value
	 *         smaller than zero means that the data item has never been
	 *         published or received from another peer.
	 */
	public final long getLastRepublish() {
		return lastRepublish;
	}

	/**
	 * Updates the last republish time by setting it to the current simulation
	 * time. A republish takes place if either this or another peer sends the
	 * data item to its neighbours.
	 */
	public final void updateLastRepublish() {
		lastRepublish = Simulator.getCurrentTime();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String toString() {
		return "index entry (value=" + this.value + ", lastRepublish="
				+ this.lastRepublish + ")";
	}

	/**
	 * @return whether this index entry has expired, that is, whether the last
	 *         republish time is more than or exactly
	 *         {@link OperationsConfig#getDataExpirationTime()} ago. Items that
	 *         have never been published do not expire (it is expected that they
	 *         are published at some point in time from which on they expire
	 *         normally).
	 */
	public final boolean hasExpired() {
		if (lastRepublish < 0) {
			return false;
		} else if (lastRepublish + config.getDataExpirationTime() <= Simulator
				.getCurrentTime()) {
			return true;
		}
		return false;
	}

	/**
	 * @return whether this index entry needs to be republished because the last
	 *         republish time is more than or exactly
	 *         {@link OperationsConfig#getRepublishInterval()} ago or has never
	 *         been (re)published at all.
	 */
	public final boolean needsRepublish() {
		if (lastRepublish < 0
				|| lastRepublish + config.getRepublishInterval() <= Simulator
						.getCurrentTime()) {
			return true;
		}
		return false;
	}
}
