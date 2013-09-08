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

package org.peerfact.impl.network.modular;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;
import org.peerfact.impl.network.modular.db.NetMeasurementDB;
import org.peerfact.impl.network.modular.db.NetMeasurementDB.Host;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.util.logging.SimLogger;


/**
 * If the hosts in the XML configuration file are not grouped by region names
 * (useRegionGroups="false"), this component will allow the net layer factory to
 * get random hosts from the network measurement database.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class DBHostListManager {

	private static Logger log = SimLogger.getLogger(DBHostListManager.class);

	private List<Host> hostList;

	int hostPointer = 0;

	public DBHostListManager(NetMeasurementDB db) {
		this.hostList = getShuffledHostList(db);
	}

	/**
	 * Returns a shuffled host list from the given network measurement database.
	 * 
	 * @param db
	 * @return
	 */
	public static List<NetMeasurementDB.Host> getShuffledHostList(
			NetMeasurementDB db) {
		long startTime = System.nanoTime();
		log.debug("Starting shuffling the host list in the measurement database.");
		List<NetMeasurementDB.Host> result = new ArrayList<NetMeasurementDB.Host>(
				db.getAllObjects(NetMeasurementDB.Host.class));
		Collections.shuffle(result,
				new Random(Simulator.getRandom().nextLong()));
		double shufflingTime = (System.nanoTime() - startTime) * 0.000001;
		log.debug("Shuffled host list in " + shufflingTime + " ms");
		return result;
	}

	/**
	 * Returns the next available host from a shuffled host list.
	 * 
	 * @return
	 */
	public NetMeasurementDB.Host getNextHost() {
		if (hostPointer >= hostList.size()) {
			throw new IllegalStateException(
					"No more hosts in the host list database.");
		}
		Host result = hostList.get(hostPointer);
		hostPointer++;
		return result;
	}

}
