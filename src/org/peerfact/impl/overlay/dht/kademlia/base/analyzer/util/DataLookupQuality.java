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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
 * Information about the quality of a data lookup.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public final class DataLookupQuality {

	// # RESULT_CORRECT LATENCY DEPTH=MSGS_SENT ALL_DATA ONLINE_DATA KCN_DATA
	// ALL_CLOSER ONLINE_CLOSER ONLINE_DATA_CLOSER SENDER_KCN PERFECT_CONTACTS
	// OFFLINE_CONTACTS MISSED_CLOSER_CONTACTS
	// true 0.032991 [4=5,] 42 35 20 13 11 11 true - - -
	// false 2.175604 [4=21,] 30 25 20 - - - - 0 2 20
	// true 4.3E-3 [4=5,] 42 35 20 13 11 11 true - - -
	private static final Pattern filePattern = Pattern
			.compile("^(\\w+) (\\d+\\.\\d+(?:E-\\d+)?) \\[((\\d+=\\d+,)*)\\] (\\d+) (\\d+) (\\d+) (\\d+|-) (\\d+|-) (\\d+|-) (\\w+|-) (\\d+|-) (\\d+|-) (\\d+|-) $");

	private static final Pattern item = Pattern.compile("(\\d+)=(\\d+),");

	public final boolean successful;

	private final double latency;

	private final Map<Integer, Integer> sentMessages;

	private final int allData;

	private final int onlineData;

	private final int kCNData;

	// in successful lookups
	private final int allCloser;

	private final int onlineCloser;

	private final int onlineDataCloser;

	private final boolean senderKCN;

	// in failed lookups
	private final int perfectContacts;

	private final int offlineContacts;

	private final int missedCloserContacts;

	/**
	 * Constructs a new DataLookupQuality. All arguments are Strings and parsed
	 * internally.
	 * 
	 */
	public DataLookupQuality(final String success, final String latency,
			final Map<String, String> sentMessagesPerCluster,
			final String allData, final String onlineData,
			final String kCNData, final String allCloser,
			final String onlineCloser, final String onlineDataCloser,
			final String senderKCN, final String perfectContacts,
			final String offlineContacts, final String missedCloserContacts) {
		this.successful = Boolean.valueOf(success);
		this.latency = Double.valueOf(latency);
		this.sentMessages = new LinkedHashMap<Integer, Integer>(
				sentMessagesPerCluster.size(), 1.0f);
		for (final Map.Entry<String, String> entry : sentMessagesPerCluster
				.entrySet()) {
			this.getSentMessages().put(Integer.valueOf(entry.getKey()), Integer
					.valueOf(entry.getValue()));
		}
		this.allData = Integer.valueOf(allData);
		this.onlineData = Integer.valueOf(onlineData);
		this.kCNData = Integer.valueOf(kCNData);
		if (successful) {
			this.allCloser = Integer.valueOf(allCloser);
			this.onlineCloser = Integer.valueOf(onlineCloser);
			this.onlineDataCloser = Integer.valueOf(onlineDataCloser);
			this.senderKCN = Boolean.valueOf(senderKCN);
			this.perfectContacts = -1;
			this.offlineContacts = -1;
			this.missedCloserContacts = -1;
		} else {
			this.allCloser = -1;
			this.onlineCloser = -1;
			this.onlineDataCloser = -1;
			this.senderKCN = false;
			this.perfectContacts = Integer.valueOf(perfectContacts);
			this.offlineContacts = Integer.valueOf(offlineContacts);
			this.missedCloserContacts = Integer.valueOf(missedCloserContacts);
		}
	}

	/**
	 * Constructs and returns a new DataLookupQuality from the given String that
	 * represents one line of a file.
	 * 
	 * @param str
	 *            one line.
	 * @return the new DataLookupQuality.
	 */
	public static DataLookupQuality fromString(final String str) {
		final Matcher line, items;
		final String clusters;
		final Map<String, String> sentMsgs = new LinkedHashMap<String, String>();

		line = filePattern.matcher(str);
		if (!line.matches()) {
			System.err.println("Wrong line: '" + str + "'");
			return null;
		}
		clusters = line.group(3);
		items = item.matcher(clusters);
		while (items.find()) {
			sentMsgs.put(items.group(1), items.group(2));
		}
		return new DataLookupQuality(line.group(1), line.group(2), sentMsgs,
				line.group(5), line.group(6), line.group(7), line.group(8),
				line.group(9), line.group(10), line.group(11), line.group(12),
				line.group(13), line.group(14));
	}

	public double getLatency() {
		return latency;
	}

	public Map<Integer, Integer> getSentMessages() {
		return sentMessages;
	}

	public int getAllData() {
		return allData;
	}

	public int getOnlineData() {
		return onlineData;
	}

	public int getkCNData() {
		return kCNData;
	}

	public int getAllCloser() {
		return allCloser;
	}

	public int getOnlineCloser() {
		return onlineCloser;
	}

	public int getOnlineDataCloser() {
		return onlineDataCloser;
	}

	public boolean isSenderKCN() {
		return senderKCN;
	}

	public int getPerfectContacts() {
		return perfectContacts;
	}

	public int getOfflineContacts() {
		return offlineContacts;
	}

	public int getMissedCloserContacts() {
		return missedCloserContacts;
	}

}
