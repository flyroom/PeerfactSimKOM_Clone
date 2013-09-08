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

package org.peerfact.impl.common;

import org.apache.log4j.Logger;
import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.common.SupportOperations;
import org.peerfact.api.simengine.SimulationEventHandler;
import org.peerfact.impl.simengine.SimulationEvent;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.util.logging.SimLogger;


/**
 * Both application and overlay operations should be subclasses of this class.
 * 
 * @author Sebastian Kaune <kaune@kom.tu-darmstadt.de>
 * @author Konstantin Pussep <pussep@kom.tu-darmstadt.de>
 * @version 1.0, 11/25/2007
 * @param <T>
 *            The exact type of the owner of this application
 * @param <S>
 *            The exact type of the application result
 * 
 */
public abstract class AbstractOperation<T extends SupportOperations, S extends Object>
		extends AbstractOperationCounter<S> implements SimulationEventHandler {

	private boolean error = false;

	private boolean finished = false;

	private OperationCallback<S> caller;

	private long startTime;

	private long endTime;

	protected static Logger log = SimLogger.getLogger(AbstractOperation.class);

	/**
	 * The owner component of this operation.
	 */
	private T owner;

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "(id=" + getOperationID()
				+ ")";
	}

	/**
	 * create an operation without an OperationCallback.
	 * 
	 * @param component
	 */
	protected AbstractOperation(T component) {
		this(component, null);
	}

	/**
	 * create an operation with an OperationCallback.
	 * 
	 * @param component
	 * @param callback
	 */
	protected AbstractOperation(T component, OperationCallback<S> callback) {
		super();
		this.owner = component;
		this.caller = callback;
	}

	/**
	 * Starts the operation
	 * 
	 */
	protected abstract void execute();

	/**
	 * Marks the operation as finished. The caller will be informed.
	 * 
	 * @param success
	 *            whether it was successful
	 */
	final protected void operationFinished(boolean success) {
		if (!finished) {
			this.finished = true;
			this.endTime = Simulator.getCurrentTime();
			this.error = !success;
			Simulator.getMonitor().operationFinished(this);
			// inform caller and owner component
			if (success) {
				if (caller != null) {
					caller.calledOperationSucceeded(this);
				}
			} else {
				if (caller != null) {
					caller.calledOperationFailed(this);
				}
			}
		}
	}

	@Override
	public abstract S getResult();

	protected boolean isError() {
		return error;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.peerfact.api.application.Operation#isSuccessful()
	 */
	@Override
	public boolean isSuccessful() {
		return isFinished() && !error;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.peerfact.api.application.Operation#isFinished()
	 */
	@Override
	public boolean isFinished() {
		return finished;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.peerfact.api.application.Operation#isFinished()
	 */
	@Override
	public long getDuration() {
		if (this.startTime != 0 && this.endTime != 0) {
			return this.endTime - this.startTime;
		} else {
			return 0;
		}
	}

	/**
	 * Each operation should have an operation timeout to ensure the proper
	 * functionality of the simulation. If a timeout event occurs in
	 * {@link #eventOccurred(SimulationEvent)} this method is invoked to perform
	 * various actions. Default behavior is to break up the operation after the
	 * first timeout.
	 * 
	 */
	protected void operationTimeoutOccured() {
		operationFinished(false);
	}

	/**
	 * Schedules an operation timeout in <code>timeout</code> simulation time
	 * units. The timeout event will be scheduled relative to the current
	 * simulation time.
	 * 
	 * @param timeout
	 */
	protected void scheduleOperationTimeout(long timeout) {
		long time = Simulator.getCurrentTime() + timeout;
		Simulator.scheduleEvent(null, time, this,
				SimulationEvent.Type.TIMEOUT_EXPIRED);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.peerfact.api.simengine.SimulationEventHandler#eventOccurred(
	 * org.peerfact.api.simengine.SimulationEvent)
	 */
	@Override
	public void eventOccurred(SimulationEvent se) {
		if (!isFinished()
				&& se.getType() == SimulationEvent.Type.TIMEOUT_EXPIRED) {
			operationTimeoutOccured();
		} else if (se.getType() == SimulationEvent.Type.OPERATION_EXECUTE
				&& se.getData() == this) {
			this.startTime = Simulator.getCurrentTime();
			Simulator.getMonitor().operationInitiated(this);
			execute();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.peerfact.api.application.Operation#scheduleImmediately()
	 */
	@Override
	public void scheduleImmediately() {
		scheduleWithDelay(0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.peerfact.api.application.Operation#scheduleWithDelay(long)
	 */
	@Override
	public void scheduleWithDelay(long delay) {
		long time = Simulator.getCurrentTime() + delay;
		scheduleAtTime(time);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.peerfact.api.application.Operation#scheduleAtTime(long)
	 */
	@Override
	public void scheduleAtTime(long time) {
		long newTime = Math.max(time, Simulator.getCurrentTime());
		Simulator.scheduleEvent(this, newTime, this,
				SimulationEvent.Type.OPERATION_EXECUTE);
	}

	@Override
	public T getComponent() {
		return owner;
	}

}
