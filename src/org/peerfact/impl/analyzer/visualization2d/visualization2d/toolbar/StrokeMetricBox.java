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

package org.peerfact.impl.analyzer.visualization2d.visualization2d.toolbar;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.peerfact.Constants;
import org.peerfact.impl.analyzer.visualization2d.api.metrics.overlay.OverlayEdgeMetric;
import org.peerfact.impl.analyzer.visualization2d.metrics.MetricsBase;
import org.peerfact.impl.analyzer.visualization2d.model.VisDataModel;
import org.peerfact.impl.analyzer.visualization2d.visualization2d.Simple2DVisualization;


/**
 * 
 * @author <info@peerfact.org>
 * @author Kalman Graffi <info@peerfact.org>
 * 
 * @version 08/18/2011
 * 
 */
public class StrokeMetricBox extends JPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7507793063049512488L;

	private static final String TOOLTIP = "Edge width";

	private static final String NULL_OBJECT = "--constant--";

	private static final ImageIcon ICON = new ImageIcon(
			Constants.ICONS_DIR + "/misc/edge_size16_16.png");

	Simple2DVisualization vis;

	JComboBox box;

	public StrokeMetricBox(Simple2DVisualization vis) {
		box = new JComboBox(appendNull());
		box.setToolTipText(TOOLTIP);
		box.addActionListener(this);
		// this.setEditable(true);
		this.vis = vis;

		this.setToolTipText(TOOLTIP);
		this.setLayout(new BorderLayout());
		this.add(box, BorderLayout.CENTER);
		this.add(new JLabel(ICON), BorderLayout.WEST);

	}

	private static Vector<Object> appendNull() {
		Vector<Object> v = new Vector<Object>();
		v.add(NULL_OBJECT);
		for (OverlayEdgeMetric m : MetricsBase.forOverlayEdges()
				.getListOfAllMetrics()) {
			if (m.isNumeric()) {
				v.add(m);
			}
		}
		return v;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (box.getSelectedItem() != NULL_OBJECT) {
			vis.setStrokeMetric((OverlayEdgeMetric) box.getSelectedItem());
		} else {
			vis.setStrokeMetric(null);
		}
		VisDataModel.needsRefresh();
	}

}
