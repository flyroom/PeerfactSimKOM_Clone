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

package org.peerfact.impl.overlay.dht.kademlia2.measurement.file;

import static org.junit.Assert.assertEquals;

import org.apache.commons.math.util.MathUtils;
import org.junit.Test;
import org.peerfact.impl.overlay.dht.kademlia.base.analyzer.util.ConfidenceCalculator;


/**
 * Test case for ConfidenceCalculator.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 */
public class ConfidenceCalculatorTest {

	/**
	 * Tests the ConfidenceCalculator with sample data from Jain, R.; The Art of
	 * Computer Systems Performance Analysis, Wiley &amp; Sons, New York 1991,
	 * p. 208.
	 */
	@Test
	public static void testConfidenceCalculator() {
		/*
		 * Some expected values differ from the data in [Jain] as we calculate
		 * with greater precision.
		 */
		double[] actual = ConfidenceCalculator.calc(new double[] { 1.5, 2.6,
				-1.8, 1.3, -0.5, 1.7, 2.4 }, 0.01);
		assertEquals("Expected mean 1.03", 1.03, MathUtils.round(actual[0], 2),
				0.1);
		assertEquals("Expected standard deviation 1.6", 1.6, MathUtils.round(
				actual[1], 2), 0.1);
		assertEquals("Expected delta 2.25", 2.25,
				MathUtils.round(actual[2], 2), 0.1);
		assertEquals("Expected lower bound -1.22", -1.22, MathUtils.round(
				actual[3], 2), 0.1);
		assertEquals("Expected upper bound 3.28", 3.28, MathUtils.round(
				actual[4], 2), 0.1);
	}
}
