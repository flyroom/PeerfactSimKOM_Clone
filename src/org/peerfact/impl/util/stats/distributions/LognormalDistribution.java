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

package org.peerfact.impl.util.stats.distributions;

import org.peerfact.impl.scenario.XMLConfigurableConstructor;
import org.peerfact.impl.simengine.Simulator;

import umontreal.iro.lecuyer.probdist.LognormalDist;

/**
 * 
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public class LognormalDistribution implements Distribution {

	private double mu;

	private double sigma;

	private LognormalDist distr;

	@Override
	public String toString() {
		return "LognormalDistribution [mu=" + mu + ", sigma=" + sigma + "]";
	}

	@XMLConfigurableConstructor({ "mu", "sigma" })
	public LognormalDistribution(double mu, double sigma) {
		this.mu = mu;
		this.sigma = sigma;
		distr = new LognormalDist(mu, sigma);
	}

	@Override
	public double returnValue() {
		double random = Simulator.getRandom().nextDouble();
		double result = 0;

		try {
			result = distr.inverseF(random);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * returns a random value lognormally distributed with mu = _mu and sigma =
	 * _sigma.
	 * 
	 * @param _mu
	 * @param _sigma
	 * @return as double
	 */
	public static double returnValue(double _mu, double _sigma) {
		try {
			LognormalDist d = new LognormalDist(_mu, _sigma);
			return d.inverseF(Simulator.getRandom().nextDouble());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}

	}

	@Override
	public void writeBackToXML(BackWriter bw) {
		bw.writeSimpleType("mu", mu);
		bw.writeSimpleType("sigma", sigma);
	}

}
