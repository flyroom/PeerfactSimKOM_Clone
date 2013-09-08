package org.peerfact.impl.service.aggregation.centralizedmonitoring.message;

import java.util.LinkedHashMap;

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
 */
public class CMonResultMessage<ID extends Object> extends AbstractTransMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7310913974586516335L;

	private LinkedHashMap<ID, Aggregate<ID>> infos;

	/**
	 * This counter is used to simulate the amount of dummy-messages, that are
	 * transmitted within this message.
	 */
	private int dummyMetricsCounter;

	public CMonResultMessage() {
		this(new LinkedHashMap<ID, Aggregate<ID>>());
		dummyMetricsCounter = 0;
	}

	public CMonResultMessage(LinkedHashMap<ID, Aggregate<ID>> infos) {
		this.infos = infos;
		dummyMetricsCounter = 0;
	}

	public void setDummyMetricsCounter(int dummyMetricsCounter) {
		this.dummyMetricsCounter = dummyMetricsCounter;
	}

	public int getDummyMetricsCounter() {
		return dummyMetricsCounter;
	}

	public LinkedHashMap<ID, Aggregate<ID>> getInfos() {
		return this.infos;
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
		long sum = 0;
		long attSize = 0;
		boolean first = true;

		for (Aggregate<ID> att : this.infos.values()) {
			if (first) {
				first = false;
				attSize = Aggregate.getSize();
			}
			sum += Aggregate.getSize();
		}

		// add the amount of dummy messages
		sum += dummyMetricsCounter * attSize;

		// calculate the size for AbstractTransMessage
		sum += (Integer.SIZE + 2 * Short.SIZE) / 8;
		return sum;
	}

}
