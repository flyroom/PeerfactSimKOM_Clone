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

package org.peerfact.impl.network.modular.st.trafCtrl;

import org.peerfact.impl.simengine.Simulator;

/**
 * 
 * A traffic queue that will drop packets, if they would have to wait longer
 * than a certain amount of time before they would BEGIN to be transferred.
 * 
 * Parameters: maxTimeSend(simulation time units), maxTimeReceive(simulation
 * time units).
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class BoundedTrafficQueue extends InfiniteTrafficQueue {

	long maxTimeSend = 3 * Simulator.SECOND_UNIT;

	long maxTimeReceive = 3 * Simulator.SECOND_UNIT;

	@Override
	protected boolean isTooLongSend(long waitTime) {
		return waitTime > maxTimeSend;
	}

	@Override
	protected boolean isTooLongRcv(long waitTime) {
		return waitTime > maxTimeReceive;
	}

	/**
	 * Sets the maximum time a message may (begin to) be sent after the send
	 * request attempt.
	 * 
	 * @param maxTimeSend
	 */
	public void setMaxTimeSend(long maxTimeSend) {
		this.maxTimeSend = maxTimeSend;
	}

	/**
	 * Sets the maximum time a message may (begin to) be received after the
	 * receive request attempt.
	 * 
	 * @param maxTimeReceive
	 */
	public void setMaxTimeReceive(long maxTimeReceive) {
		this.maxTimeReceive = maxTimeReceive;
	}

	@Override
	public void writeBackToXML(BackWriter bw) {
		bw.writeTime("maxTimeSend", maxTimeSend);
		bw.writeTime("maxTimeReceive", maxTimeReceive);
	}

}
