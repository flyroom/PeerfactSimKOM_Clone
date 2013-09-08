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

package org.peerfact.impl.overlay.kbr.messages;

import org.peerfact.api.common.Message;
import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.api.overlay.OverlayID;
import org.peerfact.api.overlay.OverlayKey;
import org.peerfact.api.overlay.kbr.KBRForwardInformation;

/**
 * 
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public class KBRForwardInformationImpl<T extends OverlayID<?>, S extends OverlayContact<T>, K extends OverlayKey<?>>
implements KBRForwardInformation<T, S, K> {

	private Message msg;

	private S nextHopAgent;

	private K key;

	public KBRForwardInformationImpl(K key, Message msg,
			S nextHopAgent) {
		this.msg = msg;
		this.key = key;
		this.nextHopAgent = nextHopAgent;
	}

	@Override
	public Message getMessage() {
		return msg;
	}

	@Override
	public S getNextHopAgent() {
		return nextHopAgent;
	}

	@Override
	public K getKey() {
		return key;
	}

	@Override
	public void setMessage(Message msg) {
		this.msg = msg;
	}

	@Override
	public void setNextHopAgent(S nextHopAgent) {
		this.nextHopAgent = nextHopAgent;
	}

	@Override
	public void setKey(K key) {
		this.key = key;
	}

}
