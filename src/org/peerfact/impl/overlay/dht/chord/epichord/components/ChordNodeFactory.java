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

import org.peerfact.api.common.Component;
import org.peerfact.api.common.Host;
import org.peerfact.impl.overlay.AbstractOverlayNode.PeerStatus;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordNodeFactory;

/**
 * 
 * This class is used by Simulator to create ChordNode instance
 * 
 * @author Minh Hoang Nguyen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class ChordNodeFactory extends AbstractChordNodeFactory {

	@Override
	public Component createComponent(Host host) {
		ChordNode node = new ChordNode(host.getTransLayer(), port, bootstrap);
		node.setPeerStatus(PeerStatus.TO_JOIN);
		return node;
	}

	public static void setP(int p) {
		EpiChordConfiguration.P = p;
	}

	public static void setL(int l) {
		EpiChordConfiguration.L = l;
	}

	public static void setJ(int j) {
		EpiChordConfiguration.J = j;
	}

	public static void setCacheSliceCount(int count) {
		EpiChordConfiguration.CHORD_CACHE_SLICE_COUNT_HINT = count;
	}

	public static void setCacheUpdateInterval(long interval) {
		EpiChordConfiguration.CHORD_CACHE_UPDATE_INTERVAL = interval;
	}

	public static void setCacheLifetime(long time) {
		EpiChordConfiguration.CHORD_CACHE_ENTRY_MAX_LIFETIME = time;
	}
}
