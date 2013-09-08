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

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.peerfact.impl.analyzer.visualization2d.api.metrics.Metric;
import org.peerfact.impl.analyzer.visualization2d.metrics.MetricsPack.MetricsPackPart;
import org.peerfact.impl.analyzer.visualization2d.metrics.overlay.OverlayEdgeMetrics;
import org.peerfact.impl.analyzer.visualization2d.metrics.overlay.OverlayNodeMetrics;
import org.peerfact.impl.analyzer.visualization2d.metrics.overlay.OverlayUniverseMetrics;


/**
 * Manages and initializes all metrics, provides various filtering mechanisms.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @param <TMetric>
 * @version 05/06/2011
 */
public abstract class MetricsBase<TMetric extends Metric> {

	// Static singleton part -------------------------------

	private static MetricsPack dynMetrics = new MetricsPack();

	private static OverlayNodeMetrics overlay_node = new OverlayNodeMetrics();

	private static OverlayEdgeMetrics overlay_edge = new OverlayEdgeMetrics();

	private static OverlayUniverseMetrics overlay_univ = new OverlayUniverseMetrics();

	/*
	 * private static NetworkNodeMetrics network_node = new
	 * NetworkNodeMetrics(); private static NetworkEdgeMetrics network_edge =
	 * new NetworkEdgeMetrics();
	 */

	private static Vector<MetricsBase<? extends Metric>> all_metrics = new Vector<MetricsBase<?>>();

	static {
		setDynamicMetricsPack(new MetricsPack());
	}

	public static void init() {
		all_metrics.add(overlay_node);
		all_metrics.add(overlay_edge);
		all_metrics.add(overlay_univ);
		// setDynamicMetricsPack(new MetricsPack());
	}

	public static List<Metric> getAllMetrics() {
		Vector<Metric> res = new Vector<Metric>();

		for (MetricsBase<? extends Metric> mb : all_metrics) {
			for (Metric m : mb.getListOfAllMetrics()) {
				res.add(m);
			}
		}

		return res;
	}

	public static void saveSettings() {
		for (MetricsBase<? extends Metric> mb : all_metrics) {
			for (Metric m : mb.getListOfAllMetrics()) {
				m.saveSettings();
			}
		}
	}

	public static MetricsPack getDynamicMetricsPack() {
		return MetricsBase.dynMetrics;
	}

	public static void setDynamicMetricsPack(MetricsPack dyn_metrics) {
		MetricsBase.dynMetrics = dyn_metrics;
		MetricsBase.forOverlayEdges().setDynamicMetrics(dyn_metrics.getEdges());
		MetricsBase.forOverlayNodes().setDynamicMetrics(dyn_metrics.getNodes());
		MetricsBase.forOverlayUniverse().setDynamicMetrics(
				dyn_metrics.getUniverse());
	}

	public static OverlayNodeMetrics forOverlayNodes() {
		return overlay_node;
	}

	public static OverlayEdgeMetrics forOverlayEdges() {
		return overlay_edge;
	}

	public static OverlayUniverseMetrics forOverlayUniverse() {
		return overlay_univ;
	}

	// Object part -------------------------------

	public MetricsPackPart<TMetric> dynamicMetrics;

	public Set<TMetric> metrics = new LinkedHashSet<TMetric>();

	public LinkedHashSet<TMetric> metrics_activated = new LinkedHashSet<TMetric>();

	public MetricsBase() {
		// Nothing to do
	}

	public MetricsBase(MetricsPackPart<TMetric> dynamic_metrics) {
		setDynamicMetrics(dynamic_metrics);
	}

	/**
	 * Adds a metric of the base. Should only be called at startup, before any
	 * play.
	 * 
	 * @param m
	 */
	public void addMetric(TMetric metric) {
		metrics.add(metric);
	}

	/**
	 * a list of all metrics defined in this base
	 * 
	 * @return
	 */
	public Vector<TMetric> getListOfAllMetrics() {

		Vector<TMetric> result = new Vector<TMetric>(metrics);
		result.addAll(getDynamicMetrics().getMetrics());
		return result;
	}

	/**
	 * a list of all active metrics defined in this base
	 * 
	 * @return
	 */
	public Collection<TMetric> getListOfActivatedMetrics() {
		Vector<TMetric> activated = new Vector<TMetric>();
		for (TMetric m : getListOfAllMetrics()) {
			if (m.isActivated()) {
				activated.add(m);
			}
		}
		return activated;
	}

	@Override
	public abstract String toString();

	public MetricsPackPart<TMetric> getDynamicMetrics() {
		return dynamicMetrics;
	}

	public void setDynamicMetrics(MetricsPackPart<TMetric> dynamicMetrics) {
		this.dynamicMetrics = dynamicMetrics;
	}

}
