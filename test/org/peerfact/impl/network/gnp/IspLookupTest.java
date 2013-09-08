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

package org.peerfact.impl.network.gnp;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.peerfact.impl.network.gnp.geoip.IspLookupService;


public class IspLookupTest {

	IspLookupService ls;

	@Before
	public void setUp() {
		ls = new IspLookupService("data/GeoIP/GeoIPISP.csv");
	}

	@Test
	public void testLookup() {
		String result;

		// 33996344,33996351,"BT North Block Cambridge"
		result = ls.getISP(33996344);
		assertEquals(result, "BT North Block Cambridge");
		result = ls.getISP(33996351);
		assertEquals(result, "BT North Block Cambridge");

		// 67108864,83886079,"Level 3 Communications"
		result = ls.getISP(83886060);
		assertEquals(result, "Level 3 Communications");

		// 1503657984,1503690751,"Skycom Nordic AB"
		result = ls.getISP(1503690743);
		assertEquals(result, "Skycom Nordic AB");

	}

	@After
	public void tearDown() {
		// No implementation here (not sure why) by Thim
		// This text is here so that later on someone will find this.

	}

}
