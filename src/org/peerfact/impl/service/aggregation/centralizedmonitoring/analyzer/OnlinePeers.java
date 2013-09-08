package org.peerfact.impl.service.aggregation.centralizedmonitoring.analyzer;

import org.peerfact.api.common.Host;
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
 */
public class OnlinePeers extends AbstractPeers<Long> {

	@Override
	public String getName() {
		return "OnlinePeers";
	}

	@Override
	public Long getMeasurmentFor(CMonPeerOverlayNode<?> peer, Host host) {
		return host.getNetLayer().isOnline() ? Long.valueOf(1) : Long
				.valueOf(0);
	}

	@Override
	protected IResultManager<Long> getResultManager() {
		return new Result();
	}

	static class Result implements IResultManager<Long> {

		Long online = Long.valueOf(0);

		@Override
		public void addValue(Long val) {
			this.online += val;
		}

		@Override
		public String getResult() {
			return this.online.toString();
		}

	}

}
