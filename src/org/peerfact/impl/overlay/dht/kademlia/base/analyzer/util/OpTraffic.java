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

import java.math.BigInteger;
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
 * Information about the traffic caused by an operation.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public final class OpTraffic {

	private static final Pattern filePattern = Pattern
			.compile("^(\\d+) (\\d+) ((\\d+=\\d+, )*)$");

	private static final Pattern item = Pattern.compile("(\\d+)=(\\d+), ");

	private final int opID;

	private final BigInteger ownCluster;

	private final Map<BigInteger, Integer> sentMessagesPerCluster;

	/**
	 * Constructs a new OperationTraffic. All arguments are passed as Strings
	 * and parsed internally.
	 * 
	 * @param operationID
	 *            the operation identifier.
	 * @param ownCluster
	 *            the identifier of the operation owner's cluster.
	 * @param sentMessagesPerCluster
	 *            a Map with cluster identifiers as keys and the number of sent
	 *            messages to that cluster as values.
	 */
	public OpTraffic(final String operationID, final String ownCluster,
			final Map<String, String> sentMessagesPerCluster) {
		this.opID = Integer.valueOf(operationID);
		this.ownCluster = new BigInteger(ownCluster, 2);
		this.sentMessagesPerCluster = new LinkedHashMap<BigInteger, Integer>(
				sentMessagesPerCluster.size(), 1.0f);
		for (final Map.Entry<String, String> entry : sentMessagesPerCluster
				.entrySet()) {
			this.sentMessagesPerCluster.put(new BigInteger(entry.getKey(), 2),
					Integer.valueOf(entry.getValue()));
		}
	}

	/**
	 * @return the identifier of the operation.
	 */
	public final int getOperationID() {
		return opID;
	}

	/**
	 * @return the BigInteger identifier of the operation owner's cluster.
	 */
	public final BigInteger getOwnCluster() {
		return ownCluster;
	}

	/**
	 * @return a Map containing cluster identifiers as keys and the number of
	 *         messages sent to that cluster as values.
	 */
	public final Map<BigInteger, Integer> getSentMessagesPerCluster() {
		return sentMessagesPerCluster;
	}

	/**
	 * Constructs and returns a new OperationTraffic from the given String that
	 * represents one line of a file.
	 * 
	 * @param str
	 *            one line.
	 * @return the new OperationTraffic.
	 */
	public static OpTraffic fromString(final String str) {
		final Matcher line, items;
		final String clusters;
		final Map<String, String> sentMsgs = new LinkedHashMap<String, String>();

		line = filePattern.matcher(str);
		if (!line.matches()) {
			System.err.println("Wrong line: '" + str + "'");
		}
		clusters = line.group(3);
		items = item.matcher(clusters);
		while (items.find()) {
			sentMsgs.put(items.group(1), items.group(2));
		}
		return new OpTraffic(line.group(1), line.group(2), sentMsgs);
	}

}
