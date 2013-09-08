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

package org.peerfact.impl.service.aggregation.skyeye.attributes;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.peerfact.api.service.skyeye.SkyNetLayer;
import org.peerfact.api.service.skyeye.SkyNetNodeInfo;
import org.peerfact.api.service.skyeye.SupportPeer;
import org.peerfact.impl.service.aggregation.skyeye.SkyNetUtilities;
import org.peerfact.impl.service.aggregation.skyeye.queries.QueryAddend;
import org.peerfact.impl.service.aggregation.skyeye.queries.QueryCondition;
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
public class AttributeStorageDelegator {

	private static Logger log = SimLogger
			.getLogger(AttributeStorageDelegator.class);

	private AttributeStorage aStorageCallback;

	public AttributeStorageDelegator(AttributeStorage aStorageCallback) {
		this.aStorageCallback = aStorageCallback;
	}

	public static TreeMap<Double, AttributeEntry> getSortedEntries(
			LinkedHashMap<BigDecimal, AttributeSubCoordinatorInfo> listOfSubCoordinators) {
		TreeMap<BigDecimal, AttributeEntry> tempMap = new TreeMap<BigDecimal, AttributeEntry>();
		Iterator<BigDecimal> subCoIter = listOfSubCoordinators.keySet()
				.iterator();
		TreeMap<BigDecimal, AttributeEntry> entryMap = null;
		Iterator<BigDecimal> entryIter = null;
		AttributeEntry entry = null;
		AttributeEntry tempEntry = null;

		while (subCoIter.hasNext()) {
			entryMap = listOfSubCoordinators.get(subCoIter.next()).getData();
			entryIter = entryMap.keySet().iterator();
			while (entryIter.hasNext()) {
				entry = entryMap.get(entryIter.next());
				if (tempMap.containsKey(entry.getNodeInfo().getSkyNetID()
						.getID())) {
					tempEntry = tempMap.remove(entry.getNodeInfo()
							.getSkyNetID().getID());
					if (tempEntry.getTimestamp() > entry.getTimestamp()) {
						tempMap.put(tempEntry.getNodeInfo().getSkyNetID()
								.getID(), tempEntry);
					} else {
						tempMap.put(entry.getNodeInfo().getSkyNetID().getID(),
								entry);
					}
				} else {
					tempMap.put(entry.getNodeInfo().getSkyNetID().getID(),
							entry);
				}
			}
		}
		TreeMap<Double, AttributeEntry> retMap = new TreeMap<Double, AttributeEntry>();
		Iterator<BigDecimal> completeIter = tempMap.keySet().iterator();
		while (completeIter.hasNext()) {
			entry = tempMap.get(completeIter.next());
			retMap.put(new Double(entry.getRank()), entry);
		}
		return retMap;
	}

	public static TreeMap<BigDecimal, AttributeEntry> getSpecifiedNumberOfEntries(
			LinkedHashMap<BigDecimal, AttributeSubCoordinatorInfo> listOfSubCoordinators,
			int offset, int amount) {
		int upperBound = offset + amount;
		// create the complete map of all entries
		TreeMap<Double, AttributeEntry> temp = getSortedEntries(listOfSubCoordinators);

		Iterator<Double> completeIter = temp.descendingKeySet().iterator();
		int count = 0;
		TreeMap<BigDecimal, AttributeEntry> result = new TreeMap<BigDecimal, AttributeEntry>();
		AttributeEntry t = null;
		Double rank = null;
		while (completeIter.hasNext() && count < upperBound) {
			rank = completeIter.next();
			if (count >= offset) {
				t = temp.get(rank);
				result.put(t.getNodeInfo().getSkyNetID().getID(), t);
			}
			count++;
		}
		return result;
	}

