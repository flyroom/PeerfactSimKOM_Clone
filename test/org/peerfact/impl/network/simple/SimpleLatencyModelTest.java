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

package org.peerfact.impl.network.simple;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.peerfact.api.common.ConnectivityEvent;
import org.peerfact.api.common.ConnectivityListener;
import org.peerfact.api.common.Message;
import org.peerfact.api.network.Bandwidth;
import org.peerfact.api.network.NetLayer;
import org.peerfact.api.network.NetMessage;
import org.peerfact.api.network.NetMessageListener;
import org.peerfact.api.network.NetMsgEvent;
import org.peerfact.api.network.NetProtocol;
import org.peerfact.impl.network.simple.SimpleSubnet.LinkID;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.simengine.SimulatorTest;
import org.peerfact.impl.transport.UDPMessage;

public class SimpleLatencyModelTest extends SimulatorTest {

	// protected final static Logger log = SimLogger
	// .getLogger(SimpleLatencyModelTest.class);

	List<Message> receivedData;

	List<Boolean> onlineStates;

	SimpleNetLayer net1, net2, net3, net4, net5, net6, net7, net8;

	private SimpleSubnet subnet;

	private short port = 0;

	SimpleLatencyModel lm;

	@Override
	@Before
	public void setUp() {
		super.setUp();
		this.receivedData = new LinkedList<Message>();
		this.onlineStates = new LinkedList<Boolean>();
		this.lm = new SimpleLatencyModel();
		subnet = new SimpleSubnet();
		subnet.setLatencyModel(new SimpleLatencyModel());

		net1 = new SimpleNetLayer(subnet, new SimpleNetID(1),
				new SimpleEuclidianPoint(0d, 0d), new Bandwidth(20d, 20d));
		net2 = new SimpleNetLayer(subnet, new SimpleNetID(2),
				new SimpleEuclidianPoint(SimpleSubnet.SUBNET_HEIGHT / 2,
						SimpleSubnet.SUBNET_WIDTH / 2), new Bandwidth(20d, 20d));
		net3 = new SimpleNetLayer(subnet, new SimpleNetID(3),
				new SimpleEuclidianPoint(SimpleSubnet.SUBNET_HEIGHT, 0d),
				new Bandwidth(20d, 20d));
		net4 = new SimpleNetLayer(subnet, null, new SimpleEuclidianPoint(0, 0),
				new Bandwidth(20d, 0));
		net5 = new SimpleNetLayer(subnet, null, new SimpleEuclidianPoint(0, 1),
				new Bandwidth(0d, 0d));
		net6 = new SimpleNetLayer(subnet, null, new SimpleEuclidianPoint(1, 0),
				new Bandwidth(0d, 0d));
		net7 = new SimpleNetLayer(subnet, null, new SimpleEuclidianPoint(2, 2),
				new Bandwidth(0d, 0d));
		net8 = new SimpleNetLayer(subnet, null, new SimpleEuclidianPoint(1.2,
				0.7), new Bandwidth(10d, 5d));

		net1.addNetMsgListener(new TestMessageListener());
		net2.addNetMsgListener(new TestMessageListener());
		net3.addNetMsgListener(new TestMessageListener());

		net1.addConnectivityListener(new TestConnectivityListener());
		net2.addConnectivityListener(new TestConnectivityListener());
		net3.addConnectivityListener(new TestConnectivityListener());

	}

	@Test
	public void testSendToOnline() {
		assertTrue(net1.isOnline());
		assertTrue(net2.isOnline());
		Message m1 = new UDPMessage(new ZeroMsg(), port, port, 0, false, null,
				null);
		net1.send(m1, net2.getNetID(), NetProtocol.IPv4);
		assertTrue(receivedData.isEmpty());
		SimulatorTest.runSimulation(60 * Simulator.MINUTE_UNIT);
		assertEquals(m1, receivedData.get(0));
	}

	@Test
	public void testSendToOffline() {
		net3.goOffline();
		assertTrue(net3.isOffline());
		assertTrue(net1.isOnline());
		assertTrue(net2.isOnline());
		Message m1 = new UDPMessage(new ZeroMsg(), port, port, 0, false, null,
				null);
		net1.send(m1, net3.getNetID(), NetProtocol.IPv4);
		assertTrue(receivedData.isEmpty());
		SimulatorTest.runSimulation(60 * Simulator.MINUTE_UNIT);
		assertTrue(receivedData.isEmpty());
	}

