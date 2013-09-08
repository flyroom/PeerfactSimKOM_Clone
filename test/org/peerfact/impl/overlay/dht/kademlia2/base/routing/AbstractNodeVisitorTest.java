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
import static org.junit.Assert.fail;

import java.math.BigInteger;
import java.util.Set;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayContact;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayID;
import org.peerfact.impl.overlay.dht.kademlia.base.routing.AbstractNodeVisitor;
import org.peerfact.impl.overlay.dht.kademlia.base.routing.BranchNode;
import org.peerfact.impl.overlay.dht.kademlia.base.routing.LeafNode;
import org.peerfact.impl.overlay.dht.kademlia.base.routing.Node;
import org.peerfact.impl.overlay.dht.kademlia.base.routing.NodeVisitor;
import org.peerfact.impl.overlay.dht.kademlia.base.routing.PseudoRootNode;
import org.peerfact.impl.overlay.dht.kademlia2.setup.ConfigStub;

/**
 * Tests for AbstractNodeVisitor.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * 
 */
public class AbstractNodeVisitorTest {

	private ConfigStub config;

	private static NodeVisitor<KademliaOverlayID> visitor;

	private NodeStub node;

	/**
	 * Setup while loading class.
	 * 
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() {
		visitor = new AbstractNodeVisitorStub();
	}

	/**
	 * Setup before each test case.
	 * 
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() {
		config = new ConfigStub();
		node = new NodeStub();
	}

	/**
	 * Test method for
	 * {@link hkademlia.components.routingtable.AbstractNodeVisitor#visit(hkademlia.components.routingtable.Node)}
	 * .
	 */
	@Test
	public void testVisitNode() {
		try {
			visitor.visit(node);
			fail("Expected exception for unknown node type");
		} catch (UnsupportedOperationException ex) {
			// expected
		}
		assertFalse("Node should not have been visited", node.called);
	}

	/**
	 * Test method for
	 * {@link hkademlia.components.routingtable.AbstractNodeVisitor#visit(hkademlia.components.routingtable.PseudoRootNode)}
	 * .
	 */
	@Test
	public void testVisitPseudoRootNode() {
		PseudoRootNode<KademliaOverlayID> pseudoRoot = new PseudoRootNode<KademliaOverlayID>(
				null, config);
		pseudoRoot.registerNewChild(node);
		visitor.visit(pseudoRoot);
		assertTrue("Child node of pseudo root should have been visited",
				node.called);
	}

	/**
	 * Test node stub - used to accept visitors.
	 * 
	 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
	 */
	static class NodeStub implements Node<KademliaOverlayID> {

		public boolean called = false;

		@Override
		public void accept(NodeVisitor<KademliaOverlayID> nodeVisitor) {
			called = true;
		}

		@Override
		public int getLevel() {
			return 0; // not used
		}

		@Override
		public KademliaOverlayID getOwnID() {
			return null; // not used
		}

		@Override
		public BigInteger getPrefix() {
			return null; // not used
		}

		@Override
		public Set<KademliaOverlayContact<KademliaOverlayID>> getAllSubContacts() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	/**
	 * Stub of AbstractNodeVisitor to test the implemented methods.
	 * 
	 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
	 */
	static class AbstractNodeVisitorStub extends
	AbstractNodeVisitor<KademliaOverlayID> {

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
