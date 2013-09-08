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

package org.peerfact.api.service.aggr;

/**
 * Result of an aggregation procedure. Note that the values in this result may
 * be approximations.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public interface AggregationResult {

	/**
	 * Returns (an approximation of) the <i>minimum</i> of the values.
	 * 
	 * @return
	 */
	public double getMinimum();

	/**
	 * Returns (an approximation of) the <i>maximum</i> of the values.
	 * 
	 * @return
	 */
	public double getMaximum();

	/**
	 * Returns (an approximation of) the <i>average</i> of the values.
	 * 
	 * @return
	 */
	public double getAverage();

	/**
	 * Returns (an approximation of) the <i>variance</i> of the values.
	 * 
	 * @return
	 */
	public double getVariance();

	/**
	 * Returns (an approximation of) the <i>node count</i> of the values. Note
	 * that this method only returns the number of nodes that carry a value with
	 * the identifier associated with this result.
	 * 
	 * @return
	 */
	public int getNodeCount();

	/**
	 * Gets the adding timestamp of the oldest date in the aggregate.
	 * 
	 * @return The time of the oldest date in the aggregate.
	 */
	public long getMinTime();

	/**
	 * Gets the adding timestamp of the newest date in the aggregate.
	 * 
	 * @return The time of the newest date in the aggregate.
	 */
	public long getMaxTime();

	/**
	 * Gets the avg adding timestamp of the data in the aggregate.
	 * 
	 * @return The average time of the data in the aggregate.
	 */
	public long getAvgTime();

}
