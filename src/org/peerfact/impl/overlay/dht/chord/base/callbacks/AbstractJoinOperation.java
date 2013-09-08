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

package org.peerfact.impl.overlay.dht.chord.base.callbacks;

import org.peerfact.api.common.Message;
import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.api.transport.TransMessageCallback;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordNode;
import org.peerfact.impl.overlay.dht.chord.base.operations.AbstractChordOperation;

/**
 * This class represents an abstract join event.
 * 
 * @author Minh Hoang Nguyen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public abstract class AbstractJoinOperation extends
		AbstractChordOperation<Object> implements
		TransMessageCallback {

	protected final AbstractChordNode joinNode;

	protected OperationCallback<Object> callback;

	public AbstractJoinOperation(AbstractChordNode component,
			OperationCallback<Object> callback) {
		super(component, callback);
		joinNode = getComponent();
		this.callback = callback;
	}

	/*
	 * join contains the following steps 1. pick a random node in
	 * ChordBootstrapManager as first contact 2. The first node look the
	 * successor for join node 3. join node send handshake message to its
	 * successor 4. successor inform join node about its predecessor,
	 * FingerTable 5. finish, join node receive successor, predecessor contact
	 * and FingerTable of successor
	 */

	@Override
	protected abstract void execute();

	/**
	 * Inform JoinOperation that join process successfully ended
	 */
	protected abstract void finish();

	@Override
	public abstract void messageTimeoutOccured(int commId);

	@Override
	public abstract void receive(Message msg, TransInfo senderInfo, int commId);

	@Override
	public Object getResult() {
		return this.isSuccessful();
	}

}
