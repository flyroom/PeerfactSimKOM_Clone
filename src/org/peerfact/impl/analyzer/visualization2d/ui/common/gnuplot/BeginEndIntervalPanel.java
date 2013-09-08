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

import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * 
 * @author <info@peerfact.org>
 * @author Kalman Graffi <info@peerfact.org>
 * 
 * @version 08/18/2011
 * 
 */
public class BeginEndIntervalPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -993784021768406482L;

	private JLabel startLabel, endLabel, intervalLabel;

	private JTextField startField, endField, intervalField;

	private long lb_start = Long.MIN_VALUE, lb_end = Long.MIN_VALUE,
			lb_interval = Long.MIN_VALUE;

	private long ub_start = Long.MAX_VALUE, ub_end = Long.MAX_VALUE,
			ub_interval = Long.MAX_VALUE;

	public BeginEndIntervalPanel() {
		createTimeChoice();
	}

	public BeginEndIntervalPanel(long lb_start, long lb_end, long lb_interval,
			long ub_start, long ub_end, long ub_interval) {
		this.lb_start = lb_start;
		this.lb_end = lb_end;
		this.lb_interval = lb_interval;
		this.ub_start = ub_start;
		this.ub_end = ub_end;
		this.ub_interval = ub_interval;
		createTimeChoice();
	}

	public void setLowerandUpperBound(long lb_start, long lb_end,
			long lb_interval, long ub_start, long ub_end, long ub_interval) {
		this.lb_start = lb_start;
		this.lb_end = lb_end;
		this.lb_interval = lb_interval;
		this.ub_start = ub_start;
		this.ub_end = ub_end;
		this.ub_interval = ub_interval;

		if (lb_start != Long.MIN_VALUE) {
			startField.setText(Long.toString(lb_start));
		}
		if (ub_end != Long.MAX_VALUE) {
			endField.setText(Long.toString(ub_end));
		}
		if (lb_interval != Long.MIN_VALUE) {
			intervalField.setText(Long.toString(lb_interval));
		}
	}

	private void createTimeChoice() {
		this.setLayout(new GridLayout(3, 2));
		this.setSize(200, 66);

		startLabel = new JLabel("Start time (s)");
		this.add(startLabel);
		startLabel.setHorizontalAlignment(SwingConstants.CENTER);
		startField = new JTextField(8);
		startField.setName("Start time");
		if (lb_start != Long.MIN_VALUE) {
			intervalField.setText(Long.toString(lb_start));
		}
		this.add(startField);

		endLabel = new JLabel("End time (s)");
		this.add(endLabel);
		endLabel.setHorizontalAlignment(SwingConstants.CENTER);
		endField = new JTextField(8);
		endField.setName("End time");
		if (ub_end != Long.MAX_VALUE) {
			endField.setText(Long.toString(ub_end));
		}
		this.add(endField);

		intervalLabel = new JLabel("Interval (s)");
		this.add(intervalLabel);
		intervalLabel.setHorizontalAlignment(SwingConstants.CENTER);
		intervalField = new JTextField(8);
		intervalField.setName("Interval");
		if (lb_interval != Long.MIN_VALUE) {
			intervalField.setText(Long.toString(lb_interval));
		}
		this.add(intervalField);

	}

	private long getValue(JTextField field) {
		long value = Long.MIN_VALUE;
		try {
			value = Long.parseLong(field.getText());
			return value;
		} catch (NumberFormatException ex) {
			JOptionPane.showMessageDialog(this, field.getName()
					+ " must be a number");
			return Long.MIN_VALUE;
		}
	}

	// returns the value of StartField if possible, otherwise returns
	// Long.MIN_VALUE
	public long getStartValue() {
		long start = getValue(startField);
		if (start == Long.MIN_VALUE) {
			return Long.MIN_VALUE;
		}
		if ((start < lb_start) || (start > ub_start)) {
			JOptionPane.showMessageDialog(this,
					"Start time has an invalid value");
		}
		return start;
	}

	public long getEndValue() {
		long end = getValue(endField);
		if (end == Long.MIN_VALUE) {
			return Long.MIN_VALUE;
		}
		if ((end < lb_end) || (end > ub_end)) {
			JOptionPane
					.showMessageDialog(this, "End time has an invalid value");
		}
		return end;
	}

	public long getIntervalValue() {
		long interval = getValue(intervalField);
		if (interval == Long.MIN_VALUE) {
			return Long.MIN_VALUE;
		}
		if ((interval < lb_interval) || (interval > ub_interval)) {
			JOptionPane
					.showMessageDialog(this, "Interval has an invalid value");
		}
		return interval;
	}

}
