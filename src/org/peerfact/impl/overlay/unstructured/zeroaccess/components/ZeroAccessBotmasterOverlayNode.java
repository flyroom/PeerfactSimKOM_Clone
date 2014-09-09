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

package org.peerfact.impl.overlay.unstructured.zeroaccess.components;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.peerfact.api.common.ConnectivityEvent;
import org.peerfact.api.common.Message;
import org.peerfact.api.common.Operation;
import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.network.Bandwidth;
import org.peerfact.api.network.NetLayer;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.api.transport.TransLayer;
import org.peerfact.impl.network.gnp.GnpNetLayer;
import org.peerfact.impl.overlay.unstructured.zeroaccess.message.BaseMessage;
import org.peerfact.impl.overlay.unstructured.zeroaccess.message.GetLMessage;
import org.peerfact.impl.overlay.unstructured.zeroaccess.message.RetLMessage;
import org.peerfact.impl.overlay.unstructured.zeroaccess.operation.GetLOperation;
import org.peerfact.impl.overlay.unstructured.zeroaccess.operation.RetLOperation;
import org.peerfact.impl.overlay.unstructured.zeroaccess.operation.ScheduleBotSoftwareUpdateOperation;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.transport.TransMsgEvent;

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
public class ZeroAccessBotmasterOverlayNode extends ZeroAccessOverlayNode {

	final public Logger log = Logger
			.getLogger(ZeroAccessBotmasterOverlayNode.class);

	private TransLayer transLayer;

	private int getL_round_robin_counter = 0;

	private long last_route_update_time = 0;

	private NetLayer netLayer;

	public ZeroAccessBotmasterOverlayNode(NetLayer netLayer,
			TransLayer transLayer,
			ZeroAccessOverlayID peerId, short port, long downBandwidth,
			long upBandwidth) {
		super(netLayer, transLayer, peerId, port, downBandwidth, upBandwidth,
				"true");
		this.netLayer = netLayer;
		this.transLayer = transLayer;

		this.netLayer = netLayer;
		if (this.netLayer instanceof GnpNetLayer)
		{
			Bandwidth currentBandwidth = new Bandwidth(downBandwidth,
					upBandwidth);
			((GnpNetLayer) this.netLayer).setCurrentBandwidth(currentBandwidth);
		}

		transLayer.addTransMsgListener(this, this.getPort());
	}

	@Override
	public TransLayer getTransLayer() {
		return transLayer;
	}

	@Override
	public void connectivityChanged(ConnectivityEvent ce) {

	}

	@Override
	public void messageTimeoutOccured(int commId) {
		//
	}

	@Override
	public void receive(Message msg, TransInfo senderInfo, int commId) {
		//
	}

	@Override
	public void messageArrived(TransMsgEvent receivingEvent) {
		Message message = receivingEvent.getPayload();

		if (isActive()) {
			// accept messages only if connection to peer exists
			if (message instanceof BaseMessage) {
				if (message instanceof GetLMessage) {
					this.processGetL(receivingEvent);
				} else if (message instanceof RetLMessage) {
					this.processRetL(receivingEvent);
				}
			}
		}
	}

	private void processGetL(TransMsgEvent receivingEvent) {

		GetLMessage getLMessage = (GetLMessage) receivingEvent
				.getPayload();

		ZeroAccessOverlayContact source_contact = (ZeroAccessOverlayContact) getLMessage
				.getContact();
		if (source_contact.getOverlayID() == this.getOverlayID()) {
			return;
		}

		LinkedList<ZeroAccessOverlayContact> latestContacts = this
				.getZeroAccessRoutingTable().getLatestContacts(16);

		source_contact.refresh();

		boolean sendGetLRequest = false;

		if (this.getZeroAccessRoutingTable().getContact(
				source_contact.getOverlayID()) == null) {
			this.getZeroAccessRoutingTable().addContact(source_contact);
			sendGetLRequest = true;
		} else {
			sendGetLRequest = false;
		}

		if (getLMessage.isRecheck())
		{
			sendGetLRequest = false;
		}

		RetLOperation retLOperation = new RetLOperation(this,
				source_contact.getTransInfo(), latestContacts,
				this.getBot_software_version(),
				new OperationCallback<Object>() {
					@Override
					public void calledOperationFailed(
							Operation<Object> op) {
						//
					}

					@Override
					public void calledOperationSucceeded(
							Operation<Object> op) {
						//
					}
				});
		retLOperation.scheduleImmediately();

		if (sendGetLRequest)
		{
			GetLOperation getLOperation = new GetLOperation(this,
					source_contact.getTransInfo(),
					new OperationCallback<Object>() {
						@Override
						public void calledOperationFailed(
								Operation<Object> op) {
							//
						}

						@Override
						public void calledOperationSucceeded(
								Operation<Object> op) {
							//
						}
					}, true, this.bot_software_version);
			getLOperation.scheduleWithDelay(2000);

		}
		if (this.getOverlayID().toString().equals("10"))
		{
			String current_time = Simulator.getSimulatedRealtime();
			// log.warn("Current Time " + current_time);

		}
	}

