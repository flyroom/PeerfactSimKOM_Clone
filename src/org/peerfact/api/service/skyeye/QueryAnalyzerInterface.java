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

package org.peerfact.api.service.skyeye;

import org.peerfact.impl.service.aggregation.skyeye.queries.Query;

/**
 * This interface defines the functionality for monitoring the treatment of
 * queries by the simulator. The methods of this interface are called if queries
 * of the type <code>Query</code> are started, lost or if unsolved or solved
 * queries are received by the originator.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 15.11.2008
 * 
 */
public interface QueryAnalyzerInterface {

	/**
	 * This method is called, if the originator of <code>query</code>
	 * instantiated the <code><QueryTransmissionOperation</code>.
	 * 
	 * @param query
	 *            contains the originated query.
	 */
	public void queryStarted(Query query);

	/**
	 * This method is called at the originator of <code>query</code>, if a
	 * timeout for <code><QueryTransmissionOperation</code> occurred due to loss
	 * of the originated query.
	 * 
	 * @param query
	 *            contains the lost query.
	 */
	public void queryLost(Query query);

	/**
	 * This method is called at the originator of <code>query</code>, if it
	 * receives the unsolved query.
	 * 
	 * @param query
	 *            contains the unsolved query
	 */
	public void unsolvedQueryReceived(Query query);

	/**
	 * This method is called at the originator of <code>query</code>, if it
	 * receives the solved query.
	 * 
	 * @param query
	 *            contains the solved query
	 */
	public void solvedQueryReceived(Query query);
}
