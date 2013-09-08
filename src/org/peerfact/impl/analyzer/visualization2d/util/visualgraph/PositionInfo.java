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

package org.peerfact.impl.analyzer.visualization2d.util.visualgraph;

import java.io.Serializable;

/**
 * Position information, which are objects in the visualization of importance.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * @version 3.0, 03.11.2008
 * 
 */
public class PositionInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3828228790124277563L;

	Coords topo_coords;

	Coords schem_coords;

	/**
	 * Constructor with no schematic coordinates
	 * 
	 * @param topo_coords
	 */
	public PositionInfo(Coords topo_coords) {
		this.topo_coords = topo_coords;
		this.schem_coords = null;
	}

	/**
	 * Default constructor.
	 * 
	 * @param topo_coords
	 * @param schem_coords
	 */
	public PositionInfo(Coords topo_coords, Coords schem_coords) {
		this.topo_coords = topo_coords;
		this.schem_coords = schem_coords;
	}

	/**
	 * Returns the topological coordinates
	 * 
	 * @return
	 */
	public Coords getTopoCoords() {
		return topo_coords;
	}

	/**
	 * Sets the topological coordinates
	 * 
	 * @param topo_coords
	 */
	public void setTopoCoords(Coords topo_coords) {
		this.topo_coords = topo_coords;
	}

	/**
	 * Returns the coordinates of a schematic node. If non-existent, the
	 * topological coordinates.
	 * 
	 * @return
	 */
	public Coords getSchemCoords() {
		if (schem_coords == null) {
			return topo_coords;
		}
		return schem_coords;
	}

	/**
	 * Sets the schematic coordinate
	 * 
	 * @param schem_coords
	 */
	public void setSchemCoords(Coords schem_coords) {
		this.schem_coords = schem_coords;
	}

}
