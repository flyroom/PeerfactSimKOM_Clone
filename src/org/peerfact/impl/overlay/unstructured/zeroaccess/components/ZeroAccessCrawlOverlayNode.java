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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

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
import org.peerfact.impl.overlay.unstructured.zeroaccess.operation.SchedulePoisonOperation;
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
public class ZeroAccessCrawlOverlayNode extends ZeroAccessOverlayNode {

	final public Logger log = Logger
			.getLogger(ZeroAccessCrawlOverlayNode.class);

	private TransLayer transLayer;

	private ConcurrentHashMap<ZeroAccessOverlayID, ZeroAccessOverlayContact> nodesMap = new ConcurrentHashMap<ZeroAccessOverlayID, ZeroAccessOverlayContact>();

	private LinkedList<ZeroAccessOverlayContact> unProbedNodeList = new LinkedList<ZeroAccessOverlayContact>();

	private ArrayList<ZeroAccessOverlayContact> fakeNodeList = new ArrayList<ZeroAccessOverlayContact>();

	private long crawl_start_time_int = 0;

	private String crawl_start_time = null;

	private String crawl_end_time = null;

	private boolean crawling = false;

	private double poison_level = 0;

	private boolean poison_permitted = false;

	private boolean poisoning = false;

	private long rand_count = 0;

	private NetLayer netLayer;

	public ZeroAccessCrawlOverlayNode(NetLayer netLayer, TransLayer transLayer,
			ZeroAccessOverlayID peerId, short port, long downloadBandwidth,
			long upBandwidth, String double_poison_level) {
		super(netLayer, transLayer, peerId, port, downloadBandwidth,
				upBandwidth, "0");

		this.transLayer = transLayer;
		transLayer.addTransMsgListener(this, this.getPort());

		this.netLayer = netLayer;
		if (this.netLayer instanceof GnpNetLayer)
		{
			Bandwidth currentBandwidth = new Bandwidth(downloadBandwidth,
					upBandwidth);
			((GnpNetLayer) this.netLayer).setCurrentBandwidth(currentBandwidth);
		}

		this.routingTable = new ZeroAccessOverlayRoutingTable(peerId);
		if (double_poison_level.equals("-1"))
		{
			poison_permitted = false;
		}
		else
		{
			poison_level = Double.parseDouble(double_poison_level);
			poison_permitted = true;
		}
	}

	@Override
	public TransLayer getTransLayer() {
		return transLayer;
	}

