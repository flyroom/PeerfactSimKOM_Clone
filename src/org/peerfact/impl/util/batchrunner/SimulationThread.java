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

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;
import org.peerfact.impl.util.logging.SimLogger;

/**
 * SimulationThread starts one independent process with a simulation and waits
 * for its end.
 * 
 * @author Matthias Feldotto <info@peerfact.org>
 * @version 1.0, 02/03/2013
 */
public class SimulationThread implements Callable<SimulationThread> {

	static Logger log = SimLogger.getLogger(SimulationThread.class);

	private String cmd;

	private File outputDir;

	private Map<String, String> variables;

	private int errorCode;

	private int tries;

	public SimulationThread(String cmd, Map<String, String> variables,
			File outputDir) {
		this.cmd = cmd;
		this.variables = variables;
		this.outputDir = outputDir;
	}

	public void updateCmd(String newCmd) {
		this.cmd = newCmd;
	}

	public void updateOutputDir(File newOutputDir) {
		this.outputDir = newOutputDir;
	}

	@Override
	public SimulationThread call() throws Exception {
		errorCode = -1;
		tries++;

		try {
			log.warn("Execute: " + cmd);

			// execute process
			Process process = Runtime.getRuntime().exec(cmd);

			// log output of process
			BufferedReader reader = new BufferedReader(new
					InputStreamReader(process.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				log.warn(process.toString() + ": " + line);
			}

			// wait for end
			errorCode = process.waitFor();

		} catch (Exception e) {
			log.error("Exception while executing simulation in own process", e);
			errorCode = 1;
		}

		return this;
	}

	public Map<String, String> getVariables() {
		return this.variables;
	}

	public int getErrorCode() {
		return this.errorCode;
	}

	public int getTries() {
		return this.tries;
	}

	public File getOutputDir() {
		return this.outputDir;
	}
}