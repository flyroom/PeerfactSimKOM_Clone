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

package org.peerfact.impl.overlay.dht.chord.base.callbacks;

import org.peerfact.api.common.OperationCallback;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordContact;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordNode;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordRoutingTable;
import org.peerfact.impl.overlay.dht.chord.base.components.ChordConfiguration;
import org.peerfact.impl.overlay.dht.chord.base.messages.LeaveMessage;
import org.peerfact.impl.overlay.dht.chord.base.operations.AbstractChordOperation;

/**
 * This class represents a leave event.
 * 
 * @author Minh Hoang Nguyen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class LeaveOperation extends AbstractChordOperation<Object> {

	private final AbstractChordNode leaveNode;

	public LeaveOperation(AbstractChordNode component,
			OperationCallback<Object> callback) {
		super(component, callback);
		leaveNode = getComponent();
	}

	@Override
	protected void execute() {

		if (leaveNode.isPresent()) {

			log.debug("node leave node = " + leaveNode);
			AbstractChordRoutingTable routingTable = leaveNode
					.getChordRoutingTable();

			// inform successor
			AbstractChordContact successor = routingTable.getSuccessor();
			if (successor != null) {
				LeaveMessage leaveMessage = new LeaveMessage(
						leaveNode.getLocalOverlayContact(), successor);
				leaveNode.getTransLayer().send(leaveMessage,
						successor.getTransInfo(), leaveNode.getPort(),
						ChordConfiguration.TRANSPORT_PROTOCOL);

			}

			// inform predecessor
			AbstractChordContact predecessor = routingTable.getPredecessor();
			if (predecessor != null) {
				LeaveMessage leaveMessage = new LeaveMessage(
						leaveNode.getLocalOverlayContact(), predecessor);
				leaveNode.getTransLayer().send(leaveMessage,
						predecessor.getTransInfo(), leaveNode.getPort(),
						ChordConfiguration.TRANSPORT_PROTOCOL);

			}
		} else {
			operationFinished(false);
		}

		leaveNode.leaveOperationFinished();
	}

	@Override
	public Object getResult() {

		return this.isSuccessful();
	}

}
