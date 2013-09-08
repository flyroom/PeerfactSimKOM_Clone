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

package org.peerfact.impl.overlay.dht.chord.base.messages;

import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordContact;

/**
 * this message is used as reply for the <code>LookupMessage</code>
 * 
 * @author Minh Hoang Nguyen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class LookupReply extends AbstractReplyMsg implements ISetupMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5632426827645063999L;

	/*
	 * The result of the look up
	 */
	private final AbstractChordContact responsibleContact;

	private final LookupMessage request;

	public LookupReply(AbstractChordContact senderContact,
			AbstractChordContact receiverContact,
			AbstractChordContact responsibleContact,
			LookupMessage request) {
		super(senderContact, receiverContact);
		this.responsibleContact = responsibleContact;
		this.request = request;
	}

	public AbstractChordContact getResponsibleContact() {
		return responsibleContact;
	}

	public LookupMessage getRequest() {
		return request;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " successor "
				+ responsibleContact + " target " + request.getTarget();
	}

	@Override
	public long getSize() {
		return responsibleContact.getTransmissionSize() + super.getSize();
	}

}
