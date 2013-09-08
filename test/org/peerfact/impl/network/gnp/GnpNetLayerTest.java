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

package org.peerfact.impl.network.gnp;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.peerfact.api.common.Message;
import org.peerfact.api.network.Bandwidth;
import org.peerfact.api.network.NetMessageListener;
import org.peerfact.api.network.NetMsgEvent;
import org.peerfact.api.network.NetProtocol;
import org.peerfact.impl.network.IPv4Message;
import org.peerfact.impl.network.IPv4NetID;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.simengine.SimulatorTest;
import org.peerfact.impl.transport.TCPMessage;

public class GnpNetLayerTest extends SimulatorTest {

	GnpNetLayer s1, s2, r1, r2;

	List<Message> receivedData;

	LinkedHashMap<Message, Long> receivedTime;

	GnpSubnet subnet;

	TestLatencyModel latencyModel;

	AbstractGnpNetBandwidthManager bandwidthManager;

	@Override
	@Before
	public void setUp() {
		super.setUp();

		receivedData = new LinkedList<Message>();
		receivedTime = new LinkedHashMap<Message, Long>();

		subnet = new GnpSubnet();

		latencyModel = new TestLatencyModel();
		bandwidthManager = new GnpNetBandwidthManagerEvent();

		subnet.setBandwidthManager(bandwidthManager);
		subnet.setLatencyModel(latencyModel);
		subnet.setPbaPeriod(200 * Simulator.MILLISECOND_UNIT);

		s1 = new GnpNetLayer(subnet, new IPv4NetID(1L), null, null,
				new Bandwidth(1000, 100));
		s2 = new GnpNetLayer(subnet, new IPv4NetID(2L), null, null,
				new Bandwidth(1000, 100));
		r1 = new GnpNetLayer(subnet, new IPv4NetID(10L), null, null,
				new Bandwidth(1000, 100));
		r2 = new GnpNetLayer(subnet, new IPv4NetID(11L), null, null,
				new Bandwidth(1000, 100));

		s1.addNetMsgListener(new TestMessageListener());
		s2.addNetMsgListener(new TestMessageListener());
		r1.addNetMsgListener(new TestMessageListener());
		r2.addNetMsgListener(new TestMessageListener());
	}

	/**
	 * 1 Sender, 1 Receiver, no TCP throughput Limitation 100 bytes/up + 1000
	 * bytes/sec down 0 5 10 min msg1: |--------------------| 10 min at 100
	 * bytes / s
	 */
	@Test
	public void testSend_1Streams_1Sender_1Receiver() {
		TcpTestMessage msg1 = new TcpTestMessage(58400, -1); // + IP Header
		// 40*20 =
		// 60,000 Bytes
		assertFalse(receivedTime.containsKey(msg1));
		s1.send(msg1, r1.getNetID(), NetProtocol.IPv4); // 600 sec => 10 min
		SimulatorTest.runSimulation(60 * Simulator.MINUTE_UNIT);
		assertEquals(10.0, receivedTime.get(msg1)
				/ (double) Simulator.MINUTE_UNIT, 0.01);
	}

	/**
	 * 1 Sender, 1 Receiver, no TCP throughput Limitation 100 bytes/up + 1000
	 * bytes/sec down 0 5 10 15 20 min msg2: |------------------------------| 5
	 * min at 100 bytes / s + 10 min at 50 bytes / s msg3:
	 * |------------------------------| 10 min at 50 bytes / s + 5 min at 100
	 * bytes / s
	 */
	@Test
	public void testSend_2Streams_1Sender_1Receiver() {
		TcpTestMessage msg2 = new TcpTestMessage(58400, -1); // + IP Header
		// 40*20 =
		// 60,000 Bytes
		TcpTestMessage msg3 = new TcpTestMessage(58400, -1); // + IP Header
		// 40*20 =
		// 60,000 Bytes
		assertFalse(receivedTime.containsKey(msg2));
		assertFalse(receivedTime.containsKey(msg3));
		s1.send(msg2, r1.getNetID(), NetProtocol.IPv4); //
		s1.send(msg3, r1.getNetID(), NetProtocol.IPv4,
				5 * Simulator.MINUTE_UNIT); // => 20 min
		SimulatorTest.runSimulation(60 * Simulator.MINUTE_UNIT);
		assertEquals(15.0, receivedTime.get(msg2)
				/ (double) Simulator.MINUTE_UNIT, 0.01);
		assertEquals(20.0, receivedTime.get(msg3)
				/ (double) Simulator.MINUTE_UNIT, 0.01);
	}

