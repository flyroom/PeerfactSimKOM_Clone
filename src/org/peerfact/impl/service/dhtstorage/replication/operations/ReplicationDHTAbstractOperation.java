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

import org.peerfact.api.common.Message;
import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.overlay.dht.DHTKey;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.api.transport.TransMessageCallback;
import org.peerfact.api.transport.TransProtocol;
import org.peerfact.impl.common.AbstractOperation;
import org.peerfact.impl.service.dhtstorage.replication.ReplicationDHTConfig;
import org.peerfact.impl.service.dhtstorage.replication.ReplicationDHTService;
import org.peerfact.impl.service.dhtstorage.replication.messages.ReplicationDHTMessage;
import org.peerfact.impl.simengine.Simulator;

/**
 * Provides common methods for all DHTService Operations, most important an
 * Message/ACK-System
 * 
 * @author Bjoern Richerzhagen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
abstract public class ReplicationDHTAbstractOperation<K extends DHTKey<?>>
		extends AbstractOperation<ReplicationDHTService<K>, Object> {

	private ReplicationDHTConfig config;

	private int waitForReply = 0;

	protected ReplicationDHTAbstractOperation(
			ReplicationDHTService<K> component,
			OperationCallback<Object> callback, ReplicationDHTConfig config) {
		super(component, callback);
		this.config = config;
	}

	protected ReplicationDHTConfig getConfig() {
		return config;
	}

	/**
	 * Try to send Message msg to receiver for n times. Uses callback-functions
	 * sendMessageFailed and sendMessageSucceeded
	 * 
	 * @param msg
	 * @param receiver
	 * @param n
	 */
	protected void sendMessage(final ReplicationDHTMessage msg,
			final TransInfo receiver, final int tries) {
		waitForReply = sendAndWait(msg, receiver,
				new TransMessageCallback() {

					@Override
					public void receive(Message message, TransInfo senderInfo,
							int commId) {
						if (commId == waitingForReplyId()) {
							sendMessageSucceeded();
						}
					}

					@Override
					public void messageTimeoutOccured(int commId) {
						if (tries <= 0) {
							sendMessageFailed();
						} else {
							sendMessage(msg, receiver, tries - 1);
						}

					}
				});
	}

	/**
	 * the ReplyID we are waiting for
	 * 
	 * @return
	 */
	protected int waitingForReplyId() {
		return waitForReply;
	}

	/**
	 * Callback, if sendMessage failed
	 */
	protected void sendMessageFailed() {
		// Callback, to be implemented if needed.
	}

	/**
	 * Callback, if sendMessage was successful.
	 */
	protected void sendMessageSucceeded() {
		// Callback, to be implemented if needed.
	}

	/**
	 * shorthand for sendAndWait
	 * 
	 * @param msg
	 * @param receiver
	 * @param callback
	 * @return
	 */
	protected int sendAndWait(ReplicationDHTMessage msg, TransInfo receiver,
			TransMessageCallback callback) {
		return getComponent()
				.getHost()
				.getTransLayer()
				.sendAndWait(msg, receiver, getComponent().getPort(),
						TransProtocol.UDP, callback, 1 * Simulator.MINUTE_UNIT);
	}

}
