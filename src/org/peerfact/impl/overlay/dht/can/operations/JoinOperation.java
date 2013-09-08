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

import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.transport.TransProtocol;
import org.peerfact.impl.common.AbstractOperation;
import org.peerfact.impl.overlay.AbstractOverlayNode.PeerStatus;
import org.peerfact.impl.overlay.dht.can.components.CanNode;
import org.peerfact.impl.overlay.dht.can.messages.JoinMsg;
import org.peerfact.impl.simengine.Simulator;

/**
 * 
 * This Operation initiate a join message when a new peer wants to enter the
 * CAN. It selects a node a send the join message to it.
 * 
 * @author Bjoern Dollak <peerfact@kom.tu-darmstadt.de>
 * @version February 2010
 * 
 */
public class JoinOperation extends AbstractOperation<CanNode, Object> {

	private CanNode master = getComponent();

	private CanNode pickedNode = null; // an existing node in Can network (from
										// bootstrap)

	private OperationCallback<Object> callback;

	/**
	 * @param master
	 *            joining peer
	 * @param pickedNode
	 *            selected peer
	 * @param callback
	 *            The operation listener
	 */
	public JoinOperation(CanNode node, OperationCallback<Object> callback) {
		super(node, callback);
		this.callback = callback;
	}

	/**
	 * sends the join message to a selected node
	 */
	@Override
	protected void execute() {
		if (master.getPeerStatus().equals(PeerStatus.ABSENT)) {
			System.err.println("peer offline, unable to join.");
			operationFinished(false);
			return;
		} else if (master.getPeerStatus().equals(PeerStatus.PRESENT)) {
			System.err.println("peer already online, unable to join.");
			operationFinished(true);
			return;
		}

		log.debug(Simulator.getSimulatedRealtime() + " join: "
				+ master.getLocalOverlayContact().getOverlayID().toString());

		master.setPeerStatus(PeerStatus.PRESENT);
		master.setAlive(true);

		pickedNode = master.getBootstrap().pick();
		if (pickedNode == null) { // create a new network
			log.debug(Simulator.getSimulatedRealtime()
					+ " New Can will be created.");
			new CreateCanOperation(master, callback).scheduleImmediately();
			operationFinished(true);
			log.debug(master.getLocalOverlayContact().getArea().toString());
			return;
		} else {
			log.debug(Simulator.getSimulatedRealtime()
					+ "Picked ID:"
					+ pickedNode.getLocalOverlayContact().getOverlayID()
							.toString());

			JoinMsg join = new JoinMsg(this.master.getOverlayID(),
					pickedNode.getOverlayID(), this.master
							.getLocalOverlayContact().clone());
			this.master.getTransLayer().send(join,
					pickedNode.getLocalOverlayContact().getTransInfo(),
					pickedNode.getPort(), TransProtocol.TCP);

		}
		this.operationFinished(true);
		master.startTakeoverOperation(); // start continuously sending ping msg
		master.setJoiningTime(Simulator.getSimulatedRealtime());
	}

	@Override
	public Object getResult() {
		return this;
	}

}
