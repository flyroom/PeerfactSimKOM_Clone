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

package org.peerfact.impl.overlay.dht.kademlia2.components;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.peerfact.api.overlay.dht.DHTObject;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaIndexer;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayID;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayKey;
import org.peerfact.impl.overlay.dht.kademlia2.setup.ConfigStub;
import org.peerfact.util.helpers.TestHelper;

/**
 * Tests for KademliaIndexer.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 */
public class KademliaIndexerTest {

	private ConfigStub conf;

	private KademliaIndexer<KademliaOverlayID> testObj;

	private static final DHTObject o1 = new DHTObject() {

		@Override
		public long getTransmissionSize() {
			// TODO Auto-generated method stub
			return 0;
		} // empty
	};

	private static final DHTObject o2 = new DHTObject() {

		@Override
		public long getTransmissionSize() {
			// TODO Auto-generated method stub
			return 0;
		} // empty
	};

	private static final DHTObject o3 = new DHTObject() {

		@Override
		public long getTransmissionSize() {
			// TODO Auto-generated method stub
			return 0;
		} // empty
	};

	private static final DHTObject o4 = new DHTObject() {

		@Override
		public long getTransmissionSize() {
			// TODO Auto-generated method stub
			return 0;
		} // empty
	};

	private KademliaOverlayKey k1;

	private KademliaOverlayKey k2;

	private KademliaOverlayKey k3;

	private KademliaOverlayKey k4;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		TestHelper.initSimulator();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		TestHelper.setSimulationTime(0);
		conf = new ConfigStub();
		testObj = new KademliaIndexer<KademliaOverlayID>(conf);
		k1 = new KademliaOverlayKey(1, conf);
		k2 = new KademliaOverlayKey(2, conf);
		k3 = new KademliaOverlayKey(3, conf);
		k4 = new KademliaOverlayKey(4, conf);
	}

	/**
	 * Test putInitialDataItem.
	 */
	@Test
	public void testPutInitialDataItem() throws Exception {
		conf.dataExpirationTime = 20;
		testObj.putInitialDataItem(k1, o1);
		assertEquals("k1/o1 not expired", o1, testObj.get(k1));
		TestHelper.setSimulationTime(15);
		assertEquals("k1/o1 still not expired", o1, testObj.get(k1));
		TestHelper.setSimulationTime(200);
		assertEquals("k1/o1 'never' expires", o1, testObj.get(k1));
	}

	/**
	 * Test putting items.
	 */
	@Test
	public void testPut() throws Exception {
		conf.dataExpirationTime = 20;
		testObj.put(k1, o1);
		assertEquals("k1/o1 not expired", o1, testObj.get(k1));
		TestHelper.setSimulationTime(15);
		testObj.put(k2, o2);
		assertEquals("k1/o1 still not expired", o1, testObj.get(k1));
		assertEquals("k2/o2 not expired", o2, testObj.get(k2));
		TestHelper.setSimulationTime(20);
		assertEquals("k1/o1 expired!", null, testObj.get(k1));
		assertEquals("k2/o2 still not expired", o2, testObj.get(k2));
		TestHelper.setSimulationTime(35);
		assertEquals("k1/o1 still expired!", null, testObj.get(k1));
		assertEquals("k2/o2 expired!", null, testObj.get(k2));
	}

	/**
	 * Test put with replacement/update of last republish.
	 */
	@Test
	public void testPutReplace() throws Exception {
		conf.dataExpirationTime = 20;
		testObj.put(k1, o1);
		assertEquals("k1/o1 not expired", o1, testObj.get(k1));
		TestHelper.setSimulationTime(15);
		assertEquals("k1/o1 still not expired", o1, testObj.get(k1));
		testObj.put(k1, o2);
		assertEquals("k1/o2 not updated (no replacement)", o1, testObj.get(k1));
		TestHelper.setSimulationTime(20);
		assertEquals("k1/o1 still not expired", o1, testObj.get(k1));
		TestHelper.setSimulationTime(35);
		assertEquals("k1/o1 expired!", null, testObj.get(k1));
	}

	/**
	 * Test getEntries()
	 */
	@Test
	public void testGetEntries() throws Exception {
		Map<KademliaOverlayKey, DHTObject> result;

		conf.dataExpirationTime = 20;
		testObj.put(k1, o1);
		TestHelper.setSimulationTime(10);
		testObj.put(k2, o2);
		TestHelper.setSimulationTime(15);
		testObj.put(k3, o3);
		TestHelper.setSimulationTime(20);
		testObj.put(k4, o4);

		result = testObj.getEntries();
		assertEquals("Contains 3 entries", 3, result.size());
		assertEquals("k2=o2", o2, result.get(k2));
		assertEquals("k3=o3", o3, result.get(k3));
		assertEquals("k4=o4", o4, result.get(k4));
	}

	/**
	 * Test getEntriesToRepublish()
	 */
	@Test
	public void testGetEntriesToRepublish() throws Exception {
		Map<KademliaOverlayKey, DHTObject> result;

		conf.dataExpirationTime = 20;
		conf.republishInterval = 10;
		testObj.put(k1, o1);
		TestHelper.setSimulationTime(10);
		testObj.put(k2, o2);
		TestHelper.setSimulationTime(15);
		testObj.put(k3, o3);
		TestHelper.setSimulationTime(20);
		testObj.putInitialDataItem(k4, o4);

		result = testObj.getEntriesToRepublish();
		assertEquals("Contains 2 entries (o1 expired, o3 fresh)", 2,
				result.size());
		assertEquals("k2=o2 needs republish", o2, result.get(k2));
		assertEquals("k4=o4 (never published)", o4, result.get(k4));

		TestHelper.setSimulationTime(31);
		result = testObj.getEntriesToRepublish();
		assertEquals("Contains 2 entries (o1, o2 expired)", 2, result.size());
		assertEquals("k3=o3 needs republish", o3, result.get(k3));
		assertEquals("k4=o4 (never published)", o4, result.get(k4));
	}
}
