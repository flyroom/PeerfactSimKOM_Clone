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

package org.peerfact.impl.service.aggregation.skyeye;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.peerfact.Constants;
import org.peerfact.api.scenario.Configurable;
import org.peerfact.api.service.skyeye.SkyNetConstants;
import org.peerfact.api.service.skyeye.SkyNetSimulationType.SimulationType;
import org.peerfact.impl.util.logging.SimLogger;


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
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public class SkyNetBatchSimulator implements Configurable {

	private static Logger log = SimLogger.getLogger(SkyNetBatchSimulator.class);

	private static SkyNetBatchSimulator instance;

	private static String[] gnuScripts = { "statusStats.plt",
			"messageStats.plt", "queryStats.plt", "skyNetStats.plt",
			"polishStats.plt", "coor2RootComp.plt", "referenceMetrics.plt" };

	private boolean isChurn;

	private boolean protectFormerSimulations;

	private long simulationSeed;

	private int simulationSize;

	private String simulationDuration;

	private SimulationType simType;

	// Settings for creating the new sub-directory in the simulation-directory
	private String simulationDirPrefix;

	private String simulationDirPostfix;

	// Settings for the properties file, which is used to obtain the needed
	// information for post-processing
	private File postProcessingFile;

	private final Properties properties;

	private SkyNetBatchSimulator() {
		properties = new Properties();
		checkForSimDirectory();
	}

	public static SkyNetBatchSimulator getInstance() {
		if (instance == null) {
			instance = new SkyNetBatchSimulator();
		}
		return instance;
	}

	public static boolean hasInstance() {
		if (instance == null) {
			return false;
		} else {
			return true;
		}
	}

	public void finish(boolean finishedWithoutError) {
		try {
			if (finishedWithoutError) {
				properties.load(new FileReader(postProcessingFile));
				properties.setProperty("LastSimulationFinished", String
						.valueOf(true));
				properties.store(new FileWriter(postProcessingFile), null);
				log.info("Successfully finished simulation");
			} else {
				log.error("Unsuccessfully finished simulation");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getCurrentSimulationDir() {
		return simulationDirPrefix + simulationDirPostfix;
	}

	public void setChurn(String isChurn) {
		this.isChurn = Boolean.parseBoolean(isChurn);
	}

	public void setSimulationType(String type) {
		log.trace("setSimulationType()");
		if (type.equals("Napster")) {
			simulationDirPrefix = "NapSim";
			simType = SimulationType.NAPSTER_SIMULATION;
		} else if (type.equals("Chord")) {
			simulationDirPrefix = "ChoSim";
			simType = SimulationType.CHORD_SIMULATION;
		} else if (type.equals("Kademlia")) {
			simulationDirPrefix = "KadSim";
			simType = SimulationType.KADEMLIA_SIMULATION;
		} else {
			simulationDirPrefix = "Sim";
			simType = null;
		}
	}

	public void setProtectFormerSimulations(String bool) {
		log.trace("setProtectFormerSimulations()");
		protectFormerSimulations = Boolean.parseBoolean(bool);
	}

	public void setSimulationSeed(String simulationSeed) {
		log.trace("setSimulationSeed()");
		this.simulationSeed = Long.parseLong(simulationSeed);
	}

	public void setSimulationSize(String simulationSize) {
		log.trace("setSimulationSize()");
		this.simulationSize = Integer.parseInt(simulationSize);
	}

	public void setSimulationDuration(String duration) {
		log.trace("setSimulationDuration()");
		simulationDuration = duration;
		simulationDirPostfix = createSimulationDirPostfix();

		createPostProcessingPropertiesFile();
		createSimulationDirectory();
		createGraphicsDirectory();
		copyGnuScriptFiles();
		copySkyNetPropertiesFile();
		log.info("Starting simulation, whose settings can be found in '"
				+ SkyNetConstants.COMMON_SIMULATIONS_PATH + File.separator
				+ getCurrentSimulationDir() + "'");
	}

	private void createPostProcessingPropertiesFile() {
		// creating the postProcessingFile, which contains the information for
		// post-processing
		properties.put("SimulationType", simType.toString());
		properties.put("LastSimulationFinished", Boolean.toString(false));
		properties.put("SimulationDataPath", simulationDirPrefix
				+ simulationDirPostfix);
		properties.put("QueryStartingProbability", Integer
				.toString(SkyNetPropertiesReader.getInstance().getIntProperty(
						"QueryStartingProbability")));

		postProcessingFile = new File(SkyNetConstants.COMMON_SIMULATIONS_PATH
				+ File.separator
				+ SkyNetConstants.POSTPROCESSING_PROPERTIES_FILE);
		try {
			properties.store(new FileWriter(postProcessingFile, false), null);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	private void createSimulationDirectory() {
		// creating the directory, which shall contain the new information of
		// the simulation. If there already exists a direction with the same
		// name, the simulation is aborted and postProcessingFile is informed
		// about the abort.
		File dir = new File(SkyNetConstants.COMMON_SIMULATIONS_PATH
				+ File.separator + simulationDirPrefix + simulationDirPostfix);
		if (dir.mkdir()) {
			log.debug("Created simulation-directory " + dir
					+ " for the next simulation.");
		} else if (protectFormerSimulations) {
			log.error("There already exists a dir '" + dir
					+ "' with an equal simulation."
					+ " Please change the settings of this simulation,"
					+ "or delete the other simulation to run this again!");
			throw new RuntimeException("There already exists a dir '" + dir
					+ "' with an equal simulation."
					+ " Please change the settings of this simulation,"
					+ "or delete the other simulation to run this again!");
		} else {
			if (wipeSimulationDir(dir)) {
				log.info("Deleting content of old dir '" + dir
						+ "' for a new simulation with the same settings");
			} else {
				log.error("Problems occured while delelting dir '" + dir
						+ "'. Please choose other simulation settings"
						+ " or clean the specified dir manually!");
				throw new RuntimeException(
						"Problems occured while delelting dir '" + dir
								+ "'. Please choose other simulation settings"
								+ " or clean the specified dir manually!");
			}

		}
	}

	private boolean wipeSimulationDir(File dir) {
		File[] dirContent = dir.listFiles();
		for (int i = 0; i < dirContent.length; i++) {
			if (dirContent[i].isDirectory()) {
				if (wipeSimulationDir(dirContent[i])) {
					dirContent[i].delete();
				} else {
					return false;
				}
			} else {
				dirContent[i].delete();
			}
		}
		String[] list = dir.list();
		if (list.length == 0) {
			return true;
		} else {
			return false;
		}
	}

	private void createGraphicsDirectory() {
		File dir = new File(SkyNetConstants.COMMON_SIMULATIONS_PATH
				+ File.separator + simulationDirPrefix + simulationDirPostfix
				+ File.separator + "graphics");
		if (dir.mkdir()) {
			log.debug("Created directory " + dir + " for the graphics.");
		}

		dir = new File(SkyNetConstants.COMMON_SIMULATIONS_PATH + File.separator
				+ simulationDirPrefix + simulationDirPostfix + File.separator
				+ "graphics" + File.separator + "coor2RootCompare");
		if (dir.mkdir()) {
			log.debug("Created sub-directory " + dir.getName()
					+ " for the graphics.");
		}

		dir = new File(SkyNetConstants.COMMON_SIMULATIONS_PATH + File.separator
				+ simulationDirPrefix + simulationDirPostfix + File.separator
				+ "graphics" + File.separator + "messages");
		if (dir.mkdir()) {
			log.debug("Created sub-directory " + dir.getName()
					+ " for the graphics.");
		}

		dir = new File(SkyNetConstants.COMMON_SIMULATIONS_PATH + File.separator
				+ simulationDirPrefix + simulationDirPostfix + File.separator
				+ "graphics" + File.separator + "queries");
		if (dir.mkdir()) {
			log.debug("Created sub-directory " + dir.getName()
					+ " for the graphics.");
		}
	}

	private void copyGnuScriptFiles() {
		File dir = new File(Constants.GNUPLOT_SCRIPTS
				+ File.separator + SkyNetConstants.GNU_SCRIPTS_PATH);
		BufferedReader br = null;
		BufferedWriter bw = null;
		if (dir.exists()) {
			File gnuScriptsDir = new File(
					SkyNetConstants.COMMON_SIMULATIONS_PATH + File.separator
							+ simulationDirPrefix + simulationDirPostfix
							+ File.separator + SkyNetConstants.GNU_SCRIPTS_PATH);
			if (gnuScriptsDir.mkdir()) {
				log.debug("Created directory " + gnuScriptsDir
						+ " for the gnuScripts.");
			}

			// create main gnuplot-file "startScripts.plt"
			writeMainGnuPlotFile(gnuScriptsDir, bw);

			// copy all files from the main gnuplot-directory to the
			// simulation-directory
			File[] files = dir.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isFile()) {
					copyFile(dir.getPath() + File.separator
							+ files[i].getName(), gnuScriptsDir, files[i]
							.getName(), br, bw);
				} else {
					File subDir = files[i];
					if (subDir.getName().equals("chord")
							&& simType.equals(SimulationType.CHORD_SIMULATION)) {
						log.info("Copying the customized gnuSript-file"
								+ " for the Chord-Overlay from "
								+ subDir.getName());
						File[] subDirFiles = subDir.listFiles();
						for (int j = 0; j < subDirFiles.length; j++) {
							if (subDirFiles[j].isFile()) {
								copyFile(dir.getPath() + File.separator
										+ subDir.getName() + File.separator
										+ subDirFiles[j].getName(),
										gnuScriptsDir,
										subDirFiles[j].getName(), br, bw);
							}
						}
					} else if (subDir.getName().equals("kademlia")
							&& simType
									.equals(SimulationType.KADEMLIA_SIMULATION)) {
						log.info("Copying the customized gnuSript-file"
								+ " for the Kademlia-Overlay from "
								+ subDir.getName());
						File[] subDirFiles = subDir.listFiles();
						for (int j = 0; j < subDirFiles.length; j++) {
							if (subDirFiles[j].isFile()) {
								copyFile(dir.getPath() + File.separator
										+ subDir.getName() + File.separator
										+ subDirFiles[j].getName(),
										gnuScriptsDir,
										subDirFiles[j].getName(), br, bw);
							}
						}
					} else if (subDir.getName().equals("napster")
							&& simType
									.equals(SimulationType.NAPSTER_SIMULATION)) {
						log.info("Copying the customized gnuSript-file"
								+ " for the Napster-Overlay from "
								+ subDir.getName());
						File[] subDirFiles = subDir.listFiles();
						for (int j = 0; j < subDirFiles.length; j++) {
							if (subDirFiles[j].isFile()) {
								copyFile(dir.getPath() + File.separator
										+ subDir.getName() + File.separator
										+ subDirFiles[j].getName(),
										gnuScriptsDir,
										subDirFiles[j].getName(), br, bw);
							}
						}
					}
				}
			}
		} else {
			log.warn("The directory " + dir.getPath()
					+ ", which should contain the scripts for gnuplot"
					+ " does not exist.");
		}
	}

	private static void writeMainGnuPlotFile(File dir, BufferedWriter bw) {
		try {
			bw = new BufferedWriter(new FileWriter(new File(dir,
					"startScripts.plt")));
			for (int i = 0; i < gnuScripts.length; i++) {
				if (gnuScripts[i].equals("queryStats.plt")) {
					if (SkyNetPropertiesReader.getInstance().getIntProperty(
							"QueryStartingProbability") > 0) {
						bw.write("load '" + gnuScripts[i] + "'");
						bw.write("\n");
					}
				} else {
					bw.write("load '" + gnuScripts[i] + "'");
					bw.write("\n");
				}
			}
			bw.close();
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	private void copySkyNetPropertiesFile() {
		File dir = new File("config");
		BufferedReader br = null;
		BufferedWriter bw = null;
		String copy = null;
		String line = null;
		if (dir.exists()) {
			File[] files = dir.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].getName().equals(
						SkyNetPropertiesReader.getInstance()
								.getSkynetPropertiesFile())) {
					try {
						br = new BufferedReader(new FileReader(files[i]));
						copy = files[i].getName();
						bw = new BufferedWriter(new FileWriter(new File(
								SkyNetConstants.COMMON_SIMULATIONS_PATH
										+ File.separator + simulationDirPrefix
										+ simulationDirPostfix, copy)));
						while ((line = br.readLine()) != null) {
							bw.write(line);
							bw.write("\n");
						}
						br.close();
						bw.close();
						log.debug("Copied " + files[i]);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		} else {
			log.warn("The directory " + dir.getName()
					+ ", which should contain the skynet.properties file"
					+ " does not exist.");
		}
	}

	private String createSimulationDirPostfix() {
		SkyNetPropertiesReader pReaderInstance = SkyNetPropertiesReader
				.getInstance();
		String ret = "";
		// adding the general settings
		ret += "_" + simulationSize + "N";
		ret += simulationSeed + "S";
		ret += simulationDuration;
		if (isChurn) {
			ret += "Churn";
		} else {
			ret += "NoChurn";
		}

		// adding the information of the SkyNet-Tree-settings
		ret += "_"
				+ pReaderInstance.getIntProperty("SkyNetTreeBranchingFactor")
				+ "TBF";

		// adding the information of the metrics-update-settings
		ret += "_" + pReaderInstance.getStringProperty("MetricUpdateTime")
				+ "MUT";
		ret += pReaderInstance
				.getIntProperty("TimeForGeneratingSystemStatistics")
				+ "TGSS";
		if (pReaderInstance.getBooleanProperty("AlwaysPushSystemStatistics")) {
			ret += "1APSS";
		} else {
			ret += "0APSS";
		}
		ret += pReaderInstance.getStringProperty("MetricRemoveStaleSubCo")
				.replace(
						".", "")
				+ "MRSS";

		// adding the information of the polishing-mode-settings
		String polishingMode = pReaderInstance
				.getStringProperty("SmoothingType");
		if (polishingMode.equals("MedianSmoothing")) {
			ret += "_MedPol";
			ret += pReaderInstance.getIntProperty("SizeOfHistory") + "H";
		} else {
			ret += "_ExpPol";
			ret += pReaderInstance.getIntProperty("SizeOfHistory") + "H";
			ret += pReaderInstance.getStringProperty(
					"ExponentialSmoothingFactor")
					.replace(".", "")
					+ "F";
		}

		// adding the information of the attribute-update-settings
		ret += "_" + pReaderInstance.getStringProperty("AttributeUpdateTime")
				+ "AUT";
		ret += pReaderInstance.getIntProperty("LowerBoundOfEntriesForCo")
				+ "LBE";
		ret += pReaderInstance.getStringProperty("AttributeRemoveStaleSubCo")
				.replace(
						".", "")
				+ "ARSS";
		return ret;
	}

	private static void checkForSimDirectory() {
		File dir = new File(SkyNetConstants.COMMON_SIMULATIONS_PATH);
		if (dir.mkdir()) {
			log.info("--> Created Directory " + dir.getName());
		} else {
			log.info("--> New simulation: Directory '" + dir.getName()
					+ "', which will contain the results of all simulations,"
					+ " already exists and needs not created.");
		}
	}

	private static void copyFile(String oldFile, File targetDir,
			String targetFile,
			BufferedReader br, BufferedWriter bw) {
		String line = null;
		try {
			br = new BufferedReader(new FileReader(oldFile));
			bw = new BufferedWriter(new FileWriter(new File(targetDir,
					targetFile)));
			while ((line = br.readLine()) != null) {
				bw.write(line);
				bw.write("\n");
			}
			br.close();
			bw.flush();
			bw.close();
			log.debug("Copied " + oldFile);
		} catch (FileNotFoundException e) {
			log.error(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}
}
