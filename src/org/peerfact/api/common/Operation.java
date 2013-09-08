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

package org.peerfact.api.common;

/**
 * This class realizes the <i>command pattern</i> for common operations in a
 * distributed system. Typically, if some action should take place in a
 * component of a host, the action will be represented by an operation object
 * providing the required functionality. Examples of operations could be:
 * JoinOperation, SearchOperation, DisconnectOperation etc.
 * <p/>
 * The basic operation provides the knowledge about its state: finished or not
 * and if finished - successful or not. The main functionality of this class is
 * its ability to be <i>scheduled</i> in the simulator.
 * <p>
 * Note that not all components are Operation-based. Components which work with
 * operations must implement the <code>SupportOperations</code> interface.
 * 
 * @author Sebastian Kaune <kaune@kom.tu-darmstadt.de>
 * @author Konstantin Pussep <pussep@kom.tu-darmstadt.de>
 * @version 3.0, 11/25/2007
 * @param <T>
 *            The result type of the operation. e.g. LookupOperation in a
 *            DHTNode should return a DHTValue.
 * 
 * @see OperationCallback
 * @see SupportOperations
 */
public interface Operation<T> {

	/**
	 * This method returns information whether a given operation was successful
	 * or not. If the operation has not finished yet it must return false.
	 * 
	 * @return returns <code>true</code> if this operation was successful.
	 */
	public boolean isSuccessful();

	/**
	 * This method returns information whether a given operation is finished or
	 * not regardless of whether it finished successfully or failed.
	 * 
	 * @return returns <code>true</code> if this operation is finished.
	 */
	public boolean isFinished();

	/**
	 * This method returns the duration of the whole operation.
	 * 
	 * @return returns the duration if this operation is finished, otherwise 0.
	 */
	public long getDuration();

	/**
	 * By constructing a new operation instance, each operation is assigned a
	 * globally unique operation identifier. That is, each operation can be
	 * differentiated by another one using this identifier. For debugging
	 * purposes the operation id is even unique among all hosts in the
	 * simulator.
	 * 
	 * @return the globally unique operation identifier
	 */
	public int getOperationID();

	/**
	 * Schedules the operation with the time delay of <b>zero</b> into the
	 * scheduler. The sense of this method is that the operations should be
	 * executed outside of the callers call context.
	 * 
	 */
	public void scheduleImmediately();

	/**
	 * This method schedules the operation relatively to the current simulation
	 * time by using an additional delay. That is, the scheduling time t is
	 * given by <code>t = currentTime + delay</code>.
	 * 
	 * @param delay
	 *            - relative (virtual) time delay after which the operation will
	 *            be executed
	 */
	public void scheduleWithDelay(long delay);

	/**
	 * This method schedules the operation at an absolute point in time
	 * irrespective of the current simulation time. More precisely the
	 * simulation will be executed at time
	 * <code>t = max(currentTime,executionTime)</code>
	 * 
	 * @param executionTime
	 *            the absolute point in time to execute this operation
	 */
	public void scheduleAtTime(long executionTime);

	/**
	 * Returns the component to which a specific operation object belongs to.
	 * 
	 * @return the component to which a specific operation object belongs to
	 */
	public SupportOperations getComponent();

	/**
	 * The result of the operation, which is only not null if the operation
	 * finished successfully.
	 * 
	 * @return operation result
	 */
	public T getResult();
}
