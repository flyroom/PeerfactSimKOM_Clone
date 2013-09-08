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
import org.peerfact.api.common.Message;
import org.peerfact.api.network.Bandwidth;
import org.peerfact.api.network.NetID;
import org.peerfact.api.network.NetProtocol;
import org.peerfact.api.transport.TransProtocol;
import org.peerfact.impl.network.AbstractNetLayer;
import org.peerfact.impl.simengine.SimulatorTest;

public class GnpNetBandwidthManagerTest extends SimulatorTest {

	// private static final Logger log = SimLogger
	// .getLogger(GnpNetBandwidthManagerTest.class);

	private GnpNetBandwidthManagerPeriodical bandwidthManagerPeriodical;

	private GnpNetBandwidthManagerEvent bandwidthManagerEvent;

	TestNetLayer sender0;

	TestNetLayer sender1;

	TestNetLayer sender2;

	TestNetLayer receiver0;

	TestNetLayer receiver1;

	TestNetLayer receiver2;

	TestNetLayer receiver3;

	TestNetLayer receiver4;

	TestNetLayer receiver5;

	@Override
	@Before
	public void setUp() {
		super.setUp();

		bandwidthManagerPeriodical = new GnpNetBandwidthManagerPeriodical();
		bandwidthManagerEvent = new GnpNetBandwidthManagerEvent();
	}

	@Test
	public void test_AllAtOnce_FullAllocation_OneStream() {
		AllAtOnce_FullAllocation_OneStream(bandwidthManagerPeriodical);
		AllAtOnce_FullAllocation_OneStream(bandwidthManagerEvent);
	}

	private void AllAtOnce_FullAllocation_OneStream(
			AbstractGnpNetBandwidthManager bwManager) {

		sender0 = new TestNetLayer(0, 10, "sender 0");
		sender1 = new TestNetLayer(0, 10, "sender 1");
		sender2 = new TestNetLayer(0, 80, "sender 2");

		receiver0 = new TestNetLayer(40, 0, "receiver 0");
		receiver1 = new TestNetLayer(30, 0, "receiver 1");
		receiver2 = new TestNetLayer(10, 0, "receiver 2");
		receiver3 = new TestNetLayer(10, 0, "receiver 3");
		receiver4 = new TestNetLayer(10, 0, "receiver 4");
		receiver5 = new TestNetLayer(40, 0, "receiver 5");

		assertNull(bwManager.getBandwidthAllocation(sender0, receiver0));
		assertNull(bwManager.getBandwidthAllocation(sender0, receiver1));
		assertNull(bwManager.getBandwidthAllocation(sender0, receiver3));
		assertNull(bwManager.getBandwidthAllocation(sender1, receiver0));
		assertNull(bwManager.getBandwidthAllocation(sender1, receiver2));
		assertNull(bwManager.getBandwidthAllocation(sender2, receiver1));
		assertNull(bwManager.getBandwidthAllocation(sender2, receiver3));
		assertNull(bwManager.getBandwidthAllocation(sender2, receiver4));
		assertNull(bwManager.getBandwidthAllocation(sender2, receiver5));

		assertEquals(0, bwManager.getChangedAllocations().size());

		bwManager.addConnection(sender0, receiver0, sender0.getMaxBandwidth()
				.getUpBW());
		bwManager.addConnection(sender0, receiver1, sender0.getMaxBandwidth()
				.getUpBW());
		bwManager.addConnection(sender0, receiver3, sender0.getMaxBandwidth()
				.getUpBW());
		bwManager.addConnection(sender1, receiver0, sender1.getMaxBandwidth()
				.getUpBW());
		bwManager.addConnection(sender1, receiver2, sender1.getMaxBandwidth()
				.getUpBW());
		bwManager.addConnection(sender2, receiver1, sender2.getMaxBandwidth()
				.getUpBW());
		bwManager.addConnection(sender2, receiver3, sender2.getMaxBandwidth()
				.getUpBW());
		bwManager.addConnection(sender2, receiver4, sender2.getMaxBandwidth()
				.getUpBW());
		bwManager.addConnection(sender2, receiver5, sender2.getMaxBandwidth()
				.getUpBW());

		assertEquals(0, bwManager.getChangedAllocations().size());

		assertNotNull(bwManager.getBandwidthAllocation(sender0, receiver0));
		assertNotNull(bwManager.getBandwidthAllocation(sender0, receiver1));
		assertNotNull(bwManager.getBandwidthAllocation(sender0, receiver3));
		assertNotNull(bwManager.getBandwidthAllocation(sender1, receiver0));
		assertNotNull(bwManager.getBandwidthAllocation(sender1, receiver2));
		assertNotNull(bwManager.getBandwidthAllocation(sender2, receiver1));
		assertNotNull(bwManager.getBandwidthAllocation(sender2, receiver3));
		assertNotNull(bwManager.getBandwidthAllocation(sender2, receiver4));
		assertNotNull(bwManager.getBandwidthAllocation(sender2, receiver5));

		bwManager.allocateBandwidth();
		// one more allocation at periodical
		if (bwManager instanceof GnpNetBandwidthManagerPeriodical) {
			bwManager.allocateBandwidth();
		}

		assertEquals(3.3333,
				bwManager.getBandwidthAllocation(sender0, receiver0)
						.getAllocatedBandwidth(), 0.0001);
		assertEquals(3.3333,
				bwManager.getBandwidthAllocation(sender0, receiver1)
						.getAllocatedBandwidth(), 0.0001);
		assertEquals(3.3333,
				bwManager.getBandwidthAllocation(sender0, receiver3)
						.getAllocatedBandwidth(), 0.0001);
		assertEquals(5.0, bwManager.getBandwidthAllocation(sender1, receiver0)
				.getAllocatedBandwidth(), 0.1);
		assertEquals(5.0, bwManager.getBandwidthAllocation(sender1, receiver2)
				.getAllocatedBandwidth(), 0.1);
		assertEquals(26.6666,
				bwManager.getBandwidthAllocation(sender2, receiver1)
						.getAllocatedBandwidth(), 0.0001);
		assertEquals(6.6666,
				bwManager.getBandwidthAllocation(sender2, receiver3)
						.getAllocatedBandwidth(), 0.0001);
		assertEquals(10.0, bwManager.getBandwidthAllocation(sender2, receiver4)
				.getAllocatedBandwidth(), 0.1);
		assertEquals(36.6666,
				bwManager.getBandwidthAllocation(sender2, receiver5)
						.getAllocatedBandwidth(), 0.0001);

		bwManager.removeConnection(sender0, receiver0, sender0
				.getMaxBandwidth().getUpBW());
		bwManager.removeConnection(sender0, receiver1, sender0
				.getMaxBandwidth().getUpBW());
		bwManager.removeConnection(sender0, receiver3, sender0
				.getMaxBandwidth().getUpBW());
		bwManager.removeConnection(sender1, receiver0, sender1
				.getMaxBandwidth().getUpBW());
		bwManager.removeConnection(sender1, receiver2, sender1
				.getMaxBandwidth().getUpBW());
		bwManager.removeConnection(sender2, receiver1, sender2
				.getMaxBandwidth().getUpBW());
		bwManager.removeConnection(sender2, receiver3, sender2
				.getMaxBandwidth().getUpBW());
		bwManager.removeConnection(sender2, receiver4, sender2
				.getMaxBandwidth().getUpBW());
		bwManager.removeConnection(sender2, receiver5, sender2
				.getMaxBandwidth().getUpBW());

		assertNull(bwManager.getBandwidthAllocation(sender0, receiver0));
		assertNull(bwManager.getBandwidthAllocation(sender0, receiver1));
		assertNull(bwManager.getBandwidthAllocation(sender0, receiver3));
		assertNull(bwManager.getBandwidthAllocation(sender1, receiver0));
		assertNull(bwManager.getBandwidthAllocation(sender1, receiver2));
		assertNull(bwManager.getBandwidthAllocation(sender2, receiver1));
		assertNull(bwManager.getBandwidthAllocation(sender2, receiver3));
		assertNull(bwManager.getBandwidthAllocation(sender2, receiver4));
		assertNull(bwManager.getBandwidthAllocation(sender2, receiver5));
	}

