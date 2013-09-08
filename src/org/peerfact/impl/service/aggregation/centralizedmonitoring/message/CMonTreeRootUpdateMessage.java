package org.peerfact.impl.service.aggregation.centralizedmonitoring.message;

import java.util.Collection;
import java.util.LinkedList;

import org.peerfact.api.common.Message;
import org.peerfact.impl.service.aggregation.centralizedmonitoring.message.content.Aggregate;
import org.peerfact.impl.transport.AbstractTransMessage;


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
 * Update message, used to transport information updates upwards the tree
 * 
 * @author Alexander Nigl
 * @param <ID>
 *            class of identifer
 * 
 */
public class CMonTreeRootUpdateMessage<ID extends Object> extends
		AbstractTransMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8823531269124313727L;

	protected Collection<Aggregate<ID>> updates;

	public CMonTreeRootUpdateMessage() {
		this(new LinkedList<Aggregate<ID>>());
	}

	public CMonTreeRootUpdateMessage(
			Collection<Aggregate<ID>> aggregateCollection) {
		this.updates = aggregateCollection;
	}

	/**
	 * Returns a collection of updates
	 * 
	 * @return collection of updates
	 */
	public Collection<Aggregate<ID>> getUpdates() {
		return this.updates;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Message getPayload() {
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getSize() {

		return this.updates.size();
	}

}
