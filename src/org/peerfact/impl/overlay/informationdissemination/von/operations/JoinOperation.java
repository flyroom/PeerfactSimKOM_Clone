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

import org.peerfact.api.common.Message;
import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.api.transport.TransMessageCallback;
import org.peerfact.impl.common.AbstractOperation;
import org.peerfact.impl.overlay.informationdissemination.von.VonConfiguration;
import org.peerfact.impl.overlay.informationdissemination.von.VonContact;
import org.peerfact.impl.overlay.informationdissemination.von.VonNode;
import org.peerfact.impl.overlay.informationdissemination.von.messages.InitialQueryMsg;

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
 * This operation handles the joining of a new node.
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class JoinOperation extends AbstractOperation<VonNode, Object> implements
		TransMessageCallback {

	private int queryCommID;

	public JoinOperation(VonNode node, OperationCallback<Object> callback) {
		super(node, callback);

	}

	@Override
	protected void execute() {
		log.debug(getComponent().getOverlayID() + " initiated join");

		scheduleOperationTimeout(VonConfiguration.OP_TIMEOUT_JOIN);

		VonNode node = getComponent();

		InitialQueryMsg qm = new InitialQueryMsg(new VonContact(
				node.getVonID(), node.getTransInfo()), node.getPosition(),
				node.getAOI());

		node.getMsgHandler().sendInitialQueryMsgToBootstrap(qm);
	}

	@Override
	public Object getResult() {
		return null;
	}

	@Override
	public void messageTimeoutOccured(int commId) {
		// Will never be called as we do not use sendAndWait!

		log.error(getComponent().getVonID() + " msg timeout in JoinOperation.");
		operationFinished(false);
	}

	@Override
	public void receive(Message msg, TransInfo senderInfo, int commId) {
		// Will never be called as we do not use sendAndWait!
		if (this.queryCommID == commId) {
			operationFinished(true);
		} else {
			operationFinished(false);
		}
	}

	/**
	 * Inform waiting JoinOperation about the arrival of the PeerMsg
	 */
	public void peerMsgReceived() {
		operationFinished(true);
	}

	public void churnDuringJoin() {
		log.error(getComponent().getVonID()
				+ " could not complete join due to churn.");
		operationFinished(false);
	}
}
