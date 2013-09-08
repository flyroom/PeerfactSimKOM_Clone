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

package org.peerfact.impl.analyzer.visualization2d.analyzer.positioners;

import java.util.List;

import org.peerfact.api.common.Host;
import org.peerfact.impl.analyzer.visualization2d.analyzer.OverlayAdapter;
import org.peerfact.impl.analyzer.visualization2d.util.visualgraph.Coords;


/**
 * A Positioner, which assumes all Overlay-Adapter and its Positioner to place
 * nodes
 * 
 * @author <info@peerfact.org>
 * @version 3.0, 20.11.2008
 * 
 */
public abstract class MultiPositioner {

	List<OverlayAdapter> adapters = null;

	/**
	 * Sets the OverlayAdapter used
	 * 
	 * @param adapters
	 */
	public void setAdapters(List<OverlayAdapter> adapters) {
		this.adapters = adapters;
	}

	/**
	 * Returns the Positioner to the XML-config-location posInCfg
	 * 
	 * @param posInCfg
	 * @return
	 */
	protected OverlayAdapter getAdapterAt(int posInCfg) {
		return adapters.get(posInCfg);
	}

	protected List<OverlayAdapter> getAllAdapters() {
		return adapters;
	}

	/**
	 * Returns the schematic position of host.
	 * 
	 * @param host
	 * @return the schematic position of host
	 */
	public abstract Coords getSchematicHostPosition(Host host);

}
