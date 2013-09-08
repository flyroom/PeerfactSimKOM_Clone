/*
 * Copyright (c) 2005-2010 KOM – Multimedia Communications Lab
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

/**
 * 
 */
package org.peerfact.impl.overlay.dht.chord.maliciouschord.components;

import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordNode;
import org.peerfact.impl.overlay.dht.chord.base.messages.LookupMessage;

/**
 * @author Tobias Wybranietz
 * @author Thim Strothmann
 */
public class MaliciousChordMessageHandlerNoForwarding extends
		AbstractMaliciousChordMessageHandler {

	public MaliciousChordMessageHandlerNoForwarding(AbstractChordNode node,
			int leadtime) {
		super(node, leadtime);
		// TODO Auto-generated constructor stub
	}

	@Override
	void doMaliciousHandleLookupMsg(LookupMessage lookupMsg) {

		// Do nothing

	}

}
