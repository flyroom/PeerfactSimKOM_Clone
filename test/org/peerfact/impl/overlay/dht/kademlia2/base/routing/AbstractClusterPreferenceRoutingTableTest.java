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
import org.peerfact.util.helpers.TestHelper;


/**
 * (Additional) tests for routing tables that prefer contacts from deeper
 * clusters (such as Kandy and HKademlia).
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 */
public abstract class AbstractClusterPreferenceRoutingTableTest extends
		AbstractRoutingTableTest {

	/**
	 * If a contact with a higher priority (deeper cluster) is seen, it replaces
	 * a contact with a lower priority (given that the bucket is full). That
	 * contact is moved to the cache. The proximity handler needs to be
	 * notified.
	 */
	@Test
	public void testAddDeeperContact() {
		// own ID is 00001001
		KademliaOverlayContact<HKademliaOverlayID> c01001001, c01011011, c01101001, c01111011;
		c01111011 = TestHelper.createContact("01111011", config);
		c01011011 = TestHelper.createContact("01011011", config);
		c01001001 = TestHelper.createContact("01001001", config);
		c01101001 = TestHelper.createContact("01101001", config);
		KademliaOverlayKey k01000000 = new KademliaOverlayKey("01000000",
				config);
		Collection<KademliaOverlayContact<HKademliaOverlayID>> firstState, secondState, thirdState;
		ProximityListenerStub listener = new ProximityListenerStub();

		// to circumvent Kandy's visibility rule, we need to test with 2
		// equally prioritised contacts at the same time:
		// add two contacts from cluster 1011 (depth 1)
		// proximity listener for that case tested in AbstractRoutingTableTest
		routingTable.addContact(c01111011);
		routingTable.addContact(c01011011);
		firstState = routingTable.localLookup(k01000000, 2);
		assertTrue("Initially contains 01111011", firstState
				.contains(c01111011));
		assertTrue("Initially contains 01011011", firstState
				.contains(c01011011));

		// add two contacts from cluster 1001 (depth 2) -> should replace
		// old contacts in bucket, listener should be notified (both closer)
		routingTable.registerProximityListener(listener);
		listener.newContact = null;
		routingTable.addContact(c01101001);
		assertEquals("Listener notifies about 01101001", c01101001,
				listener.newContact);
		listener.newContact = null;
		routingTable.addContact(c01001001);
		assertEquals("Listener notifies about 01001001", c01001001,
				listener.newContact);
		secondState = routingTable.localLookup(k01000000, 2);
		assertTrue("Then contains 01101001", secondState.contains(c01101001));
		assertTrue("Then contains 01001001", secondState.contains(c01001001));

		// mark bucket as unresponsive -> make sure contacts were in cache
		// proximity listener for that case tested in AbstractRoutingTableTest
		for (int i = 1; i <= config.staleCounter; i++) {
			routingTable.markUnresponsiveContact(c01101001.getOverlayID());
			routingTable.markUnresponsiveContact(c01001001.getOverlayID());
		}
		thirdState = routingTable.localLookup(k01000000, 2);
		assertEquals("Initial state is back", firstState, thirdState);
	}

	/**
	 * If a contact for replacement is to be chosen from the cache, the contact
	 * with the deepest cluster should be chosen.
	 */
	@Test
	public void testRightReplacementChosen() {
		// own ID is 00001001
		KademliaOverlayContact<HKademliaOverlayID> c11001001, c11011001, c11101001, c11111011;
		// bucket
		c11101001 = TestHelper.createContact("11101001", config); // depth=2
		c11001001 = TestHelper.createContact("11001001", config); // depth=2
		// cache
		c11011001 = TestHelper.createContact("11011001", config); // depth=2
		c11111011 = TestHelper.createContact("11111011", config); // depth=1

		HKademliaOverlayID i11101001;
		i11101001 = new HKademliaOverlayID("11101001", config);
		KademliaOverlayKey k11101001 = new KademliaOverlayKey("11101001",
				config);
		Collection<KademliaOverlayContact<HKademliaOverlayID>> result;

		// add 4 contacts, mark one, expect to see "best" from cache
		// simplification: no check whether choice from cache is "by accident"
		routingTable.addContact(c11101001);
		routingTable.addContact(c11001001);
		routingTable.addContact(c11011001);
		routingTable.addContact(c11111011);
		for (int i = 1; i <= config.staleCounter; i++) {
			routingTable.markUnresponsiveContact(i11101001);
		}
		result = routingTable.localLookup(k11101001, 2);
		assertTrue("Result contains 11001001", result.contains(c11001001));
		assertTrue("Result contains 11011001 (replacement)", result
				.contains(c11011001));
	}

	/**
	 * Make sure that contacts do replace each other (that is, clusters do
	 * matter here) but that there is no visibility restriction in a regular
	 * lookup.
	 * 
	 * @see KademliaRoutingTableTest#testClustersDontMatterAllVisible()
	 * @see KandyRoutingTableTest#testClustersDoMatterVisibilityRestriction()
	 */
	@Test
	public void testClustersDoMatterNoVisibilityRestriction() {
		// own contact is 00001001
		KademliaOverlayContact<HKademliaOverlayID> c01010000 = TestHelper
				.createContact("01010000", config);
		KademliaOverlayContact<HKademliaOverlayID> c01111000 = TestHelper
				.createContact("01111000", config);
		KademliaOverlayContact<HKademliaOverlayID> c01011001 = TestHelper
				.createContact("01011001", config);
		// add 3 contacts, expect to see last 2 (3rd replaces 1st one)
		// last 2 different clusters -> no visibility restriction
		routingTable.addContact(c01010000);
		routingTable.addContact(c01111000);
		routingTable.addContact(c01011001);
		Collection<KademliaOverlayContact<HKademliaOverlayID>> result;
		result = routingTable.localLookup(new KademliaOverlayKey("01000000",
				config), 2);
		assertEquals("RT returns 2 results", 2, result.size());
		assertTrue("Result contains 01111000", result.contains(c01111000));
		assertTrue("Result contains 01011001", result.contains(c01011001));
	}

}
