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

package org.peerfact.impl.service.aggregation.skyeye.components;

import org.apache.log4j.Logger;
import org.peerfact.api.common.Operation;
import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.service.skyeye.SkyNetNodeInfo;
import org.peerfact.api.service.skyeye.SkyNetNodeInterface;
import org.peerfact.impl.service.aggregation.skyeye.SkyNetUtilities;
import org.peerfact.impl.service.aggregation.skyeye.SupportPeerInfo;
import org.peerfact.impl.service.aggregation.skyeye.attributes.AttributeEntry;
import org.peerfact.impl.service.aggregation.skyeye.attributes.AttributeInputStrategy;
import org.peerfact.impl.service.aggregation.skyeye.attributes.AttributeStorage;
import org.peerfact.impl.service.aggregation.skyeye.attributes.messages.SupportPeerRequestACKMsg;
import org.peerfact.impl.service.aggregation.skyeye.attributes.operations.SupportPeerRequestOperation;
import org.peerfact.impl.service.aggregation.skyeye.attributes.operations.SupportPeerUpdateOperation;
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
 * This class is responsible for the determination of an appropriate Support
 * Peer, which is utilized for load-balancing. In addition, this class provides
 * the information about the usage of a Support Peer and about its ID etc., if a
 * Support Peer is utilized.<br>
 * This class is mostly accessed by {@link AttributeInputStrategy}, while it
 * accesses {@link AttributeStorage} for the determination of an appropriate
 * Support Peer.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 04.12.2008
 * 
 */
public class SupportPeerHandler {

	private static Logger log = SimLogger.getLogger(SupportPeerHandler.class);

	private SupportPeerInfo activeSupportPeer;

	private AttributeInputStrategy attributeInput;

	private AttributeStorage attributeStorage;

	private SkyNetNodeInterface skyNetNode;

	public SupportPeerHandler(SkyNetNodeInterface skyNetNode,
			AttributeInputStrategy attributeInput,
			AttributeStorage attributeStorage) {
		activeSupportPeer = null;
		this.attributeInput = attributeInput;
		this.attributeStorage = attributeStorage;
		this.skyNetNode = skyNetNode;
	}

	// ----------------------------------------------------------------------
	// Methods for managing and handling the possible activeSupportPeer
	// ----------------------------------------------------------------------

	/**
	 * This method puts <code>SupportPeerHandler</code> back in its initial
	 * state, if the host got off-line or if the peer left the underlying
	 * overlay.
	 */
	public void reset() {
		activeSupportPeer = null;
	}

	/**
	 * This method is responsible for storing the information about the
	 * retrieved Support Peer, which can afterwards be accessed by the
	 * SkyNet-node.
	 * 
	 * @param supportPeer
	 *            contains the info about the Support Peer
	 */
	public void setSupportPeer(SupportPeerInfo supportPeer) {
		activeSupportPeer = supportPeer;
		attributeInput.setSupportPeerInUse(true);
		attributeInput.setDownSupportPeer(false);
		// TODO choose lifetime a little bit more sophisticated
		attributeInput.setSupportPeerLifetime(20);
	}

	/**
	 * This method removes the information of the Support Peer, which is not
	 * needed any longer.
	 */
	public void removeSupportPeer() {
		activeSupportPeer = null;
		attributeInput.setSupportPeerInUse(false);
		log.debug("Removed SupportPeer");
	}

	/**
	 * This method provides the information of the currently used Support Peer
	 * to the SkyNet-node.
	 * 
	 * @return the information of the utilized SupportPeer
	 */
	public SupportPeerInfo getActiveSupportPeer() {
		return activeSupportPeer;
	}

	// ----------------------------------------------------------------------
	// Methods for determining a new candidate for the SupportPeer from the
	// storage, for informing the candidate and for handling the answer
	// ----------------------------------------------------------------------

	/**
	 * This method starts a <code>SupportPeerRequestOperation</code>, which begs
	 * a chosen node for the execution of the role as Support Peer.
	 * 
	 * @param senderInfo
	 *            contains the ID of the sender
	 * @param receiverInfo
	 *            contains the ID of the receiver
	 * @param supportPeerInfo
	 *            contains the info of the node, to which this message is sent
	 * @param skyNetMsgID
	 *            specifies the ID of the message
	 * @param callback
	 *            contains the object, which receives the corresponding ACK
	 * @return the ID of the executed operation
	 */
	public int informNewSupportPeer(SkyNetNodeInfo senderInfo,
			SkyNetNodeInfo receiverInfo, SkyNetNodeInfo supportPeerInfo,
			long skyNetMsgID,
			OperationCallback<SupportPeerRequestACKMsg> callback) {
		SupportPeerRequestOperation op = new SupportPeerRequestOperation(
				skyNetNode, senderInfo, receiverInfo, supportPeerInfo,
				skyNetMsgID, callback);
		op.scheduleImmediately();
		return op.getOperationID();
	}

