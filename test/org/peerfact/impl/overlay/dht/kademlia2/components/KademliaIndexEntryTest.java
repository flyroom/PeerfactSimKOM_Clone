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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaIndexEntry;
import org.peerfact.impl.overlay.dht.kademlia2.setup.ConfigStub;
import org.peerfact.util.helpers.TestHelper;


/**
 * Tests for KademliaIndexEntry.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 */
public class KademliaIndexEntryTest {

	private ConfigStub conf;

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
		conf = new ConfigStub();
	}

	/**
	 * Test whether the initial republish time is current simulation time (at
	 * creation).
	 */
	@Test
	public void testInitialRepublishTime() throws Exception {
		TestHelper.setSimulationTime(666);
		KademliaIndexEntry testObj = new KademliaIndexEntry(null, conf);
		assertEquals("First 'last republish time' is -1 (not published)", -1l,
				testObj.getLastRepublish());
	}

	/**
	 * Test whether hasExpired calculates correctly.
	 */
	@Test
	public void testHasExpired() throws Exception {
		TestHelper.setSimulationTime(666);
		KademliaIndexEntry testObj = new KademliaIndexEntry(null, conf);
		conf.dataExpirationTime = 234;
		assertFalse("Initially not expired.", testObj.hasExpired());
		TestHelper.setSimulationTime(666 + 999);
		assertFalse("Still not expired -> never published.", testObj
				.hasExpired());
		testObj.updateLastRepublish();
		assertFalse("Not expired after republish.", testObj.hasExpired());
		TestHelper.setSimulationTime(666 + 999 + 200);
		assertFalse("Still not expired.", testObj.hasExpired());
		TestHelper.setSimulationTime(666 + 999 + 234);
		assertTrue("Item has now expired.", testObj.hasExpired());
		TestHelper.setSimulationTime(666 + 999 + 300);
		assertTrue("Clearly still expired.", testObj.hasExpired());
	}

	/**
	 * Test whether needsRepublish calculates correctly.
	 */
	@Test
	public void testNeedsRepublish() throws Exception {
		TestHelper.setSimulationTime(0);
		KademliaIndexEntry testObj = new KademliaIndexEntry(null, conf);
		conf.republishInterval = 239;
		assertTrue("Initially republish necessary", testObj.needsRepublish());
		TestHelper.setSimulationTime(230);
		assertTrue("Republish still necessary -> never published.", testObj
				.needsRepublish());
		testObj.updateLastRepublish();
		assertFalse("No republish necessary directly after republish.", testObj
				.needsRepublish());
		TestHelper.setSimulationTime(230 + 238);
		assertFalse("Still no republish necessary.", testObj.needsRepublish());
		TestHelper.setSimulationTime(230 + 239);
		assertTrue("Item needs republish.", testObj.needsRepublish());
		TestHelper.setSimulationTime(230 + 239 + 300);
		assertTrue("Clearly still republish necessary.", testObj
				.needsRepublish());
	}

}
