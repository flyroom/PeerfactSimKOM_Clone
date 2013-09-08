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
import java.math.BigInteger;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.peerfact.api.common.Operation;
import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.api.overlay.OverlayID;
import org.peerfact.api.overlay.OverlayNode;
import org.peerfact.api.overlay.kbr.KBRNode;
import org.peerfact.api.service.skyeye.SkyNetConstants;
import org.peerfact.api.service.skyeye.SkyNetNodeInterface;
import org.peerfact.api.service.skyeye.overlay2SkyNet.TreeHandlerDelegator;
import org.peerfact.api.service.skyeye.overlay2SkyNet.util.LookupResult;
import org.peerfact.api.service.skyeye.overlay2SkyNet.util.ProcessNextLevelResult;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayContact;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayID;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayKey;
import org.peerfact.impl.overlay.dht.kademlia.base.operations.KClosestNodesLookupOperation;
import org.peerfact.impl.overlay.dht.kademlia.kademlia.components.KademliaNodeGlobalKnowledge;
import org.peerfact.impl.overlay.kbr.operations.KBRLookupOperation;
import org.peerfact.impl.service.aggregation.skyeye.SkyNetID;
import org.peerfact.impl.service.aggregation.skyeye.SkyNetUtilities;
import org.peerfact.impl.service.aggregation.skyeye.addressresolution.KademliaAddressResolutionImpl;
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
 * 
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public class KademliaTreeHandlerDelegator implements TreeHandlerDelegator {

	private static Logger log = SimLogger
			.getLogger(Chord2TreeHandlerDelegator.class);

	private SkyNetNodeInterface skyNetNode;

	private KBRNode<KademliaOverlayID, KademliaOverlayContact<KademliaOverlayID>, KademliaOverlayKey> ownOverlayNode;

	private KademliaOverlayKey coKey;

	private boolean keyResponsibility;

	private SkyNetID ownID;

	private SkyNetID skyNetCoKey;

	private BigDecimal left;

	private BigDecimal right;

	private int iter;

	public KademliaTreeHandlerDelegator() {
		keyResponsibility = false;
	}

	@Override
	public void setSkyNetNode(SkyNetNodeInterface skyNetNode) {
		this.skyNetNode = skyNetNode;
	}

	@Override
	public void setOwnOverlayNode(OverlayNode<?, ?> ownOverlayNode) {
		this.ownOverlayNode = (KademliaNodeGlobalKnowledge<KademliaOverlayID>) ownOverlayNode;
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
		log.info(SkyNetUtilities.getTimeAndNetID(skyNetNode)
				+ "calls lookupOperationSucceeded");
		TreeMap<BigInteger, KademliaOverlayContact<KademliaOverlayID>> distanceMap = new TreeMap<BigInteger, KademliaOverlayContact<KademliaOverlayID>>();
		BigInteger distance = null;
		if (op instanceof KClosestNodesLookupOperation) {
			Set<KademliaOverlayContact<KademliaOverlayID>> idSet = ((KClosestNodesLookupOperation<KademliaOverlayID>) op)
					.getNodes();
			KademliaNodeGlobalKnowledge<KademliaOverlayID> kadNode = (KademliaNodeGlobalKnowledge<KademliaOverlayID>) skyNetNode
					.getHost().getOverlay(KademliaNodeGlobalKnowledge.class);
			Iterator<KademliaOverlayContact<KademliaOverlayID>> contactIter = idSet
					.iterator();
			KademliaOverlayContact<KademliaOverlayID> tempContact = null;
			while (contactIter.hasNext()) {
				tempContact = contactIter.next();
				if (kadNode.getLocalContact().getOverlayID().getBigInt()
						.compareTo(tempContact.getOverlayID().getBigInt()) != 0) {
					distance = tempContact.getOverlayID().getBigInt().xor(
							kadNode.getLocalContact().getOverlayID()
									.getBigInt());
					distanceMap.put(distance, tempContact);
					log.debug(SkyNetUtilities.getTimeAndNetID(skyNetNode)
							+ "distance = " + distance + "; ID = "
							+ tempContact.getTransInfo().getNetId().toString());

				}
			}

			OverlayContact<KademliaOverlayID> contact = distanceMap
					.firstEntry().getValue();
			SkyNetID skyNetID = KademliaAddressResolutionImpl.getInstance(
					(int) SkyNetConstants.OVERLAY_ID_SIZE).getSkyNetID(
					((KademliaOverlayContact<KademliaOverlayID>) contact)
							.getOverlayID());
			return new DefaultLookupResult(contact, skyNetID);

		} else if (op instanceof KBRLookupOperation) {
			KBRLookupOperation<KademliaOverlayID, KademliaOverlayContact<KademliaOverlayID>, KademliaOverlayKey> lookup = (KBRLookupOperation<KademliaOverlayID, KademliaOverlayContact<KademliaOverlayID>, KademliaOverlayKey>) op;

			SkyNetID skyNetID = KademliaAddressResolutionImpl.getInstance(
					(int) SkyNetConstants.OVERLAY_ID_SIZE).getSkyNetID(
					(lookup.getResult()).getOverlayID());

			return new DefaultLookupResult(lookup.getResult(), skyNetID);
		}

		log
				.error("Something bad happend when handling the successfull operation.");

		return null;

	}

	@Override
	public void lookupParentCoordinator(SkyNetID coordinatorKey,
			OperationCallback<?> callback) {
		log.info(SkyNetUtilities.getTimeAndNetID(skyNetNode)
				+ "calls lookupParentCoordinator");
		KademliaOverlayKey overlayKey = KademliaAddressResolutionImpl
				.getInstance((int) SkyNetConstants.OVERLAY_ID_SIZE)
				.getOverlayKey(coordinatorKey);
		// KademliaOperation lookupOp = ownOverlayNode.getOperationFactory()
		// .getKClosestNodesLookupOperation(overlayKey,
		// Reason.USER_INITIATED, callback);
		// lookupOp.scheduleImmediately();

		ownOverlayNode
				.getKbrLookupProvider()
				.lookupKey(
						overlayKey,
						(OperationCallback<KademliaOverlayContact<KademliaOverlayID>>) callback);

	}

	// //////////////////////////////////////////////////////////////
	// Methods for processing the next level of the SkyNet-Tree
	// //////////////////////////////////////////////////////////////

	@Override
	public void processNextLevel(SkyNetID ownId, SkyNetID skyNetCOKey,
			BigDecimal l, BigDecimal r, int iterator,
			OperationCallback<?> callback) {
		log.info(SkyNetUtilities.getTimeAndNetID(skyNetNode)
				+ "calls processNextLevel");
		this.ownID = ownId;
		this.skyNetCoKey = skyNetCOKey;
		this.left = l;
		this.right = r;
		this.iter = iterator;
		coKey = KademliaAddressResolutionImpl.getInstance(
				(int) SkyNetConstants.OVERLAY_ID_SIZE).getOverlayKey(
				skyNetCOKey);
		if (ownOverlayNode.isRootOf(coKey)) {
			// KademliaOperation lookupOp = ownOverlayNode.getOperationFactory()
			// .getKClosestNodesLookupOperation(coKey,
			// Reason.USER_INITIATED, callback);
			// lookupOp.scheduleImmediately();
			keyResponsibility = true;
			callback.calledOperationSucceeded(null);
		} else {
			log.info(SkyNetUtilities.getTimeAndNetID(skyNetNode)
					+ " is not responsible (via isRootOf) for the key "
					+ skyNetCOKey);
			keyResponsibility = false;
			callback.calledOperationSucceeded(null);
		}

	}

	@Override
	public void processNextLevelOperationFailed(Operation<?> op) {
		log.error(Simulator.getFormattedTime(Simulator.getCurrentTime())
				+ " "
				+ skyNetNode.getSkyNetNodeInfo().getTransInfo().getNetId()
						.toString()
				+ " ----NO CHANCE TO LOOKUP K CLOSEST NODES----");
	}

	@Override
	public ProcessNextLevelResult processNextLevelOperationSucceeded(
			Operation<?> op) {
		log.info(SkyNetUtilities.getTimeAndNetID(skyNetNode)
				+ "calls processNextLevelOperationSucceeded");
		boolean isRoot = true;
		BigInteger distance = ownOverlayNode.getLocalOverlayContact()
				.getOverlayID().getBigInt().xor(coKey.getBigInt());
		if (op != null) {
			Set<KademliaOverlayContact<KademliaOverlayID>> set = ((KClosestNodesLookupOperation<KademliaOverlayID>) op)
					.getNodes();
			KademliaOverlayContact<?> con = null;
			Iterator<?> conIter = set.iterator();
			BigInteger tempDist = null;
			while (conIter.hasNext()) {
				con = (KademliaOverlayContact<?>) conIter.next();
				tempDist = con.getOverlayID().getBigInt()
						.xor(coKey.getBigInt());
				if (distance.compareTo(tempDist) == 1) {
					isRoot = false;
					break;
				}
			}
			if (isRoot) {
				keyResponsibility = true;
			} else {
				keyResponsibility = false;
			}
		}
		return new DefaultProcessNextLevelResult(keyResponsibility, ownID,
				skyNetCoKey, left, right, iter);
	}

	// //////////////////////////////////////////////////////////////
	// Methods for determining the responsibility-interval of a node on the
	// current overlay and its mapping to the ID-space of SkyNet
	// //////////////////////////////////////////////////////////////

	@Override
	public void calculateResponsibilityInterval(SkyNetID id,
			OperationCallback<OverlayContact<OverlayID<?>>> callback) {
		log.info(SkyNetUtilities.getTimeAndNetID(skyNetNode)
				+ "calls calculateResponsibilityInterval");
		// This is just dummy-method to obtain the same structure as for the
		// other overlays. Within this function there exists only the
		// function-call of processNextLevel(...)
		callback.calledOperationSucceeded(null);
	}

	@Override
	public void calculateResponsibilityIntervalOperationFailed(Operation<?> op) {
		// not needed

	}

	@Override
	public SkyNetID calculateResponsibilityIntervalOperationSucceeded(
			Operation<?> op) {
		log.info(SkyNetUtilities.getTimeAndNetID(skyNetNode)
				+ "calls calculateResponsibilityIntervalOperationSucceeded");
		// This is just dummy-method to obtain the same structure as for the
		// other overlays. Within this function there exists only the
		// function-call of processNextLevel(...)
		return null;
	}

}
