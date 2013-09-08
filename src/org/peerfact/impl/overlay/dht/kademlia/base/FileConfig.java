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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.util.logging.SimLogger;


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
 * Repository for configuration values ("constants"). This class permits to read
 * these constants from a properties file and provides default values for those
 * properties that were not defined in the file. (The default values are read
 * from a {@link StaticConfig} instance.)
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public final class FileConfig implements Config {

	private final static Logger log = SimLogger.getLogger(FileConfig.class);

	private final int bucketSize;

	private final int idLength;

	private final long refreshInterval;

	private final int replacementCacheSize;

	private final int routingTreeOrder;

	private final int staleCounter;

	private final int hierarchyDepth;

	private final int hierarchyTreeOrder;

	private final int dataSize;

	private final int numberOfInitialRoutingTableContacts;

	private final long lookupMessageTimeout;

	private final long lookupOperationTimeout;

	private final int maxConcurrentLookups;

	private final long dataExpirationTime;

	private final int numberOfDataItems;

	private final int numberOfPeers;

	private final long periodicLookupInterval;

	private final long republishInterval;

	private final String clusterMappingFilePath;

	/**
	 * Reads the properties specified in the file <code>filePath</code>. If some
	 * value needed in Kademlia is not given in that file, a default value will
	 * be used. (The default values are read from a {@link StaticConfig}
	 * instance.)
	 * 
	 * @param filePath
	 *            the path to the properties file that contains the "constant"
	 *            definitions for Kademlia.
	 */
	public FileConfig(final String filePath) {
		log.debug("Reading Kademlia properties...");
		final ResourceBundle properties = getProperties(filePath);
		final Config defaultValues = new StaticConfig();

		bucketSize = readIntProperty(properties, "K", defaultValues
				.getBucketSize());
		idLength = readIntProperty(properties, "ID_LENGTH", defaultValues
				.getIDLength());
		refreshInterval = readSimTime(properties, "REFRESH_INTERVAL",
				defaultValues.getRefreshInterval(), Simulator.HOUR_UNIT);
		replacementCacheSize = readIntProperty(properties,
				"REPLACEMENT_CACHE_SIZE", defaultValues
						.getReplacementCacheSize());
		routingTreeOrder = readIntProperty(properties, "BTREE", defaultValues
				.getRoutingTreeOrder());
		staleCounter = readIntProperty(properties, "STALE_COUNTER",
				defaultValues.getStaleCounter());
		hierarchyDepth = readIntProperty(properties, "HIERARCHY_DEPTH",
				defaultValues.getHierarchyDepth());
		hierarchyTreeOrder = readIntProperty(properties, "HIERARCHY_BTREE",
				defaultValues.getHierarchyTreeOrder());
		dataSize = readIntProperty(properties, "DATA_SIZE", defaultValues
				.getDataSize());
		numberOfInitialRoutingTableContacts = readIntProperty(properties,
				"INITIAL_ROUTING_TABLE_CONTACTS", defaultValues
						.getNumberOfInitialRoutingTableContacts());
		lookupMessageTimeout = readSimTime(properties,
				"LOOKUP_MESSAGE_TIMEOUT", defaultValues
						.getLookupMessageTimeout(), Simulator.SECOND_UNIT);
		lookupOperationTimeout = readSimTime(properties,
				"LOOKUP_OPERATION_TIMEOUT", defaultValues
						.getLookupOperationTimeout(), Simulator.SECOND_UNIT);
		maxConcurrentLookups = readIntProperty(properties, "ALPHA",
				defaultValues.getMaxConcurrentLookups());
		dataExpirationTime = readSimTime(properties, "DATA_EXPIRATION_TIME",
				defaultValues.getDataExpirationTime(), Simulator.HOUR_UNIT);
		numberOfDataItems = readIntProperty(properties, "DATA_ITEMS",
				defaultValues.getNumberOfDataItems());
		numberOfPeers = readIntProperty(properties, "PEERS", defaultValues
				.getNumberOfPeers());
		periodicLookupInterval = readSimTime(properties,
				"PERIODIC_LOOKUP_INTERVAL", defaultValues
						.getPeriodicLookupInterval(), Simulator.MINUTE_UNIT);
		republishInterval = readSimTime(properties, "REPUBLISH_INTERVAL",
				defaultValues.getRepublishInterval(), Simulator.HOUR_UNIT);
		clusterMappingFilePath = readStringProperty(properties,
				"CLUSTER_MAPPING_FILE", defaultValues
						.getClusterMappingFilePath());

		log.debug("Reading Kademlia properties done.");
	}

	private static ResourceBundle getProperties(final String filePath) {
		ResourceBundle properties = null;
		try {
			final File propertiesFile = new File(filePath);
			final InputStream in = new FileInputStream(propertiesFile);
			properties = new PropertyResourceBundle(in);
			in.close();
		} catch (final FileNotFoundException ex) {
			log.error("Could not find the given Kademlia properties file '"
					+ filePath + "' - using default values. "
					+ "(This will produce an exception for all properties.)",
					ex);
		} catch (final IOException ex) {
			log.error("Could not read from given Kademlia properties file '"
					+ filePath + "' - using default values. "
					+ "(This will produce an exception for all properties.)",
					ex);
		} catch (final Exception ex) {
			log.error("An exception occurred while reading the properties '"
					+ filePath + "' - using default values "
					+ "(This will produce an exception for all properties.)",
					ex);
		}
		return properties;
	}

	private static int readIntProperty(final ResourceBundle properties,
			final String name, final int defaultVal) {
		try {
			return Integer.parseInt(properties.getString(name));
		} catch (final Exception ex) {
			log.error("An exception occurred while reading the property value "
					+ "for '" + name + "' - using the default value "
					+ defaultVal + ".", ex);
			return defaultVal;
		}
	}

	private static long readSimTime(final ResourceBundle properties,
			final String name, final long defaultVal, final long unitScaleFactor) {
		try {
			// multiply with unitScaleFactor as value is not given in simulation
			// time units
			return (long) (Double.parseDouble(properties.getString(name)) * unitScaleFactor);
		} catch (final Exception ex) {
			log.error("An exception occurred while reading the property value "
					+ "for '" + name + "' - using the default value "
					+ defaultVal + " (in simulation time units!).", ex);
			return defaultVal;
		}
	}

	private static String readStringProperty(final ResourceBundle properties,
			final String name, final String defaultVal) {
		try {
			return properties.getString(name);
		} catch (final Exception ex) {
			log.error("An exception occurred while reading the property value "
					+ "for '" + name + "' - using the default value "
					+ defaultVal + ".", ex);
			return defaultVal;
		}
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