	@Override
	public void connectivityChanged(ConnectivityEvent ce) {
		//
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
					processGetL(receivingEvent);
				} else if (message instanceof RetLMessage) {
					processRetL(receivingEvent);
				}
			}
		}
	}

	private void processGetL(TransMsgEvent receivingEvent) {
		int i = 1;
		i++;
	}

	private void processRetL(TransMsgEvent receivingEvent) {
		if (!crawling) {
			return;
		}
		RetLMessage retLMessage = (RetLMessage) receivingEvent
				.getPayload();
		if (nodesMap.containsKey(retLMessage.getSender()))
		{
			ZeroAccessOverlayContact node_sender = nodesMap.get(retLMessage
					.getSender());
			node_sender.setBool_live(true);

		}
		LinkedList<ZeroAccessOverlayContact> contact_list = retLMessage
				.getContacts();

		for (int i = 0; i < contact_list.size(); i++)
		{
			ZeroAccessOverlayContact node = contact_list.get(i);
			if (!nodesMap.containsKey(node.getOverlayID()))
			{
				if (nodesMap.putIfAbsent(node.getOverlayID(), node) == null)
				{
					unProbedNodeList.push(node);
					if (nodesMap.size() > 0 && nodesMap.size() % 100 == 0) {
						String current_time = Simulator.getSimulatedRealtime();
						log.warn(current_time + "Current size of crawling: "
								+ nodesMap.size());
					}
				}
			}
			else
			{
				ZeroAccessOverlayContact node_returned = nodesMap.get(node
						.getOverlayID());
				node_returned.setIndegree(node_returned.getIndegree() + 1);
				if (node_returned.getIndegree() > 1)
				{
					int m = 0;
					m++;
				}
			}
		}
		long current_time = Simulator.getCurrentTime();
		long duration = (current_time - crawl_start_time_int) / 1000000;

		if (nodesMap.size() < ZeroAccessBootstrapManager.getInstance()
				.getSize() && duration < 300) {
			scheduleCrawl();
		}

	}

	public void initForCrawl()
	{
		crawling = true;
		crawl_start_time = Simulator.getSimulatedRealtime();
		crawl_start_time_int = Simulator.getCurrentTime();
		List<TransInfo> bootstrapInfos = ZeroAccessBootstrapManager
				.getInstance().getBootstrapInfo();
		for (int i = 0; i < bootstrapInfos.size(); i++)
		{
			GetLOperation getLOperation = new GetLOperation(this,
					bootstrapInfos.get(i), this.bot_software_version,
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
	}

	public void initFakeNodePool()
	{
		long id_upper_limit = ZeroAccessBootstrapManager.getInstance()
				.getSize();
		long fake_node_count = 10000;
		for (long i = 1; i < fake_node_count; i++)
		{
			// IPv4NetID nextId = new IPv4NetID(i);
			ZeroAccessOverlayID random_id = new ZeroAccessOverlayID(
					BigInteger.valueOf(i + id_upper_limit));

			ZeroAccessOverlayContact contact = new ZeroAccessOverlayContact(
					random_id, this.getTransLayer()
							.getLocalTransInfo((short) 8964));
			fakeNodeList.add(contact);
		}
	}

	private LinkedList<ZeroAccessOverlayContact> generateRandomFakeNodeList(
			int num)
	{
		LinkedList<ZeroAccessOverlayContact> resultList = new LinkedList<ZeroAccessOverlayContact>();

		Random rand = new Random(Simulator.getCurrentTime() + rand_count);
		rand_count += 1;
		for (long i = 0; i < num; i++)
		{
			long select_index = rand.nextInt(Integer.MAX_VALUE)
					% fakeNodeList.size();

			ZeroAccessOverlayContact selected_contact = fakeNodeList
					.get((int) select_index);
			resultList.add(selected_contact);
		}
		return resultList;
	}

	public void schedulePoisonRoute(SchedulePoisonOperation scheduleOperation) {

		if (this.poisoning != true) {
			return;
		}

		Iterator<Map.Entry<ZeroAccessOverlayID, ZeroAccessOverlayContact>> iterator;
		iterator = nodesMap.entrySet().iterator();
		int size = nodesMap.size();
		long attack_limit = (size);
		long count = 0;
		for (Map.Entry<ZeroAccessOverlayID, ZeroAccessOverlayContact> entry : nodesMap
				.entrySet())
		{
			if (count > attack_limit) {
				break;
			}
			ZeroAccessOverlayContact target_contact = entry.getValue();

			// if indegree of target node's is below poison_level or status is
			// dead, then we will not poison
			if (target_contact.getIndegree() < poison_level
					|| !target_contact.isBool_live())
			{
				continue;
			}

			int poison_count = 16;

			for (int i = 0; i < poison_count; i++)
			{
				LinkedList<ZeroAccessOverlayContact> fakeContacts = generateRandomFakeNodeList(16);
				RetLOperation retLOperation = new RetLOperation(this,
						target_contact.getTransInfo(), fakeContacts,
						this.bot_software_version,
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
				retLOperation.scheduleWithDelay(10);
				// retLOperation.scheduleImmediately();
			}
			count += 1;
		}
	}

	public void startSchedulePoisonRoute(long delay) {
		if (poison_permitted != true) {
			return;
		}
		poisoning = true;
		SchedulePoisonOperation scheduleGetLOperation = new SchedulePoisonOperation(
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
		scheduleGetLOperation.scheduleWithDelay((long) (Simulator
				.getRandom().nextDouble() * BigInteger.valueOf(delay)
				.doubleValue()));
	}

	public void stopPoisonRoute()
	{
		this.poisoning = false;
		log.warn(Simulator.getSimulatedRealtime() + " Poisoning Stopped");
	}

	public void resetCrawl()
	{
		crawling = false;
		nodesMap.clear();
		unProbedNodeList.clear();
	}

	public void stopCrawl()
	{
		if (crawling)
		{
			crawl_end_time = Simulator.getSimulatedRealtime();
			log.warn("Crawling Finished from node " + this.toString()
					+ " with size " + nodesMap.size() + " from "
					+ crawl_start_time + " to " + crawl_end_time);
			crawling = false;

			for (int i = 1; i <= ZeroAccessBootstrapManager.getInstance()
					.getSize(); i++)
			{
				ZeroAccessOverlayID search_id = new ZeroAccessOverlayID(
						BigInteger.valueOf(i));
				if (!nodesMap.containsKey(search_id))
				{
					log.info("Lost Node " + search_id.toString());
				}
			}
		}
	}

	public void scheduleCrawl() {
		while (!unProbedNodeList.isEmpty())
		{
			ZeroAccessOverlayContact node = unProbedNodeList.pop();
			if (node.getOverlayID() == this.getOverlayID()) {
				continue;
			}
			GetLOperation getLOperation = new GetLOperation(this,
					node.getTransInfo(), this.bot_software_version,
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

	}

	@Override
	public String toString() {
		return this.getOverlayID().toString();
	}
}
