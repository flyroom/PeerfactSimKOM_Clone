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
import org.peerfact.api.common.Operation;
import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.service.skyeye.SkyNetEventType;
import org.peerfact.api.service.skyeye.SkyNetNodeInfo;
import org.peerfact.api.service.skyeye.SkyNetNodeInterface;
import org.peerfact.api.service.skyeye.SupportPeer;
import org.peerfact.impl.overlay.AbstractOverlayNode;
import org.peerfact.impl.overlay.AbstractOverlayNode.PeerStatus;
import org.peerfact.impl.service.aggregation.skyeye.AbstractUpdateStrategy;
import org.peerfact.impl.service.aggregation.skyeye.SkyNetEventObject;
import org.peerfact.impl.service.aggregation.skyeye.SkyNetHostProperties;
import org.peerfact.impl.service.aggregation.skyeye.SkyNetPropertiesReader;
import org.peerfact.impl.service.aggregation.skyeye.SkyNetUtilities;
import org.peerfact.impl.service.aggregation.skyeye.SupportPeerInfo;
import org.peerfact.impl.service.aggregation.skyeye.analyzing.writers.AttributeWriter;
import org.peerfact.impl.service.aggregation.skyeye.attributes.messages.ParentCoordinatorInformationMsg;
import org.peerfact.impl.service.aggregation.skyeye.attributes.operations.AttributeUpdateOperation;
import org.peerfact.impl.service.aggregation.skyeye.components.SkyNetNode;
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
 * This class handles the outgoing <i>Attribute-Updates</i> as Coordinator to
 * the Parent-Coordinator. It implements the mechanisms, which are responsible
 * for determining the maximum amount of <code>AttributeEntry</code>s, which can
 * be sent. In addition, <code>AttributeUpdateStrategy</code> must process the
 * information from the Parent-Coordinator concerning the allowed amount of
 * entries and the provided Support Peer. The <code>AttributeEntry</code>s for
 * the transmission are taken from the <code>AttributeStorage</code>.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 15.11.2008
 * 
 */
