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

package org.peerfact.impl.service.dhtstorage.past.operations;
import java.util.ArrayList;
import java.util.List;

import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.impl.service.dhtstorage.past.PASTConfig;
import org.peerfact.impl.service.dhtstorage.past.PASTObject;
import org.peerfact.impl.service.dhtstorage.past.PASTService;
import org.peerfact.impl.service.dhtstorage.past.messages.ReplicationRemovedMessage;
import org.peerfact.impl.simengine.Simulator;


/**
 * Informs all other replica holder that a value was dropped by the node executing the operation.
 */
public class ReplicationRemovedOperation extends AbstractPASTOperation {

	private int actRecipient = 0;
	
	private List<TransInfo> recipients = new ArrayList<TransInfo>();
	
	private ReplicationRemovedMessage msg;
	
	private PASTObject obj;
	
	public ReplicationRemovedOperation(PASTService component,
			OperationCallback<Object> callback, PASTConfig config,
			PASTObject obj) {
		super(component, callback, config);
		this.obj = obj;
	}

	@Override
	protected void execute() {
		if(getComponent().isOnline()) {
			msg = new ReplicationRemovedMessage(obj.getKey());
			recipients.addAll(obj.getReplicationHolders());
		}
		else
			operationFinished(true); //stop if offline
	}
	
	private void sendNextMessage() {
		if(actRecipient < recipients.size() && getComponent().isOnline()) {
			long timeout = Simulator.MINUTE_UNIT;
			sendMessage(msg, recipients.get(actRecipient), getConfig().getNumberOfPingTries(), timeout);
		}
		else
			operationFinished(true);
	}

	@Override
	protected void sendMessageSucceeded() {
		getComponent().contactDidRespond(recipients.get(actRecipient));
		actRecipient++;
		sendNextMessage();
	}

	@Override
	protected void sendMessageFailed() {
		getComponent().contactDidNotRespond(recipients.get(actRecipient));
		actRecipient++;
		sendNextMessage();
	}

	@Override
	public Object getResult() {
		return null;
	}
}
