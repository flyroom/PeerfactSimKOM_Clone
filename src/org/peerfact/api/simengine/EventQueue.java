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

package org.peerfact.api.simengine;

import org.peerfact.impl.simengine.SimulationEvent;

/**
 * Event scheduling is done by keeping an ordered list of future
 * SimulationEvents waiting to happen. As each simulation event contains the
 * time at which it should occur, the so called event queue has to ensure that
 * events can be fetched in the correct order.
 * 
 * @author Sebastian Kaune <peerfact@kom.tu-darmstadt.de>
 * @author Konstantin Pussep
 * @version 3.0, 11/29/2007
 * 
 */
public interface EventQueue {

	/**
	 * Insert event into event queue.
	 * 
	 * @param event
	 *            event to insert.
	 */
	public void insert(SimulationEvent event);

	/**
	 * Remove first/next event from event queue enabling concurrent handling.
	 * 
	 * @return first event (removed) from event queue.
	 */
	public SimulationEvent remove();

	/**
	 * Remove given event from event queue.
	 * 
	 * @param event
	 *            event to insert.
	 * @return whether event was removed from event queue.
	 */
	public boolean remove(SimulationEvent event);

	/**
	 * Resets the event queue and remove all events from it.
	 */
	public void reset();

	/**
	 * Return whether the event queue has more events for concurrent execution.
	 * 
	 * @return whether event queue has more events for concurrent execution.
	 */
	public boolean more();

	/**
	 * Return whether event queue is empty.
	 * 
	 * @return whether event queue is empty.
	 */
	public boolean empty();

	/**
	 * Return size of event queue.
	 * 
	 * @return number of events in event queue.
	 */
	public int size();

}
