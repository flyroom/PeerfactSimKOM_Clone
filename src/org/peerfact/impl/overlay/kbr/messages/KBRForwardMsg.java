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

import org.peerfact.Constants;
import org.peerfact.api.common.Message;
import org.peerfact.api.overlay.OverlayID;
import org.peerfact.api.overlay.OverlayKey;
import org.peerfact.impl.overlay.AbstractOverlayMessage;

/**
 * this message is used to transport "forward" instruction to the next node
 * 
 * @author Yue Sheng (edited by Julius Rueckert) <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 */
public class KBRForwardMsg<T extends OverlayID<?>, K extends OverlayKey<?>>
		extends AbstractOverlayMessage<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3655298504326683958L;

	private final K key;

	private final Message msg;

	private int hops;

	/**
	 * @param sender
	 * @param receiver
	 * @param key
	 * @param msg
	 * @param numberOfHops
	 */
	public KBRForwardMsg(T sender, T receiver,
			K key, Message msg, int numberOfHops) {
		super(sender, receiver);
		this.key = key;
		this.msg = msg;
		this.hops = numberOfHops;
	}

	/**
	 * @param sender
	 * @param receiver
	 * @param key
	 * @param msg
	 */
	public KBRForwardMsg(T sender, T receiver,
			K key, Message msg) {
		super(sender, receiver);
		this.key = key;
		this.msg = msg;
		this.hops = 0;
	}

	@Override
	public long getSize() {
		return msg.getSize() + key.getTransmissionSize() + Constants.INT_SIZE;
	}

	public K getKey() {
		return key;
	}

	@Override
	public Message getPayload() {
		return msg;
	}

	/**
	 * @return the current number of hops
	 */
	public int getHops() {
		return hops;
	}

	/**
	 * Increases the number of hops by one
	 */
	public void incHops() {
		hops++;
	}

	@Override
	public String toString() {
		return "ForwardMsg[key=" + key + ", msq=" + msg + ", hops=" + hops
				+ "]";
	}

}
