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
 * This move model generates a random new position, derived from the old
 * position and an offset, which is limited by the move speed.
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * @version 06/01/2011
 * 
 */
public class RandomMoveModel implements IMoveModel {
	public int moveSpeedLimit;

	public int worldDimensionX;

	public int worldDimensionY;

	public RandomMoveModel() {
		this(10, 200, 200);
	}

	/**
	 * The portal component.
	 */
	public IPortalComponent portal;

	public RandomMoveModel(int moveSpeedLimit, int worldDimensionX,
			int worldDimensionY) {
		this.moveSpeedLimit = moveSpeedLimit;
		this.worldDimensionX = worldDimensionX;
		this.worldDimensionY = worldDimensionY;
	}

	@Override
	public Point getNextPosition(IDOApplication app) {
		RandomGenerator r = Simulator.getRandom();

		Point pos = app.getPlayerPosition();

		Point newPos;

		if (portal != null) {
			Point position = portal.portal(app.getPlayerPosition(), app,
					worldDimensionX, worldDimensionY);
			// if a portal is used.
			if (position != null) {
				return position;
			}
		}

		do {
			newPos = new Point(
					pos.x
							+ (r.nextInt(2 * moveSpeedLimit + 1) - moveSpeedLimit),
					pos.y
							+ (r.nextInt(2 * moveSpeedLimit + 1) - moveSpeedLimit));

		} while (newPos.x < 0 || newPos.x > worldDimensionX || newPos.y < 0
				|| newPos.y > worldDimensionY);
		setMoveVector(app, newPos);
		return newPos;
	}

	private static void setMoveVector(IDOApplication app, Point nextPosition) {
		app.setCurrentMoveVector(nextPosition.x - app.getPlayerPosition().x,
				nextPosition.y - app.getPlayerPosition().y);
	}

	public void setMoveSpeedLimit(int moveSpeedLimit) {
		this.moveSpeedLimit = moveSpeedLimit;
	}

	public void setWorldDimensionX(int worldDimensionX) {
		this.worldDimensionX = worldDimensionX;
	}

	public void setWorldDimensionY(int worldDimensionY) {
		this.worldDimensionY = worldDimensionY;
	}

	public void setPortal(IPortalComponent portal) {
		this.portal = portal;
	}
}
