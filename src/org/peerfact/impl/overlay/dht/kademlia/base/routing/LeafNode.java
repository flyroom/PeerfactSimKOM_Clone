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

import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.peerfact.impl.overlay.dht.kademlia.base.components.AbstractKademliaNode;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayContact;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayID;
import org.peerfact.impl.overlay.dht.kademlia.base.components.RoutingTableConfig;
import org.peerfact.impl.overlay.dht.kademlia.base.components.RoutingTableEntry;


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
 * A leaf of Kademlia's routing tree. Holds up to {@code k=}
 * {@link RoutingTableConfig#getBucketSize()} regular contacts and
 * {@link RoutingTableConfig#getReplacementCacheSize()} contacts in a
 * replacement cache. This data structure is also known as k-bucket.
 * 
 * A k-bucket may contain up to <code>k</code> contacts. If the k-bucket is
 * full, new contacts may be stored in the replacement cache that is used to
 * replace contacts from the original bucket if they are unresponsive.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public class LeafNode<T extends KademliaOverlayID> extends AbstractNode<T> {

	/**
	 * Data structure that contains <code>RoutingTableEntries</code> with
	 * <code>KademliaOverlayContact</code>s mapped from their corresponding
	 * <code>KademliaOverlayID</code>s.
	 */
	public final Map<T, RoutingTableEntry<T>> kBucket;

	/**
	 * Data structure to store <code>KademliaOverlayContact</code> candidates
	 * (wrapped in <code>RoutingTableEntries</code>) used to implement a
	 * replacement policy for the <code>kBucket</code>.
	 */
	protected final Map<T, RoutingTableEntry<T>> replacementCache;

	/**
	 * The simulation time of the last network-based lookup for a key in this
	 * bucket's range. Default value -1 means that this bucket has never been
	 * looked up before.
	 */
	protected long lastLookup = -1;

	/**
	 * Constructs a new top-level routing tree leaf, that is a bucket that
	 * initially covers the whole ID range and is eventually split as further
	 * contacts are seen and inserted into this bucket.
	 * 
	 * Consequently, this new bucket will have level 0 and prefix 0.
	 * 
	 * @param pseudoRoot
	 *            the pseudo root that holds a reference to the routing tree (a
	 *            pseudo root has level -1).
	 * @param conf
	 *            a RoutingTableConfig reference that permits to retrieve
	 *            configuration "constants".
	 */
	protected LeafNode(final ParentNode<T> pseudoRoot,
			final RoutingTableConfig conf,
			final AbstractKademliaNode<T> owningOverlayNode) {
		this(BigInteger.ZERO, pseudoRoot, conf, owningOverlayNode);
	}

	public LeafNode(final ParentNode<T> pseudoRoot,
			final RoutingTableConfig conf) {
		this(pseudoRoot, conf, null);
	}

	/**
	 * Constructs an empty routing tree leaf node (= bucket) with the given
	 * prefix and parent node. The node will be registered as a child to the
	 * parent node.
	 * 
	 * @param prefix
	 *            the prefix of this node, that is the bits that all contacts
	 *            saved in this bucket have in common.
	 * @param parent
	 *            the parent node of this node.
	 * @param conf
	 *            a RoutingTableConfig reference that permits to retrieve
	 *            configuration "constants".
	 */
	protected LeafNode(final BigInteger prefix, final ParentNode<T> parent,
			final RoutingTableConfig conf,
			final AbstractKademliaNode<T> owningOverlayNode) {
		super(prefix, parent, conf, owningOverlayNode);
		this.kBucket = new LinkedHashMap<T, RoutingTableEntry<T>>(
				config.getBucketSize(), 1.0f);
		this.replacementCache = new LinkedHashMap<T, RoutingTableEntry<T>>(
				config.getReplacementCacheSize(), 1.0f);
	}

	public LeafNode(final BigInteger prefix, final ParentNode<T> parent,
			final RoutingTableConfig conf) {
		this(prefix, parent, conf, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void accept(final NodeVisitor<T> visitor) {
		visitor.visit(this);
	}

	@Override
	public Set<KademliaOverlayContact<T>> getAllSubContacts() {
		Set<KademliaOverlayContact<T>> result = new LinkedHashSet<KademliaOverlayContact<T>>();
		for (RoutingTableEntry<T> b : kBucket.values()) {
			result.add(b.getContact());
		}
		for (RoutingTableEntry<T> b : getReplacementCache().values()) {
			result.add(b.getContact());
		}
		return result;
	}

	public Map<T, RoutingTableEntry<T>> getReplacementCache() {
		return replacementCache;
	}

	public long getLastLookup() {
		return lastLookup;
	}

	public void setLastLookup(long lastLookup) {
		this.lastLookup = lastLookup;
	}

}
