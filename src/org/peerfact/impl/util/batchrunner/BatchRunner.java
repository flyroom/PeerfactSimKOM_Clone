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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;
import org.peerfact.Constants;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.util.logging.SimLogger;

/**
 * This class is used to run batch simulations in <a
 * href="http://www.peerfact.org/">PeerfactSim.KOM</a>.
 * 
 * @author Matthias Feldotto <info@peerfact.org>
 * @version 1.0, 02/03/2013
 */
public class BatchRunner implements Runnable {

	static Logger log = SimLogger.getLogger(BatchRunner.class);

	private String[] args;

	private File outputDir;

	private Map<String, String> variables;

	private LinkedHashMap<String, String> commonVariables;

	private List<Map<String, String>> variablesLists;

	ScriptGenerator scriptGenerator;

	public BatchRunner(String[] args) {
		this.args = args;
	}

	@Override
	public void run() {
		if (args.length >= 1) {
			// parse arguments
			int parallel = Integer.parseInt(args[0]);
			int count = Integer.parseInt(args[1]);
			File configFile = new File(args[2]);
			int index = 3;
			if (!configFile.isFile() || parallel == 0) {
				if (parallel != 0) {
					outputDir = configFile;
				}
				configFile = new File(args[3]);
				index++;
			}

			// create variable combinations
			variables = parseVariables(args, index);
			createVariablesLists();

			// generate output directory
			if (outputDir == null) {
				outputDir = new File(Constants.OUTPUTS_DIR,
						Simulator.generateOutputDirectoryName(null,
								System.currentTimeMillis(), configFile,
								commonVariables, null, null));
			}

			// create script generator
			scriptGenerator = new ScriptGenerator(outputDir, configFile,
					parallel, commonVariables);

			if (parallel == 0) {
				// only script creation
				createSimulationScript(count, new File(args[2]));
			} else if (parallel > 0) {
				// run simulations and merge results
				runSimulations(parallel, count);
			}
			if (parallel != 0) {
				// merge simulation results (independent or after run)
				mergeResults();
			}

		} else {
			System.err
					.println("usage: SimulatorRunner <parallel> <count> <config file> (<variable=value>)*");
			System.err
					.println("usage: SimulatorRunner <parallel> <count> <ouput directory> <config file> (<variable=value>)*");
		}
	}

	/**
	 * Parse variables from input arguments.
	 * 
	 * @param args
	 *            all input arguments
	 * @param startAt
	 *            start index of variables
	 * @return a map with variables
	 */
	private static Map<String, String> parseVariables(String[] args,
			int startAt) {
		Map<String, String> variables = new LinkedHashMap<String, String>();
		for (int j = startAt; j < args.length; j++) {
			String[] tokens = args[j].split("=");
			assert tokens.length == 2 : "Bad format " + args[j];
			variables.put(tokens[0], tokens[1]);
		}
		return variables;
	}

	/**
	 * Creates lists with all possible variable combinations.
	 */
	private void createVariablesLists() {
		commonVariables = new LinkedHashMap<String, String>();
		variablesLists = new ArrayList<Map<String, String>>();
		variablesLists.add(new LinkedHashMap<String, String>());

		for (Entry<String, String> variable : variables.entrySet()) {
			String[] options = variable.getValue().split(",");
			if (options.length == 1) {
				commonVariables.put(variable.getKey(), options[0]);
			} else {
				List<Map<String, String>> newVariablesLists = new ArrayList<Map<String, String>>();

				for (Map<String, String> variablesMap : variablesLists) {
					for (String option : options) {
						Map<String, String> newVariablesMap = new LinkedHashMap<String, String>(
								variablesMap);
						newVariablesMap.put(variable.getKey(), option);
						newVariablesLists.add(newVariablesMap);
					}
				}
				variablesLists = newVariablesLists;
			}
		}
	}

	/**
	 * Creates a simulation script to run all simulations
	 * 
	 * @param count
	 *            number of runs with same parameters
	 * @param scriptFile
	 *            the file to create
	 */
	private void createSimulationScript(int count, File scriptFile) {
		try {
			scriptGenerator.writeSimulationScript(variablesLists, count,
					variables, scriptFile);
		} catch (IOException e) {
			log.error("Exception while writing simulation script", e);
		}
	}

	/**
	 * Run all simulations in parallel.
	 * 
	 * @param parallel
	 *            number of parallel processes
	 * @param count
	 *            number of runs with same parameters
	 */
	private void runSimulations(int parallel, int count) {
		ExecutorService threadPool = Executors.newFixedThreadPool(parallel);

		// create threads
		List<SimulationThread> threads = new ArrayList<SimulationThread>();
		for (Map<String, String> variablesMap : variablesLists) {
			for (int i = 0; i < count; i++) {
				long seed = ScriptGenerator.generateSeed();
				File output = scriptGenerator.generateOutputDirectory(
						variablesMap, seed);
				SimulationThread simulationThread = new SimulationThread(
						scriptGenerator.generateSimulationCommand(variablesMap,
								seed, output),
						variablesMap, output);
				threads.add(simulationThread);
			}
		}

		try {
			// run all simulations
			while (threads.size() > 0) {
				List<Future<SimulationThread>> results = threadPool
						.invokeAll(threads);

				threads.clear();
				for (Future<SimulationThread> result : results) {
					SimulationThread thread = result.get();
					// handle errors
					if (thread.getErrorCode() != 0) {
						// mark error simulation
						boolean success = thread.getOutputDir().renameTo(
								new File(thread.getOutputDir().getParentFile(),
										thread.getOutputDir().getName()
												+ "__ERROR"));
						if (!success) {
							log.error("Error while renaming output directory of unsuccessful run");
						}

						// rerun failed simulations
						if (thread.getTries() < 5) {
							long seed = ScriptGenerator.generateSeed();
							File output = scriptGenerator
									.generateOutputDirectory(
											thread.getVariables(), seed);
							thread.updateCmd(scriptGenerator
									.generateSimulationCommand(
											thread.getVariables(), seed, output));
							thread.updateOutputDir(output);
							threads.add(thread);
						} else {
							log.error("5 unsuccessful runs of "
									+ thread.getVariables());
						}
					}
				}
			}

			threadPool.shutdown();
		} catch (InterruptedException e) {
			log.error("Execution interrupted", e);
		} catch (ExecutionException e) {
			log.error("Exception occurred during execution", e);
		}
	}

	/**
	 * Merges the simulation results.
	 */
	private void mergeResults() {
		ResultMerger merger = new ResultMerger(outputDir);
		merger.mergeResults(variablesLists);
	}

}
