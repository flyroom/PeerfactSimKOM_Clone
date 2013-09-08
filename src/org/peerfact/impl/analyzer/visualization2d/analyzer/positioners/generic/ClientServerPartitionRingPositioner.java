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

package org.peerfact.impl.analyzer.visualization2d.analyzer.positioners.generic;

import org.peerfact.api.common.Host;
import org.peerfact.api.overlay.OverlayNode;
import org.peerfact.impl.analyzer.visualization2d.util.visualgraph.Coords;

/**
 * The server is located in the center, the clients are located around it.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * @version 3.0, 15.11.2008
 * 
 */
public abstract class ClientServerPartitionRingPositioner extends
		FCFSPartitionRingPositioner {

	@Override
	public Coords getSchematicHostPosition(Host host, OverlayNode<?, ?> nd) {

		if (isServer(host, nd)) {
			return new Coords(RingPositioner.offset_x, RingPositioner.offset_y);
		} else {
			return super.getSchematicHostPosition(host, nd);
		}

	}

	/**
	 * Checks whether the submitted host is the submitted OverlayNode of the
	 * server
	 * 
	 * @param host
	 * @param nd
	 * @return
	 */
	public abstract boolean isServer(Host host, OverlayNode<?, ?> nd);

}
