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

/**
 * 
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public class UniformDistribution implements Distribution {
	@Override
	public String toString() {
		return "UniformDistribution [min=" + min + ", max=" + max + "]";
	}

	private double min;

	private double max;

	private double factor;

	@XMLConfigurableConstructor({ "min", "max" })
	public UniformDistribution(double min, double max) {
		this.min = Math.min(min, max);
		this.max = Math.max(min, max);
		factor = Math.abs(max - min);
	}

	/**
	 * Delivers a random value distributed as the configured distribution.
	 */
	@Override
	public double returnValue() {

		return min + factor * Simulator.getRandom().nextDouble();
	}

	/**
	 * delivers a random value that is uniformly distributed between the _min
	 * and the _max value.
	 * 
	 * @param _min
	 * @param _max
	 * @return random value as double
	 */
	public static double returnValue(double _min, double _max) {
		double lmin, lmax, lfactor;
		if (_min < _max) {
			lmin = _min;
			lmax = _max;
		} else {
			lmin = _max;
			lmax = _min;
		}
		lfactor = Math.abs(lmax - lmin);

		return lmin + lfactor * Simulator.getRandom().nextDouble();
	}

	@Override
	public void writeBackToXML(BackWriter bw) {
		bw.writeSimpleType("min", min);
		bw.writeSimpleType("max", max);
	}

}
