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

package org.peerfact.impl.overlay.informationdissemination.psense.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.peerfact.api.transport.TransProtocol;
import org.peerfact.impl.application.infodissemination.moveModels.IMoveModel;
import org.peerfact.impl.application.infodissemination.moveModels.IPositionDistribution;
import org.peerfact.impl.scenario.DefaultConfigurator;
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
 * This class contains the configuration for every node.<br>
 * Additionally contains the parsing of the properties file for the
 * configuration.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 10/15/2010
 * 
 */
public class Configuration {

	/**
	 * Logger for this class
	 */
	final static Logger log = SimLogger.getLogger(Configuration.class);

	// 0 bedeutet es gibt keine Sectoren
	public static int NUMBER_SECTORS = 8;

	public static long DECLARE_NODE_DEATH_TIMEOUT = 9000 * Simulator.MILLISECOND_UNIT;

	public static int VISION_RANGE_RADIUS = 120;

	public static int ACTION_RANGE_RADIUS = 120;

	public static byte MAXIMAL_HOP = 5;

	public static double ENLARGING_SECTOR_FACTOR = 0.2;

	public static boolean IS_HEARTBEAT_ACTIVATED = false;

	public static long INTERVAL_BETWEEN_HEARTBEATS = 1000 * Simulator.MILLISECOND_UNIT;

	public static short PORT = 12345;

	public static TransProtocol TRANSPORT_PROTOCOL = TransProtocol.UDP;

	public static int TRANSPORT_PROTOCOL_OVERHEAD = 8;

	public static int NETWORT_PROTOCOL_OVERHEAD = 20;

	public static long WAIT_BEFORE_RETRY_JOIN = 2500 * Simulator.MILLISECOND_UNIT;

	public static long OP_WAIT_FOR_STATUS_OF_JOIN = 100 * Simulator.MILLISECOND_UNIT;

	public static long INTERVAL_BETWEEN_MOVE_OPERATIONS = 333 * Simulator.MILLISECOND_UNIT;

	public static IPositionDistribution POSITION_DISTRIBUTION = null;

	public static IMoveModel MOVE_MODEL = null;

	public static boolean FIT_TIME_BETWEEN_ROUNDS = false;

	public static long TIME_BETWEEN_ROUNDS = 333 * Simulator.MILLISECOND_UNIT;

	public static int ROUND_BYTE_LIMIT = 5000;

	public static long TIMEOUT_ALONE_IN_OVERLAY = 20 * Simulator.SECOND_UNIT;

	public static void setProperties(String file) {
		Properties properties = new Properties();
		BufferedInputStream stream;
		try {
			stream = new BufferedInputStream(new FileInputStream(file));
			properties.load(stream);
			stream.close();

			NUMBER_SECTORS = getPropertiesInt(properties, "numberSectors",
					NUMBER_SECTORS);
			if (NUMBER_SECTORS == 0) {
				log.fatal("The value for NUMBER_SECTORS is 0. It is a bad value");
			}

			DECLARE_NODE_DEATH_TIMEOUT = getPropertiesTime(properties,
					"declareNodeDeathTimeout", DECLARE_NODE_DEATH_TIMEOUT);

			VISION_RANGE_RADIUS = getPropertiesInt(properties,
					"visionRangeRadius", VISION_RANGE_RADIUS);

			ACTION_RANGE_RADIUS = getPropertiesInt(properties,
					"actionRangeRadius", ACTION_RANGE_RADIUS);

			MAXIMAL_HOP = getPropertiesByte(properties, "maximalHop",
					MAXIMAL_HOP);

			ENLARGING_SECTOR_FACTOR = getPropertiesDouble(properties,
					"enlargingSectorFactor", ENLARGING_SECTOR_FACTOR);

			IS_HEARTBEAT_ACTIVATED = getPropertiesBoolean(properties,
					"isHeartBeatActivated", IS_HEARTBEAT_ACTIVATED);

			INTERVAL_BETWEEN_HEARTBEATS = getPropertiesTime(properties,
					"intervalBetweenHeartsbeats", INTERVAL_BETWEEN_HEARTBEATS);

			PORT = getPropertiesShort(properties, "port", PORT);

			TRANSPORT_PROTOCOL = getPropertiesTransportProtocol(properties,
					"transportProtocol", TRANSPORT_PROTOCOL);

			TRANSPORT_PROTOCOL_OVERHEAD = getPropertiesInt(properties,
					"transportProtocolOverhead", TRANSPORT_PROTOCOL_OVERHEAD);

			NETWORT_PROTOCOL_OVERHEAD = getPropertiesInt(properties,
					"networkProtocolOverhead", NETWORT_PROTOCOL_OVERHEAD);

			WAIT_BEFORE_RETRY_JOIN = getPropertiesTime(properties,
					"waitBeforeRetryJoin", WAIT_BEFORE_RETRY_JOIN);

			OP_WAIT_FOR_STATUS_OF_JOIN = getPropertiesTime(properties,
					"opWaitForStatusOfJoin", OP_WAIT_FOR_STATUS_OF_JOIN);

			INTERVAL_BETWEEN_MOVE_OPERATIONS = getPropertiesTime(properties,
					"intervalBetweenMoveOperations",
					INTERVAL_BETWEEN_MOVE_OPERATIONS);

			FIT_TIME_BETWEEN_ROUNDS = getPropertiesBoolean(properties,
					"fitTimeBetweenRounds", FIT_TIME_BETWEEN_ROUNDS);

			TIME_BETWEEN_ROUNDS = getPropertiesTime(properties,
					"timeBetweenRounds", TIME_BETWEEN_ROUNDS);

			ROUND_BYTE_LIMIT = getPropertiesInt(properties, "roundByteLimit",
					ROUND_BYTE_LIMIT);

			TIMEOUT_ALONE_IN_OVERLAY = getPropertiesTime(properties,
					"timoutAloneInOverlay", TIMEOUT_ALONE_IN_OVERLAY);

		} catch (FileNotFoundException e) {
			log.error("Properties file " + file + " can not be found", e);
		} catch (IOException e) {
			log.error("Error by reading of the Properties file " + file + ".",
					e);
		}
	}

