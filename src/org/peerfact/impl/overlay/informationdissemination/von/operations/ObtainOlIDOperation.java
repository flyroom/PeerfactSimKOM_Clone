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
import org.peerfact.impl.overlay.informationdissemination.von.VonBootstrapManager;
import org.peerfact.impl.overlay.informationdissemination.von.VonConfiguration;
import org.peerfact.impl.overlay.informationdissemination.von.VonID;
import org.peerfact.impl.overlay.informationdissemination.von.VonNode;
import org.peerfact.impl.overlay.informationdissemination.von.messages.ObtainIDMsg;

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
 * This operation is used to obtain a new OverlayId when joining the overlay.
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class ObtainOlIDOperation extends AbstractOperation<VonNode, VonID>
		implements TransMessageCallback {

	private int commId;

	private VonID obtainedId;

	/**
	 * @param node
	 *            the node that starts the operation
	 * @param callback
	 *            the component informed about results of the operation
	 */
	public ObtainOlIDOperation(VonNode node, OperationCallback<VonID> callback) {
		super(node, callback);

	}

	@Override
	protected void execute() {
		log.debug(getComponent().getOverlayID() + " initiated obtaining of ID");
		this.scheduleOperationTimeout(VonConfiguration.OP_TIMEOUT_OBTAIN_ID);

		sendObtainIDMsg();
	}

	@Override
	public VonID getResult() {
		return obtainedId;
	}

	@Override
	public void messageTimeoutOccured(int communicationId) {
		/*
		 * Try again until the Operation timeout happens.
		 */

		if (this.commId == communicationId) {
			log.error(getComponent().getVonID()
					+ " a timeout occured while sending the ObtainIDMsg. I try again.");
		}
		operationFinished(false);
	}

	@Override
	public void receive(Message msg, TransInfo senderInfo, int communicationId) {
		if (this.commId == communicationId) {
			if (msg instanceof ObtainIDMsg) {
				obtainedId = ((ObtainIDMsg) msg).getReceiver();
				log.debug(getComponent().getOverlayID()
						+ " received ObtainIDMsg (ID=" + obtainedId + ")");
				operationFinished(true);
			}
		}
		operationFinished(false);
	}

	private void sendObtainIDMsg() {
		VonBootstrapManager bs = getComponent().getBootstrapManager();
		if (bs.anyNodeAvailable()) {
			TransInfo bootstrapInfo = bs.getBootstrapInfo().get(0);

			ObtainIDMsg om = new ObtainIDMsg(VonID.EMPTY_ID, VonID.EMPTY_ID);

			commId = getComponent().getTransLayer().sendAndWait(om,
					bootstrapInfo, getComponent().getPort(),
					VonConfiguration.TRANSPORT_PROTOCOL, this,
					VonConfiguration.GENERAL_MSG_TIMEOUT);
		} else {
			log.error(getComponent().getOverlayID()
					+ " there is no bootstrap node to join.");
		}
	}
}
