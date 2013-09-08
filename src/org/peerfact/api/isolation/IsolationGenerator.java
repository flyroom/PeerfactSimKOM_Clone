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

package org.peerfact.api.isolation;

import org.peerfact.api.scenario.Composable;

/**
 * This component models the isolation of single groups within p2p systems.
 * Using a specific IsolationModel, it is possible to study its impact on the
 * performance of a p2p system.
 * 
 * @author Matthias Feldotto <info@peerfact.org>
 * @version 1.0, 04/19/2013
 */
public interface IsolationGenerator extends Composable {

	/**
	 * Sets a specific IsolationModel which will be used within a simulation
	 * run.
	 * 
	 * @param model
	 *            the specified isolation model
	 */
	public void setIsolationModel(IsolationModel model);

	/**
	 * Sets the point in time at which the simulation framework will activate
	 * the isolation behavior which has to be set using
	 * setIsolationModel()-method.
	 * 
	 * @param time
	 *            start time at which groups will be affected by isolation
	 */
	public void setStart(long time);

	/**
	 * Sets the point in time at which the simulation framework will deactivate
	 * the isolation behavior.
	 * 
	 * @param time
	 *            end time at which groups will not be no longer affected by
	 *            isolation
	 */
	public void setStop(long time);
}
