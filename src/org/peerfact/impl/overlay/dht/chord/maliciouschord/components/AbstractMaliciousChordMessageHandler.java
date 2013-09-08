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
import org.peerfact.impl.overlay.dht.chord.chord.components.ChordMessageHandler;
import org.peerfact.impl.simengine.Simulator;

/**
 * @author Tobias Wybranietz
 * @author Thim Strothmann
 */
public abstract class AbstractMaliciousChordMessageHandler extends
		ChordMessageHandler {

	/**
	 * How long should the malicious nodes behave normally?
	 */
	private int leadTimeForMaliciousNodes;

	/**
	 * @param node
	 */
	public AbstractMaliciousChordMessageHandler(AbstractChordNode node,
			int leadTime) {
		super(node);
		leadTimeForMaliciousNodes = leadTime;
		// TODO Auto-generated constructor stub
	}

	/**
	 * Method handle LookupMessage only
	 * 
	 * @param lookupMsg
	 */
	@Override
	public void handleLookupMsg(LookupMessage lookupMsg) {

		// Check the simulation time
		if ((Simulator.getCurrentTime() / Simulator.MINUTE_UNIT) < leadTimeForMaliciousNodes) {
			// I am a nice malicious node and let everybody join the network:
			super.handleLookupMsg(lookupMsg);

		} else {
			// I am no longer nice. Malicious behaviour now!
			doMaliciousHandleLookupMsg(lookupMsg);

		}

	}

	abstract void doMaliciousHandleLookupMsg(LookupMessage lookupMsg);

}
