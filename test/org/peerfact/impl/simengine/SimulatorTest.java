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

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.peerfact.api.common.Host;
import org.peerfact.api.scenario.Scenario;
import org.peerfact.api.simengine.SimulationEventHandler;
import org.peerfact.impl.simengine.SimulationEvent;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.util.logging.SimLogger;


/**
 * Super class for simulator tests providing some basic functionality for
 * testing. It should be subclassed by (almost) all JUnit tests. TODO extend and
 * use in more test cases
 * 
 * @author pussep
 * @version 0.1, 26.11.2007
 * 
 */
public abstract class SimulatorTest extends Assert {
	protected final static Logger log = SimLogger
			.getLogger(SimulatorTest.class);

	/**
	 * Prepare everything required for a test here by overriding this method.
	 * (not in the test constructor!). Don't forget to call
	 * <code>super.setUp();</code>
	 * 
	 */
	@Before
	public void setUp() {
		Simulator.getInstance().setScenario(new Scenario() {
			@Override
			public void prepare() {
				// do nothing
			}

			@Override
			public int createActions(String id, String timeInterval,
					String methodName, String[] params) {
				// no actions created
				return 0;
			}

			@Override
			public void setHosts(Map<String, List<Host>> allHosts) {
				// do nothing
			}

			@Override
			public Map<String, List<Host>> getHosts() {
				// do nothing
				return null;
			}
		});
	}

	/**
	 * Flush all memory-consuming or otherwise disturbing stuff in this method
	 * by overriding it. Don't forget to call <code>super.tearDown();</code>
	 * 
	 */
	@After
	public void tearDown() {
		Simulator.getInstance().reset();
	}

	/**
	 * Run Simulation for the given time. Same as continue simulation but
	 * terminates at least when the virtual <code>endTime</code> elapse.
	 * 
	 * @param endTime
	 */
	protected static final void runSimulation(long endTime) {
		if (endTime > 0)
		{
			Simulator.getInstance();
			Simulator.getScheduler().setFinishAt(endTime); // emergency
		}
		// stop
		Simulator.getInstance().start(false);
		// scheduler.start();
		assertTrue(Simulator.isFinishedWithoutError());
	}

	/**
	 * Schedule an event (Java freaks would call it implementation sugar).
	 * 
	 * @param simulationTime
	 *            time to schedule at
	 * @param handler
	 *            receiver of the event to be schedules
	 */
	public static final void scheduleEvent(long simulationTime,
			SimulationEventHandler handler) {
		Simulator.scheduleEvent(new Object(), simulationTime, handler,
				SimulationEvent.Type.TEST_EVENT);
	}

	/**
	 * Convenience method.
	 * 
	 * @return current simulation time
	 */
	public static long getCurrentTime() {
		return Simulator.getCurrentTime();
	}

	/**
	 * Convenience method.
	 * 
	 * @param number
	 * @return number*milliseconds_unit
	 */
	public static long milliseconds(int number) {
		return number * Simulator.MILLISECOND_UNIT;
	}

	/**
	 * Convenience method.
	 * 
	 * @param number
	 * @return number*seconds_unit
	 */
	public static long seconds(int number) {
		return number * Simulator.SECOND_UNIT;
	}
}
