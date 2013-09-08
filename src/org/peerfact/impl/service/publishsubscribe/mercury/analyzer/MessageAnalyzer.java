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

package org.peerfact.impl.service.publishsubscribe.mercury.analyzer;

import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.peerfact.api.analyzer.NetAnalyzer;
import org.peerfact.api.common.Message;
import org.peerfact.api.network.NetID;
import org.peerfact.api.network.NetMessage;
import org.peerfact.impl.service.publishsubscribe.mercury.messages.AbstractMercuryMessage;
import org.peerfact.impl.service.publishsubscribe.mercury.messages.MercuryPublication;
import org.peerfact.impl.util.logging.SimLogger;


/**
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * This part of the Simulator is not maintained in the current version of
 * PeerfactSim.KOM. There is no intention of the authors to fix this
 * circumstances, since the changes needed are huge compared to overall benefit.
 * 
 * If you want it to work correctly, you are free to make the specific changes
 * and provide it to the community.
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! !!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * 
 * 
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public class MessageAnalyzer implements NetAnalyzer {

	private static Logger log = SimLogger.getLogger(MessageAnalyzer.class);

	private boolean start = false;

	private Map<NetID, Long> aggrMsgSizes = new LinkedHashMap<NetID, Long>();

	private LinkedHashMap<String, Integer> loggedMessages = new LinkedHashMap<String, Integer>();

	private LinkedHashMap<String, Integer> loggedMessagesSizes = new LinkedHashMap<String, Integer>();

	private LinkedHashMap<NetID, Integer> numMessages = new LinkedHashMap<NetID, Integer>();

	private LinkedHashMap<String, LinkedHashMap<Integer, Integer>> numMessagesBySeqNo = new LinkedHashMap<String, LinkedHashMap<Integer, Integer>>();

	private LinkedHashMap<String, Integer> numMessagesByType = new LinkedHashMap<String, Integer>();

	private void addMessage(AbstractMercuryMessage msg) {

		if (!numMessagesBySeqNo.containsKey(msg.getClass().getName())) {
			numMessagesBySeqNo.put(msg.getClass().getName(),
					new LinkedHashMap<Integer, Integer>());
		}
		LinkedHashMap<Integer, Integer> type = numMessagesBySeqNo.get(msg
				.getClass()
				.getName());
		if (!type.containsKey(msg.getSeqNr())) {
			type.put(msg.getSeqNr(), 0);
		}

		type.put(msg.getSeqNr(), type.get(msg.getSeqNr()) + 1);

		String name = msg.getClass().getName();
		if (!loggedMessages.containsKey(name)) {
			loggedMessages.put(name, 0);
			loggedMessagesSizes.put(name, 0);
		}
		loggedMessages.put(name, loggedMessages.get(name) + 1);
		loggedMessagesSizes.put(name,
				loggedMessagesSizes.get(name) + (int) msg.getSize());

	}

	private void printNumMessagesBySeqNo() {
		for (String name : numMessagesBySeqNo.keySet()) {
			log.debug(name);

			for (Integer anz : numMessagesBySeqNo.get(name).values()) {
				if (anz > 3) {
					System.out.print(anz + " ; ");
				}
			}
		}
	}

	@Override
	public void netMsgSend(NetMessage msg, NetID id) {
		if (start) {
			Message msg2 = msg.getPayload().getPayload();

			// Message merc = msg2.getPayload();

			if (msg2 instanceof AbstractMercuryMessage) {
				AbstractMercuryMessage merm = (AbstractMercuryMessage) msg2;
				addMessage(merm);
			}

			if (msg2 instanceof MercuryPublication) {
				if (numMessages.containsKey(msg.getSender())) {
					numMessages.put(msg.getSender(),
							numMessages.get(msg.getSender()) + 1);
				} else {
					numMessages.put(msg.getSender(), 1);
				}
			}

			if (!numMessagesByType.containsKey(msg2.getClass().getSimpleName())) {
				numMessagesByType.put(msg2.getClass().getSimpleName(), 0);
			}
			numMessagesByType.put(msg2.getClass().getSimpleName(),
					numMessagesByType.get(msg2.getClass().getSimpleName()) + 1);

			// if(msg2.getPayload() instanceof MercurySubscription){
			// MercurySubscription ms = (MercurySubscription) msg2.getPayload();
			// ms.get
			// }

			// log.debug(Simulator.getFormattedTime(Simulator
			// .getCurrentTime())
			// + " from:"
			// + msg.getSender().toString()
			// + " to:"
			// + msg.getReceiver().toString()
			// + " type:"
			// + msg2.getClass().getSimpleName()
			// + " size:"
			// + msg2.getSize());

			// log.debug("#######MsgSend#########");
			// log.debug("Sender: " + msg.getSender());
			// log.debug("Receiver: " + msg.getReceiver());
			// log.debug("Payload Msg: "
			// + msg.getPayload().getPayload().getClass() + msg.hashCode());
		}
	}

	@Override
	public void netMsgReceive(NetMessage msg, NetID id) {
		if (start) {
			// log.debug("#######MsgReceived#########");
			// log.debug("Sender: " + msg.getSender());
			// log.debug("Receiver: " + msg.getReceiver());
			// log.debug("Payload Msg: "
			// + msg.getPayload().getPayload().getClass() + msg.hashCode());
			// if (msg.getPayload().getPayload().getPayload() instanceof
			// MercuryMessage) {
			if (!aggrMsgSizes.containsKey(id)) {
				aggrMsgSizes.put(id, (long) 0);
			}
			aggrMsgSizes.put(id, (aggrMsgSizes.get(id) + 1));
			// }
		}
	}

	@Override
	public void netMsgDrop(NetMessage msg, NetID id) {
		// not interested
	}

	@Override
	public void start() {
		this.start = true;

	}

	@Override
	public void stop(Writer output) {
		this.start = false;
		log.debug(this.loggedMessages.toString());
		log.debug(this.loggedMessagesSizes.toString());
		log.debug(this.numMessages.toString());
		printNumMessagesBySeqNo();
		log.debug(this.numMessagesByType.toString());
		// log.debug(aggrMsgSizes.toString());
	}

}
