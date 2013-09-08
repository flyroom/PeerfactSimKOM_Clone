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
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayContact;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayKey;
import org.peerfact.impl.overlay.dht.kademlia.base.components.RoutingTable;
import org.peerfact.impl.overlay.dht.kademlia.base.components.RoutingTable.ProximityListener;
import org.peerfact.impl.overlay.dht.kademlia.hkademlia.components.HKademliaOverlayID;
import org.peerfact.impl.overlay.dht.kademlia2.setup.ConfigStub;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.util.helpers.TestHelper;


/**
 * Contains tests that can be carried out for all three types of Kademlia
 * routing tables (standard, hierarchical, Kandy).
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 */
public abstract class AbstractRoutingTableTest {

	protected ConfigStub config;

	protected RoutingTable<HKademliaOverlayID> routingTable;

	protected HKademliaOverlayID ownID;

	protected KademliaOverlayContact<HKademliaOverlayID> ownContact;

	/**
	 * Initialise this test.
	 * 
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static final void setUpBeforeClass() throws Exception {
		// Create dummy scheduler
		TestHelper.initSimulator();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public final void setUp() throws Exception {
		config = new ConfigStub();
		config.bucketSize = 2;
		config.routingTreeOrder = 2;
		config.idLength = 8;
		config.replacementCacheSize = 2;
		config.hierarchyTreeOrder = 2;
		config.hierarchyDepth = 2;

		ownID = new HKademliaOverlayID("00001001", config);
		ownContact = TestHelper.createContact(ownID, 12);
		routingTable = getEmptyRoutingTable(ownContact);
	}

	/**
	 * @param owner
	 *            the KademliaOverlayContact of the routing table owner.
	 * @return an empty RoutingTable.
	 */
	protected abstract RoutingTable<HKademliaOverlayID> getEmptyRoutingTable(
			KademliaOverlayContact<HKademliaOverlayID> owner);

	/**
	 * Each routing table should initially contain the owner's ID.
	 */
	@Test
	public void testContainsOwnID() {
		Collection<KademliaOverlayContact<HKademliaOverlayID>> result;
		result = routingTable.localLookup(new KademliaOverlayKey("00001001",
				config), 2);

		assertEquals("Expect initially one result", 1, result.size());
		assertTrue(
				"Routing table should initially contain own contact (here: ID 00001001)",
				result.contains(ownContact));
	}

	/**
	 * A lookup does not return contacts contained in a replacement cache.
	 */
	@Test
	public void testLookupNoContactsFromCache() {
		// add contacts into a bucket that cannot be split (one in cache)
		KademliaOverlayContact<HKademliaOverlayID> c11000010 = TestHelper
				.createContact("11000010", config);
		KademliaOverlayContact<HKademliaOverlayID> c11100010 = TestHelper
				.createContact("11100010", config);
		KademliaOverlayContact<HKademliaOverlayID> c11110010 = TestHelper
				.createContact("11110010", config);
		routingTable.addContact(c11000010); // bucket 11
		routingTable.addContact(c11100010); // bucket 11
		routingTable.addContact(c11110010); // cache 11
		Collection<KademliaOverlayContact<HKademliaOverlayID>> rtContents;
		rtContents = routingTable.localLookup(new KademliaOverlayKey(
				"00000000", config), 5);
		assertEquals(
				"Routing table contains 3 contacts (contact in cache is not visible)",
				3, rtContents.size());
		assertTrue("RT contains own contact", rtContents.contains(ownContact));
		assertTrue("RT contains 11000010 (bucket)", rtContents
				.contains(c11000010));
		assertTrue("RT contains 11100010 (bucket)", rtContents
				.contains(c11100010));
		assertFalse("RT does not return 11110010 (cache)", rtContents
				.contains(c11110010));
	}

