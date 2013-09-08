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

import java.math.BigInteger;
import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayContact;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayID;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayKey;
import org.peerfact.impl.overlay.dht.kademlia.base.components.RoutingTableEntry;
import org.peerfact.impl.overlay.dht.kademlia.base.routing.BranchNode;
import org.peerfact.impl.overlay.dht.kademlia.base.routing.GenericLookupNodeVisitor;
import org.peerfact.impl.overlay.dht.kademlia.base.routing.LeafNode;
import org.peerfact.impl.overlay.dht.kademlia.base.routing.NodeVisitor;
import org.peerfact.impl.overlay.dht.kademlia.base.routing.PseudoRootNode;
import org.peerfact.impl.overlay.dht.kademlia2.setup.ConfigStub;
import org.peerfact.impl.util.toolkits.Predicate;
import org.peerfact.util.helpers.TestHelper;



/**
 * Tests for GenericLookupNodeVisitor.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 */
public class GenericLookupNodeVisitorTest {

	private static ConfigStub config;

	private PseudoRootNode<KademliaOverlayID> pseudoRoot;

	private BranchNode<KademliaOverlayID> root;

	private LeafNode<KademliaOverlayID> child0, child1, child2, child3;

	private RoutingTableEntry<KademliaOverlayID> e000000, e000001, e000011,
	e101010, e111110, e111111;

	/**
	 * Initialise environment.
	 */
	@BeforeClass
	public static void setUpBeforeClass() {
		config = new ConfigStub();
		config.bucketSize = 2;
		config.routingTreeOrder = 2;
		config.idLength = 6;
		TestHelper.initSimulator();
	}

	/**
	 * Setup.
	 */
	@Before
	public void setUp() {
		pseudoRoot = new PseudoRootNode<KademliaOverlayID>(
				new KademliaOverlayID(13, config), config);
		root = new BranchNode<KademliaOverlayID>(BigInteger.ZERO, pseudoRoot,
				config);
		child0 = new LeafNode<KademliaOverlayID>(BigInteger.ZERO, root, config);
		child1 = new LeafNode<KademliaOverlayID>(BigInteger.ONE, root, config);
		child2 = new LeafNode<KademliaOverlayID>(BigInteger.valueOf(2), root,
				config);
		child3 = new LeafNode<KademliaOverlayID>(BigInteger.valueOf(3), root,
				config);
		e000000 = new RoutingTableEntry<KademliaOverlayID>(TestHelper
				.createContact(new KademliaOverlayID(0, config), 0));
		e000001 = new RoutingTableEntry<KademliaOverlayID>(TestHelper
				.createContact(new KademliaOverlayID(1, config), 1));
		e000011 = new RoutingTableEntry<KademliaOverlayID>(TestHelper
				.createContact(new KademliaOverlayID(3, config), 3));
		e101010 = new RoutingTableEntry<KademliaOverlayID>(TestHelper
				.createContact(new KademliaOverlayID(42, config), 42));
		e111110 = new RoutingTableEntry<KademliaOverlayID>(TestHelper
				.createContact(new KademliaOverlayID(62, config), 62));
		e111111 = new RoutingTableEntry<KademliaOverlayID>(TestHelper
				.createContact(new KademliaOverlayID(63, config), 63));

		child0.kBucket.put(new KademliaOverlayID(0, config), e000000);
		child0.getReplacementCache().put(new KademliaOverlayID(1, config),
				e000001);
		child0.kBucket.put(new KademliaOverlayID(3, config), e000011);
		child2.kBucket.put(new KademliaOverlayID(42, config), e101010);
		child3.kBucket.put(new KademliaOverlayID(62, config), e111110);
		child3.kBucket.put(new KademliaOverlayID(63, config), e111111);
	}

	/**
	 * Test method for
	 * {@link hkademlia.components.routingtable.GenericLookupNodeVisitor#visit(hkademlia.components.routingtable.BranchNode)}
	 * .
	 */
	@Test
	public void testVisitBranchNode() {
		// pick contacts from 3 buckets
		Set<KademliaOverlayContact<KademliaOverlayID>> res0 = new LinkedHashSet<KademliaOverlayContact<KademliaOverlayID>>();
		NodeVisitor<KademliaOverlayID> visitor = GenericLookupNodeVisitor
				.getGenericLookupNodeVisitor(
						new KademliaOverlayKey(16, config), 3, null, res0);
		root.accept(visitor);
		assertEquals("Result set should include three results", 3, res0.size());
		assertTrue("Result set should contain 000000", res0.contains(e000000
				.getContact()));
		assertTrue("Result set should contain 000011", res0.contains(e000011
				.getContact()));
		assertTrue("Result set should contain 111110", res0.contains(e111110
				.getContact()));

		// what happens if result set already full?
		NodeVisitor<KademliaOverlayID> visitor2 = GenericLookupNodeVisitor
				.getGenericLookupNodeVisitor(
						new KademliaOverlayKey(16, config), 1, null, res0);
		root.accept(visitor2);
		assertEquals("Result set should not have been altered", 3, res0.size());
		assertTrue("Result set should not have been altered", res0
				.contains(e000000.getContact()));
		assertTrue("Result set should not have been altered", res0
				.contains(e000011.getContact()));
		assertTrue("Result set should not have been altered", res0
				.contains(e111110.getContact()));

		// pick all (available!) contacts (add them to result set)
		NodeVisitor<KademliaOverlayID> visitor3 = GenericLookupNodeVisitor
				.getGenericLookupNodeVisitor(
						new KademliaOverlayKey(16, config), 99, null, res0);
		root.accept(visitor3);
		assertEquals("Result set should include five results", 5, res0.size());
		assertTrue("Result set should contain 000000", res0.contains(e000000
				.getContact()));
		assertTrue("Result set should contain 000011", res0.contains(e000011
				.getContact()));
		assertTrue("Result set should contain 111110", res0.contains(e111110
				.getContact()));
		assertTrue("Result set should contain 111111", res0.contains(e111111
				.getContact()));
		assertTrue("Result set should contain 101010", res0.contains(e101010
				.getContact()));
	}

