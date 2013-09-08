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

package org.peerfact.impl.overlay.dht.pastry.components;

import org.peerfact.impl.overlay.dht.pastry.proximity.DummyProximityMetric;
import org.peerfact.impl.overlay.dht.pastry.proximity.ProximityMetricProvider;
import org.peerfact.impl.simengine.Simulator;

/**
 * This class defines all configurable constants of this overlay.
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class PastryConstants {

	/**
	 * Bit length of the id
	 */
	public static final int ID_BIT_LENGTH = 128; // 128

	/**
	 * Maximal number of routing table entries for the same index.
	 * 
	 * Integer.MAX_VALUE means it is not bounded. Attention: this will slow down
	 * a simulation drastically!
	 */
	public static final int MAX_REDUNDANT_ROUTING_ENTRIES = 2;

	/**
	 * The maximum size of a node's leaf set
	 */
	public static final int MAX_SIZE_OF_LEAFSET = 10;

	/**
	 * The maximum size of a node's neighborhood set
	 */
	public static final int MAX_SIZE_OF_NEIGHBORHOODSET = 10;

	/**
	 * This parameter is called "b" within the paper and denotes the step size
	 * in bits when matching prefixes. The default configuration is b=4, which
	 * allows us to interpret each step as the matching of one hexadecimal
	 * cipher.
	 */
	public static final int ID_BASE_BIT_LENGTH = 4;

	/**
	 * This is the proximity metric provider used.
	 * 
	 * FIXME: Replace the dummy proximity metric provider with a better
	 * implementation.
	 */
	public static final ProximityMetricProvider PROXIMITY_PROVIDER = new DummyProximityMetric();

	/**
	 * The port the pastry overlay registers for at the transport layer.
	 */
	public static final short PASTRY_PORT = 500;

	/**
	 * The general used timeout for operations
	 */
	public static final long OP_TIMEOUT = 1 * Simulator.MINUTE_UNIT;

	/**
	 * The timeout for join operations
	 */
	public static final long OP_JOIN_TIMEOUT = 1 * Simulator.MINUTE_UNIT;

	/**
	 * Time between tries to update a RouteSet
	 */
	public static final long OP_ROUTE_SET_RETRY_DELAY = 20 * Simulator.MINUTE_UNIT;

	/**
	 * The number of maximal retries when an operations fails
	 */
	public static final int OP_MAX_RETRIES = 5;

	/**
	 * The number of maximal retries when joining was not successful
	 */
	public static final int OP_JOIN_MAX_RETRIES = 3;

	/**
	 * The timeout used when sending messages
	 */
	public static final long MSG_TIMEOUT = 5 * Simulator.SECOND_UNIT;

	/**
	 * The number of maximal message retransmissions
	 */
	public static final int MSG_MAX_RETRANSMISSIONS = 3;

	/**
	 * The maximal number of alternative tries to route a message again if it
	 * was not delivered due to failing next hops
	 */
	public static final int MSG_MAX_ALTERNATIVE_HOPS = 3;

	/**
	 * The maximal number of hops used to route a message. If this number is
	 * reached the routing is failed.
	 */
	public static final int MSG_MAX_HOPS = 50;
}
