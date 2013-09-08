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
package org.peerfact.impl.util.positioning;

import org.apache.commons.math.random.RandomGenerator;
import org.peerfact.impl.simengine.Simulator;


/**
 * Generates random 2D or 3D PositionVectors
 * 
 * @author Bjoern Richerzhagen
 * @version 1.0, 04/26/2011
 */
public class RandomPositionDistribution extends PositionDistribution {

	private int[] worldDimensions;

	public RandomPositionDistribution() {
		worldDimensions = new int[] { 800, 600, 100 };
	}

	@Override
	public PositionVector getNextPosition() {
		RandomGenerator r = Simulator.getRandom();
		double[] vec = new double[getDimensions()];
		for (int i = 0; i < getDimensions(); i++) {
			vec[i] = r.nextInt(worldDimensions[i]);
		}
		PositionVector position = new PositionVector(vec);
		return position;
	}

	public void setWorldDimensionX(int dimension) {
		this.worldDimensions[0] = dimension;
	}

	public void setWorldDimensionY(int dimension) {
		this.worldDimensions[1] = dimension;
	}

	public void setWorldDimensionZ(int dimension) {
		this.worldDimensions[2] = dimension;
	}

}
