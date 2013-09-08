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

package org.peerfact.api.overlay;

/**
 * IDGenerators are used to keep track that each generated OverlayID is unique
 * within a simulation scenario. To clarify it, it should not happen that two
 * different OverlayAgents are assigned the same OverlayIDs.
 * 
 * @author Konstantin Pussep <peerfact@kom.tu-darmstadt.de>
 * @author Sebastian Kaune
 * @version 3.0, 10.12.2007
 * 
 * @param <T>
 *            The exact type of the overlay id, that should be overlay specific,
 *            e.g. PastryOverlayID
 */
public interface OverlayIDGenerator<T extends OverlayID<?>> {

	/**
	 * This ID should be new and unused.
	 * 
	 * @return unused NetID
	 */
	public T generateNewID();

	/**
	 * Create id from its string representation.
	 * 
	 * @param idString
	 *            string representation of ID, overlay dependent
	 * @return overlay id
	 */
	public T generateNewID(String idString);

	/**
	 * Sets the size of the interval in which OverlayIds can be generated [0 -
	 * (size-1)]
	 * 
	 * @param size
	 *            the size of the id interval
	 */
	public void setIdSpaceSize(int size);

}
