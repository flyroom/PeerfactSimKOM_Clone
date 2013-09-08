package org.peerfact.impl.service.aggregation.centralizedmonitoring.server.tree;

import java.util.Collection;
import java.util.LinkedHashMap;

import org.peerfact.api.common.Host;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.impl.service.aggregation.centralizedmonitoring.message.CMonTreeRootUpdateMessage;
import org.peerfact.impl.service.aggregation.centralizedmonitoring.message.content.Aggregate;
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
public abstract class AbstractInnerNodeApplication<ID extends Object> extends
		AbstractTreeApplication {

	protected LinkedHashMap<TransInfo, Integer> childMessageCounter;

	protected LinkedHashMap<TransInfo, Collection<Aggregate<ID>>> status;

	public AbstractInnerNodeApplication(Host host) {
		super(host);
		this.childMessageCounter = new LinkedHashMap<TransInfo, Integer>();
		this.status = new LinkedHashMap<TransInfo, Collection<Aggregate<ID>>>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void messageArrived(TransMsgEvent receivingEvent) {
		if (receivingEvent.getPayload() instanceof CMonTreeRootUpdateMessage) {
			if (this.childMessageCounter.containsKey(receivingEvent
					.getSenderTransInfo())) {
				Integer counter = this.childMessageCounter.get(receivingEvent
						.getSenderTransInfo());
				this.childMessageCounter.put(receivingEvent
						.getSenderTransInfo(), ++counter);
			} else {
				this.childMessageCounter.put(receivingEvent
						.getSenderTransInfo(), 1);
			}
			this.storeMessage(receivingEvent
					.getSenderTransInfo(),
					(CMonTreeRootUpdateMessage<ID>) receivingEvent
							.getPayload());
			if (this.childMessageCounter.size() < this.numberOfChildren) {
				return;
			}
			for (Integer count : this.childMessageCounter.values()) {
				if (count < 1) {
					return;
				}
			}
			for (TransInfo child : this.childMessageCounter.keySet()) {
				Integer counter = this.childMessageCounter.get(child);
				counter--;
				this.childMessageCounter.put(child, counter);
			}
			this.sendUpdate(this.aggregateUpdate());
		}
	}

	protected LinkedHashMap<ID, Aggregate<ID>> aggregateUpdate() {
		LinkedHashMap<ID, Aggregate<ID>> all = new LinkedHashMap<ID, Aggregate<ID>>();
		for (Collection<Aggregate<ID>> clientAggregate : this.status.values()) {
			for (Aggregate<ID> aggregate : clientAggregate) {
				if (!all.containsKey(aggregate.getIdentifier())) {
					all.put(aggregate.getIdentifier(), new Aggregate<ID>(
							aggregate.getIdentifier()));
				}
				all.get(aggregate.getIdentifier()).add(aggregate);
			}
		}
		return all;
	}

	protected void storeMessage(TransInfo transInfo,
			CMonTreeRootUpdateMessage<ID> msg) {
		this.status.put(transInfo, msg.getUpdates());
	}

	protected abstract void sendUpdate(
			LinkedHashMap<ID, Aggregate<ID>> LinkedHashMap);
}
