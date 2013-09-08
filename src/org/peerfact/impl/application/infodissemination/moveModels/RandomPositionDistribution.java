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

package org.peerfact.impl.application.infodissemination.moveModels;

import java.awt.Point;

import org.apache.commons.math.random.RandomGenerator;
import org.peerfact.impl.simengine.Simulator;


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
 * This position distribution generates random positions, limited by the
 * dimensions of the world.
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * @version 01/06/2011
 * 
 */
public class RandomPositionDistribution implements IPositionDistribution {
	public int worldDimensionX;

	public int worldDimensionY;

	public RandomPositionDistribution() {
		this(200, 200);
	}

	public RandomPositionDistribution(int worldDimensionX, int worldDimensionY) {
		this.worldDimensionX = worldDimensionX;
		this.worldDimensionY = worldDimensionY;
	}

	@Override
	public Point getNextPosition() {

		RandomGenerator r = Simulator.getRandom();

		Point p = new Point(r.nextInt(worldDimensionX),
				r.nextInt(worldDimensionY));

		return p;
	}

	public void setWorldDimensionX(int worldDimensionX) {
		this.worldDimensionX = worldDimensionX;
	}

	public void setWorldDimensionY(int worldDimensionY) {
		this.worldDimensionY = worldDimensionY;
	}

}
