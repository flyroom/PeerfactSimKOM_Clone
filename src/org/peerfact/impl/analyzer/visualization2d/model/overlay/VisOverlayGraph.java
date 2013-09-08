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

package org.peerfact.impl.analyzer.visualization2d.model.overlay;

import java.util.Vector;

import javax.swing.ImageIcon;

import org.peerfact.Constants;
import org.peerfact.impl.analyzer.visualization2d.api.metrics.BoundMetric;
import org.peerfact.impl.analyzer.visualization2d.api.metrics.overlay.OverlayUniverseMetric;
import org.peerfact.impl.analyzer.visualization2d.metrics.MetricsBase;
import org.peerfact.impl.analyzer.visualization2d.model.MetricObject;
import org.peerfact.impl.analyzer.visualization2d.model.ModelIterator;


/**
 * Graph of the overlay network.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class VisOverlayGraph
		extends
		org.peerfact.impl.analyzer.visualization2d.util.visualgraph.VisualGraph<VisOverlayNode, VisOverlayEdge>
		implements MetricObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1041089548085360017L;

	protected static final ImageIcon REPR_ICON = new ImageIcon(
			Constants.ICONS_DIR + "/model/OverlayUniverse16_16.png");

	/**
	 * Graph of the overlays not specified for visualization.
	 * 
	 * @param a
	 * @param b
	 */

	public VisOverlayGraph() {
		super();
	}

	@Override
	public Vector<BoundMetric> getBoundMetrics() {
		Vector<BoundMetric> res = new Vector<BoundMetric>();
		for (OverlayUniverseMetric m : MetricsBase.forOverlayUniverse()
				.getListOfAllMetrics()) {
			res.add(m.getBoundTo());
		}
		return res;
	}

	@Override
	public String toString() {
		return "Scenario";
	}

	@Override
	public ImageIcon getRepresentingIcon() {
		return REPR_ICON;
	}

	@Override
	public void iterate(ModelIterator it) {
		// Nothing to do, the graph is invisible and unclickable yet
	}
}
