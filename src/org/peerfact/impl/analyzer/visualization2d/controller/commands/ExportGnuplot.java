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

package org.peerfact.impl.analyzer.visualization2d.controller.commands;

import javax.swing.JOptionPane;

import org.peerfact.impl.analyzer.visualization2d.controller.Controller;
import org.peerfact.impl.analyzer.visualization2d.ui.common.gnuplot.GnuplotMainWindow;


/**
 * Opens the dialog window Gnuplot.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * @author Kalman Graffi <info@peerfact.org>
 * 
 * @version 08/18/2011
 */
public class ExportGnuplot implements Command {

	@Override
	public void execute() {

		if (Controller.getModel() == null) {

			JOptionPane.showMessageDialog(Controller.getUIMainWindow(),
					"No recoding available. Please load a recoding first.",
					"Gnuplot-Export", JOptionPane.ERROR_MESSAGE);
		} else {
			startNormal();
		}

	}

	private static void startNormal() {
		try {

			/*
			 * FIXME: Why no Err-output?
			 */

			// Controller.getUIMainWindow().setEnabled(false);
			new GnuplotMainWindow().setVisible(true);

			// test1();

		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}

}
