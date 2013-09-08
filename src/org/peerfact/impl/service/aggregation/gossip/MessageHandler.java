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

package org.peerfact.impl.service.aggregation.gossip;

import org.apache.log4j.Logger;
import org.peerfact.api.common.Message;
import org.peerfact.api.transport.TransMessageListener;
import org.peerfact.api.transport.TransProtocol;
import org.peerfact.impl.service.aggregation.gossip.messages.ResyncRequest;
import org.peerfact.impl.service.aggregation.gossip.messages.ResyncResponse;
import org.peerfact.impl.service.aggregation.gossip.messages.UpdateRequestMsg;
import org.peerfact.impl.service.aggregation.gossip.operations.UpdateCalleeOperation;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.transport.TransMsgEvent;
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
 * The Gossiping Aggregation Service's message handler for the transport layer.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class MessageHandler implements TransMessageListener {

	Logger log = SimLogger.getLogger(MessageHandler.class);

	private GossipingAggregationService component;

	public MessageHandler(GossipingAggregationService component) {
		super();
		this.component = component;
	}

	@Override
	public void messageArrived(TransMsgEvent receivingEvent) {
		// log.debug("Message arrived at host " + component.getHost());
		Message msg = receivingEvent.getPayload();
		if (msg instanceof ResyncRequest) {
			long time2NextEpoch = component.isSynced() ? component.getSync()
					.getTimeToNextEpoch() : -1;
			ResyncResponse response = new ResyncResponse(component.getSync()
					.getEpoch(), time2NextEpoch);
			component
					.getHost()
					.getTransLayer()
					.sendReply(response, receivingEvent, component.getPort(),
							TransProtocol.UDP);
		} else if (msg instanceof UpdateRequestMsg) {
			if (component.isSynced()) {
				// log.debug("Dispatching update request message");
				UpdateRequestMsg reqMsg = (UpdateRequestMsg) msg;
				log.trace(Simulator.getSimulatedRealtime() + " Peer with ID "
						+ component.getHost().getNetLayer().getNetID()
						+ " received UpdateRequestMsg with "
						+ reqMsg.getPayloadInfo().size() + " entries and size "
						+ reqMsg.getSize());
				new UpdateCalleeOperation(component, receivingEvent, reqMsg)
						.scheduleImmediately();
			} else {
				// Omit. Should not harm the averaging process, since omitted
				// requests always keep
				// the total sum equal, unless omitted responses.
			}
		}
	}

}
