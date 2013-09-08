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

package org.peerfact.impl.service.aggregation.skyeye.metrics.operations;

import org.peerfact.api.common.Message;
import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.service.skyeye.SkyNetNodeInfo;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.api.transport.TransMessageCallback;
import org.peerfact.api.transport.TransProtocol;
import org.peerfact.impl.common.AbstractOperation;
import org.peerfact.impl.service.aggregation.skyeye.AbstractSkyNetNode;
import org.peerfact.impl.service.aggregation.skyeye.DHTParamterManipulator;
import org.peerfact.impl.service.aggregation.skyeye.metrics.MetricsEntry;
import org.peerfact.impl.service.aggregation.skyeye.metrics.MetricsInterpretation;
import org.peerfact.impl.service.aggregation.skyeye.metrics.messages.MetricUpdateACKMsg;
import org.peerfact.impl.service.aggregation.skyeye.metrics.messages.MetricUpdateMsg;

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
 * This class implements the operation of a metric-update. Within this operation
 * a {@link MetricUpdateMsg} containing the metric-entry is sent to a
 * Parent-Coordinator, which answers with a {@link MetricUpdateACKMsg}. Besides
 * the acknowledgment, the answer of a Parent-Coordinator can contain the latest
 * information of {@link MetricsInterpretation} and
 * {@link DHTParamterManipulator}. If no answer is received, the message is
 * retransmitted.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 05.12.2008
 * 
 */
public class MetricUpdateOperation extends
		AbstractOperation<AbstractSkyNetNode, MetricUpdateACKMsg> implements
		TransMessageCallback {

	private final MetricUpdateMsg request;

	private MetricUpdateACKMsg reply;

	private final SkyNetNodeInfo receiverInfo;

	private int retry;

	private int msgID = -2;

	public MetricUpdateOperation(AbstractSkyNetNode component,
			SkyNetNodeInfo senderInfo, SkyNetNodeInfo receiverInfo,
			MetricsEntry content, long skyNetMsgID,
			OperationCallback<MetricUpdateACKMsg> callback) {
		super(component, callback);
		request = new MetricUpdateMsg(senderInfo, receiverInfo, content,
				skyNetMsgID);
		this.receiverInfo = receiverInfo;
		retry = 0;
	}

	@Override
	protected void execute() {
		long ackTime = getComponent().getMetricUpdateStrategy().getTimeForACK();
		msgID = getComponent().getTransLayer().sendAndWait(request,
				receiverInfo.getTransInfo(), getComponent().getPort(),
				TransProtocol.UDP, this, ackTime);
		log.debug("Initiating transMessage with id " + msgID
				+ "-->SkyNetMsgID " + request.getSkyNetMsgID());
	}

	@Override
	public MetricUpdateACKMsg getResult() {
		return reply;
	}

	@Override
	public void messageTimeoutOccured(int commId) {
		log
				.info(retry
						+ ". MetricUpdateOperation failed @ "
						+ getComponent().getSkyNetNodeInfo().getTransInfo()
								.getNetId()
						+ " due to Message-timeout of transMessage with ID = "
						+ commId);
		if (retry < getComponent().getMetricUpdateStrategy()
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
		reply = (MetricUpdateACKMsg) msg;
		if (request.getSkyNetMsgID() == reply.getSkyNetMsgID()) {
			log.debug("TransMessage with ID = " + commId + " is received");

			// Set the observed level at the node
			int observedLevelFromRoot = reply.getHopsFromRoot();
			if (observedLevelFromRoot != -1) {
				getComponent().getSkyNetNodeInfo().setObservedLevelFromRoot(
						observedLevelFromRoot + 1);
			}

			operationFinished(true);
		} else {
			log
					.error("The SkyNetMsgID send does not equal the SkyNetMsgID received");
		}
	}

}
