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

package org.peerfact.impl.service.aggregation.skyeye.analyzing.analyzers.postProcessing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.peerfact.Constants;
import org.peerfact.api.service.skyeye.SkyNetConstants;
import org.peerfact.api.service.skyeye.SkyNetPostProcessing;


/**
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * This part of the Simulator is not maintained in the current version of
 * PeerfactSim.KOM. There is no intention of the authors to fix this
 * circumstances, since the changes needed are huge compared to overall benefit.
 * 
 * If you want it to work correctly, you are free to make the specific changes
 * and provide it to the community.
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! !!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * 
 * This class starts the post-processing after the termination of a simulation
 * of SkyNet on top of a certain overlay. The post-processing does not start
 * automatically, instead one must launch this class manually or write a
 * batch-file, which runs the simulation first and afterwards launches the
 * post-processing (for Windows, there already exist two batch-files
 * runskynetChord.bat and runskynetNapster.bat, which start a simulation and the
 * post-processing).
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 05.12.2008
 * 
 */
public class PostProcessingRunner {

	private static Logger log = Logger.getLogger(PostProcessingRunner.class);

	private static final String log4j_config_file = "src/log4j.properties";

	private static Properties properties;

	private synchronized static void init() {
		try {
			PropertyConfigurator.configureAndWatch(log4j_config_file, 60 * 100);
		} catch (Exception ex) {
			System.err.println("Failed to configure logging" + ex);
			throw new IllegalStateException(ex);
		}
	}

