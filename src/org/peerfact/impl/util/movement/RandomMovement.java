/*
 * Copyright (c) 2012-2013 Open Source Community - <http://www.peerfact.org>
 * Copyright (c) 2011-2012 University of Paderborn - UPB
 * Copyright (c) 2005-2011 KOM - Multimedia Communications Lab
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.peerfact.impl.util.movement;

import java.util.Set;

import org.apache.commons.math.random.RandomGenerator;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.util.positioning.PositionVector;


/**
 * Mainly for testing: random movement
 * 
 * @author Bjoern Richerzhagen, adapted from ido.MoveModels
 * @version 1.0, mm/dd/2011
 */
public class RandomMovement extends MovementModel {

	public RandomMovement() {
		super();
	}

	@Override
	public void move() {
		Set<MovementSupported> comps = getComponents();
		for (MovementSupported comp : comps) {
			if (!comp.movementActive()) {
				continue;
			}
			PositionVector pos = comp.getPosition();
			pos.add(getNextRandomDelta(pos));
			comp.positionChanged();
		}
	}

	public PositionVector getNextRandomDelta(PositionVector oldPosition) {
		RandomGenerator r = Simulator.getRandom();
		double[] delta = new double[oldPosition.getDimensions()];
		for (int i = 0; i < oldPosition.getDimensions(); i++) {
			do {
				delta[i] = (r.nextInt(2 * getMoveSpeedLimit() + 1) - getMoveSpeedLimit());
			} while (oldPosition.getEntry(i) + delta[i] > getWorldDimension(i)
					|| oldPosition.getEntry(i) + delta[i] < 0);
		}
		PositionVector deltaVector = new PositionVector(delta);
		return deltaVector;
	}

}
