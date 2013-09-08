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
import java.util.Vector;

import org.apache.log4j.Logger;
import org.peerfact.api.common.Message;
import org.peerfact.api.common.Operation;
import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.service.skyeye.InputStrategy;
import org.peerfact.api.service.skyeye.SkyNetNodeInfo;
import org.peerfact.api.service.skyeye.SkyNetNodeInterface;
import org.peerfact.api.service.skyeye.SubCoordinatorInfo;
import org.peerfact.impl.overlay.AbstractOverlayNode;
import org.peerfact.impl.overlay.AbstractOverlayNode.PeerStatus;
import org.peerfact.impl.service.aggregation.skyeye.SkyNetHostProperties;
import org.peerfact.impl.service.aggregation.skyeye.SkyNetUtilities;
import org.peerfact.impl.service.aggregation.skyeye.SupportPeerInfo;
import org.peerfact.impl.service.aggregation.skyeye.attributes.messages.AttributeUpdateMsg;
import org.peerfact.impl.service.aggregation.skyeye.components.SkyNetNode;
import org.peerfact.impl.service.aggregation.skyeye.components.SupportPeerHandler;
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
 * This class handles the incoming <i>Attribute-Updates</i> from all
 * Sub-Coordinators as Coordinator. It implements the mechanisms, which are
 * responsible for negotiating the amount of attribute-entries for each node, as
 * well as for the launch of searching a Support Peer. The received
 * <code>AttributeEntry</code>s are delivered to the
 * <code>AttributeStorage</code>, where they can be accessed by other classes,
 * while <code>SupportPeerHandler</code> is called, if a Support Peer must be
 * searched.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 14.11.2008
 * 
 */
public class AttributeInputStrategy implements InputStrategy {

	private static Logger log = SimLogger
			.getLogger(AttributeInputStrategy.class);

	private final SkyNetNodeInterface skyNetNode;

	private final SupportPeerHandler spHandler;

	private final AttributeStorage attributeStorage;

	private final AttributeCollector attributeCollector;

	// fields for SupportPeer-Logic and for determining the amount of received
	// AttributeEntries

	private boolean blockSupportPeerSearch;

	private boolean supportPeerInUse;

	private boolean moreEntriesRequested;

	private boolean downSupportPeer;

	private int actualEntryRequest;

	private final Vector<BigDecimal> newSubCos;

	private int tTreshold;

	private int supportPeerLifetime;

	public AttributeInputStrategy(SkyNetNodeInterface skyNetNode,
			AttributeStorage attributeStorage) {
		this.skyNetNode = skyNetNode;
		spHandler = new SupportPeerHandler(skyNetNode, this, attributeStorage);
		this.attributeStorage = attributeStorage;
		attributeCollector = new AttributeCollector(skyNetNode);
		blockSupportPeerSearch = false;
		supportPeerInUse = false;
		moreEntriesRequested = false;
		downSupportPeer = false;
		actualEntryRequest = 0;
		tTreshold = 0;
		supportPeerLifetime = -1;
		newSubCos = new Vector<BigDecimal>();
	}

	/**
	 * This method is called and executed, if a host goes off-line, or a peer
	 * leaves the overlay. Within this method, the class is reseted to its
	 * initial state.
	 */
	public void reset() {
		blockSupportPeerSearch = false;
		supportPeerInUse = false;
		moreEntriesRequested = false;
		downSupportPeer = false;
		actualEntryRequest = 0;
		spHandler.reset();
		tTreshold = 0;
		supportPeerLifetime = -1;
		newSubCos.clear();
	}

	@Override
	public void processUpdateMessage(Message msg, long timestamp) {
		AttributeUpdateMsg message = (AttributeUpdateMsg) msg;
		if (supportPeerInUse) {
			downSupportPeer = message.isDownSupportPeer();
		}
		SkyNetNodeInfo skyNetNodeInfo = message.getSenderNodeInfo();
		AttributeSubCoordinatorInfo subCoInfo = new AttributeSubCoordinatorInfo(
				skyNetNodeInfo, message.getNumberOfUpdates(), timestamp,
				skyNetNode.getAttributeUpdateStrategy().getUpdateInterval(),
				message.getNumberOfMaxEntries(), message.getContent(), message
						.isSenderSP());
		addSubCoordinator(subCoInfo);
	}

