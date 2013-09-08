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

import org.peerfact.impl.overlay.dht.kademlia.base.operations.AbstractKademliaOperation.OperationState;


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
 * Delay information about one operation.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public final class OpDelay {

	private static final Pattern filePattern = Pattern
			.compile("^(\\d+) ([\\d\\w.$]+) (\\w+) (\\d+(.\\d+)*)$");

	/**
	 * The type of the operation.
	 */
	public enum OperationType {

		DATA {
			@Override
			public String toString() {
				return "kademlia2.operations.lookup.DataLookupOperation";
			}
		},

		KCLOSESTNODES {
			@Override
			public String toString() {
				return "kademlia2.operations.lookup.KClosestNodesLookupOperation";
			}
		},

		HBUCKET {
			@Override
			public String toString() {
				return "kademlia2.operations.lookup.BucketLookupOperation$HierarchicalBucketLookupOperation";
			}
		},

		REPUBLISH {
			@Override
			public String toString() {
				return "kademlia2.operations.RepublishOperation";
			}
		},

		RTBUILD {
			@Override
			public String toString() {
				return "kademlia2.operations.RoutingTableBuildOperation";
			}
		},

		REFRESH {
			@Override
			public String toString() {
				return "kademlia2.operations.BucketRefreshOperation";
			}
		},

		STORE {
			@Override
			public String toString() {
				return "kademlia2.operations.StoreOperation";
			}
		},

		OTHER {
			@Override
			public String toString() {
				return "kademlia2.operations.?";
			}
		};

		@Override
		public abstract String toString();
	}

	private final int opID;

	private final OperationType type;

	private final OperationState outcome;

	private final double latency;

	/**
	 * Constructs a new OpDelay. All arguments are passed as Strings and
	 * internally parsed.
	 * 
	 * @param operationID
	 *            the operation identifier.
	 * @param className
	 *            the class name of the operation (as returned by
	 *            Class.getName()).
	 * @param finalState
	 *            the final state of the operation (as returned by
	 *            OperationState.toString()).
	 * @param duration
	 *            the latency of the operation (in seconds).
	 */
	public OpDelay(final String operationID, final String className,
			final String finalState, final String duration) {
		OperationType tmpType = OperationType.OTHER;

		this.opID = Integer.valueOf(operationID);
		for (final OperationType t : OperationType.values()) {
			if (t.toString().equals(className)) {
				tmpType = t;
				break;
			}
		}
		this.type = tmpType;

		outcome = OperationState.valueOf(finalState);
		latency = Double.valueOf(duration);
	}

	/**
	 * @return the identifier of the operation.
	 */
	public final int getOperationID() {
		return opID;
	}

	/**
	 * @return the type of the operation.
	 */
	public final OperationType getOperationType() {
		return type;
	}

	/**
	 * @return the state of the operation.
	 */
	public final OperationState getOperationState() {
		return outcome;
	}

	/**
	 * @return the latency of the operation in seconds.
	 */
	public final double getLatency() {
		return latency;
	}

	/**
	 * Constructs and returns a new OpDelay from the given String that
	 * represents one line of a file.
	 * 
	 * @param str
	 *            one line.
	 * @return the new OpDelay.
	 */
	public static OpDelay fromString(final String str) {
		final Matcher m = filePattern.matcher(str);
		if (!m.matches()) {
			System.err.println("Wrong line: '" + str + "'");
		}
		return new OpDelay(m.group(1), m.group(2), m.group(3), m.group(4));
	}
}
