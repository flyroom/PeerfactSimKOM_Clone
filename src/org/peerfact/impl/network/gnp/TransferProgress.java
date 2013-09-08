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

package org.peerfact.impl.network.gnp;

import org.peerfact.api.network.NetMessage;
import org.peerfact.impl.simengine.SimulationEvent;
import org.peerfact.impl.simengine.Simulator;

/**
 * 
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public class TransferProgress {

	private NetMessage message;

	private double bandwidth; // in Simulator Time Units;

	private double remainingBytes;

	private long scheduledAt;

	public boolean firstSchedule = true;

	public boolean obsolete = false;

	public SimulationEvent relatedEvent = null;

	public TransferProgress(NetMessage msg, double bandwidth,
			double remainingBytes, long scheduledAt) {
		this.message = msg;
		this.bandwidth = bandwidth / Simulator.SECOND_UNIT;
		this.remainingBytes = remainingBytes;
		this.scheduledAt = scheduledAt;
	}

	public NetMessage getMessage() {
		return message;
	}

	public double getRemainingBytes(long time) {
		long interval = time - scheduledAt;
		return remainingBytes - (interval * bandwidth);
	}

	/*
	 * public long getArrivalTime() { return arrivalTime; }
	 */

}
