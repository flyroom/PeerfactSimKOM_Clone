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

package org.peerfact.impl.analyzer.visualization2d.model.flashevents;

import java.util.ConcurrentModificationException;
import java.util.LinkedList;
import java.util.Queue;

import org.peerfact.impl.analyzer.visualization2d.model.ModelFilter;
import org.peerfact.impl.analyzer.visualization2d.model.ModelIterator;


/**
 * Treated FlashEvents, e.g. iterates over them.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * @version 3.0, 21.10.2008
 * 
 */
public class FlashEventHandler {

	Queue<FlashEvent> notPainted = new LinkedList<FlashEvent>();

	Queue<FlashEvent> painted_events = new LinkedList<FlashEvent>();

	private ModelFilter filter = null;

	public void addFlashEvent(FlashEvent e) {
		notPainted.add(e);
	}

	/**
	 * Resets all the entries.
	 */
	public void reset() {
		notPainted.clear();
		painted_events.clear();
	}

	/**
	 * Sets all objects as shown and then iterates over them. This has the
	 * advantage that the iterating thread does not gets in the way of
	 * addFlashEvent() calls and throws a ConcurrentModificationException.
	 * 
	 * @see ConcurrentModificationException
	 * @param it
	 */
	public void iterateAndSetPainted(ModelIterator<?, ?, ?> it) {

		synchronized (painted_events) {
			painted_events.clear();
		}
		synchronized (notPainted) {
			while (!notPainted.isEmpty()) {
				FlashEvent e = null;
				if (!notPainted.isEmpty()) {
					e = notPainted.remove();
				}

				if (e != null && (filter == null || filter.typeActivated(e))) {
					e.iterate(it);
					synchronized (painted_events) {
						painted_events.add(e);
					}
				}
			}
		}
	}

	/**
	 * Iterates <code>it</code> over all the events which are currently drawn on
	 * the screen.
	 * 
	 * @param it
	 */
	public void iteratePaintedEvents(ModelIterator<?, ?, ?> it) {
		synchronized (painted_events) {
			for (FlashEvent e : painted_events) {
				if (filter == null || filter.typeActivated(e)) {
					e.iterate(it);
				}
			}
		}
	}

	public void setEventsPainted() {
		synchronized (painted_events) {
			painted_events = notPainted;
		}
		notPainted = new LinkedList<FlashEvent>();
	}

	public void setFilter(ModelFilter filter) {
		this.filter = filter;
	}

}
