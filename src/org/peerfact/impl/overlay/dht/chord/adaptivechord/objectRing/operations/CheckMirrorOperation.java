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

import org.peerfact.api.common.Message;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.api.transport.TransMessageCallback;
import org.peerfact.impl.overlay.dht.chord.adaptivechord.objectRing.components.ChordConfiguration;
import org.peerfact.impl.overlay.dht.chord.adaptivechord.objectRing.components.ChordNode;
import org.peerfact.impl.overlay.dht.chord.adaptivechord.objectRing.messages.CheckMirrorMessage;
import org.peerfact.impl.overlay.dht.chord.adaptivechord.objectRing.messages.CheckMirrorReplyMessage;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordContact;
import org.peerfact.impl.overlay.dht.chord.base.components.ChordKey;
import org.peerfact.impl.overlay.dht.chord.base.operations.AbstractChordOperation;
import org.peerfact.impl.simengine.Simulator;

/**
 * This Operation is called periodically to check if my mirrors are still
 * online.
 * 
 * @author wette
 * @version 1.0, 06/21/2011
 */
public class CheckMirrorOperation extends AbstractChordOperation<Boolean>
implements TransMessageCallback {

	/**
	 * sender of the message
	 */
	AbstractChordContact sender;

	/**
	 * receiver of the message
	 */
	AbstractChordContact receiver;

	/**
	 * id of the document.
	 */
	ChordKey objectId;

	ChordNode owner;

	int numRetrys = 0;

	public CheckMirrorOperation(ChordNode component,
			AbstractChordContact sender, AbstractChordContact receiver,
			ChordKey objectId) {
		super(component);
		this.sender = sender;
		this.receiver = receiver;
		this.owner = component;

		this.objectId = objectId;

	}

	@Override
	protected void execute() {

		CheckMirrorMessage msg = new CheckMirrorMessage(this.sender,
				this.receiver, this.objectId);

		this.owner.getTransLayer().sendAndWait(msg,
				receiver.getTransInfo(),
				sender.getTransInfo().getPort(),
				ChordConfiguration.TRANSPORT_PROTOCOL,
				this,
				Simulator.SECOND_UNIT * 3); // this has be be fast. so non
		// default time.
	}

	@Override
	public Boolean getResult() {
		return this.isSuccessful();
	}

	@Override
	public void receive(Message msg, TransInfo senderInfo, int commId) {
		if (msg instanceof CheckMirrorReplyMessage) {
			CheckMirrorReplyMessage reply = (CheckMirrorReplyMessage) msg;

			if (reply.mirrorOnline()) {
				// mirror still exists - nothing to be done.
			} else {
				// mirror is down - delete from records!
				this.owner.removeRedirection(this.objectId);
			}

			operationFinished(true);
		}
	}

	@Override
	public void messageTimeoutOccured(int commId) {
		if (!this.isFinished()) {
			if (numRetrys > ChordConfiguration.MESSAGE_RESEND) {

				// mirror is down - delete from records!
				this.owner.removeRedirection(this.objectId);

				operationFinished(false);

			} else {
				numRetrys++;
				CheckMirrorMessage msg = new CheckMirrorMessage(this.sender,
						this.receiver, this.objectId);
				this.owner.getTransLayer().sendAndWait(msg,
						receiver.getTransInfo(),
						sender.getTransInfo().getPort(),
						ChordConfiguration.TRANSPORT_PROTOCOL,
						this,
						Simulator.SECOND_UNIT * 3);
			}
		}
	}

}