	@Override
	public void writeOwnDataInStorage() {
		attributeStorage.setOwnAttributes(attributeCollector.collectOwnData());
	}

	// ----------------------------------------------------------------------
	// Methods for adding or refreshing SubCoordinators including the data in
	// AttributeStorage, which is delivered by this coordinators
	// ----------------------------------------------------------------------

	@Override
	public void addSubCoordinator(SubCoordinatorInfo subCo) {
		AttributeSubCoordinatorInfo subCoordinator = (AttributeSubCoordinatorInfo) subCo;
		BigDecimal subCoordinatorID = subCoordinator.getNodeInfo()
				.getSkyNetID().getID();
		LinkedHashMap<BigDecimal, AttributeSubCoordinatorInfo> listOfSubCo = attributeStorage
				.getListOfSubCoordinatorsOfCo();

		// Check if a oldSubCoordinator exists or not and put the newer entry or
		// the complete new SubCo to the list of SubCos
		AttributeSubCoordinatorInfo oldSubCoordinator = listOfSubCo
				.remove(subCoordinatorID);
		if (oldSubCoordinator != null) {
			// Read out the requested number of entries for the next
			// Attribute-Update
			log.debug("Old data of SubCoordinator with nodeInfo "
					+ subCoordinatorID.toPlainString()
					+ " is substituted by new data");
		} else {
			newSubCos.add(subCoordinatorID);
			moreEntriesRequested = true;
		}
		listOfSubCo.put(subCoordinatorID, subCoordinator);
	}

	// ----------------------------------------------------------------------
	// Methods for the interaction with the SupportPeerHandler to determine if a
	// SupportPeer is needed and to distribute the new information to the
	// SubCoordinators
	// ----------------------------------------------------------------------

	/**
	 * This method updates the value of <code>actualEntryRequest</code> with the
	 * actual value of the current update-period. In addition, it sets the
	 * <code>moreEntriesRequested</code>-flag, if more entries are requested, if
	 * an existing Support Peer crashed or if the lifetime of an old Support
	 * Peer is over.
	 */
	public void compareTempWithActualEntryRequest() {
		if (supportPeerLifetime > 0) {
			supportPeerLifetime--;
			log.debug(SkyNetUtilities.getTimeAndNetID(skyNetNode)
					+ " lifetime = " + supportPeerLifetime);
		}
		// Check if the SubCos want to send more entries, if the lifetime of a
		// SupportPeer is over or if a SupportPeer crashed. All these events
		// will cause the SkyNetNode, to set the moreEntriesRequested-flag to
		// true, which
		// results in executing the method assignEntryAmountToSubCo().

		LinkedHashMap<BigDecimal, AttributeSubCoordinatorInfo> subCoList = attributeStorage
				.getListOfSubCoordinatorsOfCo();
		Iterator<BigDecimal> subCoIter = subCoList.keySet().iterator();
		int ter = 0;
		while (subCoIter.hasNext()) {
			ter += subCoList.get(subCoIter.next()).getRequestedEntries();
		}
		if (ter > actualEntryRequest || supportPeerLifetime == 0
				|| downSupportPeer) {
			moreEntriesRequested = true;
			if (supportPeerLifetime == 0) {
				supportPeerLifetime = -1;
				log.warn(SkyNetUtilities.getTimeAndNetID(skyNetNode)
						+ "Lifetime of SupportPeer is over, searching new one");
				spHandler.removeSupportPeer();
			} else if (downSupportPeer) {
				log.warn(SkyNetUtilities.getTimeAndNetID(skyNetNode)
						+ "SupportPeer is down, searching new one");
				spHandler.removeSupportPeer();
			}
		}
		actualEntryRequest = ter;
	}

