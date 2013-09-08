/*
 * Copyright (c) 2012-2013 Open Source Community - <http://www.peerfact.org>
 * Copyright (c) 2011-2012 University of Paderborn - UPB
 * Copyright (c) 2005-2011 KOM - Multimedia Communications Lab
 *
 * This file is part of PeerfactSim.KOM.
 * 
 * PeerfactSim.KOM is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 * 
 * PeerfactSim.KOM is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with PeerfactSim.KOM.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.peerfact.impl.simengine;

import java.util.LinkedHashMap;

import org.apache.log4j.Logger;
import org.peerfact.api.scenario.Configurable;
import org.peerfact.api.simengine.EventQueue;
import org.peerfact.api.simengine.SimulationEventHandler;
import org.peerfact.impl.simengine.queues.Calendar;
import org.peerfact.impl.util.logging.SimLogger;


/**
 * The Scheduler enables the insertion of the active components into the
 * simulator that can generate events. It ensures the correct execution of those
 * generated events in order to provide valid experiments. The duration of each
 * experiment is controlled by the scheduler and the parameters defined by the
 * Application.
 * 
 * @author Sebastian Kaune <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 */
public class Scheduler implements Configurable, SimulationEventHandler {

	final static Logger log = SimLogger.getLogger(Scheduler.class);

	private long processedEventCounter;

	private long endTime;

	private long currentTime;

	private long statusInterval = Simulator.MINUTE_UNIT * 2;

	private final EventQueue eventQueue;

	private final boolean statusEvent;

	private boolean processEvents = true;

	private boolean realTime;

	private double timeSkew;

	private LinkedHashMap<String, Integer> statistics = new LinkedHashMap<String, Integer>();

	/**
	 * Constructs a new scheduler instance using a calendar queue. If desired,
	 * status events about the progress of the simulation will be plotted.
	 * 
	 * @param statusEvent
	 *            the flag which speficies if status events will be plotted
	 */
	public Scheduler(boolean statusEvent) {
		this.eventQueue = new Calendar();
		this.endTime = -1;
		this.processedEventCounter = 0;
		this.currentTime = 0;
		this.statusEvent = statusEvent;
	}

	private void statistics_eventAdded(Object event) {
		if (event == null) {
			return;
		}
		Integer i = statistics.get(event.getClass().getCanonicalName());
		if (i == null) {
			i = 1;
		} else {
			i++;
		}
		statistics.put(event.getClass().getCanonicalName(), i);
	}

	private void statistics_eventRemoved(Object event) {
		if (event == null) {
			return;
		}
		Integer i = statistics.get(event.getClass().getCanonicalName());
		if (i == null) {
			// this should not happen...
		} else {
			i--;
			if (i == 0) {
				statistics.remove(event.getClass().getCanonicalName());
			} else {
				statistics.put(event.getClass().getCanonicalName(), i);
			}
		}
	}

	private void statistics_print() {
		log.info("ES: Eventqueue Statistics:");
		for (String s : statistics.keySet()) {
			Integer i = statistics.get(s);
			log.info("ES: " + i + "x " + s);
		}
	}

	/**
	 * Inserts new event in event queue
	 * 
	 * @param content
	 *            the content of the event
	 * @param simulationTime
	 *            time to schedule the event
	 * @param handler
	 *            handler which will receive this event
	 * @param eventType
	 *            see constants in SchedulerEvent class for possible values
	 */
	public void scheduleEvent(Object content, long simulationTime,
			SimulationEventHandler handler, SimulationEvent.Type eventType) {
		assert currentTime <= simulationTime : "event " + content
				+ " has time " + simulationTime;
		if (content != null) {
			statistics_eventAdded(content);
		}
		SchedulerEventImpl event = new SchedulerEventImpl(eventType, content,
				simulationTime, handler);
		log.debug("Schedule event " + content + " @ " + simulationTime);
		this.eventQueue.insert(event);

	}

	/**
	 * Starts the scheduler and begins processing the events
	 */
	public void start() {
		if (this.endTime == -1) {
			throw new IllegalStateException("No end time configured");
		}

		if (this.statusEvent) {
			this.scheduleEvent(null, statusInterval, this,
					SimulationEvent.Type.STATUS);
		}

		while (this.eventQueue.more()) {
			if (!processNextEvent()) {
				break;
			}
		}

		log.info("Simulated realtime: " + this.getSimulatedRealtime()
				+ " - End of simulation");
		log.info("Scheduler processed in total " + this.processedEventCounter
				+ " events with " + this.eventQueue.size()
				+ " unprocessed events still in queue");
	}

	/**
	 * Sets the end time at which the simulation framework will finish at the
	 * latest the simulation , irrespective if there are still unprocessed
	 * events in the event queue.
	 * 
	 * @param endTime
	 *            point in time at which the simular will finish at the latest
	 */
	void setFinishAt(long endTime) {
		if (endTime < 0) {
			throw new IllegalArgumentException("Negative end time");
		}
		this.endTime = endTime;
		// this is a small hack to assure that all other events are processed
		// before
		this.scheduleEvent(null, endTime + 1, this,
				SimulationEvent.Type.END_SIMULATION);

	}

