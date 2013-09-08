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

/**
 * Abstract class, which just implements the assignment of operation-IDs to the
 * respective operations. This includes all classes representing an operation,
 * which extend this class. In addition, the assignment of operation-IDs can
 * also be used for counting as well as for debugging (see
 * <code>getOperationID()</code> for further information on operation-IDs)
 * 
 * @author stingl <peerfact@kom.tu-darmstadt.de>
 * 
 * @param <T>
 *            The result type of the operation. e.g. LookupOperation in a
 *            DHTNode should return a DHTValue.
 * @version 05/06/2011
 */
public abstract class AbstractOperationCounter<T> implements Operation<T> {

	/**
	 * The number of all operations constructed so far.
	 */
	private static int operationCounter = 0;

	/**
	 * The identifier of a single Operation.
	 */
	private int operationID;

	protected AbstractOperationCounter() {
		operationID = ++operationCounter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.peerfact.api.application.Operation#getOperationID()
	 */
	@Override
	public int getOperationID() {
		return this.operationID;
	}

}
