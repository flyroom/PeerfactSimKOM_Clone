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

package org.peerfact.impl.service.aggregation.skyeye.queries;

import java.util.Vector;

import org.apache.log4j.Logger;
import org.peerfact.api.service.skyeye.SkyNetConstants;
import org.peerfact.impl.service.aggregation.skyeye.SkyNetPropertiesReader;
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
 * This class implements the automatic query-generation. Within
 * <code>QueryCreator</code>, one can choose between three different types of
 * generation, from which two allow for the consideration of the complexity of
 * queries. <li>Random creates a completely randomized query <li>PeerVariation
 * creates a query with a varying amount of searched peers (is used for the
 * consideration of the complexity of queries) <li>ConditionVariation creates a
 * query with varying conditions (is used for the consideration of the different
 * of queries)<br>
 * <br>
 * The type of the generation, as well as the attributes, of which a query can
 * consist, are defined by the <code>skynet.properties</code>-file in the
 * config-directory.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 08.12.2008
 * 
 */
public class QueryCreator {

	private static Logger log = SimLogger.getLogger(QueryCreator.class);

	private static QueryCreator creator;

	private String creatorType;

	private int peerVariationCounter;

	private int conditionVariationCounter;

	private String[] availableAttributes;

	private String[] availableTypes;

	private String[] countries;

	private int simulationSize;

	private int minCPU;

	private int maxCPU;

	private int minStorage;

	private int maxStorage;

	private int minRam;

	private int maxRam;

	private int minDownBandwidth;

	private int maxDownBandwidth;

	private long minOnlineTime;

	private long maxOnlineTime;

	private Vector<String> attributes;

	private Vector<String> types;

	public static QueryCreator getInstance(int simulationSize) {
		if (creator == null) {
			creator = new QueryCreator(simulationSize);
		}
		return creator;
	}

	private QueryCreator(int simulationSize) {
		// setting the needed values from the properties file
		SkyNetPropertiesReader propReader = SkyNetPropertiesReader
				.getInstance();
		creatorType = propReader.getStringProperty("AutomaticQueryGeneration");
		availableAttributes = propReader.getStringProperty(
				"AvailableAttributes").split(",");
		availableTypes = propReader
				.getStringProperty("AvailableAttributeTypes").split(",");
		countries = propReader.getStringProperty("AvailableCountries").split(
				",");
		minCPU = propReader.getIntProperty("MinCPU");
		maxCPU = propReader.getIntProperty("MaxCPU");
		minStorage = propReader.getIntProperty("MinStorage");
		maxStorage = propReader.getIntProperty("MaxStorage");
		minRam = propReader.getIntProperty("MinRam");
		maxRam = propReader.getIntProperty("MaxRam");
		minDownBandwidth = propReader.getIntProperty("MinDownBandwidth");
		maxDownBandwidth = propReader.getIntProperty("MaxDownBandwidth");
		minOnlineTime = propReader.getTimeProperty("MinOnlineTime");
		maxOnlineTime = propReader.getTimeProperty("MaxOnlineTime");

		peerVariationCounter = 0;
		conditionVariationCounter = 0;
		this.simulationSize = simulationSize;
		attributes = new Vector<String>();
		types = new Vector<String>();
	}

	/**
	 * This method is called to automatically generate a query, which afterwards
	 * is injected into the over-overlay for resolution. Within this method, the
	 * query is generated according to the chosen type of query-generation. The
	 * returned query is represented by a <code>String</code>-object, which is
	 * afterwards converted by the originating node into the <code>Query</code>
	 * -object.
	 * 
	 * @return the generated query as <code>String</code>-object
	 */
	public String createQueryString() {
		String queryString = "";
		if (creatorType.equals("PeerVariation")) {
			queryString = peerAmountCreation();
		} else if (creatorType.equals("ConditionVariation")) {
			queryString = AttributeCombination();
		} else {
			queryString = randomCreation();
		}
		return queryString;
	}

	private String peerAmountCreation() {
		String ret = "";
		int numberOfPeers = peerVariationCounter * 10 + 10;
		peerVariationCounter++;
		peerVariationCounter = peerVariationCounter % 20;

		ret = ret + "&PeerVariation:" + numberOfPeers + "&" + numberOfPeers
				+ "_of_DownBandwidth_>_300000_Double";
		return ret;
	}

