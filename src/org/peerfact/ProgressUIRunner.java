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

package org.peerfact;

import java.io.File;
import java.util.Map;

import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.util.guirunner.progress.ProgressUIAnalyzer;
import org.peerfact.impl.util.guirunner.progress.SimulationProgressView;
import org.peerfact.impl.util.livemon.LivemonCommonAnalyzer;

/**
 * With this class run as a Java Application, the simulator can be started like
 * SimulatorRunner from console, except that the AWT/Swing progress dialog (like
 * in GUIRunner) is started and shows the current progress of the simulation
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class ProgressUIRunner extends SimulatorRunner {

	public static void main(String[] args) {
		new ProgressUIRunner(args).run();
	}

	public ProgressUIRunner(String[] args) {
		super(args);
		SimulationProgressView view = SimulationProgressView.getInstance();
		view.setVisible(true);
		Thread.setDefaultUncaughtExceptionHandler(view);
	}

	@Override
	protected void configure(Simulator sim, File configFile,
			Map<String, String> variables) {
		Simulator.getMonitor().setAnalyzer(new LivemonCommonAnalyzer());
		Simulator.getMonitor().setAnalyzer(new ProgressUIAnalyzer(configFile));
	}

	@Override
	protected boolean shallThrowExceptions() {
		return true;
	}

}
