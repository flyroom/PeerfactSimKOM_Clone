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

package org.peerfact.impl.network.modular.common;

import org.apache.commons.math.random.JDKRandomGenerator;
import org.apache.commons.math.random.RandomGenerator;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.peerfact.impl.network.modular.common.PingErToolkit;
import org.peerfact.impl.network.modular.common.PingErToolkit.JitterParameter;
import org.peerfact.impl.util.logging.SimLogger;

import umontreal.iro.lecuyer.probdist.LognormalDist;

public class PingErToolkitTest {
	private static Logger log = SimLogger.getLogger(PingErToolkitTest.class);

	@Test
	public void test1() {
		double avgRtt = 243.23d;
		double minRtt = 193.7d;
		double dVar = 10.78;

		long startTime = System.nanoTime();
		JitterParameter result = PingErToolkit.getJitterParameterFrom(avgRtt,
				minRtt, dVar);
		log.debug("Calculation lasted "
				+ (System.nanoTime() - startTime) * 0.000001 + " ms");

		LognormalDist testDist = new LognormalDist(1, 0.6);

		RandomGenerator rand = new JDKRandomGenerator();

		for (int i = 0; i < 100; i++) {
			log.debug(testDist.inverseF(rand.nextDouble()));
		}

		log.debug(result);
	}

}
