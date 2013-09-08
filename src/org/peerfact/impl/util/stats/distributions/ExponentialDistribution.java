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
import org.apache.commons.math.distribution.ExponentialDistributionImpl;
import org.peerfact.api.scenario.ConfigurationException;
import org.peerfact.impl.scenario.XMLConfigurableConstructor;
import org.peerfact.impl.simengine.Simulator;


/**
 * 
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public class ExponentialDistribution implements Distribution {

	private ExponentialDistributionImpl distr = null;

	private double mu;

	@XMLConfigurableConstructor({ "mu" })
	public ExponentialDistribution(double mu) {
		this.mu = mu;
		this.distr = new ExponentialDistributionImpl(mu);
	}

	/**
	 * returns a random value that is distributed as the configured
	 * distribution.
	 */
	@Override
	public double returnValue() {

		if (distr == null) {
			throw new ConfigurationException(
					"Mu was not set for exponential distribution " + this);
		}

		double random = Simulator.getRandom().nextDouble();
		double result;

		try {
			result = distr.inverseCumulativeProbability(random);
		} catch (MathException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result = 0;
		}

		return result;
	}

	/**
	 * returns a random value exponentially distributed with mu = _mu.
	 * 
	 * @param _mu
	 * @return as double
	 */
	public static double returnValue(double _mu) {
		try {
			ExponentialDistributionImpl d = new ExponentialDistributionImpl(_mu);
			return d.inverseCumulativeProbability(Simulator.getRandom()
					.nextDouble());
		} catch (MathException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public void writeBackToXML(BackWriter bw) {
		bw.writeSimpleType("mu", mu);
	}

	@Override
	public String toString() {
		return "ExponentialDistribution [distr=" + distr + ", mu=" + mu + "]";
	}

}