	/**
	 * Test whether a regular (thus unfiltered) lookup returns all contacts from
	 * the buckets, no matter their cluster.
	 */
	@Test
	public void testUnfilteredLookup() {
		// own ID is 00001001
		// add contacts from different clusters at same level
		// --> not visible in Kandy's restricted lookup, but should be here
		KademliaOverlayContact<HKademliaOverlayID> c11000010 = TestHelper
				.createContact("11000010", config);
		KademliaOverlayContact<HKademliaOverlayID> c11101101 = TestHelper
				.createContact("11101101", config);
		KademliaOverlayContact<HKademliaOverlayID> c01111000 = TestHelper
				.createContact("01111000", config);
		KademliaOverlayContact<HKademliaOverlayID> c01011001 = TestHelper
				.createContact("01011001", config);
		routingTable.addContact(c11000010); // depth 0
		routingTable.addContact(c11101101); // depth 0
		routingTable.addContact(c01111000); // depth 1
		routingTable.addContact(c01011001); // depth 2
		Collection<KademliaOverlayContact<HKademliaOverlayID>> rtContents;
		rtContents = routingTable.localLookup(new KademliaOverlayKey(
				"00000000", config), 6);
		assertEquals("Routing table contains 5 contacts", 5, rtContents.size());
		assertTrue("RT contains own contact", rtContents.contains(ownContact));
		assertTrue("RT contains 11000010", rtContents.contains(c11000010));
		assertTrue("RT contains 11101101", rtContents.contains(c11101101));
		assertTrue("RT contains 01111000", rtContents.contains(c01111000));
		assertTrue("RT contains 01011001", rtContents.contains(c01011001));
	}

	/**
	 * Test if splitting buckets works as expected and the lookup result has the
	 * correct size.
	 */
	@Test
	public void testSplitAndTestResult() {
		KademliaOverlayContact<HKademliaOverlayID> c00001010, c00011000, c00011010;
		KademliaOverlayContact<HKademliaOverlayID> c00101000, c00101010, c00111000;
		KademliaOverlayContact<HKademliaOverlayID> c00111010, c01001000, c01001010;
		KademliaOverlayContact<HKademliaOverlayID> c10001000, c10001010, c11001000, c11001010;
		c00001010 = TestHelper.createContact("00001010", config);
		c00011000 = TestHelper.createContact("00011000", config);
		c00011010 = TestHelper.createContact("00011010", config);
		c00101000 = TestHelper.createContact("00101000", config);
		c00101010 = TestHelper.createContact("00101010", config);
		c00111000 = TestHelper.createContact("00111000", config);
		c00111010 = TestHelper.createContact("00111010", config);
		c01001000 = TestHelper.createContact("01001000", config);
		c01001010 = TestHelper.createContact("01001010", config);
		c10001000 = TestHelper.createContact("10001000", config);
		c10001010 = TestHelper.createContact("10001010", config);
		c11001000 = TestHelper.createContact("11001000", config);
		c11001010 = TestHelper.createContact("11001010", config);
		Set<KademliaOverlayContact<HKademliaOverlayID>> someContacts, result;
		someContacts = new LinkedHashSet<KademliaOverlayContact<HKademliaOverlayID>>();
		someContacts.add(c00001010);
		someContacts.add(c00011000);
		someContacts.add(c00011010);
		someContacts.add(c00101000);
		someContacts.add(c00101010);
		someContacts.add(c00111000);
		someContacts.add(c00111010);
		someContacts.add(c01001000);
		someContacts.add(c01001010);
		someContacts.add(c10001000);
		someContacts.add(c10001010);
		someContacts.add(c11001000);
		someContacts.add(c11001010);

		// put all
		for (KademliaOverlayContact<HKademliaOverlayID> c : someContacts) {
			routingTable.addContact(c);
		}

		// lookup as many as possible
		result = routingTable.localLookup(new KademliaOverlayKey("0", config),
				15);
		assertEquals("Routing table contains 14 contacts", 14, result.size());
		someContacts.add(ownContact);
		assertEquals("Routing table contains correct contacts", someContacts,
				result);

		// look up only 13 contacts
		result = routingTable.localLookup(new KademliaOverlayKey("0", config),
				13);
		assertEquals("Result contains 13 contacts", 13, result.size());
		someContacts.remove(c11001010);
		assertEquals("Routing table contains correct contacts", someContacts,
				result);

		// look up key 01111010 with 5 results expected
		result = routingTable.localLookup(new KademliaOverlayKey("01111010",
				config), 5);
		someContacts.clear();
		someContacts.add(c01001000);
		someContacts.add(c01001010);
		someContacts.add(c00111010);
		someContacts.add(c00111000);
		someContacts.add(c00101010);
		assertEquals("Result contains 5 contacts", 5, result.size());
		assertEquals("Correct result returned", someContacts, result);
	}

