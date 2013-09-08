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
import org.peerfact.impl.analyzer.visualization2d.util.Config;


/**
 * Closes the application
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * @author Kalman Graffi <info@peerfact.org>
 * 
 * @version 08/18/2011
 */

public class CloseApplication implements Command {

	public static final String CONF_PATH = "UI/MainWindow/ForceClose";

	@Override
	public void execute() {

		if (!forceClose()) {

			if (Controller.getModel() != null
					&& Controller.getModel().isUnsaved()) {
				switch (JOptionPane
						.showConfirmDialog(
								Controller.getUIMainWindow(),
								"The current visualization has not been saved, should it be saved now?",
								"Close", JOptionPane.YES_NO_CANCEL_OPTION)) {
								case JOptionPane.CANCEL_OPTION:
									return;
								case JOptionPane.NO_OPTION:
									break;
								case JOptionPane.YES_OPTION:
									new SaveToFile().execute();

				}
			}

			/*
			 * / / "Really terminate?" is too stupid. Just when you often its
			 * Impl. tests.
			 * 
			 * if (JOptionPane.showConfirmDialog(Controller.getUIMainWindow(),
			 * "Really terminate?", ""Terminated, JOptionPane.YES_NO_OPTION) ==
			 * JOptionPane.YES_OPTION) Controller.deinit();
			 */

		}

		Controller.deinit();

	}

	private static boolean forceClose() {
		return Config.getValue(CONF_PATH, false);
	}
}