	private void processRetL(TransMsgEvent receivingEvent) {

		last_route_update_time = Simulator.getCurrentTime();

		RetLMessage retLMessage = (RetLMessage) receivingEvent
				.getPayload();

		LinkedList<ZeroAccessOverlayContact> contact_list = retLMessage
				.getContacts();

		for (int i = 0; i < contact_list.size(); i++)
		{
			((ZeroAccessOverlayRoutingTable) this.routingTable)
					.addContact(contact_list.get(i));
		}
	}

	public void startScheduleGetL(long delay) {
		TransInfo bootstrapInfo = null;
		List<TransInfo> bootstrapInfos = null;
		int count = 1;

		if (this.getZeroAccessRoutingTable()
				.numberOfContacts() == 0) {
			bootstrapInfos = ZeroAccessBootstrapManager
					.getInstance().getBootstrapInfo();
		}
		else
		{
			List<ZeroAccessOverlayContact> contactList = (List<ZeroAccessOverlayContact>) this.routingTable
					.allContacts();
			bootstrapInfos = new LinkedList<TransInfo>();
			for (int i = 0; i < contactList.size(); i++)
			{
				ZeroAccessOverlayContact znode = contactList.get(i);
				bootstrapInfos.add(znode.getTransInfo());
			}
		}

		if (getL_round_robin_counter >= bootstrapInfos.size()) {
			getL_round_robin_counter = 0;
		}

		bootstrapInfo = bootstrapInfos
				.get(getL_round_robin_counter);

		if (bootstrapInfo.equals(this.getTransLayer()
				.getLocalTransInfo(this.getPort()))) {
			if (bootstrapInfos.size() == 1) {
				return;
			}
			else
			{
				getL_round_robin_counter = (getL_round_robin_counter + 1)
						% bootstrapInfos.size();
				bootstrapInfo = bootstrapInfos
						.get(getL_round_robin_counter);
				log.debug("could not send request to itself, turn to next one in bootstrap list with size: "
						+ bootstrapInfos.size() + " current index: "
						+ getL_round_robin_counter);
			}
		} else {
			getL_round_robin_counter = (getL_round_robin_counter + 1)
					% bootstrapInfos.size();
		}

		GetLOperation getLOperation = new GetLOperation(this,
				bootstrapInfo, this.bot_software_version,
				new OperationCallback<Object>() {
					@Override
					public void calledOperationFailed(
							Operation<Object> op) {
						//
					}

					@Override
					public void calledOperationSucceeded(
							Operation<Object> op) {
						//
					}
				});
		getLOperation.scheduleImmediately();
	}

	public void upgradeBotSoftwarePackage(
			ScheduleBotSoftwareUpdateOperation operation) {
		this.setBot_software_version(this.getBot_software_version() + 1);
	}

	public void startScheduleSoftwareUpdates(long delay) {

		ScheduleBotSoftwareUpdateOperation scheduleUpdateOperation = new ScheduleBotSoftwareUpdateOperation(
				this, delay, new OperationCallback<Object>() {
					@Override
					public void calledOperationFailed(Operation<Object> op) {
						//
					}

					@Override
					public void calledOperationSucceeded(Operation<Object> op) {
						//
					}
				});
		scheduleUpdateOperation.scheduleWithDelay((long) (Simulator
				.getRandom().nextDouble() * BigInteger.valueOf(delay)
				.doubleValue()));
	}

	@Override
	public String toString() {
		return this.getOverlayID().toString();
	}
}
