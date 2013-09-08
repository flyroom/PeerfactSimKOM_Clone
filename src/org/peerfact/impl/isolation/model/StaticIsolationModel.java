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

package org.peerfact.impl.isolation.model;

import java.util.List;

import org.peerfact.api.isolation.IsolationModel;
import org.peerfact.impl.simengine.Simulator;

/**
 * Isolation with fixed times which is the same for all groups.
 * 
 * @author Matthias Feldotto <info@peerfact.org>
 * @version 1.0, 04/19/2013
 */
public class StaticIsolationModel implements IsolationModel {

	private long isolationTime = 15 * Simulator.MINUTE_UNIT;

	private long globalTime = isolationTime;

	@Override
	public long getNextIsolationTime(String group) {
		return isolationTime;
	}

	@Override
	public long getNextGlobalTime(String group) {
		return globalTime;
	}

	@Override
	public void prepare(List<String> groups) {
		// no functionality by using this static generator
	}

	@Override
	public String toString() {
		return "StaticIsolationModel";
	}

	public void setIsolationTime(long time) {
		this.isolationTime = time;
	}

	public void setGlobalTime(long time) {
		this.globalTime = time;
	}

}
