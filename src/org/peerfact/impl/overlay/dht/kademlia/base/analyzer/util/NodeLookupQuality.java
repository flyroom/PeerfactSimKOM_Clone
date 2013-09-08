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
 * Information about the quality of a node lookup.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public final class NodeLookupQuality {

	private static final Pattern filePattern = Pattern
			.compile("^(\\d+) (\\d+) (\\d+) (\\d+) (\\d+)$");

	private final int opID;

	private final int perfectContacts;

	private final int offlineContacts;

	private final int missedBetterContacts;

	private final int size;

	/**
	 * Constructs a new NodeLookupQuality. All arguments are passed as Strings
	 * and internally parsed.
	 * 
	 * @param operationID
	 *            the operation identifier.
	 * @param perfectContacts
	 *            the number of perfect contacts.
	 * @param offlineContacts
	 *            the number of offline contacts.
	 * @param missedBetterContacts
	 *            the number of missed closer contacts.
	 * @param numberOfContacts
	 *            the total number of contacts.
	 */
	public NodeLookupQuality(final String operationID,
			final String perfectContacts, final String offlineContacts,
			final String missedBetterContacts, final String numberOfContacts) {
		this.opID = Integer.valueOf(operationID);
		this.perfectContacts = Integer.valueOf(perfectContacts);
		this.offlineContacts = Integer.valueOf(offlineContacts);
		this.missedBetterContacts = Integer.valueOf(missedBetterContacts);
		this.size = Integer.valueOf(numberOfContacts);
	}

	/**
	 * @return the identifier of the operation.
	 */
	public final int getOperationID() {
		return opID;
	}

	/**
	 * @return the number of perfect contacts.
	 */
	public final int getPerfectContacts() {
		return perfectContacts;
	}

	/**
	 * @return the number of offline contacts.
	 */
	public final int getOfflineContacts() {
		return offlineContacts;
	}

	/**
	 * @return the number of missed closer contacts.
	 */
	public final int getMissedCloserContacts() {
		return missedBetterContacts;
	}

	/**
	 * @return the total number of contacts found.
	 */
	public final int getSize() {
		return size;
	}

	/**
	 * Constructs and returns a new NodeLookupQuality from the given String that
	 * represents one line of a file.
	 * 
	 * @param str
	 *            one line.
	 * @return the new NodeLookupQuality.
	 */
	public static NodeLookupQuality fromString(final String str) {
		final Matcher m = filePattern.matcher(str);
		if (!m.matches()) {
			System.err.println("Wrong line: '" + str + "'");
		}
		return new NodeLookupQuality(m.group(1), m.group(2), m.group(3), m
				.group(4), m.group(5));
	}
}
