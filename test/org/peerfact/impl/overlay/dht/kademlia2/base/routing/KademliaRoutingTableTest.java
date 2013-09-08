/*
 * Copyright (c) 2012-2013 Open Source Community - <http://www.peerfact.org>
 * Copyright (c) 2011-2012 University of Paderborn - UPB
 * Copyright (c) 2005-2011 KOM - Multimedia Communications Lab
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */

package org.peerfact.impl.overlay.dht.kademlia2.base.routing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Test;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayContact;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayKey;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaRoutingTable;
import org.peerfact.impl.overlay.dht.kademlia.base.components.RoutingTable;
import org.peerfact.impl.overlay.dht.kademlia.hkademlia.components.HKademliaOverlayID;
import org.peerfact.util.helpers.TestHelper;


/**
 * Tests for KademliaRoutingTable.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 */
public class KademliaRoutingTableTest extends AbstractRoutingTableTest {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected RoutingTable<HKademliaOverlayID> getEmptyRoutingTable(
			KademliaOverlayContact<HKademliaOverlayID> owner) {
		return new KademliaRoutingTable<HKademliaOverlayID>(owner, config);
	}

	/**
	 * Make sure that contacts do not replace each other (that is, clusters do
	 * not matter here) and that all contacts from the buckets are visible.
	 * 
	 * @see KandyRoutingTableTest#testClustersDoMatterVisibilityRestriction()
	 * @see HKademliaRoutingTableTest#testClustersDoMatterNoVisibilityRestriction()
	 */
	@Test
	public void testClustersDontMatterAllVisible() {
		// own contact is 00001001
		KademliaOverlayContact<HKademliaOverlayID> c01010000 = TestHelper
				.createContact("01010000", config);
		KademliaOverlayContact<HKademliaOverlayID> c01111000 = TestHelper
				.createContact("01111000", config);
		KademliaOverlayContact<HKademliaOverlayID> c01011001 = TestHelper
				.createContact("01011001", config);
		// add 3 contacts, expect to see first 2 (all visible, no replacement)
		routingTable.addContact(c01010000);
		routingTable.addContact(c01111000);
		routingTable.addContact(c01011001);
		Collection<KademliaOverlayContact<HKademliaOverlayID>> result;
		result = routingTable.localLookup(new KademliaOverlayKey("01000000",
				config), 2);
		assertEquals("RT returns 2 results", 2, result.size());
		assertTrue("Result contains 01010000", result.contains(c01010000));
		assertTrue("Result contains 01111000", result.contains(c01111000));
		// 01011001 in cache -> not available.
	}

}
