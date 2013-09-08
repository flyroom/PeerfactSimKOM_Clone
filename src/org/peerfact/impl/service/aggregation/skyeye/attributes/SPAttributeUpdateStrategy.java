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
import org.peerfact.api.service.skyeye.SupportPeer;
import org.peerfact.impl.overlay.AbstractOverlayNode;
import org.peerfact.impl.overlay.AbstractOverlayNode.PeerStatus;
import org.peerfact.impl.service.aggregation.skyeye.AbstractUpdateStrategy;
import org.peerfact.impl.service.aggregation.skyeye.SkyNetEventObject;
import org.peerfact.impl.service.aggregation.skyeye.SkyNetHostProperties;
import org.peerfact.impl.service.aggregation.skyeye.SkyNetNodeInfoImpl;
import org.peerfact.impl.service.aggregation.skyeye.SkyNetPropertiesReader;
import org.peerfact.impl.service.aggregation.skyeye.SkyNetUtilities;
import org.peerfact.impl.service.aggregation.skyeye.SupportPeerInfo;
import org.peerfact.impl.service.aggregation.skyeye.analyzing.writers.AttributeWriter;
import org.peerfact.impl.service.aggregation.skyeye.attributes.messages.ParentCoordinatorInformationMsg;
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
 * This class handles the outgoing <i>Attribute-Updates</i> as Support Peer to
 * the Parent-Coordinator. It implements the mechanisms, which are responsible
 * for determining the maximum amount of <code>AttributeEntry</code>s, which can
 * be sent. In addition, <code>AttributeUpdateStrategy</code> must process the
 * information from the Parent-Coordinator concerning the allowed amount of
 * entries and the provided Support Peer. The <code>AttributeEntry</code>s for
 * the transmission are taken from the <code>AttributeStorage</code>. Since the
 * methods of this class have the same names as in
 * <code>AttributeUpdateStrategy</code> and contain nearly the same
 * functionality, please refer to {@link AttributeUpdateStrategy}.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 15.11.2008
 * 
 */
