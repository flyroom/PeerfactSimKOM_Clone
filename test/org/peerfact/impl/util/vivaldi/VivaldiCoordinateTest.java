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

package org.peerfact.impl.util.vivaldi;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.peerfact.impl.util.vivaldi.VivaldiCoordinate;

public class VivaldiCoordinateTest extends Assert {

	VivaldiCoordinate node1, node2, node3, node4, node5;

	@Before
	public void setUp() {
		node1 = new VivaldiCoordinate();
		node2 = new VivaldiCoordinate();
		node3 = new VivaldiCoordinate();
		node4 = new VivaldiCoordinate();
		node5 = new VivaldiCoordinate();
	}

	@Test
	public void testDelays() {
		node1.update(node4, 30);
		node4.update(node1, 30);

		node1.update(node2, 6);
		node2.update(node1, 6);

		node1.update(node3, 32);
		node3.update(node1, 32);

		node2.update(node4, 32);
		node4.update(node2, 32);

		node2.update(node3, 25);
		node3.update(node2, 25);

		node3.update(node4, 50);
		node4.update(node3, 50);

		node1.update(node4, 30);
		node4.update(node1, 30);

		node1.update(node2, 6);
		node2.update(node1, 6);

		node1.update(node3, 32);
		node3.update(node1, 32);

		node2.update(node4, 32);
		node4.update(node2, 32);

		node2.update(node3, 25);
		node3.update(node2, 25);

		node3.update(node4, 50);
		node4.update(node3, 50);

		node5.update(node1, 75);
		node5.update(node4, 97);
		node5.update(node3, 95);

		node5.update(node1, 75);
		node5.update(node4, 97);
		node5.update(node3, 95);

		assertTrue(78d < node5.distance(node2) && node5.distance(node2) < 82d);
	}

	@After
	public void tearDown() {
		// No implementation here (not sure why) by Thim
		// This text is here so that later on someone will find this.

	}
}
