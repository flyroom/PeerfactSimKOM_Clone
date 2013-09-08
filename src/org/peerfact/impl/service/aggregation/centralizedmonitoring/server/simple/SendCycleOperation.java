package org.peerfact.impl.service.aggregation.centralizedmonitoring.server.simple;

import org.peerfact.impl.common.AbstractOperation;
import org.peerfact.impl.service.aggregation.centralizedmonitoring.Configuration;
import org.peerfact.impl.service.aggregation.centralizedmonitoring.server.ISend;

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
public class SendCycleOperation<ID> extends
		AbstractOperation<ISend<ID>, Boolean> {

	public SendCycleOperation(ISend<ID> component) {
		super(component);
		this.scheduleWithDelay(Configuration.SERVER_SEND_INTERVAL);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void execute() {
		this.getComponent().send();
		this.scheduleWithDelay(Configuration.SERVER_SEND_INTERVAL);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean getResult() {
		return true;
	}
}
