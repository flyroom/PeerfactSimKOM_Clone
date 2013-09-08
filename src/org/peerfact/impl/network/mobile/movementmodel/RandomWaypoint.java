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

package org.peerfact.impl.network.mobile.movementmodel;

import org.apache.log4j.Logger;
import org.peerfact.impl.network.mobile.MobileNode;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.util.logging.SimLogger;


/**
 * 
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public class RandomWaypoint implements MovementInstance {

	private final static Logger log = SimLogger.getLogger(RandomWaypoint.class);

	private double xDest;

	private double yDest;

	private boolean inTransit;

	private double lastChanged;

	private double speed;

	public RandomWaypoint(double speed) {
		this.speed = speed;
		lastChanged = 0;
	}

	/*
	 * / This method "moves" a network node to its new position.
	 * 
	 * @param time int: Time passed since the last update.
	 */

	@Override
	public void updateMovement(MobileNode m) {
		double ct = Simulator.getCurrentTime() / Simulator.MINUTE_UNIT;
		if (Math.abs(ct - lastChanged) < 0.0000001) {
			return;
		}
		double timePassed = ct - lastChanged;
		// Change the following parameter to allow a scaling of the velocity
		if (speed == 0) {
			return;
		}
		if (!inTransit) {
			// Waiting period, can be enabled if needed
			// time = time - 0.1 * Simulator.getRandom().nextDouble();
			xDest = Simulator.getRandom().nextDouble();
			yDest = Simulator.getRandom().nextDouble();
		}

		// Calculate the distance to the destination (a^2+b^2=c)
		double distance = Math.sqrt(Math.pow(xDest - m.getXPos(), 2)
				+ Math.pow(yDest - m.getYPos(), 2));
		double traveledDistance = distance - (timePassed * speed);
		log.debug("Time passed: " + timePassed);
		if (traveledDistance > 0) {
			// It is not enough time to move to the destination, so move
			// partially. checked and working
			// Time*Speed -> Gelaufener Weg
			double xPos1 = Math.cos(Math.atan2(yDest - m.getYPos(),
					xDest - m.getXPos()))
					* (timePassed * speed) + m.getXPos();
			double yPos1 = Math.sin(Math.atan2(yDest - m.getYPos(),
					xDest - m.getXPos()))
					* (timePassed * speed) + m.getYPos();
			log.debug("Network ID" + m.toString());
			log.debug("Old X" + m.getXPos() + " New: " + xPos1);
			log.debug("Old Y" + m.getYPos() + " New: " + yPos1);
			m.setXPos(xPos1);
			m.setYPos(yPos1);

			inTransit = true;
			lastChanged = ct;
		} else {
			m.setXPos(xDest);
			m.setYPos(yDest);
			inTransit = false;
			lastChanged = lastChanged + distance / speed; // Achtung..
															// �nderung..�berpr�fen!
			while (!inTransit) {
				this.updateMovement(m);
			}
		}
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}
}