	/**
	 * This method utilizes the {@link AttributeStorage} for the retrieval of a
	 * possible Support Peer. By the provided number, this method specifies the
	 * index of the candidate for a Support Peer within {@link AttributeStorage}
	 * , which shall be retrieved.
	 * 
	 * @param index
	 *            contains the index of a candidate within
	 *            {@link AttributeStorage}
	 */
	public void determineNewSupportPeer(int index) {
		final int usedIndex = index;
		final AttributeEntry spInfo = attributeStorage
				.seekForSupportPeer(index);
		if (spInfo != null) {
			informNewSupportPeer(skyNetNode.getSkyNetNodeInfo(), spInfo
					.getNodeInfo(), skyNetNode.getTreeHandler()
					.getParentCoordinator(), skyNetNode.getMessageCounter()
					.assignmentOfMessageNumber(),
					new OperationCallback<SupportPeerRequestACKMsg>() {

						@Override
						public void calledOperationFailed(
								Operation<SupportPeerRequestACKMsg> op) {
							supportPeerRequestFailed(spInfo.getNodeInfo(),
									usedIndex);
						}

						@Override
						public void calledOperationSucceeded(
								Operation<SupportPeerRequestACKMsg> op) {
							supportPeerRequestSucceeded(op, spInfo, usedIndex);
						}
					});
		} else {
			attributeInput.sendNewEntryInfoWithoutSP();
		}
	}

	void supportPeerRequestFailed(SkyNetNodeInfo receiverInfo, int usedIndex) {
		log.error(SkyNetUtilities.getTimeAndNetID(skyNetNode)
				+ "----NO CHANCE TO SEND " + usedIndex
				+ ". SUPPORTPEERREQUESTINFO TO "
				+ SkyNetUtilities.getNetID(receiverInfo) + "----");
		if ((usedIndex + 1) < attributeStorage.getActualAmountOfEntriesOfCo()) {
			determineNewSupportPeer((usedIndex + 1));
		} else {
			attributeInput.sendNewEntryInfoWithoutSP();
		}
	}

	void supportPeerRequestSucceeded(Operation<SupportPeerRequestACKMsg> op,
			AttributeEntry spInfo, int usedIndex) {
		SupportPeerRequestACKMsg msg = op.getResult();
		if (msg.isAccept()) {
			log.warn(SkyNetUtilities.getTimeAndNetID(skyNetNode)
					+ "SupportPeerRequestOperation with id "
					+ op.getOperationID() + " succeeded!");
			int tTresholdSP = ((Integer) spInfo.getAttribute("tTresholdCo")
					.getValue()).intValue();

			SupportPeerInfo supportPeerInfo = new SupportPeerInfo(spInfo
					.getNodeInfo(), Integer.MAX_VALUE, Simulator
					.getCurrentTime(), tTresholdSP);

			setSupportPeer(supportPeerInfo);
			attributeInput.sendNewEntryInfoWithSP(true);
		} else {
			log.warn(SkyNetUtilities.getTimeAndNetID(skyNetNode)
					+ "SupportPeerRequestOperation with id "
					+ op.getOperationID()
					+ " succeeded, but the chosen peer is already SupportPeer");
			if ((usedIndex + 1) < attributeStorage
					.getActualAmountOfEntriesOfCo()) {
				determineNewSupportPeer((usedIndex + 1));
			} else {
				attributeInput.sendNewEntryInfoWithoutSP();
			}
		}

	}

	// ----------------------------------------------------------------------
	// Methods for updating the settings at the supportPeer (new
	// ParentCoordinator has to be distributed to the SupportPeer)
	// ----------------------------------------------------------------------

	/**
	 * This method starts a <code>SupportPeerUpdateOperation</code>, which
	 * informs the current Support Peer of a new Parent-Coordinator, to which
	 * the Support Peer must send its attribute-updates.
	 * 
	 * @param senderInfo
	 *            contains the ID of the sender
	 * @param receiverInfo
	 *            contains the ID of the receiver
	 * @param parentCoordinator
	 *            contains the info of the new Parent-Coordinator
	 * @param skyNetMsgID
	 *            specifies the ID of the message
	 * @param callback
	 *            contains the object, which receives the corresponding ACK
	 * @return the ID of the executed operation
	 */
	public int updateSupportPeer(SkyNetNodeInfo senderInfo,
			SkyNetNodeInfo receiverInfo, SkyNetNodeInfo parentCoordinator,
			long skyNetMsgID, OperationCallback<Object> callback) {
		SupportPeerUpdateOperation op = new SupportPeerUpdateOperation(
				skyNetNode, senderInfo, receiverInfo, parentCoordinator,
				skyNetMsgID, callback);
		op.scheduleImmediately();
		return op.getOperationID();
	}

	public void updateCurrentSettingsOfSupportPeer(
			SkyNetNodeInfo parentCoordinator) {
		updateSupportPeer(skyNetNode.getSkyNetNodeInfo(), activeSupportPeer
				.getNodeInfo(), parentCoordinator, skyNetNode
				.getMessageCounter().assignmentOfMessageNumber(),
				new OperationCallback<Object>() {

					@Override
					public void calledOperationFailed(Operation<Object> op) {
						supportPeerUpdateOperationFailed();
					}

					@Override
					public void calledOperationSucceeded(Operation<Object> op) {
						supportPeerUpdateOperationSucceeded(op);
					}

				});
	}

	void supportPeerUpdateOperationFailed() {
		log.error(SkyNetUtilities.getTimeAndNetID(skyNetNode)
				+ "----NO CHANCE TO SEND SUPPORTPEERUPDATE TO "
				+ SkyNetUtilities.getNetID(activeSupportPeer) + "----");
	}

	void supportPeerUpdateOperationSucceeded(Operation<Object> op) {
		log.warn(SkyNetUtilities.getTimeAndNetID(skyNetNode)
				+ "SupportPeerUpdateOperation with id " + op.getOperationID()
				+ " succeeded!");
	}

}
