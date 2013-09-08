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

package org.peerfact.impl.analyzer.visualization2d.metrics.overlay;

import org.peerfact.impl.analyzer.visualization2d.api.metrics.overlay.OverlayNodeMetric;
import org.peerfact.impl.analyzer.visualization2d.metrics.MetricsBase;

/**
 * Initializes and manages all node metrics.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * @author Kalman Graffi <info@peerfact.org>
 * 
 * @version 08/18/2011
 */
public class OverlayNodeMetrics extends MetricsBase<OverlayNodeMetric> {

	public OverlayNodeMetrics() {
		// TODO Enter the correct metrics for OverlayNodes

		/*
		 * this.addMetric(new NodeTestMetric1());
		 */

		this.addMetric(new NodeOverlays());

		this.addMetric(new NodeOverlaysRaw());

		this.addMetric(new NodeNeighbors());

		this.addMetric(new NetID());

		this.addMetric(new NodeMessagesPerSecond());

	}

	@Override
	public String toString() {
		return "Overlay, Noeds";
	}

}
