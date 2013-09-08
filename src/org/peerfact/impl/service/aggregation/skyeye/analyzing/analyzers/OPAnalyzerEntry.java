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

package org.peerfact.impl.service.aggregation.skyeye.analyzing.analyzers;

import org.peerfact.api.common.Operation;

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
 * This class defines and comprises the objects, which are stored within
 * {@link OPAnalyzer}. <code>OPAnalyzer</code> stores an instance of this class
 * for every finished operation, which comprises the additionally required
 * information about that operation.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 05.12.2008
 * 
 */
public class OPAnalyzerEntry {

	private Operation<?> op;

	private long duration;

	private boolean succcess;

	public OPAnalyzerEntry(Operation<?> op, long duration, boolean success) {
		this.op = op;
		this.succcess = success;
		this.duration = duration;
	}

	/**
	 * This method returns the finished operation.
	 * 
	 * @return the finished operation
	 */
	public Operation<?> getOp() {
		return op;
	}

	/**
	 * This method returns the amount of time, which was needed to finish the
	 * operation.
	 * 
	 * @return the duration of the finishing-process
	 */
	public long getDuration() {
		return duration;
	}

	/**
	 * This method specifies, if the operation was successfully finished or not.
	 * 
	 * @return <code>true</code>, if the operation was successfully finished,
	 *         <code>false</code> otherwise
	 */
	public boolean isSucccess() {
		return succcess;
	}
}
