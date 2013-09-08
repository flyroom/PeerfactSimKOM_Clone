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

package org.peerfact.impl.overlay.dht.kademlia2.types;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;

import org.junit.BeforeClass;
import org.junit.Test;
import org.peerfact.impl.overlay.dht.kademlia.hkademlia.components.HKademliaOverlayID;
import org.peerfact.impl.overlay.dht.kademlia2.setup.ConfigStub;


/**
 * Tests for HKademliaOverlayID.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 */
public class HKademliaOverlayIDTest {

	static ConfigStub config;

	/**
	 * Initialise environment.
	 */
	@BeforeClass
	public static void setupBeforeClass() {
		config = new ConfigStub();
		config.idLength = 8;
		config.hierarchyTreeOrder = 2;
		config.hierarchyDepth = 2;
	}

	/**
	 * Test method for
	 * {@link org.peerfact.impl.overlay.dht.kademlia.hkademlia.components.HKademliaOverlayID#getCommonClusterDepth(org.peerfact.impl.overlay.dht.kademlia.hkademlia.components.HKademliaOverlayID)}
	 * .
	 */
	@Test
	public void testGetCommonClusterDepth() {
		HKademliaOverlayID i01001100 = new HKademliaOverlayID(76, config);
		HKademliaOverlayID i01001011 = new HKademliaOverlayID(75, config);
		assertEquals("Deepest common cluster of 01001100 and 01001011 is 0", 0,
				i01001100.getCommonClusterDepth(i01001011));

		HKademliaOverlayID i01010111 = new HKademliaOverlayID(87, config);
		HKademliaOverlayID i11010110 = new HKademliaOverlayID(214, config);
		assertEquals("Deepest common cluster of 01010111 and 11010110 is 1", 1,
				i01010111.getCommonClusterDepth(i11010110));

		HKademliaOverlayID i11010111 = new HKademliaOverlayID(215, config);
		assertEquals("Deepest common cluster of 01010111 and 11010111 is 2", 2,
				i01010111.getCommonClusterDepth(i11010111));
	}

	/**
	 * Test for setCluster.
	 */
	@Test
	public void testSetCluster() {
		HKademliaOverlayID i01011100 = new HKademliaOverlayID(92, config);
		HKademliaOverlayID i01001011 = new HKademliaOverlayID(75, config);
		BigInteger b0100 = BigInteger.valueOf(4);
		BigInteger b100110 = BigInteger.valueOf(38);

		assertEquals("New ID should be 01010100 (including 0 before 100)",
				BigInteger.valueOf(84), i01011100.setCluster(b0100).getBigInt());

		assertEquals("New ID should be 01000110 (ignoring surplus bits)",
				BigInteger.valueOf(70), i01001011.setCluster(b100110)
				.getBigInt());
	}

	/**
	 * Test for getCluster().
	 */
	@Test
	public void testGetCluster() {
		HKademliaOverlayID i01011100 = new HKademliaOverlayID(92, config);
		HKademliaOverlayID i01001011 = new HKademliaOverlayID(75, config);

		config.hierarchyDepth = 3;
		config.hierarchyTreeOrder = 1;

		assertEquals(
				"Cluster suffix of 01011100 is 100 (hDepth=3, hTreeOrder=1)",
				new BigInteger("100", 2), i01011100.getCluster());
		assertEquals(
				"Cluster suffix of 01001011 is 011 (hDepth=3, hTreeOrder=1)",
				new BigInteger("011", 2), i01001011.getCluster());

		config.hierarchyDepth = 2;
		config.hierarchyTreeOrder = 2;

		assertEquals(
				"Cluster suffix of 01011100 is 1100 (hDepth=2, hTreeOrder=2)",
				new BigInteger("1100", 2), i01011100.getCluster());
		assertEquals(
				"Cluster suffix of 01001011 is 1011 (hDepth=2, hTreeOrder=2)",
				new BigInteger("1011", 2), i01001011.getCluster());
	}

}
