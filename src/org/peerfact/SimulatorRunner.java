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
import java.util.LinkedHashMap;
import java.util.Map;

import org.peerfact.impl.simengine.Simulator;

/**
 * This class is used to run simulations in <a
 * href="http://www.peerfact.org/">PeerfactSim.KOM</a>.
 * 
 * @author Konstantin Pussep <peerfact@kom.tu-darmstadt.de>
 * @author Sebastian Kaune
 * @version 3.0, 04.12.2007
 * 
 */
public class SimulatorRunner implements Runnable {

	private String[] args;

	/**
	 * Set private to prevent instantiation.
	 * 
	 */
	protected SimulatorRunner(String[] args) {
		this.args = args;
	}

	/**
	 * This method can be used to run a simulation. The expected arguments are:
	 * <code>config file</code> and an optional list of zero or many variable
	 * assignments<code>(variable=value)*")</code> or from the command line
	 * <code> java Scenario {config file} {variable=value}*</code>.
	 * 
	 * @param args
	 *            expect an array with the name of the configuration file and
	 *            optional variable assignments
	 */
	public static void main(String[] args) {
		new SimulatorRunner(args).run();
	}

	private static Map<String, String> parseVariables(String[] args, int startAt) {
		Map<String, String> variables = new LinkedHashMap<String, String>();
		for (int j = startAt; j < args.length; j++) {
			String[] tokens = args[j].split("=");
			assert tokens.length == 2 : "Bad format " + args[j];
			variables.put(tokens[0], tokens[1]);
		}
		return variables;
	}

	@Override
	public void run() {
		if (args.length >= 1) {
			Simulator sim = Simulator.getInstance();

			File configFile = new File(args[0]);
			int index = 1;
			if (!configFile.isFile()) {
				sim.setOuputDir(configFile);
				configFile = new File(args[1]);
				index++;
			}

			Map<String, String> variables = parseVariables(args, index);
			sim.configure(configFile, variables);
			configure(sim, configFile, variables);
			sim.start(shallThrowExceptions());
			System.exit(Simulator.isFinishedWithoutError() ? 0 : 1);
		} else {
			System.err
					.println("usage: SimulatorRunner <config file> (<variable=value>)*");
			System.err
					.println("usage: SimulatorRunner <ouput directory> <config file> (<variable=value>)*");
		}
	}

	@SuppressWarnings("static-method")
	protected boolean shallThrowExceptions() {
		return false;
	}

	protected void configure(Simulator sim, File configFile,
			Map<String, String> variables) {
		// Nothing to do here
	}
}
