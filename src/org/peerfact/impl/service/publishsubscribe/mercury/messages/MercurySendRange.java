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

package org.peerfact.impl.service.publishsubscribe.mercury.messages;

import org.peerfact.impl.service.publishsubscribe.mercury.MercuryContact;

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
 * Message to Send and Receive the Range of a node (alwas send own Info too)
 * 
 * @author Bjoern Richerzhagen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class MercurySendRange extends AbstractMercuryMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5392228144739123270L;

	private MercuryContact origin = null;

	private boolean reply = false;

	/**
	 * Get or Send Range. If reply is set, receiver sends an answer with
	 * reply=false.
	 * 
	 * @param origin
	 *            MercuryContact of sender
	 * @param reply
	 *            if set, this Message will be answered with a MercurySendRange
	 *            Message where reply is set to false
	 */
	public MercurySendRange(MercuryContact origin, boolean reply) {
		this.origin = origin;
		this.reply = reply;
	}

	public MercuryContact getOrigin() {
		return origin;
	}

	public boolean needsReply() {
		return reply;
	}

	@Override
	public long getSize() {
		return super.getSize() + origin.getTransmissionSize();
	}

}
