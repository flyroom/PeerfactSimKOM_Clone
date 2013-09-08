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
import java.util.Map;
import java.util.Set;

import org.peerfact.api.overlay.NeighborDeterminator;
import org.peerfact.api.overlay.OverlayRoutingTable;
import org.peerfact.impl.overlay.dht.kademlia.hkademlia.components.HKademliaOverlayID;

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
 * Interface for Kademlia routing tables: lookup, store, mark nodes as
 * unresponsive.
 * 
 * @param <T>
 *            the concrete (sub)type of KademliaOverlayID that is being used in
 *            the routing table.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public interface RoutingTable<T extends KademliaOverlayID> extends
		NeighborDeterminator<KademliaOverlayContact<T>>,
		OverlayRoutingTable<T, KademliaOverlayContact<T>> {

	/**
	 * Interface for hierarchy-aware routing tables: in addition to the regular
	 * routing table operations, permits to restrict lookups to contacts that
	 * have a certain minimum cluster depth with respect to a reference
	 * identifier.
	 * 
	 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
	 */
	public interface HierarchyRestrictableRoutingTable<H extends HKademliaOverlayID>
			extends RoutingTable<H> {

		/**
		 * Performs a local lookup for the given key, that is a lookup that only
		 * involves the routing table, but no communication over the network.
		 * The eligible contacts from the routing table are restricted by
		 * indicating the minimum "maximum common cluster depth"
		 * <code>minDepth</code>, which means that all contacts returned by this
		 * lookup must share a common cluster with <code>clusterRefID</code> of
		 * a depth of at least <code>minDepth</code>.
		 * 
		 * @param id
		 *            the KademliaOverlayKey that is to be looked up.
		 * @param num
		 *            the number of expected results.
		 * @param minDepth
		 *            the minimum cluster depth that all contacts returned by
		 *            this lookup must have in common with
		 *            <code>clusterRefID</code>. 0 effectively disables that
		 *            restriction as all contacts have a common cluster depth of
		 *            at least 0.
		 * @param clusterRefID
		 *            the HKademliaOverlayID that defines which would be the
		 *            "perfect" cluster with respect to <code>minDepth</code>.
		 * @return a set containing the <code>num</code> contacts from the
		 *         routing table that are closest (in the XOR metric) to
		 *         <code>id</code> <i>and</i> share a cluster of a depth of at
		 *         least <code>minDepth</code> with the routing table owner's
		 *         id. The contents of this Set is only valid until another
		 *         local lookup is made as the returned Set is shared statically
		 *         among all routing table instances!! public
		 *         Set<KademliaOverlayContact<T>> getAllContacts();
		 */
		public Set<KademliaOverlayContact<H>> localLookup(
				KademliaOverlayKey id, int num, int minDepth, H clusterRefID);

		/**
		 * Performs a local lookup for the given key, that is a lookup that only
		 * involves the routing table, but no communication over the network.
		 * The eligible contacts from the routing table are restricted by
		 * indicating the minimum "maximum common cluster depth"
		 * <code>minDepth</code>, which means that all contacts returned by this
		 * lookup must share a common cluster with <code>clusterRefID</code> of
		 * a depth of at least <code>minDepth</code>.
		 * 
		 * @param id
		 *            the KademliaOverlayKey that is to be looked up.
		 * @param num
		 *            the number of expected results.
		 * @param minDepth
		 *            the minimum cluster depth that all contacts returned by
		 *            this lookup must have in common with
		 *            <code>clusterRefID</code>. 0 effectively disables that
		 *            restriction as all contacts have a common cluster depth of
		 *            at least 0.
		 * @param clusterRefID
		 *            the HKademliaOverlayID that defines which would be the
		 *            "perfect" cluster with respect to <code>minDepth</code>.
		 * @param result
		 *            a Collection that will be cleared and filled with the
		 *            <code>num</code> contacts from the routing table that are
		 *            closest (in the XOR metric) to <code>id</code> <i>and</i>
		 *            share a cluster of a depth of at least
		 *            <code>minDepth</code> with the routing table owner's id.
		 */
		public void localLookup(KademliaOverlayKey id, int num, int minDepth,
				H clusterRefID, Collection<KademliaOverlayContact<H>> result);

	}

	/**
	 * Routing tables that permit special lookups in which not all contacts from
	 * a bucket are necessarily visible.
	 * 
	 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
	 */
	public interface VisibilityRestrictableRoutingTable<H extends HKademliaOverlayID>
			extends RoutingTable<H> {

		/**
		 * Performs a local lookup for the given key, that is a lookup that only
		 * involves the routing table, but no communication over the network.
		 * 
		 * The lookup result is filtered according to the implementation of this
		 * interface. That is, not all contacts that are contained in buckets
		 * may be visible (in Kandy, for instance).
		 * 
		 * @param id
		 *            the KademliaOverlayKey that is to be looked up.
		 * @param num
		 *            the number of expected results.
		 * @return a Set containing the <code>num</code> contacts from the
		 *         routing table that are closest (in the XOR metric) to
		 *         <code>id</code>. The contents of this Set is only valid until
		 *         another local lookup is made as the returned Set is shared
		 *         statically among all routing table instances!!
		 */
		public Set<KademliaOverlayContact<H>> visibilityRestrictedLocalLookup(
				KademliaOverlayKey id, int num);

		/**
		 * Performs a local lookup for the given key, that is a lookup that only
		 * involves the routing table, but no communication over the network.
		 * 
		 * The lookup result is filtered according to the implementation of this
		 * interface. That is, not all contacts that are contained in buckets
		 * may be visible (in Kandy, for instance).
		 * 
		 * @param id
		 *            the KademliaOverlayKey that is to be looked up.
		 * @param num
		 *            the number of expected results.
		 * 
		 * @param result
		 *            a Collection that will be cleared and filled with the
		 *            <code>num</code> contacts from the routing table that are
		 *            closest (in the XOR metric) to <code>id</code> <i>and</i>
		 *            are "visible".
		 */
		public void visibilityRestrictedLocalLookup(KademliaOverlayKey id,
				int num, Collection<KademliaOverlayContact<H>> result);

	}

	/**
	 * Adds the given contact to the routing table.
	 * 
	 * @param contact
	 *            the KademliaOverlayContact that is to be added.
	 */
	@Override
	public void addContact(KademliaOverlayContact<T> contact);

	/**
	 * Performs a local lookup for the given key, that is a lookup that only
	 * involves the routing table, but no communication over the network.
	 * 
	 * @param id
	 *            the KademliaOverlayKey that is to be looked up.
	 * @param num
	 *            the number of expected results.
	 * @return a Set containing the <code>num</code> contacts from the routing
	 *         table that are closest (in the XOR metric) to <code>id</code>.
	 *         The contents of this Set is only valid until another local lookup
	 *         is made as the returned Set is shared statically among all
	 *         routing table instances!!
	 */
	public Set<KademliaOverlayContact<T>> localLookup(KademliaOverlayKey id,
			int num);

	/**
	 * Performs a local lookup for the given key, that is a lookup that only
	 * involves the routing table, but no communication over the network.
	 * 
	 * @param id
	 *            the KademliaOverlayKey that is to be looked up.
	 * @param num
	 *            the number of expected results.
	 * @param result
	 *            a Collection that will be cleared and filled with the
	 *            <code>num</code> contacts from the routing table that are
	 *            closest (in the XOR metric) to <code>id</code>.
	 */
	public void localLookup(KademliaOverlayKey id, int num,
			Collection<KademliaOverlayContact<T>> result);

	/**
	 * Marks the routing table entry with the given <code>id</code> as
	 * unresponsive, possibly removing it from the routing table.
	 * 
	 * @param id
	 *            the KademliaOverlayID of the node that is to be marked as
	 *            unresponsive.
	 */
	public void markUnresponsiveContact(T id);

	/**
	 * Sets the point in time at which a lookup over the network has been
	 * carried out for the bucket that contains <code>key</code>.
	 * 
	 * @param key
	 *            the key that has been looked up over the network.
	 * @param time
	 *            the point in time at which the lookup took place.
	 */
	public void setLastLookupTime(KademliaOverlayKey key, long time);

	/**
	 * Returns a collection containing KademliaOverlayKeys from buckets that
	 * have to be refreshed because no lookup (over the network) has taken place
	 * since the point in time <code>notLookedUpSince</code> or has never taken
	 * place at all (for instance, after routing table creation).
	 * 
	 * @param notLookedUpAfter
	 *            a point in time, given in simulation time units. If a bucket's
	 *            last lookup time is earlier than or equal to
	 *            <code>notLookedUpAfter</code>, or a lookup in that bucket has
	 *            never taken place at all, it has to be refreshed. For
	 *            instance, if
	 *            <code>notLookedUpAfter=getCurrentSimulationTime()</code>, then
	 *            all buckets have to be refreshed.
	 * @return a new Map instance with KademliaOverlayKeys that have to be
	 *         looked up over the network as keys and the bucket depth of the
	 *         corresponding bucket as values (0 being the root bucket).
	 */
	public Map<KademliaOverlayKey, Integer> getRefreshBuckets(
			long notLookedUpAfter);

	/**
	 * Registers <code>newListener</code> as listener for join-events triggered
	 * by new contacts becoming part of the {@KademliaConfig#K
	 * 
	 * 
	 * } closest nodes around this routing table owner's own
	 * ID.
	 * 
	 * @param newListener
	 *            the ProximityListener that is to be notified whenever a new
	 *            node becomes part of this routing table owner's K closest
	 *            neighbours.
	 */
	public void registerProximityListener(ProximityListener<T> newListener);

	/**
	 * Callback interface for clients that are interested in being notified
	 * about new nodes becoming part of the K closest nodes around the owner's
	 * ID.
	 * 
	 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
	 */
	public static interface ProximityListener<T extends KademliaOverlayID> {

		/**
		 * Called by the routing table that the ProximityListener is registered
		 * with whenever a new node that is part of the {@KademliaConfig#K
		 * 
		 * 
		 * } closest nodes around the routing table owner's ID
		 * becomes known. <i>New</i> means that either the contact has just
		 * joined the overlay network, or a formerly close contact has left the
		 * network <b>and</b> another contact has been seen.
		 * 
		 * @param newContact
		 *            the new KademliaOverlayContact that is now part of the K
		 *            closest nodes around this routing table owner's own ID.
		 */
		public void newCloseContactArrived(KademliaOverlayContact<T> newContact);

	}
}
