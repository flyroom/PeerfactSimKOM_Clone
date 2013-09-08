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

import java.math.BigDecimal;

import org.apache.log4j.Logger;
import org.peerfact.api.common.Operation;
import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.api.overlay.OverlayID;
import org.peerfact.api.service.skyeye.ISkyNetMonitor;
import org.peerfact.api.service.skyeye.SkyNetNodeInfo;
import org.peerfact.api.service.skyeye.SkyNetNodeInterface;
import org.peerfact.api.service.skyeye.overlay2SkyNet.TreeHandlerDelegator;
import org.peerfact.api.service.skyeye.overlay2SkyNet.util.LookupResult;
import org.peerfact.api.service.skyeye.overlay2SkyNet.util.ProcessNextLevelResult;
import org.peerfact.impl.overlay.AbstractOverlayNode;
import org.peerfact.impl.overlay.AbstractOverlayNode.PeerStatus;
import org.peerfact.impl.service.aggregation.skyeye.SkyNetID;
import org.peerfact.impl.service.aggregation.skyeye.SkyNetNodeInfoImpl;
import org.peerfact.impl.service.aggregation.skyeye.SkyNetPropertiesReader;
import org.peerfact.impl.service.aggregation.skyeye.SkyNetUtilities;
import org.peerfact.impl.service.aggregation.skyeye.analyzing.analyzers.ChurnStatisticsAnalyzer;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.transport.DefaultTransInfo;
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
 * This class implements the calculation of the position of a SkyNet-node in the
 * SkyNet-tree and retrieves the information of the determined
 * Parent-Coordinator.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 04.12.2008
 * 
 */
public class TreeHandler {

	private static Logger log = SimLogger.getLogger(TreeHandler.class);

	private boolean isRoot;

	private SkyNetNodeInfo parentCoordinator;

	private final SkyNetNodeInterface skyNetNode;

	private SkyNetID coordinatorKey;

	private int level;

	private final int branchingFactor;

	private final TreeHandlerDelegator treeHandlerDelegator;

	public TreeHandler(SkyNetNodeInterface node,
			TreeHandlerDelegator treeHandlerDelegator) {
		branchingFactor = SkyNetPropertiesReader.getInstance().getIntProperty(
				"SkyNetTreeBranchingFactor");

		this.skyNetNode = node;
		this.treeHandlerDelegator = treeHandlerDelegator;
		this.treeHandlerDelegator.setSkyNetNode(skyNetNode);
		this.treeHandlerDelegator
				.setOwnOverlayNode(skyNetNode.getOverlayNode());
		isRoot = false;
		parentCoordinator = new SkyNetNodeInfoImpl(null, null, null, -1);
		coordinatorKey = null;
		level = 0;
	}

	/**
	 * This method puts <code>TreeHandler</code> back in its initial state, if
	 * the host got off-line or if the peer left the underlying overlay.
	 */
	public void reset() {
		isRoot = false;
	}

	/**
	 * This method tests, if a SkyNet-node is currently the root of the
	 * SkyNet-tree.
	 * 
	 * @return <code>true</code>, if the node is the root, <code>false</code>
	 *         otherwise
	 */
	public boolean isRoot() {
		return isRoot;
	}

	public void verticalDispatching() {
		// for later versions
	}

	public void horizontalDispatching() {
		// for later versions
	}

