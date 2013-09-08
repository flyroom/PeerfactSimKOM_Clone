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

package org.peerfact.impl.service.aggregation.skyeye;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.peerfact.api.scenario.Configurable;
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
 * This class defines the reader of the properties of SkyNet, which are required
 * to properly initialize a SkyNet-node. The properties for the initialization
 * are defined within the file <code>skynet.properties</code> in the
 * config-directory.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 04.12.2008
 * 
 */
public class SkyNetPropertiesReader implements Configurable {

	private static Logger log = SimLogger
			.getLogger(SkyNetPropertiesReader.class);

	private String skynetPropertiesFile;

	private static final String SKYNET_PROPERTIES_PATH = "config"
			+ File.separator;

	private static SkyNetPropertiesReader propReader;

	private Properties properties;

	public static SkyNetPropertiesReader getInstance() {
		if (propReader == null) {
			propReader = new SkyNetPropertiesReader();
		}
		return propReader;
	}

	private SkyNetPropertiesReader() {
		properties = new Properties();
	}

	/**
	 * This method returns the value of a property of the type <code>long</code>
	 * , which is identified by the name stored in <code>key</code>.
	 * 
	 * @param key
	 *            contains the key for the retrieved property.
	 * @return the value of the property
	 */
	public static long getLongProperty(String key) {
		return Long.decode(key).longValue();
	}

	public long getTimeProperty(String key) {
		String value = properties.getProperty(key);
		long ret = -1;
		if (value.endsWith("s")) {
			ret = Long.decode(value.split("s")[0]).longValue()
					* Simulator.SECOND_UNIT;
		} else if (value.endsWith("m")) {
			ret = Long.decode(value.split("m")[0]).longValue()
					* Simulator.MINUTE_UNIT;
		} else if (value.endsWith("h")) {
			ret = Long.decode(value.split("h")[0]).longValue()
					* Simulator.HOUR_UNIT;
		}
		return ret;
	}

	/**
	 * This method returns the value of a property of the type <code>int</code>,
	 * which is identified by the name stored in <code>key</code>.
	 * 
	 * @param key
	 *            contains the key for the retrieved property.
	 * @return the value of the property
	 */
	public int getIntProperty(String key) {
		return Integer.decode(properties.getProperty(key)).intValue();
	}

	/**
	 * This method returns the value of a property of the type
	 * <code>float</code>, which is identified by the name stored in
	 * <code>key</code>.
	 * 
	 * @param key
	 *            contains the key for the retrieved property.
	 * @return the value of the property
	 */
	public float getFloatProperty(String key) {
		return Float.parseFloat(properties.getProperty(key));
	}

	/**
	 * This method returns the value of a property of the type
	 * <code>String</code>, which is identified by the name stored in
	 * <code>key</code>.
	 * 
	 * @param key
	 *            contains the key for the retrieved property.
	 * @return the value of the property
	 */
	public String getStringProperty(String key) {
		return properties.getProperty(key);
	}

	public void setPropertiesFile(String filename) {
		File file = new File(SKYNET_PROPERTIES_PATH + File.separator + filename);
		if (file.exists()) {
			skynetPropertiesFile = filename;
			log.warn("Using provieded properties-file " + file.getName());
		} else {
			skynetPropertiesFile = "skynet.properties";
			log.error("The properties-file " + file.getName()
					+ " does not exist, using default file "
					+ skynetPropertiesFile);
		}

		try {
			properties.load(new FileReader(SKYNET_PROPERTIES_PATH
					+ File.separator + skynetPropertiesFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean getBooleanProperty(String key) {
		return Boolean.parseBoolean(properties.getProperty(key));
	}

	public String getSkynetPropertiesFile() {
		return skynetPropertiesFile;
	}

}
