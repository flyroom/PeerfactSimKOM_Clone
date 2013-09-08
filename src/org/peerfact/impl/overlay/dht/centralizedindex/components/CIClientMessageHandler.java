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

package org.peerfact.impl.overlay.dht.centralizedindex.components;

import org.apache.log4j.Logger;
import org.peerfact.api.common.Message;
import org.peerfact.api.transport.TransMessageListener;
import org.peerfact.impl.transport.TransMsgEvent;
import org.peerfact.impl.util.logging.SimLogger;


/**
 * Implementing a centralized DHT overlay, whose organization of the centralized
 * index is similar to the distributed index of Chord
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 08.12.2008
 */
public class CIClientMessageHandler implements TransMessageListener {

	private static Logger log = SimLogger
			.getLogger(CIServerMessageHandler.class);

	private CIClientNode client;

	public CIClientMessageHandler(CIClientNode clientNode) {
		this.client = clientNode;
	}

	@Override
	public void messageArrived(TransMsgEvent receivingEvent) {
		Message msg = receivingEvent.getPayload();
		log.info("[Client]" + client.toString()
				+ "Received message is of type "
				+ msg.getClass().getSimpleName());

	}

}
