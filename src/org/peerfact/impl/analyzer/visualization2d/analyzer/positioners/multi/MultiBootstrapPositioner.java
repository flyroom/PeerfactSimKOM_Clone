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

package org.peerfact.impl.analyzer.visualization2d.analyzer.positioners.multi;

import java.util.Iterator;
import java.util.LinkedHashMap;

import org.peerfact.api.common.Host;
import org.peerfact.api.overlay.OverlayNode;
import org.peerfact.impl.analyzer.visualization2d.analyzer.OverlayAdapter;
import org.peerfact.impl.analyzer.visualization2d.analyzer.positioners.MultiPositioner;
import org.peerfact.impl.analyzer.visualization2d.analyzer.positioners.SchematicPositioner;
import org.peerfact.impl.analyzer.visualization2d.util.visualgraph.Coords;


/**
 * Positioned nodes (schematic coordinates) in separate groups in a grid.
 * 
 * All nodes belong to a group if they use the same instance of a
 * BootstrapManagers.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * @version 3.0, 03.11.2008
 * 
 */
public class MultiBootstrapPositioner extends MultiPositioner {

	static final int DEFAULT_COLUMNS = 4;

	float offset_x;

	float offset_y;

	int columns = DEFAULT_COLUMNS;

	/**
	 * The coordinates of the ring center point for each bootstrap manager that
	 * has a node group.
	 */
	LinkedHashMap<Object, Coords> groupPositions = new LinkedHashMap<Object, Coords>();

	LinkedHashMap<Object, SchematicPositioner> positioners = new LinkedHashMap<Object, SchematicPositioner>();

	int actual_column = -1;

	int actual_row = 0;

	/**
	 * Sets the fields of the MultiBootstrapPositioner.
	 * 
	 * Is called by the XML config.
	 * 
	 * @param fields
	 */
	public void setFields(int fields) {
		this.columns = (int) Math.ceil(Math.sqrt(fields - 1));

		offset_x = 0.0f;
		offset_y = 1.0f - 1f / columns;
	}

	@Override
	public Coords getSchematicHostPosition(Host host) {
		Iterator<OverlayNode<?, ?>> it = host.getOverlays();

		while (it.hasNext()) {
			OverlayNode<?, ?> subNetNode = it.next();
			if (getSubNetAdapter().isDedicatedOverlayImplFor(
					subNetNode.getClass())) {
				return getSchematicHostPosition(host, subNetNode);
			}
		}
		return null;
	}

	/**
	 * Returns the schematic position of host for a given overlay
	 * implementation.
	 * 
	 * @param host
	 * @param node
	 * @return schematic position of host
	 */
	public Coords getSchematicHostPosition(Host host, OverlayNode<?, ?> node) {

		Coords startPos = getGroupStartPos(node);

		Coords subPos = positioners.get(
				getSubNetAdapter().getBootstrapManagerFor(node))
				.getSchematicHostPosition(host, node);

		if (subPos == null) {
			return null;
		}

		float x = subPos.x / columns;
		float y = subPos.y / columns;

		return new Coords(startPos.x + x, startPos.y + y);
	}

	protected Coords getGroupStartPos(OverlayNode<?, ?> node) {
		Object boostrap_manager = getSubNetAdapter().getBootstrapManagerFor(
				node);
		Coords position = groupPositions.get(boostrap_manager);

		if (position != null) {
			return position;
		}
		Coords new_position = createNewGroupPos();
		groupPositions.put(boostrap_manager, new_position);
		positioners
				.put(boostrap_manager, getSubNetAdapter().getNewPositioner());

		return new_position;

	}

	/**
	 * Returns the adapter on the subnetwork (the subnetworks)
	 * 
	 * @return
	 */
	private OverlayAdapter getSubNetAdapter() {
		return this.getAdapterAt(0);
	}

	private Coords createNewGroupPos() {
		actual_column++;
		if (actual_column >= columns) {
			actual_row++;
			actual_column = 0;
		}

		return new Coords(offset_x + (float) actual_column / (float) columns,
				offset_y - (float) actual_row / (float) columns);
	}

}
