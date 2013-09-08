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

import java.util.LinkedHashSet;
import java.util.Set;

import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordContact;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordNode;
import org.peerfact.impl.overlay.dht.chord.base.components.ChordConfiguration;
import org.peerfact.impl.overlay.dht.chord.base.operations.AbstractChordOperation;
import org.peerfact.impl.overlay.dht.chord.carechord.components.ChordNode;
import org.peerfact.impl.overlay.dht.chord.carechord.messages.CreateLinkMessage;


/**
 * Class to inform a node a to take another node b into its routingtable.
 * 
 * @author Markus Benter (original author)
 */
public class CreateLinkOperation extends
		AbstractChordOperation<AbstractChordNode> {

	/**
	 * sender of the message
	 */
	AbstractChordContact sender;

	/**
	 * receiver of the message
	 */
	AbstractChordContact receiver;

	/**
	 * contact that the receiver has to add to its routingtable.
	 */
	Set<AbstractChordContact> targetOfNewLink;

	AbstractChordNode owner;

	/**
	 * "unmarked" for unmarked-edge; "ring" for ring-edge; "connection" for
	 * connection-edge
	 */
	String typeOfCreatedEdge;

	int numRetrys = 0;

	public CreateLinkOperation(AbstractChordNode component,
			AbstractChordContact sender,
			AbstractChordContact receiver,
			Set<AbstractChordContact> targetOfNewLink,
			String type) {
		super(component);
		this.sender = sender;
		this.receiver = receiver;
		this.targetOfNewLink = targetOfNewLink;
		this.typeOfCreatedEdge = type;

		this.owner = component;

		((ChordNode) component).createLinkOperationCounter++;
	}

	public CreateLinkOperation(AbstractChordNode component,
			AbstractChordContact sender,
			AbstractChordContact receiver,
			AbstractChordContact targetOfNewLink, String type) {
		super(component);
		this.sender = sender;
		this.receiver = receiver;
		this.targetOfNewLink = new LinkedHashSet<AbstractChordContact>();
		this.targetOfNewLink.add(targetOfNewLink);
		this.typeOfCreatedEdge = type;

		this.owner = component;

		((ChordNode) component).createLinkOperationCounter++;
	}

	public String getTypeOfEdge() {
		return typeOfCreatedEdge;
	}

	@Override
	protected void execute() {
		if (sender.getTransInfo().getNetId() == receiver.getTransInfo()
				.getNetId()) {
			// receiver is a virtual host of mine - use local communication.
			((ChordNode) super.getComponent()).localCreateLinkOperation(
					receiver,
					targetOfNewLink, typeOfCreatedEdge);
			operationFinished(true);
		} else {
			CreateLinkMessage msg = new CreateLinkMessage(this.sender,
					this.receiver);
			msg.setTargetOfNewLink(this.targetOfNewLink);
			msg.setTypeOfEdge(typeOfCreatedEdge);

			this.owner.getTransLayer().send(msg, receiver.getTransInfo(),
					sender.getTransInfo().getPort(),
					ChordConfiguration.TRANSPORT_PROTOCOL);
			operationFinished(true);
		}
	}

	@Override
	public AbstractChordNode getResult() {
		// TODO Auto-generated method stub
		return null;
	}

	// @Override
	// public void receive(Message msg, TransInfo senderInfo, int commId) {
	// operationFinished(true);
	// }
	//
	// @Override
	// public void messageTimeoutOccured(int commId) {
	// if(! this.isFinished()) {
	// if(this.numRetrys < ChordConfiguration.MESSAGE_RESEND) {
	// CreateLinkMessage msg = new CreateLinkMessage(this.sender,
	// this.receiver);
	// msg.setTargetOfNewLink(this.targetOfNewLink);
	// msg.setTypeOfEdge(typeOfCreatedEdge);
	//
	// this.owner.getTransLayer().sendAndWait(msg,
	// receiver.getTransInfo(),
	// sender.getTransInfo().getPort(),
	// ChordConfiguration.TRANSPORT_PROTOCOL,
	// this,
	// ChordConfiguration.MESSAGE_TIMEOUT);
	//
	// numRetrys++;
	// } else {
	// operationFinished(false);
	// }
	// }
	// }

}
