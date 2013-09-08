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
import org.peerfact.api.service.skyeye.SkyNetNodeInfo;
import org.peerfact.api.service.skyeye.SkyNetNodeInterface;
import org.peerfact.api.service.skyeye.Storage;
import org.peerfact.api.service.skyeye.SupportPeer;
import org.peerfact.impl.service.aggregation.skyeye.SkyNetUtilities;
import org.peerfact.impl.service.aggregation.skyeye.queries.QueryAddend;
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
 * This class is responsible for storing all attribute-entries, that a
 * Coordinator, and possibly a Support Peer, receives. The entries for the
 * Coordinator are stored within <code>listOfSubCoordinatorsOfCo</code>, which
 * consists of all Sub-Coordinators and their provided entries. The entries for
 * the Support Peer are stored within <code>listOfSubCoordinatorsOfSP</code>
 * just as for the Coordinator. To distinguish between the access to the entries
 * of the Coordinator and the Support Peer, every method, which operates on the
 * entries, exists twice, on the one hand for the access to entries of the
 * Coordinator and on the other hand for the access to the entries of the
 * Support Peer.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 06.12.2008
 * 
 */
public class AttributeStorage implements Storage {

	private static Logger log = SimLogger.getLogger(AttributeStorage.class);

	private final AttributeStorageDelegator delegator;

	private AttributeEntry ownAttributes;

	private final SkyNetNodeInterface skyNetNode;

	private final SupportPeer supportPeer;

	// References for the peer acting as Coordinator
	private LinkedHashMap<BigDecimal, AttributeSubCoordinatorInfo> listOfSubCoordinatorsOfCo;

	// References for the peer acting as SupportPeer
	private LinkedHashMap<BigDecimal, AttributeSubCoordinatorInfo> listOfSubCoordinatorsOfSP;

	public AttributeStorage(SkyNetNodeInterface skyNetNode,
			SupportPeer supportPeer) {
		listOfSubCoordinatorsOfCo = new LinkedHashMap<BigDecimal, AttributeSubCoordinatorInfo>();
		listOfSubCoordinatorsOfSP = new LinkedHashMap<BigDecimal, AttributeSubCoordinatorInfo>();
		this.skyNetNode = skyNetNode;
		this.supportPeer = supportPeer;
		this.delegator = new AttributeStorageDelegator(this);
	}

	public void reset() {
		listOfSubCoordinatorsOfCo = new LinkedHashMap<BigDecimal, AttributeSubCoordinatorInfo>();
		listOfSubCoordinatorsOfSP = new LinkedHashMap<BigDecimal, AttributeSubCoordinatorInfo>();
	}

	/**
	 * This method returns a candidate for the role of a Support Peer. As a
	 * Support Peer can just be chosen by a Coordinator, this method exists only
	 * for the Coordinator. The provided parameter specifies the place of the
	 * entry in the list of attribute-entries, whose corresponding node will be
	 * asked to act as Support Peer. If the parameter exceeds the limit of the
	 * stored entries, the method returns <code>null</code>.
	 * 
	 * @param index
	 *            contains the place of the entry in the list of
	 *            attribute-entries, whose corresponding node will be asked to
	 *            act as Support Peer
	 * @return the chosen <code>AttributeEntry</code>, which also contains the
	 *         required information to address the corresponding node
	 */
	public AttributeEntry seekForSupportPeer(int index) {
		TreeMap<Double, AttributeEntry> temp = AttributeStorageDelegator
				.getSortedEntries(listOfSubCoordinatorsOfCo);
		Iterator<Double> reverseOrder = temp.descendingKeySet().iterator();
		if (temp.size() > 0) {
			int i = 1;
			AttributeEntry ae = null;
			while (reverseOrder.hasNext()) {
				if (i == index) {
					ae = temp.get(reverseOrder.next());
					temp.clear();
					return ae;
				} else {
					reverseOrder.next();
					i++;
				}
			}
		}
		log.warn(SkyNetUtilities.getTimeAndNetID(skyNetNode)
				+ "No candidates for SupportPeer available");
		return null;
	}

	/**
	 * This method resolves a clause of a query based on the knowledge of the
	 * <code>AttributeStorage</code>. The found matches are stored within the
	 * clause, which is returned after the resolution. This method is executed
	 * by the Coordinator.
	 * 
	 * @param addend
	 *            contains the clause of a query
	 * @param queryOriginator
	 *            contains the ID of the query-originator to avoid that the
	 *            originator itself is added as a match
	 * @return the processed clause, which includes all found matches for the
	 *         specified conditions within the clause
	 */
	public QueryAddend processQueryAddendOfCo(QueryAddend addend,
			SkyNetNodeInfo queryOriginator) {
		return delegator.processQueryAddend(skyNetNode,
				listOfSubCoordinatorsOfCo, addend, queryOriginator, true);
	}

