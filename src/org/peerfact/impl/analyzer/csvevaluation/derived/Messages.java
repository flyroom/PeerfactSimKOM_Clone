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

package org.peerfact.impl.analyzer.csvevaluation.derived;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

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
 * 
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public class Messages {

	public static void main(String[] args) {
		try {
			new Messages("fs_gnutella").start();
			new Messages("fs_kademlia").start();
			new Messages("fs_gia").start();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	private String overlayName;

	private Map<Integer, Map<String, Double>> values = new TreeMap<Integer, Map<String, Double>>();

	private Set<String> allSeenMsgTypes = new TreeSet<String>();

	public Messages(String overlayName) {
		this.overlayName = overlayName;
	}

	public void start() throws FileNotFoundException, IOException {
		BufferedReader str = new BufferedReader(new FileReader("outputs/"
				+ overlayName + "/messages"));
		BufferedWriter wr = new BufferedWriter(new FileWriter("outputs/"
				+ overlayName + "/messagesRatios"));

		wr.write("# Hosts	MsgType	Ratio\n");

		String line;

		List<String> actualNames = new ArrayList<String>();

		String hostsPrefix = "# Hosts";

		while ((line = str.readLine()) != null) {

			if (line.trim().startsWith("#")) {
				if (line.trim().startsWith(hostsPrefix)) {
					actualNames = handleNamesLine(line.substring(hostsPrefix
							.length()));
				}
			} else if (!(line.trim().equals(""))) {
				handleValuesLine(line, actualNames);
			}
		}

		fillMissingKeys();

		dumpOut(wr);

		wr.close();
		str.close();

	}

	private void fillMissingKeys() {
		for (Entry<Integer, Map<String, Double>> entry : values.entrySet()) {
			for (String type : allSeenMsgTypes) {
				if (!entry.getValue().containsKey(type)) {
					entry.getValue().put(type, 0d);
				}
			}
		}
	}

	private List<String> handleNamesLine(String line) {

		List<String> result = new ArrayList<String>();

		String[] lineParts = line.split("	");

		for (int i = 0; i < lineParts.length; i++) {
			String msgType = lineParts[i].trim();
			result.add(msgType);
			allSeenMsgTypes.add(msgType);
		}

		return result;

	}

	private void handleValuesLine(String line, List<String> actualNames) {

		Map<String, Double> results = new TreeMap<String, Double>();

		if (line.trim().equals("") || line.trim().startsWith("#")) {
			return;
		}

		String[] lineParts = line.split("	");
		if (lineParts.length <= 1) {
			return;
		}

		int hostCount = Integer.valueOf(lineParts[0].trim());

		Integer[] fields = new Integer[lineParts.length - 1];
		Integer fieldTotalCount = 0;

		for (int i = 1; i < lineParts.length; i++) {
			fields[i - 1] = Integer.parseInt(lineParts[i]);
			fieldTotalCount += fields[i - 1];
		}

		for (int i = 0; i < fields.length; i++) {
			results.put(actualNames.get(i), (double) fields[i]
					/ fieldTotalCount);
		}

		Map<String, Double> oldMap = values.get(hostCount);

		if (oldMap != null) {
			mergeMaps(oldMap, results);
		} else {
			values.put(hostCount, results);
		}
	}

	public static void mergeMaps(Map<String, Double> target,
			Map<String, Double> source) {
		for (Entry<String, Double> e : source.entrySet()) {
			Double targetVal = target.get(e.getKey());
			if (targetVal != null) {
				target.put(e.getKey(), (targetVal + e.getValue()) / 2);
			} else {
				target.put(e.getKey(), e.getValue());
			}
		}
	}

	public void dumpOut(BufferedWriter wr) throws IOException {

		wr.write("Hosts	");

		for (String type : allSeenMsgTypes) {
			wr.write(type + "	");
		}
		wr.write('\n');

		for (Entry<Integer, Map<String, Double>> entry : values.entrySet()) {

			wr.write(entry.getKey() + "  ");

			for (Entry<String, Double> subEntry : entry.getValue().entrySet()) {
				wr.write(subEntry.getValue() + "	");
			}

			wr.write('\n');
		}
	}

}
