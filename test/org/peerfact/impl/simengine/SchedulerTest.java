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

package org.peerfact.impl.simengine;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.peerfact.api.simengine.SimulationEventHandler;
import org.peerfact.impl.simengine.Scheduler;
import org.peerfact.impl.simengine.SimulationEvent;
import org.peerfact.impl.simengine.Simulator;


// DOCUMENT
public class SchedulerTest extends SimulatorTest {
	Scheduler scheduler;

	List<Object> receivedData;

	@Override
	public void setUp() {
		// don't call super.setUp() as we want to use our own Scheduler
		scheduler = new Scheduler(false);
		receivedData = new LinkedList<Object>();
	}

	@Test
	public void testScheduleWithoutStatusEvents() {
		assertTrue(scheduler.isEmpty());
		scheduler.setFinishAt(30 * Simulator.MILLISECOND_UNIT);
		assertTrue(scheduler.getEventQueueSize() == 1);
		assertEquals(0, receivedData.size());
		scheduler.scheduleEvent("TestEvent",
				20 * Simulator.MILLISECOND_UNIT, new MyHandler(),
				SimulationEvent.Type.MESSAGE_RECEIVED);
		assertTrue(scheduler.getEventQueueSize() == 2);
		scheduler.start();
		assertTrue(scheduler.isEmpty());
		assertEquals("TestEvent", receivedData.get(0));
		assertEquals(30 * Simulator.MILLISECOND_UNIT,
				scheduler.getCurrentTime());
	}

	@Test
	public void testWithoutScheduledEvents() {
		assertTrue(scheduler.isEmpty());
		scheduler.setFinishAt(30 * Simulator.MILLISECOND_UNIT);
		assertTrue(scheduler.getEventQueueSize() == 1);
		scheduler.start();
		assertTrue(scheduler.isEmpty());
		assertEquals(0, receivedData.size());
		assertEquals(30l * Simulator.MILLISECOND_UNIT,
				scheduler.getCurrentTime());
	}

	@Test
	public void testCorrectOrder() {
		assertTrue(scheduler.isEmpty());
		scheduler.setFinishAt(50 * Simulator.HOUR_UNIT);
		assertTrue(scheduler.getEventQueueSize() == 1);
		assertEquals(0, receivedData.size());

		scheduler.scheduleEvent("TestEvent1",
				20 * Simulator.MILLISECOND_UNIT, new MyHandler(),
				SimulationEvent.Type.MESSAGE_RECEIVED);
		scheduler.scheduleEvent("TestEvent2",
				40 * Simulator.MILLISECOND_UNIT, new MyHandler(),
				SimulationEvent.Type.MESSAGE_RECEIVED);
		scheduler.scheduleEvent("TestEvent3",
				30 * Simulator.MILLISECOND_UNIT, new MyHandler(),
				SimulationEvent.Type.MESSAGE_RECEIVED);
		scheduler.scheduleEvent("TestEvent4",
				3 * Simulator.MINUTE_UNIT, new MyHandler(),
				SimulationEvent.Type.MESSAGE_RECEIVED);
		scheduler.scheduleEvent("TestEvent5",
				40 * Simulator.HOUR_UNIT, new MyHandler(),
				SimulationEvent.Type.MESSAGE_RECEIVED);
		scheduler.scheduleEvent("TestEvent6",
				30 * Simulator.MINUTE_UNIT, new MyHandler(),
				SimulationEvent.Type.MESSAGE_RECEIVED);
		scheduler.scheduleEvent("TestEvent7",
				40 * Simulator.MINUTE_UNIT, new MyHandler(),
				SimulationEvent.Type.MESSAGE_RECEIVED);
		scheduler.scheduleEvent("TestEvent9",
				1 * Simulator.MILLISECOND_UNIT, new MyHandler(),
				SimulationEvent.Type.MESSAGE_RECEIVED);
		scheduler.scheduleEvent("TestEvent10",
				2 * Simulator.MILLISECOND_UNIT, new MyHandler(),
				SimulationEvent.Type.MESSAGE_RECEIVED);

		assertFalse(scheduler.isEmpty());

		scheduler.start();

		assertTrue(scheduler.isEmpty());
		assertEquals("TestEvent9", receivedData.get(0));
		assertEquals("TestEvent10", receivedData.get(1));
		assertEquals("TestEvent1", receivedData.get(2));
		assertEquals("TestEvent3", receivedData.get(3));
		assertEquals("TestEvent2", receivedData.get(4));
		assertEquals("TestEvent4", receivedData.get(5));
		assertEquals("TestEvent6", receivedData.get(6));
		assertEquals("TestEvent7", receivedData.get(7));
		assertEquals("TestEvent5", receivedData.get(8));

		assertEquals(50l * Simulator.HOUR_UNIT, scheduler.getCurrentTime());
	}

	@Test
	public void testFinishAt() {
		assertTrue(scheduler.isEmpty());
		scheduler.setFinishAt(10 * Simulator.MILLISECOND_UNIT);
		assertTrue(scheduler.getEventQueueSize() == 1);
		scheduler.scheduleEvent("TestEvent",
				20 * Simulator.MILLISECOND_UNIT, new MyHandler(),
				SimulationEvent.Type.SCENARIO_ACTION);
		assertFalse(scheduler.isEmpty());
		scheduler.start();
		assertEquals(10l * Simulator.MILLISECOND_UNIT,
				scheduler.getCurrentTime());
		assertTrue(scheduler.getEventQueueSize() == 1);
	}

	@Test
	public void testWithStatusEvents() {
		scheduler = new Scheduler(true);
		assertTrue(scheduler.isEmpty());
		scheduler.setFinishAt(120 * Simulator.MINUTE_UNIT);
		assertTrue(scheduler.getEventQueueSize() == 1);
		assertEquals(0, receivedData.size());

		scheduler.scheduleEvent("TestEvent1",
				20 * Simulator.MINUTE_UNIT, new MyHandler(),
				SimulationEvent.Type.MESSAGE_RECEIVED);
		scheduler.scheduleEvent("TestEvent2",
				105 * Simulator.MINUTE_UNIT, new MyHandler(),
				SimulationEvent.Type.MESSAGE_RECEIVED);
		scheduler.scheduleEvent("TestEvent3",
				45 * Simulator.MINUTE_UNIT, new MyHandler(),
				SimulationEvent.Type.MESSAGE_RECEIVED);

		assertTrue(scheduler.getEventQueueSize() == 4);

		scheduler.start();

		assertTrue(scheduler.isEmpty());
		assertEquals("TestEvent1", receivedData.get(0));
		assertEquals("TestEvent3", receivedData.get(1));
		assertEquals("TestEvent2", receivedData.get(2));
		assertEquals(120l * Simulator.MINUTE_UNIT, scheduler.getCurrentTime());

	}

	@Override
	public void tearDown() {
		super.tearDown();
		receivedData.clear();
	}

	class MyHandler implements SimulationEventHandler {

		@Override
		public void eventOccurred(SimulationEvent se) {
			receivedData.add(se.getData());
		}

	}

}
