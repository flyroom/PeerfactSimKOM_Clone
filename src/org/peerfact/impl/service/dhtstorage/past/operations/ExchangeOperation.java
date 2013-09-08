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
import java.util.Map;

import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.overlay.dht.DHTKey;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.impl.service.dhtstorage.past.PASTConfig;
import org.peerfact.impl.service.dhtstorage.past.PASTService;
import org.peerfact.impl.service.dhtstorage.past.messages.ExchangeMessage;
import org.peerfact.impl.simengine.Simulator;


/**
 * Starts the exchange of replication information.
 */
public class ExchangeOperation extends AbstractPASTOperation {

	private TransInfo recipient;

	private ExchangeMessage msg;

	public ExchangeOperation(PASTService component,
			OperationCallback<Object> callback, PASTConfig config,
			Map<DHTKey<?>, Map<TransInfo, BigInteger>> exchangeMap,
			TransInfo recipient, boolean reply) {
		super(component, callback, config);
		this.recipient = recipient;
		msg = new ExchangeMessage(exchangeMap, reply);
	}

	@Override
	protected void execute() {
		if (getComponent().isOnline()) {
			if (recipient != null) {
				sendMessage();
			} else {
				operationFinished(false);
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
