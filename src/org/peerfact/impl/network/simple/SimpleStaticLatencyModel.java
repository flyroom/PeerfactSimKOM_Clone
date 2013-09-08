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

package org.peerfact.impl.network.simple;

import org.peerfact.api.network.NetLatencyModel;
import org.peerfact.api.network.NetLayer;
import org.peerfact.api.network.NetPosition;
import org.peerfact.impl.simengine.Simulator;

/**
 * 
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public class SimpleStaticLatencyModel implements NetLatencyModel {
	long staticLatency = 10l * Simulator.MILLISECOND_UNIT;

	public SimpleStaticLatencyModel(long staticLatency) {
		this();
		setLatency(staticLatency);
	}

	public SimpleStaticLatencyModel() {
		// nop
	}

	/**
	 * Sets the static latency which is expected in millseconds. That is, if
	 * <code>staticLatency</code> is set to 10, the simulator will translate it
	 * into simulation units as follows: staticLatency *
	 * Simulator.MILLISECOND_UNIT.
	 * 
	 * @param staticLatency
	 *            the static latency in milliseconds.
	 */
	public void setLatency(long staticLatency) {
		this.staticLatency = staticLatency * Simulator.MILLISECOND_UNIT;
	}

	@Override
	public long getLatency(NetLayer sender, NetLayer receiver) {
		double distance = getDistance((SimpleNetLayer) sender,
				(SimpleNetLayer) receiver);
		return Math.round(staticLatency * distance);
	}

	protected static double getDistance(SimpleNetLayer sender,
			SimpleNetLayer receiver) {
		NetPosition ps = sender.getNetPosition();
		NetPosition pr = receiver.getNetPosition();
		double distance = ps.getDistance(pr);
		return distance;
	}

}
