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

import org.apache.log4j.Logger;
import org.peerfact.impl.application.infodissemination.moveModels.RandomPathMoveModel;
import org.peerfact.impl.util.logging.SimLogger;


/**
 * TODO: unfinished
 * 
 * @author bjoernr
 * @version 1.0, mm/dd/2011
 */
public class RandomPathMovement extends MovementModel {

	/**
	 * Logger for this class
	 */
	final static Logger log = SimLogger.getLogger(RandomPathMoveModel.class);

	/**
	 * The rate to change speed. If == 0, speed will not change
	 */
	public double speedChangingRate;

	public RandomPathMovement() {
		super();
	}

	public void setSpeedChangingRate(double speedChangingRate) {
		this.speedChangingRate = speedChangingRate;
	}

	@Override
	public void move() {
		/*
		 * TODO bjoernr: unfinished, will be continued...
		 */

	}
}
