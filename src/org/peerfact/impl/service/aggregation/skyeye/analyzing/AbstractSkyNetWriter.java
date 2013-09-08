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

package org.peerfact.impl.service.aggregation.skyeye.analyzing;

import java.io.File;

import org.apache.log4j.Logger;
import org.peerfact.api.simengine.SimulationEventHandler;
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
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public abstract class AbstractSkyNetWriter implements SimulationEventHandler {

	private static Logger log = SimLogger.getLogger(AbstractSkyNetWriter.class);

	protected static void initWriteDirectory(String dataPath, boolean clean) {
		File dir = new File(dataPath);
		if (dir.mkdir()) {
			log.warn("Created Directory " + dir.getName());
		} else if (clean) {
			String[] list = dir.list();
			File f;
			for (int i = 0; i < list.length; i++) {
				f = new File(dir.getPath() + File.separatorChar + list[i]);
				f.delete();
			}
			log.warn("Cleaned Directory " + dir.getName()
					+ " for new simulation.");
			if (dir.list().length != 0) {
				log.error("Directory " + dir.getName() + " is not emtpy.");
			}
		} else {
			log.warn("Directory " + dir.getName()
					+ " already exists and needs not be cleaned.");
		}
	}

}
