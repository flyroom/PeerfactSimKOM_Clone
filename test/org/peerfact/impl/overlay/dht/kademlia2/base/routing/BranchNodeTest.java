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
import static org.junit.Assert.fail;

import java.math.BigInteger;
import java.util.Set;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayContact;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayID;
import org.peerfact.impl.overlay.dht.kademlia.base.components.RoutingTableConfig;
import org.peerfact.impl.overlay.dht.kademlia.base.routing.AbstractNode;
import org.peerfact.impl.overlay.dht.kademlia.base.routing.AbstractNodeVisitor;
import org.peerfact.impl.overlay.dht.kademlia.base.routing.BranchNode;
import org.peerfact.impl.overlay.dht.kademlia.base.routing.LeafNode;
import org.peerfact.impl.overlay.dht.kademlia.base.routing.Node;
import org.peerfact.impl.overlay.dht.kademlia.base.routing.NodeVisitor;
import org.peerfact.impl.overlay.dht.kademlia.base.routing.ParentNode;
import org.peerfact.impl.overlay.dht.kademlia2.setup.ConfigStub;
import org.peerfact.util.helpers.TestHelper;


/**
 * Tests for BranchNode.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 */
public class BranchNodeTest {

	private static ConfigStub config;

	private KademliaOverlayID I011101;

	private KademliaOverlayID I010110;

	private KademliaOverlayID I011001;

	private KademliaOverlayID I010001;

	private ParentNodeStub<KademliaOverlayID> parent;

	private BranchNode<KademliaOverlayID> testObj;

	private Node<KademliaOverlayID> child0;

	private Node<KademliaOverlayID> child1;

	private Node<KademliaOverlayID> child2;

	private Node<KademliaOverlayID> child3;

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
	 * Create objects.
	 */
	@Before
	public void setUp() {
		I011101 = new KademliaOverlayID(29, config);
		I010110 = new KademliaOverlayID(22, config);
		I011001 = new KademliaOverlayID(25, config);
		I010001 = new KademliaOverlayID(17, config);

		// prefix empty
		parent = new ParentNodeStub<KademliaOverlayID>(0,
				new KademliaOverlayID(0, config), null, BigInteger.ZERO);
		// prefix 01
		testObj = new BranchNode<KademliaOverlayID>(BigInteger.ONE, parent,
				config);
		// prefix 0100
		child0 = new NodeStub(BigInteger.valueOf(4), testObj, config);
		// prefix 0101
		child1 = new NodeStub(BigInteger.valueOf(5), testObj, config);
		// prefix 0110
		child2 = new NodeStub(BigInteger.valueOf(6), testObj, config);
		// prefix 0111
		child3 = new NodeStub(BigInteger.valueOf(7), testObj, config);
	}

	/**
	 * Test method for
	 * {@link hkademlia.components.routingtable.BranchNode#BranchNode(java.math.BigInteger, hkademlia.components.routingtable.ParentNode)}
	 * .
	 */
	// @Test
	public void testBranchNode() {
		// not really anything to test
	}

	/**
	 * Test method for
	 * {@link hkademlia.components.routingtable.BranchNode#accept(hkademlia.components.routingtable.NodeVisitor)}
	 * .
	 */
	@Test
	public void testAccept() {
		NodeVisitorStub visitor = new NodeVisitorStub();
		testObj.accept(visitor);
		assertTrue("BranchNode method in visitor should have been called.",
				visitor.called);
	}

	/**
	 * Test method for
	 * {@link hkademlia.components.routingtable.BranchNode#registerNewChild(hkademlia.components.routingtable.Node)}
	 * .
	 */
	@Test
	public void testRegisterNewChild() {
		// children have already registered themselves during setUp()
		assertEquals("First child should be child0", child0, testObj.children
				.get(BigInteger.ZERO));
		assertEquals("Second child should be child1", child1, testObj.children
				.get(BigInteger.valueOf(1)));
		assertEquals("Third child should be child2", child2, testObj.children
				.get(BigInteger.valueOf(2)));
		assertEquals("Fourth child should be child3", child3, testObj.children
				.get(BigInteger.valueOf(3)));
		// now replace one child
		Node<KademliaOverlayID> newChild = new NodeStub(BigInteger.valueOf(2),
				testObj, config);
		assertEquals("Third child should now be newChild", newChild,
				testObj.children.get(BigInteger.valueOf(2)));
	}

