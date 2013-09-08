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

package org.peerfact.impl.util.guirunner.progress;

import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import org.peerfact.Constants;
import org.peerfact.impl.analyzer.visualization2d.util.Config;
import org.peerfact.impl.simengine.Simulator;


/**
 * 
 * @author <info@peerfact.org>
 * @author Kalman Graffi <info@peerfact.org>
 * 
 * @version 08/18/2011
 * 
 */
public class SimulationProgressView extends RichProgressView implements
		UncaughtExceptionHandler {

	public static SimulationProgressView inst = null;

	public static SimulationProgressView getInstance() {
		if (inst == null) {
			inst = new SimulationProgressView();
		}
		return inst;
	}

	/*
	 * Configuration paths
	 */
	static final String CONF_PATH = "GUIRunner/ProgressWindow/";

	static final String CONF_PATH_POSX = CONF_PATH + "PosX";

	static final String CONF_PATH_POSY = CONF_PATH + "PosY";

	/**
	 * 
	 */
	private static final long serialVersionUID = 1701832807987594850L;

	public static final String JOB_PREPARING = "Preparing...";

	public static final String JOB_SIMULATION = "Simulation...";

	public static final String JOB_EXCEPTION_PREFIX = "Exception: ";

	public static final String JOB_FINISHED = "Successfully finished";

	public static final Image frameIcon = new ImageIcon(
			Constants.ICONS_DIR + "/frame_icon.png").getImage();

	/**
	 * Angezeigter Fortschritt nach Vorbereitung
	 */
	public static final int PREPARING_PROGR = 100;

	static final long UPDATE_STEP_TIME = 100;

	String actualJobName = JOB_PREPARING;

	boolean simulationRunning = false;

	boolean simulationFinished = false;

	int snapshotQueueSize = 30;

	int snapshotInterval = 100;

	int snapshotIntervalCounter = 0;

	Queue<Snapshot> snaps = new LinkedBlockingQueue<Snapshot>();

	private Snapshot snap2use = new Snapshot(System.currentTimeMillis(), 0);

	static class Snapshot {
		Snapshot(long realTime, long virtTime) {
			this.realTime = realTime;
			this.virtTime = virtTime;
		}

		long realTime;

		long virtTime;
	}

	long lastUpdateTime = -1;

	int progressInt = 0;

	JButton pauseButton;

	boolean paused = false;

	// private String confName;

	protected SimulationProgressView() {
		super();
		this.setLocation(new Point(Config.getValue(CONF_PATH_POSX, 0), Config
				.getValue(CONF_PATH_POSY, 0)));
		this.setIconImage(frameIcon);
		this.setTitle("Simulation progress");
		this.setMaximum(1000 + PREPARING_PROGR);
		update();

		pauseButton = new JButton();
		pauseButton.setEnabled(false);
		pauseButton.setText("Pause");
		pauseButton.addActionListener(new PauseButtonController());

		buttonPanel.add(pauseButton);
	}

	public void setConfigurationName(String confName) {
		// this.confName = confName;
		this.setTitle(confName);
	}

	@Override
	public String getActualJobName() {
		return actualJobName;
	}

	@Override
	public int getProgress() {
		if (!simulationRunning) {
			return 0;
		} else if (simulationFinished) {
			return 1000 + PREPARING_PROGR;
		} else {
			return PREPARING_PROGR
					+ (int) ((double) getSimCurrentTime()
							/ (double) getSimEndTime() * 1000);
		}
	}

	@Override
	public void onCancel(boolean cancelled) {
		this.saveSettings();
		Config.writeXMLFile();
		System.exit(0);
	}

	public void notifySimulationRunning() {
		actualJobName = JOB_SIMULATION;
		simulationRunning = true;
		pauseButton.setEnabled(true);
	}

	public void notifySimulationFinished() {
		actualJobName = JOB_FINISHED;
		simulationFinished = true;
		pauseButton.setVisible(false);
		this.setFinished();
	}

	public void updateIfNecessary() {

		long now = System.currentTimeMillis();

		if (lastUpdateTime <= now - UPDATE_STEP_TIME) {
			update();
			lastUpdateTime = now;
		}
	}

	@Override
	public long getEstimatedTime() {
		if (!simulationRunning) {
			return -1;
		}

		long currentRealTime = System.currentTimeMillis();
		long currentVirtTime = getSimCurrentTime();

		snapshotIntervalCounter++;

		if (snapshotIntervalCounter >= snapshotInterval) {
			snaps.add(new Snapshot(currentRealTime, currentVirtTime));
			snapshotIntervalCounter++;
		}
		if (snaps.size() >= snapshotQueueSize) {
			snap2use = snaps.remove();
		}

		// log.debug("=========== " + snap2use.realTime + " === "+
		// snap2use.virtTime);

		double timePerVirt = (currentRealTime - snap2use.realTime)
				/ (double) (currentVirtTime - snap2use.virtTime);
		long result = (long) ((getSimEndTime() - getSimCurrentTime()) * timePerVirt);
		// log.debug("Estimated Time: " + result);
		return result;

	}

	protected static long getSimCurrentTime() {
		return Simulator.getCurrentTime() / Simulator.MILLISECOND_UNIT;
	}

	protected static long getSimEndTime() {
		return Simulator.getEndTime() / Simulator.MILLISECOND_UNIT;
	}

	/**
	 * Saves settings, like window size etc.
	 */
	public void saveSettings() {

		Config.setValue(CONF_PATH_POSX, this.getX());
		Config.setValue(CONF_PATH_POSY, this.getY());
	}

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		e.printStackTrace();
		actualJobName = JOB_EXCEPTION_PREFIX + e.getClass().getName();
		simulationFinished = true;
		pauseButton.setVisible(false);
		this.setFinished();
		this.update();
	}

	class PauseButtonController implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == pauseButton) {
				if (!paused) {
					Simulator.getScheduler().pause();
					pauseButton.setText("Continue");
					paused = true;
				} else {
					Simulator.getScheduler().unpause();
					pauseButton.setText("Pause");
					paused = false;
				}
			}
		}

	}

}
