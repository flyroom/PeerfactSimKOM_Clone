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

package org.peerfact.impl.application.filesharing2;

import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.peerfact.impl.util.logging.SimLogger;
import org.peerfact.impl.util.stats.distributions.ZipfDistribution;


public class ZipfTest {

	private static Logger log = SimLogger
			.getLogger(ZipfTest.class);

	static int count = 100000;

	static int size = 40;

	static double zipfExp = 0.9;

	static Map<Integer, Integer> ranks = new TreeMap<Integer, Integer>();

	private static ZipfDistribution dist;

	public static void main(String[] args) {

		dist = new ZipfDistribution(size, zipfExp);

		for (int i = 0; i < count; i++) {
			putRank(getValue());
		}

		dumpRanks();

	}

	static int getValue() {
		return (int) (dist.returnValue() * size);
	}

	static void putRank(int keyForPublish) {
		Integer rankCount = ranks.get(keyForPublish);

		if (rankCount == null) {
			ranks.put(keyForPublish, 1);
		} else {
			ranks.put(keyForPublish, rankCount + 1);
		}
	}

	static void dumpRanks() {
		for (Integer rank : ranks.keySet()) {
			log.debug(rank + "	" + ranks.get(rank));
		}
	}

}
