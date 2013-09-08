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

import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.NormalDistributionImpl;
import org.peerfact.impl.scenario.XMLConfigurableConstructor;
import org.peerfact.impl.simengine.Simulator;


/**
 * 
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public class NormalDistribution implements Distribution {

	private NormalDistributionImpl normal;

	private double mu;

	private double sigma;

	@XMLConfigurableConstructor({ "mu", "sigma" })
	public NormalDistribution(double mu, double sigma) {
		this.mu = mu;
		this.sigma = sigma;
		normal = new NormalDistributionImpl(mu, sigma);
	}

	@Override
	public void writeBackToXML(BackWriter bw) {
		bw.writeSimpleType("mu", mu);
		bw.writeSimpleType("sigma", sigma);
	}

	@Override
	public double returnValue() {
		double random = Simulator.getRandom().nextDouble();
		double result;

		try {
			result = normal.inverseCumulativeProbability(random);
		} catch (MathException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result = 0;
		}

		return result;
	}

	@Override
	public String toString() {
		return "NormalDistribution [mu=" + mu + ", sigma=" + sigma + "]";
	}

	/**
	 * returns a random value normally distributed with mu = _mu and sigma =
	 * _sigma.
	 * 
	 * @param _mu
	 * @param _sigma
	 * @return as double
	 */
	public static double returnValue(double _mu, double _sigma) {
		try {
			NormalDistributionImpl d = new NormalDistributionImpl(_mu, _sigma);
			return d.inverseCumulativeProbability(Simulator.getRandom()
					.nextDouble());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}

	}

}