public class SPAttributeUpdateStrategy extends
		AbstractUpdateStrategy<AttributeStorage> {

	private static Logger log = SimLogger
			.getLogger(SPAttributeUpdateStrategy.class);

	// references for own elements
	private final SupportPeer supportPeer;

	private boolean processSupportPeerEvents;

	private boolean listEmpty;

	private boolean downSupportPeer;

	private SkyNetNodeInfo brotherCoordinator;

	private SkyNetNodeInfo parentCoordinator;

	private final AttributeStorage attributeStorage;

	private long sendMsgIDForCo;

	private long sendMsgIDForSP;

	private TreeMap<BigDecimal, AttributeEntry> attributeEntriesForCo;

	private TreeMap<BigDecimal, AttributeEntry> attributeEntriesForSP;

	private int maxAttributeEntriesForCo;

	private final int lowerBoundOfEntriesForCo;

	private int sendMax;

	private int requestedAttributeEntriesToSend;

	private int failedSendsToSp;

	private final int maxFailedSendsToSp;

	private final float removalPeriode;

	private boolean definedAmount;

	// The reference for the supportPeer, to whom attribute-updates may be sent,
	// if it is not null. Otherwise this peer sends the updates only to its
	// parent-coordinator
	private SupportPeerInfo foreignSupportPeer;

	public SPAttributeUpdateStrategy(SupportPeer supportPeer,
			AttributeStorage attributeStorage) {
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
				.getFloatProperty("SPAttributeRemoveStaleSubCo");

		this.supportPeer = supportPeer;
		this.attributeStorage = attributeStorage;
		sendMsgIDForCo = 0;
		sendMsgIDForSP = 0;
		maxAttributeEntriesForCo = 0;
		sendMax = 0;
		requestedAttributeEntriesToSend = 0;
		listEmpty = false;
		downSupportPeer = false;
		failedSendsToSp = 0;
		definedAmount = false;
	}

	public void reset() {
		if (attributeEntriesForCo != null) {
			attributeEntriesForCo.clear();
		}
		if (attributeEntriesForSP != null) {
			attributeEntriesForSP = null;// = new TreeMap<BigDecimal,
			// AttributeEntry>();
		}
		if (foreignSupportPeer != null) {
			foreignSupportPeer = null;
		}
		maxAttributeEntriesForCo = 0;
		requestedAttributeEntriesToSend = 0;
		listEmpty = false;
		downSupportPeer = false;
		definedAmount = false;
		failedSendsToSp = 0;
		if (supportPeer.isSupportPeer()) {
			supportPeer.setSupportPeer(false);
		}
	}

	public void processParentCoordinatorInfo(
			ParentCoordinatorInformationMsg request) {
		if (request.getMaxEntriesForCo() > lowerBoundOfEntriesForCo) {
			maxAttributeEntriesForCo = request.getMaxEntriesForCo();
		}
		if (request.getSupportPeerInfo() != null) {
			// set new SupportPeer
			foreignSupportPeer = request.getSupportPeerInfo();
			log.debug(SkyNetUtilities.getTimeAndNetID(supportPeer)
					+ "Sets new SupportPeer"
					+ SkyNetUtilities.getNetID(foreignSupportPeer)
					+ ", who receives "
					+ foreignSupportPeer.getNumberOfUpdates() + " updates");
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
		// not needed
	}

	private void resetAttributeUpdateStrategy() {
		definedAmount = false;
		maxAttributeEntriesForCo = 0;
		foreignSupportPeer = null;

		if (attributeEntriesForCo == null) {
			attributeEntriesForCo = new TreeMap<BigDecimal, AttributeEntry>();
		}

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
		log.debug(SkyNetUtilities.getTimeAndNetID(supportPeer)
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
		if (((AbstractOverlayNode<?, ?>) supportPeer.getOverlayNode())
				.getPeerStatus().equals(PeerStatus.PRESENT)) {
			if (brotherCoordinator.getLevel() == 0) {
				interpretUpdate(attributeEntriesForCo);
			} else {
				if (foreignSupportPeer != null) {
					if (attributeEntriesForSP != null
							&& attributeEntriesForSP.size() > 0) {
						log.debug(SkyNetUtilities.getTimeAndNetID(supportPeer)
								+ "sends as SP his updates to the SP "
								+ SkyNetUtilities.getNetID(foreignSupportPeer));
						sendMsgIDForSP = supportPeer.getMessageCounter()
								.assignmentOfMessageNumber();
						sendUpdateToSupportPeer();
					} else {
						log.debug(SkyNetUtilities.getTimeAndNetID(supportPeer)
								+ " needs not to send as SP to SP "
								+ SkyNetUtilities.getNetID(foreignSupportPeer));
					}
				}
				sendMsgIDForCo = supportPeer.getMessageCounter()
						.assignmentOfMessageNumber();
				sendUpdateToCoordinator();
			}
		} else {
			log.warn(SkyNetUtilities.getTimeAndNetID(supportPeer)
					+ " SupportPeer cannot send AttributeUpdates"
					+ ", because he is not PRESENT");
		}
	}

	private void interpretUpdate(
			TreeMap<BigDecimal, AttributeEntry> attributeEntries) {
		AttributeWriter.getInstance().writeAttributeInfo(
				attributeEntries.size(), false);
		scheduleNextUpdateEvent();
	}

	private void sendUpdateToSupportPeer() {
		SkyNetNodeInfo senderInfo = supportPeer.getSkyNetNodeInfo();
		SkyNetNodeInfo receiverInfo = foreignSupportPeer.getNodeInfo();
		foreignSupportPeer.decrementNumberOfUpdates();
		int numberOfUpdates = foreignSupportPeer.getNumberOfUpdates();
		if (foreignSupportPeer.getNumberOfUpdates() == 0) {
			log.warn(SkyNetUtilities.getTimeAndNetID(supportPeer)
					+ "sends last update to SupportPeer "
					+ SkyNetUtilities.getNetID(foreignSupportPeer));
			foreignSupportPeer = null;
		}
		((SkyNetNode) supportPeer).attributeUpdate(senderInfo, receiverInfo,
				attributeEntriesForSP, numberOfUpdates, -1, false,
				sendMsgIDForSP, true, true, new OperationCallback<Object>() {

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
		log.error(SkyNetUtilities.getTimeAndNetID(supportPeer)
				+ "----AS SP, NO CHANCE TO UPDATE ATTRIBUTES TO SP "
				+ SkyNetUtilities.getNetID(foreignSupportPeer) + "----");
		downSupportPeer = true;
		failedSendsToSp++;
		if (failedSendsToSp == maxFailedSendsToSp) {
			log.warn(SkyNetUtilities.getTimeAndNetID(supportPeer)
					+ "deletes the SupportPeer-reference"
					+ ", because he cannot reach the SupportPeer "
					+ SkyNetUtilities.getNetID(foreignSupportPeer));
			foreignSupportPeer = null;
			attributeEntriesForSP = null;// = new TreeMap<BigDecimal,
			// AttributeEntry>();
		}
	}

	void attributeUpdateForSPSucceeded(Operation<Object> op) {
		log.debug("From SP: AttributeUpdateOperation for SupportPeer with id "
				+ op.getOperationID() + " succeeded");
		attributeEntriesForSP = null;// = new TreeMap<BigDecimal,
		// AttributeEntry>();
	}

	private void sendUpdateToCoordinator() {
		SkyNetNodeInfo receiverInfo = parentCoordinator;
		if (receiverInfo != null) {
			if (receiverInfo.getSkyNetID() != null) {
				SkyNetNodeInfo senderInfo = supportPeer.getSkyNetNodeInfo();
				log.debug(SkyNetUtilities.getTimeAndNetID(supportPeer)
						+ "sends as SupportPeer "
						+ attributeEntriesForCo.size()
						+ " entries to the ParentCoordinator "
						+ SkyNetUtilities.getNetID(receiverInfo));

				((SkyNetNode) supportPeer).attributeUpdate(senderInfo,
						receiverInfo, attributeEntriesForCo, Integer.MAX_VALUE,
						Math.min(sendMax, requestedAttributeEntriesToSend),
						downSupportPeer, sendMsgIDForCo, false, true,
						new OperationCallback<Object>() {

							@Override
							public void calledOperationFailed(
									Operation<Object> op) {
								attributeUpdateForCoFailed(op);
							}

							@Override
							public void calledOperationSucceeded(
									Operation<Object> op) {
								attributeUpdateForCoSucceeded(op);
							}

						});
				downSupportPeer = false;
			}
		} else {
			scheduleNextUpdateEvent();
		}
	}

	void attributeUpdateForCoFailed(Operation<Object> op) {
		log.error(SkyNetUtilities.getTimeAndNetID(supportPeer)
				+ " ----AS SP, NO CHANCE TO UPDATE ATTRIBUTES TO CO "
				+ SkyNetUtilities.getNetID(parentCoordinator) + "----");

		// End of an iteration of the AttributeEvent. Calculating now when to
		// start it again
		scheduleNextUpdateEvent();
	}

	void attributeUpdateForCoSucceeded(Operation<Object> op) {
		log.debug("From SP: AttributeUpdateOperation for Co with id "
				+ op.getOperationID() + " succeeded");
		// End of an iteration of the AttributeEvent. Calculating now when to
		// start it again
		scheduleNextUpdateEvent();

	}

	public SupportPeerInfo getAttributeReceiver() {
		return foreignSupportPeer;
	}

	public void setAttributeReceiver(SupportPeerInfo foreignSupportPeer) {
		this.foreignSupportPeer = foreignSupportPeer;
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
			log.warn(SkyNetUtilities.getTimeAndNetID(supportPeer)
					+ "has AttributeUpdate longer than one interval");
			long rest = diff % getUpdateInterval();
			long nextUpdateTime = getUpdateInterval() - rest;
			long time = Simulator.getCurrentTime();
			updateTime = time + nextUpdateTime;

		}
		Simulator.scheduleEvent(new SkyNetEventObject(
				SkyNetEventType.SUPPORT_PEER_UPDATE, actualTime), updateTime,
				supportPeer, null);
	}

	// ----------------------------------------------------------------------
	// Methods for determining the data, which will be send to the
	// ParentCoordinator and to the SupportPeer if it exists
	// ----------------------------------------------------------------------

	@Override
	public void setDataToSend() {
		if (brotherCoordinator.getLevel() == 0) {
			/*
			 * LinkedHashMap<BigDecimal, AttributeSubCoordinatorInfo> subCoList
			 * = attributeStorage .getListOfSubCoordinatorsOfSP();
			 * Iterator<BigDecimal> subCoIter = subCoList.keySet().iterator();
			 * AttributeSubCoordinatorInfo subCo = null; TreeMap<BigDecimal,
			 * AttributeEntry> entries = null; Iterator<BigDecimal> entryIter =
			 * null; AttributeEntry entry = null;
			 * log.fatal(SkyNetUtilities.getTimeAndNetID(supportPeer) +
			 * "is the SP of " + SkyNetUtilities.getNetID(brotherCoordinator) +
			 * " and prepares " +
			 * attributeStorage.getActualAmountOfEntriesOfSP() + " entries");
			 * while (subCoIter.hasNext()) { subCo =
			 * subCoList.get(subCoIter.next()); log.fatal("\t" +
			 * SkyNetUtilities.getNetID(subCo) + "provided " +
			 * subCo.getData().size() + " entries"); entries = subCo.getData();
			 * entryIter = entries.keySet().iterator(); while
			 * (entryIter.hasNext()) { entry = entries.get(entryIter.next());
			 * log.fatal("\t\t" + SkyNetUtilities.getNetID(entry.getNodeInfo())
			 * + "with rank " + entry.getRank()); }
			 * 
			 * }
			 */
			attributeEntriesForCo = attributeStorage
					.getSpecifiedNumberOfEntriesOfSP(0, attributeStorage
							.getActualAmountOfEntriesOfSP());
		} else {
			// create TreeMap for the data to send and first of all put own
			// attributes in it
			TreeMap<BigDecimal, AttributeEntry> tempMap = new TreeMap<BigDecimal, AttributeEntry>();
			// calculate max amount to send
			int defSend = 0;
			int sendMaxAmount = ((SkyNetHostProperties) supportPeer.getHost()
					.getProperties()).getSendMaxSP();
			int actualAttributeEntries = attributeStorage
					.getActualAmountOfEntriesOfSP();

			// --------------------------------------
			// look in attributeStorage for the data
			// --------------------------------------

			// check first, if the amount, which can be sent is defined by the
			// parentCoordinator
			if (definedAmount) {
				if (Math.min(sendMaxAmount, actualAttributeEntries) > maxAttributeEntriesForCo) {
					defSend = maxAttributeEntriesForCo;
				} else {
					defSend = Math.min(sendMaxAmount, actualAttributeEntries);
				}
			} else {
				defSend = (2 * lowerBoundOfEntriesForCo);
			}

			if (defSend > 0) {
				tempMap.putAll(attributeStorage
						.getSpecifiedNumberOfEntriesOfSP(0, defSend));
			}

			// save the calculated values
			attributeEntriesForCo = tempMap;
			this.sendMax = sendMaxAmount;
			int offset = 0;
			this.requestedAttributeEntriesToSend = Math.min(sendMaxAmount,
					actualAttributeEntries);
			offset = tempMap.size();

			// if SupportPeer exists for this SupportPeer and there is still
			// some data to send, then calculate the amount of entries for the
			// SupportPeer
			if (foreignSupportPeer != null
					&& Math.min(sendMaxAmount, actualAttributeEntries) > offset) {
				setAttributesToSendForSP(Math.min(sendMaxAmount,
						actualAttributeEntries)
						- offset, offset);
			}
		}
	}

	private void setAttributesToSendForSP(int numberOfEntries, int offset) {
		int defSend = 0;
		if (numberOfEntries > foreignSupportPeer.getTThreshold()) {
			defSend = defSend + foreignSupportPeer.getTThreshold();
		} else {
			defSend = defSend + numberOfEntries;
		}

		attributeEntriesForSP = attributeStorage
				.getSpecifiedNumberOfEntriesOfSP(offset, defSend);
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
				.getListOfSubCoordinatorsOfSP();
		log.debug("Size of attributeSubCo-list before = " + list.size());

		// check if some SubCo are sending anyway, otherwise wait a period and
		// then shut down the SupportPeer
		if (list.size() == 0) {
			if (!listEmpty) {
				listEmpty = true;
			} else {
				processSupportPeerEvents = false;
				supportPeer.setSupportPeer(false);
				listEmpty = false;
				log.warn(SkyNetUtilities.getTimeAndNetID(supportPeer)
						+ "is no longer SupportPeer"
						+ ", since no SubCo sent some messages");
			}
			// the list contains some SubCos, so check if they refresh their
			// updates, otherwise shut down the SupportPeer
		} else {
			listEmpty = false;

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
							list.remove(key);
							delete = true;
							if (list.size() > 0) {
								log
										.debug(SkyNetUtilities
												.getTimeAndNetID(supportPeer)
												+ " Size of attributeSubCo-list after deleting "
												+ key + " = " + list.size());
							} else {
								processSupportPeerEvents = false;
								supportPeer.setSupportPeer(false);
								log
										.warn(SkyNetUtilities
												.getTimeAndNetID(supportPeer)
												+ " is not needed any longer as SupportPeer");
							}
						}
					} else {
						delete = true;
						cycle = false;
					}
				}
			}
		}
	}

	@Override
	public void removeStaleSubCoordinators() {
		long actualTime = Simulator.getCurrentTime();
		LinkedHashMap<BigDecimal, AttributeSubCoordinatorInfo> list = attributeStorage
				.getListOfSubCoordinatorsOfSP();
		log.debug("Size of attributeSubCo-list before = " + list.size());

		// check if some SubCo are sending anyway, otherwise wait a period and
		// then shut down the SupportPeer
		if (list.size() == 0) {
			if (!listEmpty) {
				listEmpty = true;
			} else {
				processSupportPeerEvents = false;
				supportPeer.setSupportPeer(false);
				listEmpty = false;
				log.warn(SkyNetUtilities.getTimeAndNetID(supportPeer)
						+ "is no longer SupportPeer"
						+ ", since no SubCo sent some messages");
			}
			// the list contains some SubCos, so check if they refresh their
			// updates, otherwise shut down the SupportPeer
		} else {
			listEmpty = false;
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
					iter.remove();
					if (list.size() > 0) {
						log
								.debug(SkyNetUtilities
										.getTimeAndNetID(supportPeer)
										+ " Size of attributeSubCo-list after deleting "
										+ key + " = " + list.size());
					} else {
						processSupportPeerEvents = false;
						supportPeer.setSupportPeer(false);
						log.warn(SkyNetUtilities.getTimeAndNetID(supportPeer)
								+ " is not needed any longer as SupportPeer");
					}
				}
			}
		}
	}

	/**
	 * This method returns the ID of the Coordinator, which is supported by this
	 * Support Peer.
	 * 
	 * @return the ID of the supported Coordinator
	 */
	public SkyNetNodeInfo getBrotherCoordinator() {
		return brotherCoordinator;
	}

	/**
	 * This method sets the ID of the Coordinator, which is supported by this
	 * Support Peer.
	 * 
	 * @param brotherCoordinator
	 *            contains the ID of the supported Coordinator
	 */
	public void setBrotherCoordinator(SkyNetNodeInfo brotherCoordinator) {
		this.brotherCoordinator = new SkyNetNodeInfoImpl(brotherCoordinator
				.getSkyNetID(), brotherCoordinator.getCoordinatorKey(),
				brotherCoordinator.getTransInfo(), brotherCoordinator
						.getLevel());
	}

	/**
	 * This method returns the ID of the Coordinator's Parent-Coordinator, to
	 * which this Support Peer must send its updates.
	 * 
	 * @return the ID of the Coordinator's Parent-Coordinator
	 */
	public SkyNetNodeInfo getParentCoordinator() {
		return parentCoordinator;
	}

	/**
	 * This method sets the ID of the Coordinator's Parent-Coordinator, to which
	 * this Support Peer must send its updates.
	 * 
	 * @param parentCoordinator
	 *            contains the ID of the Coordinator's Parent-Coordinator
	 */
	public void setParentCoordinator(SkyNetNodeInfo parentCoordinator) {
		if (brotherCoordinator.getLevel() > 0) {
			if (parentCoordinator != null) {
				if (checkObject(this.parentCoordinator)) {
					if (this.parentCoordinator.getSkyNetID().getID().compareTo(
							parentCoordinator.getSkyNetID().getID()) != 0) {
						resetAttributeUpdateStrategy();
					}
				}
				this.parentCoordinator = new SkyNetNodeInfoImpl(
						parentCoordinator.getSkyNetID(), parentCoordinator
								.getCoordinatorKey(), parentCoordinator
								.getTransInfo(), parentCoordinator.getLevel());
			} else {
				log.warn(SkyNetUtilities.getTimeAndNetID(supportPeer)
						+ "No receiver for attributeUpdates");
				this.parentCoordinator = null;
			}
		} else {
			this.parentCoordinator = null;
			log.warn(SkyNetUtilities.getTimeAndNetID(supportPeer)
					+ " no need for ParentCoordinator"
					+ ", since this SupportPeer helps the root");
		}
	}

	public boolean isProcessSupportPeerEvents() {
		return processSupportPeerEvents;
	}

	public void setProcessSupportPeerEvents(boolean processSupportPeerEvents) {
		this.processSupportPeerEvents = processSupportPeerEvents;
	}

	public static boolean checkObject(SkyNetNodeInfo parentCo) {
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
