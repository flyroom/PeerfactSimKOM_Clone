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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigInteger;

import org.junit.Before;
import org.junit.Test;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayID;
import org.peerfact.impl.overlay.dht.kademlia.base.routing.AbstractNodeVisitor;
import org.peerfact.impl.overlay.dht.kademlia.base.routing.BranchNode;
import org.peerfact.impl.overlay.dht.kademlia.base.routing.LeafNode;
import org.peerfact.impl.overlay.dht.kademlia.base.routing.ParentNode;
import org.peerfact.impl.overlay.dht.kademlia2.setup.ConfigStub;


/**
 * Test LeafNode.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 */
public class LeafNodeTest {

	private ConfigStub config;

	private ParentNode<KademliaOverlayID> pseudoRoot;

	private LeafNode<KademliaOverlayID> root;

	private LeafNode<KademliaOverlayID> leaf;

	/**
	 * Setup.
	 */
	@Before
	public void setUp() {
		config = new ConfigStub();
		pseudoRoot = new ParentNodeStub<KademliaOverlayID>(-1, null, null,
				BigInteger.ZERO);
		root = new LeafNode<KademliaOverlayID>(pseudoRoot, config);
		leaf = new LeafNode<KademliaOverlayID>(BigInteger.ONE, pseudoRoot,
				config);
	}

	/**
	 * Test root-bucket constructor.
	 */
	@Test
	public void testLeafNodeParentNode() {
		assertEquals("Root bucket should have prefix zero", BigInteger.ZERO,
				root.getPrefix());
		assertEquals("Parent should be pseudoRoot", pseudoRoot, root
				.getParent());
		assertNotNull("Bucket map should have been initialised", root.kBucket);
		assertNotNull("Cache map should have been initialised",
				root.getReplacementCache());
	}

	/**
	 * Test more general constructor.
	 */
	@Test
	public void testLeafNodeBigIntegerParentNode() {
		assertEquals("Leaf bucket should have prefix one", BigInteger.ONE, leaf
				.getPrefix());
		assertEquals("Parent should be pseudoRoot", pseudoRoot, leaf
				.getParent());
		assertNotNull("Bucket map should have been initialised", leaf.kBucket);
		assertNotNull("Cache map should have been initialised",
				leaf.getReplacementCache());
	}

	/**
	 * Test accept.
	 */
	@Test
	public void testAccept() {
		VisitorStub visitor = new VisitorStub();
		leaf.accept(visitor);
		assertTrue("LeafNode-visit-method should have been called in visitor",
				visitor.called);
	}

	/**
	 * Visitor stub that permits to check whether the right visit method is
	 * called.
	 * 
	 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
	 */
	private static class VisitorStub extends
	AbstractNodeVisitor<KademliaOverlayID> {

		public boolean called = false;

		@Override
		public void visit(BranchNode<KademliaOverlayID> node) {
			fail("This method should not be called as there is no BranchNode involved here.");

		}

		@Override
		public void visit(LeafNode<KademliaOverlayID> node) {
			this.called = true;
		}
	}

}
