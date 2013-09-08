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

package org.peerfact.impl.util.logging;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * This is a wrapper class for log4j. Log4j is a logging package for printing
 * log output to different local and remote destinations. <strong>See
 * <code>log4j.properties</code> for configuration</strong>. Be sure to include
 * the path to log4j.jar in your CLASSPATH. See <a
 * href="http://logging.apache.org/log4j/1.2/index.html">log4j documentation</a>
 * for more details.
 * 
 * @author Andre Mink <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class SimLogger {
	private static final String log4j_config_file = "src/log4j.properties";

	private static SimLogger simLogger;

	private SimLogger() {
		init();
	}

	private synchronized static void init() {
		try {
			PropertyConfigurator
					.configureAndWatch(log4j_config_file, 60 * 1000);
		} catch (Exception ex) {
			System.err.println("Failed to configure logging" + ex);
			throw new IllegalStateException(ex);
		}
	}

	/**
	 * Returns a single instance of a logger
	 * 
	 * @return logger instance (singleton)
	 */
	public static synchronized SimLogger getInstance() {
		if (simLogger == null) {
			simLogger = new SimLogger();
		}
		return simLogger;
	}

	/**
	 * The common and encouraged way to obtain loggers in the simulator. The
	 * usage of this method will ensure that all loggers are configured from the
	 * same configuration file, <code>log4j.properties</code> in the root dir of
	 * the distribution dir.
	 * 
	 * @param c
	 *            - class for which the logger is required
	 * @return logger instance
	 */
	public static Logger getLogger(Class<?> c) {
		getInstance();
		Logger l = Logger.getLogger(c);
		l.trace("Initialized logger");
		return l;
	}

}
