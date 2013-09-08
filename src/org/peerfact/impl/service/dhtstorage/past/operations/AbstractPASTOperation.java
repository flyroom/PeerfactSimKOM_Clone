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

import org.peerfact.api.common.Message;
import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.api.transport.TransMessageCallback;
import org.peerfact.api.transport.TransProtocol;
import org.peerfact.impl.common.AbstractOperation;
import org.peerfact.impl.service.dhtstorage.past.PASTConfig;
import org.peerfact.impl.service.dhtstorage.past.PASTService;
import org.peerfact.impl.service.dhtstorage.past.messages.PASTMessage;

/**
 * Base class for all operations of the PASTService.  
 */
abstract public class AbstractPASTOperation extends
		AbstractOperation<PASTService, Object> {

	private PASTConfig config;

	private int waitForReply = 0;

	protected AbstractPASTOperation(PASTService component,
			OperationCallback<Object> callback, PASTConfig config) {
		super(component, callback);
		this.config = config;
	}

	protected PASTConfig getConfig() {
		return config;
	}

	protected void sendMessage(final PASTMessage msg,
			final TransInfo receiver, final int tries, final long timeout) {
		waitForReply = sendAndWait(msg, receiver,
				new TransMessageCallback() {

			@Override
			public void receive(Message message, TransInfo senderInfo, int commId) {
				if (commId == waitingForReplyId()) {
					sendMessageSucceeded();
				}
			}

			@Override
			public void messageTimeoutOccured(int commId) {
				if (tries <= 0) {
					sendMessageFailed();
				} else {
					sendMessage(msg, receiver, tries - 1, timeout);
				}

			}
		}, timeout);
	}


	protected int waitingForReplyId() {
		return waitForReply;
	}


	protected void sendMessageFailed() {
		// Callback, to be implemented if needed.
	}

	protected void sendMessageSucceeded() {
		// Callback, to be implemented if needed.
	}

	protected int sendAndWait(PASTMessage msg, TransInfo receiver, TransMessageCallback callback, long timeout) {
		return getComponent()
				.getHost()
				.getTransLayer()
				.sendAndWait(msg, receiver, getComponent().getPort(),
						TransProtocol.UDP, callback, timeout);
	}


}
