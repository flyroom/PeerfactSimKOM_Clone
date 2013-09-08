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

package org.peerfact.impl.overlay.dht.chord.base.components;

import org.peerfact.api.common.Message;
import org.peerfact.api.transport.TransMessageListener;
import org.peerfact.impl.overlay.dht.chord.base.messages.LookupMessage;
import org.peerfact.impl.transport.TransMsgEvent;

/**
 * ChordMessageHandler handle incoming Overlay Messages.
 * 
 * @author Minh Hoang Nguyen <peerfact@kom.tu-darmstadt.de>
 * @author Philip Wette <info@peerfact.org>
 * 
 * @version 18/08/2011
 */
public abstract class AbstractChordMessageHandler implements
		TransMessageListener {

	@Override
	public abstract void messageArrived(TransMsgEvent receivingEvent);

	public abstract void handleLookupMsg(LookupMessage lookupMsg);

	protected abstract void sendReply(Message reply,
			TransMsgEvent receivingEvent);

	protected abstract void sendAck(Message msg, TransMsgEvent receivingEvent,
			boolean look);

}
