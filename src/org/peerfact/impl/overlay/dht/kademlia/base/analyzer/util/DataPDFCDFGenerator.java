/*
 * Copyright (c) 2012-2013 Open Source Community - <http://www.peerfact.org>
 * Copyright (c) 2011-2012 University of Paderborn - UPB
 * Copyright (c) 2005-2011 KOM - Multimedia Communications Lab
 *
 * This file is part of PeerfactSim.KOM.
 * 
 * PeerfactSim.KOM is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 * 
 * PeerfactSim.KOM is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with PeerfactSim.KOM.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.peerfact.impl.overlay.dht.kademlia.base.analyzer.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * This part of the Simulator is not maintained in the current version of
 * PeerfactSim.KOM. There is no intention of the authors to fix this
 * circumstances, since the changes needed are huge compared to overall benefit.
 * 
 * If you want it to work correctly, you are free to make the specific changes
 * and provide it to the community.
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! !!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * 
 * Reads data lookup log files from stdin and writes data for the lookup latency
 * PDF/CDF to stdout (first column: time, second column: probability, third
 * column: cumulative probability). The time values are derived by scaling the
 * latencies from the input (scale factor given as parameter), decimals are cut
 * off and all lookups with the same integer part are grouped together.
 * <p>
 * For instance, if latencies are given in seconds and scale factor is 1000,
 * then the output is sampled at 1ms steps.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public class DataPDFCDFGenerator {

	/**
	 * @param args
	 *            0: true=consider successful lookups, false=consider failed
	 *            lookups. 1: scale factor for latencies (decimals will be cut
	 *            off; is an integer; determines step size for PDF/CDF).
	 */
	public static void main(String[] args) throws IOException {
		final boolean expectedResult = Boolean.valueOf(args[0]);
		final int scale = Integer.valueOf(args[1]);
		final BufferedReader in = new BufferedReader(new InputStreamReader(
				System.in));
		final BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
				System.out));
		final SortedMap<Integer, Integer> timeCountMapping = new TreeMap<Integer, Integer>();

		String line = null;
		DataLookupQuality tmp;
		Integer currCount;
		double total = 0;
		while ((line = in.readLine()) != null) {
			tmp = DataLookupQuality.fromString(line);
			if (tmp != null && tmp.successful == expectedResult) {
				currCount = timeCountMapping
						.get((int) (tmp.getLatency() * scale));
				if (currCount == null) {
					currCount = 1;
				} else {
					currCount++;
				}
				timeCountMapping.put((int) (tmp.getLatency() * scale),
						currCount);
				total++;
			}
		}

		out.write("# TIME PROBABILITY CUMULATIVE_PROBABILITY");
		out.newLine();
		int cumulativeCount = 0;
		for (final Map.Entry<Integer, Integer> entry : timeCountMapping
				.entrySet()) {
			cumulativeCount += entry.getValue();
			out.write(entry.getKey().toString());
			out.append(' ');
			out.write(Double.valueOf(entry.getValue() / total).toString());
			out.append(' ');
			out.write(Double.valueOf(cumulativeCount / total).toString());
			out.newLine();
		}
		out.close();
	}
}
