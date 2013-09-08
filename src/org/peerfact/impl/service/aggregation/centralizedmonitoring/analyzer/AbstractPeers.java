package org.peerfact.impl.service.aggregation.centralizedmonitoring.analyzer;

import java.util.Iterator;

import org.peerfact.api.common.Host;
import org.peerfact.api.overlay.OverlayNode;
import org.peerfact.impl.analyzer.csvevaluation.metrics.Metric;
import org.peerfact.impl.service.aggregation.centralizedmonitoring.peer.CMonPeerOverlayNode;
import org.peerfact.impl.util.oracle.GlobalOracle;


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
public abstract class AbstractPeers<T> implements Metric {

	@Override
	public String getMeasurementFor(long time) {
		IResultManager<T> result = this.getResultManager();
		for (Host host : GlobalOracle.getHosts()) {
			for (Iterator<OverlayNode<?, ?>> overlays = host.getOverlays(); overlays
					.hasNext();) {
				OverlayNode<?, ?> overlay = overlays.next();
				if (overlay instanceof CMonPeerOverlayNode) {
					result.addValue(this.getMeasurmentFor(
							(CMonPeerOverlayNode<?>) overlay, host));
				}
			}
		}
		return result.getResult();
	}

	protected abstract T getMeasurmentFor(CMonPeerOverlayNode<?> peer,
			Host host);

	protected abstract IResultManager<T> getResultManager();

}
