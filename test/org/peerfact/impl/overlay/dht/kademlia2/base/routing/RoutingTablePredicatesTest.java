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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;
import org.peerfact.impl.overlay.dht.kademlia.base.components.RoutingTableEntry;
import org.peerfact.impl.overlay.dht.kademlia.base.components.RoutingTablePredicates.MinimumClusterDepthRestrictor;
import org.peerfact.impl.overlay.dht.kademlia.hkademlia.components.HKademliaOverlayID;
import org.peerfact.impl.overlay.dht.kademlia2.setup.ConfigStub;
import org.peerfact.util.helpers.TestHelper;



/**
 * Tests for Predicates used in routing tables.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 */
public class RoutingTablePredicatesTest {

	static ConfigStub config;

	/**
	 * Configure necessary simulator-wide variables
	 */
	@BeforeClass
	public static void beforeClass() {
		// Create dummy scheduler
		TestHelper.initSimulator();
		config = new ConfigStub();
		config.idLength = 6;
		config.hierarchyTreeOrder = 1;
		config.hierarchyDepth = 2;
	}

	/**
	 * Test HierarchyRestrictor.
	 */
	@Test
	public void testHierarchyRestrictor() {
		HKademliaOverlayID i010101 = new HKademliaOverlayID(21, config);
		RoutingTableEntry<HKademliaOverlayID> e010101 = new RoutingTableEntry<HKademliaOverlayID>(
				TestHelper.createContact(i010101, 21));
		HKademliaOverlayID i011101 = new HKademliaOverlayID(29, config);
		RoutingTableEntry<HKademliaOverlayID> e011101 = new RoutingTableEntry<HKademliaOverlayID>(
				TestHelper.createContact(i011101, 29));
		HKademliaOverlayID i110100 = new HKademliaOverlayID(52, config);
		RoutingTableEntry<HKademliaOverlayID> e110100 = new RoutingTableEntry<HKademliaOverlayID>(
				TestHelper.createContact(i110100, 52));
		HKademliaOverlayID i011111 = new HKademliaOverlayID(31, config);
		RoutingTableEntry<HKademliaOverlayID> e011111 = new RoutingTableEntry<HKademliaOverlayID>(
				TestHelper.createContact(i011111, 31));

		MinimumClusterDepthRestrictor<HKademliaOverlayID> r010101_2 = new MinimumClusterDepthRestrictor<HKademliaOverlayID>(
				i010101, 2);
		assertTrue("ref 010101 >= 2: 010101 valid", r010101_2.isTrue(e010101));
		assertTrue("ref 010101 >= 2: 011101 valid", r010101_2.isTrue(e011101));
		assertFalse("ref 010101 >= 2: 110100 invalid", r010101_2
				.isTrue(e110100));
		assertFalse("ref 010101 >= 2: 011111 invalid", r010101_2
				.isTrue(e011111));

		MinimumClusterDepthRestrictor<HKademliaOverlayID> r011101_1 = new MinimumClusterDepthRestrictor<HKademliaOverlayID>(
				i011101, 1);
		assertTrue("ref 011101 >= 1: 010101 valid", r011101_1.isTrue(e010101));
		assertTrue("ref 011101 >= 1: 011101 valid", r011101_1.isTrue(e011101));
		assertTrue("ref 011101 >= 1: 110100 valid", r011101_1.isTrue(e110100));
		assertFalse("ref 011101 >= 1: 011111 invalid", r011101_1
				.isTrue(e011111));

		MinimumClusterDepthRestrictor<HKademliaOverlayID> r011111_0 = new MinimumClusterDepthRestrictor<HKademliaOverlayID>(
				i011111, 0);
		assertTrue("ref 011111 >= 0: 010101 valid", r011111_0.isTrue(e010101));
		assertTrue("ref 011111 >= 0: 011101 valid", r011111_0.isTrue(e011101));
		assertTrue("ref 011111 >= 0: 110100 valid", r011111_0.isTrue(e110100));
		assertTrue("ref 011111 >= 0: 011111 valid", r011111_0.isTrue(e011111));
	}

}
