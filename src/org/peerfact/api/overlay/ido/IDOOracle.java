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

import java.util.List;

import org.peerfact.api.overlay.OverlayID;


/**
 * The IDOOracle is an interface to evaluate the recall and precision of an IDO.
 * For that, it must be build a view of the virtual world with all positions
 * from all nodes. This view is used, to determine all nodes, that one node
 * should be known.
 * 
 * <br>
 * This interface should be implemented for every IDO.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 01/06/2011
 */
public interface IDOOracle {
	/**
	 * Insert all nodeInfos for all nodes of the Overlay.
	 * 
	 * @param nodeInfos
	 *            All nodeInfos of all nodes, that are in the overlay.
	 */
	public void insertNodeInfos(List<IDONodeInfo> nodeInfos);

	/**
	 * Reset the oracle to the initial situation. After this, it can reused for
	 * a new view of the virtual world.
	 */
	public void reset();

	/**
	 * Get all neighbor nodes for the node with the given id. The neighbors are
	 * all nodes, that are must known, with the given aoi.
	 * 
	 * @param id
	 *            An id of a node in the overlay, for that are determine the
	 *            neighbors.
	 * @param aoi
	 *            The AOI radius of the node.
	 * @return A list of nodeInfos
	 */
	public List<IDONodeInfo> getAllNeighbors(OverlayID<?> id, int aoi);
}
