/*
 * Copyright (c) 2012-2013 Open Source Community - <http://www.peerfact.org>
 * Copyright (c) 2011-2012 University of Paderborn - UPB
 * Copyright (c) 2005-2011 KOM - Multimedia Communications Lab
 *
 * This file is part of PeerfactSim.
 * 
 * PeerfactSim is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 * 
 * PeerfactSim is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with PeerfactSim.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.peerfact.impl.overlay.dht.chord.adaptivechord.capacityRing.components;

import org.peerfact.api.common.Component;
import org.peerfact.api.common.ComponentFactory;
import org.peerfact.api.common.Host;
import org.peerfact.impl.overlay.AbstractOverlayNode.PeerStatus;
import org.peerfact.impl.overlay.dht.chord.base.components.ChordBootstrapManager;

/**
 * 
 * This class is used by Simulator to create ChordNode instance
 * 
 * @author Minh Hoang Nguyen
 * 
 */
public class AdaptiveChordNodeFactory implements ComponentFactory {
	short portData = 123;

	short portLoadBalancing = 1234;

	ChordBootstrapManager bootstrapData = new ChordBootstrapManager();

	ChordBootstrapManager bootstrapLoadBalancing = new ChordBootstrapManager();

	@Override
	public Component createComponent(Host host) {

		AdaptiveChordNode node = new AdaptiveChordNode(host.getTransLayer(),
				portData, portLoadBalancing, bootstrapData,
				bootstrapLoadBalancing);
		node.setPeerStatus(PeerStatus.TO_JOIN);
		return node;
	}
}
