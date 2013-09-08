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

import org.apache.log4j.Logger;
import org.peerfact.impl.overlay.dht.kademlia.base.operations.OperationsConfig;
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
 * This class has the purpose to enable a local configuration store for nodes.
 * 
 * Such a store is needed to evaluate self optimization mechanisms where overall
 * miss configurations are detected and adapted to optimize the system. Such a
 * adaption happens decentralized and configuration changes have to be
 * distributed in the system. Therefore each node has to maintain its own local
 * configurations.
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class LocalConfig {

	private final static Logger log = SimLogger.getLogger(LocalConfig.class);

	/**
	 * The maximal number of parallel updates for lookups. In the papers this
	 * parameter is called alpha.
	 */
	Integer maxConcurrentLookups;

	/**
	 * The maximal number of results returned by a node for a lookup. In the
	 * papers this parameter is called n.
	 */
	Integer maxReturnedResults;

	/**
	 * The size of a single bucket in the rooting table. In the papers this
	 * parameter is called k.
	 */
	Integer bucketSize;

	/**
	 * @param defaultConfig
	 *            the reference to the default configuration. It is used as long
	 *            as no local adaption took place.
	 */
	/**
	 * @param defaultConfig
	 */
	public LocalConfig(OperationsConfig defaultConfig) {
		maxConcurrentLookups = defaultConfig.getMaxConcurrentLookups();
		bucketSize = defaultConfig.getBucketSize();

		/**
		 * The original implementation of Kademlia used the bucket size as value
		 * for the maximal number of returned results, so here this is also
		 * chosen as the default value.
		 */
		maxReturnedResults = bucketSize;
		log.debug("The local config was initally set to: maxConcurrentLookups="
				+ maxConcurrentLookups + "; bucketSize=" + bucketSize
				+ "; maxReturnedResults=" + maxReturnedResults + ";");
	}

	/**
	 * @return the maximal number of results returned by a node for a lookup. In
	 *         the papers this parameter is called n.
	 */
	public Integer getMaxReturnedResults() {
		return maxReturnedResults;
	}

	/**
	 * @param maxReturnedResults
	 *            the maximal number of results returned by a node for a lookup.
	 *            In the papers this parameter is called n.
	 */
	public void setMaxReturnedResults(Integer maxReturnedResults) {
		this.maxReturnedResults = maxReturnedResults;
		log.debug("The local config maxReturnedResults was set to :"
				+ maxReturnedResults);
	}

	/**
	 * @return the size of a single bucket in the rooting table. In the papers
	 *         this parameter is called k.
	 */
	public Integer getBucketSize() {
		return bucketSize;
	}

	/**
	 * @param bucketSize
	 *            the size of a single bucket in the rooting table. In the
	 *            papers this parameter is called k.
	 */
	public void setBucketSize(Integer bucketSize) {
		this.bucketSize = bucketSize;
		log.debug("The local config bucketSize was set to :" + bucketSize);
	}

	/**
	 * @return the number of parallel updates that are started up on lookups. In
	 *         the papers this parameter is named alpha.
	 */
	public int getMaxConcurrentLookups() {
		return maxConcurrentLookups;
	}

	/**
	 * @param maxConcurrentLookups
	 *            the number of parallel updates that are started up on lookups.
	 *            In the papers this parameter is named alpha.
	 */
	public void setMaxConcurrentLookups(Integer maxConcurrentLookups) {
		this.maxConcurrentLookups = maxConcurrentLookups;
		log.debug("The local config maxConcurrentLookups was set to :"
				+ maxConcurrentLookups);
	}

}