	/**
	 * If the last lookup time has never been set, all buckets need to be
	 * refreshed.
	 */
	@Test
	public void testRefreshLookupTimeNotSet() throws Exception {
		// insert some contacts
		KademliaOverlayContact<HKademliaOverlayID> c11000000 = TestHelper
				.createContact("11000000", config);
		KademliaOverlayContact<HKademliaOverlayID> c00000000 = TestHelper
				.createContact("00000000", config);
		KademliaOverlayContact<HKademliaOverlayID> c00110000 = TestHelper
				.createContact("00110000", config);
		routingTable.addContact(c11000000);
		routingTable.addContact(c00000000);
		routingTable.addContact(c00110000);

		// check result at some "arbitrary" point in time
		TestHelper.setSimulationTime((long) (0.5 * config.refreshInterval));
		long notLookedUpAfter = Simulator.getCurrentTime()
				- config.refreshInterval;
		Map<KademliaOverlayKey, Integer> refreshBuckets;
		refreshBuckets = routingTable.getRefreshBuckets(notLookedUpAfter);
		assertEquals("Initially need to refresh all buckets", 7, refreshBuckets
				.size());
		// this kind of test only works if lookup keys are not random
		assertEquals("Bucket 0000 is at depth 2", Integer.valueOf(2),
				refreshBuckets
				.get(new KademliaOverlayKey("00000000", config)));
		assertEquals("Bucket 0001 is at depth 2", Integer.valueOf(2),
				refreshBuckets
				.get(new KademliaOverlayKey("00010000", config)));
		assertEquals("Bucket 0010 is at depth 2", Integer.valueOf(2),
				refreshBuckets
				.get(new KademliaOverlayKey("00100000", config)));
		assertEquals("Bucket 0011 is at depth 2", Integer.valueOf(2),
				refreshBuckets
				.get(new KademliaOverlayKey("00110000", config)));
		assertEquals("Bucket 01 is at depth 1", Integer.valueOf(1), refreshBuckets
				.get(new KademliaOverlayKey("01000000", config)));
		assertEquals("Bucket 10 is at depth 1", Integer.valueOf(1), refreshBuckets
				.get(new KademliaOverlayKey("10000000", config)));
		assertEquals("Bucket 11 is at depth 1", Integer.valueOf(1), refreshBuckets
				.get(new KademliaOverlayKey("11000000", config)));
	}

	/**
	 * If a bucket lookup has been carried out, that bucket has to be refreshed
	 * but one refresh interval later.
	 */
	@Test
	public void testRefreshLookupTimeSet() throws Exception {
		// insert some contacts
		KademliaOverlayContact<HKademliaOverlayID> c10000000 = TestHelper
				.createContact("10000000", config);
		KademliaOverlayContact<HKademliaOverlayID> c00010000 = TestHelper
				.createContact("00010000", config);
		routingTable.addContact(c10000000);
		routingTable.addContact(c00010000);

		// mark bucket 10 as looked up at time 10
		TestHelper.setSimulationTime(10l);
		routingTable.setLastLookupTime(new KademliaOverlayKey("10000000",
				config), 10l);

		// check result less than one refresh interval later
		TestHelper
		.setSimulationTime(10l + (long) (0.5 * config.refreshInterval));
		long notLookedUpAfter = Simulator.getCurrentTime()
				- config.refreshInterval;
		Map<KademliaOverlayKey, Integer> refreshBuckets;
		refreshBuckets = routingTable.getRefreshBuckets(notLookedUpAfter);
		assertEquals(
				"Less than one hour after last lookup, need to refresh 3 buckets",
				3, refreshBuckets.size());
		// this kind of test only works if lookup keys are not random
		assertEquals("Bucket 00 is at depth 1", Integer.valueOf(1),
				refreshBuckets
				.get(new KademliaOverlayKey("00000000", config)));
		assertEquals("Bucket 01 is at depth 1", Integer.valueOf(1),
				refreshBuckets
				.get(new KademliaOverlayKey("01000000", config)));
		assertEquals("Bucket 11 is at depth 1", Integer.valueOf(1),
				refreshBuckets
				.get(new KademliaOverlayKey("11000000", config)));

		// check result one refresh interval later
		TestHelper.setSimulationTime(10l + config.refreshInterval);
		notLookedUpAfter = Simulator.getCurrentTime() - config.refreshInterval;
		refreshBuckets = routingTable.getRefreshBuckets(notLookedUpAfter);
		assertEquals(
				"More than one hour after last lookup, need to refresh 4 buckets",
				4, refreshBuckets.size());
		// this kind of test only works if lookup keys are not random
		assertEquals("Bucket 00 is at depth 1", Integer.valueOf(1),
				refreshBuckets
				.get(new KademliaOverlayKey("00000000", config)));
		assertEquals("Bucket 01 is at depth 1", Integer.valueOf(1),
				refreshBuckets
				.get(new KademliaOverlayKey("01000000", config)));
		assertEquals("Bucket 10 is at depth 1", Integer.valueOf(1),
				refreshBuckets
				.get(new KademliaOverlayKey("10000000", config)));
		assertEquals("Bucket 11 is at depth 1", Integer.valueOf(1),
				refreshBuckets
				.get(new KademliaOverlayKey("11000000", config)));
	}

