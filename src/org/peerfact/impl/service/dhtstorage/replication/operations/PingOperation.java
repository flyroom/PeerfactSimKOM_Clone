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

package org.peerfact.impl.service.dhtstorage.replication.operations;

import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.overlay.dht.DHTKey;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.impl.service.dhtstorage.replication.ReplicationDHTConfig;
import org.peerfact.impl.service.dhtstorage.replication.ReplicationDHTService;
import org.peerfact.impl.service.dhtstorage.replication.messages.PingMessage;

/**
 * Ping a contact. This will update the lastAction parameter of this contact so
 * there will be only one request per contact.
 * 
 * FIXME needed?
 * 
 * @author Bjoern Richerzhagen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class PingOperation<K extends DHTKey<?>> extends
		ReplicationDHTAbstractOperation<K> {

	private TransInfo target;

	protected PingOperation(ReplicationDHTService<K> component,
			OperationCallback<Object> callback, ReplicationDHTConfig config,
			TransInfo target) {
		super(component, callback, config);
		this.target = target;
	}

	@Override
	protected void execute() {
		PingMessage msg = new PingMessage();
		sendMessage(msg, target, getConfig().getNumberOfPingTries());
	}

	@Override
	protected void sendMessageSucceeded() {
		getComponent().contactDidRespond(target);
		operationFinished(true);
	}

	@Override
	protected void sendMessageFailed() {
		getComponent().contactDidNotRespond(target);
		operationFinished(false);
	}

	@Override
	public Object getResult() {
		return null;
	}

}
