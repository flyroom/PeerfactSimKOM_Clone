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

import java.util.ArrayList;

import org.peerfact.api.common.OperationCallback;
import org.peerfact.impl.common.AbstractOperation;
import org.peerfact.impl.overlay.AbstractOverlayNode.PeerStatus;
import org.peerfact.impl.overlay.dht.can.components.CanArea;
import org.peerfact.impl.overlay.dht.can.components.CanConfig;
import org.peerfact.impl.overlay.dht.can.components.CanNode;
import org.peerfact.impl.overlay.dht.can.components.CanOverlayContact;
import org.peerfact.impl.overlay.dht.can.components.CanVID;
import org.peerfact.impl.simengine.Simulator;

/**
 * 
 * Operation is used to build up a new CAN. If the first node joins the CAN it
 * creates a CAN.
 * 
 * 
 * @author Bjoern Dollak <peerfact@kom.tu-darmstadt.de>
 * @version February 2010
 * 
 */
public class CreateCanOperation extends AbstractOperation<CanNode, Object> {

	/**
	 * creates a CAN with the given peer
	 * 
	 * @param node
	 *            first peer in CAN
	 * @param callback
	 *            operation listener
	 */
	public CreateCanOperation(CanNode node, OperationCallback<Object> callback) {
		super(node, callback);
	}

	@Override
	protected void execute() {
		CanNode master = getComponent();
		master.setAlive(true);
		master.setPeerStatus(PeerStatus.PRESENT);

		master.setNeighbours(new ArrayList<CanOverlayContact>());
		CanArea area = new CanArea(0, CanConfig.CanSize, 0, CanConfig.CanSize);
		master.setArea(area);
		master.getLocalOverlayContact().getArea().setVid(new CanVID("0"));
		CanOverlayContact[] vidNeighbours = {
				master.getLocalOverlayContact().clone(),
				master.getLocalOverlayContact().clone() };
		master.setVIDNeigbours(vidNeighbours);
		master.setPeerStatus(PeerStatus.PRESENT);
		master.startTakeoverOperation();

		master.getBootstrap().registerNode(master);
		operationFinished(true);

		log.debug(Simulator.getSimulatedRealtime() + "Created Can");
		this.isSuccessful();
	}

	@Override
	public Object getResult() {
		return this;
	}

}