	/**
	 * Test method for
	 * {@link hkademlia.components.routingtable.BranchNode#getResponsibleChild(org.peerfact.impl.overlay.dht.kademlia2.types.KademliaOverlayID)}
	 * .
	 */
	@Test
	public void testGetResponsibleChild() {
		assertEquals("Fourth child is responsible for 001101", child3, testObj
				.getResponsibleChild(I011101.getBigInt()));
		assertEquals("Second child is responsible for 010110", child1, testObj
				.getResponsibleChild(I010110.getBigInt()));
		assertEquals("Third child is responsible for 011001", child2, testObj
				.getResponsibleChild(I011001.getBigInt()));
		assertEquals("First child is responsible for 010001", child0, testObj
				.getResponsibleChild(I010001.getBigInt()));
	}

	/**
	 * Test method for
	 * {@link hkademlia.components.routingtable.BranchNode#getDiscriminantIDBits(org.peerfact.impl.overlay.dht.kademlia2.types.KademliaOverlayID)}
	 * .
	 */
	@Test
	public void testGetDiscriminantIDBits() {
		// most of this has already been tested in BigIntegerHelpers.
		KademliaOverlayID ID011010 = new KademliaOverlayID(26, config);
		// dirty hack: set higher level implicitly via parent
		parent.level = -1;
		assertEquals("Discriminant bits of 011010 for b=2 at level 0 are 01",
				BigInteger.ONE, testObj.getDiscriminantIDBits(ID011010
						.getBigInt()));
		parent.level = 0;
		assertEquals("Discriminant bits of 011010 for b=2 at level 1 are 10",
				BigInteger.valueOf(2), testObj.getDiscriminantIDBits(ID011010
						.getBigInt()));
		parent.level = 1;
		assertEquals("Discriminant bits of 011010 for b=2 at level 2 are 10",
				BigInteger.valueOf(2), testObj.getDiscriminantIDBits(ID011010
						.getBigInt()));
	}

	/**
	 * Test method for
	 * {@link hkademlia.components.routingtable.AbstractNode#getDiscriminantPrefixBits(java.math.BigInteger)}
	 * .
	 */
	@Test
	public void testGetDiscriminantPrefixBits() {
		assertEquals(
				"Discriminant prefix bits of prefix 001101 are 01 (BTREE=2)",
				BigInteger.ONE, testObj.getDiscriminantPrefixBits(BigInteger
						.valueOf(13)));
		assertEquals(
				"Discriminant prefix bits of prefix 010110 are 10 (BTREE=2)",
				BigInteger.valueOf(2), testObj
				.getDiscriminantPrefixBits(BigInteger.valueOf(22)));
		// a little trick
		config.routingTreeOrder = 3;
		assertEquals(
				"Discriminant prefix bits of prefix 011111 are 111 (BTREE=3)",
				BigInteger.valueOf(7), testObj
				.getDiscriminantPrefixBits(BigInteger.valueOf(31)));
		// once more
		config.routingTreeOrder = 1;
		assertEquals("Discriminant prefix bit of prefix 010110 is 0 (BTREE=1)",
				BigInteger.ZERO, testObj.getDiscriminantPrefixBits(BigInteger
						.valueOf(22)));
		// and the last one
		config.routingTreeOrder = 6;
		assertEquals(
				"Discriminant prefix bits of prefix 010110 are 010110 (BTREE=6)",
				BigInteger.valueOf(22), testObj
				.getDiscriminantPrefixBits(BigInteger.valueOf(22)));
		// reset for next tests
		config.routingTreeOrder = 2;
	}

	/**
	 * Visitor stub that allows to verify that a BranchNode calls the correct
	 * method of the visitor.
	 * 
	 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
	 */
	private static class NodeVisitorStub extends
	AbstractNodeVisitor<KademliaOverlayID> {

		public boolean called = false;

		@Override
		public void visit(BranchNode<KademliaOverlayID> node) {
			called = true;
		}

		@Override
		public void visit(LeafNode<KademliaOverlayID> node) {
			// not used
			fail("This method should not be called here (no LeafNodes involved).");
		}
	}

	/**
	 * Node stub (just needs to have a prefix...).
	 * 
	 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
	 */
	private static class NodeStub extends AbstractNode<KademliaOverlayID> {

		public NodeStub(BigInteger prefix,
				ParentNode<KademliaOverlayID> parent, RoutingTableConfig conf) {
			super(prefix, parent, conf);
		}

		@Override
		public void accept(NodeVisitor<KademliaOverlayID> visitor) {
			// not used
		}

		@Override
		public Set<KademliaOverlayContact<KademliaOverlayID>> getAllSubContacts() {
			// TODO Auto-generated method stub
			return null;
		}
	}
}
