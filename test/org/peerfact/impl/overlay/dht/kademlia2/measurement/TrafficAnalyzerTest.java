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

package org.peerfact.impl.overlay.dht.kademlia2.measurement;

import static org.junit.Assert.assertEquals;

import java.io.OutputStreamWriter;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.peerfact.impl.overlay.dht.kademlia.base.analyzer.TrafficAnalyzer;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayID;
import org.peerfact.impl.overlay.dht.kademlia.base.components.RoutingTableConfig;
import org.peerfact.impl.overlay.dht.kademlia.base.messages.KademliaMsg;
import org.peerfact.impl.overlay.dht.kademlia.base.operations.AbstractKademliaOperation.Reason;
import org.peerfact.impl.overlay.dht.kademlia2.setup.ConfigStub;



/**
 * Tests for TrafficAnalyser.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 */
public class TrafficAnalyzerTest {

	static ConfigStub config;

	/**
	 * 
	 */
	@BeforeClass
	public static void beforeClass() {
		config = new ConfigStub();
	}

	/**
	 * Messages sent/received before the analysis is started should be ignored.
	 */
	@Test
	public static void testMsgBeforeStart() {
		TrafficAnalyzer a = new TrafficAnalyzer();
		a.setDuration(2);
		a.setNumberOfHosts(40);
		a.messageReceived(new KademliaMsgStub(4, Reason.MAINTENANCE, config));
		a.messageSent(new KademliaMsgStub(234, Reason.USER_INITIATED, config));
		a.messageReceived(new KademliaMsgStub(4342, Reason.USER_INITIATED,
				config));
		a.messageSent(new KademliaMsgStub(2344, Reason.USER_INITIATED, config));
		assertEquals("The avg #bytes sent (reason=MAINTENANCE) should be 0",
				0d, a.getAvgMaintenanceSent(),0.1);
		assertEquals(
				"The avg #bytes received (reason=MAINTENANCE) should be 0", 0d,
				a.getAvgMaintenanceReceived(),0.1);
		assertEquals("The avg #bytes sent (reason=USER_INITIATED) should be 0",
				0d, a.getAvgUserInitiatedSent(),0.1);
		assertEquals(
				"The avg #bytes received (reason=USER_INITIATED) should be 0",
				0d, a.getAvgUserInitiatedReceived(),0.1);
	}

	/**
	 * Tests if message average is computed correctly.
	 */
	@Test
	public static void testAverage() {
		TrafficAnalyzer a = new TrafficAnalyzer();
		a.setDuration(4);
		a.setNumberOfHosts(2);
		a.start();
		a.messageReceived(new KademliaMsgStub(2, Reason.MAINTENANCE, config));
		a.messageReceived(new KademliaMsgStub(2, Reason.MAINTENANCE, config));
		a.messageReceived(new KademliaMsgStub(2, Reason.MAINTENANCE, config));
		a.messageReceived(new KademliaMsgStub(2, Reason.MAINTENANCE, config));
		assertEquals("Avg #bytes received (MAINTENANCE) per hour & host is 1",
				1d, a.getAvgMaintenanceReceived(),0.1);

		a
		.messageReceived(new KademliaMsgStub(2, Reason.USER_INITIATED,
				config));
		a
		.messageReceived(new KademliaMsgStub(1, Reason.USER_INITIATED,
				config));
		a
		.messageReceived(new KademliaMsgStub(3, Reason.USER_INITIATED,
				config));
		a
		.messageReceived(new KademliaMsgStub(3, Reason.USER_INITIATED,
				config));
		assertEquals(
				"Avg #bytes received (USER_INITIATED) per hour&host is 1.125",
				1125d, a.getAvgUserInitiatedReceived() * 1000,0.1);

		a.messageSent(new KademliaMsgStub(20348, Reason.MAINTENANCE, config));
		assertEquals("Avg #bytes sent (MAINTENANCE) per hour&host is 2543.5",
				25435d, a.getAvgMaintenanceSent() * 10,0.1);

		a = new TrafficAnalyzer();
		a.setDuration(3);
		a.setNumberOfHosts(10093);
		a.start();
		a.messageSent(new KademliaMsgStub(234, Reason.USER_INITIATED, config));
		a.messageSent(new KademliaMsgStub(4342, Reason.USER_INITIATED, config));
		a.messageSent(new KademliaMsgStub(2344, Reason.USER_INITIATED, config));
		assertEquals(
				"Avg #bytes sent (USER_INITIATED) per hour&host is approx. 0.2285",
				Math.round((234 + 4342 + 2344) / ((double) 3 * 10093) * 10000),
				Math.round(a.getAvgUserInitiatedSent() * 10000));
	}

	/**
	 * Tests the precision for 1,000,000 sent messages.
	 */
	@Test
	public static void testPrecision() {
		long cumulatedSize = 0, rndSize = 0;

		TrafficAnalyzer a = new TrafficAnalyzer();
		a.setDuration(3);
		a.setNumberOfHosts(109358);
		a.start();

		for (int i = 1; i <= 1000000; i++) {
			rndSize = (long) (Math.random() * 1000);
			cumulatedSize = cumulatedSize + rndSize;
			a.messageReceived(new KademliaMsgStub(rndSize, Reason.MAINTENANCE,
					config));
		}
		double avg = cumulatedSize / ((double) (3 * 109358));

		assertEquals("Expect average of " + avg + " with 6 decimals precision",
				(long) (avg * 1000000),
				(long) (a.getAvgMaintenanceReceived() * 1000000));
	}

	/**
	 * Tests the output generated by stop(). Has to be verified manually.
	 */
	@Ignore
	@Test
	public static void testStopOutput() throws Exception {
		long cumulatedSize = 0, rndSize = 0;

		TrafficAnalyzer a = new TrafficAnalyzer();
		a.setDuration(3);
		a.setNumberOfHosts(109358);
		a.start();

		for (int i = 1; i <= 10; i++) {
			rndSize = (long) (Math.random() * 1000);
			cumulatedSize = cumulatedSize + rndSize;
			a.messageReceived(new KademliaMsgStub(rndSize, Reason.MAINTENANCE,
					config));
		}
		a.stop(new OutputStreamWriter(System.out));
	}

	/**
	 * Test stub for KademliaMsgs that allows to set its size and reason.
	 * 
	 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
	 */
	protected static class KademliaMsgStub extends
	KademliaMsg<KademliaOverlayID> {

		/**
		 * 
		 */
		private static final long serialVersionUID = 5873133004874434343L;

		/**
		 * The "additional" size of this message (dirty hack: set additional
		 * size so that when adding the base size from the superclass, the
		 * desired overall size results).
		 */
		private long addlSize;

		/**
		 * Constructs a new KademliaMsg test stub with the given size and
		 * reason. The sender of this message is internally set to id=0 and the
		 * receiver has id=1.
		 * 
		 * @param msgSize
		 *            the size of this message in bytes.
		 * @param why
		 *            the reason why this message has been sent.
		 */
		public KademliaMsgStub(long msgSize, Reason why, RoutingTableConfig conf) {
			super(new KademliaOverlayID("0", conf), new KademliaOverlayID("1",
					conf), why, conf);
			// subtract the "base" size of a KademliaMsg (defined in superclass)
			addlSize = msgSize - 2 * (config.getIDLength() / 8);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected long getOtherFieldSize() {
			return addlSize;
		}
	}

}
