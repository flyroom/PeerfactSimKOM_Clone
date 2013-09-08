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

package org.peerfact.impl.overlay.dht.kademlia2.measurement.file;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;
import org.peerfact.impl.overlay.dht.kademlia.base.analyzer.util.DataLookupQuality;
import org.peerfact.impl.overlay.dht.kademlia.base.analyzer.util.NodeLookupQuality;
import org.peerfact.impl.overlay.dht.kademlia.base.analyzer.util.OpDelay;
import org.peerfact.impl.overlay.dht.kademlia.base.analyzer.util.OpTraffic;
import org.peerfact.impl.overlay.dht.kademlia.base.analyzer.util.OpDelay.OperationType;
import org.peerfact.impl.overlay.dht.kademlia.base.operations.AbstractKademliaOperation.OperationState;



/**
 * Tests for OpDelay, NodeLookupQuality, OperationTraffic.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 */
public class TypesTest {

	/**
	 * Test whether OpDelay parses Strings correctly.
	 */
	@Test
	public static void testOpDelay() {
		OpDelay testObj = OpDelay
				.fromString("33576 kademlia2.operations.lookup."
						+ "BucketLookupOperation$HierarchicalBucketLookupOperation "
						+ "ERROR 9.318951");
		assertEquals("Operation ID is 33576", 33576, testObj.getOperationID());
		assertEquals("Type is HBUCKET", OperationType.HBUCKET, testObj
				.getOperationType());
		assertEquals("State is ERROR", OperationState.ERROR, testObj
				.getOperationState());
		assertEquals("Delay is 9.318951", 9.318951d, testObj.getLatency(), 0.1);
	}

	/**
	 * Test whether NodeLookupQuality parses Strings correctly.
	 */
	@Test
	public static void testNodeLookupQuality() {
		NodeLookupQuality testObj = NodeLookupQuality
				.fromString("38138 10 20 0 20");
		assertEquals("Operation ID is 38138", 38138, testObj.getOperationID());
		assertEquals("10 perfect contacts", 10, testObj.getPerfectContacts());
		assertEquals("20 stale contacts", 20, testObj.getOfflineContacts());
		assertEquals("0 missed deeper contacts", 0, testObj
				.getMissedCloserContacts());
		assertEquals("20 contacts", 20, testObj.getSize());
	}

	/**
	 * Test whether DataLookupQuality parses Strings correctly.
	 */
	@Test
	public static void testDataLookupQuality() {
		final String succ = "true 0.032991 [4=5,] 42 35 20 13 11 11 true - - - ";
		final String succExp = "true 4.3E-3 [4=5,] 42 35 20 13 11 11 true - - - ";
		final String fail = "false 2.175604 [4=21,3=1,] 30 25 20 - - - - 0 2 20 ";
		final Map<Integer, Integer> depthExpected;
		depthExpected = new LinkedHashMap<Integer, Integer>();

		DataLookupQuality testObj = DataLookupQuality.fromString(succ);
		assertEquals("successful", true, testObj.successful);
		assertEquals("delay 0.032991", 0.032991, testObj.getLatency(), 0.1);
		depthExpected.put(4, 5);
		assertEquals("depth 4=5", depthExpected, testObj.getSentMessages());
		assertEquals("all data 42", 42, testObj.getAllData());
		assertEquals("online data 35", 35, testObj.getOnlineData());
		assertEquals("kCNData 20", 20, testObj.getkCNData());
		assertEquals("all closer 13", 13, testObj.getAllCloser());
		assertEquals("online closer 11", 11, testObj.getOnlineCloser());
		assertEquals("closer data 11", 11, testObj.getOnlineDataCloser());
		assertEquals("sender KCN true", true, testObj.isSenderKCN());

		testObj = DataLookupQuality.fromString(succExp);
		assertEquals("successful", true, testObj.successful);
		assertEquals("delay 4.3E-3", 4.3E-3, testObj.getLatency(), 0.1);
		depthExpected.put(4, 5);
		assertEquals("depth 4=5", depthExpected, testObj.getSentMessages());
		assertEquals("all data 42", 42, testObj.getAllData());
		assertEquals("online data 35", 35, testObj.getOnlineData());
		assertEquals("kCNData 20", 20, testObj.getkCNData());
		assertEquals("all closer 13", 13, testObj.getAllCloser());
		assertEquals("online closer 11", 11, testObj.getOnlineCloser());
		assertEquals("closer data 11", 11, testObj.getOnlineDataCloser());
		assertEquals("sender KCN true", true, testObj.isSenderKCN());

		testObj = DataLookupQuality.fromString(fail);
		assertEquals("not successful", false, testObj.successful);
		assertEquals("delay 2.175604", 2.175604, testObj.getLatency(), 0.1);
		depthExpected.clear();
		depthExpected.put(4, 21);
		depthExpected.put(3, 1);
		assertEquals("depth 4=21, 3=1", depthExpected, testObj.getSentMessages());
		assertEquals("all data 30", 30, testObj.getAllData());
		assertEquals("online data 25", 25, testObj.getOnlineData());
		assertEquals("kCNData 20", 20, testObj.getkCNData());
		assertEquals("perfect contacts 0", 0, testObj.getPerfectContacts());
		assertEquals("offline contacts 2", 2, testObj.getOfflineContacts());
		assertEquals("missed closer contacts 20", 20,
				testObj.getMissedCloserContacts());
	}

	/**
	 * Test whether OperationTraffic parses Strings correctly.
	 */
	@Test
	public static void testOperationTraffic() {
		OpTraffic testObj;
		Map<BigInteger, Integer> sentMsgs = new LinkedHashMap<BigInteger, Integer>();

		testObj = OpTraffic.fromString("33592 11 01=5, 10=26, 11=8, ");
		assertEquals("Operation ID is 33592", 33592, testObj.getOperationID());
		assertEquals("Own cluster is 11", new BigInteger("11", 2), testObj
				.getOwnCluster());
		sentMsgs.put(new BigInteger("01", 2), 5);
		sentMsgs.put(new BigInteger("10", 2), 26);
		sentMsgs.put(new BigInteger("11", 2), 8);
		assertEquals("Correct messages sent per cluster", sentMsgs, testObj
				.getSentMessagesPerCluster());

		testObj = OpTraffic.fromString("33589 00 ");
		assertEquals("Operation ID is 33589", 33589, testObj.getOperationID());
		assertEquals("Own cluster is 00", new BigInteger("00", 2), testObj
				.getOwnCluster());
		sentMsgs.clear();
		assertEquals("Correct messages sent per cluster", sentMsgs, testObj
				.getSentMessagesPerCluster());
	}

}
