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

package org.peerfact.impl.util.toolkits;

import java.math.BigInteger;
import java.util.Comparator;

import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayContact;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayID;
import org.peerfact.impl.overlay.dht.kademlia.base.components.Node;
import org.peerfact.impl.overlay.dht.kademlia.base.components.RoutingTableComparators;
import org.peerfact.impl.overlay.dht.kademlia.base.components.RoutingTableComparators.XORMaxComparator;


/**
 * Defines often used comparators.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @author Sebastian Kaune
 * @version 05/06/2011
 */
public class Comparators {

	/**
	 * Comparator that orders KademliaOverlayContacts according to the XOR value
	 * of their KademliaOverlayID with a preset reference value. IDs with a
	 * higher XOR are <i>"bigger"</i> than IDs with a lower XOR.
	 * 
	 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
	 */
	public static class KademliaOverlayContactXORMaxComparator<T extends KademliaOverlayID>
			extends XORMaxComparator<T> implements
			Comparator<KademliaOverlayContact<T>> {

		/**
		 * 
		 */
		private static final long serialVersionUID = 49788573290831564L;

		/**
		 * Constructs a new KademliaOverlayContactXORMaxComparator that compares
		 * KademliaOverlayContacts by the XOR value of their KademliaOverlayID
		 * with <code>reference</code>.
		 * 
		 * @param ref
		 *            the BigInteger used to calculate the XOR which the
		 *            KademliaOverlayContacts to be ordered are compared
		 *            against.
		 */
		public KademliaOverlayContactXORMaxComparator(final BigInteger ref) {
			super(ref);
		}

		// implementation of compare inherited from XORMaxComparator
	}

	/**
	 * Comparator that orders Nodes according to the XOR value of their
	 * KademliaOverlayID with a preset reference value. IDs with a higher XOR
	 * are <i>"bigger"</i> than IDs with a lower XOR.
	 * 
	 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
	 */
	public static class NodeXORMaxComparator<T extends KademliaOverlayID>
			extends XORMaxComparator<T> implements Comparator<Node<T>> {

		/**
		 * 
		 */
		private static final long serialVersionUID = -5833049977859776136L;

		/**
		 * Constructs a new KademliaOverlayContactXORMaxComparator that compares
		 * KademliaOverlayContacts by the XOR value of their KademliaOverlayID
		 * with <code>reference</code>.
		 * 
		 * @param ref
		 *            the BigInteger used to calculate the XOR which the
		 *            KademliaOverlayContacts to be ordered are compared
		 *            against.
		 */
		public NodeXORMaxComparator(final BigInteger ref) {
			super(ref);
		}

		/**
		 * Compares the BigInteger values of the KademliaOverlayIDs contained in
		 * the KademliaOverlayContacts of two Nodes (from the
		 * kademlia2.components package!) according to their XOR value with the
		 * preset reference value. A BigInteger is considered greater if it has
		 * a higher XOR value with the reference. The remaining contract of this
		 * method conforms with {@link Comparator}.
		 */
		@Override
		public final int compare(final Node<T> o1, final Node<T> o2) {
			return compare(o1.getLocalContact(), o2.getLocalContact());
		}
	}

	/**
	 * Comparator that orders KademliaOverlayIDs according to the XOR value of
	 * their BigInteger representation with a preset reference value. IDs with a
	 * higher XOR are <i>"bigger"</i> than IDs with a lower XOR.
	 * 
	 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
	 */
	public static class KademliaOverlayIDXORMaxComparator<T extends KademliaOverlayID>
			extends RoutingTableComparators.XORMaxComparator<T> implements
			Comparator<T> {

		/**
		 * 
		 */
		private static final long serialVersionUID = -1764136602158184470L;

		/**
		 * Constructs a new KademliaOverlayIDXORMaxComparator that compares
		 * KademliaOverlayIDs via the XOR distance between their BigInteger IDs
		 * and <code>ref</code>.
		 * 
		 * @param reference
		 *            the BigInteger used to calculate the XOR which the
		 *            BigInteger representation of the KademliaOverlayIDs to be
		 *            ordered are compared against.
		 */
		public KademliaOverlayIDXORMaxComparator(final BigInteger ref) {
			super(ref);
		}

		// implementation of compare inherited from XORMaxComparator
	}

}
