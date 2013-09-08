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

import java.util.LinkedHashMap;
import java.util.Map;

import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.util.Tuple;
import org.peerfact.impl.util.guirunner.progress.ProgressUIAnalyzer;
import org.peerfact.impl.util.guirunner.progress.SimulationProgressView;
import org.peerfact.impl.util.livemon.LivemonCommonAnalyzer;

/**
 * 
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public class SimulationThread extends Thread {

	long seed;

	private ConfigFile f;

	public SimulationThread(ConfigFile f, long chosenSeed) {
		this.setName("SimulationThread");
		this.f = f;
		this.seed = chosenSeed;
	}

	@Override
	public void run() {
		SimulationProgressView view = SimulationProgressView.getInstance();
		Thread.setDefaultUncaughtExceptionHandler(view);
		view.setVisible(true);

		Simulator sim = Simulator.getInstance();
		Map<String, String> variables = new LinkedHashMap<String, String>(
				(int) ((f.getVariables().size() + 1) * 1.33));
		variables.put("seed", String.valueOf(seed));
		for (Tuple<String, String> t : f.getVariables()) {
			if (t.isChanged()) {
				variables.put(t.getA(), t.getB());
			}
		}

		sim.configure(f.getFile(), variables);
		Simulator.getMonitor().setAnalyzer(new LivemonCommonAnalyzer());
		Simulator.getMonitor().setAnalyzer(new ProgressUIAnalyzer(f.getFile()));
		sim.start(true);

		System.err.println("Simulation finished.");
	}

}