	@Test
	public void test_AllAtOnce_FullAllocation_MultiStream() {
		AllAtOnce_FullAllocation_MultiStream(bandwidthManagerEvent);
		AllAtOnce_FullAllocation_MultiStream(bandwidthManagerPeriodical);
	}

	private void AllAtOnce_FullAllocation_MultiStream(
			AbstractGnpNetBandwidthManager bwManager) {

		sender0 = new TestNetLayer(0, 10, "sender 0");
		sender1 = new TestNetLayer(0, 10, "sender 1");
		sender2 = new TestNetLayer(0, 80, "sender 2");

		receiver0 = new TestNetLayer(40, 0, "receiver 0");
		receiver1 = new TestNetLayer(30, 0, "receiver 1");
		receiver2 = new TestNetLayer(10, 0, "receiver 2");
		receiver3 = new TestNetLayer(10, 0, "receiver 3");
		receiver4 = new TestNetLayer(10, 0, "receiver 4");
		receiver5 = new TestNetLayer(40, 0, "receiver 5");

		assertNull(bwManager.getBandwidthAllocation(sender0, receiver0));
		assertNull(bwManager.getBandwidthAllocation(sender0, receiver1));
		assertNull(bwManager.getBandwidthAllocation(sender0, receiver3));
		assertNull(bwManager.getBandwidthAllocation(sender1, receiver0));
		assertNull(bwManager.getBandwidthAllocation(sender1, receiver2));
		assertNull(bwManager.getBandwidthAllocation(sender2, receiver1));
		assertNull(bwManager.getBandwidthAllocation(sender2, receiver3));
		assertNull(bwManager.getBandwidthAllocation(sender2, receiver4));
		assertNull(bwManager.getBandwidthAllocation(sender2, receiver5));

		// Add First Stream
		bwManager.addConnection(sender0, receiver0, sender0.getMaxBandwidth()
				.getUpBW());
		bwManager.addConnection(sender0, receiver1, sender0.getMaxBandwidth()
				.getUpBW());
		bwManager.addConnection(sender0, receiver3, sender0.getMaxBandwidth()
				.getUpBW());
		bwManager.addConnection(sender1, receiver0, sender1.getMaxBandwidth()
				.getUpBW());
		bwManager.addConnection(sender1, receiver2, sender1.getMaxBandwidth()
				.getUpBW());
		bwManager.addConnection(sender2, receiver1, sender2.getMaxBandwidth()
				.getUpBW());
		bwManager.addConnection(sender2, receiver3, sender2.getMaxBandwidth()
				.getUpBW());
		bwManager.addConnection(sender2, receiver4, sender2.getMaxBandwidth()
				.getUpBW());
		bwManager.addConnection(sender2, receiver5, sender2.getMaxBandwidth()
				.getUpBW());

		// Add Second Stream
		bwManager.addConnection(sender0, receiver0, sender0.getMaxBandwidth()
				.getUpBW());
		bwManager.addConnection(sender0, receiver1, sender0.getMaxBandwidth()
				.getUpBW());
		bwManager.addConnection(sender0, receiver3, sender0.getMaxBandwidth()
				.getUpBW());
		bwManager.addConnection(sender1, receiver0, sender1.getMaxBandwidth()
				.getUpBW());
		bwManager.addConnection(sender1, receiver2, sender1.getMaxBandwidth()
				.getUpBW());
		bwManager.addConnection(sender2, receiver1, sender2.getMaxBandwidth()
				.getUpBW());
		bwManager.addConnection(sender2, receiver3, sender2.getMaxBandwidth()
				.getUpBW());
		bwManager.addConnection(sender2, receiver4, sender2.getMaxBandwidth()
				.getUpBW());
		bwManager.addConnection(sender2, receiver5, sender2.getMaxBandwidth()
				.getUpBW());

		assertNotNull(bwManager.getBandwidthAllocation(sender0, receiver0));
		assertNotNull(bwManager.getBandwidthAllocation(sender0, receiver1));
		assertNotNull(bwManager.getBandwidthAllocation(sender0, receiver3));
		assertNotNull(bwManager.getBandwidthAllocation(sender1, receiver0));
		assertNotNull(bwManager.getBandwidthAllocation(sender1, receiver2));
		assertNotNull(bwManager.getBandwidthAllocation(sender2, receiver1));
		assertNotNull(bwManager.getBandwidthAllocation(sender2, receiver3));
		assertNotNull(bwManager.getBandwidthAllocation(sender2, receiver4));
		assertNotNull(bwManager.getBandwidthAllocation(sender2, receiver5));

		bwManager.allocateBandwidth();
		bwManager.allocateBandwidth();

		assertEquals(3.3333,
				bwManager.getBandwidthAllocation(sender0, receiver0)
						.getAllocatedBandwidth(), 0.0001);
		assertEquals(3.3333,
				bwManager.getBandwidthAllocation(sender0, receiver1)
						.getAllocatedBandwidth(), 0.0001);
		assertEquals(3.3333,
				bwManager.getBandwidthAllocation(sender0, receiver3)
						.getAllocatedBandwidth(), 0.0001);
		assertEquals(5.0, bwManager.getBandwidthAllocation(sender1, receiver0)
				.getAllocatedBandwidth(), 0.1);
		assertEquals(5.0, bwManager.getBandwidthAllocation(sender1, receiver2)
				.getAllocatedBandwidth(), 0.1);
		assertEquals(26.6666,
				bwManager.getBandwidthAllocation(sender2, receiver1)
						.getAllocatedBandwidth(), 0.0001);
		assertEquals(6.6666,
				bwManager.getBandwidthAllocation(sender2, receiver3)
						.getAllocatedBandwidth(), 0.0001);
		assertEquals(10.0, bwManager.getBandwidthAllocation(sender2, receiver4)
				.getAllocatedBandwidth(), 0.1);
		assertEquals(36.6666,
				bwManager.getBandwidthAllocation(sender2, receiver5)
						.getAllocatedBandwidth(), 0.0001);

		// Remove one Stream
		bwManager.removeConnection(sender0, receiver0, sender0
				.getMaxBandwidth().getUpBW());
		bwManager.removeConnection(sender0, receiver1, sender0
				.getMaxBandwidth().getUpBW());
		bwManager.removeConnection(sender0, receiver3, sender0
				.getMaxBandwidth().getUpBW());
		bwManager.removeConnection(sender1, receiver0, sender1
				.getMaxBandwidth().getUpBW());
		bwManager.removeConnection(sender1, receiver2, sender1
				.getMaxBandwidth().getUpBW());
		bwManager.removeConnection(sender2, receiver1, sender2
				.getMaxBandwidth().getUpBW());
		bwManager.removeConnection(sender2, receiver3, sender2
				.getMaxBandwidth().getUpBW());
		bwManager.removeConnection(sender2, receiver4, sender2
				.getMaxBandwidth().getUpBW());
		bwManager.removeConnection(sender2, receiver5, sender2
				.getMaxBandwidth().getUpBW());

		assertNotNull(bwManager.getBandwidthAllocation(sender0, receiver0));
		assertNotNull(bwManager.getBandwidthAllocation(sender0, receiver1));
		assertNotNull(bwManager.getBandwidthAllocation(sender0, receiver3));
		assertNotNull(bwManager.getBandwidthAllocation(sender1, receiver0));
		assertNotNull(bwManager.getBandwidthAllocation(sender1, receiver2));
		assertNotNull(bwManager.getBandwidthAllocation(sender2, receiver1));
		assertNotNull(bwManager.getBandwidthAllocation(sender2, receiver3));
		assertNotNull(bwManager.getBandwidthAllocation(sender2, receiver4));
		assertNotNull(bwManager.getBandwidthAllocation(sender2, receiver5));

		bwManager.allocateBandwidth();
		bwManager.allocateBandwidth();

		assertEquals(3.3333,
				bwManager.getBandwidthAllocation(sender0, receiver0)
						.getAllocatedBandwidth(), 0.0001);
		assertEquals(3.3333,
				bwManager.getBandwidthAllocation(sender0, receiver1)
						.getAllocatedBandwidth(), 0.0001);
		assertEquals(3.3333,
				bwManager.getBandwidthAllocation(sender0, receiver3)
						.getAllocatedBandwidth(), 0.0001);
		assertEquals(5.0, bwManager.getBandwidthAllocation(sender1, receiver0)
				.getAllocatedBandwidth(), 0.1);
		assertEquals(5.0, bwManager.getBandwidthAllocation(sender1, receiver2)
				.getAllocatedBandwidth(), 0.1);
		assertEquals(26.6666,
				bwManager.getBandwidthAllocation(sender2, receiver1)
						.getAllocatedBandwidth(), 0.0001);
		assertEquals(6.6666,
				bwManager.getBandwidthAllocation(sender2, receiver3)
						.getAllocatedBandwidth(), 0.0001);
		assertEquals(10.0, bwManager.getBandwidthAllocation(sender2, receiver4)
				.getAllocatedBandwidth(), 0.1);
		assertEquals(36.6666,
				bwManager.getBandwidthAllocation(sender2, receiver5)
						.getAllocatedBandwidth(), 0.0001);

		// Remove second Stream
		bwManager.removeConnection(sender0, receiver0, sender0
				.getMaxBandwidth().getUpBW());
		bwManager.removeConnection(sender0, receiver1, sender0
				.getMaxBandwidth().getUpBW());
		bwManager.removeConnection(sender0, receiver3, sender0
				.getMaxBandwidth().getUpBW());
		bwManager.removeConnection(sender1, receiver0, sender1
				.getMaxBandwidth().getUpBW());
		bwManager.removeConnection(sender1, receiver2, sender1
				.getMaxBandwidth().getUpBW());
		bwManager.removeConnection(sender2, receiver1, sender2
				.getMaxBandwidth().getUpBW());
		bwManager.removeConnection(sender2, receiver3, sender2
				.getMaxBandwidth().getUpBW());
		bwManager.removeConnection(sender2, receiver4, sender2
				.getMaxBandwidth().getUpBW());
		bwManager.removeConnection(sender2, receiver5, sender2
				.getMaxBandwidth().getUpBW());

		assertNull(bwManager.getBandwidthAllocation(sender0, receiver0));
		assertNull(bwManager.getBandwidthAllocation(sender0, receiver1));
		assertNull(bwManager.getBandwidthAllocation(sender0, receiver3));
		assertNull(bwManager.getBandwidthAllocation(sender1, receiver0));
		assertNull(bwManager.getBandwidthAllocation(sender1, receiver2));
		assertNull(bwManager.getBandwidthAllocation(sender2, receiver1));
		assertNull(bwManager.getBandwidthAllocation(sender2, receiver3));
		assertNull(bwManager.getBandwidthAllocation(sender2, receiver4));
		assertNull(bwManager.getBandwidthAllocation(sender2, receiver5));
	}

