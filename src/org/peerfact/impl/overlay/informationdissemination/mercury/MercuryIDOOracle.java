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

package org.peerfact.impl.overlay.informationdissemination.mercury;

import java.awt.Point;
import java.util.List;
import java.util.Vector;

import org.peerfact.api.overlay.OverlayID;
import org.peerfact.api.overlay.ido.IDONodeInfo;
import org.peerfact.api.overlay.ido.IDOOracle;


/**
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * This part of the Simulator is not maintained in the current version of
 * PeerfactSim.KOM. There is no intention of the authors to fix this
 * circumstances, since the changes needed are huge compared to overall benefit.
 * 
 * If you want it to work correctly, you are free to make the specific changes
 * and provide it to the community.
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! !!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * 
 * This class determine the optimal solution for the Mercury network. It uses
 * global information to determine the nodes, that should be known by a peer.
 * <p>
 * 
 * In other solutions of this interface, the data structure of the system is
 * used, for the calculation. For this system is this not practicable, because
 * it is a publish subscribe solution.<br>
 * This class gets a lot of nodeInfos and store this. For every call of
 * <code>getAllNeighbors</code>, it will be determined the subset of nodes, that
 * must known by the node. For this, it will be determine a cuboid around this
 * node with the size of the 2*AOI. All nodes that are in this cuboid, are
 * neighbors of this node.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 01/20/2011
 */
public class MercuryIDOOracle implements IDOOracle {

	/**
	 * Storage, to store the global knowledge
	 */
	private List<IDONodeInfo> storage;

	public MercuryIDOOracle() {
		this.storage = new Vector<IDONodeInfo>();
	}

	@Override
	public void insertNodeInfos(List<IDONodeInfo> nodeInfos) {
		this.storage.addAll(nodeInfos);
	}

	@Override
	public void reset() {
		this.storage = new Vector<IDONodeInfo>();
	}

	@Override
	public List<IDONodeInfo> getAllNeighbors(OverlayID<?> id, int aoi) {
		IDONodeInfo nodeInfoCenter = getNodeInfo(id);
		List<IDONodeInfo> result = new Vector<IDONodeInfo>();
		int lowerBoundX = nodeInfoCenter.getPosition().x - aoi;
		int upperBoundX = nodeInfoCenter.getPosition().x + aoi;
		int lowerBoundY = nodeInfoCenter.getPosition().y - aoi;
		int upperBoundY = nodeInfoCenter.getPosition().y + aoi;

		for (IDONodeInfo info : storage) {
			if (isInCuboid(lowerBoundX, upperBoundX, lowerBoundY, upperBoundY,
					info.getPosition())) {
				result.add(info);
			}
		}
		return result;
	}

	/**
	 * Check the given test point, it is in the cuboid. For this need the method
	 * the lower and upper bound of X and Y.
	 * 
	 * @param lowerBoundX
	 *            lower bound of X
	 * @param upperBoundX
	 *            upper bound of X
	 * @param lowerBoundY
	 *            lower bound of Y
	 * @param upperBoundY
	 *            upper bound of Y
	 * @param testPoint
	 *            The point, for that is to test, whether in the cuboid.
	 * @return <code>true</code> if the point in the defined cuboid, otherwise
	 *         <code>false</code>.
	 */
	private static boolean isInCuboid(int lowerBoundX, int upperBoundX,
			int lowerBoundY, int upperBoundY, Point testPoint) {
		if (lowerBoundX <= testPoint.x && testPoint.x <= upperBoundX) {
			if (lowerBoundY <= testPoint.y && testPoint.y <= upperBoundY) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Gets the nodeInfo with the given ID.
	 * 
	 * @param id
	 *            The id to the searched nodeInfo
	 * @return NodeInfo with the given ID.
	 */
	private IDONodeInfo getNodeInfo(OverlayID<?> id) {
		for (IDONodeInfo info : storage) {
			if (info.getID().equals(id)) {
				return info;
			}
		}
		return null;
	}

}
