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

package org.peerfact.impl.analyzer.visualization2d.metrics;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

import org.peerfact.impl.analyzer.visualization2d.api.metrics.Metric;
import org.peerfact.impl.analyzer.visualization2d.api.metrics.overlay.OverlayEdgeMetric;
import org.peerfact.impl.analyzer.visualization2d.api.metrics.overlay.OverlayNodeMetric;
import org.peerfact.impl.analyzer.visualization2d.api.metrics.overlay.OverlayUniverseMetric;


/**
 * 
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public class MetricsPack implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6782000462960510486L;

	protected MetricsPackPart<OverlayNodeMetric> nodes = new MetricsPackPart<OverlayNodeMetric>();

	protected MetricsPackPart<OverlayEdgeMetric> edges = new MetricsPackPart<OverlayEdgeMetric>();

	protected MetricsPackPart<OverlayUniverseMetric> universe = new MetricsPackPart<OverlayUniverseMetric>();

	/**
	 * Removes all instances of the contained metrics. They are instantiated at
	 * the next invocation.
	 */
	public void clearInstances() {
		nodes.clearInstances();
		edges.clearInstances();
		universe.clearInstances();
	}

	public MetricsPackPart<OverlayNodeMetric> getNodes() {
		return nodes;
	}

	public MetricsPackPart<OverlayEdgeMetric> getEdges() {
		return edges;
	}

	public MetricsPackPart<OverlayUniverseMetric> getUniverse() {
		return universe;
	}

	public static class MetricsPackPart<M extends Metric> implements
			Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 6094347201051011597L;

		Set<Class<? extends M>> metrics = new LinkedHashSet<Class<? extends M>>();

		Set<M> metricsInst = null;

		public void addMetric(Class<? extends M> m) {
			metrics.add(m);
			if (metricsInst != null) {
				try {
					metricsInst.add(m.newInstance());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		public void clearInstances() {
			this.metricsInst = null;
			// System.gc();
		}

		public Set<M> getMetrics() {
			if (metricsInst == null) {
				metricsInst = new LinkedHashSet<M>();
				try {
					for (Class<? extends M> m : metrics) {
						metricsInst.add(m.newInstance());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			return metricsInst;

		}

	}

}
