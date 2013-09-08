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

package org.peerfact.impl.simengine.queues;

import org.apache.log4j.Logger;
import org.peerfact.api.simengine.EventQueue;
import org.peerfact.impl.simengine.SimulationEvent;
import org.peerfact.impl.util.logging.SimLogger;


//DOCUMENT
/**
 * 
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public class Calendar implements EventQueue {

	final static Logger log = SimLogger.getLogger(Calendar.class);

	// public static Calendar singleton;

	/**
	 * Initial size of internal heap array.
	 */
	public static final int HEAP_LENGTH = 32;

	/**
	 * Initial size of internal calendar array.
	 */
	public static final int CAL_LENGTH = 10;

	/**
	 * Initial size of internal calendar array.
	 */
	public static final long CAL_TIME = 4000;

	/**
	 * Calendar days.
	 */
	private Heap[] bins;

	/**
	 * Event queue size.
	 */
	private int size;

	/**
	 * Width of a single bin (length of a day).
	 */
	private long width;

	/**
	 * Maximum time threshold of last bin.
	 */
	private long max;

	/**
	 * Last "day" where event was found.
	 */
	private int last;

	/**
	 * Create calendar event scheduler with given number of bins and total width
	 * of all bins (length of a year).
	 */
	public Calendar() {
		int bins1 = Calendar.CAL_LENGTH;
		long time = Calendar.CAL_TIME;
		if (time < bins1) {
			throw new IllegalArgumentException("error: year < days (" + time
					+ " < " + bins1 + ")");
		}
		this.init(time, bins1);
	}

	/**
	 * Initialize calendar event scheduler with given number of bins and total
	 * width of all bins (length of a year).
	 * 
	 * @param time
	 *            total width of all bins (length of a year).
	 * @param bins1
	 *            number of bins (number of days in a year).
	 */
	private void init(long time, int bins1) {
		this.bins = new Heap[bins1];
		for (int index = 0; index < this.bins.length; index++) {
			this.bins[index] = new Heap(Calendar.HEAP_LENGTH);
		}
		this.size = 0;
		this.width = time / bins1;
		this.max = this.width;
		this.last = 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reset() {
		// super.reset();
		if (this.size != 0) {
			log.warn("reset scheduler with " + this.size
					+ " events still in queue");
		}
		int bins1 = Calendar.CAL_LENGTH;
		long time = Calendar.CAL_TIME;
		init(time, bins1);
		// if (this.size != 0) {
		// throw new RuntimeException("error: not empty but "+this.size);
		// }
		// this.max = this.width;
		// this.last = 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int size() {
		return this.size;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean more() {
		return this.size > 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean empty() {
		return this.size == 0;
	}

	/**
	 * Scan for next bin with an event within the next calendar year, or use
	 * minimum (below) to find bin with minimum event using direct search.
	 * 
	 * @return mapped index of bin with next event.
	 */
	private int next() {
		int index = this.last;
		do {
			Heap bin = this.bins[index];
			if (!bin.empty() && (bin.peek().getSimulationTime() < this.max)) {
				return this.last = index;
			}
			index = (index + 1) % this.bins.length;
			this.max += this.width;
		} while (index != this.last);
		this.last = this.min();
		this.max = this.bins[this.last].peek().getSimulationTime();
		this.max = this.max - (this.max % this.width) + this.width;
		return this.last;
	}

	/**
	 * Return bin with minimum time event (linear scan), if there is at least on
	 * remaining event.
	 * 
	 * @return bin with minimum time event.
	 */
	private int min() {
		int min = -1;
		long time = Long.MAX_VALUE;
		for (int index = 0; index < this.bins.length; index++) {
			Heap bin = this.bins[index];
			if (!bin.empty()
					&& ((bin.peek().getSimulationTime() < time) || (min == -1))) {
				time = bin.peek().getSimulationTime();
				min = index;
			}
		}
		return min;
	}

	/**
	 * Return mapped bin index for a given time.
	 * 
	 * @param time
	 *            event time.
	 * @return mapped bin index.
	 */
	private int get(long time) {
		return (int) ((time / this.width) % this.bins.length);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void insert(SimulationEvent event) {
		this.size++;
		this.bins[this.get(event.getSimulationTime())].insert(event);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SimulationEvent remove() {
		this.size--;
		return this.bins[this.next()].remove();
	}

	/**
	 * {@inheritDoc}
	 */
	public SimulationEvent peek() {
		return this.bins[this.next()].peek();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("cal[");
		builder.append(this.bins.length);
		builder.append(",");
		builder.append(this.width);
		builder.append("]");
		if (this.size != 0) {
			builder.append(" ");
			builder.append(this.size);
			builder.append("\n");
			for (int index = 0; index < this.bins.length; index++) {
				builder.append("\t");
				builder.append(this.bins[index]);
				builder.append("\n");
			}
		}
		return builder.toString();
	}

	@Override
	public boolean remove(SimulationEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

}
