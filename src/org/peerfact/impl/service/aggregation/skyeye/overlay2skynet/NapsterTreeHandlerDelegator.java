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

package org.peerfact.impl.service.aggregation.skyeye.overlay2skynet;

import java.math.BigDecimal;
import java.util.List;

import org.apache.log4j.Logger;
import org.peerfact.api.common.Operation;
import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.api.overlay.OverlayNode;
import org.peerfact.api.service.skyeye.SkyNetConstants;
import org.peerfact.api.service.skyeye.SkyNetNodeInterface;
import org.peerfact.api.service.skyeye.overlay2SkyNet.TreeHandlerDelegator;
import org.peerfact.api.service.skyeye.overlay2SkyNet.util.LookupResult;
import org.peerfact.api.service.skyeye.overlay2SkyNet.util.ProcessNextLevelResult;
import org.peerfact.impl.overlay.dht.centralizedindex.components.CIClientNode;
import org.peerfact.impl.overlay.dht.centralizedindex.components.CIOverlayContact;
import org.peerfact.impl.overlay.dht.centralizedindex.components.CIOverlayID;
import org.peerfact.impl.overlay.dht.centralizedindex.operations.GetPredecessorOperation;
import org.peerfact.impl.service.aggregation.skyeye.SkyNetID;
import org.peerfact.impl.service.aggregation.skyeye.SkyNetUtilities;
import org.peerfact.impl.service.aggregation.skyeye.addressresolution.NapsterAddressResolutionImpl;
import org.peerfact.impl.service.aggregation.skyeye.overlay2skynet.util.DefaultLookupResult;
import org.peerfact.impl.service.aggregation.skyeye.overlay2skynet.util.DefaultProcessNextLevelResult;
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
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public class NapsterTreeHandlerDelegator implements TreeHandlerDelegator {

	private static Logger log = SimLogger
			.getLogger(NapsterTreeHandlerDelegator.class);

	private SkyNetNodeInterface skyNetNode;

	private CIClientNode ownOverlayNode;

	private boolean keyResponsibility;

	private SkyNetID predecessor;

	private SkyNetID ownID;

	private SkyNetID skyNetCoKey;

	private BigDecimal left;

	private BigDecimal right;

	private int iter;

	public NapsterTreeHandlerDelegator() {
		keyResponsibility = false;
	}

	@Override
	public void setSkyNetNode(SkyNetNodeInterface skyNetNode) {
		this.skyNetNode = skyNetNode;
	}

	@Override
	public void setOwnOverlayNode(OverlayNode<?, ?> ownOverlayNode) {
		this.ownOverlayNode = (CIClientNode) ownOverlayNode;
	}

	// //////////////////////////////////////////////////////////////
	// Methods for looking up the parent-coordinator of a coordinator by the
	// calculated coordinatorKey
	// //////////////////////////////////////////////////////////////

	@Override
	public void lookupOperationFailed(Operation<?> op) {
		log.error(Simulator.getFormattedTime(Simulator.getCurrentTime())
				+ " "
				+ skyNetNode.getSkyNetNodeInfo().getTransInfo().getNetId()
						.toString()
				+ " ----NO CHANCE TO LOOKUP PARENTCOORDINATOR----");
	}

	@Override
	public LookupResult lookupOperationSucceeded(Operation<?> op) {
		OverlayContact<?> contact = (OverlayContact<?>) op.getResult();
		SkyNetID skyNetID = NapsterAddressResolutionImpl.getInstance(
				(int) SkyNetConstants.OVERLAY_ID_SIZE).getSkyNetID(
				((CIOverlayContact) contact).getOverlayID());
		return new DefaultLookupResult(contact,
				skyNetID);
	}

	@Override
	public void lookupParentCoordinator(SkyNetID coordinatorKey,
			OperationCallback<?> callback) {
		CIOverlayID napsterCoKey = NapsterAddressResolutionImpl
				.getInstance((int) SkyNetConstants.OVERLAY_ID_SIZE)
				.getOverlayID(coordinatorKey);
		ownOverlayNode.nodeLookup(napsterCoKey,
				(OperationCallback<List<CIOverlayContact>>) callback);
	}

	// //////////////////////////////////////////////////////////////
	// Methods for processing the next level of the SkyNet-Tree
	// //////////////////////////////////////////////////////////////

	@Override
	public void processNextLevel(SkyNetID ownId, SkyNetID skyNetCOKey,
			BigDecimal l, BigDecimal r, int iterator,
			OperationCallback<?> callback) {
		this.ownID = ownId;
		this.skyNetCoKey = skyNetCOKey;
		this.left = l;
		this.right = r;
		this.iter = iterator;
		// special case: the responsibility-interval is cut into two intervals
		if (ownId.getID().compareTo(predecessor.getID()) < 1) {
			if (predecessor.getID().compareTo(skyNetCOKey.getID()) == -1
					|| skyNetCOKey.getID().compareTo(ownId.getID()) < 1) {
				keyResponsibility = true;
			} else {
				keyResponsibility = false;
			}
		}
		// normal case: the responsibility-interval is not divided
		else {
			if ((predecessor.getID().compareTo(skyNetCOKey.getID()) == -1)
					&& (skyNetCOKey.getID().compareTo(ownId.getID()) < 1)) {
				keyResponsibility = true;
			} else {
				keyResponsibility = false;
			}
		}
		callback.calledOperationSucceeded(null);
	}

	@Override
	public void processNextLevelOperationFailed(Operation<?> op) {
		// not needed
	}

	@Override
	public ProcessNextLevelResult processNextLevelOperationSucceeded(
			Operation<?> op) {
		return new DefaultProcessNextLevelResult(keyResponsibility, ownID,
				skyNetCoKey, left, right, iter);
	}

	// //////////////////////////////////////////////////////////////
	// Methods for determining the responsibility-interval of a node on the
	// current overlay and its mapping to the ID-space of SkyNet
	// //////////////////////////////////////////////////////////////

	@Override
	public void calculateResponsibilityInterval(SkyNetID id,
			OperationCallback callback) {
		ownOverlayNode.getPredecessor(callback);

	}

	@Override
	public void calculateResponsibilityIntervalOperationFailed(Operation<?> op) {
		log.error(SkyNetUtilities.getTimeAndNetID(skyNetNode)
				+ "----NO CHANCE TO GET PREDECESSOR----");
	}

	@Override
	public SkyNetID calculateResponsibilityIntervalOperationSucceeded(
			Operation<?> op) {
		SkyNetID predecessorID = NapsterAddressResolutionImpl.getInstance(
				(int) SkyNetConstants.OVERLAY_ID_SIZE).getSkyNetID(
				((GetPredecessorOperation) op).getResult().getOverlayID());
		log.info(" The client " + skyNetNode.getSkyNetNodeInfo().toString()
				+ " has the Predecessor with the id "
				+ predecessorID.getPlainSkyNetID());
		predecessor = predecessorID;
		return predecessorID;
	}

}
