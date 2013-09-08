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

import org.junit.BeforeClass;
import org.junit.Test;
import org.peerfact.impl.overlay.dht.kademlia.base.Config;
import org.peerfact.impl.overlay.dht.kademlia.base.FileConfig;
import org.peerfact.impl.overlay.dht.kademlia.base.StaticConfig;
import org.peerfact.impl.simengine.Simulator;


/**
 * Test cases for FileConfig.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 */
public class FileConfigTest {

	private static Config defaultValues;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		defaultValues = new StaticConfig();
	}

	/**
	 * Tests whether accurate default values are returned if the configuration
	 * file is empty (here: wrong path given). (The default values are taken
	 * from {@link StaticConfig}.)
	 */
	@Test
	public static void testDefaultValues() {
		Config testObj = new FileConfig("path-does-not-exist");
		assertEquals("Wrong default bucket size",
				defaultValues.getBucketSize(), testObj.getBucketSize());
		assertEquals("Wrong default data expiration time",
				defaultValues.getDataExpirationTime(),
				testObj.getDataExpirationTime());
		assertEquals("Wrong default data size", defaultValues.getDataSize(),
				testObj.getDataSize());
		assertEquals("Wrong default hierarchy depth",
				defaultValues.getHierarchyDepth(), testObj.getHierarchyDepth());
		assertEquals("Wrong default hierarchy tree order",
				defaultValues.getHierarchyTreeOrder(),
				defaultValues.getHierarchyTreeOrder());
		assertEquals("Wrong default ID length", defaultValues.getIDLength(),
				testObj.getIDLength());
		assertEquals("Wrong default lookup message timeout",
				defaultValues.getLookupMessageTimeout(),
				testObj.getLookupMessageTimeout());
		assertEquals("Wrong default lookup operation timeout",
				defaultValues.getLookupOperationTimeout(),
				testObj.getLookupOperationTimeout());
		assertEquals("Wrong default number of maximum concurrent lookups",
				defaultValues.getMaxConcurrentLookups(),
				testObj.getMaxConcurrentLookups());
		assertEquals("Wrong default number of data items",
				defaultValues.getNumberOfDataItems(),
				testObj.getNumberOfDataItems());
		assertEquals("Wrong default number of initial routing table contacts",
				defaultValues.getNumberOfInitialRoutingTableContacts(),
				testObj.getNumberOfInitialRoutingTableContacts());
		assertEquals("Wrong default number of peers",
				defaultValues.getNumberOfPeers(), testObj.getNumberOfPeers());
		assertEquals("Wrong default periodic lookup interval",
				defaultValues.getPeriodicLookupInterval(),
				testObj.getPeriodicLookupInterval());
		assertEquals("Wrong default refresh interval",
				defaultValues.getRefreshInterval(),
				testObj.getRefreshInterval());
		assertEquals("Wrong default replacement cache size",
				defaultValues.getReplacementCacheSize(),
				testObj.getReplacementCacheSize());
		assertEquals("Wrong default republish interval",
				defaultValues.getRepublishInterval(),
				testObj.getRepublishInterval());
		assertEquals("Wrong default routing tree order",
				defaultValues.getRoutingTreeOrder(),
				testObj.getRoutingTreeOrder());
		assertEquals("Wrong default stale counter value",
				defaultValues.getStaleCounter(), testObj.getStaleCounter());
	}

	/**
	 * Tests whether the values from the configuration file
	 * "completeConfigValues.properties" are correctly read. (The file is
	 * complete.)
	 */
	@Test
	public static void testFullConfigFile() {
		Config testObj = new FileConfig(
				"test/kademlia2/setup/completeConfigValues.properties");
		assertEquals("Wrong bucket size read", 1, testObj.getBucketSize());
		assertEquals("Wrong data expiration time read",
				(long) (3.2 * Simulator.HOUR_UNIT),
				testObj.getDataExpirationTime());
		assertEquals("Wrong data size read", 13, testObj.getDataSize());
		assertEquals("Wrong hierarchy depth read", 2,
				testObj.getHierarchyDepth());
		assertEquals("Wrong hierarchy tree order read", 3,
				testObj.getHierarchyTreeOrder());
		assertEquals("Wrong ID length read", 6, testObj.getIDLength());
		assertEquals("Wrong lookup message timeout read",
				1 * Simulator.SECOND_UNIT, testObj.getLookupMessageTimeout());
		assertEquals("Wrong lookup operation timeout read",
				(long) (4.5 * Simulator.SECOND_UNIT),
				testObj.getLookupOperationTimeout());
		assertEquals("Wrong number of maximum concurrent lookups read", 2,
				testObj.getMaxConcurrentLookups());
		assertEquals("Wrong number of data items read", 10,
				testObj.getNumberOfDataItems());
		assertEquals("Wrong number of initial routing table contacts read", 30,
				testObj.getNumberOfInitialRoutingTableContacts());
		assertEquals("Wrong number of peers read", 1000001,
				testObj.getNumberOfPeers());
		assertEquals("Wrong periodic lookup interval read",
				100 * Simulator.MINUTE_UNIT,
				testObj.getPeriodicLookupInterval());
		assertEquals("Wrong refresh interval read",
				(long) (1.2 * Simulator.HOUR_UNIT),
				testObj.getRefreshInterval());
		assertEquals("Wrong replacement cache size read", 12,
				testObj.getReplacementCacheSize());
		assertEquals("Wrong republish interval read",
				(long) (1.2 * Simulator.HOUR_UNIT),
				testObj.getRepublishInterval());
		assertEquals("Wrong routing tree order read", 3,
				testObj.getRoutingTreeOrder());
		assertEquals("Wrong stale counter value read", 0,
				testObj.getStaleCounter());
	}

	/**
	 * Tests whether the values that are defined in the configuration file
	 * "partialConfigValues.properties" are correctly read and the other values
	 * have accurate default settings.
	 */
	@Test
	public static void testMixedValues() {
		Config testObj = new FileConfig(
				"test/kademlia2/setup/partialConfigValues.properties");
		assertEquals("Wrong default bucket size",
				defaultValues.getBucketSize(), testObj.getBucketSize());
		assertEquals("Wrong data expiration time read",
				(long) (234.1 * Simulator.HOUR_UNIT),
				testObj.getDataExpirationTime());
		assertEquals("Wrong default data size", defaultValues.getDataSize(),
				testObj.getDataSize());
		assertEquals("Wrong default hierarchy depth",
				defaultValues.getHierarchyDepth(), testObj.getHierarchyDepth());
		assertEquals("Wrong hierarchy tree order read", 123,
				testObj.getHierarchyTreeOrder());
		assertEquals("Wrong default ID length", defaultValues.getIDLength(),
				testObj.getIDLength());
		assertEquals("Wrong default lookup message timeout",
				defaultValues.getLookupMessageTimeout(),
				testObj.getLookupMessageTimeout());
		assertEquals("Wrong lookup operation timeout read",
				(long) (23.126 * Simulator.SECOND_UNIT),
				testObj.getLookupOperationTimeout());
		assertEquals("Wrong number of maximum concurrent lookups read", 37,
				testObj.getMaxConcurrentLookups());
		assertEquals("Wrong default number of data items",
				defaultValues.getNumberOfDataItems(),
				testObj.getNumberOfDataItems());
		assertEquals("Wrong default number of initial routing table contacts",
				defaultValues.getNumberOfInitialRoutingTableContacts(),
				testObj.getNumberOfInitialRoutingTableContacts());
		assertEquals("Wrong default number of peers",
				defaultValues.getNumberOfPeers(), testObj.getNumberOfPeers());
		assertEquals("Wrong default periodic lookup interval",
				defaultValues.getPeriodicLookupInterval(),
				testObj.getPeriodicLookupInterval());
		assertEquals("Wrong refresh interval read",
				(long) (32.2 * Simulator.HOUR_UNIT),
				testObj.getRefreshInterval());
		assertEquals("Wrong replacement cache size read", 234,
				testObj.getReplacementCacheSize());
		assertEquals("Wrong republish interval read",
				(long) (34.1 * Simulator.HOUR_UNIT),
				testObj.getRepublishInterval());
		assertEquals("Wrong routing tree order read", 83,
				testObj.getRoutingTreeOrder());
		assertEquals("Wrong default stale counter value",
				defaultValues.getStaleCounter(), testObj.getStaleCounter());
	}

}
