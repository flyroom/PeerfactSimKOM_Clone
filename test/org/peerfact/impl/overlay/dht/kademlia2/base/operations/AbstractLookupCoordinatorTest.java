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

package org.peerfact.impl.overlay.dht.kademlia2.base.operations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayContact;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayID;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayKey;
import org.peerfact.impl.overlay.dht.kademlia.base.components.Node;
import org.peerfact.impl.overlay.dht.kademlia.base.components.RoutingTable;
import org.peerfact.impl.overlay.dht.kademlia.base.operations.AbstractLookupCoordinator;
import org.peerfact.impl.overlay.dht.kademlia.base.operations.LookupCoordinatorClient;
import org.peerfact.impl.overlay.dht.kademlia.base.operations.OperationsConfig;
import org.peerfact.impl.overlay.dht.kademlia.base.operations.AbstractLookupCoordinator.ContactState;
import org.peerfact.impl.overlay.dht.kademlia2.setup.ConfigStub;
import org.peerfact.util.helpers.TestHelper;


/**
 * Tests for AbstractLookupCoordinator (common functionality and abstract
 * control flow).
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 */
public class AbstractLookupCoordinatorTest {

	static ConfigStub config;

	KademliaOverlayContact<KademliaOverlayID> C010000 = TestHelper
			.createContact(new KademliaOverlayID(16, config), 16);

	KademliaOverlayContact<KademliaOverlayID> C010011 = TestHelper
			.createContact(new KademliaOverlayID(19, config), 19);

	KademliaOverlayContact<KademliaOverlayID> C011100 = TestHelper
			.createContact(new KademliaOverlayID(28, config), 28);

	KademliaOverlayContact<KademliaOverlayID> C011101 = TestHelper
			.createContact(new KademliaOverlayID(29, config), 29);

	KademliaOverlayContact<KademliaOverlayID> C011111 = TestHelper
			.createContact(new KademliaOverlayID(31, config), 31);

	AbstractLookupCoordinatorStub<KademliaOverlayID> testObj;

	LookupCoordinatorClientStub<KademliaOverlayID> client;

	KademliaOverlayKey key;

	/**
	 * Configure necessary simulator-wide variables
	 */
	@BeforeClass
	public static void beforeClass() {
		// Create dummy scheduler
		TestHelper.initSimulator();
		config = new ConfigStub();
		config.idLength = 6;
		config.bucketSize = 3;
	}

	@Before
	public void before() {
		C010000 = TestHelper.createContact(new KademliaOverlayID(16, config),
				16);
		C010011 = TestHelper.createContact(new KademliaOverlayID(19, config),
				19);
		C011100 = TestHelper.createContact(new KademliaOverlayID(28, config),
				28);
		C011101 = TestHelper.createContact(new KademliaOverlayID(29, config),
				29);
		C011111 = TestHelper.createContact(new KademliaOverlayID(31, config),
				31);
		key = new KademliaOverlayKey(BigInteger.valueOf(16), config);
		testObj = new AbstractLookupCoordinatorStub<KademliaOverlayID>(key,
				config);
		client = new LookupCoordinatorClientStub<KademliaOverlayID>();
		testObj.client = client;
	}

	/**
	 * Test method for
	 * {@link org.peerfact.impl.overlay.dht.kademlia2.operations.lookup.AbstractLookupCoordinator#insertBestContacts(java.util.Collection)}
	 * .
	 */
	@Test
	public void testInsertBestContacts() {
		List<KademliaOverlayContact<KademliaOverlayID>> contacts = new ArrayList<KademliaOverlayContact<KademliaOverlayID>>();
		assertEquals("Initially no contacts", 0, testObj.getkClosestNodes()
				.keySet()
				.size());
		contacts.add(C011111);
		contacts.add(C011101);
		contacts.add(C011100);
		testObj.insertBestContacts(contacts);
		assertEquals("C011111 contained & state TO_QUERY",
				ContactState.TO_QUERY, testObj.getkClosestNodes().get(C011111));
		assertEquals("C011101 contained & state TO_QUERY",
				ContactState.TO_QUERY, testObj.getkClosestNodes().get(C011101));
		assertEquals("C011100 contained & state TO_QUERY",
				ContactState.TO_QUERY, testObj.getkClosestNodes().get(C011100));
		testObj.getkClosestNodes().put(C011100, ContactState.QUERIED);
		contacts.clear();
		contacts.add(C010011);
		testObj.insertBestContacts(contacts);
		assertFalse("C011111 not contained",
				testObj.getkClosestNodes().containsKey(C011111));
		assertEquals("C010011 contained & state TO_QUERY",
				ContactState.TO_QUERY, testObj.getkClosestNodes().get(C010011));
		assertEquals("C011101 contained & state TO_QUERY",
				ContactState.TO_QUERY, testObj.getkClosestNodes().get(C011101));
		assertEquals("C011100 contained & state still QUERIED",
				ContactState.QUERIED, testObj.getkClosestNodes().get(C011100));
	}

