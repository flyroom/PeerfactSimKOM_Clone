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

import java.math.BigInteger;

import org.junit.BeforeClass;

/**
 * Tests for Comparators.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 */
public class ComparatorsTest {

	BigInteger zero = BigInteger.ZERO;

	BigInteger one = BigInteger.ONE;

	BigInteger five = BigInteger.valueOf(5);

	BigInteger seven = BigInteger.valueOf(7);

	/**
	 * Configure necessary simulator-wide variables
	 */
	@BeforeClass
	public static void beforeClass() {
		// Create dummy scheduler
		TestHelper.initSimulator();
		// KademliaConfig.ID_LENGTH = 6;
		// KademliaConfig.HIERARCHY_BTREE = 2;
		// KademliaConfig.HIERARCHY_DEPTH = 2;
	}

}
