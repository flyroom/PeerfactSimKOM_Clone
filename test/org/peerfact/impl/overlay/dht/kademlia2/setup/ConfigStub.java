/*
 * Copyright (c) 2012-2013 Open Source Community - <http://www.peerfact.org>
 * Copyright (c) 2011-2012 University of Paderborn - UPB
 * Copyright (c) 2005-2011 KOM - Multimedia Communications Lab
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */

package org.peerfact.impl.overlay.dht.kademlia2.setup;

import org.peerfact.impl.overlay.dht.kademlia.base.Config;
import org.peerfact.impl.overlay.dht.kademlia.base.StaticConfig;

/**
 * Test stub implementation of Config. In contrast to Config's contract, the
 * values returned by this class are <i>not</i> constant over time. That is,
 * they may be changed for testing purposes.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 */
public class ConfigStub implements Config {

	public int bucketSize;

	public int idLength;

	public long refreshInterval;

	public int replacementCacheSize;

	public int routingTreeOrder;

	public int staleCounter;

	public int hierarchyDepth;

	public int hierarchyTreeOrder;

	public int dataSize;

	public int numberOfInitialRoutingTableContacts;

	public long lookupMessageTimeout;

	public long lookupOperationTimeout;

	public int maxConcurrentLookups;

	public long dataExpirationTime;

	public int numberOfDataItems;

	public int numberOfPeers;

	public long periodicLookupInterval;

	public long republishInterval;

	public String clusterMappingFilePath;

	public ConfigStub() {
		// get default values from StaticConfig
		Config defaultValues = new StaticConfig();
		bucketSize = defaultValues.getBucketSize();
		idLength = defaultValues.getIDLength();
		refreshInterval = defaultValues.getRefreshInterval();
		replacementCacheSize = defaultValues.getReplacementCacheSize();
		routingTreeOrder = defaultValues.getRoutingTreeOrder();
		staleCounter = defaultValues.getStaleCounter();
		hierarchyDepth = defaultValues.getHierarchyDepth();
		hierarchyTreeOrder = defaultValues.getHierarchyTreeOrder();
		dataSize = defaultValues.getDataSize();
		numberOfInitialRoutingTableContacts = defaultValues
				.getNumberOfInitialRoutingTableContacts();
		lookupMessageTimeout = defaultValues.getLookupMessageTimeout();
		lookupOperationTimeout = defaultValues.getLookupOperationTimeout();
		maxConcurrentLookups = defaultValues.getMaxConcurrentLookups();
		dataExpirationTime = defaultValues.getDataExpirationTime();
		numberOfDataItems = defaultValues.getNumberOfDataItems();
		numberOfPeers = defaultValues.getNumberOfPeers();
		periodicLookupInterval = defaultValues.getPeriodicLookupInterval();
		republishInterval = defaultValues.getRepublishInterval();
		clusterMappingFilePath = defaultValues.getClusterMappingFilePath();
	}

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
