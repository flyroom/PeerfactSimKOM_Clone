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

import java.math.BigInteger;
import java.util.Comparator;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayContact;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayID;
import org.peerfact.impl.overlay.dht.kademlia.base.components.RoutingTableEntry;
import org.peerfact.impl.overlay.dht.kademlia.base.components.RoutingTableComparators.BigIntegerXORMaxComparator;
import org.peerfact.impl.overlay.dht.kademlia.base.components.RoutingTableComparators.RoutingTableEntryHierarchyComparator;
import org.peerfact.impl.overlay.dht.kademlia.base.components.RoutingTableComparators.StaleComparator;
import org.peerfact.impl.overlay.dht.kademlia.hkademlia.components.HKademliaOverlayID;
import org.peerfact.impl.overlay.dht.kademlia2.setup.ConfigStub;
import org.peerfact.util.helpers.TestHelper;



/**
 * Test cases for routing table comparators.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 */
public class RoutingTableComparatorsTest {

	ConfigStub config;

	BigInteger zero = BigInteger.ZERO;

	BigInteger one = BigInteger.ONE;

	BigInteger five = BigInteger.valueOf(5);

	BigInteger seven = BigInteger.valueOf(7);

	/**
	 * Configure simulator
	 */
	@BeforeClass
	public static void beforeClass() {
		// Create dummy scheduler
		TestHelper.initSimulator();
	}

	/**
	 * Configure necessary simulator-wide variables
	 */
	@Before
	public void before() {
		config = new ConfigStub();
		config.idLength = 6;
		config.hierarchyTreeOrder = 2;
		config.hierarchyDepth = 2;
	}

	/**
	 * Tests the class BigIntegerXORMaxComparator with reference 000.
	 */
	@Test
	public void testBigIntegerXORMaxComparatorRegular() {
		BigIntegerXORMaxComparator regular = new BigIntegerXORMaxComparator(
				zero);
		assertTrue("Comparator should work as usual, i.e. 0 < 1", regular
				.compare(zero, one) < 0);
		assertTrue("Comparator should work as usual, i.e. 5 >= 1", regular
				.compare(five, one) >= 0);
		assertTrue("Comparator should work as usual, i.e. 7 < 5", regular
				.compare(seven, five) > 0);
		assertTrue("Comparator should work as usual, i.e. 1 = 1", regular
				.compare(one, one) == 0);
		assertTrue("Comparator should work as usual, i.e. 0 <= 0", regular
				.compare(zero, zero) <= 0);
	}

	/**
	 * Tests the class BigIntegerXORMaxComparator with reference 111, hence
	 * numbers are negated before being compared.
	 */
	@Test
	public void testXORMaxComparatorNegated() {
		BigIntegerXORMaxComparator negated = new BigIntegerXORMaxComparator(
				seven);
		assertFalse("Comparator 0 < 1 should work as 7 < 6", negated.compare(
				zero, one) < 0);
		assertFalse("Comparator 5 >= 1 should work as 2 >= 6", negated.compare(
				five, one) >= 0);
		assertTrue("Comparator 7 < 5 should work as 0 < 2", negated.compare(
				seven, five) < 0);
		assertTrue("Comparator 1 = 1 should work as 6 = 6", negated.compare(
				one, one) == 0);
		assertTrue("Comparator 0 <= 0 should work as 7 <= 7", negated.compare(
				zero, zero) <= 0);
	}

	/**
	 * Tests the class BigIntegerXORMaxComparator with reference 001.
	 */
	@Test
	public void testXORMaxComparatorOne() {
		BigIntegerXORMaxComparator cOne = new BigIntegerXORMaxComparator(one);
		assertFalse("Comparator 0 < 1 should work as 1 < 0", cOne.compare(zero,
				one) < 0);
		assertTrue("Comparator 5 >= 1 should work as 4 >= 0", cOne.compare(
				five, one) >= 0);
		assertFalse("Comparator 7 < 5 should work as 6 < 4", cOne.compare(
				seven, five) < 0);
		assertTrue("Comparator 1 = 1 should work as 0 = 0", cOne.compare(one,
				one) == 0);
		assertTrue("Comparator 0 <= 0 should work as 1 <= 1", cOne.compare(
				zero, zero) <= 0);
	}

