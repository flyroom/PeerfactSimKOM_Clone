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

package org.peerfact.impl.overlay.dht.kademlia2.setup;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;

import org.junit.Before;
import org.junit.Test;
import org.peerfact.api.transport.TransLayer;
import org.peerfact.impl.overlay.dht.kademlia.base.KademliaSetup;
import org.peerfact.impl.overlay.dht.kademlia.base.components.Node;
import org.peerfact.impl.overlay.dht.kademlia.hkademlia.components.HAbstractNodeFactory;
import org.peerfact.impl.overlay.dht.kademlia.hkademlia.components.HKademliaOverlayID;


/**
 * Tests for HAbstractNodeFactory.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 */
public class HAbstractNodeFactoryTest {

	private HAbstractNodeFactory factory;

	/**
	 * Setup.
	 */
	@Before
	public void before() {
		ConfigStub conf = new ConfigStub();
		conf.clusterMappingFilePath = "test/kademlia2/setup/clusterMappingTestFile.properties";
		KademliaSetup.setConfig(conf);
		factory = new HAbstractNodeFactoryStub();
	}

	/**
	 * Test getClusterSuffix
	 */
	@Test
	public void testGetClusterSuffix() {
		assertEquals("Cluster suffix of 'Europe' is 0100", new BigInteger(
				"0100", 2), factory.getClusterSuffix("Europe"));
		assertEquals("Cluster suffix of 'North+America' is 1000",
				new BigInteger("1000", 2), factory
				.getClusterSuffix("North+America"));
		assertEquals("Cluster suffix of non-existing group identifier is 0",
				BigInteger.ZERO, factory.getClusterSuffix("does-not-exist"));
	}

	/**
	 * Test if HKademliaOverlayIDs are correctly constructed using the given
	 * data. (This is a general test that is not directly related to
	 * HAbstractNodeFactory.)
	 */
	@Test
	public void testConstructHKademliaOverlayID() {
		ConfigStub conf = new ConfigStub();
		conf.idLength = 8;
		conf.hierarchyDepth = 1;
		conf.hierarchyTreeOrder = 2;

		HKademliaOverlayID europe = new HKademliaOverlayID("10101010", conf)
		.setCluster(factory.getClusterSuffix("Europe"));
		assertEquals("europe-ID is 10101000", new BigInteger("10101000", 2),
				europe.getBigInt());

		conf.hierarchyDepth = 2;
		HKademliaOverlayID northAmerica = new HKademliaOverlayID("01010101",
				conf).setCluster(factory.getClusterSuffix("North+America"));
		assertEquals("northAmerica-ID is 01011000", new BigInteger("01011000",
				2), northAmerica.getBigInt());
	}

	/**
	 * Stub implementation of HAbstractNodeFactory for test purposes.
	 * 
	 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
	 */
	private static class HAbstractNodeFactoryStub extends HAbstractNodeFactory {

		@Override
		protected Node<HKademliaOverlayID> buildHierarchicalNode(
				HKademliaOverlayID id, short port, TransLayer msgMgr) {
			// empty
			return null;
		}

	}
}
