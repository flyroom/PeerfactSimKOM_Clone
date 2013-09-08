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

package org.peerfact.api.overlay;

import org.peerfact.api.common.OperationCallback;

/**
 * An overlay node that can join and leave a network in a simple way (no
 * additional arguments needed)
 * 
 * @author Leo Nobach, Sebastian Kaune <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public interface JoinLeaveOverlayNode<T extends OverlayID<?>, S extends OverlayContact<T>>
		extends OverlayNode<T, S> {

	/**
	 * Invoking this method bootstraps an OverlayNode into the overlay network.
	 * After the bootstraping process is finished, the registered
	 * <code>OverlayEventHandler</code> listening on <code>handlerId</code> will
	 * be informed using an <code>OverlayEvent</code>.
	 * 
	 * @param callback
	 *            - callback which will receive the operation result (either
	 *            operation succeeds with result==null or fails)
	 * @return id of the associated join operation, or -1 if no operation is
	 *         spawned.
	 */
	public int join(OperationCallback<Object> callback);

	/**
	 * Invoking this method results in leaving the network an OverlayNode is
	 * participating in. After the leave operation is finished, the registered
	 * <code>OverlayEventHandler</code> listening on <code>handerId</code> will
	 * be informed using an <code>OverlayEvent</code>.
	 * 
	 * @param callback
	 *            - callback which will receive the operation result (either
	 *            operation succeeds with result==null or fails)
	 * @return id of the associated leave operation, or -1 if no operation is
	 *         spawned.
	 */
	public int leave(OperationCallback<Object> callback);

}
