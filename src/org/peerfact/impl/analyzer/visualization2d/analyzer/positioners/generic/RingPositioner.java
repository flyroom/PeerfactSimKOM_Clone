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

package org.peerfact.impl.analyzer.visualization2d.analyzer.positioners.generic;

import org.peerfact.api.common.Host;
import org.peerfact.api.overlay.OverlayNode;
import org.peerfact.impl.analyzer.visualization2d.analyzer.positioners.SchematicPositioner;
import org.peerfact.impl.analyzer.visualization2d.util.visualgraph.Coords;

/**
 * A positioner, which stores its nodes on a ring.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * @version 3.0, 15.11.2008
 * 
 */
public abstract class RingPositioner implements SchematicPositioner {

	public static final float RING_RADIUS = 0.4f;

	public static final float offset_x = 0.5f;

	public static final float offset_y = 0.5f;

	@Override
	public Coords getSchematicHostPosition(Host host, OverlayNode<?, ?> nd) {

		double div = getPositionOnRing(nd);

		float x = RING_RADIUS * (float) Math.sin(2 * Math.PI * div);
		float y = RING_RADIUS * (float) Math.cos(2 * Math.PI * div);

		return new Coords(offset_x + x, offset_y + y);

	}

	/**
	 * Returns the position on the ring. Here, 0 = 1 = top of the ring, and as
	 * 0.5 at the bottom, rightmost 0:25 and leftmost 0.75
	 * 
	 * @param nd
	 * @return
	 */
	protected abstract double getPositionOnRing(OverlayNode<?, ?> nd);

}
