/*
 * Copyright (c) 2012-2013 Open Source Community - <http://www.peerfact.org>
 * Copyright (c) 2011-2012 University of Paderborn - UPB
 * Copyright (c) 2005-2011 KOM - Multimedia Communications Lab
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */

package org.peerfact.impl.util.evaluation.distribution;

import org.peerfact.impl.analyzer.csvevaluation.distribution.Distribution;
import org.peerfact.impl.analyzer.csvevaluation.distribution.IDistribution;
import org.peerfact.impl.analyzer.csvevaluation.distribution.IDistribution.IDistResultStream;

public class DistributionTest {

	public static void main(String[] args) {

		IDistribution<Integer> d = new Distribution<Integer>();

		d.setValue(1, 5);
		d.setValue(2, 5);
		d.setValue(3, 6);
		d.setValue(4, 5);
		d.setValue(5, 5);
		d.setValue(6, 6);
		d.setValue(7, 5);
		d.setValue(8, 7);
		d.setValue(9, 5);
		d.setValue(10, 5);
		d.setValue(11, 20);
		d.setValue(12, 5);
		d.setValue(13, 6);
		d.setValue(14, 6);
		d.setValue(15, 8);
		d.setValue(16, 7);
		d.setValue(17, 5);
		d.setValue(18, 9);
		d.setValue(19, 6);
		d.setValue(20, 2);
		d.setValue(21, 8);
		d.setValue(22, 3);
		d.setValue(23, 4);
		d.setValue(24, 7);

		printDistributionResults(d);

		d.remove(19);
		d.remove(10);
		d.remove(7);

		printDistributionResults(d);

		d.setValue(12, 2);
		d.setValue(13, 6200);
		d.setValue(14, 2);
		d.setValue(15, 15);
		d.setValue(16, 255);
		d.setValue(17, 1);
		d.setValue(19, 12);

		d.remove(19);
		d.remove(10);
		d.remove(7);

		printDistributionResults(d);

		/*
		 * d.setValue(1, 1); d.setValue(2, 2); d.setValue(3, 2); d.setValue(4,
		 * 3); d.setValue(5, 3); d.setValue(6, 4); d.setValue(7, 18);
		 * 
		 * printDistributionResults(d);
		 */

	}

	public static void printDistributionResults(IDistribution<Integer> d) {

		IDistResultStream stream = d.getResultStream();

		for (int i = 0; i < stream.getDistSize(); i++) {
			long value = stream.getNextValue();
			System.out
					.println((double) i / stream.getDistSize() + ": " + value);
		}

	}

}
