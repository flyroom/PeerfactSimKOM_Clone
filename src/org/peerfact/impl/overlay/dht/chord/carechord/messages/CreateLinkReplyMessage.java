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

public class CreateLinkReplyMessage extends AbstractRequestMessage implements
		IStabilizeMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5860904567164221426L;

	public CreateLinkReplyMessage(AbstractChordContact senderContact,
			AbstractChordContact receiverContact) {
		super(senderContact, receiverContact);
	}

}
