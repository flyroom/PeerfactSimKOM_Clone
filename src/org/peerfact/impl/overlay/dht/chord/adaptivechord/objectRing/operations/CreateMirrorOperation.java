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
import org.peerfact.api.overlay.dht.DHTObject;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.api.transport.TransMessageCallback;
import org.peerfact.impl.overlay.dht.chord.adaptivechord.objectRing.components.ChordConfiguration;
import org.peerfact.impl.overlay.dht.chord.adaptivechord.objectRing.components.ChordContact;
import org.peerfact.impl.overlay.dht.chord.adaptivechord.objectRing.components.ChordNode;
import org.peerfact.impl.overlay.dht.chord.adaptivechord.objectRing.messages.CreateMirrorMessage;
import org.peerfact.impl.overlay.dht.chord.adaptivechord.objectRing.messages.CreateMirrorReplyMessage;
import org.peerfact.impl.overlay.dht.chord.base.components.ChordKey;
import org.peerfact.impl.overlay.dht.chord.base.operations.AbstractChordOperation;

/**
 * Class create a mirror for a document.
 * 
 * @author wette
 * @version 1.0, 06/21/2011
 */
public class CreateMirrorOperation extends AbstractChordOperation<Boolean>
		implements TransMessageCallback {

	/**
	 * sender of the message
	 */
	ChordContact sender;

	/**
	 * receiver of the message
	 */
	ChordContact receiver;

	/**
	 * contact that the receiver has to add to its routingtable.
	 */
	DHTObject object;

	ChordKey objectId;

	ChordNode owner;

	CreateMirrorMessage msg;

	double usedBandwidth;

	int numRetrys = 0;

	public CreateMirrorOperation(ChordNode component, ChordContact sender,
			ChordContact receiver, DHTObject object, ChordKey objectId,
			double usedBandwidth) {
		super(component);
		this.sender = sender;
		this.receiver = receiver;
		this.owner = component;

		this.object = object;
		this.objectId = objectId;

		this.usedBandwidth = usedBandwidth;

	}

	@Override
	protected void execute() {

		if (receiver.getTransInfo().getNetId()
				.equals(sender.getTransInfo().getNetId())) {
			log.debug(this
					+ " wants to be a mirror for itself... skipping.");
			operationFinished(true);
			return;
		}

		log.debug(owner + ": starting mirroring process to "
				+ receiver + " for document " + objectId);

		msg = new CreateMirrorMessage(this.sender, this.receiver,
				this.objectId, this.object);

		this.owner.getTransLayer().sendAndWait(msg,
				receiver.getTransInfo(),
				sender.getTransInfo().getPort(),
				ChordConfiguration.TRANSPORT_PROTOCOL,
				this,
				ChordConfiguration.MESSAGE_TIMEOUT);
	}

	@Override
	public Boolean getResult() {
		return this.isSuccessful();
	}

	@Override
	public void receive(Message message, TransInfo senderInfo, int commId) {
		CreateMirrorReplyMessage rep = (CreateMirrorReplyMessage) message;

		if (rep.wasSuccessful()) {
			log.debug(owner
					+ ": successfully added mirror for document " + objectId);
			owner.addMirrorForObject(this.objectId,
					rep.getSenderContact());

			// increment minimal bandwidth
			owner.incrementMinimalMirrorBandwidth();

		} else {
			log.debug(owner + ": FAILED to add mirror for document "
					+ objectId + " on node " + rep.getSender()
					+ " needed bandwidth:" + this.usedBandwidth);

			// increment minimal bandwidth
			owner.decrementMinimalMirrorBandwidth();
		}

		operationFinished(true);
	}

	@Override
	public void messageTimeoutOccured(int commId) {
		if (!this.isFinished()) {
			if (numRetrys > ChordConfiguration.MESSAGE_RESEND) {
				// cancle event:
				operationFinished(false);

				System.out
						.println("CreateMirrorOperation: failed to create mirror :(");

			} else {
				numRetrys++;
				msg = new CreateMirrorMessage(this.sender, this.receiver,
						this.objectId, this.object);
				this.owner.getTransLayer().sendAndWait(msg,
						receiver.getTransInfo(),
						sender.getTransInfo().getPort(),
						ChordConfiguration.TRANSPORT_PROTOCOL,
						this,
						ChordConfiguration.MESSAGE_TIMEOUT);
			}
		}
	}

}
