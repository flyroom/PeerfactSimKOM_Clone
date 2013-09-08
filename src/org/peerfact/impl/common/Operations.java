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

import org.peerfact.api.common.Operation;
import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.common.SupportOperations;

/**
 * This class consists exclusively of static methods that either operate on
 * operations, return empty operations or empty operation callbacks.
 * 
 * @author Sebastian Kaune <peerfact@kom.tu-darmstadt.de>
 * @author Konstantin Pussep
 * @version 3.0, 12/05/2007
 * 
 */
public class Operations {

	/**
	 * Returns the empty operation callback (immutable) TODO: May be set to
	 * 
	 * @deprecated? Give me feedback: leo@relevantmusic.de
	 */
	@SuppressWarnings("rawtypes")
	public static final OperationCallback EMPTY_CALLBACK = new EmptyCallback();

	static class EmptyCallback<TResult> implements OperationCallback<TResult> {

		@Override
		public void calledOperationFailed(Operation<TResult> op) {
			// do nothing
		}

		@Override
		public void calledOperationSucceeded(Operation<TResult> op) {
			// do nothing
		}

	}

	/**
	 * Returns an empty OperationCallback, type-safe.
	 * 
	 * @param <TResult>
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <TResult> OperationCallback<TResult> getEmptyCallback() {
		return Operations.EMPTY_CALLBACK; // more memory-efficient (one instead
		// of many empty callbacks),
		// but only good with an
		// @SuppressWarnings above this
		// method

		// return new EmptyCallback<TResult>(); //fulfills type safety better,
		// but more memory-consuming
	}

	/**
	 * Invoking this method schedules immediately an empty operation without any
	 * functionality. In addition, the given callback will be informed that the
	 * operation has finished with success.
	 * 
	 * @param component
	 *            the owner (component) of the operation
	 * @param callback
	 *            the given callback
	 * @return the unique operation identifier
	 */
	public static <TResult> int scheduleEmptyOperation(
			SupportOperations component,
			OperationCallback<TResult> callback) {
		Operation<TResult> op = createEmptyOperation(component, callback);
		op.scheduleImmediately();
		return op.getOperationID();
	}

	/**
	 * Invoking this method creates an empty operation withoun an functionality.
	 * The execution of this operation will finish immediately with success.
	 * 
	 * @param component
	 *            The owner (component) of the operation
	 * @param callback
	 *            the given callback
	 * @return the created empty operation
	 */
	public static <TResult> Operation<TResult> createEmptyOperation(
			SupportOperations component,
			OperationCallback<TResult> callback) {
		Operation<TResult> op = new AbstractOperation<SupportOperations, TResult>(
				component, callback) {
			@Override
			protected void execute() {
				operationFinished(true);
			}

			@Override
			public TResult getResult() {
				return null;
			}

		};
		return op;
	}

	/**
	 * Invoking this method creates an empty operation withoun an functionality.
	 * The execution of this operation will finish immediately with success.
	 * 
	 * @param component
	 *            The owner (component) of the operation
	 * @param callback
	 *            the given callback
	 * @return the created empty operation
	 */
	public static <TResult> Operation<TResult> createEmptyOperationResult(
			SupportOperations component,
			OperationCallback<TResult> callback, final TResult result) {
		Operation<TResult> op = new AbstractOperation<SupportOperations, TResult>(
				component, callback) {
			@Override
			protected void execute() {
				operationFinished(true);
			}

			@Override
			public TResult getResult() {
				return result;
			}

		};
		return op;
	}
}
