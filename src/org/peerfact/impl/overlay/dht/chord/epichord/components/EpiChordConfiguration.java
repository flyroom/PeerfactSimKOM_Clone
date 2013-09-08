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

package org.peerfact.impl.overlay.dht.chord.epichord.components;

import org.peerfact.impl.simengine.Simulator;


/**
 * This class contains all configurable parameters for EpiChord.
 * 
 * @author Matthias Feldotto <info@peerfact.org>
 *
 */
public class EpiChordConfiguration {

	/**
	 * The number of parallel lookup messages. 
	 */
	public static int P = 3;
	
	/**
	 * The number of sent better contacts for a key.
	 */
	public static int L = 3;

	/**
	 * The number of sufficient unexpired cache entries in each slice.
	 */
	public static int J = 1;
	
	/**
	 * The number of cache slices in each half of the ring. 
	 */
	public static int CHORD_CACHE_SLICE_COUNT_HINT = 5;
	
	/**
	 * The interval between two chord cache updates.
	 */
	public static long CHORD_CACHE_UPDATE_INTERVAL = 5 * Simulator.MINUTE_UNIT;
	
	/**
	 * The lifetime of a cache entry.
	 */
	public static long CHORD_CACHE_ENTRY_MAX_LIFETIME = 10 * Simulator.MINUTE_UNIT;

}
