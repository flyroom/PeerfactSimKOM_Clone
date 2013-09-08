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
import org.peerfact.impl.overlay.dht.kademlia.hkademlia.components.HKademliaOverlayID;
import org.peerfact.impl.overlay.dht.kademlia.hkademlia.components.HKademliaRoutingTable;
import org.peerfact.util.helpers.TestHelper;


/**
 * Tests for HKademliaRoutingTable.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 */
public class HKademliaRoutingTableTest extends
		AbstractClusterPreferenceRoutingTableTest {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected HKademliaRoutingTable getEmptyRoutingTable(
			KademliaOverlayContact<HKademliaOverlayID> owner) {
		return new HKademliaRoutingTable(owner, config);
	}

	/**
	 * Lookup with cluster restriction and reference works as expected.
	 */
	@Test
	public void testLookupWithClusterRestriction() {
		HKademliaRoutingTable hRoutingTable;
		hRoutingTable = getEmptyRoutingTable(ownContact);

		KademliaOverlayContact<HKademliaOverlayID> c11001010, c11001000;
		KademliaOverlayContact<HKademliaOverlayID> c10001010, c10001000;
		c11001010 = TestHelper.createContact("11001010", config);
		c11001000 = TestHelper.createContact("11001000", config);
		c10001010 = TestHelper.createContact("10001010", config);
		c10001000 = TestHelper.createContact("10001000", config);

		hRoutingTable.addContact(c11001010);
		hRoutingTable.addContact(c11001000);
		hRoutingTable.addContact(c10001010);
		hRoutingTable.addContact(c10001000);

		KademliaOverlayKey k11000000 = new KademliaOverlayKey("11000000",
				config);
		HKademliaOverlayID i00001010 = new HKademliaOverlayID("1010", config);
		HKademliaOverlayID i00000000 = new HKademliaOverlayID("0", config);
		HKademliaOverlayID i00001000 = new HKademliaOverlayID("1000", config);

		Collection<KademliaOverlayContact<HKademliaOverlayID>> result;

		// look up 11000000 with reference cluster 1010, depth >= 2
		result = hRoutingTable.localLookup(k11000000, 2, 2, i00001010);
		assertTrue("Result contains 11001010", result.contains(c11001010));
		assertTrue("Result contains 10001010", result.contains(c10001010));

		// look up 11000000 with reference cluster 1010, depth >= 1
		result = hRoutingTable.localLookup(k11000000, 2, 1, i00001010);
		assertTrue("Result contains 11001010", result.contains(c11001010));
		assertTrue("Result contains 11001000", result.contains(c11001000));

		// look up 11000000 with reference cluster 0000, depth >= 2
		result = hRoutingTable.localLookup(k11000000, 2, 2, i00000000);
		assertEquals("Result is empty", 0, result.size());

		// look up 11000000 with reference cluster 0000, depth >= 1
		result = hRoutingTable.localLookup(k11000000, 2, 1, i00000000);
		assertEquals("Result is empty", 0, result.size());

		// look up 11000000 with reference cluster 0000, depth >= 0
		result = hRoutingTable.localLookup(k11000000, 2, 0, i00000000);
		assertTrue("Result contains 11001010", result.contains(c11001010));
		assertTrue("Result contains 11001000", result.contains(c11001000));

		// look up 11000000 with reference cluster 1000, depth >= 2
		result = hRoutingTable.localLookup(k11000000, 2, 2, i00001000);
		assertTrue("Result contains 11001000", result.contains(c11001000));
		assertTrue("Result contains 10001000", result.contains(c10001000));

		// look up 11000000 with reference cluster 1000, depth >= 1
		result = hRoutingTable.localLookup(k11000000, 2, 1, i00001000);
		assertTrue("Result contains 11001000", result.contains(c11001000));
		assertTrue("Result contains 11001010", result.contains(c11001010));
	}

}
