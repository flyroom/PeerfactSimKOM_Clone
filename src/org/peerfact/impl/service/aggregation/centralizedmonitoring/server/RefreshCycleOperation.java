package org.peerfact.impl.service.aggregation.centralizedmonitoring.server;

import org.peerfact.impl.common.AbstractOperation;
import org.peerfact.impl.service.aggregation.centralizedmonitoring.Configuration;

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
 * @author Alexander Nigl
 * 
 * @param <ID>
 *            class of identifier
 */
public class RefreshCycleOperation<ID extends Object> extends
		AbstractOperation<IRefresh<ID>, Boolean> {

	public RefreshCycleOperation(IRefresh<ID> component) {
		super(component);
		this.scheduleWithDelay(Configuration.SERVER_REFRESH_DELAY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void execute() {
		this.getComponent().refresh();
		this.scheduleWithDelay(Configuration.SERVER_REFRESH_DELAY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean getResult() {
		return null;
	}

}
