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

package org.peerfact.impl.overlay.dht.kademlia.base.routing;

import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayID;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayKey;

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
 * Sets the last lookup time (lookup over the network) for a bucket specified by
 * a KademliaOverlayID that the bucket is responsible for.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public final class SetLastLookupTimeNodeVisitor<T extends KademliaOverlayID>
		extends
		AbstractNodeVisitor<T> {

	/**
	 * Single, final SetLastLookupTimeNodeVisitor instance (non-reentrant).
	 */
	private static final SetLastLookupTimeNodeVisitor singleton = new SetLastLookupTimeNodeVisitor();

	/**
	 * The key that has been looked up, that is it lies in the bucket that is to
	 * be updated.
	 */
	private KademliaOverlayKey key;

	/**
	 * The point in time of the last lookup in the specified bucket.
	 */
	private long lastLookupTime;

	/**
	 * Sets the last lookup time of the bucket that contains <code>key</code> to
	 * <code>lastLookupTime</code>. ("Lookup" refers to a lookup that has been
	 * carried out over the network.)
	 * 
	 * @param key
	 *            a KademliaOverlayID from the bucket that is to be updated.
	 * @param lastLookupTime
	 *            the new "lastLookupTime" of the bucket that contains
	 *            <code>key</code>.
	 * @return an SetLastLookupTimeNodeVisitor instance. Note that this instance
	 *         is statically shared among all clients of this class. That is, at
	 *         runtime only one SetLastLookupTimeNodeVisitor instance exists.
	 *         Thus, it is non-reentrant and should not be saved by clients
	 *         (should used immediately).
	 */
	public static final <T extends KademliaOverlayID> SetLastLookupTimeNodeVisitor<T> getSetLastLookupTimeNodeVisitor(
			final KademliaOverlayKey key, final long lastLookupTime) {
		singleton.key = key;
		singleton.lastLookupTime = lastLookupTime;
		return singleton;
	}

	private SetLastLookupTimeNodeVisitor() {
		// should not be called externally
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void visit(final BranchNode<T> node) {
		node.getResponsibleChild(key.getBigInt()).accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void visit(final LeafNode<T> node) {
		node.setLastLookup(this.lastLookupTime);
	}

}
