package org.peerfact.impl.service.aggregation.centralizedmonitoring.message;

import java.util.Collection;
import java.util.LinkedList;

import org.peerfact.api.common.Message;
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
 */
public class CMonRequestMessage<ID extends Object> extends AbstractTransMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8292300367314306164L;

	private Collection<ID> ids;

	public CMonRequestMessage() {
		this(new LinkedList<ID>());
	}

	public CMonRequestMessage(Collection<ID> ids) {
		this.ids = ids;
	}

	public void addID(ID id) {
		ids.add(id);
	}

	public void setIds(Collection<ID> ids) {
		this.ids = ids;
	}

	public Collection<ID> getIds() {
		return ids;
	}

	@Override
	public Message getPayload() {
		return null;
	}

	@Override
	public long getSize() {
		// calculate the size for AbstractTransMessage
		long size = (Integer.SIZE + 2 * Short.SIZE) / 8;
		size = ids.size() * (Integer.SIZE / 8);
		return size;
	}

}
