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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.overlay.dht.DHTEntry;
import org.peerfact.api.overlay.dht.DHTKey;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.impl.service.dhtstorage.past.PASTConfig;
import org.peerfact.impl.service.dhtstorage.past.PASTObject;
import org.peerfact.impl.service.dhtstorage.past.PASTService;
import org.peerfact.impl.service.dhtstorage.past.messages.PingMessage;
import org.peerfact.impl.simengine.Simulator;


/**
 * Pings all other replica holders.
 */
public class PingOperation extends AbstractPASTOperation {

	private TransInfo target;

	private Map<DHTKey<?>, BigInteger> keys;

	private Set<TransInfo> failedNodes;

	public PingOperation(PASTService component,
			OperationCallback<Object> callback, PASTConfig config,
			TransInfo target, Set<TransInfo> failedNodes) {
		super(component, callback, config);
		this.target = target;
		keys = new LinkedHashMap<DHTKey<?>, BigInteger>();
		this.failedNodes = failedNodes;
	}

	@Override
	protected void execute() {
		if (getComponent().isOnline()) {
			Set<DHTEntry<DHTKey<?>>> entries = getComponent().getDHTEntries();
			for (DHTEntry<?> entry : entries) {
				if (entry instanceof PASTObject) {
					PASTObject obj = (PASTObject) entry;
					if (obj.getReplications().containsKey(
							getComponent().getOwnTransInfo())) {
						BigInteger distance = obj.getReplications().get(
								getComponent().getOwnTransInfo());
						keys.put(entry.getKey(), distance);
					}
				}
			}
			PingMessage msg = new PingMessage(keys, failedNodes);
			long timeout = Simulator.MINUTE_UNIT;
			sendMessage(msg, target, getConfig().getNumberOfPingTries(),
					timeout);
		}
		else {
			operationFinished(true); // stop if offline
		}
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

	public TransInfo getTarget() {
		return target;
	}

}
