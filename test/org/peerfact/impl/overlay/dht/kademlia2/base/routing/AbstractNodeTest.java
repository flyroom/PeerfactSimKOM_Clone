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
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayContact;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayID;
import org.peerfact.impl.overlay.dht.kademlia.base.components.RoutingTableConfig;
import org.peerfact.impl.overlay.dht.kademlia.base.routing.AbstractNode;
import org.peerfact.impl.overlay.dht.kademlia.base.routing.NodeVisitor;
import org.peerfact.impl.overlay.dht.kademlia.base.routing.ParentNode;
import org.peerfact.impl.overlay.dht.kademlia2.setup.ConfigStub;



/**
 * Tests class AbstractRoutingTreeNode.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * 
 */
public class AbstractNodeTest {

	ConfigStub config;

	KademliaOverlayID zero;

	KademliaOverlayID two;

	KademliaOverlayID five;

	KademliaOverlayID twelve;

	KademliaOverlayID twentyfour;

	KademliaOverlayID fourtyfour;

	/* ID is practically irrelevant */
	ParentNodeStub<KademliaOverlayID> pseudoRootID2;

	ParentNodeStub<KademliaOverlayID> rootID2;

	ParentNodeStub<KademliaOverlayID> L1ID2;

	ParentNodeStub<KademliaOverlayID> L2ID2;

	ParentNodeStub<KademliaOverlayID> L3ID2;

	AbstractNodeStub root;

	AbstractNodeStub L1_01;

	AbstractNodeStub L2_0011;

	AbstractNodeStub L3_101100;

	/**
	 * Setup stubs etc.
	 */
	@Before
	public void setUp() {
		config = new ConfigStub();
		config.idLength = 6;
		config.routingTreeOrder = 2;

		zero = new KademliaOverlayID(0, config);
		two = new KademliaOverlayID(2, config);
		five = new KademliaOverlayID(5, config);
		twelve = new KademliaOverlayID(12, config);
		twentyfour = new KademliaOverlayID(24, config);
		fourtyfour = new KademliaOverlayID(44, config);

		pseudoRootID2 = new ParentNodeStub<KademliaOverlayID>(-1, two, null,
				BigInteger.ZERO);
		rootID2 = new ParentNodeStub<KademliaOverlayID>(0, two, null,
				BigInteger.ZERO);
		L1ID2 = new ParentNodeStub<KademliaOverlayID>(1, two, null,
				BigInteger.ZERO);
		L2ID2 = new ParentNodeStub<KademliaOverlayID>(2, two, null,
				BigInteger.ZERO);
		L3ID2 = new ParentNodeStub<KademliaOverlayID>(3, two, null,
				BigInteger.ZERO);

		root = new AbstractNodeStub(BigInteger.ZERO, pseudoRootID2, config);
		L1_01 = new AbstractNodeStub(BigInteger.ONE, rootID2, config);
		L2_0011 = new AbstractNodeStub(BigInteger.valueOf(3), L1ID2, config);
		L3_101100 = new AbstractNodeStub(BigInteger.valueOf(44), L2ID2, config);
	}

	/**
	 * Test AbstractRoutingTree's constructor (if it correctly registers with
	 * parent node) & getPrefix & getParent.
	 */
	@Test
	public void testAbstractRoutingTreeConstructor() {
		assertEquals("Prefix of root is empty/0", BigInteger.ZERO,
				root.getPrefix());
		assertEquals("Parent of root is pseudoRootID2", pseudoRootID2,
				root.getParent());
		assertEquals("Node root should have registered with pseudoRootID2",
				root, pseudoRootID2.child);

		assertEquals("Prefix of L1_01 is 01", BigInteger.ONE, L1_01.getPrefix());
		assertEquals("Parent of L1_01 is rootID2", rootID2, L1_01.getParent());
		assertEquals("Node L1_01 should have registered with rootID2", L1_01,
				rootID2.child);

		assertEquals("Prefix of L2_0011 is 0011", BigInteger.valueOf(3),
				L2_0011.getPrefix());
		assertEquals("Parent of L2_0011 is L1ID2", L1ID2, L2_0011.getParent());
		assertEquals("Node L2_0011 should have registered with L1ID2", L2_0011,
				L1ID2.child);

		assertEquals("Prefix of L3_101100 is 101100", BigInteger.valueOf(44),
				L3_101100.getPrefix());
		assertEquals("Parent of L3_101100 is L2ID2", L2ID2,
				L3_101100.getParent());
		assertEquals("Node L3_101100 should have registered with L2ID2",
				L3_101100, L2ID2.child);
	}

