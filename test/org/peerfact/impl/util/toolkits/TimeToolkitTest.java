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

package org.peerfact.impl.util.toolkits;

import static org.junit.Assert.assertEquals;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;
import org.peerfact.impl.util.toolkits.TimeToolkit;

public class TimeToolkitTest {

	TimeToolkit tk = new TimeToolkit(1000);

	@Before
	public void onBefore() {
		tk.setSeparateTimeUnitsWhitespace(false);
	}

	@Test
	public void testSomeValidInputs() {

		assertEquals(0l, tk.longFromTimeString("0ms"));
		assertEquals(1000l, tk.longFromTimeString("1ms"));
		assertEquals(1020000l, tk.longFromTimeString("1s20ms"));
		assertEquals(10020000l, tk.longFromTimeString("10s20ms"));
		assertEquals(70020000l, tk.longFromTimeString("1m10s20ms"));
		assertEquals(610020000l, tk.longFromTimeString("10m10s20ms"));
		assertEquals(4210020000l, tk.longFromTimeString("1h10m10s20ms"));
		assertEquals(3600610020000l, tk.longFromTimeString("1000h10m10s20ms"));

		assertEquals(3600000000l, tk.longFromTimeString("1h"));
		assertEquals(1200000000l, tk.longFromTimeString("20m"));
		assertEquals(200000000l, tk.longFromTimeString("200s"));
		assertEquals(3600020000l, tk.longFromTimeString("20ms1h"));
		assertEquals(36060020000l, tk.longFromTimeString("20ms1m10h"));

		assertEquals(19340912090l, tk.longFromTimeString("19340912090"));

		assertEquals(4210020000l, tk.longFromTimeString("1h 10m10s20ms"));
		assertEquals(3601600020000l,
				tk.longFromTimeString("1000 h10m 1.000s20ms"));

		assertEquals(10020000l, tk.longFromTimeString("10s 20 ms"));
	}

	@Test
	public void testSomeInvalidInputs() {
		Random random = new Random();
		byte[] bytes = new byte[20];
		for (int i = 0; i < 200000; i++) {
			random.nextBytes(bytes);
			String str = String.valueOf(bytes);
			assertEquals(-1l, tk.longFromTimeString(str));
		}
	}

	@Test
	public void testIO() {

		testIO(0);
		testIO(1000);
		testIO(2000);
		testIO(10000);
		testIO(100000);
		testIO(1000000);
		testIO(10000000);
		testIO(100000000);

		for (int i = 0; i < 200000; i++) {
			testIO((long) (Math.random() * 100000000) * 1000);
		}
	}

	void testIO(long testVal) {
		String timeStr = tk.timeStringFromLong(testVal);
		long testOut = tk.longFromTimeString(timeStr);
		assertEquals(testVal, testOut);
	}

}
