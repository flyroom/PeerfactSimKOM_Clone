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

import org.peerfact.api.common.SupportOperations;

/**
 * The <code>OverlayNode</code> interface is the root interface of all specific
 * OverlayNodes. In general, an OverlayNode represents an instance of a
 * participant in the overlay. Note that one or more OverlayNodes may be hosted
 * by a single <NetID>. Participating OverlayNodes are assigned uniform random
 * <code>OverlayID</code>s from a large identifier space. Application-specific
 * objects (documents etc.) are assigned unique identifiers called
 * <code>OverlayKey</code>s, selected from the same identifier space.
 * OverlayNodes can perform several actions like joining, leaving or execute
 * particular operations depending whether the overlay is structured,
 * unstructured or hybrid.
 * 
 * @author Sebastian Kaune <kaune@kom.tu-darmstadt.de>
 * @version 1.0, 11/25/2007
 */
public interface OverlayNode<T extends OverlayID<?>, S extends OverlayContact<T>>
		extends SupportOperations {

	/**
	 * Returns the OverlayID of the OverlayNode
	 * 
	 * @return OverlayID the unique random identifier
	 */
	public T getOverlayID();

	/**
	 * Each OverlayNode is listening for incoming messages on a specific port.
	 * Invoking this method returns the aforementioned port.
	 * 
	 * @return the port on which to listen for incoming messages.
	 */
	public short getPort();

	/**
	 * @return a component that always returns the currently known neighbors of
	 *         this overlay instance. May return null if the overlay does not
	 *         support it to return neighbors to any other component.
	 */
	public NeighborDeterminator<S> getNeighbors();

	/**
	 * Is the node online and "connected" with the overlay?
	 * <p>
	 * Attention: With "connected" is mean, that the node think, it is connected
	 * with the overlay.
	 * 
	 * @return <code>true</code> if the node think, it is connected with an
	 *         overlay, otherwise <code>false</code>.
	 */
	public boolean isPresent();

}
