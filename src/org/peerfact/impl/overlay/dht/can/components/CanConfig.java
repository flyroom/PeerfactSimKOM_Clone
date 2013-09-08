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

package org.peerfact.impl.overlay.dht.can.components;

import org.peerfact.impl.simengine.Simulator;

/**
 * This static class sets the parameter for the CAN
 * 
 * @author Bjoern Dollak <peerfact@kom.tu-darmstadt.de>
 * @version February 2010
 * 
 */
public class CanConfig {

	/**
	 * the size of the CAN. Remember it's a CanSize x CanSize
	 */
	public final static short CanSize = 10000;

	/**
	 * Prepared the overload extension. This isn't implemented right now.
	 */
	public final static int overloadNumber = 0;

	/**
	 * the distribution of the peers: 0=uniform , 1=random , 2=uniform as it is
	 * introduced in the CAN paper.
	 * 
	 * (0: always the node with the biggest area square is taken from the
	 * bootstrap manager. 1: A random node gets the join message. 2: the node
	 * which receive the join message checks if one of its neighbours has a
	 * bigger square area. If it finds a bigger area, it sends the join message
	 * towards this node.)
	 */
	public final static int distribution = 2;

	/**
	 * sets the size of the visualization
	 */
	public final static int VisSize = CanSize / 750;

	// message size

	// public final static long idSize = 20;
	//
	// public final static long CanVIDSize = 10;
	//
	// public final static long transInfoSize = 5;
	//
	// public final static long isAliveSize = 1;
	//
	// public final static long CanAreaSize = CanVIDSize + 4
	// * Constants.SHORT_SIZE;
	//
	// public final static long CanOverlayIDSize = idSize;
	//
	// public final static long CanOverlayContactSize = CanOverlayIDSize
	// + transInfoSize + isAliveSize + CanAreaSize;
	//
	// public final static long hashSize = idSize + CanOverlayContactSize;

	/**
	 * max Hop number If the hop number in the message is higher than this
	 * value, the message is not send any further. It is just deleted.
	 */
	public final static short lookupMaxHop = 255;

	/**
	 * wait time time between leave and reorganize. This time is a kind of a
	 * problem, because if the peer has many neighbours this time should be
	 * high. But if it is high, it causes a lot of other problems. Therefore it
	 * is could to have a uniform distributed CAN, then this time is mostly
	 * almost the same. Another idea is it to make the time dependent from the
	 * online time of the peer.
	 */
	public final static long waitTimeAfterLeave = 60 * Simulator.SECOND_UNIT;

	/**
	 * wait time between two ping messages.
	 */
	public final static long waitTimeBetweenPing = Simulator.SECOND_UNIT * 20;

	/**
	 * set the timeout for ping messages
	 */
	public final static long timeout = waitTimeBetweenPing;

	/**
	 * wait time between the timed out ping and the TekoverOperation. In this
	 * interval all other peers with the same parents should send the
	 * TakeoverReorganizeReplyMsg. It has the same problem as the
	 * waitTimeAfterLeave.
	 */
	public final static long waitForTakeover = 4 * waitTimeBetweenPing;

	/**
	 * Time before a lookup or store message times out.
	 */
	public final static long waitTimeToStore = Simulator.SECOND_UNIT * 30;

	/**
	 * Every node has to refresh its hashes in this time interval.
	 */
	public final static long waitTimeToRefreshHash = Simulator.MINUTE_UNIT * 5;

	/**
	 * If a lookup hasn't succeeded the peer will try it again.
	 */
	public final static int numberLookups = 3;

	/**
	 * If a pong answer doesn't arrive at a peer it will send a ping message
	 * numberPings times again.
	 */
	public final static int numberPings = 3;

}
