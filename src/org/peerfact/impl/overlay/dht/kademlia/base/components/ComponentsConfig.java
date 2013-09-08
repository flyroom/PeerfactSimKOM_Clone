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

package org.peerfact.impl.overlay.dht.kademlia.base.components;

import org.peerfact.impl.overlay.dht.kademlia.base.operations.OperationsConfig;

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
 * Configuration settings for Kademlia components. All methods should return
 * constant values.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public interface ComponentsConfig extends RoutingTableConfig, OperationsConfig {

	/**
	 * @return the interval in simulation time units between two routing table
	 *         bucket refresh executions.
	 */
	@Override
	public long getRefreshInterval();

	/**
	 * @return the interval in simulation time units between two data item
	 *         republish executions.
	 */
	public long getRepublishInterval();

	/**
	 * @return the interval in simulation time units between two random data
	 *         lookup operations executed on one host.
	 */
	public long getPeriodicLookupInterval();

	/**
	 * @return the time in simulation time units after which data items are
	 *         evicted from a node's local database (if they have not been
	 *         republished since).
	 */
	public long getDataExpirationTime();

	/**
	 * @return the number of data items that are available in the overlay
	 *         network.
	 */
	public int getNumberOfDataItems();

}
