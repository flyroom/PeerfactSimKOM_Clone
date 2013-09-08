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

package org.peerfact.impl.analyzer.visualization2d.analyzer.netPosTransformers;

import org.peerfact.impl.analyzer.visualization2d.util.visualgraph.Coords;
import org.peerfact.impl.network.modular.st.positioning.GNPPositioning.GNPPosition;

/**
 * 
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public class NewGnpPositionTransformer implements
		INetPositionTransformer<GNPPosition> {

	@Override
	public Coords transform(GNPPosition netPos) {

		if (netPos.getCoords().size() < 2) {
			return new Coords(0, 0);
		}

		float x = (netPos.getCoords().get(0)).floatValue();
		float y = (netPos.getCoords().get(1)).floatValue();

		Coords coords = new Coords(x, y);

		return coords;
	}
}