	public QueryAddend processQueryAddend(
			SkyNetLayer skyNetInstance,
			LinkedHashMap<BigDecimal, AttributeSubCoordinatorInfo> listOfSubCoordinators,
			QueryAddend addend, SkyNetNodeInfo queryOriginator,
			boolean isCoordinator) {
		// collect all attributeEntries and put them into the map
		TreeMap<Double, AttributeEntry> temp = getSortedEntries(listOfSubCoordinators);

		if (isCoordinator) {
			// put the own attributes into the map
			if (aStorageCallback.getOwnAttributes() != null) {
				temp.put(aStorageCallback.getOwnAttributes().getRank(),
						aStorageCallback.getOwnAttributes());
			}
		}

		// look for possible matches
		Iterator<Double> completeIter = temp.keySet().iterator();
		AttributeEntry entry = null;
		QueryCondition<?> cond = null;
		String attName = null;
		Attribute<?> att = null;
		boolean hit = true;
		boolean querySolved = false;
		int replies = 0;
		while (completeIter.hasNext()) {
			if (addend.getSearchedElements() > 0) {
				entry = temp.get(completeIter.next());
				if (!entry.getNodeInfo().getSkyNetID().equals(
						queryOriginator.getSkyNetID())) {
					hit = true;
					// check all conditions
					for (int i = 0; i < addend.getNumberOfConditions(); i++) {
						cond = addend.getCondition(i);
						attName = cond.getName();
						att = entry.getAttribute(attName);
						if (!compareValues(att, cond)) {
							hit = false;
							break;
						}
					}
					// if all conditions were true, save the ID of that
					// attributeEntry and decrement the number of searched
					// values
					if (hit) {
						SkyNetNodeInfo node = entry.getNodeInfo();
						boolean match = false;
						// but first, check if the match already exists in the
						// vector
						for (int i = 0; i < addend.getMatches().size(); i++) {
							if (node.getSkyNetID().getID().equals(
									addend.getMatches().get(i).getSkyNetID()
											.getID())) {
								match = true;
								break;
							}
						}
						if (!match) {
							addend.addMatch(node.clone());
							addend.decrementSearchedElements();
							replies++;
							if (addend.getSearchedElements() == 0) {
								querySolved = true;
							}
						}
					}
				} else {
					log.debug(SkyNetUtilities.getNetID(queryOriginator)
							+ "cannot be added,"
							+ " since it is the queryOriginator");
				}
			} else {
				querySolved = true;
				break;
			}
		}
		if (isCoordinator) {
			addend.addReplyingPeer(skyNetInstance.getSkyNetNodeInfo().clone(),
					skyNetInstance.getSkyNetNodeInfo().getLevel(), replies,
					querySolved);
		} else {
			addend.addReplyingPeer(skyNetInstance.getSkyNetNodeInfo().clone(),
					((SupportPeer) skyNetInstance)
							.getSPAttributeUpdateStrategy()
							.getBrotherCoordinator().getLevel(), replies,
					querySolved);
		}
		return addend;
	}

	private static boolean compareValues(Attribute<?> attribute,
			QueryCondition<?> condition) {
		if (condition.getType().equals("Byte")) {
			byte attValue = (Byte) attribute.getValue();
			byte condValue = (Byte) condition.getValue();
			if (condition.getOperand().equals("<")) {
				if (attValue < condValue) {
					return true;
				} else {
					return false;
				}
			} else if (condition.getOperand().equals("=")) {
				if (attValue == condValue) {
					return true;
				} else {
					return false;
				}
			} else if (condition.getOperand().equals(">")) {
				if (attValue > condValue) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		} else if (condition.getType().equals("Boolean")) {
			boolean attValue = (Boolean) attribute.getValue();
			boolean condValue = (Boolean) condition.getValue();
			if (condition.getOperand().equals("=")) {
				if (attValue == condValue) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		} else if (condition.getType().equals("Short")) {
			short attValue = (Short) attribute.getValue();
			short condValue = (Short) condition.getValue();
			if (condition.getOperand().equals("<")) {
				if (attValue < condValue) {
					return true;
				} else {
					return false;
				}
			} else if (condition.getOperand().equals("=")) {
				if (attValue == condValue) {
					return true;
				} else {
					return false;
				}
			} else if (condition.getOperand().equals(">")) {
				if (attValue > condValue) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		} else if (condition.getType().equals("Integer")) {
			int attValue = (Integer) attribute.getValue();
			int condValue = (Integer) condition.getValue();
			if (condition.getOperand().equals("<")) {
				if (attValue < condValue) {
					return true;
				} else {
					return false;
				}
			} else if (condition.getOperand().equals("=")) {
				if (attValue == condValue) {
					return true;
				} else {
					return false;
				}
			} else if (condition.getOperand().equals(">")) {
				if (attValue > condValue) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		} else if (condition.getType().equals("Float")) {
			float attValue = (Float) attribute.getValue();
			float condValue = (Float) condition.getValue();
			if (condition.getOperand().equals("<")) {
				if (attValue < condValue) {
					return true;
				} else {
					return false;
				}
			} else if (condition.getOperand().equals("=")) {
				if (attValue == condValue) {
					return true;
				} else {
					return false;
				}
			} else if (condition.getOperand().equals(">")) {
				if (attValue > condValue) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		} else if (condition.getType().equals("Double")) {
			double attValue = (Double) attribute.getValue();
			double condValue = (Double) condition.getValue();
			if (condition.getOperand().equals("<")) {
				if (attValue < condValue) {
					return true;
				} else {
					return false;
				}
			} else if (condition.getOperand().equals("=")) {
				if (attValue == condValue) {
					return true;
				} else {
					return false;
				}
			} else if (condition.getOperand().equals(">")) {
				if (attValue > condValue) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		} else if (condition.getType().equals("Long")) {
			long attValue = (Long) attribute.getValue();
			long condValue = (Long) condition.getValue();
			if (condition.getOperand().equals("<")) {
				if (attValue < condValue) {
					return true;
				} else {
					return false;
				}
			} else if (condition.getOperand().equals("=")) {
				if (attValue == condValue) {
					return true;
				} else {
					return false;
				}
			} else if (condition.getOperand().equals(">")) {
				if (attValue > condValue) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		} else if (condition.getType().equals("String")) {
			String attValue = (String) attribute.getValue();
			String condValue = (String) condition.getValue();
			if (condition.getOperand().equals("=")) {
				if (attValue.equals(condValue)) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		}
		return false;
	}
}
