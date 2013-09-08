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

/**
 * 
 */
package org.peerfact.impl.util.stats.distributions;

import org.peerfact.impl.scenario.XMLConfigurableConstructor;
import org.peerfact.impl.simengine.Simulator;

/**
 * @author Kalman Graffi <info@peerfact.org>
 * @version 0.1, 26.09.2007 see http://en.wikipedia.org/wiki/Zipf's_law
 */
public class ZipfDistribution implements Distribution {

	@Override
	public String toString() {
		return "ZipfDistribution [maximum_Number_Of_Ranks="
				+ maximum_Number_Of_Ranks + ", zipfExponent=" + zipfExponent
				+ "]";
	}

	private int maximum_Number_Of_Ranks;

	private double zipfExponent;

	private double harmonicNormFactor;

	@XMLConfigurableConstructor({ "maxNrOfRanks", "zipfExponent" })
	public ZipfDistribution(int maxNrOfRanks, double zipfExponent) {
		this.maximum_Number_Of_Ranks = maxNrOfRanks;
		this.zipfExponent = zipfExponent;
		harmonicNormFactor = 0;
		for (int i = 1; i <= maximum_Number_Of_Ranks; i++) {
			harmonicNormFactor += Math.pow(1 / i, zipfExponent);
		}
	}

	/**
	 * Returns a RANK, not a probability
	 */
	@Override
	public double returnValue() {
		// prob = 1 / (rank^zipfExp * harmonicNormFactor)
		// => rank^zipfExp = 1 / (prob * harmonicNormFactor)
		// => rank = zipfExp-sqrt (1 / (prob*harmonicNormFactor))
		// rank = 1 ... maximum_Number_Of_Ranks => 1/rank = 0..1

		return 1 / (Math.pow(
				1 / (Simulator.getRandom().nextDouble() * harmonicNormFactor),
				1 / zipfExponent));
	}

	public int returnRank() {
		return (int) Math.floor(1 / returnValue());
	}

	@Override
	public void writeBackToXML(BackWriter bw) {
		bw.writeSimpleType("maxNrOfRanks", maximum_Number_Of_Ranks);
		bw.writeSimpleType("zipfExponent", zipfExponent);
	}

}
