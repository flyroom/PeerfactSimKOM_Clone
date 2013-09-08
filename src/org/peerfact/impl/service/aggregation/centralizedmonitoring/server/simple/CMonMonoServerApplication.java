package org.peerfact.impl.service.aggregation.centralizedmonitoring.server.simple;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.peerfact.api.common.Host;
import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.api.transport.TransMessageListener;
import org.peerfact.api.transport.TransProtocol;
import org.peerfact.impl.common.Operations;
import org.peerfact.impl.service.aggregation.centralizedmonitoring.Configuration;
import org.peerfact.impl.service.aggregation.centralizedmonitoring.message.CMonInfoAck;
import org.peerfact.impl.service.aggregation.centralizedmonitoring.message.CMonInfoMessage;
import org.peerfact.impl.service.aggregation.centralizedmonitoring.message.CMonRequestMessage;
import org.peerfact.impl.service.aggregation.centralizedmonitoring.message.CMonResultMessage;
import org.peerfact.impl.service.aggregation.centralizedmonitoring.message.content.Aggregate;
import org.peerfact.impl.service.aggregation.centralizedmonitoring.message.content.Attribute;
import org.peerfact.impl.service.aggregation.centralizedmonitoring.server.BootstrapInfo;
import org.peerfact.impl.service.aggregation.centralizedmonitoring.server.ClientInfo;
import org.peerfact.impl.service.aggregation.centralizedmonitoring.server.IRefresh;
import org.peerfact.impl.service.aggregation.centralizedmonitoring.server.ISend;
import org.peerfact.impl.service.aggregation.centralizedmonitoring.server.RefreshCycleOperation;
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
 * @author Alexander Nigl
 * 
 * @param <ID>
 *            class of identifier
 */
public class CMonMonoServerApplication<ID extends Object> implements
		org.peerfact.api.application.Application, TransMessageListener,
		IRefresh<ID>, ISend<ID> {

	private LinkedHashMap<TransInfo, ClientInfo<ID>> clients;

	private Set<ID> ids;

	private int dummyMetricsCounter;

	protected static final Logger log = SimLogger
			.getLogger(CMonMonoServerApplication.class);

	private Host host;

	/**
	 * Instantiate the server application.
	 * 
	 * @param host
	 *            host of the application
	 * @param port
	 *            port the server should listen to
	 */
	public CMonMonoServerApplication(Host host, short port) {
		this.host = host;
		this.clients = new LinkedHashMap<TransInfo, ClientInfo<ID>>();
		this.ids = new LinkedHashSet<ID>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int close(OperationCallback<?> callback) {
		BootstrapInfo.delServer(this.host.getTransLayer().getLocalTransInfo(
				Configuration.SERVER_PORT));
		this.host.getTransLayer().removeTransMsgListener(this,
				Configuration.SERVER_PORT);
		return Operations.scheduleEmptyOperation(this, callback);
	}

	/**
	 * Initialize server application, starts to listen to port
	 */
	public void init() {
		this.host.getTransLayer().addTransMsgListener(this,
				Configuration.SERVER_PORT);
		new RefreshCycleOperation<ID>(this);
		new SendCycleOperation<ID>(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int start(OperationCallback<?> callback) {
		return Operations.scheduleEmptyOperation(this, callback);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Host getHost() {
		return this.host;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void messageArrived(TransMsgEvent receivingEvent) {
		if (receivingEvent.getPayload() instanceof CMonInfoMessage) {
			CMonInfoMessage<ID> msg = (CMonInfoMessage<ID>) receivingEvent
					.getPayload();
			Collection<Attribute<ID>> infos = msg.getInfos();
			TransInfo client = receivingEvent.getSenderTransInfo();
			this.storeInfos(infos, client);
			this.host.getTransLayer().sendReply(new CMonInfoAck(),
					receivingEvent, Configuration.PEER_PORT, TransProtocol.UDP);
		} else if (receivingEvent.getPayload() instanceof CMonRequestMessage) {
			CMonRequestMessage<ID> msg = (CMonRequestMessage<ID>) receivingEvent
					.getPayload();
			Collection<ID> iDs = msg.getIds();
			CMonResultMessage<ID> reply = new CMonResultMessage<ID>(
					this.gatherResult(iDs));
			reply.setDummyMetricsCounter(dummyMetricsCounter);
			this.host.getTransLayer().sendReply(reply, receivingEvent,
					Configuration.PEER_PORT, TransProtocol.UDP);
		}
	}

	private LinkedHashMap<ID, Aggregate<ID>> gatherResult(Collection<ID> iDs) {
		LinkedHashMap<ID, Aggregate<ID>> results = new LinkedHashMap<ID, Aggregate<ID>>();
		for (ID id : iDs) {
			Aggregate<ID> answer = new Aggregate<ID>(id);
			for (ClientInfo<ID> info : this.clients.values()) {
				answer.addValue(info.get(id).getValue(), info.get(id)
						.getUpdateTime());
			}
			results.put(id, answer);
		}
		return results;
	}

	private void storeInfos(Collection<Attribute<ID>> infos, TransInfo client) {
		if (!this.clients.containsKey(client)) {
			this.clients.put(client, new ClientInfo<ID>());
		}
		ClientInfo<ID> clinfo = this.clients.get(client);
		for (Attribute<ID> attinfo : infos) {
			this.ids.add(attinfo.getIdentifier());
			clinfo.put(
					attinfo.getIdentifier(),
					new Attribute<ID>(attinfo.getIdentifier(), attinfo
							.getValue()));

		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setHost(Host host) {
		this.host = host;
	}

	/**
	 * Removes old entries from client list
	 */
	@Override
	public void refresh() {
		for (Iterator<ClientInfo<ID>> i = this.clients.values().iterator(); i
				.hasNext();) {
			ClientInfo<ID> info = i.next();
			if (info.marked) {
				i.remove();
			} else {
				info.marked = true;
			}
		}
	}

	@Override
	public void send() {
		CMonResultMessage<ID> reply = new CMonResultMessage<ID>(
				this.gatherResult(this.ids));
		reply.setDummyMetricsCounter(dummyMetricsCounter);
		for (TransInfo client : this.clients.keySet()) {
			log.debug("-->Sending message of size " + ids.size() + " to peer "
					+ client.getNetId());
			this.host.getTransLayer().send(reply, client,
					Configuration.PEER_PORT, TransProtocol.UDP);
		}

	}

	public void incrementDummyMetrics() {
		dummyMetricsCounter++;
	}

	public void increaseDummyMetrics(int amount) {
		dummyMetricsCounter += amount;
	}

	public int getDummyMetricsCounter() {
		return dummyMetricsCounter;
	}
}
