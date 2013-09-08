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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Test;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayContact;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayKey;
import org.peerfact.impl.overlay.dht.kademlia.base.components.RoutingTable.VisibilityRestrictableRoutingTable;
import org.peerfact.impl.overlay.dht.kademlia.hkademlia.components.HKademliaOverlayID;
import org.peerfact.impl.overlay.dht.kademlia.kandy.components.KandyRoutingTable;
import org.peerfact.util.helpers.TestHelper;


/**
 * Tests for KandyRoutingTable.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 */
public class KandyRoutingTableTest extends
		AbstractClusterPreferenceRoutingTableTest {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected VisibilityRestrictableRoutingTable<HKademliaOverlayID> getEmptyRoutingTable(
			KademliaOverlayContact<HKademliaOverlayID> owner) {
		return new KandyRoutingTable(owner, config);
	}

	/**
	 * Make sure that contacts do replace each other (that is, clusters do
	 * matter here) and that contacts from the buckets are visible according to
	 * Kandy's visibility rules.
	 * 
	 * @see KandyRoutingTableTest#testClustersDoMatterVisibilityRestriction()
	 * @see HKademliaRoutingTableTest#testClustersDoMatterNoVisibilityRestriction()
	 */
	@Test
	public void testClustersDoMatterVisibilityRestriction() {
		VisibilityRestrictableRoutingTable<HKademliaOverlayID> kRoutingTable;
		kRoutingTable = getEmptyRoutingTable(ownContact);
		// own contact is 00001001

		KademliaOverlayContact<HKademliaOverlayID> c01010000 = TestHelper
				.createContact("01010000", config);
		KademliaOverlayContact<HKademliaOverlayID> c01111000 = TestHelper
				.createContact("01111000", config);
		KademliaOverlayContact<HKademliaOverlayID> c01011001 = TestHelper
				.createContact("01011001", config);
		// add 3 contacts, expect to see last + own ID
		// (third replaces first one, second one not visible)
		kRoutingTable.addContact(c01010000);
		kRoutingTable.addContact(c01111000);
		kRoutingTable.addContact(c01011001);
		Collection<KademliaOverlayContact<HKademliaOverlayID>> result;
		result = kRoutingTable.visibilityRestrictedLocalLookup(
				new KademliaOverlayKey("01000000", config), 2);
		assertEquals("RT returns 2 results", 2, result.size());
		assertTrue("Result contains 01011001", result.contains(c01011001));
		assertTrue("Result contains own ID", result.contains(ownContact));
	}

	/**
	 * Test Kandy's visibility rule enforcement when adding/removing contacts.
	 */
	@Test
	public void testVisibility() {
		VisibilityRestrictableRoutingTable<HKademliaOverlayID> kRoutingTable;
		kRoutingTable = getEmptyRoutingTable(ownContact);

		// regular contacts
		KademliaOverlayContact<HKademliaOverlayID> c01000000, c01001000;
		KademliaOverlayContact<HKademliaOverlayID> c10000000, c10001000;
		KademliaOverlayContact<HKademliaOverlayID> c11000000, c11001000;
		KademliaOverlayContact<HKademliaOverlayID> c00010000;
		KademliaOverlayContact<HKademliaOverlayID> c00100000;
		KademliaOverlayContact<HKademliaOverlayID> c00110000;
		// later contacts
		KademliaOverlayContact<HKademliaOverlayID> c00111001;
		KademliaOverlayContact<HKademliaOverlayID> c00111000;
		KademliaOverlayContact<HKademliaOverlayID> c11111001;

		c01000000 = TestHelper.createContact("01000000", config);
		c01001000 = TestHelper.createContact("01001000", config);
		c10000000 = TestHelper.createContact("10000000", config);
		c10001000 = TestHelper.createContact("10001000", config);
		c11000000 = TestHelper.createContact("11000000", config);
		c11001000 = TestHelper.createContact("11001000", config);
		c00010000 = TestHelper.createContact("00010000", config);
		c00100000 = TestHelper.createContact("00100000", config);
		c00110000 = TestHelper.createContact("00110000", config);
		c00111001 = TestHelper.createContact("00111001", config);
		c00111000 = TestHelper.createContact("00111000", config);
		c11111001 = TestHelper.createContact("11111001", config);

		kRoutingTable.addContact(c01000000);
		kRoutingTable.addContact(c01001000);
		kRoutingTable.addContact(c10000000);
		kRoutingTable.addContact(c10001000);
		kRoutingTable.addContact(c11000000);
		kRoutingTable.addContact(c11001000);
		kRoutingTable.addContact(c00010000);
		kRoutingTable.addContact(c00100000);
		kRoutingTable.addContact(c00110000);

		Collection<KademliaOverlayContact<HKademliaOverlayID>> result;

		// visibility: cluster 0000 @ level 1 & cluster 1000 @ level 0
		// plus own ID
		result = kRoutingTable.visibilityRestrictedLocalLookup(ownID.toKey(),
				99);
		assertEquals("Result has size 7", 7, result.size());
		assertTrue("Result contains own ID", result.contains(ownContact));
		assertTrue("Result contains 00110000", result.contains(c00110000));
		assertTrue("Result contains 00100000", result.contains(c00100000));
		assertTrue("Result contains 00010000", result.contains(c00010000));
		assertTrue("Result contains 11001000", result.contains(c11001000));
		assertTrue("Result contains 10001000", result.contains(c10001000));
		assertTrue("Result contains 01001000", result.contains(c01001000));

		// add contact from cluster 1001 @ level 1
		// visibility: cluster 1001 @ level 0 & 1 (plus own ID)
		kRoutingTable.addContact(c00111001);
		result = kRoutingTable.visibilityRestrictedLocalLookup(ownID.toKey(),
				99);
		assertEquals("Result has size 2", 2, result.size());
		assertTrue("Result contains own ID", result.contains(ownContact));
		assertTrue("Result contains 00111001", result.contains(c00111001));

		// add contact from cluster 1001 @ level 0: will be visible
		kRoutingTable.addContact(c11111001);
		result = kRoutingTable.visibilityRestrictedLocalLookup(ownID.toKey(),
				99);
		assertEquals("Result has size 3", 3, result.size());
		assertTrue("Result contains own ID", result.contains(ownContact));
		assertTrue("Result contains 00111001", result.contains(c00111001));
		assertTrue("Result contains 11111001", result.contains(c11111001));

		// add contact from cluster 1000 (cache) & remove cluster 1001 @ level 1
		// visibility: cluster 1000 @ level 1, 1001 @ level 0 & own ID
		kRoutingTable.addContact(c00111000);
		for (int i = 1; i <= config.staleCounter; i++) {
			kRoutingTable.markUnresponsiveContact(c00111001.getOverlayID());
		}
		result = kRoutingTable.visibilityRestrictedLocalLookup(ownID.toKey(),
				99);
		assertEquals("Result has size 3", 3, result.size());
		assertTrue("Result contains own ID", result.contains(ownContact));
		assertTrue("Result contains 00111000", result.contains(c00111000));
		assertTrue("Result contains 11111001", result.contains(c11111001));

		// remove cluster 1001 @ level 1
		// visibility: cluster 1000 @ level 0 & 1 & own ID
		for (int i = 1; i <= config.staleCounter; i++) {
			kRoutingTable.markUnresponsiveContact(c11111001.getOverlayID());
		}
		result = kRoutingTable.visibilityRestrictedLocalLookup(ownID.toKey(),
				99);
		assertEquals("Result has size 5", 5, result.size());
		assertTrue("Result contains own ID", result.contains(ownContact));
		assertTrue("Result contains 00111000", result.contains(c00111000));
		assertTrue("Result contains 11001000", result.contains(c11001000));
		assertTrue("Result contains 10001000", result.contains(c10001000));
		assertTrue("Result contains 01001000", result.contains(c01001000));
	}

	/**
	 * Make sure that the proximity listener is informed about close contacts
	 * even if these are not visible (to restricted lookups).
	 */
	@Test
	public void testProximityListenerVisibility() {
		VisibilityRestrictableRoutingTable<HKademliaOverlayID> kRoutingTable;
		kRoutingTable = getEmptyRoutingTable(ownContact);
		// own ID is 00001001

		KademliaOverlayContact<HKademliaOverlayID> c11111001, c00000000;
		c11111001 = TestHelper.createContact("11111001", config);
		c00000000 = TestHelper.createContact("00000000", config);
		ProximityListenerStub listener = new ProximityListenerStub();
		listener.newContact = null;
		kRoutingTable.registerProximityListener(listener);

		kRoutingTable.addContact(c11111001);
		assertEquals("Listener notified of 11111001", c11111001,
				listener.newContact);
		listener.newContact = null;

		kRoutingTable.addContact(c00000000);
		assertEquals("Listener notified of 00000000", c00000000,
				listener.newContact);
		assertFalse("00000000 not visible", kRoutingTable
				.visibilityRestrictedLocalLookup(
						c00000000.getOverlayID().toKey(), 3)
				.contains(c00000000));
	}
}