	/**
	 * If the <code>moreEntriesRequested</code>-flag was set, this method is
	 * executed. Within this method the class determines through the variable
	 * <code>actualEntryRequest</code> the actual amount of request entries from
	 * the Sub-Coordinators. Depending on the value, the Coordinator can handle
	 * the amount, or starts the search for a new Support Peer, if
	 * <code>supportPeerInUse</code> is <code>false</code>. In that case,
	 * <code>determineNewSupportPeer(int index)</code> from
	 * <code>SupportPeerHandler</code> is called, otherwise the capacities of
	 * the Coordintor and the Support Peer are reallocated.
	 */
	public void checkForSupportPeer() {
		moreEntriesRequested = false;
		tTreshold = ((SkyNetHostProperties) skyNetNode.getHost()
				.getProperties()).getTTresholdCo();
		if (actualEntryRequest <= tTreshold) {
			blockSupportPeerSearch = false;
			supportPeerLifetime = -1;
			spHandler.removeSupportPeer();
			log.debug(SkyNetUtilities.getTimeAndNetID(skyNetNode)
					+ " Everything in bounds with " + actualEntryRequest);
			LinkedHashMap<BigDecimal, AttributeSubCoordinatorInfo> listOfSubs = attributeStorage
					.getListOfSubCoordinatorsOfCo();
			Iterator<BigDecimal> ids = listOfSubs.keySet().iterator();
			BigDecimal id = null;
			AttributeSubCoordinatorInfo subCo = null;
			while (ids.hasNext()) {
				id = ids.next();
				subCo = listOfSubs.get(id);
				sendParentCoordinatorInfo(subCo, null, subCo
						.getRequestedEntries());
			}
		} else {
			if (supportPeerInUse
					|| attributeStorage.getActualAmountOfEntriesOfCo() == 0) {
				if (attributeStorage.getActualAmountOfEntriesOfCo() == 0) {
					log.warn(SkyNetUtilities.getTimeAndNetID(skyNetNode)
							+ "has actually no candidates for a supportPeer");
					sendNewEntryInfoWithoutSP();
				} else {
					log.debug(SkyNetUtilities.getTimeAndNetID(skyNetNode)
							+ "reorganizing entries between the SubCos");
					sendNewEntryInfoWithSP(false);
				}
			} else {
				log.warn(SkyNetUtilities.getTimeAndNetID(skyNetNode)
						+ "looking for a SupportPeer");
				blockSupportPeerSearch = true;
				spHandler.determineNewSupportPeer(1);
			}
		}
	}

	/**
	 * If there is actually a Support Peer needed, but the
	 * <code>AttributeStorage</code> contains no <code>AttributeEntry</code>s
	 * from other nodes to choose a Support Peer, this method is executed.
	 */
	public void sendNewEntryInfoWithoutSP() {
		log.warn(SkyNetUtilities.getTimeAndNetID(skyNetNode)
				+ "Sending new EntryInformation without SupportPeer"
				+ ", since no one is available");
		blockSupportPeerSearch = false;
		LinkedHashMap<BigDecimal, AttributeSubCoordinatorInfo> listOfSubs = attributeStorage
				.getListOfSubCoordinatorsOfCo();

		Iterator<BigDecimal> iter = listOfSubs.keySet().iterator();
		int totalAmountOfReqEntries = 0;
		while (iter.hasNext()) {
			totalAmountOfReqEntries = totalAmountOfReqEntries
					+ listOfSubs.get(iter.next()).getRequestedEntries();
		}

		Iterator<BigDecimal> ids = listOfSubs.keySet().iterator();
		BigDecimal id = null;
		AttributeSubCoordinatorInfo subCo = null;
		float percentageCo = -1;
		int percentAttributeEntriesForCo = -1;
		while (ids.hasNext()) {
			id = ids.next();
			subCo = listOfSubs.get(id);
			percentageCo = ((float) subCo.getRequestedEntries() / (float) totalAmountOfReqEntries);
			percentAttributeEntriesForCo = (int) Math
					.ceil((percentageCo * tTreshold));
			if (skyNetNode.getTreeHandler().isRoot()) {
				log.debug("sending info without SP to "
						+ SkyNetUtilities.getNetID(subCo.getNodeInfo())
						+ ": amount of entries = "
						+ percentAttributeEntriesForCo
						+ "; with a percentage = " + percentageCo
						+ "; at a tThreshold = " + tTreshold);
			}
			log.debug(SkyNetUtilities.getTimeAndNetID(skyNetNode) + " allows "
					+ percentAttributeEntriesForCo + " entries for a subCo");
			sendParentCoordinatorInfo(subCo, null, percentAttributeEntriesForCo);
		}
		if (newSubCos.size() != 0) {
			newSubCos.clear();
		}
	}