	/**
	 * Test method for
	 * {@link hkademlia.components.routingtable.GenericLookupNodeVisitor#visit(hkademlia.components.routingtable.LeafNode)}
	 * .
	 * 
	 * Also tests default filter and order on contacts (from constructor).
	 */
	@Test
	public void testVisitLeafNode() {
		// no contacts from cache in result set
		Set<KademliaOverlayContact<KademliaOverlayID>> res0 = new LinkedHashSet<KademliaOverlayContact<KademliaOverlayID>>();
		NodeVisitor<KademliaOverlayID> visChild0 = GenericLookupNodeVisitor
				.getGenericLookupNodeVisitor(
						new KademliaOverlayKey(0, config), 3, null, res0);
		child0.accept(visChild0);
		assertEquals("Result set should contain 2 contacts", 2, res0.size());
		assertTrue("Result set should contain 000000", res0.contains(e000000
				.getContact()));
		assertTrue("Result set should contain 000011", res0.contains(e000011
				.getContact()));

		// at most numOfResults contacts in result set and default XOR metric
		Set<KademliaOverlayContact<KademliaOverlayID>> res1 = new LinkedHashSet<KademliaOverlayContact<KademliaOverlayID>>();
		NodeVisitor<KademliaOverlayID> visChild1 = GenericLookupNodeVisitor
				.getGenericLookupNodeVisitor(
						new KademliaOverlayKey(63, config), 1, null, res1);
		child3.accept(visChild1);
		assertEquals("Result set should contain one contact", 1, res1.size());
		assertTrue("Result set should contain 111111", res1.contains(e111111
				.getContact()));

		// filter out better result 000001
		Set<KademliaOverlayContact<KademliaOverlayID>> res2 = new LinkedHashSet<KademliaOverlayContact<KademliaOverlayID>>();
		NodeVisitor<KademliaOverlayID> visChild2 = GenericLookupNodeVisitor
				.getGenericLookupNodeVisitor(
						new KademliaOverlayKey(63, config), 2,
						new Predicate<RoutingTableEntry<KademliaOverlayID>>() {
							@Override
							public boolean isTrue(
									RoutingTableEntry<KademliaOverlayID> object) {
								return object.equals(e000000);
							}
						}, res2);
		child0.accept(visChild2);
		assertEquals("Result set should have size one", 1, res2.size());
		assertTrue("Result set should contain 000000", res2.contains(e000000
				.getContact()));

		// contact order (natural order instead of XOR distance)
		// TODO: currently not supported any more
		// Set<KademliaOverlayContact<KademliaOverlayID>> res3 = new
		// LinkedHashSet<KademliaOverlayContact<KademliaOverlayID>>();
		// NodeVisitor<KademliaOverlayID> visChild3 = new
		// GenericLookupNodeVisitor<KademliaOverlayID>(
		// new KademliaOverlayKey(63), 1,
		// new Comparator<KademliaOverlayContact<KademliaOverlayID>>() {
		// @Override
		// public int compare(
		// KademliaOverlayContact<KademliaOverlayID> o1,
		// KademliaOverlayContact<KademliaOverlayID> o2) {
		// return o1.getOverlayID().getBigInt().compareTo(
		// o2.getOverlayID().getBigInt());
		// }
		// }, null, res3);
		// child3.accept(visChild3);
		// assertEquals("Result set should have size one", 1, res3.size());
		// assertTrue("Result set should contain 111110",
		// res3.contains(C111110));

		// empty bucket leaves set unchanged
		Set<KademliaOverlayContact<KademliaOverlayID>> res4 = new LinkedHashSet<KademliaOverlayContact<KademliaOverlayID>>();
		res4.add(e000000.getContact());
		NodeVisitor<KademliaOverlayID> visChild4 = GenericLookupNodeVisitor
				.getGenericLookupNodeVisitor(
						new KademliaOverlayKey(63, config), 99, null, res4);
		child1.accept(visChild4);
		assertEquals("Result set should have size one", 1, res4.size());
		assertTrue("Result set should contain 000000", res4.contains(e000000
				.getContact()));
	}

}
