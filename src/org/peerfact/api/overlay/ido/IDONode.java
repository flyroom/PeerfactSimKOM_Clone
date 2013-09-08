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

package org.peerfact.api.overlay.ido;

import java.awt.Point;
import java.util.List;

import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.api.overlay.OverlayID;
import org.peerfact.api.overlay.OverlayNode;
import org.peerfact.impl.overlay.AbstractOverlayNode.PeerStatus;


//TODO: public void disseminateGameEvent(GameEvent ge);

/**
 * This interface provides a common API for Nodes in an Information
 * Dissemination Overlays (IDO).
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 01/20/2011
 * 
 */
public interface IDONode<T extends OverlayID<?>, S extends OverlayContact<T>>
		extends OverlayNode<T, S> {
	/**
	 * Node leave the overlay.
	 * 
	 * @param crash
	 *            If <code>true</code> then, the node goes offline without to
	 *            execute a routine. Otherwise it can execute a routine.
	 */
	public void leave(boolean crash);

	/**
	 * Join with the given position to the overlay.
	 * 
	 * @param position
	 *            The position on the map, where the node join.
	 */
	public void join(Point position);

	/**
	 * Disseminate the position to the nodes, that are interested of this
	 * information. Additionally sets the position for this node.
	 * 
	 * @param position
	 *            The position, which should be disseminated
	 */
	public void disseminatePosition(Point position);

	/**
	 * Gets the actually position of the node in the virtual world.
	 * 
	 * @return The position of the node in the virtual world.
	 */
	public Point getPosition();

	/**
	 * Sets the AOI radius for this node
	 * 
	 * @param aoi
	 *            the AOI radius
	 */
	public void setAOI(int aoi);

	/**
	 * Gets the AOI radius for this node
	 * 
	 * @return The AOI radius
	 */
	public int getAOI();

	/**
	 * Gets information about the current status of the overlay of the peer.
	 * 
	 * Note: This does not give any information about the connectivity status of
	 * the network layer.
	 * 
	 * @return the current overlay status of the peer.
	 */
	public PeerStatus getPeerStatus();

	/**
	 * Gets a list of nodes back, that the node knows. Thats are nodes, which
	 * are in the AOI and Nodes, which are used for the connectivity of the
	 * overlay.
	 * 
	 * @return A list of {@link IDONodeInfo}.
	 */
	public List<IDONodeInfo> getNeighborsNodeInfo();
}
