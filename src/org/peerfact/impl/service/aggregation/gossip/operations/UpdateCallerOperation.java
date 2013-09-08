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

package org.peerfact.impl.service.aggregation.gossip.operations;

import java.util.Collection;
import java.util.List;

import org.peerfact.api.common.Message;
import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.api.transport.TransMessageCallback;
import org.peerfact.api.transport.TransProtocol;
import org.peerfact.impl.common.AbstractPeriodicOperation;
import org.peerfact.impl.service.aggregation.gossip.GossipingAggregationService;
import org.peerfact.impl.service.aggregation.gossip.IConfiguration;
import org.peerfact.impl.service.aggregation.gossip.Monitoring;
import org.peerfact.impl.service.aggregation.gossip.UpdateInfo;
import org.peerfact.impl.service.aggregation.gossip.messages.UpdateRequestMsg;
import org.peerfact.impl.service.aggregation.gossip.messages.UpdateResponseMsg;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.transport.DefaultTransInfo;
import org.peerfact.impl.util.Tuple;
import org.peerfact.impl.util.stats.distributions.StaticDistribution;
import org.peerfact.impl.util.toolkits.CollectionHelpers;


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
 * 
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public class UpdateCallerOperation
		extends
		AbstractPeriodicOperation.RandomIntervalPeriodicOperation<GossipingAggregationService, Object> {

	static final boolean staticInterval = false;

	public UpdateCallerOperation(GossipingAggregationService component,
			long interval) {
		super(component, new StaticDistribution(((double) interval)
				/ Simulator.SECOND_UNIT));
	}

	@Override
	public void stop() {
		super.stop();
		getComponent().setRPCLocked(false);
	}

	@Override
	protected void executeOnce() {
		GossipingAggregationService comp = this.getComponent();
		IConfiguration conf = comp.getConf();
		comp.measureAttributes();
		List<Tuple<Object, UpdateInfo>> info = comp.getAllLocalUpdateInfos();

		// Determine next receiver of update
		Collection<OverlayContact<?>> nbrs = (Collection<OverlayContact<?>>) comp
				.getNeighborDeterminationStrategy().getNeighbors();
		OverlayContact<?> nb4req = CollectionHelpers.getRandomEntry(nbrs);
		Monitoring.onNeighborCountSeen(nbrs.size());
		if (nb4req == null) {
			return;
		}

		Message msg = new UpdateRequestMsg(comp.getSync().getEpoch(), info,
				comp.getGossipingNodeCountValue().extractInfo());
		TransMessageCallback cb = new TransMessageCallback() {

			@Override
			public void receive(Message message, TransInfo senderInfo,
					int commId) {
				getComponent().setRPCLocked(false);
				handleAnswer(message, senderInfo);
				Monitoring.addSuccessfulRPC();
			}

			@Override
			public void messageTimeoutOccured(int commId) {
				getComponent().setRPCLocked(false);
				Monitoring.addUnsuccessfulRPC();
			}
		};
		log.debug(Simulator.getSimulatedRealtime() + " Peer with ID "
				+ comp.getHost().getNetLayer().getNetID()
				+ " SENDS UpdateRequestMsg with " + info.size()
				+ " entries and size " + msg.getSize());

		comp.setRPCLocked(true);
		comp.getHost()
				.getTransLayer()
				.sendAndWait(
						msg,
						DefaultTransInfo.getTransInfo(nb4req.getTransInfo()
								.getNetId(), comp.getPort()), comp.getPort(),
						TransProtocol.UDP, cb, conf.getReqRespTimeout());
	}

	protected void handleAnswer(Message msg, TransInfo senderInfo) {
		if (msg instanceof UpdateResponseMsg) {
			UpdateResponseMsg respMsg = (UpdateResponseMsg) msg;
			GossipingAggregationService comp = this.getComponent();
			log.debug(Simulator.getSimulatedRealtime() + " Peer with ID "
					+ comp.getHost().getNetLayer().getNetID()
					+ " received UpdateResponseMsg with "
					+ respMsg.getPayloadInfo().size() + " entries and size "
					+ respMsg.getSize());
			if (comp.getSync().onEpochSeen(respMsg.getEpoch())) {
				comp.updateLocalValues(respMsg.getPayloadInfo(),
						respMsg.getNcInfo(), "fromResp");
			}
			this.getComponent().getSync().onCycleFinished();
		} else {
			throw new IllegalArgumentException(
					"Got a response message of an illegal type: "
							+ msg.getClass().getName());
		}
	}

	@Override
	public Object getResult() {
		return null;
	}

}
