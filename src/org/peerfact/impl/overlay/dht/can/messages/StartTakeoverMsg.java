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

package org.peerfact.impl.overlay.dht.can.messages;

import org.peerfact.impl.overlay.dht.can.components.CanOverlayID;

/**
 * 
 * Gives the signal to start the TakeoverOperation
 * 
 * @author Bjoern Dollak <peerfact@kom.tu-darmstadt.de>
 * @version February 2010
 * 
 */
public class StartTakeoverMsg extends CanMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2502474422745284087L;

	/**
	 * Gives the signal to start the TakeoverOperation
	 * 
	 * @param sender
	 * @param receiver
	 */
	public StartTakeoverMsg(CanOverlayID sender, CanOverlayID receiver) {
		super(sender, receiver);
	}

	@Override
	public long getSize() {
		return super.getSize();
	}

}