	/**
	 * 1 Sender, 2 Receiver, no TCP throughput Limitation 100 bytes/up + 2 *
	 * 1000 bytes/sec down 0 5 10 15 20 min msg2:
	 * |------------------------------| 5 min at 100 bytes / s + 10 min at 50
	 * bytes / s msg3: |------------------------------| 10 min at 50 bytes / s +
	 * 5 min at 100 bytes / s
	 */
	@Test
	public void testSend_2Streams_1Sender_2Receiver() {
		TcpTestMessage msg2 = new TcpTestMessage(58400, -1); // + IP Header
		// 40*20 =
		// 60,000 Bytes
		TcpTestMessage msg3 = new TcpTestMessage(58400, -1); // + IP Header
		// 40*20 =
		// 60,000 Bytes
		assertFalse(receivedTime.containsKey(msg2));
		assertFalse(receivedTime.containsKey(msg2));
		s1.send(msg2, r1.getNetID(), NetProtocol.IPv4); //
		s1.send(msg3, r2.getNetID(), NetProtocol.IPv4,
				5 * Simulator.MINUTE_UNIT); // => 20 min
		SimulatorTest.runSimulation(60 * Simulator.MINUTE_UNIT);
		assertEquals(15.0, receivedTime.get(msg2)
				/ (double) Simulator.MINUTE_UNIT, 0.01);
		assertEquals(20.0, receivedTime.get(msg3)
				/ (double) Simulator.MINUTE_UNIT, 0.01);
	}

	/**
	 * 1 Sender, 2 Receiver, no TCP throughput Limitation 2 * 100 bytes/up +
	 * 1000 bytes/sec down: 0 5 10 15 msg2: |--------------------| 10 min at 100
	 * bytes / s msg3: |--------------------| 10 min at 100 bytes / s
	 */
	@Test
	public void testSend_2Streams_2Sender_1Receiver() {
		TcpTestMessage msg2 = new TcpTestMessage(58400, -1); // + IP Header
		// 40*20 =
		// 60,000 Bytes
		TcpTestMessage msg3 = new TcpTestMessage(58400, -1); // + IP Header
		// 40*20 =
		// 60,000 Bytes
		assertFalse(receivedTime.containsKey(msg2));
		assertFalse(receivedTime.containsKey(msg3));
		s1.send(msg2, r1.getNetID(), NetProtocol.IPv4);
		s2.send(msg3, r1.getNetID(), NetProtocol.IPv4,
				5 * Simulator.MINUTE_UNIT); // => 20 min
		SimulatorTest.runSimulation(60 * Simulator.MINUTE_UNIT);
		assertEquals(10.0, receivedTime.get(msg2)
				/ (double) Simulator.MINUTE_UNIT, 0.01);
		assertEquals(15.0, receivedTime.get(msg3)
				/ (double) Simulator.MINUTE_UNIT, 0.01);
	}

	/**
	 * 1 Sender, 1 Receiver, TCP throughput limitation to 10 bytes / s 100
	 * bytes/up + 1000 bytes/sec down 0 100 min msg1: |--------------------| 100
	 * min at 10 bytes / s
	 */
	@Test
	public void testSend_1Streams_1Sender_1Receiver_throughputLimitation() {
		latencyModel.setTcpThroughput(10);
		TcpTestMessage msg1 = new TcpTestMessage(58400, -1); // + IP Header
		// 40*20 =
		// 60,000 Bytes
		assertFalse(receivedTime.containsKey(msg1));
		s1.send(msg1, r1.getNetID(), NetProtocol.IPv4);
		SimulatorTest.runSimulation(180 * Simulator.MINUTE_UNIT);
		assertEquals(100.0, receivedTime.get(msg1)
				/ (double) Simulator.MINUTE_UNIT, 0.01);
	}

