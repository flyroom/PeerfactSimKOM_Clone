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

package org.peerfact.impl.service.aggregation.gossip;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.peerfact.impl.util.Tuple;


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
 * 
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public class UpdateInfoNodeCount {

	List<Tuple<Integer, Double>> ncList;

	/**
	 * @return the current list of UIDs of nodes (int) that started a node count
	 *         along with the current estimation (double) of this attempt.
	 */
	public List<Tuple<Integer, Double>> getNCList() {
		return ncList;
	}

	public UpdateInfoNodeCount(List<Tuple<Integer, Double>> ncList) {
		this.ncList = ncList;
	}

	/**
	 * @return the size in bytes of this UpdateInfoNodeCount
	 */
	public long getSize() {
		return ncList.size() * 12;
	}

	public int getMedianNC() {
		List<Double> vals = new ArrayList<Double>(ncList.size());
		for (Tuple<Integer, Double> tuple : ncList) {
			vals.add(tuple.getB());
		}
		Collections.sort(vals);

		int size = vals.size();
		if (size == 0) {
			return -1;
		}
		return (int) Math.round(1d / vals.get((int) Math.floor(size / 2d)));
	}
}
