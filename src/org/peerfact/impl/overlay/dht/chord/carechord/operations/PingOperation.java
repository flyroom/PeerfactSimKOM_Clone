/*
 * Copyright (c) 2005-2010 KOM – Multimedia Communications Lab
 *
 * This file is part of PeerfactSim.
 * 
 * PeerfactSim is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 * 
 * PeerfactSim is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with PeerfactSim.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.peerfact.impl.overlay.dht.chord.carechord.operations;

import org.peerfact.api.common.Message;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.api.transport.TransMessageCallback;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordContact;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordNode;
import org.peerfact.impl.overlay.dht.chord.base.components.ChordConfiguration;
import org.peerfact.impl.overlay.dht.chord.base.operations.AbstractChordOperation;
import org.peerfact.impl.overlay.dht.chord.carechord.components.ChordRoutingTable;
import org.peerfact.impl.overlay.dht.chord.carechord.messages.PingMessage;

/**
 * Ping someone - and get a pong back! -- Used to check if a host is still up.
 * 
 * @author Markus Benter (original author)
 */

public class PingOperation extends AbstractChordOperation<Boolean> implements
		TransMessageCallback {

	/**
	 * sender of the message
	 */
	private AbstractChordContact sender;

	/**
	 * receiver of the message
	 */
	private AbstractChordContact receiver;

	int numRetrys = 0;

	@Override
	public void messageTimeoutOccured(int commId) {
		// Do something when timeout is exceeded
		if (numRetrys > ChordConfiguration.PING_RESEND_COUNT) {

			// remove contact because offline.
			((ChordRoutingTable) super.getComponent().getChordRoutingTable())
					.removeFromRouting(this.receiver.getOverlayID());

			operationFinished(false);
		} else {

			PingMessage msg = new PingMessage(this.sender, this.receiver);

			super.getComponent()
					.getTransLayer()
					.sendAndWait(msg, receiver.getTransInfo(),
							sender.getTransInfo().getPort(),
							ChordConfiguration.TRANSPORT_PROTOCOL, this,
							ChordConfiguration.MESSAGE_TIMEOUT);

			numRetrys++;
		}
	}

	@Override
	public void receive(Message msg, TransInfo senderInfo, int commId) {
		// Do something on arrival of reply!
		operationFinished(true);
	}

	public PingOperation(AbstractChordNode component) {
		super(component);

		this.sender = component.getLocalOverlayContact();
		if (component.getChordRoutingTable() != null) {
			this.receiver = ((ChordRoutingTable) component
					.getChordRoutingTable())
					.getNextContactToPing();
		} else {
			this.receiver = null;
		}
	}

	@Override
	protected void execute() {
		// do not ping if its a virtual host of mine.
		if (getComponent().isPresent()
				&& this.receiver != null
				&& this.sender.getTransInfo().getNetId() != this.receiver
						.getTransInfo().getNetId()) {
			PingMessage msg = new PingMessage(this.sender, this.receiver);

			super.getComponent()
					.getTransLayer()
					.sendAndWait(msg, receiver.getTransInfo(),
							sender.getTransInfo().getPort(),
							ChordConfiguration.TRANSPORT_PROTOCOL, this,
							ChordConfiguration.MESSAGE_TIMEOUT);
		} else {
			operationFinished(true);
		}

		// schedule this operation again.
		new PingOperation(super.getComponent())
				.scheduleWithDelay(ChordConfiguration.PING_INTERVAL);
	}

	@Override
	public Boolean getResult() {
		if (this.isSuccessful()) {
			return true;
		}
		return false;
	}

}
