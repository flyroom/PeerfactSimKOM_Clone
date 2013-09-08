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

package org.peerfact.impl.service.aggregation.oracle;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.peerfact.api.common.Operation;
import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.service.aggr.AggregationMap;
import org.peerfact.api.service.aggr.AggregationResult;
import org.peerfact.api.service.aggr.AggregationService;
import org.peerfact.api.service.aggr.NoSuchValueException;
import org.peerfact.impl.application.AbstractApplication;
import org.peerfact.impl.common.Operations;


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
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public class AggregationServiceOracle extends AbstractApplication implements
		AggregationService<Object> {

	private OracleUniverse universe;

	Map<Object, Double> localVals;

	public AggregationServiceOracle(OracleUniverse universe) {
		this.universe = universe;
		localVals = new LinkedHashMap<Object, Double>();
		universe.add(this);
	}

	@Override
	public double setLocalValue(Object identifier, double value)
			throws NoSuchValueException {
		Double result = localVals.put(identifier, value);
		if (result == null) {
			return Double.NaN;
		}
		return result;
	}

	@Override
	public double getLocalValue(Object identifier) throws NoSuchValueException {
		Double result = localVals.get(identifier);
		if (result == null) {
			throw new NoSuchValueException(identifier);
		}
		return result;
	}

	@Override
	public void join(OperationCallback<Object> cb) {
		Operations.createEmptyOperation(this, cb).scheduleImmediately();
	}

	@Override
	public void leave(OperationCallback<Object> cb) {
		Operations.createEmptyOperation(this, cb).scheduleImmediately();
	}

	@Override
	public int getAggregationResult(Object identifier,
			OperationCallback<AggregationResult> callback)
			throws NoSuchValueException {
		Operation<AggregationResult> op = Operations
				.createEmptyOperationResult(this, callback,
						universe.getAggregationResult(identifier));
		op.scheduleImmediately();
		return op.getOperationID();
	}

	@Override
	public int getAggregationResultMap(
			OperationCallback<AggregationMap<Object>> callback) {
		// FIXME Implement me
		return 0;
	}

	@Override
	public List<Object> getIdentifiers() {
		List<Object> result = new Vector<Object>();
		if (localVals != null) {
			result.addAll(localVals.keySet());
		}
		return result;
	}

	@Override
	public AggregationResult getStoredAggregationResult(Object identifier) {
		return universe.getAggregationResult(identifier);
	}

	@Override
	public long getGlobalAggregationReceivingTime(Object identifier) {
		// is 0 because it will be derive at call.
		return 0;
	}

	@Override
	public int getNumberOfMonitoredAttributes() {
		return 0;
	}

}
