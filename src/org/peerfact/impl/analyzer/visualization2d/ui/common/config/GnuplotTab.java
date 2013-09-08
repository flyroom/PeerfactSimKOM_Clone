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

package org.peerfact.impl.analyzer.visualization2d.ui.common.config;

import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.peerfact.impl.analyzer.visualization2d.ui.common.config.gnuplot.GnuplotExecutableChooser;


/**
 * 
 * @author <info@peerfact.org>
 * @author Kalman Graffi <info@peerfact.org>
 * 
 * @version 08/18/2011
 * 
 */
public class GnuplotTab extends AbstractConfigTab {

	/**
	 * 
	 */
	private static final long serialVersionUID = -896723173833265655L;

	GnuplotExecutableChooser exec_ch;

	public GnuplotTab() {
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		this.add(createGnuplotExecChooser());

	}

	private Component createGnuplotExecChooser() {
		JPanel pane = new JPanel();
		pane.setBorder(new TitledBorder("Gnuplot executable"));
		pane.setLayout(new FlowLayout());

		pane
				.add(new JLabel(
						"Path to the binary files of Gnuplot"));

		exec_ch = new GnuplotExecutableChooser();
		pane.add(exec_ch);

		pane.add(new JLabel("e.g. "
				+ "'gnuplot' or 'C:\\Programme\\Gnuplot\\wgnuplot.exe'"));

		return pane;
	}

	@Override
	public void commitSettings() {
		exec_ch.commitChanges();
	}

}
