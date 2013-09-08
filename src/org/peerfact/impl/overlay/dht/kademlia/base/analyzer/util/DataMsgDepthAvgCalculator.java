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
import java.util.LinkedHashMap;
import java.util.Map;

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
 * Reads data lookup files from stdin, calculates the average distribution of
 * messages over depths, and writes to stout (column1=depth, column2=message
 * ratio).
 * <p>
 * First, the absolute count of messages sent per depth in each lookup is
 * transformed into a depth ratio, that is which part of the messages of one
 * lookup has been sent with which cluster depth. Finally, an average is
 * calculated over all lookups in this representation.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public class DataMsgDepthAvgCalculator {

	/**
	 * @param args
	 *            0: true=consider successful lookups, false=consider failed
	 *            lookups.
	 */
	public static void main(String[] args) throws IOException {
		final boolean expectedResult = Boolean.valueOf(args[0]);
		final BufferedReader in = new BufferedReader(new InputStreamReader(
				System.in));
		final BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
				System.out));
		final Map<Integer, Double> depthAverageSums = new LinkedHashMap<Integer, Double>();

		double totalLookups = 0;
		double sentMsgs;
		Map<Integer, Integer> msgDepths;
		Double currAvgSum;

		String line = null;
		DataLookupQuality tmp;
		while ((line = in.readLine()) != null) {
			tmp = DataLookupQuality.fromString(line);
			if (tmp != null && tmp.successful == expectedResult) {
				totalLookups++;
				sentMsgs = 0;
				msgDepths = tmp.getSentMessages();
				for (final Map.Entry<Integer, Integer> entry : msgDepths
						.entrySet()) {
					sentMsgs += entry.getValue();
				}
				for (final Map.Entry<Integer, Integer> entry : msgDepths
						.entrySet()) {
					currAvgSum = depthAverageSums.get(entry.getKey());
					if (currAvgSum == null) {
						currAvgSum = entry.getValue() / sentMsgs;
					} else {
						currAvgSum += entry.getValue() / sentMsgs;
					}
					depthAverageSums.put(entry.getKey(), currAvgSum);
				}
			}
		}

		out.write("# DEPTH AVG_FRACTION");
		out.newLine();
		for (final Map.Entry<Integer, Double> entry : depthAverageSums
				.entrySet()) {
			out.write(entry.getKey().toString());
			out.append(' ');
			out.write(Double.valueOf(entry.getValue() / totalLookups)
					.toString());
			out.newLine();
		}
		out.close();
	}

}
