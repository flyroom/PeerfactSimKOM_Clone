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

package org.peerfact.impl.overlay.dht.can.operations;

import org.peerfact.api.common.Message;
import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.api.transport.TransMessageCallback;
import org.peerfact.impl.common.AbstractOperation;
import org.peerfact.impl.overlay.AbstractOverlayNode.PeerStatus;
import org.peerfact.impl.overlay.dht.can.components.CanNode;
import org.peerfact.impl.simengine.Simulator;

/**
 * 
 * This operation is just used for debug. It shows all data of a peer.
 * 
 * @param master
 *            peer which should output its data
 * 
 * @author Bjoern Dollak <peerfact@kom.tu-darmstadt.de>
 * @version February 2010
 * 
 */
public class GiveNodeInfoOperation extends AbstractOperation<CanNode, Object>
		implements TransMessageCallback {

	CanNode master = getComponent();

	/**
	 * Shows all the information about the peer
	 * 
	 * @param node
	 * @param callback
	 */
	public GiveNodeInfoOperation(CanNode node,
			OperationCallback<Object> callback) {
		super(node, callback);
	}

	@Override
	public void execute() {
		if (master.getPeerStatus().equals(PeerStatus.PRESENT)) {
			master.getBootstrap().update(master);
			log.debug(Simulator.getSimulatedRealtime() + " Bootstrap: "
					+ master.getBootstrap().toString());
			log.debug("Own ID: "
					+ master.getLocalOverlayContact().getOverlayID().toString()
					+ " own VID "
					+ master.getLocalOverlayContact().getArea().getVid()
							.toString()
					+ " own area "
					+ master.getLocalOverlayContact().getArea().toString()
					+ " is allive: "
					+ master.getLocalOverlayContact().isAlive()
					+ " Neighbours: ");
			try {
				for (int i = 0; i < master.getNeighbours().size(); i++) {
					log.debug(master.getNeighbours().get(i).getOverlayID()
							.toString()
							+ " "
							+ master.getNeighbours().get(i).getArea()
									.toString());
				}
				log.debug("VID Neighbours "
						+ master.getVIDNeighbours()[0].getArea().getVid()
								.toString()
						+ " "
						+ master.getVIDNeighbours()[0].getOverlayID()
								.toString()
						+ " "
						+ master.getVIDNeighbours()[1].getArea().getVid()
								.toString()
						+ " "
						+ master.getVIDNeighbours()[1].getOverlayID()
								.toString());

			} catch (Exception e) {
				log.error("Exception in GiveNodeInfoOperation occured", e);
			}
		}
	}

	@Override
	public Object getResult() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void messageTimeoutOccured(int commId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void receive(Message msg, TransInfo senderInfo, int commId) {
		// TODO Auto-generated method stub

	}

}
