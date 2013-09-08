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

import java.util.List;

/**
 * The isolation model describes the isolation behavior of groups in the network
 * within a simulation run. For instance, simple distribution such as
 * exponential, lognormal or even more complex models are conceivable.
 * 
 * @author Matthias Feldotto <info@peerfact.org>
 * @version 1.0, 04/19/2013
 */
public interface IsolationModel {

	/**
	 * Gets the next isolation time of a given group. The isolation time defines
	 * the time how long the given host will stay isolated.
	 * 
	 * @param host
	 *            the specified group
	 * 
	 * @return time to stay isolated
	 */
	public long getNextIsolationTime(String group);

	/**
	 * Gets the next global time of a given group. The global time defines the
	 * time how long the given host will have global contact.
	 * 
	 * @param host
	 *            the specified group
	 * 
	 * @return time to have global contact
	 */
	public long getNextGlobalTime(String group);

	/**
	 * Invoking this method prepares the isolation model for its future
	 * activation. All relevant information can be accessed by the given list of
	 * existing groups.
	 * 
	 * @param groups
	 *            the groups which will be affected by isolation
	 */
	public void prepare(List<String> groups);

}