	/**
	 * Test getLevel.
	 */
	@Test
	public void testGetLevel() {
		assertEquals("root has level 0", 0, root.getLevel());
		assertEquals("L1_01 has level 1", 1, L1_01.getLevel());
		assertEquals("L2_0011 has level 2", 2, L2_0011.getLevel());
		assertEquals("L3_101100 has level 3", 3, L3_101100.getLevel());
	}

	/**
	 * Test getOwnID.
	 */
	@Test
	public void testGetOwnID() {
		assertEquals("root has ID of its parent node", two, root.getOwnID());
		assertEquals("L1_01 has ID of its parent node", two, L1_01.getOwnID());
		assertEquals("L2_0011 has ID of its parent node", two,
				L2_0011.getOwnID());
		assertEquals("L3_101100 has ID of its parent node", two,
				L3_101100.getOwnID());
	}

	/**
	 * Test whether isResponsibleFor calculates correct responsibilities.
	 */
	@Test
	public void testIsResponsibleFor() {
		assertTrue("Root node is responsible for ID 00 0000",
				root.isResponsibleFor(zero));
		assertTrue("Root node is responsible for ID 00 0101",
				root.isResponsibleFor(five));
		assertTrue("Root node is responsible for ID 01 1000",
				root.isResponsibleFor(twentyfour));
		assertTrue("Root node is responsible for ID 10 1100",
				root.isResponsibleFor(fourtyfour));

		assertFalse("L1_01 is not responsible for ID 00 0000",
				L1_01.isResponsibleFor(zero));
		assertFalse("L1_01 is not responsible for ID 00 0101",
				L1_01.isResponsibleFor(five));
		assertTrue("L1_01 is responsible for ID 01 1000",
				L1_01.isResponsibleFor(twentyfour));
		assertFalse("L1_01 is not responsible for ID 10 1100",
				L1_01.isResponsibleFor(fourtyfour));

		assertFalse("L2_0011 is not responsible for ID 00 0000",
				L2_0011.isResponsibleFor(zero));
		assertFalse("L2_0011 is not responsible for ID 00 0101",
				L2_0011.isResponsibleFor(five));
		assertTrue("L2_0011 is responsible for ID 00 1100",
				L2_0011.isResponsibleFor(twelve));
		assertFalse("L2_0011 is not responsible for ID 10 1100",
				L2_0011.isResponsibleFor(fourtyfour));

		assertFalse("L3_101100 is not responsible for ID 00 0000",
				L3_101100.isResponsibleFor(zero));
		assertFalse("L3_101100 is not responsible for ID 00 1100",
				L3_101100.isResponsibleFor(twelve));
		assertTrue("L3_101100 is responsible for ID 10 1100",
				L3_101100.isResponsibleFor(fourtyfour));
	}

	/**
	 * Stub test class for AbstractRoutingTreeNode.
	 * 
	 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
	 * 
	 */
	private static class AbstractNodeStub extends
	AbstractNode<KademliaOverlayID> {

		public AbstractNodeStub(BigInteger prefix,
				ParentNode<KademliaOverlayID> parent, RoutingTableConfig conf) {
			super(prefix, parent, conf, null);
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
