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

package org.peerfact.impl.analyzer.visualization2d.ui.common.DetailsPane;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.peerfact.impl.analyzer.visualization2d.api.visualization.VisActionListener;
import org.peerfact.impl.analyzer.visualization2d.model.MetricObject;
import org.peerfact.impl.analyzer.visualization2d.model.overlay.VisOverlayEdge;
import org.peerfact.impl.analyzer.visualization2d.model.overlay.VisOverlayGraph;
import org.peerfact.impl.analyzer.visualization2d.model.overlay.VisOverlayNode;
import org.peerfact.impl.analyzer.visualization2d.ui.common.DetailsPane.EdgeFilter.EdgeFilterPane;
import org.peerfact.impl.analyzer.visualization2d.util.Config;
import org.peerfact.impl.analyzer.visualization2d.util.SplitPaneConfigSaver;


/**
 * Section of the window to display peer or connection details, its metrics or
 * similar
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class DetailsPane extends JPanel implements VisActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5520015429136616404L;

	protected static final String SPLITTER_CONF_PATH = "UI/DetailsPane/Splitter1Pos";

	protected static final String SPLITTER2_CONF_PATH = "UI/DetailsPane/Splitter2Pos";

	ObjectMetricsTable object_metrics_table = new ObjectMetricsTable();

	MetricsTable metrics_table = new MetricsTable();

	EdgeFilterPane edgeFilterPane = new EdgeFilterPane();

	Component top_pane;

	public DetailsPane() {

		this.setLayout(new BorderLayout());
		this.setPreferredSize(new Dimension(300, 100));

		JScrollPane object_sp = new JScrollPane(object_metrics_table);

		// clickedOn(Controller.getVisApi().getSelectedObject());

		JScrollPane metrics_sp = new JScrollPane(metrics_table);
		setTitledBorder(metrics_sp, "Metrics");

		JScrollPane filter_sp = new JScrollPane(edgeFilterPane);
		setTitledBorder(filter_sp, "Filter");

		JSplitPane sp2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, metrics_sp,
				filter_sp);

		sp2.setDividerLocation(Config.getValue(SPLITTER2_CONF_PATH, 250));

		sp2.addPropertyChangeListener(new SplitPaneConfigSaver(
				SPLITTER2_CONF_PATH));

		JSplitPane sp = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				object_sp, sp2);

		sp.setDividerLocation(Config.getValue(SPLITTER_CONF_PATH, 350));

		sp.addPropertyChangeListener(new SplitPaneConfigSaver(
				SPLITTER_CONF_PATH));

		this.add(sp);

	}

	static void setTitledBorder(JComponent comp, String title) {
		Border loweredbevel = BorderFactory
				.createEtchedBorder(EtchedBorder.LOWERED);
		TitledBorder timePTitle = BorderFactory.createTitledBorder(
				loweredbevel, title);
		timePTitle.setTitlePosition(TitledBorder.TOP);
		comp.setBorder(timePTitle);
	}

	/**
	 * Sets c as TopPane. If c is null, no TopPane is set.
	 * 
	 * @param c
	 */
	private void makeNewTopPane(Component c) {
		if (top_pane != null) {
			this.remove(top_pane);
		}

		top_pane = c;

		if (c != null) {
			this.add(top_pane, BorderLayout.NORTH);
		}

		this.setVisible(false);
		this.setVisible(true);
	}

	@Override
	public void clickedOn(MetricObject o) {

		// log.debug("Showing details for " + o);
		object_metrics_table.setMetrics(o.getBoundMetrics());

		if (o instanceof VisOverlayNode) {
			makeNewTopPane(new OverlayNodeDescriptionPane((VisOverlayNode) o));
		} else if (o instanceof VisOverlayEdge) {
			makeNewTopPane(new OverlayEdgeDescriptionPane((VisOverlayEdge) o));
		} else if (o instanceof VisOverlayGraph) {
			makeNewTopPane(new OverlayGraphDescriptionPane((VisOverlayGraph) o));
		} else {
			makeNewTopPane(null);
		}

	}

	public void reset() {
		this.revalidate();
		this.repaint();
	}

}