	/**
	 * Test method for
	 * {@link org.peerfact.impl.overlay.dht.kademlia2.operations.lookup.AbstractLookupCoordinator#getBestUnqueried()}
	 * .
	 */
	@Test
	public void testGetBestUnqueried() {
		testObj.getkClosestNodes().put(C010011, ContactState.TO_QUERY);
		testObj.getkClosestNodes().put(C011100, ContactState.TO_QUERY);
		assertEquals("best unqueried is 010011", C010011,
				testObj.getBestUnqueried());
		testObj.getkClosestNodes().put(C010011, ContactState.QUERIED);
		assertEquals("best unqueried is now 011100", C011100,
				testObj.getBestUnqueried());
	}

	/**
	 * TODO
	 * 
	 * Test (abstract) control flow of AbstractLookupCoordinator
	 */
	// @Test
	public void testControlFlow() {
		fail("Not yet implemented.");
	}

	/**
	 * Test the isFinished and isFinishedAppSpecific methods.
	 */
	@Test
	public void testIsFinishedAndAppSpecific() {
		testObj.getkClosestNodes().put(C010011, ContactState.TO_QUERY);
		assertFalse(
				"Initially, coordinator is not finished (if contact available)",
				testObj.isFinished());
		testObj.getkClosestNodes().clear();
		client.continuationNecessary = true;
		assertTrue(
				"transit=0 & no unqueried contacts --> isFinishedAppSpecific==true",
				testObj.isFinishedAppSpecific());
		client.transitCount = 4;
		client.continuationNecessary = true;
		assertFalse(
				"client not finished, transit > 0 --> isFinishedAppSpecific==false",
				testObj.isFinishedAppSpecific());
		client.continuationNecessary = false;
		assertTrue(
				"client finished, although transit > 0 --> isFinishedAppSpecific==true",
				testObj.isFinishedAppSpecific());
		assertTrue("client finished, transit > 0 --> isFinished==true as well",
				testObj.isFinished());
		client.continuationNecessary = true;
		assertTrue("client not finished any more, but still finished",
				testObj.isFinished());
	}

	public static class AbstractLookupCoordinatorStub<T extends KademliaOverlayID>
	extends AbstractLookupCoordinator<T> {

		public LookupCoordinatorClient<T> client = null;

		public RoutingTable<T> routingTable = null;

		public AbstractLookupCoordinatorStub(KademliaOverlayKey lookupKey,
				OperationsConfig conf) {
			super(lookupKey, conf);
		}

		@Override
		protected LookupCoordinatorClient<T> getClient() {
			return client;
		}

		@Override
		protected RoutingTable<T> getRoutingTable() {
			return routingTable;
		}

		@Override
		protected void init() {
			// No implementation here (not sure why) by Thim
		}

		@Override
		protected void proceed(int numOfMessages) {
			// No implementation here (not sure why) by Thim
		}

		@Override
		public Node<T> getNode() {
			return null;
		}

	}

	public static class LookupCoordinatorClientStub<T extends KademliaOverlayID>
	implements LookupCoordinatorClient<T> {

		public boolean coordinatorFinished = false;

		public int transitCount = 0;

		public boolean continuationNecessary = true;

		@Override
		public void coordinatorFinished() {
			coordinatorFinished = true;
		}

		@Override
		public int getTransitCount() {
			return transitCount;
		}

		@Override
		public boolean isContinuationNecessary() {
			return continuationNecessary;
		}

	}

}
