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
import java.util.Vector;

import org.apache.log4j.Logger;
import org.peerfact.api.network.NetID;
import org.peerfact.api.service.skyeye.ISkyNetMonitor;
import org.peerfact.api.service.skyeye.SkyNetConstants;
import org.peerfact.impl.service.aggregation.skyeye.SkyNetPropertiesReader;
import org.peerfact.impl.service.aggregation.skyeye.analyzing.AbstractSkyNetWriter;
import org.peerfact.impl.service.aggregation.skyeye.analyzing.analyzers.ChurnStatisticsAnalyzer;
import org.peerfact.impl.service.aggregation.skyeye.attributes.Attribute;
import org.peerfact.impl.service.aggregation.skyeye.visualization.SkyNetVisualization;
import org.peerfact.impl.service.aggregation.skyeye.visualization.util.DataSet;
import org.peerfact.impl.service.aggregation.skyeye.visualization.util.DeviationSet;
import org.peerfact.impl.service.aggregation.skyeye.visualization.util.VisualizationType;
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
 * This class monitors the P2P-system concerning the transmitted
 * attribute-entries and the attributes of each peer. As far as the attributes
 * of every peer are concerned, they are written in <code>Attributes.dat</code>
 * in the logging-directory before a simulation starts. With this file, one can
 * obtain an overview of the qualities of the peers.<br>
 * Concerning the transmitted attribute-entries, this class keeps track of the
 * entries which are stored and handled by the root as well as by a possible
 * Support Peer, which is utilized by the root for load-distribution. In
 * addition the amount of utilized Support Peers of the whole P2P-system during
 * a simulation is counted. The entries of the root and the Support Peer as well
 * as the amount of used Support Peers are stored in
 * <code>AttributeOverview.txt</code> in the gnuScripts/data-directory.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 04.12.2008
 * 
 */
public class AttributeWriter extends AbstractSkyNetWriter {

	private static Logger log = SimLogger.getLogger(AttributeWriter.class);

	private static final String RESULTS_PATH = Simulator.getOuputDir()
			.getAbsolutePath() + File.separator
			+ SkyNetConstants.COMMON_SIMULATIONS_PATH;

	private static final String ATTRIBUTE_DAT_FILE = "skynet-Attribute.dat";

	private static final String ATTRIBUTE_OVERVIEW_FILE = "skynet-AttributeOverview.txt";

	private static AttributeWriter writer;

	private SkyNetPropertiesReader propReader;

	private PrintWriter adFile;

	private PrintWriter aoFile;

	private ChurnStatisticsAnalyzer csAnalyzer;

	private int peerCounter;

	private long attributeUpdateTime;

	private int onlinePeersRoot;

	private int onlinePeersSupportPeer;

	private int amountOfSupportPeers = 0;

	// coefficients for calculating and printing the quality of a peer's
	// attributes
	private float cpuCoefficient;

	private float downBandwidthCoefficient;

	private float ramCoefficient;

	private float storageCoefficient;

	private float onlineCoefficient;

	public static AttributeWriter getInstance() {
		if (writer == null) {
			writer = new AttributeWriter();
		}
		return writer;
	}

	public static boolean hasInstance() {
		if (writer == null) {
			return false;
		} else {
			return true;
		}
	}

