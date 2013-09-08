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

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayContact;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayID;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayKey;
import org.peerfact.impl.overlay.dht.kademlia.base.routing.AddNodeVisitor;
import org.peerfact.impl.overlay.dht.kademlia.base.routing.PseudoRootNode;
import org.peerfact.impl.overlay.dht.kademlia.base.routing.RefreshNodeVisitor;
import org.peerfact.impl.overlay.dht.kademlia.base.routing.SetLastLookupTimeNodeVisitor;
import org.peerfact.impl.overlay.dht.kademlia2.setup.ConfigStub;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.util.helpers.TestHelper;


/**
 * Tests for RefreshNodeVisitor and SetLastLookupTimeNodeVisitor.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 */
public class RefreshAndSetLastLookupTimeNodeVisitorTest {

	private static ConfigStub config;

	private PseudoRootNode<KademliaOverlayID> pseudoRoot;

	/**
	 * Configure necessary simulator-wide variables
	 */
	@BeforeClass
	public static void beforeClass() {
		// Create dummy scheduler
		TestHelper.initSimulator();
		config = new ConfigStub();
		config.bucketSize = 1;
		config.routingTreeOrder = 2;
		config.idLength = 6;
		config.replacementCacheSize = 0;
	}

	/**
	 * Setup a small routing tree (with two branching nodes and 7 leaf nodes).
	 */
	@Before
	public void setUp() {
		KademliaOverlayID own010100 = new KademliaOverlayID(20, config);
		KademliaOverlayContact<KademliaOverlayID> c010101 = TestHelper
				.createContact(new KademliaOverlayID(21, config), 21);
		KademliaOverlayContact<KademliaOverlayID> c100101 = TestHelper
				.createContact(new KademliaOverlayID(37, config), 37);
		KademliaOverlayContact<KademliaOverlayID> c010010 = TestHelper
				.createContact(new KademliaOverlayID(18, config), 18);

		// last lookup time is zero for all buckets (by default)

		pseudoRoot = new PseudoRootNode<KademliaOverlayID>(own010100, config);
		AddNodeVisitor<KademliaOverlayID> addVis = AddNodeVisitor
				.getAddNodeVisitor(c010101, null, null, config);
		pseudoRoot.accept(addVis);
		addVis = AddNodeVisitor.getAddNodeVisitor(c100101, null, null, config);
		pseudoRoot.accept(addVis);
		addVis = AddNodeVisitor.getAddNodeVisitor(c010010, null, null, config);
		pseudoRoot.accept(addVis);
	}

	/**
	 * Test the RefreshNodeVisitor. Makes a strong assumption about the
	 * implementation, specifically that the IDs created for each bucket that
	 * needs to be refreshed are created by appending zeros to the bucket's
	 * prefix.
	 */
	@Test
	public void testRefresh() throws Exception {
		Map<KademliaOverlayKey, Integer> result = new LinkedHashMap<KademliaOverlayKey, Integer>();
		RefreshNodeVisitor<KademliaOverlayID> refVis = RefreshNodeVisitor
				.getRefreshNodeVisitor(0, result, config);
		pseudoRoot.accept(refVis);

		// initially:
		assertEquals("All buckets have to be refreshed", 7, result.size());
		assertEquals("000000 has to be refreshed (level 1)",
				Integer.valueOf(1),
				result
				.get(new KademliaOverlayKey(0, config)));
		assertEquals("010000 has to be refreshed (level 2)", Integer.valueOf(2),
				result
				.get(new KademliaOverlayKey(16, config)));
		assertEquals("010100 has to be refreshed (level 2)", Integer.valueOf(2),
				result
				.get(new KademliaOverlayKey(20, config)));
		assertEquals("011000 has to be refreshed (level 2)", Integer.valueOf(2),
				result
				.get(new KademliaOverlayKey(24, config)));
		assertEquals("011100 has to be refreshed (level 2)", Integer.valueOf(2),
				result
				.get(new KademliaOverlayKey(28, config)));
		assertEquals("100000 has to be refreshed (level 1)", Integer.valueOf(1),
				result
				.get(new KademliaOverlayKey(32, config)));
		assertEquals("110000 has to be refreshed (level 1)", Integer.valueOf(1),
				result
				.get(new KademliaOverlayKey(48, config)));
	}

	/**
	 * Test SetLastLookupTimeNodeVisitor (and RefreshNodeVisitor).
	 */
	@Test
	public void testSetLastLookupTimeNodeVisitor() throws Exception {
		Map<KademliaOverlayKey, Integer> result = new LinkedHashMap<KademliaOverlayKey, Integer>();

		// some buckets have been refreshed
		SetLastLookupTimeNodeVisitor<KademliaOverlayID> setVis = SetLastLookupTimeNodeVisitor
				.getSetLastLookupTimeNodeVisitor(new KademliaOverlayKey(21,
						config), config.refreshInterval + 10);
		pseudoRoot.accept(setVis);
		setVis = SetLastLookupTimeNodeVisitor
				.getSetLastLookupTimeNodeVisitor(new KademliaOverlayKey(37,
						config), config.refreshInterval + 10);
		pseudoRoot.accept(setVis);
		setVis = SetLastLookupTimeNodeVisitor
				.getSetLastLookupTimeNodeVisitor(new KademliaOverlayKey(18,
						config), config.refreshInterval + 10);
		pseudoRoot.accept(setVis);

		// shortly later (write code as in application):
		TestHelper.setSimulationTime(config.refreshInterval + 20);
		long notRefreshedAfter = Simulator.getCurrentTime()
				- config.refreshInterval;

		RefreshNodeVisitor<KademliaOverlayID> refVis = RefreshNodeVisitor
				.getRefreshNodeVisitor(notRefreshedAfter, result, config);
		pseudoRoot.accept(refVis);

		assertTrue("000000 has to be refreshed", result
				.containsKey(new KademliaOverlayKey(0, config)));
		assertFalse("010000 has been refreshed", result
				.containsKey(new KademliaOverlayKey(16, config)));
		assertFalse("010100 has been refreshed", result
				.containsKey(new KademliaOverlayKey(20, config)));
		assertTrue("011000 has to be refreshed", result
				.containsKey(new KademliaOverlayKey(24, config)));
		assertTrue("011100 has to be refreshed", result
				.containsKey(new KademliaOverlayKey(28, config)));
		assertFalse("100000 has been refreshed", result
				.containsKey(new KademliaOverlayKey(32, config)));
		assertTrue("110000 has to be refreshed", result
				.containsKey(new KademliaOverlayKey(48, config)));
		assertEquals("Exactly 4 buckets have to be refreshed", 4, result.size());

		// "force" to refresh all buckets (written as in application):
		result.clear();
		notRefreshedAfter = Simulator.getCurrentTime();
		refVis = RefreshNodeVisitor.getRefreshNodeVisitor(notRefreshedAfter,
				result, config);
		pseudoRoot.accept(refVis);
		assertEquals("All 7 buckets have to be refreshed", 7, result.size());
	}

}
