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

import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * 
 * @author <info@peerfact.org>
 * @author Kalman Graffi <info@peerfact.org>
 * 
 * @version 08/18/2011
 * 
 */
public class CreateGnuplotFileButtons extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JButton gnuplotButton;

	public CreateGnuplotFileButtons() {

		this.setLayout(new FlowLayout());
		this.setSize(250, 50);
		gnuplotButton = new JButton("Export Gnuplot graph...");
		gnuplotButton.setFont(gnuplotButton.getFont().deriveFont(Font.BOLD));
		this.add(gnuplotButton);
	}

	public JButton getGnuplotButton() {
		return gnuplotButton;
	}

}