	@Test
	public void test_StepByStep_FullAllocation_OneStream() {
		StepByStep_FullAllocation_OneStream(bandwidthManagerPeriodical);
		StepByStep_FullAllocation_OneStream(bandwidthManagerEvent);
	}

	public void StepByStep_FullAllocation_OneStream(
			AbstractGnpNetBandwidthManager bwManager) {

		sender0 = new TestNetLayer(0, 10, "sender 0");
		sender1 = new TestNetLayer(0, 10, "sender 1");
		sender2 = new TestNetLayer(0, 80, "sender 2");

		receiver0 = new TestNetLayer(40, 0, "receiver 0");
		receiver1 = new TestNetLayer(30, 0, "receiver 1");
		receiver2 = new TestNetLayer(10, 0, "receiver 2");
		receiver3 = new TestNetLayer(10, 0, "receiver 3");
		receiver4 = new TestNetLayer(10, 0, "receiver 4");
		receiver5 = new TestNetLayer(40, 0, "receiver 5");

		assertNull(bwManager.getBandwidthAllocation(sender0, receiver0));
		assertNull(bwManager.getBandwidthAllocation(sender0, receiver1));
		assertNull(bwManager.getBandwidthAllocation(sender0, receiver3));
		assertNull(bwManager.getBandwidthAllocation(sender1, receiver0));
		assertNull(bwManager.getBandwidthAllocation(sender1, receiver2));
		assertNull(bwManager.getBandwidthAllocation(sender2, receiver1));
		assertNull(bwManager.getBandwidthAllocation(sender2, receiver3));
		assertNull(bwManager.getBandwidthAllocation(sender2, receiver4));
		assertNull(bwManager.getBandwidthAllocation(sender2, receiver5));

		bwManager.addConnection(sender0, receiver0, sender0.getMaxBandwidth()
				.getUpBW());
		bwManager.allocateBandwidth();

		bwManager.addConnection(sender0, receiver1, sender0.getMaxBandwidth()
				.getUpBW());
		bwManager.allocateBandwidth();

		bwManager.addConnection(sender0, receiver3, sender0.getMaxBandwidth()
				.getUpBW());
		bwManager.allocateBandwidth();

		bwManager.addConnection(sender1, receiver0, sender1.getMaxBandwidth()
				.getUpBW());
		bwManager.allocateBandwidth();

		bwManager.addConnection(sender1, receiver2, sender1.getMaxBandwidth()
				.getUpBW());
		bwManager.allocateBandwidth();

		bwManager.addConnection(sender2, receiver1, sender2.getMaxBandwidth()
				.getUpBW());
		bwManager.allocateBandwidth();

		bwManager.addConnection(sender2, receiver3, sender2.getMaxBandwidth()
				.getUpBW());
		bwManager.allocateBandwidth();

		bwManager.addConnection(sender2, receiver4, sender2.getMaxBandwidth()
				.getUpBW());
		bwManager.allocateBandwidth();

		bwManager.addConnection(sender2, receiver5, sender2.getMaxBandwidth()
				.getUpBW());
		bwManager.allocateBandwidth();

		assertEquals(3.3333,
				bwManager.getBandwidthAllocation(sender0, receiver0)
						.getAllocatedBandwidth(), 0.0001);
		assertEquals(3.3333,
				bwManager.getBandwidthAllocation(sender0, receiver0)
						.getAllocatedBandwidth(), 0.0001);
		assertEquals(3.3333,
				bwManager.getBandwidthAllocation(sender0, receiver1)
						.getAllocatedBandwidth(), 0.0001);
		assertEquals(3.3333,
				bwManager.getBandwidthAllocation(sender0, receiver3)
						.getAllocatedBandwidth(), 0.0001);
		assertEquals(5.0, bwManager.getBandwidthAllocation(sender1, receiver0)
				.getAllocatedBandwidth(), 0.1);
		assertEquals(5.0, bwManager.getBandwidthAllocation(sender1, receiver2)
				.getAllocatedBandwidth(), 0.1);
		assertEquals(26.6666,
				bwManager.getBandwidthAllocation(sender2, receiver1)
						.getAllocatedBandwidth(), 0.0001);
		assertEquals(6.6666,
				bwManager.getBandwidthAllocation(sender2, receiver3)
						.getAllocatedBandwidth(), 0.0001);
		assertEquals(10.0, bwManager.getBandwidthAllocation(sender2, receiver4)
				.getAllocatedBandwidth(), 0.1);
		assertEquals(36.6666,
				bwManager.getBandwidthAllocation(sender2, receiver5)
						.getAllocatedBandwidth(), 0.0001);

		bwManager.removeConnection(sender0, receiver0, sender0
				.getMaxBandwidth().getUpBW());
		bwManager.removeConnection(sender0, receiver1, sender0
				.getMaxBandwidth().getUpBW());
		bwManager.removeConnection(sender0, receiver3, sender0
				.getMaxBandwidth().getUpBW());
		bwManager.removeConnection(sender1, receiver0, sender1
				.getMaxBandwidth().getUpBW());
		bwManager.removeConnection(sender1, receiver2, sender1
				.getMaxBandwidth().getUpBW());
		bwManager.removeConnection(sender2, receiver1, sender2
				.getMaxBandwidth().getUpBW());
		bwManager.removeConnection(sender2, receiver3, sender2
				.getMaxBandwidth().getUpBW());
		bwManager.removeConnection(sender2, receiver4, sender2
				.getMaxBandwidth().getUpBW());
		bwManager.removeConnection(sender2, receiver5, sender2
				.getMaxBandwidth().getUpBW());

		assertNull(bwManager.getBandwidthAllocation(sender0, receiver0));
		assertNull(bwManager.getBandwidthAllocation(sender0, receiver1));
		assertNull(bwManager.getBandwidthAllocation(sender0, receiver3));
		assertNull(bwManager.getBandwidthAllocation(sender1, receiver0));
		assertNull(bwManager.getBandwidthAllocation(sender1, receiver2));
		assertNull(bwManager.getBandwidthAllocation(sender2, receiver1));
		assertNull(bwManager.getBandwidthAllocation(sender2, receiver3));
		assertNull(bwManager.getBandwidthAllocation(sender2, receiver4));
		assertNull(bwManager.getBandwidthAllocation(sender2, receiver5));
	}

