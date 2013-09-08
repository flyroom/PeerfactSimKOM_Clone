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

package org.peerfact.impl.application.filesharing.operations;

import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.common.SupportOperations;
import org.peerfact.impl.common.AbstractOperation;

/**
 * An operation capable of periodic rescheduling.
 * 
 * @author <info@peerfact.org>
 * 
 * @version 05/06/2011
 */
public abstract class AbstractPeriodicFilesharingOperation<T extends SupportOperations, S extends Object>
		extends AbstractOperation<T, S> implements IFilesharingOperation {

	boolean reschedule = false;

	boolean stopped = false;

	private IntervalModel mdl;

	public AbstractPeriodicFilesharingOperation(T component, OperationCallback<S> callback) {
		super(component, callback);
	}

	@Override
	public void execute() {
		if (!stopped) {
			executeOnce();
		}
		if (reschedule) {
			reschedule();
		}
	}

	/**
	 * Called on each rescheduling attempt. Here, you should implement the
	 * functionality of the periodic operation that is executed on each period.
	 */
	protected abstract void executeOnce();

	public void schedulePeriodically(IntervalModel model) {
		this.mdl = model;
		reschedule = true;
		reschedule();

	}

	/**
	 * If called, the periodic rescheduling stops.
	 */
	public void stop() {
		reschedule = false;
		stopped = true;
	}

	private void reschedule() {
		this.scheduleWithDelay(mdl.getNewDelay());
	}

	/**
	 * Returns interval delays according to a specified model.
	 * 
	 * @author
	 * 
	 */
	public interface IntervalModel {

		/**
		 * Returns a new delay in simulation time units.
		 * 
		 * @return
		 */
		public long getNewDelay();
	}

}
