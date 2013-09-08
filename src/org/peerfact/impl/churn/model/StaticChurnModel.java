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

package org.peerfact.impl.churn.model;

import java.util.List;

import org.peerfact.api.churn.ChurnModel;
import org.peerfact.api.common.Host;
import org.peerfact.impl.simengine.Simulator;

/**
 * ChurnGeneration with fixed online and offline times which is the same for all
 * hosts.
 * 
 * @author Sebastian Kaune <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 */
public class StaticChurnModel implements ChurnModel {

	private long uptime = 15 * Simulator.MINUTE_UNIT;

	private long downTime = uptime;

	@Override
	public long getNextUptime(Host host) {
		return uptime;
	}

	@Override
	public long getNextDowntime(Host host) {
		return downTime;
	}

	@Override
	public void prepare(List<Host> churnHosts) {
		// no functionality by using this static generator
	}

	@Override
	public String toString() {
		return "StaticChurnModel";
	}

	public void setDowntime(long time) {
		this.downTime = time;
	}

	public void setUptime(long time) {
		this.uptime = time;
	}

}
