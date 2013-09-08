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

import java.math.BigInteger;
import java.util.LinkedHashSet;
import java.util.Set;

import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.impl.service.dhtstorage.past.PASTConfig;
import org.peerfact.impl.service.dhtstorage.past.PASTObject;
import org.peerfact.impl.service.dhtstorage.past.PASTService;
import org.peerfact.impl.service.dhtstorage.past.messages.IssueReplicationMessage;
import org.peerfact.impl.simengine.Simulator;


/**
 * Sends a IssueReplicationMessage.
 */
public class IssueReplicationOperation extends AbstractPASTOperation {

	private TransInfo recipient;

	private Set<TransInfo> ignore;

	private IssueReplicationMessage msg;

	private PASTObject obj;

	public IssueReplicationOperation(PASTService component,
			OperationCallback<Object> callback, PASTConfig config,
			PASTObject obj, Set<TransInfo> ignore) {
		super(component, callback, config);
		this.obj = obj;
		if (ignore == null) {
			this.ignore = new LinkedHashSet<TransInfo>();
		} else {
			this.ignore = new LinkedHashSet<TransInfo>(ignore);
		}
	}

	@Override
	protected void execute() {
		if (getComponent().isOnline()) {
			msg = new IssueReplicationMessage(obj.getKey(), ignore);
			if (!obj.getReplications().containsKey(
					getComponent().getOwnTransInfo())) {
				operationFinished(false);
			} else {
				BigInteger distance = obj.getReplications().get(
						getComponent().getOwnTransInfo());
				BigInteger minDistance = null;
				TransInfo min = null;
				for (TransInfo key : obj.getReplications().keySet()) {
					BigInteger value = obj.getReplications().get(key);
					if (value.compareTo(distance) == 1
							&& (minDistance == null || value
									.compareTo(minDistance) == -1)) {
						minDistance = value;
						min = key;
					}
				}
				recipient = min;
				// recipient = getComponent().getMinimum(obj.getReplications(),
				// getComponent().getOwnTransInfo());
				if (recipient != null) {
					sendMessage();
				} else {
					operationFinished(false);
				}
			}
		}
		else {
			operationFinished(false); // stop if offline
		}
	}

	private void sendMessage() {
		if (getComponent().isOnline()) {
			long timeout = Simulator.MINUTE_UNIT;
			sendMessage(msg, recipient, getConfig().getNumberOfPingTries(),
					timeout);
		} else {
			operationFinished(false);
		}
	}

	@Override
	protected void sendMessageSucceeded() {
		getComponent().contactDidRespond(recipient);
		operationFinished(true);
	}

	@Override
	protected void sendMessageFailed() {
		getComponent().contactDidNotRespond(recipient);
		operationFinished(false);
	}

	@Override
	public Object getResult() {
		return null;
	}
}
