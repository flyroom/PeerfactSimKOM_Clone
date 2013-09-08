package org.peerfact.impl.service.aggregation.centralizedmonitoring.message;

import java.util.Collection;
import java.util.LinkedList;

import org.peerfact.api.common.Message;
import org.peerfact.impl.service.aggregation.centralizedmonitoring.message.content.Attribute;
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
public class CMonInfoMessage<ID extends Object> extends AbstractTransMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7524796845452116281L;

	private Collection<Attribute<ID>> infos;

	/**
	 * This counter is used to simulate the amount of dummy-messages, that are
	 * transmitted within this message.
	 */
	private int dummyMetricsCounter;

	public CMonInfoMessage() {
		this.infos = new LinkedList<Attribute<ID>>();
		dummyMetricsCounter = 0;
	}

	public CMonInfoMessage(Collection<Attribute<ID>> infos) {
		this.infos = infos;
		dummyMetricsCounter = 0;
	}

	public void setDummyMetricsCounter(int dummyMetricsCounter) {
		this.dummyMetricsCounter = dummyMetricsCounter;
	}

	public int getDummyMetricsCounter() {
		return dummyMetricsCounter;
	}

	public void addInfo(Attribute<ID> info) {
		this.infos.add(info);
	}

	public Collection<Attribute<ID>> getInfos() {
		return this.infos;
	}

	public Collection<ID> getIdentifiers() {
		LinkedList<ID> ids = new LinkedList<ID>();
		for (Attribute<ID> info : this.infos) {
			ids.add(info.getIdentifier());
		}
		return ids;
	}

	@Override
	public Message getPayload() {
		return this;
	}

	@Override
	public long getSize() {
		long size = Long.SIZE / 8;
		long attSize = 0;
		boolean first = true;

		for (Attribute<?> info : this.infos) {
			if (first) {
				first = false;
				attSize = Attribute.getSize();
			}
			size += Attribute.getSize();
		}

		// add the amount of dummy messages
		size += dummyMetricsCounter * attSize;

		// calculate the size for AbstractTransMessage
		size += (Integer.SIZE + 2 * Short.SIZE) / 8;
		return size;
	}

}
