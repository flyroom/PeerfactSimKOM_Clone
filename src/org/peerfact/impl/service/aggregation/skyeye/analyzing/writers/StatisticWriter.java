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

package org.peerfact.impl.service.aggregation.skyeye.analyzing.writers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.peerfact.api.analyzer.Analyzer;
import org.peerfact.api.scenario.Configurable;
import org.peerfact.api.service.skyeye.ISkyNetMonitor;
import org.peerfact.api.service.skyeye.SkyNetConstants;
import org.peerfact.impl.service.aggregation.skyeye.SkyNetBatchSimulator;
import org.peerfact.impl.service.aggregation.skyeye.analyzing.AbstractSkyNetWriter;
import org.peerfact.impl.service.aggregation.skyeye.analyzing.analyzers.ChurnStatisticsAnalyzer;
import org.peerfact.impl.service.aggregation.skyeye.metrics.MetricsAggregate;
import org.peerfact.impl.simengine.SimulationEvent;
import org.peerfact.impl.simengine.Simulator;
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
 * This class is responsible for creating a Weka-file out of the metrics, which
 * are periodically calculated by the root of the SkyNet-tree. The type of the
 * Weka-file (training or test) is configured by the configuration file.
 * Additionally, as the Weka-file is currently used for the determination of
 * churn, the file contains the class-label <code>churn</code> or
 * <code>none</code>, which indicates, if the simulation was executed in the
 * presence of churn or not. The class-label is also set by the configuration
 * file. Last but not least, the configuration file also determines, if a new
 * Weka-file for the next simulation is created or if the new data is append to
 * the old Weka-file.<br>
 * The Weka-file can be found in the logging-directory.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 05.12.2008
 * 
 */