	/**
	 * This method creates the <code>SupportPeerInfo</code>-objects for ever
	 * Sub-Coordinator and supplies everyone with the amount of entries, which
	 * is can be received.
	 * 
	 * @param newSupportPeer
	 *            determines if a new Support Peer was chosen, or if an old one
	 *            already is utilised
	 */
	public void sendNewEntryInfoWithSP(boolean newSupportPeer) {
		blockSupportPeerSearch = false;
		SupportPeerInfo spInfo = spHandler.getActiveSupportPeer();
		int tTresholdOfSP = spInfo.getTThreshold();
		LinkedHashMap<BigDecimal, AttributeSubCoordinatorInfo> listOfSubs = attributeStorage
				.getListOfSubCoordinatorsOfCo();

		// CHECK here, a more complicated and sophisticated strategy can be used
		// in future
		Iterator<BigDecimal> iter = listOfSubs.keySet().iterator();
		int totalAmountOfReqEntries = 0;
		while (iter.hasNext()) {
			totalAmountOfReqEntries = totalAmountOfReqEntries
					+ listOfSubs.get(iter.next()).getRequestedEntries();
		}

		Iterator<BigDecimal> ids = listOfSubs.keySet().iterator();
		BigDecimal id = null;
		AttributeSubCoordinatorInfo subCo = null;
		double percentageCo = -1;
		int percentAttributeEntriesForCo = -1;
		int percentAttributeEntriesForSP = -1;
		while (ids.hasNext()) {
			id = ids.next();
			subCo = listOfSubs.get(id);
			if (subCo.getRequestedEntries() < 1) {
				log.error(SkyNetUtilities.getTimeAndNetID(subCo.getNodeInfo())
						+ "request less than 1 entry");
			}
			percentageCo = subCo.getRequestedEntries()
					/ (double) totalAmountOfReqEntries;
			percentAttributeEntriesForCo = (int) Math
					.ceil((percentageCo * tTreshold));
			percentAttributeEntriesForSP = (int) Math
					.ceil((percentageCo * tTresholdOfSP));
			if (skyNetNode.getTreeHandler().isRoot()) {
				log.debug("sending info including SP to "
						+ SkyNetUtilities.getNetID(subCo.getNodeInfo())
						+ ": amount of entries = "
						+ percentAttributeEntriesForCo
						+ "; with a percentage = " + percentageCo
						+ "; at a tThreshold = " + tTreshold);
			}
			if (newSubCos.contains(id) || newSupportPeer) {
				newSubCos.remove(id);
				sendParentCoordinatorInfo(subCo, new SupportPeerInfo(spInfo
						.getNodeInfo(), supportPeerLifetime, Simulator
						.getCurrentTime(), percentAttributeEntriesForSP),
						percentAttributeEntriesForCo);
			} else {
				sendParentCoordinatorInfo(subCo, null,
						percentAttributeEntriesForCo);
			}
		}
		if (newSubCos.size() != 0) {
			newSubCos.clear();
		}
		if (newSupportPeer) {
			log.warn(SkyNetUtilities.getTimeAndNetID(skyNetNode)
					+ "sets his new SupportPeer "
					+ SkyNetUtilities.getNetID(spInfo) + " for the first time");
		}
	}

