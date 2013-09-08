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

package org.peerfact.impl.overlay.dht.chord.adaptivechord.objectRing.components;

import org.peerfact.api.transport.TransProtocol;
import org.peerfact.impl.simengine.Simulator;

/**
 * This class contains all configurable parameters
 * 
 * @author Minh Hoang Nguyen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class ChordConfiguration {

	/**
	 * To enable the evaluation functions
	 */
	public final static boolean DO_CHORD_EVALUATION = false;

	/**
	 * Transport protocol, which should be used in Overlay
	 */
	public final static TransProtocol TRANSPORT_PROTOCOL = TransProtocol.UDP;

	/**
	 * If no reply is received in this period, assumably messages loss
	 */
	public final static long MESSAGE_TIMEOUT = 10 * Simulator.SECOND_UNIT;

	/**
	 * If no reply is received in this period, assumably messages loss
	 */
	public final static long OPERATION_TIMEOUT = 2 * Simulator.MINUTE_UNIT;

	/**
	 * If no reply is received, the message will be resent until a certain times
	 * again
	 */
	public final static int MESSAGE_RESEND = 3;

	/**
	 * If an operation failed, the operation will be executed until a certain
	 * times again
	 */
	public final static int OPERATION_MAX_REDOS = 3;

	/**
	 * Maximum time for performing a lookup operation.
	 */
	public final static long LOOKUP_TIMEOUT = 2 * Simulator.MINUTE_UNIT;

	/**
	 * The interval between two operations, which refresh next direct successor
	 */
	public final static long UPDATE_SUCCESSOR_INTERVAL_DATANET = 30 * Simulator.SECOND_UNIT;

	public final static long UPDATE_SUCCESSOR_INTERVAL_LOADNET = 10 * Simulator.SECOND_UNIT;

	/**
	 * The interval between two operations, which refresh finger point
	 */
	public final static long UPDATE_FINGERTABLE_INTERVAL_DATANET = 30 * Simulator.SECOND_UNIT;

	public final static long UPDATE_FINGERTABLE_INTERVAL_LOADNET = 15 * Simulator.SECOND_UNIT;

	/**
	 * The interval between two operations, which refresh a
	 * successor/predecessor in successors/predecessors list
	 */
	public final static long UPDATE_NEIGHBOURS_INTERVAL_DATANET = 30 * Simulator.SECOND_UNIT;

	public final static long UPDATE_NEIGHBOURS_INTERVAL_LOADNET = 5 * Simulator.SECOND_UNIT;

	/**
	 * number of successors/predecessors which are stored and used as back up
	 * for direct successor/predecessor
	 */
	public static int STORED_NEIGHBOURS_DATANET = 3;

	public static int STORED_NEIGHBOURS_LOADNET = 10;

	/**
	 * message will be dropped if whose path exceeds this value
	 */
	public final static int MAX_HOP_COUNT = 50;

	/**
	 * This parameter is used by Gnuplot to plot metrics values by the time t
	 */
	public final static long METRIC_INTERVALL = 5 * Simulator.MINUTE_UNIT;

	/**
	 * The interval between two random lookup operations executed on one host.
	 */
	public final static long TIME_BETWEEN_RANDOM_LOOKUPS = 20 * Simulator.MINUTE_UNIT;

	/**
	 * Maximal time interval between a failed join operation and a retry
	 */
	public final static long MAX_WAIT_BEFORE_JOIN_RETRY = 5 * Simulator.MINUTE_UNIT;

	/**
	 * time interval between two checks if a mirror is still online
	 */
	public final static long CHECK_MIRROR_INTERVAL = 60 * Simulator.SECOND_UNIT;

	/**
	 * Maximum time for performing a lookup operation.
	 */
	public final static long MAX_DOWNLOAD_CHUNK_SIZE = 1024 * 265; // byte
																	// payload
																	// per
																	// message

	public final static long INIT_DOWNLOAD_CHUNK_SIZE = 1024 * 1; // byte
																	// payload
																	// per
																	// message

	public static Integer RANDOM_TIE_BREAKER_SIZE = 2000000000;

	/**
	 * if a node has a smaller maximal bandwidth that this it is considered as
	 * being overloaded.
	 */
	public final static int MINIMAL_USABLE_BANDWIDTH = 1024 * 70; // 70 kByte /
																	// s

	/**
	 * the default minimal free bandwidth of a node that can work as a mirror.
	 */
	public final static int MINIMAL_MIRROR_BANDWIDTH_DEFAULT = 1024 * 150; // 150
																			// kByte
																			// /
																			// s

	/**
	 * increment / decrement factor of MINIMAL_MIRROR_BANDWIDTH_DEFAULT: from
	 * time to time the value is incremented or decremented depending on the
	 * number of failed / succeeded mirror operations.
	 */
	public final static float MIRROR_BANDWIDTH_INCREMENT_FACTOR = 0.2f;

	/**
	 * max count of mirrored elements per node.
	 */
	public final static int MAX_MIRROR_COUNT = 100;

	/**
	 * id of loadnet note only going to change if ratio higher than this
	 */
	public final static float LOADNET_ID_CHANGE_FACTOR = 0.3f;

	/**
	 * time between two loadbalancing operations.
	 */
	public final static long LOADBALANCING_OPERATION_INTERVALL = 3 * Simulator.MINUTE_UNIT;

	public final static boolean USE_LOADBALANCING = true;

}