	@Test
	public void testOrderedSend() {
		assertTrue(net2.isOnline());
		assertTrue(receivedData.isEmpty());

		Message m1 = new UDPMessage(new ZeroMsg(), port, port, 0, false, null,
				null);
		Message m2 = new UDPMessage(new ZeroMsg(), port, port, 0, false, null,
				null);
		Message m3 = new UDPMessage(new ZeroMsg(), port, port, 0, false, null,
				null);

		// send 3 messages
		NetMessage msg1 = new SimpleNetMessage(m1, net2.getNetID(), net1
				.getNetID(), NetProtocol.IPv4);
		subnet.scheduleReceiveEvent(msg1, net2, 10);
		assertEquals(10l, subnet.getLastArrivalTime(new LinkID(net1.getNetID(),
				net2.getNetID())));

		NetMessage msg2 = new SimpleNetMessage(m2, net2.getNetID(), net1
				.getNetID(), NetProtocol.IPv4);
		subnet.scheduleReceiveEvent(msg2, net2, 30);
		assertEquals(30l, subnet.getLastArrivalTime(new LinkID(net1.getNetID(),
				net2.getNetID())));

		// should arrive at timepoint 31 (so after msg2) as ordered delivery
		// expected
		NetMessage msg3 = new SimpleNetMessage(m3, net2.getNetID(), net1
				.getNetID(), NetProtocol.IPv4);
		subnet.scheduleReceiveEvent(msg3, net2, 20);
		assertEquals(30l + SimpleSubnet.inOrderOffset,
				subnet.getLastArrivalTime(new LinkID(net1.getNetID(), net2
						.getNetID())));

		SimulatorTest.runSimulation(60 * Simulator.MINUTE_UNIT);

		List<Message> expected = new LinkedList<Message>();
		expected.add(m1);
		expected.add(m2);
		expected.add(m3);
		assertEquals(expected, receivedData);
	}

	@Test
	public void testConnectivityListener() {
		net1.goOffline();
		net1.goOnline();
		net1.goOffline();

		List<Boolean> expected = new LinkedList<Boolean>();
		expected.add(false);
		expected.add(true);
		expected.add(false);
		assertEquals(expected, onlineStates);
	}

	@Test
	public void testLinks() {
		Set<LinkID> links = new LinkedHashSet<LinkID>();
		links.add(new LinkID(net1.getNetID(), net2.getNetID()));
		LinkID a = new LinkID(net1.getNetID(), net2.getNetID());
		assertTrue(links.contains(a));
		LinkID b = new LinkID(net1.getNetID(), net2.getNetID());
		assertEquals(a, b);
	}

	@Test
	public void testStaticDelayHashFunction() {
		double dist4to5 = SimpleLatencyModel.getDistance(net4, net5);
		double dist4to6 = SimpleLatencyModel.getDistance(net4, net6);
		double dist4to7 = SimpleLatencyModel.getDistance(net4, net7);
		double dist4to8 = SimpleLatencyModel.getDistance(net4, net8);

		double sd1 = lm.calcStaticDelay(net5, dist4to5);
		double sd2 = lm.calcStaticDelay(net5, dist4to5);
		assertEquals(sd1, sd2, 0.1);
		assertTrue(sd1 > 0);

		double sd3 = lm.calcStaticDelay(net6, dist4to6);
		double sd4 = lm.calcStaticDelay(net6, dist4to6);
		assertEquals(sd3, sd4, 0.1);
		assertTrue(sd3 > 0);

		double sd5 = lm.calcStaticDelay(net7, dist4to7);
		double sd6 = lm.calcStaticDelay(net7, dist4to7);
		assertEquals(sd5, sd6, 0.1);
		assertTrue(sd5 > 0);

		double sd7 = lm.calcStaticDelay(net8, dist4to8);
		double sd8 = lm.calcStaticDelay(net8, dist4to8);
		assertEquals(sd7, sd8, 0.1);
		assertTrue(sd7 > 0);
	}

	@Override
	@After
	public void tearDown() {
		super.tearDown();
		receivedData.clear();
	}

	class TestMessageListener implements NetMessageListener {
		@Override
		public void messageArrived(NetMsgEvent nme) {
			assertNotNull(nme);
			receivedData.add(nme.getPayload());
			log.debug("Received: " + nme.getPayload() + " @ "
					+ Simulator.getCurrentTime());
		}

	}

	class TestConnectivityListener implements ConnectivityListener {
		@Override
		public void connectivityChanged(ConnectivityEvent ce) {
			assertNotNull(ce);
			NetLayer net = (NetLayer) ce.getSource();
			onlineStates.add(ce.isOnline());
			log.debug("[" + net.getNetID() + "] Online: " + ce.isOnline());
		}
	}

	private static class ZeroMsg implements Message {
		/**
		 * 
		 */
		private static final long serialVersionUID = 6510529475989029049L;

		/**
		 * 
		 */

		@Override
		public Message getPayload() {
			return null;
		}

		@Override
		public long getSize() {
			return 0;
		}
	}

}