	/**
	 * Process the next event from the event queue.
	 * 
	 * @return whether an event was processed
	 */
	synchronized private boolean processNextEvent() {
		if (!processEvents) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		processedEventCounter++;
		SchedulerEventImpl event = (SchedulerEventImpl) this.eventQueue
				.remove();

		statistics_eventRemoved(event.getData());

		Long timeToWait = currentTime;
		currentTime = event.getSimulationTime();
		timeToWait = currentTime - timeToWait;

		log.debug("process next event @ " + currentTime);
		if (event.getType() == SimulationEvent.Type.END_SIMULATION) {
			currentTime--;// Here, we hide that the end event is scheduled at
			// endTime+1
			return false;
		} else {

			/*
			 * code for slowing down the simulator to realtime timeskew variable
			 * indicates how much faster the realtime the simulation should run
			 */
			if (realTime && timeToWait > 0 && timeSkew > 0) {
				log.debug("Scheduler sleeping: " + timeToWait / 1000
						+ " milliseconds");
				try {
					Thread.sleep((long) ((timeToWait / 1000) / timeSkew));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			SimulationEventHandler handler = event.handler;
			log.debug("next event " + event.getData());
			handler.eventOccurred(event);
			return true;
		}
	}

	/**
	 * Return whether event queue is empty.
	 * 
	 * @return whether event queue is empty.
	 */
	public boolean isEmpty() {
		return this.eventQueue.empty();
	}

	/**
	 * Returns the current time of the scheduler
	 * 
	 * @return current scheduler time
	 */
	public long getCurrentTime() {
		return currentTime;
	}

	/**
	 * Returns the end time of the scheduler
	 * 
	 * @return
	 */
	public long getEndTime() {
		return endTime;
	}

	private static long getSimMilliSeconds(long time) {
		return Math.round(Math
				.floor((double) time / Simulator.MILLISECOND_UNIT));
	}

	private static long getSimSeconds(long time) {
		return Math
				.round(Math.floor(Scheduler.getSimMilliSeconds(time) / 1000d));
	}

	private static long getSimMinutes(long time) {
		return Math.round(Math.floor(Scheduler.getSimSeconds(time) / 60d));
	}

	private static long getHours(long time) {
		return Math.round(Math.floor(Scheduler.getSimMinutes(time) / 60d));
	}

	protected String getSimulatedRealtime() {
		return getFormattedTime(getCurrentTime());
	}

	@Override
	public void eventOccurred(SimulationEvent se) {
		if (se.getType().equals(SimulationEvent.Type.STATUS)) {
			if (eventQueue.size() != 1) {
				this.scheduleEvent(null, statusInterval + this.currentTime,
						this, SimulationEvent.Type.STATUS);
			}
			log.info("Simulated realtime: " + getSimulatedRealtime());
			statistics_print();
		}
	}

	/**
	 * Method for JUnit tests in order to verify the correctness
	 * 
	 * @return number of events in event queue.
	 */
	public int getEventQueueSize() {
		return this.eventQueue.size();
	}

	private static final class SchedulerEventImpl extends SimulationEvent
			implements Comparable<SchedulerEventImpl> {

		/**
		 * 
		 */
		private static final long serialVersionUID = -9001051619728245180L;

		final SimulationEventHandler handler;

		SchedulerEventImpl(SimulationEvent.Type type, Object eventData,
				long simTime, SimulationEventHandler handler) {
			super(type, eventData, simTime, Simulator.getInstance());
			this.handler = handler;
		}

		@Override
		public int compareTo(SchedulerEventImpl o) {
			return Double.compare(this.simTime, o.simTime);
		}

	}

	static String getFormattedTime(long time) {
		return getHours(time) + ":" + getSimMinutes(time) % 60 + ":"
				+ getSimSeconds(time) % 60 + ":" + getSimMilliSeconds(time)
				% 1000 + " (H:m:s:ms)";
	}

	void setStatusInterval(long statusInterval) {
		this.statusInterval = statusInterval;
	}

	/**
	 * a flag for slowing down the simulation down to real time. This flag only
	 * makes sense for simulations which run faster than real time
	 * 
	 * @param realTime
	 *            flag for switching the scheduler to real time mode
	 */
	public void setRealTime(boolean realTime) {
		this.realTime = realTime;
	}

	/**
	 * method for setting the time skew. A time skew of 100 means, that the
	 * simulation runs 100 times faster than real time
	 * 
	 * @param timeSkew
	 *            the time skew
	 */
	public void setTimeSkew(double timeSkew) {
		this.timeSkew = timeSkew;
	}

	public void pause() {
		processEvents = false;
	}

	synchronized public void unpause() {
		processEvents = true;
		notifyAll();
	}
}