	// ----------------------------------------------------------------------
	// Methods for calling the parentCoordinatorInfoUpdate()-method as well as
	// processing the result of this call (succeeded/failed)
	// ----------------------------------------------------------------------

	/**
	 * This method calls <code>parentCoordinatorInfoUpdate</code> from
	 * <code>SkyNetNode</code>, which starts the
	 * <code>ParentCoordinatorInformationOperation</code>.
	 * 
	 * @param subCo
	 *            contains the receiving Sub-Coordinator of the sent message
	 * @param spInfo
	 *            contains the new <code>SupportPeerInfo</code>-object for the
	 *            Sub-Coordinator
	 * @param maxEntriesForCo
	 *            contains the maximum amount of attribute-entries, which the
	 *            Sub-Coordinator can send.
	 */
	private void sendParentCoordinatorInfo(AttributeSubCoordinatorInfo subCo,
			SupportPeerInfo spInfo, int maxEntriesForCo) {
		if (((AbstractOverlayNode<?, ?>) skyNetNode.getOverlayNode())
				.getPeerStatus()
				.equals(PeerStatus.PRESENT)) {
			SkyNetNodeInfo senderInfo = skyNetNode.getSkyNetNodeInfo();
			final SkyNetNodeInfo receiverInfo = subCo.getNodeInfo();
			long msgNo = skyNetNode.getMessageCounter()
					.assignmentOfMessageNumber();
			((SkyNetNode) skyNetNode).parentCoordinatorInfoUpdate(senderInfo,
					receiverInfo, spInfo, maxEntriesForCo, msgNo, subCo
							.isSupportPeer(), new OperationCallback<Object>() {

						@Override
						public void calledOperationFailed(Operation<Object> op) {
							parentCoordinatorInfoFailed(receiverInfo);
						}

						@Override
						public void calledOperationSucceeded(
								Operation<Object> op) {
							parentCoordinatorInfoSucceeded(op);
						}
					});
		} else {
			log.warn(SkyNetUtilities.getTimeAndNetID(skyNetNode)
					+ "cannot send ParentCoordinatorInfo"
					+ ", because he is not PRESENT");
		}
	}

	void parentCoordinatorInfoFailed(SkyNetNodeInfo receiverInfo) {
		log.info(SkyNetUtilities.getTimeAndNetID(skyNetNode)
				+ "----NO CHANCE TO SEND PARENTCOORDINATORINFO TO "
				+ SkyNetUtilities.getNetID(receiverInfo) + "----");
	}

	void parentCoordinatorInfoSucceeded(Operation<Object> op) {
		log.debug(SkyNetUtilities.getTimeAndNetID(skyNetNode)
				+ "ParentCoordinatorInfoOperation with id "
				+ op.getOperationID() + " succeeded");
	}

	// ----------------------------------------------------------------------
	// Getters and Setters
	// ----------------------------------------------------------------------

	public boolean isSupportPeerInUse() {
		return supportPeerInUse;
	}

	public void setSupportPeerInUse(boolean supportPeerInUse) {
		this.supportPeerInUse = supportPeerInUse;
	}

	public void setDownSupportPeer(boolean downSupportPeer) {
		this.downSupportPeer = downSupportPeer;
	}

	public boolean isMoreEntriesRequested() {
		return moreEntriesRequested;
	}

	public void setMoreEntriesRequested(boolean newSubCo) {
		this.moreEntriesRequested = newSubCo;
	}

	public boolean isBlockSupportPeerSearch() {
		return blockSupportPeerSearch;
	}

	public void setBlockSupportPeerSearch(boolean blockSupportPeerSearch) {
		this.blockSupportPeerSearch = blockSupportPeerSearch;
	}

	public int getSupportPeerLifetime() {
		return supportPeerLifetime;
	}

	public void setSupportPeerLifetime(int supportPeerLifetime) {
		this.supportPeerLifetime = supportPeerLifetime;
	}

	public SupportPeerHandler getSpHandler() {
		return spHandler;
	}

	public AttributeStorage getAttributeStorage() {
		return attributeStorage;
	}

}
