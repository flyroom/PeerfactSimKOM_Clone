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

package org.peerfact.impl.overlay.dht.chord.adaptivechord.capacityRing.vis;

import java.math.BigInteger;

import org.peerfact.api.overlay.OverlayNode;
import org.peerfact.impl.analyzer.visualization2d.analyzer.positioners.generic.RingPositioner;
import org.peerfact.impl.overlay.dht.chord.adaptivechord.capacityRing.components.AdaptiveChordNode;
import org.peerfact.impl.overlay.dht.chord.adaptivechord.objectRing.components.ChordNode;


/**
 * Positions a chord-node in the ring.
 * 
 * @author Leo Nobach (ported to the new Chord by Dominik Stingl)
 *         <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class AdaptiveChordRingPositioner extends RingPositioner {

	private static final BigInteger MAX_KEY_SIZE;

	static {
		byte[] sha1hash_max = new byte[20];
		for (int i = 0; i < 20; i++) {
			sha1hash_max[i] = -1;
		}
		MAX_KEY_SIZE = new BigInteger(1, sha1hash_max);
		// Creates the biggest BigInteger with 20 bytes.
	}

	@Override
	protected double getPositionOnRing(OverlayNode<?, ?> nd) {
		if (nd instanceof AdaptiveChordNode) {

			ChordNode node;
			node = (ChordNode) ((AdaptiveChordNode) nd).getDataNetNode();

			return node.getOverlayID().getValue().doubleValue()
					/ MAX_KEY_SIZE.doubleValue();
		} else {
			return 0;
		}
	}

}
