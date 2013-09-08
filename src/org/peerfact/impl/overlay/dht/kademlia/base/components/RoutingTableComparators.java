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

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Comparator;

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
 * Comparators that are exclusively used in the context of Kademlia routing
 * tables.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public class RoutingTableComparators {

	/**
	 * A comparator over RoutingTableEntries that compares the wrapped
	 * KademliaOverlayContacts by their last seen time.
	 */
	@SuppressWarnings("unchecked")
	private static final Comparator<RoutingTableEntry<? extends KademliaOverlayID>> LASTSEEN_COMPARATOR = new LastSeenComp();

	/**
	 * @return a Comparator over RoutingTableEntries that compares the wrapped
	 *         KademliaOverlayContacts according to their last seen time
	 *         (preferring recently seen contacts).
	 */
	public static final Comparator<RoutingTableEntry<? extends KademliaOverlayID>> getLastSeenComp() {
		return LASTSEEN_COMPARATOR;
	}

	/**
	 * A Comparator over RoutingTableEntries that compares them according to
	 * their stale counter. Entries with a higher counter are smaller.
	 */
	private static final Comparator<RoutingTableEntry<?>> STALE_COMPARATOR = new StaleComparator();

	/**
	 * @return a Comparator over RoutingTableEntries that compares them
	 *         according to their stale counter. Entries with a higher counter
	 *         are smaller.
	 */
	public static final Comparator<RoutingTableEntry<?>> getStaleComparator() {
		return STALE_COMPARATOR;
	}

	/**
	 * A LastSeenComparator is capable of comparing RoutingTableEntry objects
	 * according to time last seen of the wrapped KademliaOverlayContact.
	 * 
	 * @author Sebastian Kaune
	 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
	 */
	static final class LastSeenComp<T extends KademliaOverlayID> implements
			Comparator<RoutingTableEntry<T>>, Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 7055497290981328986L;

		/**
		 * Compare two RoutingTableEntries resp. their KademliaOverlayContacts
		 * to determine which is seen last. If <code>arg0</code> is seen more
		 * recently than <code>arg1</code> a positive integer is returned, etc.
		 */
		@Override
		public final int compare(final RoutingTableEntry<T> arg0,
				final RoutingTableEntry<T> arg1) {
			return (int) (arg0.getLastSeen() - arg1.getLastSeen());
		}
	}

	/**
	 * A class that offers various comparator methods to compare objects
	 * according to their XOR value with a preset reference value. The object
	 * that has the higher XOR value with the reference will be considered as
	 * greater.
	 * <p>
	 * This class is abstract and does not implement the Comparator interface as
	 * it is the base class for several distinct Comparators on different types.
	 * (The Comparator interface cannot be implemented with different type
	 * parameters in the same class.)
	 * 
	 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
	 */
	public static abstract class XORMaxComparator<T extends KademliaOverlayID>
			implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = -479382727709827723L;

		/** Reference value used in the XOR metric */
		private final BigInteger reference;

		/**
		 * Constructs a new abstract BigIntRefComparator that stores a
		 * BigInteger on behalf of its subclasses to be used in comparisons
		 * later on.
		 * 
		 * @param reference
		 *            a BigInteger reference value.
		 */
		protected XORMaxComparator(final BigInteger reference) {
			this.reference = reference;
		}

		/**
		 * Compares two BigIntegers according to their XOR value with the preset
		 * reference value. A BigInteger is considered greater if it has a
		 * higher XOR value with the reference. The remaining contract of this
		 * method conforms with {@link Comparator#compare(Object, Object)}.
		 */
		public final int compare(final BigInteger o1, final BigInteger o2) {
			return o1.xor(reference).compareTo(o2.xor(reference));
		}

		/**
		 * Compares the BigInteger values of two KademliaOverlayIDs according to
		 * their XOR value with the preset reference value. A BigInteger is
		 * considered greater if it has a higher XOR value with the reference.
		 * The remaining contract of this method conforms with
		 * {@link Comparator#compare(Object, Object)}.
		 */
		public final int compare(final T o1, final T o2) {
			return compare(o1.getBigInt(), o2.getBigInt());
		}

		/**
		 * Compares the BigInteger values of the KademliaOverlayIDs contained in
		 * two KademliaOverlayContacts according to their XOR value with the
		 * preset reference value. A BigInteger is considered greater if it has
		 * a higher XOR value with the reference. The remaining contract of this
		 * method conforms with {@link Comparator#compare(Object, Object)}.
		 */
		public final int compare(final KademliaOverlayContact<T> o1,
				final KademliaOverlayContact<T> o2) {
			return compare(o1.getOverlayID(), o2.getOverlayID());
		}

		/**
		 * Compares the BigInteger values of the KademliaOverlayIDs contained in
		 * the KademliaOverlayContacts of two RoutingTableEntries according to
		 * their XOR value with the preset reference value. A BigInteger is
		 * considered greater if it has a higher XOR value with the reference.
		 * The remaining contract of this method conforms with
		 * {@link Comparator#compare(Object, Object)}.
		 */
		public final int compare(final RoutingTableEntry<T> o1,
				final RoutingTableEntry<T> o2) {
			return compare(o1.getContact(), o2.getContact());
		}

	}

	/**
	 * Comparator that orders BigIntegers according to their XOR value with a
	 * preset reference value. BigIntegers with a higher XOR are <i>"bigger"</i>
	 * than BigIntegers with a lower XOR.
	 * 
	 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
	 */
	public static final class BigIntegerXORMaxComparator extends
			XORMaxComparator<KademliaOverlayID> implements
			Comparator<BigInteger> {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1088534107376898381L;

		/**
		 * Constructs a new BigIntegerXORMaxComparator that compares BigIntegers
		 * by their XOR value with <code>reference</code>.
		 * 
		 * @param reference
		 *            the BigInteger used to calculate the XOR which the
		 *            BigIntegers to be ordered are compared against.
		 */
		public BigIntegerXORMaxComparator(final BigInteger reference) {
			super(reference);
		}

		// implementation of compare inherited from XORMaxComparator
	}

	/**
	 * Comparator that orders KademliaOverlayContacts contained in
	 * RoutingTableEntries according to the XOR value of their KademliaOverlayID
	 * with a preset reference value. IDs with a higher XOR are <i>"bigger"</i>
	 * than IDs with a lower XOR.
	 * 
	 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
	 */
	public static final class RoutingTableEntryXORMaxComparator<T extends KademliaOverlayID>
			extends XORMaxComparator<T> implements
			Comparator<RoutingTableEntry<T>> {

		/**
		 * 
		 */
		private static final long serialVersionUID = 2844237451751842721L;

		/**
		 * Constructs a new RoutingTableEntryXORMaxComparator that compares
		 * KademliaOverlayContacts contained in RoutingTableEntries by the XOR
		 * value of their KademliaOverlayID with <code>reference</code>.
		 * 
		 * @param ref
		 *            the BigInteger used to calculate the XOR which the
		 *            KademliaOverlayContacts to be ordered are compared
		 *            against.
		 */
		public RoutingTableEntryXORMaxComparator(final BigInteger ref) {
			super(ref);
		}

		// implementation of compare inherited from XORMaxComparator
	}

	/**
	 * Comparator that compares the cluster "closeness" of HKademliaOverlayIDs
	 * with a preset value (for example a routing table owner's identifier).
	 * <p>
	 * For example, if the preset ID is 110101 with cluster 0101, the identifier
	 * 010101 will be greater than 010100, because 010101 shares the same
	 * cluster as 110101 (with depth 2, thus in the "cluster metric", 010101 is
	 * equal to 110101), whereas 010100 only has common cluster depth 1 when
	 * compared to 110101 (in the "cluster metric", 010100 is one smaller than
	 * 110101).
	 * 
	 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
	 */
	static abstract class HierarchyComparator<H extends KademliaOverlayID>
			implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 4211175186423286163L;

		/**
		 * The reference ID which the other IDs are to be compared to.
		 */
		private final H reference;

		/**
		 * Constructs a new Comparator that compares the HKademliaOverlayIDs
		 * wrapped in RoutingTableEntries with respect to their cluster
		 * similarity with <code>reference</code>. That is, a HKademliaOverlayID
		 * that shares a deeper (specific) cluster with <code>reference</code>
		 * is considered greater than a HKademliaOverlayID with a more general
		 * cluster.
		 * 
		 * @param reference
		 *            the reference HKademliaOverlayID which the other IDs are
		 *            to be compared to.
		 */
		protected HierarchyComparator(final H reference) {
			this.reference = reference;
		}

		/**
		 * Compares the cluster depth of two HKademliaOverlayIDs with the preset
		 * reference value. A smaller common cluster depth results in the ID
		 * being considered smaller. The general contract is as in
		 * {@link Comparator#compare(Object, Object)}.
		 */
		public final int compare(final H o1, final H o2) {
			final int commonCluster1 = ((HKademliaOverlayID) reference)
					.getCommonClusterDepth((HKademliaOverlayID) o1);
			final int commonCluster2 = ((HKademliaOverlayID) reference)
					.getCommonClusterDepth((HKademliaOverlayID) o2);

			return (commonCluster1 - commonCluster2);
		}

		/**
		 * Compares the cluster depth of two HKademliaOverlayIDs from two
		 * KademliaOverlayContacts with the preset reference value. A smaller
		 * common cluster depth results in the ID being considered smaller. The
		 * general contract is as in {@link Comparator#compare(Object, Object)}.
		 */

		public final int compare(final KademliaOverlayContact<H> o1,
				final KademliaOverlayContact<H> o2) {
			return compare(o1.getOverlayID(), o2.getOverlayID());
		}

		/**
		 * Compares two RoutingTableEntries according to the cluster depth of
		 * their HKademliaOverlayIDs with the preset reference value. A smaller
		 * common cluster depth results in the ID being considered smaller. The
		 * general contract is as in {@link Comparator#compare(Object, Object)}.
		 */
		public final int compare(final RoutingTableEntry<H> o1,
				final RoutingTableEntry<H> o2) {
			return compare(o1.getContact(), o2.getContact());
		}
	}

	/**
	 * Comparator that compares KademliaOverlayContacts resp. the wrapped
	 * HKademliaOverlayIDs by the cluster "closeness" with a preset value (for
	 * example a routing table owner's identifier).
	 * 
	 * @see HierarchyComparator
	 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
	 */
	public static final class KademliaOverlayContactHierarchyComparator<H extends KademliaOverlayID>
			extends HierarchyComparator<H> implements
			Comparator<KademliaOverlayContact<H>> {

		/**
		 * 
		 */
		private static final long serialVersionUID = 8336764135242815276L;

		/**
		 * Constructs a new Comparator that compares the HKademliaOverlayIDs
		 * wrapped in KademliaOverlayContacts with respect to their cluster
		 * similarity with <code>reference</code>. That is, a HKademliaOverlayID
		 * that shares a deeper (specific) cluster with <code>reference</code>
		 * is considered greater than a HKademliaOverlayID with a more general
		 * cluster.
		 * 
		 * @param reference
		 *            the reference HKademliaOverlayID which the other IDs are
		 *            to be compared to.
		 */
		public KademliaOverlayContactHierarchyComparator(final H reference) {
			super(reference);
		}

		// implementation of compare inherited from superclass
	}

	/**
	 * Comparator that compares RoutingTableEntries resp. the wrapped
	 * KademliaOverlayContacts by the cluster "closeness" of their
	 * HKademliaOverlayIDs with a preset value (for example a routing table
	 * owner's identifier).
	 * 
	 * @see HierarchyComparator
	 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
	 */
	public static final class RoutingTableEntryHierarchyComparator<H extends KademliaOverlayID>
			extends HierarchyComparator<H> implements
			Comparator<RoutingTableEntry<H>> {

		/**
		 * 
		 */
		private static final long serialVersionUID = 4503066728878210956L;

		/**
		 * Constructs a new Comparator that compares the HKademliaOverlayIDs
		 * wrapped in RoutingTableEntries with respect to their cluster
		 * similarity with <code>reference</code>. That is, a HKademliaOverlayID
		 * that shares a deeper (specific) cluster with <code>reference</code>
		 * is considered greater than a HKademliaOverlayID with a more general
		 * cluster.
		 * 
		 * @param reference
		 *            the reference HKademliaOverlayID which the other IDs are
		 *            to be compared to.
		 */
		public RoutingTableEntryHierarchyComparator(final H reference) {
			super(reference);
		}

		// implementation of compare inherited from superclass
	}

	/**
	 * Compares RoutingTableEntries by their stale counter. Contacts with a
	 * higher stale counter are smaller.
	 * 
	 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
	 */
	public static final class StaleComparator implements
			Comparator<RoutingTableEntry<?>>, Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = -1465178159304171712L;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final int compare(final RoutingTableEntry<?> o1,
				final RoutingTableEntry<?> o2) {
			// LARGER stale counter is SMALLER
			return -(o1.getStaleCounter() - o2.getStaleCounter());
		}
	}

}
