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

import org.junit.Before;
import org.junit.Test;
import org.peerfact.impl.common.DefaultHost;
import org.peerfact.impl.common.DefaultHostProperties;
import org.peerfact.impl.network.IPv4Message;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.simengine.SimulatorTest;
import org.peerfact.impl.transport.UDPMessage;

public class GnpLatencyModelTest extends SimulatorTest {

	GnpNetLayer us1, ca1, jp1, cn1, de1, br1;

	GnpNetLayerFactory netLayerFactoryGnp;

	GnpLatencyModel latencyModelGnp;

	@Override
	@Before
	public void setUp() {
		super.setUp();
		try {
			latencyModelGnp = new GnpLatencyModel();
			netLayerFactoryGnp = new GnpNetLayerFactory();
			netLayerFactoryGnp
					.setGnpFile("test/de/tud/kom/p2psim/impl/network/gnp/testHosts.xml");
			netLayerFactoryGnp.setLatencyModel(latencyModelGnp);

			DefaultHostProperties propUs1 = new DefaultHostProperties();
			propUs1.setGroupID("UnitedStates");
			DefaultHost hostUs1 = new DefaultHost();
			hostUs1.setProperties(propUs1);

			DefaultHostProperties propCa1 = new DefaultHostProperties();
			propCa1.setGroupID("Canada");
			DefaultHost hostCa1 = new DefaultHost();
			hostCa1.setProperties(propCa1);

			DefaultHostProperties propJp1 = new DefaultHostProperties();
			propJp1.setGroupID("Japan");
			DefaultHost hostJp1 = new DefaultHost();
			hostJp1.setProperties(propJp1);

			DefaultHostProperties propCn1 = new DefaultHostProperties();
			propCn1.setGroupID("China");
			DefaultHost hostCn1 = new DefaultHost();
			hostCn1.setProperties(propCn1);

			DefaultHostProperties propDe1 = new DefaultHostProperties();
			propDe1.setGroupID("Germany");
			DefaultHost hostDe1 = new DefaultHost();
			hostDe1.setProperties(propDe1);

			DefaultHostProperties propBr1 = new DefaultHostProperties();
			propBr1.setGroupID("Brazil");
			DefaultHost hostBr1 = new DefaultHost();
			hostBr1.setProperties(propBr1);

			us1 = netLayerFactoryGnp.createComponent(hostUs1);
			ca1 = netLayerFactoryGnp.createComponent(hostCa1);
			jp1 = netLayerFactoryGnp.createComponent(hostJp1);
			cn1 = netLayerFactoryGnp.createComponent(hostCn1);
			de1 = netLayerFactoryGnp.createComponent(hostDe1);
			br1 = netLayerFactoryGnp.createComponent(hostBr1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testSkitterPropagationDelayGnp() {

		latencyModelGnp.setUsePingErRttData(false);
		latencyModelGnp.setUsePingErJitter(false);

		assertEquals(0l, latencyModelGnp.getPropagationDelay(us1, us1));
		assertEquals(10 * Simulator.MILLISECOND_UNIT, latencyModelGnp
				.getPropagationDelay(us1, ca1));
		assertEquals(15 * Simulator.MILLISECOND_UNIT, latencyModelGnp
				.getPropagationDelay(us1, jp1));
		assertEquals(25 * Simulator.MILLISECOND_UNIT, latencyModelGnp
				.getPropagationDelay(us1, cn1));
		assertEquals(30 * Simulator.MILLISECOND_UNIT, latencyModelGnp
				.getPropagationDelay(us1, de1));
		assertEquals(40 * Simulator.MILLISECOND_UNIT, latencyModelGnp
				.getPropagationDelay(us1, br1));

		assertEquals(0l, latencyModelGnp.getPropagationDelay(ca1, ca1));
		assertEquals(5 * Simulator.MILLISECOND_UNIT, latencyModelGnp
				.getPropagationDelay(ca1, jp1));
		assertEquals(15 * Simulator.MILLISECOND_UNIT, latencyModelGnp
				.getPropagationDelay(ca1, cn1));
		assertEquals(20 * Simulator.MILLISECOND_UNIT, latencyModelGnp
				.getPropagationDelay(ca1, de1));
		assertEquals(30 * Simulator.MILLISECOND_UNIT, latencyModelGnp
				.getPropagationDelay(ca1, br1));

		assertEquals(0l, latencyModelGnp.getPropagationDelay(jp1, jp1));
		assertEquals(10 * Simulator.MILLISECOND_UNIT, latencyModelGnp
				.getPropagationDelay(jp1, cn1));
		assertEquals(15 * Simulator.MILLISECOND_UNIT, latencyModelGnp
				.getPropagationDelay(jp1, de1));
		assertEquals(25 * Simulator.MILLISECOND_UNIT, latencyModelGnp
				.getPropagationDelay(jp1, br1));

		assertEquals(0l, latencyModelGnp.getPropagationDelay(cn1, cn1));
		assertEquals(5 * Simulator.MILLISECOND_UNIT, latencyModelGnp
				.getPropagationDelay(cn1, de1));
		assertEquals(15 * Simulator.MILLISECOND_UNIT, latencyModelGnp
				.getPropagationDelay(cn1, br1));

		assertEquals(0l, latencyModelGnp.getPropagationDelay(de1, de1));
		assertEquals(10 * Simulator.MILLISECOND_UNIT, latencyModelGnp
				.getPropagationDelay(de1, br1));

		assertEquals(0l, latencyModelGnp.getPropagationDelay(br1, br1));

		latencyModelGnp.setUsePingErJitter(true);

	}

	@Test
	public void testPingErPropagationDelayGnp() {

		latencyModelGnp.setUsePingErRttData(true);
		latencyModelGnp.setUsePingErJitter(false);

		long averageMinRtt = (long) Math
				.floor(118.75 / 2 * Simulator.MILLISECOND_UNIT);

		assertEquals(latencyModelGnp.getPropagationDelay(us1, us1),
				30 / 2 * Simulator.MILLISECOND_UNIT);
		assertEquals(latencyModelGnp.getPropagationDelay(us1, ca1),
				30 / 2 * Simulator.MILLISECOND_UNIT);
		assertEquals(latencyModelGnp.getPropagationDelay(us1, jp1),
				250 / 2 * Simulator.MILLISECOND_UNIT);
		assertEquals(latencyModelGnp.getPropagationDelay(us1, cn1),
				averageMinRtt);
		assertEquals(latencyModelGnp.getPropagationDelay(us1, de1),
				200 / 2 * Simulator.MILLISECOND_UNIT);
		assertEquals(latencyModelGnp.getPropagationDelay(us1, br1),
				100 / 2 * Simulator.MILLISECOND_UNIT);

		assertEquals(latencyModelGnp.getPropagationDelay(ca1, us1),
				30 / 2 * Simulator.MILLISECOND_UNIT);
		assertEquals(latencyModelGnp.getPropagationDelay(ca1, ca1),
				averageMinRtt);
		assertEquals(latencyModelGnp.getPropagationDelay(ca1, jp1),
				250 / 2 * Simulator.MILLISECOND_UNIT);
		assertEquals(latencyModelGnp.getPropagationDelay(ca1, cn1),
				averageMinRtt);
		assertEquals(latencyModelGnp.getPropagationDelay(ca1, de1),
				averageMinRtt);
		assertEquals(latencyModelGnp.getPropagationDelay(ca1, br1),
				100 / 2 * Simulator.MILLISECOND_UNIT);

		assertEquals(latencyModelGnp.getPropagationDelay(jp1, us1),
				250 / 2 * Simulator.MILLISECOND_UNIT);
		assertEquals(latencyModelGnp.getPropagationDelay(jp1, ca1),
				250 / 2 * Simulator.MILLISECOND_UNIT);
		assertEquals(latencyModelGnp.getPropagationDelay(jp1, jp1),
				20 / 2 * Simulator.MILLISECOND_UNIT);
		assertEquals(latencyModelGnp.getPropagationDelay(jp1, cn1),
				averageMinRtt);
		assertEquals(latencyModelGnp.getPropagationDelay(jp1, de1),
				300 / 2 * Simulator.MILLISECOND_UNIT);
		assertEquals(latencyModelGnp.getPropagationDelay(jp1, br1),
				averageMinRtt);

		assertEquals(latencyModelGnp.getPropagationDelay(cn1, us1),
				averageMinRtt);
		assertEquals(latencyModelGnp.getPropagationDelay(cn1, ca1),
				averageMinRtt);
		assertEquals(latencyModelGnp.getPropagationDelay(cn1, jp1),
				averageMinRtt);
		assertEquals(latencyModelGnp.getPropagationDelay(cn1, cn1),
				averageMinRtt);
		assertEquals(latencyModelGnp.getPropagationDelay(cn1, de1),
				averageMinRtt);
		assertEquals(latencyModelGnp.getPropagationDelay(cn1, br1),
				averageMinRtt);

		assertEquals(latencyModelGnp.getPropagationDelay(de1, us1),
				200 / 2 * Simulator.MILLISECOND_UNIT);
		assertEquals(latencyModelGnp.getPropagationDelay(de1, ca1),
				averageMinRtt);
		assertEquals(latencyModelGnp.getPropagationDelay(de1, jp1),
				300 / 2 * Simulator.MILLISECOND_UNIT);
		assertEquals(latencyModelGnp.getPropagationDelay(de1, cn1),
				averageMinRtt);
		assertEquals(latencyModelGnp.getPropagationDelay(de1, de1),
				10 / 2 * Simulator.MILLISECOND_UNIT);
		assertEquals(latencyModelGnp.getPropagationDelay(de1, br1),
				averageMinRtt);

		assertEquals(latencyModelGnp.getPropagationDelay(br1, us1),
				100 / 2 * Simulator.MILLISECOND_UNIT);
		assertEquals(latencyModelGnp.getPropagationDelay(br1, ca1),
				100 / 2 * Simulator.MILLISECOND_UNIT);
		assertEquals(latencyModelGnp.getPropagationDelay(br1, jp1),
				averageMinRtt);
		assertEquals(latencyModelGnp.getPropagationDelay(br1, cn1),
				averageMinRtt);
		assertEquals(latencyModelGnp.getPropagationDelay(br1, de1),
				averageMinRtt);
		assertEquals(latencyModelGnp.getPropagationDelay(br1, br1),
				40 / 2 * Simulator.MILLISECOND_UNIT);

		latencyModelGnp.setUsePingErJitter(true);

		assertTrue(latencyModelGnp.getPropagationDelay(us1, us1) >= 30 / 2 * Simulator.MILLISECOND_UNIT);
		assertTrue(latencyModelGnp.getPropagationDelay(us1, ca1) >= 30 / 2 * Simulator.MILLISECOND_UNIT);
		assertTrue(latencyModelGnp.getPropagationDelay(us1, jp1) >= 250 / 2 * Simulator.MILLISECOND_UNIT);
		assertTrue(latencyModelGnp.getPropagationDelay(us1, cn1) >= averageMinRtt);
		assertTrue(latencyModelGnp.getPropagationDelay(us1, de1) >= 200 / 2 * Simulator.MILLISECOND_UNIT);
		assertTrue(latencyModelGnp.getPropagationDelay(us1, br1) >= 100 / 2 * Simulator.MILLISECOND_UNIT);

		assertTrue(latencyModelGnp.getPropagationDelay(ca1, us1) >= 30 / 2 * Simulator.MILLISECOND_UNIT);
		assertTrue(latencyModelGnp.getPropagationDelay(ca1, ca1) >= averageMinRtt);
		assertTrue(latencyModelGnp.getPropagationDelay(ca1, jp1) >= 250 / 2 * Simulator.MILLISECOND_UNIT);
		assertTrue(latencyModelGnp.getPropagationDelay(ca1, cn1) >= averageMinRtt);
		assertTrue(latencyModelGnp.getPropagationDelay(ca1, de1) >= averageMinRtt);
		assertTrue(latencyModelGnp.getPropagationDelay(ca1, br1) >= 100 / 2 * Simulator.MILLISECOND_UNIT);

		assertTrue(latencyModelGnp.getPropagationDelay(jp1, us1) >= 250 / 2 * Simulator.MILLISECOND_UNIT);
		assertTrue(latencyModelGnp.getPropagationDelay(jp1, ca1) >= 250 / 2 * Simulator.MILLISECOND_UNIT);
		assertTrue(latencyModelGnp.getPropagationDelay(jp1, jp1) >= 20 / 2 * Simulator.MILLISECOND_UNIT);
		assertTrue(latencyModelGnp.getPropagationDelay(jp1, cn1) >= averageMinRtt);
		assertTrue(latencyModelGnp.getPropagationDelay(jp1, de1) >= 300 / 2 * Simulator.MILLISECOND_UNIT);
		assertTrue(latencyModelGnp.getPropagationDelay(jp1, br1) >= averageMinRtt);

		assertTrue(latencyModelGnp.getPropagationDelay(cn1, us1) >= averageMinRtt);
		assertTrue(latencyModelGnp.getPropagationDelay(cn1, ca1) >= averageMinRtt);
		assertTrue(latencyModelGnp.getPropagationDelay(cn1, jp1) >= averageMinRtt);
		assertTrue(latencyModelGnp.getPropagationDelay(cn1, cn1) >= averageMinRtt);
		assertTrue(latencyModelGnp.getPropagationDelay(cn1, de1) >= averageMinRtt);
		assertTrue(latencyModelGnp.getPropagationDelay(cn1, br1) >= averageMinRtt);

		assertTrue(latencyModelGnp.getPropagationDelay(de1, us1) >= 200 / 2 * Simulator.MILLISECOND_UNIT);
		assertTrue(latencyModelGnp.getPropagationDelay(de1, ca1) >= averageMinRtt);
		assertTrue(latencyModelGnp.getPropagationDelay(de1, jp1) >= 300 / 2 * Simulator.MILLISECOND_UNIT);
		assertTrue(latencyModelGnp.getPropagationDelay(de1, cn1) >= averageMinRtt);
		assertTrue(latencyModelGnp.getPropagationDelay(de1, de1) >= 10 / 2 * Simulator.MILLISECOND_UNIT);
		assertTrue(latencyModelGnp.getPropagationDelay(de1, br1) >= averageMinRtt);

		assertTrue(latencyModelGnp.getPropagationDelay(br1, us1) >= 100 / 2 * Simulator.MILLISECOND_UNIT);
		assertTrue(latencyModelGnp.getPropagationDelay(br1, ca1) >= 100 / 2 * Simulator.MILLISECOND_UNIT);
		assertTrue(latencyModelGnp.getPropagationDelay(br1, jp1) >= averageMinRtt);
		assertTrue(latencyModelGnp.getPropagationDelay(br1, cn1) >= averageMinRtt);
		assertTrue(latencyModelGnp.getPropagationDelay(br1, de1) >= averageMinRtt);
		assertTrue(latencyModelGnp.getPropagationDelay(br1, br1) >= 40 / 2 * Simulator.MILLISECOND_UNIT);

	}

	@Test
	public void testTransmissionDelay() {
		assertEquals(0 * Simulator.MILLISECOND_UNIT, GnpLatencyModel
				.getTransmissionDelay(0, 1000)); // 0 bytes at 1000 bytes/s
		assertEquals(500 * Simulator.MILLISECOND_UNIT, GnpLatencyModel
				.getTransmissionDelay(500, 1000)); // 500 bytes at 1000 bytes/s
		assertEquals(1000 * Simulator.MILLISECOND_UNIT, GnpLatencyModel
				.getTransmissionDelay(1000, 1000)); // 1000 bytes at 1000
		// bytes/s
		assertEquals(2000 * Simulator.MILLISECOND_UNIT, GnpLatencyModel
				.getTransmissionDelay(1000, 500)); // 1000 bytes at 500 bytes/s
		assertEquals(Long.MAX_VALUE, GnpLatencyModel.getTransmissionDelay(1000,
				0)); // 1000 bytes at 0 bytes/s
		assertEquals(0 * Simulator.MILLISECOND_UNIT, GnpLatencyModel
				.getTransmissionDelay(0, 0)); // 0 bytes at 0 bytes/s
	}

	@Test
	public void testErrorProbability() {
		latencyModelGnp.setUsePingErPacketLoss(true);
		// Loss Propability from DE to DE
		// for 1 Packet
		double pl1 = latencyModelGnp.getUDPerrorProbability(de1, de1,
				new IPv4TestMessage(1));
		// Loss for 2 Packets
		double pl2 = latencyModelGnp.getUDPerrorProbability(de1, de1,
				new IPv4TestMessage(2));
		// Loss for 3 Packets
		double pl3 = latencyModelGnp.getUDPerrorProbability(de1, de1,
				new IPv4TestMessage(3));
		assertEquals(0.005012, pl1, 0.00001);
		assertEquals(0.01, pl2, 0.00001);
		assertEquals(0.014962, pl3, 0.00001);
	}

	@Test
	public void testTcpTroughput() {
		// TCP Throughput from US to DE

		// /////// with PingER Data

		latencyModelGnp.setUsePingErRttData(true);

		// - Jitter
		// + Packet Loss
		latencyModelGnp.setUsePingErPacketLoss(true);
		latencyModelGnp.setUsePingErJitter(false);
		assertEquals(89181, latencyModelGnp.getTcpThroughput(us1, de1), 1);

		// + Jitter
		// + Packet Loss
		latencyModelGnp.setUsePingErPacketLoss(true);
		latencyModelGnp.setUsePingErJitter(true);
		assertEquals(71345, latencyModelGnp.getTcpThroughput(us1, de1), 1);

		// - Jitter
		// - Packet Loss
		latencyModelGnp.setUsePingErJitter(false);
		latencyModelGnp.setUsePingErPacketLoss(false);
		assertEquals(Double.POSITIVE_INFINITY, latencyModelGnp
				.getTcpThroughput(us1, de1), 1);

		// + Jitter
		// - Packet Loss
		latencyModelGnp.setUsePingErJitter(true);
		latencyModelGnp.setUsePingErPacketLoss(false);
		assertEquals(Double.POSITIVE_INFINITY, latencyModelGnp
				.getTcpThroughput(us1, de1), 1);

		// /////// with GNP RTT Data

		latencyModelGnp.setUsePingErRttData(false);

		// - Jitter
		// + Packet Loss
		latencyModelGnp.setUsePingErPacketLoss(true);
		latencyModelGnp.setUsePingErJitter(false);
		assertEquals(297271, latencyModelGnp.getTcpThroughput(us1, de1), 1);

		// + Jitter
		// + Packet Loss
		latencyModelGnp.setUsePingErPacketLoss(true);
		latencyModelGnp.setUsePingErJitter(true);
		assertEquals(162148, latencyModelGnp.getTcpThroughput(us1, de1), 1);

		// - Jitter
		// - Packet Loss
		latencyModelGnp.setUsePingErJitter(false);
		latencyModelGnp.setUsePingErPacketLoss(false);

		assertEquals(Double.POSITIVE_INFINITY, latencyModelGnp
				.getTcpThroughput(us1, de1), 1);

		// + Jitter
		// - Packet Loss
		latencyModelGnp.setUsePingErJitter(true);
		latencyModelGnp.setUsePingErPacketLoss(false);
		assertEquals(Double.POSITIVE_INFINITY, latencyModelGnp
				.getTcpThroughput(us1, de1), 1);

	}

	private static class IPv4TestMessage extends IPv4Message {

		/**
		 * 
		 */
		private static final long serialVersionUID = 2246090462068818752L;

		int numberOfPackets;

		public IPv4TestMessage(int numberOfPackets) {
			super(new UdpTestMessage(), null, null);
			this.numberOfPackets = numberOfPackets;
		}

		@Override
		public int getNoOfFragments() {
			return numberOfPackets;
		}

	}

	private static class UdpTestMessage extends UDPMessage {

		/**
		 * 
		 */
		private static final long serialVersionUID = -8673434450011437613L;

		public UdpTestMessage() {
			super(null, (short) 10, (short) 10, 1, false, null, null);
		}

		@Override
		public long getSize() {
			return 0;
		}

	}

}
