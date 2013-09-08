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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.util.LiveMonitoring;
import org.peerfact.impl.util.LiveMonitoring.ProgressValue;
import org.peerfact.impl.util.Tuple;
import org.peerfact.impl.util.livemon.LivemonCommonAnalyzer;
import org.peerfact.impl.util.logging.SimLogger;

/**
 * This class is used to run simulations in <a
 * href="http://www.peerfact.org/">PeerfactSim.KOM</a>.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * @version 3.0, 04.12.2007
 * 
 */
public class ProgressConsoleRunner extends SimulatorRunner {

	private static Logger log = SimLogger
			.getLogger(ProgressConsoleRunner.class);

	long displayInterval = 10000;

	Thread displayThread;

	Object treeLock = new Object();

	public static void main(String[] args) {
		new ProgressConsoleRunner(args).run();
	}

	@Override
	public void run() {
		Runtime.getRuntime().addShutdownHook(new Thread() {

			@Override
			public void run() {
				terminateDisplayThread();
			}

		});
		super.run();
		terminateDisplayThread();
	}

	public void terminateDisplayThread() {
		if (displayThread != null) {
			displayThread.interrupt();
		}
		displayThread = null;
	}

	public ProgressConsoleRunner(String[] args) {
		super(args);
	}

	@Override
	protected void configure(Simulator sim, File configFile,
			Map<String, String> variables) {
		Simulator.getMonitor().setAnalyzer(new LivemonCommonAnalyzer());
		displayThread = new Thread() {

			@Override
			public void run() {
				processDisplay();
			}

		};

		displayThread.start();
	}

	protected void processDisplay() {
		while (true) {
			try {
				Thread.sleep(displayInterval);
				dumpDisplay();
			} catch (InterruptedException e) {
				// Simulation is finished.
				break;
			}
		}
	}

	private static void dumpDisplay() {
		System.out
				.println("============================ Progress ============================");

		List<Tuple<String, String>> hList = new ArrayList<Tuple<String, String>>(
				20);
		for (ProgressValue val : LiveMonitoring.getProgressValues()) {
			hList.add(new Tuple<String, String>(val.getName(), val.getValue()));
		}

		int maxLength = 0;
		for (Tuple<String, String> h : hList) {
			int l = h.getA().length();
			if (maxLength < l) {
				maxLength = l;
			}
		}

		StringBuffer buf = new StringBuffer();

		for (Tuple<String, String> h : hList) {
			buf.append(h.getA());
			buf.append(": ");
			int l = maxLength - h.getA().length();
			for (int i = 0; i < l; i++) {
				buf.append(" ");
			}
			buf.append(h.getB());
			buf.append("\n");
		}

		log.debug(buf.toString());
		log.debug("==================================================================");
	}

	@Override
	protected boolean shallThrowExceptions() {
		return false;
	}

}
