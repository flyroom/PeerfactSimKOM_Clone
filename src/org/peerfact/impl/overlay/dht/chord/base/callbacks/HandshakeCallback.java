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

package org.peerfact.impl.overlay.dht.chord.base.callbacks;

import java.util.List;

import org.apache.log4j.Logger;
import org.peerfact.api.common.Operation;
import org.peerfact.api.common.OperationCallback;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordContact;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordNode;
import org.peerfact.impl.overlay.dht.chord.base.components.ChordConfiguration;
import org.peerfact.impl.overlay.dht.chord.base.messages.JoinReply;
import org.peerfact.impl.transport.TransMsgEvent;
import org.peerfact.impl.util.logging.SimLogger;


/**
 * This event occurs when a new join node contacts with its successor at the
 * first time. The successor send to new join node its predecessor and set the
 * new join node as its new predecessor
 * 
 * @author Minh Hoang Nguyen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class HandshakeCallback implements
		OperationCallback<List<AbstractChordContact>> {

	private static Logger log = SimLogger.getLogger(HandshakeCallback.class);

	private AbstractChordNode masterNode;

	private TransMsgEvent receivingEvent;

	private AbstractChordContact joinNode;

	public HandshakeCallback(AbstractChordContact joinNode,
			AbstractChordNode masterNode,
			TransMsgEvent receivingEvent) {
		super();
		this.joinNode = joinNode;
		this.masterNode = masterNode;
		this.receivingEvent = receivingEvent;
	}

	@Override
	public void calledOperationFailed(Operation<List<AbstractChordContact>> op) {
		log.info("Operation Failed node = " + masterNode);
	}

	@Override
	public void calledOperationSucceeded(
			Operation<List<AbstractChordContact>> op) {

		List<AbstractChordContact> responders = op.getResult();
		if (!responders.isEmpty()) {

			JoinReply reply = new JoinReply(
					masterNode.getLocalOverlayContact(),
					joinNode, responders.get(0));
			masterNode.getTransLayer()
					.sendReply(reply, receivingEvent, masterNode.getPort(),
							ChordConfiguration.TRANSPORT_PROTOCOL);
		}

	}

	public AbstractChordContact getJoinNode() {
		return joinNode;
	}

}
