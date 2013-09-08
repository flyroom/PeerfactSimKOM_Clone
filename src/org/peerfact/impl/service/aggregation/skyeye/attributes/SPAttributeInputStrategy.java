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

import org.apache.log4j.Logger;
import org.peerfact.api.common.Message;
import org.peerfact.api.service.skyeye.InputStrategy;
import org.peerfact.api.service.skyeye.SkyNetNodeInfo;
import org.peerfact.api.service.skyeye.SubCoordinatorInfo;
import org.peerfact.api.service.skyeye.SupportPeer;
import org.peerfact.impl.service.aggregation.skyeye.SkyNetHostProperties;
import org.peerfact.impl.service.aggregation.skyeye.SkyNetUtilities;
import org.peerfact.impl.service.aggregation.skyeye.attributes.messages.AttributeUpdateMsg;
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
 * This class handles the incoming <i>Attribute-Updates</i> from all
 * Sub-Coordinators as Support Peer. It is much more simpler than the
 * corresponding class <code>AttributeInputStrategy</code> for a Coordinator.
 * Since the methods of this class have the same names as in
 * <code>AttributeInputStrategy</code> and contain nearly the same
 * functionality, please refer to {@link AttributeInputStrategy}.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 15.11.2008
 * 
 */
public class SPAttributeInputStrategy implements InputStrategy {

	private static Logger log = SimLogger
			.getLogger(SPAttributeInputStrategy.class);

	private SupportPeer supportPeer;

	private AttributeStorage attributeStorage;

	// fields for SupportPeer-Logic and for determining the amount of received
	// AttributeEntries

	// private int actualEntryRequest;

	private int tTreshold;

	public SPAttributeInputStrategy(SupportPeer supportPeer,
			AttributeStorage attributeStorage) {
		this.supportPeer = supportPeer;
		this.attributeStorage = attributeStorage;
		// actualEntryRequest = 0;
		tTreshold = 0;
	}

	public void reset() {
		// actualEntryRequest = 0;
		tTreshold = 0;
	}

	@Override
	public void processUpdateMessage(Message msg, long timestamp) {
		AttributeUpdateMsg message = (AttributeUpdateMsg) msg;
		SkyNetNodeInfo skyNetNodeInfo = message.getSenderNodeInfo();
		AttributeSubCoordinatorInfo subCoInfo = new AttributeSubCoordinatorInfo(
				skyNetNodeInfo, message.getNumberOfUpdates(), timestamp,
				supportPeer.getSPAttributeUpdateStrategy().getUpdateInterval(),
				message.getNumberOfMaxEntries(), message.getContent(), message
						.isSenderSP());
		addSubCoordinator(subCoInfo);
	}

	@Override
	public void writeOwnDataInStorage() {
		// not needed
	}

	// ----------------------------------------------------------------------
	// Methods for adding or refreshing SubCoordinators including the data in
	// AttributeStorage, which is delivered by this coordinators
	// ----------------------------------------------------------------------

	@Override
	public void addSubCoordinator(SubCoordinatorInfo subCo) {
		tTreshold = ((SkyNetHostProperties) supportPeer.getHost()
				.getProperties()).getTTresholdSP();
		AttributeSubCoordinatorInfo subCoordinator = (AttributeSubCoordinatorInfo) subCo;
		BigDecimal subCoordinatorID = subCoordinator.getNodeInfo()
				.getSkyNetID().getID();

		// Check if a oldSubCoordinator exists or not and put the newer entry or
		// the complete new SubCo to the list of SubCos

		LinkedHashMap<BigDecimal, AttributeSubCoordinatorInfo> subCoList = attributeStorage
				.getListOfSubCoordinatorsOfSP();
		subCoList.remove(subCoordinatorID);
		subCoList.put(subCoordinatorID, subCoordinator);

		Iterator<BigDecimal> subCoIter = subCoList.keySet().iterator();
		int ter = 0;
		AttributeSubCoordinatorInfo s = null;
		while (subCoIter.hasNext()) {
			s = subCoList.get(subCoIter.next());
			ter += s.getRequestedEntries();
		}
		// if (ter > actualEntryRequest) {
		if (ter > tTreshold) {
			log.warn(SkyNetUtilities.getTimeAndNetID(supportPeer)
					+ "received too much entries as SupportPeer");
		}
		// }
	}
}
