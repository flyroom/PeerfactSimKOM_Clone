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

package org.peerfact.impl.network;

import java.util.Random;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.peerfact.impl.network.IPv4NetID;
import org.peerfact.impl.util.logging.SimLogger;


public class IPv4NetIDTest {

	private static Logger log = SimLogger.getLogger(IPv4NetIDTest.class);

	Random random = new Random();

	@Test
	public void test1() {
		testStrIntStr("255.255.255.255");
		testStrIntStr("1.1.1.1");
	}

	@Test
	public void test2() {
		for (int i = 0; i < 20000; i++) {
			testRandom();
		}
	}

	public void testRandom() {
		String ip = get0To255() + "." + get0To255() + "." + get0To255() + "."
				+ get0To255();
		testStrIntStr(ip);
		testStrIntLongStr(ip);
		testStrLongIntStr(ip);
	}

	public String get0To255() {
		return String.valueOf(random.nextInt(256));
	}

	void testStrIntStr(String ipStr) {
		int ip = IPv4NetID.ipToInt(ipStr);
		String ipStr2 = IPv4NetID.intToIP(ip);
		log.debug("Test: " + ipStr + " => " + ip + " => " + ipStr2);
		Assert.assertEquals(ipStr, ipStr2);
	}

	void testStrIntLongStr(String ipStr) {
		int ip = IPv4NetID.ipToInt(ipStr);
		long ipL = IPv4NetID.intToLong(ip);
		String ipStr2 = IPv4NetID.ipToString(ipL);
		log.debug("Test: " + ipStr + " => " + ip + " => " + ipL
				+ " => " + ipStr2);
		Assert.assertEquals(ipStr, ipStr2);
	}

	void testStrLongIntStr(String ipStr) {
		long ipL = IPv4NetID.ipToLong(ipStr);
		int ip = IPv4NetID.longToInt(ipL);
		String ipStr2 = IPv4NetID.intToIP(ip);
		log.debug("Test: " + ipStr + " => " + ip + " => " + ipL
				+ " => " + ipStr2);
		Assert.assertEquals(ipStr, ipStr2);
	}

}
