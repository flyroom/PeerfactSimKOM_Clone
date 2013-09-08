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

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.peerfact.impl.overlay.dht.kademlia.base.routing.AddNodeVisitor;
import org.peerfact.impl.overlay.dht.kademlia.base.routing.GenericLookupNodeVisitor;
import org.peerfact.impl.overlay.dht.kademlia.base.routing.MarkUnresponsiveNodeVisitor;
import org.peerfact.impl.overlay.dht.kademlia.base.routing.Node;
import org.peerfact.impl.overlay.dht.kademlia.base.routing.PseudoRootNode;
import org.peerfact.impl.overlay.dht.kademlia.base.routing.RefreshNodeVisitor;
import org.peerfact.impl.overlay.dht.kademlia.base.routing.SetLastLookupTimeNodeVisitor;

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
 * KademliaRoutingTable for Standard-Kademlia. Permits to store, lookup and mark
 * contacts as unresponsive.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public class KademliaRoutingTable<T extends KademliaOverlayID> implements
		RoutingTable<T> {

	/**
	 * Static instance of a Set returned to clients with lookup results. Shared
	 * among all routing table instances!
	 */
	protected final Set<KademliaOverlayContact<T>> sharedResultSet = new LinkedHashSet<KademliaOverlayContact<T>>();

	/**
	 * (Pseudo) root of the routing tree.
	 */
	public final PseudoRootNode<T> pseudoRoot;

	/**
	 * Handler for changes to the set of the K closest nodes around own ID.
	 */
	protected final ProximityHandler<T> proxHandler;

	/**
	 * Configuration values ("constants").
	 */
	protected final RoutingTableConfig config;

	/**
	 * Constructs a new routing table (according to standard Kademlia) with the
	 * given contact information of the owning node (it will be inserted into
	 * the routing table).
	 * 
	 * @param ownContact
	 *            the KademliaOverlayContact that identifies the node that this
	 *            routing table belongs to.
	 * @param conf
	 *            a RoutingTableConfig reference that permits to retrieve
	 *            configuration "constants".
	 */
	public KademliaRoutingTable(final KademliaOverlayContact<T> ownContact,
			final RoutingTableConfig conf,
			AbstractKademliaNode<T> owningOverlayNode) {
		this.config = conf;
		pseudoRoot = new PseudoRootNode<T>(ownContact.getOverlayID(), config,
				owningOverlayNode);
		proxHandler = new ProximityHandler<T>(ownContact.getOverlayID(), conf);
		addContact(ownContact);
	}

	public KademliaRoutingTable(final KademliaOverlayContact<T> ownContact,
			final RoutingTableConfig conf) {
		this(ownContact, conf, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addContact(final KademliaOverlayContact<T> contact) {
		final AddNodeVisitor<T> addVis = AddNodeVisitor.getAddNodeVisitor(
				contact, null, proxHandler, config);
		pseudoRoot.accept(addVis);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void localLookup(final KademliaOverlayKey id, final int num,
			final Collection<KademliaOverlayContact<T>> result) {
		result.clear();
		final GenericLookupNodeVisitor<T> lookupVis = GenericLookupNodeVisitor
				.getGenericLookupNodeVisitor(id, num, null, result);
		pseudoRoot.accept(lookupVis);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Set<KademliaOverlayContact<T>> localLookup(
			final KademliaOverlayKey id, final int num) {
		localLookup(id, num, sharedResultSet);
		return sharedResultSet;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void markUnresponsiveContact(final T id) {
		final MarkUnresponsiveNodeVisitor<T> unresVis = MarkUnresponsiveNodeVisitor
				.getMarkUnresponsiveNodeVisitor(id, null, proxHandler, config);
		pseudoRoot.accept(unresVis);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void setLastLookupTime(final KademliaOverlayKey key,
			final long time) {
		final SetLastLookupTimeNodeVisitor<T> setVis = SetLastLookupTimeNodeVisitor
				.getSetLastLookupTimeNodeVisitor(key, time);
		pseudoRoot.accept(setVis);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Map<KademliaOverlayKey, Integer> getRefreshBuckets(
			final long notLookedUpSince) {
		final Map<KademliaOverlayKey, Integer> result = new LinkedHashMap<KademliaOverlayKey, Integer>();
		final RefreshNodeVisitor<T> refVis = RefreshNodeVisitor
				.getRefreshNodeVisitor(notLookedUpSince, result, config);
		pseudoRoot.accept(refVis);
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void registerProximityListener(
			final org.peerfact.impl.overlay.dht.kademlia.base.components.RoutingTable.ProximityListener<T> newListener) {
		proxHandler.registerProximityListener(newListener);
	}

	@Override
	public Collection<KademliaOverlayContact<T>> getNeighbors() {
		Node<T> rootNode = pseudoRoot.getRoot();
		Collection<KademliaOverlayContact<T>> cs = rootNode
				.getAllSubContacts();
		return Collections.unmodifiableCollection(cs);
	}

	@Override
	public void removeContact(T oid) {
		// TODO Auto-generated method stub

	}

	@Override
	public KademliaOverlayContact<T> getContact(T oid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void clearContacts() {
		// TODO Auto-generated method stub

	}

	@Override
	public List<KademliaOverlayContact<T>> allContacts() {
		// TODO Auto-generated method stub
		return null;
	}

}
