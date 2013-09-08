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

package org.peerfact.impl.transport;

import java.util.LinkedHashMap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.peerfact.api.common.Message;
import org.peerfact.api.network.Bandwidth;
import org.peerfact.api.transport.TransLayer;
import org.peerfact.api.transport.TransMessageListener;
import org.peerfact.api.transport.TransProtocol;
import org.peerfact.impl.network.IPv4Message;
import org.peerfact.impl.network.IPv4NetID;
import org.peerfact.impl.network.gnp.AbstractGnpNetBandwidthManager;
import org.peerfact.impl.network.gnp.GnpLatencyModel;
import org.peerfact.impl.network.gnp.GnpNetBandwidthManagerPeriodical;
import org.peerfact.impl.network.gnp.GnpNetLayer;
import org.peerfact.impl.network.gnp.GnpSubnet;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.transport.DefaultTransLayer;
import org.peerfact.impl.transport.TransMsgEvent;
import org.peerfact.util.ComponentTest;


/**
 * 
 * 
 * @author Gerald Klunker
 * @version 0.1, 19.12.2007
 * 
 */
public class DefaultTransLayerInOrderDeliveryTest extends ComponentTest {


	GnpNetLayer s1, r1;

	short port = 100;

	LinkedHashMap<Message, Long> receivedTime;

	GnpSubnet subnet;

	TestLatencyModel latencyModel;

	AbstractGnpNetBandwidthManager bandwidthManager;

	TransLayer tlSender, tlReceiver;

	DummyMessage msg1, msg2, msg3;

	// private SimpleNetworkFactory netFactory;

	@Override
	@Before
	public void setUp() {
		super.setUp();

		receivedTime = new LinkedHashMap<Message, Long>();

		subnet = new GnpSubnet();

		latencyModel = new TestLatencyModel();
		bandwidthManager = new GnpNetBandwidthManagerPeriodical();

		subnet.setBandwidthManager(bandwidthManager);
		subnet.setLatencyModel(latencyModel);
		subnet.setPbaPeriod(300 * Simulator.MILLISECOND_UNIT);

		s1 = new GnpNetLayer(subnet, new IPv4NetID(1L), null, null,
				new Bandwidth(1000, 100));
		r1 = new GnpNetLayer(subnet, new IPv4NetID(10L), null, null,
				new Bandwidth(1000, 100));

		tlSender = new DefaultTransLayer(s1);
		tlReceiver = new DefaultTransLayer(r1);

		tlReceiver.addTransMsgListener(getTransListener(), port);

	}

	/**
	 * UDP Message without IP Fragmentation => one IP - Packet (size < MTU.size)
	 * Delivery: msg1 <= msg2 <= msg3 Different Size, but Packets will be send
	 * one after another
	 */
	@Test
	public void testDelivery_SinglePackets() {
		msg1 = new DummyMessage(300);
		msg2 = new DummyMessage(100);
		msg3 = new DummyMessage(200);
		assertTrue(receivedTime.isEmpty());
		tlSender.send(msg1, tlReceiver.getLocalTransInfo(port), port,
				TransProtocol.UDP);
		tlSender.send(msg2, tlReceiver.getLocalTransInfo(port), port,
				TransProtocol.UDP);
		tlSender.send(msg3, tlReceiver.getLocalTransInfo(port), port,
				TransProtocol.UDP);
		runSimulation(10 * Simulator.MINUTE_UNIT);
		assertTrue(receivedTime.get(msg1) < receivedTime.get(msg2));
		assertTrue(receivedTime.get(msg2) < receivedTime.get(msg3));
	}

	/**
	 * UDP Message with IP Fragmentation => multiple IP - Packets per Message
	 * (size > MTU.size) Delivery: msg2 <= msg3 <= msg1 Smaller Messages will
	 * delivered first
	 */
	@Test
	public void testDelivery_UDPStream() {
		msg1 = new DummyMessage(30000);
		msg2 = new DummyMessage(10000);
		msg3 = new DummyMessage(20000);
		assertTrue(receivedTime.isEmpty());
		tlSender.send(msg1, tlReceiver.getLocalTransInfo(port), port,
				TransProtocol.UDP);
		tlSender.send(msg2, tlReceiver.getLocalTransInfo(port), port,
				TransProtocol.UDP);
		tlSender.send(msg3, tlReceiver.getLocalTransInfo(port), port,
				TransProtocol.UDP);
		runSimulation(100 * Simulator.MINUTE_UNIT);
		assertTrue(receivedTime.get(msg2) < receivedTime.get(msg3));
		assertTrue(receivedTime.get(msg3) < receivedTime.get(msg1));
	}

	/**
	 * TCP Stream with => multiple IP - Packets per Message (size > MTU.size)
	 * Delivery: msg1 <= msg2 <= msg2 Reliable in-order delivery
	 */
	@Test
	public void testDelivery_TCPStream() {
		msg1 = new DummyMessage(30000);
		msg2 = new DummyMessage(10000);
		msg3 = new DummyMessage(20000);
		assertTrue(receivedTime.isEmpty());
		tlSender.send(msg1, tlReceiver.getLocalTransInfo(port), port,
				TransProtocol.TCP);
		tlSender.send(msg2, tlReceiver.getLocalTransInfo(port), port,
				TransProtocol.TCP);
		tlSender.send(msg3, tlReceiver.getLocalTransInfo(port), port,
				TransProtocol.TCP);
		runSimulation(100 * Simulator.MINUTE_UNIT);
		assertTrue(receivedTime.get(msg1) <= receivedTime.get(msg2));
		assertTrue(receivedTime.get(msg2) <= receivedTime.get(msg3));
	}

	protected TransMessageListener getTransListener() {
		return new TransMessageListener() {
			@Override
			public void messageArrived(TransMsgEvent receivingEvent) {
				Assert
				.assertTrue(receivingEvent.getPayload() instanceof DummyMessage);
				receivedTime.put(receivingEvent.getPayload(), Simulator
						.getCurrentTime());
			}
		};
	}

	static class TestLatencyModel extends GnpLatencyModel {

		@Override
		public double getUDPerrorProbability(GnpNetLayer sender,
				GnpNetLayer receiver, IPv4Message msg) {
			return 0.0;
		}

		@Override
		public double getTcpThroughput(GnpNetLayer sender, GnpNetLayer receiver) {
			return Double.MAX_VALUE;
		}

		@Override
		public long getPropagationDelay(GnpNetLayer sender, GnpNetLayer receiver) {
			return 0;
		}
	}

	static class DummyMessage implements Message {
		/**
		 * 
		 */
		private static final long serialVersionUID = 2321804337656324479L;
		long size;

		DummyMessage(long size) {
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

}