	@Test
	public void testAllAtOnce_RestrictedAllocation_MultiStream() {
		allAtOnce_RestrictedAllocation_MultiStream(bandwidthManagerPeriodical);
		allAtOnce_RestrictedAllocation_MultiStream(bandwidthManagerEvent);
	}

	private void allAtOnce_RestrictedAllocation_MultiStream(
			AbstractGnpNetBandwidthManager bwManager) {

		sender0 = new TestNetLayer(0, 10, "sender 0");
		sender1 = new TestNetLayer(0, 10, "sender 1");
		sender2 = new TestNetLayer(0, 80, "sender 2");

		receiver0 = new TestNetLayer(40, 0, "receiver 0");
		receiver1 = new TestNetLayer(30, 0, "receiver 1");
		receiver2 = new TestNetLayer(10, 0, "receiver 2");
		receiver3 = new TestNetLayer(10, 0, "receiver 3");
		receiver4 = new TestNetLayer(10, 0, "receiver 4");
		receiver5 = new TestNetLayer(40, 0, "receiver 5");

		assertNull(bwManager.getBandwidthAllocation(sender0, receiver0));
		assertNull(bwManager.getBandwidthAllocation(sender0, receiver1));
		assertNull(bwManager.getBandwidthAllocation(sender0, receiver3));
		assertNull(bwManager.getBandwidthAllocation(sender1, receiver0));
		assertNull(bwManager.getBandwidthAllocation(sender1, receiver2));
		assertNull(bwManager.getBandwidthAllocation(sender2, receiver1));
		assertNull(bwManager.getBandwidthAllocation(sender2, receiver3));
		assertNull(bwManager.getBandwidthAllocation(sender2, receiver4));
		assertNull(bwManager.getBandwidthAllocation(sender2, receiver5));

		bwManager.addConnection(sender0, receiver0, 2);
		bwManager.addConnection(sender0, receiver1, sender0.getMaxBandwidth()
				.getUpBW());
		bwManager.addConnection(sender0, receiver3, sender0.getMaxBandwidth()
				.getUpBW());
		bwManager.addConnection(sender1, receiver0, 2);
		bwManager.addConnection(sender1, receiver2, sender1.getMaxBandwidth()
				.getUpBW());
		bwManager.addConnection(sender2, receiver1, sender2.getMaxBandwidth()
				.getUpBW());
		bwManager.addConnection(sender2, receiver3, sender2.getMaxBandwidth()
				.getUpBW());
		bwManager.addConnection(sender2, receiver4, sender2.getMaxBandwidth()
				.getUpBW());
		bwManager.addConnection(sender2, receiver5, 11);

		assertNotNull(bwManager.getBandwidthAllocation(sender0, receiver0));
		assertNotNull(bwManager.getBandwidthAllocation(sender0, receiver1));
		assertNotNull(bwManager.getBandwidthAllocation(sender0, receiver3));
		assertNotNull(bwManager.getBandwidthAllocation(sender1, receiver0));
		assertNotNull(bwManager.getBandwidthAllocation(sender1, receiver2));
		assertNotNull(bwManager.getBandwidthAllocation(sender2, receiver1));
		assertNotNull(bwManager.getBandwidthAllocation(sender2, receiver3));
		assertNotNull(bwManager.getBandwidthAllocation(sender2, receiver4));
		assertNotNull(bwManager.getBandwidthAllocation(sender2, receiver5));

		bwManager.allocateBandwidth();
		bwManager.allocateBandwidth();

		assertEquals(2.0, bwManager.getBandwidthAllocation(sender0, receiver0)
				.getAllocatedBandwidth(), 0.1);
		assertEquals(4.0, bwManager.getBandwidthAllocation(sender0, receiver1)
				.getAllocatedBandwidth(), 0.1);
		assertEquals(4.0, bwManager.getBandwidthAllocation(sender0, receiver3)
				.getAllocatedBandwidth(), 0.1);
		assertEquals(2.0, bwManager.getBandwidthAllocation(sender1, receiver0)
				.getAllocatedBandwidth(), 0.1);
		assertEquals(8.0, bwManager.getBandwidthAllocation(sender1, receiver2)
				.getAllocatedBandwidth(), 0.1);
		assertEquals(26.0, bwManager.getBandwidthAllocation(sender2, receiver1)
				.getAllocatedBandwidth(), 0.1);
		assertEquals(6.0, bwManager.getBandwidthAllocation(sender2, receiver3)
				.getAllocatedBandwidth(), 0.1);
		assertEquals(10.0, bwManager.getBandwidthAllocation(sender2, receiver4)
				.getAllocatedBandwidth(), 0.1);
		assertEquals(11.0, bwManager.getBandwidthAllocation(sender2, receiver5)
				.getAllocatedBandwidth(), 0.1);

		bwManager.addConnection(sender0, receiver0, sender0.getMaxBandwidth()
				.getUpBW());
		bwManager.addConnection(sender0, receiver1, sender0.getMaxBandwidth()
				.getUpBW());
		bwManager.addConnection(sender0, receiver3, sender0.getMaxBandwidth()
				.getUpBW());
		bwManager.addConnection(sender1, receiver0, sender1.getMaxBandwidth()
				.getUpBW());
		bwManager.addConnection(sender1, receiver2, sender1.getMaxBandwidth()
				.getUpBW());
		bwManager.addConnection(sender2, receiver1, sender2.getMaxBandwidth()
				.getUpBW());
		bwManager.addConnection(sender2, receiver3, sender2.getMaxBandwidth()
				.getUpBW());
		bwManager.addConnection(sender2, receiver4, sender2.getMaxBandwidth()
				.getUpBW());
		bwManager.addConnection(sender2, receiver5, sender2.getMaxBandwidth()
				.getUpBW());

		bwManager.allocateBandwidth();
		bwManager.allocateBandwidth();
		bwManager.allocateBandwidth();
		bwManager.allocateBandwidth();

		assertEquals(3.3333,
				bwManager.getBandwidthAllocation(sender0, receiver0)
						.getAllocatedBandwidth(), 0.0001);
		assertEquals(3.3333,
				bwManager.getBandwidthAllocation(sender0, receiver1)
						.getAllocatedBandwidth(), 0.0001);
		assertEquals(3.3333,
				bwManager.getBandwidthAllocation(sender0, receiver3)
						.getAllocatedBandwidth(), 0.0001);
		assertEquals(5.0, bwManager.getBandwidthAllocation(sender1, receiver0)
				.getAllocatedBandwidth(), 0.1);
		assertEquals(5.0, bwManager.getBandwidthAllocation(sender1, receiver2)
				.getAllocatedBandwidth(), 0.1);
		assertEquals(26.6666,
				bwManager.getBandwidthAllocation(sender2, receiver1)
						.getAllocatedBandwidth(), 0.0001);
		assertEquals(6.6666,
				bwManager.getBandwidthAllocation(sender2, receiver3)
						.getAllocatedBandwidth(), 0.0001);
		assertEquals(10.0, bwManager.getBandwidthAllocation(sender2, receiver4)
				.getAllocatedBandwidth(), 0.1);
		assertEquals(36.6666,
				bwManager.getBandwidthAllocation(sender2, receiver5)
						.getAllocatedBandwidth(), 0.0001);

		bwManager.removeConnection(sender0, receiver0, sender0
				.getMaxBandwidth().getUpBW());
		bwManager.removeConnection(sender0, receiver1, sender0
				.getMaxBandwidth().getUpBW());
		bwManager.removeConnection(sender0, receiver3, sender0
				.getMaxBandwidth().getUpBW());
		bwManager.removeConnection(sender1, receiver0, sender1
				.getMaxBandwidth().getUpBW());
		bwManager.removeConnection(sender1, receiver2, sender1
				.getMaxBandwidth().getUpBW());
		bwManager.removeConnection(sender2, receiver1, sender2
				.getMaxBandwidth().getUpBW());
		bwManager.removeConnection(sender2, receiver3, sender2
				.getMaxBandwidth().getUpBW());
		bwManager.removeConnection(sender2, receiver4, sender2
				.getMaxBandwidth().getUpBW());
		bwManager.removeConnection(sender2, receiver5, sender2
				.getMaxBandwidth().getUpBW());

		bwManager.allocateBandwidth();
		bwManager.allocateBandwidth();

		assertEquals(2.0, bwManager.getBandwidthAllocation(sender0, receiver0)
				.getAllocatedBandwidth(), 0.1);
		assertEquals(4.0, bwManager.getBandwidthAllocation(sender0, receiver1)
				.getAllocatedBandwidth(), 0.1);
		assertEquals(4.0, bwManager.getBandwidthAllocation(sender0, receiver3)
				.getAllocatedBandwidth(), 0.1);
		assertEquals(2.0, bwManager.getBandwidthAllocation(sender1, receiver0)
				.getAllocatedBandwidth(), 0.1);
		assertEquals(8.0, bwManager.getBandwidthAllocation(sender1, receiver2)
				.getAllocatedBandwidth(), 0.1);
		assertEquals(26.0, bwManager.getBandwidthAllocation(sender2, receiver1)
				.getAllocatedBandwidth(), 0.1);
		assertEquals(6.0, bwManager.getBandwidthAllocation(sender2, receiver3)
				.getAllocatedBandwidth(), 0.1);
		assertEquals(10.0, bwManager.getBandwidthAllocation(sender2, receiver4)
				.getAllocatedBandwidth(), 0.1);
		assertEquals(11.0, bwManager.getBandwidthAllocation(sender2, receiver5)
				.getAllocatedBandwidth(), 0.1);

		bwManager.addConnection(sender0, receiver0, sender0.getMaxBandwidth()
				.getUpBW());
		bwManager.addConnection(sender0, receiver1, sender0.getMaxBandwidth()
				.getUpBW());
		bwManager.addConnection(sender0, receiver3, sender0.getMaxBandwidth()
				.getUpBW());
		bwManager.addConnection(sender1, receiver0, sender1.getMaxBandwidth()
				.getUpBW());
		bwManager.addConnection(sender1, receiver2, sender1.getMaxBandwidth()
				.getUpBW());
		bwManager.addConnection(sender2, receiver1, sender2.getMaxBandwidth()
				.getUpBW());
		bwManager.addConnection(sender2, receiver3, sender2.getMaxBandwidth()
				.getUpBW());
		bwManager.addConnection(sender2, receiver4, sender2.getMaxBandwidth()
				.getUpBW());
		bwManager.addConnection(sender2, receiver5, sender2.getMaxBandwidth()
				.getUpBW());

		bwManager.removeConnection(sender0, receiver0, 2);
		bwManager.removeConnection(sender0, receiver1, sender0
				.getMaxBandwidth().getUpBW());
		bwManager.removeConnection(sender0, receiver3, sender0
				.getMaxBandwidth().getUpBW());
		bwManager.removeConnection(sender1, receiver0, 2);
		bwManager.removeConnection(sender1, receiver2, sender1
				.getMaxBandwidth().getUpBW());
		bwManager.removeConnection(sender2, receiver1, sender2
				.getMaxBandwidth().getUpBW());
		bwManager.removeConnection(sender2, receiver3, sender2
				.getMaxBandwidth().getUpBW());
		bwManager.removeConnection(sender2, receiver4, sender2
				.getMaxBandwidth().getUpBW());
		bwManager.removeConnection(sender2, receiver5, 11);

		bwManager.allocateBandwidth();
		bwManager.allocateBandwidth();

		assertEquals(3.3333,
				bwManager.getBandwidthAllocation(sender0, receiver0)
						.getAllocatedBandwidth(), 0.0001);
		assertEquals(3.3333,
				bwManager.getBandwidthAllocation(sender0, receiver1)
						.getAllocatedBandwidth(), 0.0001);
		assertEquals(3.3333,
				bwManager.getBandwidthAllocation(sender0, receiver3)
						.getAllocatedBandwidth(), 0.0001);
		assertEquals(5.0, bwManager.getBandwidthAllocation(sender1, receiver0)
				.getAllocatedBandwidth(), 0.1);
		assertEquals(5.0, bwManager.getBandwidthAllocation(sender1, receiver2)
				.getAllocatedBandwidth(), 0.1);
		assertEquals(26.6666,
				bwManager.getBandwidthAllocation(sender2, receiver1)
						.getAllocatedBandwidth(), 0.0001);
		assertEquals(6.6666,
				bwManager.getBandwidthAllocation(sender2, receiver3)
						.getAllocatedBandwidth(), 0.0001);
		assertEquals(10.0, bwManager.getBandwidthAllocation(sender2, receiver4)
				.getAllocatedBandwidth(), 0.1);
		assertEquals(36.6666,
				bwManager.getBandwidthAllocation(sender2, receiver5)
						.getAllocatedBandwidth(), 0.0001);

		bwManager.removeConnection(sender0, receiver0, sender0
				.getMaxBandwidth().getUpBW());
		bwManager.removeConnection(sender0, receiver1, sender0
				.getMaxBandwidth().getUpBW());
		bwManager.removeConnection(sender0, receiver3, sender0
				.getMaxBandwidth().getUpBW());
		bwManager.removeConnection(sender1, receiver0, sender1
				.getMaxBandwidth().getUpBW());
		bwManager.removeConnection(sender1, receiver2, sender1
				.getMaxBandwidth().getUpBW());
		bwManager.removeConnection(sender2, receiver1, sender2
				.getMaxBandwidth().getUpBW());
		bwManager.removeConnection(sender2, receiver3, sender2
				.getMaxBandwidth().getUpBW());
		bwManager.removeConnection(sender2, receiver4, sender2
				.getMaxBandwidth().getUpBW());
		bwManager.removeConnection(sender2, receiver5, sender2
				.getMaxBandwidth().getUpBW());

		assertNull(bwManager.getBandwidthAllocation(sender0, receiver0));
		assertNull(bwManager.getBandwidthAllocation(sender0, receiver1));
		assertNull(bwManager.getBandwidthAllocation(sender0, receiver3));
		assertNull(bwManager.getBandwidthAllocation(sender1, receiver0));
		assertNull(bwManager.getBandwidthAllocation(sender1, receiver2));
		assertNull(bwManager.getBandwidthAllocation(sender2, receiver1));
		assertNull(bwManager.getBandwidthAllocation(sender2, receiver3));
		assertNull(bwManager.getBandwidthAllocation(sender2, receiver4));
		assertNull(bwManager.getBandwidthAllocation(sender2, receiver5));
	}