	private static TransProtocol getPropertiesTransportProtocol(
			Properties properties, String key, TransProtocol stdValue) {
		TransProtocol result = null;
		if (properties.containsKey(key)) {
			String string = properties.getProperty(key);
			try {
				result = Enum.valueOf(TransProtocol.class, string);
			} catch (IllegalArgumentException e) {
				log.fatal(
						"Wrong configuration in the properties file for key \""
								+ key
								+ "\". It doesn't exist a transport protocol \""
								+ string + "\" in enum TransProtocol.", e);
				System.exit(-1);
			}
		} else {
			if (log.isInfoEnabled()) {
				log.info("Properties file not contain \"" + key
						+ "\", it is used the standard value of " + stdValue);
			}
			result = stdValue;
		}
		return result;
	}

	private static Integer getPropertiesInt(Properties properties, String key,
			int stdValue) {
		Integer result = null;
		if (properties.containsKey(key)) {
			result = new Integer(properties.getProperty(key));
		} else {
			if (log.isInfoEnabled()) {
				log.info("Properties file not contain \"" + key
						+ "\", it is used the standard value of " + stdValue);
			}
			result = stdValue;
		}
		return result;
	}

	// private static Long getPropertiesLong(Properties properties, String key,
	// long stdValue) {
	// Long result = null;
	// if (properties.containsKey(key)) {
	// result = new Long(properties.getProperty(key));
	// } else {
	// if (log.isInfoEnabled()) {
	// log.info("Properties file not contain \"" + key
	// + "\", it is used the standard value of " + stdValue);
	// }
	// result = stdValue;
	// }
	// return result;
	// }

	private static Boolean getPropertiesBoolean(Properties properties,
			String key, boolean stdValue) {
		Boolean result = null;
		if (properties.containsKey(key)) {
			result = Boolean.valueOf(properties.getProperty(key));
		} else {
			if (log.isInfoEnabled()) {
				log.info("Properties file not contain \"" + key
						+ "\", it is used the standard value of " + stdValue);
			}
			result = stdValue;
		}
		return result;
	}

	private static Byte getPropertiesByte(Properties properties, String key,
			byte stdValue) {
		Byte result = null;
		if (properties.containsKey(key)) {
			result = new Byte(properties.getProperty(key));
		} else {
			if (log.isInfoEnabled()) {
				log.info("Properties file not contain \"" + key
						+ "\", it is used the standard value of " + stdValue);
			}
			result = stdValue;
		}
		return result;
	}

	private static Short getPropertiesShort(Properties properties, String key,
			short stdValue) {
		Short result = null;
		if (properties.containsKey(key)) {
			result = new Short(properties.getProperty(key));
		} else {
			if (log.isInfoEnabled()) {
				log.info("Properties file not contain \"" + key
						+ "\", it is used the standard value of " + stdValue);
			}
			result = stdValue;
		}
		return result;
	}

	private static Double getPropertiesDouble(Properties properties,
			String key, double stdValue) {
		Double result = null;
		if (properties.containsKey(key)) {
			result = new Double(properties.getProperty(key));
		} else {
			if (log.isInfoEnabled()) {
				log.info("Properties file not contain \"" + key
						+ "\", it is used the standard value of " + stdValue);
			}
			result = stdValue;
		}
		return result;
	}

	private static Long getPropertiesTime(Properties properties, String key,
			long stdValue) {
		Long result = null;
		if (properties.containsKey(key)) {
			String value = properties.getProperty(key);
			result = DefaultConfigurator.parseTime(value);
		} else {
			if (log.isInfoEnabled()) {
				log.info("Properties file not contain \"" + key
						+ "\", it is used the standard value of " + stdValue);
			}
			result = stdValue;
		}
		return result;
	}

	/*
	 * Setter for the movements
	 */

	public static void setPositionDistribution(
			IPositionDistribution positionDistribution) {
		POSITION_DISTRIBUTION = positionDistribution;
	}

	public static void setMoveModel(IMoveModel moveModel) {
		MOVE_MODEL = moveModel;
	}
}