	private AttributeWriter() {
		initWriteDirectory(RESULTS_PATH, false);

		// setting the needed values from the properties file
		propReader = SkyNetPropertiesReader.getInstance();
		attributeUpdateTime = propReader.getTimeProperty("AttributeUpdateTime");
		cpuCoefficient = propReader.getFloatProperty("CPUCoefficient");
		downBandwidthCoefficient = propReader
				.getFloatProperty("DownBandwidthCoefficient");
		ramCoefficient = propReader.getFloatProperty("RAMCoefficient");
		storageCoefficient = propReader.getFloatProperty("STORAGECoefficient");
		onlineCoefficient = propReader.getFloatProperty("ONLINECoefficient");

		long time = Simulator.getCurrentTime();
		long delta = time % attributeUpdateTime;
		Simulator.scheduleEvent("Update", time + attributeUpdateTime - delta,
				this, SimulationEvent.Type.STATUS);

		ISkyNetMonitor monitor = (ISkyNetMonitor) Simulator.getMonitor();
		csAnalyzer = (ChurnStatisticsAnalyzer) monitor
				.getConnectivityAnalyzer(ChurnStatisticsAnalyzer.class);
		peerCounter = 1;
		onlinePeersRoot = 0;
		onlinePeersSupportPeer = 0;
		try {
			adFile = new PrintWriter(new BufferedWriter(new FileWriter(
					RESULTS_PATH + File.separatorChar
							+ ATTRIBUTE_DAT_FILE)), true);
			aoFile = new PrintWriter(new BufferedWriter(new FileWriter(
					RESULTS_PATH + File.separator
							+ ATTRIBUTE_OVERVIEW_FILE)), true);
			writeHeader();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Through this method, the root and the Support Peer of the root
	 * periodically pass the amount of managed attribute-entries to this class,
	 * which afterwards prints them in the file, as mentioned above.
	 * 
	 * @param onlinePeers
	 *            contains the amount of managed attribute-entries
	 * @param root
	 *            specifies if the information is delivered by the root or by
	 *            the Support Peer of the root
	 */
	public void writeAttributeInfo(int onlinePeers, boolean root) {
		if (root) {
			onlinePeersRoot = onlinePeers;
		} else {
			onlinePeersSupportPeer = onlinePeers;
		}
	}

	/**
	 * This method is responsible for writing the attributes of each peer to the
	 * file, as mentioned above. This method is executed at the beginning of a
	 * simulation.
	 * 
	 * @param vec
	 *            contains all attributes of a peer
	 * @param ip
	 *            contains the {@link NetID} of the peer, whose attributes are
	 *            written
	 */
	public void writeAttributesOfPeer(Vector<Attribute<?>> vec, String ip) {
		aoFile.println(peerCounter + ".Peer------------" + ip + "------------");
		Attribute<?> att;
		double rank = 0;
		for (int i = 0; i < vec.size(); i++) {
			att = vec.get(i);
			if (att.getName().equals("DownBandwidth")) {
				rank = rank
						+ normalizeValue(
								propReader
										.getIntProperty("LowerBoundDownBandwidth"),
								propReader
										.getIntProperty("UpperBoundDownBandwidth"),
								(Double) att.getValue())
						* downBandwidthCoefficient;
			} else if (att.getName().equals("CPU")) {
				rank = rank
						+ normalizeValue(propReader
								.getIntProperty("LowerBoundCPU"), propReader
								.getIntProperty("UpperBoundCPU"), (Integer) att
								.getValue()) * cpuCoefficient;
			} else if (att.getName().equals("RAM")) {
				rank = rank
						+ normalizeValue(propReader
								.getIntProperty("LowerBoundRAM"), propReader
								.getIntProperty("UpperBoundRAM"), (Integer) att
								.getValue()) * ramCoefficient;
			} else if (att.getName().equals("Storage")) {
				rank = rank
						+ normalizeValue(propReader
								.getIntProperty("LowerBoundStorage"),
								propReader.getIntProperty("UpperBoundStorage"),
								(Integer) att.getValue()) * storageCoefficient;
			} else if (att.getName().equals("AvgOnline")) {
				rank = rank
						+ normalizeValue(
								propReader
										.getTimeProperty("LowerBoundOnlineTime"),
								propReader
										.getTimeProperty("UpperBoundOnlineTime"),
								(Double) att.getValue()) * onlineCoefficient;
			}

			aoFile.println("\tname = " + att.getName() + ",\ttype = "
					+ att.getType() + ",\tvalue = " + att.getValue());
		}

		aoFile.println("\tQuality of attributes = " + rank);
		aoFile.println();
		peerCounter++;
	}

	private void writeHeader() {
		adFile.println("# time in sec");
		adFile.println("# AttributeEntries of Online-Peers");
		adFile.println("# Offline-Peers");
		adFile.println("# AttributeEntries of the root");
		adFile.println("# AttributeEntries of the SupportPeer from the root");
		adFile.println("# Amount of active SupportPeers\n");
		adFile.println();
	}

	public void closeWriter() {
		adFile.flush();
		adFile.close();

		aoFile.flush();
		aoFile.close();
	}

	@Override
	public void eventOccurred(SimulationEvent se) {
		if (se.getType().equals(SimulationEvent.Type.STATUS)) {
			if (((String) se.getData()).equals("Update")) {
				long time = Simulator.getCurrentTime();
				adFile
						.print((time / SkyNetConstants.DIVISOR_FOR_SECOND)
								+ "\t");
				adFile.print(csAnalyzer.getNoOfPresentPeers() + "\t");
				adFile.print(csAnalyzer.getNoOfAbsentPeers() + "\t");
				adFile.print(onlinePeersRoot + "\t");
				adFile.print(onlinePeersSupportPeer + "\t");
				adFile.print(amountOfSupportPeers + "\t");
				adFile.println();
				adFile.flush();

				// add the values of counted attributes-entries by the root and
				// available attribute-entries to the SkyNetVisualization
				DeviationSet[] values = {
						new DeviationSet(csAnalyzer.getNoOfPresentPeers()),
						new DeviationSet(onlinePeersRoot),
						new DeviationSet(onlinePeersSupportPeer),
						new DeviationSet(onlinePeersRoot
								+ onlinePeersSupportPeer) };
				String[] names = { "Available Amount AttributeEntries",
						"AttributeEntries of the root",
						"AttributeEntries of the SP",
						"AttributeEntries of root + SP" };
				if (SkyNetVisualization.hasInstance()) {
					SkyNetVisualization.getInstance().updateDisplayedMetric(
							"Available Attributes",
							new DataSet(VisualizationType.State, time / 1000,
									values, names));
				}
				onlinePeersRoot = 0;
				onlinePeersSupportPeer = 0;
				Simulator.scheduleEvent("Update", time + attributeUpdateTime,
						this, SimulationEvent.Type.STATUS);

			}
		}
	}

	/**
	 * If a new Support Peer is created, this method is called to increment the
	 * amount of counted Support Peers.
	 */
	public void incrementAmountOfSupportPeers() {
		amountOfSupportPeers++;
	}

	/**
	 * If a Support Peer is released, this method is called to decrement the
	 * amount of counted Support Peers.
	 */
	public void decrementAmountOfSupportPeers() {
		amountOfSupportPeers--;
		if (amountOfSupportPeers < 0) {
			log.fatal("Amount of Support Peers is smaller than 0");
		}
	}

	private double normalizeValue(long lowerBound, long upperBound,
			double currentValue) {
		if (currentValue < lowerBound) {
			return 0d;
		} else if (currentValue > upperBound) {
			return 1d;
		} else {
			return (currentValue - lowerBound) / (upperBound - lowerBound);
		}
	}
}
