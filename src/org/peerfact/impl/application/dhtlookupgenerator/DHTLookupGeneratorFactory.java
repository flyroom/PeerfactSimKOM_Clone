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

package org.peerfact.impl.application.dhtlookupgenerator;

import org.peerfact.api.common.Component;
import org.peerfact.api.common.ComponentFactory;
import org.peerfact.api.common.Host;
import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.api.overlay.OverlayID;
import org.peerfact.api.overlay.dht.DHTKey;
import org.peerfact.api.overlay.dht.DHTNode;
import org.peerfact.impl.util.stats.distributions.Distribution;

/**
 * This component factory creates instances of a lookup generator for DHT
 * overlays.
 * 
 * @author Matthias Feldotto <info@peerfact.org>
 * 
 * @version 01/14/2012
 */
public class DHTLookupGeneratorFactory implements ComponentFactory {

	private Distribution distribution;

	@Override
	public Component createComponent(Host host) {

		// Retrieve the KBRNode
		DHTNode<OverlayID<?>, OverlayContact<OverlayID<?>>, DHTKey<?>> node = (DHTNode<OverlayID<?>, OverlayContact<OverlayID<?>>, DHTKey<?>>) host
				.getOverlay(DHTNode.class);

		// Create the application
		DHTLookupGenerator application = new DHTLookupGenerator(node,
				distribution);

		return application;
	}

	public void setDistribution(Distribution distribution) {
		this.distribution = distribution;
	}

}
