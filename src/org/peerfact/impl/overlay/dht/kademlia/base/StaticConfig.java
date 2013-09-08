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

package org.peerfact.impl.overlay.dht.kademlia.base;

import org.peerfact.impl.simengine.Simulator;

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
 * Repository for configuration values ("constants"). This class provides
 * "static" compile-time constants.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public final class StaticConfig implements Config {

	private static final int bucketSize = 20;

	private static final int idLength = 160;

	private static final long refreshInterval = Simulator.HOUR_UNIT;

	private static final int replacementCacheSize = 20;

	private static final int routingTreeOrder = 2;

	private static final int staleCounter = 2;

	private static final int hierarchyDepth = 0;

	private static final int hierarchyTreeOrder = 2;

	private static final int dataSize = 100;

	private static final int numberOfInitialRoutingTableContacts = 100;

	private static final long lookupMessageTimeout = 2 * Simulator.SECOND_UNIT;

	private static final long lookupOperationTimeout = 45 * Simulator.SECOND_UNIT;

	private static final int maxConcurrentLookups = 3;

	private static final long dataExpirationTime = (long) (1.3 * Simulator.HOUR_UNIT);

	private static final int numberOfDataItems = 10000;

	private static final int numberOfPeers = 10000;

	private static final long periodicLookupInterval = 10 * Simulator.MINUTE_UNIT;

	private static final long republishInterval = 1 * Simulator.HOUR_UNIT;

	private static final String clusterMappingFilePath = "config/overlay-dht/kademlia/kademlia2-clusterMapping.properties";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int getBucketSize() {
		return bucketSize;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int getIDLength() {
		return idLength;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final long getRefreshInterval() {
		return refreshInterval;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int getReplacementCacheSize() {
		return replacementCacheSize;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int getRoutingTreeOrder() {
		return routingTreeOrder;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int getStaleCounter() {
		return staleCounter;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int getHierarchyDepth() {
		return hierarchyDepth;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int getHierarchyTreeOrder() {
		return hierarchyTreeOrder;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int getDataSize() {
		return dataSize;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int getNumberOfInitialRoutingTableContacts() {
		return numberOfInitialRoutingTableContacts;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final long getLookupMessageTimeout() {
		return lookupMessageTimeout;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final long getLookupOperationTimeout() {
		return lookupOperationTimeout;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int getMaxConcurrentLookups() {
		return maxConcurrentLookups;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final long getDataExpirationTime() {
		return dataExpirationTime;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int getNumberOfDataItems() {
		return numberOfDataItems;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int getNumberOfPeers() {
		return numberOfPeers;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final long getPeriodicLookupInterval() {
		return periodicLookupInterval;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final long getRepublishInterval() {
		return republishInterval;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String getClusterMappingFilePath() {
		return clusterMappingFilePath;
	}

}
