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

package org.peerfact.impl.util.evaluation;

import org.apache.log4j.Logger;
import org.peerfact.impl.util.logging.SimLogger;
import org.peerfact.impl.util.toolkits.AverageAccumulator;
import org.peerfact.impl.util.toolkits.NumberFormatToolkit;


public class AverageAccumulatorTest {
	private static Logger log = SimLogger
			.getLogger(AverageAccumulatorTest.class);

	public static void main(String[] args) {
		int experimentSize = 10000000;

		AverageAccumulator accu = new AverageAccumulator();

		for (int i = 0; i < experimentSize; i++) {
			accu.accumulate(Math.random());
		}

		log.debug(accu.returnAverage()
				+ ", should return something around 0.5");

		log.debug("Free: "
				+ getMemStr(Runtime.getRuntime().freeMemory())
				+ "Max: " + getMemStr(Runtime.getRuntime().maxMemory())
				+ "Total: " + getMemStr(Runtime.getRuntime().totalMemory())
				);

	}

	public static String getMemStr(long mem) {
		return NumberFormatToolkit.floorToDecimalsString(mem / 1048576, 2)
				+ " MB";
	}

}