	/**
	 * A marked contact is not removed from the bucket if the replacement cache
	 * is empty.
	 */
	@Test
	public void testMarkUnresponsiveEmptyCache() {
		KademliaOverlayContact<HKademliaOverlayID> c11000100 = TestHelper
				.createContact("11000100", config);
		routingTable.addContact(c11000100);
		Collection<KademliaOverlayContact<HKademliaOverlayID>> rtContentsBefore;
		rtContentsBefore = routingTable.localLookup(new KademliaOverlayKey(
				"00000000", config), 3);
		assertEquals("Routing table contains 2 contacts", 2, rtContentsBefore
				.size());

		for (int i = 1; i <= config.staleCounter + 1; i++) {
			routingTable.markUnresponsiveContact(c11000100.getOverlayID());
			assertEquals("Routing table contents has not been modified",
					rtContentsBefore, routingTable.localLookup(
							new KademliaOverlayKey("00000000", config), 3));
		}
	}

	/**
	 * A contact with STALE_COUNTER marks from a full bucket (and empty cache)
	 * is replaced as soon as a new contact is seen.
	 */
	@Test
	public void testMarkUnresponsiveEmptyCacheReplaceUnresponsiveWhenNewContactSeen() {
		KademliaOverlayContact<HKademliaOverlayID> c01000000 = TestHelper
				.createContact("01000000", config);
		KademliaOverlayContact<HKademliaOverlayID> c01010000 = TestHelper
				.createContact("01010000", config);
		KademliaOverlayContact<HKademliaOverlayID> c01110000 = TestHelper
				.createContact("01110000", config);

		// add two contacts, mark one of them -> should stay as cache empty
		routingTable.addContact(c01000000);
		routingTable.addContact(c01010000);
		Collection<KademliaOverlayContact<HKademliaOverlayID>> before, result;
		before = routingTable.localLookup(c01000000.getOverlayID().toKey(), 2);
		for (int i = 1; i <= config.staleCounter; i++) {
			routingTable.markUnresponsiveContact(c01000000.getOverlayID());
			result = routingTable.localLookup(c01000000.getOverlayID().toKey(),
					2);
			assertEquals("RT contents not modified", before, result);
		}

		// add another contact to bucket -> should replace stale contact
		routingTable.addContact(c01110000);
		result = routingTable.localLookup(c01000000.getOverlayID().toKey(), 4);
		assertEquals("RT contains 3 contacts", 3, result.size());
		assertTrue("RT contains own ID", result.contains(ownContact));
		assertTrue("RT contains 01010000", result.contains(c01010000));
		assertFalse("RT does not contain 01000000", result.contains(c01000000));
		assertTrue("RT contains 01110000", result.contains(c01110000));
	}

