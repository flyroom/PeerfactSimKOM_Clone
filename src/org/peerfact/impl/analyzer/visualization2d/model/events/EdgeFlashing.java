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

package org.peerfact.impl.analyzer.visualization2d.model.events;

import java.io.Serializable;

import org.peerfact.impl.analyzer.visualization2d.controller.Controller;
import org.peerfact.impl.analyzer.visualization2d.model.overlay.FlashOverlayEdge;


/**
 * A connection appears shortly and then disappears.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * @version 3.0, 19.10.2008
 * 
 */
public class EdgeFlashing extends Event implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 111039272563104948L;

	private FlashOverlayEdge edge;

	public EdgeFlashing(FlashOverlayEdge edge) {
		this.edge = edge;
	}

	@Override
	public void makeHappen() {
		Controller.getVisApi().queueFlashEvent(edge);
	}

	@Override
	public void undoMakeHappen() {
		// maybe once again:
		// Controller.getModel().getOverlayGraph().addEdge(edge);
		// is a matter of taste, which looks better.
		// Would make sense if reverse run is implemented.
	}

	@Override
	public String toString() {
		return "EdgeFlashing: " + edge.toString();
	}

}
