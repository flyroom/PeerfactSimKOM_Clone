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

package org.peerfact.impl.util.batchrunner;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.peerfact.Constants;
import org.peerfact.SimulatorRunner;
import org.peerfact.impl.simengine.Simulator;

/**
 * Generator to create commands to execute the simulator as independent process.
 * 
 * @author Matthias Feldotto <info@peerfact.org>
 * @version 1.0, 02/03/2013
 */
public class ScriptGenerator {

	private File outputRoot;

	private File configFile;

	private long maxMemory;

	private int parallel;

	private Map<String, String> commonVariables;

	public ScriptGenerator(File outputRoot, File configFile, int parallel,
			Map<String, String> commonVariables) {
		this.outputRoot = outputRoot;
		this.configFile = configFile;
		this.parallel = parallel;
		this.commonVariables = commonVariables;
		this.maxMemory = Runtime.getRuntime().maxMemory();
	}

	/**
	 * Generates a new random seed.
	 * 
	 * @return a new random seed
	 */
	public static long generateSeed() {
		return new Random().nextLong();
	}

	public File generateOutputDirectory(
			Map<String, String> simulationVariables, long seed) {
		return new File(outputRoot, Simulator.generateOutputDirectoryName(
				"simulation", null, null, simulationVariables, null, null)
				+ File.separator
				+ Simulator.generateOutputDirectoryName(null, null, null, null,
						seed, null));
	}

	/**
	 * Generate command for one simulation.
	 * 
	 * @param simulationVariables
	 *            the concrete simulation variables
	 * @return the command string
	 */
	public String generateSimulationCommand(
			Map<String, String> simulationVariables, long seed, File outputDir) {

		Map<String, String> variables = new LinkedHashMap<String, String>(
				commonVariables);
		variables.putAll(simulationVariables);
		variables.put("seed", new Long(seed).toString());
		String variablesString = "";
		for (Entry<String, String> variable : variables.entrySet()) {
			variablesString += " " +
					variable.getKey() + "=" + variable.getValue();
		}

		String cmd = "java -Xmx" + (maxMemory / (parallel + 1)) + " -cp lib"
				+ File.separator + "*" + File.pathSeparator + "bin "
				+ SimulatorRunner.class.getName() + " "
				+ outputDir.getPath() + " " + configFile.getPath()
				+ variablesString;

		return cmd;
	}

	/**
	 * Create complete simulation script to use without batch runner.
	 * 
	 * @param variablesLists
	 *            all variable combinations
	 * @param count
	 *            number of runs with same parameters
	 * @param variables
	 *            the initial variables
	 * @param scriptFile
	 *            the file to create
	 * @throws IOException
	 *             if an exception occurs while writing the script
	 */
	public void writeSimulationScript(List<Map<String, String>> variablesLists,
			int count, Map<String, String> variables, File scriptFile)
			throws IOException {

		BufferedWriter writer = new BufferedWriter(new FileWriter(scriptFile));
		writer.write(Constants.LINE_END);

		// different simulation runs
		for (Map<String, String> variablesMap : variablesLists) {
			for (int i = 0; i < count; i++) {
				long seed = generateSeed();
				File outputDir = generateOutputDirectory(variablesMap, seed);
				writer.write(generateSimulationCommand(variablesMap, seed,
						outputDir));
				writer.write(Constants.LINE_END);
			}
			writer.write(Constants.LINE_END);
		}
		writer.write(Constants.LINE_END);

		// merge command
		String cmd = "java -Xmx" + maxMemory + " -cp lib"
				+ File.separator + "*" + File.pathSeparator + "bin "
				+ org.peerfact.BatchRunner.class.getName() + " -1 " + count
				+ " " + outputRoot.getPath() + " " + configFile.getPath();
		for (Entry<String, String> variable : variables.entrySet()) {
			cmd += " " + variable.getKey() + "=" + variable.getValue();
		}

		writer.write(cmd);
		writer.write(Constants.LINE_END);

		writer.flush();
		writer.close();
	}
}