	/**
	 * 1 Sender, 1 Receiver, TCP throughput limitation to 10 bytes / s 100
	 * bytes/up + 1000 bytes/sec down 0 5 100 105 min msg2:
	 * |------------------------------| 100 min at 10 bytes / s msg3:
	 * |------------------------------| 100 min at 10 bytes / s
	 */
	@Test
	public void testSend_2Streams_1Sender_1Receiver_throughputLimitation() {
		latencyModel.setTcpThroughput(10);
		TcpTestMessage msg2 = new TcpTestMessage(58400, -1); // + IP Header
		// 40*20 =
		// 60,000 Bytes
		TcpTestMessage msg3 = new TcpTestMessage(58400, -1); // + IP Header
		// 40*20 =
		// 60,000 Bytes
		assertFalse(receivedTime.containsKey(msg2));
		assertFalse(receivedTime.containsKey(msg3));
		s1.send(msg2, r1.getNetID(), NetProtocol.IPv4); //
		s1.send(msg3, r1.getNetID(), NetProtocol.IPv4,
				5 * Simulator.MINUTE_UNIT); // => 20 min
		SimulatorTest.runSimulation(180 * Simulator.MINUTE_UNIT);
		assertEquals(100.0, receivedTime.get(msg2)
				/ (double) Simulator.MINUTE_UNIT, 0.01);
		assertEquals(105.0, receivedTime.get(msg3)
				/ (double) Simulator.MINUTE_UNIT, 0.01);
	}

	/**
	 * 1 Sender, 2 Receiver, TCP throughput limitation to 10 bytes / s 100
	 * bytes/up + 2* 1000 bytes/sec down 0 5 100 105 min msg2:
	 * |------------------------------| 100 min at 10 bytes / s msg3:
	 * |------------------------------| 100 min at 10 bytes / s
	 */
	@Test
	public void testSend_2Streams_1Sender_2Receiver_throughputLimitation() {
		latencyModel.setTcpThroughput(10);
		TcpTestMessage msg2 = new TcpTestMessage(58400, -1); // + IP Header
		// 40*20 =
		// 60,000 Bytes
		TcpTestMessage msg3 = new TcpTestMessage(58400, -1); // + IP Header
		// 40*20 =
		// 60,000 Bytes
		assertFalse(receivedTime.containsKey(msg2));
		assertFalse(receivedTime.containsKey(msg3));
		s1.send(msg2, r1.getNetID(), NetProtocol.IPv4); //
		s1.send(msg3, r2.getNetID(), NetProtocol.IPv4,
				5 * Simulator.MINUTE_UNIT); // => 20 min
		SimulatorTest.runSimulation(180 * Simulator.MINUTE_UNIT);
		assertEquals(100.0, receivedTime.get(msg2)
				/ (double) Simulator.MINUTE_UNIT, 0.01);
		assertEquals(105.0, receivedTime.get(msg3)
				/ (double) Simulator.MINUTE_UNIT, 0.01);
	}

	/**
	 * 2 Sender, 1 Receiver, TCP throughput limitation to 10 bytes / s 2 * 100
	 * bytes/up + 1000 bytes/sec down: 0 5 10 15 msg2:
	 * |------------------------------| 100 min at 10 bytes / s msg3:
	 * |------------------------------| 100 min at 10 bytes / s
	 */
	@Test
	public void testSend_2Streams_2Sender_1Receiver_throughputLimitation() {
		latencyModel.setTcpThroughput(10);
		TcpTestMessage msg2 = new TcpTestMessage(58400, -1); // + IP Header
		// 40*20 =
		// 60,000 Bytes
		TcpTestMessage msg3 = new TcpTestMessage(58400, -1); // + IP Header
		// 40*20 =
		// 60,000 Bytes
		assertFalse(receivedTime.containsKey(msg2));
		assertFalse(receivedTime.containsKey(msg3));
		s1.send(msg2, r1.getNetID(), NetProtocol.IPv4); //
		s2.send(msg3, r1.getNetID(), NetProtocol.IPv4,
				5 * Simulator.MINUTE_UNIT); // => 20 min
		SimulatorTest.runSimulation(180 * Simulator.MINUTE_UNIT);
		assertEquals(100.0, receivedTime.get(msg2)
				/ (double) Simulator.MINUTE_UNIT, 0.01);
		assertEquals(105.0, receivedTime.get(msg3)
				/ (double) Simulator.MINUTE_UNIT, 0.01);
	}