	public void printInfo(AbstractGnpNetBandwidthManager bwManager) {
		log.info("bw 0->0 :"
				+ bwManager.getBandwidthAllocation(sender0, receiver0)
						.getAllocatedBandwidth());
		log.info("bw 0->1 :"
				+ bwManager.getBandwidthAllocation(sender0, receiver1)
						.getAllocatedBandwidth());
		log.info("bw 0->3 :"
				+ bwManager.getBandwidthAllocation(sender0, receiver3)
						.getAllocatedBandwidth());
		log.info("bw 1->0 :"
				+ bwManager.getBandwidthAllocation(sender1, receiver0)
						.getAllocatedBandwidth());
		log.info("bw 1->2 :"
				+ bwManager.getBandwidthAllocation(sender1, receiver2)
						.getAllocatedBandwidth());
		log.info("bw 2->1 :"
				+ bwManager.getBandwidthAllocation(sender2, receiver1)
						.getAllocatedBandwidth());
		log.info("bw 2->3 :"
				+ bwManager.getBandwidthAllocation(sender2, receiver3)
						.getAllocatedBandwidth());
		log.info("bw 2->4 :"
				+ bwManager.getBandwidthAllocation(sender2, receiver4)
						.getAllocatedBandwidth());
		log.info("bw 2->5 :"
				+ bwManager.getBandwidthAllocation(sender2, receiver5)
						.getAllocatedBandwidth());

	}

	public static class TestNetLayer extends AbstractNetLayer {

		String name;

		public TestNetLayer(double maxDownBandwidth, double maxUpBandwidth,
				String name) {
			super(new Bandwidth(maxDownBandwidth, maxUpBandwidth), null, null);
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}

		@Override
		protected boolean isSupported(TransProtocol protocol) {
			return true;
		}

		@Override
		public void cancelTransmission(int commId) {
			// TODO Auto-generated method stub

		}

		@Override
		public void send(Message msg, NetID receiver, NetProtocol protocol) {
			return;
		}

	}
}