	/**
	 * A marked contact is replaced having obtained STALE_COUNTER marks with a
	 * contact from the replacement cache (here: only one replacement contact
	 * available). Also assures that a contact's stale counter is reset when the
	 * contact is reinserted.
	 */
	@Test
	public void testMarkUnresponsiveOneReplacementCandidate() {
		// add contacts into a bucket that cannot be split (one in cache)
		KademliaOverlayContact<HKademliaOverlayID> c11000001 = TestHelper
				.createContact("11000001", config);
		KademliaOverlayContact<HKademliaOverlayID> c11100001 = TestHelper
				.createContact("11100001", config);
		KademliaOverlayContact<HKademliaOverlayID> c11110001 = TestHelper
				.createContact("11110001", config);
		routingTable.addContact(c11000001); // bucket 11
		routingTable.addContact(c11100001); // bucket 11
		routingTable.addContact(c11110001); // cache 11
		Collection<KademliaOverlayContact<HKademliaOverlayID>> rtContentsBefore;
		rtContentsBefore = routingTable.localLookup(new KademliaOverlayKey(
				"000000", config), 5);

		// mark once, expect routing table to remain the same
		routingTable.markUnresponsiveContact(c11000001.getOverlayID());
		assertEquals("Routing table contents has not been modified",
				rtContentsBefore, routingTable.localLookup(
						new KademliaOverlayKey("00000000", config), 5));
		// now reinsert, expect behaviour as if contact had never been marked
		routingTable.addContact(c11000001);

		// contact should be dropped having received staleConter marks
		for (int i = 1; i <= config.staleCounter + 1; i++) {
			routingTable.markUnresponsiveContact(c11000001.getOverlayID());
			if (i < config.staleCounter) {
				assertEquals("Routing table contents has not been modified",
						rtContentsBefore, routingTable.localLookup(
								new KademliaOverlayKey("00000000", config), 5));
			} else {
				Collection<KademliaOverlayContact<HKademliaOverlayID>> rtContentsAfter;
				rtContentsAfter = routingTable.localLookup(
						new KademliaOverlayKey("00000000", config), 5);
				assertEquals("Routing table contains 3 contacts", 3,
						rtContentsAfter.size());
				assertTrue("RT contains own contact 001001", rtContentsAfter
						.contains(ownContact));
				assertTrue("RT contains 11100001 (remained in RT)",
						rtContentsAfter.contains(c11100001));
				assertTrue("RT contains 11110001 (replacement from cache)",
						rtContentsAfter.contains(c11110001));
			}
		}
	}

	/**
	 * A contact to be marked that is contained in a full replacement cache is
	 * immediately dropped.
	 */
	@Test
	public void testMarkUnresponsiveFullCache() {
		// add contacts into a bucket that cannot be split (two in cache)
		KademliaOverlayContact<HKademliaOverlayID> c01000000 = TestHelper
				.createContact("01000000", config);
		KademliaOverlayContact<HKademliaOverlayID> c01100000 = TestHelper
				.createContact("01100000", config);
		KademliaOverlayContact<HKademliaOverlayID> c01010000 = TestHelper
				.createContact("01010000", config);
		KademliaOverlayContact<HKademliaOverlayID> c01110000 = TestHelper
				.createContact("01110000", config);
		routingTable.addContact(c01000000); // bucket 01
		routingTable.addContact(c01100000); // bucket 01
		routingTable.addContact(c01010000); // cache 01
		routingTable.addContact(c01110000); // cache 01

		// mark one contact from cache -> should be dropped immediately
		routingTable.markUnresponsiveContact(c01010000.getOverlayID());

		// clear bucket so that other contact from cache is moved to bucket
		for (int i = 1; i <= config.staleCounter; i++) {
			routingTable.markUnresponsiveContact(c01000000.getOverlayID());
			routingTable.markUnresponsiveContact(c01100000.getOverlayID());
		}

		// look up 4 contacts and expect 3: own ID, contact moved from cache,
		// and second contact from bucket (not dropped because cache is empty)
		// --> if 01010000 had not been dropped from cache, would be in bucket
		// now
		Collection<KademliaOverlayContact<HKademliaOverlayID>> rtContentsAfter;
		rtContentsAfter = routingTable.localLookup(new KademliaOverlayKey(
				"00000000", config), 4);
		assertEquals("Routing table contains 3 contacts", 3, rtContentsAfter
				.size());
		assertTrue("Routing table contains own contact", rtContentsAfter
				.contains(ownContact));
		assertTrue(
				"Routing table contains contact that has been moved from cache to bucket",
				rtContentsAfter.contains(c01110000));
		assertTrue("Routing table contains second contact from bucket",
				rtContentsAfter.contains(c01100000));
	}

