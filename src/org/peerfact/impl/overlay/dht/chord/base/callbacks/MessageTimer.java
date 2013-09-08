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

import org.apache.log4j.Logger;
import org.peerfact.api.common.Message;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.api.transport.TransMessageCallback;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordContact;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordNode;
import org.peerfact.impl.overlay.dht.chord.base.components.ChordConfiguration;
import org.peerfact.impl.util.logging.SimLogger;


/**
 * This class is used as Observer for a message. A timer will be started, if
 * message time out event occurs, the message will be resent. After a specified
 * number of retry, if the receiver still not reacts, <code>MessageTimer</code>
 * will notify the sender about offline event of receiver.
 * 
 * @author Minh Hoang Nguyen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class MessageTimer implements TransMessageCallback {

	final static Logger log = SimLogger.getLogger(MessageTimer.class);

	private int resendCounter = 0;

	private final AbstractChordNode masterNode;

	private final Message msg;

	private final AbstractChordContact receiver;

	private static int timeoutCount;

	public MessageTimer(AbstractChordNode masterNode, Message msg,
			AbstractChordContact receiver) {

		this.masterNode = masterNode;
		this.msg = msg;
		this.receiver = receiver;
	}

	@Override
	public void messageTimeoutOccured(int commId) {

		if (!masterNode.isPresent()) {
			return;
		}

		timeoutCount++;
		log.debug("time out msg = " + msg + " resendTime = " + resendCounter
				+ " sumTimeOut " + timeoutCount);
		if (resendCounter < ChordConfiguration.MESSAGE_RESEND) {
			resendCounter++;
			resendMessage();
		} else {
			log.debug("Receiver (" + receiver + ") of "
					+ msg.getClass().getSimpleName() + " did not answer after "
					+ resendCounter + " tries.");
			masterNode.getChordRoutingTable().receiveOfflineEvent(receiver);

		}
	}

	@Override
	public void receive(Message message, TransInfo senderInfo, int commId) {
		log.debug("receive ack msg = " + message);
	}

	private void resendMessage() {
		log.debug("resend msg = " + msg + " receiver "
				+ receiver.getOverlayID() + " resend times " + resendCounter);
		masterNode.getTransLayer().sendAndWait(msg, receiver.getTransInfo(),
				masterNode.getPort(), ChordConfiguration.TRANSPORT_PROTOCOL,
				this, ChordConfiguration.MESSAGE_TIMEOUT);
	}
}
