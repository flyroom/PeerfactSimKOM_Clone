package org.peerfact.impl.service.aggregation.centralizedmonitoring.peer.operation;

import org.peerfact.impl.common.AbstractOperation;
import org.peerfact.impl.service.aggregation.centralizedmonitoring.peer.CMonPeerOverlayNode;

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
 * Event to aggregate and send attributes to the server.
 * 
 * @param <ID>
 *            class of identifier
 */
public class PeriodicalySendAttributes<ID extends Object> extends
		AbstractOperation<CMonPeerOverlayNode<ID>, Boolean> {

	private boolean stopped;

	/**
	 * Schedules the event and takes care of possible connectivity changes
	 * 
	 * @param component
	 *            Owner Component
	 */
	public PeriodicalySendAttributes(CMonPeerOverlayNode<ID> component) {
		super(component);
		this.stopped = false;
		this.scheduleWithDelay(component.getDelay());
	}

	@Override
	protected void execute() {
		if (this.stopped) {
			return;
		}

		new SendAttribute<ID>(this.getComponent()).scheduleImmediately();
		this.scheduleWithDelay(this.getComponent().getDelay());
	}

	@Override
	public Boolean getResult() {
		return true; // FIXME Change to a meaningful result.
	}

	public void stop() {
		this.stopped = true;
	}
}
