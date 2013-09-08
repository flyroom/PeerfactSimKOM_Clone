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

package org.peerfact.impl.service.aggregation.gossip;

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
 * A configuration component capable of supplying the Gossiping Aggregation
 * Service with its configuration parameters
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public interface IConfiguration {

	/**
	 * @return the time after which a response becomes obsolete.
	 */
	public long getReqRespTimeout();

	/**
	 * @return the count of updates from neighbors until the value shall be
	 *         rendered and the process must be restarted, see Section 4.1 (p.
	 *         231)
	 */
	public int getRestartCount();

	/**
	 * @return the time between two active update attempts at random nodes, (see
	 *         parameter 'delta' in Fig 1 on page 222)
	 * 
	 */
	public long getUpdatePeriod();

	/**
	 * @return whether other nodes shall collect updates of values that are not
	 *         defined locally.
	 */
	public boolean gatherMode();

	/**
	 * @return the number of concurrent nodes which shall initiate the node
	 *         counting procedure (parameter C, p. 238)
	 */
	public int getConcurrentNCLeaders();

	/**
	 * Returns the node count that is initially assumed for calculating the
	 * probability of a node to start a node count procedure.
	 * 
	 * @return
	 */
	public int getInitiallyAssumedNodeCount();

	/**
	 * Returns the cycle in which the node count starts. 0 will result in the
	 * behavior described in the paper. A higher value may result in a more
	 * stable node count, because it is likely that all nodes have landed in the
	 * newest epoch, but decreases the number of cycles in which the averaging
	 * procedure "flattens" the node count value.
	 * 
	 * @return
	 */
	public int getNodeCountStartCycle();

	/**
	 * Timeout for resync messages, sent by joining peers to know the beginning
	 * of the next epoch.
	 * 
	 * @return
	 */
	public long getResyncTimeout();

	/**
	 * Whether to ignore incoming merge attempts while an outgoing merge request
	 * (RPC) is in progress.
	 * 
	 * @return
	 */
	public boolean shallLockMergeOnRPC();

}
