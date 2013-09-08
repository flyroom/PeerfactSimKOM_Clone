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

import java.math.BigDecimal;
import java.math.RoundingMode;

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
 * Permits to keep a running total as well as the number of samples and to
 * calculate the average.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public final class AvgAccumulator {

	/** the number of samples so far */
	private long count = 0;

	/** the running total (sum of all samples) */
	private BigDecimal sum = BigDecimal.ZERO;

	/** the minimum and maximum amount added in all samples. */
	private double min = 0, max = 0;

	/**
	 * Whether the first sample has been added, that is, whether min and max
	 * have correct values.
	 */
	private boolean firstValueAdded = false;

	/**
	 * Adds the sample <code>amount</code> to the current running total and
	 * increases the number of samples by one.
	 * 
	 * @param amount
	 *            the value to be added.
	 */
	public final void addToTotal(final double amount) {
		sum = sum.add(BigDecimal.valueOf(amount));
		count++;
		if (!firstValueAdded) {
			firstValueAdded = true;
			min = amount;
			max = amount;
		} else if (amount < min) {
			min = amount;
		} else if (amount > max) {
			max = amount;
		}
	}

	/**
	 * Calculates the average amount per sample.
	 * 
	 * @return the average of all samples.
	 */
	public final double getAverage() {
		if (count == 0) {
			return 0;
		}
		return sum.divide(BigDecimal.valueOf(count), 10, RoundingMode.CEILING)
				.doubleValue();
	}

	/**
	 * @return the total number of samples.
	 */
	public final long getCount() {
		return count;
	}

	/**
	 * @return the minimum amount that has been added in one sample.
	 */
	public final double getMin() {
		return min;
	}

	/**
	 * @return the maximum amount that has been added in one sample.
	 */
	public final double getMax() {
		return max;
	}
}
