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

package org.peerfact.impl.overlay.informationdissemination;

import java.awt.Point;
import java.math.BigInteger;

import org.peerfact.api.overlay.OverlayID;
import org.peerfact.api.overlay.ido.IDONodeInfo;


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
 * A basic implementation of a {@link IDONodeInfo}. It provides a container of
 * information for a node.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 01/06/2011
 */
public class NodeInfo implements IDONodeInfo {

	/**
	 * The position of a node
	 */
	protected Point position;

	/**
	 * The area of interest of a node
	 */
	protected int aoi;

	/**
	 * The id of a node.
	 */
	protected OverlayID<BigInteger> id;

	/**
	 * Constructor of this class. It sets the given values.
	 * 
	 * @param position
	 *            The position of the node.
	 * @param aoi
	 *            The area of interest of the node.
	 * @param id
	 *            The ID of the node.
	 */
	public NodeInfo(Point position, int aoi, OverlayID<BigInteger> id) {
		this.position = position;
		this.aoi = aoi;
		this.id = id;
	}

	@Override
	public Point getPosition() {
		return position;
	}

	@Override
	public int getAoiRadius() {
		return aoi;
	}

	@Override
	public OverlayID<?> getID() {
		return id;
	}

}