	/**
	 * This method returns the information of the current Parent-Coordinator,
	 * which is required to address the Parent-Coordinator.
	 * 
	 * @return the <code>SkyNetNodeInfo</code> of the ParentCoordinator
	 */
	public SkyNetNodeInfo getParentCoordinator() {
		if (parentCoordinator != null) {
			if (parentCoordinator.isComplete()) {
				return parentCoordinator.clone();
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	// ----------------------------------------------------------------------
	// methods for traveling down the tree
	// ----------------------------------------------------------------------

	private void checkResponsibility(SkyNetID ownID, SkyNetID skyNetCoKey,
			BigDecimal left, BigDecimal right, int iter) {
		skyNetNode.getSkyNetNodeInfo().setCoordinatorKey(skyNetCoKey);
		log.info(" The client " + skyNetNode.getSkyNetNodeInfo().toString()
				+ " is responsible for the CoordinatorKey "
				+ skyNetCoKey.getPlainSkyNetID() + " in the interval["
				+ left.toPlainString() + ";" + right.toPlainString()
				+ "] @ level " + iter);

		ISkyNetMonitor monitor = (ISkyNetMonitor) Simulator.getMonitor();
		ChurnStatisticsAnalyzer csAnalyzer = (ChurnStatisticsAnalyzer) monitor
				.getConnectivityAnalyzer(ChurnStatisticsAnalyzer.class);

		if (iter > 0) {
			// complete the parentSkyNetNode Info
			if (isRoot) {
				log.warn(SkyNetUtilities.getTimeAndNetID(skyNetNode)
						+ "lost the root-position");
				csAnalyzer.lostRootPosition(skyNetNode);
				skyNetNode.getMetricUpdateStrategy().setLastMetricSync(0);
			}
			skyNetNode.getSkyNetNodeInfo().setLevel(iter);
			isRoot = false;
			lookupParentCoordinator();
		} else {
			if (!isRoot) {
				log.warn(SkyNetUtilities.getTimeAndNetID(skyNetNode)
						+ "got the root-position");
				csAnalyzer.gotRootPosition(skyNetNode);
				skyNetNode.getMetricUpdateStrategy().setLastMetricSync(0);
			}
			skyNetNode.getSkyNetNodeInfo().setLevel(iter);

			// Also set the observed level at this point
			skyNetNode.getSkyNetNodeInfo().setObservedLevelFromRoot(0);

			isRoot = true;
			skyNetNode.getAttributeUpdateStrategy()
					.resetOnlyAttributeUpdateStrategy();
			skyNetNode.getMetricUpdateStrategy().sendNextDataUpdate();
		}
	}

	private void calculateNextLevel(SkyNetID ownID, SkyNetID skyNetCoKey,
			BigDecimal left, BigDecimal right, int iter) {
		if (iter == 0) {
			coordinatorKey = skyNetCoKey;
			level = iter;
		} else {
			coordinatorKey = skyNetCoKey;
			level = iter;
		}
		if (iter < 100) {
			// call processNextLevel(...), but before split the interval
			// defined by left and right in k subintervals as defined by
			// branchingFactor.
			BigDecimal interval = right.subtract(left);
			BigDecimal intervalStep = interval.divide(new BigDecimal(String
					.valueOf(branchingFactor)));
			BigDecimal tempLeft = left;
			BigDecimal tempRight = left.add(new BigDecimal(String
					.valueOf(intervalStep)));
			log.info(SkyNetUtilities.getTimeAndNetID(skyNetNode)
					+ "is actually in the interval between "
					+ left.toPlainString() + " and " + right.toPlainString()
					+ ". The size of the interval is" + interval);
			int correctnessCounter = 0;
			while (ownID.getID().compareTo(tempRight) > -1) {
				tempLeft = tempRight;
				tempRight = tempRight.add(new BigDecimal(String
						.valueOf(intervalStep)));
				if (correctnessCounter < branchingFactor) {
					correctnessCounter++;
				} else {
					log.fatal("There are actually " + (correctnessCounter + 1)
							+ " intervals instead of " + branchingFactor);
				}
			}
			processNextLevel(ownID, tempLeft, tempRight, iter + 1);

		} else {
			skyNetNode.getMetricUpdateStrategy().scheduleNextUpdateEvent();
			parentCoordinator = null;
			if (isRoot) {
				log.warn(SkyNetUtilities.getTimeAndNetID(skyNetNode)
						+ "lost the root-position");
			}
			isRoot = false;
			log.error(SkyNetUtilities.getTimeAndNetID(skyNetNode)
					+ "is to deep in the tree."
					+ "Probably no predecessor in the overlay can be found"
					+ " to calculate the 'isRootOf'-method");
		}
	}

	// ----------------------------------------------------------------------
	// methods for checking the responsibility-interval on different overlays
	// ----------------------------------------------------------------------

	/**
	 * If it is possible to calculate the responsibility interval of a peer on
	 * the current overlay, this method calculates the corresponding
	 * responsibility interval of the node in the ID-space of SkyNet. In this
	 * context, the responsibility interval is used to test, if a SkyNet-node is
	 * responsible for a Coordinator.
	 * 
	 * @param id
	 *            contains the ID of this SkyNet-node
	 */
	public void calculateResponsibilityInterval(SkyNetID id) {
		if (((AbstractOverlayNode<?, ?>) skyNetNode.getOverlayNode())
				.getPeerStatus()
				.equals(PeerStatus.PRESENT)) {
			// execute getPredecessor-method
			final SkyNetID ownID = id;
			treeHandlerDelegator.calculateResponsibilityInterval(ownID,
					new OperationCallback<OverlayContact<OverlayID<?>>>() {

						@Override
						public void calledOperationFailed(
								Operation<OverlayContact<OverlayID<?>>> op) {
							calculateResponsibilityIntervalOperationFailed(op);
						}

						@Override
						public void calledOperationSucceeded(
								Operation<OverlayContact<OverlayID<?>>> op) {
							calculateResponsibilityIntervalOperationSucceeded(
									op, ownID);
						}
					});
		} else {
			log.warn("SkyNetNode cannot get Predecessor"
					+ ", because he is not PRESENT");
		}
	}

	void calculateResponsibilityIntervalOperationFailed(
			Operation<OverlayContact<OverlayID<?>>> op) {
		treeHandlerDelegator.calculateResponsibilityIntervalOperationFailed(op);
		skyNetNode.getMetricUpdateStrategy().scheduleNextUpdateEvent();
	}

	void calculateResponsibilityIntervalOperationSucceeded(
			Operation<OverlayContact<OverlayID<?>>> op, SkyNetID ownID) {
		treeHandlerDelegator
				.calculateResponsibilityIntervalOperationSucceeded(op);
		int iter = 0;
		BigDecimal left = new BigDecimal(0);
		BigDecimal right = new BigDecimal(1);
		level = 0;
		processNextLevel(ownID, left, right, iter);
	}

	// ----------------------------------------------------------------------
	// methods for traveling down the tree
	// ----------------------------------------------------------------------

	/**
	 * This method is responsible for the calculation of the position of the
	 * SkyNet-node in the SkyNet-tree. Additionally, it determines the key of
	 * the Parent-Coordinator, which can be looked up.<br>
	 * This method starts the recursive calculation, where first the
	 * determination of the Coordinator for every new interval is executed. Then
	 * it is checked, if the SkyNet-node is responsible for that Coordinator. In
	 * case of a positive outcome, the Parent-Coordinator is looked up,
	 * otherwise, the interval at the next level is calculated, to which the
	 * SkyNet-node must descend.
	 * 
	 * @param ownID
	 *            the ID of this SkyNet-node
	 * @param left
	 *            the left bound of the interval
	 * @param right
	 *            the right bound of the interval
	 * @param iter
	 *            the current level
	 */
	private void processNextLevel(SkyNetID ownID, BigDecimal left,
			BigDecimal right, int iter) {

		BigDecimal temp = left.add(right);
		temp = temp.divide(new BigDecimal(String.valueOf(2)));
		// BigDecimal temp = new BigDecimal((left + right) / 2);
		SkyNetID skyNetCoKey = new SkyNetID(temp);

		// call the process-method for the corresponding overlay
		treeHandlerDelegator.processNextLevel(ownID, skyNetCoKey, left, right,
				iter, new OperationCallback<Object>() {

					@Override
					public void calledOperationFailed(Operation<Object> op) {
						processNextLevelOperationFailed(op);
					}

					@Override
					public void calledOperationSucceeded(Operation<Object> op) {
						processNextLevelOperationSucceeded(op);
					}

				});
	}

	void processNextLevelOperationFailed(Operation<?> op) {
		treeHandlerDelegator.processNextLevelOperationFailed(op);
		skyNetNode.getMetricUpdateStrategy().scheduleNextUpdateEvent();
	}

	void processNextLevelOperationSucceeded(Operation<?> op) {
		ProcessNextLevelResult result = treeHandlerDelegator
				.processNextLevelOperationSucceeded(op);
		if (result.isKeyResponsibility()) {
			checkResponsibility(result.getOwnID(), result.getSkyNetCoKey(),
					result.getLeft(), result.getRight(), result.getIter());
		} else {
			calculateNextLevel(result.getOwnID(), result.getSkyNetCoKey(),
					result.getLeft(), result.getRight(), result.getIter());
		}
	}

	// ----------------------------------------------------------------------
	// methods for looking up the needed additional information of the
	// ParentCoordinator on different overlays
	// ----------------------------------------------------------------------

	private void lookupParentCoordinator() {
		if (((AbstractOverlayNode<?, ?>) skyNetNode.getOverlayNode())
				.getPeerStatus()
				.equals(PeerStatus.PRESENT)) {
			log.debug(SkyNetUtilities.getTimeAndNetID(skyNetNode)
					+ "starts a lookup for the key "
					+ coordinatorKey.getPlainSkyNetID());
			treeHandlerDelegator.lookupParentCoordinator(coordinatorKey,
					new OperationCallback<OverlayContact<OverlayID<?>>>() {

						@Override
						public void calledOperationFailed(
								Operation<OverlayContact<OverlayID<?>>> op) {
							lookupOperationFailed(op);
						}

						@Override
						public void calledOperationSucceeded(
								Operation<OverlayContact<OverlayID<?>>> op) {
							lookupOperationSucceeded(op);
						}

					});
		} else {
			log.warn("SkyNetNode cannot lookup ParentCoordinator"
					+ ", because he is not PRESENT");
		}
	}

	void lookupOperationFailed(Operation<OverlayContact<OverlayID<?>>> op) {
		treeHandlerDelegator.lookupOperationFailed(op);
		skyNetNode.getMetricUpdateStrategy().scheduleNextUpdateEvent();
	}

	void lookupOperationSucceeded(Operation<OverlayContact<OverlayID<?>>> op) {
		LookupResult result = treeHandlerDelegator.lookupOperationSucceeded(op);
		parentCoordinator = new SkyNetNodeInfoImpl(result.getSkyNetID(),
				coordinatorKey, DefaultTransInfo.getTransInfo(result
						.getContact().getTransInfo().getNetId(),
						skyNetNode.getPort()), level);
		log.info(parentCoordinator.toString() + " is parentCoordinator of "
				+ skyNetNode.getSkyNetNodeInfo().toString());

		skyNetNode.getMetricUpdateStrategy().sendNextDataUpdate();
	}

}
