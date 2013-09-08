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

package org.peerfact.impl.analyzer.visualization2d.ui.common.gnuplot;

import java.awt.BorderLayout;
import java.util.List;
import java.util.Vector;

import javax.swing.JTextArea;

import org.apache.log4j.Logger;
import org.peerfact.impl.analyzer.visualization2d.api.metrics.overlay.OverlayEdgeMetric;
import org.peerfact.impl.analyzer.visualization2d.controller.Controller;
import org.peerfact.impl.analyzer.visualization2d.gnuplot.GnuplotExporter;
import org.peerfact.impl.analyzer.visualization2d.gnuplot.ResultTable;
import org.peerfact.impl.analyzer.visualization2d.metrics.MetricsBase;
import org.peerfact.impl.analyzer.visualization2d.model.overlay.VisOverlayEdge;
import org.peerfact.impl.analyzer.visualization2d.model.overlay.VisOverlayNode;
import org.peerfact.impl.analyzer.visualization2d.util.visualgraph.Edge;
import org.peerfact.impl.util.logging.SimLogger;


/**
 * 
 * @author <info@peerfact.org>
 * @author Kalman Graffi <info@peerfact.org>
 * 
 * @version 08/18/2011
 * 
 */
public class MetricConnectionVsTimeDialog extends
		MetricObjectVsTimeDialog<OverlayEdgeMetric, VisOverlayEdge> {

	private static Logger log = SimLogger
			.getLogger(MetricConnectionVsTimeDialog.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 6230110489407073653L;

	private static final String MANUAL_TXT = "Presents the selected edges according to a given metric in a graph. "
			+ "Please select the edges to consider and the desired metric, start time and "
			+ "end time, and the inteval in which the metric should be ploted.";

	public MetricConnectionVsTimeDialog() {
		JTextArea manual = new JTextArea(MANUAL_TXT);
		manual.setLineWrap(true);
		manual.setWrapStyleWord(true);
		manual.setOpaque(false);
		this.add(manual, BorderLayout.NORTH);
	}

	@Override
	public List<OverlayEdgeMetric> getMetrics() {
		Vector<OverlayEdgeMetric> result = new Vector<OverlayEdgeMetric>();
		for (OverlayEdgeMetric m : MetricsBase.forOverlayEdges()
				.getListOfAllMetrics()) {
			if (m.isNumeric())
			{
				result.add(m); // Only metrics that are numeric, are used.
			}
		}

		return result;
	}

	@Override
	protected ResultTable createTable(long[] values) {
		log.debug("Chosen metric: " + this.getSelectedMetric());
		return GnuplotExporter.generateOneMetricConnVsTime(this
				.getListOfSelectedObjects(), this.getSelectedMetric(),
				values[0], values[1], values[2]);
	}

	@Override
	public List<VisOverlayEdge> getObjects() {
		List<VisOverlayEdge> res = new Vector<VisOverlayEdge>();
		for (Edge<VisOverlayNode, VisOverlayEdge> e : Controller.getModel()
				.getOverlayGraph().edges) {
			res.add((VisOverlayEdge) e);
		}
		return res;
	}

}
