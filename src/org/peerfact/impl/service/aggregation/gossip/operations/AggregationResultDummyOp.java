/*
 * Copyright (c) 2012-2013 Open Source Community - <http://www.peerfact.org>
 * Copyright (c) 2011-2012 University of Paderborn - UPB
 * Copyright (c) 2005-2011 KOM - Multimedia Communications Lab
 *
 * This file is part of PeerfactSim.KOM.
 * 
 * PeerfactSim.KOM is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 * 
 * PeerfactSim.KOM is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with PeerfactSim.KOM.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.peerfact.impl.service.aggregation.gossip.operations;

import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.service.aggr.AggregationResult;
import org.peerfact.impl.common.AbstractOperation;
import org.peerfact.impl.service.aggregation.gossip.GossipingAggregationService;

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
 * Operation that immediately finishes and returns the aggregation result given
 * in the constructor.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class AggregationResultDummyOp extends
		AbstractOperation<GossipingAggregationService, AggregationResult> {

	private AggregationResult result;

	/**
	 * Default constructor
	 * 
	 * @param component
	 *            , the component that executes the operation
	 * @param callback
	 *            , the operation callback that will be called when this
	 *            operation finishes, i.e. immediately.
	 * @param result
	 *            , the result that shall be handed over to the callback.
	 */
	public AggregationResultDummyOp(GossipingAggregationService component,
			OperationCallback<AggregationResult> callback,
			AggregationResult result) {
		super(component, callback);
		this.result = result;
	}

	@Override
	protected void execute() {
		this.operationFinished(true);
	}

	@Override
	public AggregationResult getResult() {
		return result;
	}

}
