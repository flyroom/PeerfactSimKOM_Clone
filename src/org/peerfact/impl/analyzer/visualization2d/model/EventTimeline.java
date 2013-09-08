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

package org.peerfact.impl.analyzer.visualization2d.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.SortedMap;
import java.util.TreeMap;

import org.peerfact.impl.analyzer.visualization2d.model.events.EdgeFlashing;
import org.peerfact.impl.analyzer.visualization2d.model.events.Event;
import org.peerfact.impl.simengine.Simulator;


/**
 * Event time line. Transfers all events, makes them happen for their time and
 * undo. Is controlled by the player, but can also be controlled arbitrarily
 * manually.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class EventTimeline implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5906861906836376909L;

	public static ArrayList<TimelineEventListener> listeners = new ArrayList<TimelineEventListener>();

	private long maxTime;

	/**
	 * current virtual time
	 */
	private long actTime;

	protected TreeMap<Long, ArrayList<Event>> timeline = new TreeMap<Long, ArrayList<Event>>();

	/**
	 * EventTimeline with maximum time 0
	 */
	public EventTimeline() {
		this(0);
	}

	/**
	 * EventTimeline with maximum time maxTime
	 * 
	 * @param maxTime
	 */
	public EventTimeline(int maxTime) {
		this.maxTime = maxTime;
	}

	public long getActualTime() {
		return this.actTime;
	}

	/**
	 * Returns all the events in a long array that occur from <b>begin</b>
	 * (inclusive) to <b>end</b> (exclusive).
	 * 
	 * @param begin
	 * @param end
	 * @return
	 */
	public ArrayList<Event> getEventsBetween(long begin, long end) {

		synchronized (this) {

			ArrayList<Event> result = new ArrayList<Event>();

			for (ArrayList<Event> l : this.getMappedEventsBetween(begin, end)
					.values()) {
				for (Event e : l) {
					result.add(e);
				}
			}

			return result;

		}
	}

	/**
	 * Returns all the events that occur from <b>begin</b> (inclusive) to
	 * <b>end</b> (exclusive).
	 * 
	 * @param begin
	 * @param end
	 * @return
	 */
	public SortedMap<Long, ArrayList<Event>> getMappedEventsBetween(long begin,
			long end) {

		return timeline.subMap(begin, end);
	}

	/**
	 * Jumps at a time
	 * 
	 * @param time
	 */
	public synchronized void jumpToTime(long newTime) {

		synchronized (this) {

			if (newTime == actTime) {
				return;
			}

			long tempNewTime = newTime;

			if (tempNewTime > maxTime) {
				tempNewTime = maxTime;
			} else if (tempNewTime < 0) {
				tempNewTime = 0;
			}

			boolean reverse = tempNewTime < actTime;

			long stepWide = Math.abs(tempNewTime - actTime);

			SortedMap<Long, ArrayList<Event>> betwElem;
			if (!reverse) {
				betwElem = timeline.subMap(actTime + 1, tempNewTime + 1);
			} else {
				betwElem = timeline.subMap(tempNewTime + 1, actTime + 1);
			}

			if (!reverse) {
				for (ArrayList<Event> events : betwElem.values()) {

					for (Event e : events) {
						/*
						 * Leave out flash events since they tend to overload
						 * the visualization upon larger jumps on the time-line
						 */
						if (!(e instanceof EdgeFlashing)) {
							e.makeHappen();
						} else if (stepWide < Simulator.SECOND_UNIT * 10) {
							/*
							 * make flashing edges only happen if the step wide
							 * is smaller than 10 seconds.
							 */
							e.makeHappen();
						}
					}
				}
			} else {

				/*
				 * Iterate backwards is difficult and with effort, as the leaves
				 * are not backward chained in TreeSet.
				 */
				ArrayList<ArrayList<Event>> it_list = new ArrayList<ArrayList<Event>>(
						betwElem.values());

				ListIterator<ArrayList<Event>> it = it_list
						.listIterator(it_list.size());
				while (it.hasPrevious()) {

					ArrayList<Event> events = it.previous();

					ListIterator<Event> it2 = events
							.listIterator(events.size());
					while (it2.hasPrevious()) {
						Event e = it2.previous();

						e.undoMakeHappen();
					}
				}
			}

			this.actTime = tempNewTime;
		}

		notifyActualTimeChanged(this);

		VisDataModel.needsRefresh();
	}

	/**
	 * Sets an event e at time t.
	 * 
	 * @param e
	 * @param t
	 */
	public void insertEvent(Event e, long t) {

		synchronized (this) {

			ArrayList<Event> actual_tl = timeline.get(t);

			if (actual_tl == null) {

				ArrayList<Event> te = new ArrayList<Event>();
				te.add(e);

				timeline.put(t, te);

			} else {
				actual_tl.add(e);
			}
			if (t > maxTime) {
				maxTime = t;
				notifyMaxTimeChanged(this);
			}

		}

	}

	/**
	 * Not finished yet!
	 * 
	 * @param e
	 */
	public void removeEvent(Event e) {
		// TODO
	}

	public long getMaxTime() {
		return maxTime;
	}

	public void setMaxTime(int maxTime) {
		this.maxTime = maxTime;
	}

	@Override
	public String toString() {
		return this.timeline.toString();
	}

	public void reset() {
		this.actTime = -1;
		notifyActualTimeChanged(this);
	}

	public static void addEventListener(TimelineEventListener l) {
		listeners.add(l);
	}

	protected static void notifyActualTimeChanged(EventTimeline tl) {
		for (TimelineEventListener l : listeners) {
			l.actualTimeChanged(tl);
		}
	}

	protected static void notifyMaxTimeChanged(EventTimeline tl) {
		for (TimelineEventListener l : listeners) {
			l.maxTimeChanged(tl);
		}
	}

}
