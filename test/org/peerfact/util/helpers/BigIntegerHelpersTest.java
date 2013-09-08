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

package org.peerfact.util.helpers;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;

import org.junit.Test;
import org.peerfact.impl.util.toolkits.BigIntegerHelpers;


/**
 * Tests BigIntegerHelpers.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 */
public class BigIntegerHelpersTest {

	BigInteger zero = BigInteger.ZERO; // 0

	BigInteger one = BigInteger.ONE; // 1

	BigInteger two = BigInteger.valueOf(2); // 10

	BigInteger three = BigInteger.valueOf(3); // 11

	BigInteger five = BigInteger.valueOf(5); // 101

	BigInteger seven = BigInteger.valueOf(7); // 111

	BigInteger fifteen = BigInteger.valueOf(15); // 1111

	BigInteger twohundredfifteen = BigInteger.valueOf(215); // 1101 0111

	/**
	 * Tests whether getNRightmostBits actually returns the n rightmost bits.
	 */
	@Test
	public void testGgetNRightmostBits() {
		assertEquals("2 rightmost bits of 0 are 0", zero, BigIntegerHelpers
				.getNRightmostBits(zero, 2));
		assertEquals("2 rightmost bits of 1 are 1", one, BigIntegerHelpers
				.getNRightmostBits(one, 2));
		assertEquals("2 rightmost bits of 101 are 1", one, BigIntegerHelpers
				.getNRightmostBits(five, 2));
		assertEquals("2 rightmost bits of 11 are 11", three, BigIntegerHelpers
				.getNRightmostBits(three, 2));
		assertEquals("rightmost bit of 111 is 1", one, BigIntegerHelpers
				.getNRightmostBits(seven, 1));
		assertEquals("2 rightmost bits of 111 are 11", three, BigIntegerHelpers
				.getNRightmostBits(seven, 2));
		assertEquals("2 rightmost bits of 1111 are 11", three,
				BigIntegerHelpers.getNRightmostBits(fifteen, 2));
		assertEquals("3 rightmost bits of 1111 are 111", seven,
				BigIntegerHelpers.getNRightmostBits(fifteen, 3));
		assertEquals("2 rightmost bits of 1101 0111 are 11", three,
				BigIntegerHelpers.getNRightmostBits(twohundredfifteen, 2));
		assertEquals("8 rightmost bits of 1101 0111 are 1101 0111",
				twohundredfifteen, BigIntegerHelpers.getNRightmostBits(
						twohundredfifteen, 8));
	}

	/**
	 * Test shiftLeft.
	 */
	@Test
	public void testShiftLeft() {
		assertEquals("0 with 0 shifted in is 0", zero, BigIntegerHelpers
				.shiftLeft(zero, zero, 1));
		assertEquals("0 with one bit of 11 shifted in is 1", one,
				BigIntegerHelpers.shiftLeft(zero, three, 1));
		assertEquals("0 with one bit of 10 shifted in is 0", zero,
				BigIntegerHelpers.shiftLeft(zero, two, 1));
		assertEquals("0 with 11 shifted in is 11", three, BigIntegerHelpers
				.shiftLeft(zero, three, 2));
		assertEquals("0 with no bit of 11 shifted in is 0", zero,
				BigIntegerHelpers.shiftLeft(zero, three, 0));
		assertEquals("11 with no bit of 11 shifted in is 11", three,
				BigIntegerHelpers.shiftLeft(three, three, 0));
		assertEquals("0 with 11, 01, 01, 11 shifted in is 11010111",
				twohundredfifteen, BigIntegerHelpers.shiftLeft(
						BigIntegerHelpers.shiftLeft(BigIntegerHelpers
								.shiftLeft(BigIntegerHelpers.shiftLeft(zero,
										three, 2), one, 2), one, 2), three, 2));
	}

	/**
	 * Test getNthBitstring.
	 */
	@Test
	public void testGgetNthBitstring() {
		assertEquals("zero-level bit of 11010111 is 1", one, BigIntegerHelpers
				.getNthBitstring(twohundredfifteen, 7, 1));
		assertEquals("1st-level bit of 11010111 is 1", one, BigIntegerHelpers
				.getNthBitstring(twohundredfifteen, 6, 1));
		assertEquals("2nd-level bit of 11010111 is 0", zero, BigIntegerHelpers
				.getNthBitstring(twohundredfifteen, 5, 1));
		assertEquals("7th-level bit of 11010111 is 1", one, BigIntegerHelpers
				.getNthBitstring(twohundredfifteen, 0, 1));

		assertEquals("two zero-level bits of 11010111 are 11", three,
				BigIntegerHelpers.getNthBitstring(twohundredfifteen, 3, 2));
		assertEquals("two 1st-level bits of 11010111 are 01", one,
				BigIntegerHelpers.getNthBitstring(twohundredfifteen, 2, 2));
		assertEquals("two 2nd-level bits of 11010111 are 01", one,
				BigIntegerHelpers.getNthBitstring(twohundredfifteen, 1, 2));
		assertEquals("two 3rd-level bits of 11010111 are 11", three,
				BigIntegerHelpers.getNthBitstring(twohundredfifteen, 0, 2));

		assertEquals("three 1st-level bits of 11010111 are 010", two,
				BigIntegerHelpers.getNthBitstring(twohundredfifteen, 1, 3));
	}

}
