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

package org.peerfact.impl.overlay.dht.kademlia.base.analyzer.util;

import org.apache.commons.math.stat.StatUtils;
import org.apache.log4j.Logger;
import org.peerfact.impl.util.logging.SimLogger;
import org.peerfact.impl.util.stats.ConfidenceInterval;


/**
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * This part of the Simulator is not maintained in the current version of
 * PeerfactSim.KOM. There is no intention of the authors to fix this
 * circumstances, since the changes needed are huge compared to overall benefit.
 * 
 * If you want it to work correctly, you are free to make the specific changes
 * and provide it to the community.
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! !!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * 
 * Permits to calculate the confidence interval of a given set of means. The
 * formula is according to Jain, R.; The Art of Computer Systems Performance
 * Analysis, John Wiley &amp; Sons, New York 1991, p. 206.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public class ConfidenceCalculator {

	/** The significance level */
	private static final double ALPHA = 0.01;

	private static Logger log = SimLogger.getLogger(ConfidenceCalculator.class);

	/**
	 * Calculates the mean, standard deviation and confidence interval for the
	 * given arguments.
	 * 
	 * @param args
	 *            a space-separated string containing an arbitrary number of
	 *            samples as the first argument.
	 */
	public static void main(String[] args) {
		String[] split = args;// args[0].split(" ");
		double[] sample = new double[split.length];
		double[] result;

		System.out.print("Input: ");
		for (int i = 0; i < split.length; i++) {
			sample[i] = Double.valueOf(split[i]);
			System.out.print(split[i] + " ");
		}
		log.debug('\n');

		result = calc(sample, ALPHA);

		log.debug("Mean: " + result[0]);
		log.debug("Standard deviation: " + result[1]);
		log.debug("Delta: " + result[2]);
		log.debug("Interval: " + result[3] + " " + result[4]);
	}

	/**
	 * @return 0: mean, 1: standard deviation, 2: delta (half interval width),
	 *         3: lower bound, 4: upper bound of confidence interval.
	 */
	public static double[] calc(double[] sample, double alpha) {
		double mean, standardDeviation, delta, ivLow, ivHigh;

		mean = StatUtils.mean(sample);
		standardDeviation = Math.sqrt(StatUtils.variance(sample, mean));
		delta = ConfidenceInterval.getDeltaBound(standardDeviation,
				sample.length, alpha);
		ivLow = mean - delta;
		ivHigh = mean + delta;

		return new double[] { mean, standardDeviation, delta, ivLow, ivHigh };
	}

}
