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

package org.peerfact.impl.analyzer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.apache.log4j.Logger;
import org.peerfact.api.analyzer.Analyzer;
import org.peerfact.api.simengine.SimulationEventHandler;
import org.peerfact.impl.simengine.SimulationEvent;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.util.logging.SimLogger;

/**
 * AbstractFileAnalyzer is the basis class for all analyzer which print
 * statistics to files.
 * 
 * It offers methods for creating directories, files, file writers and write the
 * statistics at the end of the monitoring. The concrete analyzers only have to
 * implement the three abstract methods initialize(), doEvaluation() and
 * doFinalEvaluation() which generate the statistics.
 * 
 * @author Matthias Feldotto <info@peerfact.org>
 * @version 1.0, 12/21/2011
 */
public abstract class AbstractFileAnalyzer implements Analyzer,
		SimulationEventHandler {

	static final Logger log = SimLogger.getLogger(AbstractFileAnalyzer.class);

	public static final String FILE_EXTENSION = ".dat";

	public static File statDir;

	protected String outputFileName = "data";

	protected BufferedWriter output;

	protected boolean stopped = false;

	protected boolean evalDone = false;

	protected boolean flushEveryLine = false;

	protected long beginOfAnalyzing = 0 * Simulator.MINUTE_UNIT;

	protected long endOfAnalyzing = Simulator.getEndTime() + 1;

	protected long timeBetweenAnalyzeSteps = 1 * Simulator.MINUTE_UNIT;

	/*
	 * Analyzer methods
	 */

	@Override
	public void start() {
		try {
			initDatFiles();

			initialize();

			if (Simulator.getCurrentTime() >= beginOfAnalyzing) {
				if (!stopped) {
					try {
						doEvaluation(Simulator.getCurrentTime());

					} catch (IOException e) {
						e.printStackTrace();
					}
					scheduleWithDelay(timeBetweenAnalyzeSteps);
				}
			} else {
				scheduleAtTime(beginOfAnalyzing);
			}
		} catch (IOException e) {
			log.error("IOException occured", e);
		}
	}

	private void scheduleAtTime(long time) {
		long correctTime = Math.max(time, Simulator.getCurrentTime());
		Simulator.scheduleEvent(this, correctTime, this,
				SimulationEvent.Type.ANALYZE_EVENT);
	}

	private void scheduleWithDelay(long delay) {
		scheduleAtTime(Simulator.getCurrentTime() + delay);
	}

	@Override
	public void eventOccurred(SimulationEvent se) {

		if (Simulator.getCurrentTime() <= endOfAnalyzing) {
			if (!stopped) {
				try {
					doEvaluation(Simulator.getCurrentTime());
					evalDone = true;
				} catch (IOException e) {
					log.error("IOException occured", e);
				}
				scheduleWithDelay(timeBetweenAnalyzeSteps);
			}
		}
	}

	@Override
	public void stop(Writer SysOutput) {
		try {
			if (!evalDone) {
				doEvaluation(Simulator.getCurrentTime());
			}
			output.flush();
			output.close();
			doFinalEvaluation();
			stopped = true;
		} catch (IOException e) {
			log.error("IOException occured", e);
		}
	}

	private void initDatFiles() {
		try {
			File outputFile = new File(Simulator.getOuputDir(), outputFileName
					+ FILE_EXTENSION);

			output = new BufferedWriter(new FileWriter(outputFile));

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Optional to use setters
	 */

	protected void setFlushEveryLine(boolean flushEveryLine) {
		this.flushEveryLine = flushEveryLine;
	}

	protected void setBeginOfAnalyzing(long beginOfAnalyzing) {
		this.beginOfAnalyzing = beginOfAnalyzing;
	}

	protected void setEndOfAnalyzing(long endOfAnalyzing) {
		this.endOfAnalyzing = endOfAnalyzing;
	}

	protected void setOutputFileName(String outputFileName) {
		this.outputFileName = outputFileName;
	}

	protected void setTimeBetweenAnalyzeSteps(long timeBetweenAnalyzeSteps) {
		this.timeBetweenAnalyzeSteps = timeBetweenAnalyzeSteps;
	}

	/*
	 * Abstract methods for concrete analyzer implementations
	 */

	/**
	 * Initializes the anaylzer at the beginning.
	 * 
	 * @throws IOException
	 */
	protected abstract void initialize() throws IOException;

	/**
	 * Does a time measurement.
	 * 
	 * @param currentTime
	 *            the current simulation time
	 * 
	 * @throws IOException
	 */
	protected abstract void doEvaluation(long currentTime) throws IOException;

	/**
	 * Does an optional measurement at the end.
	 * 
	 * @throws IOException
	 */
	protected abstract void doFinalEvaluation() throws IOException;

}
