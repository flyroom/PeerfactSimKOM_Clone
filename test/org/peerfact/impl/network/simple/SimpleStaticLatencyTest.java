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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.peerfact.api.network.Bandwidth;
import org.peerfact.impl.network.simple.SimpleEuclidianPoint;
import org.peerfact.impl.network.simple.SimpleNetLayer;
import org.peerfact.impl.network.simple.SimpleStaticLatencyModel;
import org.peerfact.impl.network.simple.SimpleSubnet;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.simengine.SimulatorTest;


// TODO: merge with SimpleStaticLatencyTest
public class SimpleStaticLatencyTest extends SimulatorTest {

	private static final double diffY = 0.1d;

	private SimpleNetLayer net1;

	private SimpleNetLayer net2;

	@Override
	@Before
	public void setUp() {
		super.setUp();
		double y = 0;
		SimpleSubnet subNet = new SimpleSubnet();
		this.net1 = new SimpleNetLayer(subNet, null, new SimpleEuclidianPoint(
				0, y), new Bandwidth(0d, 0d));
		this.net2 = new SimpleNetLayer(subNet, null, new SimpleEuclidianPoint(
				0, y + diffY), new Bandwidth(0d, 0d));

	}

	@Test
	public void testDistance() {
		assertEquals(diffY,
				net1.getNetPosition().getDistance(net2.getNetPosition()), 0.1);
	}

	@Test
	public void testStaticModel() {
		long latencyFactor = 10;
		SimpleStaticLatencyModel lm = new SimpleStaticLatencyModel(
				latencyFactor);
		assertEquals(diffY, SimpleStaticLatencyModel.getDistance(net1, net2),
				0.1);
		long expectedLatency = Simulator.MILLISECOND_UNIT
				* (long) (diffY * latencyFactor);
		assertEquals(expectedLatency, lm.getLatency(net1, net2));
		assertEquals(expectedLatency, lm.getLatency(net2, net1));
	}

	@Override
	@After
	public void tearDown() {
		super.tearDown();
	}
}
