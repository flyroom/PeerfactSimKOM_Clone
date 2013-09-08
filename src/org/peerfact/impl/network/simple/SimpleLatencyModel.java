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

import org.apache.log4j.Logger;
import org.peerfact.api.network.NetLatencyModel;
import org.peerfact.api.network.NetLayer;
import org.peerfact.api.network.NetPosition;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.util.logging.SimLogger;


/**
 * This model is abstracting the details of the four lower OSI layers (UDP and
 * TCP) from the end-to-end connections between peers although important network
 * characteristics, like the geographical distance between peers, the processing
 * delay of intermediate systems, signal propagation, congestions,
 * retransmission and packet loss are incorporated into it. The message delay is
 * calculated using the following formula:
 * 
 * Message delay = f * (df + dist/v)
 * 
 * where dist - describes the geographical distance between the start and the
 * end point of the transmission, df - represents the processing delay of the
 * intermediate systems, v - stands for the speed of the signal propagation
 * through the transmission medium, and f - is a variable part which
 * encapsulates the retransmission, congestion.
 * 
 * @author Sebastian Kaune <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class SimpleLatencyModel implements NetLatencyModel {

	private final static Logger log = SimLogger
			.getLogger(SimpleLatencyModel.class);

	/**
	 * Speed in kilometer per second
	 */
	private final int signalSpeed = 100000;

	/**
	 * Earth circumference in kilometres
	 */
	private final int earth_circumference = 40000;

	private final double relSignalSpeed;

	/**
	 * Constructor
	 * 
	 */
	public SimpleLatencyModel() {
		relSignalSpeed = signalSpeed
				* (SimpleSubnet.SUBNET_WIDTH / earth_circumference);
	}

	@Override
	public long getLatency(NetLayer sender, NetLayer receiver) {
		double distance = getDistance((SimpleNetLayer) sender,
				(SimpleNetLayer) receiver);
		double staticDelay = Simulator.MILLISECOND_UNIT
				* this.calcStaticDelay(receiver, distance);
		int f = (Simulator.getRandom().nextInt(10) + 1);
		long latency = Math.round(f * staticDelay * 0.1);
		log.debug("Latency " + sender + " -> " + receiver + ": " + latency);
		return latency;
	}

	static double getDistance(SimpleNetLayer sender, SimpleNetLayer receiver) {
		NetPosition ps = sender.getNetPosition();
		NetPosition pr = receiver.getNetPosition();
		return ps.getDistance(pr);
	}

	double calcStaticDelay(NetLayer receiver, double distance) {
		int df = Math.abs(receiver.hashCode() % 31);
		return (df + (distance / relSignalSpeed) * 1000);
	}

}
