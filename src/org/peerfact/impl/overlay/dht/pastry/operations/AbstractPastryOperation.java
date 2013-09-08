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

package org.peerfact.impl.overlay.dht.pastry.operations;

import org.peerfact.api.common.OperationCallback;
import org.peerfact.impl.common.AbstractOperation;
import org.peerfact.impl.overlay.dht.pastry.components.PastryNode;

/**
 * @author Fabio Zöllner <peerfact@kom.tu-darmstadt.de>
 * 
 * @param <S>
 *            the return type of the operation
 * @version 05/06/2011
 */
public abstract class AbstractPastryOperation<S> extends
		AbstractOperation<PastryNode, S> {

	protected AbstractPastryOperation(PastryNode component) {
		super(component);
	}

	protected AbstractPastryOperation(PastryNode component,
			OperationCallback<S> callback) {
		super(component, callback);
	}

}
