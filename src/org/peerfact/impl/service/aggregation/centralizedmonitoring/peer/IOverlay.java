package org.peerfact.impl.service.aggregation.centralizedmonitoring.peer;

import org.peerfact.api.common.ConnectivityListener;
import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.api.overlay.OverlayID;
import org.peerfact.api.overlay.OverlayNode;
import org.peerfact.api.service.aggr.AggregationService;

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
public interface IOverlay<T extends OverlayID<?>, S extends OverlayContact<T>, ID extends Object>
		extends OverlayNode<T, S>, ConnectivityListener,
		AggregationService<Object> {
	// No implementation here (not sure why) by Thim
	// This text is here so that later on someone will find this.
}
