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

package org.peerfact.impl.overlay.dht.centralized;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.peerfact.api.overlay.OverlayKey;
import org.peerfact.impl.common.DefaultHost;
import org.peerfact.impl.overlay.dht.centralizedstorage.components.CSClientNode;
import org.peerfact.impl.overlay.dht.centralizedstorage.components.CSClientServerFactory;
import org.peerfact.impl.overlay.dht.centralizedstorage.components.CSOverlayID;
import org.peerfact.impl.overlay.dht.centralizedstorage.components.CSOverlayKey;
import org.peerfact.impl.overlay.dht.centralizedstorage.components.CSServerNode;
import org.peerfact.impl.overlay.dht.centralizedstorage.messages.StoreResultMsg;
import org.peerfact.impl.overlay.dht.centralizedstorage.operations.LookupOperation;
import org.peerfact.util.ComponentTest;


/**
 * The concrete subclass should select an implementation of the email system
 * through decision which <code>Server</code> and <code>Client</code> to use.
 * 
 * @author Konstantin Pussep
 * 
 */
public class CentralizedTest extends ComponentTest {

	CSClientNode client1, client2, client3;

	CSServerNode server;

	private CSClientServerFactory factory = new CSClientServerFactory();

	private CSOverlayKey key1 = new CSOverlayKey("first_doc");

	private CSOverlayKey key2 = new CSOverlayKey("second_doc");

	@Override
	@Before
	public void setUp() {
		super.setUp();

		this.server = createServer();
		CSOverlayID serverID = this.server.getServerID();
		assertNotNull(serverID);

		this.client1 = createClient(serverID);
		this.client2 = createClient(serverID);
		this.client3 = createClient(serverID);

		// pre-conditions
		assertTrue(this.server.isIndexEmpty());
	}

	/**
	 * Test join and leave of new clients
	 * 
	 * @throws Exception
	 */
	@Test
	public void testJoinClient() throws Exception {
		final int id1 = client1.join(getOperationCallback());
		assertFalse(processedOpIds.contains(id1));

		runSimulation(milliseconds(1000));

		assertTrue(processedOpIds.contains(id1));
	}

	/**
	 * Test client's leave operation.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testLeaveClient() throws Exception {

		final int id2 = client1.leave(getOperationCallback());
		assertFalse(processedOpIds.contains(id2));

		runSimulation(milliseconds(1000));

		assertTrue(processedOpIds.contains(id2));
	}

	/**
	 * Test store while the server is online.
	 * 
	 */
	@Test
	public void testStore() {
		SimpleDHTObject value1 = new SimpleDHTObject(key1, null);
		server.join(getOperationCallback());
		int id1 = this.client1.store(key1, value1, getOperationCallback());

		runSimulation(seconds(1000));

		assertEquals(value1, this.server.getDHTObject(key1));
		assertEquals(StoreResultMsg.STORE_SUCCEEDED, results.get(id1));
		assertTrue(failedOperations.isEmpty());
	}

	/**
	 * Test store operation without server being online - must fail.
	 * 
	 */
	@Test
	public void testStoreWithoutServer() {
		SimpleDHTObject value1 = new SimpleDHTObject(key1, null);// client1.getAddress()
		int id1 = this.client1.store(key1, value1, getOperationCallback());

		runSimulation(seconds(1000));
		assertTrue(processedOpIds.contains(id1));
		assertTrue(failedOperations.contains(id1));
		assertFalse(results.containsKey(id1));
	}

	/**
	 * Check lookup of stored values.
	 * 
	 */
	@Test
	public void testLookupExisting() {
		// pre-conditions
		assertTrue(server.isIndexEmpty());
		SimpleDHTObject value1 = new SimpleDHTObject(key2, null);
		server.join(getOperationCallback());
		server.updateIndex(key2, value1);
		assertTrue(server.containsIndexKey(key2));

		int id1 = client3.valueLookup(key2, getOperationCallback());

		runSimulation(seconds(1000));

		// check post-conditions
		assertEquals(value1, results.get(id1));
		assertTrue(failedOperations.isEmpty());
	}

	/**
	 * Test lookup for a non-existing value.
	 * 
	 */
	@Test
	public void testLookupMissing() {
		// pre-conditions
		assertTrue(server.isIndexEmpty());

		server.join(getOperationCallback());

		client3.valueLookup(key2, getOperationCallback());

		runSimulation(seconds(1000));

		// check post-conditions
		assertEquals(null, results.get(0));
		assertTrue(failedOperations.isEmpty());
	}

	/**
	 * Test sending of several store messages from several clients. Stored
	 * objects can overwrite each other.
	 * 
	 */
	@Test
	public void testManyStores() {

		SimpleDHTObject value1 = new SimpleDHTObject(key1, null);
		SimpleDHTObject value2 = new SimpleDHTObject(key2, null);
		SimpleDHTObject value3 = new SimpleDHTObject(key2, null);
		assertNotNull(value1.getKey());

		server.join(getOperationCallback());

		// perform stores
		client1.store(value1.getKey(), value1, getOperationCallback());
		client2.store(value2.getKey(), value2, getOperationCallback());
		client3.store(value3.getKey(), value3, getOperationCallback());

		runSimulation(seconds(1000));

		Map<OverlayKey<?>, SimpleDHTObject> expectedIndex = new LinkedHashMap<OverlayKey<?>, SimpleDHTObject>();
		expectedIndex.put(key1, value1);
		expectedIndex.put(key2, value3); // value3 will overwrite value2

		// check index
		assertEquals(expectedIndex.keySet(), this.server.listIndex().keySet());
		log.debug("Server stores " + this.server.listIndex());
	}

	/**
	 * Test sending and fetching of a single message.
	 * 
	 */
	@Test
	public void testStoreAndLookup() {
		SimpleDHTObject value1 = new SimpleDHTObject(key1, null);

		server.join(getOperationCallback());

		int idStore = this.client1.store(key1, value1, getOperationCallback());

		// now send lookup and continue
		LookupOperation lookup = new LookupOperation(client2, null, key1,
				getOperationCallback());
		lookup.scheduleAtTime(seconds(100));

		runSimulation(seconds(1000));

		// check store
		assertEquals(StoreResultMsg.STORE_SUCCEEDED, results.get(idStore));
		assertTrue(failedOperations.isEmpty());

		// check lookup result
		assertEquals(value1, results.get(lookup.getOperationID()));
		assertTrue(failedOperations.isEmpty());
	}

	private CSClientNode createClient(CSOverlayID serverID) {
		DefaultHost host = createEmptyHost();
		createHostProperties(host);
		createNetworkWrapper(host);
		createTransLayer(host);
		factory.setIsServer(false);
		CSClientNode client = (CSClientNode) factory.createComponent(host);
		host.setOverlayNode(client);
		return client;
	}

	private CSServerNode createServer() {
		DefaultHost host = createEmptyHost();
		createHostProperties(host);
		createNetworkWrapper(host);
		createTransLayer(host);
		factory.setIsServer(true);
		CSServerNode cSServerNode = (CSServerNode) factory
				.createComponent(host);
		host.setOverlayNode(cSServerNode);
		return cSServerNode;
	}

	@Override
	@After
	public void tearDown() {
		super.tearDown();
	}

}