public class AttributeUpdateStrategy extends
		AbstractUpdateStrategy<AttributeStorage> {

	private static Logger log = SimLogger
			.getLogger(AttributeUpdateStrategy.class);

	// references for own elements
	private SkyNetNodeInterface skyNetNode;

	private AttributeStorage attributeStorage;

	private long sendMsgIDForCo;

	private long sendMsgIDForSP;

	private TreeMap<BigDecimal, AttributeEntry> attributeEntriesForCo;

	private TreeMap<BigDecimal, AttributeEntry> attributeEntriesForSP;

	private int maxAttributeEntriesForCo;

	private int lowerBoundOfEntriesForCo;

	private int sendMax;

	private int requestedAttributeEntriesToSend;

	private int failedSendsToSp;

	private int maxFailedSendsToSp;

	private boolean downSupportPeer;

	private float removalPeriode;

	private boolean definedAmount;

	// The reference for the supportPeer, to whom attribute-updates may be sent,
	// if it is not null. Otherwise this peer sends the updates to its
	// parent-coordinator
	private SupportPeerInfo supportPeer;

	public AttributeUpdateStrategy(SkyNetNodeInterface skyNetNode,
			SupportPeer supportPeer) {
		super();
		// setting the needed values from the properties file
		SkyNetPropertiesReader propReader = SkyNetPropertiesReader
				.getInstance();
		updateInterval = propReader.getTimeProperty("AttributeUpdateTime");
		numberOfRetransmissions = propReader
				.getIntProperty("AttributeNumberOfRetransmissions");
		timeForAck = propReader.getTimeProperty("AttributeTimeForAck");
		lowerBoundOfEntriesForCo = propReader
				.getIntProperty("LowerBoundOfEntriesForCo");
		maxFailedSendsToSp = propReader.getIntProperty("MaxFailedSendsToSP");
		removalPeriode = propReader
				.getFloatProperty("AttributeRemoveStaleSubCo");

		this.skyNetNode = skyNetNode;
		attributeStorage = new AttributeStorage(skyNetNode, supportPeer);
		sendMsgIDForCo = 0;
		sendMsgIDForSP = 0;
		maxAttributeEntriesForCo = 0;
		sendMax = 0;
		requestedAttributeEntriesToSend = 1;
		downSupportPeer = false;
		failedSendsToSp = 0;
		definedAmount = false;
	}

	/**
	 * This method puts <code>AttributeUpdateStrategy</code> back in its initial
	 * state, if the host got off-line or if the peer left the underlying
	 * overlay.
	 */
	public void reset() {
		resetOnlyAttributeUpdateStrategy();
		attributeStorage.reset();
		failedSendsToSp = 0;
	}

	public void resetOnlyAttributeUpdateStrategy() {
		if (attributeEntriesForCo != null) {
			attributeEntriesForCo.clear();
		}
		if (attributeEntriesForSP != null) {
			attributeEntriesForSP = null;// = new TreeMap<BigDecimal,
			// AttributeEntry>();
		}
		if (supportPeer != null) {
			supportPeer = null;
		}
		definedAmount = false;
		maxAttributeEntriesForCo = 0;
		requestedAttributeEntriesToSend = 1;
		downSupportPeer = false;
	}

	/**
	 * This method is responsible for processing the data, which was sent by a
	 * <code>ParentCoordinatorInformationMsg</code>. The data comprises the
	 * possible introduction of a Support Peer and the amount of
	 * <code>AttributeEntry</code>s, which a Parent-Coordinator accepts at
	 * maximum.
	 * 
	 * @param request
	 *            contains the received
	 *            <code>ParentCoordinatorInformationMsg</code>
	 */
	public void processParentCoordinatorInfo(
			ParentCoordinatorInformationMsg request) {
		definedAmount = true;
		maxAttributeEntriesForCo = request.getMaxEntriesForCo();
		if (request.getSupportPeerInfo() != null) {
			// set new SupportPeer
			supportPeer = request.getSupportPeerInfo();
			log.debug(SkyNetUtilities.getTimeAndNetID(skyNetNode)
					+ "Setting new SupportPeer"
					+ SkyNetUtilities.getNetID(supportPeer) + ", who receives "
					+ supportPeer.getNumberOfUpdates() + " updates");
			failedSendsToSp = 0;
		}
	}

	@Override
	public void calculateUpdateInterval() {
		// TODO other implementation possible

	}

	@Override
	public void calculateNumberOfRetransmissions() {
		// TODO other implementation possible

	}

	@Override
	public void calculateReceiverForNextUpdate() {
		if (skyNetNode.getTreeHandler().isRoot()) {
			log.info("This the root of the SkyNet-Tree."
					+ " No need to send attributes");
		} else {
			if (checkObject(this.receiverOfNextUpdate)) {
				if (skyNetNode.getTreeHandler().getParentCoordinator() != null) {
					if (receiverOfNextUpdate.getSkyNetID().getID().compareTo(
							skyNetNode.getTreeHandler().getParentCoordinator()
									.getSkyNetID().getID()) != 0) {
						// Sending the new parentCoordinator to a SupportPeer-
						// if in use- so that it can update his current settings
						if (skyNetNode.getAttributeInputStrategy()
								.isSupportPeerInUse()) {
							skyNetNode.getAttributeInputStrategy()
									.getSpHandler()
									.updateCurrentSettingsOfSupportPeer(
											skyNetNode.getTreeHandler()
													.getParentCoordinator());
						}
						resetAttributeUpdateStrategy();
					}
				} else {
					if (skyNetNode.getAttributeInputStrategy()
							.isSupportPeerInUse()) {
						skyNetNode.getAttributeInputStrategy().getSpHandler()
								.updateCurrentSettingsOfSupportPeer(
										skyNetNode.getTreeHandler()
												.getParentCoordinator());
					}
					resetAttributeUpdateStrategy();
				}
			}
			if (skyNetNode.getTreeHandler().getParentCoordinator() != null) {
				this.receiverOfNextUpdate = skyNetNode.getTreeHandler()
						.getParentCoordinator();
			} else {
				this.receiverOfNextUpdate = null;
			}
		}
	}

	/**
	 * If a new ParentCoordinator was calculated, the reference of the current
	 * Support Peer is deleted, while the Coordinator must re-negotiate the
	 * amount of entries with the new ParentCoordinator. For this reason, the
	 * amount of entries, which are currently sent, is set back to the initial
	 * value.
	 */
	private void resetAttributeUpdateStrategy() {
		definedAmount = false;
		maxAttributeEntriesForCo = 0;
		supportPeer = null;

		// copy the elements from attributeEntriesForCo in a temporary TreeMap
		// to choose the best entries again
		int before = attributeEntriesForCo.size();
		Iterator<BigDecimal> iter = attributeEntriesForCo.keySet().iterator();
		TreeMap<Double, AttributeEntry> temp = new TreeMap<Double, AttributeEntry>();
		AttributeEntry entry = null;
		while (iter.hasNext()) {
			entry = attributeEntriesForCo.get(iter.next());
			temp.put(entry.getRank(), entry);
		}

		// choose the best entries and put them back in attributeEntriesForCo
		attributeEntriesForCo.clear();
		Iterator<Double> rankIter = temp.keySet().iterator();
		int i = 0;
		while (i < 2 * lowerBoundOfEntriesForCo && rankIter.hasNext()) {
			entry = temp.get(rankIter.next());
			attributeEntriesForCo.put(
					entry.getNodeInfo().getSkyNetID().getID(), entry);
			i++;
		}
		log.debug(SkyNetUtilities.getTimeAndNetID(skyNetNode)
				+ "resets actual attributeUpdateStrategy. Sending "
				+ attributeEntriesForCo.size() + " entries instead of "
				+ before);
	}

	@Override
	public void calculateTimeForACK(long time) {
		// TODO other implementation possible

	}

	@Override
	public AttributeStorage getStorage() {
		return attributeStorage;
	}

	@Override
	public void sendNextDataUpdate() {
		if (((AbstractOverlayNode<?, ?>) skyNetNode.getOverlayNode())
				.getPeerStatus()
				.equals(PeerStatus.PRESENT)) {
			if (skyNetNode.getTreeHandler().isRoot()) {
				interpretUpdate(attributeEntriesForCo);
			} else {
				if (supportPeer != null) {
					if (attributeEntriesForSP != null
							&& attributeEntriesForSP.size() > 0) {
						log.debug(SkyNetUtilities.getTimeAndNetID(skyNetNode)
								+ "sends to SupportPeer "
								+ SkyNetUtilities.getNetID(supportPeer));
						sendMsgIDForSP = skyNetNode.getMessageCounter()
								.assignmentOfMessageNumber();
						sendUpdateToSupportPeer();
					} else {
						log.debug(SkyNetUtilities.getTimeAndNetID(skyNetNode)
								+ "needs not to send to SupportPeer "
								+ SkyNetUtilities.getNetID(supportPeer));
					}
				}
				calculateReceiverForNextUpdate();
				if (receiverOfNextUpdate != null) {
					sendMsgIDForCo = skyNetNode.getMessageCounter()
							.assignmentOfMessageNumber();
					sendUpdateToCoordinator();
				} else {
					log.debug(SkyNetUtilities.getTimeAndNetID(skyNetNode)
							+ "cannot update attributes"
							+ ", since no parentCoordinator is known");
					scheduleNextUpdateEvent();
				}
			}
		} else {
			log.warn(SkyNetUtilities.getTimeAndNetID(skyNetNode)
					+ "cannot send AttributeUpdates"
					+ ", because he is not PRESENT");
		}
	}

	/**
	 * This method is only uses by the root of the tree, to write the actual
	 * data in files for later analysis.
	 * 
	 * @param attributeEntries
	 *            contains the actual amount of <code>AttributeEntry</code>s,
	 *            which are known by the root
	 */
	private void interpretUpdate(
			TreeMap<BigDecimal, AttributeEntry> attributeEntries) {
		AttributeWriter.getInstance().writeAttributeInfo(
				attributeEntries.size(), true);
		scheduleNextUpdateEvent();
	}

	/**
	 * If a Support Peer must be addressed with <i>attribute-updates</i>, this
	 * method starts the transmission of the data to the supporting node. The
	 * method is called out of <code>sendNextDataUpdate()</code>.
	 */
	private void sendUpdateToSupportPeer() {
		SkyNetNodeInfo senderInfo = skyNetNode.getSkyNetNodeInfo();
		SkyNetNodeInfo receiverInfo = supportPeer.getNodeInfo();
		supportPeer.decrementNumberOfUpdates();
		log.debug(SkyNetUtilities.getTimeAndNetID(skyNetNode)
				+ "can still send " + supportPeer.getNumberOfUpdates()
				+ " updates to SupportPeer "
				+ SkyNetUtilities.getNetID(supportPeer));
		int numberOfUpdates = supportPeer.getNumberOfUpdates();
		if (supportPeer.getNumberOfUpdates() == 0) {
			log.info(SkyNetUtilities.getTimeAndNetID(skyNetNode)
					+ "sends last update to SupportPeer "
					+ SkyNetUtilities.getNetID(supportPeer));
			supportPeer = null;
		}
		((SkyNetNode) skyNetNode).attributeUpdate(senderInfo, receiverInfo,
				attributeEntriesForSP, numberOfUpdates, -1, false,
				sendMsgIDForSP, true, false, new OperationCallback<Object>() {

					@Override
					public void calledOperationFailed(Operation<Object> op) {
						attributeUpdateForSPFailed(op);
					}

					@Override
					public void calledOperationSucceeded(Operation<Object> op) {
						attributeUpdateForSPSucceeded(op);
					}

				});
	}

	void attributeUpdateForSPFailed(Operation<Object> op) {
		log.error(SkyNetUtilities.getTimeAndNetID(skyNetNode)
				+ "----AS CO, NO CHANCE TO UPDATE ATTRIBUTES TO SP "
				+ SkyNetUtilities.getNetID(((AttributeUpdateOperation) op)
						.getReceiverInfo()) + "----");
		downSupportPeer = true;
		failedSendsToSp++;
		if (failedSendsToSp == maxFailedSendsToSp) {
			log.warn(SkyNetUtilities.getTimeAndNetID(skyNetNode)
					+ "deletes the SupportPeer-reference"
					+ ", because he cannot reach the SupportPeer"
					+ SkyNetUtilities.getNetID(supportPeer));
			supportPeer = null;
			attributeEntriesForSP = null;// = new TreeMap<BigDecimal,
			// AttributeEntry>();
		}
	}

	void attributeUpdateForSPSucceeded(Operation<Object> op) {
		log.debug("From Co: AttributeUpdateOperation for SupportPeer with id "
				+ op.getOperationID() + " succeeded");
		attributeEntriesForSP = null;// = new TreeMap<BigDecimal,
		// AttributeEntry>();
	}

	/**
	 * This method starts the transmission of the data within the
	 * <i>attribute-updates</i> to the ParentCoordinator. The method is called
	 * out of <code>sendNextDataUpdate()</code>.
	 */
	private void sendUpdateToCoordinator() {
		SkyNetNodeInfo receiverInfo = receiverOfNextUpdate;
		if (receiverInfo.getSkyNetID() != null) {
			SkyNetNodeInfo senderInfo = skyNetNode.getSkyNetNodeInfo();
			if (sendMax < requestedAttributeEntriesToSend) {
				log.debug(SkyNetUtilities.getTimeAndNetID(skyNetNode)
						+ "Would like to send "
						+ requestedAttributeEntriesToSend
						+ " but can only send " + sendMax + " due to own cap");
			}
			((SkyNetNode) skyNetNode).attributeUpdate(senderInfo, receiverInfo,
					attributeEntriesForCo, Integer.MAX_VALUE, Math.max(Math
							.min(sendMax, requestedAttributeEntriesToSend), 1),
					downSupportPeer, sendMsgIDForCo, false, false,
					new OperationCallback<Object>() {

						@Override
						public void calledOperationFailed(Operation<Object> op) {
							attributeUpdateForCoFailed(op);
						}

						@Override
						public void calledOperationSucceeded(
								Operation<Object> op) {
							attributeUpdateForCoSucceeded(op);
						}

					});
			downSupportPeer = false;
		} else {
			log.warn(SkyNetUtilities.getTimeAndNetID(skyNetNode)
					+ "No receiver for attributeUpdates");
			scheduleNextUpdateEvent();
		}
	}

	void attributeUpdateForCoFailed(Operation<Object> op) {
		log.info(SkyNetUtilities.getTimeAndNetID(skyNetNode)
				+ " ----AS CO, NO CHANCE TO UPDATE ATTRIBUTES TO CO "
				+ SkyNetUtilities.getNetID(receiverOfNextUpdate) + "----");

		// End of an iteration of the AttributeEvent. Calculating now when to
		// start it again
		scheduleNextUpdateEvent();
	}

	void attributeUpdateForCoSucceeded(Operation<Object> op) {
		log.debug("From Co: AttributeUpdateOperation for Co with id "
				+ op.getOperationID() + " succeeded");
		// End of an iteration of the AttributeEvent. Calculating now when to
		// start it again
		scheduleNextUpdateEvent();

	}

	/**
	 * This method returns the <code>SupportPeerInfo</code>-object, which
	 * contains the required information for sending an <i>attribute-update</i>
	 * to the Support Peer.
	 * 
	 * @return the <code>SupportPeerInfo</code>-object
	 */
	public SupportPeerInfo getAttributeReceiver() {
		return supportPeer;
	}

	/**
	 * This method sets the <code>SupportPeerInfo</code>-object, which contains
	 * the required information for sending an <i>attribute-update</i> to the
	 * new Support Peer.
	 * 
	 * @param attributeReceiver
	 *            contains the <code>SupportPeerInfo</code>-object
	 */
	public void setAttributeReceiver(SupportPeerInfo attributeReceiver) {
		this.supportPeer = attributeReceiver;
	}

	@Override
	public void scheduleNextUpdateEvent() {
		long updateTime = 0;
		long actualTime = Simulator.getCurrentTime();
		long diff = actualTime - sendingTime;
		if (diff < getUpdateInterval()) {
			long nextUpdateTime = getUpdateInterval() - diff;
			long time = Simulator.getCurrentTime();
			updateTime = time + nextUpdateTime;
		} else {
			log.warn(SkyNetUtilities.getTimeAndNetID(skyNetNode)
					+ " AttributeUpdate longer than one interval");
			long rest = diff % getUpdateInterval();
			long nextUpdateTime = getUpdateInterval() - rest;
			long time = Simulator.getCurrentTime();
			updateTime = time + nextUpdateTime;

		}
		Simulator.scheduleEvent(new SkyNetEventObject(
				SkyNetEventType.ATTRIBUTE_UPDATE, actualTime), updateTime,
				skyNetNode, null);

		long parentCoordinatorUpdate = updateTime
				- (long) Math.floor(getUpdateInterval() / 4);

		Simulator.scheduleEvent(new SkyNetEventObject(
				SkyNetEventType.PARENT_COORDINATOR_INFORMATION_UPDATE,
				actualTime), parentCoordinatorUpdate, skyNetNode, null);
	}

	// ----------------------------------------------------------------------
	// Methods for determining the data, which will be send to the
	// ParentCoordinator and to the SupportPeer if it exists
	// ----------------------------------------------------------------------

	@Override
	public void setDataToSend() {
		// create TreeMap for the data to send
		TreeMap<BigDecimal, AttributeEntry> tempMap = new TreeMap<BigDecimal, AttributeEntry>();
		if (skyNetNode.getTreeHandler().isRoot()) {
			/*
			 * LinkedHashMap<BigDecimal, AttributeSubCoordinatorInfo> subCoList
			 * = attributeStorage .getListOfSubCoordinatorsOfCo();
			 * Iterator<BigDecimal> subCoIter = subCoList.keySet().iterator();
			 * AttributeSubCoordinatorInfo subCo = null; TreeMap<BigDecimal,
			 * AttributeEntry> entries = null; Iterator<BigDecimal> entryIter =
			 * null; AttributeEntry entry = null;
			 * log.fatal(SkyNetUtilities.getTimeAndNetID(skyNetNode) + "is the
			 * root and prepares " +
			 * attributeStorage.getActualAmountOfEntriesOfCo() + " entries");
			 * while (subCoIter.hasNext()) { subCo =
			 * subCoList.get(subCoIter.next()); log.fatal("\t" +
			 * SkyNetUtilities.getNetID(subCo) + "provided " +
			 * subCo.getData().size() + " entries"); entries = subCo.getData();
			 * entryIter = entries.keySet().iterator(); while
			 * (entryIter.hasNext()) { entry = entries.get(entryIter.next());
			 * log.fatal("\t\t" + SkyNetUtilities.getNetID(entry.getNodeInfo())
			 * + "with rank " + entry.getRank()); } }
			 */
			tempMap.putAll(attributeStorage.getSpecifiedNumberOfEntriesOfCo(0,
					attributeStorage.getActualAmountOfEntriesOfCo()));
			// put own attributes to the treemap
			tempMap
					.put(attributeStorage.getOwnAttributes().getNodeInfo()
							.getSkyNetID().getID(), attributeStorage
							.getOwnAttributes());
			attributeEntriesForCo = tempMap;
		} else {
			// calculate max amount to send
			int defSend = 0;
			int sendMaxAmount = ((SkyNetHostProperties) skyNetNode.getHost()
					.getProperties()).getSendMaxCo();
			int actualAttributeEntries = attributeStorage
					.getActualAmountOfEntriesOfCo() + 1;

			// --------------------------------------
			// look in attributeStorage for the data
			// --------------------------------------

			// check first, if the amount, which can be sent is defined by
			// the
			// parentCoordinator
			if (definedAmount) {
				if (Math.min(sendMaxAmount, actualAttributeEntries) > maxAttributeEntriesForCo) {
					defSend = maxAttributeEntriesForCo - 1;
				} else {
					defSend = Math.min(sendMaxAmount, actualAttributeEntries) - 1;
				}
			} else {
				defSend = (2 * lowerBoundOfEntriesForCo) - 1;
			}
			if (defSend > 0) {
				tempMap.putAll(attributeStorage
						.getSpecifiedNumberOfEntriesOfCo(0, defSend));
			}

			// put own attributes to the treemap
			tempMap
					.put(attributeStorage.getOwnAttributes().getNodeInfo()
							.getSkyNetID().getID(), attributeStorage
							.getOwnAttributes());

			// save the calculated values
			attributeEntriesForCo = tempMap;

			this.sendMax = sendMaxAmount;
			int offset = 0;
			this.requestedAttributeEntriesToSend = Math.min(sendMaxAmount,
					actualAttributeEntries);
			offset = tempMap.size() - 1;

			// if SupportPeer exists for this SubCo and there is still some data
			// to send, then calculate the amount of entries for the SupportPeer
			if (supportPeer != null
					&& Math.min(sendMaxAmount, actualAttributeEntries) > offset) {
				setAttributesToSendForSP(Math.min(sendMaxAmount,
						actualAttributeEntries)
						- (offset + 1), offset);
			}
		}
	}

	private void setAttributesToSendForSP(int numberOfEntries, int offset) {
		int defSend = 0;
		if (numberOfEntries > supportPeer.getTThreshold()) {
			defSend = supportPeer.getTThreshold();
		} else {
			defSend = numberOfEntries;
		}
		attributeEntriesForSP = attributeStorage
				.getSpecifiedNumberOfEntriesOfCo(offset, defSend);
	}

	// ----------------------------------------------------------------------
	// Methods for removing stale SubCoordinators including the data in
	// AttributeStorage, which was delivered by this stale coordinators
	// ----------------------------------------------------------------------
	public void removeStaleSubCoordinatorsOLD() {
		boolean delete = false;
		boolean cycle = true;
		long actualTime = Simulator.getCurrentTime();
		LinkedHashMap<BigDecimal, AttributeSubCoordinatorInfo> list = attributeStorage
				.getListOfSubCoordinatorsOfCo();

		Iterator<BigDecimal> iter = null;
		BigDecimal key = null;
		AttributeSubCoordinatorInfo subCoInfo = null;
		long periode = -1;
		long sub = -1;
		long treshold = -1;
		while (cycle) {
			iter = list.keySet().iterator();
			delete = false;
			while (!delete) {
				if (iter.hasNext()) {
					key = iter.next();
					subCoInfo = list.get(key);
					periode = subCoInfo.getUpdatePeriode();
					sub = actualTime - subCoInfo.getTimestampOfUpdate();
					treshold = (long) (removalPeriode * periode);
					if (treshold < sub) {
						log.debug(SkyNetUtilities.getTimeAndNetID(skyNetNode)
								+ "Size of attributeSubCo-list before = "
								+ list.size());
						list.remove(key);
						skyNetNode.getAttributeInputStrategy()
								.setMoreEntriesRequested(true);
						delete = true;
						log.debug(SkyNetUtilities.getTimeAndNetID(skyNetNode)
								+ "Size of attributeSubCo-list after deleting "
								+ key + " = " + list.size());
					}
				} else {
					delete = true;
					cycle = false;
				}
			}
		}
	}

	@Override
	public void removeStaleSubCoordinators() {
		long actualTime = Simulator.getCurrentTime();
		LinkedHashMap<BigDecimal, AttributeSubCoordinatorInfo> list = attributeStorage
				.getListOfSubCoordinatorsOfCo();

		Iterator<BigDecimal> iter = list.keySet().iterator();
		BigDecimal key = null;
		AttributeSubCoordinatorInfo subCoInfo = null;
		long periode = -1;
		long sub = -1;
		long treshold = -1;

		while (iter.hasNext()) {
			key = iter.next();
			subCoInfo = list.get(key);
			periode = subCoInfo.getUpdatePeriode();
			sub = actualTime - subCoInfo.getTimestampOfUpdate();
			treshold = (long) (removalPeriode * periode);
			if (treshold < sub) {
				log
						.debug(SkyNetUtilities.getTimeAndNetID(skyNetNode)
								+ "Size of attributeSubCo-list before = "
								+ list.size());
				iter.remove();
				skyNetNode.getAttributeInputStrategy().setMoreEntriesRequested(
						true);
				log.debug(SkyNetUtilities.getTimeAndNetID(skyNetNode)
						+ "Size of attributeSubCo-list after deleting " + key
						+ " = " + list.size());
			}
		}
	}

	private static boolean checkObject(SkyNetNodeInfo parentCo) {
		if (parentCo != null) {
			if (parentCo.getSkyNetID() != null
					&& parentCo.getTransInfo() != null) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

}
