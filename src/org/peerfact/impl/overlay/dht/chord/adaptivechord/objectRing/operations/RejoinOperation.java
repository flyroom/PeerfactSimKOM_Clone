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

package org.peerfact.impl.overlay.dht.chord.adaptivechord.objectRing.operations;

import org.peerfact.api.common.Operation;
import org.peerfact.api.common.OperationCallback;
import org.peerfact.impl.overlay.dht.chord.adaptivechord.objectRing.components.ChordNode;
import org.peerfact.impl.overlay.dht.chord.base.components.ChordID;
import org.peerfact.impl.overlay.dht.chord.base.operations.AbstractChordOperation;

/**
 * This Operation rejoins the network with a different id. if the id is already
 * taken it is incremented as long as a new free id is found.
 * 
 * @author wette
 * @version 1.0, 06/21/2011
 */
public class RejoinOperation extends AbstractChordOperation<Boolean> implements
		OperationCallback<Object> {

	ChordNode owner;

	ChordID newId;

	public RejoinOperation(ChordNode component, ChordID newId) {
		super(component);
		this.owner = component;
		this.newId = newId;
	}

	@Override
	protected void execute() {
		owner.iDOnNextRejoin = newId;
		owner.leave(this);
	}

	@Override
	public Boolean getResult() {
		return this.isSuccessful();
	}

	@Override
	public void calledOperationFailed(Operation<Object> op) {
		operationFinished(true);

	}

	@Override
	public void calledOperationSucceeded(Operation<Object> op) {
		operationFinished(true);

	}

}
