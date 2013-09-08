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

import org.apache.log4j.Logger;
import org.peerfact.impl.overlay.dht.kademlia.base.analyzer.KademliaMonitor;
import org.peerfact.impl.overlay.dht.kademlia.base.components.AbstractNodeFactory;
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
 * Holds static references to global (singleton) components in a Kademlia
 * experiment.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public class KademliaSetup {

	private final static Logger log = SimLogger.getLogger(KademliaSetup.class);

	/** Singleton instance of KademliaSetup. */
	private static KademliaSetup singleton;

	/** The currently used node factory. */
	private AbstractNodeFactory nodeFactory;

	/** The currently used workload generator. */
	private WorkloadGenerator workloadGen;

	/**
	 * The Monitor that is being used to monitor Kademlia specific events.
	 */
	private KademliaMonitor monitor;

	/**
	 * The Config that contains constants for use in Kademlia.
	 */
	private Config config;

	private KademliaSetup() {
		// should not be called externally
	}

	/**
	 * @return the singleton instance of KademliaSetup.
	 */
	public static KademliaSetup getInstance() {
		if (singleton == null) {
			singleton = new KademliaSetup();
		}
		return singleton;
	}

	/**
	 * Sets the currently used node factory.
	 * 
	 * @param factory
	 *            the AbstractNodeFactory that is currently being used.
	 */
	public void setNodeFactory(final AbstractNodeFactory factory) {
		nodeFactory = factory;
	}

	/**
	 * @return the currently used AbstractNodeFactory.
	 */
	public static AbstractNodeFactory getNodeFactory() {
		return getInstance().nodeFactory;
	}

	/**
	 * Sets the currently used workload generator.
	 * 
	 * @param generator
	 *            the WorkloadGenerator that is currently being used.
	 */
	public void setWorkload(final WorkloadGenerator generator) {
		workloadGen = generator;
	}

	/**
	 * @return the currently used WorkloadGenerator.
	 */
	public static WorkloadGenerator getWorkloadGenerator() {
		return getInstance().workloadGen;
	}

	/**
	 * Sets the KademliaMonitor to be used in this setup.
	 */
	public void setMonitor(final KademliaMonitor mon) {
		this.monitor = mon;
	}

	/**
	 * @return the KademliaMonitor to be used. The result of this method is
	 *         never null.
	 */
	public static KademliaMonitor getMonitor() {
		return getInstance().monitor;
	}

	/**
	 * Sets the global Config object that contains Kademlia-wide constants. This
	 * method has an effect only if a Config object has never been set before.
	 * 
	 * @param confPath
	 *            a String with the path to the Kademlia properties file
	 *            (absolute or relative to the programme directory).
	 */
	public void setConfigPath(final String confPath) {
		if (config == null) {
			config = new FileConfig(confPath);
			log.info("Using Kademlia configuration from '" + confPath + "'.");
		}
	}

	/**
	 * Sets the Config instance used in this simulation to <code>conf</code>.
	 * <b>This method is for test purposes only!!</b>
	 * 
	 * @param conf
	 *            the Config to be used.
	 */
	public static void setConfig(final Config conf) {
		getInstance().config = conf;
	}

	/**
	 * @return the global Config object that contains Kademlia-wide constants.
	 *         If none has been configured before, it will be attempted to read
	 *         configuration values from the default location or use the default
	 *         values.
	 *         <p>
	 *         For efficiency reasons, other components should copy the result
	 *         of this method into a final instance variable instead of
	 *         accessing it here each time needed. The result of this method can
	 *         be assumed to be constant over time.
	 */
	public static Config getConfig() {
		final KademliaSetup ins = getInstance();
		if (ins.config == null) {
			ins.config = new FileConfig("config/kademlia.properties");
		}
		return ins.config;
	}
}
