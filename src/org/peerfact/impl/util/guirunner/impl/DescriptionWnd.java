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

package org.peerfact.impl.util.guirunner.impl;

import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import org.peerfact.impl.util.guirunner.impl.RunnerController.IRunnerCtrlListener;


/**
 * 
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public class DescriptionWnd {

	ConfigFile selectedFile;

	JTextPane p;

	public DescriptionWnd(RunnerController ctrl) {
		selectedFile = ctrl.getSelectedFile();
		ctrl.addListener(this.new RunnerCtrlListenerImpl());
		p = new JTextPane();
		String desc = selectedFile != null ? selectedFile.getDesc() : null;
		p.setText(desc == null ? "" : desc);
	}

	public JComponent getComponent() {
		JScrollPane pnl = new JScrollPane(p);
		pnl.setPreferredSize(new Dimension(230, 230));
		return pnl;
	}

	class RunnerCtrlListenerImpl implements IRunnerCtrlListener {

		@Override
		public void newFileSelected(ConfigFile f) {
			String desc = f != null ? f.getDesc() : null;
			selectedFile = f;
			p.setText(desc == null ? "" : desc);
			p.revalidate();
			p.repaint();
		}

	}

}
