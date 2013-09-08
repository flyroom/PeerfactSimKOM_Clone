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

package org.peerfact.impl.overlay.informationdissemination.psense.operations;

import java.util.List;
import java.util.Vector;

import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.impl.common.AbstractOperation;
import org.peerfact.impl.overlay.AbstractOverlayNode.PeerStatus;
import org.peerfact.impl.overlay.informationdissemination.psense.PSenseBootstrapManager;
import org.peerfact.impl.overlay.informationdissemination.psense.PSenseID;
import org.peerfact.impl.overlay.informationdissemination.psense.PSenseNode;
import org.peerfact.impl.overlay.informationdissemination.psense.messages.PositionUpdateMsg;
import org.peerfact.impl.overlay.informationdissemination.psense.messages.SensorRequestMsg;
import org.peerfact.impl.overlay.informationdissemination.psense.util.Configuration;
import org.peerfact.impl.simengine.Simulator;


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
 * This class is a operation for the join. The operation try to join to the
 * overlay. Additionally sets the status of the node during the join.
 * 
 * <p>
 * For the join, the operation used the bootstrap instance. It fetches for every
 * sensor node a node in the overlay over the bootstrap instance. Then take
 * every X ms a lookup for a receiving of a response. If a response is arrived,
 * then is the joinig operations finished.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 10/15/2010
 */
public class JoinOperation extends AbstractOperation<PSenseNode, Boolean> {

	private PSenseNode node;

	private PSenseBootstrapManager bootstrap;

	private boolean joinSuccessful;

	public JoinOperation(PSenseNode component,
			OperationCallback<Boolean> callback) {
		super(component, callback);
		node = component;
		bootstrap = node.getBootstrapManager();
		joinSuccessful = false;
	}

	@Override
	protected void execute() {

		if (node.getPeerStatus() == PeerStatus.TO_JOIN) {

			if (node.isAMessageArrived()) {
				if (log.isDebugEnabled()) {
					log.debug("A message is arrived. The node "
							+ node.getOverlayID()
							+ " is present in the overlay!");
				}
				node.setPeerStatus(PeerStatus.PRESENT);
				bootstrap.registerNode(node);
				joinSuccessful = true;
			} else {
				if (Simulator.getCurrentTime() - node.getLastJoinAttempt() > Configuration.WAIT_BEFORE_RETRY_JOIN) {
					// do a rejoin
					node.setLastJoinAttempt(Simulator.getCurrentTime());
					boolean operationSuccessful = join();

					if (operationSuccessful) {
						node.setPeerStatus(PeerStatus.PRESENT);
						joinSuccessful = true;
					} else {
						joinSuccessful = false;
					}
				} else {
					// Do nothing, because waiting
					joinSuccessful = false;
				}
			}

		} else if (node.getPeerStatus() == PeerStatus.ABSENT) {

			node.setLastJoinAttempt(Simulator.getCurrentTime());
			boolean operationFinished = join();

			if (operationFinished) {
				node.setPeerStatus(PeerStatus.PRESENT);
				joinSuccessful = true;
			} else {
				node.setPeerStatus(PeerStatus.TO_JOIN);
				joinSuccessful = false;
			}

		} else {
			log.error("Wrong peer status or rather this operation has no handling for this peer status.");
		}
		operationFinished(true);
	}

	private boolean join() {
		if (!bootstrap.anyNodeAvailable()) {
			if (log.isInfoEnabled()) {
				log.info("Alone in the Overlay!");
			}
			bootstrap.registerNode(node);
			return true;
		} else {

			List<TransInfo> toRequest;
			toRequest = bootstrap
					.getBootstrapInfo(Configuration.NUMBER_SECTORS);

			node.incSeqNr();

			for (int i = 0; i < toRequest.size()
					&& i < Configuration.NUMBER_SECTORS; i++) {
				SensorRequestMsg reqMsg = new SensorRequestMsg(
						node.getOverlayID(), Configuration.MAXIMAL_HOP,
						node.getSeqNr(), Configuration.VISION_RANGE_RADIUS,
						node.getPosition(), (byte) i);
				PositionUpdateMsg posMsg = new PositionUpdateMsg(
						node.getOverlayID(), Configuration.MAXIMAL_HOP,
						node.getSeqNr(), new Vector<PSenseID>(),
						Configuration.VISION_RANGE_RADIUS, node.getPosition());
				// Data amount to few for sorting out of needless
				// updateMessages
				// or that the bandwidth not reach
				node.getTransLayer().send(reqMsg, toRequest.get(i),
						node.getPort(), Configuration.TRANSPORT_PROTOCOL);
				node.getTransLayer().send(posMsg, toRequest.get(i),
						node.getPort(), Configuration.TRANSPORT_PROTOCOL);
			}
			return false;
		}
	}

	@Override
	public Boolean getResult() {
		return joinSuccessful;
	}

	public void churnDuringJoin() {
		log.error(node.getOverlayID()
				+ " could not complete join due to churn.");
		operationFinished(false);
	}
}
