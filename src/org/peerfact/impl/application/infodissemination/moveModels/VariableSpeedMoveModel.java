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
 * This move model generates new positions for which it takes into account the
 * current movement vector of the node.
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * @version 01/06/2011
 */
public class VariableSpeedMoveModel implements IMoveModel {

	public final static int SPEEDCHANGE_BY_STEP = 2;

	public int moveSpeedLimit;

	public int worldDimensionX;

	public int worldDimensionY;

	/**
	 * The portal component.
	 */
	public IPortalComponent portal;

	public VariableSpeedMoveModel() {
		this(10, 200, 200);
	}

	public VariableSpeedMoveModel(int moveSpeedLimit, int worldDimensionX,
			int worldDimensionY) {
		this.moveSpeedLimit = moveSpeedLimit;
		this.worldDimensionX = worldDimensionX;
		this.worldDimensionY = worldDimensionY;
	}

	@Override
	public Point getNextPosition(IDOApplication app) {
		Point pos = app.getPlayerPosition();
		int[] currMoveVec = app.getCurrentMoveVector();

		if (portal != null) {
			Point position = portal.portal(app.getPlayerPosition(), app,
					worldDimensionX, worldDimensionY);
			// if a portal is used.
			if (position != null) {
				return position;
			}
		}

		Point newPos;

		int spChangeDirect = app.speedChanging();

		if (spChangeDirect != 0) {
			double speedDistrToDim = Simulator.getRandom().nextDouble();

			int signumMoveX = (int) Math.signum(currMoveVec[0]);
			int signumMoveY = (int) Math.signum(currMoveVec[1]);

			int vecChangeX = (int) (spChangeDirect * signumMoveX * Math
					.floor(Math.sqrt(SPEEDCHANGE_BY_STEP * SPEEDCHANGE_BY_STEP
							* speedDistrToDim)));
			int vecChangeY = (int) (spChangeDirect * signumMoveY * Math
					.floor(Math.sqrt(SPEEDCHANGE_BY_STEP * SPEEDCHANGE_BY_STEP
							* (1 - speedDistrToDim))));

			double moveX = currMoveVec[0] + vecChangeX;
			double moveY = currMoveVec[1] + vecChangeY;

			if (Math.signum(moveX) != Math.signum(currMoveVec[0])) {
				moveX = 0;
			}

			if (Math.signum(moveX) != Math.signum(currMoveVec[0])) {
				moveY = 0;
			}

			double newSpeed = Math.sqrt(Math.pow((moveX), 2)
					+ Math.pow(moveY, 2));

			if (newSpeed <= moveSpeedLimit) {
				currMoveVec[0] = (int) moveX;
				currMoveVec[1] = (int) moveY;
			}
		}

		newPos = new Point(pos.x + currMoveVec[0], pos.y + currMoveVec[1]);

		// Avoid that nodes leave the world
		if (newPos.x < 0 || newPos.x > worldDimensionX || newPos.y < 0
				|| newPos.y > worldDimensionY) {
			if (newPos.x < 0 || newPos.x > worldDimensionX) {
				currMoveVec[0] = -currMoveVec[0];
			}
			if (newPos.y < 0 || newPos.y > worldDimensionY) {
				currMoveVec[1] = -currMoveVec[1];
			}

			// Recompute with changed move vector
			newPos = new Point(pos.x + currMoveVec[0], pos.y + currMoveVec[1]);
		}

		app.setCurrentMoveVector(currMoveVec[0], currMoveVec[1]);

		return newPos;
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