	/**
	 * This method is responsible for starting the post-processing of a
	 * simulation. For the evaluation of that simulation,
	 * <code>PostProcessingRunner</code> extracts all information, which was
	 * serialized during a simulation, and writes the data in the predefined
	 * files.
	 * 
	 * @param args
	 *            contains a String, which describes the type of simulation
	 *            (currently Chord or Napster)
	 */
	public static void main(String[] args) {
		PostProcessingRunner ppr = new PostProcessingRunner();
		PostProcessingRunner.init();
		properties = new Properties();

		File postProcFile = new File(SkyNetConstants.COMMON_SIMULATIONS_PATH
				+ File.separator
				+ SkyNetConstants.POSTPROCESSING_PROPERTIES_FILE);

		if (postProcFile.exists()) {
			try {
				properties.load(new FileReader(postProcFile));
				if (Boolean.parseBoolean(properties
						.getProperty("LastSimulationFinished"))) {
					// Determine the type of the simulation
					String simType = null;
					if (properties.getProperty("SimulationType").equals(
							"NAPSTER_SIMULATION")) {
						simType = "Napster";
					} else if (properties.getProperty("SimulationType").equals(
							"CHORD_SIMULATION")) {
						simType = "Chord";
					} else if (properties.getProperty("SimulationType").equals(
							"KADEMLIA_SIMULATION")) {
						simType = "Kademlia";
					}

					// start the post-processing and the create the graphics
					// with gnuplot
					if (simType != null) {
						String dataPath = SkyNetConstants.COMMON_SIMULATIONS_PATH
								+ File.separator
								+ properties.getProperty("SimulationDataPath")
								+ File.separator + "data";
						PostProcessingRunner.startPostProcessing(simType,
								dataPath);
						File gnuPlotBinDir = null;

						// Check for right operating system
						if (System.getProperty("os.name").toLowerCase()
								.indexOf("windows") >= 0) {
							gnuPlotBinDir = new File(
									Constants.GNU_BIN_DIRECTORY_WINDOWS);
						} else {
							gnuPlotBinDir = new File(
									Constants.GNU_BIN_DIRECTORY_LINUX);
						}
						if (gnuPlotBinDir != null && gnuPlotBinDir.exists()) {
							PostProcessingRunner
									.createGraphicsWithGnuPlot(properties);
						} else {
							log.warn("Could not call gnuplot."
									+ "Either it is not installed,"
									+ "or the path " + gnuPlotBinDir
									+ " does not match the current location.");
						}
					} else {
						log.error("Could not start post-processing,"
								+ " as the simulationType, is unknown!");
					}
				} else {
					log.error("Could not start post-processing,"
							+ " as the simulation, whose data is situated in "
							+ properties.getProperty("SimulationDataPath")
							+ "was not finished properly or an error occured.");
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} else {
			log.error("Could not start post-processing, as "
					+ SkyNetConstants.POSTPROCESSING_PROPERTIES_FILE
					+ ", which contains the required information"
					+ " to start the postprocessing does not exist.");
		}

	}

	private static void startPostProcessing(String simulationType,
			String dataPath) {
		try {
			long overallTimestamp = -1;
			long timestamp = -1;

			// PostProcessing for the messages
			timestamp = System.currentTimeMillis();
			overallTimestamp = timestamp;
			log.info("Starting to process the messages");
			SkyNetPostProcessing snpp = new NetLayerPostProcessor(dataPath,
					simulationType);
			snpp.extractDataOfFiles();
			snpp.processData();
			snpp.writeDataFile();
			log.info("Finished to process the messages in "
					+ (System.currentTimeMillis() - timestamp) + "ms");

			// PostProcessing for the queries
			if (Integer.parseInt(properties
					.getProperty("QueryStartingProbability")) > 0) {
				timestamp = System.currentTimeMillis();
				log.info("Starting to process the queries");
				snpp = new QueryPostProcessor(dataPath);
				snpp.extractDataOfFiles();
				snpp.processData();
				snpp.writeDataFile();
				log.info("Finished to process the queries in "
						+ (System.currentTimeMillis() - timestamp) + "ms");
			}

			// PostProcessing for the status
			timestamp = System.currentTimeMillis();
			log.info("Starting to process the status");
			snpp = new StatusPostProcessor(dataPath);
			snpp.extractDataOfFiles();
			snpp.processData();
			snpp.writeDataFile();
			log.info("Finished to process the status in "
					+ (System.currentTimeMillis() - timestamp) + "ms");

			// PostProcessing for Coordinator2Root-Comparison
			timestamp = System.currentTimeMillis();
			log.info("Starting to process the Coordinator2Root-Comparison");
			snpp = new Coor2RootPostProcessor(dataPath);
			snpp.extractDataOfFiles();
			snpp.processData();
			snpp.writeDataFile();
			log.info("Finished to process the Coordinator2Root-Comparison in "
					+ (System.currentTimeMillis() - timestamp) + "ms");
			log.info("The complete post-processing was successfully"
					+ " and lasted about "
					+ (System.currentTimeMillis() - overallTimestamp) + "ms");
		} catch (Exception e) {
			log.error("The post-proccessing was not successful"
					+ " and must be aborted." + "Reason = " + e.toString());
			e.printStackTrace();
		}
	}

	private static void createGraphicsWithGnuPlot(Properties passedProperties) {
		File pltFile = new File(SkyNetConstants.COMMON_SIMULATIONS_PATH
				+ File.separator
				+ passedProperties.getProperty("SimulationDataPath")
				+ File.separator + SkyNetConstants.GNU_SCRIPTS_PATH
				+ File.separator + "startScripts.plt");
		pltFile = pltFile.getAbsoluteFile();

		String[] command = { "", pltFile.toString() };

		// Check for right operating system
		if (System.getProperty("os.name").toLowerCase().indexOf("windows") >= 0) {
			command[0] = Constants.GNU_BIN_DIRECTORY_WINDOWS + File.separator
					+ Constants.GNU_EXECUTABLE_WINDOWS;
		} else {
			command[0] = Constants.GNU_EXECUTABLE_LINUX;
		}

		log.info("Starting gnuplot with 'startScripts.plt'");
		try {
			Process process = Runtime.getRuntime().exec(command, null,
					pltFile.getParentFile());
			int exitValue = process.waitFor();
			if (exitValue == 0) {
				log.info("Successfully created graphics with gnuplot.");
			} else {
				log
						.warn("The call of gnuplot was not successfully. "
								+ "Probably there are false or no graphics created at all!");
			}
		} catch (IOException e) {
			log.error("Problems occured during executing gnuplot."
					+ "Reason = " + e.toString());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
