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

package org.peerfact.impl.scenario;

import junit.framework.Assert;

import org.junit.Test;
import org.peerfact.impl.scenario.DefaultConfigurator;

public class DefaultConfiguratorTest {

	@Test
	public void test1() {

		testWpb("0");
		testWpb("1m");
		testWpb("2m");
		testWpb("3m");
		testWpb("12345678");
		testWpb("999");
		testWpb("1ms");
		testWpb("2ms");
		testWpb("3ms");
		testWpb("1h");
		testWpb("2h");
		testWpb("3h");

	}

	public void testWpb(String value) {
		long valueL = DefaultConfigurator.parseTime(value);
		String valueRes = DefaultConfigurator.writeTime(valueL);
		Assert.assertEquals(value, valueRes);
	}

}
