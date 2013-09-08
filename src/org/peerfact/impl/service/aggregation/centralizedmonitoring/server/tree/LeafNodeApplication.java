package org.peerfact.impl.service.aggregation.centralizedmonitoring.server.tree;

import java.util.Collection;
import java.util.LinkedHashMap;

import org.peerfact.api.common.Host;
import org.peerfact.impl.service.aggregation.centralizedmonitoring.message.content.Aggregate;


// TODO: There might be the possibility to get more accurate results by additionally using local information  
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
public class LeafNodeApplication<ID extends Object> extends
		AbstractLeafApplication<ID> {

	public LeafNodeApplication(Host host) {
		super(host);
	}

	@Override
	protected LinkedHashMap<ID, Aggregate<ID>> gatherResult(Collection<ID> ids) {
		LinkedHashMap<ID, Aggregate<ID>> result = new LinkedHashMap<ID, Aggregate<ID>>();
		for (ID id : ids) {
			result.put(id, this.currentGobalState.get(id));
		}
		return result;
	}
}