	/**
	 * A contact to be marked that is contained in a non-full replacement cache
	 * is dropped having obtained STALE_COUNTER marks. Part A: contact is not
	 * dropped if it has less than STALE_COUNTER marks.
	 */
	@Test
	public void testMarkUnresponsiveOneContactInCacheA() {
		// add contacts into a bucket that cannot be split (one in cache)
		KademliaOverlayContact<HKademliaOverlayID> c01000000 = TestHelper
				.createContact("01000000", config);
		KademliaOverlayContact<HKademliaOverlayID> c01100000 = TestHelper
				.createContact("01100000", config);
		KademliaOverlayContact<HKademliaOverlayID> c01010000 = TestHelper
				.createContact("01010000", config);
		routingTable.addContact(c01000000); // bucket 01
		routingTable.addContact(c01100000); // bucket 01
		routingTable.addContact(c01010000); // cache 01

		// mark one contact from cache -> should not be dropped
		routingTable.markUnresponsiveContact(c01010000.getOverlayID());

		// clear bucket so that contact from cache is moved to bucket
		for (int i = 1; i <= config.staleCounter; i++) {
			routingTable.markUnresponsiveContact(c01000000.getOverlayID());
			routingTable.markUnresponsiveContact(c01100000.getOverlayID());
		}

		// look up 4 contacts and expect 3: own ID, contact moved from cache,
		// and second contact from bucket (not dropped because cache is empty)
		Collection<KademliaOverlayContact<HKademliaOverlayID>> rtContentsAfter;
		rtContentsAfter = routingTable.localLookup(new KademliaOverlayKey(
				"00000000", config), 4);
		assertEquals("Routing table contains 3 contacts", 3, rtContentsAfter
				.size());
		assertTrue("Routing table contains own contact", rtContentsAfter
				.contains(ownContact));
		assertTrue(
				"Routing table contains contact that has been moved from cache to bucket "
						+ "(marked but not dropped)", rtContentsAfter
						.contains(c01010000));
		assertTrue("Routing table contains second contact from bucket",
				rtContentsAfter.contains(c01100000));
	}

	/**
	 * A contact to be marked that is contained in a non-full replacement cache
	 * is dropped having obtained STALE_COUNTER marks. Part B: contact is
	 * dropped if it has at least STALE_COUNTER marks.
	 */
	@Test
	public void testMarkUnresponsiveOneContactInCacheB() {
		// add contacts into a bucket that cannot be split (one in cache)
		HKademliaOverlayID i10000000 = new HKademliaOverlayID("10000000",
				config);
		KademliaOverlayContact<HKademliaOverlayID> c10000000 = TestHelper
				.createContact(i10000000, 1);
		HKademliaOverlayID i10100000 = new HKademliaOverlayID("10100000",
				config);
		KademliaOverlayContact<HKademliaOverlayID> c10100000 = TestHelper
				.createContact(i10100000, 2);
		HKademliaOverlayID i10010000 = new HKademliaOverlayID("10010000",
				config);
		KademliaOverlayContact<HKademliaOverlayID> c10010000 = TestHelper
				.createContact(i10010000, 3);
		routingTable.addContact(c10000000); // bucket 10
		routingTable.addContact(c10100000); // bucket 10
		routingTable.addContact(c10010000); // cache 10

		// repetitively mark one contact from cache -> should be dropped
		for (int i = 1; i <= config.staleCounter; i++) {
			routingTable.markUnresponsiveContact(i10010000);
		}

		// attempt to clear bucket (should "fail": cache expected to be empty)
		for (int i = 1; i <= config.staleCounter; i++) {
			routingTable.markUnresponsiveContact(i10000000);
			routingTable.markUnresponsiveContact(i10100000);
		}

		// look up 4 contacts and expect 3: own ID and both old contacts from
		// the bucket
		Collection<KademliaOverlayContact<HKademliaOverlayID>> rtContentsAfter;
		rtContentsAfter = routingTable.localLookup(new KademliaOverlayKey(
				"00000000", config), 4);
		assertEquals("Routing table contains 3 contacts", 3, rtContentsAfter
				.size());
		assertTrue("Routing table contains own contact", rtContentsAfter
				.contains(ownContact));
		assertTrue("Routing table contains contact 10000000 from bucket",
				rtContentsAfter.contains(c10000000));
		assertTrue("Routing table contains contact 10100000 from bucket",
				rtContentsAfter.contains(c10100000));
	}

