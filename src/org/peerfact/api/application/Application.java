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

package org.peerfact.api.application;

import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.common.SupportOperations;

/**
 * Application interface provides basic methods common to any application
 * behavior
 * 
 * @author Sebastian Kaune <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 */

public interface Application extends SupportOperations {

	// CHECK Whether we really need these methods in the interface

	/**
	 * Starts the application and establishes all necessary connections.
	 * 
	 * @param callback
	 *            - callback which will receive the result of the operation.
	 * @return operation id
	 */
	public int start(OperationCallback<?> callback);

	/**
	 * simulate an application crash
	 * 
	 */
	// public void crash();
	/**
	 * Application do a regular close.
	 * 
	 * @param callback
	 *            - callback which will receive the result of the operation.
	 * @return operation id
	 */
	public int close(OperationCallback<?> callback);

}
