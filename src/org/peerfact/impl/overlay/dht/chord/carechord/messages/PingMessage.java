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

package org.peerfact.impl.overlay.dht.chord.carechord.messages;

import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordContact;
import org.peerfact.impl.overlay.dht.chord.base.messages.AbstractRequestMessage;
import org.peerfact.impl.overlay.dht.chord.base.messages.IStabilizeMessage;

/**
 * This is just a ping message - you send it and will get a pongMessage back if
 * the host is still up.
 * 
 * @author wette
 * @version 1.0, 06/09/2011
 */
public class PingMessage extends AbstractRequestMessage implements
		IStabilizeMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3520902104090345067L;

	public PingMessage(AbstractChordContact senderContact,
			AbstractChordContact receiverContact) {
		super(senderContact, receiverContact);
	}

}