	/**
	 * Test proximity listener notification when adding contacts: is called if
	 * and only if a contact is added to the bucket, if contact is part of k
	 * closest nodes and is new.
	 */
	@Test
	public void testProximityListenerNotificationAdd() {
		// own ID is 00001001
		KademliaOverlayContact<HKademliaOverlayID> c01110000, c01100000, c01010000, c11111111;
		c01110000 = TestHelper.createContact("01110000", config);
		c01100000 = TestHelper.createContact("01100000", config);
		c01010000 = TestHelper.createContact("01010000", config);
		c11111111 = TestHelper.createContact("11111111", config);

		// register proximity listener
		ProximityListenerStub listener = new ProximityListenerStub();
		routingTable.registerProximityListener(listener);
		assertEquals("Until now, nothing has happened.", null,
				listener.newContact);

		// add second contact (first is own ID)
		routingTable.addContact(c01110000);
		assertEquals("Notified about new contact 01110000 (2nd contact)",
				c01110000, listener.newContact);
		listener.newContact = null;

		// add distant contact
		routingTable.addContact(c11111111);
		assertEquals("Not notified about distant contact 11111111", null,
				listener.newContact);
		listener.newContact = null;

		// add closer contact
		routingTable.addContact(c01100000);
		assertEquals("Notified about new contact 01100000 (closer contact)",
				c01100000, listener.newContact);
		listener.newContact = null;

		// add even closer contact, but in cache
		routingTable.addContact(c01010000);
		assertEquals("Not notified about closer contact 01010000 (cache)",
				null, listener.newContact);
		listener.newContact = null;

		// reinsert close contact
		routingTable.addContact(c01100000);
		assertEquals("Not notified again about reinserted contact 01100000",
				null, listener.newContact);
	}

	/**
	 * Test if a contact added to the bucket from the replacement cache is
	 * announced via the proximity listener.
	 */
	@Test
	public void testProximityListenerNotificationMarkUnresponsive() {
		// own ID is 00001001
		KademliaOverlayContact<HKademliaOverlayID> c11100000, c11000000, c11110000;
		c11110000 = TestHelper.createContact("11110000", config);
		c11100000 = TestHelper.createContact("11100000", config);
		c11000000 = TestHelper.createContact("11000000", config);
		routingTable.addContact(c11110000);
		routingTable.addContact(c11100000);
		routingTable.addContact(c11000000); // in cache
		ProximityListenerStub listener = new ProximityListenerStub();
		routingTable.registerProximityListener(listener);
		for (int i = 1; i <= config.staleCounter + 1; i++) {
			listener.newContact = null;
			routingTable.markUnresponsiveContact(new HKademliaOverlayID(
					"11110000", config));
			if (i < config.staleCounter) {
				assertEquals("Not notified yet (no change)", null,
						listener.newContact);
			} else if (i == config.staleCounter) {
				assertEquals("Notified about new contact (from cache)",
						c11000000, listener.newContact);
			} else {
				assertEquals("Not notified any more (no change)", null,
						listener.newContact);
			}
		}
	}

	/**
	 * Test stub class for routing table proximity listeners.
	 * 
	 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
	 */
	protected static class ProximityListenerStub implements
	ProximityListener<HKademliaOverlayID> {

		/** The contact of which this listener has been notified. */
		protected KademliaOverlayContact<HKademliaOverlayID> newContact = null;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void newCloseContactArrived(
				KademliaOverlayContact<HKademliaOverlayID> c) {
			this.newContact = c;
		}
	}
}
