package org.peerfact.impl.service.aggregation.centralizedmonitoring.server.tree;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.peerfact.api.common.Host;
import org.peerfact.api.common.Message;
import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.api.transport.TransProtocol;
import org.peerfact.impl.service.aggregation.centralizedmonitoring.Configuration;
import org.peerfact.impl.service.aggregation.centralizedmonitoring.message.CMonInfoAck;
import org.peerfact.impl.service.aggregation.centralizedmonitoring.message.CMonInfoMessage;
import org.peerfact.impl.service.aggregation.centralizedmonitoring.message.CMonRequestMessage;
import org.peerfact.impl.service.aggregation.centralizedmonitoring.message.CMonResultMessage;
import org.peerfact.impl.service.aggregation.centralizedmonitoring.message.CMonTreeRootUpdateMessage;
import org.peerfact.impl.service.aggregation.centralizedmonitoring.message.content.Aggregate;
import org.peerfact.impl.service.aggregation.centralizedmonitoring.message.content.Attribute;
import org.peerfact.impl.service.aggregation.centralizedmonitoring.server.ClientInfo;
import org.peerfact.impl.service.aggregation.centralizedmonitoring.server.IRefresh;
import org.peerfact.impl.service.aggregation.centralizedmonitoring.server.RefreshCycleOperation;
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
 */
public abstract class AbstractLeafApplication<ID extends Object> extends
		AbstractTreeApplication implements IRefresh<ID> {

	protected LinkedHashMap<ID, Aggregate<ID>> currentGobalState;

	protected LinkedHashMap<TransInfo, ClientInfo<ID>> clients;

	public AbstractLeafApplication(Host host) {
		super(host);
		this.clients = new LinkedHashMap<TransInfo, ClientInfo<ID>>();
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void messageArrived(TransMsgEvent receivingEvent) {
		Message receivedMessage = receivingEvent.getPayload();
		if (receivedMessage instanceof CMonInfoMessage) {
			CMonInfoMessage<ID> msg = (CMonInfoMessage<ID>) receivedMessage;
			Collection<Attribute<ID>> infos = msg.getInfos();
			TransInfo client = receivingEvent.getSenderTransInfo();
			this.storeInfos(infos, client);
			this.getHost().getTransLayer().sendReply(new CMonInfoAck(),
					receivingEvent, Configuration.PEER_PORT, TransProtocol.UDP);
		} else if (receivedMessage instanceof CMonRequestMessage) {
			CMonRequestMessage<ID> msg = (CMonRequestMessage<ID>) receivedMessage;
			Collection<ID> ids = msg.getIds();
			LinkedHashMap<ID, Aggregate<ID>> results = this.gatherResult(ids);
			CMonResultMessage<ID> reply = new CMonResultMessage<ID>(results);
			this.host.getTransLayer().sendReply(reply, receivingEvent,
					Configuration.PEER_PORT, TransProtocol.UDP);
		} else if (receivedMessage instanceof CMonResultMessage) {
			this.currentGobalState = ((CMonResultMessage<ID>) receivedMessage)
					.getInfos();
		}
	}

	@Override
	public int start(OperationCallback<?> callback) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int close(OperationCallback<?> callback) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void init() {
		new RefreshCycleOperation<ID>(this);
		super.init();
	}

	protected void storeInfos(Collection<Attribute<ID>> infos, TransInfo client) {
		if (!this.clients.containsKey(client)) {
			this.clients.put(client, new ClientInfo<ID>());
		}
		ClientInfo<ID> clinfo = this.clients.get(client);
		for (Attribute<ID> attinfo : infos) {
			clinfo.put(attinfo.getIdentifier(), new Attribute<ID>(
					attinfo.getIdentifier(), attinfo.getValue()));
		}
	}

	@Override
	public void refresh() {
		LinkedHashMap<ID, Aggregate<ID>> result = new LinkedHashMap<ID, Aggregate<ID>>();
		for (Iterator<ClientInfo<ID>> i = this.clients.values().iterator(); i
				.hasNext();) {
			ClientInfo<ID> info = i.next();
			if (info.marked) {
				i.remove();
			} else {
				info.marked = true;
				for (Attribute<ID> att : info.values()) {
					if (!result.containsKey(att.getIdentifier())) {
						result.put(
								att.getIdentifier(),
								new Aggregate<ID>(att
										.getIdentifier()));
					}
					result.get(att.getIdentifier()).addValue(att.getValue(),
							att.getUpdateTime());
				}
			}
		}
		this.host.getTransLayer().send(
				new CMonTreeRootUpdateMessage<ID>(result.values()),
				this.parent,
				Configuration.SERVER_PORT, TransProtocol.UDP);
	}

	/**
	 * Returns a LinkedHashMap of Aggregates over all requested ids
	 * 
	 * @param ids
	 *            requested IDs
	 * @return LinkedHashMap of Aggregates
	 */
	protected abstract LinkedHashMap<ID, Aggregate<ID>> gatherResult(
			Collection<ID> ids);

}
