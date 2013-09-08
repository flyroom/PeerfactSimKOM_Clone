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

package org.peerfact.impl.overlay.informationdissemination.von.operations;

import org.peerfact.api.common.OperationCallback;
import org.peerfact.impl.common.AbstractOperation;
import org.peerfact.impl.overlay.AbstractOverlayNode.PeerStatus;
import org.peerfact.impl.overlay.informationdissemination.von.VonConfiguration;
import org.peerfact.impl.overlay.informationdissemination.von.VonID;
import org.peerfact.impl.overlay.informationdissemination.von.VonNode;
import org.peerfact.impl.overlay.informationdissemination.von.VonNodeInfo;
import org.peerfact.impl.overlay.informationdissemination.von.messages.MoveMsg;
import org.peerfact.impl.overlay.informationdissemination.von.voronoi.Voronoi;
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
 * This operation is primarily concerned with the mechanisms for doing heart
 * beats if necessary. Before it does so, it checks the local voronoi diagram
 * for stale neighbors and removes them if necessary.
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class HeartbeatAndMaintenanceOperation extends
		AbstractOperation<VonNode, Object> {

	public HeartbeatAndMaintenanceOperation(VonNode component,
			OperationCallback<Object> callback) {
		super(component, callback);
	}

	@Override
	protected void execute() {
		VonNode node = getComponent();

		if (node.getPeerStatus() != PeerStatus.PRESENT) {
			operationFinished(false);
			return;
		}

		Voronoi v = node.getLocalVoronoi();

		/*
		 * Remove stale contacts from voronoi
		 */
		long removeBefore = Simulator.getCurrentTime()
				- VonConfiguration.STALE_NEIGHBOR_INTERVAL;
		v.removeStaleContacts(removeBefore);

		/*
		 * Do heart beating if necessary
		 */
		if (node.getLastHeartbeatTime()
				+ VonConfiguration.INTERVAL_BETWEEN_HEARTBEATS <= Simulator
					.getCurrentTime()) {

			// Retrieve all neighbors
			VonNodeInfo[] neighbors = node.getLocalVoronoi().getVonNeighbors(
					node.getVonID(), node.getAOI());

			for (VonNodeInfo toInform : neighbors) {
				VonID toInformId = toInform.getContact().getOverlayID();

				MoveMsg mMsg;

				if (v.isBoundaryNeighborOf(node.getVonID(), toInformId,
						node.getAOI())) {
					mMsg = new MoveMsg(node.getVonID(), toInformId, true,
							node.getPosition(), node.getAOI(),
							Simulator.getCurrentTime());

				} else {
					mMsg = new MoveMsg(node.getVonID(), toInformId, false,
							node.getPosition(), node.getAOI(),
							Simulator.getCurrentTime());
				}

				node.getTransLayer().send(mMsg,
						toInform.getContact().getTransInfo(), node.getPort(),
						VonConfiguration.TRANSPORT_PROTOCOL);
			}

			node.setLastHeartbeatTime(Simulator.getCurrentTime());

			operationFinished(true);
		} else {
			operationFinished(true);
		}

	}

	@Override
	public Object getResult() {
		// There is no result
		return null;
	}

}
