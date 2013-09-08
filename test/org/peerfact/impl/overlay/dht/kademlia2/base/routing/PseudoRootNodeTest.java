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
import java.util.Set;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayContact;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayID;
import org.peerfact.impl.overlay.dht.kademlia.base.components.RoutingTableConfig;
import org.peerfact.impl.overlay.dht.kademlia.base.routing.AbstractNode;
import org.peerfact.impl.overlay.dht.kademlia.base.routing.BranchNode;
import org.peerfact.impl.overlay.dht.kademlia.base.routing.LeafNode;
import org.peerfact.impl.overlay.dht.kademlia.base.routing.Node;
import org.peerfact.impl.overlay.dht.kademlia.base.routing.NodeVisitor;
import org.peerfact.impl.overlay.dht.kademlia.base.routing.ParentNode;
import org.peerfact.impl.overlay.dht.kademlia.base.routing.PseudoRootNode;
import org.peerfact.impl.overlay.dht.kademlia2.setup.ConfigStub;



/**
 * Test case for PseudoRootNode.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 */
public class PseudoRootNodeTest {

	private static ConfigStub config;

	private KademliaOverlayID id;

	private PseudoRootNode<KademliaOverlayID> testObj;

	/**
	 * Initialise environment.
	 */
	@BeforeClass
	public static void setUpBeforeClass() {
		config = new ConfigStub();
		config.idLength = 6;
	}

	/**
	 * Setup
	 */
	@Before
	public void setUp() {
		id = new KademliaOverlayID(43, config);
		testObj = new PseudoRootNode<KademliaOverlayID>(id, config);
	}

	/**
	 * Test method for
	 * {@link hkademlia.components.routingtable.PseudoRootNode#getOwnID()}.
	 */
	@Test
	public void testGetOwnID() {
		assertEquals("Own ID is 43", id, testObj.getOwnID());
	}

	/**
	 * Test method for
	 * {@link hkademlia.components.routingtable.PseudoRootNode#registerNewChild(hkademlia.components.routingtable.Node)}
	 * .
	 */
	@Test
	public void testRegisterNewChild() {
		// automatically registers itself as child to parent==testObj
		Node<KademliaOverlayID> child = new NodeStub(BigInteger.ZERO, testObj,
				config);
		assertEquals("Child should be registered as root", child,
				testObj.getRoot());
	}

	/**
	 * Test method for
	 * {@link hkademlia.components.routingtable.PseudoRootNode#getLevel()}.
	 */
	@Test
	public void testGetLevel() {
		assertEquals("Level of pseudo root is always -1", -1, testObj
				.getLevel());
	}

	/**
	 * Test method for
	 * {@link hkademlia.components.routingtable.PseudoRootNode#accept(hkademlia.components.routingtable.NodeVisitor)}
	 * .
	 */
	@Test
	public void testAccept() {
		VisitorStub visitor = new VisitorStub();
		testObj.accept(visitor);
		assertTrue(
				"Correct method, i.e. PseudoRootNode-accept method should be called",
				visitor.called);
	}

	/**
	 * Test method for
	 * {@link hkademlia.components.routingtable.PseudoRootNode#getPrefix()}.
	 */
	@Test
	public void testGetPrefix() {
		assertEquals("Prefix of pseudo root node is always zero",
				BigInteger.ZERO, testObj.getPrefix());
	}

	/**
	 * Node stub.
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

	/**
	 * Visitor stub that allows to verify that the correct method (the one for
	 * PseudoRootNode) has been called.
	 * 
	 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
	 */
	private static class VisitorStub implements NodeVisitor<KademliaOverlayID> {

		public boolean called = false;

		@Override
		public void visit(Node<KademliaOverlayID> node) {
			// not used
		}

		@Override
		public void visit(PseudoRootNode<KademliaOverlayID> node) {
			this.called = true;
		}

		@Override
		public void visit(BranchNode<KademliaOverlayID> node) {
			// not used
		}

		@Override
		public void visit(LeafNode<KademliaOverlayID> node) {
			// not used
		}
	}
}
