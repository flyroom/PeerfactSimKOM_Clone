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

package org.peerfact.api.service.aggr;

import java.util.List;

import org.peerfact.api.common.Component;
import org.peerfact.api.common.OperationCallback;


/**
 * Given a set of floating-point numerical values, each associated with an
 * identifier, an aggregation service allows to render results of aggregate
 * functions of them at every node in the network, such as
 * <ul>
 * <li>minimum
 * <li>maximum
 * <li>average
 * <li>variance
 * <li>node count
 * </ul>
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public interface AggregationService<T extends Object> extends Component {

	/**
	 * Sets the local value identified by "identifier" to the given value.
	 * 
	 * @param identifier
	 *            , the identifier of the value to set
	 * @param value
	 *            , the new value
	 * @return the local value it was previously set to.
	 * @throws NoSuchValueException
	 */
	public double setLocalValue(Object identifier, double value)
			throws NoSuchValueException;

	/**
	 * Returns the local value identified by "identifier".
	 * 
	 * @param identifier
	 *            , the identifier of the value to get.
	 * @return the local value identified by "identifier".
	 * @throws NoSuchValueException
	 */
	public double getLocalValue(Object identifier) throws NoSuchValueException;

	/**
	 * Joins the network. For example, this may trigger a bootstrapping process.
	 * 
	 * @param cb
	 */
	public void join(OperationCallback<Object> cb);

	/**
	 * Leaves the network.
	 * 
	 * @param cb
	 */
	public void leave(OperationCallback<Object> cb);

	/**
	 * Starts an operation that will return an aggregation result when
	 * successfully finished.
	 * 
	 * @param identifier
	 *            , the identifier of the value for which an aggregation result
	 *            shall be returned.
	 * @param callback
	 *            , the operation callback that is called whenever the operation
	 *            finished.
	 * @return the identifier of the operation started.
	 * @throws NoSuchValueException
	 */
	public int getAggregationResult(Object identifier,
			OperationCallback<AggregationResult> callback)
			throws NoSuchValueException;

	public int getAggregationResultMap(
			OperationCallback<AggregationMap<T>> callback);

	/**
	 * Gets a list of identifiers, which are known in this service.
	 * 
	 * @return A list of identifiers.
	 */
	public List<Object> getIdentifiers();

	/**
	 * Gets the number of attributes that are currently monitored by the
	 * respective node. Besides meaningful attributes, this method also takes
	 * the dummy-attributes for benchmarking into consiration, which might not
	 * be reflected in the list of identifiers returned by
	 * <code>getIdentifiers()</code> depending on the impelementation of
	 * {@link AggregationService}.
	 * 
	 * @return number of monitored attributes (including dummy-ones)
	 */
	public int getNumberOfMonitoredAttributes();

	/**
	 * Gets the last stored {@link AggregationResult}.
	 * 
	 * @param identifier
	 *            The identifier of the value for which an aggregation result
	 *            shall be returned.
	 * @return The last {@link AggregationResult} for the given identifier. If
	 *         no aggregation stored for the given identifier, then return
	 *         <code>null</code>.
	 */
	public AggregationResult getStoredAggregationResult(Object identifier);

	/**
	 * Gets the receiving timestamp of the global aggregation to the given
	 * identifier at the service. <br>
	 * 
	 * @param identifier
	 *            The identifier for the global aggregation
	 * @return The receiving timestamp of the global aggregation in simulation
	 *         time.
	 */
	public long getGlobalAggregationReceivingTime(Object identifier);
}
