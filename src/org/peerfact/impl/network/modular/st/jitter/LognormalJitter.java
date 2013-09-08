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

package org.peerfact.impl.network.modular.st.jitter;

import org.apache.commons.math.random.RandomGenerator;
import org.peerfact.api.network.NetMessage;
import org.peerfact.impl.network.modular.ModularNetLayer;
import org.peerfact.impl.network.modular.db.NetMeasurementDB;
import org.peerfact.impl.network.modular.st.JitterStrategy;
import org.peerfact.impl.simengine.Simulator;

import umontreal.iro.lecuyer.probdist.LognormalDist;

/**
 * Applies a jitter that is log-normally distributed.
 * 
 * Parameters: mu (double, unit in msec) Parameters: sigma (double, unit in
 * msec)
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class LognormalJitter implements JitterStrategy {

	LognormalDist dist = null;

	RandomGenerator rand = Simulator.getRandom();

	private double mu = 1; // Default value

	private double sigma = 0.6; // Default value

	@Override
	public long getJitter(long cleanMsgPropagationDelay, NetMessage msg,
			ModularNetLayer nlSender, ModularNetLayer nlReceiver,
			NetMeasurementDB db) {
		if (dist == null) {
			dist = new LognormalDist(mu, sigma);
		}
		return Math.round(dist.inverseF(rand.nextDouble())
				* Simulator.MILLISECOND_UNIT);
	}

	/**
	 * Sets the mu parameter (Unit is msec)
	 * 
	 * @param mu
	 */
	public void setMu(double mu) {
		this.mu = mu;
	}

	/**
	 * Sets the sigma parameter (Unit is msec)
	 * 
	 * @param sigma
	 */
	public void setSigma(double sigma) {
		this.sigma = sigma;
	}

	@Override
	public void writeBackToXML(BackWriter bw) {
		bw.writeSimpleType("mu", mu);
		bw.writeSimpleType("sigma", sigma);
	}

}
