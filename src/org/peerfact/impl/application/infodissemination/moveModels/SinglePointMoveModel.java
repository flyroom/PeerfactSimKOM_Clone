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
import java.util.LinkedHashMap;
import java.util.Map;

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
 * This move model moved all players to one point and then back to the origin
 * point. This will be periodic repeat with the given frequency. The point is in
 * the center of the map. All players came to the same time to this point!
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 04/05/2011
 */
public class SinglePointMoveModel implements IMoveModel {

	/**
	 * The frequency to fulfill the movement to the center of the map and then
	 * back.
	 */
	private long frequency;

	/**
	 * The datastructure to store the origin of a node.
	 */
	private Map<IDOApplication, Point> origin = new LinkedHashMap<IDOApplication, Point>();

	/**
	 * The world dimension in X direction.
	 */
	public int worldDimensionX;

	/**
	 * The world dimension in Y direction.
	 */
	public int worldDimensionY;

	/**
	 * The portal component.
	 */
	public IPortalComponent portal;

	/**
	 * The flash crowd point
	 */
	public Point crowdPoint = null;

	@Override
	public Point getNextPosition(IDOApplication app) {
		if (crowdPoint == null) {
			crowdPoint = new Point(worldDimensionX / 2, worldDimensionY / 2);
		}

		if (portal != null) {
			Point position = portal.portal(app.getPlayerPosition(), app,
					worldDimensionX, worldDimensionY);
			// if a portal is used.
			if (position != null) {
				origin.put(app, position);
				return position;
			}
		}

		Point nextPosition;
		Point originPos = this.origin.get(app);
		if (originPos == null) {
			origin.put(app, app.getPlayerPosition());
			originPos = app.getPlayerPosition();
		}

		long time = Simulator.getCurrentTime() % frequency;

		int distX = originPos.x - crowdPoint.x;
		int distY = originPos.y - crowdPoint.y;

		double factor = 0;
		// go to the point
		if (time <= frequency / 2.0) {
			factor = (frequency / 2.0 - time) / (frequency / 2.0);
		} else {
			// go to the origin
			factor = -(frequency / 2.0 - time) / (frequency / 2.0);
		}
		nextPosition = new Point(crowdPoint.x + (int) (factor * distX),
				crowdPoint.y + (int) (factor * distY));

		setMoveVector(app, nextPosition);
		return nextPosition;
	}

	/**
	 * Sets the current move vector for this move.
	 * 
	 * @param app
	 *            The application
	 * @param nextPosition
	 *            The next position
	 */
	private static void setMoveVector(IDOApplication app, Point nextPosition) {
		app.setCurrentMoveVector(nextPosition.x - app.getPlayerPosition().x,
				nextPosition.y - app.getPlayerPosition().y);
	}

	public void setFrequency(long frequency) {
		this.frequency = frequency;
	}

	public void setWorldDimensionX(int worldDimensionX) {
		this.worldDimensionX = worldDimensionX;
	}

	public void setWorldDimensionY(int worldDimensionY) {
		this.worldDimensionY = worldDimensionY;
	}

	public void setFlashCrowdPoint(int x, int y) {
		this.crowdPoint = new Point(x, y);
	}

	public void setPortal(IPortalComponent portal) {
		this.portal = portal;
	}

}
