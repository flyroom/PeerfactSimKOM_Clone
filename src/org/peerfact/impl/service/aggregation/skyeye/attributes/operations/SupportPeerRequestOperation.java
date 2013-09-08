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

package org.peerfact.impl.service.aggregation.skyeye.attributes.operations;

import org.peerfact.api.common.Message;
import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.service.skyeye.SkyNetNodeInfo;
import org.peerfact.api.service.skyeye.SkyNetNodeInterface;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.api.transport.TransMessageCallback;
import org.peerfact.api.transport.TransProtocol;
import org.peerfact.impl.common.AbstractOperation;
import org.peerfact.impl.service.aggregation.skyeye.attributes.messages.SupportPeerRequestACKMsg;
import org.peerfact.impl.service.aggregation.skyeye.attributes.messages.SupportPeerRequestMsg;

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
 * This class implements the operation of a SupportPeerRequest. Within this
 * operation a {@link SupportPeerRequestMsg}, which contains the request for a
 * node to act as Support Peer, is sent to a chosen SkyNet-node, which answers
 * with a {@link SupportPeerRequestACKMsg}. Besides the acknowledgment, the
 * message contains the answer of the addressed node concerning the Support
 * Peer-request. If no answer is received, the message is retransmitted.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 05.12.2008
 * 
 */
public class SupportPeerRequestOperation extends
		AbstractOperation<SkyNetNodeInterface, SupportPeerRequestACKMsg>
		implements TransMessageCallback {

	private SupportPeerRequestMsg request;

	private SupportPeerRequestACKMsg reply;

	private SkyNetNodeInfo receiverInfo;

	private int retry;

	private int msgID = -2;

	public SupportPeerRequestOperation(SkyNetNodeInterface skyNetNode,
			SkyNetNodeInfo senderInfo, SkyNetNodeInfo receiverInfo,
			SkyNetNodeInfo parentCoordinator, long skyNetMsgID,
			OperationCallback<SupportPeerRequestACKMsg> operationCallback) {
		super(skyNetNode, operationCallback);
		request = new SupportPeerRequestMsg(senderInfo, receiverInfo,
				parentCoordinator, skyNetMsgID);
		this.receiverInfo = receiverInfo;
		retry = 0;
	}

	@Override
	protected void execute() {
		long ackTime = getComponent().getAttributeUpdateStrategy()
				.getTimeForACK();
		msgID = getComponent().getTransLayer().sendAndWait(request,
				receiverInfo.getTransInfo(), getComponent().getPort(),
				TransProtocol.UDP, this, ackTime);
		log.debug("Initiating transMessage with id " + msgID
				+ "-->SkyNetMsgID " + request.getSkyNetMsgID());
	}

	@Override
	public SupportPeerRequestACKMsg getResult() {
		return reply;
	}

	@Override
	public void messageTimeoutOccured(int commId) {
		log
				.info(retry
						+ ". SupportPeerRequestOperation failed @ "
						+ getComponent().getSkyNetNodeInfo().getTransInfo()
								.getNetId()
						+ " due to Message-timeout of transMessage with ID = "
						+ commId);
		if (retry < getComponent().getAttributeUpdateStrategy()
				.getNumberOfRetransmissions()) {
			retry = retry + 1;
			execute();
		} else {
			retry = 0;
			operationFinished(false);
		}
	}

	@Override
	public void receive(Message msg, TransInfo senderInfo, int commId) {
		retry = 0;
		reply = (SupportPeerRequestACKMsg) msg;
		if (request.getSkyNetMsgID() == reply.getSkyNetMsgID()) {
			log.debug("TransMessage with ID = " + commId + " is received");
			operationFinished(true);
		} else {
			log
					.error("The SkyNetMsgID send does not equal the SkyNetMsgID received");
		}
	}

}
