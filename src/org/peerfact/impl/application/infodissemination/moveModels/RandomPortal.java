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

import org.peerfact.impl.application.infodissemination.IDOApplication;
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
 * This class provides a random portal. With a certain probability will be
 * executed a portal at any position to any position on the map
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 05/04/2011
 */
public class RandomPortal implements IPortalComponent {

	/**
	 * The probability for the execution of a portal
	 */
	private double probability;

	@Override
	public Point portal(Point actuallyPos, IDOApplication app,
			int worldDimensionX, int worldDimensionY) {
		if (probability != 0
				&& Simulator.getRandom().nextDouble() <= probability) {
			// for speed =0
			app.setCurrentMoveVector(0, 0);
			return new Point(Simulator.getRandom().nextInt(worldDimensionX),
					Simulator.getRandom().nextInt(worldDimensionY));
		}

		return null;
	}

	@Override
	public void setProbability(double probability) {
		if (probability < 0 || probability > 1) {
			throw new RuntimeException(
					"The probability for RandomPortal must be between 0 and 1 [0,1]");
		}
		this.probability = probability;
	}

}