	/**
	 * This method resolves a clause of a query based on the knowledge of the
	 * <code>AttributeStorage</code>. The found matches are stored within the
	 * clause, which is returned after the resolution. This method is executed
	 * by the Support Peer.
	 * 
	 * @param addend
	 *            contains the clause of a query
	 * @param queryOriginator
	 *            contains the ID of the query-originator to avoid that the
	 *            originator itself is added as a match
	 * @return the processed clause, which includes all found matches for the
	 *         specified conditions within the clause
	 */
	public QueryAddend processQueryAddendOfSP(QueryAddend addend,
			SkyNetNodeInfo queryOriginator) {
		return delegator.processQueryAddend(supportPeer,
				listOfSubCoordinatorsOfSP, addend, queryOriginator, false);
	}

	/**
	 * This method returns all attribute-entries of
	 * <code>listOfSubCoordinatorsOfCo</code>, which are situated within the
	 * range, that is specified by the two parameters. If the defined range is
	 * larger than the amount of available attribute-entries, the method returns
	 * all entries, which are currently stored in the list. The result of this
	 * method is a list of attribute-entries, which is sorted in a descending
	 * order according to the quality of the attribute-entry. This method is
	 * called by the Coordinator.
	 * 
	 * @param offset
	 *            contains the lower bound of the range
	 * @param amount
	 *            contains the upper bound of the range
	 * @return the sorted list of attribute-entries, whose amount is specified
	 *         by the two parameters
	 */
	public TreeMap<BigDecimal, AttributeEntry> getSpecifiedNumberOfEntriesOfCo(
			int offset, int amount) {
		return AttributeStorageDelegator.getSpecifiedNumberOfEntries(
				listOfSubCoordinatorsOfCo,
				offset, amount);
	}

	/**
	 * This method returns all attribute-entries of
	 * <code>listOfSubCoordinatorsOfSP</code>, which are situated within the
	 * range, that is specified by the two parameters. If the defined range is
	 * larger than the amount of available attribute-entries, the method returns
	 * all entries, which are currently stored in the list. The result of this
	 * method is a list of attribute-entries, which is sorted in a descending
	 * order according to the quality of the attribute-entry. This method is
	 * called by the Support Peer.
	 * 
	 * @param offset
	 *            contains the lower bound of the range
	 * @param amount
	 *            contains the upper bound of the range
	 * @return the sorted list of attribute-entries, whose amount is specified
	 *         by the two parameters
	 */
	public TreeMap<BigDecimal, AttributeEntry> getSpecifiedNumberOfEntriesOfSP(
			int offset, int amount) {
		return AttributeStorageDelegator.getSpecifiedNumberOfEntries(
				listOfSubCoordinatorsOfSP,
				offset, amount);
	}

	/**
	 * This method returns a list of the current Sub-Coordinators of a
	 * Coordinator.
	 * 
	 * @return the list of the Sub-Coordinators
	 */
	public LinkedHashMap<BigDecimal, AttributeSubCoordinatorInfo> getListOfSubCoordinatorsOfCo() {
		return listOfSubCoordinatorsOfCo;
	}

	/**
	 * This method stores the own attribute-entry in the storage.
	 * 
	 * @param ownAttributes
	 *            contains the own <code>AttributeEntry</code>-object
	 */
	public void setOwnAttributes(AttributeEntry ownAttributes) {
		this.ownAttributes = ownAttributes;
	}

	/**
	 * This method returns the own attribute-entry of a SkyNet-node, which is
	 * also stored within the <code>AttributeStorage</code>.
	 * 
	 * @return the own <code>AttributeEntry</code>-object
	 */
	public AttributeEntry getOwnAttributes() {
		return ownAttributes;
	}

	/**
	 * This method returns a list of the current Sub-Coordinators of a Support
	 * Peer.
	 * 
	 * @return the list of the Sub-Coordinators
	 */
	public LinkedHashMap<BigDecimal, AttributeSubCoordinatorInfo> getListOfSubCoordinatorsOfSP() {
		return listOfSubCoordinatorsOfSP;
	}

	/**
	 * This method returns the amount of attribute-entries, which are currently
	 * stored for a Coordinator.
	 * 
	 * @return amount of attribute-entries of a Coordinator
	 */
	public int getActualAmountOfEntriesOfCo() {
		return AttributeStorageDelegator.getSortedEntries(
				listOfSubCoordinatorsOfCo).size();
	}

	/**
	 * This method returns the amount of attribute-entries, which are currently
	 * stored for a Support Peer.
	 * 
	 * @return amount of attribute-entries of a Support Peer
	 */
	public int getActualAmountOfEntriesOfSP() {
		return AttributeStorageDelegator.getSortedEntries(
				listOfSubCoordinatorsOfSP).size();
	}

	/**
	 * This method returns the current amount of Sub-Coordinators of a
	 * Coordinator
	 * 
	 * @return amount of Sub-Coordinators of a Coordinator
	 */
	public int getSubCoCounterOfCo() {
		return listOfSubCoordinatorsOfCo.size();
	}

	/**
	 * This method returns the current amount of Sub-Coordinators of a Support
	 * Peer
	 * 
	 * @return amount of Sub-Coordinators of a Support Peer
	 */
	public int getSubCoCounterOfSP() {
		return listOfSubCoordinatorsOfSP.size();
	}

	public TreeMap<Double, AttributeEntry> getActualEntriesOfCo() {
		return AttributeStorageDelegator
				.getSortedEntries(listOfSubCoordinatorsOfCo);
	}

}
