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

package org.peerfact.impl.analyzer.visualization2d;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.peerfact.impl.analyzer.visualization2d.controller.Controller;
import org.peerfact.impl.analyzer.visualization2d.model.VisDataModel;
import org.peerfact.impl.util.logging.SimLogger;


/**
 * Loads the application in standalone-mode, e.g. to replay a recorded
 * visualization
 * 
 * See documentation on <http://www.peerfact.org>.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * @author Kalman Graffi <info@peerfact.org>
 * 
 * @version 05/06/2011
 */
public class RunStandalone {

	/**
	 * Starts the application in standalone mode, e.g. for playing records or
	 * similar
	 * 
	 * @param args
	 */
	private static Logger log = SimLogger.getLogger(RunStandalone.class);

	public static void main(String[] args) {
		Controller.init();
		if (args.length > 0) {
			log.debug("Loading given file: \""
					+ args[0] + "\"...");
			try {
				File f = new File(args[0]);
				Controller.loadModelFrontend(VisDataModel.fromFile(f));
				Controller.getModel().setName(f.getName());
			} catch (IOException e) {
				log.debug("Unable to load file: "
						+ e.getMessage());
				e.printStackTrace();
			}
		}
	}

}
