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

import java.util.LinkedHashMap;

import org.apache.log4j.Logger;
import org.peerfact.api.service.skyeye.SkyNetNodeInterface;
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
 * This class defines the functionality to convert a query, which is represented
 * as a <code>String</code>-object, into the <code>Query</code>-object. The
 * <code>String</code>-representation of a query can derive from a defined or
 * automatically generated query.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 08.12.2008
 * 
 */
public class QueryResolver {

	private static Logger log = SimLogger.getLogger(QueryResolver.class);

	private LinkedHashMap<String, String> availableAttributes;

	private SkyNetNodeInterface skyNetNode;

	private String[] addends;

	private String[] conditions;

	private String[] elements;

	boolean correctAddend = true;

	public QueryResolver(SkyNetNodeInterface skyNetNode) {
		this.skyNetNode = skyNetNode;
		availableAttributes = new LinkedHashMap<String, String>();
		initializeAvailableOperations();
	}

	public String getTypeOfAttribute(String name) {
		return availableAttributes.get(name);
	}

	/**
	 * Within this method, the conversion from the <code>String</code>
	 * -representation of the query, which is provided by the parameter, to the
	 * <code>Query</code>-object is executed.
	 * 
	 * @param command
	 *            contains the query as <code>String</code>-object
	 * @return the query as <code>Query</code>-object
	 */
	public Query createQuery(String command) {
		Query query = new Query(skyNetNode.getSkyNetNodeInfo().clone());
		QueryAddend queryAddend;
		String[] types;
		if (command.startsWith("&")) {
			types = command.substring(1).split("&");
			query.setQueryType(types[0]);
			addends = types[1].split("\\+");
		} else {
			addends = command.split("\\+");
		}

		for (int i = 0; i < addends.length; i++) {
			String[] temp = addends[i].split("of");
			if (temp.length == 2) {
				String s = temp[0];
				if (s.startsWith("_")) {
					s = s.substring(1);
				}
				if (s.endsWith("_")) {
					s = s.substring(0, s.length() - 1);
				}
				int count = Integer.parseInt(s);
				queryAddend = new QueryAddend(count);
				String t = temp[1];
				if (t.startsWith("_")) {
					t = t.substring(1);
				}
				if (t.endsWith("_")) {
					t = t.substring(0, t.length() - 1);
				}
				conditions = t.split(",");
				processAddend(queryAddend, conditions, i);
				if (correctAddend) {
					query.addAddend(queryAddend);
				} else {
					correctAddend = true;
				}
			} else {
				log.error(i + ". addend has not the right format,"
						+ " and will be neglected.");
			}
		}
		if (query.getNumberOfAddends() == 0) {
			return null;
		} else {
			return query;
		}
	}

	public static void printQuery(Query query) {
		log.debug(query.toString());
	}

	private void initializeAvailableOperations() {
		availableAttributes.put("DownBandwidth", "Double");
		availableAttributes.put("UpBandwidth", "Double");
		availableAttributes.put("GroupID", "String");
		availableAttributes.put("Position", "String");
		availableAttributes.put("CPU", "Integer");
		availableAttributes.put("RAM", "Integer");
		availableAttributes.put("Storage", "Integer");
		availableAttributes.put("AvgOnline", "Time");
		availableAttributes.put("tMaxCo", "Integer");
		availableAttributes.put("tTresholdCo", "Integer");
		availableAttributes.put("sendMaxCo", "Integer");
		availableAttributes.put("tMaxSP", "Integer");
		availableAttributes.put("tTresholdSP", "Integer");
		availableAttributes.put("sendMaxSP", "Integer");
		availableAttributes.put("tMin", "Integer");
	}

	private void processAddend(QueryAddend queryAddend,
			String[] passedConditions,
			int addendNo) {
		for (int j = 0; j < passedConditions.length; j++) {
			String c = passedConditions[j];
			if (c.startsWith("_")) {
				c = c.substring(1);
			}
			if (c.endsWith("_")) {
				c = c.substring(0, c.length() - 1);
			}
			elements = c.split("_");
			if (elements.length != 4
					|| !addConditionToAddend(queryAddend, elements)) {
				log.error((j + 1) + ". condition of " + (addendNo + 1)
						+ ".addend has not the right format."
						+ " The whole addend will be neglected.");
				correctAddend = false;
				// queryAddend = null;
				break;
			}
		}
	}

	private boolean addConditionToAddend(QueryAddend queryAddend,
			String[] passedConditions) {
		if (availableAttributes.containsKey(passedConditions[0])
				&& availableAttributes.get(passedConditions[0]).equals(
						passedConditions[3])) {

			if (passedConditions[3].equals("Byte")) {
				queryAddend.addCondition(new QueryCondition<Byte>(
						passedConditions[0], new Byte(passedConditions[2]),
						passedConditions[1]));
			} else if (passedConditions[3].equals("Boolean")) {
				queryAddend.addCondition(new QueryCondition<Boolean>(
						passedConditions[0], Boolean
								.valueOf(passedConditions[2]),
						passedConditions[1]));
			} else if (passedConditions[3].equals("Short")) {
				queryAddend
						.addCondition(new QueryCondition<Short>(
								passedConditions[0],
								new Short(passedConditions[2]),
								passedConditions[1]));
			} else if (passedConditions[3].equals("Integer")) {
				queryAddend.addCondition(new QueryCondition<Integer>(
						passedConditions[0], new Integer(passedConditions[2]),
						passedConditions[1]));
			} else if (passedConditions[3].equals("Float")) {
				queryAddend
						.addCondition(new QueryCondition<Float>(
								passedConditions[0],
								new Float(passedConditions[2]),
								passedConditions[1]));
			} else if (passedConditions[3].equals("Double")) {
				queryAddend
						.addCondition(new QueryCondition<Double>(
								passedConditions[0],
								new Double(passedConditions[2]),
								passedConditions[1]));
			} else if (passedConditions[3].equals("Long")) {
				queryAddend.addCondition(new QueryCondition<Long>(
						passedConditions[0], new Long(passedConditions[2]),
						passedConditions[1]));
			} else if (passedConditions[3].equals("Time")) {
				String value = passedConditions[2];
				double ret = 0;
				if (value.endsWith("s")) {
					ret = Double.parseDouble(value.split("s")[0])
							* Simulator.SECOND_UNIT;
					queryAddend.addCondition(new QueryCondition<Double>(
							passedConditions[0], new Double(ret),
							passedConditions[1]));
				} else if (value.endsWith("m")) {
					ret = Double.parseDouble(value.split("m")[0])
							* Simulator.MINUTE_UNIT;
					queryAddend.addCondition(new QueryCondition<Double>(
							passedConditions[0], new Double(ret),
							passedConditions[1]));
				} else if (value.endsWith("h")) {
					ret = Double.parseDouble(value.split("h")[0])
							* Simulator.HOUR_UNIT;
					queryAddend.addCondition(new QueryCondition<Double>(
							passedConditions[0], new Double(ret),
							passedConditions[1]));
				} else {
					ret = Double.parseDouble(value);
					queryAddend.addCondition(new QueryCondition<Double>(
							passedConditions[0], new Double(ret),
							passedConditions[1]));

				}
			} else if (passedConditions[3].equals("String")) {
				queryAddend.addCondition(new QueryCondition<String>(
						passedConditions[0], passedConditions[2],
						passedConditions[1]));
			}

			return true;

		} else {
			log.fatal("Attribute " + passedConditions[0] + " or its type "
					+ passedConditions[3] + " are unknown");
			return false;
		}
	}
}
