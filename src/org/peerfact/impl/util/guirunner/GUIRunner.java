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

package org.peerfact.impl.util.guirunner;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.WindowConstants;

import org.peerfact.Constants;
import org.peerfact.impl.analyzer.visualization2d.util.Config;
import org.peerfact.impl.analyzer.visualization2d.util.GlobalKeyEventDispatcher;
import org.peerfact.impl.util.guirunner.impl.DescriptionWnd;
import org.peerfact.impl.util.guirunner.impl.DirView;
import org.peerfact.impl.util.guirunner.impl.LastOpened;
import org.peerfact.impl.util.guirunner.impl.RunnerController;
import org.peerfact.impl.util.guirunner.impl.VariableFieldEditor;
import org.peerfact.impl.util.guirunner.seed.SeedChooser;


/**
 * A window to select a configuration file to run PFS from. Useful for
 * developers switching between different configuration files many times, as
 * well as for presentations etc.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * @author Kalman Graffi <info@peerfact.org>
 * 
 * @version 08/18/2011
 * 
 */
public class GUIRunner extends JFrame implements WindowListener, KeyListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6126914669745711438L;

	/*
	 * Konfigurationspfade
	 */
	static final String CONF_PATH = "GUIRunner/MainWindow/";

	static final String CONF_PATH_WIDTH = CONF_PATH + "Width";

	static final String CONF_PATH_HEIGHT = CONF_PATH + "Height";

	static final String CONF_PATH_POSX = CONF_PATH + "PosX";

	static final String CONF_PATH_POSY = CONF_PATH + "PosY";

	public static final String DEFAULT_CONFIG_DIR = "./config";

	public static final Image frameIcon = new ImageIcon(
			Constants.ICONS_DIR + "/frame_icon.png").getImage();

	private static final String SPLITTER_CONF_PATH = CONF_PATH + "SplitterPos";

	public static LastOpened lastOpened = new LastOpened();

	public static RunnerController ctrl = new RunnerController();

	JSplitPane splitPane;

	public GUIRunner() {
		ctrl.setLastOpened(lastOpened);
		ctrl.setMainWindow(this);

		this.setTitle("PeerfactSim.KOM - Select Launch Configuration");
		this.setIconImage(frameIcon);

		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(this);

		this.setSize(Config.getValue(CONF_PATH_WIDTH, 600), Config.getValue(
				CONF_PATH_HEIGHT, 600));
		this.setLocation(new Point(Config.getValue(CONF_PATH_POSX, 0), Config
				.getValue(CONF_PATH_POSY, 0)));

		this.setLayout(new BorderLayout());

		JScrollPane sp = new JScrollPane(new DirView(ctrl, lastOpened));

		this.add(new ButtonBar(), BorderLayout.SOUTH);

		JPanel sidepanel = new JPanel();
		sidepanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		SeedChooser seed = new SeedChooser(ctrl.getDetermination());
		seed.setBorder(BorderFactory.createTitledBorder("Seed options"));
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 0;
		c.fill = GridBagConstraints.BOTH;
		sidepanel.add(seed, c);

		JComponent vars = new VariableFieldEditor(ctrl).getComponent();
		vars.setBorder(BorderFactory.createTitledBorder("Variables"));
		c.gridy = 1;
		c.weighty = 1;
		sidepanel.add(vars, c);

		JComponent desc = new DescriptionWnd(ctrl).getComponent();
		desc.setBorder(BorderFactory.createTitledBorder("Description"));
		c.gridy = 2;
		c.weighty = 1;
		sidepanel.add(desc, c);

		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sidepanel, sp);
		splitPane.setDividerLocation(Config.getValue(SPLITTER_CONF_PATH, 400));
		this.add(splitPane, BorderLayout.CENTER);

		GlobalKeyEventDispatcher disp = new GlobalKeyEventDispatcher(this);
		disp.addKeyListener(this);

		this.setVisible(true);
	}

	public class ButtonBar extends JPanel implements ActionListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = -7482502225920138689L;

		private JButton cancelBtn;

		public ButtonBar() {
			this.setLayout(new FlowLayout());

			JButton launchBtn = new JButton("Start Simulation");
			ctrl.setLaunchButton(launchBtn);
			this.add(launchBtn);

			JButton startVisBtn = new JButton("Only Visualization");
			ctrl.setStartVisBtn(startVisBtn);
			this.add(startVisBtn);

			cancelBtn = new JButton("Cancel");
			this.add(cancelBtn);

			cancelBtn.addActionListener(this);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == cancelBtn) {
				closeRunner();
			}
		}

	}

	public void closeRunner() {
		disposeRunner();
		System.exit(0);
	}

	public void disposeRunner() {
		saveSettings();
		this.setVisible(false);
		this.dispose();
		Config.writeXMLFile();
	}

	/**
	 * Speichert Einstellungen wie Fenstergröße o.ä.
	 */
	public void saveSettings() {

		Config.setValue(CONF_PATH_WIDTH, this.getWidth());
		Config.setValue(CONF_PATH_HEIGHT, this.getHeight());
		Config.setValue(CONF_PATH_POSX, this.getX());
		Config.setValue(CONF_PATH_POSY, this.getY());
		Config.setValue(SPLITTER_CONF_PATH, splitPane.getDividerLocation());
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
		closeRunner();
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

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			ctrl.invokeRunSimulator();
		} else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			this.closeRunner();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// Nothing to do

	}

	@Override
	public void keyTyped(KeyEvent e) {
		// Nothing to do
	}

}
