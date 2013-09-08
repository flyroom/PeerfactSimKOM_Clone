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

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JTabbedPane;

import org.peerfact.Constants;
import org.peerfact.impl.analyzer.visualization2d.controller.Controller;
import org.peerfact.impl.analyzer.visualization2d.util.Config;


/**
 * 
 * @author <info@peerfact.org>
 * @author Kalman Graffi <info@peerfact.org>
 * 
 * @version 08/18/2011
 * 
 */
public class GnuplotMainWindow extends JDialog implements WindowListener {

	/*
	 * configuration paths
	 */
	static final String CONF_PATH = "UI/GnuplotExportWindow/";

	static final String CONF_PATH_WIDTH = CONF_PATH + "Width";

	static final String CONF_PATH_HEIGHT = CONF_PATH + "Height";

	static final String CONF_PATH_POSX = CONF_PATH + "PosX";

	static final String CONF_PATH_POSY = CONF_PATH + "PosY";

	static final String CONF_PATH_SELECTED = CONF_PATH + "SelectedTab";

	/**
	 * 
	 */
	private static final long serialVersionUID = -8220820572696470496L;

	JTabbedPane tabbedPane;

	public GnuplotMainWindow() {
		this.addWindowListener(this);

		this.setModal(true);
		this.setTitle("PeerfactSim.KOM | Gnuplot export");
		this.setIconImage(Controller.getUIMainWindow().getIconImage());

		tabbedPane = new JTabbedPane();
		ImageIcon metric = new ImageIcon(Constants.ICONS_DIR
				+ "/misc/Metric16_16.png");
		ImageIcon peer = new ImageIcon(
				Constants.ICONS_DIR + "/model/OverlayNode16_16.png");
		ImageIcon connection = new ImageIcon(
				Constants.ICONS_DIR + "/model/OverlayEdge16_16.png");

		tabbedPane.addTab("Metrics over time", metric,
				new MetricsVsTimeDialog(),
				"Exports chosen metric of a time interval");
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_M);

		tabbedPane.addTab("Peers over time", peer,
				new MetricPeerVsTimeDialog(),
				"Presentation of peers according to a metric over time.");
		tabbedPane.setMnemonicAt(1, KeyEvent.VK_P);

		tabbedPane
				.addTab(
						"Edges over time",
						connection,
						new MetricConnectionVsTimeDialog(),
						"Presentation of edges between peers according to a metric over time.");
		tabbedPane.setMnemonicAt(1, KeyEvent.VK_P);

		this.getContentPane().add(tabbedPane);

		this.setSize(Config.getValue(CONF_PATH_WIDTH, 500), Config.getValue(
				CONF_PATH_HEIGHT, 500));
		this.setLocation(new Point(Config.getValue(CONF_PATH_POSX, 0), Config
				.getValue(CONF_PATH_POSY, 0)));

		tabbedPane.setSelectedIndex(Config.getValue(CONF_PATH_SELECTED, 0));
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
		// Nothing to do
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		// Nothing to do
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		this.saveSettings();
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		// Nothing to do
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
		// Nothing to do
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
		// Nothing to do
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
		// Nothing to do
	}

	/**
	 * Saves settings such as window size or similar
	 */
	protected void saveSettings() {
		Config.setValue(CONF_PATH_WIDTH, this.getWidth());
		Config.setValue(CONF_PATH_HEIGHT, this.getHeight());
		Config.setValue(CONF_PATH_POSX, this.getX());
		Config.setValue(CONF_PATH_POSY, this.getY());
		Config.setValue(CONF_PATH_SELECTED, tabbedPane.getSelectedIndex());
	}

}
