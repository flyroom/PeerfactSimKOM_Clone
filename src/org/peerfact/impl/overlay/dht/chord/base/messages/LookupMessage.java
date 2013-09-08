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

import org.apache.log4j.Logger;
import org.peerfact.Constants;
import org.peerfact.api.common.Message;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordContact;
import org.peerfact.impl.overlay.dht.chord.base.components.ChordID;
import org.peerfact.impl.util.logging.SimLogger;

/**
 * This message is used to find the responder for specified key
 * 
 * @author Minh Hoang Nguyen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class LookupMessage extends AbstractRequestMessage implements
		IServiceMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8990068192172187267L;

	private static Logger log = SimLogger.getLogger(LookupMessage.class);

	private final int lookupId;

	private ChordID target;

	/**
	 * Notice
	 * 
	 * @param senderContact
	 *            : address of the lookup starter (do not always mean sender)
	 */
	public LookupMessage(AbstractChordContact senderContact,
			AbstractChordContact receiverContact, ChordID target, int lookupId,
			int hop) {
		super(senderContact, receiverContact);
		this.lookupId = lookupId;
		this.target = target;
		setHop(hop);
		log.trace("init LookupMessage id = " + lookupId);
	}

	@Override
	public Message getPayload() {
		return this;
	}

	@Override
	public long getSize() {
		return target.getTransmissionSize() + Constants.INT_SIZE
				+ super.getSize();
	}

	public int getLookupID() {
		return lookupId;
	}

	public ChordID getTarget() {
		return target;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " sender = " + getSender()
				+ " receiver = " + getReceiver() + " target = " + target
				+ " id = " + lookupId;
	}
}