	private String AttributeCombination() {
		String ret = "";
		double downBandwidth = conditionVariationCounter * 40000 + 5000;
		conditionVariationCounter++;
		conditionVariationCounter = conditionVariationCounter % 20;

		ret = ret + "&ConditionVariation:" + downBandwidth
				+ "&50_of_DownBandwidth_>_" + downBandwidth + "_Double";
		return ret;
	}

	private String randomCreation() {
		StringBuffer buf = new StringBuffer();
		String ret = "";
		int addends = Simulator.getRandom().nextInt(3) + 1;
		int conditions = 0;
		int numberOfPeers = 0;
		for (int i = 0; i < addends; i++) {
			numberOfPeers = Simulator.getRandom().nextInt(
					Math.min(200, (simulationSize / 2)) - 20) + 20;
			conditions = Simulator.getRandom().nextInt(5) + 1;
			buf.append(numberOfPeers + "_of_");
			for (int k = 0; k < availableAttributes.length; k++) {
				attributes.add(availableAttributes[k]);
				types.add(availableTypes[k]);
			}
			for (int j = 0; j < conditions; j++) {
				String temp = createCondition(Simulator.getRandom().nextInt(
						attributes.size()));
				if (temp != null) {
					buf.append(temp + ",");
				}
			}
			ret = buf.substring(0, buf.length() - 1);
			ret = ret + "+";
		}
		ret = ret.substring(0, ret.length() - 1);
		return ret;
	}

	private String createCondition(int index) {
		String attribute = attributes.remove(index);
		String type = types.remove(index);
		String value = "";
		String operator = "";
		if (type.equals("String")) {
			operator = "=";
			value = countries[Simulator.getRandom().nextInt(countries.length)];
		} else {
			operator = ">";
			if (type.equals("Integer")) {
				value = determineIntValue(attribute) + "";
			} else if (type.equals("Double")) {
				value = determineDoubleValue(attribute) + "";
			} else if (type.equals("Time")) {
				value = createOnlineTime() + "";
			} else {
				log.error("The type of attribute " + attribute
						+ " is unkown or wrong."
						+ " No condition for this attribute will be created.");
				return null;
			}
		}
		return attribute + "_" + operator + "_" + value + "_" + type;
	}

	private double determineDoubleValue(String attribute) {
		if (attribute.equals("DownBandwidth")) {
			return createDownBandwidth();
		} else if (attribute.equals("UpBandwidth")) {
			return createUpBandwidth();
		}
		return 0;
	}

	private int determineIntValue(String attribute) {
		if (attribute.equals("CPU")) {
			return createCpu();
		} else if (attribute.equals("RAM")) {
			return createRam();
		} else if (attribute.equals("Storage")) {
			return createStorage();
		}
		return 0;
	}

	private double createDownBandwidth() {
		return minDownBandwidth
				+ Simulator.getRandom().nextInt(
						maxDownBandwidth - minDownBandwidth);
		/*
		 * double randomDownBandwidth = Simulator.getRandom().nextInt(
		 * maxDownBandwidth); if (randomDownBandwidth < minDownBandwidth) {
		 * randomDownBandwidth = randomDownBandwidth + minDownBandwidth; }
		 * return randomDownBandwidth;
		 */
	}

	private double createUpBandwidth() {
		double randomDownBandwidth = minDownBandwidth
				+ Simulator.getRandom().nextInt(
						maxDownBandwidth - minDownBandwidth);
		/*
		 * double randomDownBandwidth = Simulator.getRandom().nextInt(
		 * maxDownBandwidth); if (randomDownBandwidth < minDownBandwidth) {
		 * randomDownBandwidth = randomDownBandwidth + minDownBandwidth; }
		 */
		return Math.floor(randomDownBandwidth * 0.1);
	}

	private int createCpu() {
		return minCPU + Simulator.getRandom().nextInt(maxCPU - minCPU);
	}

	private int createStorage() {
		return minStorage
				+ Simulator.getRandom().nextInt(maxStorage - minStorage);
	}

	private int createRam() {
		return minRam + Simulator.getRandom().nextInt(maxRam - minRam);
	}

	private long createOnlineTime() {
		int dif = (int) ((maxOnlineTime / SkyNetConstants.DIVISOR_FOR_SECOND) - (minOnlineTime / SkyNetConstants.DIVISOR_FOR_SECOND));
		long temp = Simulator.getRandom().nextInt(dif);
		return minOnlineTime + temp * SkyNetConstants.DIVISOR_FOR_SECOND;
	}
}
