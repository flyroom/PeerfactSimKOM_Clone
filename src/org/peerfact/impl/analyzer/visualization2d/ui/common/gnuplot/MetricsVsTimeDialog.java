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
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.peerfact.impl.analyzer.visualization2d.api.metrics.BoundMetric;
import org.peerfact.impl.analyzer.visualization2d.controller.Controller;
import org.peerfact.impl.analyzer.visualization2d.gnuplot.GnuplotExporter;
import org.peerfact.impl.analyzer.visualization2d.gnuplot.ResultTable;


/**
 * 
 * @author <info@peerfact.org>
 * @author Kalman Graffi <info@peerfact.org>
 * 
 * @version 08/18/2011
 * 
 */
public class MetricsVsTimeDialog extends MetricVsTimeBasisPanel {

	private static final long serialVersionUID = 8884409185702564261L;

	// private JPanel checkBoxPanel;

	private Collection<BoundMetric> m = new Vector<BoundMetric>();

	private ArrayList<JCheckBox> ListOfCheckBox;

	private static final String MANUAL_TXT = "Presents the selected metrics corresponding to a given object in a graph. "
			+ "Please select the desired metric, start time and "
			+ "end time, and the inteval in which the metric should be ploted.";

	public MetricsVsTimeDialog() {
		for (BoundMetric metr : Controller.getVisApi().getSelectedObject()
				.getBoundMetrics()) {
			if (metr.isNumeric()) {
				m.add(metr);
			}
		}
		createContentPanel();

		JTextArea manual = new JTextArea(MANUAL_TXT);
		manual.setLineWrap(true);
		manual.setWrapStyleWord(true);
		manual.setOpaque(false);
		this.add(manual, BorderLayout.NORTH);
	}

	public void createContentPanel() {
		createCheckBox();
	}

	private void createCheckBox() {
		/*
		 * checkBoxPanel = new JPanel(); checkBoxPanel.setLayout(null);
		 * checkBoxPanel.setBounds(55, 20, 200, 200);
		 */

		ListOfCheckBox = new ArrayList<JCheckBox>();

		JPanel checkBox = new JPanel();
		// checkBox.setBorder(BorderFactory.createLoweredBevelBorder());
		checkBox.setBackground(Color.WHITE);
		checkBox.setPreferredSize(new Dimension(150, 150));
		checkBox.setBounds(50, 50, 200, 200);
		checkBox.setLayout(new BoxLayout(checkBox, BoxLayout.PAGE_AXIS));

		for (BoundMetric boundMetric : m) {
			JCheckBox box = new JCheckBox(boundMetric.getName());
			ListOfCheckBox.add(box);
			checkBox.add(box);
		}

		this.add(new JScrollPane(checkBox), BorderLayout.CENTER);
	}

	@Override
	protected ResultTable createTable(long[] values) {
		// <<These are selected in the CheckBox.
		Collection<BoundMetric> selectedMetrics = new Vector<BoundMetric>();
		for (JCheckBox checbox : ListOfCheckBox) {
			if (checbox.isSelected()) {
				String metric = checbox.getText();
				for (BoundMetric boundMetric : m) {
					if (boundMetric.getName().equals(metric)) {
						selectedMetrics.add(boundMetric);
						break;
					}
				}
			}
		}
		return GnuplotExporter.generateResultTable(selectedMetrics,
				values[0], values[1], values[2]);
	}

}
