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

package org.peerfact.impl.analyzer.visualization2d.ui.common.config.gnuplot;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.peerfact.impl.analyzer.visualization2d.util.Config;


/**
 * 
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public class GnuplotExecutableChooser extends JPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7315226523509468698L;

	/**
	 * Initial value for the path of Gnuplot-Executable. Is overwritten by
	 * config.xml.
	 */
	private static final String INITIAL_VALUE = "gnuplot";

	private static final String CONF_PATH = "Gnuplot/ExecPath";

	JButton browseButton;

	JTextField tf;

	public GnuplotExecutableChooser() {
		this.setLayout(new BorderLayout());
		tf = new JTextField();
		tf.setText(getInitialValue());
		tf.setPreferredSize(new Dimension(300, 20));
		this.add(tf, BorderLayout.CENTER);
		browseButton = new JButton("Search...");
		browseButton.addActionListener(this);
		this.add(browseButton, BorderLayout.EAST);
	}

	private static String getInitialValue() {
		return Config.getValue(CONF_PATH, INITIAL_VALUE);
	}

	protected File askFileDialog() {
		JFileChooser chooser = new JFileChooser();
		int returnVal = chooser.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			return chooser.getSelectedFile();
		} else {
			return null;
		}
	}

	public void commitChanges() {
		Config.setValue(CONF_PATH, tf.getText());
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource() == browseButton) {
			File exe = askFileDialog();
			if (exe != null) {
				tf.setText(exe.getAbsolutePath());
			}
		}
	}

}