	/**
	 * Test HierarchyComparator.
	 */
	@Test
	public void testHierarchyComparator() {
		HKademliaOverlayID i110101 = new HKademliaOverlayID(53, config);
		HKademliaOverlayID i010101 = new HKademliaOverlayID(21, config);
		HKademliaOverlayID i010100 = new HKademliaOverlayID(20, config);
		HKademliaOverlayID i100101 = new HKademliaOverlayID(37, config);
		HKademliaOverlayID i001100 = new HKademliaOverlayID(12, config);
		HKademliaOverlayID i001000 = new HKademliaOverlayID(8, config);
		KademliaOverlayContact<HKademliaOverlayID> c0 = TestHelper
				.createContact(i110101, 0);
		KademliaOverlayContact<HKademliaOverlayID> c1 = TestHelper
				.createContact(i010101, 1);
		KademliaOverlayContact<HKademliaOverlayID> c2 = TestHelper
				.createContact(i010100, 2);
		KademliaOverlayContact<HKademliaOverlayID> c3 = TestHelper
				.createContact(i100101, 3);
		KademliaOverlayContact<HKademliaOverlayID> c4 = TestHelper
				.createContact(i001100, 4);
		KademliaOverlayContact<HKademliaOverlayID> c5 = TestHelper
				.createContact(i001000, 5);
		RoutingTableEntry<HKademliaOverlayID> e0 = new RoutingTableEntry<HKademliaOverlayID>(
				c0);
		RoutingTableEntry<HKademliaOverlayID> e1 = new RoutingTableEntry<HKademliaOverlayID>(
				c1);
		RoutingTableEntry<HKademliaOverlayID> e2 = new RoutingTableEntry<HKademliaOverlayID>(
				c2);
		RoutingTableEntry<HKademliaOverlayID> e3 = new RoutingTableEntry<HKademliaOverlayID>(
				c3);
		RoutingTableEntry<HKademliaOverlayID> e4 = new RoutingTableEntry<HKademliaOverlayID>(
				c4);
		RoutingTableEntry<HKademliaOverlayID> e5 = new RoutingTableEntry<HKademliaOverlayID>(
				c5);

		RoutingTableEntryHierarchyComparator<HKademliaOverlayID> testObj0 = new RoutingTableEntryHierarchyComparator<HKademliaOverlayID>(
				i110101);
		assertEquals("010101 is greater than 010100 (by one)", 1, testObj0
				.compare(e1, e2));
		assertEquals("010101 is equal to 100101", 0, testObj0.compare(e1, e3));
		assertEquals("001100 is smaller than 010100 (by one)", -1, testObj0
				.compare(e4, e2));
		assertEquals("001100 is equal to 001000", 0, testObj0.compare(e4, e5));

		RoutingTableEntryHierarchyComparator<HKademliaOverlayID> testObj1 = new RoutingTableEntryHierarchyComparator<HKademliaOverlayID>(
				i010100);
		assertEquals("001100 and 001000 are equal", 0, testObj1.compare(e4, e5));
		assertEquals("110101 and 010101 are equal", 0, testObj1.compare(e0, e1));
		assertEquals("010100 is greater than 001100 (by two)", 2, testObj1
				.compare(e2, e4));
	}

	/**
	 * Test for UnresponsiveComparator.
	 */
	@Test
	public void testStaleComparator() {
		config.staleCounter = 2;

		Comparator<RoutingTableEntry<?>> cmp = new StaleComparator();
		RoutingTableEntry<?> fresh, markedOnce, markedTwice, evenMoreMarks;
		fresh = new RoutingTableEntry<KademliaOverlayID>(null);
		markedOnce = new RoutingTableEntry<KademliaOverlayID>(null);
		markedOnce.increaseStaleCounter();
		markedTwice = new RoutingTableEntry<KademliaOverlayID>(null);
		markedTwice.increaseStaleCounter();
		markedTwice.increaseStaleCounter();
		evenMoreMarks = new RoutingTableEntry<KademliaOverlayID>(null);
		evenMoreMarks.increaseStaleCounter();
		evenMoreMarks.increaseStaleCounter();
		evenMoreMarks.increaseStaleCounter();

		assertTrue("Entries with no mark are equal",
				cmp.compare(fresh, fresh) == 0);
		assertTrue("Entries with no mark are greater than with two marks", cmp
				.compare(fresh, markedTwice) > 0);
		assertTrue("Entries with 2 marks are smaller than with no marks", cmp
				.compare(markedTwice, fresh) < 0);
		assertTrue("Entries with 1 mark are greater than with two marks", cmp
				.compare(markedOnce, markedTwice) > 0);
		assertTrue("Entries with 2 marks are smaller than with 1 mark", cmp
				.compare(markedTwice, markedOnce) < 0);
		assertTrue("Entry with 3 marks is smaller than with 2 marks", cmp
				.compare(evenMoreMarks, markedTwice) < 0);
		assertTrue("Entry with 2 marks is greater than with 3 marks", cmp
				.compare(markedTwice, evenMoreMarks) > 0);
	}

}
