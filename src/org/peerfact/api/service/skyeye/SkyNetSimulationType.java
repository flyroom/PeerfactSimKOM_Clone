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

package org.peerfact.api.service.skyeye;

/**
 * This class is used to determine the type of the overlay, on which SkyNet
 * runs. It implements the singleton-pattern, and allows to choose the type of
 * an underlying overlay out of a set of possible overlay-types, which is
 * defined by the enumeration <code>SimulationType</code>.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 15.11.2008
 * 
 */
public class SkyNetSimulationType {

	/**
	 * This enumeration contains the overlay-types, on which SkyNet can actually
	 * run.
	 * 
	 * @author Dominik Stingl
	 * @version 1.0, 15.11.2008
	 * 
	 */
	public enum SimulationType {
		NAPSTER_SIMULATION, CHORD_SIMULATION, KADEMLIA_SIMULATION;
	}

	private static SimulationType simulationType;

	private static SkyNetSimulationType sim;

	private SkyNetSimulationType(SimulationType type) {
		SkyNetSimulationType.simulationType = type;
	}

	public static void createInstance(SimulationType type) {
		if (sim == null) {
			sim = new SkyNetSimulationType(type);
		}
	}

	public static SimulationType getSimulationType() {
		return simulationType;
	}
}
