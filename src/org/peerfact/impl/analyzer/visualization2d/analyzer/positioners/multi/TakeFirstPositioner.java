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

package org.peerfact.impl.analyzer.visualization2d.analyzer.positioners.multi;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.peerfact.api.common.Host;
import org.peerfact.api.overlay.OverlayNode;
import org.peerfact.impl.analyzer.visualization2d.analyzer.OverlayAdapter;
import org.peerfact.impl.analyzer.visualization2d.analyzer.positioners.MultiPositioner;
import org.peerfact.impl.analyzer.visualization2d.analyzer.positioners.SchematicPositioner;
import org.peerfact.impl.analyzer.visualization2d.util.visualgraph.Coords;


/**
 * A MultiPositioner, which creates the OverlayAdapter in order and the first to
 * have a position from the host, used to determine the position.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * @version 3.0, 20.11.2008
 * 
 */
public class TakeFirstPositioner extends MultiPositioner {

	Map<OverlayAdapter, SchematicPositioner> loadedPositioners = new LinkedHashMap<OverlayAdapter, SchematicPositioner>();

	@Override
	public Coords getSchematicHostPosition(Host host) {

		Iterator<OverlayNode<?, ?>> it = host.getOverlays();

		while (it.hasNext()) {
			OverlayNode<?, ?> node = it.next();
			for (OverlayAdapter adapter : this.getAllAdapters()) {
				if (adapter.isDedicatedOverlayImplFor(node.getClass())) {

					SchematicPositioner positioner = loadedPositioners
							.get(adapter);
					if (positioner == null) {
						positioner = adapter.getNewPositioner();
						loadedPositioners.put(adapter, positioner);
					}

					Coords pos = positioner
							.getSchematicHostPosition(host, node);
					if (pos != null) {
						return pos;
					}
				}
			}
		}
		return null; // There is no adapter that has a position for the node.
	}

}
