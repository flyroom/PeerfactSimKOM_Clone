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

package org.peerfact.impl.overlay.dht.kademlia2.measurement;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.peerfact.impl.overlay.dht.kademlia.base.analyzer.util.AvgAccumulator;


/**
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * 
 */
public class AvgAccumulatorTest {

	private AvgAccumulator a;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		a = new AvgAccumulator();
	}

	/**
	 * The average of zero samples is zero.
	 */
	@Test
	public void testZeroSamples() {
		assertEquals("The average of zero samples is zero.", 0d,
				a.getAverage(), 0.1);
	}

	/**
	 * The average of one sample is the sample itself.
	 */
	@Test
	public void testOneSample() {
		a.addToTotal(948);
		assertEquals("The average of one sample is the sample itself.", 948d, a
				.getAverage(), 0.1);
	}

	/**
	 * Tests whether the average is computed correctly.
	 */
	@Test
	public void testAverage() {
		a.addToTotal(39);
		a.addToTotal(4875);
		a.addToTotal(39584587);
		a.addToTotal(1);
		a.addToTotal(0);
		assertEquals("The average should be 7917900.4", 7917900.4d, a
				.getAverage(), 0.1);
	}

}
