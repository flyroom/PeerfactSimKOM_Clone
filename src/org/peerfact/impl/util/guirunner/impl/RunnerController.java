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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;

import org.apache.log4j.Logger;
import org.peerfact.impl.analyzer.visualization2d.controller.Controller;
import org.peerfact.impl.util.guirunner.GUIRunner;
import org.peerfact.impl.util.guirunner.seed.SeedDetermination;
import org.peerfact.impl.util.logging.SimLogger;


/**
 * 
 * Runs the simulator or invokes operations on the view.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * @author Kalman Graffi <info@peerfact.org>
 * 
 * @version 08/18/2011
 * 
 */
public class RunnerController implements ActionListener {

	private static Logger log = SimLogger.getLogger(RunnerController.class);

	LastOpened lastOpened = null;

	ConfigFile selectedFile = null;

	JButton launchButton = null;

	private GUIRunner runner;

	private JButton startVisBtn = null;

	private List<IRunnerCtrlListener> listeners = new LinkedList<IRunnerCtrlListener>();

	SeedDetermination det = new SeedDetermination();

	/**
	 * Runs the simulator with the specified config file string.
	 * 
	 * @param configFile
	 */
	private void runSimulator() {
		new SimulationThread(selectedFile, det.getChosenSeed()).start();
	}

	public void setLastOpened(LastOpened lastOpened) {
		this.lastOpened = lastOpened;
	}

	public SeedDetermination getDetermination() {
		return det;
	}

	/**
	 * Called when the user wants to start the simulation.
	 */
	public void invokeRunSimulator() {

		if (selectedFile == null)
		{
			return; // No file is selected!
		}

		if (lastOpened != null) {
			lastOpened.append(selectedFile);
		}
		lastOpened.saveToFile();

		log.debug("GUIRunner: Starting simulator with "
				+ selectedFile.getFile().getAbsolutePath());
		runner.disposeRunner();

		det.saveSettings();

		runSimulator();
	}

	/**
	 * Called when the user has selected a file.
	 * 
	 * @param file
	 */
	public void selectFile(ConfigFile file) {
		selectedFile = file;

		if (launchButton != null) {
			launchButton.setEnabled(selectedFile != null);
		}
		if (file != null) {
			det.loadFile(selectedFile);
		}
		newFileSelected(file);
	}

	/**
	 * Sets the launch button that invokes the launch of the simulation
	 * 
	 * @param b
	 */
	public void setLaunchButton(JButton b) {
		b.addActionListener(this);
		this.launchButton = b;
	}

	/**
	 * Sets the button that invokes the standalone execution of the
	 * visualization.
	 * 
	 * @param b
	 */
	public void setStartVisBtn(JButton b) {
		b.addActionListener(this);
		this.startVisBtn = b;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource() == launchButton) {
			invokeRunSimulator();
		} else if (arg0.getSource() == startVisBtn) {
			invokeStartVis();
		}
	}

	/**
	 * Starts the visualization stand-alone.
	 */
	private void invokeStartVis() {
		log.debug("GUIRunner: Starting Visualization");
		runner.disposeRunner();
		Controller.init();
	}

	public void addListener(IRunnerCtrlListener l) {
		listeners.add(l);
	}

	public void removeListener(IRunnerCtrlListener l) {
		listeners.remove(l);
	}

	void newFileSelected(ConfigFile f) {
		for (IRunnerCtrlListener l : listeners) {
			l.newFileSelected(f);
		}
	}

	/**
	 * Sets the main window of the GUIRunner.
	 * 
	 * @param runner
	 */
	public void setMainWindow(GUIRunner runner) {
		this.runner = runner;
	}

	public static interface IRunnerCtrlListener {

		public void newFileSelected(ConfigFile f);

	}

	public ConfigFile getSelectedFile() {
		return selectedFile;
	}

}