	/**
	 * 1 Sender, 2 Receiver, no TCP throughput Limitation 100 bytes/sec up + 2 *
	 * 1000 bytes/sec down 0 5 12.5 min msg2: |------X 5 min at 50 bytes / s
	 * msg3: |--------------------| 5 min at 50 bytes / s + 7.5 min at 100 bytes
	 * / s
	 */
	@Test
	public void testSend_2Streams_1Sender_2Receiver_Offline() {
		TcpTestMessage msg2 = new TcpTestMessage(58400, -1); // + IP Header
		// 40*20 =
		// 60,000 Bytes
		TcpTestMessage msg3 = new TcpTestMessage(58400, -1); // + IP Header
		// 40*20 =
		// 60,000 Bytes
		assertFalse(receivedTime.containsKey(msg2));
		assertFalse(receivedTime.containsKey(msg3));
		s1.send(msg2, r1.getNetID(), NetProtocol.IPv4);
		s1.send(msg3, r2.getNetID(), NetProtocol.IPv4);
		r1.goOffline(5 * Simulator.MINUTE_UNIT);
		SimulatorTest.runSimulation(6180 * Simulator.MINUTE_UNIT);
		assertFalse(receivedTime.containsKey(msg2));
		assertEquals(12.5, receivedTime.get(msg3)
				/ (double) Simulator.MINUTE_UNIT, 0.01);
	}

	/**
	 * 1 Sender, 2 Receiver, no TCP throughput Limitation 100 bytes/sec up + 2 *
	 * 1000 bytes/sec down 0 5 12.5 min msg2: |------X 5 min at 50 bytes / s
	 * msg3: |--------------------| 5 min at 50 bytes / s + 7.5 min at 100 bytes
	 * / s
	 */
	@Test
	public void testSend_2Streams_1Sender_2Receiver_CancelStream() {
		TcpTestMessage msg2 = new TcpTestMessage(58400, -1); // + IP Header
		// 40*20 =
		// 60,000 Bytes
		TcpTestMessage msg3 = new TcpTestMessage(58400, -1); // + IP Header
		// 40*20 =
		// 60,000 Bytes
		assertFalse(receivedTime.containsKey(msg2));
		assertFalse(receivedTime.containsKey(msg3));
		s1.send(msg2, r1.getNetID(), NetProtocol.IPv4);
		s1.send(msg3, r2.getNetID(), NetProtocol.IPv4);
		r1.cancelTransmission(msg2.getCommId(), 5 * Simulator.MINUTE_UNIT);
		SimulatorTest.runSimulation(6180 * Simulator.MINUTE_UNIT);
		assertFalse(receivedTime.containsKey(msg2));
		assertEquals(12.5, receivedTime.get(msg3)
				/ (double) Simulator.MINUTE_UNIT, 0.01);
	}

	static class TestMessage implements Message {

		/**
		 * 
		 */
		private static final long serialVersionUID = -7098501679879778297L;

		long size;

		public TestMessage(long size) {
			this.size = size;
		}

		@Override
		public Message getPayload() {
			return null;
		}

		@Override
		public long getSize() {
			return size;
		}

	}

	static class TcpTestMessage extends TCPMessage {
		/**
		 * 
		 */
		private static final long serialVersionUID = -4767081683573144489L;

		public TcpTestMessage(long payloadSize, int commId) {
			super(new TestMessage(payloadSize), (short) 10, (short) 10, commId,
					false, 0, null, null);
		}
	}

	static class TestLatencyModel extends GnpLatencyModel {

		double tcpThroughput = Double.MAX_VALUE;

		double udpErrorProb = 0;

		public void setTcpThroughput(double tcpThroughput) {
			this.tcpThroughput = tcpThroughput;
		}

		public void setUdpErrorProb(double udpErrorProb) {
			this.udpErrorProb = udpErrorProb;
		}

		@Override
		public double getUDPerrorProbability(GnpNetLayer sender,
				GnpNetLayer receiver, IPv4Message msg) {
			return 0.0;
		}

		@Override
		public double getTcpThroughput(GnpNetLayer sender, GnpNetLayer receiver) {
			return tcpThroughput;
		}

		@Override
		public long getPropagationDelay(GnpNetLayer sender, GnpNetLayer receiver) {
			return 0;
		}
	}

	class TestMessageListener implements NetMessageListener {
		@Override
		public void messageArrived(NetMsgEvent nme) {
			assertNotNull(nme);
			receivedData.add(nme.getPayload());
			receivedTime.put(nme.getPayload(), Simulator.getCurrentTime());
		}
	}

}