public class StatisticWriter extends AbstractSkyNetWriter implements
		Configurable, Analyzer {

	private static Logger log = SimLogger.getLogger(StatisticWriter.class);

	private static final String RESULTS_PATH = SkyNetConstants.COMMON_SIMULATIONS_PATH;

	private final String AGGREGATES_FILE = "skynet-Statistic.dat";

	private final String ARFF_TRAINING_FILE = "skynet-testFile.arff";

	private final String ARFF_CHURN_FILE = "skynet-churnFile.arff";

	private final String ARFF_NO_CHURN_FILE = "skynet-noChurnFile.arff";

	private static StatisticWriter writer;

	private ChurnStatisticsAnalyzer csAnalyzer;

	private boolean writingEnabled = false;

	// settings for the aggregateWriter
	private PrintWriter aggregateWriter;

	private Vector<String> aggregateNameSpace = null;

	private boolean aggregateHeader = false;

	private double leastSquareError;

	// settings for the arffWriter
	private PrintWriter arffWriter;

	private Vector<String> arffNameSpace = null;

	private boolean arffHeader = false;

	private boolean training;

	private boolean churn;

	private boolean append;

	public static StatisticWriter getInstance() {
		return writer;
	}

	public StatisticWriter() {
		writer = this;
		leastSquareError = 0d;
		initWriteDirectory(RESULTS_PATH, false);
	}

	private void initOutput() {
		try {
			aggregateWriter = new PrintWriter(new BufferedWriter(
					new FileWriter(new File(RESULTS_PATH + File.separator
							+ AGGREGATES_FILE))), true);
			ISkyNetMonitor monitor = (ISkyNetMonitor) Simulator.getMonitor();
			csAnalyzer = (ChurnStatisticsAnalyzer) monitor
					.getConnectivityAnalyzer(ChurnStatisticsAnalyzer.class);

			if (training) {
				arffWriter = new PrintWriter(
						new BufferedWriter(new FileWriter(new File(RESULTS_PATH
								+ File.separator + ARFF_TRAINING_FILE), append)),
						true);
				log.warn("Creating training-file for Weka");
			} else if (churn) {
				arffWriter = new PrintWriter(new BufferedWriter(
						new FileWriter(
								new File(
										RESULTS_PATH + File.separator
												+ ARFF_CHURN_FILE),
								false)), true);
				log.warn("Creating churn-file for testing in Weka");
			} else if (!churn) {
				arffWriter = new PrintWriter(
						new BufferedWriter(new FileWriter(new File(RESULTS_PATH
								+ File.separator
								+ SkyNetBatchSimulator.getInstance()
										.getCurrentSimulationDir()
								+ File.separator + ARFF_NO_CHURN_FILE), false)),
						true);
				log.warn("Creating no-churn-file for testing in Weka");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int writeAggregateEntry(LinkedHashMap<String, MetricsAggregate> map,
			long time) {
		int counter = -1;
		if (writingEnabled) {
			if (!aggregateHeader) {
				aggregateHeader = true;
				writeAggregateHeader(map);
			}
			aggregateWriter.print((time / SkyNetConstants.DIVISOR_FOR_SECOND)
					+ "\t");
			aggregateWriter.print(csAnalyzer.getNoOfPresentPeers() + "\t");
			double peers = csAnalyzer.getNoOfPresentPeers();
			String line;
			String name;
			boolean first = true;
			for (int i = 0; i < aggregateNameSpace.size(); i++) {
				name = aggregateNameSpace.get(i);
				if (first) {
					first = false;
					counter = map.get(name).getNodeCount();
					peers = peers - map.get(name).getNodeCount();
					aggregateWriter.print(counter + "\t");
					aggregateWriter.print(peers + "\t");
					aggregateWriter.print((peers / csAnalyzer
							.getNoOfPresentPeers())
							+ "\t");
					aggregateWriter.print(leastSquareError + "\t");
				}
				if ("aggregateFreshness".equals(name)) {
					line = (time - map.get(name).getMinimum()) + "\t"
							+ (time - map.get(name).getMaximum()) + "\t"
							+ (time - map.get(name).getSumOfAggregates())
							+ "\t" + (time - map.get(name).getAverage()) + "\t"
							+ (time - map.get(name).getStandardDeviation())
							+ "\t";
				} else {
					line = map.get(name).getMinimum() + "\t"
							+ map.get(name).getMaximum() + "\t"
							+ map.get(name).getSumOfAggregates() + "\t"
							+ map.get(name).getAverage() + "\t"
							+ map.get(name).getStandardDeviation() + "\t";
				}
				aggregateWriter.print(line);
			}
			aggregateWriter.println();
			aggregateWriter.flush();
			leastSquareError = leastSquareError + Math.pow(peers, 2);
		}
		return counter;
	}

	private void writeAggregateHeader(
			LinkedHashMap<String, MetricsAggregate> map) {
		// create the required header for gnuplot
		aggregateNameSpace = new Vector<String>(map.keySet().size());
		Iterator<String> names = map.keySet().iterator();
		while (names.hasNext()) {
			aggregateNameSpace.add(names.next());
		}
		Collections.sort(aggregateNameSpace);

		aggregateWriter.println("# TIME");
		aggregateWriter.println("# Online Peers");
		aggregateWriter.println("# Counted Peers");
		aggregateWriter.println("# PeerError");
		aggregateWriter.println("# Relative PeerError");
		aggregateWriter.println("# LeastSquareError");

		for (int i = 0; i < aggregateNameSpace.size(); i++) {
			aggregateWriter.println("# " + aggregateNameSpace.get(i) + "->MIN");
			aggregateWriter.println("# " + aggregateNameSpace.get(i) + "->MAX");
			aggregateWriter.println("# " + aggregateNameSpace.get(i) + "->SUM");
			aggregateWriter.println("# " + aggregateNameSpace.get(i) + "->AVG");
			aggregateWriter.println("# " + aggregateNameSpace.get(i) + "->STD");
		}

		// create the required header for Excel
		aggregateWriter.print("# \"TIME\"\t");
		aggregateWriter.print("\"Online Peers\"\t");
		aggregateWriter.print("\"Counted Peers\"\t");
		aggregateWriter.print("\"PeerError\"\t");
		aggregateWriter.print("\"Relative PeerError\"\t");
		aggregateWriter.print("\"LeastSquareError\"\t");
		for (int i = 0; i < aggregateNameSpace.size(); i++) {
			aggregateWriter.print("\"" + aggregateNameSpace.get(i)
					+ "->MIN\"\t");
			aggregateWriter.print("\"" + aggregateNameSpace.get(i)
					+ "->MAX\"\t");
			aggregateWriter.print("\"" + aggregateNameSpace.get(i)
					+ "->SUM\"\t");
			aggregateWriter.print("\"" + aggregateNameSpace.get(i)
					+ "->AVG\"\t");
			aggregateWriter.print("\"" + aggregateNameSpace.get(i)
					+ "->STD\"\t");
		}
		aggregateWriter.println();
	}

	/**
	 * This method is periodically executed by the root of the SkyNet-tree.
	 * Every call of this method writes a record, containing all metrics, in the
	 * Weka-file.
	 * 
	 * @param map
	 *            contains all metrics of the root
	 * @param time
	 *            contains the time, when the method was called
	 */
	public void writeArffLine(LinkedHashMap<String, MetricsAggregate> map,
			long time) {
		if (writingEnabled) {
			if (!arffHeader) {
				arffHeader = true;
				writeArffHeader(map);
			}
			Double average;
			arffWriter.println();
			for (int i = 0; i < arffNameSpace.size(); i++) {
				average = map.get(arffNameSpace.get(i)).getAverage();
				arffWriter.print(average + ",");
			}
			if (churn) {
				arffWriter.print("churn");
			} else {
				arffWriter.print("none");
			}
			arffWriter.flush();
		}
	}

	private void writeArffHeader(LinkedHashMap<String, MetricsAggregate> map) {
		Iterator<String> names = map.keySet().iterator();
		arffNameSpace = new Vector<String>(map.keySet().size());
		if (!append) {
			arffWriter.println();
			arffWriter.println("@relation skynet");
			arffWriter.println();
		}
		String name;
		while (names.hasNext()) {
			name = names.next();
			arffNameSpace.add(name);
			if (!append) {
				arffWriter.println("@attribute " + name + " real");
			}
		}
		if (!append) {
			arffWriter.println("@attribute churn {churn, none}");
			arffWriter.println();
			arffWriter.println("@data");
		}
	}

	public void finalizeOutput() {
		aggregateWriter.flush();
		aggregateWriter.close();
		arffWriter.flush();
		arffWriter.close();
		log.warn("Disabled StatisticWriter!!!");
	}

	@Override
	public void eventOccurred(SimulationEvent se) {
		if (se.getType().equals(SimulationEvent.Type.MONITOR_START)) {
			start();
		} else if (se.getType().equals(SimulationEvent.Type.MONITOR_STOP)) {
			stop(null);
		}
	}

	@Override
	public void start() {
		initOutput();
		writingEnabled = true;
		log.warn(Simulator.getFormattedTime(Simulator.getCurrentTime())
				+ " Enabled StatisticWriter!!!");
	}

	@Override
	public void stop(Writer output) {
		writingEnabled = false;
		finalizeOutput();
	}

	public void setStart(long time) {
		Simulator.scheduleEvent(null, time, this,
				SimulationEvent.Type.MONITOR_START);
	}

	public void setStop(long time) {
		Simulator.scheduleEvent(null, time, this,
				SimulationEvent.Type.MONITOR_STOP);
	}

	/**
	 * This method is used by the configuration file to determine, if the next
	 * simulation creates a Weka-file to train Weka, or if the file is tested
	 * within Weka
	 * 
	 * @param training
	 *            specifies the meaning of the file for Weka
	 */
	public void setTraining(boolean training) {
		this.training = training;
	}

	/**
	 * This method is used by the configuration file to determine, if the next
	 * simulation is executed in the presence of churn
	 * 
	 * @param churn
	 *            specifies the presence of churn
	 */
	public void setChurn(boolean churn) {
		this.churn = churn;
	}

	/**
	 * This method is used by the configuration file to determine, if the data
	 * of the next simulation is append to the current Weka-file, or if the data
	 * overwrites the old file.
	 * 
	 * @param append
	 *            specifies location of the generated data
	 */
	public void setAppend(boolean append) {
		this.append = append;
	}
}
